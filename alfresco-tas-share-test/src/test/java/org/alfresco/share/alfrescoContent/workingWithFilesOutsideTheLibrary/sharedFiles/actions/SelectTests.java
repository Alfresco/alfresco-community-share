package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;


import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.TestGroup;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;

@Slf4j
/**
 * @author Laura.Capsa
 */
public class SelectTests extends BaseTest
{
    private final String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private final String user = "user" + uniqueIdentifier;

    //@Autowired
    private final String password = "password";
    private UserModel testUser1;
    private RepositoryPage repositoryPage;
    //@Autowired
    private HeaderMenuBar headerMenuBar;
    //@Autowired
    private MyFilesPage myFilesPage;
    //@Autowired
    private NewFolderDialog newFolderDialog;

    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private final String sharedFolderName = "Shared";
    private DocumentLibraryPage2 documentLibraryPage;
    private final String folderName ="TestFolder" + RandomData.getRandomAlphanumeric();

    String document = "Documents";
    String invertSelection = "Invert Selection";
    String all = "All";
    String none = "None";
    String folder = "Folders";
    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {

        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createUser(user, password);
        getCmisApi().authenticateUser(getAdminUser());
        site.set(getDataSite().usingUser(testUser1).createPublicRandomSite());

        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        repositoryPage = new RepositoryPage(webDriver);
        headerMenuBar = new HeaderMenuBar(webDriver);
        repositoryPage = new RepositoryPage(webDriver);
        myFilesPage = new MyFilesPage(webDriver);
        newFolderDialog = new NewFolderDialog(webDriver);
    }
    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(testUser1);
    }

    @TestRail (id = "C8004")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void selectFile()
    {
        String testFile = RandomData.getRandomAlphanumeric() + "testFile.txt";

        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);

        documentLibraryPage
            .clickCreate()
            .clickTextPlain()
            .assertCreateContentPageIsOpened()
            .typeName(testFile)
            .clickCreate();
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);

        log.info("STEP1: Click 'Select' button and choose 'Documents' option.");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption(document);
        ArrayList<String> expectedContentList1 = new ArrayList<>(Collections.singletonList(testFile));
        myFilesPage
            .assertContentItemsSelected(expectedContentList1);
        log.info("STEP2: Click 'Select' button and choose 'Invert Selection' option");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption(invertSelection);
        myFilesPage
            .assertContentItemsNotSelected(expectedContentList1);
        log.info("STEP3: Click 'Select' button and choose 'All'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption(all);
        myFilesPage
            .assertContentItemsSelected(expectedContentList1);
        headerMenuBar
            .assertSelectedItemsMenuEnabled();
        log.info("STEP4: Click 'Select' button and choose 'None'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption(none);
        myFilesPage
            .assertContentItemsNotSelected(expectedContentList1);
        log.info("STEP5: Click 'Select' button and choose 'Folders'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption(folder);
        myFilesPage
            .assertContentItemsNotSelected(expectedContentList1);
        log.info("STEP6: Click on document checkbox");
        myFilesPage
            .clickCheckBox(testFile);
        myFilesPage
            .assertContentItemsSelected(expectedContentList1);
        repositoryPage
            .select_ItemsAction(testFile, ItemActions.DELETE_DOCUMENT)
            .clickOnDeleteButtonOnDeletePrompt();

    }

    @TestRail (id = "C8005")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void selectFolder()
    {
        log.info("Precondition: create folder in Shared folder from user ");
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        myFilesPage
            .click_CreateButton()
            .click_FolderLink();
        newFolderDialog
            .typeName(folderName)
            .clickSave();
        myFilesPage
            .isContentNameDisplayed(folderName);

        log.info("STEP1: Click 'Select' button and choose 'Folders'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption(folder);
        ArrayList<String> expectedContentList1 = new ArrayList<>(Collections.singletonList(folderName));
        myFilesPage
            .assertContentItemsSelected(expectedContentList1);
        log.info("STEP2: Click 'Select' button and choose 'Invert Selection' option");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption(invertSelection);
        myFilesPage
            .assertContentItemsNotSelected(expectedContentList1);
        log.info("STEP3: Click 'Select' button and choose 'All'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption(all);
        myFilesPage
            .assertContentItemsSelected(expectedContentList1);
        log.info("STEP4: Click 'Select' button and choose 'None'");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption(none);
        myFilesPage
            .assertContentItemsNotSelected(expectedContentList1);
        log.info("STEP5: Click 'Select' button and choose 'Documents' option.");
        headerMenuBar
            .clickSelectMenu()
            .click_SelectOption(document);
        myFilesPage
            .assertContentItemsNotSelected(expectedContentList1);
        log.info("STEP6: Click on folder checkbox");
        myFilesPage
            .clickCheckBox(folderName);
        myFilesPage
            .assertContentItemsSelected(expectedContentList1);
        repositoryPage
            .select_ItemsAction(folderName, ItemActions.DELETE_FOLDER)
            .clickOnDeleteButtonOnDeletePrompt();
    }
}