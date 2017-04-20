package org.alfresco.po.share.alfrescoContent.document;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

@PageObject
public class GoogleDocsCommon extends SharePage<GoogleDocsCommon>
{
    @FindBy(id = "prompt_h")
    private WebElement promptAuthorizeWithGoogleDocs;

    @FindBy(xpath = "//div[@id ='prompt_c']//span[@class ='yui-button yui-push-button alf-primary-button']")
    private WebElement okButtonAuthorizeWithGoogleDocsPopup;

    @FindBy(xpath = "//*[contains(text(), 'OK')]")
    protected WebElement okButtonOnVersionPopup;

    @FindBy(id = "Email")
    protected WebElement googleDocsEmail;

    @FindBy(id = "next")
    protected WebElement submitEmail;

    @FindBy(id = "Passwd")
    protected WebElement googleDocsPassword;

    @FindBy(id = "signIn")
    protected WebElement signInToGoogleDocsButton;

    @FindBy(xpath = "//*[contains(text(), 'Yes')]")
    protected WebElement confirmFormatUpgrade;

    @FindBy(css = ".docs-title-input")
    protected WebElement googleDocsTitle;

    @FindBy(css = ".kix-appview-editor")
    protected WebElement googleDocsContent;

    @FindBy(xpath = "//img[contains(@title,'Locked by you')]")
    public WebElement lockedIcon;

    @FindBy(xpath = "//img[contains(@title,'status.googledrive')]")
    protected WebElement googleDriveIcon;

    @FindBy(xpath = "//div[contains(text(), 'This document is locked by you')]")
    protected WebElement lockedDocumentMessage;

    @FindBy(xpath = "//span[contains(text(), 'Check In Google Doc™')]")
    protected WebElement checkInGoogleDoc;

    @FindBy(xpath = "//div[contains(text(), 'Version Information')]")
    protected WebElement versionInformationPopup;

    @FindBy(id = "docs-file-menu")
    protected WebElement fileLinkGoogleDocs;

    @FindBy(id = "insertLinkButton")
    protected WebElement addLinkInGoogleDoc;

    @FindBy(id = "t-insert-link")
    protected WebElement addLinkInGoogleSheet;

    @FindBy(xpath = "//input[contains(@class, 'docs-link-insertlinkbubble-text jfk-textinput label-input-label')]")
    protected WebElement inputTextForLinkInGoogleDoc;

    @FindBy(xpath = "//input[contains(@class, 'docs-link-insertlinkbubble-text jfk-textinput label-input-label')]")
    protected WebElement inputTextForLinkInGoogleSheet;

    @FindBy(xpath = "//input[contains(@class, 'docs-link-urlinput-url')]")
    protected WebElement inputLinkInGoogleDoc;

    @FindBy(xpath = "//input[contains(@class, 'docs-link-urlinput-url')]")
    protected WebElement inputLinkInGoogleSheets;

    @FindBy(xpath = "//div[contains(@class, 'docs-link-insertlinkbubble-buttonbar')]/div")
    protected WebElement applyButtonGoogleDoc;

    @FindBy(xpath = "//div[contains(@class, 'docs-link-insertlinkbubble-buttonbar')]/div")
    protected WebElement applyButtonGoogleSheets;

    @FindBy(id = "reauthEmail")
    protected WebElement reauthEmail;

    public By confirmationPopup = By.cssSelector("span.wait");
    protected String googleDocsLoginUrl = "https://accounts.google.com/ServiceLogin#identifier";
    protected String googleDocsTestEmail = "test.alfresco5@gmail.com";
    protected String googleDocsTestPassword = "Ness2015*";

    public void clickOkButton()
    {
        if (browser.isElementDisplayed(okButtonOnVersionPopup))
        {
            okButtonOnVersionPopup.click();
            browser.waitInSeconds(10);
        }
    }

    public void loginToGoogleDocs()
    {
        String currentWindow = browser.getCurrentUrl();

        browser.get(googleDocsLoginUrl);

        if (isReauthEmailDisplayed())
        {
            browser.waitUntilElementVisible(googleDocsPassword).sendKeys(googleDocsTestPassword);
            signInToGoogleDocsButton.click();
        }

        else
        {
            googleDocsEmail.sendKeys(googleDocsTestEmail);
            submitEmail.click();
            browser.waitUntilElementVisible(googleDocsPassword).sendKeys(googleDocsTestPassword);
            signInToGoogleDocsButton.click();
        }
        browser.waitInSeconds(1);
        browser.get(currentWindow);

    }

    public void confirmFormatUpgrade()
    {
        if (browser.isElementDisplayed(confirmFormatUpgrade))
            confirmFormatUpgrade.click();
    }

    public void switchToGoogleDocsWindowandAndEditContent(String title, String content)
    {
        browser.switchWindow(1);
        changeGoogleDocsTitle(title);
        editGoogleDocsContent(content);
        browser.closeWindowAndSwitchBack();
    }

