package org.alfresco.po.share.alfrescoContent;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.SharePage2;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

public abstract class AlfrescoContentPage<T> extends SharePage2<AlfrescoContentPage<T>>
{
    @Autowired
    private CopyMoveUnzipToDialog copyMoveDialog;

    @RenderWebElement
    private final By createButton = By.cssSelector("button[id$='createContent-button-button']");
    private final By createOptionsArea = By.cssSelector("div[id$='_default-createContent-menu'][style*='visible']");
    private final By createTextFileOption = By.cssSelector("span.text-file");
    private final By createXmlFileOption = By.cssSelector("span.xml-file");
    private final By createHtmlFileOption = By.cssSelector("span.html-file");
    private final By createFolderOption = By.cssSelector(".folder-file");
    private final By currentBreadcrumb = By.cssSelector(".crumb .label a");
    private final By folderUp = By.cssSelector("button[id$='folderUp-button-button']");
    private final By uploadButton = By.cssSelector("button[id$='-fileUpload-button-button']");
    private final By createFileFromTemplate = By.cssSelector("div[id$='createContent-menu']>div>ul:nth-of-type(2)>li:nth-of-type(1) span");
    private final By createFolderFromTemplate = By.cssSelector("div[id$='createContent-menu']>div>ul:nth-of-type(2)>li:nth-of-type(2) span");
    private final By selectedItemsLink = By.cssSelector("button[id$='selectedItems-button-button']");
    private final By selectedItemsActions = By.cssSelector("div[id$=default-selectedItems-menu]");
    private final By copyToFromSelectedItems = By.cssSelector(".onActionCopyTo");
    private final By documentsFilter = By.cssSelector("a.filter-link");
    private final By selectedFilter = By.cssSelector(".filterLink .selected");
    private final By documentsFilterHeaderTitle = By.cssSelector("div[id$='default-description'] .message");
    private final By documentsRootBreadcrumb = By.cssSelector("div[class='crumb documentDroppable documentDroppableHighlights']");
    private final String folderInFilterElement = "//tr[starts-with(@class,'ygtvrow documentDroppable')]//span[text()='%s']";
    protected final String contentRow = "//h3[@class='filename']//a[text()='%s']/../../../../..";
    protected final By selectCheckBox = By.cssSelector("input[name='fileChecked']");
    private final By selectMenu = By.cssSelector("button[id$='fileSelect-button-button']");
    private final By selectedItemsActionNames = By.cssSelector("div[id$=default-selectedItems-menu] a[class='yuimenuitemlabel'] span");
    private final By startWorkflowFromSelectedItems = By.cssSelector(".onActionAssignWorkflow");
    private final By deleteFromSelectedItems = By.cssSelector(".onActionDelete");

    private String breadcrumb = "//div[@class='crumb documentDroppable documentDroppableHighlights']//a[text()='%s']";
    private String templateName = "//a[@class='yuimenuitemlabel']//span[text()='%s']";

    public AlfrescoContentPage<T> clickCreate()
    {
        LOG.info("Click Create");
        getBrowser().waitUntilElementClickable(createButton).click();
        getBrowser().waitUntilElementVisible(createOptionsArea);
        return this;
    }

    public CreateContentPage clickTextPlain()
    {
        LOG.info("Click Plain Text...");
        getBrowser().waitUntilElementVisible(createTextFileOption).click();
        return (CreateContentPage) new CreateContentPage(browser).renderedPage();
    }

    public CreateContentPage clickXml()
    {
        LOG.info("Click XML...");
        getBrowser().waitUntilElementVisible(createXmlFileOption).click();
        return (CreateContentPage) new CreateContentPage(browser).renderedPage();
    }

    public CreateContentPage clickHtml()
    {
        LOG.info("Click HTML...");
        getBrowser().waitUntilElementVisible(createHtmlFileOption).click();
        return (CreateContentPage) new CreateContentPage(browser).renderedPage();
    }

    public NewFolderDialog clickFolder()
    {
        LOG.info("Click Create Folder");
        getBrowser().waitUntilElementVisible(createFolderOption).click();
        return (NewFolderDialog) new NewFolderDialog(browser).renderedPage();
    }

    protected WebElement getContentRow(String contentName)
    {
        return getBrowser().waitWithRetryAndReturnWebElement(By.xpath(String.format(contentRow, contentName)), 1, WAIT_30);
    }

    public AlfrescoContentPage<T> assertFolderIsDisplayedInFilter(FolderModel folder)
    {
        LOG.info("Assert folder {} is displayed in documents filter from left side", folder.getName());
        WebElement folderLink = getBrowser().waitUntilElementVisible(By.xpath(String.format(folderInFilterElement, folder.getName())));
        assertTrue(getBrowser().isElementDisplayed(folderLink),
            String.format("Folder %s is displayed in filter", folder.getName()));
        return this;
    }

