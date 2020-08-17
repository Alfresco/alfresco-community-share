package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.common.Utils;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.utility.Utility;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import java.util.List;

/**
 * @author Laura.Capsa
 */
@PageObject
public class TagManagerPage extends AdminToolsPage
{
    private final By editIconSelector = By.cssSelector("a[class$='edit-tag-active']");
    private final By deleteIconSelector = By.cssSelector("a[class$='delete-tag-active']");

    private String tagRow = "//b[text()='%s']/../../../../..";

    @Autowired
    private DeleteDialog deleteDialog;

    @Autowired
    private EditTagDialog editTagDialog;

    @FindBy (css = ".tags-List>.title")
    private WebElement tableTitle;

    @RenderWebElement
    @FindBy (css = ".tags-List div.yui-dt:not([class*='hidden']), .tags-List div.tags-list-info:not([class*='hidden'])")
    private WebElement tableBody;

    @FindBy (css = ".dashlet thead")
    private WebElement tableHead;

    @FindBy (css = "a[id*='next-link']")
    private WebElement nextLink;

    @FindBy (css = "span[id*='next-span']")
    private WebElement nextLinkDisabled;

    @FindBy (css = "a[id*='prev-link']")
    private WebElement previousLink;

    @FindBy (css = "span[id*='prev-span']")
    private WebElement previousLinkDisabled;

    @FindBy (css = "div[id*='list-bar-bottom'] span[id*='pages'] .yui-pg-page")
    private List<WebElement> pagesList;

    @FindBy (css = "span[class*='current-page']")
    private WebElement currentPage;

    @RenderWebElement
    @FindBy (css = "input[id*='search']")
    private WebElement searchInput;

    @RenderWebElement
    @FindBy (css = "button[id*='search']")
    private WebElement searchButton;

    @FindBy (css = "div[class*='tags-list-info']")
    private WebElement noFoundMessage;

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/tag-management";
    }

    /**
     * Click 'Edit Tag' icon for a tag
     *
     * @param tag to be edited
     * @return the edit tag dialog
     */
    public EditTagDialog clickEdit(String tag)
    {
        LOG.info(String.format("Click edit for tag: %s", tag));
        WebElement tagRow = getTagRow(tag);
        browser.mouseOver(searchInput);
        browser.mouseOver(tagRow);
        browser.waitUntilElementVisible(editIconSelector);
        browser.waitUntilElementVisible(tagRow.findElement(editIconSelector)).click();
        return (EditTagDialog) editTagDialog.renderedPage();
    }

    /**
     * Click 'Delete Tag' icon for a tag
     *
     * @param tag to be deleted
     * @return the delete tag dialog
     */
    public DeleteDialog clickDelete(String tag)
    {
        LOG.info(String.format("Click delete for tag: %s", tag));
        WebElement tagRow = getTagRow(tag);
        browser.mouseOver(searchInput);
        browser.mouseOver(tagRow);
        browser.waitUntilElementVisible(tagRow.findElement(deleteIconSelector)).click();
        return (DeleteDialog) deleteDialog.renderedPage();
    }

    private WebElement getTagRow(String tagName)
    {
        boolean found = isTagDisplayed(tagName);
        int pageCount = 1;
        while(!found)
        {
            if(browser.isElementDisplayed(nextLink))
            {
                clickNextPage();
                pageCount++;
                browser.waitUntilElementContainsText(currentPage, String.valueOf(pageCount));
                found = isTagDisplayed(tagName);
            }
            else
            {
                break;
            }
        }
        return browser.waitUntilElementVisible(By.xpath(String.format(tagRow, tagName)));
    }

    public boolean isTagDisplayed(String tagName)
    {
        return browser.isElementDisplayed(By.xpath(String.format(tagRow, tagName)));
    }

    private TagManagerPage clickNextPage()
    {
        if (!currentPage.getText().equals(Integer.toString(pagesList.size())))
        {
            nextLink.click();
        }
        return (TagManagerPage) this.renderedPage();
    }

    public TagManagerPage assertSearchButtonIsDisplayed()
    {
        LOG.info("Assert Search button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public TagManagerPage assertSearchInputFieldDisplayed()
    {
        LOG.info("Assert Search input is displayed");
        Assert.assertTrue(browser.isElementDisplayed(searchInput), "Search input is displayed");
        return this;
    }

    public TagManagerPage assertTableTitleIsCorrect()
    {
        LOG.info(String.format("Assert tags table title is: %s", language.translate("tagManager.tableTitle")));
        Assert.assertEquals(tableTitle.getText(), language.translate("tagManager.tableTitle"), "Table title");
        return this;
    }

    public TagManagerPage assertTableHeadersAreCorrect()
    {
        LOG.info("Assert tag table headers are correct");
        Assert.assertEquals(tableHead.getText(), language.translate("tagManager.tableHead"), "Table headers");
        return this;
    }

    /**
     * Search by a specific tag name
     *
     * @param tagName - tag name
     */
    public TagManagerPage searchTag(String tagName)
    {
        LOG.info(String.format("Search for tag: %s", tagName));
        search(tagName);
        boolean found = isTagDisplayed(tagName);
        int retryCount = 0;
        while(!found && retryCount < WAIT_30)
        {
            Utility.waitToLoopTime(1);
            LOG.error(String.format("Wait for tag %s to be displayed - retry: %s", tagName, retryCount));
            search(tagName);
            found = isTagDisplayed(tagName);
            retryCount++;
        }
        return this;
    }

    private TagManagerPage search(String tagName)
    {
        searchInput.clear();
        searchInput.sendKeys(tagName);
        searchButton.click();
        return (TagManagerPage) this.renderedPage();
    }

    public TagManagerPage assertTagIsDisplayed(String tag)
    {
        LOG.info(String.format("Assert tag %s is displayed", tag));
        Assert.assertTrue(isTagDisplayed(tag), String.format("Tag %s was found", tag));
        return this;
    }

    public TagManagerPage assertTagIsNotDisplayed(String tag)
    {
        LOG.info(String.format("Assert tag %s is NOT displayed", tag));
        Assert.assertFalse(isTagDisplayed(tag), String.format("Tag %s was found", tag));
        return this;
    }
}