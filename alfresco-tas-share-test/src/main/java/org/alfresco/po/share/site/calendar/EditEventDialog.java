package org.alfresco.po.share.site.calendar;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by Claudia Agache on 7/20/2016.
 */
@Slf4j
public class EditEventDialog extends AddEventDialogPage
{


    private final By eventInformationPanels = By.id("eventInfoPanel_h");
    private final By dialogHeader = By.id("eventEditPanel-dialog_h");

    private final By eventLocation = By.id("eventEditPanel-location");

    private final By editButton = By.cssSelector("button[id$='edit-button-button']");

    private final By eventDescription = By.id("eventEditPanel-description");

    private final By currentTagList = By.cssSelector("#eventEditPanel-current-tags .taglibrary-action>span");

    private final By tagsDetails = By.xpath("//div[contains(text(),'Tags:')]/following-sibling::div");

    private final By descriptionDetails = By.xpath("//div[contains(text(),'Description:')]/following-sibling::div");

    private final By whereDetails = By.xpath("//div[contains(text(),'Where:')]/following-sibling::div");

    private final By whatDetails = By.xpath("//div[contains(text(),'What:')]/following-sibling::div");

    private final By endDateTime = By.cssSelector("div[id$='_defaultContainer-enddate']");

    private final By startDateTime = By.cssSelector("div[id$='_defaultContainer-startdate']");

    private final By cancelButton = By.cssSelector("button[id$='_defaultContainer-cancel-button-button']");

    private final By deleteButton = By.cssSelector("button[id$='delete-button-button']");

    public EditEventDialog(ThreadLocal<WebDriver> webDriver) {
        super(webDriver);
    }

    public Boolean isDialogDisplayed()
    {
        waitInSeconds(3);
        return isElementDisplayed(dialogHeader) && findElement(dialogHeader).getText().equals("Edit Event");
    }

    public Boolean isEventInformationPanelDisplayed()
    {
        waitInSeconds(3);
        return isElementDisplayed(eventInformationPanels) && findElement(eventInformationPanels).getText().equals("Event Information");
    }

    public String getEventLocation()
    {
        return findElement(eventLocation).getAttribute("value");
    }

    public void clickEditButton()
    {
        findElement(editButton).click();
    }

    public String getEventDescription()
    {
        return findElement(eventDescription).getAttribute("value");
    }

    public void removeTag(String tag) {
        findElement(currentTagList).click();
    }

    public String getStartDateTime()
    {
        return findElement(startDateTime).getText();
    }


    public String getWhatDetails()
    {
        waitInSeconds(3);
        return findElement(whatDetails).getText();
    }

    public String getWhereDetails()
    {
        return findElement(whereDetails).getText();
    }

    public String getDescriptionDetails()
    {
        return findElement(descriptionDetails).getText();
    }

    public String getTagsDetails()
    {
        return findElement(tagsDetails).getText().replaceAll("  ", " ");
    }

    public String getEndDateTime()
    {
        return findElement(endDateTime).getText();
    }

    public void clickOnCancelButton()
    {
        findElement(cancelButton).click();
    }

    public boolean areButtonsEnabled()
    {
        return findElement(cancelButton).isEnabled() && findElement(editButton).isEnabled() && findElement(deleteButton).isEnabled();
    }
}
