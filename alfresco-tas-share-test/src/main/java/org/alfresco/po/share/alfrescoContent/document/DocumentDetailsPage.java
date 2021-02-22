package org.alfresco.po.share.alfrescoContent.document;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.DataUtil;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ChangeContentTypeDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.model.FileModel;
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
    private final By downloadPreviousVersion = By.cssSelector("div[id$='_default-olderVersions'] div.version-panel-right a.download");
    private final By revertButton = By.cssSelector("div[id$='_default-olderVersions'] div.version-panel-right a[class$='_default revert']");
    private final By commentTextArea = By.cssSelector("iframe[id*='comments']");
    private final By lockedMessage = By.xpath(".//span[contains(@class,'locked')]");
    private final By okOnRevertPopup = By.cssSelector("#alfresco-revertVersion-instance-ok-button-button");
    private final By addCommentButton = By.cssSelector("span[class$='onAddCommentClick'] button");
    private final By commentsIframe = By.cssSelector("iframe[id$='default-add-content_ifr']");
    private final By copyToAction = By.cssSelector("#onActionCopyTo > a");
    private final By documentsLink = By.xpath("//span[@class = 'folder-link']//a");
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
                  .equals("Add document to favorites"); //TODO: update
    }

    public void clickOnLikeUnlike()
    {
        waitUntilElementIsVisible(likeUnlikeAction);
        clickElement(likeUnlikeAction);
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
        waitUntilElementIsVisible(commentContentIframe);
        clickElement(commentContentIframe);
        findElement(commentContentIframe).sendKeys(comment);
        clickElement(addCommentButtonSave);
        waitUntilElementDisappears(message);

        return this;
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

    public DocumentDetailsPage assertPropertiesAreDisplayed(String... properties)
    {
        log.info("Assert properties are displayed {}", Arrays.asList(properties));
        assertTrue(arePropertiesDisplayed(properties), "Not all properties are displayed");
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
        clickElement(saveButtonEditComment);
    }

    public AspectsForm clickManageAspects()
    {
        clickElement(manageAspectsButton);
        return new AspectsForm(webDriver);
    }

    public EditPropertiesPage clickEditProperties()
    {
        clickElement(editPropertiesLink);
        return new EditPropertiesPage(webDriver);
    }

    public String getContentText()
    {
        return getElementText(contentText).trim();
    }

    public DocumentDetailsPage assertFileContentEquals(String expectedContent)
    {
        log.info("Assert file has content {}", expectedContent);
        assertEquals(getContentText(), expectedContent, "File content is correct");
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
        waitUntilElementDeletedFromDom(aspectXPath);
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
}