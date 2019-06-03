package org.alfresco.po.alfrescoconsoles;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Table;

/**
 * Created by Mirela Tifui on 11/1/2017.
 */
@PageObject
public class WebDavConsolePage extends Console<WebDavConsolePage>
{
    @RenderWebElement
    @FindBy (css = "td.textLocation")
    private WebElement pageHeader;

    @FindBy (css = "table.listingTable")
    private Table listingTable;

    @FindBy (css = "a[href='/alfresco/webdav/Shared']")
    private WebElement sharedLink;

    @FindBy (css = "a[href='/alfresco/webdav']")
    private WebElement upALevelLink;

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/webdav";
    }

    public String getPageHeaderText()
    {
        browser.waitUntilElementVisible(pageHeader);
        return pageHeader.getText();
    }

    public boolean isDirectoryPresent(String directoryName)
    {
        getBrowser().waitUntilElementIsDisplayedWithRetry(By.cssSelector("table.listingTable"));
        String getNameColumn = listingTable.getColumnsAsString().subList(0, 1).toString();
        return getNameColumn.contains(directoryName);
    }

    public void clickOnDirectory(int directoryIndex)
    {
        System.out.println("cell text " + listingTable.getCellAt(1, directoryIndex).getText());
        listingTable.getCellAt(1, directoryIndex).click();
    }

    public void clickSharedLink()
    {
        sharedLink.click();
        getBrowser().waitUntilElementVisible(upALevelLink);
    }

    public boolean isUpALevelLinkDisplayed()
    {
        return browser.isElementDisplayed(upALevelLink);
    }

    public void clickUpToLevel()
    {
        getBrowser().waitUntilElementClickable(upALevelLink, 5).click();
    }
}
