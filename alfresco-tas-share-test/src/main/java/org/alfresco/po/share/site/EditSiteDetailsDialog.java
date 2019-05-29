package org.alfresco.po.share.site;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * @author Laura.Capsa
 */
@Primary
@PageObject
public class EditSiteDetailsDialog extends ShareDialog
{
    @Autowired
    SiteDashboardPage siteDashboardPage;
    @Autowired
    SiteDashboardPage siteDashboard;
    @FindBy (css = "div[id='EDIT_SITE_FIELD_TITLE'] input[class$='dijitInputInner']")
    private TextInput nameInput;
    @FindBy (css = "div[id='EDIT_SITE_FIELD_TITLE'] label.label")
    private WebElement nameLabel;
    @FindBy (css = "div[id='EDIT_SITE_FIELD_DESCRIPTION'] textarea")
    private WebElement descriptionInput;
    @FindBy (css = "div[id='EDIT_SITE_FIELD_DESCRIPTION'] label.label")
    private WebElement descriptionLabel;
    @FindBy (css = "div[id='EDIT_SITE_FIELD_VISIBILITY'] label.label")
    private WebElement visibilityLabel;
    @FindBy (css = "div[id='EDIT_SITE_FIELD_VISIBILITY_CONTROL_OPTION0'] div[class*='dijitRadio'] input")
    private WebElement publicVisibilityRadioButton;
    @FindBy (css = "div[id='EDIT_SITE_FIELD_VISIBILITY_CONTROL_OPTION0'] div.radio-button-label div")
    private WebElement publicVisibilityDescription;
    @FindBy (css = "div[id='EDIT_SITE_FIELD_VISIBILITY_CONTROL_OPTION1'] div[class*='dijitRadio'] input")
    private WebElement moderatedVisibilityRadioButton;
    @FindBy (css = "div[id='EDIT_SITE_FIELD_VISIBILITY_CONTROL_OPTION1'] div.radio-button-label div")
    private WebElement moderatedVisibilityDescription;
    @FindBy (css = "div[id='EDIT_SITE_FIELD_VISIBILITY_CONTROL_OPTION2'] div[class*='dijitRadio'] input")
    private WebElement privateVisibilityRadioButton;
    @FindBy (css = "div[id='EDIT_SITE_FIELD_VISIBILITY_CONTROL_OPTION2'] div.radio-button-label div")
    private WebElement privateVisibilityDescription;
    @RenderWebElement
    @FindBy (id = "EDIT_SITE_DIALOG_OK_label")
    private WebElement saveButton;
    @FindBy (id = "EDIT_SITE_DIALOG_CANCEL_label")
    private Button cancel;

    /**
     * Navigate to site
     * Opens Edit Details dialog
     *
     * @param siteId
     */
    public void navigateToDialog(String siteId)
    {
        siteDashboard.navigate(siteId);
        siteDashboard.clickSiteConfiguration();
        siteDashboard.clickOptionInSiteConfigurationDropDown("Edit Site Details", this);
    }

    public void typeDetails(String title, String description)
    {
        nameInput.clear();
        nameInput.sendKeys(title);

        descriptionInput.clear();
        descriptionInput.sendKeys(description);
    }

    public boolean isNameInputDisplayed()
    {
        return nameInput.isDisplayed();
    }

    public String getNameLabel()
    {
        return nameLabel.getText();
    }

    public String getTitleInputText()
    {
        return nameInput.getText();
    }

    public boolean isDescriptionInputDisplayed()
    {
        return browser.isElementDisplayed(descriptionInput);
    }

    public String getDescriptionLabel()
    {
        return descriptionLabel.getText();
    }

    public String getVisibilityLabel()
    {
        return visibilityLabel.getText();
    }

    public boolean isPublicVisibilityRadioButtonDisplayed()
    {
        return browser.isElementDisplayed(By.cssSelector("div[id='EDIT_SITE_FIELD_VISIBILITY_CONTROL_OPTION0'] div[class*='dijitRadio']"));
    }

    public String getPublicVisibilityDescription()
    {
        return publicVisibilityDescription.getText();
    }

    public boolean isModeratedVisibilityRadioButtonDisplayed()
    {
        return browser.isElementDisplayed(By.cssSelector("div[id='EDIT_SITE_FIELD_VISIBILITY_CONTROL_OPTION1'] div[class*='dijitRadio']"));
    }

    public String getModeratedVisibilityDescription()
    {
        return moderatedVisibilityDescription.getText();
    }

    public boolean isPrivateVisibilityRadioButtonDisplayed()
    {
        return browser.isElementDisplayed(By.cssSelector("div[id='EDIT_SITE_FIELD_VISIBILITY_CONTROL_OPTION2'] div[class*='dijitRadio']"));
    }

    public boolean isPrivateVisibilitySelected()
    {
        return privateVisibilityRadioButton.isSelected();
    }

    public String getPrivateVisibilityDescription()
    {
        return privateVisibilityDescription.getText();
    }

    public void selectPrivateVisibility()
    {
        privateVisibilityRadioButton.click();
    }

    public boolean isSaveButtonDisplayed()
    {
        return browser.isElementDisplayed(saveButton);
    }

    public void clickSaveButton()
    {
        saveButton.click();
    }

    public boolean isCancelButtonDisplayed()
    {
        return cancel.isDisplayed();
    }

    public void clickCancelButton()
    {
        cancel.click();
    }
}