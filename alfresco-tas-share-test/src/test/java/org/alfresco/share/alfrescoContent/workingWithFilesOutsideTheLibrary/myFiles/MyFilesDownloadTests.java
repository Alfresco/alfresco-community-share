package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import static org.alfresco.common.Utils.isFileInDirectory;
import static org.alfresco.common.Utils.testDataFolder;

import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;

import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
@Slf4j
public class MyFilesDownloadTests extends BaseTest
{
    // @Autowired
    private       MyFilesPage            myFilesPage;
    //@Autowired
    private       NewFolderDialog        newFolderDialog;
    private       UploadContent          uploadContent;
    private final String                 testFile      = RandomData.getRandomAlphanumeric() + "testFile.txt";
    private final String                 testFilePath  = testDataFolder + testFile;
    private final String                 testFile1     = RandomData.getRandomAlphanumeric() + "testFile1.txt";
    private final String                 testFilePath1 = testDataFolder + testFile1;
    private final String                 testFile2     = RandomData.getRandomAlphanumeric() + "testFile2.txt";
    private final String                 testFilePath2 = testDataFolder + testFile2;
    private final ThreadLocal<UserModel> user          = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site          = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("PreCondition: Creating a TestUser");
        user.set(getDataUser().usingAdmin()
                     .createRandomTestUser());
        site.set(getDataSite().usingUser(user.get())
                     .createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingCookies(user.get());

        myFilesPage = new MyFilesPage(webDriver);
        uploadContent = new UploadContent(webDriver);
        newFolderDialog = new NewFolderDialog(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }

    @TestRail(id = "C7799")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesDownloadFileFromAlfresco()
    {
        log.info("Precondition: Navigate to My Files page and upload a file.");
        myFilesPage.navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        uploadContent.uploadContent(testFilePath);
        myFilesPage.assertIsContantNameDisplayed(testFile);

        log.info("Step 1: Mouse over file, click Download");
        myFilesPage.selectItemActionFormFirstThreeAvailableOptions(testFile, ItemActions.DOWNLOAD);
        myFilesPage.acceptAlertIfDisplayed();

        log.info("Step 2: Check the file was saved locally");
        Assert.assertTrue(isFileInDirectory(testFile, null), "The file was not found in the specified location");
    }

    @TestRail(id = "C7802")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void downloadFolder()
    {
        log.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        myFilesPage.navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        myFilesPage.click_CreateButton()
            .click_FolderLink();

        newFolderDialog.typeName("TestFolder")
            .typeTitle("TestTitle")
            .typeDescription("TestDescription")
            .clickSave();

        assertTrue(myFilesPage.isContentNameDisplayed("TestFolder"),
                   "TestFolder" + " displayed in My Files documents list.");

        log.info("Step 1: Mouse over folder, click Download");
        myFilesPage.selectItemActionFormFirstThreeAvailableOptions("TestFolder", ItemActions.DOWNLOAD_AS_ZIP);
        myFilesPage.acceptAlertIfDisplayed();

        log.info("Step 2: Check the folder was saved locally");
        Assert.assertTrue(isFileInDirectory("TestFolder", ".zip"),
                          "The folder was not found in the specified location");
    }

    @TestRail(id = "XAT-10433")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT, "DownloadTest" })
    public void performingActionsOnSeveralSelectedItemsDownloadAsZip()
    {
        log.info("Step1: Login as user, navigate to My Files page and create a couple of plain text files.");
        myFilesPage.navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");

        List<String> uploadTestContent = Arrays.asList(testFilePath1, testFilePath2);
        for (String uploads : uploadTestContent)
        {
            uploadContent.uploadContent(uploads);
        }

        log.info("Step2: Select Multiple Document checkboxes");
        List<String> testFiles = Arrays.asList(testFile1, testFile2);
        for (String file : testFiles)
        {
            myFilesPage.selectDocumentCheckbox(file);
        }

        log.info("Step3: click on selectedItems button");
        myFilesPage.clickSelectedItemsButton();

        log.info("Step4: From drop down select Download as ZIP");
        myFilesPage.clickSelectedItemsAction("Download as Zip");

        log.info("Step5: verify folder is displayed in specified location");
        Assert.assertTrue(isFileInDirectory("Archive", ".zip"),
                          "The zip archive was not found in the specified location");
    }
}