package org.alfresco.po.share.site;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.Utility;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SelectPopUpPage extends BaseDialogComponent
{
    private final By resultsList = By.cssSelector(".yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-results'] [class$='dt-data'] tr");
    private final By selectedList = By.cssSelector(".yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-selectedItems'] [class$='dt-data'] tr");
    @RenderWebElement
    private final By okButton = By.cssSelector(".yui-dialog[style*='visibility: visible'] [id$='cntrl-ok-button']");
    private final By searchInput = By.cssSelector(".yui-dialog[style*='visibility: visible'] input[id*='cntrl-picker-searchText']");
    private final By searchButton = By.cssSelector(".yui-dialog[style*='visibility: visible'] button[id$='searchButton-button']");
    private final By addIcon = By.cssSelector(".yui-dialog[style*='visibility: visible'] [class*='addIcon']");
    private final By removeIcon = By.cssSelector("[class*='removeIcon']");

    public SelectPopUpPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public WebElement selectDetailsRowResultList(String item)
    {
        return getBrowser().findFirstElementWithValue(resultsList, item);
    }

    public WebElement selectDetailsRowSelectedList(String item)
    {
        return getBrowser().findFirstElementWithValue(selectedList, item);
    }

    public void clickItem(String item)
    {
        selectDetailsRowResultList(item).findElement(By.cssSelector("h3.item-name a")).click();
    }

    public void clickAddIcon(String item)
    {
        getBrowser().waitUntilElementsVisible(resultsList);
        selectDetailsRowResultList(item).findElement(addIcon).click();
    }

    public boolean isStringPresentInSearchList(String toCheck)
    {
        return DataUtil.isStringPresentInWebElementList(toCheck, getBrowser().findElements(resultsList));
    }

    public boolean isStringPresentInSelectedList(String toCheck)
    {
        return DataUtil.isStringPresentInWebElementList(toCheck, getBrowser().findElements(selectedList));
    }


    public void clickRemoveIcon(String item)
    {
        getBrowser().waitUntilElementsVisible(selectedList);
        selectDetailsRowResultList(item).findElement(removeIcon).click();
    }

    public void clickOkButton()
    {
        getBrowser().findElement(okButton).click();
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
        clearAndType(searchInput, searchText);
        getBrowser().waitUntilElementClickable(searchButton).click();
        int counter = 0;
        while (!getBrowser().isElementDisplayed(addIcon) && counter < WAIT_15)
        {
            LOG.info("Search: {}", counter);
            Utility.waitToLoopTime(1);
            getBrowser().findElement(searchButton).click();
            counter++;
        }
    }

    public boolean isSearchButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(searchButton);
    }
}
