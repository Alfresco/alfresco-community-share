package org.alfresco.share.adminTools.alfrescoPowerUsers;

import static java.util.Arrays.asList;
import static org.alfresco.share.TestUtils.ALFRESCO_SITE_ADMINISTRATORS;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.alfresco.common.EnvProperties;
import org.alfresco.dataprep.SiteService.Visibility;
import org.alfresco.po.share.SystemErrorPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SitesManagerTests extends BaseTest
{
    private UserModel user, siteAdmin;
    private SiteModel site1, site2, site3, site4, site5, site6;
    private final String siteDescription = "Site Description";

    private SitesManagerPage sitesManagerPage;
    private SiteDashboardPage siteDashboardPage;
    private SystemErrorPage systemErrorPage;

    @Autowired
    private EnvProperties properties;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteDashboardPage = new SiteDashboardPage(browser);
        sitesManagerPage = new SitesManagerPage(browser);
        systemErrorPage = new SystemErrorPage(browser);
    }

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        user = dataUser.createRandomTestUser();
        siteAdmin = dataUser.createRandomTestUser();

        dataGroup.usingUser(siteAdmin).addUserToGroup(ALFRESCO_SITE_ADMINISTRATORS);
        dataGroup.usingUser(user).addUserToGroup(ALFRESCO_SITE_ADMINISTRATORS);
        site1 = new SiteModel(RandomData.getRandomName("siteModerated"));
        site1.setDescription(siteDescription);
        site1.setVisibility(Visibility.MODERATED);
        dataSite.usingUser(siteAdmin).createSite(site1);
        site2 = dataSite.usingUser(siteAdmin).createModeratedRandomSite();
        site3 = dataSite.usingUser(siteAdmin).createPrivateRandomSite();
        site4 = dataSite.usingUser(siteAdmin).createPublicRandomSite();
        site5 = dataSite.usingUser(siteAdmin).createPublicRandomSite();
        site6 = dataSite.usingAdmin().createPublicRandomSite();
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user, siteAdmin);
        asList(site1, site2, site3, site4, site5, site6)
            .forEach(site -> dataSite.usingAdmin().deleteSite(site));
    }

    @TestRail (id = "C8674")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifySiteManagerPage()
    {
        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate()
            .assertSiteManagerPageIsOpened()
            .assertBrowserPageTitleIs(language.translate("adminTools.sitesManager.browser.pageTitle"))
            .assertTableHasAllColumns()
            .usingSite(site1)
                .assertSiteDescriptionIs(siteDescription)
                .assertSiteVisibilityEquals(Visibility.MODERATED)
                .assertSiteManagerIsYes();
    }

    @TestRail (id = "C8675")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void updateSiteVisibilityToPublic()
    {
        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate().usingSite(site2)
            .changeSiteVisibility(Visibility.PUBLIC)
            .assertSuccessIndicatorIsDisplayed()
            .assertSiteVisibilityEquals(Visibility.PUBLIC);
        siteDashboardPage.navigate(site2).assertSiteVisibilityIs(Visibility.PUBLIC);
    }

    @TestRail (id = "C8676")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void updateSiteVisibilityToModerated()
    {
        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate().usingSite(site3)
            .changeSiteVisibility(Visibility.MODERATED)
            .assertSuccessIndicatorIsDisplayed()
            .assertSiteVisibilityEquals(Visibility.MODERATED);
        siteDashboardPage.navigate(site3).assertSiteVisibilityIs(Visibility.MODERATED);
    }

    @TestRail (id = "C8680")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void updateSiteVisibilityToPrivate()
    {
        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate().usingSite(site4)
            .changeSiteVisibility(Visibility.PRIVATE)
            .assertSuccessIndicatorIsDisplayed()
            .assertSiteVisibilityEquals(Visibility.PRIVATE);
        siteDashboardPage.navigate(site4).assertSiteVisibilityIs(Visibility.PRIVATE);
    }

    @TestRail (id = "C8683, C8682, C2868")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifyUserAddedAndRemovedFromSiteAdminGroup()
    {
        setupAuthenticatedSession(user);
        userDashboardPage.navigate(user);
        toolbar.assertSitesManagerIsDisplayed().clickSitesManager().assertSiteManagerPageIsOpened();
        dataGroup.removeUserFromGroup(ALFRESCO_SITE_ADMINISTRATORS, user);
        setupAuthenticatedSession(user);
        toolbar.assertSitesManagerIsNotDisplayed();
    }

    @TestRail (id = "C8689")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void siteAdminBecomeSitesManager()
    {
        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate()
            .usingSite(site6).becomeSiteManager().assertSiteManagerIsYes();
    }

    @TestRail (id = "C8696")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteSiteAsSiteAdmin()
    {
        setupAuthenticatedSessionViaLoginPage(siteAdmin);
        sitesManagerPage.navigate().usingSite(site5)
            .clickDelete()
            .assertConfirmMessageFromSiteManagerIsCorrect(site5.getTitle())
            .clickDeleteFromSitesManager();
        sitesManagerPage.waiUntilLoadingMessageDisappears()
            .usingSite(site5).assertSiteIsNotDisplayed();
        siteDashboardPage.navigateWithoutRender(site5);
        systemErrorPage.renderedPage();
        systemErrorPage.assertSomethingIsWrongWithThePageMessageIsDisplayed();
    }
}