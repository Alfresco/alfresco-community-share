package org.alfresco.po.share.alfrescoContent;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

public abstract class AlfrescoContentPage<T> extends SharePage<AlfrescoContentPage<T>>
{
    @Autowired
    private CreateContentPage createContentPage;

    @Autowired
    private NewFolderDialog newFolderDialog;

    @Autowired
    private UploadFileDialog uploadFileDialog;

    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    @Autowired
    private CopyMoveUnzipToDialog copyMoveDialog;

    @RenderWebElement
    @FindBy(css = "button[id$='createContent-button-button']")
    private WebElement createButton;

    @FindBy(css = "div[id$='_default-createContent-menu'][style*='visible']")
    private WebElement createOptionsArea;

    @FindBy(css = "span.text-file")
    private WebElement createTextFileOption;

    @FindBy(css = "span.xml-file")
    private WebElement createXmlFileOption;

    @FindBy(css = "span.html-file")
    private WebElement createHtmlFileOption;

    @FindBy(css = ".folder-file")
    private WebElement createFolderOption;

    @FindBy(css = ".crumb .label a")
    private WebElement currentBreadcrumb;

    @FindBy(css = "button[id$='folderUp-button-button']")
    private WebElement folderUp;

    @FindBy(css = "button[id$='-fileUpload-button-button']")
    private WebElement uploadButton;

    @FindBy(css = "div[id$='createContent-menu']>div>ul:nth-of-type(2)>li:nth-of-type(1) span")
    private WebElement createFileFromTemplate;

    @FindBy(css = "div[id$='createContent-menu']>div>ul:nth-of-type(2)>li:nth-of-type(2) span")
    private WebElement createFolderFromTemplate;

    @FindBy (css = "button[id$='selectedItems-button-button']")
    private WebElement selectedItemsLink;

    @FindBy (css = "div[id$=default-selectedItems-menu]")
    private WebElement selectedItemsActions;

    @FindBy (css = ".onActionCopyTo")
    private WebElement copyToFromSelectedItems;

    protected String folderInFilterElement = "//tr[starts-with(@class,'ygtvrow documentDroppable')]//span[text()='%s']";
    protected String contentRow = "//h3[@class='filename']//a[text()='%s']/../../../../..";
    protected String breadcrumb = "//div[@class='crumb documentDroppable documentDroppableHighlights']//a[text()='%s']";
    private String templateName = "//a[@class='yuimenuitemlabel']//span[text()='%s']";
    private By selectCheckBox = By.cssSelector("input[name='fileChecked']");

    public T clickCreate()
    {
        LOG.info("Click Create");
        browser.waitUntilElementClickable(createButton).click();
        browser.waitUntilElementVisible(createOptionsArea);
        return (T) this;
    }

    public CreateContentPage clickCreateTextPlain()
    {
        LOG.info("Click Plain Text...");
        browser.waitUntilElementVisible(createTextFileOption).click();
        return (CreateContentPage) createContentPage.renderedPage();
    }

    public CreateContentPage clickCreateXml()
    {
        LOG.info("Click XML...");
        browser.waitUntilElementVisible(createXmlFileOption).click();
        return (CreateContentPage) createContentPage.renderedPage();
    }

    public CreateContentPage clickCreateHtml()
    {
        LOG.info("Click HTML...");
        browser.waitUntilElementVisible(createHtmlFileOption).click();
        return (CreateContentPage) createContentPage.renderedPage();
    }

    public NewFolderDialog clickCreateFolder()
    {
        LOG.info("Click Create Folder");
        browser.waitUntilElementVisible(createFolderOption).click();
        return (NewFolderDialog) newFolderDialog.renderedPage();
    }

    protected WebElement getContentRow(String contentName)
    {
        return browser.waitWithRetryAndReturnWebElement(By.xpath(String.format(contentRow, contentName)), 1, WAIT_30);
    }

    public T assertFolderIsDisplayedInFilter(FolderModel folder)
    {
        LOG.info(String.format("Assert folder %s is displayed in documents filter from left side", folder.getName()));
        WebElement folderLink = browser.waitUntilElementVisible(By.xpath(String.format(folderInFilterElement, folder.getName())));
        Assert.assertTrue(browser.isElementDisplayed(folderLink),
            String.format("Folder %s is displayed in filter", folder.getName()));
        return (T) this;
    }

