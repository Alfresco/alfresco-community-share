package org.alfresco.common;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

public class Parameter
{

    /**
     * Helper method to check the parameters. This method should be used for all public methods.
     *
     * @param paramName {@link String} A name for the parameter to check
     * @param object {@link Object} The object to check
     * @param <E> type
     * @exception IllegalArgumentException will be thrown if the parameter value is null
     *                (for {@link String} also if the value is empty or blank)
     */
    public static <E> void checkIsMandotary(final String paramName, final Object object)
    {
        if (object == null)
        {
            throw new IllegalArgumentException(String.format("'%s' is a mandatory parameter and must have a value", paramName));
        }
        if (object instanceof String && StringUtils.isBlank((String) object))
        {
            throw new IllegalArgumentException(String.format("'%s' is a mandatory parameter and cannot be empty", paramName));
        }
        if (object instanceof Collection<?> && ((Collection<?>) object).isEmpty())
        {
            throw new IllegalArgumentException(String.format("'%s' is a mandatory parameter and cannot be empty", paramName));
        }
    }
}
