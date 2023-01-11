package org.alfresco.po.share.alfrescoContent.document;

import lombok.extern.slf4j.Slf4j;

import static org.testng.Assert.*;

import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.model.FileModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class FileActionsPage extends SharePage2<FileActionsPage>
{
    private FileModel currentFile;

    private final By documentActionBlock = By.cssSelector("div[class='document-actions document-details-panel']");
    private final By viewInBrowserLink = By.cssSelector("a[title='View In Browser']");
    private final By editInGoogleDocsLink = By.cssSelector("a[title='Edit in Google Docs™']");
    private final By editInMicrosoftOfficeLink = By.cssSelector("a[title='Edit in Microsoft Office™']");
    private final By uploadNewVersionLink = By.cssSelector("a[title='Upload New Version']");
    private final By editPropertiesLink = By.cssSelector("a[title='Edit Properties']");
    private final By moveToLink = By.cssSelector("a[title='Move to...']");
    private final By copyToLink = By.cssSelector("a[title='Copy to...']");
    private final By deleteDocumentLink = By.cssSelector("a[title='Delete Document']");
    private final By startWorkflowLink = By.cssSelector("a[title='Start Workflow']");
    private final By managePermissionsLink = By.cssSelector("a[title='Manage Permissions']");
    private final By becomeOwnerLink = By.cssSelector("a[title='Become Owner']");
    private final By manageAspectsLink = By.cssSelector("a[title='Manage Aspects']");
    private final By changeTypeLink = By.cssSelector("a[title='Change Type']");
    private final By tagBlock = By.cssSelector(".document-tags.document-details-panel");
    private final By shareBlock = By.cssSelector(".document-links.document-details-panel");
    private final By shareLinkEditField = By.cssSelector(".link-info input");
    private final By propertiesBlock = By.cssSelector(".document-metadata-header.document-details-panel");
    private final By workflowsBlock = By.cssSelector(".document-workflows.document-details-panel");
    private final By versionHistoryBlock = By.cssSelector(".document-versions.document-details-panel");

    public FileActionsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public boolean isDocumentActionsBlockDisplayed()
    {
        return isElementDisplayed(documentActionBlock);
    }

    public boolean isViewInBrowserDisplayed()
    {
        return isElementDisplayed(viewInBrowserLink);
    }

    public boolean isEditInGoogleDocsDisplayed()
    {
        return isElementDisplayed(editInGoogleDocsLink);
    }

    public boolean isEditInMicrosoftOfficeDisplayed()
    {
        return isElementDisplayed(editInMicrosoftOfficeLink);
    }

    public boolean isUploadNewVersionDisplayed()
    {
        return isElementDisplayed(uploadNewVersionLink);
    }

    public boolean isEditPropertiesDisplayed()
    {
        return isElementDisplayed(editPropertiesLink);
    }

    public boolean isMoveToDisplayed()
    {
        return isElementDisplayed(moveToLink);
    }

    public boolean isCopyToDisplayed()
    {
        return isElementDisplayed(copyToLink);
    }

    public boolean isDeleteDocumentDisplayed()
    {
        return isElementDisplayed(deleteDocumentLink);
    }

    public boolean isStartWorkflowDisplayed()
    {
        return isElementDisplayed(startWorkflowLink);
    }

    public boolean isManagePermissionsDisplayed()
    {
        return isElementDisplayed(managePermissionsLink);
    }

    public boolean isBecomeOwnerDisplayed()
    {
        return isElementDisplayed(becomeOwnerLink);
    }

    public boolean isManageAspectsDisplayed()
    {
        return isElementDisplayed(manageAspectsLink);
    }

    public boolean isChangeTypeDisplayed()
    {
        return isElementDisplayed(changeTypeLink);
    }

    public boolean isTagsBlockDisplayed()
    {
        return isElementDisplayed(tagBlock);
    }

    public boolean isShareBlockDisplayed()
    {
        return isElementDisplayed(shareBlock);
    }

    public String getShareLink()
    {
        WebElement element = webDriver.get().findElement(shareLinkEditField);
        return element.getAttribute("value");
    }

    public boolean isPropertiesBlockDisplayed()
    {
        return isElementDisplayed(propertiesBlock);
    }

    public boolean isWorkflowsBlockDisplayed()
    {
        return isElementDisplayed(workflowsBlock);
    }

    public boolean isVersionHistoryBlockDisplayed()
    {
        return isElementDisplayed(versionHistoryBlock);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/document-details?nodeRef=workspace://SpacesStore/%s", getCurrentFile().getNodeRefWithoutVersion());
    }

    public FileModel getCurrentFile()
    {
        return currentFile;
    }

    public FileActionsPage assertIsDocumentActionsBlockDisplayed()
    {
        log.info("Verify that the Document Action Block displayed");
        assertTrue(isDocumentActionsBlockDisplayed(), "Document Actions block is not displayed");
        return this;
    }

    public FileActionsPage assertIsViewInBrowserDisplayed()
    {
        log.info("Verify that the View in browser option displayed");
        assertTrue(isViewInBrowserDisplayed(), "View in Browser option is not displayed");
        return this;
    }

    public FileActionsPage assertIsEditInGoogleDocsDisplayed()
    {
        log.info("Verify that the Edit In Google Docs option displayed");
        assertTrue(isEditInGoogleDocsDisplayed(), "Edit in Google Docs option is not displayed");
        return this;
    }

    public FileActionsPage assertIsEditInMicrosoftOfficeDisplayed()
    {
        log.info("Verify that the Edit In Microsoft Office option displayed");
        assertTrue(isEditInMicrosoftOfficeDisplayed(), "Edit in Microsoft Office option is not displayed");
        return this;
    }

    public FileActionsPage assertIsUploadNewVersionDisplayed()
    {
        log.info("Verify that the Upload New Version option displayed");
        assertTrue(isUploadNewVersionDisplayed(), "Upload New Version option is not displayed");
        return this;
    }

    public FileActionsPage assertIsEditPropertiesDisplayed()
    {
        log.info("Verify that the Edit Properties option displayed");
        assertTrue(isEditPropertiesDisplayed(), "Edit Properties option is not displayed");
        return this;
    }

    public FileActionsPage assertIsMoveToOptionDisplayed()
    {
        log.info("Verify that the Move to... option displayed");
        assertTrue(isMoveToDisplayed(), "Move to... option is not displayed");
        return this;
    }

    public FileActionsPage assertIsCopyToOptionDisplayed()
    {
        log.info("Verify that the Copy to... option displayed");
        assertTrue(isCopyToDisplayed(), "Copy to... option is not displayed");
        return this;
    }

    public FileActionsPage assertIsDeleteDocumentOptionDisplayed()
    {
        log.info("Verify that the Delete Document... option displayed");
        assertTrue(isDeleteDocumentDisplayed(), "Delete Document... option is not displayed");
        return this;
    }

    public FileActionsPage assertIsStartWorkflowDisplayed()
    {
        log.info("Verify that the Start Workflow... option displayed");
        assertTrue(isStartWorkflowDisplayed(), "Start Workflow... option is not displayed");
        return this;
    }

    public FileActionsPage assertIsManagePermissionDisplayed()
    {
        log.info("Verify that the Manage Permission... option displayed");
        assertTrue(isManagePermissionsDisplayed(), "Manage Permission... option is not displayed");
        return this;
    }

    public FileActionsPage assertIsBecomeOwnerDisplayed()
    {
        log.info("Verify that the Become Owner... option displayed");
        assertTrue(isBecomeOwnerDisplayed(), "Become Owner... option is not displayed");
        return this;
    }

    public FileActionsPage assertIsManageAspectsDisplayed()
    {
        log.info("Verify that the Manage Aspects... option displayed");
        assertTrue(isManageAspectsDisplayed(), "Manage Aspects... option is not displayed");
        return this;
    }

    public FileActionsPage assertIsChangeTypeOptionDisplayed()
    {
        log.info("Verify that the Change Type... option displayed");
        assertTrue(isChangeTypeDisplayed(), "Change Type... option is not displayed");
        return this;
    }

    public FileActionsPage assertIsTagBlockDisplayed()
    {
        log.info("Verify that the Tag Block displayed");
        assertTrue(isTagsBlockDisplayed(), "Tag Block is not displayed");
        return this;
    }

    public FileActionsPage assertIsShareBlockDisplayed()
    {
        log.info("Verify that the Share Block displayed");
        assertTrue(isShareBlockDisplayed(), "Share Block is not displayed");
        return this;
    }

    public FileActionsPage assertIsPropertiesBlockDisplayed()
    {
        log.info("Verify that the Properties Block displayed");
        assertTrue(isPropertiesBlockDisplayed(), "Properties Block is not displayed");
        return this;
    }

    public FileActionsPage assertIsWorkFlowsBlockDisplayed()
    {
        log.info("Verify that the Work Flows Block displayed");
        assertTrue(isWorkflowsBlockDisplayed(), "Work Flows Block is not displayed");
        return this;
    }

    public FileActionsPage assertIsVersionHistoryBlockDisplayed()
    {
        log.info("Verify that the Version History Block displayed");
        assertTrue(isVersionHistoryBlockDisplayed(), "Version History Block is not displayed");
        return this;
    }
    public FileActionsPage assertVerifyShareLink(String linkText)
    {
        log.info("Verify the share link text");
        assertEquals(linkText, getShareLink(), String.format("Share link text not mathced with %s ", getShareLink()));
        return this;
    }
}