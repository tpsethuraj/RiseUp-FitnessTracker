package com.fitnesstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitnesstracker.ui.SplashScreen
import com.fitnesstracker.ui.home.HomeScreen
import com.fitnesstracker.ui.login.LoginScreen
import com.fitnesstracker.ui.signup.SignUpScreen
import com.fitnesstracker.ui.theme.FitnessTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            FitnessTrackerTheme {
                FitnessTrackerApp()
            }
        }
    }
}

@Composable
fun FitnessTrackerApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("signup") {
            SignUpScreen(navController = navController)
        }
        composable("home/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            HomeScreen(username = username)
        }
    }
}
