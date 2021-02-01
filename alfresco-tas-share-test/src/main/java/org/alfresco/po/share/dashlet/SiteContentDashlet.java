package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.alfresco.common.Wait.WAIT_1;
import static org.alfresco.common.Wait.WAIT_60;

import org.alfresco.common.WebElementInteraction;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.SiteModel;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SiteContentDashlet extends Dashlet<SiteContentDashlet>
{
    private final By filters = By.cssSelector("div[class^='dashlet docsummary'] .yuimenuitemlabel");
    private final By dashletContainer = By.cssSelector("div.dashlet.docsummary");
    private final By simpleViewIcon = By.cssSelector("div.dashlet.docsummary span[class$='first-child'] [title='Simple View']");
    private final By detailedViewIcon = By.cssSelector("div.dashlet.docsummary span[class$='first-child'] [title='Detailed View']");
    private final By defaultFilterButton = By.cssSelector("[id$='default-filters']");
    private final By emptyMessage = By.cssSelector("div[id$='default-documents'] .empty");
    protected String documentRow = "//div[starts-with(@class, 'dashlet docsummary')]//a[text()='%s']/../../../..";

    public SiteContentDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    protected String getDashletTitle()
    {
        return webElementInteraction.getElementText(webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    protected WebElement getDocumentRow(String documentName)
    {
        return webElementInteraction.waitWithRetryAndReturnWebElement(
            By.xpath(String.format(documentRow, documentName)), WAIT_1.getValue(), WAIT_60.getValue());
    }

    public SiteContentDashlet assertEmptySiteContentMessageIsCorrect()
    {
        LOG.info("Assert empty site content message is correct");
        assertEquals(webElementInteraction.getElementText(emptyMessage), language.translate("siteContentDashlet.emptyList"),
        "Empty list site content dashlet message is not correct");

        return this;
    }

    public SiteContentDashlet clickSimpleViewIcon()
    {
        LOG.info("Click simple view icon");
        webElementInteraction.clickElement(simpleViewIcon);
        return this;
    }

    public SiteContentDashlet openFilterDropdown()
    {
        LOG.info("Open filter dropdown");
        webElementInteraction.clickElement(defaultFilterButton);
        return this;
    }

    public SiteContentDashlet assertFilterLabelEquals(String expectedFilterLabel)
    {
        LOG.info("Assert filter label equals: {}", expectedFilterLabel);
        WebElement dropdownFilterLabel = webElementInteraction.findFirstElementWithValue(filters, expectedFilterLabel);
        assertEquals(dropdownFilterLabel.getText(), expectedFilterLabel,
            String.format("Filter label not equals %s ", expectedFilterLabel));

        return this;
    }

    public SiteContentDashlet assertDetailedViewIconIsDisplayed()
    {
        LOG.info("Assert detailed view icon is displayed");
        webElementInteraction.waitUntilElementIsVisible(detailedViewIcon);
        assertTrue(webElementInteraction.isElementDisplayed(detailedViewIcon), "Detailed view icon is not displayed");

        return this;
    }

    public SiteContentDashlet clickDetailedViewButton()
    {
        LOG.info("Click detailed view button");
        webElementInteraction.clickElement(detailedViewIcon);
        return this;
    }

    public ManageSiteContent usingDocument(FileModel file)
    {
        return new ManageSiteContent(this,
            webElementInteraction,
            new DocumentDetailsPage(webDriver),
            file);
    }

    public class ManageSiteContent
    {
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

        public ManageSiteContent(SiteContentDashlet siteContentDashlet,
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

        public ManageSiteContent assertFileIsDisplayed()
        {
            assertTrue(webElementInteraction.isElementDisplayed(getFileRow()), String.format("File %s is displayed", file.getName()));
            return this;
        }

        public ManageSiteContent assertFileIsNotDisplayed()
        {
            assertFalse(webElementInteraction.isElementDisplayed(
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
            assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(thumbnail)),
                "Thumbnail is not displayed");
            return this;
        }

        public DocumentDetailsPage clickFileName()
        {
            LOG.info("Click file name");
            getFileRow().findElement(fileNameLocator).click();

            return new DocumentDetailsPage(webDriver);
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
            webElementInteraction.mouseOver(getFileRow().findElement(fileNameLocator));
            webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), documentVersion);
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
            assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(likeAction)), "Like is not displayed");
            return this;
        }

        public ManageSiteContent assertUnlikeIsDisplayed()
        {
            LOG.info("Assert unlike is displayed");
            assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(unlikeAction)), "Unlike is not displayed");
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
            webElementInteraction.clickJS(likeBtn);
            webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), unlikeAction);
            return this;
        }

        public ManageSiteContent clickUnlike()
        {
            LOG.info("Click unlike");
            webElementInteraction.clickJS(getFileRow().findElement(unlikeAction));
            webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), likeAction);
            return this;
        }

        public ManageSiteContent assertAddToFavoriteIsDisplayed()
        {
            LOG.info("Assert add to favorite is displayed");
            assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(favoriteAction)),
                "Favorite action is not displayed");
            return this;
        }

        public ManageSiteContent assertRemoveFromFavoriteIsDisplayed()
        {
            LOG.info("Assert remove from favorite is displayed");
            assertTrue(webElementInteraction.isElementDisplayed(getFileRow().findElement(removeFromFavorite)),
            "Remove from favorite is not displayed");
            return this;
        }

        public ManageSiteContent addToFavorite()
        {
            LOG.info("Add file to favorite");
            webElementInteraction.clickJS(getFileRow().findElement(favoriteAction));
            webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), removeFromFavorite);
            return this;
        }

        public ManageSiteContent removeFromFavorite()
        {
            LOG.info("Remove file from favorite");
            webElementInteraction.clickJS(getFileRow().findElement(removeFromFavorite));
            webElementInteraction.waitUntilChildElementIsPresent(getFileRow(), favoriteAction);
            return this;
        }

        public DocumentDetailsPage clickCommentLink()
        {
            getFileRow().findElement(commentLink).click();
            return new DocumentDetailsPage(webDriver);
        }
    }
}
