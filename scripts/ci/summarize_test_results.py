import html
import re
from collections import defaultdict
from pathlib import Path
import xml.etree.ElementTree as ET


TARGET_JOBS = ("test", "enterprise_shared_UI_configuration")
JOB_LABELS = {
    "test": "test",
    "enterprise_shared_UI_configuration": "enterprise_shared_UI_configuration",
}


def empty_summary():
    return {
        "total": 0,
        "passed": 0,
        "failed": 0,
        "skipped": 0,
        "flaky": 0,
        "duplicates": [],
        "flaky_tests": [],
    }


def detect_artifact(xml_path: str):
    path = Path(xml_path)
    try:
        return path.parts[path.parts.index("artifacts") + 1]
    except (ValueError, IndexError):
        return None


def job_for_artifact(artifact_name):
    if artifact_name is None:
        return None
    if artifact_name.endswith("-tests"):
        return "test"
    if artifact_name.startswith("ags-"):
        return "enterprise_shared_UI_configuration"
    return None


def detect_job(xml_path: str):
    return job_for_artifact(detect_artifact(xml_path))


def attempt_status(testcase):
    """Status of a single <testcase> element, i.e. one invocation of a test method."""
    if testcase.find("failure") is not None or testcase.find("error") is not None:
        return "failed"
    if testcase.find("skipped") is not None:
        return "skipped"
    return "passed"


INVOCATION_INDEX = re.compile(r"\((\d+)\)$")


def split_invocation(name):
    """Split ``method[params](N)`` into base name and invocation index.

    ``(N)`` increments on retries so it is not part of a test's identity; ``[params]``
    is kept because it tells one data-provider row from another.
    """
    if not name:
        return name, None
    match = INVOCATION_INDEX.search(name)
    if not match:
        return name, None
    return name[:match.start()], int(match.group(1))


def group_invocations(entries):
    """Rebuild logical invocations from the raw attempt stream of one base name.

    No index means one invocation. Otherwise TestNG marks a retried-away attempt
    ``skipped``, so a run of skips belongs to the non-skipped attempt after it;
    trailing skips are genuine skips.
    """
    if all(entry["index"] is None for entry in entries):
        return [entries]

    invocations = []
    pending = []
    for entry in entries:
        if entry["status"] == "skipped":
            pending.append(entry)
            continue
        invocations.append(pending + [entry])
        pending = []
    invocations.extend([entry] for entry in pending)
    return invocations


def order_entries(entries):
    """Order attempts of one base name; the invocation index is authoritative."""
    if all(entry["index"] is not None for entry in entries):
        return sorted(entries, key=lambda entry: entry["index"])
    return entries


def artifact_status(attempts):
    """Collapse every attempt of one invocation into a single status.

    RetryAnalyzer reruns failures, so passing only after more than one attempt is flaky.
    """
    if "passed" in attempts:
        return "flaky" if len(attempts) > 1 else "passed"
    if "failed" in attempts:
        return "failed"
    return "skipped"


def overall_status(statuses):
    """Collapse the same invocation seen in several artifacts into one status.

    A workflow re-run leaves both attempts' artifacts in place; counting once keeps
    the totals equal to the real suite size.
    """
    if "flaky" in statuses:
        return "flaky"
    passed = "passed" in statuses
    failed = "failed" in statuses
    if passed:
        return "flaky" if failed else "passed"
    if failed:
        return "failed"
    return "skipped"


ARTIFACT_ROOT = Path("artifacts")
job_summaries = {job: empty_summary() for job in TARGET_JOBS}
warnings = []


def warn(message):
    """Record a warning and surface it as a GitHub annotation.

    Reporting problems must be visible without ever failing the job, which would
    otherwise hold up the release.
    """
    warnings.append(message)
    print(f"::warning title=Test Summary::{message}")


def find_report_files(artifact_dir: Path):
    """Locate the Surefire XML of one artifact, whichever layout it was uploaded in.

    upload-artifact roots the archive at the common ancestor of the files it matched,
    so an artifact whose run produced no reports/ or screenshots/ has its XML at the
    root instead of under surefire-reports/.
    """
    nested = sorted(str(path) for path in artifact_dir.glob("**/surefire-reports/TEST-*.xml"))
    if nested:
        return nested
    return sorted(str(path) for path in artifact_dir.glob("**/TEST-*.xml"))


def suite_counter():
    return {"total": 0, "passed": 0, "failed": 0, "skipped": 0, "flaky": 0}


# job -> (classname, base name) -> artifact -> [attempt, ...]. Everything is parsed
# before tallying: forkCount=2 can split one class's attempts over several files.
job_runs = {job: defaultdict(lambda: defaultdict(list)) for job in TARGET_JOBS}
suite_counts = {job: defaultdict(suite_counter) for job in TARGET_JOBS}

