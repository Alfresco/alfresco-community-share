package org.alfresco.po.share.site;

import static org.alfresco.common.Wait.WAIT_15;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.DataUtil;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.Utility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SelectPopUpPage extends BaseDialogComponent
{
    private final By resultsList = By.cssSelector(".yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-results'] [class$='dt-data'] tr");
    private final By selectedList = By.cssSelector(".yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-selectedItems'] [class$='dt-data'] tr");
    private final By okButton = By.cssSelector(".yui-dialog[style*='visibility: visible'] [id$='cntrl-ok-button']");
    private final By searchInput = By.cssSelector(".yui-dialog[style*='visibility: visible'] input[id*='cntrl-picker-searchText']");
    private final By searchButton = By.cssSelector(".yui-dialog[style*='visibility: visible'] button[id$='searchButton-button']");
    private final By addIcon = By.cssSelector(".yui-dialog[style*='visibility: visible'] [class*='addIcon']");
    private final By removeIcon = By.cssSelector("[class*='removeIcon']");

    public SelectPopUpPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public WebElement selectDetailsRowResultList(String item)
    {
        return findFirstElementWithValue(resultsList, item);
    }

    public WebElement selectDetailsRowSelectedList(String item)
    {
        return findFirstElementWithValue(selectedList, item);
    }

    public void clickItem(String item)
    {
        selectDetailsRowResultList(item).findElement(By.cssSelector("h3.item-name a"));
    }
    public void click_Item(String item)
    {
        selectDetailsRowResultList(item).click();
    }

    public void clickAddIcon(String item)
    {
        waitUntilElementsAreVisible(resultsList);
        clickElement(selectDetailsRowResultList(item).findElement(addIcon));
    }

    public boolean isStringPresentInSearchList(String toCheck)
    {
        waitInSeconds(3);
        return DataUtil.isStringPresentInWebElementList(toCheck, findElements(resultsList));
    }

    public boolean isStringPresentInSelectedList(String toCheck)
    {
        return DataUtil.isStringPresentInWebElementList(toCheck, findElements(selectedList));
    }


    public void clickRemoveIcon(String item)
    {
        waitUntilElementsAreVisible(selectedList);
        clickElement(selectDetailsRowSelectedList(item).findElement(removeIcon));
    }

    public void clickOkButton()
    {
        clickElement(okButton);
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
        clickElement(searchButton);
        int counter = 0;
        while (!isElementDisplayed(addIcon) && counter < WAIT_15.getValue())
        {
            log.info("Search: {}", counter);
            Utility.waitToLoopTime(1);
            clickElement(searchButton);
            counter++;
        }
    }

    public boolean isSearchButtonDisplayed()
    {
        return isElementDisplayed(searchButton);
    }
}