    public T waitForCurrentBreadcrumb(String folderName)
    {
        browser.waitUntilElementContainsText(currentBreadcrumb, folderName);
        return (T) this;
    }

    public T waitForCurrentBreadcrumb(FolderModel folder)
    {
        return waitForCurrentBreadcrumb(folder.getName());
    }

    public T assertFolderIsDisplayedInBreadcrumb(FolderModel folder)
    {
        LOG.info(String.format("Assert folder %s is displayed in breadcrumb", folder.getName()));
        Assert.assertTrue(browser.isElementDisplayed(By.xpath(String.format(breadcrumb, folder.getName()))),
            String.format("Folder %s is displayed in breadcrumb", folder.getName()));
        return (T) this;
    }

    public T clickFolderFromFilter(FolderModel folder)
    {
        LOG.info(String.format("Click folder '%s' from filter", folder.getName()));
        browser.waitUntilElementVisible(By.xpath(String.format(folderInFilterElement, folder.getName()))).click();
        waitForCurrentBreadcrumb(folder);
        return (T) this;
    }

    public T clickFolderFromBreadcrumb(String folderName)
    {
        LOG.info(String.format("Click folder '%s' from breadcrumb", folderName));
        browser.findElement(By.xpath(String.format(breadcrumb, folderName))).click();
        waitForCurrentBreadcrumb(folderName);
        return (T) this;
    }

    public T clickFolderFromBreadcrumb(FolderModel folder)
    {
        return clickFolderFromBreadcrumb(folder.getName());
    }

    public T clickFolderUp()
    {
        LOG.info("Click folder up button");
        folderUp.click();
        return (T) this;
    }

    public T uploadContent(FileModel file)
    {
        LOG.info(String.format("Upload file %s", file.getName()));
        uploadButton.click();
        uploadFileDialog.renderedPage();
        uploadFileDialog.uploadFile(file);
        uploadFileDialog.waitForUploadDialogToDisappear();
        return (T) this;
    }

    public T createFileFromTemplate(FileModel templateFile)
    {
        LOG.info(String.format("Create new file from template %s", templateFile));
        browser.mouseOver(createButton);
        browser.mouseOver(createFileFromTemplate);
        createFileFromTemplate.click();
        browser.waitUntilElementVisible(By.xpath(String.format(templateName, templateFile.getName()))).click();
        waitUntilNotificationMessageDisappears();
        return (T) this;
    }

    public NewFolderDialog clickCreateFolderFromTemplate(String folderTemplateName)
    {
        LOG.info(String.format("Create new folder from template %s", folderTemplateName));
        browser.mouseOver(createButton);
        browser.mouseOver(createFolderFromTemplate);
        createFolderFromTemplate.click();
        browser.waitUntilElementVisible(By.xpath(String.format(templateName, folderTemplateName))).click();
        return newFolderDialog;
    }

    public NewFolderDialog clickCreateFolderFromTemplate(FolderModel folderTemplate)
    {
        return clickCreateFolderFromTemplate(folderTemplate.getName());
    }

    public T checkContent(ContentModel... contentsToSelect)
    {
        for(ContentModel content : contentsToSelect)
        {
            LOG.info(String.format("Check content: %s", content.getName()));
            getContentRow(content.getName()).findElement(selectCheckBox).click();
        }
        return (T) this;
    }

    public T clickSelectItems()
    {
        LOG.info("Click Selected Items");
        browser.waitUntilElementClickable(selectedItemsLink).click();
        browser.waitUntilElementHasAttribute(selectedItemsActions, "class", "visible");
        return (T) this;
    }

    public CopyMoveUnzipToDialog clickCopyToFromSelectedItems()
    {
        LOG.info("Click Copy To...");
        browser.waitUntilElementVisible(copyToFromSelectedItems).click();
        return (CopyMoveUnzipToDialog) copyMoveDialog.renderedPage();
    }

    public ContentAction usingContent(ContentModel contentModel)
    {
        return new ContentAction(contentModel, this, documentDetailsPage, copyMoveDialog);
    }
}
