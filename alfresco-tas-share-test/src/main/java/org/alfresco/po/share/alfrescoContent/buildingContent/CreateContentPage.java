package org.alfresco.po.share.alfrescoContent.buildingContent;

import org.alfresco.common.Utils;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

@PageObject
public class CreateContentPage extends SharePage<CreateContentPage>
{
    @Autowired
    private TinyMceEditor tinyMceEditor;

    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    @RenderWebElement
    @FindBy (css = "div[id*='_default-form-fields']")
    private WebElement createForm;

    @RenderWebElement
    @FindBy (css = "input[name='prop_cm_name']")
    private WebElement nameField;

    @FindBy (css = "textarea[name='prop_cm_content']")
    private WebElement contentField;

    @FindBy (css = "input[name='prop_cm_title']")
    private WebElement titleField;

    @FindBy (css = "textarea[name='prop_cm_description']")
    private WebElement descriptionField;

    @FindBy (css = "label[for$='default_prop_cm_name'] .mandatory-indicator")
    private WebElement nameFieldIsMandatoryMarker;

    @RenderWebElement
    @FindBy (css = "button[id$='submit-button']")
    private WebElement submitButton;

    @FindBy (css = "button[id*='form-cancel-button']")
    private WebElement cancelButton;

    @FindBy (css = "div[class ='mce-edit-area mce-container mce-panel mce-stack-layout-item'] iframe")
    private WebElement htmlContentField;

    @Override
    public String getRelativePath()
    {
        return "";
    }

    public CreateContentPage assertCreateContentPageIsOpened()
    {
        Assert.assertTrue(browser.isElementDisplayed(nameField), "Name field is displayed");
        return this;
    }

    public CreateContentPage assertNameInputIsMandatory()
    {
        LOG.info("Assert Name input is mandatory");
        Assert.assertTrue(browser.isElementDisplayed(nameFieldIsMandatoryMarker), "Name input is mandatory");
        return this;
    }

    public CreateContentPage assertCreateButtonIsDisplayed()
    {
        LOG.info("Assert Create button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(submitButton), "Create button is displayed");
        return this;
    }

    public CreateContentPage assertCancelButtonIsDisplayed()
    {
        LOG.info("Assert Cancel button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    /**
     * Method to send input to the Name field
     */
    public CreateContentPage typeName(String name)
    {
        Utils.clearAndType(nameField, name);
        return this;
    }

    /**
     * Method to send input to the Title field
     */
    public CreateContentPage typeTitle(String title)
    {
        Utils.clearAndType(titleField, title);
        return this;
    }

    /**
     * Method to send input to the Content field
     */
    public CreateContentPage typeContent(String content)
    {
        Utils.clearAndType(contentField, content);
        return this;
    }

    /**
     * Method to send input to the Description field
     */
    public CreateContentPage typeDescription(String description)
    {
        Utils.clearAndType(descriptionField, description);
        return this;
    }

    /**
     * Method to click the Create button
     */
    public DocumentDetailsPage clickCreate()
    {
        submitButton.click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    /**
     * Method to click the Cancel button
     */
    public void clickCancel()
    {
        cancelButton.click();
    }

    /**
     * Method to send input to the HTML content field
     *
     * @param inputHTMLContent
     */
    public CreateContentPage sendInputForHTMLContent(String inputHTMLContent)
    {
        tinyMceEditor.setText(inputHTMLContent);
        browser.focusOnWebElement(nameField);
        return this;
    }
}