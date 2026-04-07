package com.example.spendwise.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spendwise.screen.*

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen (no bottom bar)
        composable(Screen.Splash.route) {
            SplashScreen(
                navController = navController,
                onTimeout = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Screens with Bottom Bar
        composable(Screen.Home.route) {
            Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
                HomeScreen(modifier = Modifier.padding(padding))
            }
        }

        composable(Screen.Transaction.route) {
            Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
                TransactionScreen(modifier = Modifier.padding(padding))
            }
        }

        composable(Screen.Summary.route) {
            Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
                SummaryScreen(modifier = Modifier.padding(padding))
            }
        }

        composable(Screen.Settings.route) {
            Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
                SettingsScreen(modifier = Modifier.padding(padding))
            }
        }
    }
}