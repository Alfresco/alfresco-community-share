package org.alfresco.po.share.alfrescoContent.document;

import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class SocialFeatures extends DocumentLibraryPage
{
    public By facebookHomeLink = By.id("homelink");
    public By googlePlusEmailField = By.id("Email");
    public By quickShareWindow = By.cssSelector("div.yuimenu.quickshare-action-menu.yui-module.yui-overlay.visible");
    public By enabledLikeButton = By.cssSelector("a[class ='like-action enabled']");
    protected String user = "alfresco.cloud@gmail.com";
    protected String password = "alfresco123!";
    protected String gEmail = "test.alfresco5@gmail.com";
    protected String gPassword = "Ness2015*";
    @Autowired
    DocumentDetailsPage documentDetailsPage;
    @FindBy (css = ".section input[id*='input']")
    private WebElement publicLinkInputField;
    @FindBy (css = "a.quickshare-action-view")
    private WebElement publicLinkViewButton;
    @FindBy (css = "a.linkshare-action-facebook")
    private WebElement shareFacebook;
    @FindBy (css = "a.linkshare-action-twitter")
    private WebElement shareTwitter;
    @FindBy (css = "a.linkshare-action-google-plus")
    private WebElement shareGooglePlus;
    @FindBy (css = "a.d-s.ot-anchor")
    private WebElement shareGooglePlusLink;
    @FindBy (id = "Email")
    private WebElement googleEmail;
    @FindBy (id = "Passwd")
    private WebElement googlePassword;
    @FindBy (id = "next")
    private WebElement nextButton;
    @FindBy (id = "signIn")
    private WebElement signInButton;
    @FindBy (css = "a.quickshare-action-unshare")
    private WebElement unshareButton;
    @FindBy (css = "#alf-id6-input")
    private WebElement sharedUrl;
    @FindBy (xpath = "//a[text()='Login']")
    private WebElement loginButtonOnSharedFilePage;
    @FindBy (xpath = "//a[text()='Document Details']")
    private WebElement documentDetailsButtonOnSharedFilePage;
    @FindBy (css = ".textLayer>div")
    private WebElement contentFromSharedFilePage;
    private By commentLinkSelector = By.cssSelector(".comment");
    private By commentCounterSelector = By.cssSelector(".comment-count");
    private By shareButton = By.cssSelector("a.quickshare-action");
    private By likesCount = By.cssSelector("span.likes-count");

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method to get the Like button tooltip message when mouse is hovered over element
     */

    public String getLikeButtonMessage(String fileName)
    {
        return selectDocumentLibraryItemRow(fileName).findElement(likeButton).getAttribute("title");
    }

    /**
     * Method to get the number of likes
     *
     * @param fileName
     * @return
     */
    public int getNumberOfLikes(String fileName)
    {
        String likesNo = selectDocumentLibraryItemRow(fileName).findElement(likesCount).getText();
        return Integer.parseInt(likesNo);
    }

    /**
     * Method to click the like button
     *
     * @param fileName
     */
    public void clickLikeButton(String fileName)
    {
        selectDocumentLibraryItemRow(fileName).findElement(likeButton).click();
        getBrowser().waitUntilElementVisible(enabledLikeButton);
    }

    /**
     * Method to check if the like button is enabled
     *
     * @param fileName
     * @return
     */
    public boolean isLikeButtonEnabled(String fileName)
    {
        return browser.isElementDisplayed(selectDocumentLibraryItemRow(fileName), enabledLikeButton);
    }

    /**
     * Method to get the text when like is enabled
     *
     * @param fileName
     * @return
     */
    public String getLikeButtonEnabledText(String fileName)
    {
        return selectDocumentLibraryItemRow(fileName).findElement(enabledLikeButton).getAttribute("title");
    }

    /**
     * Method to click Unlike
     *
     * @param fileName
     */
    public void clickUnlike(String fileName)
    {
        selectDocumentLibraryItemRow(fileName).findElement(enabledLikeButton).click();
    }

    public DocumentDetailsPage clickCommentLink(String contentName)
    {
        selectDocumentLibraryItemRow(contentName).findElement(commentLinkSelector).click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    public int getNumberOfComments(String contentName)
    {
        return Integer.parseInt(selectDocumentLibraryItemRow(contentName).findElement(commentCounterSelector).getText());
    }

    /**
     * Method to get the Share tooltip
     */
    public String getShareButtonTooltip(String fileName)
    {
        return selectDocumentLibraryItemRow(fileName).findElement(shareButton).getAttribute("title");
    }

    /**
     * Method to check if the Share button is displayed for selected content
     */
    public boolean isShareButtonDisplayed(String contentName)
    {
        return browser.isElementDisplayed(selectDocumentLibraryItemRow(contentName), shareButton);
    }

    /**
     * Method to click the Share button
     */
    public void clickShareButton(String contentName)
    {
        selectDocumentLibraryItemRow(contentName).findElement(shareButton).click();
    }

    /**
     * Method to check id the quick share window is displayed when Share button is pressed
     */
    public boolean isQuickshareWindowDisplayed()
    {
        return browser.isElementDisplayed(quickShareWindow);
    }

    /**
     * Method to get the public link
     */
    public boolean isPublicLinkDisplayed()
    {
        return browser.isElementDisplayed(publicLinkViewButton);
    }

    /**
     * Method to click Share with Facebook
     */
    public void clickShareWithFacebook()
    {
        shareFacebook.click();
    }


    public String getFacebookWindowTitle()
    {
        return browser.findElement(By.id("homelink")).getText();
    }

    public void loginFacebook()
    {
        browser.findElement(By.id("email")).sendKeys(user);
        browser.findElement(By.id("pass")).sendKeys(password);
        browser.findElement(By.id("loginbutton")).click();
        browser.waitInSeconds(2);
    }

    public boolean isShareLinkDisplayedOnFacebook()
    {
        return browser.isElementDisplayed(By.xpath("//div[@class='mbs _6m6 _2cnj _5s6c']"));
    }

    /**
     * Method to click the Twitter icon
     */
    public void clickTwitterIcon()
    {
        shareTwitter.click();
        browser.waitInSeconds(2);
    }

    /**
     * Method to get page title for Twitter
     */
    public String getTwitterPageLogo()
    {
        return browser.findElement(By.xpath("//h1[@class ='logo']/a")).getText();
    }

    /**
     * Method to get the Twitter page title
     */
    public String getTwitterPageTitle()
    {
        return browser.findElement(By.cssSelector("h2.action-information")).getText();
    }

    /**
     * Method to get the link shared with Twitter
     */
    public String getTwitterShareLink()
    {
        String link = browser.findElement(By.id("status")).getText();
        link = link.substring(0, link.lastIndexOf('/'));
        return link;
    }

    /**
     * Method to click on Google Plus icon on the share dialog window
     */
    public void clickGooglePlus()
    {
        shareGooglePlus.click();
    }

    /**
     * Method to get the link shared on GooglePlus
     */
    public String getLinkSharedWithGooglePlus()
    {
        String sharedLink = shareGooglePlusLink.getAttribute("href");
        sharedLink = sharedLink.substring(0, sharedLink.lastIndexOf('/'));
        return sharedLink;
    }

    /**
     * Method to sing into Google+
     */
    public void loginToGoogleAccount()
    {
        browser.waitUntilElementIsDisplayedWithRetry(googlePlusEmailField, 3);
        googleEmail.sendKeys(gEmail);
        nextButton.click();
        browser.waitUntilWebElementIsDisplayedWithRetry(googlePassword, 3);
        googlePassword.sendKeys(gPassword);
        signInButton.click();
    }

    /**
     * Method to click the Unshare button
     */
    public void clickUnshareButton()
    {
        unshareButton.click();
    }

    public boolean checkShareButtonAvailability()
    {
        return browser.isElementDisplayed(shareButton);
    }

    public boolean isPublicLinkInputFieldDisplayed()
    {
        return browser.isElementDisplayed(publicLinkInputField);
    }

    /**
     * Method to click on PublicLinkViewButton
     */
    public void clickPublicLinkViewButton()
    {
        publicLinkViewButton.click();
    }

    /**
     * Method to check if Login button is displayed on Shared File page
     */

    public boolean isLoginButtonOnSharedFilePage()
    {
        browser.waitUntilElementVisible(loginButtonOnSharedFilePage);
        return browser.isElementDisplayed(loginButtonOnSharedFilePage);
    }

    /**
     * Method to check if Document Details button is displayed on Shared File page
     */

    public boolean isDocumentDetailsButtonOnSharedFilePageDisplayed()
    {
        browser.waitUntilElementVisible(documentDetailsButtonOnSharedFilePage);
        return browser.isElementDisplayed(documentDetailsButtonOnSharedFilePage);
    }

    public void clickLoginButtonOnSharedFilePage()
    {
        loginButtonOnSharedFilePage.click();
    }

    public DocumentDetailsPage clickDocumentDetailsButtonOnSharedFilePage()
    {
        documentDetailsButtonOnSharedFilePage.click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    /**
     * Method to get the content text of the shared file page
     */

    public String getContentTextFromSharedFilePage()
    {
        return contentFromSharedFilePage.getText();
    }

    /**
     * Method to check if shared url is displayed when clicking 'Share' button
     */

    public boolean isShareUrlDisplayed()
    {
        browser.waitUntilElementVisible(sharedUrl);
        return browser.isElementDisplayed(sharedUrl);
    }
}