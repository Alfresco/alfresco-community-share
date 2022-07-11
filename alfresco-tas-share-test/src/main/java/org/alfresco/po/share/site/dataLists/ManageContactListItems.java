package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class ManageContactListItems extends ShareDialog
{
    @FindBy (css = "button[id*='newRowButton-button']")
    protected WebElement newItemButton;

    @FindBy (css = "input[id*='FirstName']")
    protected WebElement firstName;

    @FindBy (css = "input[id*='LastName']")
    protected WebElement lastName;

    @FindBy (css = "input[id*='Email']")
    protected WebElement email;

    @FindBy (css = "input[id*='Company']")
    protected WebElement company;

    @FindBy (css = "input[id*='Job']")
    protected WebElement job;

    @FindBy (css = "input[id*='PhoneOffice']")
    protected WebElement phoneOffice;

    @FindBy (css = "input[id*='PhoneMobile']")
    protected WebElement phoneMobile;

    @FindBy (css = "textarea[id*='contactNotes']")
    protected WebElement notes;

    @FindBy (css = "button[id*='submit']")
    protected WebElement save;

    @FindBy (css = "td[class*='col-actions'] div")
    protected WebElement actions;

    @FindBy (css = "a[title='Edit']")
    protected WebElement editAction;

    @FindBy (css = "a[title='Duplicate']")
    protected WebElement duplicateAction;

    @FindBy (css = "a[title='Delete']")
    protected WebElement deleteAction;

    @FindBy (xpath = "//button[text()='Delete']")
    protected WebElement confirmDeleteAction;

    @FindBy (xpath = "//button[text()='Cancel']")
    protected WebElement cancelDeleteAction;

    @FindBy (xpath = "//div[text()='Delete Item']")
    protected WebElement deletePopup;

    @FindBy (css = "input[name*='fileChecked']")
    protected WebElement fileChecked;

    @FindBy (css = "div[id*='createRow-dialogTitle']")
    protected WebElement createNewItem;

    @FindBy (css = "div[id*='editDetails-dialogTitle']")
    protected WebElement editItem;

    @FindBy (xpath = "//div[text()='No list items']")
    protected WebElement noListItems;

    protected String dataListLink = "a[title*='";

    /**
     * Method to send input to the First Name field
     *
     * @param newFirstName
     */
    public void fillInCreateItemForm(String newFirstName, String newLastName, String newEmail, String newCompany, String newJob, String newPhoneOffice, String newPhoneMobile, String newNotes)
    {

        firstName.sendKeys(newFirstName);
        lastName.sendKeys(newLastName);
        email.sendKeys(newEmail);
        company.sendKeys(newCompany);
        job.sendKeys(newJob);
        phoneOffice.sendKeys(newPhoneOffice);
        phoneMobile.sendKeys(newPhoneMobile);
        notes.sendKeys(newNotes);

    }

    public void clearItemDetails()
    {
        firstName.clear();
        lastName.clear();
        email.clear();
        company.clear();
        job.clear();
        phoneOffice.clear();
        phoneMobile.clear();
        notes.clear();
    }

    public void clickOnListLink(String dataListTitle)
    {
        String newDataListLink = dataListLink + dataListTitle + "']";
        String dataList = StringUtils.deleteWhitespace(newDataListLink);
        browser.findElement(By.cssSelector(dataList)).click();
        browser.waitInSeconds(3);
    }

    public void createNewContactItem(String newContactFirstName, String newContactLastName, String newContactEmail, String newContactCompany, String newContactJob, String newContactPhoneOffice, String newContactPhoneMobile, String newContactNotes)
    {
        browser.waitUntilElementVisible(By.cssSelector("button[id*='newRowButton-button']"));
        newItemButton.click();
        fillInCreateItemForm(newContactFirstName, newContactLastName, newContactEmail, newContactCompany, newContactJob, newContactPhoneOffice,
            newContactPhoneMobile, newContactNotes);
        save.click();

    }

    public void editContactItem(String editedFirstName, String editedLastName, String editedEmail, String editedCompany, String editedJob, String editedPhoneOffice, String editedPhoneMobile, String editedNotes)
    {
        clearItemDetails();
        fillInCreateItemForm(editedFirstName, editedLastName, editedEmail, editedCompany, editedJob, editedPhoneOffice, editedPhoneMobile, editedNotes);
        save.click();
    }

    public void clickEditAction()
    {
        browser.mouseOver(actions);
        editAction.click();
    }

    public void clickDeleteAction()
    {
        browser.mouseOver(actions);
        deleteAction.click();
    }

    public void clickDuplicateAction()
    {
        browser.mouseOver(actions);
        duplicateAction.click();
    }

    /**
     * Verify if Delete pop-up is displayed
     *
     * @return True if the pop-up is displayed, else false.
     */

    public boolean isDeletePopUpDisplayed()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(deletePopup);
        return browser.isElementDisplayed(deletePopup);
    }

    /**
     * Verify if Delete pop-up is closed (e.g. after confirming/canceling delete action)
     *
     * @return True if the pop-up is displayed, else false.
     */

    public boolean isDeletePopClosed()
    {
        browser.waitUntilElementDisappears(By.xpath("//div[text()='Delete Item']"), 10);
        return browser.isElementDisplayed(deletePopup);

    }

    /**
     * Verify if Delete button is displayed on Delete pop-up
     *
     * @return True if the Delete button is displayed, else false.
     */

    public boolean isDeleteButtonDisplayedOnDeletePopup()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(deletePopup);
        return browser.isElementDisplayed(confirmDeleteAction);
    }

    /**
     * Verify if Cancel button is displayed on Delete pop-up
     *
     * @return True if the Cancel button is displayed, else false.
     */

    public boolean isCancelButtonDisplayedOnDeletePopup()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(cancelDeleteAction);
        return browser.isElementDisplayed(cancelDeleteAction);
    }

    public void confirmDeleteAction()
    {
        // browser.waitUntilElementVisible(By.xpath("//button[text()='Delete']"));
        confirmDeleteAction.click();
    }

    public void cancelDeleteAction()

    {
        cancelDeleteAction.click();
    }

    /**
     * Verify if Delete action is displayed on Actions column
     *
     * @return True if the Delete action is displayed, else false.
     */

    public boolean isDeleteActionDisplayed()
    {
        browser.mouseOver(actions);
        return browser.isElementDisplayed(deleteAction);
    }

    /**
     * Verify if Edit action is displayed on Actions column
     *
     * @return True if the Edit action is displayed, else false.
     */

    public boolean isEditActionDisplayed()
    {
        browser.mouseOver(actions);
        return browser.isElementDisplayed(editAction);
    }

    /**
     * Verify if Duplicate action is displayed on Actions column
     *
     * @return True if the Duplicate action is displayed, else false.
     */

    public boolean isDuplicateActionDisplayed()
    {
        browser.mouseOver(actions);
        return browser.isElementDisplayed(duplicateAction);
    }

    /**
     * Verify if Data List table contains any row
     *
     * @return True if data list table contains any row, else false.
     */

    public boolean isDataListTableEmpty()

    {
        return browser.isElementDisplayed(fileChecked);
    }

    /**
     * Verify if Edit Item Window is displayed
     *
     * @return True if Edit Item Window is displayed, else false.
     */

    public boolean isEditItemWindowDisplayed()
    {
        return browser.isElementDisplayed(editItem);
    }

    /**
     * Verify No List Items is displayed (when no item is created for a data list)
     *
     * @return True if there is no item for the data list, else false.
     */

    public boolean isNoListItemsDisplayed()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(noListItems);
        return browser.isElementDisplayed(noListItems);
    }

    public boolean areCorrectValuesDisplayedOnEditPopup(String firstName, String lastName, String email, String company, String jobTitle, String phoneOffice, String phoneMobile)
    {

        String FirstName = "input[value*='" + firstName + "']";
        String LastName = "input[value*='" + lastName + "']";
        String Email = "input[value*='" + email + "']";
        String Company = "input[value*='" + company + "']";
        String JobTitle = "input[value*='" + jobTitle + "']";
        String PhoneOffice = "input[value*='" + phoneOffice + "']";
        String PhoneMobile = "input[value*='" + email + "']";

        String firstName1 = StringUtils.deleteWhitespace(FirstName);
        String lastName1 = StringUtils.deleteWhitespace(LastName);
        String email1 = StringUtils.deleteWhitespace(Email);
        String company1 = StringUtils.deleteWhitespace(Company);
        String jobTitle1 = StringUtils.deleteWhitespace(JobTitle);
        String phoneOffice1 = StringUtils.deleteWhitespace(PhoneOffice);
        String phoneMobile1 = StringUtils.deleteWhitespace(PhoneMobile);

        return browser.isElementDisplayed(By.cssSelector(firstName1)) && browser.isElementDisplayed(By.cssSelector(lastName1)) && browser
            .isElementDisplayed(By.cssSelector(email1)) && browser.isElementDisplayed(By.cssSelector(company1)) && browser
            .isElementDisplayed(By.cssSelector(jobTitle1)) && browser.isElementDisplayed(By.cssSelector(phoneOffice1)) && browser
            .isElementDisplayed(By.cssSelector(phoneMobile1));

    }
}
