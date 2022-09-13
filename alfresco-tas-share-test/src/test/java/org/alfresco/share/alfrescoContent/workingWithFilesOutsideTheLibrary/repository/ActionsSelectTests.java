package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import static org.alfresco.common.Utils.testDataFolder;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.TestGroup;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;

@Slf4j
public class ActionsSelectTests extends BaseTest
{
    private final String user = String.format("8163TestUser%s", RandomData.getRandomAlphanumeric());
    private final String testFile = RandomData.getRandomAlphanumeric() + "testFile.txt";
    private final String testFilePath = testDataFolder + testFile;
    private final String userHomeFolderName = "User Homes";

    private final String folderName = String.format("C8164testFolder%s", RandomData.getRandomAlphanumeric());
    private final String password = "password";
    private UserModel testUser1;
    private RepositoryPage repositoryPage;
    //@Autowired
    private HeaderMenuBar headerMenuBar;
    //@Autowired
    private MyFilesPage myFilesPage;
    //@Autowired
    private NewFolderDialog newFolderDialog;
    private UploadContent uploadContent;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {

        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createUser(user, password);
        getCmisApi().authenticateUser(getAdminUser());

        authenticateUsingLoginPage(getAdminUser());

        repositoryPage = new RepositoryPage(webDriver);
        headerMenuBar = new HeaderMenuBar(webDriver);
        repositoryPage = new RepositoryPage(webDriver);
        myFilesPage = new MyFilesPage(webDriver);
        uploadContent = new UploadContent(webDriver);
        newFolderDialog = new NewFolderDialog(webDriver);
    }
    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(testUser1);
        log.info("Delete the Created Folder from Admin Page");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName("User Homes");
        repositoryPage.select_ItemsAction(user, ItemActions.DELETE_FOLDER)
            .clickOnDeleteButtonOnDeletePrompt();
    }

    @TestRail (id = "C8163")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void selectFile()
    {

        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(userHomeFolderName);
        repositoryPage
            .click_FolderName(user);
        uploadContent
            .uploadContent(testFilePath);
        myFilesPage
            .assertIsContantNameDisplayed(testFile);

        log.info("STEP1: Click 'Select' button and choose 'Documents' option.");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("Documents");
        ArrayList<String> expectedContentList1 = new ArrayList<>(Collections.singletonList(testFile));
        myFilesPage
            .assertContentItemsSelected(expectedContentList1);
        headerMenuBar
            .assertSelectedItemsMenuEnabled();
        log.info("STEP2: Click 'Select' button and choose 'Invert Selection' option");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("Invert Selection");
        myFilesPage
            .assertContentItemsNotSelected(expectedContentList1);
        headerMenuBar
            .assertSelectedItemsMenuDisabled();
        log.info("STEP3: Click 'Select' button and choose 'All'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("All");
        myFilesPage
            .assertContentItemsSelected(expectedContentList1);
        headerMenuBar
            .assertSelectedItemsMenuEnabled();
        log.info("STEP4: Click 'Select' button and choose 'None'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("None");
        myFilesPage
            .assertContentItemsNotSelected(expectedContentList1);
        headerMenuBar
            .assertSelectedItemsMenuDisabled();
        log.info("STEP5: Click 'Select' button and choose 'Folders'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("Folders");
        myFilesPage
            .assertContentItemsNotSelected(expectedContentList1);
        headerMenuBar
            .assertSelectedItemsMenuDisabled();
        log.info("STEP6: Click on document checkbox");
        myFilesPage
            .clickCheckBox(testFile);
        myFilesPage
            .assertContentItemsSelected(expectedContentList1);
        headerMenuBar
            .assertSelectedItemsMenuEnabled();

    }

    @TestRail (id = "C8164")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void selectFolder()
    {

        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(userHomeFolderName);
        repositoryPage
            .click_FolderName(user);
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
        myFilesPage
            .assertContentItemsSelected(expectedContentList1);
        headerMenuBar
            .assertSelectedItemsMenuEnabled();
        log.info("STEP2: Click 'Select' button and choose 'Invert Selection' option");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("Invert Selection");
        myFilesPage
            .assertContentItemsNotSelected(expectedContentList1);
        headerMenuBar
            .assertSelectedItemsMenuDisabled();
        log.info("STEP3: Click 'Select' button and choose 'All'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("All");
        myFilesPage
            .assertContentItemsSelected(expectedContentList1);
        headerMenuBar
            .assertSelectedItemsMenuEnabled();
        log.info("STEP4: Click 'Select' button and choose 'None'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("None");
        myFilesPage
            .assertContentItemsNotSelected(expectedContentList1);
        headerMenuBar
            .assertSelectedItemsMenuDisabled();
        log.info("STEP5: Click 'Select' button and choose 'Documents' option.");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption("Documents");
        myFilesPage
            .assertContentItemsNotSelected(expectedContentList1);
        headerMenuBar
            .assertSelectedItemsMenuDisabled();
        log.info("STEP6: Click on folder checkbox");
        myFilesPage
            .clickCheckBox(folderName);
        myFilesPage
            .assertContentItemsSelected(expectedContentList1);
        headerMenuBar
            .assertSelectedItemsMenuEnabled();

    }

}