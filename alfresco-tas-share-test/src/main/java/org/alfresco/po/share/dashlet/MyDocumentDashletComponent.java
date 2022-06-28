package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.model.FileModel;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class MyDocumentDashletComponent extends MyDocumentsDashlet
{
    private final By documentNameLink = By.cssSelector("h3.filename > a");
    private final By smallIconThumbnail = By.cssSelector("td[headers$='thumbnail '] .icon32");
    private final By commentLink = By.cssSelector(".comment");
    private final By likeAction = By.cssSelector("a[class*='like-action like']");
    private final By unlikeAction = By.cssSelector("a.like-action.enabled");
    private final By likesCount = By.cssSelector(".likes-count");
    private final By favoriteAction = By.cssSelector("a[class^='favourite-action']");
    private final By removeFromFavorite = By.cssSelector("a.favourite-action.enabled");
    private final By descriptionElement = By.cssSelector(".faded");
    private final By versionElement = By.cssSelector(".document-version");
    private final By thumbnail = By.cssSelector("td[headers$='thumbnail '] a");

    private final FileModel file;

    public MyDocumentDashletComponent(ThreadLocal<WebDriver> webDriver, FileModel file)
    {
        super(webDriver);
        this.file = file;
    }

    private WebElement getFileRow()
    {
        return getDocumentRow(file.getName());
    }

    public MyDocumentDashletComponent assertFileIsDisplayed()
    {
        assertTrue(isElementDisplayed(getFileRow()), String.format("File %s is displayed", file.getName()));
        return this;
    }

    public MyDocumentDashletComponent assertFileIsNotDisplayed()
    {
        assertFalse(isElementDisplayed(By.xpath(String.format(documentRow, file.getName()))));
        return this;
    }

    public MyDocumentDashletComponent assertSmallIconThumbnailIsDisplayed()
    {
        assertTrue(isElementDisplayed(getFileRow().findElement(smallIconThumbnail)),
                "Small icon thumbnail is displayed");
        return this;
    }

    public MyDocumentDashletComponent assertCommentLinkIsDisplayed()
    {
        waitUntilElementIsVisible(commentLink);
        assertTrue(isElementDisplayed(getFileRow().findElement(commentLink)), "Comment link is displayed");
        return this;
    }

    public MyDocumentDashletComponent assertLikeIsDisplayed()
    {
        assertTrue(isElementDisplayed(getFileRow().findElement(likeAction)), "Like is displayed");
        return this;
    }

    public MyDocumentDashletComponent assertUnlikeIsDisplayed()
    {
        assertTrue(isElementDisplayed(getFileRow().findElement(unlikeAction)), "Unlike is displayed");
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
        clickElement(likeBtn);
        waitUntilChildElementIsPresent(getFileRow(), unlikeAction);
        return this;
    }

    public MyDocumentDashletComponent assertAddToFavoriteIsDisplayed()
    {
        assertTrue(isElementDisplayed(getFileRow().findElement(favoriteAction)),
                "Favorite action is displayed");
        return this;
    }

    public MyDocumentDashletComponent assertRemoveFromFavoriteIsDisplayed()
    {
        assertTrue(isElementDisplayed(getFileRow().findElement(removeFromFavorite)),
                "Remove from favorite is displayed");
        return this;
    }

    public MyDocumentDashletComponent addToFavorite()
    {
        clickElement(getFileRow().findElement(favoriteAction));
        waitUntilChildElementIsPresent(getFileRow(), removeFromFavorite);
        return this;
    }

    public MyDocumentDashletComponent removeFromFavorite()
    {
        clickElement(getFileRow().findElement(removeFromFavorite));
        waitUntilChildElementIsPresent(getFileRow(), favoriteAction);
        return this;
    }

    public MyDocumentDashletComponent assertNoDescriptionIsDisplayed()
    {
        assertEquals(getElementText(getFileRow().findElement(descriptionElement)),
            language.translate("myDocumentsDashlet.noDescription"));
        return this;
    }

    public MyDocumentDashletComponent assertVersionIs(double version)
    {
        WebElement row = getFileRow();
        WebElement docName = row.findElement(documentNameLink);
        mouseOver(docName);
        waitUntilElementHasAttribute(row, "class", "highlighted");

        assertEquals(getElementText(row.findElement(versionElement)),
            String.valueOf(version), "Document version is correct");
        return this;
    }

    public MyDocumentDashletComponent assertThumbnailIsDisplayed()
    {
        log.info("Assert thumbnail is displayed");
        assertTrue(isElementDisplayed(getFileRow().findElement(thumbnail)), "Thumbnail is displayed");
        return this;
    }

    public DocumentDetailsPage selectDocument()
    {
        clickElement(getFileRow().findElement(documentNameLink));
        return new DocumentDetailsPage(webDriver);
    }

    public DocumentDetailsPage clickThumbnail()
    {
        clickElement(getFileRow().findElement(thumbnail));
        return new DocumentDetailsPage(webDriver);
    }

    public DocumentDetailsPage addComment()
    {
        clickElement(getFileRow().findElement(commentLink));
        return new DocumentDetailsPage(webDriver);
    }
}
