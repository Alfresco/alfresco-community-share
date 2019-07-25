package org.alfresco.po.share.user.profile;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.alfrescoContent.organizingContent.EmptyTrashcanDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class UserTrashcanPage extends SharePage<UserTrashcanPage>
{
    @Autowired
    EmptyTrashcanDialog emptyTrashcanDialog;
    int counter = 0;
    @RenderWebElement
    @FindBy (css = "input[id$='default-search-text']")
    private TextInput searchInput;
    @RenderWebElement
    @FindBy (css = "button[id$='search-button-button']")
    private Button searchButton;
    @FindBy (css = "button[id*='empty']")
    private WebElement emptyButton;
    @FindBy (css = ".yui-dt-data tr")
    private List<WebElement> itemRowsList;
    @FindAll (@FindBy (css = ".yui-dt-liner > .name"))
    private List<WebElement> itemsNameList;
    private By deletionTimestampSelector = By.cssSelector(".yui-dt-liner div:nth-child(2)");
    private By locationBeforeDeletionSelector = By.cssSelector(".yui-dt-liner div:nth-child(3)");
    private By recoverButtonSelector = By.cssSelector(".yui-dt-liner>span[id*='user-trashcan']>span:nth-child(1) button");
    private By deleteButtonSelector = By.cssSelector(".yui-dt-liner>span[id*='user-trashcan'] span:nth-child(2) button");
    private By emptyTrashcanMessageSelector = By.cssSelector(".yui-dt-empty div");

    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/user-trashcan", getUserName());
    }

    /**
     * Search for items
     *
     * @param searchTerm
     * @return List<WebElement>
     */
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

    public UserTrashcanPage navigate(String userName)
    {
        setUserName(userName);
        return navigate();
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

    public WebElement selectItemRow(String item)
    {
        return browser.findFirstElementWithValue(itemRowsList, item);
    }

    /**
     * Verify the following buttons are displayed for each content item from Trashcan: 'Recover' and 'Delete' button
     *
     * @return message on verrify
     */
    public String verifyItemButtons()
    {
        for (WebElement anItemsNameList : itemsNameList)
        {
            WebElement recoverButton = selectItemRow(anItemsNameList.getText()).findElement(recoverButtonSelector);
            if (!browser.isElementDisplayed(recoverButton))
                return anItemsNameList.getText() + " -> 'Recover' button isn't displayed!";

            WebElement deleteButton = selectItemRow(anItemsNameList.getText()).findElement(deleteButtonSelector);
            if (!browser.isElementDisplayed(deleteButton))
                return anItemsNameList.getText() + " -> 'Delete' button isn't displayed!";
        }
        return "ok";
    }

    /**
     * Verify the following is displayed for each content item from Trashcan: timestamp of deletion and location before deletion
     *
     * @return message on verify
     */
    public String verifyItemDeleteInfo()
    {
        for (WebElement anItemsNameList : itemsNameList)
        {
            WebElement deletionTimestamp = selectItemRow(anItemsNameList.getText()).findElement(deletionTimestampSelector);
            if (!browser.isElementDisplayed(deletionTimestamp))
                return anItemsNameList.getText() + " -> timestamp of deletion isn't displayed!";

            WebElement locationBeforeDeletion = selectItemRow(anItemsNameList.getText()).findElement(locationBeforeDeletionSelector);
            if (!browser.isElementDisplayed(locationBeforeDeletion))
                return anItemsNameList.getText() + " -> location before deletion isn't displayed!";
        }
        return "ok";
    }

    /**
     * Click on 'Recover' button for a trashcan item
     *
     * @param itemName item's button to be clicked
     */
    public void clickRecoverButton(String itemName)
    {
        selectItemRow(itemName).findElement(recoverButtonSelector).click();
        LOG.info("Waiting for trashcan item '" + itemName + "' to be deleted...");
        browser.waitInSeconds(2);
    }

    /**
     * Empty Trashcan
     */
    public EmptyTrashcanDialog clickEmptyButton()
    {
        emptyButton.click();
        return (EmptyTrashcanDialog) emptyTrashcanDialog.renderedPage();
    }

    public String getNoItemsMessage()
    {
        WebElement emptyTrashcanMessage = browser.waitUntilElementVisible(emptyTrashcanMessageSelector);
        if (!isEmptyTrashcanMessageDisplayed() && counter < 2)
        {
            counter++;
            browser.refresh();
            this.renderedPage();
            getNoItemsMessage();
        }
        return emptyTrashcanMessage.getText();
    }

    public boolean isEmptyTrashcanMessageDisplayed()
    {
        return browser.isElementDisplayed(emptyTrashcanMessageSelector);
    }
}
