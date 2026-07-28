# 🚀 Quick Fix: Enable AI Test Healing

## Current Status
✅ XML parsing bug fixed  
✅ Failure detection working  
✅ AI healing code ready  
❌ **IAM permissions missing** ← You are here

## Fix in 3 Steps (5 minutes)

### 1. Create IAM Policy

**AWS Console:**
1. Go to: https://console.aws.amazon.com/iam/
2. Click **Policies** → **Create policy** → **JSON** tab
3. Paste contents from [BEDROCK_IAM_POLICY.json](../../../../.github/BEDROCK_IAM_POLICY.json)
4. Name it: `BedrockClaudeInvokePolicy`
5. Click **Create policy**

**AWS CLI:**
```bash
aws iam create-policy \
  --policy-name BedrockClaudeInvokePolicy \
  --policy-document file://.github/BEDROCK_IAM_POLICY.json
```

### 2. Attach to User

**AWS Console:**
1. Go to **Users** → `jenkins.ags.build`
2. Click **Add permissions** → **Attach policies directly**
3. Search for `BedrockClaudeInvokePolicy`
4. Select and click **Add permissions**

**AWS CLI:**
```bash
aws iam attach-user-policy \
  --user-name jenkins.ags.build \
  --policy-arn arn:aws:iam::586394462691:policy/BedrockClaudeInvokePolicy
```

### 3. Enable Model Access

1. Go to: https://console.aws.amazon.com/bedrock/home?region=us-east-1#/modelaccess
2. Click **Manage model access**
3. Find **Claude 3.5 Sonnet** and **Claude Sonnet 4.5**
4. Check the boxes and click **Request access**
5. Wait for approval (usually instant for existing AWS accounts)

## Verify It Works

```bash
aws iam simulate-principal-policy \
  --policy-source-arn arn:aws:iam::586394462691:user/jenkins.ags.build \
  --action-names bedrock:InvokeModel \
  --resource-arns "arn:aws:bedrock:us-east-1::foundation-model/anthropic.claude-sonnet-4-5-20250929-v1:0"
```

Expected output: `"EvalDecision": "allowed"`

## Test the Workflow

Re-run the GitHub Actions workflow. You should see:

```
✅ XML parsing: 2 failures detected
✅ AI healing: Successfully fixed tests
✅ Tests passed on attempt 2
✅ Auto-commit created
```

## Costs

- **~$0.06 per healing attempt**
- **~$2.40/month** (40 attempts)
- Negligible vs. developer time saved

## Need Help?

- Full guide: [FIX_BEDROCK_PERMISSIONS.md](FIX_BEDROCK_PERMISSIONS.md)
- Workflow reference: [AI_HEALING_WORKFLOW.md](AI_HEALING_WORKFLOW.md)
- Quick reference: [QUICK_REFERENCE.md](QUICK_REFERENCE.md)

---

**That's it!** AI healing will work automatically after permissions are added. 🎉

