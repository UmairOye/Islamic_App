import os

home_screen_path = "app/src/main/java/com/ub/islamicapp/screens/home/screen/HomeScreen.kt"
if os.path.exists(home_screen_path):
    with open(home_screen_path, "r") as f:
        content = f.read()
    if "import com.ub.islamicapp.screens.home.components.HomeTopHeader" not in content:
        content = content.replace("import com.ub.islamicapp.screens.home.components.LastReadCard",
                                  "import com.ub.islamicapp.screens.home.components.LastReadCard\nimport com.ub.islamicapp.screens.home.components.HomeTopHeader")
    with open(home_screen_path, "w") as f:
        f.write(content)
