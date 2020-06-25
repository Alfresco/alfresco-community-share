package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import java.util.List;

import org.alfresco.common.Utils;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class EditInAlfrescoPage extends SiteCommon<EditInAlfrescoPage>
{
    @FindBy (css = "button[id*='submit']")
    protected WebElement saveButton;
    @FindBy (css = "button[id*='cancel']")
    protected WebElement cancelButton;
    @Autowired
    DocumentLibraryPage documentLibraryPage;
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

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/inline-edit?nodeRef=workspace://SpacesStore/", getCurrentSiteName());
    }

    /**
     * Click on a submit form button
     *
     * @param buttonName to be clicked: Save, Cancel
     */
    public DocumentLibraryPage clickButton(String buttonName)
    {
        browser.findFirstElementWithValue(buttonsList, buttonName).click();
        return (DocumentLibraryPage) documentLibraryPage.renderedPage();
    }

    public void typeContent(String content)
    {
        Utils.clearAndType(contentTextarea, content);
    }

    /***
     * Fill in document details fields
     *
     * @param name
     * @param content
     * @param title
     * @param description
     */
    public void sendDocumentDetailsFields(String name, String content, String title, String description)
    {
        Utils.clearAndType(nameInput, name);
        typeContent(content);
        Utils.clearAndType(titleInput, title);
        Utils.clearAndType(descriptionTextarea, description);
    }

    /**
     * Method to click save button
     */
    public DocumentLibraryPage clickSaveButton()
    {
        saveButton.sendKeys(Keys.ENTER);

        return (DocumentLibraryPage) documentLibraryPage.renderedPage();
    }
}
