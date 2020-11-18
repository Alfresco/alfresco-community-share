package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import static org.testng.Assert.*;

@PageObject
public class SiteContentDashlet extends Dashlet<SiteContentDashlet>
{
    @FindAll (@FindBy (css = ".yuimenuitemlabel.yuimenuitemlabel"))
    protected List<WebElement> filters;

    @RenderWebElement
    @FindBy (css = "div.dashlet.docsummary")
    private WebElement dashletContainer;

    @FindBy (css = "div.dashlet.docsummary span[class$='first-child'] [title='Simple View']")
    private WebElement simpleViewIcon;

    @FindBy (css = " div.dashlet.docsummary span[class$='first-child'] [title='Detailed View']")
    private WebElement detailedViewIcon;

    @FindBy (css = "[id$='default-filters']")
    private WebElement defaultFilterButton;

    private final By emptyMessage = By.cssSelector("div[id$='default-documents'] .empty");
    protected String documentRow = "//div[starts-with(@class, 'dashlet docsummary')]//a[text()='%s']/../../../..";

    //@Autowired
    private DocumentDetailsPage documentDetailsPage;

    @Override
    protected String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    protected WebElement getDocumentRow(String documentName)
    {
        return browser.waitWithRetryAndReturnWebElement
            (By.xpath(String.format(documentRow, documentName)), WAIT_1, RETRY_TIMES);
    }

    public SiteContentDashlet assertEmptySiteContentMessageIsCorrect()
    {
        LOG.info("Assert empty site content message is correct");
        assertEquals(getBrowser().findElement(emptyMessage).getText(), language.translate("siteContentDashlet.emptyList"),
        "Empty list site content dashlet message is not correct");

        return this;
    }

    public SiteContentDashlet clickSimpleViewIcon()
    {
        LOG.info("Click simple view icon");
        simpleViewIcon.click();
        return this;
    }

    public SiteContentDashlet openFilterDropdown()
    {
        LOG.info("Open filter dropdown");
        defaultFilterButton.click();
        return this;
    }

    public SiteContentDashlet assertFilterLabelEquals(String expectedFilterLabel)
    {
        LOG.info("Assert filter label equals: {}", expectedFilterLabel);
        WebElement dropdownFilterLabel = browser.findFirstElementWithValue(filters, expectedFilterLabel);
        assertEquals(dropdownFilterLabel.getText(), expectedFilterLabel,
            String.format("Filter label not equals %s ", expectedFilterLabel));

        return this;
    }

    public SiteContentDashlet assertDetailedViewIconIsDisplayed()
    {
        LOG.info("Assert detailed view icon is displayed");
        assertTrue(browser.isElementDisplayed(detailedViewIcon), "Detailed view icon is not displayed");

        return this;
    }

    public SiteContentDashlet clickDetailedViewButton()
    {
        LOG.info("Click detailed view button");
        browser.waitUntilElementVisible(detailedViewIcon).click();
        return this;
    }

    public ManageSiteContent usingDocument(FileModel file)
    {
        return new ManageSiteContent(this, documentDetailsPage, file);
    }

    public class ManageSiteContent
    {
        private SiteContentDashlet siteContentDashlet;
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

        public ManageSiteContent(SiteContentDashlet siteContentDashlet, DocumentDetailsPage documentDetailsPage, FileModel file)
        {
            this.siteContentDashlet = siteContentDashlet;
            this.documentDetailsPage = documentDetailsPage;
            this.file = file;

            LOG.info(String.format("Using file: %s in Site Content dashlet", file.getName()));
        }

        private WebBrowser getBrowser()
        {
            return siteContentDashlet.getBrowser();
        }

        public WebElement getFileRow()
        {
            return siteContentDashlet.getDocumentRow(file.getName());
        }

        public ManageSiteContent assertFileIsDisplayed()
        {
            assertTrue(getBrowser().isElementDisplayed(getFileRow()), String.format("File %s is displayed", file.getName()));
            return this;
        }

        public ManageSiteContent assertFileIsNotDisplayed()
        {
            assertFalse(getBrowser().isElementDisplayed(
                By.xpath(String.format(siteContentDashlet.documentRow, file.getName()))));
            return this;
        }

        public ManageSiteContent assertSiteEqualsTo(SiteModel site)
        {
            LOG.info("Assert site equals to {}", site.getTitle());
            assertEquals(getFileRow().findElement(siteLocator).getText(), site.getTitle(),
                String.format("Site %s is not displayed", site.getTitle()));
            return this;
        }

