package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.common.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Link;

@PageObject
public class SiteContentDashlet extends Dashlet<SiteContentDashlet>
{
    @FindAll (@FindBy (css = ".filename>a"))
    protected List<WebElement> documentsLinksList;
    @FindAll (@FindBy (css = ".yuimenuitemlabel.yuimenuitemlabel"))
    protected List<WebElement> filters;
    protected By docDetails = By.xpath("../following-sibling::*[1][@class='detail']/span[1]");
    protected String smallThumbnailIcon = "//img[contains(@src, '/share/res/components/images/filetypes/generic-file-32.png')][contains(@title,'%s')]";
    protected String bigThumbnailIcon = "//img[contains(@src, '/content/thumbnails/doclib?c=queue&ph=true')][contains(@title,'%s')]";
    @Autowired
    DocumentDetailsPage documentDetailsPage;
    @RenderWebElement
    @FindBy (css = "div.dashlet.docsummary")
    private WebElement dashletContainer;
    @FindBy (css = "div.dashlet.docsummary span[class$='first-child'] [title='Simple View']")
    private WebElement simpleViewButton;
    @FindBy (css = " div.dashlet.docsummary span[class$='first-child'] [title='Detailed View']")
    private WebElement detailedViewButton;
    @FindAll (@FindBy (css = ".bd>img"))
    private WebElement bigPreview;
    @FindBy (css = "[id$='default-filters']")
    private WebElement defaultFilterButton;
    private By addToFavoritesLink = By.cssSelector("a[title = 'Add document to favorites']");
    private By removeFromFavoritesLink = By.cssSelector("a[title = 'Remove document from favorites']");
    private By like = By.cssSelector("a[title = 'Like this document']");
    private By unlike = By.cssSelector("a[title = 'Unlike']");
    private By commentLink = By.cssSelector("a.comment");
    private By numberOfLikes = By.cssSelector("span.likes-count");
    private By documentsList = By.cssSelector("tbody.yui-dt-data tr");
    private By documentVersion = By.cssSelector("span.document-version");

    @Override
    protected String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    /**
     * This method is used to click on "Simple View" button
     */
    public void clickSimpleViewButton()
    {
        simpleViewButton.click();
    }

    /**
     * This method is used to verify if an small thumbnail icon is displayed for a file
     */

    public boolean isSmallThumbnailDisplayed(String fileName)
    {
        String smallThumbnail = StringUtils.deleteWhitespace(String.format(smallThumbnailIcon, fileName));

        browser.waitUntilElementIsDisplayedWithRetry(By.xpath(smallThumbnail), 5);
        return browser.isElementDisplayed(By.xpath(smallThumbnail));
    }

    /**
     * This method is used to verify if an big thumbnail icon is displayed for a file
     */

    public boolean isBigThumbnailDisplayed(String fileName)
    {
        String bigThumbnail = StringUtils.deleteWhitespace(String.format(bigThumbnailIcon, fileName));
        browser.waitUntilElementIsDisplayedWithRetry(By.xpath(bigThumbnail), 5);
        return browser.isElementDisplayed(By.xpath(bigThumbnail));
    }

    /**
     * Get list of document links displayed in Site Content dashlet
     */

    public List<WebElement> getDocumentsLinksList()
    {
        return documentsLinksList;
    }

    /**
     * Retrieves the link that matches a file name.
     *
     * @param fileName identifier
     * @return {@link Link} that matches fileName
     */

    public WebElement getFileLink(final String fileName)
    {
        browser.waitUntilElementIsDisplayedWithRetry(documentsList, 5);
        return browser.findFirstElementWithValue(documentsLinksList, fileName);
    }

    /**
     * Verify if a link for a file is displayed in Site Content dashlet
     *
     * @param fileName
     * @return True if user exists
     */

    public boolean isFileLinkPresent(String fileName)
    {
        int counter = 0;
        boolean found = false;
        while (!found && counter < 6)
        {
            found = browser.isElementDisplayed(getFileLink(fileName));

            if (!found)
            {
                browser.refresh();
                browser.waitInSeconds(5);
                counter++;
            }
        }
        return found;
    }

