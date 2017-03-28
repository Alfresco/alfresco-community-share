package org.alfresco.po.share.alfrescoContent.document;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class GoogleDocsCommon extends SharePage<GoogleDocsCommon>
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @FindBy(id = "prompt_h")
    private WebElement promptAuthorizeWithGoogleDocs;

    @FindBy(xpath = "//div[@id ='prompt_c']//span[@class ='yui-button yui-push-button alf-primary-button']")
    private WebElement okButtonAuthorizeWithGoogleDocsPopup;

    @FindBy(xpath = "//span[contains(text(), 'Edit in Google Docs™')]")
    protected WebElement editInGoogleDocs;

    @FindBy(xpath = "//*[contains(text(), 'OK')]")
    protected WebElement okButtonOnVersionPopup;

    @FindBy(xpath = ".//*[@id='Email']")
    protected WebElement googleDocsEmail;

    @FindBy(xpath = ".//*[@id='next']")
    protected WebElement submitEmail;

    @FindBy(xpath = ".//*[@id='Passwd']")
    protected WebElement googleDocsPassword;

    @FindBy(xpath = ".//*[@id='signIn']")
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

    @FindBy(xpath = ".//*[@id='docs-file-menu']")
    protected WebElement fileLinkGoogleDocs;

    @FindBy(xpath = "//div[@id='insertLinkButton']")
    protected WebElement addLinkInGoogleDoc;

    @FindBy(xpath = "//div[@id='t-insert-link']")
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

    @FindBy(xpath = ".//*[@id='reauthEmail']")
    protected WebElement reauthEmail;

    public By confirmationPopup = By.cssSelector("span.wait");
    protected String googleDocsLoginUrl = "https://accounts.google.com/ServiceLogin#identifier";
    protected String googleDocsTestEmail = "test.alfresco5@gmail.com";
    protected String googleDocsTestPassword = "Ness2015*";

    public void editInGoogleDocs()
    {
        browser.waitUntilElementVisible(editInGoogleDocs).click();
    }

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
            googleDocsPassword.sendKeys(googleDocsTestPassword);
            signInToGoogleDocsButton.click();
        }

        else
        {
            googleDocsEmail.sendKeys(googleDocsTestEmail);
            submitEmail.click();
            googleDocsPassword.sendKeys(googleDocsTestPassword);
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
        String currentWindow = browser.getWindowHandle();

        for (String winHandle : browser.getWindowHandles())
        {
            browser.switchTo().window(winHandle);
        }

        changeGoogleDocsTitle(title);
        editGoogleDocsContent(content);
        browser.close();
        browser.switchTo().window(currentWindow);
    }

    public void switchToGoogleSheetsWindowandAndEditContent(String title, String content)
    {
        String currentWindow = browser.getWindowHandle();

        for (String winHandle : browser.getWindowHandles())
        {
            browser.switchTo().window(winHandle);
        }

        changeGoogleDocsTitle(title);
        editGoogleSheetsContent(content);
        browser.close();
        browser.switchTo().window(currentWindow);
    }

    public void switchToGooglePresentationsAndEditContent(String title)
    {
        String currentWindow = browser.getWindowHandle();

        for (String winHandle : browser.getWindowHandles())
        {
            browser.switchTo().window(winHandle);
        }

        changeGoogleDocsTitle(title);
        browser.close();
        browser.switchTo().window(currentWindow);

    }

    public void editGoogleDocsContent(String content)
    {
        int counter = 1;
        int retryRefreshCount = 5;
        while (counter <= retryRefreshCount)
        {
            try
            {     
                String js = "arguments[0].style.height='auto'; arguments[0].style.visibility='visible';";
                ((JavascriptExecutor) browser).executeScript(js, addLinkInGoogleDoc);
                addLinkInGoogleDoc.click();                
                browser.waitInSeconds(4);
                if (inputTextForLinkInGoogleDoc.isDisplayed() && inputLinkInGoogleDoc.isDisplayed())
                {
                    inputTextForLinkInGoogleDoc.sendKeys(content);
                    browser.waitInSeconds(2);
                    inputLinkInGoogleDoc.sendKeys("test");
                    break;
                }
            }
            catch (NoSuchElementException e)
            {
                LOG.info("Wait for element after refresh: " + counter);
                browser.refresh();
                counter++;
            }
        }
        applyButtonGoogleDoc.click();
        browser.waitInSeconds(4);
    }

    public void editGoogleSheetsContent(String content)
    {
        int counter = 1;
        int retryRefreshCount = 5;
        while (counter <= retryRefreshCount)
        {
            try
            {                          
                String js = "arguments[0].style.height='auto'; arguments[0].style.visibility='visible';";
                ((JavascriptExecutor) browser).executeScript(js, addLinkInGoogleSheet);
                addLinkInGoogleSheet.click();
                browser.waitInSeconds(4);
                if (inputLinkInGoogleSheets.isDisplayed() && inputLinkInGoogleSheets.isDisplayed())
                {
                    inputTextForLinkInGoogleSheet.sendKeys(content);
                    browser.waitInSeconds(2);
                    inputLinkInGoogleSheets.sendKeys("test");
                    break;
                }
            }
            catch (NoSuchElementException e)
            {
                LOG.info("Wait for element after refresh: " + counter);
                browser.refresh();
                counter++;
            }
        }
        applyButtonGoogleSheets.click();
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

        return lockedIcon.isDisplayed();
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

    public boolean checkLockedLAbelIsDisplayed() throws Exception
    {
        try
        {
            browser.findElement(By.xpath("//div[contains(text(), 'This document is locked by you')]"));
            return true;

        }
        catch (NoSuchElementException e)
        {

            return false;
        }

    }

    /**
     * Verify if 'Google Drive icon is displayed for a file checked out in Google Docs
     *
     * @return True if the Google Drive icon is displayed, else false.
     */

    public boolean isGoogleDriveIconDisplayed()

    {

        return googleDriveIcon.isDisplayed();
    }

    public boolean checkGoogleDriveIconIsDisplayed() throws Exception
    {
        try
        {
            browser.findElement(By.xpath("//img[contains(@title,'status.googledrive')]')]"));
            return true;

        }
        catch (NoSuchElementException e)
        {

            return false;
        }

    }

    public void checkInGoogleDoc(String file)

    {
        String fileLocator = "//a[contains(text(), '" + file + "')]";

        WebElement fileLink = browser.findElement(By.xpath(fileLocator));

        browser.mouseOver(fileLink);
        checkInGoogleDoc.click();
        browser.waitInSeconds(10);
    }

    public boolean isDocumentNameUpdated(String updatedName)

    {

        String fileName = "//a[contains(text(), '" + updatedName + "')]";

        WebElement fileNameLink = browser.findElement(By.xpath(fileName));

        return fileNameLink.isDisplayed();

    }

    public DocumentDetailsPage clickOnUpdatedName(String updatedName)

    {
        String docName = "//a[contains(text(), '" + updatedName + "')]";

        WebElement docNameLink = browser.findElement(By.xpath(docName));

        docNameLink.click();

        return (DocumentDetailsPage) documentDetailsPage.renderedPage();

    }

    public boolean isVersionInformationPopupDisplayed() throws Exception
    {
        try
        {
            browser.findElement(By.xpath("//div[contains(text(), 'Version Information')]"));
            return true;

        }
        catch (NoSuchElementException e)
        {

            return false;
        }

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
        return promptAuthorizeWithGoogleDocs.isDisplayed();
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
