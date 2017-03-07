package org.alfresco.po.share.alfrescoContent.document;

import org.alfresco.po.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject

public class PreviewFileActionsSection extends DocumentCommon<PreviewFileActionsSection>
{

    @FindBy(css = "div[id*= '_document-actions']")
    private WebElement documentActionsBlock;

    @FindBy(css = "div[class= 'document-view-content alf-action-group-end']  a")
    private WebElement viewInBrowser;

    @FindBy(id = "onGoogledocsActionEdit")
    private WebElement editInGoogleDocs;

    @FindBy(id = "onActionEditOnlineAos")
    private WebElement editInMicrosoftOffice;

    @FindBy(id = "onActionEditOffline")
    private WebElement editOffline;

    @FindBy(id = "onActionUploadNewVersion")
    private WebElement uploadNewVersion;

    @FindBy(css = "div[class ='document-edit-metadata alf-action-group-end']")
    private WebElement editProperties;

    @FindBy(id = "onActionMoveTo")
    private WebElement moveTo;

    @FindBy(id = "onActionCopyTo")
    private WebElement copyTo;

    @FindBy(id = "onActionDelete")
    private WebElement deleteDocument;

    @FindBy(id = "onActionAssignWorkflow")
    private WebElement startWorkflow;

    @FindBy(css = "div[class ='document-manage-granular-permissions']")
    private WebElement managePermissions;

    @FindBy(id = "onActionTakeOwnership")
    private WebElement becomeOwner;

    @FindBy(id = "onActionManageAspects")
    private WebElement manageAspects;

    @FindBy(id = "onActionChangeType")
    private WebElement changeType;

    @FindBy(css = "div[id*='document-tags']")
    private WebElement tagsBlock;

    @FindBy(css = "div[id*='document-links']")
    private WebElement shareBlock;

    @FindBy(css = "input[id*='document-links']")
    private WebElement shareLink;

    @FindBy(css = "div[class = 'document-metadata-header document-details-panel']")
    private WebElement propertiesBlock;

    @FindBy(css = "div[id*='_document-workflows_']")
    private WebElement workflowsBlock;

    @FindBy(css = "div[id*='_document-versions_']")
    private WebElement versionHistory;

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isDocumentActionsBlockDisplayed()
    {
        return documentActionsBlock.isDisplayed();
    }

    public boolean isViewInBrowserDisplayed()
    {
        return viewInBrowser.isDisplayed();
    }

    public boolean isEditInGoogleDocsDisplayed()
    {
        return editInGoogleDocs.isDisplayed();
    }

    public boolean isEditInMicrosoftOfficeDisplayed()
    {
        return editInMicrosoftOffice.isDisplayed();
    }

    public boolean isEditOfflineDisplayed()
    {
        return editOffline.isDisplayed();
    }

    public boolean isUploadNewVersionDisplayed()
    {
        return uploadNewVersion.isDisplayed();
    }

    public boolean isEditPropertiesDisplayed()
    {
        return editProperties.isDisplayed();
    }

    public boolean isMoveToDisplayed()
    {
        return moveTo.isDisplayed();
    }

    public boolean isCopyToDisplayed()
    {
        return copyTo.isDisplayed();
    }

    public boolean isDeleteDocumentDisplayed()
    {
        return deleteDocument.isDisplayed();
    }

    public boolean isStartWorkflowDisplayed()
    {
        return startWorkflow.isDisplayed();
    }

    public boolean isManagePermissionsDisplayed()
    {
        return managePermissions.isDisplayed();
    }

    public boolean isBecomeOwnerDisplayed()
    {
        return becomeOwner.isDisplayed();
    }

    public boolean isManageAspectsDisplayed()
    {
        return manageAspects.isDisplayed();
    }

    public boolean isChangeTypeDisplayed()
    {
        return changeType.isDisplayed();
    }

    public boolean isTagsBlockDisplayed()
    {
        return tagsBlock.isDisplayed();
    }

    public boolean isShareBlockDisplayed()
    {
        return shareBlock.isDisplayed();
    }

    public boolean isShareLinkDisplayed()
    {
        return shareLink.isDisplayed();
    }

    public boolean isPropertiesBlockDisplayed()
    {
        return propertiesBlock.isDisplayed();
    }

    public boolean isWorkflowsBlockDisplayed()
    {
        return workflowsBlock.isDisplayed();
    }

    public boolean isVersionHistoryBlockDisplayed()

    {
        return versionHistory.isDisplayed();
    }
}