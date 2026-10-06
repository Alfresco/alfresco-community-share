# Test Failure Counting Discrepancy

## Three Different Counts

| Count | Source | Example | What It Measures |
|-------|--------|---------|------------------|
| **18** | Surefire Maven | "Tests run: 78, Failures: 18" | Test method invocations that failed |
| **62** | analyze-failures.py | "Found 62 failures" | XML `<failure>` elements (includes parameterized test variations) |
| **13/62** | Retry suite | "13 classes, 62 methods" | Unique test classes / unique method invocations |

## Why Numbers Differ

**Parameterized tests**: Same test method run multiple times with different data parameters
- TestA.method1 with params 1, 2, 3 → 3 `<failure>` XML elements (3 counted)
- But same method name, so "method1" appears once in retry suite
- Result: Higher failure count (62) than test method count (18)

## Debug Commands

```bash
# Count XML failures
grep -c '<failure' surefire-reports/TEST-*.xml

# Analyze JSON
jq 'length' failure-analysis.json                    # Total failures
jq 'map(.test_class) | unique | length' failure-analysis.json  # Unique classes

# Check retry suite
grep -c '<class name=' retry-suite-1.xml            # Class count
grep -c '<include name=' retry-suite-1.xml          # Method count
```

## Key Point

This is **normal behavior** with parameterized/data-driven tests. All 62 method invocations will be retried, even though only 18 unique test methods failed initially.

