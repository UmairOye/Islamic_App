import os
import re

base_dir = "app/src/main/java/com/ub/islamicapp"

file_packages = {
    # Home
    "HomeScreen.kt": "com.ub.islamicapp.screens.home.screen",
    "HomeViewModel.kt": "com.ub.islamicapp.screens.home.viewmodel",
    "HomeUiState.kt": "com.ub.islamicapp.screens.home.viewmodel",
    "AppComponents.kt": "com.ub.islamicapp.screens.home.components",
    "FeatureGrid.kt": "com.ub.islamicapp.screens.home.components",
    "FeatureItem.kt": "com.ub.islamicapp.screens.home.components",
    "LastReadCard.kt": "com.ub.islamicapp.screens.home.components",
    "PrayerItem.kt": "com.ub.islamicapp.screens.home.components",
    "PrayerTracker.kt": "com.ub.islamicapp.screens.home.components",

    # Prayers
    "PrayerTimesScreen.kt": "com.ub.islamicapp.screens.prayers.screen",

    # Calendar
    "CalendarScreens.kt": "com.ub.islamicapp.screens.calendar.screen",
    "CalendarView.kt": "com.ub.islamicapp.screens.calendar.components",

    # Qibla
    "QiblaScreen.kt": "com.ub.islamicapp.screens.qibla.screen",
    "QiblaViewModel.kt": "com.ub.islamicapp.screens.qibla.viewmodel",
    "QiblaUiState.kt": "com.ub.islamicapp.screens.qibla.viewmodel",

    # Location
    "SelectLocationScreen.kt": "com.ub.islamicapp.screens.location.screen",

    # Notifications
    "PrayerNotificationsScreen.kt": "com.ub.islamicapp.screens.notifications.screen",
}

for root, _, files in os.walk(base_dir):
    for file in files:
        if file.endswith(".kt"):
            filepath = os.path.join(root, file)
            with open(filepath, "r") as f:
                content = f.read()

            if file in file_packages:
                new_pkg = file_packages[file]
                content = re.sub(r"^package .*", f"package {new_pkg}", content, flags=re.MULTILINE)

            # Fix imports globally
            content = content.replace("com.ub.islamicapp.presentation.state.HomeUiState", "com.ub.islamicapp.screens.home.viewmodel.HomeUiState")
            content = content.replace("com.ub.islamicapp.presentation.state.PrayerTime", "com.ub.islamicapp.screens.home.viewmodel.PrayerTime")
            content = content.replace("com.ub.islamicapp.presentation.state.QiblaUiState", "com.ub.islamicapp.screens.qibla.viewmodel.QiblaUiState")

            content = content.replace("com.ub.islamicapp.presentation.viewmodel.HomeViewModel", "com.ub.islamicapp.screens.home.viewmodel.HomeViewModel")
            content = content.replace("com.ub.islamicapp.presentation.viewmodel.QiblaViewModel", "com.ub.islamicapp.screens.qibla.viewmodel.QiblaViewModel")

            content = content.replace("com.ub.islamicapp.presentation.components.CalendarView", "com.ub.islamicapp.screens.calendar.components.CalendarView")
            content = content.replace("com.ub.islamicapp.presentation.components.", "com.ub.islamicapp.screens.home.components.")

            content = content.replace("com.ub.islamicapp.presentation.screens.HomeScreen", "com.ub.islamicapp.screens.home.screen.HomeScreen")
            content = content.replace("com.ub.islamicapp.presentation.screens.HijriCalendarScreen", "com.ub.islamicapp.screens.calendar.screen.HijriCalendarScreen")
            content = content.replace("com.ub.islamicapp.presentation.screens.GregorianCalendarScreen", "com.ub.islamicapp.screens.calendar.screen.GregorianCalendarScreen")
            content = content.replace("com.ub.islamicapp.presentation.prayers.PrayerTimesScreen", "com.ub.islamicapp.screens.prayers.screen.PrayerTimesScreen")
            content = content.replace("com.ub.islamicapp.presentation.screens.prayers.screen.PrayerTimesScreen", "com.ub.islamicapp.screens.prayers.screen.PrayerTimesScreen")
            content = content.replace("com.ub.islamicapp.presentation.screens.QiblaScreen", "com.ub.islamicapp.screens.qibla.screen.QiblaScreen")
            content = content.replace("com.ub.islamicapp.presentation.screens.PrayerNotificationsScreen", "com.ub.islamicapp.screens.notifications.screen.PrayerNotificationsScreen")
            content = content.replace("com.ub.islamicapp.presentation.screens.SelectLocationScreen", "com.ub.islamicapp.screens.location.screen.SelectLocationScreen")

            with open(filepath, "w") as f:
                f.write(content)
