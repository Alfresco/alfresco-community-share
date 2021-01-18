package org.alfresco.share.userDashboard;

import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SettingHomePageTest extends ContextAwareWebTest
{
    //@Autowired
    private SharedFilesPage sharedFilesPage;

    //@Autowired
    private PeopleFinderPage peopleFinderPage;

    //@Autowired
    private UserProfilePage userProfilePage;

    private UserModel user;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(user);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C2858")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void useCurrentPage()
    {
        userProfilePage.navigate(user);
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
        
        userDashboard.assertUserDashboardPageIsOpened();
    }
}
