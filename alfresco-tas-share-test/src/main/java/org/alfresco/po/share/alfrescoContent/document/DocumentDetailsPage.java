package org.alfresco.po.share.alfrescoContent.document;

import static org.alfresco.common.Utils.isFileInDirectory;
import static org.alfresco.common.Wait.WAIT_1;
import static org.alfresco.common.Wait.WAIT_2;
import static org.testng.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.DataUtil;
import org.alfresco.common.Wait;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ChangeContentTypeDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.model.FileModel;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;


@Slf4j
public class DocumentDetailsPage extends SharePage2<DocumentDetailsPage>
{
    private FileModel currentFile;
    private static final int BEGIN_INDEX = 0;
    private static final String VERSION_NUMBER = "1";

    private final By noComments = By.cssSelector("div[id*='_default-comments-list'] td[class ='yui-dt-empty']");
    private final By documentTitle = By.cssSelector("div[class='node-info'] h1");
    private final By docDetailsPageHeader = By.cssSelector(".node-header");
    private final By headerFileName = By.cssSelector(".node-header h1");
    private final By likeUnlikeAction = By.cssSelector("[class*=like-action]");
    private final By likesCount = By.cssSelector("[class=likes-count]");
    private final By itemModifier = By.cssSelector(".item-modifier a");
    private final By modifyDate = By.cssSelector(".item-modifier span");
    private final By documentVersion = By.cssSelector("div[class='node-info'] h1 span");
    private final By favoriteUnfavoriteAction = By.cssSelector("[class*=favourite-action]");
    private final By commentDocument = By.cssSelector("[name*='commentNode']");
    private final By commentForm = By.cssSelector("[class=comment-form]");
    private final By addCommentButtonSave = By.cssSelector("[id*='default-add-submit-button']");
    private final By addCommentButtonCancel = By.cssSelector("[id*='default-add-cancel-button']");
    private final By commentContent = By.cssSelector("[class=comment-content]");
    private final By sharePopUp = By.cssSelector("[class*=quickshare-action] [class=bd]");
    private final By shareDocument = By.cssSelector(".quickshare-action");
    private final By maximizeButton = By.cssSelector("[id*=default-fullpage-button]");
    private final By zoomInButton = By.cssSelector("[id*=zoomIn-button]");
    private final By zoomOutButton = By.cssSelector("[id*=zoomOut-button]");
    private final By scaleButton = By.cssSelector("[id*=default-scaleSelectBtn-button]");
    private final By nextButton = By.cssSelector("[id*=default-next-button]");
    private final By previousButton = By.cssSelector("[id*=default-previous-button]");
    private final By pageNumber = By.cssSelector("[id*=default-pageNumber]");
    private final By searchButton = By.cssSelector("[id*=searchBarToggle-button]");
    private final By searchDialog = By.cssSelector("[class*=searchDialog]");
    private final By pageReport = By.cssSelector("[id*=default-paginator-top] [id*=page-report]");
    private final By nextPage = By.cssSelector("[id*=default-paginator-top] [class*=next]");
    private final By previousPage = By.cssSelector("[id*=default-paginator-top] [class*=previous]");
    private final By propertiesValuesList = By.cssSelector(".viewmode-value");
    private final By tagsFeaturePanel = By.cssSelector(".folder-tags.folder-details-panel");
    private final By folderActionsPanel = By.cssSelector(".folder-actions.folder-details-panel");
    private final By filePropertiesdetailsPanel = By.cssSelector(".folder-metadata-header.folder-details-panel");
    private final By socialFeaturesPanel = By.cssSelector(".folder-links.folder-details-panel");
    private final By folderLinkFromBreadcrumbTrail = By.cssSelector(".folder-link.folder-closed a");
    private final By manageAspectsButton = By.xpath(".//*[@id='onActionManageAspects']/a/span");
    private final By editPropertiesLink = By.xpath("//a[contains(@title,'Edit Properties')]");
    private final By documentImage = By.xpath("//div[@class='previewer Image']/img");
    private final By commentsList = By.cssSelector("[id*=comment-container]");
    private final By downloadButton = By.cssSelector("span[class$='onDownloadDocumentClick']");
    private final By deleteCommentButton = By.xpath("//span[@class='comment-actions']//a[@title = 'Delete Comment']"); //TODO: edit with css + others
    private final By editCommentButton = By.xpath("//span[@class='comment-actions']//a[@title = 'Edit Comment']");
    private final By deleteButtonOnPrompt = By.xpath("//span[@class='button-group']//span[@class = 'yui-button yui-push-button']//button");
    private final By deleteCommentPrompt = By.id("prompt_h");
    private final By editCommentBoxTitle = By.xpath("//h2[text()= 'Edit Comment...']");
    private final By saveButtonEditComment = By.xpath("//button[text()='Save']");
    private final By contentText = By.cssSelector("div[class ='textLayer']>div");
    private final By noContentText = By.cssSelector("div[class ='message']");
    private final By contentTittle = By.xpath("(//span[@class='viewmode-value'])[2]");
    private final By contentDescription = By.xpath("(//span[@class='viewmode-value'])[3]");
    private final By downloadPreviousVersion = By.cssSelector("div[id$='_default-olderVersions'] div.version-panel-right a.download");
    private final By revertButton = By.cssSelector("div[id$='_default-olderVersions'] div.version-panel-right a[class$='_default revert']");
    private final By commentTextArea = By.cssSelector("iframe[id*='comments']");
    private final By lockedMessage = By.xpath(".//span[contains(@class,'locked')]");
    private final By okOnRevertPopup = By.cssSelector("#alfresco-revertVersion-instance-ok-button-button");
    private final By addCommentButton = By.cssSelector("span[class$='onAddCommentClick'] button");
    private final By commentsIframe = By.cssSelector("iframe[id$='default-add-content_ifr']");
    private final By copyToAction = By.cssSelector("#onActionCopyTo > a");
    private final By documentsLink = By.xpath("//span[@class = 'folder-link']//a");
    private final By folderOpenedLink = By.xpath("//span[@class='folder-link folder-open']");

