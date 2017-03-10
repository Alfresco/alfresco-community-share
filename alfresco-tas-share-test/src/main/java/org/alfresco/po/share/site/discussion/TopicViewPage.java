package org.alfresco.po.share.site.discussion;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.Link;

import java.util.List;

/**
 * Created by Claudia Agache on 8/8/2016.
 */
@PageObject
public class TopicViewPage extends SiteCommon<TopicViewPage>
{
    /* Top Buttons */
    @FindBy(css = ".backLink>a")
    private Link discussionsTopicListLink;

    @RenderWebElement
    @FindBy(css = "div.new-topic button[id$='default-create-button-button']")
    private Button newTopicButton;

    /* Topic area*/
    @RenderWebElement
    @FindBy(css = "div.node.topic")
    private WebElement topicElement;

    @FindBy(className = "nodeTitle")
    private WebElement topicTitle;

    @FindBy(className = "published")
    private WebElement topicPublished;

    @FindBy(css = ".tagLabel+span")
    private WebElement topicTags;

    @FindBy(css = ".onEditTopic>a")
    private Link editLink;

    @FindBy(css = ".onDeleteTopic>a")
    private Link deleteLink;

    private By replyLink = By.cssSelector(".onAddReply>a");
    private By content = By.className("content");
    private By status = By.className("nodeStatus");
    private By noReplies = By.cssSelector(".replyTo+span");

    /* Replies area*/
    @FindAll(@FindBy(css = ".reply"))
    private List<WebElement> repliesList;

    private By replyAuthor = By.cssSelector(".userLink>a");
    private By replyPostedOn = By.xpath("//*[text()='Posted on: ']/following-sibling::*[1]");
    private By editReplyLink = By.cssSelector(".onEditReply>a");
    private By showHideReplies = By.className("showHideChildren");

    /* Add/Edit Reply area */
    private By replyTextArea = By.xpath("//iframe[contains(@title,'Rich Text Area')]");

    @FindBy(className = "replyTitle")
    private WebElement replyBoxTitle;

    @FindBy(css = "button[id$='submit-button']")
    private Button submitButton;

    @FindBy(css = "button[id$='cancel-button']")
    private Button cancelButton;

    @FindBy(xpath = "//button[descendant-or-self::*[text()='Insert']]")
    private Button insertMenuButton;

