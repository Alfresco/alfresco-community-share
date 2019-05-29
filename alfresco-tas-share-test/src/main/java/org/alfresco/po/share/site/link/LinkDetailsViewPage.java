package org.alfresco.po.share.site.link;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Claudia Agache on 7/22/2016.
 */
@PageObject
public class LinkDetailsViewPage extends SiteCommon<LinkDetailsViewPage>
{
    @Autowired
    LinkPage linkPage;

    @Autowired
    EditLinkPage editLinkPage;

    @RenderWebElement
    @FindBy (css = "[class*=onAddCommentClick] button")
    private WebElement addCommentButton;

    @FindBy (xpath = "//a[contains(text(), 'Links List')]")
    private WebElement linksListLink;

    @FindBy (css = ".node.linksview")
    private WebElement linkView;

    @FindBy (css = ".nodeTitle>a")
    private Link linkTitle;

    @FindBy (css = ".nodeURL>a")
    private Link linkURL;

    @FindBy (xpath = "//*[@class='nodeAttrLabel' and normalize-space(text())='Created on:']/following-sibling::*[1]")
    private WebElement creationDate;

    @FindBy (xpath = "//*[@class='nodeAttrLabel' and normalize-space(text())='Created by:']/following-sibling::*[1]/a")
    private WebElement createdBy;

    @FindBy (xpath = "//*[@class='nodeAttrLabel' and normalize-space(text())='Description:']/following-sibling::*[1]")
    private WebElement description;

    @FindAll (@FindBy (className = "tag-link"))
    private List<WebElement> tagsList;

    @FindBy (css = ".comments-list>h2")
    private WebElement commentsSection;

    @FindBy (css = "[id*=default-comments-list]")
    private WebElement defaultCommentsSection;

    @FindBy (css = ".onEditLink>a")
    private WebElement editLink;

    @FindBy (css = ".onDeleteLink>a")
    private WebElement deleteLink;

    @FindBy (css = "[id=prompt]")
    private WebElement deleteLinkPrompt;

    @FindBy (css = "[id*=default-add-submit-button]")
    private WebElement submitCommentButton;

    @FindAll (@FindBy (css = ".comment-content"))
    private List<WebElement> commentsList;

    @FindAll (@FindBy (css = "[class=info] a"))
    private List<WebElement> commentAuthor;

    @FindAll (@FindBy (css = ".comment-details"))
    private List<WebElement> commentDetailsList;

    @FindBy (css = "[id*=default-add-cancel-button]")
    private WebElement cancelSubmitCommentButton;
    private By commentContentIframe = By.xpath("//iframe[contains(@title,'Rich Text Area')]");

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/links-view", getCurrentSiteName());
    }

    public String getLinkTitle()
    {
        return linkTitle.getText();
    }

    public String getLinkURL()
    {
        return linkURL.getText();
    }

    public String getCreationDate()
    {
        return creationDate.getText();
    }

    public String getCreatedBy()
    {
        return createdBy.getText();
    }

    public String getDescription()
    {
        return description.getText();
    }

    public boolean isTagDisplayedInTagsList(String tag)
    {
        return browser.findFirstElementWithValue(tagsList, tag) != null;
    }

    public boolean isCommentsSectionDisplayed()
    {
        return commentsSection.isDisplayed();
    }

    public boolean isAddCommentButtonDisplayed()
    {
        return addCommentButton.isDisplayed();
    }

    public String getNoCommentsMessage()
    {
        return defaultCommentsSection.getText();
    }

    public boolean isEditLinkDisplayed()
    {
        return editLink.isDisplayed();
    }

    public boolean isDeleteLinkDisplayed()
    {
        return deleteLink.isDisplayed();
    }

    public LinkPage clickOnLinksListLink()
    {
        linksListLink.click();
        return (LinkPage) linkPage.renderedPage();
    }

    public List<String> getTagsList()
    {

        List<String> linkTags = new ArrayList<>();
        for (WebElement linkTag : tagsList)
        {
            linkTags.add(linkTag.getText());
        }
        return linkTags;
    }

    public void clickOnLinkURL(String linkURL)
    {
        browser.findElement(By.xpath("//a[@href ='" + linkURL + "']")).click();

    }

    public int getWinHandlesNo()
    {
        Set<String> set = browser.getWindowHandles();
        return set.size();
    }

    public EditLinkPage clickOnEditLink()
    {
        editLink.click();
        return (EditLinkPage) editLinkPage.renderedPage();
    }

    public boolean clickOnDeleteLink()
    {
        deleteLink.click();
        return deleteLinkPrompt.isDisplayed();
    }

    public boolean clickOnAddCommentButton()
    {
        addCommentButton.click();
        return browser.findElement(commentContentIframe).isDisplayed();
    }

    public void addComment(String comment)
    {
        browser.switchTo().frame((WebElement) browser.findElement(commentContentIframe));
        WebElement editable = browser.switchTo().activeElement();
        editable.sendKeys(comment);
        browser.switchTo().defaultContent();
        submitCommentButton.click();
        browser.refresh();
    }

    public void cancelAddComment(String comment)
    {
        browser.switchTo().frame(browser.findElement(commentContentIframe));
        WebElement editable = browser.switchTo().activeElement();
        editable.sendKeys(comment);
        browser.switchTo().defaultContent();
        cancelSubmitCommentButton.click();
    }

    public List<String> getCommentsList()
    {
        List<String> comments = new ArrayList<>();
        for (WebElement comment : commentsList)
        {
            comments.add(comment.getText());
        }
        return comments;
    }

    public WebElement selectCommentDetailsRow(String commentTitle)
    {
        return browser.findFirstElementWithValue(commentDetailsList, commentTitle);
    }

    public String getCommentAuthor(String comment)
    {
        return selectCommentDetailsRow(comment).findElement(By.cssSelector(".info a")).getText();
    }

    public String getCommentCreationTime(String comment)
    {
        return selectCommentDetailsRow(comment).findElement(By.cssSelector(".info span")).getText();
    }

    public boolean clickEditComment(String comment)
    {
        browser.mouseOver(browser.findFirstElementWithValue(commentDetailsList, comment));
        selectCommentDetailsRow(comment).findElement(By.cssSelector("[class*=edit-comment]")).click();
        return browser.findElement(commentContentIframe).isDisplayed();
    }

    public void clearCommentContent()
    {
        browser.switchTo().frame(browser.findElement(commentContentIframe));
        WebElement editable = browser.switchTo().activeElement();
        editable.clear();
        browser.switchTo().defaultContent();
    }

    public boolean clickDeleteCommentLink(String comment)
    {
        browser.mouseOver(browser.findFirstElementWithValue(commentDetailsList, comment));
        selectCommentDetailsRow(comment).findElement(By.cssSelector("[class*=delete-comment]")).click();
        return deleteLinkPrompt.isDisplayed();
    }
}
