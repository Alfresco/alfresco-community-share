# AI Self-Healing Test Workflow

## Process Overview

```
Test Failure → Analyze → Detect UI change?
                           ├─ YES → Stop (manual review)
                           └─ NO → Invoke Claude AI → Fix code → Retry
```

## Step-by-Step Flow

| # | Step | Input | Output | On Failure |
|---|------|-------|--------|-----------|
| 1 | Run tests | Suite XML | Test results | Exit 0 if pass |
| 2 | Analyze | TEST-*.xml files | failure-analysis.json (62 failures) | Check for dump files, exit if none |
| 3 | Detect UI change | failure-analysis.json | UI% score | Stop if ≥70% UI patterns |
| 4 | Heal with Claude | Test code + failures | healing-summary.json | Exit if healing fails |
| 5 | Generate retry suite | failure-analysis.json | retry-suite-1.xml | Use original suite if fails |
| 6 | Rerun tests | retry-suite-1.xml | Test results (attempt 2) | Repeat steps 2-5 |

**Note**: Healed test files are NOT automatically committed. Changes remain local to the test run.

## Key Scripts

### analyze-failures.py
Parses TEST-*.xml files and extracts failures:
```bash
python3 .github/scripts/ai-healing/analyze-failures.py "$TEST_REPORTS_DIR"
```
Output: `failure-analysis.json` with [test_class, test_method, error_message, stack_trace]

### detect-ui-change.py
Scores error patterns (UI vs test code):
```bash
python3 .github/scripts/ai-healing/detect-ui-change.py
```
Exit: 0 if UI ≥70%, non-zero if test code issue

### heal-with-claude.py
Invokes Claude Sonnet 4.5 on AWS Bedrock:
```bash
python3 .github/scripts/ai-healing/heal-with-claude.py
```
Output: `healing-summary.json` with fixed files and changes

### generate-retry-suite.py
Creates TestNG XML with only failed tests:
```bash
python3 .github/scripts/ai-healing/generate-retry-suite.py \
  --failures failure-analysis.json \
  --original-suite original.xml \
  --output retry-suite-1.xml \
  --log-details
```

## Configuration

| Variable | Default | Purpose |
|----------|---------|---------|
| `ENABLE_AI_HEALING` | `true` | Enable/disable globally |
| `MAX_HEALING_ATTEMPTS` | `3` | Max retry attempts |
| `TEST_REPORTS_DIR` | Job-specific | Where Surefire writes XML |
| `AWS_REGION` | `us-east-1` | Bedrock region |

## AWS Setup Required

1. **Create IAM policy** with `bedrock:InvokeModel` permission
2. **Enable model access** in AWS Bedrock Console for Claude Sonnet 4.5
3. **Store credentials** in GitHub Secrets:
   - `AGS_AWS_ACCESS_KEY_ID`
   - `AGS_AWS_SECRET_ACCESS_KEY`

See: `.github/docs/ai-healing-docs/FIX_BEDROCK_PERMISSIONS.md`

## Job Implementation

### Share Tests (`test` job)
- Test code: `alfresco-tas-share-test/src/test/java/**/*.java`
- Test resources: `alfresco-tas-share-test/src/test/resources/test-suites/*.xml`
- Reports: `alfresco-tas-share-test/target/surefire-reports/`

### Enterprise Tests (`enterprise_shared_UI_configuration` job)
- Test code: `amps/ags/rm-automation/rm-automation-ui/src/test/java/**/*.java`
- Test resources: `amps/ags/rm-automation/rm-automation-ui/src/test/resources/*.xml`
- Reports: `amps/ags/rm-automation/rm-automation-ui/target/surefire-reports/`

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| `AccessDeniedException` | Missing IAM permission | See FIX_BEDROCK_PERMISSIONS.md |
| `ValidationException` | Model not available | Check AWS Bedrock Console |
| `No TEST-*.xml files` + dump files | JVM crash | Auto-retry once |
| `UI change detected` | 70%+ UI errors | Manual selector update needed |
| `Retry suite generation fails` | Invalid XML or missing file | Check original suite exists |

## Performance

| Component | Time |
|-----------|------|
| Failure analysis | <1s |
| UI change detection | <1s |
| Claude AI per class | 10-60s |
| Retry suite generation | <1s |
| Total per attempt | 10-120s |

## Files Modified

- `.github/workflows/nightly-ci-build.yml`: Main workflow
- `.github/scripts/ai-healing/`: Python helper scripts (created at runtime)
- Source code from failing tests: Auto-fixed by Claude (local changes only, not committed)

