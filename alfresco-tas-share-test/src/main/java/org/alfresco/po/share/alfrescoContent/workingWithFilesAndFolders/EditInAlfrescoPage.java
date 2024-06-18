package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import static org.testng.Assert.assertEquals;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class EditInAlfrescoPage extends SiteCommon<EditInAlfrescoPage>
{
    @FindBy (css = "button[id*='cancel']")
    protected WebElement cancelButton;

    @FindBy (css = ".heading")
    private WebElement editContentHeader;

    @FindBy (css = "input[id*='name']")
    private WebElement nameInput;

//    @FindBy (css = "textarea[id*='_content']")
//    private WebElement contentTextarea;
    private final By nameField = By.cssSelector("input[name='prop_cm_name']");
    private final By titleField = By.cssSelector("input[name='prop_cm_title']");
    private final By descriptionField = By.cssSelector("textarea[name='prop_cm_description']");
    private final By contentField=By.cssSelector("textarea[id*='_content']");
    private By contentTextarea = By.cssSelector("textarea[id*='_content']");

    @FindBy (css = "input[id*='title']")
    private WebElement titleInput;

    @FindBy (css = "textarea[id*='description']")
    private WebElement descriptionTextarea;

    private By saveButton = By.cssSelector("button[id*='submit']");
    private By buttonsList = By.cssSelector("button[id*='form']");

    public EditInAlfrescoPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/inline-edit?nodeRef=workspace://SpacesStore/", getCurrentSiteName());
    }

    public DocumentLibraryPage clickButton(String buttonName)
    {
        waitInSeconds(3);
        findFirstElementWithValue(findElements(buttonsList), buttonName);
        return new DocumentLibraryPage(webDriver);
    }

    public DocumentLibraryPage clickSaveButton()
    {
        waitInSeconds(2);
        scrollToElement(findElement(saveButton));
        log.info("Click Save button");
        clickElement(saveButton);
        return new DocumentLibraryPage(webDriver);
    }

    public EditInAlfrescoPage enterDocumentDetails(String name, String content, String title, String description)
    {
        typeName(name);
        typeContent(content);
        typeTitle(title);
        typeDescription(description);

        return this;
    }

    public EditInAlfrescoPage typeName(String name)
    {
        log.info("Type name: {}", name);
        clearAndType(nameField,name);
        return this;
    }

    public EditInAlfrescoPage typeContent(String content)
    {
        log.info("Type content: {}", content);
        clearAndType(contentField, content);
        return this;
    }

    public EditInAlfrescoPage typeTitle(String title)
    {
        log.info("Type title: {}", title);
        clearAndType(titleField, title);
        return this;
    }

    public EditInAlfrescoPage typeDescription(String description)
    {
        log.info("Type description: {}", description);
        clearAndType(descriptionField, description);
        return this;
    }

    public EditInAlfrescoPage assertEditInAlfrescoPageTitleEquals(String title)
    {
        log.info("Verify Edit in Alfresco page title {}", title);
        assertEquals(getPageTitle(), title, String.format("Edit in page title should be '"+getPageTitle()+"'(s)' not matched %s ", title));
        return this;
    }
}
