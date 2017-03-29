package org.alfresco.po.share.alfrescoContent.document;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@PageObject
public class DocumentDetailsPage extends DocumentCommon<DocumentDetailsPage>
{
    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    EditPropertiesPage editPropertiesPage;

    @Autowired
    AspectsForm aspectsForm;

    @FindBy(css = ".filename")
    protected WebElement fileListLocator;

    @FindBy(css = ".folder-actions.folder-details-panel a[title='Manage Permissions']")
    protected WebElement managePermissionsLink;

    @FindAll(@FindBy(css = ".filename [href*=document-details]"))
    private List<WebElement> filesList;

    @Autowired
    TinyMceEditor tinyMceEditor;

    @FindBy(css = "div[class='node-info'] h1")
    protected WebElement documentTitle;

    @RenderWebElement
    @FindBy(css = ".node-header")
    protected WebElement docDetailsPageHeader;

    @FindBy(linkText = "Download")
    protected WebElement downloadDocument;

    @FindBy(css = "div[class='node-social']")
    protected WebElement socialBar;

    @FindBy(css = "[class*=like-action]")
    protected WebElement likeUnlikeAction;

    @FindBy(css = "[class=likes-count]")
    protected WebElement likesCount;

    @FindBy(css = ".item-modifier a")
    protected WebElement itemModifier;

    @FindBy(css = ".item-modifier span")
    protected WebElement modifyDate;

    @FindBy(css = "div[class='node-info'] h1 span")
    protected WebElement documentVersion;

    @FindBy(css = "[class*=favourite-action]")
    protected WebElement favoriteUnfavoriteAction;

    @FindBy(css = "[name*='commentNode']")
    protected WebElement commentDocument;

    @FindBy(css = "[class=comment-form]")
    protected WebElement commentForm;

    @FindBy(css = "[id*='default-add-submit-button']")
    protected WebElement addCommentButton;

    @FindBy(css = "[class=comment-content]")
    protected WebElement commentContent;

    @FindBy(css = "[class*=quickshare-action] [class=bd]")
    protected WebElement sharePopUp;

    @FindBy(css = "[title*='Share document']")
    protected WebElement shareDocument;

    @FindBy(css = "[id*=default-fullpage-button]")
    protected WebElement maximizeButton;

    @FindBy(css = "[id*=zoomIn-button]")
    protected WebElement zoomInButton;

    @FindBy(css = "[id*=zoomOut-button]")
    protected WebElement zoomOutButton;

    @FindBy(css = "[id*=default-scaleSelectBtn-button]")
    protected WebElement scaleButton;

    @FindBy(css = "[id*=default-next-button]")
    protected WebElement nextButton;

    @FindBy(css = "[id*=default-previous-button]")
    protected WebElement previousButton;

    @FindBy(css = "[id*=default-pageNumber]")
    protected WebElement pageNumber;

    @FindBy(css = "[id*=searchBarToggle-button]")
    protected WebElement searchButton;

    @FindBy(css = "[class*=searchDialog]")
    protected WebElement searchDialog;

    @FindAll(@FindBy(css = "[id*=comment-container]"))
    private List<WebElement> commentsList;

    @FindBy(css = "[id*=default-paginator-top] [id*=page-report]")
    protected WebElement pageReport;

    @FindBy(css = "[id*=default-paginator-top] [class*=next]")
    protected WebElement nextPage;

    @FindBy(css = "[id*=default-paginator-top] [class*=previous]")
    protected WebElement previousPage;

    @RenderWebElement
    @FindBy(css = ".viewmode-label")
    protected List<WebElement> propertiesList;

    @FindBy(css = ".viewmode-value")
    protected List<WebElement> propertiesValuesList;

    @FindBy(css = "span[class='yui-button yui-link-button onDownloadDocumentClick']")
    private WebElement downloadButton;

    @FindBy(css = ".folder-tags.folder-details-panel")
    protected WebElement tagsFeaturePanel;

    @FindBy(css = ".folder-actions.folder-details-panel")
    protected WebElement folderActionsPanel;

    @FindBy(css = ".folder-metadata-header.folder-details-panel")
    protected WebElement filePropertiesdetailsPanel;

    @FindBy(css = ".folder-links.folder-details-panel")
    protected WebElement socialFeaturesPanel;

    @FindBy(css = ".folder-link.folder-closed a")
    protected WebElement folderLinkFromBreadcrumbTrail;

    @FindBy(xpath = "//span[@class='comment-actions']//a[@title = 'Delete Comment']")
    private WebElement deleteCommentButton;

