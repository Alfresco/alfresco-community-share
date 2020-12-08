package org.alfresco.po.share.site;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

public class CreateSiteDialog extends BaseDialogComponent
{
    private Toolbar toolbar;
    private SiteDashboardPage siteDashboardPage;

    @RenderWebElement
    private final By siteIDInput = By.cssSelector("div[id='CREATE_SITE_FIELD_SHORTNAME'] div[class*='InputField'] input");
    private final By siteIdDescription = By.cssSelector("div[id='CREATE_SITE_FIELD_SHORTNAME'] div.description");
    private final By nameMandatory = By.cssSelector("div[id='CREATE_SITE_FIELD_TITLE'] span[class='requirementIndicator required']");
    private final By siteIDMandatory = By.cssSelector("div[id='CREATE_SITE_FIELD_SHORTNAME'] span[class='requirementIndicator required']");
    @RenderWebElement
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

    public  CreateSiteDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
        toolbar = new Toolbar(browser);
        siteDashboardPage = new SiteDashboardPage(browser);
    }

    public CreateSiteDialog navigateByMenuBar()
    {
        toolbar.clickSites().clickCreateSite();
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
        return getBrowser().findElement(siteIDInput).getAttribute("value");
    }

    public String getSiteIDFieldLabel()
    {
        return getBrowser().findElement(siteIDLabel).getText();
    }

    public String getSiteIDDescriptionText()
    {
        return getBrowser().findElement(siteIdDescription).getText();
    }

    public void clearSiteIdInput()
    {
        getBrowser().findElement(siteIDInput).clear();
    }

    public boolean isSiteIdInputEmpty()
    {
        return getBrowser().findElement(siteIDInput).getText().isEmpty();
    }

    public String getUrlErrorMessage()
    {
        getBrowser().waitUntilElementVisible(urlErrorMessage);
        return getBrowser().findFirstDisplayedElement(urlErrorMessage).getText();
    }

    public boolean isTitleMandatory()
    {
        return getBrowser().findElement(nameMandatory).getText().contains("*");
    }

    public boolean isSiteIDMandatory()
    {
        return getBrowser().findElement(siteIDMandatory).getText().contains("*");
    }

    public boolean isNameInputFieldDisplayed()
    {
        return getBrowser().isElementDisplayed(nameInputField);
    }

    public String getNameFieldLabel()
    {
        return getBrowser().waitUntilElementVisible(nameFieldLabel).getText();
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
        return getBrowser().findElement(publicVisibilityRadioButton).getAttribute("value").trim();
    }

    public String isModeratedVisibilityButtonDisplayed()
    {
        return getBrowser().findElement(moderatedVisibilityButton).getAttribute("value").trim();
    }

    public String isPrivateVisibilityButtonDisplayed()
    {
        return getBrowser().findElement(privateVisibilityButton).getAttribute("value").trim();
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
        return getBrowser().waitUntilElementVisible(createButtonState).getAttribute("aria-disabled");
    }

    public boolean isCancelButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(cancelButton);
    }

    public CreateSiteDialog typeInNameInput(String siteName)
    {
        getBrowser().waitUntilElementClickable(nameInputField);
        clearAndType(getBrowser().findElement(nameInputField), siteName);
        return this;
    }

    public CreateSiteDialog typeInDescription(String siteDescription)
    {
        getBrowser().waitUntilElementVisible(descriptionInputField);
        clearAndType(getBrowser().findElement(descriptionInputField), siteDescription);
        return this;
    }

    public CreateSiteDialog typeInSiteID(String siteID)
    {
        WebElement idInput = getBrowser().waitUntilElementVisible(siteIDInput);
        idInput.clear();
        idInput.click();
        idInput.sendKeys(siteID);
        return this;
    }

    public String getTitleInputText(String text)
    {
        WebElement nameInput = getBrowser().findElement(nameInputField);
        getBrowser().waitUntilElementContainsText(nameInput, text);
        return nameInput.getText();
    }

    public String getNameInputText()
    {
        return getBrowser().findElement(nameInputField).getAttribute("value");
    }

    public String getDescriptionInputText()
    {
        return getBrowser().findElement(descriptionInputField).getAttribute("value");
    }

    public void selectPublicVisibility()
    {
        getBrowser().findElement(publicVisibilityRadioButton).click();
    }

    public void selectModeratedVisibility()
    {
        getBrowser().findElement(moderatedVisibilityButton).click();
    }

    public void selectPrivateVisibility()
    {
        getBrowser().findElement(privateVisibilityButton).click();
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

    public SiteDashboardPage clickCreateButton()
    {
        WebElement create = getBrowser().findElement(createButton);
        getBrowser().waitUntilElementHasAttribute(create,"aria-disabled", "false");
        getBrowser().waitUntilElementClickable(create).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }

    public void clickCancelButton()
    {
        getBrowser().findElement(cancelButton).click();
    }

    public void clickCloseXButton()
    {
        getBrowser().findElement(closeXButton).click();
        getBrowser().waitUntilElementDisappears(createSiteDialog);
    }

    public boolean isCloseXButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(closeXButton);
    }

    public CreateSiteDialog assertCreateSiteDialogIsDisplayed()
    {
        Assert.assertTrue(getBrowser().isElementDisplayed(createSiteDialog), "Create site dialog is displayed");
        return this;
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
        return getBrowser().waitUntilElementVisible(nameFieldWarningMessage).getText();
    }
}