    private final By googleDocsEdit = By.xpath("//span[contains(text(), 'Edit in Google Docs™')]");
    private final By commentContentIframe = By.xpath("//iframe[contains(@title,'Rich Text Area')]");
    private final By editComment = By.cssSelector("[class*=edit-comment]");
    private final By commContent = By.cssSelector("[class=comment-content]");
    private final By documentActionsOptionsSelector = By.cssSelector(".action-set div a span");
    private final By olderVersion = By.cssSelector("div[id$='_default-olderVersions'] span.document-version");
    private final By latestVersion = By.cssSelector("div[id$='_default-latestVersion'] span.document-version");
    private final By addCommentBlock = By.cssSelector("div[id*='default-add-comment']");
    private final By propertiesList = By.cssSelector(".viewmode-label");
    private final By message = By.cssSelector("span[class='message']");
    private final By changeTypeAction = By.cssSelector("#onActionChangeType > a");
    private final By unzipToAction = By.cssSelector("div[id='onActionUnzipTo'] a[class='action-link']");
    private final By modifierName = By.xpath("(//div[@class='viewmode-field'])[9]");
    private final By balloon = By.cssSelector(".balloon>.text>div");
    private final By shareUrl = By.id("template_x002e_document-links_x002e_document-details_x0023_default-page");
    private final By cancelButtonOnPrompt = By.xpath("(//button[text()=\"Cancel\"])[1]");
    private final By helpIcon = By.name(".onHelpLinkClick");
    private final By commentsEditingToolbar = By.xpath("//div[@class=\"mce-top-part mce-container mce-stack-layout-item mce-first\"]");
    private final By helpText = By.className("help-text");
    private final String helpIconText = "Keyboard Shortcuts: Bold (Ctrl+B), Italic (Ctrl+I), Underline (Ctrl+U), Undo (Ctrl+Z), Redo (Ctrl+Y).";

    public DocumentDetailsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public DocumentDetailsPage navigate(FileModel file)
    {
        log.info("Navigate to document details of file: {}", file.getCmisLocation());
        setCurrentFileModel(file);
        return navigate();
    }

    public void setCurrentFileModel(FileModel currentFileModel)
    {
        this.currentFile = currentFileModel;
    }

    public FileModel getCurrentFile()
    {
        return currentFile;
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/document-details?nodeRef=workspace://SpacesStore/%s", getCurrentFile().getNodeRefWithoutVersion());
    }

    public DocumentDetailsPage assertDocumentTitleEquals(FileModel expectedFileTitle)
    {
        log.info("Assert file title equals: {}", expectedFileTitle.getName());
        String text = getElementText(headerFileName);
        String fileTitle = text.substring(BEGIN_INDEX,text.indexOf(VERSION_NUMBER));
        assertEquals(fileTitle, expectedFileTitle.getName(),
            String.format("Document title not equals %s", expectedFileTitle.getName()));

        return this;
    }

    public boolean isDocumentFavourite()
    {
        waitUntilElementIsDisplayedWithRetry(By.cssSelector("div[class='node-social'] a.favourite-action-favourite.enabled"), 5);
        return isElementDisplayed(By.cssSelector("div[class='node-social'] a.favourite-action-favourite.enabled"));
    }

    public boolean isAddToFavoriteLinkDisplayed()
    {
        return waitUntilElementsAreVisible(By.cssSelector("a[class$='favourite-document']")).get(0).getAttribute("title")
            .equals("Add document to favorites");
    }

    public void clickOnLikeUnlike()
    {
        waitUntilElementIsVisible(likeUnlikeAction);
        clickElement(likeUnlikeAction);
    }
    public void clickOpenedFloder()
    {
        waitUntilElementIsVisible(folderOpenedLink);
        clickElement(folderOpenedLink);
    }

    public int getLikesNo()
    {
        return Integer.parseInt(getElementText(likesCount));
    }

    public String getFileName()
    {
        waitUntilElementIsDisplayedWithRetry(By.cssSelector("div[class='node-info'] h1"));
        return getElementText(documentTitle).replace(getFileVersion(), "");
    }

    public boolean isNewVersionAvailable(String version)
    {
        waitInSeconds(5);
        List<WebElement> versionList = waitUntilElementsAreVisible(latestVersion);
        return findFirstElementWithValue(versionList, version) != null;
    }

    public String getLockedMessage()
    {
        return getElementText(lockedMessage);
    }

    public String getFileVersion()
    {
        return getElementText(documentVersion);
    }

    public String getItemModifier()
    {
        return getElementText(itemModifier);
    }

    public String getModifyDate()
    {
        return getElementText(modifyDate);
    }

