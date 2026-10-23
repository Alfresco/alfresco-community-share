#!/usr/bin/env python3
import xml.etree.ElementTree as ET
import json, glob, sys, os, argparse

parser = argparse.ArgumentParser(description="Analyze Surefire XML reports for test failures")
parser.add_argument("report_dir", nargs="?", help="Path to surefire-reports directory")
args = parser.parse_args()

# Resolution order: CLI arg → env var → default
test_dir = (
    args.report_dir
    or os.environ.get("TEST_REPORTS_DIR")
    or "alfresco-tas-share-test/target/surefire-reports"
)

failures = []

# ── Guard: directory must exist ──────────────────────────────────────────────
if not os.path.exists(test_dir):
    print(f"ERROR: Test report directory not found: {test_dir}", file=sys.stderr)
    print("Test execution may have failed before generating reports.", file=sys.stderr)
    print(json.dumps([]))
    sys.exit(0)

xml_files = glob.glob(f"{test_dir}/TEST-*.xml")

# ── Warn if no XML files found ────────────────────────────────────────────────
if not xml_files:
    print(f"WARNING: No TEST-*.xml files found in {test_dir}", file=sys.stderr)
    print("Directory contents:", file=sys.stderr)
    for item in os.listdir(test_dir):
        print(f"  - {item}", file=sys.stderr)

    # Surface Surefire dump files (JVM / fork crashes)
    dump_files = sorted(
        glob.glob(f"{test_dir}/*.dump") + glob.glob(f"{test_dir}/*.dumpstream")
    )
    if dump_files:
        print(
            "Detected Surefire dump files (possible fork/JVM crash):",
            file=sys.stderr,
        )
        for dump in dump_files:
            print(f"  - {dump}", file=sys.stderr)

# ── Parse each XML report ─────────────────────────────────────────────────────
for xml_file in xml_files:
    try:
        tree = ET.parse(xml_file)
        for testcase in tree.findall(".//testcase"):
            failure = testcase.find("failure")
            if failure is None:
                failure = testcase.find("error")
            if failure is not None:
                failures.append(
                    {
                        "test_class": testcase.get("classname", ""),
                        "test_method": testcase.get("name", ""),
                        "error_type": failure.get("type", ""),
                        "error_message": failure.get("message", ""),
                        "stack_trace": failure.text or "",
                        "xml_file": xml_file,
                    }
                )
    except Exception as e:
        print(f"Error parsing {xml_file}: {e}", file=sys.stderr)

print(
    f"Analyzed {len(xml_files)} XML files, found {len(failures)} failures",
    file=sys.stderr,
)
print(json.dumps(failures, indent=2))
