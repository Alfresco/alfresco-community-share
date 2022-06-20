package org.alfresco.share.site.members;

import static org.alfresco.po.enums.GroupRoles.COLLABORATOR;
import static org.alfresco.po.enums.GroupRoles.CONSUMER;
import static org.alfresco.po.enums.GroupRoles.MANAGER;
import static org.alfresco.utility.constants.UserRole.SiteCollaborator;
import static org.alfresco.utility.constants.UserRole.SiteConsumer;

import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.DataUser.ListUserWithRoles;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ReviewingSiteMembersTest extends BaseTest
{
    private final String EMPTY_SPACE = " ";
    private final String USER_PROFILE_PAGE_PAGE_TITLE = "userProfilePage.title";

    private SiteUsersPage siteUsersPage;

    private final ThreadLocal<UserModel> userModel = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userModel.set(dataUser.createRandomTestUser());
        siteModel.set(dataSite.usingUser(userModel.get()).createPublicRandomSite());

        authenticateUsingCookies(userModel.get());

        siteUsersPage = new SiteUsersPage(webDriver);
    }

    @TestRail(id = "C2816")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayAllSiteMembers()
    {
        ListUserWithRoles userRoles = getDataUser()
            .addUsersWithRolesToSite(siteModel.get(), SiteCollaborator);
        UserModel collaborator = userRoles.getOneUserWithRole(SiteCollaborator);

        String managerFullName = userModel.get().getFirstName().concat(EMPTY_SPACE).concat(userModel.get().getLastName());
        String collaboratorFullName = collaborator.getFirstName().concat(EMPTY_SPACE).concat(collaborator.getLastName());

        siteUsersPage
            .navigate(siteModel.get())
            .assertSiteMemberNameEqualsTo(managerFullName)
            .assertSiteMemberNameEqualsTo(collaboratorFullName);

        siteUsersPage
            .assertSelectedRoleEqualsTo(MANAGER.getValue(), managerFullName)
            .assertSelectedRoleEqualsTo(COLLABORATOR.getValue(), collaboratorFullName);

        siteUsersPage
            .assertSiteMemberRemoveButtonIsEnabled(managerFullName)
            .assertSiteMemberRemoveButtonIsEnabled(collaboratorFullName);

        siteUsersPage
            .navigateToUserProfilePage(collaboratorFullName)
            .assertBrowserPageTitleIs(language.translate(USER_PROFILE_PAGE_PAGE_TITLE));
    }

    @TestRail(id = "C2818")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayConsumerFullNameWhenSearch()
    {
        ListUserWithRoles userRoles = getDataUser()
            .addUsersWithRolesToSite(siteModel.get(), SiteConsumer);
        UserModel consumer = userRoles.getOneUserWithRole(SiteConsumer);

        String consumerFullName = consumer.getFirstName().concat(EMPTY_SPACE).concat(consumer.getLastName());

        siteUsersPage.navigate(siteModel.get());
        siteUsersPage.searchUserWithName(consumerFullName);

        siteUsersPage
            .assertSelectedRoleEqualsTo(CONSUMER.getValue(), consumerFullName)
            .assertSiteMemberRemoveButtonIsEnabled(consumerFullName);

        siteUsersPage
            .navigateToUserProfilePage(consumerFullName)
            .assertBrowserPageTitleIs(language.translate(USER_PROFILE_PAGE_PAGE_TITLE));
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(userModel.get());
        deleteSitesIfNotNull(siteModel.get());
    }
}
