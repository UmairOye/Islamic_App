import re
import os

files = [
    "app/src/main/java/com/ub/islamicapp/presentation/components/CalendarView.kt",
    "app/src/main/java/com/ub/islamicapp/presentation/screens/CalendarScreens.kt"
]

for file_path in files:
    if os.path.exists(file_path):
        with open(file_path, 'r') as f:
            content = f.read()

        # Remove single-line comments
        content = re.sub(r'//.*', '', content)

        # Remove empty lines that might have been left behind
        content = re.sub(r'\n\s*\n', '\n\n', content)

        with open(file_path, 'w') as f:
            f.write(content)
