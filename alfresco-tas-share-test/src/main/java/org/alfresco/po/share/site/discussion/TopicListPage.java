package org.alfresco.po.share.site.discussion;

import java.util.List;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.Link;

public class TopicListPage extends SiteCommon<TopicListPage>
{
    @FindBy (css = "div.new-topic button[id$='default-create-button-button']")
    private Button newTopicButton;
    private final By tagsList = By.cssSelector("ul[id$='discussions-topiclist_x0023_default-tags'] .tag>a");
    private final By listTitle = By.className("listTitle");
    private final By newTopics = By.cssSelector("ul.filterLink .new>a");
    private final By mostActiveTopics = By.cssSelector("ul.filterLink .hot>a");
    private final By allTopics = By.cssSelector("ul.filterLink .all>a");
    private final By myTopics = By.cssSelector("ul.filterLink .mine>a");
    private final By noTopicsMessage = By.cssSelector(".yui-dt-empty div");
    private final By title = By.cssSelector(".nodeTitle>a");
    private final By status = By.cssSelector(".nodeStatus");
    private final By topicPublished = By.cssSelector(".published");
    private final By topicContent = By.cssSelector(".content");
    private final By topicReplies = By.cssSelector(".replyTo+span");
    private final By topicTags = By.cssSelector(".tagLabel+span");
    private final By readTopic = By.cssSelector("div.nodeFooter span.nodeAttrValue>a");
    private final By viewTopic = By.cssSelector(".onViewTopic a");
    private final By editTopic = By.cssSelector(".onEditTopic a");
    private final By deleteTopic = By.cssSelector(".onDeleteTopic a");
    private final By topicsList = By.cssSelector("tr.yui-dt-rec td.yui-dt-col-topics");
    private final By viewButton = By.cssSelector("button[id*='discussions-topiclist_x0023_default-simpleView-button']");
    private final By new_TopicButton = By.cssSelector("div.new-topic button[id$='default-create-button-button']");


    public TopicListPage(ThreadLocal<WebDriver> webDriver)
    {
      super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/discussions-topiclist", getCurrentSiteName());
    }

    /**
     * Click on New Topic button
     *
     * @return
     */
    public CreateNewTopicPage clickNewTopicButton()
    {
        clickElement(new_TopicButton);
        return new CreateNewTopicPage(webDriver);
    }

    /**
     * Method to get topic element by its title
     *
     * @param topicTitle
     * @return
     */
    private WebElement getTopicElement(String topicTitle)
    {
        return findFirstElementWithValue(topicsList, topicTitle);
    }

    /**
     * Check if a topic is or not displayed in list
     *
     * @param topicTitle
     * @return true if topic is displayed
     */
    public boolean isTopicDisplayed(String topicTitle)
    {
        return isElementDisplayed(getTopicElement(topicTitle));
    }

