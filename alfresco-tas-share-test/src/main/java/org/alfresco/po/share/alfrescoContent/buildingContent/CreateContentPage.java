package org.alfresco.po.share.alfrescoContent.buildingContent;

import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.assertTrue;

public class CreateContentPage extends SharePage2<CreateContentPage>
{
    @RenderWebElement
    private final By nameField = By.cssSelector("input[name='prop_cm_name']");
    private final By contentField = By.cssSelector("textarea[name='prop_cm_content']");
    private final By titleField = By.cssSelector("input[name='prop_cm_title']");
    private final By descriptionField = By.cssSelector("textarea[name='prop_cm_description']");
    private final By nameInputIsMandatoryMarker = By.cssSelector("label[for$='default_prop_cm_name'] .mandatory-indicator");
    @RenderWebElement
    private final By submitButton = By.cssSelector("button[id$='submit-button']");
    private final By cancelButton = By.cssSelector("button[id*='form-cancel-button']");

    public CreateContentPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    @Override
    public String getRelativePath()
    {
        return null;
    }

    public CreateContentPage assertCreateContentPageIsOpened()
    {
        LOG.info("Assert Content page is opened");
        assertTrue(getBrowser().getCurrentUrl().contains("create-content"), "Create content page is not opened");
        return this;
    }

    public CreateContentPage assertNameInputHasMandatoryMarker()
    {
        LOG.info("Assert Name input has mandatory marker");
        assertTrue(getBrowser().isElementDisplayed(nameInputIsMandatoryMarker), "Name input is not mandatory");
        return this;
    }

    public CreateContentPage assertCreateButtonIsDisplayed()
    {
        LOG.info("Assert Create button is displayed");
        assertTrue(getBrowser().isElementDisplayed(submitButton), "Create button is not displayed");
        return this;
    }

    public CreateContentPage assertCancelButtonIsDisplayed()
    {
        LOG.info("Assert Cancel button is displayed");
        assertTrue(getBrowser().isElementDisplayed(cancelButton), "Cancel button is not displayed");
        return this;
    }

    public CreateContentPage typeName(String name)
    {
        clearAndType(nameField, name);
        return this;
    }

    public CreateContentPage typeTitle(String title)
    {
        clearAndType(titleField, title);
        return this;
    }

    public CreateContentPage typeContent(String content)
    {
        clearAndType(contentField, content);
        return this;
    }

    public CreateContentPage typeDescription(String description)
    {
        clearAndType(descriptionField, description);
        return this;
    }

    public DocumentDetailsPage clickCreate()
    {
        getBrowser().findElement(submitButton).click();
        return (DocumentDetailsPage) new DocumentDetailsPage(browser).renderedPage();
    }

    public void clickCancel()
    {
        getBrowser().findElement(cancelButton).click();
    }

    public CreateContentPage sendInputForHTMLContent(String inputHTMLContent)
    {
        LOG.info("Send input for HTML file types");
        TinyMceEditor tinyMceEditor = new TinyMceEditor(browser);
        tinyMceEditor.setText(inputHTMLContent);
        getBrowser().focusOnWebElement(getBrowser().findElement(nameField));
        return this;
    }
}