package org.alfresco.po.share;

import static org.alfresco.common.Wait.WAIT_40;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.WebDriver;

public class MyFilesPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    public MyFilesPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/context/mine/myfiles";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public MyFilesPage navigateByMenuBar()
    {
        return new Toolbar(webDriver).clickMyFiles();
    }

    public MyFilesPage assertMyFilesPageIsOpened()
    {
        webElementInteraction.waitUrlContains(getRelativePath(), WAIT_40.getValue());
        assertTrue(webElementInteraction.getCurrentUrl().contains(getRelativePath()), "My Files page is opened");
        return this;
    }
}
