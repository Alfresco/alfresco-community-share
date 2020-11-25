package org.alfresco.po.share.alfrescoContent.document;

import org.alfresco.common.DataUtil;
import org.alfresco.common.Utils;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ChangeContentTypeDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DocumentDetailsPage extends SharePage2<DocumentDetailsPage>
{
    private FileModel currentFile;
    private static final int BEGIN_INDEX = 0;
    private static final String VERSION_NUMBER = "1";

    private final By noComments = By.cssSelector("div[id*='_default-comments-list'] td[class ='yui-dt-empty']");
    private final By managePermissionsLink = By.cssSelector(".folder-actions.folder-details-panel a[title='Manage Permissions']");
    private final By documentTitle = By.cssSelector("div[class='node-info'] h1");
    @RenderWebElement
    private final By docDetailsPageHeader = By.cssSelector(".node-header");
    private final By headerFileName = By.cssSelector(".node-header h1");
    private final By socialBar = By.cssSelector("div[class='node-social']");
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
    @RenderWebElement
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
    private final By contentError = By.cssSelector(".message");
    private final By addCommentButton = By.cssSelector("span[class$='onAddCommentClick'] button");
    private final By commentsIframe = By.cssSelector("iframe[id$='default-add-content_ifr']");
    private final By copyToAction = By.id("onActionCopyTo");
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
    private final By unzipToAction = By.id("onActionUnzipTo");

    public DocumentDetailsPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public DocumentDetailsPage navigate(FileModel file)
    {
        LOG.info(String.format("Navigate to document details of file: %s", file.getCmisLocation()));
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

    public DocumentDetailsPage assertDocumentDetailsPageIsOpened()
    {
        LOG.info("Assert Document Details page is opened");
        assertTrue(getBrowser().isElementDisplayed(docDetailsPageHeader), "Document details page is opened");
        return this;
    }

    public DocumentDetailsPage assertDocumentTitleEquals(FileModel expectedFileTitle)
    {
        LOG.info("Assert file title equals: {}", expectedFileTitle.getName());
        String text = getElementText(headerFileName);
        String fileTitle = text.substring(BEGIN_INDEX,text.indexOf(VERSION_NUMBER));
        assertEquals(fileTitle, expectedFileTitle.getName(),
            String.format("Document title not equals %s", expectedFileTitle.getName()));

        return this;
    }

    public boolean isDocumentFavourite()
    {
        getBrowser().waitUntilElementIsDisplayedWithRetry(By.cssSelector("div[class='node-social'] a.favourite-action-favourite.enabled"), 5);
        return getBrowser().isElementDisplayed(By.cssSelector("div[class='node-social'] a.favourite-action-favourite.enabled"));
    }

    public boolean isAddToFavoriteLinkDisplayed()
    {
        return getBrowser().waitUntilElementsVisible(By.cssSelector("a[class$='favourite-document']")).get(0).getAttribute("title")
                  .equals("Add document to favorites"); //TODO: update
    }

    public boolean isAddCommentBlockDisplayed()
    {
        getBrowser().waitUntilElementIsDisplayedWithRetry(addCommentBlock);
        return getBrowser().isElementDisplayed(addCommentBlock);
    }

    public void clickOnLikeUnlike()
    {
        getBrowser().findElement(likeUnlikeAction).click();
    }

    public int getLikesNo()
    {
        return Integer.parseInt(getElementText(likesCount));
    }

    public boolean isCommentBoxOpened()
    {
        return getBrowser().isElementDisplayed(By.xpath("//iframe[contains(@title,'Rich Text Area')]"));
    }

    public DocumentDetailsPage assertCommentsAreaIsOpened()
    {
        assertTrue(getBrowser().isElementDisplayed(commentsIframe), "Comments area is opened");
        return this;
    }

    public String getFileName()
    {
        getBrowser().waitUntilElementIsDisplayedWithRetry(By.cssSelector("div[class='node-info'] h1"));
        return getElementText(documentTitle).replace(getFileVersion(), "");
    }

    public boolean isNewVersionAvailable(String version)
    {
        List<WebElement> versionList = getBrowser().waitUntilElementsVisible(latestVersion);
        return getBrowser().findFirstElementWithValue(versionList, version) != null;
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
        getBrowser().findElement(favoriteUnfavoriteAction).click();
    }

    public String getFavoriteText()
    {
        return getElementText(favoriteUnfavoriteAction);
    }

    public boolean clickOnCommentDocument()
    {
        getBrowser().findElement(commentDocument).click();
        return getBrowser().isElementDisplayed(commentForm);
    }

    public DocumentDetailsPage addComment(String comment)
    {
        LOG.info("Add comment: {}", comment);
        getBrowser().waitUntilElementClickable(commentContentIframe, WAIT_5).click();
        getBrowser().findElement(commentContentIframe).sendKeys(comment);
        getBrowser().findElement(addCommentButtonSave).click();
        getBrowser().waitUntilElementDisappears(message, WAIT_5);

        return (DocumentDetailsPage) renderedPage();
    }

    public DocumentDetailsPage assertAddedCommentEquals(String expectedComment)
    {
        LOG.info("Assert added comment equals: {}", expectedComment);
        assertEquals(getCommentContent(expectedComment), expectedComment);
        return this;
    }

    public DocumentDetailsPage clickOkOnRevertPopup()
    {
        getBrowser().findElement(okOnRevertPopup).click();
        return (DocumentDetailsPage) this.renderedPage();
    }

    public String getCommentContent()
    {
        return getBrowser().waitUntilElementVisible(commentContent).getText();
    }

    public boolean clickOnSharedLink()
    {
        getBrowser().findElement(shareDocument).click();
        return getBrowser().isElementDisplayed(sharePopUp);
    }

    public void clickOnEditGoogleDocs()
    {
        getBrowser().findElement(googleDocsEdit).click();
    }

    public void clickDownloadButton()
    {
        getBrowser().findElement(downloadButton).click();
    }

    public String getMinimizeMaximizeText()
    {
        return getElementText(maximizeButton);
    }

    public void clickOnMaximizeMinimizeButton()
    {
        getBrowser().findElement(maximizeButton).click();
    }

    public void clickOnZoomInButton()
    {
        getBrowser().findElement(zoomInButton).click();
    }

    public void clickOnZoomOutButton()
    {
        getBrowser().findElement(zoomOutButton).click();
    }

    public String getScaleValue()
    {
        return getBrowser().findElement(scaleButton).getText();
    }

    public void clickOnNextButton()
    {
        getBrowser().findElement(nextButton).click();
    }

    public void clickOnPreviousButton()
    {
        getBrowser().findElement(previousButton).click();
    }

    public String getCurrentPageNo()
    {
        return getBrowser().findElement(pageNumber).getAttribute("value");
    }

    public boolean isZoomInButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(zoomInButton);
    }

    public boolean isZoomOutButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(zoomOutButton);
    }

    public boolean isMaximizetButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(maximizeButton);
    }

    public boolean isNextPageButton()
    {
        return getBrowser().isElementDisplayed(nextButton);
    }

    public boolean isPreviousPageButton()
    {
        return getBrowser().isElementDisplayed(previousButton);
    }

    public boolean clickOnSearchButton()
    {
        getBrowser().findElement(searchButton).click();
        return getBrowser().isElementDisplayed(searchDialog);
    }

    public boolean isDocDetailsPageHeaderDisplayed()
    {
        return getBrowser().isElementDisplayed(docDetailsPageHeader);
    }

    public WebElement selectCommentDetailsRow(String comment)
    {
        return getBrowser().findFirstElementWithValue(commentsList, comment);
    }

    public void clickOnEditComment(String comment)
    {
        Actions actions = new Actions(getBrowser());
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
        getBrowser().switchTo().frame(getBrowser().findElement(commentContentIframe));
        WebElement editable = getBrowser().switchTo().activeElement();
        editable.sendKeys(modification);
        getBrowser().switchToDefaultContent();
    }

    public String getPageNoReport()
    {
        return getElementText(pageReport);
    }

    public void clickOnNextPage()
    {
        getBrowser().findElement(nextPage).click();
    }

    public void clickOnPreviousPage()
    {
        getBrowser().findElement(previousPage).click();
    }

    public int getCommentsListSize()
    {
        return getBrowser().findElements(commentsList).size();
    }

    public void clickDocumentActionsOption(String optionName)
    {
        List<WebElement> optionsList = getBrowser().waitUntilElementsVisible(documentActionsOptionsSelector);
        getBrowser().selectOptionFromFilterOptionsList(optionName, optionsList);
    }

    public boolean arePropertiesDisplayed(String... expectedPropertiesList)
    {
        List<String> propertiesTextList = new ArrayList<>();
        List<WebElement> properties = getBrowser().findElements(propertiesList);
        for (WebElement property : properties)
        {
            propertiesTextList.add(property.getText().substring(0, property.getText().indexOf(":")));
        }
        return DataUtil.areListsEquals(propertiesTextList, expectedPropertiesList);
    }

    public DocumentDetailsPage assertPropertiesAreDisplayed(String... properties)
    {
        LOG.info("Assert properties are displayed {}", Arrays.asList(properties));
        assertTrue(arePropertiesDisplayed(properties), "Not all properties are displayed");
        return this;
    }

    public String checkPropertiesAreNotDisplayed(ArrayList<String> propertiesNotDisplayedList)
    {
        List<WebElement> properties = getBrowser().findElements(propertiesList);
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
        List<WebElement> properties = getBrowser().findElements(propertiesList);
        for (WebElement aPropertiesList : properties)
        {
            if (aPropertiesList.getText().equals(propertyText))
                return true;
        }
        return false;
    }

    public String getPropertyValue(String propertyName)
    {
        List<WebElement> properties = getBrowser().findElements(propertiesList);
        for (int i = 0; i < properties.size(); i++)
        {
            if (properties.get(i).getText().replace(":", "").equals(propertyName))
            {
                return getBrowser().findElements(propertiesValuesList).get(i).getText();
            }
        }
        throw new PageOperationException(String.format("%s isn't displayed in Properties section!", propertyName));
    }

    public DocumentDetailsPage assertPropertyValueEquals(String propertyName, String expectedValue)
    {
        LOG.info("Assert property {} has value {}", propertyName, expectedValue);
        assertEquals(getPropertyValue(propertyName), expectedValue,
            String.format("Property %s has the expected value", propertyName));
        return this;
    }

    public boolean isDocumentsLinkPresent()
    {
        return getBrowser().isElementDisplayed(By.xpath("//span[@class = 'folder-link']//a"));
    }

    public void clickDocumentsLink()
    {
        getBrowser().findElement(documentsLink).click();
    }

    public String isDocumentThumbnailDisplayed()
    {
        return getBrowser().findElement(By.xpath("//img[@class = 'node-thumbnail']")).getAttribute("src");
    }

    public boolean isLikeButtonPresent()
    {
        return getBrowser().isElementDisplayed(By.xpath("//span[@class= 'item item-separator item-social']//a[text()='Like']"));
    }

    public boolean isDownloadButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(downloadButton);
    }

    public boolean isFilePropertiesDetailsDisplayed()
    {
        return getBrowser().isElementDisplayed(filePropertiesdetailsPanel);
    }

    public boolean isFolderActionsPanelDisplayed()
    {
        return getBrowser().isElementDisplayed(folderActionsPanel);
    }

    public boolean isSocialFeaturesActionsPanelDisplayed()
    {
        return getBrowser().isElementDisplayed(socialFeaturesPanel);
    }

    public boolean isTagsFeaturePanelDisplayed()
    {
        return getBrowser().isElementDisplayed(tagsFeaturePanel);
    }

    public void clickOnFolderFromBreadcrumbTrail()
    {
        getBrowser().findElement(folderLinkFromBreadcrumbTrail).click();
    }

    public boolean isDeleteButtonDisplayedForComment(String comment)
    {
        getBrowser().mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        return getBrowser().isElementDisplayed(deleteCommentButton);
    }

    public boolean isEditButtonDisplayedForComment(String comment)
    {
        getBrowser().mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        return getBrowser().isElementDisplayed(editCommentButton);
    }

    public void clickDeleteComment(String comment)
    {
        getBrowser().waitUntilElementVisible(commContent);
        getBrowser().mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        getBrowser().waitUntilElementClickable(deleteCommentButton).click();
    }

    public void clickEditComment(String comment)
    {
        getBrowser().mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        getBrowser().findElement(editCommentButton).click();
    }

    public void clickDeleteComment(String comment, String comment2)
    {
        getBrowser().waitUntilElementVisible(commContent);
        try
        {
            getBrowser().mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        }
        catch(Exception e)
        {
            getBrowser().mouseOver(selectCommentDetailsRow(comment2).findElement(commContent));
        }
        getBrowser().waitUntilElementClickable(deleteCommentButton).click();
    }

    public void clickDeleteOnDeleteComment()
    {
        getBrowser().findElement(deleteButtonOnPrompt).click();
    }

    public boolean isDeleteCommentPromptDisplayed()
    {
        return getBrowser().isElementDisplayed(deleteCommentPrompt);
    }

    public String getNoCommentsText()
    {
        return getElementText(noComments);
    }

    public void editComment(String comment)
    {
        TinyMceEditor tinyMceEditor = new TinyMceEditor(browser);
        tinyMceEditor.setText(comment);
    }

    public boolean isEditCommentDisplayed()
    {
        return getBrowser().isElementDisplayed(editCommentBoxTitle);
    }

    public void clickOnSaveButtonEditComment()
    {
        getBrowser().findElement(saveButtonEditComment).click();
    }

    public AspectsForm clickManageAspects()
    {
        getBrowser().findElement(manageAspectsButton).click();
        return (AspectsForm) new AspectsForm(browser).renderedPage();
    }

    public EditPropertiesPage clickEditProperties()
    {
        getBrowser().findElement(editPropertiesLink).click();
        return (EditPropertiesPage) new EditPropertiesPage(browser).renderedPage();
    }

    public String getContentText()
    {
        return getBrowser().waitUntilElementVisible(contentText).getText().trim();
    }

    public DocumentDetailsPage assertFileContentEquals(String expectedContent)
    {
        LOG.info("Assert file has content {}", expectedContent);
        assertEquals(getContentText(), expectedContent, "File content is correct");
        return this;
    }

    public boolean isRestrictableValueUpdated(String hours)
    {
        String restrictable = "//span[contains(@class,'viewmode-value')] [contains(text(),'" + hours + "')]";
        return getBrowser().isElementDisplayed(By.xpath(restrictable));
    }

    public boolean isAspectDisplayed(String aspectName)
    {
        By aspectXPath = By.xpath(String.format("//div[contains(@class, 'set-bordered-panel') and normalize-space(.)='%s']", aspectName));
        try
        {
            getBrowser().waitUntilElementVisible(aspectXPath);
        }
        catch (TimeoutException ex)
        {
            return getBrowser().isElementDisplayed(aspectXPath);
        }
        return getBrowser().isElementDisplayed(aspectXPath);
    }

    public boolean isAspectNotDisplayed(String aspectName)
    {
        By aspectXPath = By.xpath(String.format("//div[contains(@class, 'set-bordered-panel') and normalize-space(.)='%s']", aspectName));
        getBrowser().waitUntilElementDeletedFromDom(aspectXPath);
        return getBrowser().isElementDisplayed(aspectXPath);
    }

    public boolean isActionAvailable(String actionName)
    {
        List<WebElement> optionsList = getBrowser().waitUntilElementsVisible(documentActionsOptionsSelector);
        return getBrowser().findFirstElementWithValue(optionsList, actionName) != null;
    }

    public boolean isVersionAvailable(String version)
    {
        List<WebElement> versionList = getBrowser().waitUntilElementsVisible(olderVersion);
        return getBrowser().findFirstElementWithValue(versionList, version) != null;
    }

    public void clickDownloadPreviousVersion()
    {
        getBrowser().waitUntilElementClickable(downloadPreviousVersion).click();
        acceptAlertIfDisplayed();
    }

    public boolean isRevertButtonAvailable()
    {
        return getBrowser().isElementDisplayed(revertButton);
    }

    public void clickRevertButton()
    {
        getBrowser().waitUntilElementClickable(revertButton).click();
        getBrowser().waitUntilElementVisible(okOnRevertPopup);
    }

    public void addCommentToItem(String comment)
    {
        getBrowser().switchTo().frame(getBrowser().waitUntilElementVisible(commentTextArea));
        WebElement commentBody = getBrowser().findElement(By.id("tinymce"));
        Utils.clearAndType(commentBody, comment);
        getBrowser().switchTo().defaultContent();
        getBrowser().waitUntilElementClickable(addCommentButtonSave).click();
    }

    public void clickAddCommentButton()
    {
        getBrowser().waitUntilElementClickable(addCommentButton).click();
        getBrowser().waitUntilElementVisible(By.cssSelector("form[id$='_default-add-form']"), 5L);
    }

    public void assertUploadedDocumentImageIsDisplayed()
    {
        LOG.info("Assert document image is displayed");
        assertTrue(getBrowser().isElementDisplayed(documentImage), "Document image is not displayed");
    }

    public CopyMoveUnzipToDialog clickCopyTo()
    {
        LOG.info("Click Copy To...");
        getBrowser().waitUntilElementClickable(copyToAction).click();
        return (CopyMoveUnzipToDialog) new CopyMoveUnzipToDialog(browser).renderedPage();
    }


    public ChangeContentTypeDialog clickChangeType()
    {
        LOG.info("Click Change Type");
        getBrowser().waitUntilElementClickable(changeTypeAction).click();
        return (ChangeContentTypeDialog) new ChangeContentTypeDialog(browser).renderedPage();
    }

    public CopyMoveUnzipToDialog clickUnzipTo()
    {
        LOG.info("Click Unzip To...");
        getBrowser().waitUntilElementClickable(unzipToAction).click();
        return (CopyMoveUnzipToDialog) new CopyMoveUnzipToDialog(browser).renderedPage();
    }
}