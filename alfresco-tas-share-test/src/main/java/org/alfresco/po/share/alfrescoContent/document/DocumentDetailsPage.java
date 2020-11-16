package org.alfresco.po.share.alfrescoContent.document;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.alfresco.common.DataUtil;
import org.alfresco.common.Utils;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class DocumentDetailsPage extends DocumentCommon<DocumentDetailsPage>
{
    private static final int BEGIN_INDEX = 0;
    private static final String VERSION_NUMBER = "1";

    //@Autowired
    EditPropertiesPage editPropertiesPage;

    @Autowired
    private AspectsForm aspectsForm;

   // @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    private TinyMceEditor tinyMceEditor;

    @Autowired
    private CopyMoveUnzipToDialog copyMoveDialog;

    @FindBy (css = "div[id*='_default-comments-list'] td[class ='yui-dt-empty']")
    public WebElement noComments;

    @FindBy (css = ".folder-actions.folder-details-panel a[title='Manage Permissions']")
    protected WebElement managePermissionsLink;

    @FindBy (css = "div[class='node-info'] h1")
    protected WebElement documentTitle;

    @RenderWebElement
    @FindBy (css = ".node-header")
    protected WebElement docDetailsPageHeader;

    @FindBy (css = ".node-header h1")
    protected WebElement headerFileName;

    @RenderWebElement
    @FindBy (linkText = "Download")
    protected WebElement downloadDocument;

    @FindBy (css = "div[class='node-social']")
    protected WebElement socialBar;

    @FindBy (css = "[class*=like-action]")
    protected WebElement likeUnlikeAction;

    @FindBy (css = "[class=likes-count]")
    protected WebElement likesCount;

    @FindBy (css = ".item-modifier a")
    protected WebElement itemModifier;

    @FindBy (css = ".item-modifier span")
    protected WebElement modifyDate;

    @FindBy (css = "div[class='node-info'] h1 span")
    protected WebElement documentVersion;

    @FindBy (css = "[class*=favourite-action]")
    protected WebElement favoriteUnfavoriteAction;

    @FindBy (css = "[name*='commentNode']")
    protected WebElement commentDocument;

    @FindBy (css = "[class=comment-form]")
    protected WebElement commentForm;

    @FindBy (css = "[id*='default-add-submit-button']")
    protected WebElement addCommentButtonSave;

    @FindBy (css = "[class=comment-content]")
    protected WebElement commentContent;

    @FindBy (css = "[class*=quickshare-action] [class=bd]")
    protected WebElement sharePopUp;

    @FindBy (css = "[title*='Share document']")
    protected WebElement shareDocument;

    @FindBy (css = "[id*=default-fullpage-button]")
    protected WebElement maximizeButton;

    @FindBy (css = "[id*=zoomIn-button]")
    protected WebElement zoomInButton;

    @FindBy (css = "[id*=zoomOut-button]")
    protected WebElement zoomOutButton;

    @FindBy (css = "[id*=default-scaleSelectBtn-button]")
    protected WebElement scaleButton;

    @FindBy (css = "[id*=default-next-button]")
    protected WebElement nextButton;

    @FindBy (css = "[id*=default-previous-button]")
    protected WebElement previousButton;

    @FindBy (css = "[id*=default-pageNumber]")
    protected WebElement pageNumber;

    @FindBy (css = "[id*=searchBarToggle-button]")
    protected WebElement searchButton;

    @FindBy (css = "[class*=searchDialog]")
    protected WebElement searchDialog;

    @FindBy (css = "[id*=default-paginator-top] [id*=page-report]")
    protected WebElement pageReport;

    @FindBy (css = "[id*=default-paginator-top] [class*=next]")
    protected WebElement nextPage;

    @FindBy (css = "[id*=default-paginator-top] [class*=previous]")
    protected WebElement previousPage;

    @FindBy (css = ".viewmode-value")
    protected List<WebElement> propertiesValuesList;

    @FindBy (css = ".folder-tags.folder-details-panel")
    protected WebElement tagsFeaturePanel;

    @FindBy (css = ".folder-actions.folder-details-panel")
    protected WebElement folderActionsPanel;

    @FindBy (css = ".folder-metadata-header.folder-details-panel")
    protected WebElement filePropertiesdetailsPanel;

    @FindBy (css = ".folder-links.folder-details-panel")
    protected WebElement socialFeaturesPanel;

    @FindBy (css = ".folder-link.folder-closed a")
    protected WebElement folderLinkFromBreadcrumbTrail;

    @FindBy (xpath = ".//*[@id='onActionManageAspects']/a/span")
    protected WebElement manageAspectsButton;

    @FindBy (xpath = "//a[contains(@title,'Edit Properties')]")
    protected WebElement editPropertiesLink;

    @FindBy (xpath = "//div[@class='previewer Image']/img")
    protected WebElement documentImage;

    @FindAll (@FindBy (css = "[id*=comment-container]"))
    private List<WebElement> commentsList;

    @FindBy (css = "span[class='yui-button yui-link-button onDownloadDocumentClick']")
    private WebElement downloadButton;

    @FindBy (xpath = "//span[@class='comment-actions']//a[@title = 'Delete Comment']")
    private WebElement deleteCommentButton;

    @FindBy (xpath = "//span[@class='comment-actions']//a[@title = 'Edit Comment']")
    private WebElement editCommentButton;

    @FindBy (xpath = "//span[@class='button-group']//span[@class = 'yui-button yui-push-button']//button")
    private WebElement deleteButtonOnPrompt;

    @FindBy (id = "prompt_h")
    private WebElement deleteCommentPrompt;

    @FindBy (xpath = "//h2[text()= 'Edit Comment...']")
    private WebElement editCommentBoxTitle;

    @FindBy (xpath = "//button[text()='Save']")
    private WebElement saveButtonEditComment;

    @FindBy (css = "div[class ='textLayer']>div")
    private WebElement contentText;

    @FindBy (css = "div[id$='_default-olderVersions'] div.version-panel-right a.download")
    private WebElement downloadPreviousVersion;

    @FindBy (css = "div[id$='_default-olderVersions'] div.version-panel-right a[class$='_default revert']")
    private WebElement revertButton;

    @FindBy (css = "iframe[id*='comments']")
    private WebElement commentTextArea;

    @FindBy (xpath = ".//span[contains(@class,'locked')]")
    private WebElement lockedMessage;

    @FindBy (css = "#alfresco-revertVersion-instance-ok-button-button")
    private WebElement okOnRevertPopup;

    @FindBy (css = ".message")
    private WebElement contentError;

    @FindBy (css = "span[class$='onAddCommentClick'] button")
    private WebElement addCommentButton;

    @FindBy (css = "iframe[id$='default-add-content_ifr']")
    private WebElement commentsIframe;

    @FindBy (id = "onActionCopyTo")
    private WebElement copyToAction;

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

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/document-details?nodeRef=workspace://SpacesStore/%s", getCurrentFile().getNodeRefWithoutVersion());
    }

    public DocumentDetailsPage assertDocumentDetailsPageIsOpened()
    {
        LOG.info("Assert Document Details page is opened");
        assertTrue(browser.isElementDisplayed(docDetailsPageHeader), "Document details page is opened");
        return this;
    }

    public DocumentDetailsPage assertDocumentTitleEquals(FileModel expectedFileTitle)
    {
        LOG.info("Assert file title equals: {}", expectedFileTitle.getName());
        String fileTitle = headerFileName.getText()
            .substring(BEGIN_INDEX, headerFileName.getText()
                .indexOf(VERSION_NUMBER));

        assertEquals(fileTitle, expectedFileTitle.getName(),
            String.format("Document title not equals %s", expectedFileTitle.getName()));

        return this;
    }

    public boolean isDocumentFavourite()
    {
        browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector("div[class='node-social'] a.favourite-action-favourite.enabled"), 5);
        return browser.isElementDisplayed(By.cssSelector("div[class='node-social'] a.favourite-action-favourite.enabled"));
    }

    public boolean isAddToFavoriteLinkDisplayed()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(socialBar, 5);
        return browser.waitUntilElementsVisible(By.cssSelector("a[class$='favourite-document']")).get(0).getAttribute("title")
                      .equals("Add document to favorites");
    }

    public boolean isAddCommentBlockDisplayed()
    {
        browser.waitUntilElementIsDisplayedWithRetry(addCommentBlock);
        return browser.isElementDisplayed(addCommentBlock);
    }

    public void clickOnLikeUnlike()
    {
        likeUnlikeAction.click();
        browser.waitInSeconds(2);
    }

    public int getLikesNo()
    {
        return Integer.parseInt(likesCount.getText());
    }

    public boolean isCommentBoxOpened()
    {
        try
        {
            return browser.isElementDisplayed(By.xpath("//iframe[contains(@title,'Rich Text Area')]"));
        } catch (Exception ex)
        {
            LOG.info("The comment box is not displayed " + ex.getStackTrace());
            return false;
        }
    }

    public DocumentDetailsPage assertCommentsAreaIsOpened()
    {
        assertTrue(browser.isElementDisplayed(commentsIframe), "Comments area is opened");
        return this;
    }

    public String getFileName()
    {
        browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector("div[class='node-info'] h1"));
        return documentTitle.getText().replace(getFileVersion(), "");
    }

    public boolean isNewVersionAvailable(String version)
    {
        List<WebElement> versionList = browser.waitUntilElementsVisible(latestVersion);
        return browser.findFirstElementWithValue(versionList, version) != null;
    }

    public String getLockedMessage()
    {
        return lockedMessage.getText();
    }

    public String getFileVersion()
    {
        return documentVersion.getText();
    }

    public String getItemModifier()
    {
        return itemModifier.getText();
    }

    public String getModifyDate()
    {
        return modifyDate.getText();
    }

    public void clickOnFavoriteUnfavoriteLink()
    {
        favoriteUnfavoriteAction.click();
        browser.waitInSeconds(2);
    }

    public String getFavoriteText()
    {
        return favoriteUnfavoriteAction.getText();
    }

    public boolean clickOnCommentDocument()
    {
        commentDocument.click();
        return browser.isElementDisplayed(commentForm);
    }

    public DocumentDetailsPage addComment(String comment)
    {
        LOG.info("Add comment: {}", comment);
        browser.waitUntilElementClickable(commentContentIframe, WAIT_5).click();
        browser.findElement(commentContentIframe).sendKeys(comment);
        addCommentButtonSave.click();
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
        okOnRevertPopup.click();
        browser.waitInSeconds(3);
        browser.refresh();
        return (DocumentDetailsPage) this.renderedPage();
    }

    public String getCommentContent()
    {
        return browser.waitUntilElementVisible(commentContent).getText();
    }

    public boolean clickOnSharedLink()
    {
        shareDocument.click();
        return browser.isElementDisplayed(sharePopUp);
    }

    public void clickOnEditGoogleDocs()
    {
        browser.findElement(googleDocsEdit).click();
    }

    public void clickOnDownloadButton()
    {
        downloadDocument.click();
        browser.waitInSeconds(WAIT_10);
    }

    public String getMinimizeMaximizeText()
    {
        return maximizeButton.getText();
    }

    public void clickOnMaximizeMinimizeButton()
    {
        maximizeButton.click();
    }

    public void clickOnZoomInButton()
    {
        zoomInButton.click();
    }

    public void clickOnZoomOutButton()
    {
        zoomOutButton.click();
    }

    public String getScaleValue()
    {
        return scaleButton.getText();
    }

    public void clickOnNextButton()
    {
        nextButton.click();
    }

    public void clickOnPreviousButton()
    {
        previousButton.click();
    }

    public String getCurrentPageNo()
    {
        return pageNumber.getAttribute("value");
    }

    public boolean isZoomInButtonDisplayed()
    {
        return browser.isElementDisplayed(zoomInButton);
    }

    public boolean isZoomOutButtonDisplayed()
    {
        return browser.isElementDisplayed(zoomOutButton);
    }

    public boolean isMaximizetButtonDisplayed()
    {
        return browser.isElementDisplayed(maximizeButton);
    }

    public boolean isNextPageButton()
    {
        return browser.isElementDisplayed(nextButton);
    }

    public boolean isPreviousPageButton()
    {
        return browser.isElementDisplayed(previousButton);
    }

    public boolean clickOnSearchButton()
    {
        searchButton.click();
        return browser.isElementDisplayed(searchDialog);
    }

    public boolean isDocDetailsPageHeaderDisplayed()
    {
        return browser.isElementDisplayed(docDetailsPageHeader);
    }

    public WebElement selectCommentDetailsRow(String comment)
    {
        return browser.findFirstElementWithValue(commentsList, comment);
    }

    public void clickOnEditComment(String comment)
    {
        Actions actions = new Actions(browser);
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
        browser.switchTo().frame(browser.findElement(commentContentIframe));
        WebElement editable = browser.switchTo().activeElement();
        editable.sendKeys(modification);
        browser.switchToDefaultContent();
    }

    public String getPageNoReport()
    {
        return pageReport.getText();
    }

    public void clickOnNextPage()
    {
        nextPage.click();
        browser.waitInSeconds(WAIT_5);
    }

    public void clickOnPreviousPage()
    {
        previousPage.click();
        browser.waitInSeconds(2);
    }

    public int getCommentsListSize()
    {
        return commentsList.size();
    }

    public void clickDocumentActionsOption(String optionName)
    {
        List<WebElement> optionsList = browser.waitUntilElementsVisible(documentActionsOptionsSelector);
        browser.selectOptionFromFilterOptionsList(optionName, optionsList);
    }

    public <T> HtmlPage clickDocumentActionsOption(String optionName, HtmlPage page)
    {
        clickDocumentActionsOption(optionName);
        return page.renderedPage();
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
        List<WebElement> properties = browser.findElements(propertiesList);
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
        List<WebElement> properties = browser.findElements(propertiesList);
        for (WebElement aPropertiesList : properties)
        {
            if (aPropertiesList.getText().equals(propertyText))
                return true;
        }
        return false;
    }

    public String getPropertyValue(String propertyName)
    {
        List<WebElement> properties = browser.findElements(propertiesList);
        for (int i = 0; i < properties.size(); i++)
        {
            if (properties.get(i).getText().replace(":", "").equals(propertyName))
            {
                return propertiesValuesList.get(i).getText();
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
        return browser.isElementDisplayed(By.xpath("//span[@class = 'folder-link']//a"));
    }

    public DocumentLibraryPage clickDocumentsLink()
    {
        browser.findElement(documentsLink).click();
        return (DocumentLibraryPage) documentLibraryPage.renderedPage();
    }

    public String isDocumentThumbnailDisplayed()
    {
        return browser.findElement(By.xpath("//img[@class = 'node-thumbnail']")).getAttribute("src");
    }

    public boolean isLikeButtonPresent()
    {
        return browser.isElementDisplayed(By.xpath("//span[@class= 'item item-separator item-social']//a[text()='Like']"));
    }

    public boolean isDownloadButtonDisplayed()
    {
        return browser.isElementDisplayed(downloadButton);
    }

    public boolean isFilePropertiesDetailsDisplayed()
    {
        return browser.isElementDisplayed(filePropertiesdetailsPanel);
    }

    public boolean isFolderActionsPanelDisplayed()
    {
        return browser.isElementDisplayed(folderActionsPanel);
    }

    public boolean isSocialFeaturesActionsPanelDisplayed()
    {
        return browser.isElementDisplayed(socialFeaturesPanel);
    }

    public boolean isTagsFeaturePanelDisplayed()
    {
        return browser.isElementDisplayed(tagsFeaturePanel);
    }

    public void clickOnFolderFromBreadcrumbTrail()
    {
        folderLinkFromBreadcrumbTrail.click();
    }

    public boolean isDeleteButtonDisplayedForComment(String comment)
    {
        browser.mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        return browser.isElementDisplayed(deleteCommentButton);
    }

    public boolean isEditButtonDisplayedForComment(String comment)
    {
        browser.mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        return browser.isElementDisplayed(editCommentButton);
    }

    public void clickDeleteComment(String comment)
    {
        getBrowser().waitUntilElementVisible(commContent);
        browser.mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        getBrowser().waitUntilElementClickable(deleteCommentButton).click();
    }

    public void clickEditComment(String comment)
    {
        browser.mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        editCommentButton.click();
    }

    public void clickDeleteComment(String comment, String comment2)
    {
        getBrowser().waitUntilElementVisible(commContent);
        try
        {
            browser.mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        }
        catch(Exception e)
        {
            browser.mouseOver(selectCommentDetailsRow(comment2).findElement(commContent));
        }
        getBrowser().waitUntilElementClickable(deleteCommentButton).click();
    }

    public void clickDeleteOnDeleteComment()
    {
        deleteButtonOnPrompt.click();
    }

    public boolean isDeleteCommentPromptDisplayed()
    {
        return browser.isElementDisplayed(deleteCommentPrompt);
    }

    public String getNoCommentsText()
    {
        return noComments.getText();
    }

    public void editComment(String comment)
    {
        tinyMceEditor.setText(comment);
    }

    public boolean isEditCommentDisplayed()
    {
        return browser.isElementDisplayed(editCommentBoxTitle);
    }

    public void clickOnSaveButtonEditComment()
    {
        saveButtonEditComment.click();
    }

    public AspectsForm clickManageAspects()

    {
        manageAspectsButton.click();
        return (AspectsForm) aspectsForm.renderedPage();
    }

    public EditPropertiesPage clickEditProperties()
    {
        editPropertiesLink.click();
        return (EditPropertiesPage) editPropertiesPage.renderedPage();
    }

    public String getContentText()
    {
        return Utils.retry(() -> browser.waitUntilElementVisible(contentText).getText().trim(), WAIT_5);
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
        return browser.isElementDisplayed(By.xpath(restrictable));
    }

    public boolean isAspectDisplayed(String aspectName)
    {
        By aspectXPath = By.xpath(String.format("//div[contains(@class, 'set-bordered-panel') and normalize-space(.)='%s']", aspectName));
        try
        {
            browser.waitUntilElementVisible(aspectXPath);
        } catch (TimeoutException ex)
        {
            return browser.isElementDisplayed(aspectXPath);
        }
        return browser.isElementDisplayed(aspectXPath);
    }

    public boolean isAspectNotDisplayed(String aspectName)
    {
        By aspectXPath = By.xpath(String.format("//div[contains(@class, 'set-bordered-panel') and normalize-space(.)='%s']", aspectName));
        browser.waitUntilElementDeletedFromDom(aspectXPath);
        return browser.isElementDisplayed(aspectXPath);
    }

    public boolean isActionAvailable(String actionName)
    {
        List<WebElement> optionsList = browser.waitUntilElementsVisible(documentActionsOptionsSelector);
        return browser.findFirstElementWithValue(optionsList, actionName) != null;
    }

    public boolean isVersionAvailable(String version)
    {
        List<WebElement> versionList = browser.waitUntilElementsVisible(olderVersion);
        return browser.findFirstElementWithValue(versionList, version) != null;
    }

    public void clickDownloadPreviousVersion()
    {
        browser.waitUntilElementClickable(downloadPreviousVersion).click();
        acceptAlertIfDisplayed();
    }

    public boolean isRevertButtonAvailable()
    {
        return browser.isElementDisplayed(revertButton);
    }

    public void clickRevertButton()
    {
        browser.waitUntilElementClickable(revertButton).click();
        browser.waitUntilElementVisible(okOnRevertPopup);
    }

    public void addCommentToItem(String comment)
    {
        browser.switchTo().frame(browser.waitUntilElementVisible(commentTextArea));
        WebElement commentBody = browser.findElement(By.id("tinymce"));
        Utils.clearAndType(commentBody, comment);
        browser.switchTo().defaultContent();
        browser.waitUntilElementClickable(addCommentButtonSave).click();
    }

    public void clickAddCommentButton()
    {
        getBrowser().waitUntilElementClickable(addCommentButton).click();
        getBrowser().waitUntilElementVisible(By.cssSelector("form[id$='_default-add-form']"), 5L);
    }

    public void assertUploadedDocumentImageIsDisplayed()
    {
        LOG.info("Assert document image is displayed");
        assertTrue(browser.isElementDisplayed(documentImage), "Document image is not displayed");
    }

    public CopyMoveUnzipToDialog clickCopyTo()
    {
        LOG.info("Click Copy To...");
        browser.waitUntilElementClickable(copyToAction).click();
        return (CopyMoveUnzipToDialog) copyMoveDialog.renderedPage();
    }
}