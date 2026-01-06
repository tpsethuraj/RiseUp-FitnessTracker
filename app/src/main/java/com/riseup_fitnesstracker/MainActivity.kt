package com.riseup_fitnesstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.riseup_fitnesstracker.data.pref.FitnessPreferences
import com.riseup_fitnesstracker.ui.SplashScreen
import com.riseup_fitnesstracker.ui.history.HistoryScreen
import com.riseup_fitnesstracker.ui.home.HomeScreen
import com.riseup_fitnesstracker.ui.login.LoginScreen
import com.riseup_fitnesstracker.ui.settings.SettingsScreen
import com.riseup_fitnesstracker.ui.signup.SignUpScreen
import com.riseup_fitnesstracker.ui.theme.FitnessTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val context = LocalContext.current
            val fitnessPrefs = remember { FitnessPreferences(context) }
            var themeMode by remember { mutableStateOf(fitnessPrefs.getThemeMode()) }

            FitnessTrackerTheme(themeMode = themeMode) {
                FitnessTrackerApp(onThemeChange = { newMode ->
                    themeMode = newMode
                })
            }
        }
    }
}