try:
    # A partial download reads "0 failed", indistinguishable from a clean run,
    # so every gap is called out explicitly.
    artifact_dirs = (
        sorted(path.name for path in ARTIFACT_ROOT.iterdir() if path.is_dir())
        if ARTIFACT_ROOT.is_dir()
        else []
    )
    if not artifact_dirs:
        warn(
            "No artifacts were found. The downloads may have failed, or no test job "
            "uploaded results. The figures below are not a valid test run."
        )

    files_by_artifact = defaultdict(list)
    for artifact in artifact_dirs:
        files_by_artifact[artifact] = find_report_files(ARTIFACT_ROOT / artifact)
    xml_files = sorted(
        xml_file for files in files_by_artifact.values() for xml_file in files
    )

    for artifact in artifact_dirs:
        if job_for_artifact(artifact) is None:
            warn(
                f"Artifact '{artifact}' does not belong to any reported job and was "
                "ignored. Check the artifact naming if its results were expected."
            )
        elif not files_by_artifact.get(artifact):
            warn(
                f"Artifact '{artifact}' contains no test reports, so none of its "
                "tests are counted. The suite most likely never ran."
            )

    for xml_file in xml_files:
        job = detect_job(xml_file)
        if job not in job_runs:
            continue

        artifact = detect_artifact(xml_file)
        try:
            tree = ET.parse(xml_file)
            root = tree.getroot()
            for testcase in root.findall(".//testcase"):
                base_name, index = split_invocation(testcase.attrib.get("name"))
                key = (testcase.attrib.get("classname"), base_name)
                job_runs[job][key][artifact].append(
                    {
                        "index": index,
                        "status": attempt_status(testcase),
                    }
                )
        except (ET.ParseError, FileNotFoundError, OSError) as error:
            warn(f"Could not parse {xml_file}: {error}")
except Exception as error:  # noqa: BLE001 - a summary must always be produced
    warn(f"Unexpected error while collecting test results: {error!r}")


for job in TARGET_JOBS:
    summary = job_summaries[job]
    try:
        for key in sorted(job_runs[job], key=lambda item: (item[0] or "", item[1] or "")):
            classname, base_name = key

            # Per artifact, rebuild the logical invocations of this base name.
            artifacts = sorted(job_runs[job][key])
            per_artifact = []
            for artifact in artifacts:
                entries = order_entries(job_runs[job][key][artifact])
                per_artifact.append(
                    [
                        artifact_status([attempt["status"] for attempt in invocation])
                        for invocation in group_invocations(entries)
                    ]
                )

            # Several artifacts of one job means it ran more than once (suite overlap
            # or a re-run). Counted once, but reported so the redundancy is visible.
            if len(artifacts) > 1:
                summary["duplicates"].append(
                    {
                        "name": base_name,
                        "classname": classname,
                        "artifacts": artifacts,
                    }
                )

            # Per suite counts are what that suite itself reported, so a run-to-run
            # difference can be traced to the leg that caused it.
            for artifact, invocations in zip(artifacts, per_artifact):
                counts = suite_counts[job][artifact]
                for invocation_status in invocations:
                    counts["total"] += 1
                    counts[invocation_status] += 1

            # Data-provider rows are emitted in a deterministic order, so a row is
            # matched across artifacts by position rather than counted twice.
            for position in range(max(len(invocations) for invocations in per_artifact)):
                seen = [
                    invocations[position]
                    for invocations in per_artifact
                    if position < len(invocations)
                ]
                status = overall_status(seen)
                summary["total"] += 1
                summary[status] += 1
                if status == "flaky":
                    # Flaky from one artifact is a real retry; flaky because two suites
                    # disagreed is a hidden failure and must not read the same way.
                    summary["flaky_tests"].append(
                        {
                            "name": base_name,
                            "classname": classname,
                            "artifacts": artifacts,
                            "divergent": len(set(seen)) > 1,
                        }
                    )
    except Exception as error:  # noqa: BLE001 - a summary must always be produced
        warn(f"Unexpected error while summarising job '{job}': {error!r}")


total = empty_summary()
for job in TARGET_JOBS:
    for key in ("total", "passed", "failed", "skipped", "flaky"):
        total[key] += job_summaries[job][key]

for job in TARGET_JOBS:
    if job_summaries[job]["total"] == 0:
        warn(
            f"No test results were counted for job '{job}'. A zero row is not evidence "
            "that the job passed."
        )


