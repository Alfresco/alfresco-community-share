package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesUnzipContentTests extends BaseTest
{

    private final String zipFile = "archiveC7816.zip";
    private final String zipContent = "TestFileC7816";
    private final String zipFilePath = testDataFolder + zipFile;
    private final String acpFile = "archiveC7817.acp";
    private final String acpPath = testDataFolder + acpFile;
    //@Autowired
    private MyFilesPage myFilesPage;
    //@Autowired
    private DocumentDetailsPage documentDetailsPage;
    //@Autowired
    private CopyMoveUnzipToDialog unzipToDialog;
    //@Autowired
    private UploadContent uploadContent;
    //@Autowired
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();



 @BeforeMethod(alwaysRun = true)
 public void setupTest() {
     log.info("PreCondition: Creating a TestUser");
     user.set(getDataUser().usingAdmin().createRandomTestUser());
     getCmisApi().authenticateUser(getAdminUser());
     authenticateUsingCookies(user.get());

     myFilesPage = new MyFilesPage(webDriver);
     uploadContent = new UploadContent(webDriver);
     documentDetailsPage = new DocumentDetailsPage(webDriver);
     unzipToDialog = new CopyMoveUnzipToDialog(webDriver);
 }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }

    @TestRail (id = "C7816")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void unzipZipArchiveToMyFiles() throws InterruptedException {
        log.info("Precondition: Login as user, navigate to My Files page and upload a zip archive.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        uploadContent
            .uploadContent(zipFilePath);
        log.info("STEP1: Click archive name, e.g: testArchive");
        myFilesPage
            .clickOnFile(zipFile);
        documentDetailsPage
            .assertContentNameEquals(zipFile);
        log.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage
            .clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog
            .getDialogTitle(),
            "Unzip " + zipFile + " to...", "'Unzip to....' dialog is displayed");

        log.info("STEP3: Select option My Files from 'Destination' section");
        unzipToDialog
            .selectMyFilesDestination();
        log.info("STEP4: Click 'Unzip' button and navigate to My Files");
        unzipToDialog
            .clickUnzipButton();
        myFilesPage
            .navigate();
        myFilesPage
            .assertIsContantNameDisplayed(zipContent);
 }

    @TestRail (id = "C7817")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, enabled = false)
    public void unzipACPArchiveToMyFiles()
    {
        log.info("Precondition: Login as user, navigate to My Files page and upload a zip archive.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        uploadContent
            .uploadContent(acpPath);
        log.info("STEP1: Click archive name, e.g: testArchive");
        myFilesPage
            .clickOnFile(acpFile);
        documentDetailsPage
            .assertContentNameEquals(acpFile);
        System.out.println("exeuted step1");
        log.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage
            .clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog
            .getDialogTitle(),
            "Unzip " + acpFile + " to...", "'Unzip to....' dialog is displayed");

        log.info("STEP3: Select option My Files from 'Destination' section");
        unzipToDialog
            .selectMyFilesDestination();
        log.info("STEP4: Click 'Unzip' button and navigate to My Files");
        unzipToDialog
            .clickUnzipButton();
        myFilesPage
            .navigate();
        myFilesPage
           .assertIsContantNameDisplayed(zipContent);
    }
}
