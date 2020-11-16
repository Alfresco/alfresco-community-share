package org.alfresco.po.share.site.link;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Link;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LinkDetailsViewPage extends SiteCommon<LinkDetailsViewPage>
{
    //@Autowired
    LinkPage linkPage;

    //@Autowired
    EditLinkPage editLinkPage;

    @RenderWebElement
    @FindBy (css = "[class*=onAddCommentClick] button")
    private WebElement addCommentButton;

    @FindBy (xpath = "//a[contains(text(), 'Links List')]")
    private WebElement linksListLink;

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

    @FindAll (@FindBy (css = ".comment-details"))
    private List<WebElement> commentDetailsList;

    @FindBy (css = "[id*=default-add-cancel-button]")
    private WebElement cancelSubmitCommentButton;

    private final By commentContentIframe = By.xpath("//iframe[contains(@title,'Rich Text Area')]");

    public LinkDetailsViewPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/links-view", getCurrentSiteName());
    }

    public LinkDetailsViewPage assertLinkTitleEquals(String expectedLinkTitle)
    {
        LOG.info("Assert link title equals: {}", expectedLinkTitle);
        assertEquals(linkTitle.getText(), expectedLinkTitle,
            String.format("Link title not equals %s ", expectedLinkTitle));

        return this;
    }

    public LinkDetailsViewPage assertLinkUrlEquals(String expectedLinkUrl)
    {
        LOG.info("Assert link url equals: {}", expectedLinkUrl);
        assertEquals(linkURL.getText(), expectedLinkUrl,
            String.format("Link url not equals %s ", expectedLinkUrl));
        return this;
    }

    public LinkDetailsViewPage assertLinkCreationDateContains(DateFormat dateFormat)
    {
        LOG.info("Assert link creation date contains: {}", dateFormat);
        assertTrue(creationDate.getText().contains(dateFormat.format(new Date())),
            String.format("Link creation date not contains %s", dateFormat));

        return this;
    }

    public LinkDetailsViewPage assertCreatedByLabelEqualsFullUserName(String firstName, String lastName)
    {
        LOG.info("Assert created by label equals full username: {}", firstName, lastName);
        assertEquals(createdBy.getText(), firstName.concat(" " + lastName),
                String.format("Full username not equals %s and %s ", firstName, lastName));

        return this;
    }

    public LinkDetailsViewPage assertLinkDescriptionEquals(String expectedLinkDescription)
    {
        LOG.info("Assert link description equals: {}", expectedLinkDescription);
        assertEquals(description.getText(), expectedLinkDescription,
            String.format("Link description not equals %s ", expectedLinkDescription));

        return this;
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
        getBrowser().findElement(By.xpath("//a[@href ='" + linkURL + "']")).click();
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
        return getBrowser().findElement(commentContentIframe).isDisplayed();
    }

    public void addComment(String comment)
    {
        getBrowser().switchTo().frame(getBrowser().findElement(commentContentIframe));
        WebElement editable = getBrowser().switchTo().activeElement();

        editable.sendKeys(comment);
        getBrowser().switchTo().defaultContent();
        submitCommentButton.click();
    }

    public void cancelAddComment(String comment)
    {
        getBrowser().switchTo().frame(getBrowser().findElement(commentContentIframe));
        WebElement editable = getBrowser().switchTo().activeElement();
        editable.sendKeys(comment);
        getBrowser().switchTo().defaultContent();
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
        return getBrowser().findFirstElementWithValue(commentDetailsList, commentTitle);
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
        getBrowser().mouseOver(getBrowser().findFirstElementWithValue(commentDetailsList, comment));
        selectCommentDetailsRow(comment).findElement(By.cssSelector("[class*=edit-comment]")).click();
        return getBrowser().findElement(commentContentIframe).isDisplayed();
    }

    public void clearCommentContent()
    {
        getBrowser().switchTo().frame(getBrowser().findElement(commentContentIframe));
        WebElement editable = getBrowser().switchTo().activeElement();
        editable.clear();
        getBrowser().switchTo().defaultContent();
    }

    public boolean clickDeleteCommentLink(String comment)
    {
        getBrowser().mouseOver(getBrowser().findFirstElementWithValue(commentDetailsList, comment));
        selectCommentDetailsRow(comment).findElement(By.cssSelector("[class*=delete-comment]")).click();
        return deleteLinkPrompt.isDisplayed();
    }
}
