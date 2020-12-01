package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Andrei.Nechita
 */
public class ActionsUploadTests extends ContextAwareWebTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String user = "user1-" + random;
    //@Autowired
    private RepositoryPage repositoryPage;
    //@Autowired
    private UploadContent uploadContent;
    //@Autowired
    private DocumentDetailsPage documentDetailsPage;
    private String testFile = RandomData.getRandomAlphanumeric() + "-testFile-C8172-.txt";
    private String testFilePath = testDataFolder + testFile;
    private String testFile2 = RandomData.getRandomAlphanumeric() + "-OldFile-C8175.txt";
    private String newVersionFile = RandomData.getRandomAlphanumeric() + "-NewFile-C8175.txt";


    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = false)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        contentService.deleteContentByPath(adminUser, adminPassword, "/Shared/" + testFile);
        contentService.deleteContentByPath(adminUser, adminPassword, "/Shared/" + newVersionFile);
    }

    @TestRail (id = "C8172")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void uploadDocument()
    {
        String testFile = RandomData.getRandomAlphanumeric() + "-testFile-C8172-.txt";
        String testFilePath = testDataFolder + testFile;
        LOG.info("Precondition: Navigate to Repository page.");
        repositoryPage.navigate();
        LOG.info("STEP1: Check if the Upload button is greyed on the Repository page .");
        Assert.assertEquals(repositoryPage.getUploadButtonStatusDisabled(), "true", "The Upload Button is not disabled");
        LOG.info("STEP2: Go to Shared folder and upload a file.");
        repositoryPage.clickFolderFromExplorerPanel("Shared");
        assertEquals(repositoryPage.getPageHeader(), "Repository Browser");
        uploadContent.uploadContent(testFilePath);
        assertTrue(repositoryPage.isContentNameDisplayed(testFile), String.format("File [%s] is displayed", testFile));
    }

    //    @Bug (id = "MNT-18059", status = Bug.Status.FIXED)
    @TestRail (id = "C8175")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void updateDocumentNewVersion()
    {
        String testFilePath2 = testDataFolder + testFile2;
        String newVersionFilePath = testDataFolder + newVersionFile;
        LOG.info("Precondition: Navigate to Shared folder from Repository page and upload a file");
        repositoryPage.navigate();
        assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser");
        repositoryPage.clickFolderFromExplorerPanel("Shared");
        assertEquals(repositoryPage.getPageHeader(), "Repository Browser");
        uploadContent.uploadContent(testFilePath2);
        LOG.info("STEP1: Click on the file and check content");
        repositoryPage.clickOnFile(testFile2);
        assertEquals(documentDetailsPage.getContentText(), "contents", String.format("Contents of %s are wrong.", testFile2));
        LOG.info("STEP2: Navigate to Shared folder from Repository page and click on upload new version");
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("Shared");
        repositoryPage.clickDocumentLibraryItemAction(testFile2, ItemActions.UPLOAD_NEW_VERSION);
        LOG.info("STEP3: Select file to upload. Update version");
        uploadContent.updateDocumentVersion(newVersionFilePath, "comments", UploadContent.Version.Major);
        repositoryPage.renderedPage();
        assertTrue(repositoryPage.isContentNameDisplayed(newVersionFile), String.format("File [%s] is displayed", newVersionFile));
        assertFalse(repositoryPage.isContentNameDisplayed(testFile), testFile2 + " is displayed.");
        LOG.info("STEP4: Click on the file and check the version and content are updated.");
        repositoryPage.clickOnFile(newVersionFile);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFile));
        assertEquals(documentDetailsPage.getFileVersion(), "2.0", String.format("Version of %s is wrong.", newVersionFile));
    }
}