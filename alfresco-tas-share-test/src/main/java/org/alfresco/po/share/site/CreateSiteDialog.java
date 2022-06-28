package org.alfresco.po.share.site;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class CreateSiteDialog extends BaseDialogComponent
{
    private final By siteIDInput = By.cssSelector("div[id='CREATE_SITE_FIELD_SHORTNAME'] div[class*='InputField'] input");
    private final By createSiteDialog = By.id("CREATE_SITE_DIALOG");
    private final By createButton = By.id("CREATE_SITE_DIALOG_OK");
    private final By cancelButton = By.id("CREATE_SITE_DIALOG_CANCEL_label");
    private final By nameInputField = By.cssSelector("div[id='CREATE_SITE_FIELD_TITLE'] div[class*='dijitInputField'] input");
    private final By descriptionInputField = By.cssSelector("div[id='CREATE_SITE_FIELD_DESCRIPTION'] textarea");
    private final By publicVisibilityRadioButton = By.cssSelector("div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION0'] .radio-button-widget");
    private final By moderatedVisibilityButton = By.cssSelector("div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION1'] .radio-button-widget");
    private final By privateVisibilityButton = By.cssSelector("div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION2'] .radio-button-widget");
    private final By publicVisibilityRadioButtonChecked = By.cssSelector("div[id='CREATE_SITE_FIELD_VISIBILITY_CONTROL_OPTION0'] input");
    private final By closeXButton = By.cssSelector("span.dijitDialogCloseIcon");
    private final By nameFieldWarningMessage = By.cssSelector("div[id='CREATE_SITE_FIELD_TITLE'] div[class$='__warning-row'] div");
    private final By siteIdErrorMessage = By.cssSelector("div[id='CREATE_SITE_FIELD_SHORTNAME'] span.validation-message");
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

    public boolean isSiteIDInputFieldDisplayed()
    {
        return isElementDisplayed(siteIDInput);
    }

    public String getSiteIdInputText()
    {
        return findElement(siteIDInput).getAttribute("value");
    }

    public CreateSiteDialog assertSiteIdErrorMessageEqualsWithExpected(String expectedErrorLabel)
    {
        assertEquals(getElementText(siteIdErrorMessage), expectedErrorLabel,
            "Site id error message not equals with expected");
        return this;
    }

    public CreateSiteDialog assertSiteNameInUseWarningMessageEquals(String expectedErrorLabel)
    {
        assertEquals(getElementText(nameFieldWarningMessage), expectedErrorLabel,
            "Site id error message not equals with expected");
        return this;
    }

    public boolean isDescriptionInputFieldDisplayed()
    {
        return isElementDisplayed(descriptionInputField);
    }

    public boolean isCreateButtonDisplayed()
    {
        return isElementDisplayed(createButton);
    }

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
        waitUntilElementIsVisible(nameInputField);
        clearAndType(nameInputField, siteName);
        return this;
    }

    public CreateSiteDialog typeInDescription(String siteDescription)
    {
        waitUntilElementIsVisible(descriptionInputField);
        clearAndType(descriptionInputField, siteDescription);
        return this;
    }

    public CreateSiteDialog assertSiteIdValueEquals(String expectedId)
    {
        log.info("Assert site id value equals to {}", expectedId);
        waitUntilElementIsVisible(siteIDInput);
        assertEquals(findElement(siteIDInput).getAttribute("value"), expectedId.toLowerCase(),
            String.format("Site id is not %s", expectedId));
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

    public CreateSiteDialog selectPublicVisibility()
    {
        log.info("Select Public site visibility");
        clickJS(findElement(publicVisibilityRadioButton));
        return this;
    }

    public CreateSiteDialog assertPublicVisibilityIsSelected()
    {
        log.info("Assert public visibility is selected");
        assertTrue(findElement(publicVisibilityRadioButtonChecked).isSelected(), "Public visibility is not selected");
        return this;
    }

    public CreateSiteDialog selectModeratedVisibility()
    {
        log.info("Select Moderated site visibility");
        clickElement(moderatedVisibilityButton);
        return this;
    }

    public CreateSiteDialog selectPrivateVisibility()
    {
        log.info("Select Private site visibility");
        clickElement(privateVisibilityButton);
        return this;
    }

    public SiteDashboardPage clickCreateButton()
    {
        log.info("Click Create button");
        WebElement create = waitUntilElementIsVisible(createButton);
        waitUntilElementHasAttribute(create,"aria-disabled", "false");
        clickElement(create);

        SiteDashboardPage siteDashboardPage = new SiteDashboardPage(webDriver);
        siteDashboardPage.waitForSiteDashboardPageToBeLoaded();

        return siteDashboardPage;
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

    public CreateSiteDialog assertCreateSiteDialogIsDisplayed()
    {
        waitUntilElementIsVisible(createSiteDialog);
        assertTrue(isElementDisplayed(createSiteDialog), "Create site dialog is displayed");
        return this;
    }
}