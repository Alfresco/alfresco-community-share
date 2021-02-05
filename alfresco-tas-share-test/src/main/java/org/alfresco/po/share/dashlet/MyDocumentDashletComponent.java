package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.WebElementInteraction;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.model.FileModel;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

@Slf4j
public class MyDocumentDashletComponent
{
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
        log.info("Using file: {} in My Documents dashlet", file.getName());
    }

    private WebElement getFileRow()
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
        assertFalse(webElementInteraction.isElementDisplayed(
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
        webElementInteraction.clickElement(likeBtn);
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
        webElementInteraction.clickElement(getFileRow().findElement(favoriteAction));
        webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), removeFromFavorite);
        return this;
    }

    public MyDocumentDashletComponent removeFromFavorite()
    {
        webElementInteraction.clickElement(getFileRow().findElement(removeFromFavorite));
        webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), favoriteAction);
        return this;
    }

    public MyDocumentDashletComponent assertNoDescriptionIsDisplayed()
    {
        assertEquals(
            webElementInteraction.getElementText(getFileRow().findElement(descriptionElement)),
            myDocumentsDashlet.language.translate("myDocumentsDashlet.noDescription"));
        return this;
    }

    public MyDocumentDashletComponent assertVersionIs(double version)
    {
        WebElement row = getFileRow();
        WebElement docName = row.findElement(documentNameLink);
        webElementInteraction.mouseOver(docName);
        webElementInteraction.waitUntilElementHasAttribute(row, "class", "highlighted");

        assertEquals(webElementInteraction.getElementText(row.findElement(versionElement)),
            String.valueOf(version), "Document version is correct");
        return this;
    }

    public MyDocumentDashletComponent assertThumbnailIsDisplayed()
    {
        log.info("Assert thumbnail is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(thumbnail)), "Thumbnail is displayed");
        return this;
    }

    public DocumentDetailsPage selectDocument()
    {
        webElementInteraction.clickElement(getFileRow().findElement(documentNameLink));
        return documentDetailsPage;
    }

    public DocumentDetailsPage clickThumbnail()
    {
        webElementInteraction.clickElement(getFileRow().findElement(thumbnail));
        return documentDetailsPage;
    }

    public DocumentDetailsPage addComment()
    {
        webElementInteraction.clickElement(getFileRow().findElement(commentLink));
        return documentDetailsPage;
    }
}
