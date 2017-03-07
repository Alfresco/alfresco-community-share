package org.alfresco.po.share.navigation;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.exception.PageNotAccesibleByMenuBarException;

/**
 * @author Paul.Brodner
 */
@PageObject
public class MenuNavigationBar
{
    public <T> T goTo(T sharePage)
    {
        if (sharePage instanceof AccessibleByMenuBar)
        {
            AccessibleByMenuBar menuBar = (AccessibleByMenuBar) sharePage;
            menuBar.navigateByMenuBar();
        }
        else
        {
            throw new PageNotAccesibleByMenuBarException(sharePage);
        }
        return sharePage;
    }

}
