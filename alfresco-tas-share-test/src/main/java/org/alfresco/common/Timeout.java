package org.alfresco.common;

public enum Timeout
{
    VERY_SHORT(1L),
    SHORT(5L),
    MEDIUM(10L),
    LONG(15L),
    VERY_LONG(30L);

    /** The length of the timeout in seconds. */
    private long timeoutSeconds;

    Timeout(long defaultSeconds)
    {
        this.timeoutSeconds = defaultSeconds;
    }

    /**
     * Get the length of this timeout.
     *
     * @return The timeout length in seconds.
     */
    public long getTimeoutSeconds()
    {
        return this.timeoutSeconds;
    }
}
