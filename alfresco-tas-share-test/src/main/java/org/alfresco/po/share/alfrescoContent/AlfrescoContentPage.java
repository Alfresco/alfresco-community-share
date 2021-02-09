package org.alfresco.po.share.alfrescoContent;

import static org.alfresco.common.Wait.*;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public abstract class AlfrescoContentPage<T> extends SharePage2<AlfrescoContentPage<T>>
{
    private final By contentArea = By.cssSelector("div[class='documents yui-dt']");
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
    protected final By selectCheckBox = By.cssSelector("input[name='fileChecked']");
    private final By selectMenu = By.cssSelector("button[id$='fileSelect-button-button']");
    private final By selectedItemsActionNames = By.cssSelector("div[id$=default-selectedItems-menu] a[class='yuimenuitemlabel'] span");
    private final By startWorkflowFromSelectedItems = By.cssSelector(".onActionAssignWorkflow");
    private final By deleteFromSelectedItems = By.cssSelector(".onActionDelete");

    private final String breadcrumb = "//div[@class='crumb documentDroppable documentDroppableHighlights']//a[text()='%s']";
    private final String templateName = "//a[@class='yuimenuitemlabel']//span[text()='%s']";
    private final String folderInFilterElement = "//tr[starts-with(@class,'ygtvrow documentDroppable')]//span[text()='%s']";
    private final String contentRow = "//h3[@class='filename']//a[text()='%s']/../../../../..";

    protected AlfrescoContentPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    private void waitForContentPageToBeLoaded()
    {
        webElementInteraction.waitUntilElementIsVisible(contentArea);
    }

    public AlfrescoContentPage<T> clickCreate()
    {
        log.info("Click Create");
        webElementInteraction.clickElement(createButton);
        webElementInteraction.waitUntilElementIsVisible(createOptionsArea);
        return this;
    }

    public CreateContentPage clickTextPlain()
    {
        log.info("Click Plain Text...");
        webElementInteraction.clickElement(createTextFileOption);
        return new CreateContentPage(webDriver);
    }

    public CreateContentPage clickXml()
    {
        log.info("Click XML...");
        webElementInteraction.clickElement(createXmlFileOption);
        return new CreateContentPage(webDriver);
    }

    public CreateContentPage clickHtml()
    {
        log.info("Click HTML...");
        webElementInteraction.clickElement(createHtmlFileOption);
        return new CreateContentPage(webDriver);
    }

    public NewFolderDialog clickFolder()
    {
        log.info("Click Create Folder");
        webElementInteraction.clickElement(createFolderOption);
        return new NewFolderDialog(webDriver);
    }

    protected WebElement getContentRow(String contentName)
    {
        By contentRowElement = By.xpath(String.format(contentRow, contentName));

        int retryCount = 0;
        while(retryCount < WAIT_80.getValue() && !webElementInteraction.isElementDisplayed(contentRowElement))
        {
            log.error("Wait for content {} to be displayed. Retry {}", contentName, retryCount);
            webElementInteraction.refresh();
            waitToLoopTime(WAIT_1.getValue());
            waitForContentPageToBeLoaded();
            retryCount++;
        }
        return webElementInteraction.waitUntilElementIsVisible(contentRowElement);
    }

    public AlfrescoContentPage<T> assertFolderIsDisplayedInFilter(FolderModel folder)
    {
        log.info("Assert folder {} is displayed in documents filter from left side", folder.getName());
        WebElement folderLink = webElementInteraction.waitUntilElementIsVisible(
            By.xpath(String.format(folderInFilterElement, folder.getName())));
        assertTrue(webElementInteraction.isElementDisplayed(folderLink),
            String.format("Folder %s is displayed in filter", folder.getName()));
        return this;
    }

    public AlfrescoContentPage<T> waitForCurrentFolderBreadcrumb(String folderName)
    {
        log.info("Wait for folder breadcrumb {}", folderName);
        webElementInteraction.waitUntilElementContainsText(currentBreadcrumb, folderName);
        return this;
    }

    public AlfrescoContentPage<T> waitForCurrentFolderBreadcrumb(FolderModel folder)
    {
        return waitForCurrentFolderBreadcrumb(folder.getName());
    }

    public AlfrescoContentPage<T> assertFolderIsDisplayedInBreadcrumb(FolderModel folder)
    {
        log.info("Assert folder {} is displayed in breadcrumb ", folder.getName());
        By folderBreadcrumb = By.xpath(String.format(breadcrumb, folder.getName()));
        WebElement folderElement = webElementInteraction.waitUntilElementIsVisible(folderBreadcrumb);
        assertTrue(webElementInteraction.isElementDisplayed(folderElement),
            String.format("Folder %s is displayed in breadcrumb", folder.getName()));
        return this;
    }

    public AlfrescoContentPage<T> clickFolderFromFilter(FolderModel folder)
    {
        log.info("Click folder {} from filter", folder.getName());
        webElementInteraction.clickElement(By.xpath(String.format(folderInFilterElement, folder.getName())));
        waitForCurrentFolderBreadcrumb(folder);
        return this;
    }

    public AlfrescoContentPage<T> clickFolderFromBreadcrumb(String folderName)
    {
        log.info("Click folder {} from breadcrumb", folderName);
        webElementInteraction.clickElement(By.xpath(String.format(breadcrumb, folderName)));
        waitForCurrentFolderBreadcrumb(folderName);

        return this;
    }

    public AlfrescoContentPage<T> clickFolderUpButton()
    {
        log.info("Click folder up button");
        webElementInteraction.clickElement(folderUp);
        return this;
    }

    public AlfrescoContentPage<T> uploadContent(FileModel file)
    {
        log.info("Upload file {}", file.getName());
        webElementInteraction.waitUntilElementIsVisible(uploadButton);
        webElementInteraction.clickElement(uploadButton);
        UploadFileDialog uploadFileDialog = new UploadFileDialog(webDriver);
        uploadFileDialog.uploadFile(file);
        uploadFileDialog.waitForUploadDialogToDisappear();
        waitForContentPageToBeLoaded();
        return this;
    }

    public AlfrescoContentPage<T> createFileFromTemplate(FileModel templateFile)
    {
        log.info("Create new file from template {}", templateFile);
        webElementInteraction.mouseOver(webElementInteraction.findElement(createButton));
        webElementInteraction.mouseOver(webElementInteraction.findElement(createFileFromTemplate));
        webElementInteraction.clickElement(createFileFromTemplate);
        webElementInteraction.clickElement(By.xpath(String.format(templateName, templateFile.getName())));
        waitUntilNotificationMessageDisappears();

        return this;
    }

    public NewFolderDialog clickCreateFolderFromTemplate(String folderTemplateName)
    {
        log.info("Create new folder from template {}", folderTemplateName);
        webElementInteraction.mouseOver(webElementInteraction.findElement(createButton));
        webElementInteraction.mouseOver(webElementInteraction.findElement(createFileFromTemplate));
        webElementInteraction.clickElement(createFolderFromTemplate);
        webElementInteraction.clickElement(By.xpath(String.format(templateName, folderTemplateName)));

        return new NewFolderDialog(webDriver);
    }

    public NewFolderDialog clickCreateFolderFromTemplate(FolderModel folderTemplate)
    {
        return clickCreateFolderFromTemplate(folderTemplate.getName());
    }

    public AlfrescoContentPage<T> checkContent(ContentModel... contentsToSelect)
    {
        webElementInteraction.waitUntilElementIsVisible(selectCheckBox);
        for(ContentModel content : contentsToSelect)
        {
            log.info("Check content: {}", content.getName());
            webElementInteraction.clickElement( getContentRow(content.getName()).findElement(selectCheckBox));
        }
        return this;
    }

    public AlfrescoContentPage<T> assertSelectedItemsMenuIsEnabled()
    {
        log.info("Assert Selected Items is enabled");
        assertTrue(webElementInteraction.findElement(selectedItemsLink).isEnabled(), "Selected Items is not enabled");
        return this;
    }

    public AlfrescoContentPage<T> assertSelectedItemsMenuIsDisabled()
    {
        log.info("Assert Selected Items is enabled");
        assertFalse(webElementInteraction.findElement(selectedItemsLink).isEnabled(), "Selected Items is not enabled");
        return this;
    }

    public AlfrescoContentPage<T> clickSelectedItems()
    {
        log.info("Click Selected Items");
        webElementInteraction.clickElement(selectedItemsLink);
        webElementInteraction.waitUntilElementHasAttribute(webElementInteraction.findElement(selectedItemsActions), "class", "visible");

        return this;
    }

    public AlfrescoContentPage<T> assertActionsInSelectedItemsMenuEqualTo(String... actions)
    {
        log.info("Assert available actions from selected items menu are {}", Arrays.asList(actions));
        webElementInteraction.waitUntilElementsAreVisible(selectedItemsActionNames);
        List<WebElement> items = webElementInteraction.findElements(selectedItemsActionNames);
        String[] values = webElementInteraction.getTextFromElementList(items).toArray(new String[0]);
        Arrays.sort(values);
        Arrays.sort(actions);
        assertTrue(Arrays.asList(values).containsAll(Arrays.asList(actions)),
            String.format("Not all actions were found %s", Arrays.asList(actions)));

        return this;
    }

    public AlfrescoContentPage<T> clickSelectMenu()
    {
        log.info("Click Select menu");
        webElementInteraction.clickElement(selectMenu);
        return this;
    }

    public AlfrescoContentPage<T> selectOptionFromSelectMenu(SelectMenuOptions selectMenuOptions)
    {
        log.info("Select Document option from Select menu");
        By option = By.cssSelector(selectMenuOptions.getLocator());
        webElementInteraction.clickElement(option);
        return this;
    }

    public AlfrescoContentPage<T> assertContentsAreChecked(ContentModel... contentModels)
    {
        for(ContentModel content:contentModels)
        {
            log.info("Assert content {} is checked", content.getName());
            assertTrue(getContentRow(content.getName()).findElement(selectCheckBox).isSelected(),
                String.format("Content %s is not checked", content.getName()));
        }
        return this;
    }

    public AlfrescoContentPage<T> assertContentsAreNotChecked(ContentModel... contentModels)
    {
        for(ContentModel content:contentModels)
        {
            log.info("Assert content {} is checked", content.getName());
            assertFalse(getContentRow(content.getName()).findElement(selectCheckBox).isSelected(),
                String.format("Content %s is not checked", content.getName()));
        }
        return this;
    }

    public CopyMoveUnzipToDialog clickCopyToFromSelectedItems()
    {
        log.info("Click Copy To...");
        webElementInteraction.clickElement(copyToFromSelectedItems);
        return new CopyMoveUnzipToDialog(webDriver);
    }

    public StartWorkflowPage clickStartWorkflowFromSelectedItems()
    {
        log.info("Click Start Workflow...");
        webElementInteraction.clickElement(startWorkflowFromSelectedItems);
        return new StartWorkflowPage(webDriver);
    }

    public DeleteDialog clickDeleteFromSelectedItems()
    {
        log.info("Click Delete");
        webElementInteraction.clickElement(deleteFromSelectedItems);
        return new DeleteDialog(webDriver);
    }

    public AlfrescoContentPage<T> selectFromDocumentsFilter(DocumentsFilter documentFilter)
    {
        log.info("Select document filter {}", documentFilter.toString());
        List<WebElement> filters = webElementInteraction.waitUntilElementsAreVisible(documentsFilter);
        webElementInteraction.findFirstElementWithValue(filters, getDocumentsFilterValue(documentFilter)).click();
        webElementInteraction.waitUntilElementIsVisible(selectedFilter);
        return this;
    }

    public AlfrescoContentPage<T> assertDocumentsFilterHeaderTitleEqualsTo(String expectedHeaderTitle)
    {
        log.info("Assert documents filter header title '{}' is displayed", expectedHeaderTitle);
        assertEquals(webElementInteraction.getElementText(documentsFilterHeaderTitle), expectedHeaderTitle,
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

    public ContentActionComponent usingContent(ContentModel contentModel)
    {
        return new ContentActionComponent(
            contentModel,
            webElementInteraction,
            this,
            new DocumentDetailsPage(webDriver),
            new CopyMoveUnzipToDialog(webDriver),
            new DeleteDialog(webDriver),
            new EditPropertiesDialog(webDriver));
    }

    //todo: move into separate file
    public enum DocumentsFilter
    {
        ALL_DOCUMENTS,
        EDITING_ME,
        EDITING_OTHERS,
        RECENTLY_MODIFIED,
        RECENTLY_ADDED,
        FAVORITES
    }

    //todo: move into separate file
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
