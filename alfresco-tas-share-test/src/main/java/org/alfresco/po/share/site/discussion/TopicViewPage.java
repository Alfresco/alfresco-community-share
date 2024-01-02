package org.alfresco.po.share.site.discussion;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by Claudia Agache on 8/8/2016.
 */
public class TopicViewPage extends SiteCommon<TopicViewPage>
{
    private final By insertMenuButton = By.xpath("//button[descendant-or-self::*[text()='Insert']]");
    private final By cancelButton = By.cssSelector("button[id$='cancel-button']");
    private final By addReplyHeader = By.xpath("//div[contains(text(), 'Add Reply')]");
    private final By replyBoxMenu = By.className("mce-menu");
    private final By replyBoxMenuItem = By.className("mce-text");
    private final By replyLink = By.cssSelector(".onAddReply>a");
    private final By content = By.className("content");
    private final By status = By.className("nodeStatus");
    private final By noReplies = By.cssSelector(".replyTo+span");
    private final By replyAuthor = By.cssSelector(".userLink>a");
    private final By replyPostedOn = By.xpath("//*[text()='Posted on: ']/following-sibling::*[1]");
    private final By editReplyLink = By.cssSelector(".onEditReply>a");
    private final By showHideReplies = By.className("showHideChildren");
    private final By replyTextArea = By.xpath("//iframe[contains(@title,'Rich Text Area')]");
    private final By topic_Title = By.className("nodeTitle");
    private final By topic_Element = By.cssSelector("div.node.topic");
    private final By topic_Tags = By.cssSelector(".tagLabel+span");
    private final By discussionTopicListLink = By.cssSelector(".backLink>a");
    private final By delete_Link = By.cssSelector(".onDeleteTopic>a");
    private final By editLink = By.cssSelector(".onEditTopic>a");
    private final By topicPublished = By.className("published");
    private final By topicElement = By.cssSelector("div.node.topic");
    private final By replyBoxTitle = By.className("replyTitle");
    private final By submitButton = By.cssSelector("button[id$='submit-button']");
    private final By repliesList = By.cssSelector(".reply");
    private final By repliesLists = By.cssSelector(".reply");

    public TopicViewPage(ThreadLocal<WebDriver> webDriver)
    {
      super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/discussions-topicview?topicId=%s", getCurrentSiteName());
    }

    /**
     * Get element for the specified reply
     *
     * @param reply
     * @return
     */
    private WebElement selectReply(String reply)
    {
        waitInSeconds(3);
        return findFirstElementWithValue(repliesList, reply);
    }

    /**
     * Get Topic Title
     *
     * @return
     */
    public String getTopicTitle()
    {
        return findElement(topic_Title).getText();
    }

    /**
     * Get Add Reply Header text
     *
     * @return
     */
    public String getTopicReplyHeader()
    {
        waitUntilElementIsVisible(addReplyHeader);
        return findElement(addReplyHeader).getText();
    }

    /**
     * Get creation details (date and author)
     *
     * @return
     */
    public String getTopicPublished()
    {
        waitInSeconds(3);
        return findElement(topicPublished).getText();
    }

    /**
     * Get Topic Content
     *
     * @return
     */
    public String getTopicContent()
    {
        return findElement(topic_Element).findElement(content).getText();
    }

    /**
     * Get Number of noReplies to the topic
     *
     * @return
     */
    public String getTopicReplies()
    {
        waitInSeconds(3);
        return findElement(topicElement).findElement(noReplies).getText();
    }

    /**
     * Get topic list of tags
     *
     * @return
     */
    public String getTopicTags()
    {
        return findElement(topic_Tags).getText();
    }

    /**
     * Get the author for the specified reply
     *
     * @param reply
     * @return
     */
    public String getReplyAuthor(String reply)
    {
        waitInSeconds(3);
        return selectReply(reply).findElement(replyAuthor).getText();
    }

    /**
     * Get the number of noReplies for the specified reply
     *
     * @param reply
     * @return
     */
    public String getReplyNoReplies(String reply)
    {
        return selectReply(reply).findElement(noReplies).getText();
    }

