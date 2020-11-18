package org.alfresco.po.share.site;

import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.ShareDialog2;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Primary;

@Primary
@PageObject
public class SelectPopUpPage extends ShareDialog
{
    @FindAll (@FindBy (css = ".yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-results'] [class$='dt-data'] tr"))
    protected List<WebElement> resultsList;
    @FindAll (@FindBy (css = ".yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-selectedItems'] [class$='dt-data'] tr"))
    protected List<WebElement> selectedList;
    @RenderWebElement
    @FindBy (css = ".yui-dialog[style*='visibility: visible'] [id$='cntrl-ok-button']")
    private WebElement okButton;
    @FindBy (css = "[id$='issueAssignedTo-cntrl-cancel-button']")
    private WebElement cancelButton;
    @FindBy (css = ".yui-dialog[style*='visibility: visible'] input[id*='cntrl-picker-searchText']")
    private WebElement searchInput;
    @FindBy (css = ".yui-dialog[style*='visibility: visible'] button[id$='searchButton-button']")
    private WebElement searchButton;
    private By addIcon = By.cssSelector(".yui-dialog[style*='visibility: visible'] [class*='addIcon']");
    private By removeIcon = By.cssSelector("[class*='removeIcon']");

    public WebElement selectDetailsRowResultList(String item)
    {
        return browser.findFirstElementWithValue(resultsList, item);
    }

    public WebElement selectDetailsRowSelectedList(String item)
    {
        return browser.findFirstElementWithValue(selectedList, item);
    }

    public void clickItem(String item)
    {
        selectDetailsRowResultList(item).findElement(By.cssSelector("h3.item-name a")).click();
    }

    public void clickAddIcon(String item)
    {
        browser.waitUntilElementsVisible(resultsList);
        selectDetailsRowResultList(item).findElement(addIcon).click();
    }

    public boolean isStringPresentInSearchList(String toCheck)
    {
        return DataUtil.isStringPresentInWebElementList(toCheck, resultsList);
    }

    public boolean isStringPresentInSelectedList(String toCheck)
    {
        return DataUtil.isStringPresentInWebElementList(toCheck, selectedList);
    }


    public void clickRemoveIcon(String item)
    {
        browser.waitUntilElementsVisible(selectedList);
        selectDetailsRowResultList(item).findElement(removeIcon).click();
    }

    public void clickOkButton()
    {
        okButton.click();
    }


    public boolean isAddIconDisplayed(String item)
    {
        return selectDetailsRowResultList(item).findElement(addIcon).isDisplayed();
    }

    public boolean isRemoveIconDisplayed(String item)
    {
        return selectDetailsRowSelectedList(item).findElement(removeIcon).isDisplayed();
    }

    public void search(String searchText)
    {
        browser.waitUntilElementVisible(searchInput).clear();
        searchInput.sendKeys(searchText);
        browser.waitUntilElementClickable(searchButton).click();
        int counter = 0;
        while (!browser.isElementDisplayed(addIcon) && counter < 2)
        {
            LOG.info("Search: " + counter);
            searchButton.click();
            browser.waitInSeconds(5);
            counter++;
        }
    }

    public boolean isSearchButtonDisplayed()
    {
        return browser.isElementDisplayed(searchButton);
    }
}