        public ManageSiteContent assertThumbnailIsDisplayed()
        {
            LOG.info("Assert thumbnail is displayed");
            assertTrue(getBrowser().isElementDisplayed(getFileRow().findElement(thumbnail)),
                "Thumbnail is not displayed");
            return this;
        }

        public DocumentDetailsPage clickFileName()
        {
            LOG.info("Click file name");
            getFileRow().findElement(fileNameLocator).click();
            return (DocumentDetailsPage) documentDetailsPage.renderedPage();
        }

        public ManageSiteContent assertDescriptionEqualsTo(String expectedDescription)
        {
            LOG.info("Assert expected description equals to {}", expectedDescription);
            assertEquals(getFileRow().findElement(description).getText(), expectedDescription,
                "File description is not correct");
            return this;
        }

        public ManageSiteContent assertFileVersionEqualsTo(double expectedVersion)
        {
            LOG.info("Assert file version equals to {}", expectedVersion);
            getBrowser().mouseOver(getFileRow().findElement(fileNameLocator));
            getBrowser().waitUntilChildElementIsPresent(getFileRow(), documentVersion);
            assertEquals(Double.valueOf(getFileRow().findElement(documentVersion).getText()), expectedVersion,
                "File version is not correct");
            return this;
        }

        public ManageSiteContent assertFileSizeEqualsTo(String expectedFileSize)
        {
            LOG.info("Assert file size equals to {}", expectedFileSize);
            assertEquals(getFileRow().findElement(fileSize).getText(), expectedFileSize,
                "File size is not correct");
            return this;
        }

        public ManageSiteContent assertLikeIsDisplayed()
        {
            LOG.info("Assert like is displayed");
            assertTrue(getBrowser().isElementDisplayed(getFileRow().findElement(likeAction)), "Like is not displayed");
            return this;
        }

        public ManageSiteContent assertUnlikeIsDisplayed()
        {
            LOG.info("Assert unlike is displayed");
            assertTrue(getBrowser().isElementDisplayed(getFileRow().findElement(unlikeAction)), "Unlike is not displayed");
            return this;
        }

        public ManageSiteContent assertNumberOfLikesEqualsTo(int nrOfLikes)
        {
            LOG.info("Assert number of likes equals to {}", nrOfLikes);
            int likes = Integer.parseInt(getFileRow().findElement(likesCount).getText());
            assertEquals(likes, nrOfLikes, "Number of likes is correct");
            return this;
        }

        public ManageSiteContent clickLike()
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
            browser.clickJS(likeBtn);
            browser.waitUntilChildElementIsPresent(getFileRow(), unlikeAction);
            return this;
        }

        public ManageSiteContent clickUnlike()
        {
            LOG.info("Click unlike");
            browser.clickJS(getFileRow().findElement(unlikeAction));
            browser.waitUntilChildElementIsPresent(getFileRow(), likeAction);
            return this;
        }

        public ManageSiteContent assertAddToFavoriteIsDisplayed()
        {
            LOG.info("Assert add to favorite is displayed");
            assertTrue(browser.isElementDisplayed(getFileRow().findElement(favoriteAction)),
                "Favorite action is not displayed");
            return this;
        }

        public ManageSiteContent assertRemoveFromFavoriteIsDisplayed()
        {
            LOG.info("Assert remove from favorite is displayed");
            assertTrue(browser.isElementDisplayed(getFileRow().findElement(removeFromFavorite)),
            "Remove from favorite is not displayed");
            return this;
        }

        public ManageSiteContent addToFavorite()
        {
            LOG.info("Add file to favorite");
            browser.clickJS(getFileRow().findElement(favoriteAction));
            browser.waitUntilChildElementIsPresent(getFileRow(), removeFromFavorite);
            return this;
        }

        public ManageSiteContent removeFromFavorite()
        {
            LOG.info("Remove file from favorite");
            browser.clickJS(getFileRow().findElement(removeFromFavorite));
            browser.waitUntilChildElementIsPresent(getFileRow(), favoriteAction);
            return this;
        }

        public DocumentDetailsPage clickCommentLink()
        {
            getFileRow().findElement(commentLink).click();
            return (DocumentDetailsPage) documentDetailsPage.renderedPage();
        }
    }
}
