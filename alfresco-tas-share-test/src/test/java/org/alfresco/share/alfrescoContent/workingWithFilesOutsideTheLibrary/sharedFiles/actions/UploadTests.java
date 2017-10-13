package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class UploadTests extends ContextAwareWebTest
{
    @Autowired private SharedFilesPage sharedFilesPage;

    @Autowired private DocumentDetailsPage documentDetailsPage;

    @Autowired private HeaderMenuBar headerMenuBar;

    @Autowired private UploadContent uploadContent;

    @Autowired private DeleteDialog deleteDialog;

    private final String random = RandomData.getRandomAlphanumeric();
    private final String user = "user1-" + random;
    private final String user2 = "user2-" + random;
    private final String path = "Shared";
    private final String doc1 = random + "-testFile-C7939-.txt";
    private final String doc2 = random + "-OldFile-C7942.txt";
    private final String newDoc2 = random + "-NewFile-C7942.txt";
    private final String doc3 = "Doc-C13756-" + random;

    @BeforeClass(alwaysRun = true)
    public void setupTest() {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        userService.create(adminUser, adminPassword, user2, password, user + domain, user2, user2);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, CMISUtil.DocumentType.TEXT_PLAIN, doc3, "");
    }

    @TestRail(id = "C7939")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void uploadDocument() {
        setupAuthenticatedSession(user, password);
        String testFilePath = testDataFolder + doc1;
        LOG.info("Precondition: Navigate to Shared Files page.");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        LOG.info("STEP1: Upload a file.");
        uploadContent.uploadContent(testFilePath);
        assertTrue(sharedFilesPage.isContentNameDisplayed(doc1), String.format("File [%s] is displayed", doc1));
        LOG.info("STEP2: Logout and login with another user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        LOG.info("STEP3: Navigate to Shared Files page");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(doc1), String.format("File [%s] is displayed", doc1));
        cleanupAuthenticatedSession();
    }
    @Bug(id="MNT-18059",status = Bug.Status.FIXED)
    @TestRail(id = "C7942")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void updateDocumentNewVersion() {
        String testFilePath = testDataFolder + doc2;
        String newVersionFilePath = testDataFolder + newDoc2;
        setupAuthenticatedSession(adminUser, adminPassword);
        LOG.info("Precondition: Navigate to Shared Files page and upload a file");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");
        uploadContent.uploadContent(testFilePath);
        LOG.info("STEP1: Click on the file and check content");
        sharedFilesPage.clickOnFile(doc2);
        assertEquals(documentDetailsPage.getContentText(), "contents", String.format("Contents of %s are wrong.", doc2));
        LOG.info("STEP2: Navigate to Shared Files page and click on upload new version");
        sharedFilesPage.navigate();
        sharedFilesPage.clickDocumentLibraryItemAction(doc2, language.translate("documentLibrary.contentAction.uploadNewVersion"), uploadContent);
        LOG.info("STEP3: Select file to upload. Update version");
        uploadContent.updateDocumentVersion(newVersionFilePath, "comments", UploadContent.Version.Major);
        getBrowser().waitInSeconds(2);
        assertTrue(sharedFilesPage.isContentNameDisplayed(newDoc2), String.format("File [%s] is displayed", newDoc2));
        assertFalse(sharedFilesPage.isContentNameDisplayed(doc2), doc2 + " is displayed.");
        LOG.info("STEP4: Click on the file and check the version and content are updated.");
        sharedFilesPage.clickOnFile(newDoc2);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newDoc2));
        assertEquals(documentDetailsPage.getFileVersion(), "2.0", String.format("Version of %s is wrong.", newDoc2));
        LOG.info("STEP5: Logout and login with another user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        LOG.info("STEP6: Navigate to Shared Files page");
        sharedFilesPage.navigate();
        assertTrue(sharedFilesPage.isContentNameDisplayed(newDoc2), String.format("File [%s] is displayed", newDoc2));
        assertFalse(sharedFilesPage.isContentNameDisplayed(doc2), String.format("File [%s] is displayed", doc2));
        LOG.info("STEP7: Navigate to newFile details page");
        sharedFilesPage.clickOnFile(newDoc2);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newDoc2));
        assertEquals(documentDetailsPage.getFileVersion(), "2.0", String.format("Version of %s is wrong.", newDoc2));
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C13756")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void optionNotDisplayed() {
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        LOG.info("STEP1: Hover over the file");
        assertFalse(sharedFilesPage.isActionAvailableForLibraryItem(doc3, language.translate("documentLibrary.contentAction.uploadNewVersion")),
                language.translate("documentLibrary.contentAction.uploadNewVersion") + " option is displayed for " + doc3);
        cleanupAuthenticatedSession();
    }

    @AfterClass
    public void cleanUp() {
        contentService.deleteContentByPath(user, password, path + "/" + doc1);
        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + newDoc2);
        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + doc3);
    }
}