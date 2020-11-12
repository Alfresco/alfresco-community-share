package org.alfresco.po.share.alfrescoContent;

import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.browser.WebBrowser;

import static org.testng.Assert.assertTrue;

public class SharedFilesPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    public SharedFilesPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
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
        return (SharedFilesPage) new Toolbar(browser).clickSharedFiles().renderedPage();
    }

    public SharedFilesPage assertSharedFilesPageIsOpened()
    {
        assertTrue(getBrowser().getCurrentUrl().contains(getRelativePath()), "Shared Files page is opened");
        return this;
    }
}