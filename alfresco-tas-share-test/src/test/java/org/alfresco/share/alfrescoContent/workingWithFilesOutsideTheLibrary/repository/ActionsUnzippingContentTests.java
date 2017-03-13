package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.ContentService;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ActionsUnzippingContentTests extends ContextAwareWebTest
{

    @Autowired
    ContentService contentService;

    @Autowired
    RepositoryPage repositoryPage;

    @Autowired
    DeleteDialog deleteDialog;

    @Autowired
    CopyMoveUnzipToDialog unzipToDialog;

    @Autowired
    DocumentDetailsPage documentDetailsPage;
  
    private String user = "C8256TestUser" + DataUtil.getUniqueIdentifier();
    private String path = "";
    private String zipFile = "testFileC8256.zip";
    private String testDataFolder = srcRoot + "testdata" + File.separator;
    private String zipContent = "testFile1";
    private String acpFile = "archiveC8257.acp";
   
    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        contentService.uploadFileInRepository(adminUser, adminPassword, path, testDataFolder + zipFile);    
        contentService.uploadFileInRepository(adminUser, adminPassword, path, testDataFolder + acpFile); 
    }

    @TestRail(id = "C8256")
    @Test

    public void unzipZipFileToRepository()
    {
        LOG.info("Upload zip archive");
        repositoryPage.navigate();
        getBrowser().refresh();
        repositoryPage.renderedPage();
        assertTrue(repositoryPage.getFilesList().contains(zipFile), "Repository: list of files=");

        LOG.info("STEP1: Click archive name, e.g: testArchive");
        repositoryPage.clickOnFile(zipFile);
        assertTrue(documentDetailsPage.getFileName().equals(zipFile), "Wrong file name!");

        LOG.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage.clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog.getDialogTitle(), "Unzip " + zipFile + " to...", "'Unzip to....' dialog is displayed");

        LOG.info("STEP3: Select option My Files from 'Destination' section");
        unzipToDialog.clickDestinationButton("My Files");
        ArrayList expectedDestionationPath = new ArrayList(Arrays.asList("My Files"));
        assertEquals(unzipToDialog.getPathList(), expectedDestionationPath.toString(), "Destionation set to=");

        LOG.info("STEP4: Click 'Unzip' button and navigate to My Files");
        unzipToDialog.clickButtton(language.translate("documentLibrary.contentActions.unzip"));
        repositoryPage.navigate();
        repositoryPage.renderedPage();
        repositoryPage.clickOnContent("User Homes");
        repositoryPage.clickOnContent(user);
        getBrowser().waitUntilElementClickable(repositoryPage.subfolderDocListTree(user), 10L);
        assertTrue(repositoryPage.isContentNameDisplayed(zipContent), "content is displayed, " + zipContent);
    }
    
    @TestRail(id ="C8257")
    @Test
    
    public void unzipAcpFileToRepository()
    {
        LOG.info("Upload acp archive");
        repositoryPage.navigate();
        assertTrue(repositoryPage.getFilesList().contains(acpFile), "Repository: list of files=");

        LOG.info("STEP1: Click archive name, e.g: testArchive");
        repositoryPage.clickOnFile(acpFile);
        assertTrue(documentDetailsPage.getFileName().equals(acpFile), "Wrong file name!");

        LOG.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage.clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog.getDialogTitle(), "Unzip " + acpFile + " to...", "'Unzip to....' dialog is displayed");

        LOG.info("STEP3: Select option My Files from 'Destination' section");
        unzipToDialog.clickDestinationButton("My Files");
        ArrayList expectedDestionationPath = new ArrayList(Arrays.asList("My Files"));
        assertEquals(unzipToDialog.getPathList(), expectedDestionationPath.toString(), "Destionation set to=");

        LOG.info("STEP4: Click 'Unzip' button and navigate to My Files");
        unzipToDialog.clickButtton(language.translate("documentLibrary.contentActions.unzip"));
        repositoryPage.navigate();
        repositoryPage.renderedPage();
        repositoryPage.clickOnContent("User Homes");
        repositoryPage.clickOnContent(user);
        repositoryPage.getDocumentListHeader();
        repositoryPage.selectDocumentLibraryItemRow("fileC8257");
        assertTrue(repositoryPage.isContentNameDisplayed("fileC8257"), acpFile + " is not displayed " );
    }
}
