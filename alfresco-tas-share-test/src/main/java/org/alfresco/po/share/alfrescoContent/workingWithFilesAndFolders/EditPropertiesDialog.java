package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class EditPropertiesDialog extends BaseDialogComponent
{
    private final By dialogTitle = By.cssSelector("div[id$='dialogTitle']");
    private final By propertyName = By.cssSelector("input[name='prop_cm_name']");
    private final By propertyTitle = By.cssSelector("input[name='prop_cm_title']");
    private final By propertyDescription = By.cssSelector("textarea[name='prop_cm_description']");
    private final By hostInputField = By.cssSelector("input[id*='endpointhost']");
    private final By portInputField = By.cssSelector("input[id*='endpointport']");
    private final By usernameInputField = By.cssSelector("input[id*='username']");
    private final By passwordInputField = By.cssSelector("input[id*='password']");
    private final By enabledCheckbox = By.cssSelector(".formsCheckBox");
    private final By selectTagsButton = By.cssSelector("div[id$='prop_cm_taggable-cntrl-itemGroupActions'] button");
    private final By selectCategoriesButton = By.cssSelector("div[id$='prop_cm_categories-cntrl-itemGroupActions'] button");
    private final By allPropertiesButton = By.cssSelector("a[id*='editMetadata-button']");
    private final By selectedTags = By.cssSelector("div[id$='prop_cm_taggable-cntrl-currentValueDisplay'] div");
    private final By selectedCategories = By.cssSelector("div[id$='prop_cm_categories-cntrl-currentValueDisplay'] div");
    private final By saveButton = By.cssSelector("button[id$='form-submit-button']");
    private final By cancelButton = By.cssSelector("button[id$='form-cancel-button']");

    public EditPropertiesDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getDialogTitle()
    {
        return webElementInteraction.getElementText(dialogTitle);
    }

    public void clickSelectCategories()
    {
        webElementInteraction.waitUntilElementIsVisible(selectCategoriesButton);
        webElementInteraction.clickElement(selectCategoriesButton);
    }

    public boolean isSelectTagsButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(selectTagsButton);
    }

    public void clickSelectTags()
    {
        webElementInteraction.waitUntilElementIsVisible(selectTagsButton);
        webElementInteraction.clickElement(selectTagsButton);
    }

    public void clickSave()
    {
        webElementInteraction.clickElement(saveButton);
        webElementInteraction.waitUntilElementDisappears(dialogTitle);
        waitUntilNotificationMessageDisappears();
    }

    public void clickCancel()
    {
        webElementInteraction.clickElement(cancelButton);
    }

    public boolean isCategorySelected(String category)
    {
        return webElementInteraction.findFirstElementWithValue(selectedCategories, category) != null;
    }

    public EditPropertiesDialog assertCategoryIsSelected(String category)
    {
        log.info("Assert category {} is selected", category);
        assertTrue(isCategorySelected(category), String.format("Category %s is not displayed", category));
        return this;
    }

    public boolean isTagSelected(String tag)
    {
        return webElementInteraction.findFirstElementWithValue(selectedTags, tag) != null;
    }

    public EditPropertiesDialog assertTagIsSelected(String tag)
    {
        log.info("Assert tag {} is selected", tag);
        assertTrue(isTagSelected(tag), String.format("Tag %s is not displayed", tag));
        return this;
    }

    public void setName(String fileName)
    {
        webElementInteraction.clearAndType(propertyName, fileName);
    }

    public void setTitle(String fileTitle)
    {
        webElementInteraction.clearAndType(propertyTitle, fileTitle);
    }

    public void setDescription(String fileDescription)
    {
        webElementInteraction.clearAndType(propertyDescription, fileDescription);
    }

    public void typeHost(String host)
    {
        webElementInteraction.clearAndType(hostInputField, host);
    }

    public void typePort(String port)
    {
        webElementInteraction.clearAndType(portInputField, port);
    }

    public void typeUsername(String username)
    {
        webElementInteraction.clearAndType(usernameInputField, username);
    }

    public void typePassword(String password)
    {
        webElementInteraction.clearAndType(passwordInputField, password);
    }

    public void checkEnabled()
    {
        webElementInteraction.clickElement(enabledCheckbox);
    }

    public void updateFolderDetailsForReplication(String host, String port, String username, String password)
    {
        typeHost(host);
        typePort(port);
        typeUsername(username);
        typePassword(password);
        checkEnabled();
        clickSave();
    }

    public void clickAllPropertiesLink()
    {
        webElementInteraction.clickElement(allPropertiesButton);
    }
}