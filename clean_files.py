import os
import re

for root, dirs, files in os.walk('app/src/main/java'):
    for file in files:
        if file.endswith('.kt'):
            file_path = os.path.join(root, file)
            with open(file_path, 'r') as f:
                content = f.read()

            # Remove single-line comments except those starting with `// noinspection`
            content = re.sub(r'(?<!http:)(?<!https:)//(?!\s*noinspection).*', '', content)

            # Clean up empty lines created
            content = re.sub(r'\n\s*\n', '\n\n', content)

            with open(file_path, 'w') as f:
                f.write(content)
