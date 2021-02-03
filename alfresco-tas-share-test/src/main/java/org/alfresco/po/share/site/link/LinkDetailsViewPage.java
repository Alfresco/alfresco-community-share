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
    private final By addCommentButton = By.cssSelector("[class*=onAddCommentClick] button");
    private final By linksListLink = By.xpath("//a[contains(text(), 'Links List')]");
    private final By linkTitle = By.cssSelector(".nodeTitle>a");
    private final By linkURL = By.cssSelector(".nodeURL>a");
    private final By creationDate = By.xpath("//*[@class='nodeAttrLabel' and normalize-space(text())='Created on:']/following-sibling::*[1]");
    private final By createdBy = By.xpath("//*[@class='nodeAttrLabel' and normalize-space(text())='Created by:']/following-sibling::*[1]/a");
    private final By description = By.xpath("//*[@class='nodeAttrLabel' and normalize-space(text())='Description:']/following-sibling::*[1]");
    private final By tagsList = By.className("tag-link");
    private final By editLink = By.cssSelector(".onEditLink>a");
    private final By deleteLink = By.cssSelector(".onDeleteLink>a");
    private final By deleteLinkPrompt = By.cssSelector("[id=prompt]");
    private final By submitCommentButton = By.cssSelector("[id*=default-add-submit-button]");
    private final By commentsList = By.cssSelector(".comment-content");
    private final By commentDetailsList = By.cssSelector(".comment-details");
    private final By cancelSubmitCommentButton = By.cssSelector("[id*=default-add-cancel-button]");
    private final By commentContentIframe = By.xpath("//iframe[contains(@title,'Rich Text Area')]");

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
        assertEquals(webElementInteraction.getElementText(linkTitle), expectedLinkTitle,
            String.format("Link title not equals %s ", expectedLinkTitle));

        return this;
    }

    public LinkDetailsViewPage assertLinkUrlEquals(String expectedLinkUrl)
    {
        log.info("Assert link url equals: {}", expectedLinkUrl);
        assertEquals(webElementInteraction.getElementText(linkURL), expectedLinkUrl,
            String.format("Link url not equals %s ", expectedLinkUrl));
        return this;
    }

    public LinkDetailsViewPage assertLinkCreationDateContains(DateFormat dateFormat)
    {
        log.info("Assert link creation date contains: {}", dateFormat);
        assertTrue(webElementInteraction.getElementText(creationDate).contains(dateFormat.format(new Date())),
            String.format("Link creation date not contains %s", dateFormat));

        return this;
    }

    public LinkDetailsViewPage assertCreatedByLabelEqualsFullUserName(String firstName, String lastName)
    {
        log.info("Assert created by label equals full username: {}", firstName, lastName);
        assertEquals(webElementInteraction.getElementText(createdBy), firstName.concat(" " + lastName),
                String.format("Full username not equals %s and %s ", firstName, lastName));

        return this;
    }

    public LinkDetailsViewPage assertLinkDescriptionEquals(String expectedLinkDescription)
    {
        log.info("Assert link description equals: {}", expectedLinkDescription);
        assertEquals(webElementInteraction.getElementText(description), expectedLinkDescription,
            String.format("Link description not equals %s ", expectedLinkDescription));

        return this;
    }

    public void clickOnLinksListLink()
    {
        webElementInteraction.clickElement(linksListLink);
    }

    public List<String> getTagsList()
    {

        List<String> linkTags = new ArrayList<>();
        for (WebElement linkTag : webElementInteraction.waitUntilElementsAreVisible(tagsList))
        {
            linkTags.add(linkTag.getText());
        }
        return linkTags;
    }

    public void clickOnLinkURL(String linkURL)
    {
        webElementInteraction.findElement(By.xpath("//a[@href ='" + linkURL + "']")).click();
    }

    public EditLinkPage clickOnEditLink()
    {
        webElementInteraction.clickElement(editLink);
        return new EditLinkPage(webDriver);
    }

    public boolean clickOnDeleteLink()
    {
        webElementInteraction.clickElement(deleteLink);
        return webElementInteraction.isElementDisplayed(deleteLinkPrompt);
    }

    public boolean clickOnAddCommentButton()
    {
        webElementInteraction.clickElement(addCommentButton);
        return webElementInteraction.isElementDisplayed(commentContentIframe);
    }

    public void addComment(String comment)
    {
        webElementInteraction.switchTo().frame(webElementInteraction.findElement(commentContentIframe));
        WebElement editable = webElementInteraction.switchTo().activeElement();

        editable.sendKeys(comment);
        webElementInteraction.switchTo().defaultContent();
        webElementInteraction.clickElement(submitCommentButton);
    }

    public void cancelAddComment(String comment)
    {
        webElementInteraction.switchTo().frame(webElementInteraction.findElement(commentContentIframe));
        WebElement editable = webElementInteraction.switchTo().activeElement();
        editable.sendKeys(comment);
        webElementInteraction.switchTo().defaultContent();
        webElementInteraction.clickElement(cancelSubmitCommentButton);
    }

    public List<String> getCommentsList()
    {
        List<String> comments = new ArrayList<>();
        for (WebElement comment : webElementInteraction.waitUntilElementsAreVisible(commentsList))
        {
            comments.add(comment.getText());
        }
        return comments;
    }

    public WebElement selectCommentDetailsRow(String commentTitle)
    {
        return webElementInteraction.findFirstElementWithValue(commentDetailsList, commentTitle);
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
        webElementInteraction.mouseOver(
            webElementInteraction.findFirstElementWithValue(commentDetailsList, comment));
        selectCommentDetailsRow(comment).findElement(By.cssSelector("[class*=edit-comment]")).click();
        return webElementInteraction.findElement(commentContentIframe).isDisplayed();
    }

    public void clearCommentContent()
    {
        webElementInteraction.switchTo().frame(webElementInteraction.findElement(commentContentIframe));
        WebElement editable = webElementInteraction.switchTo().activeElement();
        editable.clear();
        webElementInteraction.switchTo().defaultContent();
    }

    public boolean clickDeleteCommentLink(String comment)
    {
        webElementInteraction.mouseOver(
            webElementInteraction.findFirstElementWithValue(commentDetailsList, comment));
        selectCommentDetailsRow(comment).findElement(By.cssSelector("[class*=delete-comment]")).click();
        return webElementInteraction.isElementDisplayed(deleteLinkPrompt);
    }
}
