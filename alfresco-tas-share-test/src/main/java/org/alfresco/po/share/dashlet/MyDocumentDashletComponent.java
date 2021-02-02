package org.alfresco.po.share.dashlet;

import org.alfresco.common.WebElementInteraction;
import org.alfresco.po.share.BasePage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.model.FileModel;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MyDocumentDashletComponent
{
    private final Logger LOG = LoggerFactory.getLogger(BasePage.class);
    private final MyDocumentsDashlet myDocumentsDashlet;
    private final WebElementInteraction webElementInteraction;
    private final DocumentDetailsPage documentDetailsPage;
    private final FileModel file;

    private final By documentNameLink = By.cssSelector("h3.filename > a");
    private final By smallIconThumbnail = By.cssSelector("td[headers$='thumbnail '] .icon32");
    private final By commentLink = By.cssSelector(".comment");
    private final By likeAction = By.cssSelector("a[class='like-action like4']");
    private final By unlikeAction = By.cssSelector("a[class='like-action like4 enabled']");
    private final By likesCount = By.cssSelector(".likes-count");
    private final By favoriteAction = By.cssSelector("a[class^='favourite-action']");
    private final By removeFromFavorite = By.cssSelector("a[class='favourite-action favourite3 enabled']");
    private final By descriptionElement = By.cssSelector(".faded");
    private final By versionElement = By.cssSelector(".document-version");
    private final By thumbnail = By.cssSelector("td[headers$='thumbnail '] a");

    public MyDocumentDashletComponent(MyDocumentsDashlet myDocumentsDashlet,
                                      DocumentDetailsPage documentDetailsPage,
                                      WebElementInteraction webElementInteraction,
                                      FileModel file)
    {
        this.myDocumentsDashlet = myDocumentsDashlet;
        this.documentDetailsPage = documentDetailsPage;
        this.webElementInteraction = webElementInteraction;
        this.file = file;
        LOG.info(String.format("Using file: %s in My Documents dashlet", file.getName()));
    }

    public WebElement getFileRow()
    {
        return myDocumentsDashlet.getDocumentRow(file.getName());
    }

    public MyDocumentDashletComponent assertFileIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(getFileRow()), String.format("File %s is displayed", file.getName()));
        return this;
    }

    public MyDocumentDashletComponent assertFileIsNotDisplayed()
    {
        Assert.assertFalse(webElementInteraction.isElementDisplayed(
                By.xpath(String.format(myDocumentsDashlet.documentRow, file.getName()))));
        return this;
    }

    public MyDocumentDashletComponent assertSmallIconThumbnailIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(smallIconThumbnail)),
                "Small icon thumbnail is displayed");
        return this;
    }

    public MyDocumentDashletComponent assertCommentLinkIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(commentLink);
        assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(commentLink)), "Comment link is displayed");
        return this;
    }

    public MyDocumentDashletComponent assertLikeIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(likeAction)), "Like is displayed");
        return this;
    }

    public MyDocumentDashletComponent assertUnlikeIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(unlikeAction)), "Unlike is displayed");
        return this;
    }

    public MyDocumentDashletComponent assertNumberOfLikesIs(int nrOfLikes)
    {
        int likes = Integer.parseInt(getFileRow().findElement(likesCount).getText());
        assertEquals(likes, nrOfLikes, "Number of likes is correct");
        return this;
    }

    public MyDocumentDashletComponent like()
    {
        WebElement likeBtn;
        try
        {
            likeBtn = getFileRow().findElement(likeAction);
        }
        catch (NoSuchElementException e)
        {
            likeBtn = getFileRow().findElement(likeAction);
        }
        webElementInteraction.clickJS(likeBtn);
        webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), unlikeAction);
        return this;
    }

    public MyDocumentDashletComponent assertAddToFavoriteIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(favoriteAction)),
                "Favorite action is displayed");
        return this;
    }

    public MyDocumentDashletComponent assertRemoveFromFavoriteIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(removeFromFavorite)),
                "Remove from favorite is displayed");
        return this;
    }

    public MyDocumentDashletComponent addToFavorite()
    {
        webElementInteraction.clickJS(getFileRow().findElement(favoriteAction));
        webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), removeFromFavorite);
        return this;
    }

    public MyDocumentDashletComponent removeFromFavorite()
    {
        webElementInteraction.clickJS(getFileRow().findElement(removeFromFavorite));
        webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), favoriteAction);
        return this;
    }

    public MyDocumentDashletComponent assertNoDescriptionIsDisplayed()
    {
        assertEquals(getFileRow().findElement(descriptionElement).getText(),
                myDocumentsDashlet.language.translate("myDocumentsDashlet.noDescription"));
        return this;
    }

    public MyDocumentDashletComponent assertVersionIs(double version)
    {
        WebElement row = getFileRow();
        WebElement docName = row.findElement(documentNameLink);
        webElementInteraction.mouseOver(docName);
        webElementInteraction.waitUntilElementHasAttribute(row, "class", "highlighted");
        assertEquals(row.findElement(versionElement).getText(), String.valueOf(version),
                "Document version is correct");
        return this;
    }

    public MyDocumentDashletComponent assertThumbnailIsDisplayed()
    {
        LOG.info("Assert thumbnail is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(thumbnail)), "Thumbnail is displayed");
        return this;
    }

    public DocumentDetailsPage selectDocument()
    {
        getFileRow().findElement(documentNameLink).click();
        return documentDetailsPage;
    }

    public DocumentDetailsPage clickThumbnail()
    {
        getFileRow().findElement(thumbnail).click();
        return documentDetailsPage;
    }

    public DocumentDetailsPage addComment()
    {
        getFileRow().findElement(commentLink).click();
        return documentDetailsPage;
    }
}
