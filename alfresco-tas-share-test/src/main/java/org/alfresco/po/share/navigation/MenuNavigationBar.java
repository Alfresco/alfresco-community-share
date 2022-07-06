package org.alfresco.po.share.navigation;

import org.alfresco.utility.exception.PageNotAccesibleByMenuBarException;
import org.alfresco.utility.web.annotation.PageObject;

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
        } else
        {
            throw new PageNotAccesibleByMenuBarException(sharePage);
        }
        return sharePage;
    }

}