    public void clickOnFavoriteUnfavoriteLink()
    {
        waitUntilElementIsVisible(favoriteUnfavoriteAction);
        clickElement(favoriteUnfavoriteAction);
    }

    public String getFavoriteText()
    {
        return getElementText(favoriteUnfavoriteAction);
    }

    public boolean clickOnCommentDocument()
    {
        waitUntilElementIsVisible(commentDocument);
        clickElement(commentDocument);
        return isElementDisplayed(commentForm);
    }

    public DocumentDetailsPage addComment(String comment)
    {
        log.info("Add comment: {}", comment);
        switchTo().frame(findElement(commentContentIframe));
        WebElement commentTextArea = switchTo().activeElement();
        waitUntilElementIsVisible(commentTextArea);
        clickElement(commentTextArea);
        commentTextArea.sendKeys(comment);
        switchToDefaultContent();
        clickElement(addCommentButtonSave);
        waitUntilElementDisappears(message);
        return this;
    }
    public DocumentDetailsPage refreshpage()
    {
        getWebDriver().navigate().refresh();
        waitUntilDomReadyStateIsComplete();
        return this;
    }
    public WebDriver getWebDriver()
    {
        return webDriver.get();
    }

    public DocumentDetailsPage assertCommentsAreaIsOpened()
    {
        waitUntilElementIsVisible(commentsIframe);
        assertTrue(isElementDisplayed(commentsIframe), "Comments area is opened");
        return this;
    }

    public DocumentDetailsPage clickOkOnRevertPopup()
    {
        waitUntilElementIsVisible(okOnRevertPopup);
        clickElement(okOnRevertPopup);
        return this;
    }

    public String getCommentContent()
    {
        waitInSeconds(2);
        return getElementText(commentContent);
    }

    public boolean clickOnSharedLink()
    {
        waitUntilElementIsVisible(shareDocument);
        clickElement(shareDocument);
        return isElementDisplayed(sharePopUp);
    }

    public void clickDownloadButton()
    {
        waitUntilElementIsVisible(downloadButton);
        clickElement(downloadButton);
    }

    public String getMinimizeMaximizeText()
    {
        return getElementText(maximizeButton);
    }

    public void clickOnMaximizeMinimizeButton()
    {
        waitUntilElementIsVisible(maximizeButton);
        clickElement(maximizeButton);
    }

    public void clickOnZoomInButton()
    {
        waitUntilElementIsVisible(zoomInButton);
        clickElement(zoomInButton);
    }

    public void clickOnZoomOutButton()
    {
        waitUntilElementIsVisible(zoomOutButton);
        clickElement(zoomOutButton);
    }

    public String getScaleValue()
    {
        waitInSeconds(Wait.WAIT_1.getValue());
        return getElementText(scaleButton);
    }

    public void clickOnNextButton()
    {
        waitUntilElementIsVisible(nextButton);
        clickElement(nextButton);
    }

    public void clickOnPreviousButton()
    {
        waitUntilElementIsVisible(previousButton);
        clickElement(previousButton);
    }

    public String getCurrentPageNo()
    {
        return findElement(pageNumber).getAttribute("value");
    }

    public boolean isZoomInButtonDisplayed()
    {
        return isElementDisplayed(zoomInButton);
    }

    public boolean isZoomOutButtonDisplayed()
    {
        return isElementDisplayed(zoomOutButton);
    }

    public boolean isNextPageButton()
    {
        return isElementDisplayed(nextButton);
    }

    public boolean isPreviousPageButton()
    {
        return isElementDisplayed(previousButton);
    }

    public boolean clickOnSearchButton()
    {
        waitUntilElementIsVisible(searchButton);
        clickElement(searchButton);
        return isElementDisplayed(searchDialog);
    }

    public boolean isDocDetailsPageHeaderDisplayed()
    {
        waitUntilElementIsVisible(docDetailsPageHeader);
        return isElementDisplayed(docDetailsPageHeader);
    }

    public WebElement selectCommentDetailsRow(String comment)
    {
        return findFirstElementWithValue(commentsList, comment);
    }

    public void clickOnEditComment(String comment)
    {
        Actions actions = new Actions(webDriver.get());
        actions.moveToElement(selectCommentDetailsRow(comment));
        actions.moveToElement(selectCommentDetailsRow(comment).findElement(editComment));
        actions.click();
        actions.perform();
    }

    public String getCommentContent(String comment)
    {
        return selectCommentDetailsRow(comment).findElement(commContent).getText();
    }

    public void modifyCommContent(String modification)
    {
        switchTo().frame(findElement(commentContentIframe));
        WebElement editable = switchTo().activeElement();
        editable.sendKeys(modification);
        switchToDefaultContent();
        clickOnSaveButtonEditComment();
    }

    public String getPageNoReport()
    {
        return getElementText(pageReport);
    }

    public void clickOnNextPage()
    {
        waitUntilElementIsVisible(nextPage);
        clickElement(nextPage);
    }

    public void clickOnPreviousPage()
    {
        waitUntilElementIsVisible(previousPage);
        clickElement(previousPage);
    }

    public int getCommentsListSize()
    {
        return findElements(commentsList).size();
    }

    public void clickDocumentActionsOption(String optionName)
    {
        List<WebElement> optionsList = waitUntilElementsAreVisible(documentActionsOptionsSelector);
        selectOptionFromFilterOptionsList(optionName, optionsList);
    }