    public AlfrescoContentPage<T> waitForCurrentFolderBreadcrumb(String folderName)
    {
        LOG.info("Wait for folder breadcrumb {}", folderName);
        getBrowser().waitUntilElementContainsText(getBrowser().findElement(currentBreadcrumb), folderName);
        return this;
    }

    public AlfrescoContentPage<T> waitForCurrentFolderBreadcrumb(FolderModel folder)
    {
        return waitForCurrentFolderBreadcrumb(folder.getName());
    }

    public AlfrescoContentPage<T> assertFolderIsDisplayedInBreadcrumb(FolderModel folder)
    {
        LOG.info(String.format("Assert folder %s is displayed in breadcrumb", folder.getName()));
        assertTrue(getBrowser().isElementDisplayed(By.xpath(String.format(breadcrumb, folder.getName()))),
            String.format("Folder %s is displayed in breadcrumb", folder.getName()));
        return this;
    }

    public AlfrescoContentPage<T> assertDocumentsRootBreadcrumbIsDisplayed()
    {
        LOG.info("Assert Documents root breadcrumb is displayed");
        getBrowser().waitUntilElementVisible(documentsRootBreadcrumb);
        assertTrue(getBrowser().isElementDisplayed(documentsRootBreadcrumb), "Documents root breadcrumb is displayed");
        return this;
    }

    public AlfrescoContentPage<T> clickFolderFromFilter(FolderModel folder)
    {
        LOG.info("Click folder '%s' from filter {}", folder.getName());
        getBrowser().waitUntilElementVisible(By.xpath(String.format(folderInFilterElement, folder.getName()))).click();
        waitForCurrentFolderBreadcrumb(folder);
        return this;
    }

    public AlfrescoContentPage<T> clickFolderFromBreadcrumb(String folderName)
    {
        LOG.info("Click folder {} from breadcrumb", folderName);
        getBrowser().findElement(By.xpath(String.format(breadcrumb, folderName))).click();
        waitForCurrentFolderBreadcrumb(folderName);

        return this;
    }

    public AlfrescoContentPage<T> clickFolderFromBreadcrumb(FolderModel folder)
    {
        return clickFolderFromBreadcrumb(folder.getName());
    }

    public AlfrescoContentPage<T> clickFolderUpButton()
    {
        LOG.info("Click folder up button");
        getBrowser().findElement(folderUp).click();
        return this;
    }

    public AlfrescoContentPage<T> uploadContent(FileModel file)
    {
        LOG.info("Upload file {}", file.getName());
        getBrowser().findElement(uploadButton).click();
        UploadFileDialog uploadFileDialog = new UploadFileDialog(browser);
        uploadFileDialog.renderedPage();
        uploadFileDialog.uploadFile(file);
        uploadFileDialog.waitForUploadDialogToDisappear();

        return this;
    }

    public AlfrescoContentPage<T> createFileFromTemplate(FileModel templateFile)
    {
        LOG.info("Create new file from template {}", templateFile);
        getBrowser().mouseOver(getBrowser().findElement(createButton));
        getBrowser().mouseOver(getBrowser().findElement(createFileFromTemplate));
        getBrowser().findElement(createFileFromTemplate).click();
        getBrowser().waitUntilElementVisible(By.xpath(String.format(templateName, templateFile.getName()))).click();
        waitUntilNotificationMessageDisappears();

        return this;
    }

    public NewFolderDialog clickCreateFolderFromTemplate(String folderTemplateName)
    {
        LOG.info(String.format("Create new folder from template %s", folderTemplateName));
        getBrowser().mouseOver(getBrowser().findElement(createButton));
        getBrowser().mouseOver(getBrowser().findElement(createFileFromTemplate));
        getBrowser().findElement(createFolderFromTemplate).click();
        getBrowser().waitUntilElementVisible(By.xpath(String.format(templateName, folderTemplateName))).click();

        return (NewFolderDialog) new NewFolderDialog(browser).renderedPage();
    }

    public NewFolderDialog clickCreateFolderFromTemplate(FolderModel folderTemplate)
    {
        return clickCreateFolderFromTemplate(folderTemplate.getName());
    }

    public AlfrescoContentPage<T> checkContent(ContentModel... contentsToSelect)
    {
        for(ContentModel content : contentsToSelect)
        {
            LOG.info("Check content: {}", content.getName());
            getContentRow(content.getName()).findElement(selectCheckBox).click();
        }
        return this;
    }

    public AlfrescoContentPage<T> assertSelectedItemsMenuIsEnabled()
    {
        LOG.info("Assert Selected Items is enabled");
        assertTrue(getBrowser().findElement(selectedItemsLink).isEnabled(), "Selected Items is not enabled");
        return this;
    }

