package org.alfresco.po.share.alfrescoContent;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.enums.SelectMenuOptions;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class AlfrescoContentPage<T> extends SharePage2<AlfrescoContentPage<T>> {
    protected final By documentsFilterHeaderTitle = By.cssSelector("div[id$='default-description'] .message");
    protected final By selectCheckBox = By.cssSelector("input[name='fileChecked']");
    private final By contentArea = By.cssSelector("div[class='documents yui-dt']");
    private final By createButton = By.cssSelector("button[id$='createContent-button-button']");
    private final By createOptionsArea =
        By.cssSelector("div[id$='_default-createContent-menu'][style*='visible']");
    private final By createTextFileOption = By.cssSelector("span.text-file");
    private final By createXmlFileOption = By.cssSelector("span.xml-file");
    private final By createHtmlFileOption = By.cssSelector("span.html-file");
    private final By createFolderOption = By.cssSelector(".folder-file");
    private final By currentBreadcrumb = By.cssSelector(".crumb .label a");
    private final By folderUp = By.cssSelector("button[id$='folderUp-button-button']");
    private final By uploadButton = By.cssSelector("button[id$='-fileUpload-button-button']");
    private final By createFileFromTemplate =
        By.cssSelector("div[id$='createContent-menu']>div>ul:nth-of-type(2)>li:nth-of-type(1) span");
    private final By createFolderFromTemplate =
        By.cssSelector("div[id$='createContent-menu']>div>ul:nth-of-type(2)>li:nth-of-type(2) span");
    private final By selectedItemsLink = By.cssSelector("button[id$='selectedItems-button-button']");
    private final By selectedItemsActions = By.cssSelector("div[id$=default-selectedItems-menu]");
    private final By copyToFromSelectedItems = By.cssSelector(".onActionCopyTo");
    private final By selectMenu = By.cssSelector("button[id$='fileSelect-button-button']");
    private final By selectedItemsActionNames =
        By.cssSelector("div[id$=default-selectedItems-menu] a[class='yuimenuitemlabel'] span");
    private final By startWorkflowFromSelectedItems = By.cssSelector(".onActionAssignWorkflow");
    private final By deleteFromSelectedItems = By.cssSelector(".onActionDelete");
    private final By documentsRootBreadcrumb =
        By.cssSelector("div[id$='default-navBar'] div[class^='crumb documentDroppable']:nth-of-type(1)");
    private final By optionDropdownButton =
        By.cssSelector("button[id$='default-options-button-button']");
    private final By optionsMenu = By.cssSelector("div[id$='default-options-menu']");
    private final By hideBreadcrumbOption = By.cssSelector(".hidePath");
    private final By showBreadcrumbOption = By.cssSelector(".showPath");
    private final By filmstripViewOption = By.cssSelector("span[class='view filmstrip']");
    private final By simpleViewOption = By.cssSelector("span[class='view simple']");
    private final By tableViewOption = By.cssSelector("span[class='view table']");
    private final By galleryViewOption = By.cssSelector("span[class='view gallery']");
    private final By audioViewOption = By.cssSelector("span[class='view audio']");
    private final By mediaViewOption = By.cssSelector("span[class='view media_table']");
    private final By setDefaultViewLocator = By.cssSelector("span[class='setDefaultView']");
    private final By removeDefaultViewLocator = By.cssSelector("span[class='removeDefaultView']");
    private final By tableViewContentArea = By.cssSelector("div[class='documents yui-dt alf-table']");
    private final By detailedViewContentArea = By.cssSelector("div[class='documents yui-dt']");
    private final By unzipCopyMoveButton = By.cssSelector("button[id$='_default-copyMoveTo-ok-button']");
    private final By myFiles = By.id("template_x002e_documentlist_v2_x002e_documentlibrary_x0023_default-copyMoveTo-myfiles-button");
    private final By cancelMyFiles = By.id("template_x002e_documentlist_v2_x002e_documentlibrary_x0023_default-copyMoveTo-cancel-button");
    private final By dialogBody = By.cssSelector("div[id$='default-copyMoveTo-dialog']");
    private final String breadcrumb =
        "//div[@class='crumb documentDroppable documentDroppableHighlights']//a[text()='%s']";
    private final String templateName = "//a[@class='yuimenuitemlabel']//span[text()='%s']";
    private final String contentRow = "//h3[@class='filename']//a[text()='%s']/../../../../..";
    private By all_DocumentsFilter = By.cssSelector("span.all a");

    protected AlfrescoContentPage(ThreadLocal<WebDriver> webDriver) {
        super(webDriver);
    }

    @Override
    public String getRelativePath() {
        return null;
    }

    protected void waitForContentPageToBeLoaded() {
        waitUntilElementIsVisible(contentArea);
    }

    public AlfrescoContentPage<T> clickCreate() {
        log.info("Click Create");
        clickElement(createButton);
        waitUntilElementIsVisible(createOptionsArea);
        return this;
    }

    public CreateContentPage clickTextPlain() {
        log.info("Click Plain Text...");
        clickElement(createTextFileOption);
        return new CreateContentPage(webDriver);
    }

    public CreateContentPage clickXml() {
        log.info("Click XML...");
        clickElement(createXmlFileOption);
        return new CreateContentPage(webDriver);
    }

    public CreateContentPage clickHtml() {
        log.info("Click HTML...");
        clickElement(createHtmlFileOption);
        return new CreateContentPage(webDriver);
    }

    public NewFolderDialog clickFolder() {
        log.info("Click Create Folder");
        clickElement(createFolderOption);
        return new NewFolderDialog(webDriver);
    }

    protected WebElement getContentRow(String contentName) {
        By contentRowElement = By.xpath(String.format(contentRow, contentName));

        int retryCount = 0;
        while (retryCount < RETRY_TIME_80.getValue() && !isElementDisplayed(contentRowElement)) {
            log.warn("Content {} not displayed - retry: {}", contentName, retryCount);
            refresh();
            waitToLoopTime(WAIT_2.getValue());
            waitForContentPageToBeLoaded();
            retryCount++;
        }
        return waitUntilElementIsVisible(contentRowElement);
    }

    public AlfrescoContentPage<T> waitForCurrentFolderBreadcrumb(String folderName) {
        log.info("Wait for folder breadcrumb {}", folderName);
        waitUntilElementContainsText(currentBreadcrumb, folderName);
        return this;
    }

    public AlfrescoContentPage<T> waitForCurrentFolderBreadcrumb(FolderModel folder) {
        return waitForCurrentFolderBreadcrumb(folder.getName());
    }

    public AlfrescoContentPage<T> assertDocumentsRootBreadcrumbIsDisplayed() {
        log.info("Assert Documents root breadcrumb is displayed");
        waitUntilElementIsVisible(documentsRootBreadcrumb);
        assertTrue(isElementDisplayed(documentsRootBreadcrumb), "Documents root folder is not displayed");
        return this;
    }

    public AlfrescoContentPage<T> assertDocumentsRootBreadcrumbIsNotDisplayed() {
        log.info("Assert Documents root breadcrumb is noy displayed");
        assertFalse(isElementDisplayed(documentsRootBreadcrumb), "Documents root folder is displayed");
        return this;
    }

    public AlfrescoContentPage<T> assertFolderIsDisplayedInBreadcrumb(FolderModel folder) {
        log.info("Assert folder {} is displayed in breadcrumb ", folder.getName());
        By folderBreadcrumb = By.xpath(String.format(breadcrumb, folder.getName()));
        WebElement folderElement = waitUntilElementIsVisible(folderBreadcrumb);
        assertTrue(isElementDisplayed(folderElement),
            String.format("Folder %s is displayed in breadcrumb", folder.getName()));
        return this;
    }

    public AlfrescoContentPage<T> clickFolderFromBreadcrumb(String folderName) {
        log.info("Click folder {} from breadcrumb", folderName);
        clickElement(By.xpath(String.format(breadcrumb, folderName)));
        waitForCurrentFolderBreadcrumb(folderName);

        return this;
    }

    public AlfrescoContentPage<T> clickFolderUpButton() {
        log.info("Click folder up button");
        clickElement(folderUp);
        return this;
    }

    public AlfrescoContentPage<T> uploadContent(FileModel file) {
        log.info("Upload file {}", file.getName());
        waitUntilElementIsVisible(uploadButton);
        clickElement(uploadButton);
        UploadFileDialog uploadFileDialog = new UploadFileDialog(webDriver);
        uploadFileDialog.uploadFile(file);
        uploadFileDialog.waitForUploadDialogToDisappear();
        waitForContentPageToBeLoaded();
        return this;
    }

    public AlfrescoContentPage<T> createFileFromTemplate(FileModel templateFile) {
        log.info("Create new file from template {}", templateFile);
        mouseOver(findElement(createButton));
        mouseOver(findElement(createFileFromTemplate));
        clickElement(createFileFromTemplate);
        clickElement(By.xpath(String.format(templateName, templateFile.getName())));
        waitUntilNotificationMessageDisappears();

        return this;
    }

    public NewFolderDialog clickCreateFolderFromTemplate(String folderTemplateName) {
        log.info("Create new folder from template {}", folderTemplateName);
        mouseOver(findElement(createButton));
        mouseOver(findElement(createFileFromTemplate));
        clickElement(createFolderFromTemplate);
        clickElement(By.xpath(String.format(templateName, folderTemplateName)));

        return new NewFolderDialog(webDriver);
    }

    public NewFolderDialog clickCreateFolderFromTemplate(FolderModel folderTemplate) {
        return clickCreateFolderFromTemplate(folderTemplate.getName());
    }

    public AlfrescoContentPage<T> checkContent(ContentModel... contentsToSelect) {
        waitUntilElementIsVisible(selectCheckBox);
        for (ContentModel content : contentsToSelect) {
            log.info("Check content: {}", content.getName());
            clickElement(getContentRow(content.getName()).findElement(selectCheckBox));
        }
        return this;
    }

    public AlfrescoContentPage<T> assertSelectedItemsMenuIsEnabled() {
        log.info("Assert Selected Items is enabled");
        assertTrue(findElement(selectedItemsLink).isEnabled(), "Selected Items is not enabled");
        return this;
    }

    public AlfrescoContentPage<T> assertSelectedItemsMenuIsDisabled() {
        log.info("Assert Selected Items is enabled");
        assertFalse(findElement(selectedItemsLink).isEnabled(), "Selected Items is not enabled");
        return this;
    }

    public AlfrescoContentPage<T> clickSelectedItems() {
        log.info("Click Selected Items");
        clickElement(selectedItemsLink);
        waitUntilElementHasAttribute(findElement(selectedItemsActions), "class", "visible");

        return this;
    }

    public AlfrescoContentPage<T> assertActionsInSelectedItemsMenuEqualTo(String... actions) {
        log.info("Assert available actions from selected items menu are {}", Arrays.asList(actions));
        waitUntilElementsAreVisible(selectedItemsActionNames);
        List<WebElement> items = findElements(selectedItemsActionNames);
        String[] values = getTextFromElementList(items).toArray(new String[0]);
        Arrays.sort(values);
        Arrays.sort(actions);
        assertTrue(Arrays.asList(values)
                .containsAll(Arrays.asList(actions)),
            String.format("Not all actions were found %s", Arrays.asList(actions)));

        return this;
    }

    public AlfrescoContentPage<T> clickSelectMenu() {
        log.info("Click Select menu");
        clickElement(selectMenu);
        return this;
    }

    public AlfrescoContentPage<T> selectOptionFromSelectMenu(SelectMenuOptions selectMenuOptions) {
        log.info("Select Document option from Select menu");
        By option = By.cssSelector(selectMenuOptions.getLocator());
        clickElement(option);
        return this;
    }

    public AlfrescoContentPage<T> assertContentsAreChecked(ContentModel... contentModels) {
        for (ContentModel content : contentModels) {
            log.info("Assert content {} is checked", content.getName());
            assertTrue(getContentRow(content.getName()).findElement(selectCheckBox).isSelected(),
                String.format("Content %s is not checked", content.getName()));
        }
        return this;
    }

    public AlfrescoContentPage<T> assertContentsAreNotChecked(ContentModel... contentModels) {
        for (ContentModel content : contentModels) {
            log.info("Assert content {} is checked", content.getName());
            assertFalse(getContentRow(content.getName()).findElement(selectCheckBox).isSelected(),
                String.format("Content %s is not checked", content.getName()));
        }
        return this;
    }

    public CopyMoveUnzipToDialog clickCopyToFromSelectedItems() {
        log.info("Click Copy To...");
        clickElement(copyToFromSelectedItems);
        return new CopyMoveUnzipToDialog(webDriver);
    }

    public StartWorkflowPage clickStartWorkflowFromSelectedItems() {
        log.info("Click Start Workflow...");
        clickElement(startWorkflowFromSelectedItems);
        return new StartWorkflowPage(webDriver);
    }

    public DeleteDialog clickDeleteFromSelectedItems() {
        log.info("Click Delete");
        clickElement(deleteFromSelectedItems);
        return new DeleteDialog(webDriver);
    }

    public AlfrescoContentPage<T> assertDocumentsFilterHeaderTitleEqualsTo(String expectedHeaderTitle) {
        log.info("Assert documents filter header title '{}' is displayed", expectedHeaderTitle);
        assertEquals(getElementText(documentsFilterHeaderTitle), expectedHeaderTitle,
            String.format("%s header title is not equals to", expectedHeaderTitle));
        return this;
    }

    public AlfrescoContentPage<T> clickOptions() {
        log.info("Click Options");
        waitInSeconds(2);
        clickElement(optionDropdownButton);
        waitUntilElementIsVisible(optionsMenu);
        return this;
    }

    public AlfrescoContentPage<T> selectHideBreadcrumbFromOptions() {
        log.info("Select Hide Breadcrumb option");
        clickElement(hideBreadcrumbOption);
        waitUntilElementDisappears(documentsRootBreadcrumb);
        return this;
    }

    public AlfrescoContentPage<T> selectShowBreadcrumbFromOptions() {
        log.info("Select Show Breadcrumb option");
        clickElement(showBreadcrumbOption);
        waitUntilElementIsVisible(documentsRootBreadcrumb);
        return this;
    }

    public FilmstripViewComponent selectFilmstripView() {
        log.info("Select Filmstrip view");
        clickElement(filmstripViewOption);
        return new FilmstripViewComponent(webDriver);
    }

    public AlfrescoContentPage<T> selectSimpleView() {
        log.info("Select Simple view");
        clickElement(simpleViewOption);
        return this;
    }

    public AlfrescoContentPage<T> selectgalleryView() {
        log.info("Select Gallery view");
        clickElement(galleryViewOption);
        return this;
    }

    public ContentTableViewComponent selectTableView() {
        log.info("Select Filmstrip view");
        clickElement(tableViewOption);
        return new ContentTableViewComponent(webDriver);
    }

    public AlfrescoContentPage<T> selectAudioView() {
        log.info("Select Gallery view");
        clickElement(audioViewOption);
        return this;
    }

    public AlfrescoContentPage<T> selectMediaView() {
        log.info("Select Gallery view");
        clickElement(mediaViewOption);
        return this;
    }

    public AlfrescoContentPage<T> assertTableViewIsSet() {
        log.info("Assert table view is set");
        waitUntilElementIsVisible(tableViewContentArea);
        assertTrue(isElementDisplayed(tableViewContentArea), "Table view is not set");
        return this;
    }

    public AlfrescoContentPage<T> assertDetailedViewIsSet() {
        log.info("Assert table view is set");
        waitUntilElementIsVisible(detailedViewContentArea);
        assertTrue(isElementDisplayed(detailedViewContentArea), "Detailed view is not set");
        return this;
    }

    public AlfrescoContentPage<T> assertSetDefaultViewForFolderEqualsTo(String label) {
        log.info("Assert Set default view text equals to {}", label);
        assertEquals(getElementText(setDefaultViewLocator), label, "Set default view text is not as expected");
        return this;
    }

    public AlfrescoContentPage<T> assertRemoveDefaultViewForFolderEqualsTo(String label) {
        log.info("Assert Remove default view text equals to {}", label);
        assertEquals(getElementText(removeDefaultViewLocator), label, "Remove default view text is not as expected");
        return this;
    }

    public AlfrescoContentPage<T> pageRefresh() {
        waitInSeconds(2);
        getWebDriver().navigate().refresh();
        return this;
    }

    public AlfrescoContentPage<T> setDefaultView() {
        log.info("Set default view");
        clickElement(setDefaultViewLocator);
        return this;
    }

    public AlfrescoContentPage<T> removeDefaultView() {
        log.info("Remove default view");
        clickElement(removeDefaultViewLocator);
        return this;
    }

    public ContentActionComponent usingContent(ContentModel contentModel) {
        return new ContentActionComponent(webDriver, contentModel);
    }

    public ContentFiltersComponent usingContentFilters() {
        return new ContentFiltersComponent(webDriver);
    }

    public void clickOkButton() {
        log.info("Click Move button");
        clickElement(unzipCopyMoveButton);
        waitUntilElementDisappears(dialogBody);
        waitUntilNotificationMessageDisappears();
    }

    public void clickOnMyFiles() {
        clickElement(myFiles);
    }

    public void clickOnCancelMyFiles() {
        clickElement(cancelMyFiles);
    }

    public void clickOnAllDocuments() {
        clickElement(all_DocumentsFilter);
    }
}