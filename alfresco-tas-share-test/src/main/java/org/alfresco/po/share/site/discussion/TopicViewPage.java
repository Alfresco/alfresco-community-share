package org.alfresco.po.share.site.discussion;

import java.util.List;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.Link;

/**
 * Created by Claudia Agache on 8/8/2016.
 */
public class TopicViewPage extends SiteCommon<TopicViewPage>
{
   // @Autowired
    EditTopicPage editTopicPage;

    //@Autowired
    DeleteDialog deleteDialog;

    //@Autowired
    TopicListPage topicListPage;

    /* Top Buttons */
    @FindBy (css = ".backLink>a")
    private Link discussionsTopicListLink;

    @RenderWebElement
    @FindBy (css = "div.new-topic button[id$='default-create-button-button']")
    private Button newTopicButton;

    /* Topic area */
    @RenderWebElement
    @FindBy (css = "div.node.topic")
    private WebElement topicElement;

    @FindBy (className = "nodeTitle")
    private WebElement topicTitle;

    @FindBy (className = "published")
    private WebElement topicPublished;

    @FindBy (css = ".tagLabel+span")
    private WebElement topicTags;

    @FindBy (css = ".onEditTopic>a")
    private Link editLink;

    @FindBy (css = ".onDeleteTopic>a")
    private Link deleteLink;

    @FindBy (xpath = "//div[contains(text(), 'Add Reply')]")
    private WebElement addReplyHeader;

    /* Replies area */
    @FindAll (@FindBy (css = ".reply"))
    private List<WebElement> repliesList;

    @FindBy (className = "replyTitle")
    private WebElement replyBoxTitle;

    @FindBy (css = "button[id$='submit-button']")
    private Button submitButton;

    @FindBy (css = "button[id$='cancel-button']")
    private Button cancelButton;

    @FindBy (xpath = "//button[descendant-or-self::*[text()='Insert']]")
    private Button insertMenuButton;

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
        return webElementInteraction.findFirstElementWithValue(repliesList, reply);
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
     * Get Add Reply Header text
     *
     * @return
     */
    public String getTopicReplyHeader()
    {
        webElementInteraction.waitUntilElementIsVisible(addReplyHeader);
        return addReplyHeader.getText();
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
        topicElement.findElement(replyLink).click();
        return new TopicViewPage(webDriver);
    }

    /**
     * Method to edit topic
     *
     * @return
     */
    public EditTopicPage editTopic()
    {
        editLink.click();
        return new EditTopicPage(webDriver);
    }

    /**
     * Method to delete topic
     *
     * @return
     */
    public DeleteDialog deleteTopic()
    {
        deleteLink.click();
        return new DeleteDialog(webDriver);
    }

    /**
     * Method to return back to Discussions Topic List
     *
     * @return
     */
    public TopicListPage clickDiscussionsTopicListLink()
    {
        discussionsTopicListLink.click();
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
        webElementInteraction.switchToFrame(webElementInteraction.waitUntilElementIsVisible(replyTextArea).getAttribute("id"));
        WebElement editable = webElementInteraction.switchTo().activeElement();
        editable.clear();
        editable.sendKeys(content);
        webElementInteraction.switchToDefaultContent();
    }

    /**
     * Get the title of the Reply Box
     *
     * @return
     */
    public String getReplyBoxTitle()
    {
        return replyBoxTitle.getText();
    }

    /**
     * Get the content of the Reply Box
     *
     * @return
     */
    public String getReplyBoxContent()
    {
        webElementInteraction.switchToFrame(webElementInteraction.findElement(replyTextArea).getAttribute("id"));
        String editableText = webElementInteraction.switchTo().activeElement().getText();
        webElementInteraction.switchToDefaultContent();
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
        return new TopicViewPage(webDriver);
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
     *
     * @param replyChild
     * @param replyParent
     * @return
     */
    public boolean isReplyIndentedFromItsParent(String replyChild, String replyParent)
    {
        return webElementInteraction.isElementDisplayed(selectReply(replyParent),
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
        selectReply(reply).findElement(showHideReplies).click();
        return new TopicViewPage(webDriver);
    }

    /**
     * Check if a reply is visible or not on page
     *
     * @param reply
     * @return true if it is visible
     */
    public boolean isReplyVisible(String reply)
    {
        return webElementInteraction.isElementDisplayed(selectReply(reply));
    }

    public InsertLinkPopUp selectOptionFromInsertMenu(String option)
    {
        insertMenuButton.click();
        WebElement insertMenu = webElementInteraction.waitUntilElementIsVisible(replyBoxMenu);
        webElementInteraction.findFirstElementWithValue(insertMenu.findElements(replyBoxMenuItem), option).click();
        return new InsertLinkPopUp(webDriver);
    }

    /**
     * Click on the specified link from reply content
     *
     * @param reply
     * @param linkTitle
     */
    public void clickLinkInReply(String reply, String linkTitle)
    {
        selectReply(reply).findElement(content).findElement(By.cssSelector("a[title='" + linkTitle + "']")).click();
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
        return webElementInteraction.isElementDisplayed(selectReply(reply), By.cssSelector("img[src='" + imageSource + "']"));
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
