# Quick Reference

## Decision Tree When Tests Fail

```
✅ "All tests passed" → SUCCESS (done)
❌ "Tests failed"
  ├─ Retry #2 automatically starts
  ├─ "UI change detected" → MANUAL (update selectors)
  └─ "AI applied fixes" → Auto-fixed locally, retrying
    └─ "All tests passed" → SUCCESS (changes not committed)
    └─ Retry #3 → Same as #2
      └─ "Tests still failing" → FAIL (debug needed)
```

**Note**: AI healing fixes are applied locally during the test run but are NOT committed to the repository.

## Key Log Messages

| Message | Meaning | What to Do |
|---------|---------|-----------|
| `Found 62 test failures` | Failures analyzed | ✅ OK |
| `UI change detected` | 70%+ UI errors | 🔴 Update selectors manually |
| `Fixed: TestFile.java` | Code was healed locally | ✅ Review in artifacts |
| `Detected dump files` | JVM crashed | ℹ️ Auto-retrying |
| `All tests passed` | Success | ✅ Done (changes not committed) |
| `Tests still failing` | Max retries hit | 🔴 Debug needed |

## Enable/Disable Healing

```yaml
# .github/workflows/nightly-ci-build.yml line 47
ENABLE_AI_HEALING: "true"        # false to disable
MAX_HEALING_ATTEMPTS: 3          # change retry count
```

## Troubleshooting

| Issue | Fix |
|-------|-----|
| `AccessDeniedException` | Add AWS IAM permission for `bedrock:InvokeModel` |
| `No TEST-*.xml files` | Check if `.dump` files exist (JVM crash); auto-retried once |
| `XML validation failed` | Check `failure-analysis.json` is valid |
| Different failure counts | Normal with parameterized tests; see TEST_FAILURE_COUNTING.md |

## File Locations

```
Share tests:     alfresco-tas-share-test/src/test/java|resources/
Enterprise:      amps/ags/rm-automation/rm-automation-ui/src/test/java|resources/
Reports:         */target/surefire-reports/
Healing scripts:  .github/scripts/ai-healing/
```

## Local Testing Retry Suite

```bash
python3 .github/scripts/ai-healing/generate-retry-suite.py \
  --failures failure-analysis.json \
  --original-suite original.xml \
  --output retry.xml \
  --log-details
```

## AWS Setup

1. IAM: Add `bedrock:InvokeModel` permission
2. Console: Enable Claude Sonnet 4.5 model access
3. Secrets: Add `AGS_AWS_ACCESS_KEY_ID` and `AGS_AWS_SECRET_ACCESS_KEY`

See: `.github/docs/ai-healing-docs/FIX_BEDROCK_PERMISSIONS.md`

## Performance

- Share tests: 10-15m per attempt
- Enterprise: 15-30m per attempt
- Max 3 attempts = 45-120m total

