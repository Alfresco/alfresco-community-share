package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import java.util.List;

@PageObject
public class MyDocumentsDashlet extends Dashlet<MyDocumentsDashlet>
{
    @RenderWebElement
    @FindBy (css = "div.dashlet.my-documents")
    protected WebElement dashletContainer;

    @FindAll (@FindBy (css = "div[id$='default-documents'] tr[class*='yui-dt-rec']"))
    protected List<WebElement> documentRowList;

    @FindAll (@FindBy (css = "div[class*='my-documents'] div.bd ul li"))
    protected List<WebElement> filterOptions;

    @FindBy (css = "div[class*='my-documents'] button[id$='default-filters-button']")
    protected WebElement filterButton;

    @FindBy (css = "div[class*='my-documents'] div[class*='empty']")
    protected WebElement defaultDocumentsText;

    @FindBy (css = "div[id$='default-simpleDetailed'] span:nth-of-type(1) button")
    private WebElement simpleViewButton;

    @FindBy (css = "div[id$='default-simpleDetailed'] span:nth-of-type(2) button")
    private WebElement detailedViewButton;

    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    protected String documentRow = "//div[starts-with(@class,'dashlet my-documents')]//a[text()='%s']/../../../..";
    private By documentNameLink = By.cssSelector("h3.filename > a");

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    @Override
    public String getRelativePath()
    {
        return super.getRelativePath();
    }

    public WebElement getDocumentRow(String documentName)
    {
        return browser.waitWithRetryAndReturnWebElement
            (By.xpath(String.format(documentRow, documentName)), 1, 20);
    }

    private String getFilterValue(DocumentsFilter filter)
    {
        String filterValue = "";
        switch (filter)
        {
            case RECENTLY_MODIFIED:
                filterValue = language.translate("myDocumentsDashlet.filter.recentlyModified");
                break;
            case EDITING:
                filterValue = language.translate("myDocumentsDashlet.filter.editing");
                break;
            case MY_FAVORITES:
                filterValue = language.translate("myDocumentsDashlet.filter.favorites");
                break;
            default:
                break;
        }
        return filterValue;
    }

    public MyDocumentsDashlet assertSelectedFilterIs(DocumentsFilter filter)
    {
        Assert.assertEquals(filterButton.getText().substring(0, filterButton.getText().length() - 2),
            getFilterValue(filter), "Selected filter is correct");
        return this;
    }

    public MyDocumentsDashlet filter(DocumentsFilter filter)
    {
        filterButton.click();
        browser.selectOptionFromFilterOptionsList(getFilterValue(filter), filterOptions);
        return this;
    }

    public MyDocumentsDashlet selectDetailedView()
    {
        detailedViewButton.click();
        return this;
    }

    public MyDocumentsDashlet selectSimpleView()
    {
        simpleViewButton.click();
        return this;
    }

    /**
     * Check that expected number of documents are listed
     *
     * @param noOfDocs
     * @return
     */
    public boolean isNumberOfDocumentsDisplayed(int noOfDocs)
    {
        boolean bool = dashletContainer.findElements(By.cssSelector("h3.filename > a")).size() == noOfDocs;
        int i = 0;
        while (!bool && i < 5)
        {
            browser.refresh();
            renderedPage();
            bool = dashletContainer.findElements(By.cssSelector("h3.filename > a")).size() == noOfDocs;
            i++;
        }
        return bool;
    }

    public MyDocumentsDashlet assertNrOfDisplayedDocumentsIs(int nrOfDocs)
    {
        Assert.assertTrue(isNumberOfDocumentsDisplayed(nrOfDocs), "Nr of displayed documents is correct");
        return this;
    }

    public ManagerMyDocument usingDocument(FileModel file)
    {
        return new ManagerMyDocument(this, documentDetailsPage, file);
    }

    public enum DocumentsFilter
    {
        RECENTLY_MODIFIED,
        EDITING,
        MY_FAVORITES
    }

    public class ManagerMyDocument
    {
        private MyDocumentsDashlet myDocumentsDashlet;
        private DocumentDetailsPage documentDetailsPage;
        private FileModel file;

        private By documentNameLink = By.cssSelector("h3.filename > a");
        private By smallIconThumbnail = By.cssSelector("td[headers$='thumbnail '] .icon32");
        private By commentLink = By.cssSelector(".comment");
        private By likeAction = By.cssSelector("a[class='like-action like4']");
        private By unlikeAction = By.cssSelector("a[class='like-action like4 enabled']");
        private By likesCount = By.cssSelector(".likes-count");
        private By favoriteAction = By.cssSelector("a[class^='favourite-action']");
        private By removeFromFavorite = By.cssSelector("a[class='favourite-action favourite3 enabled']");
        private By descriptionElement = By.cssSelector(".faded");
        private By versionElement = By.cssSelector(".document-version");
        private By thumbnail = By.cssSelector("td[headers$='thumbnail '] a");

