package org.alfresco.share.userDashboard;

import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class SettingHomePageTest extends BaseTest
{
    private SharedFilesPage sharedFilesPage;
    private PeopleFinderPage peopleFinderPage;
    private UserProfilePage userProfilePage;
    private Toolbar toolbar;

    private final ThreadLocal<UserModel> testUser = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userProfilePage = new UserProfilePage(webDriver);
        peopleFinderPage = new PeopleFinderPage(webDriver);
        sharedFilesPage = new SharedFilesPage(webDriver);
        toolbar = new Toolbar(webDriver);

        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        authenticateUsingCookies(testUser.get());
    }

    @TestRail (id = "C2858")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void useCurrentPage()
    {
        userProfilePage.navigate(testUser.get());
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        toolbar.clickHome();
        userProfilePage.assertUserProfilePageIsOpened();

        peopleFinderPage.navigate();
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        toolbar.clickHome();
        peopleFinderPage.assertPeopleFinderPageIsOpened();
    }

    @TestRail (id = "C2859")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void useMyDashboard()
    {
        sharedFilesPage.navigate();
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        toolbar.clickHome();
        sharedFilesPage.assertSharedFilesPageIsOpened();
        toolbar.clickUserMenu().clickSetDashBoardAsHome();
        toolbar.clickHome();
        
        userDashboardPage.assertUserDashboardPageIsOpened();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(testUser.get());
    }
}