table = [
    "## Automation Test Summary",
    "",
    "| Job | Total | Passed | Failed | Skipped | Flaky |",
    "|-----|------:|-------:|-------:|--------:|------:|",
]
for job in TARGET_JOBS:
    data = job_summaries[job]
    table.append(
        f"| `{JOB_LABELS[job]}` | {data['total']} | {data['passed']} | {data['failed']} | {data['skipped']} | {data['flaky']} |"
    )
table.append(
    f"| **Total** | **{total['total']}** | **{total['passed']}** | **{total['failed']}** | **{total['skipped']}** | **{total['flaky']}** |"
)

if warnings:
    table.append("")
    table.append(
        f"> [!WARNING]\n"
        f"> **The results above may be incomplete ({len(warnings)} problem(s) detected).**\n"
        "> This has not failed the build; the figures are reported as-is."
    )
    table.append("")
    table.append("**Warnings:**")
    for warning in warnings:
        table.append(f"- {warning}")

duplicate_count = sum(len(job_summaries[job]["duplicates"]) for job in TARGET_JOBS)
flaky_total = sum(len(job_summaries[job]["flaky_tests"]) for job in TARGET_JOBS)
divergent = [
    (job, entry)
    for job in TARGET_JOBS
    for entry in job_summaries[job]["flaky_tests"]
    if entry["divergent"]
]

# Only suites with something to explain are listed; a clean suite adds no information.
noteworthy = [
    (job, suite, counts)
    for job in TARGET_JOBS
    for suite, counts in sorted(suite_counts[job].items())
    if counts["failed"] or counts["skipped"]
]
if noteworthy:
    table.append("")
    table.append("<details>")
    table.append(
        f"<summary><strong>Per-suite breakdown</strong> "
        f"({len(noteworthy)} suite(s) with failures or skips)</summary>"
    )
    table.append("")
    table.append("| Job | Suite | Total | Passed | Failed | Skipped | Flaky |")
    table.append("|-----|-------|------:|-------:|-------:|--------:|------:|")
    for job, suite, counts in noteworthy:
        table.append(
            f"| `{JOB_LABELS[job]}` | `{suite}` | {counts['total']} | {counts['passed']} "
            f"| {counts['failed']} | {counts['skipped']} | {counts['flaky']} |"
        )
    table.append("")
    table.append(
        "Counts are what each suite reported on its own, so a test that ran in two "
        "suites is listed under both."
    )
    table.append("</details>")

if duplicate_count:
    table.append("")
    table.append("<details>")
    table.append(
        f"<summary><strong>Duplicate executions</strong> "
        f"({duplicate_count} test(s) run more than once)</summary>"
    )
    table.append("")
    table.append(
        "Counted once each in the table above. Running the same test in several suites "
        "of one job is redundant, and a test that passes in one suite while failing in "
        "another is reported as flaky rather than failed."
    )
    for job in TARGET_JOBS:
        duplicates = job_summaries[job]["duplicates"]
        if not duplicates:
            continue
        table.append("")
        table.append(f"**`{JOB_LABELS[job]}`**")
        table.append("")
        for duplicate in duplicates:
            artifacts = ", ".join(f"`{artifact}`" for artifact in duplicate["artifacts"])
            table.append(
                f"- `{duplicate['classname']}.{duplicate['name']}` — ran in: {artifacts}"
            )
    table.append("</details>")

if flaky_total:
    table.append("")
    table.append("<details>")
    table.append(
        f"<summary><strong>Flaky tests</strong> ({flaky_total} test(s)"
        + (f", {len(divergent)} from disagreeing suites" if divergent else "")
        + ")</summary>"
    )
    table.append("")
    table.append(
        "A test is flaky when it needed more than one attempt to pass. Entries marked "
        "**suite disagreement** passed in one suite and failed in another, so they are "
        "a masked failure rather than a retry."
    )
    for job in TARGET_JOBS:
        flaky_tests = job_summaries[job]["flaky_tests"]
        if not flaky_tests:
            continue
        table.append("")
        table.append(f"**`{JOB_LABELS[job]}`**")
        table.append("")
        for entry in flaky_tests:
            suffix = ""
            if entry["divergent"]:
                artifacts = ", ".join(f"`{artifact}`" for artifact in entry["artifacts"])
                suffix = f" — **suite disagreement** across: {artifacts}"
            table.append(f"- `{entry['classname']}.{entry['name']}`{suffix}")
    table.append("</details>")

try:
    with open("test-summary.md", "w", encoding="utf-8") as summary_markdown:
        summary_markdown.write("\n".join(table))
except OSError as error:
    print(f"::warning title=Test Summary::Could not write test-summary.md: {error}")

