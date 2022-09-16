package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import static org.alfresco.common.Utils.testDataFolder;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Andrei.Nechita
 */
public class ActionsUploadTests extends BaseTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String user = "user1-" + random;
    //@Autowired
    private RepositoryPage repositoryPage;
    //@Autowired
    //@Autowired
    private DocumentDetailsPage documentDetailsPage;
    private String testFile = RandomData.getRandomAlphanumeric() + "-testFile-C8172-.txt";
    private String testFilePath = testDataFolder + testFile;
    private String newVersionFile = RandomData.getRandomAlphanumeric() + "-NewFile-C8175.txt";
    private DocumentLibraryPage documentLibrary;
    private CreateContentPage createContent;
    //@Autowired
    private MyFilesPage myFilesPage;
    //@Autowired
    private UploadContent uploadContent;
    private final String password = "password";
    private UserModel testUser1;
    private final String sharedFolderName = "Shared";
    String newVersionFilePath = testDataFolder + newVersionFile;
    private final String fileName = RandomData.getRandomAlphanumeric() + "Test File";

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createUser(user, password);
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingLoginPage(getAdminUser());

        repositoryPage = new RepositoryPage(webDriver);
        createContent = new CreateContentPage(webDriver);
        documentLibrary = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        repositoryPage = new RepositoryPage(webDriver);
        myFilesPage = new MyFilesPage(webDriver);
        uploadContent = new UploadContent(webDriver);

    }
    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(testUser1);
    }


    @TestRail (id = "C8172")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void uploadDocument()
    {
        log.info("Precondition: Login to share and navigate to Repository->Shared ");
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        documentLibrary
            .assertUploadButtonStatusDisabled();
        repositoryPage
            .click_FolderName(sharedFolderName);
        uploadContent
            .uploadContent(testFilePath);
        myFilesPage
            .assertIsContantNameDisplayed(testFile);

    }

    //    @Bug (id = "MNT-18059", status = Bug.Status.FIXED)
    @TestRail (id = "C8175")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void updateDocumentNewVersion() {
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        myFilesPage
            .click_CreateButton();

        log.info(" Click \"Plain Text...\" option.");
        myFilesPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(fileName);
        createContent
            .clickCreate();
        documentDetailsPage
            .clickOpenedFloder();
        myFilesPage
            .assertIsContantNameDisplayed(fileName);
        myFilesPage
            .clickOnFile(fileName);
        documentDetailsPage
            .assertDacumentNOContent();
        documentDetailsPage
            .clickOpenedFloder();
        myFilesPage
            .selectItemAction(fileName, ItemActions.UPLOAD_NEW_VERSION);
        log.info(" Update the file with major version.");
        uploadContent
            .updateDocumentVersion(newVersionFilePath,
                "comments",
                UploadContent.Version.Major);
        myFilesPage
            .assertIsContantNameDisplayed(newVersionFile);
        myFilesPage
            .assertIsContentDeleted(fileName);
        log.info(" Click on the file and check the version and contents are updated.");
        myFilesPage
            .clickOnFile(newVersionFile);
        documentDetailsPage
            .assertFileContentEquals("updated by upload new version");
        documentDetailsPage
            .assertVerifyFileVersion("2.0");

    }

}