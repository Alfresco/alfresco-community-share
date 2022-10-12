package org.alfresco.po.share.alfrescoContent.document;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.WebElementInteraction;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

@Slf4j
public class GoogleDocsLogIn extends WebElementInteraction
{
    protected String googleDocsLoginUrl = "https://accounts.google.com/ServiceLogin#identifier";


    private final By reauthEmail = By.xpath("//*[@id='profileIdentifier']|//*[@id='reauthEmail']");

    private final By googleDocagainEmailId = By.xpath("//input[@aria-label='Email Address']");
    private final By googleDocsPassword = By.xpath("//*[@name='password']|//*[@name='Passwd']");
    private final By googleDocEmailId =By.xpath("//*[@id='identifierId']|//*[@id='Email']");


    private final By useAnothorAc = By.xpath("//div[text()='Use another account']");
    private final By okButtonPopUp = By.xpath("//*[contains(text(), 'OK')]");

    private final By lockedIcon = By.xpath("//img[contains(@title,'Locked by you')]");
    private final By lockedDocumentMessage = By.xpath("//div[contains(text(), 'This document is locked by you')]");

    private final By googleDriveIcon = By.xpath("//img[contains(@title,'Editing in Google Docs')]");

    private final By checkInGoogleDoc = By.xpath("//span[contains(text(), 'Check In Google Doc™')]");
    private final By googleDocsTitle = By.cssSelector(".docs-title-input");

    private final By addLinkInGoogleDoc = By.id("insertLinkButton");

    private final By inputTextForLinkInGoogleDoc = By.xpath("//input[contains(@class, 'docs-link-smartinsertlinkbubble-text jfk-textinput')]");

    private final By inputLinkInGoogleDoc = By.xpath("//input[contains(@class, 'docs-link-searchinput-search')]");

    private final By submitEmails = By.xpath("//*[@id='identifierNext']|//*[@id='next']");
    private final By nextButton = By.xpath("//div[@id='login-parent']//div/button[text()='Next']");
    private final By passwordNextButton = By.xpath("//div[@id='passwordNext']//button");

    private final By signInToGoogleDocsButton = By.xpath("//*[@id='passwordNext']|//*[@id='signIn']");
    private final By logInToGoogleDocsButton = By.xpath("//button[text()='Log in']");
    private final By continueGoogleDocsButton = By.xpath("//div[@data-primary-action-label='Continue']//div[@jsname='k77Iif']//button");
    protected String googleDocsTestEmail = "alfrescotest65@gmail.com";
    protected String googleDocsTestPassword = "Alfresco@65";
    public GoogleDocsLogIn(ThreadLocal<WebDriver> webDriver) {
        super(webDriver);
    }



    public void clickOkButton()
    {
        Parameter.checkIsMandotary("OK button on version popup", okButtonPopUp);
        waitUntilElementIsVisible(okButtonPopUp).click();
    }
    public void changeGoogleDocsTitle(String newGoogleDocsTitle)
    {
        waitUntilElementIsDisplayedWithRetry(By.cssSelector(".docs-title-input"));
        waitUntilElementIsVisible(googleDocsTitle).clear();
        waitUntilElementIsVisible(googleDocsTitle).sendKeys(newGoogleDocsTitle);
        waitUntilElementIsVisible(googleDocsTitle).sendKeys(Keys.ENTER);
    }
    public void editGoogleDocsContent(String content)
    {
        waitUntilElementIsDisplayedWithRetry(By.id("insertLinkButton"));
        waitUntilElementIsVisible(addLinkInGoogleDoc).click();
        waitUntilElementIsVisible(inputTextForLinkInGoogleDoc).sendKeys(content);
        waitUntilElementIsVisible(inputLinkInGoogleDoc).sendKeys("test");
        waitUntilElementIsVisible(inputLinkInGoogleDoc).sendKeys(Keys.ENTER);
        waitInSeconds(5);
    }
    public void switchToGoogleDocsWindowandAndEditContent(String title, String content)
    {
        waitInSeconds(20);
        switchWindow(1);
        waitInSeconds(8);
        changeGoogleDocsTitle(title);
        editGoogleDocsContent(content);
        closeWindowAndSwitchBack();
    }
    public void loginToGoogleDocs() {
        log.info("Navigate to Login Page");

        String currentWindow = webDriver.get().getCurrentUrl();

        webDriver.get().get(googleDocsLoginUrl);
        waitInSeconds(5);

        if(isElementDisplayed(googleDocEmailId)) {
        waitUntilElementIsVisible(googleDocEmailId).sendKeys(googleDocsTestEmail);
        waitUntilElementIsVisible(submitEmails).click();
        waitInSeconds(5);
            if(isElementDisplayed(googleDocagainEmailId)) {
                waitUntilElementIsVisible(googleDocagainEmailId).sendKeys(googleDocsTestEmail);
                waitUntilElementIsVisible(nextButton).click();
                waitInSeconds(5);
                waitUntilElementIsVisible(googleDocsPassword).sendKeys(googleDocsTestPassword);
                waitUntilElementIsVisible(logInToGoogleDocsButton).click();
                waitInSeconds(10);
                waitUntilElementIsVisible(continueGoogleDocsButton).click();
                waitInSeconds(5);

            }else
            {
                waitUntilElementIsVisible(googleDocsPassword).sendKeys(googleDocsTestPassword);
                waitUntilElementIsVisible(passwordNextButton).click();
                waitInSeconds(10);
            }
        }

        waitInSeconds(2);
        webDriver.get().get(currentWindow);

    }



    public boolean isLockedIconDisplayed()
    {
        return isElementDisplayed(lockedIcon);
    }

    public boolean isLockedDocumentMessageDisplayed()
    {
        return isElementDisplayed(lockedDocumentMessage);
    }
    public boolean isGoogleDriveIconDisplayed()
    {
        return isElementDisplayed(googleDriveIcon);
    }
    public void assertisLockedIconDisplayed()
    {
        Assert.assertTrue(isLockedIconDisplayed(), "Locked Icon is not displayed");
    }
    public void assertisLockedDocumentMessageDisplayed()
    {
        Assert.assertTrue(isLockedDocumentMessageDisplayed(), "Locked Icon is not displayed");
    }
    public void assertisGoogleDriveIconDisplayed()
    {
        Assert.assertTrue(isGoogleDriveIconDisplayed(), "Locked Icon is not displayed");
    }

    public void checkInGoogleDoc(String file)
    {
        String fileLocator = "//a[contains(text(), '" + file + "')]";

        waitUntilElementIsVisible(By.xpath(fileLocator));
        WebElement fileLink = findElement(By.xpath(fileLocator));

        mouseOver(fileLink);
        waitUntilElementIsVisible(checkInGoogleDoc).click();

        waitUntilWebElementIsDisplayedWithRetry(fileLink);

    }


}