    public void switchToGoogleSheetsWindowandAndEditContent(String title, String content)
    {
        browser.switchWindow(1);
        changeGoogleDocsTitle(title);
        editGoogleSheetsContent(content);
        browser.closeWindowAndSwitchBack();
    }

    public void switchToGooglePresentationsAndEditContent(String title)
    {
        browser.switchWindow(1);
        changeGoogleDocsTitle(title);
        browser.closeWindowAndSwitchBack();

    }

    public void editGoogleDocsContent(String content)
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(addLinkInGoogleDoc, 5);
        addLinkInGoogleDoc.click();
        browser.waitUntilElementVisible(inputTextForLinkInGoogleDoc).sendKeys(content);
        browser.waitUntilElementVisible(inputLinkInGoogleDoc).sendKeys("test");
        browser.waitUntilElementClickable(applyButtonGoogleDoc).click();
        browser.waitInSeconds(3);
    }

    public void editGoogleSheetsContent(String content)
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(addLinkInGoogleSheet, 5);
        addLinkInGoogleSheet.click();
        browser.waitUntilElementVisible(inputTextForLinkInGoogleSheet).sendKeys(content);
        browser.waitUntilElementVisible(inputLinkInGoogleSheets).sendKeys("test");
        browser.waitUntilElementClickable(applyButtonGoogleSheets).click();
        browser.waitInSeconds(3);
    }

    public void changeGoogleDocsTitle(String newGoogleDocsTitle)
    {
        browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector(".docs-title-input"));
        googleDocsTitle.clear();
        googleDocsTitle.sendKeys(newGoogleDocsTitle);
        googleDocsTitle.sendKeys(Keys.ENTER);
    }

    /**
     * Verify if Locked icon is displayed for a file checked out in Google Docs
     *
     * @return True if Locked icon is displayed, else false.
     */

    public boolean isLockedIconDisplayed()
    {
        return browser.isElementDisplayed(lockedIcon);
    }

    /**
     * Verify if 'This document is locked by you' message is displayed for a file checked out in Google Docs
     *
     * @return True if the message is displayed, else false.
     */

    public boolean isLockedDocumentMessageDisplayed()
    {
        return lockedDocumentMessage.isDisplayed();
    }

    /**
     * Verify if 'Google Drive icon is displayed for a file checked out in Google Docs
     *
     * @return True if the Google Drive icon is displayed, else false.
     */

    public boolean isGoogleDriveIconDisplayed()
    {
        return browser.isElementDisplayed(googleDriveIcon);
    }

    public boolean checkGoogleDriveIconIsDisplayed() throws Exception
    {
        return browser.isElementDisplayed(By.xpath("//img[contains(@title,'status.googledrive')]')]"));
    }

    public void checkInGoogleDoc(String file)
    {
        String fileLocator = "//a[contains(text(), '" + file + "')]";

        WebElement fileLink = browser.findElement(By.xpath(fileLocator));

        browser.mouseOver(fileLink);
        checkInGoogleDoc.click();
        browser.waitInSeconds(10);
    }

    public boolean isVersionInformationPopupDisplayed() throws Exception
    {
        return browser.isElementDisplayed(By.xpath("//div[contains(text(), 'Version Information')]"));
    }

    @Override
    public String getRelativePath()
    {
        return null;
    }

    /**
     * Method to check that the Authorize with Google Docs is displayed
     */

    public boolean isAuthorizeWithGoogleDocsDisplayed()
    {
        return browser.isElementDisplayed(promptAuthorizeWithGoogleDocs);
    }

    /**
     * Method to click OK on the Authorize with Google Docs popup (if popup is displayed)
     * 
     * @throws Exception
     */

    public void clickOkButtonOnTheAuthPopup() throws Exception
    {
        try
        {
            if (isAuthorizeWithGoogleDocsDisplayed())

                okButtonAuthorizeWithGoogleDocsPopup.click();

        }
        catch (NoSuchElementException e)
        {

        }
        finally
        {
            browser.waitInSeconds(10);
        }
    }

    /**
     * Method to check if 'OK' button is displayed on the 'Authorize with Google Docs popup'
     * 
     * @throws Exception
     */

    public boolean isOkButtonOnTheAuthorizeWithGoogleDocsPopupDisplayed() throws Exception
    {
        try
        {
            browser.findElement(By.xpath("//button[contains(text(),'OK')]"));
            return true;

        }
        catch (NoSuchElementException e)
        {
            return false;
        }

    }

    public boolean isReauthEmailDisplayed()
    {
        return browser.isElementDisplayed(reauthEmail);
    }

    public String getConfirmationPopUpMessage()
    {
        return browser.findElement(confirmationPopup).getText();
    }

}
