package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageObject
public class EditInAlfrescoPage extends SiteCommon<EditInAlfrescoPage>
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @FindBy(css = ".heading")
    private WebElement editContentHeader;

    @FindBy(css = "input[id*='name']")
    private WebElement nameInput;

    @RenderWebElement
    @FindBy(css = "textarea[id*='_content']")
    private WebElement contentTextarea;

    @FindBy(css = "input[id*='title']")
    private WebElement titleInput;

    @FindBy(css = "textarea[id*='description']")
    private WebElement descriptionTextarea;

    @FindBy(css = "button[id*='submit']")
    protected WebElement saveButton;

    @FindBy(css = "button[id*='cancel']")
    protected WebElement cancelButton;

    @FindBy(css = "button[id*='form']")
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
        for (WebElement aButtonsList : buttonsList)
        {
            if (aButtonsList.getText().equals(buttonName))
                aButtonsList.click();
        }

        return (DocumentLibraryPage) documentLibraryPage.renderedPage();
    }

    public void typeContent(String content)
    {
        contentTextarea.clear();
        contentTextarea.sendKeys(content);
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
        nameInput.clear();
        nameInput.sendKeys(name);

        typeContent(content);

        titleInput.clear();
        titleInput.sendKeys(title);

        descriptionTextarea.clear();
        descriptionTextarea.sendKeys(description);
    }
    
    /**
     * Method to click save button
     */
    public void clickSaveButton()
    {
        saveButton.sendKeys(Keys.ENTER);
    }
}
