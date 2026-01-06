package com.riseup_fitnesstracker.ui.home

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.riseup_fitnesstracker.data.model.DailyStepData
import com.riseup_fitnesstracker.data.model.FitnessData
import com.riseup_fitnesstracker.data.model.HourlyStepData
import com.riseup_fitnesstracker.data.pref.FitnessPreferences
import com.riseup_fitnesstracker.ui.theme.FitnessTrackerTheme
import com.riseup_fitnesstracker.util.rememberFitnessData
import com.riseup_fitnesstracker.util.rememberHourlyStepData
import com.riseup_fitnesstracker.util.rememberWeeklyStepData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(username: String, navController: NavController) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val fitnessPrefs = remember { FitnessPreferences(context) }
    var showGoalDialog by remember { mutableStateOf(false) }
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    // Handle fitness data
    val fitnessDataStateObj = if (isPreview) {
        val mockData = remember { mutableStateOf(FitnessData(steps = 5432, calories = 230f, distance = 3800f, waterIntake = 1250)) }
        com.riseup_fitnesstracker.util.FitnessDataState(mockData) {}
    } else {
        rememberFitnessData(context as Activity)
    }
    val fitnessData = fitnessDataStateObj.data.value
    
    // Dynamic Goals
    val dailyStepGoal = if (isPreview) 10000 else fitnessPrefs.getStepGoal()
    val dailyWaterGoal = if (isPreview) 2000 else fitnessPrefs.getWaterGoal()

    // Handle hourly data
    val hourlyDataStateObj = if (isPreview) {
        val mockData = remember { mutableStateOf(listOf(HourlyStepData(14, 450), HourlyStepData(12, 1200), HourlyStepData(10, 800))) }
        com.riseup_fitnesstracker.util.HourlyDataState(mockData) {}
    } else {
        rememberHourlyStepData(context as Activity, LocalDate.now())
    }
    val hourlyData = hourlyDataStateObj.data.value.filter { it.steps > 0 }.reversed()

    // Handle weekly data
    val weeklyDataStateObj = if (isPreview) {
        val mockData = remember { mutableStateOf(listOf(DailyStepData("Mon", 7000, true), DailyStepData("Tue", 4000, false))) }
        com.riseup_fitnesstracker.util.WeeklyDataState(mockData) {}
    } else {
        rememberWeeklyStepData(context as Activity)
    }
    val weeklyData = weeklyDataStateObj.data.value
    val goalsReached = weeklyData.count { it.goalReached }

    // Refresh Logic
    fun refresh() = refreshScope.launch {
        refreshing = true
        if (!isPreview) {
            fitnessDataStateObj.refresh()
            hourlyDataStateObj.refresh()
            weeklyDataStateObj.refresh()
        }
        delay(1000)
        refreshing = false
    }

    val pullRefreshState = rememberPullRefreshState(refreshing, ::refresh)

    // Animations
    val stepProgress by animateFloatAsState(
        targetValue = (fitnessData.steps.toFloat() / dailyStepGoal).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000), label = ""
    )
    val waterProgress by animateFloatAsState(
        targetValue = (fitnessData.waterIntake.toFloat() / dailyWaterGoal).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000), label = ""
    )

    // Celebratory Pop-up
    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text(text = "Goal Reached! 🎉", fontWeight = FontWeight.Bold) },
            text = { Text(text = "You've successfully reached your daily water goal of ${dailyWaterGoal}ml. Great job!") },
            confirmButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Hello, $username", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
                Text(text = "Ready to achieve your goals?", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Light)

                Spacer(modifier = Modifier.height(24.dp))

                // Main Stats Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatItem(label = "Today's Steps", value = NumberFormat.getIntegerInstance().format(fitnessData.steps))
                        StatItem(label = "Calories Burned", value = "${fitnessData.calories.toInt()}")
                        StatItem(label = "Distance", value = String.format(Locale.US, "%.2f", fitnessData.distance / 1000) + " km")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Step Progress Circle
                Box(contentAlignment = Alignment.Center, modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)) {
                    CircularProgressIndicator(
                        progress = 1f,
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 16.dp,
                        color = MaterialTheme.colors.primary.copy(alpha = 0.1f)
                    )
                    CircularProgressIndicator(
                        progress = stepProgress,
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 16.dp,
                        color = MaterialTheme.colors.primary
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(NumberFormat.getIntegerInstance().format(fitnessData.steps), style = MaterialTheme.typography.h3, fontWeight = FontWeight.Bold)
                        Text("Steps")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Water Intake Card
                Text(
                    text = "Hydration",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 2.dp,
                    backgroundColor = if (fitnessData.waterIntake >= dailyWaterGoal) Color(0xFFE3F2FD) else MaterialTheme.colors.surface
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF2196F3))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Water Intake", fontWeight = FontWeight.Medium)
                            }
                            Text(text = "${fitnessData.waterIntake} ml / ${dailyWaterGoal} ml", style = MaterialTheme.typography.caption)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (fitnessData.waterIntake >= dailyWaterGoal) {
                            Text(
                                text = "Daily goal reached! 💧",
                                color = Color(0xFF1976D2),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        } else {
                            LinearProgressIndicator(
                                progress = waterProgress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFF2196F3),
                                backgroundColor = Color(0xFF2196F3).copy(alpha = 0.1f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = {
                                val newWater = (fitnessData.waterIntake - 250).coerceAtLeast(0)
                                val newData = fitnessData.copy(waterIntake = newWater)
                                (fitnessDataStateObj.data as MutableState).value = newData
                                fitnessPrefs.saveFitnessData(newData)
                            }) { Icon(Icons.Default.Remove, contentDescription = null) }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    val oldWater = fitnessData.waterIntake
                                    val newWater = fitnessData.waterIntake + 250
                                    val newData = fitnessData.copy(waterIntake = newWater)
                                    (fitnessDataStateObj.data as MutableState).value = newData
                                    fitnessPrefs.saveFitnessData(newData)
                                    
                                    if (oldWater < dailyWaterGoal && newWater >= dailyWaterGoal) {
                                        showGoalDialog = true
                                    }
                                },
                                enabled = fitnessData.waterIntake < dailyWaterGoal
                            ) { 
                                Icon(
                                    Icons.Default.Add, 
                                    contentDescription = null,
                                    tint = if (fitnessData.waterIntake < dailyWaterGoal) MaterialTheme.colors.onSurface else MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                                ) 
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Weekly Highlights
                Text(
                    text = "Weekly Highlights",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 2.dp,
                    backgroundColor = MaterialTheme.colors.surface
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colors.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "You've reached your daily goal $goalsReached times this week!",
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Health Tip Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Health Tip", style = MaterialTheme.typography.subtitle2, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Staying hydrated improves focus and energy levels. Aim for 8 glasses of water today!",
                            style = MaterialTheme.typography.body2
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Recent Activity Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Activity",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { navController.navigate("history") }) {
                        Text("View Full History", color = MaterialTheme.colors.primary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Compact activity list
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 250.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colors.surface,
                    elevation = 2.dp
                ) {
                    if (hourlyData.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No activity recorded yet today.", color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
                        }
                    } else {
                        Column(modifier = Modifier.padding(16.dp)) {
                            hourlyData.take(3).forEach { data ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = formatHour(data.hour), fontWeight = FontWeight.Medium)
                                    Text(text = "${data.steps} steps", color = MaterialTheme.colors.primary, fontWeight = FontWeight.Bold)
                                }
                                if (data != hourlyData.take(3).last()) Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
        }
    }
}

private fun formatHour(hour: Int): String {
    val amPm = if (hour < 12) "am" else "pm"
    val h12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "$h12 $amPm"
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.caption
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FitnessTrackerTheme {
        HomeScreen(username = "User", navController = rememberNavController())
    }
}
