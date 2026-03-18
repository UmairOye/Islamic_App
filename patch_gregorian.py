import re

file_path = "app/src/main/java/com/ub/islamicapp/presentation/components/CalendarView.kt"

with open(file_path, 'r') as f:
    content = f.read()

content = re.sub(r'modifier\s*=\s*Modifier\.fillMaxWidth\(\)\.heightIn\(max\s*=\s*500\.dp\),\n\s*verticalArrangement\s*=\s*Arrangement\.spacedBy\(16\.dp\),\n\s*horizontalArrangement\s*=\s*Arrangement\.spacedBy\(8\.dp\),\n\s*userScrollEnabled\s*=\s*false',
                 '''modifier = Modifier.fillMaxWidth().height(350.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false''', content)

# Remove the `.heightIn(max = 500.dp)` entirely and set a proper `height(350.dp)` for Hijri grid as well so it fits its 6 rows comfortably without breaking the scroll.

content = re.sub(r'modifier\s*=\s*Modifier\.fillMaxWidth\(\)\.heightIn\(max\s*=\s*500\.dp\),\n\s*verticalArrangement\s*=\s*Arrangement\.spacedBy\(8\.dp\),\n\s*horizontalArrangement\s*=\s*Arrangement\.spacedBy\(4\.dp\),\n\s*userScrollEnabled\s*=\s*false',
                 '''modifier = Modifier.fillMaxWidth().height(350.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                userScrollEnabled = false''', content)


with open(file_path, 'w') as f:
    f.write(content)
