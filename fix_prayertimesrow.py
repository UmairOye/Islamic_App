import os

ptrow_path = "app/src/main/java/com/ub/islamicapp/screens/home/components/PrayerTimesRow.kt"
with open(ptrow_path, "r") as f:
    content = f.read()

content = content.replace("package com.ub.islamicapp.presentation.components", "package com.ub.islamicapp.screens.home.components")
content = content.replace("import com.ub.islamicapp.presentation.state.PrayerTime", "import com.ub.islamicapp.screens.home.viewmodel.PrayerTime")

with open(ptrow_path, "w") as f:
    f.write(content)