    private By replyBoxMenu = By.className("mce-menu");
    private By replyBoxMenuItem = By.className("mce-text");

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/discussions-topicview?topicId=%s", getCurrentSiteName());
    }

    /**
     * Get element for the specified reply
     * @param reply
     * @return
     */
    private WebElement selectReply(String reply)
    {
        return browser.findFirstElementWithValue(repliesList, reply);
    }

    /**
     * Get Topic Title
     *
     * @return
     */
    public String getTopicTitle()
    {
        return topicTitle.getText();
    }

    /**
     * Get creation details (date and author)
     *
     * @return
     */
    public String getTopicPublished()
    {
        return topicPublished.getText();
    }

    /**
     * Get Topic Content
     *
     * @return
     */
    public String getTopicContent()
    {
        return topicElement.findElement(content).getText();
    }

    /**
     * Get Number of noReplies to the topic
     *
     * @return
     */
    public String getTopicReplies()
    {
        return topicElement.findElement(noReplies).getText();
    }

    /**
     * Get topic list of tags
     *
     * @return
     */
    public String getTopicTags()
    {
        return topicTags.getText();
    }

    /**
     * Get the author for the specified reply
     *
     * @param reply
     * @return
     */
    public String getReplyAuthor(String reply)
    {
        return selectReply(reply).findElement(replyAuthor).getText();
    }

    /**
     * Get the number of noReplies for the specified reply
     * @param reply
     * @return
     */
    public String getReplyNoReplies(String reply)
    {
        return selectReply(reply).findElement(noReplies).getText();
    }

    /**
     * Get when the specified reply was posted
     * @param reply
     * @return
     */
    public String getReplyDate(String reply)
    {
        return selectReply(reply).findElement(replyPostedOn).getText();
    }

    /**
     * Method to add reply to topic
     * @return
     */
    public TopicViewPage addTopicReply()
    {
        topicElement.findElement(replyLink).click();
        return (TopicViewPage) this.renderedPage();
    }

    /**
     * Method to edit topic
     * @return
     */
    public EditTopicPage editTopic()
    {
        editLink.click();
        return new EditTopicPage();
    }

    /**
     * Method to delete topic
     * @return
     */
    public DeleteDialog deleteTopic()
    {
        deleteLink.click();
        return new DeleteDialog();
    }

    /**
     * Method to return back to Discussions Topic List
     *
     * @return
     */
    public TopicListPage clickDiscussionsTopicListLink()
    {
        discussionsTopicListLink.click();
        return new TopicListPage();
    }

    /**
     * Method to reply to the specified reply
     *
     * @param reply
     * @return
     */
    public TopicViewPage replyToReply(String reply)
    {
        selectReply(reply).findElement(replyLink).click();
        return (TopicViewPage) this.renderedPage();
    }

    /**
     * Method to edit the specified reply
     *
     * @param reply
     * @return
     */
    public TopicViewPage editReply(String reply)
    {
        selectReply(reply).findElement(editReplyLink).click();
        return (TopicViewPage) this.renderedPage();
    }

    /**
     * Method used to type reply content
     */

    public void typeReply(String content)
    {
        browser.switchToFrame(browser.waitUntilElementVisible(replyTextArea).getAttribute("id"));
        WebElement editable = browser.switchTo().activeElement();
        editable.clear();
        editable.sendKeys(content);
        browser.switchToDefaultContent();
    }

    /**
     * Get the title of the Reply Box
     * @return
     */
    public String getReplyBoxTitle()
    {
        return replyBoxTitle.getText();
    }

    /**
     * Get the content of the Reply Box
     * @return
     */
    public String getReplyBoxContent()
    {
        browser.switchToFrame(browser.findElement(replyTextArea).getAttribute("id"));
        String editableText = browser.switchTo().activeElement().getText();
        browser.switchToDefaultContent();
        return editableText;
    }

    /**
     * Click on Create/Update button to submit Reply form
     *
     * @return
     */
    public TopicViewPage submitReply()
    {
        submitButton.click();
        return (TopicViewPage) this.renderedPage();
    }

    /**
     * Click on Cancel button in Reply form
     *
     * @return
     */
    public void cancelReply()
    {
        cancelButton.click();
    }

    /**
     * Check if replyChild appears indented from its parent
     * @param replyChild
     * @param replyParent
     * @return
     */
    public boolean isReplyIndentedFromItsParent(String replyChild, String replyParent)
    {
        return browser.isElementDisplayed(selectReply(replyParent), By.xpath("following-sibling::*[@class='indented']//*[contains(@class, 'content')]/*[text()='" + replyChild + "']"));
    }

    /**
     * Click on Show/ Hide Replies link from Reply Parent
     * @param reply
     * @return
     */
    public TopicViewPage showHideReplies(String reply)
    {
        selectReply(reply).findElement(showHideReplies).click();
        return (TopicViewPage) this.renderedPage();
    }

    /**
     * Check if a reply is visible or not on page
     *
     * @param reply
     * @return true if it is visible
     */
    public boolean isReplyVisible(String reply)
    {
        return browser.isElementDisplayed(selectReply(reply));
    }

    public InsertLinkPopUp selectOptionFromInsertMenu(String option)
    {
        insertMenuButton.click();
        WebElement insertMenu = browser.waitUntilElementVisible(replyBoxMenu);
        browser.findFirstElementWithValue(insertMenu.findElements(replyBoxMenuItem), option).click();
        return new InsertLinkPopUp();
    }

    /**
     * Click on the specified link from reply content
     *
     * @param reply
     * @param linkTitle
     */
    public void clickLinkInReply(String reply, String linkTitle)
    {
        selectReply(reply).findElement(content).findElement(By.cssSelector("a[title='"+ linkTitle +"']")).click();
    }

    /**
     * Check if the specified image is displayed in reply content
     *
     * @param reply
     * @param imageSource
     * @return
     */
    public boolean isImageDisplayedInReply(String reply, String imageSource)
    {
        return browser.isElementDisplayed(selectReply(reply), By.cssSelector("img[src='" + imageSource + "']"));
    }

    /**
     * Get reply status (Updated text)
     *
     * @param reply
     * @return
     */
    public String getReplyStatus(String reply)
    {
        return selectReply(reply).findElement(status).getText();
    }
}
