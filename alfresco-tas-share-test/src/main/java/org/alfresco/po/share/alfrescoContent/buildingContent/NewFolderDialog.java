package org.alfresco.po.share.alfrescoContent.buildingContent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class NewFolderDialog extends BaseDialogComponent
{
    private final By dialogTitle = By.cssSelector("div[id$='_default-createFolder-dialogTitle']");
    private final By saveButton = By.cssSelector("button[id$='submit-button']");
    private final By cancelButton = By.cssSelector("button[id$='form-cancel-button']");
    private final By nameField = By.cssSelector("input[name='prop_cm_name']");
    private final By titleField = By.cssSelector("input[name='prop_cm_title']");
    private final By descriptionField = By.cssSelector("textarea[name='prop_cm_description']");
    private final By mandatoryIndicator = By.cssSelector("label[for$='createFolder_prop_cm_name'] .mandatory-indicator");

    public NewFolderDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public NewFolderDialog assertDialogTitleEquals(String expectedTitle)
    {
        log.info("Assert dialog title is {}", expectedTitle);
        assertEquals(webElementInteraction.getElementText(dialogTitle), expectedTitle, "Dialog title is not correct");
        return this;
    }

    public NewFolderDialog assertSaveButtonIsDisplayed()
    {
        log.info("Assert Save button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(saveButton), "Save button is not displayed");
        return this;
    }

    public NewFolderDialog assertCancelButtonIsDisplayed()
    {
        log.info("Assert Cancel button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(cancelButton), "Cancel button is not displayed");
        return this;
    }

    public NewFolderDialog assertNameInputIsDisplayed()
    {
        log.info("Assert name input is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(nameField), "Name input is not displayed");
        return this;
    }

    public NewFolderDialog assertTitleInputIsDisplayed()
    {
        log.info("Assert title input is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(titleField), "Title input is not displayed");
        return this;
    }

    public NewFolderDialog assertDescriptionInputIsDisplayed()
    {
        log.info("Assert description input is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(descriptionField), "Description input is not displayed");
        return this;
    }

    public NewFolderDialog assertMandatoryIndicatorForNameIsDisplayed()
    {
        log.info("Assert mandatory indicator is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(mandatoryIndicator), "Mandatory indicator for name input is displayed");
        return this;
    }

    public void clickSave()
    {
        log.info("Click Save");
        webElementInteraction.waitUntilElementIsVisible(saveButton);
        webElementInteraction.clickElement(saveButton);
        waitUntilNotificationMessageDisappears();
    }

    public void clickCancel()
    {
        log.info("Click Cancel");
        webElementInteraction.waitUntilElementIsVisible(cancelButton);
        webElementInteraction.clickElement(cancelButton);
    }

    public NewFolderDialog typeName(String name)
    {
        log.info("Type name {}", name);
        webElementInteraction.clearAndType(nameField, name);
        return this;
    }

    public NewFolderDialog fillInDetails(String name, String title, String description)
    {
        log.info("Set name, title and description");
        webElementInteraction.clearAndType(nameField, name);
        webElementInteraction.clearAndType(titleField, title);
        webElementInteraction.clearAndType(descriptionField, description);
        return this;
    }

    public String getNameFieldValue()
    {
        return webElementInteraction.findElement(nameField).getAttribute("value");
    }

    public boolean isNewFolderPopupDisplayed()
    {
        return webElementInteraction.isElementDisplayed(dialogTitle);
    }

    public NewFolderDialog assertNameInputIsInvalid()
    {
        log.info("Assert name input is invalid");
        webElementInteraction.waitUntilElementHasAttribute(webElementInteraction.findElement(nameField), "class", "invalid");
        assertTrue(webElementInteraction.findElement(nameField).getAttribute("class").contains("invalid"),  "Name field is invalid");
        return this;
    }

    public NewFolderDialog assertNameInputContainsIllegalCharactersMessageIsDisplayed()
    {
        log.info("Assert name contains illegal characters message is displayed");
        assertEquals(language.translate("newFolderDialog.invalidName"),
            webElementInteraction.findElement(nameField).getAttribute("title"));
        return this;
    }
}