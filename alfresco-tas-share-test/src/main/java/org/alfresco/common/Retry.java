package org.alfresco.common;

/**
 * Retry execution
 */
@FunctionalInterface
public interface Retry <T extends Object>
{
    /**
     * retry execution
     */
    T execute();
}
