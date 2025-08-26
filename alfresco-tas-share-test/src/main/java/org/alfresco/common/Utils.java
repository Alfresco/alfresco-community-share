package org.alfresco.common;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;
import org.alfresco.utility.Utility;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class containing helpful methods.
 */
public final class Utils
{
    protected static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    public static String srcRoot = StringUtils.substringBeforeLast(System.getProperty("user.dir"), File.separator + "target") + File.separator;
    public static String testDataFolder = srcRoot + "testdata" + File.separator;

    public static File screenshotFolder = new File("./target/reports/screenshots");

    /**
     * Clear control
     */
    public static <T extends WebElement> T clear(T field)
    {
        int fieldTextLength = field.getText().length();
        field.clear();
        //workaround for textarea when field is not cleared (only the last letter)
        for (int i = 0; i < fieldTextLength; i++)
        {
            field.sendKeys(Keys.BACK_SPACE);
        }
        return field;
    }

    /**
     * Clear control and enter text
     */
    public static <T extends WebElement> T clearAndType(T field, String text)
    {
        clear(field);
        field.sendKeys(text);

        // brief pause to allow the UI to process all the characters entered
        try
        {
            Thread.sleep(250);
        }
        catch (InterruptedException e)
        {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }

        return field;
    }

    /**
     * Helper method to retry the provided {@link Retry code block}, ignoring any {@code Exception}s until either
     * the code block completes successfully or the maximum number of retries has been reached.
     *
     * @param <T>       the return type from the code block.
     * @param retry     a code block to execute.
     * @param count     maximum number of retries.
     * @return          result of the code block.
     */
    @SuppressWarnings("unchecked")
    public static final <T> T retry(Retry<T> retry, int count)
    {
        return retry(retry, count, Exception.class, AssertionError.class);
    }

    /**
     * Helper method to retry the provided {@link Retry code block}, ignoring the specified exception types until either
     * the code block completes successfully or the maximum number of retries has been reached.
     *
     * @param <T>       the return type from the code block.
     * @param retry     a code block to execute.
     * @param count     maximum number of retries.
     * @param retryExceptions a sequence of throwable types any one of which will start a retry.
     * @return          result of the code block.
     */
    @SuppressWarnings("unchecked")
    public static final <T> T retry(Retry<T> retry, int count, Class<? extends Throwable>... retryExceptions)
    {
        int attempt = 0;

        while (true)
        {
            try
            {
                // try and execute
                return retry.execute();
            }
            catch (Throwable throwable)
            {
                LOG.info("Exception ignored on attempt {}: {}", attempt, throwable);

                // Is the caught exception a type that requires a retry?
                if (asList(retryExceptions)
                        .stream()
                        .anyMatch(e -> e.isAssignableFrom(throwable.getClass())))
                {
                    attempt++;
                    // if we have used up all our retries throw the exception
                    if (attempt >= count)
                    {
                        throw throwable;
                    }

                    // otherwise do nothing and try again
                }
                else
                {
                    throw throwable;
                }
            }
        }
    }

    /**
     * Helper method to retry the provided {@link Retry code block} until the {@code predicate} block returns
     * {@code true} or the maximum number of retries has been reached.
     * Note that any exceptions thrown within the {@link Retry} will not be caught internally and will be thrown
     * immediately.
     *
     * @param retry     the code block to retry.
     * @param predicate a predicate code block which determines when the {@link Retry} is successfully completed.
     * @param count     the maximum number of retries.
     * @param <T>       the return type of the {@link Retry} block.
     * @return          the value returned by the {@link Retry} block.
     */
    public static final <T> T retryUntil(Retry<T> retry, Supplier<Boolean> predicate, int count)
    {
        for (int attempt = 0; attempt < count; attempt++)
        {
            T result = retry.execute();
            if (predicate.get())
            {
                return result;
            }
            LOG.info("Retry number {} failed", attempt);
        }
        throw new RuntimeException("Tried " + count + " times without successful completion.");
    }

    /**
     * Method to check if file exists in specified directory
     *
     * @param fileName  file name
     * @param extension file extension
     * @return true if file exists, otherwise false
     */
    public static boolean isFileInDirectory(String fileName, String extension) {
    int retry = 0;
    int seconds = 20;

    if (extension != null) {
    fileName = fileName + extension;
}

    String filePath = "testdata" + File.separator + fileName;
    String tempFilePath = filePath + ".crdownload";

    // Ensure testdata directory exists
    File directory = new File("testdata");
    if (!directory.exists()) {
    directory.mkdirs();
}

    Utility.waitToLoopTime(2, "Initial wait before checking file existence");

    while (retry <= seconds) {
    File file = new File(filePath);
    File tempFile = new File(tempFilePath);

    if (file.exists() && !tempFile.exists()) {
        return true;
    }

    retry++;
    Utility.waitToLoopTime(2, String.format("Waiting for '%s' to finish downloading...", fileName));
}

    return false;
}

    public static boolean isFileInDirectory(File file)
    {
        int retry = 0;
        while (retry <= Wait.WAIT_15.getValue() && !file.exists())
        {
            retry++;
            Utility.waitToLoopTime(1, String.format("Wait for '%s' to get downloaded", file.getName()));
        }
        return file.exists();
    }

    /**
     * Method to save screenshot
     * @param webDriver browser
     * @param testMethod test name
     */
    public static void saveScreenshot(ThreadLocal<WebDriver> webDriver, Method testMethod)
    {
        if(!screenshotFolder.exists())
        {
            LOG.info("Creating screenshot folder");
            screenshotFolder.mkdir();
        }
        TakesScreenshot screenshot = ((TakesScreenshot) webDriver.get());
        File screen = screenshot.getScreenshotAs(OutputType.FILE);
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