    public boolean arePropertiesDisplayed(String... expectedPropertiesList)
    {
        List<String> propertiesTextList = new ArrayList<>();
        List<WebElement> properties = waitUntilElementsAreVisible(propertiesList);
        for (WebElement property : properties)
        {
            propertiesTextList.add(property.getText().substring(0, property.getText().indexOf(":")));
        }
        return DataUtil.areListsEquals(propertiesTextList, expectedPropertiesList);
    }

    public boolean arePropertiesDisplayed_(ArrayList<String> expectedPropertiesList)
    {
        List<String> propertiesTextList = new ArrayList<>();
        List<WebElement> properties = waitUntilElementsAreVisible(propertiesList);
        for (WebElement property : properties)
        {
            propertiesTextList.add(property.getText().substring(0, property.getText().indexOf(":")));
        }
        return propertiesTextList.containsAll(expectedPropertiesList);
    }

    public DocumentDetailsPage assertPropertiesAreDisplayed(String... properties)
    {
        log.info("Assert properties are displayed {}", Arrays.asList(properties));
        assertTrue(arePropertiesDisplayed(properties), "Not all properties are displayed");
        return this;
    }

    public DocumentDetailsPage assertPropertiesAreDisplayed_(ArrayList<String> properties)
    {
        log.info("Assert properties are displayed {}", Arrays.asList(properties));
        assertTrue(arePropertiesDisplayed_(properties), "Not all properties are displayed");
        return this;
    }

    public DocumentDetailsPage assertPropertiesAreNotDisplayed(String... properties)
    {
        log.info("Assert properties are displayed {}", Arrays.asList(properties));
        assertFalse(arePropertiesDisplayed(properties), "All properties are displayed");
        return this;
    }

    public String checkPropertiesAreNotDisplayed(List<String> propertiesNotDisplayedList)
    {
        List<WebElement> properties = findElements(propertiesList);
        for (int i = 0; i < properties.size(); i++)
        {
            String property = properties.get(i).getText();
            for (String aPropertiesNotDisplayedList : propertiesNotDisplayedList)
            {
                if (property.equals(aPropertiesNotDisplayedList))
                    return propertiesNotDisplayedList.get(i);
            }
        }
        return "Given list isn't displayed";
    }

    public boolean isPropertyDisplayed(String propertyText)
    {
        List<WebElement> properties = findElements(propertiesList);
        for (WebElement aPropertiesList : properties)
        {
            if (aPropertiesList.getText().equals(propertyText))
                return true;
        }
        return false;
    }

    public String getPropertyValue(String propertyName)
    {
        List<WebElement> properties = findElements(propertiesList);
        for (int i = 0; i < properties.size(); i++)
        {
            if (properties.get(i).getText().replace(":", "").equals(propertyName))
            {
                return findElements(propertiesValuesList).get(i).getText();
            }
        }
        throw new PageOperationException(String.format("%s isn't displayed in Properties section!", propertyName));
    }

    public DocumentDetailsPage assertPropertyValueEquals(String propertyName, String expectedValue)
    {
        log.info("Assert property {} has value {}", propertyName, expectedValue);
        assertEquals(getPropertyValue(propertyName), expectedValue,
            String.format("Property %s has the expected value", propertyName));
        return this;
    }

    public boolean isDocumentsLinkPresent()
    {
        return isElementDisplayed(By.xpath("//span[@class = 'folder-link']//a"));
    }

    public void clickDocumentsLink()
    {
        clickElement(documentsLink);
    }

    public String isDocumentThumbnailDisplayed()
    {
        return findElement(By.xpath("//img[@class = 'node-thumbnail']")).getAttribute("src");
    }

    public boolean isLikeButtonPresent()
    {
        return isElementDisplayed(By.xpath("//span[@class= 'item item-separator item-social']//a[text()='Like']"));
    }

    public boolean isDownloadButtonDisplayed()
    {
        return isElementDisplayed(downloadButton);
    }

    public boolean isFilePropertiesDetailsDisplayed()
    {
        return isElementDisplayed(filePropertiesdetailsPanel);
    }

    public boolean isFolderActionsPanelDisplayed()
    {
        return isElementDisplayed(folderActionsPanel);
    }

    public boolean isSocialFeaturesActionsPanelDisplayed()
    {
        return isElementDisplayed(socialFeaturesPanel);
    }

    public boolean isTagsFeaturePanelDisplayed()
    {
        return isElementDisplayed(tagsFeaturePanel);
    }

    public void clickOnFolderFromBreadcrumbTrail()
    {
        waitUntilElementIsVisible(folderLinkFromBreadcrumbTrail);
        clickElement(folderLinkFromBreadcrumbTrail);
    }

    public boolean isDeleteButtonDisplayedForComment(String comment)
    {
        mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        return isElementDisplayed(deleteCommentButton);
    }

    public boolean isEditButtonDisplayedForComment(String comment)
    {
        mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        return isElementDisplayed(editCommentButton);
    }

    public void clickDeleteComment(String comment)
    {
        waitUntilElementIsVisible(commContent);
        mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        clickElement(deleteCommentButton);
    }

    public void clickEditComment(String comment)
    {
        mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        clickElement(editCommentButton);
    }

