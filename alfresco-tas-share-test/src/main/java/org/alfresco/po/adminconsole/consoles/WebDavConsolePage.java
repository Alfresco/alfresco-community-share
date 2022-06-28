package org.alfresco.po.adminconsole.consoles;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
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

    @RenderWebElement
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

    public WebDavConsolePage assertPageHeaderRootIsCorrect()
    {
        Assert.assertEquals(pageHeader.getText(), "Directory listing for /", "WebDav page header is correct");
        return this;
    }

    public WebDavConsolePage assertPageHeaderForDirectoryIsCorrect(String directorPath)
    {
        Assert.assertEquals(pageHeader.getText(), String.format("Directory listing for /%s", directorPath), "WebDav page header is correct");
        return this;
    }

    private boolean isDirectoryPresent(String directoryName)
    {
        getBrowser().waitUntilElementIsDisplayedWithRetry(By.cssSelector("table.listingTable"));
        String getNameColumn = listingTable.getColumnsAsString().subList(0, 1).toString();
        return getNameColumn.contains(directoryName);
    }

    public WebDavConsolePage assertDirectoryIsDisplayed(String directoryName)
    {
        Assert.assertTrue(isDirectoryPresent(directoryName), String.format("Directory %s is displayed", directoryName));
        return this;
    }

    public WebDavConsolePage clickSharedLink()
    {
        sharedLink.click();
        getBrowser().waitUntilElementVisible(upALevelLink);
        return this;
    }

    public WebDavConsolePage assertUpALevelLinkIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(upALevelLink), "Up a level link is displayed");
        return this;
    }

    public WebDavConsolePage assertUpALevelLinkIsNotDisplayed()
    {
        Assert.assertFalse(browser.isElementDisplayed(upALevelLink), "Up a level link is displayed");
        return this;
    }

    public WebDavConsolePage clickUpToLevel()
    {
        getBrowser().waitUntilElementClickable(upALevelLink, 5).click();
        return this;
    }
}
