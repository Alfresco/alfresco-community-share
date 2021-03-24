package org.alfresco.share;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

@Slf4j
public class TestListener extends ScreenshotHelper implements ITestListener
{
    @Override
    public void onTestStart(ITestResult result)
    {
        log.info("STARTED TEST: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result)
    {
        log.info("TEST PASSED: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result)
    {
        log.warn("TEST FAILED: {}", result.getMethod().getMethodName());
        WebDriver webDriver = (WebDriver) result.getTestContext().getAttribute("driver");
        captureAndSaveScreenshot(webDriver, result);
    }

    @Override
    public void onTestSkipped(ITestResult result)
    {
        log.warn("TEST SKIPPED: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result)
    {
    }

    @Override
    public void onStart(ITestContext context)
    {
    }

    @Override
    public void onFinish(ITestContext context)
    {
    }
}
