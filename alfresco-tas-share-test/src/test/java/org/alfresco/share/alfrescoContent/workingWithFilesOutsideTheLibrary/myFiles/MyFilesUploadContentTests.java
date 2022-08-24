package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import static org.alfresco.common.Utils.testDataFolder;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
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
 * @author Razvan.Dorobantu
 */
public class MyFilesUploadContentTests extends BaseTest
{
    //@Autowired
    private MyFilesPage myFilesPage;
    //@Autowired
    private DocumentDetailsPage documentDetailsPage;
    //@Autowired
    private UploadContent uploadContent;
    private String testFile = RandomData.getRandomAlphanumeric() + "testFile.txt";
    private String testFilePath = testDataFolder + testFile;
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
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }
    @TestRail (id = "C7651")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesUploadDocument()
    {
        log.info("STEP1: On the My Files page upload a file.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        uploadContent
            .uploadContent(testFilePath);
        myFilesPage
            .assertIsContantNameDisplayed(testFile);
    }

    //    @Bug (id = "MNT-18059", status = Bug.Status.FIXED)
    @TestRail (id = "C7792")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesUpdateDocumentNewVersion() throws InterruptedException {
        String testFile = RandomData.getRandomAlphanumeric() + "testFile.txt";
        String newVersionFile = RandomData.getRandomAlphanumeric() + "newVersionFile.txt";
        String testFilePath = testDataFolder + testFile;
        String newVersionFilePath = testDataFolder + newVersionFile;
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        uploadContent
            .uploadContent(testFilePath);
        myFilesPage
            .assertIsContantNameDisplayed(testFile);
        log.info("STEP1: Click on the file and check contents.");
        myFilesPage.clickOnFile(testFile);
        documentDetailsPage
            .assertFileContentEquals("contents");
        log.info("STEP2: Navigate back to My Files page and click on upload new version for the file.");
        myFilesPage
            .navigate();
        Thread.sleep(5000);
        myFilesPage
            .selectItemAction(testFile, ItemActions.UPLOAD_NEW_VERSION);
        log.info("STEP3: Update the file with major version.");
        uploadContent
            .updateDocumentVersion(newVersionFilePath,
                "comments",
                UploadContent.Version.Major);
        myFilesPage
            .assertIsContantNameDisplayed(newVersionFile);
        myFilesPage
            .assertIsContentDeleted(testFile);
        log.info("STEP4: Click on the file and check the version and contents are updated.");
        myFilesPage
            .clickOnFile(newVersionFile);
        documentDetailsPage
            .assertFileContentEquals("updated by upload new version");
        documentDetailsPage
            .assertVerifyFileVersion("2.0");
    }

}
