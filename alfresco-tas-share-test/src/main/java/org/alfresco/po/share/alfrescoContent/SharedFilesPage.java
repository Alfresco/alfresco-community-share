package org.alfresco.po.share.alfrescoContent;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.WebDriver;

public class SharedFilesPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    public SharedFilesPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/context/shared/sharedfiles";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public SharedFilesPage navigateByMenuBar()
    {
        return new Toolbar(webDriver).clickSharedFiles();
    }

    public SharedFilesPage assertSharedFilesPageIsOpened()
    {
        webElementInteraction.waitUrlContains(getRelativePath(), 30);
        assertTrue(webElementInteraction.getCurrentUrl().contains(getRelativePath()), "Shared Files page is opened");
        return this;
    }
}