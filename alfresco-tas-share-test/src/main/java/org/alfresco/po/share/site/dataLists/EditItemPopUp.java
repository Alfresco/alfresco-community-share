package org.alfresco.po.share.site.dataLists;

import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import static org.testng.Assert.assertTrue;
@Slf4j
public class EditItemPopUp extends CreateNewItemPopUp
{
    public EditItemPopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }
    private final By saveButton = By.cssSelector("button[id$='submit-button']");
    private final By cancelButton = By.cssSelector("button[id$='form-cancel-button']");
    private final By closeIcon = By.className("container-close");
    private final By calendarIcon = By.className("datepicker-icon");
    private final String dropDownLocator = "select[id*='%s']";
    private final String dropDownOptions = "//option[@value='%s']";
    public EditItemPopUp editContent(String field, String content)
    {
        waitUntilElementIsVisible(By.cssSelector(String.format(fieldLocator, field)));
        findElement(By.cssSelector(String.format(fieldLocator, field))).clear();
        addContent(field, content);
        return this;
    }

    public EditItemPopUp verifyDataItemForm(String field)
    {
        waitUntilElementIsVisible(By.cssSelector(String.format(fieldLocator, field)));
        Assert.assertTrue(isElementDisplayed(By.cssSelector(String.format(fieldLocator, field))));
        return this;
    }

    public EditItemPopUp assertCalendarIconIsDisplayed()
    {
        log.info("Assert Save button is displayed");
        assertTrue(isElementDisplayed(calendarIcon), "Calendar Icon is not displayed");
        return this;
    }

    public EditItemPopUp assertSaveButtonIsDisplayed()
    {
        log.info("Assert Save button is displayed");
        assertTrue(isElementDisplayed(saveButton), "Save button is not displayed");
        return this;
    }

    public EditItemPopUp assertCancelButtonIsDisplayed()
    {
        log.info("Assert Cancel button is displayed");
        assertTrue(isElementDisplayed(cancelButton), "Cancel button is not displayed");
        return this;
    }

    public EditItemPopUp assertCloseIconIsDisplayed()
    {
        log.info("Assert Cancel button is displayed");
        assertTrue(isElementDisplayed(closeIcon), "close Icon is not displayed");
        return this;
    }

    public void verifyDropDownItems(String item, String dropDownList, String dropDownOption)
    {
        if (item != null)
        {
            waitUntilElementsAreVisible(By.cssSelector("option"));
            Select dropdown = new Select(findElement(By.cssSelector(String.format(dropDownLocator, dropDownList))));
            dropdown.selectByValue(item);
            Assert.assertTrue(dropdown.getOptions().equals(dropDownOption));
        }
    }

    public EditItemPopUp assertVerifyDropDownOptions(String option)
    {
        log.info("Assert DropDown options are displayed");
        assertTrue(isElementDisplayed(By.xpath(String.format(dropDownOptions, option))), "Check Drop drown options");
        return this;
    }
}