    public void clickDeleteOnDeleteComment()
    {
        waitUntilElementIsVisible(deleteButtonOnPrompt);
        clickElement(deleteButtonOnPrompt);
    }

    public boolean isDeleteCommentPromptDisplayed()
    {
        return isElementDisplayed(deleteCommentPrompt);
    }

    public String getNoCommentsText()
    {
        return getElementText(noComments);
    }

    public void editComment(String comment)
    {
        TinyMceEditor tinyMceEditor = new TinyMceEditor(webDriver);
        tinyMceEditor.setText(comment);
    }

    public boolean isEditCommentDisplayed()
    {
        return isElementDisplayed(editCommentBoxTitle);
    }

    public void clickOnSaveButtonEditComment()
    {
        waitInSeconds(2);
        clickElement(saveButtonEditComment);
    }

    public AspectsForm clickManageAspects()
    {
        clickElement(manageAspectsButton);
        waitInSeconds(2);
        return new AspectsForm(webDriver);
    }

    public EditPropertiesPage clickEditProperties()
    {
        clickElement(editPropertiesLink);
        return new EditPropertiesPage(webDriver);
    }

    public String getContentText()
    {
        waitInSeconds(5);
        return getElementText(contentText).trim();
    }
    public String getNoContentMassageText()
    {
        return getElementText(noContentText).trim();
    }
    public String getContentTitle()
    {
        return getElementText(contentTittle).trim();
    }
    public String getContentDescription()
    {
        return getElementText(contentDescription).trim();
    }

    public DocumentDetailsPage assertFileContentEquals(String expectedContent)
    {
        log.info("Assert file has content {}", expectedContent);
        assertEquals(getContentText(), expectedContent, "File content is correct");
        return this;
    }
    public DocumentDetailsPage assertFileContentContains(String expectedContent)
    {
        log.info("Assert file has content {}", expectedContent);
        assertTrue(getContentText().contains(expectedContent));
        return this;
    }
    public DocumentDetailsPage assertDacumentNOContent()
    {
        log.info("Assert file has content {}");
        assertEquals(getNoContentMassageText(), "This document has no content.", "File content is correct");
        return this;
    }

    public DocumentDetailsPage assertContentNameEquals(String contentName)
    {
        log.info("Assert file/folder name is {}", contentName);
        assertEquals(getFileName(), contentName, "File name is not correct");
        return this;
    }
    public DocumentDetailsPage assertContentTittleEquals(String contentName)
    {
        log.info("Assert file/folder Tittle is {}", contentName);
        assertEquals(getContentTitle(), contentName, "Content Title is not correct");
        return this;
    }
    public DocumentDetailsPage assert_ContentDescriptionEquals(String contentName)
    {
        log.info("Assert file/folder Tittle is {}", contentName);
        assertEquals(getContentDescription(), contentName, "Content Description is not correct");
        return this;
    }
    public DocumentDetailsPage assertPageTitleEquals(String pageTitle)
    {
        log.info("Verify that the page title is {}", pageTitle);
        waitInSeconds(3);
        assertEquals(getPageTitle(), pageTitle, "Page title is not matched with [%s]" +pageTitle);
        return this;
    }

    public boolean isRestrictableValueUpdated(String hours)
    {
        String restrictable = "//span[contains(@class,'viewmode-value')] [contains(text(),'" + hours + "')]";
        return isElementDisplayed(By.xpath(restrictable));
    }

    public boolean isAspectDisplayed(String aspectName)
    {
        By aspectXPath = By.xpath(String.format("//div[contains(@class, 'set-bordered-panel') and normalize-space(.)='%s']", aspectName));
        try
        {
            waitUntilElementIsVisible(aspectXPath);
        }
        catch (TimeoutException ex)
        {
            return isElementDisplayed(aspectXPath);
        }
        return isElementDisplayed(aspectXPath);
    }

    public boolean isAspectNotDisplayed(String aspectName)
    {
        By aspectXPath = By.xpath(String.format("//div[contains(@class, 'set-bordered-panel') and normalize-space(.)='%s']", aspectName));
        return isElementDisplayed(aspectXPath);
    }

    public boolean isActionAvailable(String actionName)
    {
        List<WebElement> optionsList = waitUntilElementsAreVisible(documentActionsOptionsSelector);
        return findFirstElementWithValue(optionsList, actionName) != null;
    }

    public boolean isVersionAvailable(String version)
    {
        List<WebElement> versionList = waitUntilElementsAreVisible(olderVersion);
        return findFirstElementWithValue(versionList, version) != null;
    }

    public void clickDownloadPreviousVersion()
    {
        clickElement(downloadPreviousVersion);
        acceptAlertIfDisplayed();
    }

    public boolean isRevertButtonAvailable()
    {
        return isElementDisplayed(revertButton);
    }

    public void clickRevertButton()
    {
        clickElement(revertButton);
        waitUntilElementIsVisible(okOnRevertPopup);
    }

    public void addCommentToItem(String comment)
    {
        switchTo().frame(waitUntilElementIsVisible(commentTextArea));
        WebElement commentBody = findElement(By.id("tinymce"));
        clearAndType(commentBody, comment);
        switchTo().defaultContent();
        clickElement(addCommentButtonSave);
    }

    public void clickAddCommentButton()
    {
        clickElement(addCommentButton);
        waitUntilElementIsVisible(By.cssSelector("form[id$='_default-add-form']"));
    }

