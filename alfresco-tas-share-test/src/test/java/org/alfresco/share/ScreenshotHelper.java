package org.alfresco.share;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;

@Slf4j
public class ScreenshotHelper extends BaseTest
{
    private final File screenshotFolder = new File("./target/reports/screenshots");

    /**
     * Method to capture and save screenshot
     *
     * @param testMethod test name
     * @implNote this method is executed only a test is failed
     */
    public void captureAndSaveScreenshot(WebDriver webDriver, ITestResult testMethod)
    {
        log.info("Creating screenshot folder");
        File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        try
        {
            log.info("Generating screenshot for test: {}", testMethod.getName());
            File path = getScreenshotPath(testMethod);
            FileUtils.copyFile(screenshot, path);
        }
        catch (IOException e)
        {
            log.warn(String.format("Failed to copy screenshot %s", screenshot.getAbsolutePath()));
        }
    }

    private File getScreenshotPath(ITestResult testMethod)
    {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.hhmmss");

        return new File(
            String.format("%s%s%s_%s.png", screenshotFolder.getAbsolutePath(),
                File.separator, testMethod.getName() + "#" + testMethod.getName(),
                simpleDateFormat.format(date)));
    }
}
