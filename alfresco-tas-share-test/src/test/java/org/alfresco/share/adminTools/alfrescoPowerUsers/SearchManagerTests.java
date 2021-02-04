package org.alfresco.share.adminTools.alfrescoPowerUsers;

import static org.alfresco.share.TestUtils.ALFRESCO_ADMIN_GROUP;
import static org.alfresco.share.TestUtils.ALFRESCO_SEARCH_ADMINISTRATORS;

import org.alfresco.po.share.searching.SearchManagerPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class SearchManagerTests extends BaseTest
{
    private SearchManagerPage searchManagerPage;

    private ThreadLocal<UserModel> userAdmin = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod()
    {
        searchManagerPage = new SearchManagerPage(webDriver);
        userAdmin.set(getDataUser().usingAdmin().createRandomTestUser());
    }

    @TestRail (id = "C8703")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void userHasSearchManagerRightsWhenAddedToAdminGroup()
    {
        setupAuthenticatedSession(userAdmin.get());
        userDashboardPage.navigate(userAdmin.get());
        toolbar.search("test").assertSearchManagerButtonIsNotDisplayed();
        dataGroup.usingUser(userAdmin.get()).addUserToGroup(ALFRESCO_ADMIN_GROUP);
        setupAuthenticatedSession(userAdmin.get());
        userDashboardPage.navigate(userAdmin.get());
        toolbar.search("test").assertSearchManagerButtonIsDisplayed()
            .clickSearchManagerLink()
                .assertBrowserPageTitleIs(language.translate("searchManager.browser.pageTitle"))
                .assertSearchManagerPageIsOpened();
    }

    @TestRail (id = "C8704, C8713")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void userHasSearchManagerRightsWhenAddedToSearchAdministratorsGroup()
    {
        String filterId = RandomData.getRandomAlphanumeric();

        setupAuthenticatedSession(userAdmin.get());
        userDashboardPage.navigate(userAdmin.get());
        toolbar.search("test").assertSearchManagerButtonIsNotDisplayed();
        dataGroup.usingUser(userAdmin.get()).addUserToGroup(ALFRESCO_SEARCH_ADMINISTRATORS);

        setupAuthenticatedSession(userAdmin.get());
        userDashboardPage.navigate(userAdmin.get());
        searchManagerPage.navigate()
            .assertBrowserPageTitleIs(language.translate("searchManager.browser.pageTitle"))
            .assertSearchManagerPageIsOpened()
            .createNewFilter()
                .typeFilterId(filterId)
                .typeFilterName(filterId)
                .clickSave();
        searchManagerPage
            .editFilterProperty(filterId, "audio:album (Album)")
            .assertFilterPropertyIs(filterId, "audio:album (Album)")
            .deleteFilter(filterId)
            .assertFilterIsNotDisplayed(filterId);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(userAdmin.get());
    }
}