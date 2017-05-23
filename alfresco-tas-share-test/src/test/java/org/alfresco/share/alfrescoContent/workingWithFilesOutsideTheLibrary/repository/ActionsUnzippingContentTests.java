package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ActionsUnzippingContentTests extends ContextAwareWebTest
{
    @Autowired private RepositoryPage repositoryPage;

    @Autowired private CopyMoveUnzipToDialog unzipToDialog;

    @Autowired private DocumentDetailsPage documentDetailsPage;
  
    private final String user = String.format("C8256TestUser%s", RandomData.getRandomAlphanumeric());
    private final String zipFile = "testFileC8256.zip";
    private final String zipContent = "testFile1";
    private final String acpFile = "archiveC8257.acp";
   
    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        setupAuthenticatedSession(user, password);
        contentService.uploadFileInRepository(adminUser, adminPassword, null, testDataFolder + zipFile);
        contentService.uploadFileInRepository(adminUser, adminPassword, null, testDataFolder + acpFile);
    }

    @TestRail(id = "C8256")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})

    public void unzipZipFileToRepository()
    {
        LOG.info("Upload zip archive");
        repositoryPage.navigate();
        assertTrue(repositoryPage.isContentNameDisplayed(zipFile), "Repository: list of files=");

        LOG.info("STEP1: Click archive name, e.g: testArchive");
        repositoryPage.clickOnFile(zipFile);
        assertTrue(documentDetailsPage.getFileName().equals(zipFile), "Wrong file name!");

        LOG.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage.clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog.getDialogTitle(), "Unzip " + zipFile + " to...", "'Unzip to....' dialog is displayed");

        LOG.info("STEP3: Select option My Files from 'Destination' section");
        unzipToDialog.clickDestinationButton("My Files");
        ArrayList expectedDestionationPath = new ArrayList(Collections.singletonList("My Files"));
        assertEquals(unzipToDialog.getPathList(), expectedDestionationPath.toString(), "Destionation set to=");

        LOG.info("STEP4: Click 'Unzip' button and navigate to My Files");
        unzipToDialog.clickButtton(language.translate("documentLibrary.contentActions.unzip"));
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        assertTrue(repositoryPage.isContentNameDisplayed(zipContent), "content is displayed, " + zipContent);
    }
    
    @TestRail(id ="C8257")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void unzipAcpFileToRepository()
    {
        LOG.info("Upload acp archive");
        repositoryPage.navigate();
        assertTrue(repositoryPage.isContentNameDisplayed(acpFile), "Repository: list of files=");

        LOG.info("STEP1: Click archive name, e.g: testArchive");
        repositoryPage.clickOnFile(acpFile);
        assertTrue(documentDetailsPage.getFileName().equals(acpFile), "Wrong file name!");

        LOG.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage.clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog.getDialogTitle(), "Unzip " + acpFile + " to...", "'Unzip to....' dialog is displayed");

        LOG.info("STEP3: Select option My Files from 'Destination' section");
        unzipToDialog.clickDestinationButton("My Files");
        ArrayList expectedDestionationPath = new ArrayList(Collections.singletonList("My Files"));
        assertEquals(unzipToDialog.getPathList(), expectedDestionationPath.toString(), "Destionation set to=");

        LOG.info("STEP4: Click 'Unzip' button and navigate to My Files");
        unzipToDialog.clickButtton(language.translate("documentLibrary.contentActions.unzip"));
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        repositoryPage.getDocumentListHeader();
        repositoryPage.selectDocumentLibraryItemRow("fileC8257");
        assertTrue(repositoryPage.isContentNameDisplayed("fileC8257"), acpFile + " is not displayed " );
    }
}