    public AlfrescoContentPage<T> assertSelectedItemsMenuIsDisabled()
    {
        LOG.info("Assert Selected Items is enabled");
        assertFalse(getBrowser().findElement(selectedItemsLink).isEnabled(), "Selected Items is not enabled");
        return this;
    }

    public AlfrescoContentPage<T> clickSelectedItems()
    {
        LOG.info("Click Selected Items");
        getBrowser().waitUntilElementClickable(selectedItemsLink).click();
        getBrowser().waitUntilElementHasAttribute(getBrowser().findElement(selectedItemsActions), "class", "visible");

        return this;
    }

    public AlfrescoContentPage<T> assertActionsInSelectedItemsMenuEqualTo(String... actions)
    {
        LOG.info("Assert available actions from selected items menu are {}", Arrays.asList(actions));
        List<WebElement> items = getBrowser().findElements(selectedItemsActionNames);
        String[] values = getBrowser().getTextFromElementList(items).toArray(new String[0]);
        Arrays.sort(values);
        Arrays.sort(actions);
        assertEquals(values, actions, String.format("Actions not equals to %s", Arrays.asList(actions)));

        return this;
    }

    public AlfrescoContentPage<T> clickSelectMenu()
    {
        LOG.info("Click Select menu");
        getBrowser().findElement(selectMenu).click();
        return this;
    }

    public AlfrescoContentPage<T> selectOptionFromSelectMenu(SelectMenuOptions selectMenuOptions)
    {
        LOG.info("Select Document option from Select menu");
        By option = By.cssSelector(selectMenuOptions.getLocator());
        getBrowser().waitUntilElementVisible(option).click();
        return this;
    }

    public AlfrescoContentPage<T> assertContentsAreChecked(ContentModel... contentModels)
    {
        for(ContentModel content:contentModels)
        {
            LOG.info("Assert content {} is checked", content.getName());
            assertTrue(getContentRow(content.getName()).findElement(selectCheckBox).isSelected(),
                String.format("Content %s is not checked", content.getName()));
        }
        return this;
    }

    public AlfrescoContentPage<T> assertContentsAreNotChecked(ContentModel... contentModels)
    {
        for(ContentModel content:contentModels)
        {
            LOG.info("Assert content {} is checked", content.getName());
            assertFalse(getContentRow(content.getName()).findElement(selectCheckBox).isSelected(),
                String.format("Content %s is not checked", content.getName()));
        }
        return this;
    }

    public CopyMoveUnzipToDialog clickCopyToFromSelectedItems()
    {
        LOG.info("Click Copy To...");
        getBrowser().waitUntilElementVisible(copyToFromSelectedItems).click();
        return (CopyMoveUnzipToDialog) copyMoveDialog.renderedPage();
    }

    public StartWorkflowPage clickStartWorkflowFromSelectedItems()
    {
        LOG.info("Click Stat Workflow...");
        getBrowser().waitUntilElementVisible(startWorkflowFromSelectedItems).click();
        return (StartWorkflowPage) new StartWorkflowPage(browser).renderedPage();
    }

    public DeleteDialog clickDeleteFromSelectedItems()
    {
        LOG.info("Click Delete");
        getBrowser().waitUntilElementVisible(deleteFromSelectedItems).click();
        return (DeleteDialog) new DeleteDialog(browser).renderedPage();
    }

    public AlfrescoContentPage<T> selectFromDocumentsFilter(DocumentsFilter documentFilter)
    {
        LOG.info("Select document filter {}", documentFilter.toString());
        List<WebElement> filters = getBrowser().findElements(documentsFilter);
        getBrowser().findFirstElementWithValue(filters, getDocumentsFilterValue(documentFilter)).click();
        getBrowser().waitUntilElementVisible(selectedFilter);
        return this;
    }

    public AlfrescoContentPage<T> assertDocumentsFilterHeaderTitleEqualsTo(String expectedHeaderTitle)
    {
        LOG.info("Assert documents filter header title '{}' is displayed", expectedHeaderTitle);
        assertEquals(getBrowser().waitUntilElementVisible(documentsFilterHeaderTitle).getText(), expectedHeaderTitle,
            String.format("%s header title is not equals to", expectedHeaderTitle));
        return this;
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
        return new ContentAction(contentModel,
            this,
            new DocumentDetailsPage(browser),
            copyMoveDialog,
            new DeleteDialog(browser));
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

    public enum SelectMenuOptions
    {
        DOCUMENTS(".selectDocuments"),
        FOLDERS(".selectFolders"),
        ALL(".selectAll"),
        INVERT_SELECTION(".selectInvert"),
        NONE(".selectNone");

        private String locator;

        SelectMenuOptions(String locator)
        {
            this.locator = locator;
        }

        public String getLocator()
        {
            return locator;
        }
    }
}