        public ManagerMyDocument(MyDocumentsDashlet myDocumentsDashlet, DocumentDetailsPage documentDetailsPage, FileModel file)
        {
            this.myDocumentsDashlet = myDocumentsDashlet;
            this.documentDetailsPage = documentDetailsPage;
            this.file = file;
            LOG.info(String.format("Using file: %s in My Documents dashlet", file.getName()));
        }

        private WebBrowser getBrowser()
        {
            return myDocumentsDashlet.getBrowser();
        }

        public WebElement getFileRow()
        {
            return myDocumentsDashlet.getDocumentRow(file.getName());
        }

        public ManagerMyDocument assertFileIsDisplayed()
        {
            Assert.assertTrue(getBrowser().isElementDisplayed(getFileRow()), String.format("File %s is displayed", file.getName()));
            return this;
        }

        public ManagerMyDocument assertFileIsNotDisplayed()
        {
            Assert.assertFalse(getBrowser().isElementDisplayed(
                By.xpath(String.format(myDocumentsDashlet.documentRow, file.getName()))));
            return this;
        }

        public ManagerMyDocument assertSmallIconThumbnailIsDisplayed()
        {
            Assert.assertTrue(getBrowser().isElementDisplayed(getFileRow().findElement(smallIconThumbnail)),
                "Small icon thumbnail is displayed");
            return this;
        }

        public ManagerMyDocument assertCommentLinkIsDisplayed()
        {
            Assert.assertTrue(getBrowser().isElementDisplayed(getFileRow().findElement(commentLink)), "Comment link is displayed");
            return this;
        }

        public ManagerMyDocument assertLikeIsDisplayed()
        {
            Assert.assertTrue(getBrowser().isElementDisplayed(getFileRow().findElement(likeAction)), "Like is displayed");
            return this;
        }

        public ManagerMyDocument assertUnlikeIsDisplayed()
        {
            Assert.assertTrue(getBrowser().isElementDisplayed(getFileRow().findElement(unlikeAction)), "Unlike is displayed");
            return this;
        }

        public ManagerMyDocument assertNumberOfLikesIs(int nrOfLikes)
        {
            int likes = Integer.parseInt(getFileRow().findElement(likesCount).getText());
            Assert.assertEquals(likes, nrOfLikes, "Number of likes is correct");
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
            likeBtn.click();
            getBrowser().waitUntilElementVisible(getFileRow().findElement(unlikeAction));
            return this;
        }

        public ManagerMyDocument assertAddToFavoriteIsDisplayed()
        {
            Assert.assertTrue(browser.isElementDisplayed(getFileRow().findElement(favoriteAction)),
                "Favorite action is displayed");
            return this;
        }

        public ManagerMyDocument assertRemoveFromFavoriteIsDisplayed()
        {
            Assert.assertTrue(browser.isElementDisplayed(getFileRow().findElement(removeFromFavorite)),
                "Remove from favorite is displayed");
            return this;
        }

        public ManagerMyDocument addToFavorite()
        {
            getFileRow().findElement(favoriteAction).click();
            getBrowser().waitUntilElementVisible(getFileRow().findElement(removeFromFavorite));
            return this;
        }

        public ManagerMyDocument removeFromFavorite()
        {
            getFileRow().findElement(removeFromFavorite).click();
            getBrowser().waitUntilElementVisible(getFileRow().findElement(favoriteAction));
            return this;
        }

        public ManagerMyDocument assertNoDescriptionIsDisplayed()
        {
            Assert.assertEquals(getFileRow().findElement(descriptionElement).getText(),
                myDocumentsDashlet.language.translate("myDocumentsDashlet.noDescription"));
            return this;
        }

        public ManagerMyDocument assertVersionIs(double version)
        {
            WebElement row = getFileRow();
            WebElement docName = row.findElement(documentNameLink);
            getBrowser().mouseOver(docName);
            getBrowser().waitUntilElementHasAttribute(row, "class", "highlighted");
            Assert.assertEquals(row.findElement(versionElement).getText(), String.valueOf(version),
                "Document version is correct");
            return this;
        }

        public ManagerMyDocument assertThumbnailIsDisplayed()
        {
            Assert.assertTrue(browser.isElementDisplayed(getFileRow().findElement(thumbnail)), "Thumbnail is displayed");
            return this;
        }

        public DocumentDetailsPage selectDocument()
        {
            getFileRow().findElement(documentNameLink).click();
            return (DocumentDetailsPage) documentDetailsPage.renderedPage();
        }

        public DocumentDetailsPage clickThumbnail()
        {
            getFileRow().findElement(thumbnail).click();
            return (DocumentDetailsPage) documentDetailsPage.renderedPage();
        }

        public DocumentDetailsPage addComment()
        {
            getFileRow().findElement(commentLink).click();
            return (DocumentDetailsPage) documentDetailsPage.renderedPage();
        }
    }
}