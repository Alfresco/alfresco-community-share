package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesDeleteTests extends ContextAwareWebTest
{
    private final String user = String.format("user%s", RandomData.getRandomAlphanumeric());
    private final String testFile = String.format("testFile%s", RandomData.getRandomAlphanumeric());
    private final String folderName = String.format("testFolder%s", RandomData.getRandomAlphanumeric());
    private final String myFilesPath = "User Homes/" + user;
   // @Autowired
    private MyFilesPage myFilesPage;
    //@Autowired
    private DeleteDialog deleteDialog;

    @BeforeClass (alwaysRun = true)
    public void createUser()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);

    }


    @TestRail (id = "C7896")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void myFilesDeleteDocument()
    {
        LOG.info("Precondition: Login as user, navigate to My Files page and upload a file.");
        contentService.createDocumentInRepository(user, password, myFilesPath, CMISUtil.DocumentType.TEXT_PLAIN, testFile, "some content");
        myFilesPage.navigate();
//        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        assertTrue(myFilesPage.isContentNameDisplayed(testFile), String.format("The file [%s] is not present", testFile));

        LOG.info("STEP1: Hover over the file. STEP2: Click 'More...' link. Click 'Delete Document' link");
        myFilesPage.clickDocumentLibraryItemAction(testFile, ItemActions.DELETE_DOCUMENT);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteDocument"), "'Delete Document' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), testFile));

        LOG.info("STEP3: Click 'Delete' button");
        deleteDialog.confirmDeletion();

        LOG.info("STEP4: Verify that the file was deleted");
        assertFalse(myFilesPage.isContentNameDisplayed(testFile), "Documents item list is refreshed and is empty");
    }

    @TestRail (id = "C7896")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void myFilesDeleteFolder()
    {
        LOG.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        contentService.createFolderInRepository(user, password, folderName, myFilesPath);
        myFilesPage.navigate();
//        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        assertTrue(myFilesPage.isContentNameDisplayed(folderName), folderName + " displayed in My Files documents list.");

        LOG.info("STEP1: Hover over the folder. STEP2: Click on 'More...' link and choose 'Delete Folder' from the dropdown list.");
        myFilesPage.clickDocumentLibraryItemAction(folderName, ItemActions.DELETE_FOLDER);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteFolder"), "'Delete Folder' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), folderName));

        LOG.info("STEP3: Click 'Delete' button");
        deleteDialog.confirmDeletion();
        assertFalse(myFilesPage.isContentNameDisplayed(folderName), "Documents item list is refreshed and is empty");
        assertFalse(myFilesPage.getExplorerPanelDocuments().contains(folderName), "'DelFolder' is not visible in 'Library' section of the browsing pane.");
    }
}
