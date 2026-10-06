#!/usr/bin/env python3
import boto3, json, os, sys, glob
from pathlib import Path

AWS_REGION = os.environ.get("AWS_REGION", "us-east-1")
TEST_DIR = os.environ.get(
    "TEST_REPORTS_DIR", "alfresco-tas-share-test/target/surefire-reports"
)

# ── Bedrock client ────────────────────────────────────────────────────────────
try:
    bedrock = boto3.client(
        service_name="bedrock-runtime",
        region_name=AWS_REGION,
        aws_access_key_id=os.environ.get("AGS_AWS_ACCESS_KEY_ID"),
        aws_secret_access_key=os.environ.get("AGS_AWS_SECRET_ACCESS_KEY"),
    )
except Exception as e:
    print(f"ERROR: Failed to create Bedrock client: {e}", file=sys.stderr)
    sys.exit(1)

# ── Load failures ─────────────────────────────────────────────────────────────
with open("failure-analysis.json", "r") as f:
    failures = json.load(f)

if not failures:
    sys.exit(0)

# Group failures by test class
by_class: dict = {}
for failure in failures:
    cls = failure["test_class"]
    by_class.setdefault(cls, []).append(failure)

fixed: list = []
prompts_used: list = []

# ── Process each failing class ────────────────────────────────────────────────
for test_class, class_failures in by_class.items():
    file_path = test_class.replace(".", "/") + ".java"
    possible = list(Path(".").glob(f"**/{file_path}"))

    if not possible:
        print(f"Source file not found for: {test_class}", file=sys.stderr)
        continue

    actual = str(possible[0])
    with open(actual, "r", encoding="utf-8") as f:
        code = f.read()

    # Read relevant surefire text logs for extra context
    class_simple_name = test_class.split(".")[-1]
    logs = ""
    log_files = glob.glob(f"{TEST_DIR}/{class_simple_name}*.txt") or glob.glob(
        f"{TEST_DIR}/*.txt"
    )[:3]
    for log in log_files[:3]:
        try:
            with open(log, "r", encoding="utf-8", errors="ignore") as f:
                logs += f.read()[:3000]
        except Exception:
            pass

    # ── Build prompt ──────────────────────────────────────────────────────────
    failing_methods = [f["test_method"] for f in class_failures]

    prompt = (
        "You are a Selenium/Java test engineer specializing in flaky/intermittent test fixes.\n"
        "Your task: diagnose and fix ONLY test code issues in this Alfresco Share test file.\n\n"
        "## Framework\n"
        "  timing issues with Alfresco async operations\n\n"
        "## Failure Context\n"
        f"## Failing Tests in: {actual}\n"
        f"Methods: {', '.join(failing_methods)}\n\n"
        f"## Failure Details\n{json.dumps(class_failures, indent=2)}\n\n"
        f"## Full Test File\n```java\n{code}\n```\n\n"
    )
    if logs:
        prompt += f"## Relevant Logs\n{logs}\n\n"
    prompt += (
        "## Rules\n"
        "1. Fix ONLY timing/wait/sync issues - do NOT change assertion logic unless it is a wait issue\n"
        "2. Do NOT add @Ignore or @Test(enabled=false)\n"
        "3. Do NOT refactor or rename anything\n"
        "4. If failures indicate genuine UI element changes (locators removed from DOM), set is_test_code_issue=false\n"
        "5. Return the COMPLETE fixed Java file in fixed_code (not just the changed method)\n\n"
        'Return ONLY valid JSON (no markdown fences): '
        '{"analysis": "...", "is_test_code_issue": true/false, '
        '"fixed_code": "complete file content", "changes_made": ["..."]}'
    )

    print("\n=== Prompt sent to Claude ===", file=sys.stderr)
    print(f"File: {actual}", file=sys.stderr)
    print(f"Test Class: {test_class}", file=sys.stderr)
    print(prompt, file=sys.stderr)
    print("=== End Prompt ===\n", file=sys.stderr)

    prompts_used.append(
        {
            "file": actual,
            "test_class": test_class,
            "prompt": prompt,
            "prompt_size": len(prompt),
        }
    )

    # ── Call Bedrock ──────────────────────────────────────────────────────────
    try:
        request_body = json.dumps(
            {
                "anthropic_version": "bedrock-2023-05-31",
                "max_tokens": 8000,
                "temperature": 0.1,
                "messages": [{"role": "user", "content": prompt}],
            }
        )

        response = bedrock.invoke_model(
            modelId="anthropic.claude-sonnet-4-5-20250929-v1:0",
            body=request_body,
        )

        response_body = json.loads(response["body"].read())
        resp_text = response_body["content"][0]["text"].strip()

        # Strip any accidental markdown fences
        if resp_text.startswith("```"):
            lines = resp_text.split("\n")
            resp_text = "\n".join(
                line for line in lines if not line.strip().startswith("```")
            )

        resp = json.loads(resp_text)

        if not resp.get("is_test_code_issue"):
            print(f"UI change detected (skipping auto-fix): {actual}", file=sys.stderr)
            continue

        if resp.get("fixed_code"):
            with open(actual, "w", encoding="utf-8") as f:
                f.write(resp["fixed_code"])
            fixed.append(
                {
                    "file": actual,
                    "changes": resp.get("changes_made", []),
                    "analysis": resp.get("analysis", "No analysis provided"),
                }
            )
            print(f"Fixed: {actual}", file=sys.stderr)

    except Exception as e:
        error_msg = str(e)
        print(f"Error processing {actual}: {e}", file=sys.stderr)

        if "AccessDeniedException" in error_msg and "bedrock:InvokeModel" in error_msg:
            print("\n⚠️  IAM PERMISSION MISSING", file=sys.stderr)
            print(
                "    The AWS user needs 'bedrock:InvokeModel' permission.", file=sys.stderr
            )
            print("    See: .github/FIX_BEDROCK_PERMISSIONS.md", file=sys.stderr)
        elif "ValidationException" in error_msg and "foundation-model" in error_msg:
            print("\n⚠️  MODEL NOT AVAILABLE", file=sys.stderr)
            print(
                "    Claude Sonnet 4.5 may not be available in this region.",
                file=sys.stderr,
            )
            print(
                "    Check AWS Bedrock Console → Model access", file=sys.stderr
            )
        elif (
            "ThrottlingException" in error_msg
            or "TooManyRequestsException" in error_msg
        ):
            print("\n⚠️  RATE LIMIT EXCEEDED", file=sys.stderr)
            print(
                "    Bedrock API rate limit hit. Consider adding delays between requests.",
                file=sys.stderr,
            )

# ── Write summary ─────────────────────────────────────────────────────────────
with open("healing-summary.json", "w") as f:
    json.dump(
        {
            "fixed_files": fixed,
            "total_failures": len(failures),
            "prompts_used": prompts_used,
        },
        f,
        indent=2,
    )

sys.exit(0 if fixed else 1)