    @FindBy(xpath = "//span[@class='comment-actions']//a[@title = 'Edit Comment']")
    private WebElement editCommentButton;

    @FindBy(xpath = "//span[@class='button-group']//span[@class = 'yui-button yui-push-button']//button")
    private WebElement deleteButtonOnPrompt;

    @FindBy(id = "prompt_h")
    private WebElement deleteCommentPrompt;

    @FindBy(css = "div[id*='_default-comments-list'] td[class ='yui-dt-empty']")
    public WebElement noComments;

    @FindBy(xpath = "//h2[text()= 'Edit Comment...']")
    private WebElement editCommentBoxTitle;

    @FindBy(xpath = "//button[text()='Save']")
    private WebElement saveButtonEditComment;

    @FindBy(css = "div[class ='textLayer']")
    private WebElement contentText;

    @FindBy(xpath = ".//*[@id='onActionManageAspects']/a/span")
    protected WebElement manageAspectsButton;

    @FindBy(xpath = "//a[contains(@title,'Edit Properties')]")
    protected WebElement editPropertiesLink;

    @FindBy(css = "div[id$='_default-olderVersions'] div.version-panel-right a.download")
    private WebElement downloadPreviousVersion;

    @FindBy(css = "div[id$='_default-olderVersions'] div.version-panel-right a[class$='_default revert']")
    private WebElement revertButton;

    @FindBy(xpath = "//iframe[contains(@title,'Rich Text Area')]")
    private WebElement CommentTextArea;

    @FindBy(xpath = ".//span[contains(@class,'locked')]")
    private WebElement lockedMessage;

    @FindBy(css = "#alfresco-revertVersion-instance-ok-button-button")
    private WebElement okOnRevertPopup;

    @FindBy(css = ".message")
    private WebElement contentError;

    private By fileLocation = By.xpath("//span[@class= 'folder-link folder-open']//a");

    private By documentsLink = By.xpath("//span[@class = 'folder-link']//a");
    private By googleDocsEdit = By.xpath("//span[contains(text(), 'Edit in Google Docs™')]");
    private By commentButton = By.xpath("//span[@class ='item item-separator item-social']//a[@title = 'Comment on this document']");

