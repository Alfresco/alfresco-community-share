package org.alfresco.share;

/**
 * Thrown when cookie-based authentication cannot obtain a valid {@code HttpState}
 * from the underlying login call, so callers can react (e.g. fall back to UI login)
 * instead of failing with an opaque {@link NullPointerException}.
 */
public class AuthenticationFailedException extends RuntimeException
{
    public AuthenticationFailedException(String message)
    {
        super(message);
    }
}
