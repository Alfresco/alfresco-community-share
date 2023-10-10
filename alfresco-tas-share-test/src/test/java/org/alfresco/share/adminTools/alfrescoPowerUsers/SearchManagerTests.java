package org.alfresco.share.adminTools.alfrescoPowerUsers;

import static org.alfresco.share.TestUtils.ALFRESCO_ADMIN_GROUP;
import static org.alfresco.share.TestUtils.ALFRESCO_SEARCH_ADMINISTRATORS;

import org.alfresco.po.share.searching.SearchManagerPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class SearchManagerTests extends BaseTest
{
    private SearchManagerPage searchManagerPage;
    private Toolbar toolbar;
    private final ThreadLocal<UserModel> userAdmin = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod()
    {
        searchManagerPage = new SearchManagerPage(webDriver);
        toolbar = new Toolbar(webDriver);

        userAdmin.set(getDataUser().usingAdmin().createRandomTestUser());
    }

    @TestRail (id = "C8703")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void userHasSearchManagerRightsWhenAddedToAdminGroup()
    {
        authenticateUsingLoginPage(userAdmin.get());
        userDashboardPage.navigate(userAdmin.get());
        toolbar.search("test").assertSearchManagerButtonIsNotDisplayed();
        getDataGroup().usingUser(userAdmin.get()).addUserToGroup(ALFRESCO_ADMIN_GROUP);
        authenticateUsingCookies(userAdmin.get());
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
        getDataGroup().usingUser(userAdmin.get()).addUserToGroup(ALFRESCO_SEARCH_ADMINISTRATORS);

        authenticateUsingLoginPage(userAdmin.get());
        searchManagerPage.navigate()
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