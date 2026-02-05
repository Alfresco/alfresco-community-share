import os
import glob
import xml.etree.ElementTree as ET

# Find all surefire XML reports
xml_files = glob.glob('artifacts/**/surefire-reports/TEST-*.xml', recursive=True)

summary = {
    'total': 0,
    'passed': 0,
    'failed': 0,
    'skipped': 0,
    'flaky': 0,
    'failures': []
}

def get_testcase_url(xml_path, testcase):
    # This is a placeholder. You can customize this to point to your Allure report or logs if published online.
    return f"{xml_path}#L1"

for xml_file in xml_files:
    tree = ET.parse(xml_file)
    root = tree.getroot()
    for testcase in root.findall('.//testcase'):
        summary['total'] += 1
        skipped = testcase.find('skipped') is not None
        failure = testcase.find('failure') is not None or testcase.find('error') is not None
        flaky = testcase.attrib.get('flaky') == 'true'
        if skipped:
            summary['skipped'] += 1
        elif failure:
            summary['failed'] += 1
            summary['failures'].append({
                'name': testcase.attrib.get('name'),
                'classname': testcase.attrib.get('classname'),
                'file': xml_file,
                'url': get_testcase_url(xml_file, testcase)
            })
        elif flaky:
            summary['flaky'] += 1
        else:
            summary['passed'] += 1

# Markdown summary
table = [
    '| Total | Passed | Failed | Skipped | Flaky |',
    '|-------|--------|--------|---------|-------|',
    f"| {summary['total']} | {summary['passed']} | {summary['failed']} | {summary['skipped']} | {summary['flaky']} |"
]

if summary['failures']:
    table.append('\n**Failed Tests:**')
    for fail in summary['failures']:
        # The URL is a placeholder; update if you have a real link
        table.append(f"- [{fail['classname']}.{fail['name']}]({fail['url']})")

with open('test-summary.md', 'w', encoding='utf-8') as f:
    f.write('\n'.join(table))

# HTML summary
table_html = f"""
<html><body>
<h2>Test Execution Summary</h2>
<table border='1' cellpadding='5' cellspacing='0'>
<tr><th>Total</th><th>Passed</th><th>Failed</th><th>Skipped</th><th>Flaky</th></tr>
<tr><td>{summary['total']}</td><td>{summary['passed']}</td><td>{summary['failed']}</td><td>{summary['skipped']}</td><td>{summary['flaky']}</td></tr>
</table>
"""
if summary['failures']:
    table_html += '<h3>Failed Tests</h3><ul>'
    for fail in summary['failures']:
        table_html += f"<li><a href='{fail['url']}'>{fail['classname']}.{fail['name']}</a></li>"
    table_html += '</ul>'
table_html += '</body></html>'

with open('test-summary.html', 'w', encoding='utf-8') as f:
    f.write(table_html)

