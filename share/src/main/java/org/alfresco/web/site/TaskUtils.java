package org.alfresco.web.site;

import org.apache.commons.logging.Log;

public class TaskUtils {

    public static <E extends Throwable> void retry(int maxRetries, Task<E> task) throws E {
        retry(maxRetries, 0, null, task);
    }

    public static <E extends Throwable> void retry(int maxRetries, long waitTimeMs, Log logger, Task<E> task) throws E {
        while (maxRetries > 0) {
            maxRetries--;
            try {
                task.run();
            } catch (Exception e) {
                if (maxRetries == 0) {
                    try {
                        throw e;
                    } catch (Exception ignored) { // can't happen but just in case we wrap it in
                        throw new RuntimeException(e);
                    }
                }

                if (logger != null)
                    logger.info("Attempt " + maxRetries + " failed", e);
                try {
                    Thread.sleep(waitTimeMs);
                } catch (InterruptedException ignored) {
                    logger.error(ignored.getMessage());
                }
            }
        }
    }

    public interface Task<E extends Throwable> {
        void run() throws E;
    }
}
