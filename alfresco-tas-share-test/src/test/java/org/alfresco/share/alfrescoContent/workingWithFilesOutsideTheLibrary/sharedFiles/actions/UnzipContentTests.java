package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class UnzipContentTests extends ContextAwareWebTest
{
    @Autowired
    HeaderMenuBar headerMenuBar;

    @Autowired
    SharedFilesPage sharedFilesPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    CopyMoveUnzipToDialog unzipToDialog;

    @Autowired
    UploadContent uploadContent;

    @Autowired
    DeleteDialog deleteDialog;

    private String user = "C8040TestUser" + DataUtil.getUniqueIdentifier();
    private String path = "Shared";
    private String testDataFolder = srcRoot + "testdata" + File.separator;
    private String zipFile = "archiveC8040.zip";
    private String zipContent = "fileC8040";
    private String acpFile = "archiveC8041.acp";

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        contentService.uploadFileInRepository(adminUser, adminPassword, path, testDataFolder + zipFile);
        contentService.uploadFileInRepository(adminUser, adminPassword, path, testDataFolder + acpFile);
    }

    @TestRail(id = "C8040")
    @Test
    public void unzipZipArchiveToSharedFile()
    {
        String deletePath = path + "/" + acpFile;
        sharedFilesPage.navigate();

        LOG.info("STEP1: Click archive name, e.g: testArchive");

        sharedFilesPage.clickOnFile(zipFile);
        assertTrue(documentDetailsPage.getFileName().equals(zipFile), "Wrong file name!");

        LOG.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage.clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog.getDialogTitle(), "Unzip " + zipFile + " to...", "'Unzip to....' dialog is displayed");

        LOG.info("STEP3: Select option Shared Files from 'Destination' section");
        unzipToDialog.clickDestinationButton(language.translate("documentLibrary.sharedFiles"));
        String expectedDestionationPath= "Shared Files";
        //Assert.assertTrue(unzipToDialog.getPathList().contains(expectedDestionationPath.toString()));
        assertEquals(unzipToDialog.getPathFirstItem(), expectedDestionationPath.toString(), "Destination set to=");
        Assert.assertTrue(unzipToDialog.getPathList().contains(expectedDestionationPath.toString()), "Destination set to = ");
        LOG.info("STEP4: Click 'Unzip' button and navigate to Shared Files");
        unzipToDialog.clickButtton(language.translate("documentLibrary.contentActions.unzip"));
        sharedFilesPage.navigate();
        assertTrue(sharedFilesPage.isContentNameDisplayed(zipContent), zipFile + "'s content is displayed, " + zipContent);

        //contentService.deleteContentByPath(adminUser, adminPassword, deletePath);
    }

    @TestRail(id = "C8041")
    @Test
    public void unzipAcpArchiveToSharedFiles()
    {
        String deletePath = path + "/" + acpFile;
        sharedFilesPage.navigate();

        LOG.info("STEP1: Click archive name, e.g: testArchive");
        sharedFilesPage.clickOnFile(acpFile);
        assertTrue(documentDetailsPage.getFileName().equals(acpFile), "Wrong file name!");

        LOG.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage.clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog.getDialogTitle(), "Unzip " + acpFile + " to...", "'Unzip to....' dialog is displayed");

        LOG.info("STEP3: Select option Shared Files from 'Destination' section");
        unzipToDialog.clickDestinationButton(language.translate("documentLibrary.sharedFiles"));
        String expectedDestinationPath ="Shared Files";
        //Assert.assertTrue(unzipToDialog.getPathList().contains(expectedDestinationPath.toString()), "Destination set to = ");
        assertEquals(unzipToDialog.getPathFirstItem(), expectedDestinationPath.toString(), "Destionation set to=");

        LOG.info("STEP4: Click 'Unzip' button and navigate to Shared Files");
        unzipToDialog.clickButtton(language.translate("documentLibrary.contentActions.unzip"));
        sharedFilesPage.navigate();
        assertTrue(sharedFilesPage.isContentNameDisplayed(acpFile.substring(0, acpFile.indexOf("."))),
                "A folder with archive name is present in Documents list.");
        //contentService.deleteContentByPath(adminUser, adminPassword, deletePath);

    }
}