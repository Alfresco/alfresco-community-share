package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.constants.ShareGroups;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.DocumentLibraryPage.CreateMenuOption;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.alfresco.common.Utils.testDataFolder;

@Slf4j
public class MyFilesTests extends BaseTest
{
    private final String user1 = String.format("C7648TestUser%s", RandomData.getRandomAlphanumeric());
    private final String user2 = String.format("C7648TestUser1%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("C7658SiteName%s", RandomData.getRandomAlphanumeric());
    private final String testFile = RandomData.getRandomAlphanumeric() + "testFile.txt";
    private final String testFilePath = testDataFolder + testFile;
    private final String C7648name = "C7648 name";
    private final String C7648title = "C7648 title";
    private final String C7648content = "C7648 content";
    private final String C7648description = "C7648 description";
    private final String password = "password";
    private MyFilesPage myFilesPage;
    private CreateContentPage create;
    private UserDashboardPage userDashboard;
    private Toolbar toolbar;
    private UploadContent uploadContent;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        Assert.assertNotNull(getDataUser(), "DataUser is not initialized");
        Assert.assertNotNull(getDataUser().usingAdmin(), "Admin data user context is not available");

        toolbar = new Toolbar(webDriver);
        userDashboard = new UserDashboardPage(webDriver);
        myFilesPage = new MyFilesPage(webDriver);
        create = new CreateContentPage(webDriver);
        uploadContent = new UploadContent(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }

    @TestRail (id = "C7648")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesContentAvailability() {

        log.info("PreCondition: Two users are created via API, e.g: user1 and user2");
        UserModel testuser1 = dataUser.usingAdmin().createUser(user1, password);
        UserModel testuser2 = dataUser.usingAdmin().createUser(user2, password);

        log.info("Step 1: Login with user1 and create Plain Text document in MyFiles");
        authenticateUsingLoginPage(testuser1);

        myFilesPage
            .navigate();
        myFilesPage
            .click_CreateButton()
            .click_CreateFromTemplateOption(CreateMenuOption.PLAIN_TEXT);
        create
            .typeName(C7648name)
            .typeContent(C7648content)
            .typeTitle(C7648title)
            .typeDescription(C7648description)
            .clickCreate();
        myFilesPage
            .navigate();
        Assert.assertTrue(myFilesPage.isFileNameDisplayed(C7648name), "C7648 name is not displayed in My Files");

        log.info("Step 2: Login with user2 and check that the file created by user1 is not visible in My Files");
        authenticateUsingLoginPage(testuser2);

        myFilesPage
            .navigate();
        uploadContent
            .uploadContent(testFilePath);
        myFilesPage
            .navigate();
        Assert.assertFalse(myFilesPage.isFileNameDisplayed(C7648name),"C7648 name is displayed in My Files");

        deleteUsersIfNotNull(testuser1);
        deleteUsersIfNotNull(testuser2);
    }

    @TestRail (id = "C7658")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, ShareGroups.SHARE_PRIORITY_1})
    public void verifyPresenceOfMyFilesInHeaderBar() {
        log.info("PreCondition: Creating a TestUser");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingCookies(user.get());

        log.info("Step 1: Check that the My Files link is available in the toolbar");
        userDashboard
            .navigate(siteName);
        toolbar
            .assertMyFilesIsDisplayed();

        log.info("Step 2: Access the My Files via link in toolbar");
        toolbar
            .clickMyFiles();
        myFilesPage
            .assertBrowserPageTitleIs("Alfresco » My Files");
    }
}