package com.ub.islamicapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ub.islamicapp.screens.home.screen.HomeScreen
import com.ub.islamicapp.screens.calendar.screen.HijriCalendarScreen
import com.ub.islamicapp.screens.calendar.screen.GregorianCalendarScreen

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
            com.ub.islamicapp.screens.prayers.screen.PrayerTimesScreen(navController = navController)
        }
        composable("qibla") {
            com.ub.islamicapp.screens.qibla.screen.QiblaScreen(navController = navController)
        }
        composable("prayer_notifications") {
            com.ub.islamicapp.screens.notifications.screen.PrayerNotificationsScreen(navController = navController)
        }
        composable("select_location") {
            com.ub.islamicapp.screens.location.screen.SelectLocationScreen(navController = navController)
        }
    }
}
