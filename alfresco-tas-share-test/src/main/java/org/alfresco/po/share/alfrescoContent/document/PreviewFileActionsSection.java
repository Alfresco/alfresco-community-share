package org.alfresco.po.share.alfrescoContent.document;

import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class PreviewFileActionsSection extends DocumentCommon<PreviewFileActionsSection>
{

    @FindBy (css = "div[id*= '_document-actions']")
    private WebElement documentActionsBlock;

    @FindBy (css = "div[class= 'document-view-content alf-action-group-end']  a")
    private WebElement viewInBrowser;

    @FindBy (id = "onGoogledocsActionEdit")
    private WebElement editInGoogleDocs;

    @FindBy (id = "onActionEditOnlineAos")
    private WebElement editInMicrosoftOffice;

    @FindBy (id = "onActionEditOffline")
    private WebElement editOffline;

    @FindBy (id = "onActionUploadNewVersion")
    private WebElement uploadNewVersion;

    @FindBy (css = "div[class ='document-edit-metadata alf-action-group-end']")
    private WebElement editProperties;

    @FindBy (id = "onActionMoveTo")
    private WebElement moveTo;

    @FindBy (id = "onActionCopyTo")
    private WebElement copyTo;

    @FindBy (id = "onActionDelete")
    private WebElement deleteDocument;

    @FindBy (id = "onActionAssignWorkflow")
    private WebElement startWorkflow;

    @FindBy (css = "div[class ='document-manage-granular-permissions']")
    private WebElement managePermissions;

    @FindBy (id = "onActionTakeOwnership")
    private WebElement becomeOwner;

    @FindBy (id = "onActionManageAspects")
    private WebElement manageAspects;

    @FindBy (id = "onActionChangeType")
    private WebElement changeType;

    @FindBy (css = "div[id*='document-tags']")
    private WebElement tagsBlock;

    @FindBy (css = "div[id*='document-links']")
    private WebElement shareBlock;

    @FindBy (css = "input[id*='document-links']")
    private WebElement shareLink;

    @FindBy (css = "div[class = 'document-metadata-header document-details-panel']")
    private WebElement propertiesBlock;

    @FindBy (css = "div[id*='_document-workflows_']")
    private WebElement workflowsBlock;

    @FindBy (css = "div[id*='_document-versions_']")
    private WebElement versionHistory;

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isDocumentActionsBlockDisplayed()
    {
        return browser.isElementDisplayed(documentActionsBlock);
    }

    public boolean isViewInBrowserDisplayed()
    {
        return browser.isElementDisplayed(viewInBrowser);
    }

    public boolean isEditInGoogleDocsDisplayed()
    {
        return browser.isElementDisplayed(editInGoogleDocs);
    }

    public boolean isEditInMicrosoftOfficeDisplayed()
    {
        return browser.isElementDisplayed(editInMicrosoftOffice);
    }

    public boolean isEditOfflineDisplayed()
    {
        return browser.isElementDisplayed(editOffline);
    }

    public boolean isUploadNewVersionDisplayed()
    {
        return browser.isElementDisplayed(uploadNewVersion);
    }

    public boolean isEditPropertiesDisplayed()
    {
        return browser.isElementDisplayed(editProperties);
    }

    public boolean isMoveToDisplayed()
    {
        return browser.isElementDisplayed(moveTo);
    }

    public boolean isCopyToDisplayed()
    {
        return browser.isElementDisplayed(copyTo);
    }

    public boolean isDeleteDocumentDisplayed()
    {
        return browser.isElementDisplayed(deleteDocument);
    }

    public boolean isStartWorkflowDisplayed()
    {
        return browser.isElementDisplayed(startWorkflow);
    }

    public boolean isManagePermissionsDisplayed()
    {
        return browser.isElementDisplayed(managePermissions);
    }

    public boolean isBecomeOwnerDisplayed()
    {
        return browser.isElementDisplayed(becomeOwner);
    }

    public boolean isManageAspectsDisplayed()
    {
        return browser.isElementDisplayed(manageAspects);
    }

    public boolean isChangeTypeDisplayed()
    {
        return browser.isElementDisplayed(changeType);
    }

    public boolean isTagsBlockDisplayed()
    {
        return browser.isElementDisplayed(tagsBlock);
    }

    public boolean isShareBlockDisplayed()
    {
        return browser.isElementDisplayed(shareBlock);
    }

    public boolean isShareLinkDisplayed()
    {
        return browser.isElementDisplayed(shareLink);
    }

    public boolean isPropertiesBlockDisplayed()
    {
        return browser.isElementDisplayed(propertiesBlock);
    }

    public boolean isWorkflowsBlockDisplayed()
    {
        return browser.isElementDisplayed(workflowsBlock);
    }

    public boolean isVersionHistoryBlockDisplayed()

    {
        return browser.isElementDisplayed(versionHistory);
    }
}