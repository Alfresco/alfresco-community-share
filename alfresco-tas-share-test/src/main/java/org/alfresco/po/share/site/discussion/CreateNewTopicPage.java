package org.alfresco.po.share.site.discussion;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

public class CreateNewTopicPage extends SiteCommon<CreateNewTopicPage>
{
    @FindBy (css = "button[id$='discussions-createtopic_x0023_default-submit-button']")
    protected WebElement saveButton;

    @FindBy (css = "button[id$='discussions-createtopic_x0023_default-cancel-button']")
    protected WebElement cancelButton;
    @FindBy (css = "input[id$='discussions-createtopic_x0023_default-title']")
    protected WebElement topicTitle;
    protected By currentTagList = By.cssSelector("ul[id$='discussions-createtopic_x0023_default-current-tags'] .taglibrary-action");
    protected By topicContent = By.cssSelector("iframe[id*='discussions-createtopic_x0023_default-content']");
    protected By page_Header = By.cssSelector(".page-form-header>h1");
    protected By topic_Title = By.cssSelector("input[id$='discussions-createtopic_x0023_default-title']");
    protected By tag_Input = By.cssSelector("input[id$='discussions-createtopic_x0023_default-tag-input-field']");
    protected By add_TagButton = By.cssSelector("button[id*='discussions-createtopic_x0023_default-add-tag-button']");
    protected By buttonSave = By.cssSelector("button[id$='discussions-createtopic_x0023_default-submit-button']");
    protected By buttonCancel = By.cssSelector("button[id$='discussions-createtopic_x0023_default-cancel-button']");

    public CreateNewTopicPage(ThreadLocal<WebDriver> webDriver)
    {
      super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/discussions-createtopic", getCurrentSiteName());
    }

    /**
     * Check if page is opened
     *
     * @return true if page header is Create New Topic
     */
    public boolean isPageDisplayed()
    {
        return findElement(page_Header).getText().equals("Create New Topic");
    }

    /**
     * Method used to type topic title
     */
    public void typeTopicTitle(String title)
    {
        findElement(topic_Title).clear();
        findElement(topic_Title).sendKeys(title);
    }

    /**
     * Method used to type topic content
     */
    public void typeTopicContent(String content)
    {
        waitUntilElementIsVisible(topicContent);
        switchToFrame(findElement(topicContent).getAttribute("id"));
        WebElement editable = switchTo().activeElement();
        editable.clear();
        editable.sendKeys(content);
        switchToDefaultContent();
    }

    /**
     * Click on save button
     *
     * @return wiki page
     */
    public void clickSaveButton()
    {
        findElement(buttonSave).click();
    }

    /**
     * Click on cancel button
     *
     * @return wiki pages list
     */
    public void clickCancelButton()
    {
        findElement(buttonCancel).click();
    }

    /**
     * Method used to add tag
     */
    public void addTag(String tagName)
    {
        findElement(tag_Input).clear();
        findElement(tag_Input).sendKeys(tagName);
        findElement(add_TagButton).click();
    }

    /**
     * This method returns the list of topic tags
     *
     * @return list of wiki page titles
     */
    public List<String> getTopicCurrentTagsList()
    {
        List<String> topicTags = new ArrayList<>();
        for (WebElement tag : findElements(currentTagList))
        {
            topicTags.add(tag.getText());
        }
        return topicTags;
    }

    public String getTopicTitle()
    {
        return topicTitle.getAttribute("value");
    }

    public String getTopicContent()
    {
        switchToFrame(findElement(topicContent).getAttribute("id"));
        WebElement editable = switchTo().activeElement();
        String editableText = editable.getText();
        switchToDefaultContent();
        return editableText;
    }

    /**
     * Remove tag by clicking on it
     *
     * @param tag
     */
    public void removeTag(String tag)
    {
        findFirstElementWithValue(currentTagList, tag).click();
    }
}