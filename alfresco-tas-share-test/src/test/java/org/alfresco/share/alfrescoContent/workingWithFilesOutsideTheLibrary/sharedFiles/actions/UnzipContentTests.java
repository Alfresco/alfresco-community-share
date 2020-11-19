package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
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
 * @author Laura.Capsa
 */
public class UnzipContentTests extends ContextAwareWebTest
{
    private final String user = String.format("C8040TestUser%s", RandomData.getRandomAlphanumeric());
    private final String path = "Shared";
    private final String zipFile = "archiveC8040.zip";
    private final String zipContent = "fileC8040";
    private final String acpFile = "archiveC8041.acp";
   // @Autowired
    HeaderMenuBar headerMenuBar;
    //@Autowired
    UploadContent uploadContent;
    //@Autowired
    DeleteDialog deleteDialog;
    //@Autowired
    private SharedFilesPage sharedFilesPage;
    //@Autowired
    private DocumentDetailsPage documentDetailsPage;
    //@Autowired
    private CopyMoveUnzipToDialog unzipToDialog;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        setupAuthenticatedSession(user, password);
        contentService.uploadFileInRepository(adminUser, adminPassword, path, testDataFolder + zipFile);
        contentService.uploadFileInRepository(adminUser, adminPassword, path, testDataFolder + acpFile);
    }

    @AfterClass
    public void deleteContent()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);

        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + zipFile);
        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + acpFile);
        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + "fileC8040.txt");
        contentService.deleteTreeByPath(adminUser, adminPassword, path + "/" + "archiveC7410");

    }


    @TestRail (id = "C8040")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void unzipZipArchiveToSharedFile()
    {
        sharedFilesPage.navigate();
        LOG.info("STEP1: Click archive name, e.g: testArchive");
        sharedFilesPage.clickOnFile(zipFile);
        assertTrue(documentDetailsPage.getFileName().equals(zipFile), "Wrong file name!");
        LOG.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage.clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog.getDialogTitle(), "Unzip " + zipFile + " to...", "'Unzip to....' dialog is displayed");
        LOG.info("STEP3: Select option Shared Files from 'Destination' section");
        unzipToDialog.selectSharedFilesDestination();
        String expectedDestionationPath = "Shared Files";
        //assertEquals(unzipToDialog.getPathFirstItem(), expectedDestionationPath, "Destination set to=");
        //Assert.assertTrue(unzipToDialog.getPathList().contains(expectedDestionationPath), "Destination set to = ");
        LOG.info("STEP4: Click 'Unzip' button and navigate to Shared Files");
        unzipToDialog.clickUnzipButton();
        sharedFilesPage.navigate();
        assertTrue(sharedFilesPage.isContentNameDisplayed(zipContent), zipFile + "'s content is displayed, " + zipContent);
    }

    @TestRail (id = "C8041")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void unzipAcpArchiveToSharedFiles()
    {
        sharedFilesPage.navigate();
        LOG.info("STEP1: Click archive name, e.g: testArchive");
        sharedFilesPage.clickOnFile(acpFile);
        assertTrue(documentDetailsPage.getFileName().equals(acpFile), "Wrong file name!");
        LOG.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage.clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog.getDialogTitle(), "Unzip " + acpFile + " to...", "'Unzip to....' dialog is displayed");
        LOG.info("STEP3: Select option Shared Files from 'Destination' section");
        //unzipToDialog.clickDestinationButton(language.translate("documentLibrary.sharedFiles"));
        String expectedDestinationPath = "Shared Files";
        //assertEquals(unzipToDialog.getPathFirstItem(), expectedDestinationPath, "Destionation set to=");
        LOG.info("STEP4: Click 'Unzip' button and navigate to Shared Files");
        unzipToDialog.clickUnzipButton();
        sharedFilesPage.navigate();
        assertTrue(sharedFilesPage.isContentNameDisplayed(acpFile.substring(0, acpFile.indexOf("."))),
            "A folder with archive name is present in Documents list.");
    }
}