    public void assertUploadedDocumentImageIsDisplayed()
    {
        log.info("Assert document image is displayed");
        assertTrue(isElementDisplayed(documentImage), "Document image is not displayed");
    }

    public CopyMoveUnzipToDialog clickCopyTo()
    {
        log.info("Click Copy To...");
        clickElement(copyToAction);
        return new CopyMoveUnzipToDialog(webDriver);
    }


    public ChangeContentTypeDialog clickChangeType()
    {
        log.info("Click Change Type");
        WebElement changeTypeButton = waitUntilElementIsVisible(changeTypeAction);
        mouseOver(changeTypeButton);
        clickElement(changeTypeButton);
        ChangeContentTypeDialog changeContentTypeDialog = new ChangeContentTypeDialog(webDriver);
        if(!changeContentTypeDialog.isDialogDisplayed())
        {
            log.error("Retry click on change type button");
            clickJS(changeTypeButton);
        }
        return new ChangeContentTypeDialog(webDriver);
    }

    public CopyMoveUnzipToDialog clickUnzipTo()
    {
        log.info("Click Unzip To...");
        clickElement(unzipToAction);
        return new CopyMoveUnzipToDialog(webDriver);
    }

    public DocumentDetailsPage assertVerifyCommentNumber(String content)
    {
        log.info("Verify the comment number {}", content);
        waitInSeconds(WAIT_1.getValue());
        assertTrue(getPageNoReport().equals(content),String.format("Wrong page report!"));
        return this;
    }

    public DocumentDetailsPage assertVerifyCommentContent(String content)
    {
        log.info("Verify the comment content {}", content);
        waitInSeconds(WAIT_1.getValue());
        assertEquals(getCommentContent(),content, String.format("Comment content not matched %s", content));
        return this;
    }

    public DocumentDetailsPage assertNextPageCommentContent(int content)
    {   waitInSeconds(WAIT_2.getValue());
        log.info("Verify the NextPageComment content Count{}", content);
        assertEquals(getCommentsListSize(),content, String.format("comment should be displayed as expected!"));
        return this;
    }



    public DocumentDetailsPage assertIsDeleteButtonDisplayedForComment(String comment)
    {
        log.info("Verify Delete button displayed for the comment Added {}", comment);
        assertTrue(isDeleteButtonDisplayedForComment(comment), "Delete Button not displayed for the comment %s" +comment);
        return this;
    }

    public DocumentDetailsPage assertIsEditButtonDisplayedForComment(String comment)
    {
        log.info("Verify Edit button displayed for the comment Added {}", comment);
        assertTrue(isEditButtonDisplayedForComment(comment), "Edit Button not displayed for the comment %s" +comment);
        return this;
    }

    public DocumentDetailsPage assertIsDeleteCommentPromptDisplayed()
    {
        log.info("Verify that the Delete Comment Prompt Displayed");
        assertTrue(isDeleteCommentPromptDisplayed(), "Delete Comment prompt is not displayed");
        return this;
    }

    public DocumentDetailsPage assertNoCommentNotificationText(String noComment)
    {
        log.info("Verify 'No Comment' notification text {}", noComment);
        assertEquals(getNoCommentsText(), noComment, String.format("No comment notification text no matched with %s", noComment));
        return this;
    }

    public DocumentDetailsPage assertIsEditCommentTitleDisplayed()
    {
        log.info("Verify Edit comment title of the box is Displayed");
        assertTrue(isEditCommentDisplayed(),"Edit comment is not displayed");
        return this;
    }

    public DocumentDetailsPage assertIsFileNameDisplayedOnPreviewPage(String fileName)
    {
        log.info("Verify File name displayed in preview page {}", fileName);
        assertEquals(getFileName(), fileName, String.format("File name not matched with %s ", fileName));
        return this;
    }

    public DocumentDetailsPage assertVerifyMinimizeButtonTextOnPreviewPage(String minimize)
    {
        log.info("Verify minimize text displayed on preview page after clicking on maximize {}", minimize);
        assertEquals(getMinimizeMaximizeText(), minimize, "Minimize button is not displayed!");
        return this;
    }

    public DocumentDetailsPage assertPageNoIsDifferent(String currentPageNo, String newPageNo)
    {
        log.info("Verify page number should be different {} {}", currentPageNo, newPageNo);
        assertNotEquals(currentPageNo, newPageNo, String.format("Page no is matched with current page no %s and new page no %s", currentPageNo, newPageNo));
        return this;
    }

    public DocumentDetailsPage assertPageNoIsSame(String currentPageNo, String newPageNo)
    {
        log.info("Verify page number should be same {} {}", currentPageNo, newPageNo);
        assertEquals(currentPageNo, newPageNo, String.format("Page no is not matched with current page no %s and new page no %s", currentPageNo, newPageNo));
        return this;
    }

    public DocumentDetailsPage assertScalValueIsDifferent(String currentScale, String newScale)
    {
        log.info("Verify scale value should be different {} {}", newScale, currentScale);
        assertNotEquals(currentScale, newScale, String.format("Scale value is not matched with current scale %s and new scale %s", currentScale, newScale));
        return this;
    }

    public DocumentDetailsPage assertScalValueIsSame(String currentScale, String newScale)
    {
        log.info("Verify scale value should be same {} {}", newScale, currentScale);
        assertEquals(currentScale, newScale, String.format("Scale value is matched with current scale %s and new scale %s", currentScale, newScale));
        return this;
    }

