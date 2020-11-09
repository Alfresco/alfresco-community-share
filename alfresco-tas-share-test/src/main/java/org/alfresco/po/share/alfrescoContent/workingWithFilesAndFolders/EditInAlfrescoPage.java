package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import java.util.List;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class EditInAlfrescoPage extends SiteCommon<EditInAlfrescoPage>
{
    @FindBy (css = "button[id*='cancel']")
    protected WebElement cancelButton;

    @FindBy (css = ".heading")
    private WebElement editContentHeader;

    @FindBy (css = "input[id*='name']")
    private WebElement nameInput;

    @RenderWebElement
    @FindBy (css = "textarea[id*='_content']")
    private WebElement contentTextarea;

    @FindBy (css = "input[id*='title']")
    private WebElement titleInput;

    @FindBy (css = "textarea[id*='description']")
    private WebElement descriptionTextarea;

    @FindBy (css = "button[id*='form']")
    private List<WebElement> buttonsList;

    private final By saveButton = By.xpath("//button[contains(text(), 'Save')]");

    @Autowired
    private DocumentLibraryPage documentLibraryPage;

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/inline-edit?nodeRef=workspace://SpacesStore/", getCurrentSiteName());
    }

    public DocumentLibraryPage clickButton(String buttonName)
    {
        browser.findFirstElementWithValue(buttonsList, buttonName).click();
        return (DocumentLibraryPage) documentLibraryPage.renderedPage();
    }

    public DocumentLibraryPage clickSaveButton()
    {
        LOG.info("Click Save button");
        browser.findElement(saveButton).click();
        return (DocumentLibraryPage) documentLibraryPage.renderedPage();
    }

    public EditInAlfrescoPage enterDocumentDetails(String name, String content, String title, String description)
    {
        typeName(name);
        typeContent(content);
        typeTitle(title);
        typeDescription(description);

        return this;
    }

    private void typeName(String name) {
        LOG.info("Type name: {}", name);
        clearAndType(nameInput, name);
    }

    public void typeContent(String content)
    {
        LOG.info("Type content: {}", content);
        clearAndType(contentTextarea, content);
    }

    private void typeTitle(String title) {
        LOG.info("Type title: {}", title);
        clearAndType(titleInput, title);
    }

    private void typeDescription(String description) {
        LOG.info("Type description: {}", description);
        clearAndType(descriptionTextarea, description);
    }
}
