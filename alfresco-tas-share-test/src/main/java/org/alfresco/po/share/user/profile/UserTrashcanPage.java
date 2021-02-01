package org.alfresco.po.share.user.profile;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UserTrashcanPage extends SharePage2<UserTrashcanPage>
{
    private String userName;

    private final By searchInput = By.cssSelector("input[id$='default-search-text']");
    private final By searchButton = By.cssSelector("button[id$='search-button-button']");
    private final By emptyButton = By.cssSelector("button[id*='empty']");
    private final By itemsNameList = By.cssSelector(".yui-dt-liner > .name");
    private final By recoverButtonSelector = By.cssSelector("td[headers$='th-actions ']>div>span>span:nth-of-type(1) button");
    private final By deleteButtonSelector = By.cssSelector("td[headers$='th-actions ']>div>span>span:nth-of-type(2) button");
    private final By emptyTrashcanMessageSelector = By.cssSelector(".yui-dt-empty div");
    private final By itemRowsList = By.cssSelector(".yui-dt-data tr");

    public UserTrashcanPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
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
        webElementInteraction.clearAndType(searchInput, searchTerm);
        clickSearch();
        webElementInteraction.waitUntilElementsAreVisible(itemsNameList);
        return webElementInteraction.findElements(itemsNameList);
    }

    public void clickSearch()
    {
        webElementInteraction.clickElement(searchButton);
    }

    public String getItemsNamesList()
    {
        List<String> itemsNameTextList = Collections.synchronizedList(new ArrayList<>());
        for (WebElement anItemsNameList : webElementInteraction.findElements(itemsNameList))
        {
            itemsNameTextList.add(anItemsNameList.getText());
        }
        return itemsNameTextList.toString();
    }

    private WebElement getItemRow(String item)
    {
        return webElementInteraction.findFirstElementWithValue(itemRowsList, item);
    }

    private boolean findRow(String itemName)
    {
        List<WebElement> trashRows = webElementInteraction.findElements(itemRowsList);
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
//        webElementInteraction.waitUntilElementIsVisible(getItemRow(itemName).findElement(recoverButtonSelector));
        webElementInteraction.clickElement(getItemRow(itemName).findElement(recoverButtonSelector));
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
//        webElementInteraction.waitUntilElementIsVisible(getItemRow(itemName).findElement(deleteButtonSelector));
        webElementInteraction.clickElement(getItemRow(itemName).findElement(deleteButtonSelector));

        return new DeleteDialog(webDriver);
    }

    public DeleteDialog clickDeleteButton(ContentModel content)
    {
        return clickDeleteButton(content.getName());
    }

    public DeleteDialog clickEmptyButton()
    {
//        webElementInteraction.waitUntilElementIsVisible(emptyButton);
        webElementInteraction.clickElement(emptyButton);
        return new DeleteDialog(webDriver);
    }

    public UserTrashcanPage assertNoItemsExistMessageIsDisplayed()
    {
        LOG.info("Assert No Items Exist message is displayed");
        WebElement emptyMessage = webElementInteraction.waitUntilElementIsVisible(emptyTrashcanMessageSelector);
        assertTrue(webElementInteraction.isElementDisplayed(emptyMessage), "Empty items message is not displayed");
        return this;
    }

    public UserTrashcanPage assertNoItemsExistMessageEqualTo(String expectedMessage)
    {
        LOG.info("Assert No Items Exist message is correct");
        assertEquals(webElementInteraction.getElementText(emptyTrashcanMessageSelector), expectedMessage, String.format("No items exist message not equal to %s", expectedMessage));
        return this;
    }
}
