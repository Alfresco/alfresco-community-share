package org.alfresco.po.share.alfrescoContent.document;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class GoogleDocsCommon extends SharePage<GoogleDocsCommon>
{
    @FindBy (xpath = "//img[contains(@title,'Locked by you')]")
    public WebElement lockedIcon;
    public By confirmationPopup = By.cssSelector("span.wait");
    @FindBy (xpath = "//*[contains(text(), 'OK')]")
    protected WebElement okButtonOnVersionPopup;
    @FindBy (xpath = "//*[@id='identifierId']|//*[@id='Email']")
    protected WebElement googleDocsEmail;
    @FindBy (xpath = "//*[@id='identifierNext']|//*[@id='next']")
    protected WebElement submitEmail;
    @FindBy (xpath = "//*[@name='password']|//*[@id='Passwd']")
    protected WebElement googleDocsPassword;
    @FindBy (xpath = "//*[@id='passwordNext']|//*[@id='signIn']")
    protected WebElement signInToGoogleDocsButton;
    @FindBy (xpath = "//*[contains(text(), 'Yes')]")
    protected WebElement confirmFormatUpgrade;
    @FindBy (css = ".docs-title-input")
    protected WebElement googleDocsTitle;
    @FindBy (css = ".kix-appview-editor")
    protected WebElement googleDocsContent;
    @FindBy (xpath = "//img[contains(@title,'Editing in Google Docs')]")
    protected WebElement googleDriveIcon;
    @FindBy (xpath = "//div[contains(text(), 'This document is locked by you')]")
    protected WebElement lockedDocumentMessage;
    @FindBy (xpath = "//span[contains(text(), 'Check In Google Doc™')]")
    protected WebElement checkInGoogleDoc;
    @FindBy (xpath = "//div[contains(text(), 'Version Information')]")
    protected WebElement versionInformationPopup;
    @FindBy (id = "docs-file-menu")
    protected WebElement fileLinkGoogleDocs;
    @FindBy (id = "insertLinkButton")
    protected WebElement addLinkInGoogleDoc;
    @FindBy (id = "t-insert-link")
    protected WebElement addLinkInGoogleSheet;
    @FindBy (css = "#yui-gen82-button")
    protected WebElement confirmDocumentFormatUpgradeYes;
    @FindBy (xpath = "//input[contains(@class, 'docs-link-insertlinkbubble-text jfk-textinput label-input-label')]")
    protected WebElement inputTextForLinkInGoogleDoc;
    @FindBy (xpath = "//input[contains(@class, 'docs-link-insertlinkbubble-text jfk-textinput label-input-label')]")
    protected WebElement inputTextForLinkInGoogleSheet;
    @FindBy (xpath = "//input[contains(@class, 'docs-link-urlinput-url')]")
    protected WebElement inputLinkInGoogleDoc;
    @FindBy (xpath = "//input[contains(@class, 'docs-link-urlinput-url')]")
    protected WebElement inputLinkInGoogleSheets;
    @FindBy (xpath = "//div[contains(@class, 'docs-link-insertlinkbubble-buttonbar')]/div")
    protected WebElement applyButtonGoogleDoc;
    @FindBy (xpath = "//div[contains(@class, 'docs-link-insertlinkbubble-buttonbar')]/div")
    protected WebElement applyButtonGoogleSheets;
    @FindBy (xpath = "//*[@id='profileIdentifier']|//*[@id='reauthEmail']")
    protected WebElement reauthEmail;
    protected String googleDocsLoginUrl = "https://accounts.google.com/ServiceLogin#identifier";
    protected String googleDocsTestEmail = "tsealfresco123@gmail.com";
    protected String googleDocsTestPassword = "NessPassword1!";
    //@Autowired
    DocumentLibraryPage documentLibraryPage;
    @FindBy (id = "prompt_h")
    private WebElement promptAuthorizeWithGoogleDocs;
    @FindBy (xpath = "//div[@id ='prompt_c']//span[@class ='yui-button yui-push-button alf-primary-button']")
    private WebElement okButtonAuthorizeWithGoogleDocsPopup;

    /*
    //old credentials that don't work anymore
    protected String googleDocsTestEmail = "test.alfresco5@gmail.com";
    protected String googleDocsTestPassword = "Ness2015*";
    */

    public void clickOkButton()
    {
        Parameter.checkIsMandotary("OK button on version popup", okButtonOnVersionPopup);
        okButtonOnVersionPopup.click();
        browser.waitInSeconds(10);
    }

    public void loginToGoogleDocs()
    {
        String currentWindow = browser.getCurrentUrl();
        browser.get(googleDocsLoginUrl);

        if (browser.isElementDisplayed(reauthEmail))
        {
            browser.waitUntilElementVisible(googleDocsPassword).sendKeys(googleDocsTestPassword);
            browser.waitUntilElementClickable(signInToGoogleDocsButton).click();
        } else
        {
            browser.waitUntilElementVisible(googleDocsEmail).sendKeys(googleDocsTestEmail);
            browser.waitUntilElementClickable(submitEmail).click();
            browser.waitUntilElementVisible(googleDocsPassword).sendKeys(googleDocsTestPassword);
            browser.waitUntilElementClickable(signInToGoogleDocsButton).click();
        }
        browser.waitInSeconds(1);
        browser.get(currentWindow);
    }

    public void confirmFormatUpgrade()
    {
        Parameter.checkIsMandotary("Yes button on format upgrade popup", confirmFormatUpgrade);
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

    public void confirmDocumentFormatUpgradeYes()
    {
        getBrowser().waitUntilElementClickable(confirmDocumentFormatUpgradeYes).click();
    }

    public void editGoogleDocsContent(String content)
    {
//        String js = "arguments[0].style.height='auto'; arguments[0].style.visibility='visible';";
//        browser.executeScript(js, addLinkInGoogleDoc);
        browser.waitUntilWebElementIsDisplayedWithRetry(addLinkInGoogleDoc, 7);
        addLinkInGoogleDoc.click();
        browser.waitUntilElementVisible(inputTextForLinkInGoogleDoc).sendKeys(content);
        browser.waitUntilElementVisible(inputLinkInGoogleDoc).sendKeys("test");
        browser.waitUntilElementVisible(inputLinkInGoogleDoc).sendKeys(Keys.ENTER);
        //   browser.waitUntilElementClickable(applyButtonGoogleDoc).click();

        browser.waitInSeconds(3);
    }

    public void editGoogleSheetsContent(String content)
    {
//        String js = "arguments[0].style.height='auto'; arguments[0].style.visibility='visible';";
//        browser.executeScript(js, addLinkInGoogleSheet);
        browser.waitUntilWebElementIsDisplayedWithRetry(addLinkInGoogleSheet, 7);
        addLinkInGoogleSheet.click();
        browser.waitUntilElementVisible(inputTextForLinkInGoogleSheet).sendKeys(content);
        browser.waitUntilElementVisible(inputLinkInGoogleSheets).sendKeys("test");
        browser.waitUntilElementVisible(inputLinkInGoogleDoc).sendKeys(Keys.ENTER);

        //     browser.waitUntilElementClickable(applyButtonGoogleSheets).click();
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

    public GoogleDocsCommon checkInGoogleDoc(String file)
    {
        String fileLocator = "//a[contains(text(), '" + file + "')]";

        browser.waitUntilElementVisible(By.xpath(fileLocator));
        WebElement fileLink = browser.findElement(By.xpath(fileLocator));

        browser.mouseOver(fileLink);
        checkInGoogleDoc.click();

        browser.waitUntilWebElementIsDisplayedWithRetry(fileLink);
        return this;
    }

    public boolean isVersionInformationPopupDisplayed() throws Exception
    {
        return browser.isElementDisplayed(versionInformationPopup);
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

        } catch (NoSuchElementException e)
        {

        } finally
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
        return browser.isElementDisplayed(By.xpath("//button[contains(text(),'OK')]"));
    }

    public String getConfirmationPopUpMessage()
    {
        return browser.findElement(confirmationPopup).getText();
    }

}
