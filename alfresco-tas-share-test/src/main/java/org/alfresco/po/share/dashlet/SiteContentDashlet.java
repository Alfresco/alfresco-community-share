package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Link;

import java.util.List;

@PageObject
public class SiteContentDashlet extends Dashlet<SiteContentDashlet>
{
    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    UserProfilePage userProfile;

    @RenderWebElement
    @FindBy(css = "div.dashlet.docsummary")
    private WebElement dashletContainer;

    @FindBy(css = "div.dashlet.docsummary span[class$='first-child'] [title='Simple View']")
    private WebElement simpleViewButton;

    @FindBy(css = " div.dashlet.docsummary span[class$='first-child'] [title='Detailed View']")
    private WebElement detailedViewButton;

    @FindAll(@FindBy(css = ".filename>a"))
    protected List<WebElement> documentsLinksList;

    @FindAll(@FindBy(css = ".bd>img"))
    private WebElement bigPreview;

    @FindAll(@FindBy(css = ".yuimenuitemlabel.yuimenuitemlabel"))
    protected List<WebElement> filters;

    @FindBy(css = "[id$='default-filters']")
    private WebElement defaultFilterButton;

    @FindBy(xpath = "//a[@title = 'Add document to favorites']")
    private WebElement addToFavoritesLink;

    @FindBy(xpath = "//a[@title = 'Remove document from favorites']")
    private WebElement removeFromFavoritesLink;

    @FindBy(xpath = "//a[@title = 'Like this document']")
    private WebElement likeDocument;

    @FindBy(xpath = "//a[@title = 'Unlike']")
    private WebElement unlikeDocument;

    private By numberOfLikes = By.xpath("//span[contains(@class, 'likes-count')]");

    private By documentsList = By.cssSelector("h3.filename > a");

    protected By docDetails = By.xpath("../following-sibling::*[1][@class='detail']/span[1]");
    protected String smallThumbnailIcon = "//img[contains(@src, '/share/res/components/images/filetypes/generic-file-32.png')][contains(@title,'";
    protected String bigThumbnailIcon = "//img[contains(@src, '/content/thumbnails/doclib?c=queue&ph=true')][contains(@title,'";

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
        String smallThumbnail1 = smallThumbnailIcon + fileName + "')]";
        String smallThumbnail = StringUtils.deleteWhitespace(smallThumbnail1);

        browser.waitUntilElementVisible(By.xpath(smallThumbnail));
        return browser.isElementDisplayed(By.xpath(smallThumbnail));
    }

    /**
     * This method is used to verify if an big thumbnail icon is displayed for a file
     */

    public boolean isBigThumbnailDisplayed(String fileName)
    {
        String bigThumbnail = bigThumbnailIcon + fileName + "')]";
        browser.waitUntilElementVisible(By.xpath(bigThumbnail));
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
     * @param fileName
     *            identifier
     * @return {@link Link} that matches fileName
     */

    public WebElement getFileLink(final String fileName)
    {
        browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector("h3.filename > a"), 5);
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
                browser.waitInSeconds(5);
                browser.refresh();
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
        String bigThumbnail = bigThumbnailIcon + fileName + "')]";
        browser.findElement(By.xpath(bigThumbnail)).click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    /**
     * This method is used to hover over small thumbnail icon
     */

    public void mouseHoverSmallThumbail(String fileName)
    {
        String smallThumbnail1 = smallThumbnailIcon + fileName + "')]";
        String smallThumbnail = StringUtils.deleteWhitespace(smallThumbnail1);

        browser.mouseOver(browser.findElement(By.xpath(smallThumbnail)));
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

        // *[contains(text(), 'editedDescription')]

        // By fileDescription = By.xpath("//span[contains(@class, 'likes-count')]");
        By fileDescription = By.xpath("// *[contains(text(), '" + description + "')]");

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
        return simpleViewButton.isDisplayed();
    }

    public boolean isDetailedViewButtonPresent()

    {
        return detailedViewButton.isDisplayed();
    }

    public void clickDetailedViewButton()
    {
        detailedViewButton.click();
    }

    public boolean isDocumentFavorited() throws Exception
    {
        try
        {
            browser.findElement(By.xpath("//a[@title = 'Remove document from favorites']"));
            return true;

        }
        catch (NoSuchElementException e)
        {

            return false;
        }

    }

    public boolean isDocumentRemovedFromFavorites() throws Exception
    {
        try
        {
            browser.findElement(By.xpath("//a[@title = 'Add document to favorites']"));
            return true;

        }
        catch (NoSuchElementException e)
        {

            return false;
        }

    }

    public void clickOnFavoriteLink()
    {
        browser.waitUntilElementVisible(addToFavoritesLink);
        addToFavoritesLink.click();
        browser.waitInSeconds(1);
    }

    public void removeFromFavoritesLink()
    {
        browser.waitUntilElementVisible(removeFromFavoritesLink);
        removeFromFavoritesLink.click();
        browser.waitInSeconds(1);
    }

    public void likeFile(String fileName)
    {
        selectItem(fileName).findElement(By.xpath("//a[@title = 'Like this document']")).click();
        browser.waitUntilElementVisible(By.xpath("//a[@title = 'Unlike']"));
    }

    public void unlikeFile(String fileName)
    {
        selectItem(fileName).findElement(By.xpath("//a[@title = 'Unlike']")).click();
        browser.waitUntilElementVisible(By.xpath("//a[@title = 'Like this document']"));
    }

    public boolean isLikeButtonDisplayed(String fileName)
    {
        return selectItem(fileName).findElement(By.xpath("//a[@title = 'Like this document']")) != null;
    }

    public boolean isUnlikeLinkDisplayed(String fileName)
    {
        return selectItem(fileName).findElement(By.xpath("//a[@title = 'Unlike']")) != null;
    }

    public boolean isAddToFavoritesLinkDisplayed(String fileName)
    {
        return selectItem(fileName).findElement(By.xpath("//a[@title = 'Add document to favorites']")) != null;
    }

    public boolean isCommentLinkDisplayed(String fileName)
    {
        return selectItem(fileName).findElement(By.xpath("//*[contains(text(), 'Comment')]")) != null;
    }
    
    public void clickCommentLink(String fileName)
    {
        selectItem(fileName).findElement(By.xpath("//*[contains(text(), 'Comment')]")).click();
    }

    public boolean isFileVersionDisplayed(String fileName, String fileVersion)
    {
        return selectItem(fileName).findElement(By.xpath("//*[contains(text(), '" + fileVersion + "')]")) != null;
    }

    public boolean isRemoveFromFavoritesLinkDisplayed(String fileName)
    {
        return selectItem(fileName).findElement(By.xpath("//a[@title = 'Remove document from favorites']")) != null;
    }

    public int getNumberOfLikes(String fileName)
    {
        return Integer.parseInt(selectItem(fileName).findElement(numberOfLikes).getText());
    }

    public boolean isDocumentSizedisplayed(String fileName, String size)
    {

        String documentSize = "//*[contains(text(), '" + size + "')]";
        return selectItem(fileName).findElement(By.xpath(documentSize)) != null;
    }

    public WebElement selectItem(String document)
    {
        browser.waitUntilElementIsDisplayedWithRetry(documentsList, 3);
        List<WebElement> itemsList = browser.findElements(documentsList);
        return browser.findFirstElementWithValue(itemsList, document);
    }

}
