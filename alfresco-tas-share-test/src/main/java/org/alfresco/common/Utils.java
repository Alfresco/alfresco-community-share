package org.alfresco.common;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * Utility class containing helpful methods.
 */
public final class Utils
{
    /**
     * Clear control
     */
    public static <T extends WebElement> T clear(T field)
    {
        field.clear();
        field.sendKeys(Keys.BACK_SPACE);
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
}
