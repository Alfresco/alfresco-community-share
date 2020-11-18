package org.alfresco.po.share.alfrescoContent.buildingContent;

import org.alfresco.po.share.ShareDialog2;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class NewFolderDialog extends ShareDialog2
{
    @RenderWebElement
    protected By dialogTitle = By.cssSelector("div[id$='_default-createFolder-dialogTitle']");
    @RenderWebElement
    private By saveButton = By.cssSelector("button[id$='submit-button']");
    private By cancelButton = By.cssSelector("button[id$='form-cancel-button']");
    private By nameField = By.cssSelector("input[name='prop_cm_name']");
    private By titleField = By.cssSelector("input[name='prop_cm_title']");
    private By descriptionField = By.cssSelector("textarea[name='prop_cm_description']");
    private By mandatoryIndicator = By.cssSelector("label[for$='createFolder_prop_cm_name'] .mandatory-indicator");

    public NewFolderDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public NewFolderDialog assertDialogTitleEquals(String expectedTitle)
    {
        LOG.info("Assert dialog title is {}", expectedTitle);
        assertEquals(getElementText(dialogTitle), expectedTitle, "Dialog title is not correct");
        return this;
    }

    public NewFolderDialog assertSaveButtonIsDisplayed()
    {
        LOG.info("Assert Save button is displayed");
        assertTrue(getBrowser().isElementDisplayed(saveButton), "Save button is not displayed");
        return this;
    }

    public NewFolderDialog assertCancelButtonIsDisplayed()
    {
        LOG.info("Assert Cancel button is displayed");
        assertTrue(getBrowser().isElementDisplayed(cancelButton), "Cancel button is not displayed");
        return this;
    }

    public NewFolderDialog assertNameInputIsDisplayed()
    {
        LOG.info("Assert name input is displayed");
        assertTrue(getBrowser().isElementDisplayed(nameField), "Name input is not displayed");
        return this;
    }

    public NewFolderDialog assertTitleInputIsDisplayed()
    {
        LOG.info("Assert title input is displayed");
        assertTrue(getBrowser().isElementDisplayed(titleField), "Title input is not displayed");
        return this;
    }

    public NewFolderDialog assertDescriptionInputIsDisplayed()
    {
        LOG.info("Assert description input is displayed");
        assertTrue(getBrowser().isElementDisplayed(descriptionField), "Description input is not displayed");
        return this;
    }

    public NewFolderDialog assertMandatoryIndicatorForNameIsDisplayed()
    {
        LOG.info("Assert mandatory indicator is displayed");
        assertTrue(getBrowser().isElementDisplayed(mandatoryIndicator), "Mandatory indicator for name input is displayed");
        return this;
    }

    public void clickSave()
    {
        LOG.info("Click Save");
        getBrowser().waitUntilElementClickable(saveButton).click();
        waitUntilNotificationMessageDisappears();
    }

    public void clickCancel()
    {
        LOG.info("Click Cancel");
        getBrowser().findElement(cancelButton).click();
    }

    public NewFolderDialog typeName(String name)
    {
        LOG.info("Type name {}", name);
        clearAndType(nameField, name);
        return this;
    }

    public NewFolderDialog fillInDetails(String name, String title, String description)
    {
        LOG.info("Set name, title and description");
        clearAndType(nameField, name);
        clearAndType(titleField, title);
        clearAndType(descriptionField, description);
        return this;
    }

    public String getNameFieldValue()
    {
        return getBrowser().findElement(nameField).getAttribute("value");
    }

    public boolean isNewFolderPopupDisplayed()
    {
        return getBrowser().isElementDisplayed(dialogTitle);
    }

    public NewFolderDialog assertNameInputIsInvalid()
    {
        LOG.info("Assert name input is invalid");
        getBrowser().waitUntilElementHasAttribute(getBrowser().findElement(nameField), "class", "invalid");
        assertTrue(getBrowser().findElement(nameField).getAttribute("class").contains("invalid"),  "Name field is invalid");
        return this;
    }

    public NewFolderDialog assertNameInputContainsIllegalCharactersMessageIsDisplayed()
    {
        LOG.info("Assert name contains illegal characters message is displayed");
        assertTrue(getBrowser().findElement(nameField).getAttribute("title").equals(language.translate("newFolderDialog.invalidName")));
        return this;
    }
}