    /**
     * Get when the specified reply was posted
     *
     * @param reply
     * @return
     */
    public String getReplyDate(String reply)
    {
        return selectReply(reply).findElement(replyPostedOn).getText();
    }

    /**
     * Method to add reply to topic
     *
     * @return
     */
    public TopicViewPage clickReply()
    {
        findElement(topicElement).findElement(replyLink).click();
        return new TopicViewPage(webDriver);
    }

    /**
     * Method to edit topic
     *
     * @return
     */
    public EditTopicPage editTopic()
    {
        findElement(editLink).click();
        return new EditTopicPage(webDriver);
    }

    /**
     * Method to delete topic
     *
     * @return
     */
    public DeleteDialog deleteTopic()
    {
        clickElement(delete_Link);
        return new DeleteDialog(webDriver);
    }

    /**
     * Method to return back to Discussions Topic List
     *
     * @return
     */
    public TopicListPage clickDiscussionsTopicListLink()
    {
        findElement(discussionTopicListLink).click();
        return new TopicListPage(webDriver);
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
        return new TopicViewPage(webDriver);
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
        return new TopicViewPage(webDriver);
    }

    /**
     * Method used to type reply content
     */

    public void typeReply(String content)
    {
        waitInSeconds(3);
        switchToFrame(waitUntilElementIsVisible(replyTextArea).getAttribute("id"));
        WebElement editable = switchTo().activeElement();
        editable.clear();
        editable.sendKeys(content);
        switchToDefaultContent();
    }

    /**
     * Get the title of the Reply Box
     *
     * @return
     */
    public String getReplyBoxTitle()
    {
        return findElement(replyBoxTitle).getText();
    }

    /**
     * Get the content of the Reply Box
     *
     * @return
     */
    public String getReplyBoxContent()
    {
        switchToFrame(findElement(replyTextArea).getAttribute("id"));
        String editableText = switchTo().activeElement().getText();
        switchToDefaultContent();
        return editableText;
    }

    /**
     * Click on Create/Update button to submit Reply form
     *
     * @return
     */
    public TopicViewPage submitReply()
    {
        findElement(submitButton).click();
        return new TopicViewPage(webDriver);
    }

    /**
     * Click on Cancel button in Reply form
     *
     * @return
     */
    public void cancelReply()
    {
        findElement(cancelButton).click();
    }

    /**
     * Check if replyChild appears indented from its parent
     *
     * @param replyChild
     * @param replyParent
     * @return
     */
    public boolean isReplyIndentedFromItsParent(String replyChild, String replyParent)
    {
        return isElementDisplayed(selectReply(replyParent),
            By.xpath("following-sibling::*[@class='indented']//*[contains(@class, 'content')]/*[text()='" + replyChild + "']"));
    }

    /**
     * Click on Show/ Hide Replies link from Reply Parent
     *
     * @param reply
     * @return
     */
    public TopicViewPage showHideReplies(String reply)
    {
        waitInSeconds(2);
        findElement(showHideReplies).click();
        return new TopicViewPage(webDriver);
    }

    public boolean is_ReplyVisible(String reply)
    {
        waitInSeconds(2);
        for (WebElement listItems : findElements(repliesLists))
        {
            if (listItems.getText().contains(reply))
            {
                return true;
            }
        }
        return false;
    }

    public InsertLinkPopUp selectOptionFromInsertMenu(String option)
    {
        waitInSeconds(3);
        findElement(insertMenuButton).click();
        WebElement insertMenu = waitUntilElementIsVisible(replyBoxMenu);
        findFirstElementWithValue(insertMenu.findElements(replyBoxMenuItem), option).click();
        return new InsertLinkPopUp(webDriver);
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
        waitInSeconds(3);
        return isElementDisplayed(selectReply(reply), By.cssSelector("img[src='" + imageSource + "']"));
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

    public void selectOptionFromInsertMenus(String option)
    {
        waitInSeconds(3);
        findElement(insertMenuButton).click();
        findFirstElementWithValue(findElements(replyBoxMenuItem), option).click();
    }

    public void refreshPage()
    {
        refresh();
        waitInSeconds(3);
    }
}
