package org.alfresco.po.share.alfrescoContent.buildingContent;

import org.alfresco.common.Utils;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import static org.testng.Assert.assertTrue;

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
    private WebElement nameInputIsMandatoryMarker;

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
        LOG.info("Assert Content page is opened");
        assertTrue(browser.getCurrentUrl().contains("create-content"), "Create content page is not opened");
        return this;
    }

    public CreateContentPage assertNameInputHasMandatoryMarker()
    {
        LOG.info("Assert Name input has mandatory marker");
        assertTrue(browser.isElementDisplayed(nameInputIsMandatoryMarker), "Name input is not mandatory");
        return this;
    }

    public CreateContentPage assertCreateButtonIsDisplayed()
    {
        LOG.info("Assert Create button is displayed");
        assertTrue(browser.isElementDisplayed(submitButton), "Create button is not displayed");
        return this;
    }

    public CreateContentPage assertCancelButtonIsDisplayed()
    {
        LOG.info("Assert Cancel button is displayed");
        assertTrue(browser.isElementDisplayed(cancelButton), "Cancel button is not displayed");
        return this;
    }

    public CreateContentPage typeName(String name)
    {
        Utils.clearAndType(nameField, name);
        return this;
    }

    public CreateContentPage typeTitle(String title)
    {
        Utils.clearAndType(titleField, title);
        return this;
    }

    public CreateContentPage typeContent(String content)
    {
        Utils.clearAndType(contentField, content);
        return this;
    }

    public CreateContentPage typeDescription(String description)
    {
        Utils.clearAndType(descriptionField, description);
        return this;
    }

    public DocumentDetailsPage clickCreate()
    {
        submitButton.click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    public void clickCancel()
    {
        cancelButton.click();
    }

    public CreateContentPage sendInputForHTMLContent(String inputHTMLContent)
    {
        LOG.info("Send input for HTML file types");
        tinyMceEditor.setText(inputHTMLContent);
        browser.focusOnWebElement(nameField);
        return this;
    }
}