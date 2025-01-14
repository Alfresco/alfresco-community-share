package org.alfresco.po.share.site.link;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class LinkDetailsViewPage extends SiteCommon<LinkDetailsViewPage>
{
    private final By addCommentButton          = By.cssSelector("[class*=onAddCommentClick] button");
    private final By linksListLink             = By.xpath("//a[contains(text(), 'Links List')]");
    private final By linkTitle                 = By.cssSelector(".nodeTitle>a");
    private final By linkURL                   = By.cssSelector(".nodeURL>a");
    private final By creationDate              =
        By.xpath("//*[@class='nodeAttrLabel' and normalize-space(text())='Created on:']/following-sibling::*[1]");
    private final By createdBy                 =
        By.xpath("//*[@class='nodeAttrLabel' and normalize-space(text())='Created by:']/following-sibling::*[1]/a");
    private final By description               =
        By.xpath("//*[@class='nodeAttrLabel' and normalize-space(text())='Description:']/following-sibling::*[1]");
    private final By tagsList                  = By.className("tag-link");
    private final By editLink                  = By.cssSelector(".onEditLink>a");
    private final By deleteLink                = By.cssSelector(".onDeleteLink>a");
    private final By deleteLinkPrompt          = By.cssSelector("[id=prompt]");
    private final By submitCommentButton       = By.cssSelector("[id*=default-add-submit-button]");
    private final By commentsList              = By.cssSelector(".comment-content");
    private final By commentDetailsList        = By.cssSelector(".comment-details");
    private final By cancelSubmitCommentButton = By.cssSelector("[id*=default-add-cancel-button]");
    private final By commentContentIframe      = By.xpath("//iframe[contains(@title,'Rich Text Area')]");
    private final By noComment                 = By.cssSelector(".yui-dt-empty .yui-dt-liner");
    private final By saveButton                =
        By.cssSelector("#template_x002e_comments_x002e_links-view_x0023_default-yui-rec5-submit-button");
    private final By deleteMessage             = By.cssSelector("[id=prompt] [class=bd]");
    private final By deleteButton              = By.xpath("//button[contains(text(), 'Delete')]");
    private final By cancelDeleteButton        = By.xpath("//button[contains(text(), 'Cancel')]");
    private final By commentContentIEditframe  =
        By.cssSelector("#template_x002e_comments_x002e_links-view_x0023_default-yui-rec5-content_ifr");
    private       By deleteAction              = By.xpath("//a[text()='Delete']");
    private       By editAction                = By.xpath("//a[text()='Edit']");

    public LinkDetailsViewPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/links-view", getCurrentSiteName());
    }

    public LinkDetailsViewPage assertLinkTitleEquals(String expectedLinkTitle)
    {
        log.info("Assert link title equals: {}", expectedLinkTitle);
        assertEquals(getElementText(linkTitle), expectedLinkTitle,
                     String.format("Link title not equals %s ", expectedLinkTitle));

        return this;
    }

    public LinkDetailsViewPage assertLinkUrlEquals(String expectedLinkUrl)
    {
        log.info("Assert link url equals: {}", expectedLinkUrl);
        assertEquals(getElementText(linkURL), expectedLinkUrl,
                     String.format("Link url not equals %s ", expectedLinkUrl));
        return this;
    }

    public LinkDetailsViewPage assertLinkCreationDateContains(DateFormat dateFormat)
    {
        log.info("Assert link creation date contains: {}", dateFormat);
        assertTrue(getElementText(creationDate).contains(dateFormat.format(new Date())),
                   String.format("Link creation date not contains %s", dateFormat));

        return this;
    }

    public LinkDetailsViewPage assertCreatedByLabelEqualsFullUserName(String firstName, String lastName)
    {
        log.info("Assert created by label equals full username: {}", firstName, lastName);
        assertEquals(getElementText(createdBy), firstName.concat(" " + lastName),
                     String.format("Full username not equals %s and %s ", firstName, lastName));

        return this;
    }

    public LinkDetailsViewPage assertLinkDescriptionEquals(String expectedLinkDescription)
    {
        log.info("Assert link description equals: {}", expectedLinkDescription);
        assertEquals(getElementText(description), expectedLinkDescription,
                     String.format("Link description not equals %s ", expectedLinkDescription));

        return this;
    }

    public void clickOnLinksListLink()
    {
        clickElement(linksListLink);
    }

    public List<String> getTagsList()
    {

        List<String> linkTags = new ArrayList<>();
        for (WebElement linkTag : waitUntilElementsAreVisible(tagsList))
        {
            linkTags.add(linkTag.getText());
        }
        return linkTags;
    }

    public void clickOnLinkURL(String linkURL)
    {
        findElement(By.xpath("//a[@href ='" + linkURL + "']")).click();
    }

    public EditLinkPage clickOnEditLink()
    {
        clickElement(editLink);
        return new EditLinkPage(webDriver);
    }

    public boolean clickOnDeleteLink()
    {
        clickElement(deleteLink);
        return isElementDisplayed(deleteLinkPrompt);
    }

    public boolean clickOnAddCommentButton()
    {
        clickElement(addCommentButton);
        return isElementDisplayed(commentContentIframe);
    }

    public void addComment(String comment)
    {
        switchTo().frame(findElement(commentContentIframe));
        WebElement editable = switchTo().activeElement();
        editable.sendKeys(comment);
        switchTo().defaultContent();
        clickElement(submitCommentButton);
    }

    public void cancelAddComment(String comment)
    {
        switchTo().frame(findElement(commentContentIframe));
        WebElement editable = switchTo().activeElement();
        editable.sendKeys(comment);
        switchTo().defaultContent();
        clickElement(cancelSubmitCommentButton);
    }

    public List<String> getCommentsList()
    {
        List<String> comments = new ArrayList<>();
        waitInSeconds(3);
        for (WebElement comment : waitUntilElementsAreVisible(commentsList))
        {
            comments.add(comment.getText());
        }
        return comments;
    }

    public WebElement selectCommentDetailsRow(String commentTitle)
    {
        return findFirstElementWithValue(commentDetailsList, commentTitle);
    }

    public String getCommentAuthor(String comment)
    {
        return selectCommentDetailsRow(comment).findElement(By.cssSelector(".info a"))
            .getText();
    }

    public String getCommentCreationTime(String comment)
    {
        return selectCommentDetailsRow(comment).findElement(By.cssSelector(".info span"))
            .getText();
    }

    public boolean clickEditComment(String comment)
    {
        mouseOver(findFirstElementWithValue(commentDetailsList, comment));
        clickElement(selectCommentDetailsRow(comment).findElement(By.cssSelector("[class*=edit-comment]")));
        return findElement(commentContentIframe).isDisplayed();
    }

    public void clearCommentContent()
    {
        switchTo().frame(findElement(commentContentIframe));
        WebElement editable = switchTo().activeElement();
        editable.clear();
        switchTo().defaultContent();
    }

    public boolean clickDeleteCommentLink(String comment)
    {
        mouseOver(findFirstElementWithValue(commentDetailsList, comment));
        selectCommentDetailsRow(comment).findElement(By.cssSelector("[class*=delete-comment]"))
            .click();
        return isElementDisplayed(deleteLinkPrompt);
    }

    public String getLinkTitle()
    {
        return findElement(linkTitle).getText();
    }

    public String getLinkURL()
    {
        return findElement(linkURL).getText();
    }

    public String getCreationDate()
    {
        return findElement(creationDate).getText();
    }

    public String getCreatedBy()
    {
        return findElement(createdBy).getText();
    }

    public String getDescription()
    {
        return findElement(description).getText();
    }

    public String getLinkTag()
    {
        return findElement(tagsList).getText();
    }

    public String getNoCommentsMessage()
    {
        return findElement(noComment).getText();
    }

    public String getDeleteMessage()
    {
        return findElement(deleteMessage).getText();
    }

    public void clickDelete()
    {
        findElement(deleteButton).click();
    }

    public void clickCancel()
    {
        findElement(cancelDeleteButton).click();
    }

    public void clearEditCommentContent()
    {
        switchTo().frame(findElement(commentContentIEditframe));
        WebElement editable = switchTo().activeElement();
        editable.clear();
        switchTo().defaultContent();
    }

    public void editComment(String comment)
    {
        switchTo().frame(findElement(commentContentIEditframe));
        WebElement editable = switchTo().activeElement();
        editable.sendKeys(comment);
        switchTo().defaultContent();
        clickElement(saveButton);
    }

    public boolean isTagDisplayedInTagsList(String tag)
    {
        for (WebElement tagList : findElements(tagsList))
        {
            if (tagList.getText()
                .contains(tag))
            {
                waitInSeconds(3);
                return true;
            }
        }
        return false;
    }

    public boolean isAddCommentButtonDisplayed()
    {
        return isElementDisplayed(addCommentButton);
    }

    public boolean isEditLinkDisplayed()
    {
        return isElementDisplayed(editAction);
    }

    public boolean isDeleteLinkDisplayed()
    {
        return isElementDisplayed(deleteAction);
    }
}
