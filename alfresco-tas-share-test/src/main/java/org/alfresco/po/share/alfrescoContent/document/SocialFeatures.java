package org.alfresco.po.share.alfrescoContent.document;

import java.util.List;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class SocialFeatures extends SiteCommon<SocialFeatures>
{
    @FindBy(css = "a.like-action")
    public WebElement likeButton;

    @FindBy(css = ".section input[id*='input']")
    private WebElement publicLinkInputField;

    @FindBy(css = "a.quickshare-action-view")
    private WebElement publicLinkViewButton;

    @FindBy(css = "a.linkshare-action-facebook")
    private WebElement shareFacebook;

    @FindBy(css = "a.linkshare-action-twitter")
    private WebElement shareTwitter;

    @FindBy(css = "a.linkshare-action-google-plus")
    private WebElement shareGooglePlus;

    @FindBy(css = "a.d-s.ot-anchor")
    private WebElement shareGooglePlusLink;

    @FindBy(id = "Email")
    private WebElement googleEmail;

    @FindBy(id = "Passwd")
    private WebElement googlePassword;

    @FindBy(id = "next")
    private WebElement nextButton;

    @FindBy(id = "signIn")
    private WebElement signInButton;

    @FindBy(css = "a.quickshare-action-unshare")
    private WebElement unshareButton;

    @FindBy(css = "#alf-id6-input")
    private WebElement sharedUrl;

    @FindBy(xpath = "//a[text()='Login']")
    private WebElement loginButtonOnSharedFilePage;

    @FindBy(xpath = "//a[text()='Document Details']")
    private WebElement documentDetailsButtonOnSharedFilePage;

    @FindBy(css = ".textLayer>div")
    private WebElement contentFromSharedFilePage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    public By facebookHomeLink = By.id("homelink");
    public By googlePlusEmailField = By.id("Email");

    private By commentLinkSelector = By.cssSelector(".comment");
    private By commentCounterSelector = By.cssSelector(".comment-count");
    private By shareButton = By.cssSelector("a.quickshare-action");
    private By likesCount = By.cssSelector("span.likes-count");
    private By documentLibraryItemsList = By.cssSelector("[class*=data] tr");
    public By quickShareWindow = By.cssSelector("div.yuimenu.quickshare-action-menu.yui-module.yui-overlay.visible");
    public By enabledLikeButton = By.cssSelector("a[class ='like-action enabled']");

    public WebElement selectDocumentLibraryItemRow(String documentItem)
    {
        browser.waitUntilElementIsDisplayedWithRetry(documentLibraryItemsList, 6);
        List<WebElement> itemsList = browser.findElements(documentLibraryItemsList);
        return browser.findFirstElementWithValue(itemsList, documentItem);
    }

    protected String user = "alfresco.cloud@gmail.com";
    protected String password = "alfresco123!";
    protected String gEmail = "test.alfresco5@gmail.com";
    protected String gPassword = "Ness2015*";

    private WebElement selectContent(String contentName)
    {
        return browser.findElement(By.xpath("//a[text()='" + contentName + "']"));
    }

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
        return selectDocumentLibraryItemRow(fileName).findElement(By.cssSelector("a.like-action")).getAttribute("title").toString();
    }

    /**
     * Method to get the number of likes
     * 
     * @param fileName
     * @return
     */
    public int getNumberOfLikes(String fileName)
    {
        String likesNo = selectDocumentLibraryItemRow(fileName).findElement(likesCount).getText().toString();
        int numberOfLikes = Integer.parseInt(likesNo);
        return numberOfLikes;
    }

    /**
     * Method to click the like button
     * 
     * @param fileName
     */
    public void clickLikeButton(String fileName)
    {
        selectDocumentLibraryItemRow(fileName).findElement(By.cssSelector("a.like-action")).click();
    }

    /**
     * Method to check if the like button is enabled
     * 
     * @param fileName
     * @return
     */
    public boolean isLikeButtonEnabled(String fileName)
    {
        return selectDocumentLibraryItemRow(fileName).findElement(By.cssSelector("a[class ='like-action enabled']")).isDisplayed();
    }

    /**
     * Method to get the text when like is enabled
     * 
     * @param fileName
     * @return
     */
    public String getLikeButtonEnabledText(String fileName)
    {
        return selectDocumentLibraryItemRow(fileName).findElement(By.cssSelector("a[class ='like-action enabled']")).getAttribute("title").toString();
    }

    /**
     * Method to click Unlike
     * 
     * @param fileName
     */
    public void clickUnlike(String fileName)
    {
        selectDocumentLibraryItemRow(fileName).findElement(By.cssSelector("a[class ='like-action enabled']")).click();
    }

    public void clickCommentLink(String contentName)
    {
        selectDocumentLibraryItemRow(contentName).findElement(commentLinkSelector).click();
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
        return selectDocumentLibraryItemRow(fileName).findElement(shareButton).getAttribute("title").toString();
    }

    /**
     * Method to check if the Share button is displayed for selected content
     */
    public boolean isShareButtonDisplayed(String contentName)
    {
        return selectDocumentLibraryItemRow(contentName).findElement(shareButton).isDisplayed();
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
        return browser.findElement(quickShareWindow).isDisplayed();
    }

    /**
     * Method to get the public link
     */
    public boolean isPublicLinkDisplayed()
    {
        return publicLinkViewButton.isDisplayed();
    }

    /**
     * Method to click Share with Facebook
     */
    public void clickShareWithFacebook()
    {
        shareFacebook.click();
    }

    /**
     * Method to switch to facebook, twitter, google+ page
     */
    public void switchToSecondWindow()
    {
        for (String winHandle : browser.getWindowHandles())
        {
            browser.switchTo().window(winHandle);
        }
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
        return browser.findElement(By.xpath("//div[@class='mbs _6m6 _2cnj _5s6c']")).isDisplayed();
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
        String sharedLink = shareGooglePlusLink.getAttribute("href").toString();
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
        return loginButtonOnSharedFilePage.isDisplayed();

    }

    /**
     * Method to check if Document Details button is displayed on Shared File page
     */

    public boolean isDocumentDetailsButtonOnSharedFilePageDisplayed()

    {
        browser.waitUntilElementVisible(documentDetailsButtonOnSharedFilePage);
        return documentDetailsButtonOnSharedFilePage.isDisplayed();

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
        return sharedUrl.isDisplayed();

    }

}