package org.alfresco.po.share;

import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.annotation.PageObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

@PageObject
public class MyFilesPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    @Override
    public String getRelativePath()
    {
        return "share/page/context/mine/myfiles";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public MyFilesPage navigateByMenuBar()
    {
        return toolbar.clickMyFiles();
    }

    public MyFilesPage assertMyFilesPageIsOpened()
    {
        Assert.assertTrue(browser.getCurrentUrl().contains(getRelativePath()), "My Files page is opened");
        return this;
    }
}
