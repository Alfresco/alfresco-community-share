package org.alfresco.share.adminTools.alfrescoPowerUsers;

import static org.alfresco.share.TestUtils.ALFRESCO_SITE_ADMINISTRATORS;

import org.alfresco.dataprep.SiteService.Visibility;
import org.alfresco.po.share.SystemErrorPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class SitesManagerTests extends BaseTest
{
    private final ThreadLocal<UserModel> siteAdmin = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> testSite = new ThreadLocal<>();

    private SitesManagerPage sitesManagerPage;
    private SiteDashboardPage siteDashboardPage;
    private SystemErrorPage systemErrorPage;
    private Toolbar toolbar;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteAdmin.set(getDataUser().createRandomTestUser());
        getDataGroup().usingUser(siteAdmin.get()).addUserToGroup(ALFRESCO_SITE_ADMINISTRATORS);

        siteDashboardPage = new SiteDashboardPage(webDriver);
        sitesManagerPage = new SitesManagerPage(webDriver);
        systemErrorPage = new SystemErrorPage(webDriver);
        toolbar = new Toolbar(webDriver);
    }

    @TestRail (id = "C8674")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifySiteManagerPage()
    {
        String siteDescription = "Site Description";
        SiteModel site = new SiteModel(RandomData.getRandomName("siteModerated"));
        site.setDescription(siteDescription);
        site.setVisibility(Visibility.MODERATED);
        testSite.set(site);
        getDataSite().usingUser(siteAdmin.get()).createSite(testSite.get());

        authenticateUsingLoginPage(siteAdmin.get());
        sitesManagerPage.navigate()
            .assertSiteManagerPageIsOpened()
            .assertBrowserPageTitleIs(language.translate("adminTools.sitesManager.browser.pageTitle"))
            .assertTableHasAllColumns()
            .usingSite(testSite.get())
            .assertSiteDescriptionEqualsTo(siteDescription)
            .assertSiteVisibilityEquals(Visibility.MODERATED)
            .assertSiteManagerIsYes();

        getDataSite().usingUser(siteAdmin.get()).deleteSite(site);
    }

    @TestRail (id = "C8675")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void updateSiteVisibilityToPublic()
    {
        testSite.set(getDataSite().usingUser(siteAdmin.get()).createModeratedRandomSite());

        authenticateUsingLoginPage(siteAdmin.get());
        sitesManagerPage.navigate().usingSite(testSite.get())
            .changeSiteVisibility(Visibility.PUBLIC)
            .assertSiteVisibilityEquals(Visibility.PUBLIC)
            .assertSuccessIndicatorIsDisplayed();
        siteDashboardPage.navigate(testSite.get()).assertSiteVisibilityIs(Visibility.PUBLIC);
        getDataSite().usingUser(siteAdmin.get()).deleteSite(testSite.get());
    }

    @TestRail (id = "C8676")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void updateSiteVisibilityToModerated()
    {
        testSite.set(getDataSite().usingUser(siteAdmin.get()).createPrivateRandomSite());

        authenticateUsingLoginPage(siteAdmin.get());
        sitesManagerPage.navigate().usingSite(testSite.get())
            .changeSiteVisibility(Visibility.MODERATED)
            .assertSiteVisibilityEquals(Visibility.MODERATED)
            .assertSuccessIndicatorIsDisplayed();
        siteDashboardPage.navigate(testSite.get()).assertSiteVisibilityIs(Visibility.MODERATED);
        getDataSite().usingUser(siteAdmin.get()).deleteSite(testSite.get());
    }

    @TestRail (id = "C8680")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void updateSiteVisibilityToPrivate()
    {
        testSite.set(getDataSite().usingUser(siteAdmin.get()).createPublicRandomSite());

        authenticateUsingLoginPage(siteAdmin.get());
        sitesManagerPage.navigate().usingSite(testSite.get())
            .changeSiteVisibility(Visibility.PRIVATE)
            .assertSiteVisibilityEquals(Visibility.PRIVATE)
            .assertSuccessIndicatorIsDisplayed();
        siteDashboardPage.navigate(testSite.get()).assertSiteVisibilityIs(Visibility.PRIVATE);
        getDataSite().usingUser(siteAdmin.get()).deleteSite(testSite.get());
    }

    @TestRail (id = "C8683, C8682, C2868")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION })
    public void verifyUserAddedAndRemovedFromSiteAdminGroup()
    {
        UserModel user = dataUser.usingAdmin().createRandomTestUser();
        dataGroup.usingUser(user).addUserToGroup(ALFRESCO_SITE_ADMINISTRATORS);

        authenticateUsingLoginPage(user);
        userDashboardPage.navigate(user);
        toolbar.assertSitesManagerIsDisplayed().clickSitesManager().assertSiteManagerPageIsOpened();
        dataGroup.removeUserFromGroup(ALFRESCO_SITE_ADMINISTRATORS, user);
        authenticateUsingLoginPage(user);
        toolbar.assertSitesManagerIsNotDisplayed();

        dataUser.usingAdmin().deleteUser(user);
    }

    @TestRail (id = "C8689")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void siteAdminBecomeSitesManager()
    {
        SiteModel adminSite = getDataSite().usingAdmin().createPublicRandomSite();

        authenticateUsingLoginPage(siteAdmin.get());
        sitesManagerPage.navigate()
            .usingSite(adminSite)
            .becomeSiteManager();
        sitesManagerPage.usingSite(adminSite).assertSiteManagerIsYes();

        getDataSite().usingAdmin().deleteSite(adminSite);
    }

    @TestRail (id = "C8696")
    @Test (enabled = false, groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteSiteAsSiteAdmin()
    {
        testSite.set(getDataSite().usingUser(siteAdmin.get()).createPublicRandomSite());

        authenticateUsingLoginPage(siteAdmin.get());
        sitesManagerPage.navigate().usingSite(testSite.get())
            .clickDelete()
            .assertConfirmMessageFromSiteManagerIsCorrect(testSite.get().getTitle())
            .clickDeleteFromSitesManager();
        sitesManagerPage
            .navigate()
            .usingSite(testSite.get()).assertSiteIsNotDisplayed();
        siteDashboardPage.navigateWithoutRender(testSite.get());
        systemErrorPage.assertSomethingIsWrongWithThePageMessageIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUp()
    {
        deleteUsersIfNotNull(siteAdmin.get());
        deleteSitesIfNotNull(testSite.get());
    }
}