package org.alfresco.common;

import static java.util.Arrays.asList;

import java.util.function.Supplier;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class containing helpful methods.
 */
public final class Utils
{
    protected static final Logger LOG = LoggerFactory.getLogger(Utils.class);

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
}
