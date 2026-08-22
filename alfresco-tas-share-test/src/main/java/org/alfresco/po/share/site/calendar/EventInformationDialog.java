package org.alfresco.po.share.site.calendar;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Claudia Agache on 7/12/2016.
 */
@PageObject
public class EventInformationDialog extends ShareDialog
{
    @RenderWebElement
    @FindBy (id = "eventInfoPanel_c")
    private WebElement eventInformationPanel;

    @RenderWebElement
    @FindBy (css = "button[id$='_defaultContainer-cancel-button-button']")
    private WebElement cancelButton;

    @FindBy (css = "button[id$='edit-button-button']")
    private WebElement editButton;

    @FindBy (css = "button[id$='delete-button-button']")
    private WebElement deleteButton;

    @FindBy (css = "div[id$='_defaultContainer-startdate']")
    private WebElement startDateTime;

    @FindBy (css = "div[id$='_defaultContainer-enddate']")
    private WebElement endDateTime;

    @FindBy (xpath = "//div[contains(text(),'What:')]/following-sibling::div")
    private WebElement whatDetails;

    @FindBy (xpath = "//div[contains(text(),'Where:')]/following-sibling::div")
    private WebElement whereDetails;

    @FindBy (xpath = "//div[contains(text(),'Description:')]/following-sibling::div")
    private WebElement descriptionDetails;

    @FindBy (xpath = "//div[contains(text(),'Tags:')]/following-sibling::div")
    private WebElement tagsDetails;

    public String getStartDateTime()
    {
        return startDateTime.getText();
    }

    public String getEndDateTime()
    {
        return endDateTime.getText();
    }

    public String getWhatDetails()
    {
        return whatDetails.getText();
    }

    public String getWhereDetails()
    {
        return whereDetails.getText();
    }

    public String getDescriptionDetails()
    {
        return descriptionDetails.getText();
    }

    public String getTagsDetails()
    {
        return tagsDetails.getText().replaceAll("  ", " ");
    }

    public void clickCancelButton()
    {
        cancelButton.click();
    }

    public boolean isEventInformationPanelDisplayed()
    {
        return browser.isElementDisplayed(eventInformationPanel);
    }

    public boolean areButtonsEnabled()
    {
        return cancelButton.isEnabled() && editButton.isEnabled() && deleteButton.isEnabled();
    }

    public void clickDeleteButton()
    {
        deleteButton.click();
    }

    public void clickEditButton()
    {
        editButton.click();
    }
}
