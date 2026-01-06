package com.riseup_fitnesstracker.ui.history

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.riseup_fitnesstracker.data.model.DailyStepData
import com.riseup_fitnesstracker.data.model.HourlyStepData
import com.riseup_fitnesstracker.ui.theme.FitnessTrackerTheme
import com.riseup_fitnesstracker.util.rememberHourlyStepData
import com.riseup_fitnesstracker.util.rememberWeeklyStepData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    var selectedTab by remember { mutableStateOf("Week") }
    var selectedView by remember { mutableStateOf("Steps") } // "Steps" or "Heart Points"
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    val weeklyDataStateObj = if (isPreview) {
        val mockData = remember { mutableStateOf(listOf(
            DailyStepData("Mon", 4500, false, LocalDate.now().minusDays(6)),
            DailyStepData("Tue", 7000, true, LocalDate.now().minusDays(5)),
            DailyStepData("Wed", 3000, false, LocalDate.now().minusDays(4)),
            DailyStepData("Thu", 8500, true, LocalDate.now().minusDays(3)),
            DailyStepData("Fri", 5000, false, LocalDate.now().minusDays(2)),
            DailyStepData("Sat", 2000, false, LocalDate.now().minusDays(1)),
            DailyStepData("Sun", 6500, true, LocalDate.now())
        )) }
        com.riseup_fitnesstracker.util.WeeklyDataState(mockData) {}
    } else {
        rememberWeeklyStepData(context as Activity)
    }
    val weeklyData = weeklyDataStateObj.data.value

    val hourlyDataStateObj = if (isPreview) {
        val mockData = remember { mutableStateOf(listOf(
            HourlyStepData(8, 450),
            HourlyStepData(10, 1200),
            HourlyStepData(12, 800),
            HourlyStepData(14, 1500),
            HourlyStepData(16, 600)
        )) }
        com.riseup_fitnesstracker.util.HourlyDataState(mockData) {}
    } else {
        rememberHourlyStepData(context as Activity, selectedDate)
    }
    val hourlyData = hourlyDataStateObj.data.value

    // Dynamic Date Range Calculation
    val today = LocalDate.now()
    val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
    val endOfWeek = startOfWeek.plusDays(6)
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
    
    val dateRangeText = if (selectedTab == "Week") {
        "${startOfWeek.format(dateFormatter)} – ${endOfWeek.format(dateFormatter)}"
    } else {
        selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d MMM", Locale.getDefault()))
    }

    // Dynamic Points Calculation
    val totalSteps = if (selectedTab == "Week") weeklyData.sumOf { it.steps } else hourlyData.sumOf { it.steps }
    val totalVal = if (selectedView == "Steps") totalSteps else (totalSteps / 100)
    val unit = if (selectedView == "Steps") "steps" else "pts"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My activity", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colors.background)
        ) {
            // Tab Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("Day", "Week").forEach { tab ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TextButton(onClick = { selectedTab = tab }) {
                            Text(
                                text = tab,
                                color = if (selectedTab == tab) MaterialTheme.colors.onBackground else MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        if (selectedTab == tab) {
                            Box(modifier = Modifier.width(40.dp).height(2.dp).background(MaterialTheme.colors.onBackground))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Date Range Navigation
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    if (selectedTab == "Day") selectedDate = selectedDate.minusDays(1)
                }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = null)
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = dateRangeText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (selectedView == "Steps") Icons.Default.Done else Icons.Default.Favorite, 
                            contentDescription = null, 
                            tint = if (selectedView == "Steps") MaterialTheme.colors.primary else Color(0xFF00E676), 
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "$totalVal $unit", color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f))
                    }
                }

                IconButton(onClick = { 
                    if (selectedTab == "Day") selectedDate = selectedDate.plusDays(1)
                }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chart Section with Horizontal Scroll
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 24.dp)
            ) {
                // Horizontal grid lines
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    repeat(4) {
                        Divider(color = MaterialTheme.colors.onBackground.copy(alpha = 0.1f))
                    }
                }
                
                // Fixed Y-axis labels with Round Figures
                val currentMax = if (selectedTab == "Week") {
                    weeklyData.maxOfOrNull { it.steps }?.coerceAtLeast(1000) ?: 1000
                } else {
                    hourlyData.maxOfOrNull { it.steps }?.coerceAtLeast(100) ?: 100
                }
                
                val yMax = if (selectedTab == "Week") ((currentMax + 999) / 1000) * 1000 else ((currentMax + 99) / 100) * 100
                val displayMax = if (selectedView == "Steps") yMax else (yMax / 100)
                
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 20.dp, end = 40.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    if (selectedTab == "Week") {
                        weeklyData.forEach { data ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { 
                                    data.date?.let { 
                                        selectedDate = it
                                        selectedTab = "Day"
                                    }
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height((data.steps.toFloat() / yMax * 150).coerceAtLeast(4f).dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (selectedView == "Steps") {
                                                if (data.goalReached) MaterialTheme.colors.primary else MaterialTheme.colors.primary.copy(alpha = 0.5f)
                                            } else {
                                                Color(0xFF00E676)
                                            }
                                        ),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    if (selectedView == "Steps" && data.goalReached) {
                                        Icon(Icons.Default.Done, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp).padding(top = 2.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = data.dayName, fontSize = 12.sp, color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f))
                            }
                        }
                    } else if (selectedTab == "Day") {
                        val displayHours = listOf(0, 4, 8, 12, 16, 20)
                        
                        hourlyData.forEach { data ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(12.dp)
                                        .height((data.steps.toFloat() / yMax * 150).coerceAtLeast(2f).dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(if (selectedView == "Steps") MaterialTheme.colors.primary else Color(0xFF00E676))
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (data.hour in displayHours) {
                                    Text(text = formatHour(data.hour), fontSize = 10.sp, color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f))
                                } else {
                                    Spacer(modifier = Modifier.height(14.dp))
                                }
                            }
                        }
                    }
                }
                
                Column(modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd), verticalArrangement = Arrangement.SpaceBetween) {
                    Text(text = if (displayMax >= 1000) "${displayMax / 1000}k" else "$displayMax", fontSize = 10.sp, color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f))
                    Text(text = if (displayMax >= 1000) "${(displayMax * 0.66).toInt() / 1000}k" else "${(displayMax * 0.66).toInt()}", fontSize = 10.sp, color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f))
                    Text(text = if (displayMax >= 1000) "${(displayMax * 0.33).toInt() / 1000}k" else "${(displayMax * 0.33).toInt()}", fontSize = 10.sp, color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f))
                    Text(text = "0", fontSize = 10.sp, color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Detailed List Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                if (selectedTab == "Week") {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(weeklyData.reversed()) { data ->
                            val itemVal = if (selectedView == "Steps") data.steps else (data.steps / 100)
                            ActivityListItem(
                                title = data.dayName,
                                subtitle = data.date?.format(DateTimeFormatter.ofPattern("d MMMM yyyy")) ?: "",
                                value = "$itemVal $unit",
                                icon = if (selectedView == "Steps" && data.goalReached) Icons.Default.Done else null,
                                onClick = {
                                    data.date?.let {
                                        selectedDate = it
                                        selectedTab = "Day"
                                    }
                                }
                            )
                        }
                    }
                } else if (selectedTab == "Day") {
                    val filteredHours = hourlyData.filter { it.steps > 0 }
                    if (filteredHours.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No activity recorded for this day", color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f))
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(filteredHours.reversed()) { data ->
                                val itemVal = if (selectedView == "Steps") data.steps else (data.steps / 100)
                                ActivityListItem(
                                    title = formatHour(data.hour),
                                    subtitle = "Activity during this hour",
                                    value = "$itemVal $unit"
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Selection
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { selectedView = "Heart Points" },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (selectedView == "Heart Points") Color(0xFF2D2F33) else MaterialTheme.colors.surface,
                        contentColor = if (selectedView == "Heart Points") Color.White else MaterialTheme.colors.onSurface
                    )
                ) {
                    Text("Heart Points")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = { selectedView = "Steps" },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (selectedView == "Steps") Color(0xFF2D2F33) else MaterialTheme.colors.surface,
                        contentColor = if (selectedView == "Steps") Color.White else MaterialTheme.colors.onSurface
                    )
                ) {
                    Text("Steps")
                }
            }
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
fun ActivityListItem(
    title: String, 
    subtitle: String, 
    value: String, 
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(12.dp),
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = value, fontWeight = FontWeight.Medium, color = MaterialTheme.colors.primary)
                if (icon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(icon, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    FitnessTrackerTheme {
        HistoryScreen(navController = rememberNavController())
    }
}
