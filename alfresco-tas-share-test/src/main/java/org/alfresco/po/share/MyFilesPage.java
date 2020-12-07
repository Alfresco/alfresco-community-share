package org.alfresco.po.share;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.browser.WebBrowser;

public class MyFilesPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    public MyFilesPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
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
        return (MyFilesPage) new Toolbar(browser).clickMyFiles().renderedPage();
    }

    public MyFilesPage assertMyFilesPageIsOpened()
    {
        getBrowser().waitUrlContains(getRelativePath(), 30);
        assertTrue(getBrowser().getCurrentUrl().contains(getRelativePath()), "My Files page is opened");
        return this;
    }
}
