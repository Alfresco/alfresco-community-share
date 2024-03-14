package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EditPropertiesDialog extends BaseDialogComponent
{
    private final By editPropertiesList = By.xpath("//*[@id=\"template_x002e_documentlist_v2_x002e_myfiles_x0023_default-editDetails-alf-id103-form-fields\"]/div/div/label");
    private final By dialogTitle = By.cssSelector("div[id$='dialogTitle']");
    private final By propertyName = By.cssSelector("input[name='prop_cm_name']");
    private final By propertyTitle = By.cssSelector("input[name='prop_cm_title']");
    private final By propertyDescription = By.cssSelector("textarea[name='prop_cm_description']");
    private final By propertyContent=By.cssSelector("textarea[id*='_content']");
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
    private final By longitudeEditField = By.cssSelector("input[title$='Longitude']");
    private final By lattitudeEditField = By.cssSelector("input[title$='Latitude']");

    public EditPropertiesDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public SelectDialog clickSelectCategories()
    {
        waitUntilElementIsVisible(selectCategoriesButton);
        clickElement(selectCategoriesButton);
        SelectDialog selectDialog = new SelectDialog(webDriver);
        selectDialog.waitForLeftAreaResults();
        return selectDialog;
    }

    public void setLongitudeLattitude(double longitude, double lattitude)
    {
        waitUntilElementIsVisible(longitudeEditField);
        clearAndType(longitudeEditField, longitude+"");
        clearAndType(lattitudeEditField, lattitude+"");
        clickSave();
    }

    public boolean isSelectTagsButtonDisplayed()
    {
        return isElementDisplayed(selectTagsButton);
    }

    public void clickSelectTags()
    {
        waitUntilElementIsVisible(selectTagsButton);
        clickElement(selectTagsButton);
    }

    public void clickSave()
    {
        clickElement(saveButton);
        waitUntilElementDisappears(dialogTitle);
        waitUntilNotificationMessageDisappears();
    }

    public void clickCancel()
    {
        clickElement(cancelButton);
    }

    public boolean isCategorySelected(String category)
    {
        return findFirstElementWithValue(selectedCategories, category) != null;
    }

    public EditPropertiesDialog assertCategoryIsSelected(String category)
    {
        log.info("Assert category {} is selected", category);
        assertTrue(isCategorySelected(category), String.format("Category %s is not displayed", category));
        return this;
    }

    public boolean isTagSelected(String tag)
    {
        return findFirstElementWithValue(selectedTags, tag) != null;
    }

    public EditPropertiesDialog assertTagIsSelected(String tag)
    {
        log.info("Assert tag {} is selected", tag);
        assertTrue(isTagSelected(tag), String.format("Tag %s is not displayed", tag));
        return this;
    }

    public EditPropertiesDialog assertIseditPropertiesDialogEquals(String contentName)
    {
        log.info("Verify Edit properties title.");
        assertEquals(getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), contentName),
            "Displayed dialog=");
        return this;
    }
    public boolean verifyAllElementsAreDisplayed()
    {
        List<String> elementList = new ArrayList<>();
        for (WebElement file : findElements(editPropertiesList))
        {
            elementList.add(file.getText());
        }
        return true;
    }
    public void assertVerifyEditPropertiesElementsAreDisplayed()
    {
        log.info("Verify that the all Elements in Edit Propertied names are Displayed.");
        assertTrue(verifyAllElementsAreDisplayed(), "Some elements of the 'Edit Properties' dialog are not displayed");
    }

    public EditPropertiesDialog assertIsSelectTagsButtonDisplayed()
    {
        log.info("Verify that the Select Tag Button is Displayed.");
        assertTrue(isSelectTagsButtonDisplayed(), "Select Tag Button is not displayed.");
        return this;
    }

    public void setName(String fileName)
    {
        clearAndType(propertyName, fileName);
    }

    public void setTitle(String fileTitle)
    {
        clearAndType(propertyTitle, fileTitle);
    }

    public void setDescription(String fileDescription)
    {
        clearAndType(propertyDescription, fileDescription);
    }

    public void typeHost(String host)
    {
        clearAndType(hostInputField, host);
    }

    public void typePort(String port)
    {
        clearAndType(portInputField, port);
    }

    public void typeUsername(String username)
    {
        clearAndType(usernameInputField, username);
    }

    public void typePassword(String password)
    {
        clearAndType(passwordInputField, password);
    }

    public void checkEnabled()
    {
        clickElement(enabledCheckbox);
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

    public void setContent(String content)
    {
        clearAndType(propertyContent, content);
    }

    public void clickAllPropertiesLink()
    {
        waitInSeconds(3);
        clickElement(allPropertiesButton);
    }
}