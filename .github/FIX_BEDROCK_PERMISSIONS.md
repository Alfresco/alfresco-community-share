# Fix AWS Bedrock Permissions for AI Test Healing

## Problem

The CI workflow's AI healing feature fails with:

```
AccessDeniedException: User: arn:aws:iam::586394462691:user/jenkins.ags.build 
is not authorized to perform: bedrock:InvokeModel on resource: 
arn:aws:bedrock:us-east-1::foundation-model/anthropic.claude-sonnet-4-5-20250929-v1:0
```

## Root Cause

The IAM user `jenkins.ags.build` lacks the `bedrock:InvokeModel` permission required to invoke Claude models through AWS Bedrock.

## Solution

Apply the IAM policy from [BEDROCK_IAM_POLICY.json](BEDROCK_IAM_POLICY.json) to the user.

---

## Option 1: AWS Console (Recommended for Quick Fix)

### Step 1: Create the IAM Policy

1. Open [AWS IAM Console - Policies](https://console.aws.amazon.com/iam/home#/policies)
2. Click **Create policy**
3. Switch to the **JSON** tab
4. Copy the contents of [.github/BEDROCK_IAM_POLICY.json](BEDROCK_IAM_POLICY.json) and paste it
5. Click **Next: Tags** (skip tags)
6. Click **Next: Review**
7. Enter policy name: `BedrockClaudeInvokePolicy`
8. Enter description: `Allows invoking Claude models via AWS Bedrock for test healing`
9. Click **Create policy**

### Step 2: Attach Policy to User

1. Open [AWS IAM Console - Users](https://console.aws.amazon.com/iam/home#/users)
2. Find and click on user: `jenkins.ags.build`
3. Click the **Permissions** tab
4. Click **Add permissions** → **Attach policies directly**
5. In the search box, type: `BedrockClaudeInvokePolicy`
6. Check the box next to the policy
7. Click **Add permissions**

### Step 3: Enable Bedrock Model Access

1. Open [AWS Bedrock Console - Model Access](https://console.aws.amazon.com/bedrock/home?region=us-east-1#/modelaccess)
2. Click **Manage model access** (or **Edit** if already managing)
3. Find **Claude 3.5 Sonnet** in the list
4. Check the box for **Claude 3.5 Sonnet**
5. Find **Claude Sonnet 4.5** in the list
6. Check the box for **Claude Sonnet 4.5**
7. Click **Request model access** at the bottom
8. Wait for approval (usually instant for existing accounts)

---

## Option 2: AWS CLI (For Automation)

### Prerequisites

- AWS CLI installed and configured
- IAM permissions to create policies and attach them to users

### Step 1: Create the IAM Policy

```bash
cd .github

aws iam create-policy \
  --policy-name BedrockClaudeInvokePolicy \
  --policy-document file://BEDROCK_IAM_POLICY.json \
  --description "Allows invoking Claude models via AWS Bedrock for AI test healing"
```

**Note the ARN** from the output. It will look like:
```
arn:aws:iam::586394462691:policy/BedrockClaudeInvokePolicy
```

### Step 2: Attach Policy to User

```bash
aws iam attach-user-policy \
  --user-name jenkins.ags.build \
  --policy-arn arn:aws:iam::586394462691:policy/BedrockClaudeInvokePolicy
```

### Step 3: Request Bedrock Model Access

**Note:** This step typically requires console access as the CLI API for requesting model access is limited. If you must use CLI:

```bash
# List currently available models
aws bedrock list-foundation-models --region us-east-1 --query 'modelSummaries[?contains(modelId, `claude`)].{id:modelId, name:modelName}'
```

For requesting access, use the [AWS Console](https://console.aws.amazon.com/bedrock/home?region=us-east-1#/modelaccess) instead.

---

## Verification

### Verify IAM Policy Attachment

```bash
aws iam list-attached-user-policies --user-name jenkins.ags.build
```

You should see `BedrockClaudeInvokePolicy` in the list.

### Verify Permissions with Policy Simulator

```bash
aws iam simulate-principal-policy \
  --policy-source-arn arn:aws:iam::586394462691:user/jenkins.ags.build \
  --action-names bedrock:InvokeModel \
  --resource-arns "arn:aws:bedrock:us-east-1::foundation-model/anthropic.claude-sonnet-4-5-20250929-v1:0"
```

**Expected Output:**
```json
{
    "EvaluationResults": [
        {
            "EvalActionName": "bedrock:InvokeModel",
            "EvalDecision": "allowed"
        }
    ]
}
```

If you see `"EvalDecision": "denied"`, the policy wasn't attached correctly.

### Test Bedrock Access Directly

```bash
aws bedrock-runtime invoke-model \
  --region us-east-1 \
  --model-id anthropic.claude-sonnet-4-5-20250929-v1:0 \
  --body '{"anthropic_version":"bedrock-2023-05-31","max_tokens":100,"messages":[{"role":"user","content":"Hello"}]}' \
  /tmp/bedrock-test-output.json

cat /tmp/bedrock-test-output.json | jq .
```

**Expected:** JSON response from Claude with a greeting.

---

## Re-run the Workflow

Once permissions are configured:

1. Go to your [GitHub Actions workflow runs](../../actions)
2. Find the failed run
3. Click **Re-run failed jobs**
4. Monitor the logs for:

```
🔧 Invoking Claude AI to heal tests...
Fixed: amps/ags/rm-automation/rm-automation-ui/src/test/java/org/alfresco/test/enterprise/security/guides/GuidedClassificationCrumbsTests.java
✏️ AI applied fixes. Generating retry suite with only failed tests...
```

---

## Cost Estimation

| Item | Cost per Request | Monthly Estimate |
|------|-----------------|------------------|
| Input tokens (~8,000) | $0.024 | - |
| Output tokens (~2,000) | $0.036 | - |
| **Per healing attempt** | **~$0.06** | - |
| **40 attempts/month** | - | **~$2.40** |

This is negligible compared to developer time saved debugging flaky tests.

---

## Troubleshooting

### Error: "Model not found"

**Cause:** Model access not enabled in AWS Bedrock.

**Fix:** Go to [Bedrock Model Access](https://console.aws.amazon.com/bedrock/home?region=us-east-1#/modelaccess) and request access to Claude models.

### Error: "AccessDeniedException" still occurs

**Possible causes:**

1. **Policy not attached:** Run verification commands above
2. **Wrong IAM user:** Verify the workflow uses `AGS_AWS_ACCESS_KEY_ID` and `AGS_AWS_SECRET_ACCESS_KEY` secrets that correspond to `jenkins.ags.build`
3. **Wrong region:** The workflow uses `us-east-1`. Ensure model access is requested in that region.
4. **Resource ARN mismatch:** The policy must match the exact model ID being invoked

### Error: "ThrottlingException" or "TooManyRequestsException"

**Cause:** Bedrock API rate limits exceeded.

**Default limits:** 
- 10 requests/minute (on-demand)
- 50 requests/minute (provisioned throughput)

**Fixes:**
1. Add delays between healing attempts (already implemented in workflow)
2. Request increased limits: [Bedrock Quotas Console](https://console.aws.amazon.com/servicequotas/home/services/bedrock/quotas)

### Healing succeeds but tests still fail

This is expected! The AI healing:
- **DOES NOT** fix actual UI changes in the application
- **ONLY** fixes test code issues (timing, waits, stale selectors)

If tests still fail after healing:
1. Check if a real UI change occurred (the workflow detects this via `detect-ui-change.py`)
2. Review the healing summary in the GitHub Actions job summary
3. Manually update the test to match the new UI behavior

---

## Security Considerations

### Principle of Least Privilege

The IAM policy grants **only** the permissions needed:
- `bedrock:InvokeModel` - Invoke models synchronously
- `bedrock:InvokeModelWithResponseStream` - Invoke models with streaming (future use)
- **Limited to:** Claude Sonnet models only (no access to other AWS services)

### Secrets Management

The workflow uses GitHub Actions secrets:
- `AGS_AWS_ACCESS_KEY_ID`
- `AGS_AWS_SECRET_ACCESS_KEY`

**Best practices:**
1. Rotate credentials regularly
2. Use dedicated IAM user for CI (already done: `jenkins.ags.build`)
3. Never commit credentials to the repository
4. Review [GitHub Actions logs redaction](https://docs.github.com/en/actions/security-guides/encrypted-secrets#reviewing-access-to-organization-level-secrets)

### Cost Controls

To prevent unexpected costs:

1. **Set AWS Budget Alerts:**
   ```bash
   aws budgets create-budget \
     --account-id 586394462691 \
     --budget file://bedrock-budget.json
   ```

2. **Monitor usage:**
   - [AWS Bedrock Monitoring Console](https://console.aws.amazon.com/bedrock/home?region=us-east-1#/monitoring)
   - CloudWatch Metrics: `Bedrock > InvocationMetrics`

3. **Limit in workflow:** Already implemented via `MAX_HEALING_ATTEMPTS: 3`

---

## Alternative: Use GitHub Actions Environment Secrets

If you want to avoid giving `jenkins.ags.build` user direct Bedrock access, create a separate IAM user specifically for GitHub Actions:

1. Create new IAM user: `github-actions-ai-healing`
2. Attach the `BedrockClaudeInvokePolicy` to this user
3. Generate access keys
4. Update GitHub secrets:
   - `AGS_AWS_ACCESS_KEY_ID` → new user's access key
   - `AGS_AWS_SECRET_ACCESS_KEY` → new user's secret key

---

## Additional Resources

- [AWS Bedrock Documentation](https://docs.aws.amazon.com/bedrock/)
- [Claude API via Bedrock](https://docs.anthropic.com/claude/docs/claude-on-amazon-bedrock)
- [IAM Policy Reference](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies.html)
- [Quick Fix Guide](QUICK_FIX.md)
- [AI Healing Summary](AI_HEALING_SUMMARY_EXAMPLE.md)

---

## Summary

**What you need to do:**

1. ✅ Create IAM policy from `BEDROCK_IAM_POLICY.json`
2. ✅ Attach policy to user `jenkins.ags.build`
3. ✅ Enable Claude model access in Bedrock console
4. ✅ Re-run the workflow

**Total time:** ~5 minutes

**Result:** AI healing will automatically fix flaky tests! 🎉
