package org.alfresco.po.share.alfrescoContent;

import static org.alfresco.common.Wait.WAIT_1;
import static org.alfresco.common.Wait.WAIT_5;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.MessageFormat;
import java.util.List;
@Slf4j
public class RepositoryPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    private By createButton = By.cssSelector("button[id*='createContent']");
    private By likeButton = By.cssSelector("a.like-action");
    private By repositoryFilesItemsList = By.cssSelector("div[id$='default-documents'] tbody[class$='data'] tr");
    private static final String ACTION_SELECTOR = "div[id*='default-actions']:not([class*='hidden'])>.action-set .{0}>a";
    private static final String ACTION_SELECTOR_MORE = "div[id*='default-actions']:not([class*='hidden']) div.more-actions>.{0}>a";
    private By moreSelector = By.cssSelector("div[id*='default-actions']:not([class*='hidden']) a.show-more");
    private By moreMenuSelector = By.cssSelector("div[class='action-set detailed'] div[id*='onActionShowMore'] a span");
    private By contentNameSelector = By.cssSelector(".filename a");
    private By actionsSet = By.cssSelector(".action-set a span");
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
    public RepositoryPage click_FolderName(String folderName)
    {
        waitInSeconds(5);
        List<WebElement> folderList = findElements(contentNameSelector);
        for (WebElement webElement : folderList)
        {
            if (webElement.getText().contains(folderName))
            {
                    webElement.click();
            }
        }
        return this;
    }
    public boolean isFileNameDisplayed(String fileName)
    {
        waitInSeconds(3);
        if (selectDocumentLibraryItemRow(fileName) != null)
        {
            return true;
        }
        else
        {
            refresh();
            waitInSeconds(WAIT_5.getValue());
            if (selectDocumentLibraryItemRow(fileName) != null)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
    public RepositoryPage select_ItemsAction(String contentItem, ItemActions action)
    {
        waitInSeconds(3);
        WebElement libraryItem = mouseOverContentItem(contentItem);
        By actionSelector = By.cssSelector(MessageFormat.format(ACTION_SELECTOR, action.getActionLocator()));
        WebElement actionElement;
        if (isActionInMoreActionsContainer(action))
        {
            clickOnMoreActions(libraryItem);
        }
        try
        {
            actionElement = waitUntilElementIsVisible(actionSelector);
        }
        catch (TimeoutException timeoutException)
        {
            throw new TimeoutException(
                "The action " + action.getActionName() + " could not be found for list item " + contentItem);
        }
        mouseOver(actionElement);
        clickElement(actionElement);
        return this;
    }

    private boolean isActionInMoreActionsContainer(ItemActions action)
    {
        By actionSelector = By.cssSelector(MessageFormat.format(ACTION_SELECTOR_MORE, action.getActionLocator()));
        WebElement actionElement = waitUntilElementIsPresent(actionSelector);
        return actionElement != null;
    }
    private void clickOnMoreActions(WebElement libraryItem)
    {
        WebElement moreAction = waitUntilChildElementIsPresent(libraryItem, moreSelector);
        mouseOver(moreAction);
        clickElement(moreAction);
    }
    public RepositoryPage assertFileIsDisplayed(String fileName)
    {
        log.info("Assert file is displayed in Repository Browser {}", fileName);
        assertTrue(isFileNameDisplayed(fileName), fileName + " is not displayed");
        return this;
    }
    public WebElement selectDocumentLibraryItemRow(String documentItem)
    {
        waitInSeconds(2);
        List<WebElement> itemsList = findElements(repositoryFilesItemsList);
        return findFirstElementWithValue(itemsList, documentItem);
    }
    public RepositoryPage assertIsLikeButtonDisplayed(String fileName)
    {
        log.info("Assert Like Button is displayed for Repository Browser Files/Folders {}", fileName);
        assertTrue(isElementDisplayed(selectDocumentLibraryItemRow(fileName), likeButton),"Like Button is Not Displayed");
        return this;
    }
//    public RepositoryPage refreshpage()
//    {
//        waitInSeconds(2);
//        getWebDriver().navigate().refresh();
//        waitUntilDomReadyStateIsComplete();
//        return this;
//    }
    public boolean isActionItemAvailableInTheRepositoryLibraryItems(String content, ItemActions actionItem)
    {

        boolean actionAvailable;
        if (isMoreMenuDisplayed(content))
        {
            actionAvailable = isActionAvailableForLibraryItem(content, actionItem);
        }else
        {
            actionAvailable = false;
        }
        return actionAvailable;
    }
    public RepositoryPage assertActionItem_Not_AvailableInTheRepositoryLibraryItems(String content, ItemActions actionItem)
    {
        log.info("Verify that the Action Item is Available in the list for the Document Library Itme {}", content );
        assertFalse(isActionItemAvailableInTheRepositoryLibraryItems(content, actionItem), "Action is available");
        return this;
    }
    public RepositoryPage assertActionItem_AvailableInTheRepositoryLibraryItems(String content, ItemActions actionItem)
    {
        log.info("Verify that the Action Item is Available in the list for the Document Library Itme {}", content );
        assertTrue(isActionItemAvailableInTheRepositoryLibraryItems(content, actionItem),"Action is Not Available");
        return this;
    }
    public RepositoryPage clickOnFolderName(String folderName)
    {
        waitInSeconds(WAIT_1.getValue());
        WebElement folderElement = selectDocumentLibraryItemRow(folderName);
        clickElement(folderElement.findElement(contentNameSelector));
        return this;
    }
    public RepositoryPage click_FolderLink()
    {
        clickElement(CreateMenuOption.FOLDER.getLocator());
        return this;
    }
    public RepositoryPage click_CreateButton()
    {
        clickElement(createButton);
        waitUntilElementIsVisible(By.cssSelector("div[id$='_default-createContent-menu'][style*='visible']"));
        return this;
    }
    public RepositoryPage click_PlainTextLink()
    {
        clickElement(CreateMenuOption.PLAIN_TEXT.getLocator());
        return this;
    }
    public RepositoryPage assertFileIsNotDisplayed(String fileName)
    {
        log.info("Assert file is not displayed in Document Library {}", fileName);
        assertFalse(isFileNameDisplayed(fileName), fileName + " is displayed in Document Library");
        return this;
    }
    public RepositoryPage assertIsMoreMenuDisplayed(String filename)
    {
        log.info("Verify More Menu is Displaying");
        assertTrue(isMoreMenuDisplayed(filename), "More Menu is not displayed");
        return this;
    }
    public RepositoryPage assertIsMoreMenuNotDisplayed(String filename)
    {
        log.info("Verify More Menu is Not Displaying");
        assertFalse(isMoreMenuDisplayed(filename), "More Menu is displayed");
        return this;
    }
}
