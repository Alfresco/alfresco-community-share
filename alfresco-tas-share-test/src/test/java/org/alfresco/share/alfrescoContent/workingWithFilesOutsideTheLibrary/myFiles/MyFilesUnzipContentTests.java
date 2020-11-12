package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
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
 * @author Razvan.Dorobantu
 */
public class MyFilesUnzipContentTests extends ContextAwareWebTest
{

    private final String user = String.format("user%s", RandomData.getRandomAlphanumeric());
    private final String zipFile = "archiveC7816.zip";
    private final String zipContent = "TestFileC7816";
    private final String zipFilePath = testDataFolder + zipFile;
    private final String acpFile = "archiveC7816.acp";
    private final String acpPath = testDataFolder + acpFile;
    //@Autowired
    private MyFilesPage myFilesPage;
    @Autowired
    private DocumentDetailsPage documentDetailsPage;
    @Autowired
    private CopyMoveUnzipToDialog unzipToDialog;
    //@Autowired
    private UploadContent uploadContent;
    //@Autowired
    private SiteDashboardPage sitePage;

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


    @TestRail (id = "C7816")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void unzipZipArchiveToMyFiles()
    {
        LOG.info("Precondition: Login as user, navigate to My Files page and upload a zip archive.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(zipFilePath);
        LOG.info("STEP1: Click archive name, e.g: testArchive");
        myFilesPage.clickOnFile(zipFile);
        assertTrue(documentDetailsPage.getFileName().equals(zipFile), "Wrong file name!");
        LOG.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage.clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog.getDialogTitle(), "Unzip " + zipFile + " to...", "'Unzip to....' dialog is displayed");
        LOG.info("STEP3: Select option My Files from 'Destination' section");
        unzipToDialog.clickDestinationButton("My Files");
        ArrayList expectedDestinationPath = new ArrayList(Collections.singletonList("My Files"));
        assertEquals(unzipToDialog.getPathList(), expectedDestinationPath.toString(), "Destionation set to=");
        LOG.info("STEP4: Click 'Unzip' button and navigate to My Files");
        unzipToDialog.clickUnzipButton();
        myFilesPage.navigate();
        assertTrue(myFilesPage.isContentNameDisplayed(zipContent), zipFile + "'s content is displayed, " + zipContent);
    }

    @TestRail (id = "C7817")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, enabled = false)
    public void unzipACPArchiveToMyFiles()
    {
        LOG.info("Precondition: Login as user, navigate to My Files page and upload a zip archive.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(acpPath);
        LOG.info("STEP1: Click archive name, e.g: testArchive");
        myFilesPage.clickOnFile(acpFile);
        assertTrue(documentDetailsPage.getFileName().equals(acpFile), "Wrong file name!");
        LOG.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage.clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog.getDialogTitle(), "Unzip " + acpFile + " to...", "'Unzip to....' dialog is displayed");
        LOG.info("STEP3: Select option My Files from 'Destination' section");
        unzipToDialog.clickDestinationButton("My Files");
        ArrayList expectedDestinationPath = new ArrayList(Collections.singletonList("My Files"));
        assertEquals(unzipToDialog.getPathList(), expectedDestinationPath.toString(), "Destionation set to=");
        LOG.info("STEP4: Click 'Unzip' button and navigate to My Files");
        unzipToDialog.clickUnzipButton();
        myFilesPage.navigate();
        assertTrue(myFilesPage.isContentNameDisplayed(acpFile.substring(0, acpFile.indexOf("."))),
            "A folder with archive name is present in Documents list.");
    }
}
