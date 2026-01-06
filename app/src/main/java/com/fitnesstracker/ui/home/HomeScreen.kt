package com.fitnesstracker.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitnesstracker.ui.theme.FitnessTrackerTheme

@Composable
fun HomeScreen(username: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") }, // Title is now in the content
                actions = {
                    IconButton(onClick = { /* Handle settings click */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
        ) {
            Text(
                text = "Hello, ${username.substringBefore("@")}",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary
            )
            Text(
                text = "Ready to achieve your goals?",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatItem(label = "Today's Steps", value = "0")
                    StatItem(label = "Calories Burned", value = "0")
                    StatItem(label = "Distance", value = "0 km")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                    progress = 0.0f, // This will be dynamic later
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 16.dp,
                    color = MaterialTheme.colors.primary
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("0", style = MaterialTheme.typography.h3, fontWeight = FontWeight.Bold)
                    Text("Steps")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colors.surface
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recent activity yet.",
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
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
        HomeScreen(username = "user@example.com")
    }
}
