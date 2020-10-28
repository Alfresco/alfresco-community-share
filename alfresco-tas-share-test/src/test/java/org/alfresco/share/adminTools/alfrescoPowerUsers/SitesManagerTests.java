package org.alfresco.share.adminTools.alfrescoPowerUsers;

import org.alfresco.dataprep.SiteService.Visibility;
import org.alfresco.po.share.SystemErrorPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;

public class SitesManagerTests extends ContextAwareWebTest
{
    private UserModel user, siteAdmin;
    private SiteModel site1, site2, site3, site4, site5, site6;
    private final String siteDescription = "Site Description";

    @Autowired
    private SitesManagerPage sitesManagerPage;

    @Autowired
    private SiteDashboardPage siteDashboardPage;

    @Autowired
    private SystemErrorPage systemErrorPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
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

        setupAuthenticatedSession(siteAdmin);
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
        try
        {
            setupAuthenticatedSession(user);
            toolbar.assertSitesManagerIsDisplayed().clickSitesManager().assertSiteManagerPageIsOpened();
            dataGroup.removeUserFromGroup(ALFRESCO_SITE_ADMINISTRATORS, user);
            setupAuthenticatedSession(user);
            toolbar.assertSitesManagerIsNotDisplayed();
        }
        finally
        {
            setupAuthenticatedSession(siteAdmin);
        }
    }

    @TestRail (id = "C8689")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void siteAdminBecomeSitesManager()
    {
        sitesManagerPage.navigate()
            .usingSite(site6).becomeSiteManager().assertSiteManagerIsYes();
    }

    @TestRail (id = "C8696")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteSiteAsSiteAdmin()
    {
        sitesManagerPage.navigate().usingSite(site5)
            .clickDelete()
            .assertConfirmMessageFromSiteManagerIsCorrect(site5.getTitle())
            .clickDeleteFromSitesManager().waiUntilLoadingMessageDisappears()
                .usingSite(site5).assertSiteIsNotDisplayed();
        navigate(String.format(properties.getShareUrl() + "/page/site/%s/dashboard", site5.getId()));
        systemErrorPage.renderedPage();
        systemErrorPage.assertSomethingIsWrongWithThePageMessageIsDisplayed();
    }
}