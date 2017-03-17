package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Laura.Capsa
 */
@PageObject
public class TagManagerPage extends AdminToolsPage
{
    @Autowired
    private DeleteDialog deleteDialog;

    @RenderWebElement
    @FindBy(css = ".title")
    private WebElement tableTitle;

    @RenderWebElement
    @FindBy(css = ".dashlet thead")
    private WebElement tableHead;

    @FindBy(css = "a[id*='next-link']")
    private WebElement nextLink;

    @RenderWebElement
    @FindBy(css = "div[id*='list-bar-bottom'] span[id*='pages'] .yui-pg-page")
    private List<WebElement> pagesList;

    @FindBy(css = "span[class*='current-page']")
    private WebElement currentPage;

    @RenderWebElement
    @FindBy(css = ".yui-dt-data tr")
    private List<WebElement> tagsList;

    @RenderWebElement
    @FindBy(css = "input[id*='search']")
    private WebElement searchInput;

    @RenderWebElement
    @FindBy(css = "button[id*='search']")
    private WebElement searchButton;

    @FindBy(css = "input[id*='edit-tag']")
    private WebElement editTagInputField;

    @FindBy(css = "button[id*='ok']")
    private WebElement okButton;

    @FindBy(css = "button[id*='tag-cancel']")
    private WebElement cancelButton;

    @FindBy(css = "form[id*='edit-tag'] label")
    private WebElement renameLabel;

    @FindBy(css = "form[id*='edit-tag'] div[class='yui-u']")
    private WebElement requiredInput;

    @FindAll(@FindBy(css = "a[id*='edit']"))
    private List<WebElement> editIconList;

    @FindAll(@FindBy(css = "td[class*='action'] a[id*='delete']"))
    private List<WebElement> deleteIconList;

    private final By tagSelector = By.cssSelector(".yui-dt-data tr b");
    private WebElement tagElement;
    private int indexOfTag;

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
        browser.waitUntilElementIsDisplayedWithRetry(tagSelector, 3);
        if (isTagDisplayed(tag))
        {
            browser.mouseOver(tagElement);
            browser.waitUntilElementVisible(editIconList.get(indexOfTag)).click();
            browser.waitUntilElementClickable(editTagInputField, 30);
            return "'Edit tag' icon clicked for " + tag;
        }
        else
            return tag + " isn't displayed. 'Edit tag' icon not clicked!";
    }

    public boolean isEditTagDialogDisplayed()
    {
        return browser.isElementDisplayed(editTagInputField);
    }

    /**
     * Type text in rename tag input field from Edit Tag dialog. Click 'Ok' button
     * 
     * @param updatedTag new tag
     */
    public void renameTag(String updatedTag)
    {
        browser.waitUntilElementClickable(editTagInputField, 10);
        browser.waitUntilElementClickable(okButton, 10);
        editTagInputField.clear();
        editTagInputField.sendKeys(updatedTag);
        browser.waitInSeconds(1);
        okButton.click();
        browser.waitInSeconds(2);
        this.renderedPage();
    }

    /**
     * Verify presence of tag in Tags List table
     * 
     * @param tag to be found
     * @return true if tag is displayed
     */
    public boolean isTagDisplayed(String tag)
    {
        List<WebElement> rowsList;
        int counter = 0;
        this.renderedPage();

        while (counter < pagesList.size() + 2)
        {
            counter++;
            if (!currentPage.getText().equals(Integer.toString(pagesList.size())))
            {
                nextLink.click();
                this.renderedPage();
            }
            browser.waitInSeconds(2);
            rowsList = browser.waitUntilElementsVisible(tagSelector);
            tagElement = browser.findFirstElementWithValue(rowsList, tag);

            if (browser.isElementDisplayed(tagElement))
            {
                browser.mouseOver(tagElement);
                indexOfTag = rowsList.indexOf(tagElement);
                return true;
            }
        }
        return false;
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
            browser.mouseOver(deleteIconList.get(indexOfTag));
            browser.waitUntilElementVisible(deleteIconList.get(indexOfTag)).click();
            browser.waitUntilElementClickable(deleteDialog.deleteButton, 30);
            return "'Delete tag' icon clicked for " + tag;
        }
        else
            return tag + " isn't displayed. 'Delete tag' icon not clicked!";
    }
}