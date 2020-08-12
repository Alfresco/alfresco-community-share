package org.alfresco.po.share.alfrescoContent;

import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.annotation.PageObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

@PageObject
public class SharedFilesPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    @Override
    public String getRelativePath()
    {
        return "share/page/context/shared/sharedfiles";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public SharedFilesPage navigateByMenuBar()
    {
        return toolbar.clickSharedFiles();
    }

    public SharedFilesPage assertSharedFilesPageIsOpened()
    {
        Assert.assertTrue(browser.getCurrentUrl().contains(getRelativePath()), "Shared Files page is opened");
        return this;
    }
}