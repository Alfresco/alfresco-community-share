package org.alfresco.po.share.user.profile;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
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
        clearAndType(searchInput, searchTerm);
        clickSearch();
        waitUntilElementsAreVisible(itemsNameList);
        return findElements(itemsNameList);
    }

    public void clickSearch()
    {
        clickElement(searchButton);
    }

    public String getItemsNamesList()
    {
        List<String> itemsNameTextList = Collections.synchronizedList(new ArrayList<>());
        for (WebElement anItemsNameList : findElements(itemsNameList))
        {
            itemsNameTextList.add(anItemsNameList.getText());
        }
        return itemsNameTextList.toString();
    }

    private WebElement getItemRow(String item)
    {
        return findFirstElementWithValue(itemRowsList, item);
    }

    private boolean findRow(String itemName)
    {
        List<WebElement> trashRows = findElements(itemRowsList);
        return trashRows.stream().anyMatch(row -> row.getText().equals(itemName));
    }

    public UserTrashcanPage assertContentIsNotDisplayed(String contentName)
    {
        log.info("Assert content {} is not displayed", contentName);
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
        log.info("Click Recover button for item {}", itemName);
        clickElement(getItemRow(itemName).findElement(recoverButtonSelector));
        waitUntilNotificationMessageDisappears();

        return this;
    }

    public UserTrashcanPage clickRecoverButton(ContentModel content)
    {
        return clickRecoverButton(content.getName());
    }

    public DeleteDialog clickDeleteButton(String itemName)
    {
        log.info("Click Recover button for item {}", itemName);
        clickElement(getItemRow(itemName).findElement(deleteButtonSelector));

        return new DeleteDialog(webDriver);
    }

    public DeleteDialog clickDeleteButton(ContentModel content)
    {
        return clickDeleteButton(content.getName());
    }

    public DeleteDialog clickEmptyButton()
    {
        clickElement(emptyButton);
        return new DeleteDialog(webDriver);
    }

    public UserTrashcanPage assertNoItemsExistMessageIsDisplayed()
    {
        log.info("Assert No Items Exist message is displayed");
        WebElement emptyMessage = waitUntilElementIsVisible(emptyTrashcanMessageSelector);
        assertTrue(isElementDisplayed(emptyMessage), "Empty items message is not displayed");
        return this;
    }

    public UserTrashcanPage assertNoItemsExistMessageEqualTo(String expectedMessage)
    {
        log.info("Assert No Items Exist message is correct");
        assertEquals(getElementText(emptyTrashcanMessageSelector), expectedMessage, String.format("No items exist message not equal to %s", expectedMessage));
        return this;
    }
}
