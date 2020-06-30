package org.alfresco.po.share.user.admin.adminTools;

import static org.alfresco.common.Utils.retry;
import static org.alfresco.common.Utils.retryUntil;
import static org.testng.AssertJUnit.assertTrue;

import java.util.List;

import org.alfresco.common.Utils;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Laura.Capsa
 */
@PageObject
public class TagManagerPage extends AdminToolsPage
{
    private static final By editIconSelector = By.cssSelector("a[id*='edit']");
    private static final By deleteIconSelector = By.cssSelector("a[id*='delete']");
    private static final By tagSelector = By.cssSelector("td.yui-dt-col-name b");
    private static final By tagsRow = By.cssSelector(".yui-dt-data tr");
    private WebElement rowElement;

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
    public EditTagDialog clickEditTagIcon(String tag)
    {
        WebElement editIcon = retry(() ->
        {
            search(tag);
            assertTrue(isTagDisplayed(tag));
            browser.mouseOver(rowElement);
            return browser.waitUntilElementVisible(rowElement.findElement(editIconSelector), WAIT_15_SEC);
        }, DEFAULT_RETRY, TimeoutException.class, AssertionError.class);
        editIcon.click();
        return (EditTagDialog) editTagDialog.renderedPage();
    }

    /**
     * Click 'Delete Tag' icon for a tag
     *
     * @param tag to be deleted
     * @return the delete tag dialog
     */
    public DeleteDialog clickDeleteTagIcon(String tag)
    {
        WebElement deleteIcon = retry(() ->
        {
            search(tag);
            assertTrue(isTagDisplayed(tag));
            browser.mouseOver(rowElement);
            return browser.waitUntilElementVisible(rowElement.findElement(deleteIconSelector), WAIT_15_SEC);
        }, DEFAULT_RETRY);
        deleteIcon.click();
        return (DeleteDialog) deleteDialog.renderedPage();
    }

    /**
     * Verify presence of tag in Tags List table
     *
     * @param tag to be found
     * @return true if tag is displayed
     */
    public boolean isTagDisplayed(String tag)
    {
        browser.waitUntilElementIsDisplayedWithRetry(tagSelector, WAIT_15_SEC);
        LOG.info("Check that tag is displayed: " + tag);

        int counter = 0;
        rowElement = null;

        while (counter <= pagesList.size())
        {
            counter++;
            searchTagInTable(tag);
            if (rowElement != null)
            {
                return true;
            }
            clickNextPage();
        }
        return false;
    }

    private void searchTagInTable(String tag)
    {
        LOG.info(String.format("Looking for tag '%s' on page number #%s", tag, currentPage.getText()));
        List<WebElement> tagsList = browser.waitUntilElementsVisible(tagsRow);
        for (WebElement row : tagsList)
        {
            WebElement tagElement = row.findElement(tagSelector);
            if (tagElement.getText().equals(tag))
            {
                rowElement = row;
                browser.mouseOver(tagElement);
                break;
            }
        }
    }

    private TagManagerPage clickNextPage()
    {
        if (!currentPage.getText().equals(Integer.toString(pagesList.size())))
        {
            nextLink.click();
        }
        return (TagManagerPage) this.renderedPage();
    }

    public boolean isSearchButtonDisplayed()
    {
        return browser.isElementDisplayed(searchButton);
    }

    public boolean isSearchInputFieldDisplayed()
    {
        return browser.isElementDisplayed(searchInput);
    }

    public String getTableTitle()
    {
        return tableTitle.getText();
    }

    public String getTableHead()
    {
        return tableHead.getText();
    }

    /**
     * Search by a specific tag name
     *
     * @param tagName - tag name
     */
    public boolean searchTag(String tagName)
    {
        try
        {
            retryUntil(() -> search(tagName), () -> isTagDisplayed(tagName), DEFAULT_RETRY);
            return true;
        }
        catch(Exception exception)
        {
            return false;
        }
    }

    private TagManagerPage search(String tagName)
    {
        Utils.clearAndType(searchInput, tagName);
        searchButton.click();
        return (TagManagerPage) this.renderedPage();
    }
}