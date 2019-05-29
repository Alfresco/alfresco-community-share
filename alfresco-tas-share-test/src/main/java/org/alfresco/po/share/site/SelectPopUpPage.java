package org.alfresco.po.share.site;

import java.util.List;

import org.alfresco.po.share.ShareDialog;
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

    public WebElement selectDetailsRow(String item)
    {
        return browser.findFirstElementWithValue(resultsList, item);
    }

    public void clickItem(String item)
    {
        selectDetailsRow(item).findElement(By.cssSelector("h3.item-name a")).click();
    }

    public void clickAddIcon(String item)
    {
        browser.waitUntilElementsVisible(resultsList);
        selectDetailsRow(item).findElement(addIcon).click();
    }

    public void clickRemoveIcon(String item)
    {
        selectDetailsRow(item).findElement(removeIcon).click();
    }

    public void clickOkButton()
    {
        okButton.click();
    }

    public void search(String searchText)
    {
        browser.waitUntilElementVisible(searchInput).clear();
        searchInput.sendKeys(searchText);
        getBrowser().waitInSeconds(5);
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
}
