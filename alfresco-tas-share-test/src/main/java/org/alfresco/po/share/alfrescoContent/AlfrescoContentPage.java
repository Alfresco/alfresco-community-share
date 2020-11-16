package org.alfresco.po.share.alfrescoContent;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

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

    @Autowired
    private DeleteDialog deleteDialog;

    @Autowired
    private StartWorkflowPage startWorkflowPage;

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

    private By documentsFilter = By.cssSelector("a.filter-link");
    private By selectedFilter = By.cssSelector(".filterLink .selected");
    private By documentsFilterHeaderTitle = By.cssSelector("div[id$='default-description'] .message");
    private By documentsRootBreadcrumb = By.cssSelector("div[class='crumb documentDroppable documentDroppableHighlights']");
    protected String folderInFilterElement = "//tr[starts-with(@class,'ygtvrow documentDroppable')]//span[text()='%s']";
    protected String contentRow = "//h3[@class='filename']//a[text()='%s']/../../../../..";
    protected String breadcrumb = "//div[@class='crumb documentDroppable documentDroppableHighlights']//a[text()='%s']";
    private String templateName = "//a[@class='yuimenuitemlabel']//span[text()='%s']";
    protected By selectCheckBox = By.cssSelector("input[name='fileChecked']");
    private By selectMenu = By.cssSelector("button[id$='fileSelect-button-button']");
    private By selectMenuDocumentsOption = By.cssSelector(".selectDocuments");
    private By selectMenuFoldersOption = By.cssSelector(".selectFolders");
    private By selectMenuAllOption = By.cssSelector(".selectAll");
    private By selectNoneOption = By.cssSelector(".selectNone");
    private By selectMenuInvertSelectionOption = By.cssSelector(".selectInvert");
    private By selectedItemsActionNames = By.cssSelector("div[id$=default-selectedItems-menu] a[class='yuimenuitemlabel'] span");
    private By startWorkflowFromSelectedItems = By.cssSelector(".onActionAssignWorkflow");
    private By deleteFromSelectedItems = By.cssSelector(".onActionDelete");

    public T clickCreate()
    {
        LOG.info("Click Create");
        browser.waitUntilElementClickable(createButton).click();
        browser.waitUntilElementVisible(createOptionsArea);
        return (T) this;
    }

    public CreateContentPage clickTextPlain()
    {
        LOG.info("Click Plain Text...");
        browser.waitUntilElementVisible(createTextFileOption).click();
        return (CreateContentPage) createContentPage.renderedPage();
    }

    public CreateContentPage clickXml()
    {
        LOG.info("Click XML...");
        browser.waitUntilElementVisible(createXmlFileOption).click();
        return (CreateContentPage) createContentPage.renderedPage();
    }

    public CreateContentPage clickHtml()
    {
        LOG.info("Click HTML...");
        browser.waitUntilElementVisible(createHtmlFileOption).click();
        return (CreateContentPage) createContentPage.renderedPage();
    }

    public NewFolderDialog clickFolder()
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
        LOG.info("Assert folder {} is displayed in documents filter from left side", folder.getName());
        WebElement folderLink = browser.waitUntilElementVisible(By.xpath(String.format(folderInFilterElement, folder.getName())));
        assertTrue(browser.isElementDisplayed(folderLink),
            String.format("Folder %s is displayed in filter", folder.getName()));
        return (T) this;
    }

    public T waitForCurrentFolderBreadcrumb(String folderName)
    {
        LOG.info("Wait for folder breadcrumb {}", folderName);
        browser.waitUntilElementContainsText(currentBreadcrumb, folderName);
        return (T) this;
    }

    public T waitForCurrentFolderBreadcrumb(FolderModel folder)
    {
        return waitForCurrentFolderBreadcrumb(folder.getName());
    }

    public T assertFolderIsDisplayedInBreadcrumb(FolderModel folder)
    {
        LOG.info(String.format("Assert folder %s is displayed in breadcrumb", folder.getName()));
        assertTrue(browser.isElementDisplayed(By.xpath(String.format(breadcrumb, folder.getName()))),
            String.format("Folder %s is displayed in breadcrumb", folder.getName()));
        return (T) this;
    }

    public T assertDocumentsRootBreadcrumbIsDisplayed()
    {
        LOG.info("Assert Documents root breadcrumb is displayed");
        browser.waitUntilElementVisible(documentsRootBreadcrumb);
        assertTrue(getBrowser().isElementDisplayed(documentsRootBreadcrumb), "Documents root breadcrumb is displayed");
        return (T) this;
    }

    public T clickFolderFromFilter(FolderModel folder)
    {
        LOG.info("Click folder '%s' from filter {}", folder.getName());
        browser.waitUntilElementVisible(By.xpath(String.format(folderInFilterElement, folder.getName()))).click();
        waitForCurrentFolderBreadcrumb(folder);
        return (T) this;
    }

    public T clickFolderFromBreadcrumb(String folderName)
    {
        LOG.info("Click folder {} from breadcrumb", folderName);
        browser.findElement(By.xpath(String.format(breadcrumb, folderName))).click();
        waitForCurrentFolderBreadcrumb(folderName);

        return (T) this;
    }

    public T clickFolderFromBreadcrumb(FolderModel folder)
    {
        return clickFolderFromBreadcrumb(folder.getName());
    }

    public T clickFolderUpButton()
    {
        LOG.info("Click folder up button");
        folderUp.click();
        return (T) this;
    }

    public T uploadContent(FileModel file)
    {
        LOG.info("Upload file {}", file.getName());
        uploadButton.click();
        uploadFileDialog.renderedPage();
        uploadFileDialog.uploadFile(file);
        uploadFileDialog.waitForUploadDialogToDisappear();

        return (T) this;
    }

    public T createFileFromTemplate(FileModel templateFile)
    {
        LOG.info("Create new file from template {}", templateFile);
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
            LOG.info("Check content: {}", content.getName());
            getContentRow(content.getName()).findElement(selectCheckBox).click();
        }
        return (T) this;
    }

    public T assertSelectedItemsMenuIsEnabled()
    {
        LOG.info("Assert Selected Items is enabled");
        assertTrue(selectedItemsLink.isEnabled(), "Selected Items is not enabled");
        return (T) this;
    }

    public T assertSelectedItemsMenuIsDisabled()
    {
        LOG.info("Assert Selected Items is enabled");
        assertFalse(selectedItemsLink.isEnabled(), "Selected Items is not enabled");
        return (T) this;
    }

    public T clickSelectedItems()
    {
        LOG.info("Click Selected Items");
        browser.waitUntilElementClickable(selectedItemsLink).click();
        browser.waitUntilElementHasAttribute(selectedItemsActions, "class", "visible");

        return (T) this;
    }

    public T assertActionsInSelectedItemsMenuEqualsTo(String... actions)
    {
        LOG.info("Assert available actions from selected items menu are {}", Arrays.asList(actions));
        List<WebElement> items = getBrowser().findElements(selectedItemsActionNames);
        String[] values = getBrowser().getTextFromElementList(items).toArray(new String[0]);
        Arrays.sort(values);
        Arrays.sort(actions);
        Assert.assertEquals(values, actions, "Not all actions are found");

        return (T) this;
    }

    public T clickSelectMenu()
    {
        LOG.info("Click Select menu");
        browser.findElement(selectMenu).click();
        return (T) this;
    }

    public T selectDocumentsFromSelectMenu()
    {
        LOG.info("Select Document option from Select menu");
        browser.waitUntilElementVisible(selectMenuDocumentsOption).click();
        return (T) this;
    }

    public T selectFolderFromSelectMenu()
    {
        LOG.info("Select Folder option from Select menu");
        browser.waitUntilElementVisible(selectMenuFoldersOption).click();
        return (T) this;
    }

    public T selectAllFromSelectMenu()
    {
        LOG.info("Select All option from Select menu");
        browser.waitUntilElementVisible(selectMenuAllOption).click();
        return (T) this;
    }

    public T selectNoneFromSelectMenu()
    {
        LOG.info("Select None option from Select menu");
        browser.waitUntilElementVisible(selectNoneOption).click();
        return (T) this;
    }

    public T selectInvertSelectionFromSelectMenu()
    {
        LOG.info("Select All option from Select menu");
        browser.waitUntilElementVisible(selectMenuInvertSelectionOption).click();
        return (T) this;
    }

    public T assertContentsAreChecked(ContentModel... contentModels)
    {
        for(ContentModel content:contentModels)
        {
            LOG.info("Assert content {} is checked", content.getName());
            assertTrue(getContentRow(content.getName()).findElement(selectCheckBox).isSelected(),
                String.format("Content %s is not checked", content.getName()));
        }
        return (T) this;
    }

    public T assertContentsAreNotChecked(ContentModel... contentModels)
    {
        for(ContentModel content:contentModels)
        {
            LOG.info("Assert content {} is checked", content.getName());
            assertFalse(getContentRow(content.getName()).findElement(selectCheckBox).isSelected(),
                String.format("Content %s is not checked", content.getName()));
        }
        return (T) this;
    }

    public CopyMoveUnzipToDialog clickCopyToFromSelectedItems()
    {
        LOG.info("Click Copy To...");
        browser.waitUntilElementVisible(copyToFromSelectedItems).click();

        return (CopyMoveUnzipToDialog) copyMoveDialog.renderedPage();
    }

    public StartWorkflowPage clickStartWorkflowFromSelectedItems()
    {
        LOG.info("Click Stat Workflow...");
        browser.waitUntilElementVisible(startWorkflowFromSelectedItems).click();

        return (StartWorkflowPage) startWorkflowPage.renderedPage();
    }

    public DeleteDialog clickDeleteFromSelectedItems()
    {
        LOG.info("Click Delete");
        browser.waitUntilElementVisible(deleteFromSelectedItems).click();

        return (DeleteDialog) deleteDialog.renderedPage();
    }

    public T selectFromDocumentsFilter(DocumentsFilter documentFilter)
    {
        LOG.info("Select document filter {}", documentFilter.toString());
        List<WebElement> filters = getBrowser().findElements(documentsFilter);
        browser.findFirstElementWithValue(filters, getDocumentsFilterValue(documentFilter)).click();
        browser.waitUntilElementVisible(selectedFilter);
        return (T) this;
    }

    public T assertDocumentsFilterHeaderTitleEqualsTo(String expectedHeaderTitle)
    {
        LOG.info("Assert documents filter header title '{}' is displayed", expectedHeaderTitle);
        assertEquals(getBrowser().waitUntilElementVisible(documentsFilterHeaderTitle).getText(), expectedHeaderTitle,
            String.format("%s header title is not equals to", expectedHeaderTitle));
        return (T) this;
    }

    private String getDocumentsFilterValue(DocumentsFilter documentsFilter)
    {
        String filterValue = "";
        switch (documentsFilter)
        {
            case ALL_DOCUMENTS:
                filterValue = language.translate("documentLibrary.documentsFilter.all");
                break;
            case EDITING_ME:
                filterValue = language.translate("documentLibrary.documentsFilter.editingMe");
                break;
            case EDITING_OTHERS:
                filterValue = language.translate("documentLibrary.documentsFilter.othersEditing");
                break;
            case RECENTLY_MODIFIED:
                filterValue = language.translate("documentLibrary.documentsFilter.recentlyModified");
                break;
            case RECENTLY_ADDED:
                filterValue = language.translate("documentLibrary.documentsFilter.recentlyAdded");
                break;
            case FAVORITES:
                filterValue = language.translate("documentLibrary.documentsFilter.favorites");
                break;
            default:
                break;
        }
        return filterValue;
    }

    public ContentAction usingContent(ContentModel contentModel)
    {
        return new ContentAction(contentModel,this, documentDetailsPage, copyMoveDialog, deleteDialog);
    }

    public enum DocumentsFilter
    {
        ALL_DOCUMENTS,
        EDITING_ME,
        EDITING_OTHERS,
        RECENTLY_MODIFIED,
        RECENTLY_ADDED,
        FAVORITES
    }
}
