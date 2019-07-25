package org.alfresco.po.share.user.admin.adminTools;

import java.util.List;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Laura.Capsa
 */
@PageObject
public class TagManagerPage extends AdminToolsPage
{
    private final By editIconSelector = By.cssSelector("a[id*='edit']");
    private final By deleteIconSelector = By.cssSelector("a[id*='delete']");
    private final By tagSelector = By.cssSelector("b");
    @Autowired
    private DeleteDialog deleteDialog;
    @Autowired
    private EditTagDialog editTagDialog;
    @RenderWebElement
    @FindBy (css = ".title")
    private WebElement tableTitle;
    //@RenderWebElement
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
    //	@RenderWebElement
    @FindBy (css = "div[id*='list-bar-bottom'] span[id*='pages'] .yui-pg-page")
    private List<WebElement> pagesList;
    @RenderWebElement
    @FindBy (css = "span[class*='current-page']")
    private WebElement currentPage;
    @RenderWebElement
    @FindAll (@FindBy (css = ".yui-dt-data tr"))
    private List<WebElement> tagsList;
    @RenderWebElement
    @FindBy (css = "input[id*='search']")
    private WebElement searchInput;
    @RenderWebElement
    @FindBy (css = "button[id*='search']")
    private WebElement searchButton;
    @FindBy (css = "input[id*='edit-tag']")
    private WebElement editTagInputField;
    @FindBy (css = "button[id*='ok']")
    private WebElement okButton;
    @FindBy (css = "button[id*='tag-cancel']")
    private WebElement cancelButton;
    @FindBy (css = "form[id*='edit-tag'] label")
    private WebElement renameLabel;
    @FindBy (css = "form[id*='edit-tag'] div[class='yui-u']")
    private WebElement requiredInput;
    @FindAll (@FindBy (css = "a[id*='edit']"))
    private List<WebElement> editIconList;
    @FindAll (@FindBy (css = "td[class*='action'] a[id*='delete']"))
    private List<WebElement> deleteIconList;
    @FindBy (css = "div[class*='tags-list-info']")
    private WebElement noFoundMessage;
    private String cellsFromColumn = ".yui-dt-data tr td:nth-child(%s)";
    private WebElement rowElement;

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/tag-management";
    }

    /**
     * Click 'Edit Tag' icon for a tag
     *
     * @param tag to be edited
     * @return message if tag is displayed (icon clicked) or not
     */
    public String clickEditTagIcon(String tag)
    {
        if (isTagDisplayed(tag))
        {
            browser.waitUntilElementVisible(rowElement.findElement(editIconSelector)).click();
            editTagDialog.renderedPage();
            return "'Edit tag' icon clicked for " + tag;
        } else
            return tag + " isn't displayed. 'Edit tag' icon not clicked!";
    }

    public boolean isEditTagDialogDisplayed()
    {
        return browser.isElementDisplayed(editTagInputField);
    }

    /**
     * Verify presence of tag in Tags List table
     *
     * @param tag to be found
     * @return true if tag is displayed
     */
    public boolean isTagDisplayed(String tag)
    {
        browser.waitUntilElementIsDisplayedWithRetry(tagSelector, 10);
        LOG.info("Check that tag is displayed: " + tag);

        int counter = 0;
        rowElement = null;

        while (counter <= pagesList.size())
        {
            counter++;
            searchTagInTable(tag);
            if (rowElement != null)
                return true;
            clickNextPage();
        }
        return false;
    }

    private void searchTagInTable(String tag)
    {
        LOG.info(String.format("Looking for tag '%s' on page number #%s", tag, currentPage.getText()));
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
            nextLink.click();
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

    private boolean isTableTitleDisplayed()
    {
        return browser.isElementDisplayed(tableTitle);
    }

    public String getTableTitle()
    {
        if (isTableTitleDisplayed())
            return tableTitle.getText();

        return "Table title isn't displayed";
    }

    public String getTableHead()
    {
        return tableHead.getText();
    }

    public String getRenameLabel()
    {
        return renameLabel.getText();
    }

    public boolean isOkButtonDisplayed()
    {
        return browser.isElementDisplayed(okButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        return browser.isElementDisplayed(cancelButton);
    }

    public String getRequiredInput()
    {
        return requiredInput.getText();
    }

    /**
     * Click 'Delete Tag' icon for a tag
     *
     * @param tag to be deleted
     * @return message if tag is displayed (icon clicked) or not
     */
    public String clickDeleteTagIcon(String tag)
    {
        if (isTagDisplayed(tag))
        {
            browser.waitUntilElementVisible(rowElement.findElement(deleteIconSelector)).click();
            browser.waitUntilElementClickable(deleteDialog.deleteButton, 10);
            return "'Delete tag' icon clicked for " + tag;
        } else
            return tag + " isn't displayed. 'Delete tag' icon not clicked!";
    }


    /**
     * Search by a specific tag name
     *
     * @param tagName - tag name
     */
    public boolean searchTag(String tagName)
    {

        int counter = 0;
        search(tagName);
        while (!isTagDisplayed(tagName) && counter < 3)
        {
            search(tagName);
            counter++;
        }
        return isTagDisplayed(tagName);
    }

    private void search(String tagName)
    {
        searchInput.clear();
        searchInput.sendKeys(tagName);
        searchButton.click();
        browser.waitInSeconds(2);
    }

}