    public DocumentDetailsPage assertIsZoomInButtonNotDisplayed()
    {
        log.info("Verify Zoom In button is not displayed");
        assertFalse(isZoomInButtonDisplayed(), "Zoom in button is displayed!");
        return this;
    }

    public DocumentDetailsPage assertIsZoomOutButtonNotDisplayed()
    {
        log.info("Verify Zoom Out button is not displayed");
        assertFalse(isZoomOutButtonDisplayed(), "Zoom Out button is displayed!");
        return this;
    }

    public DocumentDetailsPage assertIsNextPageButtonNotDisplayed()
    {
        log.info("Verify next page button is not displayed");
        assertFalse(isNextPageButton(), "Next Page button is displayed!");
        return this;
    }

    public DocumentDetailsPage assertIsPreviousPageButtonNotDisplayed()
    {
        log.info("Verify Previous page button is not displayed");
        assertFalse(isPreviousPageButton(), "Previous Page Button is displayed!");
        return this;
    }

    public DocumentDetailsPage assertVerifyFileVersion(String version)
    {
        log.info("Verify file version {}", version);
        assertEquals(getFileVersion(), version, String.format("File version not matched with %s ", version));
        return this;
    }

    public DocumentDetailsPage assertVerifyItemModifire(String firstName, String lastName)
    {
        log.info("Verify the modifire name on preview page {}", firstName + " " + lastName);
        assertEquals(getItemModifier(), firstName+ " " +lastName, String.format("Item modifire name not matched with %s ", firstName+ " " +lastName));
        return this;
    }

    public DocumentDetailsPage assertVerifyModifiedDate(String date)
    {
        log.info("Verify item modified date {}", date);
        assertTrue(getModifyDate().contains(date), "Wrong modification date!");
        return this;
    }

    public DocumentDetailsPage assertVerifyFileInDirectory(String fileName)
    {
        log.info("Verify that the downloaded content/file in to the Directory. {}", fileName);
        assertTrue(isFileInDirectory(fileName, null), "File does not exist!");
        return this;
    }

    public DocumentDetailsPage assertVerifyNoOfLikes(int noOfLikes)
    {
        log.info("Verify No of likes on the content {}", noOfLikes);
        assertEquals(getLikesNo(), noOfLikes, "No of likes not matched with %s " +noOfLikes);
        return this;
    }

    public DocumentDetailsPage assertContantMarkedAsFavorite()
    {
        log.info("Verify that the contant marked as Favorite");
        assertTrue(getFavoriteText().isEmpty(), "File is not marked as favorite!");
        return this;
    }

    public DocumentDetailsPage assertContantNotMarkedAsFavorite()
    {
        log.info("Verify Contant is not marked as favorite");
        assertTrue(getFavoriteText().equals("Favorite"), "File should be already added to favorite!");
        return this;
    }

    public DocumentDetailsPage assertVerifyFilePropertiesDetailsDisplayed()
    {
        log.info("File Properties details are displayed");
        assertTrue(isFilePropertiesDetailsDisplayed(), "File Properties details are not displayed");
        return this;
    }

    public DocumentDetailsPage assertVerifySocialFeaturesActionsPanelDisplayed()
    {
        log.info("Social features actions panel is  displayed");
        assertTrue(isSocialFeaturesActionsPanelDisplayed(), "Social features actions panel is not displayed");
        return this;
    }

    public DocumentDetailsPage assertVerifyFolderActionsPanelDisplayed()
    {
        log.info("Folder actions panel is displayed");
        assertTrue(isFolderActionsPanelDisplayed(), "Folder actions panel is not displayed");
        return this;
    }

    public DocumentDetailsPage assertVerifyTagsFeaturePanelDisplayed()
    {
        log.info("Tags feature panel is  displayed");
        assertTrue(isTagsFeaturePanelDisplayed(), "Tags feature panel is not displayed");
        return this;
    }

    public DocumentDetailsPage assertIsDocumentsLinkPresent()
    {
        log.info("Verify that the documents link is present");
        assertTrue(isDocumentsLinkPresent(), "Documents link to return to Document Library is not displayed");
        return this;
    }

    public DocumentDetailsPage assertIsDocumentThumbnailDisplayed(String imageName)
    {
        log.info("Verify that the Document thumb nail image displayed or not {}", imageName);
        assertTrue(isDocumentThumbnailDisplayed().contains(imageName), String.format("Document thumb nail not contains image name as %s", imageName));
        return this;
    }

    public DocumentDetailsPage assertLikeButtonDisplayedOnDocumentDetailsPage()
    {
        log.info("Verify that like button present for the document on the document details page");
        assertTrue(isLikeButtonPresent(), "The like button is not displayed");
        return this;
    }

    public DocumentDetailsPage assertIsAddToFavoriteLinkDisplayed()
    {
        log.info("Verify that document favorite link is displayed on preview page");
        assertTrue(isAddToFavoriteLinkDisplayed(), "Favorite button is not displayed");
        return this;
    }

    public DocumentDetailsPage assertContentDescriptionEquals(String expectedEditedContent)
    {   waitInSeconds(WAIT_1.getValue());
        log.info("Verify that the editedContent is displaying correctly");
        assertEquals(getElementText(contentText).trim(), expectedEditedContent, String.format("The actual content is not matched with %s ", expectedEditedContent));
        return this;
    }

