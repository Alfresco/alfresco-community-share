package org.alfresco.po.share.alfrescoContent;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageObject
public class SharedFilesPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    @Autowired
    Toolbar toolbar;

    private By documentLibraryItemsList = By.cssSelector("[class*=data] tr");
    private By actionsSet = By.cssSelector(".action-set a span");

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
    
    private WebElement selectContent(String contentName)
    {
        browser.waitUntilElementIsDisplayedWithRetry(documentLibraryItemsList, 6);
        List<WebElement> itemsList = browser.findElements(documentLibraryItemsList);
        return browser.findFirstElementWithValue(itemsList, contentName);
    }

    public WebElement selectItemRow(String documentItem)
    {
        browser.waitUntilElementIsDisplayedWithRetry(documentLibraryItemsList, 6);
        List<WebElement> itemsList = browser.findElements(documentLibraryItemsList);
        return browser.findFirstElementWithValue(itemsList, documentItem);
    }
}