package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import static org.alfresco.common.Utils.testDataFolder;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.report.Bug;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import java.util.Arrays;
import java.util.List;

@Slf4j

/**
 * @author Laura.Capsa
 */
public class UploadTests extends BaseTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String user = "user1-" + random;
    private final String user2 = "user2-" + random;
    private final String path = "Shared";
    private RepositoryPage repositoryPage;
    private DocumentLibraryPage documentLibrary;
    private CreateContentPage createContent;

    private MyFilesPage myFilesPage;
    private final String password = "password";
    private UserModel testUser1;
    private UserModel testUser2;
    private final String doc1 = random + "-testFile-C7939-.txt";
    private final String doc2 = random + "-OldFile-C7942.txt";
    private final String newDoc2 = random + "-NewFile-C7942.txt";
    private final String doc3 = "Doc-C13756-" + random;
    private SharedFilesPage sharedFilesPage;
    private DocumentDetailsPage documentDetailsPage;
    private UploadContent uploadContent;
    private DocumentLibraryPage documentLibraryPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createUser(user, password);
        testUser2 = dataUser.usingAdmin().createUser(user2, password);
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingLoginPage(getAdminUser());

        repositoryPage = new RepositoryPage(webDriver);
        createContent = new CreateContentPage(webDriver);
        documentLibrary = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        repositoryPage = new RepositoryPage(webDriver);
        myFilesPage = new MyFilesPage(webDriver);
        uploadContent = new UploadContent(webDriver);
        sharedFilesPage = new SharedFilesPage(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);

    }

    @TestRail (id = "C7939")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void uploadDocument()
    {

        authenticateUsingLoginPage(testUser1);
        String testFilePath = testDataFolder + doc1;
        log.info("Precondition: Navigate to Shared Files page.");
        sharedFilesPage
            .navigate();
       log.info("STEP1: Upload a file.");
        uploadContent
            .uploadContent(testFilePath);
       myFilesPage
            .assertIsContantNameDisplayed(doc1);
        log.info("STEP2: Logout and login with another user");
       authenticateUsingLoginPage(testUser2);
       log.info("STEP3: Navigate to Shared Files page");
        sharedFilesPage
            .navigate();
        myFilesPage
            .assertIsContantNameDisplayed(doc1);
        log.info(" Delete the created document");
        authenticateUsingLoginPage(testUser1);
        sharedFilesPage
            .navigate();
        repositoryPage.select_ItemsAction(doc1, ItemActions.DELETE_DOCUMENT)
            .clickOnDeleteButtonOnDeletePrompt();



    }

    @Bug (id = "MNT-18059", status = Bug.Status.FIXED)
    @TestRail (id = "C7942")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT  })
    public void updateDocumentNewVersion()
    {
        String testFilePath = testDataFolder + doc2;
        String newVersionFilePath = testDataFolder + newDoc2;
       authenticateUsingLoginPage(testUser1);
        log.info("Precondition: Navigate to Shared Files page and upload a file");
        sharedFilesPage
            .navigate();
        uploadContent
            .uploadContent(testFilePath);
        log.info("STEP1: Click on the file and check content");
        sharedFilesPage
            .clickOnFile(doc2);
        log.info("STEP2: Navigate to Shared Files page and click on upload new version");
        sharedFilesPage
            .navigate();
        sharedFilesPage
            .selectItemAction(doc2, ItemActions.UPLOAD_NEW_VERSION);
        log.info("STEP3: Select file to upload. Update version");
        uploadContent
            .updateDocumentVersion(newVersionFilePath, "comments", UploadContent.Version.Major);
        myFilesPage
            .assertIsContantNameDisplayed(newDoc2);
        myFilesPage
            .assertIsContentDeleted(doc2);
        log.info("STEP4: Click on the file and check the version and content are updated.");
        sharedFilesPage
            .clickOnFile(newDoc2);
        documentDetailsPage
            .assertFileContentEquals("updated by upload new version");
        documentDetailsPage
            .assertVerifyFileVersion("2.0");
        log.info("STEP5: Logout and login with another user");
        authenticateUsingLoginPage(testUser2);
        log.info("STEP6: Navigate to Shared Files page");
        sharedFilesPage
            .navigate();
        myFilesPage
           .assertIsContantNameDisplayed(newDoc2);
        myFilesPage
           .assertIsContentDeleted(doc2);
        log.info("STEP7: Navigate to newFile details page");
        sharedFilesPage
            .clickOnFile(newDoc2);
        documentDetailsPage
            .assertFileContentEquals("updated by upload new version");
        documentDetailsPage
            .assertVerifyFileVersion("2.0");
        log.info(" Delete the created document");
        authenticateUsingLoginPage(testUser1);
        sharedFilesPage
            .navigate();
        repositoryPage.select_ItemsAction(newDoc2, ItemActions.DELETE_DOCUMENT)
            .clickOnDeleteButtonOnDeletePrompt();
            }

    @TestRail (id = "C13756")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void optionNotDisplayed()
    {
        String testFilePath = testDataFolder + doc3;
        log.info("STEP1: login  with user2 and upload a file  ");
        authenticateUsingLoginPage(testUser2);
        sharedFilesPage
            .navigate();
        uploadContent
            .uploadContent(testFilePath);
        log.info("STEP1: logout and login with user1 and navigate to shared files page");
        authenticateUsingLoginPage(testUser1);
        sharedFilesPage
            .navigate();
        repositoryPage
        .isContentNameDisplayed(doc3);
        log.info("STEP3: Hover over the folder and validated more option is not available ");
        sharedFilesPage
            .mouseOverContentItem(doc3);
        documentLibraryPage
            .assertisMoreMenuNotDisplayed(doc3);
        log.info("STEP4: Hover over and check for Upload New Version option");
       List<String> notExpectedActions = Arrays
            .asList("Upload New Version");
     documentLibraryPage.assertActionsNoteAvailableForLibrary(doc3,notExpectedActions);
        log.info(" Delete the created document");
        authenticateUsingLoginPage(testUser2);
        sharedFilesPage
            .navigate();
        repositoryPage.select_ItemsAction(doc3, ItemActions.DELETE_DOCUMENT)
            .clickOnDeleteButtonOnDeletePrompt();

    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {

        deleteUsersIfNotNull(testUser1);
        deleteUsersIfNotNull(testUser2);


    }
}