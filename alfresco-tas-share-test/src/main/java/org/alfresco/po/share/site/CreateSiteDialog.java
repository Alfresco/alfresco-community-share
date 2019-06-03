package org.alfresco.po.share.site;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.toolbar.ToolbarSitesMenu;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Laura.Capsa
 */
@PageObject
public class CreateSiteDialog extends ShareDialog
{
    @FindBy (css = "label[for='alfresco-createSite-instance-shortName']")
    private WebElement urlNameLabel;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_SHORTNAME'] div[class*='InputField'] input")
    private WebElement siteIDInput;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_SHORTNAME'] div.description")
    private WebElement siteIdDescription;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_TITLE'] span[class='requirementIndicator required']")
    private WebElement nameMandatory;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_SHORTNAME'] span[class='requirementIndicator required']")
    private WebElement siteIDMandatory;

    @RenderWebElement
    @FindBy (id = "CREATE_SITE_DIALOG")
    private WebElement createSiteDialog;

    //@RenderWebElement
    @FindBy (css = "span[id='CREATE_SITE_DIALOG_OK_label']")
    private WebElement createButton;

    @RenderWebElement
    @FindBy (id = "CREATE_SITE_DIALOG_CANCEL_label")
    private WebElement cancelButton;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_TITLE'] div[class*='dijitInputField'] input")
    private WebElement nameInputField;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_TITLE'] label ")
    private WebElement nameFieldLabel;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_DESCRIPTION'] textarea")
    private WebElement descriptionInputField;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_DESCRIPTION'] label")
    private WebElement descriptionLabel;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_SHORTNAME'] label")
    private WebElement siteIDLabel;
    @FindBy (css = "div[id='CREATE_SITE_FIELD_VISIBILITY'] label")
    private WebElement visibilityLabel;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION0'] input")
    private WebElement publicVisibilityRadioButton;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION1'] input")
    private WebElement moderatedVisibilityButton;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION2'] input")
    private WebElement privateVisibilityButton;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION0'] .alfresco-forms-controls-RadioButtons__description")
    private WebElement publicVisibilityDescription;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION1'] .alfresco-forms-controls-RadioButtons__description")
    private WebElement moderatedVisibilityDescription;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION2'] .alfresco-forms-controls-RadioButtons__description")
    private WebElement privateVisibilityDescription;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION0'] div[class*='dijitRadioChecked']")
    private WebElement publicVisibilityRadioButtonChecked;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION1'] div[class*='dijitRadioChecked']")
    private WebElement moderatedVisibilityRadioButtonChecked;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION2'] div[class*='dijitRadioChecked']")
    private WebElement privateVisibilityRadioButtonChecked;

    @FindBy (css = "span.dijitDialogCloseIcon")
    private WebElement closeXButton;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_PRESET'] label.label")
    private WebElement typeLabel;

    @FindBy (css = "table[id='CREATE_SITE_FIELD_PRESET_CONTROL'] div[class$='dijitButtonText'] span")
    private WebElement typeLabelValue;

    @FindBy (css = "div[id='CREATE_SITE_FIELD_TITLE'] div[class$='__warning-row'] div")
    private WebElement nameFieldWarningMessage;

    private By urlErrorMessage = By.cssSelector("div[id='CREATE_SITE_FIELD_SHORTNAME'] span.validation-message");
    private By urlErrorOkButton = By.cssSelector("div[class='ft'] button");
    private By createButtonState = By.cssSelector("span[aria-labelledby='CREATE_SITE_DIALOG_OK_label']");

    @Autowired
    private MySitesDashlet mySitesDashlet;

    @Autowired
    private ToolbarSitesMenu toolbarSitesMenu;

    @SuppressWarnings ("unchecked")

    public CreateSiteDialog navigateByMenuBar()
    {
        toolbarSitesMenu.clickCreateSite();
        return (CreateSiteDialog) renderedPage();
    }

    public CreateSiteDialog navigateFromDashlet()
    {
        mySitesDashlet.clickCreateSiteButton();
        return (CreateSiteDialog) renderedPage();
    }

    public boolean isSiteIDInputFieldDisplayed()
    {
        return getBrowser().isElementDisplayed(siteIDInput);
    }

    public String getSiteIdInputText()
    {
        return siteIDInput.getAttribute("value");
    }

    public String getSiteIDFieldLabel()
    {
        return siteIDLabel.getText();
    }

    public String getSiteIDDescriptionText()
    {
        return siteIdDescription.getText();
    }

    public void clearSiteIdInput()
    {
        siteIDInput.clear();
    }

    public boolean isSiteIdInputEmpty()
    {
        return siteIDInput.getText().isEmpty();
    }

    public void typeSiteID(String siteID)
    {
        siteIDInput.sendKeys(siteID);
    }


    public String getUrlErrorMessage()
    {
        browser.waitUntilElementVisible(urlErrorMessage);
        return browser.findFirstDisplayedElement(urlErrorMessage).getText();
    }

