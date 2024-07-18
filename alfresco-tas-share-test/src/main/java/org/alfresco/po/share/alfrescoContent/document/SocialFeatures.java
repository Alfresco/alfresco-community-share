package org.alfresco.po.share.alfrescoContent.document;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class SocialFeatures extends DocumentLibraryPage
{
    //@Autowired
    private DocumentDetailsPage documentDetailsPage;

    public final By facebookHomeLink = By.id("homelink");
    public final By googlePlusEmailField = By.id("Email");
    public final By quickShareWindow = By.cssSelector("div.yuimenu.quickshare-action-menu.yui-module.yui-overlay.visible");
    public final By enabledLikeButton = By.cssSelector("a[class ='like-action enabled']");
    protected String user = "alfresco.cloud@gmail.com";
    protected String password = "alfresco123!";
    protected String gEmail = "test.alfresco5@gmail.com";
    protected String gPassword = "Ness2015*";
    private final By commentContent = By.cssSelector("[class=comment-content]");
    public final By likeButton = By.cssSelector("a.like-action");
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
    private By publicLink_InputField = By.cssSelector(".section input[id*='input']");
    private By enabled_ShareButton = By.xpath("//a[@class='quickshare-action enabled']");

    public SocialFeatures(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLikeButtonMessage(String fileName)
    {
        return selectDocumentLibraryItemRow(fileName).findElement(likeButton).getAttribute("title");
    }

    public String getCommentButtonMessage(String fileName)
    {
        return selectDocumentLibraryItemRow(fileName).findElement(commentButton).getAttribute("title");
    }
    public String getCommentContent()
    {
        return getElementText(commentContent);
    }
    public int getNumberOfLikes(String fileName)
    {
        waitInSeconds(2);
        String likesNo = selectDocumentLibraryItemRow(fileName).findElement(likesCount).getText();
        return Integer.parseInt(likesNo);
    }

    public SocialFeatures clickLikeButton(String fileName)
    {
        selectDocumentLibraryItemRow(fileName).findElement(likeButton).click();
        waitUntilElementIsVisible(enabledLikeButton);
        return this;
    }

    public boolean isLikeButtonEnabled(String fileName)
    {
        waitInSeconds(3);
        return isElementDisplayed(selectDocumentLibraryItemRow(fileName), enabledLikeButton);
    }
    public SocialFeatures assertCommentButtonMessage(String fileName, String message)
    {
        log.info("Assert Comment button message {}");
        waitInSeconds(2);
        assertEquals(getCommentButtonMessage(fileName), message, String.format("Comment Button Message not equal %s ", message));
        return this;
    }
    public SocialFeatures assertCommentContent(String comment)
    {
        log.info("Assert Commented text {}");
        waitInSeconds(2);
        assertEquals(getCommentContent(), comment, "Comment text is not correct");
        return this;
    }

    public String getLikeButtonEnabledText(String fileName)
    {
        waitInSeconds(3);
        return selectDocumentLibraryItemRow(fileName).findElement(enabledLikeButton).getAttribute("title");
    }

    public SocialFeatures clickUnlike(String fileName)
    {
        waitUntilElementIsVisible(enabledLikeButton);
        selectDocumentLibraryItemRow(fileName).findElement(enabledLikeButton).click();
        return this;
    }

    public DocumentDetailsPage clickCommentLink(String contentName)
    {
        waitInSeconds(2);
        selectDocumentLibraryItemRow(contentName).findElement(commentLinkSelector).click();
        return new DocumentDetailsPage(webDriver);
    }

    public int getNumberOfComments(String contentName)
    {
        return Integer.parseInt(selectDocumentLibraryItemRow(contentName).findElement(commentCounterSelector).getText());
    }

    public String getShareButtonTooltip(String fileName)
    {
        return selectDocumentLibraryItemRow(fileName).findElement(shareButton).getAttribute("title");
    }

    public boolean isShareButtonDisplayed(String contentName)
    {
        return isElementDisplayed(selectDocumentLibraryItemRow(contentName), shareButton);
    }

    public void clickShareButton(String contentName)
    {
        selectDocumentLibraryItemRow(contentName).findElement(shareButton).click();
    }

    public boolean isQuickshareWindowDisplayed()
    {
        return isElementDisplayed(quickShareWindow);
    }

    public boolean isPublicLinkDisplayed()
    {
        return isElementDisplayed(publicLinkViewButton);
    }

    public void clickShareWithFacebook()
    {
        shareFacebook.click();
    }

    public String getFacebookWindowTitle()
    {
        return findElement(By.id("homelink")).getText();
    }

    public void loginFacebook()
    {
        findElement(By.id("email")).sendKeys(user);
        findElement(By.id("pass")).sendKeys(password);
        findElement(By.id("loginbutton")).click();
    }

    public boolean isShareLinkDisplayedOnFacebook()
    {
        return isElementDisplayed(By.xpath("//div[@class='mbs _6m6 _2cnj _5s6c']"));
    }

    public void clickTwitterIcon()
    {
        shareTwitter.click();
    }

    public String getTwitterPageTitle()
    {
        return findElement(By.cssSelector("h2.action-information")).getText();
    }

    public String getTwitterShareLink()
    {
        String link = findElement(By.id("status")).getText();
        link = link.substring(0, link.lastIndexOf('/'));
        return link;
    }

    public void clickGooglePlus()
    {
        shareGooglePlus.click();
    }

    public String getLinkSharedWithGooglePlus()
    {
        String sharedLink = shareGooglePlusLink.getAttribute("href");
        sharedLink = sharedLink.substring(0, sharedLink.lastIndexOf('/'));
        return sharedLink;
    }

    public void loginToGoogleAccount()
    {
        waitUntilElementIsDisplayedWithRetry(googlePlusEmailField, 3);
        googleEmail.sendKeys(gEmail);
        nextButton.click();
        waitUntilWebElementIsDisplayedWithRetry(googlePassword, 3);
        googlePassword.sendKeys(gPassword);
        signInButton.click();
    }

    public void clickUnshareButton()
    {
        unshareButton.click();
    }

    public boolean checkShareButtonAvailability()
    {
        return isElementDisplayed(shareButton);
    }

    public boolean isPublicLinkInputFieldDisplayed()
    {
        return isElementDisplayed(publicLink_InputField);
    }

    public void clickPublicLinkViewButton()
    {
        publicLinkViewButton.click();
    }

    public boolean isLoginButtonOnSharedFilePage()
    {
        waitUntilElementIsVisible(loginButtonOnSharedFilePage);
        return isElementDisplayed(loginButtonOnSharedFilePage);
    }

    public boolean isDocumentDetailsButtonOnSharedFilePageDisplayed()
    {
        waitUntilElementIsVisible(documentDetailsButtonOnSharedFilePage);
        return isElementDisplayed(documentDetailsButtonOnSharedFilePage);
    }

    public void clickLoginButtonOnSharedFilePage()
    {
        loginButtonOnSharedFilePage.click();
    }

    public DocumentDetailsPage clickDocumentDetailsButtonOnSharedFilePage()
    {
        documentDetailsButtonOnSharedFilePage.click();
        return new DocumentDetailsPage(webDriver);
    }

    public String getContentTextFromSharedFilePage()
    {
        return contentFromSharedFilePage.getText();
    }

    public boolean isShareUrlDisplayed()
    {
        waitUntilElementIsVisible(sharedUrl);
        return isElementDisplayed(sharedUrl);
    }

    public SocialFeatures assertLikeButtonMessage(String fileName, String message)
    {
        log.info("Assert like button message {}");
        waitInSeconds(2);
        assertEquals(getLikeButtonMessage(fileName), message, String.format("Like Button Message not equal %s ", message));
        return this;
    }

    public SocialFeatures assertNoOfLikesVerify(String fileName, int noOfLikes)
    {
        log.info("Verify no of likes for the file/folder {}");
        waitInSeconds(2);
        assertEquals(getNumberOfLikes(fileName), noOfLikes, String.format("Number of likes is not equals to %s ", noOfLikes));
        return this;
    }
    public SocialFeatures assertNoOfCommentsVerify(String fileName, int noOfComments)
    {
        log.info("Verify no of Comments for the file/folder {}");
        waitInSeconds(2);
        assertEquals(getNumberOfComments(fileName), noOfComments, String.format("Number of Comments is not equals to %s ", noOfComments));
        return this;
    }

    public SocialFeatures assertIsLikeButtonEnabled(String fileName)
    {
        log.info("Verify Like button is enabled for file/Folder {}");
        waitInSeconds(2);
        assertTrue(isLikeButtonEnabled(fileName), "Like button is not enabled for File/Folder");
        return this;
    }
    public SocialFeatures assertIsShareButtonAvailable()
    {
        assertTrue(isElementDisplayed(shareButton), "Share Button is not Available");
        return this;
    }
    public SocialFeatures assertIsPublicLinkInputFieldDisplayed()
    {
        assertTrue(isPublicLinkInputFieldDisplayed(), "Share Button is not Available");
        return this;
    }
    public SocialFeatures assertShareButtonEnabled(String fileName)
    {
        assertTrue(isElementDisplayed(selectDocumentLibraryItemRow(fileName), enabled_ShareButton), "Share Button is not Enabled");
        return this;
    }
}