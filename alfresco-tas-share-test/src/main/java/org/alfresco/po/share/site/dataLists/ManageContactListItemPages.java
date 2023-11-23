package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.web.browser.WebBrowser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.common.DataUtil.isEnumContainedByList;

public class ManageContactListItemPages extends BaseDialogComponent {

    protected WebBrowser browser;
    private By actions = By.cssSelector("td[class*='col-actions'] div");
    private By editAction = By.cssSelector("a[title='Edit']");
    private By deleteAction = By.cssSelector("a[title='Delete']");
    private By duplicateAction = By.cssSelector("a[title='Duplicate']");
    private By deletePopup = By.xpath("//div[text()='Delete Item']");
    private By confirmDeleteAction = By.xpath("//button[text()='Delete']");
    private By cancelDeleteAction = By.xpath("//button[text()='Cancel']");
    private By noListItems = By.xpath("//div[text()='No list items']");
    private By editItem = By.cssSelector("div[id*='editDetails-dialogTitle']");
    private By firstName = By.cssSelector("input[id*='FirstName']");
    private By lastName = By.cssSelector("input[id*='LastName']");
    private By email = By.cssSelector("input[id*='Email']");
    private By company = By.cssSelector("input[id*='Company']");
    private By job = By.cssSelector("input[id*='Job']");
    private By phoneOffice = By.cssSelector("input[id*='PhoneOffice']");
    private By phoneMobile = By.cssSelector("input[id*='PhoneMobile']");
    private By notes = By.cssSelector("textarea[id*='contactNotes']");
    private By save = By.cssSelector("button[id*='submit']");

    public ManageContactListItemPages(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public boolean isEditActionDisplayed()
    {
        mouseOver(actions);
        return isElementDisplayed(editAction);
    }

    public boolean isDuplicateActionDisplayed()
    {
        mouseOver(actions);
        return isElementDisplayed(duplicateAction);
    }

    public boolean isDeleteActionDisplayed()
    {
        mouseOver(actions);
        return isElementDisplayed(deleteAction);
    }

    public void clickDuplicateAction()
    {
        mouseOver(actions);
        clickElement(duplicateAction);
    }

    public void clickEditAction()
    {
        mouseOver(actions);
        clickElement(editAction);
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

        waitInSeconds(4);
        return isElementDisplayed(By.cssSelector(firstName1)) && isElementDisplayed(By.cssSelector(lastName1)) && isElementDisplayed(By.cssSelector(email1)) && isElementDisplayed(By.cssSelector(company1)) && isElementDisplayed(By.cssSelector(jobTitle1)) && isElementDisplayed(By.cssSelector(phoneOffice1)) && isElementDisplayed(By.cssSelector(phoneMobile1));
    }

    public void clickDeleteAction()
    {
        mouseOver(actions);
        clickElement(deleteAction);
    }

    public boolean isDeletePopUpDisplayed()
    {
        waitInSeconds(3);
        return isElementDisplayed(deletePopup);
    }

    public boolean isDeleteButtonDisplayedOnDeletePopup()
    {
        waitInSeconds(3);
        return isElementDisplayed(confirmDeleteAction);
    }

    public boolean isCancelButtonDisplayedOnDeletePopup()
    {
        waitInSeconds(3);
        return isElementDisplayed(cancelDeleteAction);
    }

    public void confirmDeleteAction()
    {
        clickElement(confirmDeleteAction);
    }

    public boolean isNoListItemsDisplayed()
    {
        waitInSeconds(3);
        return isElementDisplayed(noListItems);
    }

    public boolean isEditItemWindowDisplayed()
    {
        waitInSeconds(3);
        return isElementDisplayed(editItem);
    }

    public void clearItemDetails()
    {
        findElement(firstName).clear();
        findElement(lastName).clear();
        findElement(email).clear();
        findElement(company).clear();
        findElement(job).clear();
        findElement(phoneOffice).clear();
        findElement(phoneMobile).clear();
        findElement(notes).clear();
    }

    public void fillInCreateItemForm(String newFirstName, String newLastName, String newEmail, String newCompany, String newJob, String newPhoneOffice, String newPhoneMobile, String newNotes)
    {

        findElement(firstName).sendKeys(newFirstName);
        findElement(lastName).sendKeys(newLastName);
        findElement(email).sendKeys(newEmail);
        findElement(company).sendKeys(newCompany);
        findElement(job).sendKeys(newJob);
        findElement(phoneOffice).sendKeys(newPhoneOffice);
        findElement(phoneMobile).sendKeys(newPhoneMobile);
        findElement(notes).sendKeys(newNotes);

    }

    public void editContactItem(String editedFirstName, String editedLastName, String editedEmail, String editedCompany, String editedJob, String editedPhoneOffice, String editedPhoneMobile, String editedNotes)
    {
        clearItemDetails();
        fillInCreateItemForm(editedFirstName, editedLastName, editedEmail, editedCompany, editedJob, editedPhoneOffice, editedPhoneMobile, editedNotes);
        findElement(save).click();
    }
}
