package org.alfresco.po.share.alfrescoContent.buildingContent;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.Utils;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class CreateContent extends SiteCommon<CreateContent>
{
    @Autowired
    TinyMceEditor tinyMceEditor;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @RenderWebElement
    @FindBy (css = "div[id*='_default-form-fields']")
    private WebElement createForm;
    @RenderWebElement
    @FindBy (css = "input[id*='prop_cm_name']")
    private WebElement nameField;
    @FindBy (css = "textarea[id*='prop_cm_content']")
    private WebElement contentField;
    @FindBy (css = "input[id*='prop_cm_title']")
    private WebElement titleField;
    @FindBy (css = "textarea[id*='prop_cm_description']")
    private WebElement descriptionField;
    @FindBy (xpath = "//div[@class ='form-field']//label[contains(@for,'_default_prop_cm_name')]//span[@class = 'mandatory-indicator']")
    private WebElement nameFieldIsMandatoryMarker;
    @RenderWebElement
    @FindBy (css = "button[id*='form-submit-button']")
    private WebElement submitButton;
    @FindBy (css = "button[id*='form-cancel-button']")
    private WebElement cancelButton;
    @FindBy (css = "div[class ='mce-edit-area mce-container mce-panel mce-stack-layout-item'] iframe")
    private WebElement htmlContentField;

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/create-content", getCurrentSiteName());
    }

    /**
     * Method to check the the Name Field is present on the Create form
     */
    public boolean isNameFieldDisplayedOnTheCreateForm()
    {
        return browser.isElementDisplayed(nameField);
    }

    /**
     * Method to check the the Content Field is present on the Create form
     */
    public boolean isContentFieldDisplayedOnTheCreateForm()
    {
        return browser.isElementDisplayed(contentField);
    }

    /**
     * Method to check the the Title Field is present on the Create form
     */
    public boolean isTitleFieldDisplayedOnTheCreateForm()
    {
        return browser.isElementDisplayed(titleField);
    }

    /**
     * Method to check the the Description Field is present on the Create form
     */
    public boolean isDescriptionFieldDisplayedOnTheCreateForm()
    {
        return browser.isElementDisplayed(descriptionField);
    }

    /**
     * Method to determine if the mandatory marker is present for the Name field
     */
    public boolean isMandatoryMarkPresentForNameField()
    {
        return browser.isElementDisplayed(nameFieldIsMandatoryMarker);
    }

    /**
     * Method to check if the Save button is present on the Create form
     */
    public boolean isCreateButtonPresent()
    {
        return browser.isElementDisplayed(submitButton);
    }

    /**
     * Method to check if the Cancel button is present on the Create form
     */
    public boolean isCancelButtonPresent()
    {
        return browser.isElementDisplayed(cancelButton);
    }

    /**
     * Method to send input to the Name field
     */
    public void sendInputForName(String name)
    {
        Utils.clearAndType(nameField, name);
    }

    /**
     * Method to send input to the Title field
     */
    public void sendInputForTitle(String title)
    {
        Utils.clearAndType(titleField, title);
    }

    /**
     * Method to send input to the Content field
     */
    public void sendInputForContent(String content)
    {
        Utils.clearAndType(contentField, content);
    }

    /**
     * Method to send input to the Description field
     */
    public void sendInputForDescription(String description)
    {
        Utils.clearAndType(descriptionField, description);
    }

    /**
     * Method to click the Create button
     */
    public DocumentDetailsPage clickCreateButton()
    {
        submitButton.click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    /**
     * Method to click the Cancel button
     */
    public void clickCancelButton()
    {
        cancelButton.click();
    }

    /**
     * Method to check if the HTML content field is displayed on the create form
     */
    public boolean isHTMLContentFieldDisplayed()
    {
        return browser.isElementDisplayed(htmlContentField);
    }

    /**
     * Method to send input to the HTML content field
     *
     * @param inputHTMLContent
     */
    public void sendInputForHTMLContent(String inputHTMLContent)
    {
        tinyMceEditor.setText(inputHTMLContent);
    }

    /**
     * Method to check if the Content field is marked as mandatory
     *
     * @return
     */
    public boolean isContentMarkedAsMandatory()
    {
        return browser.isElementDisplayed(By.cssSelector("textarea[id*='_prop_cm_content'] span.mandatory-indicator"));
    }

    /**
     * Method to check if the Title field is marked as mandatory
     */
    public boolean isTitleMarkedAsMandatory()
    {
        return browser.isElementDisplayed(By.cssSelector("input[id*='_default_prop_cm_title'] span.mandatory-indicator"));
    }

    /**
     * Method to check if the Description field is marked as mandatory
     */
    public boolean isDescriptionMarkedAsMandatory()
    {
        return browser.isElementDisplayed(By.cssSelector("textarea[id*='_default_prop_cm_description'] span.mandatory-indicator"));
    }
}