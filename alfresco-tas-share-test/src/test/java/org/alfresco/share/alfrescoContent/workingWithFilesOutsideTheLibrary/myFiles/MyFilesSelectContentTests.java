package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.ContentService;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
@Slf4j
/**
 * @author Razvan.Dorobantu
 */
public class MyFilesSelectContentTests extends BaseTest
{
    private final String testFile = RandomData.getRandomAlphanumeric() + "testFile.txt";
    private final String testFilePath = testDataFolder + testFile;
    private final String folderName = String.format("testFolder%s", RandomData.getRandomAlphanumeric());
    private final String testFile1 = RandomData.getRandomAlphanumeric() + "testFile1.txt";
    private final String testFilePath1 = testDataFolder + testFile1;
    private final String testFile2 = RandomData.getRandomAlphanumeric() + "testFile2.txt";
    private final String testFilePath2 = testDataFolder + testFile2;
    private String myFilesPath;
    protected ContentService contentService;
    //@Autowired
    private MyFilesPage myFilesPage;
    private NewFolderDialog newFolderDialog;
    private UploadContent uploadContent;
    //@Autowired
    private HeaderMenuBar headerMenuBar;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    @BeforeMethod (alwaysRun = true)
    public void createUser()
    {
        log.info("PreCondition: Creating a TestUser");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingCookies(user.get());

        myFilesPage = new MyFilesPage(webDriver);
        headerMenuBar = new HeaderMenuBar(webDriver);
        uploadContent = new UploadContent(webDriver);
        newFolderDialog = new NewFolderDialog(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }

    @TestRail (id = "C7682")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void selectFileByMenu()
    {
        log.info("Precondition: Login as user, navigate to My Files page and upload a file.");
        myFilesPage
            .navigate();
        uploadContent
            .uploadContent(testFilePath);
        myFilesPage
            .assertIsContantNameDisplayed(testFile);

        log.info("STEP1: Click 'Select' button and choose 'Documents' option.");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("Documents");
        ArrayList<String> expectedContentList1 = new ArrayList<>(Collections.singletonList(testFile));
        assertEquals(myFilesPage.verifyContentItemsSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is disabled.");

        log.info("STEP2: Click 'Select' button and choose 'Invert Selection' option");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("Invert Selection");
        assertEquals(myFilesPage.verifyContentItemsNotSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        Assert.assertFalse(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        log.info("STEP3: Click 'Select' button and choose 'All'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("All");
        assertEquals(myFilesPage.verifyContentItemsSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is disabled.");

        log.info("STEP4: Click 'Select' button and choose 'None'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("None");
        assertEquals(myFilesPage.verifyContentItemsNotSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        Assert.assertFalse(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        log.info("STEP5: Click 'Select' button and choose 'Folders'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("Folders");
        assertEquals(myFilesPage.verifyContentItemsNotSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        Assert.assertFalse(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        log.info("STEP6: Click on document checkbox");
        myFilesPage
            .clickCheckBox(testFile);
        assertEquals(myFilesPage.verifyContentItemsSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");
    }

    @TestRail (id = "C7683")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void selectFolderByMenu()
    {
        log.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        myFilesPage
            .click_CreateButton()
            .click_FolderLink();
        newFolderDialog
            .typeName(folderName)
            .clickSave();
        myFilesPage
            .assertIsContantNameDisplayed(folderName);

        log.info("STEP1: Click 'Select' button and choose 'Folders'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("Folders");
        ArrayList<String> expectedContentList1 = new ArrayList<>(Collections.singletonList(folderName));
        assertEquals(myFilesPage.verifyContentItemsSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is disabled.");

        log.info("STEP2: Click 'Select' button and choose 'Invert Selection' option");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("Invert Selection");
        assertEquals(myFilesPage.verifyContentItemsNotSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        Assert.assertFalse(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        log.info("STEP3: Click 'Select' button and choose 'All'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("All");
        assertEquals(myFilesPage.verifyContentItemsSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is disabled.");

        log.info("STEP4: Click 'Select' button and choose 'None'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("None");
        assertEquals(myFilesPage.verifyContentItemsNotSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        Assert.assertFalse(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        log.info("STEP5: Click 'Select' button and choose 'Documents' option.");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("Documents");
        assertEquals(myFilesPage.verifyContentItemsNotSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        Assert.assertFalse(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        log.info("STEP6: Click on folder checkbox");
        myFilesPage
            .clickCheckBox(folderName);
        assertEquals(myFilesPage.verifyContentItemsSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");
    }

    @TestRail(id = "XAT-10434")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT, "DownloadTest" })
    public void performingActionsOnSeveralSelectedItemsCopyTo()
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

        log.info("Step4: From drop down select Copy To");
        myFilesPage.clickSelectedItemsAction("Copy to...");

        log.info("Step4: From Copy To screen click target folder i.e.,Shared files");
        myFilesPage.clickSharedFiles().clickCopy();

        log.info("Step5: verify content copy available in Shared files");
        myFilesPage
            .clickSharedFilesFromHeaderMenu()
            .assertIsContantNameDisplayed(testFile1)
            .assertIsContantNameDisplayed(testFile2);




    }
}
