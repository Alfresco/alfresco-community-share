package org.alfresco.po.share.dashlet;

import org.alfresco.common.WebElementInteraction;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.SiteModel;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.testng.Assert.*;
import static org.testng.Assert.assertTrue;

public class SiteContentDashletComponent
{
    private final Logger LOG = LoggerFactory.getLogger(SiteContentDashletComponent.class);
    private SiteContentDashlet siteContentDashlet;
    private WebElementInteraction webElementInteraction;
    private DocumentDetailsPage documentDetailsPage;
    private FileModel file;

    private By siteLocator = By.cssSelector(".detail span a");
    private By thumbnail = By.cssSelector("td[headers$='thumbnail '] a");
    private By fileNameLocator = By.cssSelector(".filename>a");
    private By description = By.cssSelector(".detail:nth-of-type(2) > span");
    private By documentVersion = By.cssSelector(".document-version");
    private By fileSize = By.cssSelector(".detail:nth-of-type(1)>span:nth-of-type(2)");
    private By likeAction = By.cssSelector("a[class='like-action like1']");
    private By unlikeAction = By.cssSelector("a[class='like-action like1 enabled']");
    private By likesCount = By.cssSelector(".likes-count");
    private By favoriteAction = By.cssSelector("a[class^='favourite-action']");
    private By removeFromFavorite = By.cssSelector("a[class='favourite-action favourite0 enabled']");
    private By commentLink = By.cssSelector("a.comment");

    public SiteContentDashletComponent(SiteContentDashlet siteContentDashlet,
                                       WebElementInteraction webElementInteraction,
                                       DocumentDetailsPage documentDetailsPage,
                                       FileModel file)
    {
        this.siteContentDashlet = siteContentDashlet;
        this.webElementInteraction = webElementInteraction;
        this.documentDetailsPage = documentDetailsPage;
        this.file = file;

        LOG.info(String.format("Using file: %s in Site Content dashlet", file.getName()));
    }

    public WebElement getFileRow()
    {
        return siteContentDashlet.getDocumentRow(file.getName());
    }

    public SiteContentDashletComponent assertFileIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(getFileRow()), String.format("File %s is displayed", file.getName()));
        return this;
    }

    public SiteContentDashletComponent assertSiteEqualsTo(SiteModel site)
    {
        LOG.info("Assert site equals to {}", site.getTitle());
        assertEquals(getFileRow().findElement(siteLocator).getText(), site.getTitle(),
                String.format("Site %s is not displayed", site.getTitle()));
        return this;
    }

    public SiteContentDashletComponent assertThumbnailIsDisplayed()
    {
        LOG.info("Assert thumbnail is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(thumbnail)),
                "Thumbnail is not displayed");
        return this;
    }

    public DocumentDetailsPage clickFileName()
    {
        LOG.info("Click file name");
        getFileRow().findElement(fileNameLocator).click();

        return documentDetailsPage;
    }

    public SiteContentDashletComponent assertDescriptionEqualsTo(String expectedDescription)
    {
        LOG.info("Assert expected description equals to {}", expectedDescription);
        assertEquals(getFileRow().findElement(description).getText(), expectedDescription,
                "File description is not correct");
        return this;
    }

    public SiteContentDashletComponent assertFileVersionEqualsTo(double expectedVersion)
    {
        LOG.info("Assert file version equals to {}", expectedVersion);
        webElementInteraction.mouseOver(getFileRow().findElement(fileNameLocator));
        webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), documentVersion);
        assertEquals(Double.valueOf(getFileRow().findElement(documentVersion).getText()), expectedVersion,
                "File version is not correct");
        return this;
    }

    public SiteContentDashletComponent assertFileSizeEqualsTo(String expectedFileSize)
    {
        LOG.info("Assert file size equals to {}", expectedFileSize);
        assertEquals(getFileRow().findElement(fileSize).getText(), expectedFileSize,
                "File size is not correct");
        return this;
    }

    public SiteContentDashletComponent assertLikeIsDisplayed()
    {
        LOG.info("Assert like is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(likeAction)), "Like is not displayed");
        return this;
    }

    public SiteContentDashletComponent assertUnlikeIsDisplayed()
    {
        LOG.info("Assert unlike is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(unlikeAction)), "Unlike is not displayed");
        return this;
    }

    public SiteContentDashletComponent assertNumberOfLikesEqualsTo(int nrOfLikes)
    {
        LOG.info("Assert number of likes equals to {}", nrOfLikes);
        int likes = Integer.parseInt(getFileRow().findElement(likesCount).getText());
        assertEquals(likes, nrOfLikes, "Number of likes is correct");
        return this;
    }

    public SiteContentDashletComponent clickLike()
    {
        LOG.info("Click like");
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

    public SiteContentDashletComponent clickUnlike()
    {
        LOG.info("Click unlike");
        webElementInteraction.clickJS(getFileRow().findElement(unlikeAction));
        webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), likeAction);
        return this;
    }

    public SiteContentDashletComponent assertAddToFavoriteIsDisplayed()
    {
        LOG.info("Assert add to favorite is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(favoriteAction)),
                "Favorite action is not displayed");
        return this;
    }

    public SiteContentDashletComponent assertRemoveFromFavoriteIsDisplayed()
    {
        LOG.info("Assert remove from favorite is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(removeFromFavorite)),
                "Remove from favorite is not displayed");
        return this;
    }

    public SiteContentDashletComponent addToFavorite()
    {
        LOG.info("Add file to favorite");
        webElementInteraction.clickJS(getFileRow().findElement(favoriteAction));
        webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), removeFromFavorite);
        return this;
    }

    public SiteContentDashletComponent removeFromFavorite()
    {
        LOG.info("Remove file from favorite");
        webElementInteraction.clickJS(getFileRow().findElement(removeFromFavorite));
        webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), favoriteAction);
        return this;
    }

    public DocumentDetailsPage clickCommentLink()
    {
        getFileRow().findElement(commentLink).click();
        return documentDetailsPage;
    }
}