    protected By favouriteIcon = By.cssSelector("a[class$='favourite-action-favourite']");
    protected By addToFavouriteIcon = By.cssSelector("a[class$='favourite-document']");
    protected By addCommentBlock = By.cssSelector("div[id*='default-add-comment']");
    private By commentContentIframe = By.xpath("//iframe[contains(@title,'Rich Text Area')]");
    private By editComment = By.cssSelector("[class*=edit-comment]");
    private By commContent = By.cssSelector("[class=comment-content]");
    private By documentActionsOptionsSelector = By.cssSelector(".action-set div a span");
    private By olderVersion = By.cssSelector("div[id$='_default-olderVersions'] span.document-version");
    private By latestVersion = By.cssSelector("div[id$='_default-latestVersion'] span.document-version");

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/document-details", getCurrentSiteName());
    }

    public boolean isDocumentFavourite()
    {
        browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector("div[class='node-social'] a.favourite-action-favourite.enabled"), 5);
        return browser.isElementDisplayed(By.cssSelector("div[class='node-social'] a.favourite-action-favourite.enabled"));
    }

    public boolean isAddToFavoriteLinkDisplayed()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(socialBar, 5);
        return browser.waitUntilElementsVisible(By.cssSelector("a[class$='favourite-document']")).get(0).getAttribute("title").equals("Add document to favorites");
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
        return browser.isElementDisplayed(By.xpath("//iframe[contains(@title,'Rich Text Area')]"));
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

    /**
     * Method to get the locked message for document
     */
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
        return commentForm.isDisplayed();
    }

    /**
     * Method used to enter and add comment
     */
    public void addComment(String comment)
    {
        browser.waitUntilElementClickable(commentContentIframe, 5L);
        browser.findElement(commentContentIframe).sendKeys(comment);
        addCommentButton.click();
        browser.waitInSeconds(4);
    }

    /**
     * Method used to click 'Ok' button on 'Revert to previous version' pop-up
     */
    public void clickOkOnRevertPopup()
    {
        okOnRevertPopup.click();
    }

    public String getCommentContent()
    {
        return commentContent.getText();
    }

    public boolean clickOnSharedLink()
    {
        shareDocument.click();
        return sharePopUp.isDisplayed();
    }

    public void clickOnEditGoogleDocs()
    {
        browser.findElement(googleDocsEdit).click();
    }

    public void clickOnDownloadButton()
    {
        downloadDocument.click();
        browser.waitInSeconds(10);
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
        return searchDialog.isDisplayed();
    }

    public boolean isDocDetailsPageHeaderDisplayed()
    {
        return browser.isElementDisplayed(docDetailsPageHeader);
    }

    public WebElement selectCommentDetailsRow(String comment)
    {
        return browser.findFirstElementWithValue(commentsList, comment);
    }

    /**
     * Method used to edit comment
     */
    public void clickOnEditComment(String comment)
    {
        // browser.refresh();
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
        browser.waitInSeconds(2);
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

    /**
     * Click any option from 'Document Actions' section
     *
     * @param optionName
     */
    public void clickDocumentActionsOption(String optionName)
    {
        List<WebElement> optionsList = browser.waitUntilElementsVisible(documentActionsOptionsSelector);
        browser.selectOptionFromFilterOptionsList(optionName, optionsList);
    }

    /**
     * Verify displayed elements from 'Properties' section
     *
     * @param expectedPropertiesList
     *            list of expected properties to be checked
     * @return
     */
    public boolean arePropertiesDisplayed(String... expectedPropertiesList)
    {
        List<String> propertiesTextList = new ArrayList<>();
        for(WebElement property : propertiesList)
            propertiesTextList.add(property.getText().substring(0, property.getText().indexOf(":")));
        return DataUtil.areListsEquals(propertiesTextList, expectedPropertiesList);
    }

    /**
     * Verify a list of elements isn't displayed in 'Properties' section
     *
     * @param propertiesNotDisplayedList
     *            list of properties to be checked that aren't displayed
     * @return first property from given list that is displayed
     */
    public String checkPropertiesAreNotDisplayed(ArrayList<String> propertiesNotDisplayedList)
    {
        for (int i = 0; i < propertiesList.size(); i++)
        {
            String property = propertiesList.get(i).getText();
            for (String aPropertiesNotDisplayedList : propertiesNotDisplayedList)
            {
                if (property.equals(aPropertiesNotDisplayedList))
                    return propertiesNotDisplayedList.get(i);
            }
        }
        return "Given list isn't displayed";
    }

    /**
     * Verify presence of given property in 'Properties' section
     *
     * @param propertyText
     *            to be checked
     * @return true if property is displayed
     */
    public boolean isPropertyDisplayed(String propertyText)
    {
        for (WebElement aPropertiesList : propertiesList)
        {
            if (aPropertiesList.getText().equals(propertyText))
                return true;
        }
        return false;
    }

    /**
     * @param propertyName
     *            to be found in 'Properties' section
     * @return value of the given propertyName
     */
    public String getPropertyValue(String propertyName)
    {
        for (int i = 0; i < propertiesList.size(); i++)
        {
            if (propertiesList.get(i).getText().equals(propertyName))
                return propertiesValuesList.get(i).getText();
        }
        return propertyName + " isn't displayed in Properties section!";
    }

    /**
     * Method to get the file location (folder name for the folder containing
     * the file under test)
     */

    public String getFileLocation()
    {
        return browser.findElement(fileLocation).getText();
    }

    /**
     * Method to check if Documents link is present to return to Document
     * Library
     * 
     * @return
     */
    public boolean isDocumentsLinkPresent()
    {
        return browser.isElementDisplayed(By.xpath("//span[@class = 'folder-link']//a"));
    }

    /**
     * Method to click on the Documents link
     */
    public void clickDocumentsLink()
    {
        browser.findElement(documentsLink).click();
    }

    /**
     * Method to check if the thumbnail indicating the document type is present
     * 
     * @return
     */
    public String isDocumentThumbnailDisplayed()
    {
        return browser.findElement(By.xpath("//img[@class = 'node-thumbnail']")).getAttribute("src");
    }

    /**
     * Method to check if the Like button is present
     * 
     * @return
     */

    public boolean isLikeButtonPresent()
    {
        return browser.isElementDisplayed(By.xpath("//span[@class= 'item item-separator item-social']//a[text()='Like']"));
    }

    /**
     * Method to check if the comment button is displayed
     * 
     * @return
     */
    public boolean isCommentButtonDisplayed()
    {
        return browser.isElementDisplayed(commentButton);
    }

    /**
     * Method to check if the Download button is available in the top right side
     * of the page
     */

    public boolean isDownloadButtonDisplayed()
    {
        return browser.isElementDisplayed(downloadButton);
    }

    /**
     * Method to click on the Download button in the top right side of the page
     */

    public void clickTheDownloadButtonTopRight()
    {
        downloadButton.click();
    }

    public boolean isFilePropertiesDetailsDisplayed()
    {
        return filePropertiesdetailsPanel.isDisplayed();
    }

    public boolean isFolderActionsPanelDisplayed()
    {
        return folderActionsPanel.isDisplayed();
    }

    public boolean isSocialFeaturesActionsPanelDisplayed()
    {
        return socialFeaturesPanel.isDisplayed();
    }

    public boolean isTagsFeaturePanelDisplayed()
    {
        return tagsFeaturePanel.isDisplayed();
    }

    public void clickOnFolderFromBreadrcumbTrail()
    {
        folderLinkFromBreadcrumbTrail.click();
    }

    public boolean isDeleteButtonDisplayedForComment(String comment)
    {
        try
        {
            browser.mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
            return deleteCommentButton.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            // continue
        }
        return false;
    }

    public boolean isEditButtonDisplayedForComment(String comment)
    {
        try
        {
            browser.mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
            return editCommentButton.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            // continue
        }
        return false;
    }

    public void clickDeleteComment(String comment)
    {
        browser.mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        deleteCommentButton.click();
    }

    public void clickEditComment(String comment)
    {
        browser.mouseOver(selectCommentDetailsRow(comment).findElement(commContent));
        editCommentButton.click();
    }

    public void clickDeleteOnDeleteComment()
    {
        deleteButtonOnPrompt.click();
    }

    public boolean isDeleteCommentPromptDisplayed()
    {
        return deleteCommentPrompt.isDisplayed();
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
        return editCommentBoxTitle.isDisplayed();
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

    /**
     * Verify if Document Library is opened
     * 
     * @param siteName
     *            String - the name of the site
     * @return true if opened
     */
    public boolean isDocumentLibraryOpened(String siteName)
    {
        return siteDashboardPage.getCurrentUrl().contains(siteName + "/documentlibrary");
    }

    /**
     * This method is used to get the list of files name
     *
     * @return foldersName
     */
    public List<String> getDocumentLibraryFilesList()
    {
        List<String> filesName = new ArrayList<>();
        for (WebElement file : filesList)
        {
            filesName.add(file.getText());
        }
        return filesName;
    }

    /**
     * Method to get the content text of the file previewed
     */

    public String getContentText()
    {
        String content;

        try
        {
            browser.waitUntilElementVisible(contentText);
            content = contentText.getText();
        }
        catch (TimeoutException e1)
        {
            LOG.info(e1.toString());
            try
            {
                content = contentError.getText();
            }
            catch (NoSuchElementException e2)
            {
                LOG.info(e1.toString());
                content = "Content not loaded";
            }
        }
        return content;
    }

    /**
     * Method to check if the value for 'Offline Expires After (hours)' is
     * updated when 'Restrictable' aspect is applied
     */

    public boolean isRestrictableValueUpdated(String hours)
    {

        String restrictable = "//span[contains(@class,'viewmode-value')] [contains(text(),'" + hours + "')]";

        WebElement restrictableValue = browser.findElement(By.xpath(restrictable));

        return restrictableValue.isDisplayed();

    }

    public boolean isRestrictableAspectDisplayed() throws Exception
    {
        try
        {
            browser.findElement(By.xpath("//div[contains(@class, 'set-bordered-panel') and normalize-space(.)='Restrictable']"));
            return true;

        }
        catch (NoSuchElementException e)
        {

            return false;
        }

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
        browser.waitUntilElementClickable(downloadPreviousVersion, 6);
        downloadPreviousVersion.click();
    }

    public boolean isRevertButtonAvailable()
    {
        return browser.isElementDisplayed(revertButton);
    }

    /**
     * Method used to click on revert to previous version button
     */
    public void clickRevertButton()
    {
        revertButton.click();
    }

    /**
     * Method used to enter and add comment to an item - with retry
     */
    public void addCommentToItem(String comment)
    {
        int counter = 1;
        int retryRefreshCount = 5;

        while (counter <= retryRefreshCount)
        {
            try
            {

                if (CommentTextArea.isDisplayed())
                {
                    CommentTextArea.sendKeys(comment);
                    browser.waitInSeconds(2);
                    break;
                }

            }
            catch (NoSuchElementException e)
            {
                LOG.info("Wait for element after refresh: " + counter);
                browser.refresh();
                counter++;
            }
        }
        addCommentButton.click();
        browser.waitInSeconds(2);
    }

}