package org.alfresco.po.share.alfrescoContent.document;

import org.alfresco.po.share.site.DocumentLibraryPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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

    public int getNumberOfLikes(String fileName)
    {
        String likesNo = selectDocumentLibraryItemRow(fileName).findElement(likesCount).getText();
        return Integer.parseInt(likesNo);
    }

    public void clickLikeButton(String fileName)
    {
        selectDocumentLibraryItemRow(fileName).findElement(likeButton).click();
        webElementInteraction.waitUntilElementIsVisible(enabledLikeButton);
    }

    public boolean isLikeButtonEnabled(String fileName)
    {
        return webElementInteraction.isElementDisplayed(selectDocumentLibraryItemRow(fileName), enabledLikeButton);
    }

    public String getLikeButtonEnabledText(String fileName)
    {
        return selectDocumentLibraryItemRow(fileName).findElement(enabledLikeButton).getAttribute("title");
    }

    public void clickUnlike(String fileName)
    {
        selectDocumentLibraryItemRow(fileName).findElement(enabledLikeButton).click();
    }

    public DocumentDetailsPage clickCommentLink(String contentName)
    {
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
        return webElementInteraction.isElementDisplayed(selectDocumentLibraryItemRow(contentName), shareButton);
    }

    public void clickShareButton(String contentName)
    {
        selectDocumentLibraryItemRow(contentName).findElement(shareButton).click();
    }

    public boolean isQuickshareWindowDisplayed()
    {
        return webElementInteraction.isElementDisplayed(quickShareWindow);
    }

    public boolean isPublicLinkDisplayed()
    {
        return webElementInteraction.isElementDisplayed(publicLinkViewButton);
    }

    public void clickShareWithFacebook()
    {
        shareFacebook.click();
    }

    public String getFacebookWindowTitle()
    {
        return webElementInteraction.findElement(By.id("homelink")).getText();
    }

    public void loginFacebook()
    {
        webElementInteraction.findElement(By.id("email")).sendKeys(user);
        webElementInteraction.findElement(By.id("pass")).sendKeys(password);
        webElementInteraction.findElement(By.id("loginbutton")).click();
    }

    public boolean isShareLinkDisplayedOnFacebook()
    {
        return webElementInteraction.isElementDisplayed(By.xpath("//div[@class='mbs _6m6 _2cnj _5s6c']"));
    }

    public void clickTwitterIcon()
    {
        shareTwitter.click();
    }

    public String getTwitterPageTitle()
    {
        return webElementInteraction.findElement(By.cssSelector("h2.action-information")).getText();
    }

    public String getTwitterShareLink()
    {
        String link = webElementInteraction.findElement(By.id("status")).getText();
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
        webElementInteraction.waitUntilElementIsDisplayedWithRetry(googlePlusEmailField, 3);
        googleEmail.sendKeys(gEmail);
        nextButton.click();
        webElementInteraction.waitUntilWebElementIsDisplayedWithRetry(googlePassword, 3);
        googlePassword.sendKeys(gPassword);
        signInButton.click();
    }

    public void clickUnshareButton()
    {
        unshareButton.click();
    }

    public boolean checkShareButtonAvailability()
    {
        return webElementInteraction.isElementDisplayed(shareButton);
    }

    public boolean isPublicLinkInputFieldDisplayed()
    {
        return webElementInteraction.isElementDisplayed(publicLinkInputField);
    }

    public void clickPublicLinkViewButton()
    {
        publicLinkViewButton.click();
    }

    public boolean isLoginButtonOnSharedFilePage()
    {
        webElementInteraction.waitUntilElementIsVisible(loginButtonOnSharedFilePage);
        return webElementInteraction.isElementDisplayed(loginButtonOnSharedFilePage);
    }

    public boolean isDocumentDetailsButtonOnSharedFilePageDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(documentDetailsButtonOnSharedFilePage);
        return webElementInteraction.isElementDisplayed(documentDetailsButtonOnSharedFilePage);
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
        webElementInteraction.waitUntilElementIsVisible(sharedUrl);
        return webElementInteraction.isElementDisplayed(sharedUrl);
    }
}