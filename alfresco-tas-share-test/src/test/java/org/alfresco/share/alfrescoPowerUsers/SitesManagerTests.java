package org.alfresco.share.alfrescoPowerUsers;

import org.alfresco.dataprep.SiteService.Visibility;
import org.alfresco.po.share.SystemErrorPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
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

/**
 * @author Laura.Capsa
 */
public class SitesManagerTests extends ContextAwareWebTest
{
    private UserModel user2, user3, user4, siteAdmin;
    private SiteModel site1, site2, site3, site4, site5, site6;
    private final String siteDescription = "Site Description";

    @Autowired
    private AdminToolsPage adminToolsPage;

    @Autowired
    private SitesManagerPage sitesManagerPage;

    @Autowired
    private SiteDashboardPage siteDashboardPage;

    @Autowired
    private SystemErrorPage systemErrorPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user2 = dataUser.createRandomTestUser();
        user3 = dataUser.createRandomTestUser();
        user4 = dataUser.createRandomTestUser();
        siteAdmin = dataUser.createRandomTestUser();

        dataGroup.usingUser(siteAdmin).addUserToGroup(ALFRESCO_SITE_ADMINISTRATORS);
        dataGroup.usingUser(user4).addUserToGroup(ALFRESCO_SITE_ADMINISTRATORS);
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
        removeUserFromAlfresco(user2, user3, user4, siteAdmin);
        asList(site1, site2, site3, site4, site5, site6)
            .forEach(site -> dataSite.usingAdmin().deleteSite(site));
    }

    @TestRail (id = "C8674")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifySiteManagerPage()
    {
        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate()
            .assertSiteManagerPageIsOpened()
            .assertPageTitleIs(language.translate("adminTools.sitesManager.pageTitle"))
            .assertTableHasAllColumns()
            .usingSite(site1)
                .assertSiteDescriptionIs(siteDescription)
                .assertSiteVisibilityIs(Visibility.MODERATED)
                .assertSiteManagerIsYes();
    }

    @TestRail (id = "C8675")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void updateSiteVisibilityToPublic()
    {
        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate().usingSite(site2)
            .changeSiteVisibility(Visibility.PUBLIC)
            .assertSuccessIndicatorIsDisplayed()
            .assertSiteVisibilityIs(Visibility.PUBLIC);
        siteDashboardPage.navigate(site2).assertSiteVisibilityIs(Visibility.PUBLIC);
    }

    @TestRail (id = "C8676")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void updateSiteVisibilityToModerated()
    {
        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate().usingSite(site3)
            .changeSiteVisibility(Visibility.MODERATED)
            .assertSuccessIndicatorIsDisplayed()
            .assertSiteVisibilityIs(Visibility.MODERATED);
        siteDashboardPage.navigate(site3).assertSiteVisibilityIs(Visibility.MODERATED);
    }

    @TestRail (id = "C8680")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void updateSiteVisibilityToPrivate()
    {
        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate().usingSite(site4)
            .changeSiteVisibility(Visibility.PRIVATE)
            .assertSuccessIndicatorIsDisplayed()
            .assertSiteVisibilityIs(Visibility.PRIVATE);
        siteDashboardPage.navigate(site4).assertSiteVisibilityIs(Visibility.PRIVATE);
    }

    @TestRail (id = "C8681")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyUserIsAddedToAlfrescoAdminGroup()
    {
        setupAuthenticatedSession(user2);
        toolbar.assertAdminToolsIsNotDisplayed();

        dataGroup.usingUser(user2).addUserToGroup(ALFRESCO_ADMIN_GROUP);
        setupAuthenticatedSession(user2);
        toolbar.assertAdminToolsIsDisplayed().clickAdminTools();
        adminToolsPage.navigateToNodeFromToolsPanel(language.translate("adminTools.sitesManagerNode"), sitesManagerPage);
        sitesManagerPage.assertSiteManagerPageIsOpened();
    }

    @TestRail (id = "C8682, C2868" )
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyUserIsAddedToSiteAdminGroup()
    {
        setupAuthenticatedSession(user3);
        toolbar.assertSitesManagerIsNotDisplayed();

        dataGroup.usingUser(user3).addUserToGroup(ALFRESCO_SITE_ADMINISTRATORS);
        setupAuthenticatedSession(user3);
        toolbar.assertSitesManagerIsDisplayed()
            .clickSitesManager().assertSiteManagerPageIsOpened();
    }

    @TestRail (id = "C8683")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void removeUserFromSiteAdmin()
    {
        setupAuthenticatedSession(user4);
        toolbar.assertSitesManagerIsDisplayed().clickSitesManager().assertSiteManagerPageIsOpened();
        dataGroup.removeUserFromGroup(ALFRESCO_SITE_ADMINISTRATORS, user4);
        setupAuthenticatedSession(user4);
        toolbar.assertSitesManagerIsNotDisplayed();
    }

    @TestRail (id = "C8689")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void siteAdminBecomeSitesManager()
    {
        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate()
            .usingSite(site6).becomeSiteManager().assertSiteManagerIsYes();
    }

    @TestRail (id = "C8696")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void deleteSiteAsSiteAdmin()
    {
        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate();

        sitesManagerPage.usingSite(site5).clickDelete()
            .assertConfirmMessageFromSiteManagerIsCorrect(site5.getTitle())
            .clickDeleteFromSitesManager().waitForLoadingMessageToDisappear()
                .usingSite(site5).assertSiteIsNotDisplayed();
        navigate(String.format(properties.getShareUrl() + "/page/site/%s/dashboard", site5.getId()));
        systemErrorPage.renderedPage();
        systemErrorPage.assertSomethingIsWrongWithThePageMessageIsDisplayed();
    }
}