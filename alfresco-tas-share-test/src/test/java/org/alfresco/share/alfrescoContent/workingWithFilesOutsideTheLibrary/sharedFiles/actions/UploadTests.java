package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class UploadTests extends ContextAwareWebTest
{
    @Autowired
    SharedFilesPage sharedFilesPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    HeaderMenuBar headerMenuBar;

    @Autowired
    UploadContent uploadContent;

    @Autowired
    DeleteDialog deleteDialog;

    String random = DataUtil.getUniqueIdentifier();
    String user = "user1-" + random;
    String user2 = "user2-" + random;
    String path = "Shared";

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        userService.create(adminUser, adminPassword, user2, password, user + "@tests.com", user2, user2);
    }

    @TestRail(id = "C7939")
    @Test
    public void uploadDocument()
    {
        setupAuthenticatedSession(user, password);
        String testFile = DataUtil.getUniqueIdentifier() + "-testFile-C7939-.txt";
        String testFilePath = testDataFolder + testFile;

        LOG.info("Precondition: Navigate to Shared Files page.");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");

        LOG.info("STEP1: Upload a file.");
        uploadContent.uploadContent(testFilePath);
        assertTrue(sharedFilesPage.isContentNameDisplayed(testFile), String.format("File [%s] is displayed", testFile));

        LOG.info("STEP2: Logout and login with another user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP3: Navigate to Shared Files page");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(testFile), String.format("File [%s] is displayed", testFile));

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7942")
    @Test
    public void updateDocumentNewVersion()
    {
        setupAuthenticatedSession(adminUser, adminPassword);

        String testFile = DataUtil.getUniqueIdentifier() + "-OldFile-C7942.txt";
        String newVersionFile = DataUtil.getUniqueIdentifier() + "-NewFile-C7942.txt";
        String testFilePath = testDataFolder + testFile;
        String newVersionFilePath = testDataFolder + newVersionFile;

        LOG.info("Precondition: Navigate to Shared Files page and upload a file");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");
        uploadContent.uploadContent(testFilePath);

        LOG.info("STEP1: Click on the file and check content");
        sharedFilesPage.clickOnFile(testFile);
        assertEquals(documentDetailsPage.getContentText(), "contents", String.format("Contents of %s are wrong.", testFile));

        LOG.info("STEP2: Navigate to Shared Files page and click on upload new version");
        sharedFilesPage.navigate();
        sharedFilesPage.mouseOverFileName(testFile);
        getBrowser().waitInSeconds(3);
        sharedFilesPage.clickDocumentLibraryItemAction(testFile, language.translate("documentLibrary.contentAction.uploadNewVersion"), uploadContent);

        LOG.info("STEP3: Select file to upload. Update version");
        uploadContent.updateDocumentVersion(newVersionFilePath, "comments", UploadContent.Version.Major);
        getBrowser().waitInSeconds(4);
        assertTrue(sharedFilesPage.isContentNameDisplayed(newVersionFile), String.format("File [%s] is displayed", newVersionFile));
        assertFalse(sharedFilesPage.isContentNameDisplayed(testFile), testFile + " is displayed.");

        LOG.info("STEP4: Click on the file and check the version and content are updated.");
        sharedFilesPage.clickOnFile(newVersionFile);
        getBrowser().waitInSeconds(3);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFile));
        assertEquals(documentDetailsPage.getFileVersion(), "2.0", String.format("Version of %s is wrong.", newVersionFile));

        LOG.info("STEP5: Logout and login with another user");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP6: Navigate to Shared Files page");
        sharedFilesPage.navigate();
        getBrowser().waitInSeconds(4);
        assertTrue(sharedFilesPage.isContentNameDisplayed(newVersionFile), String.format("File [%s] is displayed", newVersionFile));
        assertFalse(sharedFilesPage.isContentNameDisplayed(testFile), String.format("File [%s] is displayed", testFile));

        LOG.info("STEP7: Navigate to newFile details page");
        sharedFilesPage.clickOnFile(newVersionFile);
        getBrowser().waitInSeconds(3);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFile));
        assertEquals(documentDetailsPage.getFileVersion(), "2.0", String.format("Version of %s is wrong.", newVersionFile));

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C13756")
    @Test()
    public void optionNotDisplayed()
    {
        String docName = "Doc-C13756-" + DataUtil.getUniqueIdentifier();
        contentService.createDocumentInRepository(adminUser, adminPassword, path, CMISUtil.DocumentType.TEXT_PLAIN, docName, "");
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();

        LOG.info("STEP1: Hover over the file");
        sharedFilesPage.mouseOverFileName(docName);
        getBrowser().waitInSeconds(3);
        assertFalse(sharedFilesPage.isActionAvailableForLibraryItem(docName, language.translate("documentLibrary.contentAction.uploadNewVersion")),
                language.translate("documentLibrary.contentAction.uploadNewVersion") + " option is displayed for " + docName);

        cleanupAuthenticatedSession();
    }

    @AfterClass
    public void cleanUp()
    {
        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("Delete All from 'Shared Files'");
        sharedFilesPage.navigate();
        getBrowser().waitInSeconds(4);
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.all"));
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption(language.translate("documentLibrary.breadcrumb.selectedItems.delete"));
        deleteDialog.clickDelete();
    }
}