package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class UserTrashcanPage extends SharePage2<UserTrashcanPage>
{
    private String userName;

    @RenderWebElement
    private final By searchInput = By.cssSelector("input[id$='default-search-text']");
    @RenderWebElement
    private final By searchButton = By.cssSelector("button[id$='search-button-button']");
    private final By emptyButton = By.cssSelector("button[id*='empty']");
    private final By itemsNameList = By.cssSelector(".yui-dt-liner > .name");
    private final By deletionTimestampSelector = By.cssSelector(".yui-dt-liner div:nth-child(2)");
    private final By locationBeforeDeletionSelector = By.cssSelector(".yui-dt-liner div:nth-child(3)");
    private final By recoverButtonSelector = By.cssSelector("td[headers$='th-actions ']>div>span>span:nth-of-type(1) button");
    private final By deleteButtonSelector = By.cssSelector("td[headers$='th-actions ']>div>span>span:nth-of-type(2) button");
    private final By emptyTrashcanMessageSelector = By.cssSelector(".yui-dt-empty div");
    private final By itemRowsList = By.cssSelector(".yui-dt-data tr");

    public UserTrashcanPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/user-trashcan", getUserName());
    }

    public UserTrashcanPage navigate(String userName)
    {
        setUserName(userName);
        return navigate();
    }

    public UserTrashcanPage navigate(UserModel userModel)
    {
        setUserName(userModel.getUsername());
        return navigate();
    }

    public List<WebElement> search(String searchTerm)
    {
        clearAndType(searchInput, searchTerm);
        clickSearch();
        return getBrowser().findElements(itemsNameList);
    }

    public void clickSearch()
    {
        getBrowser().findElement(searchButton).click();
    }

    public String getItemsNamesList()
    {
        ArrayList<String> itemsNameTextList = new ArrayList<>();
        for (WebElement anItemsNameList : getBrowser().findElements(itemsNameList))
        {
            itemsNameTextList.add(anItemsNameList.getText());
        }
        return itemsNameTextList.toString();
    }

    private WebElement getItemRow(String item)
    {
        return getBrowser().findFirstElementWithValue(itemRowsList, item);
    }

    private boolean findRow(String itemName)
    {
        List<WebElement> trashRows = getBrowser().findElements(itemRowsList);
        return trashRows.stream().anyMatch(row -> row.getText().equals(itemName));
    }

    public UserTrashcanPage assertContentIsNotDisplayed(String contentName)
    {
        LOG.info("Assert content {} is not displayed", contentName);
        assertFalse(findRow(contentName), String.format("Content %s is displayed", contentName));
        return this;
    }

    public UserTrashcanPage assertContentIsNotDisplayed(ContentModel contentModel)
    {
        assertContentIsNotDisplayed(contentModel.getName());
        return this;
    }

    public UserTrashcanPage clickRecoverButton(String itemName)
    {
        LOG.info("Click Recover button for item {}", itemName);
        getItemRow(itemName).findElement(recoverButtonSelector).click();
        waitUntilNotificationMessageDisappears();

        return this;
    }

    public UserTrashcanPage clickRecoverButton(ContentModel content)
    {
        return clickRecoverButton(content.getName());
    }

    public DeleteDialog clickDeleteButton(String itemName)
    {
        LOG.info("Click Recover button for item {}", itemName);
        getItemRow(itemName).findElement(deleteButtonSelector).click();

        return (DeleteDialog) new DeleteDialog(browser).renderedPage();
    }

    public DeleteDialog clickDeleteButton(ContentModel content)
    {
        return clickDeleteButton(content.getName());
    }

    public DeleteDialog clickEmptyButton()
    {
        getBrowser().waitUntilElementClickable(emptyButton).click();
        return (DeleteDialog) new DeleteDialog(browser).renderedPage();
    }

    public UserTrashcanPage assertNoItemsExistMessageIsDisplayed()
    {
        LOG.info("Assert No Items Exist message is displayed");
        WebElement emptyMessage = getBrowser().waitUntilElementVisible(emptyTrashcanMessageSelector);
        assertTrue(getBrowser().isElementDisplayed(emptyMessage), "Empty items message is not displayed");
        return this;
    }

    public UserTrashcanPage assertNoItemsExistMessageEqualTo(String expectedMessage)
    {
        LOG.info("Assert No Items Exist message is correct");
        WebElement emptyMessage = getBrowser().waitUntilElementVisible(emptyTrashcanMessageSelector);
        assertEquals(emptyMessage.getText(), expectedMessage, String.format("No items exist message not equal to %s", expectedMessage));
        return this;
    }
}
