package org.alfresco.po.share.alfrescoContent.buildingContent;

import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class CreateContentPage extends SharePage2<CreateContentPage>
{
    private final By nameField = By.cssSelector("input[name='prop_cm_name']");
    private final By contentField = By.cssSelector("textarea[name='prop_cm_content']");
    private final By titleField = By.cssSelector("input[name='prop_cm_title']");
    private final By descriptionField = By.cssSelector("textarea[name='prop_cm_description']");
    private final By nameInputIsMandatoryMarker = By.cssSelector("label[for$='default_prop_cm_name'] .mandatory-indicator");
    private final By submitButton = By.cssSelector("button[id$='submit-button']");
    private final By cancelButton = By.cssSelector("button[id*='form-cancel-button']");

    public CreateContentPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return null;
    }

    public CreateContentPage assertCreateContentPageIsOpened()
    {
        log.info("Assert Content page is opened");
        assertTrue(webElementInteraction.getCurrentUrl().contains("create-content"), "Create content page is not opened");
        return this;
    }

    public CreateContentPage assertNameInputHasMandatoryMarker()
    {
        log.info("Assert Name input has mandatory marker");
        assertTrue(webElementInteraction.isElementDisplayed(nameInputIsMandatoryMarker), "Name input is not mandatory");
        return this;
    }

    public CreateContentPage assertCancelButtonIsDisplayed()
    {
        log.info("Assert Cancel button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(cancelButton), "Cancel button is not displayed");
        return this;
    }

    public CreateContentPage typeName(String name)
    {
        webElementInteraction.clearAndType(nameField, name);
        return this;
    }

    public CreateContentPage typeTitle(String title)
    {
        webElementInteraction.clearAndType(titleField, title);
        return this;
    }

    public CreateContentPage typeContent(String content)
    {
        webElementInteraction.clearAndType(contentField, content);
        return this;
    }

    public CreateContentPage typeDescription(String description)
    {
        webElementInteraction.clearAndType(descriptionField, description);
        return this;
    }

    public DocumentDetailsPage clickCreate()
    {
        webElementInteraction.waitUntilElementIsVisible(submitButton);
        webElementInteraction.clickElement(submitButton);
        return new DocumentDetailsPage(webDriver);
    }

    public void clickCancel()
    {
        webElementInteraction.waitUntilElementIsVisible(cancelButton);
        webElementInteraction.clickElement(cancelButton);
    }

    public CreateContentPage sendInputForHTMLContent(String inputHTMLContent)
    {
        log.info("Send input for HTML file types");
        TinyMceEditor tinyMceEditor = new TinyMceEditor(webDriver);
        tinyMceEditor.setText(inputHTMLContent);
        webElementInteraction.focusOnWebElement(webElementInteraction.findElement(nameField));
        return this;
    }
}