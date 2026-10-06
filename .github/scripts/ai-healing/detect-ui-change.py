#!/usr/bin/env python3
import json, sys, re

with open("failure-analysis.json", "r") as f:
    failures = json.load(f)

if not failures:
    sys.exit(1)

UI_PATTERNS = [
    r"NoSuchElementException",
    r"ElementNotInteractableException",
    r"ElementNotVisibleException",
    r"StaleElementReferenceException",
    r"TimeoutException.*element",
    r"Could not find element",
]

TEST_PATTERNS = [
    r"NullPointerException",
    r"IllegalStateException",
    r"IndexOutOfBoundsException",
    r"AssertionError",
]

ui_score = sum(
    1
    for f in failures
    for p in UI_PATTERNS
    if re.search(p, f.get("error_message", "") + f.get("stack_trace", ""), re.I)
)
test_score = sum(
    1
    for f in failures
    for p in TEST_PATTERNS
    if re.search(p, f.get("error_message", "") + f.get("stack_trace", ""), re.I)
)

ui_pct = (ui_score / len(failures) * 100) if failures else 0
print(f"UI: {ui_pct:.1f}%, Test: {test_score}/{len(failures)}", file=sys.stderr)

sys.exit(0 if ui_pct >= 70 else 1)
