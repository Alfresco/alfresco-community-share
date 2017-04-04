package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders.editingFiles;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EditingFilesInAlfrescoTests extends ContextAwareWebTest
{
    private String userName;
    private String siteName;
    private String fileName;
    private String fileContent;
    private String editedName;
    private String editedContent;
    private String editedTitle;
    private String editedDescription;

    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DocumentDetailsPage detailsPage;

    @SuppressWarnings("rawtypes")
    @Autowired private DocumentCommon documentCommon;

    @Autowired private EditInAlfrescoPage editInAlfrescoPage;

    @Autowired private GoogleDocsCommon docsCommon;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        LOG.info("Preconditions for Editing files in Google Docs tests");

        userName = String.format("User%s", DataUtil.getUniqueIdentifier());
        siteName = String.format("siteName%s", DataUtil.getUniqueIdentifier());

        fileName = "testFile";
        fileContent = "testContent";
        editedName = "edited test file";
        editedContent = "edited test content";
        editedTitle = "Edited test title";
        editedDescription = "Edited description";

        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        contentService.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
    }

    @TestRail(id = "C7036")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void editFileInAlfresco()
    {
        LOG.info("Preconditions: Navigate to document library page for the test site");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Hover over the test file and click Edit in Alfresco option");
        documentLibraryPage.clickCheckBox(fileName);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, language.translate("documentLibrary.contentActions.editInAlfresco"),
                editInAlfrescoPage);

        LOG.info("Step2: Edit the document's properties by sending new input");
        editInAlfrescoPage.sendDocumentDetailsFields(editedName, editedContent, editedTitle, editedDescription);

        LOG.info("Step3: Click Save button");
        editInAlfrescoPage.clickButton("Save");
        
        getBrowser().waitInSeconds(3);
        LOG.info("Step4: Verify the new title for the document");
        Assert.assertTrue(docsCommon.isDocumentNameUpdated(editedName), "Document name is not updated");

        LOG.info("Step5: Click on document title to open the document's details page");
        docsCommon.clickOnUpdatedName(editedName);

        LOG.info("Step6: Verify the document's content");
        Assert.assertEquals(detailsPage.getContentText(), editedContent);

        LOG.info("Step7: Verify Title and Description fields");
        Assert.assertTrue(documentCommon.isPropertyValueDisplayed(editedTitle), "Updated title is not displayed");
        Assert.assertTrue(documentCommon.isPropertyValueDisplayed(editedDescription), "Updated description is not displayed");
    }
}