package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import java.util.List;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class EditInAlfrescoPage extends SiteCommon<EditInAlfrescoPage>
{
    @FindBy (css = "button[id*='cancel']")
    protected WebElement cancelButton;

    @FindBy (css = ".heading")
    private WebElement editContentHeader;

    @FindBy (css = "input[id*='name']")
    private WebElement nameInput;

    @FindBy (css = "textarea[id*='_content']")
    private WebElement contentTextarea;

    @FindBy (css = "input[id*='title']")
    private WebElement titleInput;

    @FindBy (css = "textarea[id*='description']")
    private WebElement descriptionTextarea;

    @FindBy (css = "button[id*='form']")
    private List<WebElement> buttonsList;

    private By saveButton = By.cssSelector("button[id*='submit']");

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
        webElementInteraction.findFirstElementWithValue(buttonsList, buttonName);
        return new DocumentLibraryPage(webDriver);
    }

    public DocumentLibraryPage clickSaveButton()
    {
        LOG.info("Click Save button");
        webElementInteraction.clickElement(saveButton);
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

    private void typeName(String name)
    {
        LOG.info("Type name: {}", name);
        webElementInteraction.clearAndType(nameInput, name);
    }

    public void typeContent(String content)
    {
        LOG.info("Type content: {}", content);
        webElementInteraction.clearAndType(contentTextarea, content);
    }

    private void typeTitle(String title)
    {
        LOG.info("Type title: {}", title);
        webElementInteraction.clearAndType(titleInput, title);
    }

    private void typeDescription(String description)
    {
        LOG.info("Type description: {}", description);
        webElementInteraction.clearAndType(descriptionTextarea, description);
    }
}
