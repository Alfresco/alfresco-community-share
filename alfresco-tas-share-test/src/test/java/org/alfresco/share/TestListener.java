package org.alfresco.share;

import java.io.File;
import java.io.IOException;

import org.alfresco.browser.WebBrowser;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

public class TestListener implements ITestListener
{
    private WebBrowser browser;
    private static final Logger LOG = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onTestFailure(ITestResult result)
    {
        this.browser = ((ContextAwareWebTest) result.getInstance()).getBrowser();

        File screenshot = ((TakesScreenshot) browser).getScreenshotAs(OutputType.FILE);

        String outputDir = result.getTestContext().getOutputDirectory();
        LOG.info("Output Directory: {}" + outputDir);
        outputDir = outputDir.substring(0, outputDir.lastIndexOf(File.separator)) + File.separator + "screenshots";
        File saved = new File(outputDir, result.getMethod().getMethodName() + ".png");
        try
        {
            FileUtils.copyFile(screenshot, saved);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        printScreenshot(saved);
    }

    protected void printScreenshot(File file)
    {
        System.setProperty("org.uncommons.reportng.escape-output", "false");
        Reporter.log("<a href=\"" + "file://" + file + "\"><p align=\"left\"><strong>See the screenshot for this failure</strong>" + "</p><br/>", true);
    }

    @Override
    public void onTestStart(ITestResult result)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTestSuccess(ITestResult result)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTestSkipped(ITestResult result)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStart(ITestContext context)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFinish(ITestContext context)
    {
        // TODO Auto-generated method stub

    }

}