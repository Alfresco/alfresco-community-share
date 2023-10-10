package org.alfresco.po.share.alfrescoContent;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.WebDriver;

public class RepositoryPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    public RepositoryPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/repository";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public RepositoryPage navigateByMenuBar()
    {
        return new Toolbar(webDriver).clickRepository();
    }

    public RepositoryPage assertRepositoryPageIsOpened()
    {
        assertTrue(getCurrentUrl().contains(getRelativePath()), "Repository page is opened");
        return this;
    }
}
