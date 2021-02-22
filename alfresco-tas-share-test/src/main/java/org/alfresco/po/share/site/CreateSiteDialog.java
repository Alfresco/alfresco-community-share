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

    public  CreateSiteDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public CreateSiteDialog navigateByMenuBar()
    {
        new Toolbar(webDriver).clickSites().clickCreateSite();
        return new CreateSiteDialog(webDriver);
    }

    public void navigateFromDashlet()
    {
        new MySitesDashlet(webDriver).clickCreateSiteButton();
    }

    public boolean isSiteIDInputFieldDisplayed()
    {
        return isElementDisplayed(siteIDInput);
    }

    public String getSiteIdInputText()
    {
        return findElement(siteIDInput).getAttribute("value");
    }

    public String getSiteIDFieldLabel()
    {
        return findElement(siteIDLabel).getText();
    }

    public String getSiteIDDescriptionText()
    {
        return findElement(siteIdDescription).getText();
    }

    public void clearSiteIdInput()
    {
        findElement(siteIDInput).clear();
    }

    public boolean isSiteIdInputEmpty()
    {
        return findElement(siteIDInput).getText().isEmpty();
    }

    public String getUrlErrorMessage()
    {
        waitUntilElementIsVisible(urlErrorMessage);
        return findFirstDisplayedElement(urlErrorMessage).getText();
    }

    public boolean isTitleMandatory()
    {
        return findElement(nameMandatory).getText().contains("*");
    }

    public boolean isSiteIDMandatory()
    {
        return findElement(siteIDMandatory).getText().contains("*");
    }

    public boolean isNameInputFieldDisplayed()
    {
        return isElementDisplayed(nameInputField);
    }

    public String getNameFieldLabel()
    {
        return waitUntilElementIsVisible(nameFieldLabel).getText();
    }

    public boolean isDescriptionInputFieldDisplayed()
    {
        return isElementDisplayed(descriptionInputField);
    }

    public String getDescriptionLabel()
    {
        return waitUntilElementIsVisible(descriptionLabel).getText();
    }

    public String getVisibilityLabel()
    {
        return waitUntilElementIsVisible(visibilityLabel).getText();
    }

    public String getPublicVisibilityButtonState()
    {
        return findElement(publicVisibilityRadioButton).getAttribute("value").trim();
    }

    public String isModeratedVisibilityButtonDisplayed()
    {
        return findElement(moderatedVisibilityButton).getAttribute("value").trim();
    }

    public String isPrivateVisibilityButtonDisplayed()
    {
        return findElement(privateVisibilityButton).getAttribute("value").trim();
    }

    public String getPublicVisibilityDescription()
    {
        return getElementText(publicVisibilityDescription);
    }

    public String getModeratedVisibilityDescription()
    {
        return getElementText(moderatedVisibilityDescription);
    }

    public String getPrivateVisibilityDescription()
    {
        return getElementText(privateVisibilityDescription);
    }

    public boolean isCreateButtonDisplayed()
    {
        return isElementDisplayed(createButton);
    }

    /**
     * Get PopUp Create Site button state.
     *
     * @return - false if button is enabled.
     * - true if button is disabled.
     */
    public String getCreateButtonState()
    {
        return waitUntilElementIsVisible(createButtonState).getAttribute("aria-disabled");
    }

    public boolean isCancelButtonDisplayed()
    {
        return isElementDisplayed(cancelButton);
    }

    public CreateSiteDialog typeInNameInput(String siteName)
    {
        clearAndType(nameInputField, siteName);
        return this;
    }

    public CreateSiteDialog typeInDescription(String siteDescription)
    {
        waitUntilElementIsVisible(descriptionInputField);
        clearAndType(descriptionInputField, siteDescription);
        return this;
    }

    public CreateSiteDialog typeInSiteID(String siteID)
    {
        WebElement idInput = waitUntilElementIsVisible(siteIDInput);
        idInput.clear();
        clickElement(idInput);
        idInput.sendKeys(siteID);
        return this;
    }

    public String getNameInputText()
    {
        return findElement(nameInputField).getAttribute("value");
    }

    public String getDescriptionInputText()
    {
        return findElement(descriptionInputField).getAttribute("value");
    }

    public void selectPublicVisibility()
    {
        clickElement(publicVisibilityRadioButton);
    }

    public void selectModeratedVisibility()
    {
        clickElement(moderatedVisibilityButton);
    }

    public void selectPrivateVisibility()
    {
        clickElement(privateVisibilityButton);
    }

    public boolean isPublicVisibilityRadioButtonChecked()
    {
        return isElementDisplayed(publicVisibilityRadioButtonChecked);
    }

    public boolean isModeratedVisibilityRadioButtonChecked()
    {
        return isElementDisplayed(moderatedVisibilityRadioButtonChecked);
    }

    public boolean isPrivateVisibilityRadioButtonChecked()
    {
        return isElementDisplayed(privateVisibilityRadioButtonChecked);
    }

    public SiteDashboardPage clickCreateButton()
    {
        WebElement create = waitUntilElementIsVisible(createButton);
        waitUntilElementHasAttribute(create,"aria-disabled", "false");
        clickElement(create);
        return new SiteDashboardPage(webDriver);
    }

    public void clickCancelButton()
    {
        clickElement(cancelButton);
    }

    public void clickCloseXButton()
    {
        clickElement(closeXButton);
        waitUntilElementDisappears(createSiteDialog);
    }

    public boolean isCloseXButtonDisplayed()
    {
        return isElementDisplayed(closeXButton);
    }

    public CreateSiteDialog assertCreateSiteDialogIsDisplayed()
    {
        waitUntilElementIsVisible(createSiteDialog);
        assertTrue(isElementDisplayed(createSiteDialog), "Create site dialog is displayed");
        return this;
    }

    public boolean isTypeLabelDisplayed()
    {
        return isElementDisplayed(typeLabel);
    }

    public String getTypeLabelValue()
    {
        return waitUntilElementIsVisible(typeLabelValue).getText();
    }

    public String getNameFieldWarningMessage()
    {
        return waitUntilElementIsVisible(nameFieldWarningMessage).getText();
    }
}