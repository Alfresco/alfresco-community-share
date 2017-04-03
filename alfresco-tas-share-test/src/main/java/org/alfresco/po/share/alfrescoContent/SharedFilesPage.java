package org.alfresco.po.share.alfrescoContent;

import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class SharedFilesPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    @Autowired
    Toolbar toolbar;

    @RenderWebElement
    @FindBy(xpath = "//span[@class= 'label']/a[text()='Shared Files']")
    private WebElement sharedFilesBreadcrumb;

    @Override
    public String getRelativePath()
    {
        return "share/page/context/shared/sharedfiles";
    }

    @SuppressWarnings("unchecked")
    @Override
    public SharedFilesPage navigateByMenuBar()
    {
        toolbar.clickSharedFiles();
        return (SharedFilesPage) renderedPage();
    }
}