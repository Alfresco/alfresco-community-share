package org.alfresco.po.share.alfrescoContent;

import static org.alfresco.common.Wait.WAIT_30;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
@Slf4j
public class SharedFilesPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    private By actionsSet = By.cssSelector(".action-set a span");
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
        waitUrlContains(getRelativePath(), WAIT_30.getValue());
        assertTrue(getCurrentUrl().contains(getRelativePath()), "Shared Files page is opened");
        return this;
    }
    private List<WebElement> getAvailableActions_(String libraryItem)
    {
        WebElement itemRow = mouseOverContentItem(libraryItem);
        return itemRow.findElements(actionsSet);
    }
    public void assertIsActionAvailableForLibraryItem(String libraryItem, ItemActions action)
    {
        log.info("Verifying the Item Actions is Available {}", action);
        assertTrue(isElementDisplayed(
            findFirstElementWithExactValue(getAvailableActions_(libraryItem),action.getActionName())),"Shared Files List ");
    }
}