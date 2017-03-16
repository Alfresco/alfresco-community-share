package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Andrei.Nechita
 */
public class ActionsUploadTests extends ContextAwareWebTest
{
    @Autowired private RepositoryPage repositoryPage;

    @Autowired private UploadContent uploadContent;

    @Autowired private DocumentDetailsPage documentDetailsPage;

    private final String random = DataUtil.getUniqueIdentifier();
    private final String user = "user1-" + random;

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
    }

    @TestRail(id = "C8172")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void uploadDocument()
    {
        setupAuthenticatedSession(user, password);
        String testFile = DataUtil.getUniqueIdentifier() + "-testFile-C8172-.txt";
        String testFilePath = testDataFolder + testFile;

        LOG.info("Precondition: Navigate to Repository page.");
        repositoryPage.navigate();
        assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser");

        LOG.info("STEP1: Check if the Upload button is greyed on the Repository page .");
        Assert.assertEquals(repositoryPage.getUploadButtonStatusDisabled(), "true", "The Upload Button is not disabled");

        LOG.info("STEP2: Go to Shared folder and upload a file.");
        repositoryPage.clickFolderFromExplorerPanel("Shared");
        assertEquals(repositoryPage.getPageHeader(), "Repository Browser");

        uploadContent.uploadContent(testFilePath);
        assertTrue(repositoryPage.isContentNameDisplayed(testFile), String.format("File [%s] is displayed", testFile));

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C8175")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void updateDocumentNewVersion()
    {
        setupAuthenticatedSession(user, password);

        String testFile = DataUtil.getUniqueIdentifier() + "-OldFile-C8175.txt";
        String newVersionFile = DataUtil.getUniqueIdentifier() + "-NewFile-C8175.txt";
        String testFilePath = testDataFolder + testFile;
        String newVersionFilePath = testDataFolder + newVersionFile;

        LOG.info("Precondition: Navigate to Shared folder from Repository page and upload a file");
        repositoryPage.navigate();
        assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser");
        repositoryPage.clickFolderFromExplorerPanel("Shared");
        assertEquals(repositoryPage.getPageHeader(), "Repository Browser");
        uploadContent.uploadContent(testFilePath);

        LOG.info("STEP1: Click on the file and check content");
        repositoryPage.clickOnFile(testFile);
        assertEquals(documentDetailsPage.getContentText(), "contents", String.format("Contents of %s are wrong.", testFile));

        LOG.info("STEP2: Navigate to Shared folder from Repository page and click on upload new version");
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("Shared");
        repositoryPage.mouseOverFileName(testFile);
        repositoryPage.clickDocumentLibraryItemAction(testFile, language.translate("documentLibrary.contentAction.uploadNewVersion"), uploadContent);

        LOG.info("STEP3: Select file to upload. Update version");
        uploadContent.updateDocumentVersion(newVersionFilePath, "comments", UploadContent.Version.Major);
        getBrowser().waitInSeconds(5);
        assertTrue(repositoryPage.isContentNameDisplayed(newVersionFile), String.format("File [%s] is displayed", newVersionFile));
        assertFalse(repositoryPage.isContentNameDisplayed(testFile), testFile + " is displayed.");

        LOG.info("STEP4: Click on the file and check the version and content are updated.");
        repositoryPage.clickOnFile(newVersionFile);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFile));
        assertEquals(documentDetailsPage.getFileVersion(), "2.0", String.format("Version of %s is wrong.", newVersionFile));

        cleanupAuthenticatedSession();
    }
}