    public DocumentDetailsPage assertIsAspectDisplayedOnDetailsPage(String aspectName)
    {
        log.info("Verify that the Aspect is displayed on the Document Details Page {}", aspectName);
        assertTrue(isAspectDisplayed(aspectName), String.format("Aspect Added on the details page is not matched with %s ", aspectName));
        return this;
    }

    public DocumentDetailsPage assertIsAspectNotDisplayedOnDetailsPage(String aspectName)
    {
        log.info("Verify that the Aspect is not displayed on the Document Details Page {}", aspectName);
        assertFalse(isAspectNotDisplayed(aspectName), "Aspect added on the page is displayed %s " +aspectName);
        return this;
    }
    public DocumentDetailsPage assertIsPropertyDisplayed(String propertyText)
    {
        log.info("Verify that the property is displayed on the document details page {}", propertyText);
        assertTrue(isPropertyDisplayed(propertyText), String.format("Property text not matched with %s ", propertyText));
        return this;
    }

    public DocumentDetailsPage assertIsPropertyNotDisplayed(String propertyText)
    {
        log.info("Verify that the property is not displayed on the document details page {}", propertyText);
        assertFalse(isPropertyDisplayed(propertyText), "Property is displayed");
        return this;
    }

    public DocumentDetailsPage assertIsRestrictableValueIsEquals(String hrs)
    {
        log.info("Verify that the value of Offline Expires After (hours) is Equals {}", hrs);
        assertTrue(isRestrictableValueUpdated(hrs), "The value for Offline Expires After (hours) has not been updated");
        return this;
    }

    public String getModifierValue()
    {
        return getElementText(modifierName);
    }

    public DocumentDetailsPage addCommentAndCancel(String comment)
    {
        log.info("Add comment: {}", comment);
        switchTo().frame(findElement(commentContentIframe));
        WebElement commentTextArea = switchTo().activeElement();
        waitUntilElementIsVisible(commentTextArea);
        clickElement(commentTextArea);
        commentTextArea.sendKeys(comment);
        switchToDefaultContent();
        clickElement(addCommentButtonCancel);
        waitUntilElementDisappears(message);
        return this;
    }

    public DocumentDetailsPage assertVerifyNoCommentsIsDisplayed()
    {
        log.info("Verify the comment content");
        waitInSeconds(WAIT_1.getValue());
        assertTrue(isElementDisplayed(noComments), "Check Comments");
        return this;
    }

    public String getBalloonMessage()
    {
        return findElement(balloon).getText();
    }

    public DocumentDetailsPage clearCommentBoxAndSave(String BallonMessage)
    {
        log.info("Clear Comment box and save");
        switchTo().frame(findElement(commentContentIframe));
        WebElement commentTextArea = switchTo().activeElement();
        waitUntilElementIsVisible(commentTextArea);
        clickElement(commentTextArea);
        commentTextArea.clear();
        switchToDefaultContent();
        clickElement(addCommentButtonSave);
        waitInSeconds(2);
        assertTrue(getBalloonMessage().equals(BallonMessage), "Cannot save with empty string: Check Ballon Message");
        return this;
    }

    public String getShareLink()
    {
        return findElement(shareUrl).getAttribute("value");
    }

    public void openUrlNewTab() {
        log.info("open new tab");
        String copiedUrl = getShareLink();
        String parentWindow = getWebDriver().getWindowHandle();
        ((JavascriptExecutor) getWebDriver()).executeScript("window.open();");

        log.info("switch to new tab");
        Set<String> allWindows = getWebDriver().getWindowHandles();
        for (String window : allWindows) {
            if (!window.equals(parentWindow)) {
                getWebDriver().switchTo().window(window);
                break;
            }
        }
        getWebDriver().get(copiedUrl);
    }

    public DocumentDetailsPage verifyHelpDetailsInComment()
    {
        log.info("Add comment");
        switchTo().frame(findElement(commentContentIframe));
        WebElement commentTextArea = switchTo().activeElement();
        waitUntilElementIsVisible(commentTextArea);
        clickElement(commentTextArea);
        switchToDefaultContent();
        assertTrue(isHelpIconPresent(),"Check Help Icon");
        clickElement(helpIcon);
        waitInSeconds(2);
        assertEquals(getHelpText(), helpIconText, "check help Icon text");
        return this;
    }

    public boolean isHelpIconPresent()
    {
        return isElementDisplayed(helpIcon);
    }

    public boolean isEditingToolbarPresent()
    {
        return isElementDisplayed(commentsEditingToolbar);
    }

    public String getHelpText(){
        return findElement(helpText).getText();
    }


    public DocumentDetailsPage verifyEditingToolbarsForComment()
    {
        log.info("Add comment");
        waitInSeconds(2);
        assertTrue(isEditingToolbarPresent(),"Check Editing toolbar in comment box");
        return this;
    }

    public void clickCancelOnDeleteCommentPrompt()
    {
        waitInSeconds(2);
        clickElement(cancelButtonOnPrompt);
    }

    public DocumentDetailsPage clickCommentContent(String content)
    {
        log.info("click comment content {}", content);
        waitInSeconds(2);
        By commentContent = By.xpath("//a[text()=\"https://support.hyland.com/home\"]");
        findElement(commentContent).click();
        return this;
    }
}