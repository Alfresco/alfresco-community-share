package org.alfresco.po.share;

import static org.alfresco.common.Wait.WAIT_3;
import static org.alfresco.common.Wait.WAIT_40;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.Utils;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.model.FileModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.Keys;

import java.text.MessageFormat;

@Slf4j
public class MyFilesPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    public MyFilesPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    private By createButton = By.cssSelector("button[id*='createContent']");

    private final By dialogTitle = By.xpath("//div[@class='hd']");

    @Override
    public String getRelativePath()
    {
        return "share/page/context/mine/myfiles";
    }

    private static final String ACTION_SELECTOR          =
        "div[id*='default-actions']:not([class*='hidden'])>.action-set .{0}>a";
    private static final String ACTION_SELECTOR_MORE     =
        "div[id*='default-actions']:not([class*='hidden']) div.more-actions>.{0}>a";
    private              By     moreSelector             =
        By.cssSelector("div[id*='default-actions']:not([class*='hidden']) a.show-more");
    private              By     moreActionsMenu          =
        By.cssSelector("div[id*='default-actions']:not([class*='hidden'])>.action-set>.more-actions");
    private final        By     message                  = By.cssSelector("#prompt_h + div.bd");
    private              By     documentLibraryItemsList =
        By.cssSelector("div[id$='default-documents'] tbody[class$='data'] tr");
    private final        String templateName             = "//a[@class='yuimenuitemlabel']//span[text()='%s']";
    private final        By     createFileFromTemplate   =
        By.cssSelector("div[id$='createContent-menu']>div>ul:nth-of-type(2)>li:nth-of-type(1) span");
    private By editTagInputField_ = By.cssSelector(".inlineTagEditAutoCompleteWrapper input");
    private By selectedItemsButton = By.xpath("(//span[@class= \"yui-button yui-menu-button\"])[2]");
    private By sharedFilesButton = By.xpath("//button[text()=\"Shared Files\"]");
    private By sharedFilesFromHeaderMenu = By.xpath("//a[@title='Shared Files']");
    private By copyButton = By.xpath("//button[text()=\"Copy\"]");

    @Override
    public MyFilesPage navigateByMenuBar()
    {
        return new Toolbar(webDriver).clickMyFiles();
    }

    public MyFilesPage assertMyFilesPageIsOpened()
    {
        waitUrlContains(getRelativePath(), WAIT_40.getValue());
        assertTrue(getCurrentUrl().contains(getRelativePath()), "My Files page is opened");
        return this;
    }

    public MyFilesPage click_CreateButton()
    {
        clickElement(createButton);
        waitUntilElementIsVisible(By.cssSelector("div[id$='_default-createContent-menu'][style*='visible']"));
        return this;
    }

    public MyFilesPage click_CreateFromTemplateOption(CreateMenuOption option)
    {
        clickElement(option.getLocator());
        return new MyFilesPage(webDriver);
    }

    public void create_FileFromTemplate(FileModel templateFile)
    {
        log.info("Create new file from template {}", templateFile);
        mouseOver(findElement(createButton));
        mouseOver(findElement(createFileFromTemplate));
        clickElement(createFileFromTemplate);
        clickElement(By.xpath(String.format(templateName, templateFile.getName())));
        waitUntilNotificationMessageDisappears();

    }

    public MyFilesPage click_FolderLink()
    {
        clickElement(CreateMenuOption.FOLDER.getLocator());
        return this;
    }

    public MyFilesPage assertDialogTitleEquals(String contentName)
    {
        log.info("Verify the Dialog Title..");
        assertEquals(getDialogTitle(), contentName, String.format("Dialog title not matched with [%s]", contentName));
        return this;
    }

    public void assertConfirmationMessage(String contentName)
    {
        log.info("Verify the Confirmation Message");
        assertEquals(getMessage(), contentName, String.format("Dialog title not matched with [%s]", contentName));
    }

    public String getMessage()
    {
        return getElementText(message);
    }

    public MyFilesPage assertIsContantNameDisplayed(String contantName)
    {
        log.info("Verify file/folder name displayed {}", contantName);
        waitInSeconds(3);
        assertTrue(isContentNameDisplayed(contantName),
                   String.format("file/folder name not matched with %s ", contantName));
        return this;
    }

    public boolean isLikeButtonDisplayed(String fileName)
    {
        log.info("To Verify the Like Button is Displayed");
        return isElementDisplayed(selectDocumentLibraryItemRow(fileName), likeButton);
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public MyFilesPage select_ItemAction(String contentItem, ItemActions action)
    {
        waitInSeconds(WAIT_3.getValue());
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

    public void assertIsContentDeleted(String contentName)
    {
        log.info("Verify file/folder deleted");
        assertFalse(isContentAvailable(contentName), String.format("Check file/folder %s", contentName));
    }

    public boolean isContentAvailable(String contentNameInList)
    {
        for (WebElement contentList : findElements(documentLibraryItemsList))
        {
            if (contentList.getText()
                .equals(contentNameInList))
                return true;
        }
        return false;
    }

    public MyFilesPage assertIsFolderPresentInList(String folderName)
    {
        log.info("Verify that the folder is present in the list.");
        assertTrue(getFoldersList().contains(folderName), "Folder is not present in the list.");
        return this;
    }

    public void type_TagName(String tagName)
    {
        waitInSeconds(3);
        Utils.clearAndType(waitUntilElementIsVisible(findElement(editTagInputField_)), tagName);
        findElement(editTagInputField_).sendKeys(Keys.ENTER);
        waitInSeconds(2);
        findElement(editTagInputField_).sendKeys(Keys.ENTER);
    }

    private By getCheckboxByDocumentName(String documentName)
    {
        return By.xpath("//a[text()='" + documentName + "']//preceding::input[@type='checkbox'][1]");
    }

    public void selectDocumentCheckbox(String documentName)
    {
        WebElement checkbox = findElement(getCheckboxByDocumentName(documentName));
        if (!checkbox.isSelected())
        {
            checkbox.click();
        }
    }

    public void clickSelectedItemsButton()
    {
        int retryCount = 0;
        int maxRetries = 3;

        while (retryCount < maxRetries)
        {
            WebElement button = findElement(selectedItemsButton);
            if (button.isEnabled())
            {
                button.click();
                System.out.println("Button clicked successfully.");
                return;
            }
            else
            {
                retryCount++;
            }
        }
    }

    public MyFilesPage clickSelectedItemsAction (String buttonName) {
        By actionName = By.xpath("//span[text()='" + buttonName + "']");
        findElement(actionName).click();
        waitInSeconds(3);
        return this;
    }

    public MyFilesPage clickSharedFiles () {
        findElement(sharedFilesButton).click();
        return this;
    }

    public MyFilesPage clickSharedFilesFromHeaderMenu () {
        findElement(sharedFilesFromHeaderMenu).click();
        return this;
    }

    public MyFilesPage clickCopy()
    {
        findElement(copyButton).click();
        return  this;
    }
}
