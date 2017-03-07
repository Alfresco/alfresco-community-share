package org.alfresco.po.exception;

/**
 * Alfresco page operation exception thrown when an operation fail to execute
 * 
 * @author Bogdan Bocancea
 */
public class PageOperationException extends RuntimeException
{
    private static final long serialVersionUID = 4622322705041953710L;
    private static final String DEFAULT_MESSAGE = "The operation failed to complete";

    public PageOperationException(String reason)
    {
        super(reason);
    }

    public PageOperationException(String reason, Throwable cause)
    {
        super(reason, cause);
    }

    public PageOperationException()
    {
        super(DEFAULT_MESSAGE);
    }
}
