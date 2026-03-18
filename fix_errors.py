import os

qibla_state_path = "app/src/main/java/com/ub/islamicapp/screens/qibla/viewmodel/QiblaUiState.kt"
if os.path.exists(qibla_state_path):
    with open(qibla_state_path, "r") as f:
        content = f.read()
    if "import com.ub.islamicapp.screens.home.viewmodel.PrayerTime" not in content:
        content = content.replace("package com.ub.islamicapp.screens.qibla.viewmodel",
                                  "package com.ub.islamicapp.screens.qibla.viewmodel\n\nimport com.ub.islamicapp.screens.home.viewmodel.PrayerTime")
    with open(qibla_state_path, "w") as f:
        f.write(content)

qibla_vm_path = "app/src/main/java/com/ub/islamicapp/screens/qibla/viewmodel/QiblaViewModel.kt"
if os.path.exists(qibla_vm_path):
    with open(qibla_vm_path, "r") as f:
        content = f.read()
    if "import com.ub.islamicapp.screens.home.viewmodel.PrayerTime" not in content:
        content = content.replace("import com.ub.islamicapp.domain.usecase.GetPrayerTimesUseCase",
                                  "import com.ub.islamicapp.domain.usecase.GetPrayerTimesUseCase\nimport com.ub.islamicapp.screens.home.viewmodel.PrayerTime")
    with open(qibla_vm_path, "w") as f:
        f.write(content)

app_nav_path = "app/src/main/java/com/ub/islamicapp/presentation/navigation/AppNavigation.kt"
if os.path.exists(app_nav_path):
    with open(app_nav_path, "r") as f:
        content = f.read()
        content = content.replace("import com.ub.islamicapp.presentation.screens.HomeScreen", "import com.ub.islamicapp.screens.home.screen.HomeScreen")
        content = content.replace("import com.ub.islamicapp.presentation.screens.HijriCalendarScreen", "import com.ub.islamicapp.screens.calendar.screen.HijriCalendarScreen")
        content = content.replace("import com.ub.islamicapp.presentation.screens.GregorianCalendarScreen", "import com.ub.islamicapp.screens.calendar.screen.GregorianCalendarScreen")
        content = content.replace("com.ub.islamicapp.presentation.screens.prayers.screen.PrayerTimesScreen", "com.ub.islamicapp.screens.prayers.screen.PrayerTimesScreen")
        content = content.replace("com.ub.islamicapp.presentation.screens.QiblaScreen", "com.ub.islamicapp.screens.qibla.screen.QiblaScreen")
        content = content.replace("com.ub.islamicapp.presentation.screens.PrayerNotificationsScreen", "com.ub.islamicapp.screens.notifications.screen.PrayerNotificationsScreen")
        content = content.replace("com.ub.islamicapp.presentation.screens.SelectLocationScreen", "com.ub.islamicapp.screens.location.screen.SelectLocationScreen")

    with open(app_nav_path, "w") as f:
        f.write(content)
