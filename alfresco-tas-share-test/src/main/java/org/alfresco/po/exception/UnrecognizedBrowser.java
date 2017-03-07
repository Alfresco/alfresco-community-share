package org.alfresco.po.exception;

import java.util.Arrays;

import org.alfresco.browser.Browser;

/**
 * @author Paul.Brodner
 */
public class UnrecognizedBrowser extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_MESSAGE = "Unrecognized Browser name [%s] passed in environment. Check you configuration files. Available browser options %s";

    public UnrecognizedBrowser(String browserName)
    {
        super(String.format(DEFAULT_MESSAGE, browserName, Arrays.asList(Browser.values()).toString()));
    }
}
