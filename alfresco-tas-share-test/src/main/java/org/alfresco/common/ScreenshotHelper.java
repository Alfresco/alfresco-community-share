package org.alfresco.common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenshotHelper {

    private final Logger LOG = LoggerFactory.getLogger(ScreenshotHelper.class);

    private final File screenshotFolder = new File("./target/reports/screenshots");

    private ThreadLocal<WebDriver> webDriver;

    public ScreenshotHelper(ThreadLocal<WebDriver> webDriver)
    {
        this.webDriver = webDriver;
    }

    /**
     * Method to capture and save screenshot
     *
     * @param webDriver  web driver
     * @param testMethod test name
     * @implNote this method is executed only a test is failed
     */
    public void captureAndSaveScreenshot(ThreadLocal<WebDriver> webDriver, Method testMethod)
    {
        LOG.info("Creating screenshot folder");
        File screen = ((TakesScreenshot) webDriver.get()).getScreenshotAs(OutputType.FILE);
        try
        {
            if(testMethod != null)
            {
                LOG.info("Generating screenshot for test: {}, {}",
                    testMethod.getDeclaringClass().getSimpleName(), testMethod.getName());

                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.hhmmss");

                File destination = new File(String.format("%s%s%s_%s.png", screenshotFolder.getAbsolutePath(),
                    File.separator, testMethod.getDeclaringClass().getSimpleName() + "#" + testMethod.getName(), simpleDateFormat.format(date)));
                FileUtils.copyFile(screen, destination);
            }
        }
        catch (IOException e)
        {
            LOG.error(String.format("Failed to copy screenshot %s", screen.getAbsolutePath()));
        }
    }
}
