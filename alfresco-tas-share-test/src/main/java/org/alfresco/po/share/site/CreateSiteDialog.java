package org.alfresco.po.share.site;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CreateSiteDialog extends BaseDialogComponent
{
    private Toolbar toolbar;

    private final By siteIDInput = By.cssSelector("div[id='CREATE_SITE_FIELD_SHORTNAME'] div[class*='InputField'] input");
    private final By siteIdDescription = By.cssSelector("div[id='CREATE_SITE_FIELD_SHORTNAME'] div.description");
    private final By nameMandatory = By.cssSelector("div[id='CREATE_SITE_FIELD_TITLE'] span[class='requirementIndicator required']");
    private final By siteIDMandatory = By.cssSelector("div[id='CREATE_SITE_FIELD_SHORTNAME'] span[class='requirementIndicator required']");
    private final By createSiteDialog = By.id("CREATE_SITE_DIALOG");
    private final By createButton = By.id("CREATE_SITE_DIALOG_OK");
    private final By cancelButton = By.id("CREATE_SITE_DIALOG_CANCEL_label");
    private final By nameInputField = By.cssSelector("div[id='CREATE_SITE_FIELD_TITLE'] div[class*='dijitInputField'] input");
    private final By nameFieldLabel = By.cssSelector("div[id='CREATE_SITE_FIELD_TITLE'] label");
    private final By descriptionInputField = By.cssSelector("div[id='CREATE_SITE_FIELD_DESCRIPTION'] textarea");
    private final By descriptionLabel = By.cssSelector("div[id='CREATE_SITE_FIELD_DESCRIPTION'] label");
    private final By siteIDLabel = By.cssSelector("div[id='CREATE_SITE_FIELD_SHORTNAME'] label");
    private final By visibilityLabel = By.cssSelector("div[id='CREATE_SITE_FIELD_VISIBILITY'] label");
    private final By publicVisibilityRadioButton = By.cssSelector("div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION0'] input");
    private final By moderatedVisibilityButton = By.cssSelector("div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION1'] input");
    private final By privateVisibilityButton = By.cssSelector("div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION2'] input");
    private final By publicVisibilityDescription = By.cssSelector("div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION0'] .alfresco-forms-controls-RadioButtons__description");
    private final By moderatedVisibilityDescription = By.cssSelector("div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION1'] .alfresco-forms-controls-RadioButtons__description");
    private final By privateVisibilityDescription = By.cssSelector("div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION2'] .alfresco-forms-controls-RadioButtons__description");
    private final By publicVisibilityRadioButtonChecked = By.cssSelector("div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION0'] div[class*='dijitRadioChecked']");
    private final By moderatedVisibilityRadioButtonChecked = By.cssSelector("div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION1'] div[class*='dijitRadioChecked']");
    private final By privateVisibilityRadioButtonChecked = By.cssSelector("div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION2'] div[class*='dijitRadioChecked']");
    private final By closeXButton = By.cssSelector("span.dijitDialogCloseIcon");
    private final By typeLabel = By.cssSelector("div[id='CREATE_SITE_FIELD_PRESET'] label.label");
    private final By typeLabelValue = By.cssSelector("table[id='CREATE_SITE_FIELD_PRESET_CONTROL'] div[class$='dijitButtonText'] span");
    private final By nameFieldWarningMessage = By.cssSelector("div[id='CREATE_SITE_FIELD_TITLE'] div[class$='__warning-row'] div");
    private final By urlErrorMessage = By.cssSelector("div[id='CREATE_SITE_FIELD_SHORTNAME'] span.validation-message");
    private final By createButtonState = By.cssSelector("span[aria-labelledby='CREATE_SITE_DIALOG_OK_label']");

    private MySitesDashlet mySitesDashlet;

    public  CreateSiteDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
        toolbar = new Toolbar(webDriver);
    }

    public CreateSiteDialog navigateByMenuBar()
    {
        toolbar.clickSites().clickCreateSite();
        return new CreateSiteDialog(webDriver);
    }

    public void navigateFromDashlet()
    {
        mySitesDashlet.clickCreateSiteButton();
    }

    public boolean isSiteIDInputFieldDisplayed()
    {
        return webElementInteraction.isElementDisplayed(siteIDInput);
    }

    public String getSiteIdInputText()
    {
        return webElementInteraction.findElement(siteIDInput).getAttribute("value");
    }

    public String getSiteIDFieldLabel()
    {
        return webElementInteraction.findElement(siteIDLabel).getText();
    }

    public String getSiteIDDescriptionText()
    {
        return webElementInteraction.findElement(siteIdDescription).getText();
    }

    public void clearSiteIdInput()
    {
        webElementInteraction.findElement(siteIDInput).clear();
    }

    public boolean isSiteIdInputEmpty()
    {
        return webElementInteraction.findElement(siteIDInput).getText().isEmpty();
    }

    public String getUrlErrorMessage()
    {
        webElementInteraction.waitUntilElementIsVisible(urlErrorMessage);
        return webElementInteraction.findFirstDisplayedElement(urlErrorMessage).getText();
    }

    public boolean isTitleMandatory()
    {
        return webElementInteraction.findElement(nameMandatory).getText().contains("*");
    }

    public boolean isSiteIDMandatory()
    {
        return webElementInteraction.findElement(siteIDMandatory).getText().contains("*");
    }

    public boolean isNameInputFieldDisplayed()
    {
        return webElementInteraction.isElementDisplayed(nameInputField);
    }

    public String getNameFieldLabel()
    {
        return webElementInteraction.waitUntilElementIsVisible(nameFieldLabel).getText();
    }

    public boolean isDescriptionInputFieldDisplayed()
    {
        return webElementInteraction.isElementDisplayed(descriptionInputField);
    }

    public String getDescriptionLabel()
    {
        return webElementInteraction.waitUntilElementIsVisible(descriptionLabel).getText();
    }

    public String getVisibilityLabel()
    {
        return webElementInteraction.waitUntilElementIsVisible(visibilityLabel).getText();
    }

    public String getPublicVisibilityButtonState()
    {
        return webElementInteraction.findElement(publicVisibilityRadioButton).getAttribute("value").trim();
    }

    public String isModeratedVisibilityButtonDisplayed()
    {
        return webElementInteraction.findElement(moderatedVisibilityButton).getAttribute("value").trim();
    }

    public String isPrivateVisibilityButtonDisplayed()
    {
        return webElementInteraction.findElement(privateVisibilityButton).getAttribute("value").trim();
    }

    public String getPublicVisibilityDescription()
    {
        return webElementInteraction.getElementText(publicVisibilityDescription);
    }

    public String getModeratedVisibilityDescription()
    {
        return webElementInteraction.getElementText(moderatedVisibilityDescription);
    }

    public String getPrivateVisibilityDescription()
    {
        return webElementInteraction.getElementText(privateVisibilityDescription);
    }

    public boolean isCreateButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(createButton);
    }

    /**
     * Get PopUp Create Site button state.
     *
     * @return - false if button is enabled.
     * - true if button is disabled.
     */
    public String getCreateButtonState()
    {
        return webElementInteraction.waitUntilElementIsVisible(createButtonState).getAttribute("aria-disabled");
    }

    public boolean isCancelButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(cancelButton);
    }

    public CreateSiteDialog typeInNameInput(String siteName)
    {
        webElementInteraction.clearAndType(nameInputField, siteName);
        return this;
    }

    public CreateSiteDialog typeInDescription(String siteDescription)
    {
        webElementInteraction.waitUntilElementIsVisible(descriptionInputField);
        webElementInteraction.clearAndType(descriptionInputField, siteDescription);
        return this;
    }

    public CreateSiteDialog typeInSiteID(String siteID)
    {
        WebElement idInput = webElementInteraction.waitUntilElementIsVisible(siteIDInput);
        idInput.clear();
        webElementInteraction.clickElement(idInput);
        idInput.sendKeys(siteID);
        return this;
    }

    public String getNameInputText()
    {
        return webElementInteraction.findElement(nameInputField).getAttribute("value");
    }

    public String getDescriptionInputText()
    {
        return webElementInteraction.findElement(descriptionInputField).getAttribute("value");
    }

    public void selectPublicVisibility()
    {
        webElementInteraction.clickElement(publicVisibilityRadioButton);
    }

    public void selectModeratedVisibility()
    {
        webElementInteraction.clickElement(moderatedVisibilityButton);
    }

    public void selectPrivateVisibility()
    {
        webElementInteraction.clickElement(privateVisibilityButton);
    }

    public boolean isPublicVisibilityRadioButtonChecked()
    {
        return webElementInteraction.isElementDisplayed(publicVisibilityRadioButtonChecked);
    }

    public boolean isModeratedVisibilityRadioButtonChecked()
    {
        return webElementInteraction.isElementDisplayed(moderatedVisibilityRadioButtonChecked);
    }

    public boolean isPrivateVisibilityRadioButtonChecked()
    {
        return webElementInteraction.isElementDisplayed(privateVisibilityRadioButtonChecked);
    }

    public SiteDashboardPage clickCreateButton()
    {
        WebElement create = webElementInteraction.findElement(createButton);
        webElementInteraction.waitUntilElementHasAttribute(create,"aria-disabled", "false");
        webElementInteraction.clickElement(create);
        return new SiteDashboardPage(webDriver);
    }

    public void clickCancelButton()
    {
        webElementInteraction.clickElement(cancelButton);
    }

    public void clickCloseXButton()
    {
        webElementInteraction.clickElement(closeXButton);
        webElementInteraction.waitUntilElementDisappears(createSiteDialog);
    }

    public boolean isCloseXButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(closeXButton);
    }

    public CreateSiteDialog assertCreateSiteDialogIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(createSiteDialog), "Create site dialog is displayed");
        return this;
    }

    public boolean isTypeLabelDisplayed()
    {
        return webElementInteraction.isElementDisplayed(typeLabel);
    }

    public String getTypeLabelValue()
    {
        return webElementInteraction.waitUntilElementIsVisible(typeLabelValue).getText();
    }

    public String getNameFieldWarningMessage()
    {
        return webElementInteraction.waitUntilElementIsVisible(nameFieldWarningMessage).getText();
    }
}