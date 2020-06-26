package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import java.util.List;

import org.alfresco.common.Utils;
import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;

/**
 * Created by Claudia Agache on 9/14/2016.
 */
@PageObject
public class EditPropertiesDialog extends ShareDialog
{
    @Autowired
    SelectDialog selectDialog;

    @Autowired
    EditPropertiesPage editPropertiesPage;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @RenderWebElement
    @FindBy (css = "div[id$='dialogTitle']")
    private WebElement dialogTitle;

    @RenderWebElement
    @FindBy (css = "input[name='prop_cm_name']")
    private WebElement propertyName;

    @FindBy (css = "input[name='prop_cm_title']")
    private WebElement propertyTitle;

    @FindBy (css = "textarea[name='prop_cm_description']")
    private WebElement propertyDescription;

    @FindBy (css = "input[id*='endpointhost']")
    private WebElement hostInputField;

    @FindBy (css = "input[id*='endpointport']")
    private WebElement portInputField;

    @FindBy (css = "input[id*='username']")
    private WebElement usernameInputField;

    @FindBy (css = "input[id*='password']")
    private WebElement passwordInputField;

    @FindBy (css = ".formsCheckBox")
    private WebElement enabledCheckbox;

    @FindBy (css = "div[id$='prop_cm_taggable-cntrl-itemGroupActions'] button")
    private WebElement selectTagsButton;

    @FindBy (css = "div[id$='prop_cm_categories-cntrl-itemGroupActions'] button")
    private Button selectCategoriesButton;

    @FindBy (css = "a[id*='editMetadata-button']")
    private WebElement allPropertiesButton;

    @FindAll (@FindBy (css = "div[id$='prop_cm_taggable-cntrl-currentValueDisplay'] div"))
    private List<WebElement> selectedTags;

    @FindAll (@FindBy (css = "div[id$='prop_cm_categories-cntrl-currentValueDisplay'] div"))
    private List<WebElement> selectedCategories;

    @RenderWebElement
    @FindBy (css = "button[id$='form-submit-button']")
    private WebElement saveButton;

    @FindBy (css = "button[id$='form-cancel-button']")
    private WebElement cancelButton;

    public String getDialogTitle()
    {
        return dialogTitle.getText();
    }

    public SelectDialog clickSelectCategories()
    {
        selectCategoriesButton.click();
        return (SelectDialog) selectDialog.renderedPage();
    }

    public boolean isSelectTagsButtonDisplayed()
    {
        return browser.isElementDisplayed(selectTagsButton);
    }

    public SelectDialog clickSelectTags()
    {
        browser.waitUntilElementClickable(selectTagsButton).click();
        return (SelectDialog) selectDialog.renderedPage();
    }

    public DocumentLibraryPage clickSave()
    {
        browser.waitUntilElementClickable(saveButton).click();
        waitUntilMessageDisappears();
        return (DocumentLibraryPage) documentLibraryPage.renderedPage();
    }

    public void clickCancel()
    {
        cancelButton.click();
    }

    public boolean isCategorySelected(String category)
    {
        return browser.findFirstElementWithValue(selectedCategories, category) != null;
    }

    public boolean isTagSelected(String tag)
    {
        return browser.findFirstElementWithValue(selectedTags, tag) != null;
    }

    public void setName(String fileName)
    {
        Utils.clearAndType(propertyName, fileName);
    }

    public void setTitle(String fileTitle)
    {
        Utils.clearAndType(propertyTitle, fileTitle);
    }

    public void setDescription(String fileDescription)
    {
        Utils.clearAndType(propertyDescription, fileDescription);
    }

    public boolean verifyAllElementsAreDisplayed()
    {
        return browser.isElementDisplayed(propertyName) &&
            browser.isElementDisplayed(propertyTitle) &&
            browser.isElementDisplayed(propertyDescription) &&
            browser.isElementDisplayed(selectTagsButton) &&
            browser.isElementDisplayed(saveButton) &&
            browser.isElementDisplayed(cancelButton);
    }

    public void typeHost(String host)
    {
        Utils.clearAndType(hostInputField, host);
    }

    public void typePort(String port)
    {
        Utils.clearAndType(portInputField, port);
    }

    public void typeUsername(String username)
    {
        Utils.clearAndType(usernameInputField, username);
    }

    public void typePassword(String password)
    {
        Utils.clearAndType(passwordInputField, password);
    }

    public void checkEnabled()
    {
        enabledCheckbox.click();
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

    public EditPropertiesPage clickAllPropertiesLink()
    {
        browser.waitUntilElementVisible(allPropertiesButton);
        allPropertiesButton.click();
        return (EditPropertiesPage) editPropertiesPage.renderedPage();
    }
}