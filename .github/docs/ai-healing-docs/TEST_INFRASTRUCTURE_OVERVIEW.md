# Test Infrastructure Overview

## Jobs & Configurations

| Job | Test Framework | Location | Reports | Matrix Size |
|-----|---|---|---|---|
| **test** | Selenium + TestNG | `alfresco-tas-share-test/` | `target/surefire-reports/` | 4 suites |
| **enterprise_shared_UI_configuration** | Selenium + TestNG | `amps/ags/rm-automation/rm-automation-ui/` | `target/surefire-reports/` | 10 suites |

## AI Healing Features

Both jobs have:
- ✅ Test failure analysis and retry
- ✅ Claude Sonnet 4.5 code fixing
- ✅ Dump file detection (JVM crash handling)
- ✅ Detailed logging of retried tests
- ✅ Automatic commit of healed code

## Process Flow

```
Test Fail → Analyze failures → Detect UI change?
              ├─ YES: Stop (requires manual fix)
              └─ NO: Claude fixes code → Retry only failed tests
```

## Configuration

**Enable/disable globally** (`.github/workflows/nightly-ci-build.yml` line 47):
```yaml
ENABLE_AI_HEALING: "true"        # Set to "false" to disable
MAX_HEALING_ATTEMPTS: 3          # Number of retry attempts
```

## AWS Requirements

- Create IAM user with `bedrock:InvokeModel` permission
- Enable Claude Sonnet 4.5 model access
- Add credentials to GitHub Secrets:
  - `AGS_AWS_ACCESS_KEY_ID`
  - `AGS_AWS_SECRET_ACCESS_KEY`

See: `.github/docs/ai-healing-docs/FIX_BEDROCK_PERMISSIONS.md` for details

## Adding New Test Suite

**Share tests**: Add to `.github/workflows/nightly-ci-build.yml` (lines 319-344)

**Enterprise tests**: Add to `.github/workflows/nightly-ci-build.yml` (lines 1113-1125)

Create `.xml` file in corresponding test resources directory

## Common Issues

| Issue | Solution |
|-------|----------|
| `AccessDeniedException` | AWS IAM permission missing (see FIX_BEDROCK_PERMISSIONS.md) |
| `UI change detected` | Manual selectors update needed; healing skipped |
| `No TEST-*.xml files` + dump files | JVM crash; auto-retried once |
| Test count mismatch | Normal with parameterized tests (see TEST_FAILURE_COUNTING.md) |

## Performance

- Share tests: 10-15m per attempt
- Enterprise tests: 15-30m per attempt  
- Max 3 attempts = ~30-60m total for full healing cycle

## Key Files

- `.github/workflows/nightly-ci-build.yml` - Main workflow
- `.github/scripts/ai-healing/` - Python helper scripts
- `.github/docs/ai-healing-docs/AI_HEALING_WORKFLOW.md` - Detailed workflow
- `.github/docs/ai-healing-docs/TEST_FAILURE_COUNTING.md` - Failure count explanation
- `.github/docs/ai-healing-docs/QUICK_REFERENCE.md` - Quick troubleshooting

