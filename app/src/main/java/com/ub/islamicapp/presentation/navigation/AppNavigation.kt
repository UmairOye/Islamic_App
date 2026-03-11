package com.ub.islamicapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ub.islamicapp.presentation.screens.HomeScreen
import com.ub.islamicapp.presentation.screens.HijriCalendarScreen
import com.ub.islamicapp.presentation.screens.GregorianCalendarScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("hijri_calendar") {
            HijriCalendarScreen(navController = navController)
        }
        composable("gregorian_calendar") {
            GregorianCalendarScreen(navController = navController)
        }
        composable("prayer_times") {
            com.ub.islamicapp.presentation.screens.PrayerTimesScreen(navController = navController)
        }
        composable("qibla") {
            com.ub.islamicapp.presentation.screens.QiblaScreen(navController = navController)
        }
    }
}