    /**
     * Open document details page for a file
     *
     * @param fileName
     * @return
     */
    public DocumentDetailsPage clickFileLink(final String fileName)
    {
        getFileLink(fileName).click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    /**
     * Open user profile page
     *
     * @param fileName
     * @return
     */
    public DocumentDetailsPage clickBigThumbnailForFile(String fileName)
    {
        String bigThumbnail = StringUtils.deleteWhitespace(String.format(bigThumbnailIcon, fileName));
        browser.findElement(By.xpath(bigThumbnail)).click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    /**
     * This method is used to hover over small thumbnail icon
     */

    public void mouseHoverSmallThumbail(String fileName)
    {
        String smallThumbnail = StringUtils.deleteWhitespace(String.format(smallThumbnailIcon, fileName));

        browser.mouseOver(browser.findElement(By.xpath(smallThumbnail)));
        browser.waitUntilElementVisible(By.cssSelector(".bd>img"));
    }

    /**
     * This method is used to verify if an big preview is displayed for a file
     */

    public boolean isBigPreviewDisplayed()
    {
        return browser.isElementDisplayed(bigPreview);
    }

    /**
     * Verify if a document details section is displayed
     *
     * @param document
     * @return True if details are displayed
     */
    public boolean isDocDetailsSectionPresent(String document)
    {
        return getFileLink(document).findElement(docDetails) != null;
    }

    public boolean areFileDetailsDisplayed(String fileName, String siteName)
    {

        String details = getFileLink(fileName).findElement(docDetails).getText();
        return (details.contains("Modified") || details.contains("Created")) && details.contains("in " + siteName)
                && ((details.contains("just now") || (details.contains("ago"))));

    }

    public boolean isFileDescription(String fileName, String description)
    {
        By fileDescription = By.xpath("// *[contains(text(), '" + description + "')]");
        Parameter.checkIsMandotary("File", selectItem(fileName));
        return selectItem(fileName).findElement(fileDescription) != null;
    }

    /**
     * Get document details
     *
     * @param document
     * @return
     */
    public String getDocDetails(String document)
    {
        return getFileLink(document).findElement(docDetails).getText();
    }

    /**
     * This method is used to click on default filter button
     */

    public void clickDefaultFilterButton()

    {
        defaultFilterButton.click();
    }

    public boolean isFilterDisplayed(String filter)
    {
        return browser.findFirstElementWithValue(filters, filter) != null;
    }

    public boolean isSimpleViewButtonPresent()
    {
        return browser.isElementDisplayed(simpleViewButton);
    }

    public boolean isDetailedViewButtonPresent()
    {
        return browser.isElementDisplayed(detailedViewButton);
    }

    public void clickDetailedViewButton()
    {
        browser.waitUntilElementVisible(detailedViewButton).click();
    }

    public void addFileToFavorites(String fileName)
    {
        Parameter.checkIsMandotary("File", selectItem(fileName));
        browser.waitUntilElementVisible(selectItem(fileName).findElement(addToFavoritesLink)).click();
        //selectItem(fileName).findElement(addToFavoritesLink).click();
        int counter = 0;
        while (!isFileAddedToFavorites(fileName) && counter < 5)
        {
            browser.waitInSeconds(1);
            counter++;
        }
    }

    public void removeFileFromFavorites(String fileName)
    {
        Parameter.checkIsMandotary("File", selectItem(fileName));
        selectItem(fileName).findElement(removeFromFavoritesLink).click();
        int counter = 0;
        while (!isAddToFavoritesLinkDisplayed(fileName) && counter < 5)
        {
            browser.waitInSeconds(1);
            counter++;
        }
    }

    public void likeFile(String fileName)
    {
        Parameter.checkIsMandotary("File", selectItem(fileName));
        selectItem(fileName).findElement(like).click();
        int counter = 0;
        while (!isUnlikeLinkDisplayed(fileName) && counter < 5)
        {
            browser.waitInSeconds(1);
            counter++;
        }
    }

    public void unlikeFile(String fileName)
    {
        Parameter.checkIsMandotary("File", selectItem(fileName));
        selectItem(fileName).findElement(unlike).click();
        int counter = 0;
        while (!isLikeButtonDisplayed(fileName) && counter < 5)
        {
            browser.waitInSeconds(1);
            counter++;
        }
    }

    public boolean isLikeButtonDisplayed(String fileName)
    {
        return browser.isElementDisplayed(selectItem(fileName), like);
    }

    public boolean isUnlikeLinkDisplayed(String fileName)
    {
        return browser.isElementDisplayed(selectItem(fileName), unlike);
    }

    public boolean isAddToFavoritesLinkDisplayed(String fileName)
    {
        return browser.isElementDisplayed(selectItem(fileName), addToFavoritesLink);
    }

    public boolean isCommentLinkDisplayed(String fileName)
    {
        return browser.isElementDisplayed(selectItem(fileName), commentLink);
    }

    public DocumentDetailsPage clickCommentLink(String fileName)
    {
        browser.waitUntilElementIsDisplayedWithRetry(commentLink);
        selectItem(fileName).findElement(commentLink).click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    public boolean isFileVersionDisplayed(String fileName, String fileVersion)
    {
        browser.mouseOver(selectItem(fileName));
        if (browser.isElementDisplayed(selectItem(fileName), documentVersion))
            return selectItem(fileName).findElement(documentVersion).getText().equals(fileVersion);
        return false;
    }

    public boolean isFileAddedToFavorites(String fileName)
    {
        return browser.isElementDisplayed(selectItem(fileName), removeFromFavoritesLink);
    }

    public int getNumberOfLikes(String fileName)
    {
        Parameter.checkIsMandotary("File", selectItem(fileName));
        return Integer.parseInt(selectItem(fileName).findElement(numberOfLikes).getText());
    }

    public boolean isDocumentSizeDisplayed(String fileName, String size)
    {
        String documentSize = "//*[contains(text(), '" + size + "')]";
        return browser.isElementDisplayed(selectItem(fileName), By.xpath(documentSize));
    }

    public WebElement selectItem(String document)
    {
        browser.waitUntilElementIsDisplayedWithRetry(documentsList, 6);
        List<WebElement> itemsList = browser.findElements(documentsList);
        return browser.findFirstElementWithValue(itemsList, document);
    }

    public SiteContentDashlet selectFilter(String filterName)
    {
        browser.findFirstElementWithValue(filters, filterName).click();
        return (SiteContentDashlet) this.renderedPage();
    }
}
