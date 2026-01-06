package com.riseup_fitnesstracker.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.riseup_fitnesstracker.data.model.DailyStepData
import com.riseup_fitnesstracker.data.model.FitnessData
import com.riseup_fitnesstracker.data.model.HourlyStepData
import com.riseup_fitnesstracker.data.pref.FitnessPreferences
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import java.util.concurrent.TimeUnit

const val TAG = "StepCounter"

data class FitnessDataState(
    val data: State<FitnessData>,
    val refresh: () -> Unit
)

@Composable
fun rememberFitnessData(activity: Activity): FitnessDataState {

    val context: Context = activity
    val fitnessPrefs = remember { FitnessPreferences(context) }
    val state = remember { mutableStateOf<FitnessData>(fitnessPrefs.getFitnessData()) }

    var hasActivityPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val fitnessOptions = remember {
        FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .build()
    }

    val account = remember {
        GoogleSignIn.getAccountForExtension(context, fitnessOptions)
    }

    var hasFitPermission by remember {
        mutableStateOf(GoogleSignIn.hasPermissions(account, fitnessOptions))
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            hasActivityPermission = it
        }

    val signInLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            hasFitPermission = it.resultCode == Activity.RESULT_OK
        }

    fun subscribeRecording() {
        Fitness.getRecordingClient(context, account)
            .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
        Fitness.getRecordingClient(context, account)
            .subscribe(DataType.TYPE_CALORIES_EXPENDED)
        Fitness.getRecordingClient(context, account)
            .subscribe(DataType.TYPE_DISTANCE_DELTA)
    }

    fun readTodayData() {
        val start = LocalDate.now().atStartOfDay()
        val end = LocalDateTime.now()

        val request = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
            .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(
                start.atZone(ZoneId.systemDefault()).toEpochSecond(),
                end.atZone(ZoneId.systemDefault()).toEpochSecond(),
                TimeUnit.SECONDS
            )
            .build()

        Fitness.getHistoryClient(context, account)
            .readData(request)
            .addOnSuccessListener { res ->
                val bucket = res.buckets.firstOrNull()
                val steps =
                    bucket?.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA)
                        ?.dataPoints?.firstOrNull()
                        ?.getValue(Field.FIELD_STEPS)?.asInt() ?: 0
                val calories =
                    bucket?.getDataSet(DataType.AGGREGATE_CALORIES_EXPENDED)
                        ?.dataPoints?.firstOrNull()
                        ?.getValue(Field.FIELD_CALORIES)?.asFloat() ?: 0f
                val distance =
                    bucket?.getDataSet(DataType.AGGREGATE_DISTANCE_DELTA)
                        ?.dataPoints?.firstOrNull()
                        ?.getValue(Field.FIELD_DISTANCE)?.asFloat() ?: 0f

                val currentWater = state.value.waterIntake
                val newData = FitnessData(steps, calories, distance, currentWater)
                state.value = newData
                fitnessPrefs.saveFitnessData(newData)
            }
    }

    LaunchedEffect(hasActivityPermission, hasFitPermission) {
        when {
            !hasActivityPermission -> {
                permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
            !hasFitPermission -> {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .addExtension(fitnessOptions)
                    .build()
                signInLauncher.launch(
                    GoogleSignIn.getClient(activity, gso).signInIntent
                )
            }
            else -> {
                subscribeRecording()
                readTodayData()
            }
        }
    }

    return FitnessDataState(state) {
        if (hasActivityPermission && hasFitPermission) {
            readTodayData()
        }
    }
}

data class HourlyDataState(
    val data: State<List<HourlyStepData>>,
    val refresh: () -> Unit
)

@Composable
fun rememberHourlyStepData(activity: Activity, date: LocalDate): HourlyDataState {
    val context: Context = activity
    val hourlyData = remember { mutableStateOf<List<HourlyStepData>>(emptyList()) }

    val fitnessOptions = remember {
        FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build()
    }

    val account = remember {
        GoogleSignIn.getAccountForExtension(context, fitnessOptions)
    }

    fun readHourlyData() {
        val start = date.atStartOfDay()
        val end = if (date == LocalDate.now()) LocalDateTime.now() else date.atTime(23, 59, 59)

        val request = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.HOURS)
            .setTimeRange(
                start.atZone(ZoneId.systemDefault()).toEpochSecond(),
                end.atZone(ZoneId.systemDefault()).toEpochSecond(),
                TimeUnit.SECONDS
            )
            .build()

        Fitness.getHistoryClient(context, account)
            .readData(request)
            .addOnSuccessListener { res ->
                val list = res.buckets.map { bucket ->
                    val hour = LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(bucket.getStartTime(TimeUnit.MILLISECONDS)),
                        ZoneId.systemDefault()
                    ).hour
                    val steps = bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA)
                        ?.dataPoints?.firstOrNull()
                        ?.getValue(Field.FIELD_STEPS)?.asInt() ?: 0
                    HourlyStepData(hour, steps)
                }
                hourlyData.value = list
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to read hourly data", it)
            }
    }

    LaunchedEffect(date) {
        if (GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            readHourlyData()
        }
    }

    return HourlyDataState(hourlyData) {
        if (GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            readHourlyData()
        }
    }
}

data class WeeklyDataState(
    val data: State<List<DailyStepData>>,
    val refresh: () -> Unit
)

@Composable
fun rememberWeeklyStepData(activity: Activity): WeeklyDataState {
    val context: Context = activity
    val weeklyData = remember { mutableStateOf<List<DailyStepData>>(emptyList()) }

    val fitnessOptions = remember {
        FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build()
    }

    val account = remember {
        GoogleSignIn.getAccountForExtension(context, fitnessOptions)
    }

    fun readWeeklyData() {
        val today = LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1).atStartOfDay()
        val end = LocalDateTime.now()

        val request = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(
                startOfWeek.atZone(ZoneId.systemDefault()).toEpochSecond(),
                end.atZone(ZoneId.systemDefault()).toEpochSecond(),
                TimeUnit.SECONDS
            )
            .build()

        Fitness.getHistoryClient(context, account)
            .readData(request)
            .addOnSuccessListener { res ->
                val list = res.buckets.map { bucket ->
                    val date = LocalDate.ofInstant(
                        java.time.Instant.ofEpochMilli(bucket.getStartTime(TimeUnit.MILLISECONDS)),
                        ZoneId.systemDefault()
                    )
                    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    val steps = bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA)
                        ?.dataPoints?.firstOrNull()
                        ?.getValue(Field.FIELD_STEPS)?.asInt() ?: 0
                    DailyStepData(dayName, steps, steps >= 6000, date) 
                }
                
                val fullWeek = (1..7).map { i ->
                    val date = startOfWeek.toLocalDate().plusDays(i.toLong() - 1)
                    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    list.find { it.date == date } ?: DailyStepData(dayName, 0, false, date)
                }
                
                weeklyData.value = fullWeek
            }
    }

    LaunchedEffect(Unit) {
        if (GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            readWeeklyData()
        }
    }

    return WeeklyDataState(weeklyData) {
        if (GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            readWeeklyData()
        }
    }
}
