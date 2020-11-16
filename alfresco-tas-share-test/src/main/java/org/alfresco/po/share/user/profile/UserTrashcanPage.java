package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.SharePage;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class UserTrashcanPage extends SharePage<UserTrashcanPage>
{
    @Autowired
    private DeleteDialog deleteDialog;

    int counter = 0;

    @RenderWebElement
    @FindBy (css = "input[id$='default-search-text']")
    private TextInput searchInput;

    @RenderWebElement
    @FindBy (css = "button[id$='search-button-button']")
    private Button searchButton;

    @FindBy (css = "button[id*='empty']")
    private WebElement emptyButton;

    @FindAll (@FindBy (css = ".yui-dt-liner > .name"))
    private List<WebElement> itemsNameList;

    private By deletionTimestampSelector = By.cssSelector(".yui-dt-liner div:nth-child(2)");
    private By locationBeforeDeletionSelector = By.cssSelector(".yui-dt-liner div:nth-child(3)");
    private By recoverButtonSelector = By.cssSelector("td[headers$='th-actions ']>div>span>span:nth-of-type(1) button");
    private By deleteButtonSelector = By.cssSelector("td[headers$='th-actions ']>div>span>span:nth-of-type(2) button");
    private By emptyTrashcanMessageSelector = By.cssSelector(".yui-dt-empty div");
    private By itemRowsList = By.cssSelector(".yui-dt-data tr");
    private By itemsFolderName = By.xpath("//div[@class='name' and text()='%s']");

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
        searchInput.clear();
        searchInput.sendKeys(searchTerm);
        clickSearch();
        return itemsNameList;
    }

    public void clickSearch()
    {
        searchButton.click();
    }

    public String getItemsNamesList()
    {
        ArrayList<String> itemsNameTextList = new ArrayList<>();
        for (WebElement anItemsNameList : itemsNameList)
        {
            itemsNameTextList.add(anItemsNameList.getText());
        }
        return itemsNameTextList.toString();
    }

    private WebElement getItemRow(String item)
    {
        return browser.findFirstElementWithValue(itemRowsList, item);
    }

    private boolean findRow(String itemName)
    {
        List<WebElement> trashRows = browser.findElements(itemRowsList);
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

        return (DeleteDialog) deleteDialog.renderedPage();
    }

    public DeleteDialog clickDeleteButton(ContentModel content)
    {
        return clickDeleteButton(content.getName());
    }

    public DeleteDialog clickEmptyButton()
    {
        browser.waitUntilElementClickable(emptyButton).click();
        return (DeleteDialog) deleteDialog.renderedPage();
    }

    public UserTrashcanPage assertNoItemsExistMessageIsDisplayed()
    {
        LOG.info("Assert No Items Exist message is displayed");
        WebElement emptyMessage = browser.waitUntilElementVisible(emptyTrashcanMessageSelector);
        assertTrue(browser.isElementDisplayed(emptyMessage), "Empty items message is not displayed");
        return this;
    }

    public UserTrashcanPage assertNoItemsExistsMessageIsCorrect()
    {
        LOG.info("Assert No Items Exist message is correct");
        WebElement emptyMessage = browser.waitUntilElementVisible(emptyTrashcanMessageSelector);
        assertEquals(emptyMessage.getText(), language.translate("emptyTrashcan.noItems"),
            "Empty trashcan message is not correct");
        return this;
    }
}
