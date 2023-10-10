package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.SiteModel;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SiteContentDashletComponent extends SiteContentDashlet
{
    private final By siteLocator = By.cssSelector(".detail span a");
    private final By thumbnail = By.cssSelector("td[headers$='thumbnail '] a");
    private final By fileNameLocator = By.cssSelector(".filename>a");
    private final By description = By.cssSelector(".detail:nth-of-type(2) > span");
    private final By documentVersion = By.cssSelector(".document-version");
    private final By fileSize = By.cssSelector(".detail:nth-of-type(1)>span:nth-of-type(2)");
    private final By likeAction = By.cssSelector("a[class*='like-action like']");
    private final By unlikeAction = By.cssSelector("a.like-action.enabled");
    private final By likesCount = By.cssSelector(".likes-count");
    private final By favoriteAction = By.cssSelector("a[class^='favourite-action']");
    private final By removeFromFavorite = By.cssSelector("a.favourite-action.enabled");
    private final By commentLink = By.cssSelector("a.comment");

    private final FileModel file;

    public SiteContentDashletComponent(ThreadLocal<WebDriver> webDriver, FileModel file)
    {
        super(webDriver);
        this.file = file;
    }

    public WebElement getFileRow()
    {
        return getDocumentRowWithRetry(file.getName());
    }

    public SiteContentDashletComponent assertFileIsDisplayed()
    {
        assertTrue(isElementDisplayed(getFileRow()), String.format("File %s is displayed", file.getName()));
        return this;
    }

    public SiteContentDashletComponent assertSiteEqualsTo(SiteModel site)
    {
        log.info("Assert site equals to {}", site.getTitle());
        assertEquals(getElementText(getFileRow().findElement(siteLocator)),
            site.getTitle(), String.format("Site %s is not displayed", site.getTitle()));
        return this;
    }

    public SiteContentDashletComponent assertThumbnailIsDisplayed()
    {
        log.info("Assert thumbnail is displayed");
        assertTrue(isElementDisplayed(getFileRow().findElement(thumbnail)),
                "Thumbnail is not displayed");
        return this;
    }

    public DocumentDetailsPage clickFileName()
    {
        log.info("Click file name");
        clickElement(getFileRow().findElement(fileNameLocator));

        return new DocumentDetailsPage(webDriver);
    }

    public SiteContentDashletComponent assertDescriptionEqualsTo(String expectedDescription)
    {
        log.info("Assert expected description equals to {}", expectedDescription);
        assertEquals(getElementText(getFileRow().findElement(description)),
            expectedDescription, "File description is not correct");
        return this;
    }

    public SiteContentDashletComponent assertFileVersionEqualsTo(double expectedVersion)
    {
        log.info("Assert file version equals to {}", expectedVersion);
        mouseOver(getFileRow().findElement(fileNameLocator));
        waitUntilChildElementIsPresent(getFileRow(), documentVersion);

        assertEquals(Double.valueOf(
            getElementText(getFileRow().findElement(documentVersion))),
            expectedVersion, "File version is not correct");
        return this;
    }

    public SiteContentDashletComponent assertFileSizeEqualsTo(String expectedFileSize)
    {
        log.info("Assert file size equals to {}", expectedFileSize);
        assertEquals(getElementText(getFileRow().findElement(fileSize)), expectedFileSize,
                "File size is not correct");
        return this;
    }

    public SiteContentDashletComponent assertLikeIsDisplayed()
    {
        log.info("Assert like is displayed");
        assertTrue(isElementDisplayed(getFileRow().findElement(likeAction)), "Like is not displayed");
        return this;
    }

    public SiteContentDashletComponent assertUnlikeIsDisplayed()
    {
        log.info("Assert unlike is displayed");
        assertTrue(isElementDisplayed(getFileRow().findElement(unlikeAction)), "Unlike is not displayed");
        return this;
    }

    public SiteContentDashletComponent assertNumberOfLikesEqualsTo(int nrOfLikes)
    {
        log.info("Assert number of likes equals to {}", nrOfLikes);
        assertEquals(Integer.parseInt(getElementText(getFileRow().findElement(likesCount))), nrOfLikes, "Number of likes is correct");
        return this;
    }

    public SiteContentDashletComponent clickLike()
    {
        log.info("Click like");
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

    public SiteContentDashletComponent clickUnlike()
    {
        log.info("Click unlike");
        WebElement unlikeActionElement = getFileRow().findElement(unlikeAction);
        clickElement(unlikeActionElement);
        waitUntilElementDisappears(unlikeActionElement);
        waitUntilChildElementIsPresent(getFileRow(), likeAction);

        return this;
    }

    public SiteContentDashletComponent assertAddToFavoriteIsDisplayed()
    {
        log.info("Assert add to favorite is displayed");
        assertTrue(isElementDisplayed(getFileRow().findElement(favoriteAction)),
                "Favorite action is not displayed");
        return this;
    }

    public SiteContentDashletComponent assertRemoveFromFavoriteIsDisplayed()
    {
        log.info("Assert remove from favorite is displayed");
        assertTrue(isElementDisplayed(getFileRow().findElement(removeFromFavorite)),
                "Remove from favorite is not displayed");
        return this;
    }

    public SiteContentDashletComponent addToFavorite()
    {
        log.info("Add file to favorite");
        WebElement favorite = getFileRow().findElement(favoriteAction);
        mouseOver(favorite);
        clickElement(favorite);
        waitUntilChildElementIsPresent(getFileRow(), removeFromFavorite);
        return this;
    }

    public SiteContentDashletComponent removeFromFavorite()
    {
        log.info("Remove file from favorite");
        clickElement(getFileRow().findElement(removeFromFavorite));
        waitUntilChildElementIsPresent(getFileRow(), favoriteAction);
        return this;
    }

    public DocumentDetailsPage clickCommentLink()
    {
        clickElement(getFileRow().findElement(commentLink));
        return new DocumentDetailsPage(webDriver);
    }
}