    public boolean isTitleMandatory()
    {
        return nameMandatory.getText().contains("*");
    }

    public boolean isSiteIDMandatory()
    {
        return siteIDMandatory.getText().contains("*");
    }

    public boolean isNameInputFieldDisplayed()
    {
        return getBrowser().isElementDisplayed(nameInputField);
    }

    public String getNameFieldLabel()
    {
        return browser.waitUntilElementVisible(nameFieldLabel).getText();
    }

    public boolean isDescriptionInputFieldDisplayed()
    {
        return getBrowser().isElementDisplayed(descriptionInputField);
    }

    public String getDescriptionLabel()
    {
        return getBrowser().waitUntilElementVisible(descriptionLabel).getText();
    }

    public String getVisibilityLabel()
    {
        return getBrowser().waitUntilElementVisible(visibilityLabel).getText();
    }

    public String getPublicVisibilityButtonState()
    {
        return publicVisibilityRadioButton.getAttribute("value").toString().trim();
    }

    public String isModeratedVisibilityButtonDisplayed()
    {
        return moderatedVisibilityButton.getAttribute("value").toString().trim();
    }

    public String isPrivateVisibilityButtonDisplayed()
    {
        return privateVisibilityButton.getAttribute("value").toString().trim();
    }

    public String getPublicVisibilityDescription()
    {
        return getBrowser().waitUntilElementVisible(publicVisibilityDescription).getText();
    }

    public String getModeratedVisibilityDescription()
    {
        return getBrowser().waitUntilElementVisible(moderatedVisibilityDescription).getText();
    }

    public String getPrivateVisibilityDescription()
    {
        return getBrowser().waitUntilElementVisible(privateVisibilityDescription).getText();
    }

    public boolean isCreateButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(createButton);
    }

    /**
     * Get PopUp Create Site button state.
     *
     * @return - false if button is enabled.
     * - true if button is disabled.
     */
    public String getCreateButtonState()
    {
        return browser.waitUntilElementVisible(createButtonState).getAttribute("aria-disabled");
    }

    public boolean isCancelButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(cancelButton);
    }

    public void typeInNameInput(String siteName)
    {
        getBrowser().waitUntilElementClickable(nameInputField);
        nameInputField.click();
        nameInputField.sendKeys(siteName);
    }

    public void typeInDescription(String siteDescription)
    {
        getBrowser().waitUntilElementVisible(descriptionInputField);
        descriptionInputField.click();
        descriptionInputField.sendKeys(siteDescription);
    }

    public void typeInSiteID(String siteID)
    {
        browser.waitUntilElementVisible(siteIDInput);
        siteIDInput.click();
        siteIDInput.clear();
        siteIDInput.click();
        siteIDInput.sendKeys(siteID);
    }

    public String getTitleInputText(String text)
    {
        browser.waitUntilElementContainsText(nameInputField, text);
        return nameInputField.getText();
    }

    public String getNameInputText()
    {
        return nameInputField.getAttribute("value");
    }

    /**
     * Get Description input text
     *
     * @return description input text
     */
    public String getDescriptionInputText()
    {
        return descriptionInputField.getAttribute("value");
    }

    public void selectPublicVisibility()
    {
        publicVisibilityRadioButton.click();
    }

    public void selectModeratedVisibility()
    {
        moderatedVisibilityButton.click();
    }

    public void selectPrivateVisibility()
    {
        privateVisibilityButton.click();
    }

    public boolean isPublicVisibilityRadioButtonChecked()
    {
        return getBrowser().isElementDisplayed(publicVisibilityRadioButtonChecked);
    }

    public boolean isModeratedVisibilityRadioButtonChecked()
    {
        return getBrowser().isElementDisplayed(moderatedVisibilityRadioButtonChecked);
    }

    public boolean isPrivateVisibilityRadioButtonChecked()
    {
        return getBrowser().isElementDisplayed(privateVisibilityRadioButtonChecked);
    }

    public HtmlPage clickCreateButton(HtmlPage page)
    {
        createButton.click();
        return page.renderedPage();
    }

    public void clickCreateButtonWithoutRenderer()
    {
        createButton.click();
    }

    public void clickCancelButton()
    {
        cancelButton.click();
    }

    public void clickCloseXButton()
    {
        closeXButton.click();
    }

    public boolean isCloseXButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(closeXButton);
    }

    public boolean isCreateSiteDialogDisplayed()
    {
        return getBrowser().isElementDisplayed(createSiteDialog);
    }

    public boolean isTypeLabelDisplayed()
    {
        return getBrowser().isElementDisplayed(typeLabel);
    }

    public String getTypeLabelValue()
    {
        return getBrowser().waitUntilElementVisible(typeLabelValue).getText();
    }

    public String getNameFieldWarningMessage()
    {
        return browser.waitUntilElementVisible(nameFieldWarningMessage).getText();
    }
}