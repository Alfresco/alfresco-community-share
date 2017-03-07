package org.alfresco.po.exception;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;

/**
 * Error thrown for {@link PageObject} that will not implement {@link AccessibleByMenuBar} interface
 * 
 * @author Paul.Brodner
 */
public class PageNotAccesibleByMenuBarException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    public <T> PageNotAccesibleByMenuBarException(T page)
    {
        super(String.format(
                "Page Object Passed %s is not accessible by alfresco menu bar. Please implement AccessibleByMenuBar interface in order to use this class in AlfrescoMenuNavigationBar",
                page.getClass().getName()));
    }

}
