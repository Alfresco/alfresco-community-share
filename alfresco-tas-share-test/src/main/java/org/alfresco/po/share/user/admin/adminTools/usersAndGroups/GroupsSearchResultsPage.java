package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Mirela Tifui on 12/12/2016.
 */
@PageObject
public class GroupsSearchResultsPage extends SharePage<GroupsSearchResultsPage>
{
    @RenderWebElement
    @FindBy (css = "button[id$='_default-search-button-button']")
    private WebElement searchButton;

    @RenderWebElement
    @FindBy (css = "button[id$='_default-browse-button-button']")
    private WebElement browseButton;

    @RenderWebElement
    @FindBy (css = "input[id$='_default-show-all']")
    private WebElement showSystemGroups;

    public WebElement identifier(String identifierName)
    {
        return browser.findElement(By.xpath("//div[text()='" + identifierName + "']"));
    }

    public WebElement displayName(String displayName)
    {
        return browser.findElement(By.xpath("//div[text()='" + displayName + "']/../..//td[contains(@class,'yui-dt-col-displayName ')]"));
    }

    public WebElement editButtonForGroup(String groupName)
    {
        return browser.findElement(By.xpath("//div[text()='" + groupName + "']/../..//a[@class='update']"));
    }

    public WebElement deleteButtonForGroup(String groupName)
    {
        return browser.findElement(By.xpath("//div[text()='" + groupName + "']/../..//a[@class='delete']"));
    }

    @Override
    public String getRelativePath()
    {
        return null;
    }

    public HtmlPage clickEditButtonGroup(String groupName, HtmlPage page)
    {
        browser.waitUntilElementClickable(editButtonForGroup(groupName), 5L);
        editButtonForGroup(groupName).click();
        return page.renderedPage();
    }

    public HtmlPage clickDeleteButtonForGroup(String groupName, HtmlPage page)
    {
        browser.waitUntilElementClickable(deleteButtonForGroup(groupName), 5L);
        deleteButtonForGroup(groupName).click();
        return page.renderedPage();
    }

    public String getIdentifierText(String identifierName)
    {
        browser.waitUntilElementVisible(identifier(identifierName));
        return identifier(identifierName).getText();
    }

    public String getDisplayName(String displayName)
    {
        browser.waitUntilElementVisible(displayName(displayName));
        return displayName(displayName).getText();
    }
}
