#!/usr/bin/env python3
import xml.etree.ElementTree as ET
import json, sys, argparse
from collections import defaultdict

parser = argparse.ArgumentParser(description="Generate a TestNG retry suite for failed tests")
parser.add_argument("--failures", required=True, help="Path to failure-analysis.json")
parser.add_argument("--original-suite", required=True, help="Path to original TestNG XML suite")
parser.add_argument("--output", required=True, help="Output path for retry suite XML")
args = parser.parse_args()

# ── Load failures ─────────────────────────────────────────────────────────────
with open(args.failures, "r") as f:
    failures = json.load(f)

if not failures:
    print("No failures to retry", file=sys.stderr)
    sys.exit(1)


def normalize_class_name(name: str) -> str:
    """Strip parenthetical suite suffixes that some Surefire versions append,
    e.g. 'com.example.MyTest(site-tests.xml)' → 'com.example.MyTest'."""
    if not name:
        return ""
    normalized = name.strip()
    if "(" in normalized and normalized.endswith(")"):
        normalized = normalized[: normalized.rfind("(")].strip()
    return normalized


# ── Group by class ────────────────────────────────────────────────────────────
failed_classes: dict[str, set] = defaultdict(set)
for failure in failures:
    test_class = normalize_class_name(failure.get("test_class", ""))
    test_method = (failure.get("test_method", "") or "").strip()
    if test_class and test_method:
        failed_classes[test_class].add(test_method)

print(
    f"Found {len(failed_classes)} failed test classes with {len(failures)} failed methods",
    file=sys.stderr,
)

# ── Parse original suite to preserve structure ────────────────────────────────
listeners_xml = ""
suite_attrs: dict = {}
try:
    tree = ET.parse(args.original_suite)
    root = tree.getroot()
    suite_attrs = dict(root.attrib)
    suite_attrs["name"] = f"{suite_attrs.get('name', 'Retry Suite')} - Retry"

    listeners = root.find("listeners")
    if listeners is not None:
        listeners_xml = ET.tostring(listeners, encoding="unicode")
except Exception as e:
    print(f"Warning: Could not parse original suite: {e}", file=sys.stderr)
    suite_attrs = {"name": "Retry Suite", "verbose": "1"}

# Ensure critical attributes are present
suite_attrs.setdefault("verbose", "1")
suite_attrs.setdefault("configfailurepolicy", "continue")

# ── Build retry suite ─────────────────────────────────────────────────────────
suite = ET.Element("suite", suite_attrs)

if listeners_xml:
    suite.append(ET.fromstring(listeners_xml))

test_elem = ET.SubElement(suite, "test", name="Failed Tests Retry")
classes_elem = ET.SubElement(test_elem, "classes")

for test_class, methods in sorted(failed_classes.items()):
    class_elem = ET.SubElement(classes_elem, "class", name=test_class)
    methods_elem = ET.SubElement(class_elem, "methods")
    for method in sorted(methods):
        ET.SubElement(methods_elem, "include", name=method)

# ── Write output with DOCTYPE ─────────────────────────────────────────────────
output_tree = ET.ElementTree(suite)
ET.indent(output_tree, space="  ")

with open(args.output, "w", encoding="utf-8") as f:
    f.write('<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >\n\n')
    output_tree.write(f, encoding="unicode", xml_declaration=False)

print(f"Generated retry suite: {args.output}", file=sys.stderr)
print(f"  - {len(failed_classes)} test classes", file=sys.stderr)
print(f"  - {len(failures)} test methods", file=sys.stderr)
