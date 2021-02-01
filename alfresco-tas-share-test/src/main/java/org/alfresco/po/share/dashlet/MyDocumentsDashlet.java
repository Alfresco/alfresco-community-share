package org.alfresco.po.share.dashlet;

import static org.alfresco.common.Wait.WAIT_60;

import org.alfresco.common.WebElementInteraction;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.model.FileModel;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MyDocumentsDashlet extends Dashlet<MyDocumentsDashlet>
{
    private final By dashletContainer = By.cssSelector("div.dashlet.my-documents");
    private final By filterOptions = By.cssSelector("div[class*='my-documents'] div.bd ul li");
    private final By filterButton = By.cssSelector("div[class*='my-documents'] button[id$='default-filters-button']");
    private final By simpleViewButton = By.cssSelector("div[id$='default-simpleDetailed'] span:nth-of-type(1) button");
    private final By detailedViewButtonSpan = By.cssSelector("span[class*='detailed-view']");
    private final By detailedViewButton = By.cssSelector("span[class*='detailed-view'] button");
    private final By documentNameLink = By.cssSelector("h3.filename > a");
    private final By documentsArea = By.cssSelector("div[id$='default-documents'] .yui-dt-data");

    private final String documentRow = "//div[starts-with(@class,'dashlet my-documents')]//a[text()='%s']/../../../..";
    private final String buttonChecked = "yui-radio-button-checked";

    public MyDocumentsDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.waitUntilElementIsVisible(dashletContainer).findElement(dashletTitle).getText();
    }

    public WebElement getDocumentRow(String documentName)
    {
        By docLocator = By.xpath(String.format(documentRow, documentName));
        boolean found = webElementInteraction.isElementDisplayed(docLocator);
        int i = 0;
        while (i < WAIT_60.getValue() && !found)
        {
            i++;
            LOG.info("Wait for document {} to be displayed in My Documents dashlet", documentName);
            webElementInteraction.refresh();
            webElementInteraction.waitInSeconds(1);
            webElementInteraction.waitUntilElementIsVisible(dashletContainer);
            found = webElementInteraction.isElementDisplayed(docLocator);
        }
        return webElementInteraction.waitUntilElementIsVisible(docLocator);
    }

    private String getFilterValue(DocumentsFilter filter)
    {
        String filterValue = "";
        switch (filter)
        {
            case RECENTLY_MODIFIED:
                filterValue = language.translate("dashlet.filter.recentlyModified");
                break;
            case EDITING:
                filterValue = language.translate("dashlet.filter.editing");
                break;
            case MY_FAVORITES:
                filterValue = language.translate("dashlet.filter.favorites");
                break;
            default:
                break;
        }
        return filterValue;
    }

    public MyDocumentsDashlet assertSelectedFilterIs(DocumentsFilter filter)
    {
        String filterText = webElementInteraction.getElementText(filterButton);
        assertEquals(filterText.substring(0, filterText.length() - 2),
            getFilterValue(filter), "Selected filter is correct");
        return this;
    }

    public MyDocumentsDashlet filter(DocumentsFilter filter)
    {
        webElementInteraction.waitUntilElementClickable(filterButton).click();
        List<WebElement> options = webElementInteraction.waitUntilElementsAreVisible(filterOptions);
        webElementInteraction.selectOptionFromFilterOptionsList(getFilterValue(filter), options);
        return this;
    }

    public MyDocumentsDashlet selectDetailedView()
    {
        LOG.info("Select Detailed View");
        webElementInteraction.clickElement(detailedViewButton);
        webElementInteraction.waitUntilElementHasAttribute(detailedViewButtonSpan, "class", buttonChecked);

        return this;
    }

    public MyDocumentsDashlet selectSimpleView()
    {
        LOG.info("Select Simple View");
        webElementInteraction.clickElement(simpleViewButton);
        webElementInteraction.waitUntilElementIsVisible(documentsArea);
        return this;
    }

    public boolean isNumberOfDocumentsDisplayed(int noOfDocs)
    {
        webElementInteraction.waitUntilElementIsVisible(dashletContainer);
        boolean bool = webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElements(documentNameLink).size() == noOfDocs;
        int i = 0;
        while (!bool && i < 5)
        {
            webElementInteraction.refresh();
            bool = webElementInteraction.waitUntilElementIsVisible(dashletContainer)
                .findElements(documentNameLink).size() == noOfDocs;
            i++;
        }
        return bool;
    }

    public MyDocumentsDashlet assertNrOfDisplayedDocumentsIs(int nrOfDocs)
    {
        assertTrue(isNumberOfDocumentsDisplayed(nrOfDocs), "Nr of displayed documents is correct");
        return this;
    }

    public ManagerMyDocument usingDocument(FileModel file)
    {
        return new ManagerMyDocument(this, webElementInteraction, file);
    }

    public enum DocumentsFilter
    {
        RECENTLY_MODIFIED,
        EDITING,
        MY_FAVORITES
    }

    public class ManagerMyDocument
    {
        private final MyDocumentsDashlet myDocumentsDashlet;
        private final WebElementInteraction webElementInteraction;
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

        public ManagerMyDocument(MyDocumentsDashlet myDocumentsDashlet,
                                 WebElementInteraction webElementInteraction,
                                 FileModel file)
        {
            this.myDocumentsDashlet = myDocumentsDashlet;
            this.webElementInteraction = webElementInteraction;
            this.file = file;
            LOG.info(String.format("Using file: %s in My Documents dashlet", file.getName()));
        }

        public WebElement getFileRow()
        {
            return myDocumentsDashlet.getDocumentRow(file.getName());
        }

        public ManagerMyDocument assertFileIsDisplayed()
        {
            assertTrue(webElementInteraction.isElementDisplayed(getFileRow()), String.format("File %s is displayed", file.getName()));
            return this;
        }

        public ManagerMyDocument assertFileIsNotDisplayed()
        {
            Assert.assertFalse(webElementInteraction.isElementDisplayed(
                By.xpath(String.format(myDocumentsDashlet.documentRow, file.getName()))));
            return this;
        }

        public ManagerMyDocument assertSmallIconThumbnailIsDisplayed()
        {
            assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(smallIconThumbnail)),
                "Small icon thumbnail is displayed");
            return this;
        }

        public ManagerMyDocument assertCommentLinkIsDisplayed()
        {
            webElementInteraction.waitUntilElementIsVisible(commentLink);
            assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(commentLink)), "Comment link is displayed");
            return this;
        }

        public ManagerMyDocument assertLikeIsDisplayed()
        {
            assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(likeAction)), "Like is displayed");
            return this;
        }

        public ManagerMyDocument assertUnlikeIsDisplayed()
        {
            assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(unlikeAction)), "Unlike is displayed");
            return this;
        }

        public ManagerMyDocument assertNumberOfLikesIs(int nrOfLikes)
        {
            int likes = Integer.parseInt(getFileRow().findElement(likesCount).getText());
            assertEquals(likes, nrOfLikes, "Number of likes is correct");
            return this;
        }

        public ManagerMyDocument like()
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

        public ManagerMyDocument assertAddToFavoriteIsDisplayed()
        {
            assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(favoriteAction)),
                "Favorite action is displayed");
            return this;
        }

        public ManagerMyDocument assertRemoveFromFavoriteIsDisplayed()
        {
            assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(removeFromFavorite)),
                "Remove from favorite is displayed");
            return this;
        }

        public ManagerMyDocument addToFavorite()
        {
            webElementInteraction.clickJS(getFileRow().findElement(favoriteAction));
            webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), removeFromFavorite);
            return this;
        }

        public ManagerMyDocument removeFromFavorite()
        {
            webElementInteraction.clickJS(getFileRow().findElement(removeFromFavorite));
            webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), favoriteAction);
            return this;
        }

        public ManagerMyDocument assertNoDescriptionIsDisplayed()
        {
            assertEquals(getFileRow().findElement(descriptionElement).getText(),
                myDocumentsDashlet.language.translate("myDocumentsDashlet.noDescription"));
            return this;
        }

        public ManagerMyDocument assertVersionIs(double version)
        {
            WebElement row = getFileRow();
            WebElement docName = row.findElement(documentNameLink);
            webElementInteraction.mouseOver(docName);
            webElementInteraction.waitUntilElementHasAttribute(row, "class", "highlighted");
            assertEquals(row.findElement(versionElement).getText(), String.valueOf(version),
                "Document version is correct");
            return this;
        }

        public ManagerMyDocument assertThumbnailIsDisplayed()
        {
            LOG.info("Assert thumbnail is displayed");
            assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(thumbnail)), "Thumbnail is displayed");
            return this;
        }

        public DocumentDetailsPage selectDocument()
        {
            getFileRow().findElement(documentNameLink).click();
            return new DocumentDetailsPage(webDriver);
        }

        public DocumentDetailsPage clickThumbnail()
        {
            getFileRow().findElement(thumbnail).click();
            return new DocumentDetailsPage(webDriver);
        }

        public DocumentDetailsPage addComment()
        {
            getFileRow().findElement(commentLink).click();
            return new DocumentDetailsPage(webDriver);
        }
    }
}