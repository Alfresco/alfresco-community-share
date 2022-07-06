package org.alfresco.share;

import lombok.extern.slf4j.Slf4j;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

@Slf4j
public class RetryAnalyzer implements IRetryAnalyzer
{
    private int retryCount = 0;
    private final int MAX_RETRIES = 2;

    @Override
    public boolean retry(ITestResult result)
    {
      String methodName = result.getMethod().getMethodName();
      if (retryCount < MAX_RETRIES)
      {
        log.warn("RERUNNING FAILED TEST {}, {}", methodName, retryCount);
        retryCount++;
        return true;
      }
      return false;
    }
}