    public boolean is_TopicDisplayed(String topicName)
    {
        waitInSeconds(2);
        for (WebElement listItems : findElements(topicsList))
        {
            if (listItems.getText().contains(topicName))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Click on the specified topic title
     *
     * @param topicTitle
     * @return
     */
    public TopicViewPage clickTopicTitle(String topicTitle)
    {
        getTopicElement(topicTitle).findElement(title).click();
        return new TopicViewPage(webDriver);
    }

    /**
     * Get topic status (Updated status)
     *
     * @param topicTitle
     * @return
     */
    public String getTopicStatus(String topicTitle)
    {
        return getTopicElement(topicTitle).findElement(status).getText();
    }

    /**
     * Get publishing details( created date and author) for the specified topic
     *
     * @param topicTitle
     * @return
     */
    public String getTopicPublishedDetails(String topicTitle)
    {
        return getTopicElement(topicTitle).findElement(topicPublished).getText();
    }

    /**
     * Get content for the specified topic
     *
     * @param topicTitle
     * @return
     */
    public String getTopicContent(String topicTitle)
    {
        return getTopicElement(topicTitle).findElement(topicContent).getText();
    }

    /**
     * Get number of replies for the specified topic
     *
     * @param topicTitle
     * @return
     */
    public String getTopicReplies(String topicTitle)
    {
        return getTopicElement(topicTitle).findElement(topicReplies).getText();
    }

    /**
     * Get tags  for the specified topic
     *
     * @param topicTitle
     * @return
     */
    public String getTopicTags(String topicTitle)
    {
        return getTopicElement(topicTitle).findElement(topicTags).getText();
    }

    /**
     * Click on Read link for the specified topic
     *
     * @param topicTitle
     * @return
     */
    public TopicViewPage readTopic(String topicTitle)
    {
        getTopicElement(topicTitle).findElement(readTopic).click();
        return new TopicViewPage(webDriver);
    }

    /**
     * Click on view button for the specified topic
     *
     * @param topicTitle
     * @return
     */
    public TopicViewPage viewTopic(String topicTitle)
    {
        getTopicElement(topicTitle).findElement(viewTopic).click();
        return new TopicViewPage(webDriver);
    }

    /**
     * Click on edit button for the specified topic
     *
     * @param topicTitle
     * @return
     */
    public EditTopicPage editTopic(String topicTitle)
    {
        getTopicElement(topicTitle).findElement(editTopic).click();
        return new EditTopicPage(webDriver);
    }

    /**
     * Click on delete button for the specified topic
     *
     * @param topicTitle
     * @return
     */
    public DeleteDialog deleteTopic(String topicTitle)
    {
        getTopicElement(topicTitle).findElement(deleteTopic).click();
        return new DeleteDialog(webDriver);
    }

    /**
     * Click on Simple/Detailed View button
     *
     * @return
     */
    public TopicListPage toggleBetweenSimpleAndDetailedView()
    {
        String viewButtonText = findElement(viewButton).getText();
        findElement(viewButton).click();
        if (viewButtonText.equals("Simple View"))
        {
            waitUntilElementIsVisible(By.cssSelector(".node.topic.simple"));
        } else
        {
            waitUntilElementDeletedFromDom(By.cssSelector(".node.topic.simple"));
        }
        return new TopicListPage(webDriver);

    }

    /**
     * Check if the specified tag is displayed in Tags area
     *
     * @param tagName
     * @return
     */
    public boolean isTagDisplayed(String tagName)
    {
        return findFirstElementWithValue(tagsList, tagName) != null;
    }

    /**
     * Get number of topics associated with the specified tag (the number between ())
     *
     * @param tagName
     * @return
     */
    public String getTagAssociatedTopicsNo(String tagName)
    {
        String tagElement = findFirstElementWithValue(tagsList, tagName).findElement(By.xpath("..")).getText();
        return tagElement.substring(tagElement.indexOf("("));
    }

    /**
     * Click on the specified tag
     *
     * @param tagName
     * @return
     */
    public TopicListPage clickTag(String tagName)
    {
        findFirstElementWithValue(tagsList, tagName).click();
        return new TopicListPage(webDriver);
    }

    public void refresh(String tagName)
    {
        findFirstElementWithValue(tagsList, tagName).click();
        waitInSeconds(19);
        refresh();
    }

    public boolean isTopicContentDisplayed(String topic)
    {
        try
        {
            return isElementDisplayed(getTopicElement(topic), topicContent);
        }
        catch (NoSuchElementException se)
        {
            return false;
        }
    }

    public boolean areTopicRepliesDisplayed(String topic)
    {
        try
        {
           return isElementDisplayed(getTopicElement(topic), topicReplies);
        }
        catch (NoSuchElementException se)
        {
            return false;
        }
    }

    public boolean areTopicTagsDisplayed(String topic)
    {
        try
        {
            return isElementDisplayed(getTopicElement(topic), topicTags);
        }
        catch (NoSuchElementException se)
        {
            return false;
        }
    }

    public boolean isReadLinkDisplayed(String topic)
    {
        try
        {
            return isElementDisplayed(getTopicElement(topic), readTopic);
        }
        catch (NoSuchElementException se)
        {
            return false;
        }
    }

    public String getMessageDisplayed()
    {
        return findElement(noTopicsMessage).getText();
    }

    public String getTopicListTitle()
    {
        waitInSeconds(3);
        return findElement(listTitle).getText();
    }

    /**
     * Click on the specified option from Topics filter
     *
     * @param option
     * @return
     */
    public TopicListPage filterTopicsBy(String option)
    {
        switch (option)
        {
            case "New":
                findElement(newTopics).click();
                break;
            case "Most Active":
                findElement(mostActiveTopics).click();
                break;
            case "All":
                findElement(allTopics).click();
                break;
            case "My Topics":
                findElement(myTopics).click();
                break;
            default:
                findElement(newTopics).click();
                break;
        }
        return new TopicListPage(webDriver);
    }
}
