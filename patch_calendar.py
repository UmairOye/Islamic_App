import re

file_path = "app/src/main/java/com/ub/islamicapp/presentation/components/CalendarView.kt"

with open(file_path, 'r') as f:
    content = f.read()

# Make the outer Column vertically scrollable
content = re.sub(r'Column\(\s*modifier\s*=\s*modifier\s*\n\s*\.fillMaxWidth\(\)\n\s*\.padding\(horizontal\s*=\s*16\.dp,\s*vertical\s*=\s*8\.dp\),\s*horizontalAlignment\s*=\s*Alignment\.CenterHorizontally\s*\)',
                 '''Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )''', content)

content = re.sub(r'Column\(modifier\s*=\s*modifier\.fillMaxWidth\(\)\.padding\(16\.dp\)\)',
                 '''Column(modifier = modifier.fillMaxWidth().verticalScroll(androidx.compose.foundation.rememberScrollState()).padding(16.dp))''', content)

with open(file_path, 'w') as f:
    f.write(content)
