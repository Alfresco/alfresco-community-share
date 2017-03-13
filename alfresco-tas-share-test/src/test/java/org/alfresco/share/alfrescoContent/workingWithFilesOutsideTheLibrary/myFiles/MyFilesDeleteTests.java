package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.DeleteDocumentOrFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesDeleteTests extends ContextAwareWebTest
{
    @Autowired
    MyFilesPage myFilesPage;

    @Autowired
    SiteDashboardPage sitePage;

    @Autowired
    NewContentDialog newContentDialog;

    @Autowired
    DeleteDocumentOrFolderDialog deleteDialog;

    @Autowired
    private UploadContent uploadContent;

    private String testFile =  DataUtil.getUniqueIdentifier() + "testFile.txt";
    private String testFilePath = testDataFolder + testFile;
    private String folderName = "testFolder" + DataUtil.getUniqueIdentifier();

    @TestRail(id = "C7896")
    @Test
    public void myFilesDeleteDocument()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and upload a file.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(testFilePath);
        assertTrue(myFilesPage.isContentNameDisplayed(testFile),String.format("The file [%s] is not present", testFile));

        LOG.info("STEP1: Hover over the file");
        myFilesPage.mouseOverFileName(testFile);

        LOG.info("STEP2: Click 'More...' link. Click 'Delete Document' link");
        myFilesPage.clickDocumentLibraryItemAction(testFile, "Delete Document", deleteDialog);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteDocument"), "'Delete Document' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), testFile));

        LOG.info("STEP3: Click 'Delete' button");
        deleteDialog.confirmDocumentOrFolderDelete();

        LOG.info("STEP4: Verify that the file was deleted");
        assertFalse(myFilesPage.isContentNameDisplayed(testFile), "Documents item list is refreshed and is empty");
    }

    @TestRail(id = "C7896")
    @Test
    public void myFilesDeleteFolder()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        myFilesPage.clickCreateButton();
        myFilesPage.clickFolderLink();
        newContentDialog.fillInNameField(folderName);
        newContentDialog.clickSaveButton();
        getBrowser().waitInSeconds(3);
        assertTrue(myFilesPage.isContentNameDisplayed(folderName), folderName + " displayed in My Files documents list.");

        LOG.info("STEP1: Hover over the folder");
        myFilesPage.mouseOverContentItem(folderName);

        LOG.info("STEP2: Click on 'More...' link and choose 'Delete Folder' from the dropdown list.");
        myFilesPage.clickDocumentLibraryItemAction(folderName, "Delete Folder", deleteDialog);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteFolder"), "'Delete Folder' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), folderName));

        LOG.info("STEP3: Click 'Delete' button");
        deleteDialog.confirmDocumentOrFolderDelete();
        assertFalse(myFilesPage.isContentNameDisplayed(folderName), "Documents item list is refreshed and is empty");
        assertFalse(myFilesPage.getExplorerPanelDocuments().contains(folderName), "'DelFolder' is not visible in 'Library' section of the browsing pane.");
    }
}