table_html = """
<html><body>
<h2>Automation Test Summary</h2>
<table border='1' cellpadding='5' cellspacing='0'>
<tr><th>Job</th><th>Total</th><th>Passed</th><th>Failed</th><th>Skipped</th><th>Flaky</th></tr>
"""
for job in TARGET_JOBS:
    data = job_summaries[job]
    table_html += (
        f"<tr><td>{JOB_LABELS[job]}</td><td>{data['total']}</td><td>{data['passed']}</td><td>{data['failed']}</td>"
        f"<td>{data['skipped']}</td><td>{data['flaky']}</td></tr>"
    )
table_html += (
    f"<tr><td><strong>Total</strong></td><td><strong>{total['total']}</strong></td><td><strong>{total['passed']}</strong></td>"
    f"<td><strong>{total['failed']}</strong></td><td><strong>{total['skipped']}</strong></td><td><strong>{total['flaky']}</strong></td></tr>"
)
table_html += "</table>"

if warnings:
    table_html += (
        f"<p style='color:#b35c00'><strong>The results above may be incomplete "
        f"({len(warnings)} problem(s) detected).</strong> This has not failed the build; "
        "the figures are reported as-is.</p>"
    )
    table_html += "<h3>Warnings</h3><ul>"
    for warning in warnings:
        table_html += f"<li>{html.escape(warning)}</li>"
    table_html += "</ul>"

if noteworthy:
    table_html += (
        "<details><summary><strong>Per-suite breakdown</strong> "
        f"({len(noteworthy)} suite(s) with failures or skips)</summary>"
        "<table border='1' cellpadding='5' cellspacing='0'>"
        "<tr><th>Job</th><th>Suite</th><th>Total</th><th>Passed</th><th>Failed</th>"
        "<th>Skipped</th><th>Flaky</th></tr>"
    )
    for job, suite, counts in noteworthy:
        table_html += (
            f"<tr><td>{html.escape(JOB_LABELS[job])}</td><td>{html.escape(suite)}</td>"
            f"<td>{counts['total']}</td><td>{counts['passed']}</td><td>{counts['failed']}</td>"
            f"<td>{counts['skipped']}</td><td>{counts['flaky']}</td></tr>"
        )
    table_html += (
        "</table><p>Counts are what each suite reported on its own, so a test that ran "
        "in two suites is listed under both.</p></details>"
    )

if duplicate_count:
    table_html += (
        "<details><summary><strong>Duplicate executions</strong> "
        f"({duplicate_count} test(s) run more than once)</summary>"
        "<p>Counted once each in the table above. Running the same test in several "
        "suites of one job is redundant, and a test that passes in one suite while "
        "failing in another is reported as flaky rather than failed.</p>"
    )
    for job in TARGET_JOBS:
        duplicates = job_summaries[job]["duplicates"]
        if not duplicates:
            continue
        table_html += f"<h4>{html.escape(JOB_LABELS[job])}</h4><ul>"
        for duplicate in duplicates:
            name = f"{duplicate['classname']}.{duplicate['name']}"
            artifacts = ", ".join(duplicate["artifacts"])
            table_html += (
                f"<li>{html.escape(name)} &mdash; ran in: {html.escape(artifacts)}</li>"
            )
        table_html += "</ul>"
    table_html += "</details>"

if flaky_total:
    table_html += (
        "<details><summary><strong>Flaky tests</strong> "
        f"({flaky_total} test(s)"
        + (f", {len(divergent)} from disagreeing suites" if divergent else "")
        + ")</summary>"
        "<p>A test is flaky when it needed more than one attempt to pass. Entries marked "
        "<strong>suite disagreement</strong> passed in one suite and failed in another, "
        "so they are a masked failure rather than a retry.</p>"
    )
    for job in TARGET_JOBS:
        flaky_tests = job_summaries[job]["flaky_tests"]
        if not flaky_tests:
            continue
        table_html += f"<h4>{html.escape(JOB_LABELS[job])}</h4><ul>"
        for entry in flaky_tests:
            name = f"{entry['classname']}.{entry['name']}"
            suffix = ""
            if entry["divergent"]:
                artifacts = ", ".join(entry["artifacts"])
                suffix = (
                    f" &mdash; <strong>suite disagreement</strong> across: "
                    f"{html.escape(artifacts)}"
                )
            table_html += f"<li>{html.escape(name)}{suffix}</li>"
        table_html += "</ul>"
    table_html += "</details>"

table_html += "</body></html>"

try:
    with open("test-summary.html", "w", encoding="utf-8") as summary_html:
        summary_html.write(table_html)
except OSError as error:
    print(f"::warning title=Test Summary::Could not write test-summary.html: {error}")
