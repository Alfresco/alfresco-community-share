/*
 * # * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site;

import java.io.IOException;
import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.test.FailedTestListener;
import org.springframework.social.alfresco.connect.exception.AlfrescoException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Site CRUD integration test.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@Test(groups = "alfresco-one")
public class SiteTest extends AbstractTest
{
    private String siteName;
    private String privateSiteName;
    private String moderatedSiteName;
    private String privateModSiteName;
    private String publicSiteNameLabel;
    private String moderatedSiteNameLabel;
    private String privateSiteNameLabel;

    DashBoardPage dashBoard;
    String testuser = "testuser" + System.currentTimeMillis();
    String testuser2 = "testuser2" + System.currentTimeMillis();

    @BeforeTest(groups = "alfresco-one")
    public void setup()
    {
        siteName = String.format("test-%d-site-crud", System.currentTimeMillis());
        privateSiteName = "private-" + siteName;
        moderatedSiteName = "mod-" + siteName;
        privateModSiteName = "privateMod-" + siteName;
        publicSiteNameLabel = "publicSiteNameLabel" + System.currentTimeMillis();
        moderatedSiteNameLabel = "moderatedSiteNameLabel" + System.currentTimeMillis();
        privateSiteNameLabel = "privateSiteNameLabel" + System.currentTimeMillis();
        // user joining the above sites
    }

    @BeforeClass(groups = "alfresco-one")
    public void loginPrep() throws Exception
    {
        createEnterpriseUser(testuser);
        dashBoard = loginAs(username, password);
    }

    @AfterClass(groups = "alfresco-one")
    public void teardown() throws Exception
    {
        try
        {
            siteUtil.deleteSite(username, password, siteName);
        }
        catch (AlfrescoException ae)
        {
            // Ignore as site has already been removed notification.
        }
        siteUtil.deleteSite(username, password, privateSiteName);
        siteUtil.deleteSite(username, password, moderatedSiteName);
        siteUtil.deleteSite(username, password, privateModSiteName);
        siteUtil.deleteSite(username, password, publicSiteNameLabel);
        siteUtil.deleteSite(username, password, moderatedSiteNameLabel);
        siteUtil.deleteSite(username, password, privateSiteNameLabel);
    }

    @BeforeMethod
    public void navigateToDash()
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
    }

    /**
     * Test Site creation.
     * 
     * @throws Exception if error
     */
    
    @Test
    public void createSite() throws Exception
    {
        // TODO: Create site option is not available for admin, admin user in Cloud. Pl run tests with other user, i.e. user1@freenet.test
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();

        // checks for site visbility help text
        Assert.assertTrue(createSite.isPublicCheckboxHelpTextDisplayed());
        Assert.assertTrue(createSite.isPrivateCheckboxHelpTextDisplayed());
        Assert.assertTrue(createSite.isModeratedCheckboxHelpTextDisplayed());

        SiteDashboardPage site = createSite.createNewSite(siteName).render();

        Assert.assertTrue(factoryPage.getPage(driver) instanceof SiteDashboardPage);

        Assert.assertTrue(siteName.equalsIgnoreCase(site.getPageTitle()));
        Assert.assertTrue(site.getSiteNav().isDashboardActive());
        Assert.assertFalse(site.getSiteNav().isDocumentLibraryActive());
        Assert.assertTrue(site.getSiteNav().isDashboardDisplayed());
        Assert.assertTrue(site.getSiteNav().isSelectSiteMembersDisplayed());
    }
    
    
    @Test(dependsOnMethods = "createSite")
    public void createDuplicateSite() throws Exception
    {
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        SharePopup errorPopup = createSite.createNewSite(siteName).render();
        try
        {
            errorPopup.handleMessage();
            Assert.fail("This is exception line");
        }
        catch (ShareException se)
        {
        }
        createSite.cancel();
    }
    

    @Test(dependsOnMethods = "createDuplicateSite")
    public void checkSiteNavigation()
    {
        SharePage sharePage = resolvePage(driver).render();
        SiteFinderPage siteFinder = sharePage.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        siteFinder = siteUtil.siteSearchRetry(driver, siteFinder, siteName);
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        DocumentLibraryPage docPage = siteDash.getSiteNav().selectDocumentLibrary().render();

        Assert.assertFalse(factoryPage.getPage(driver) instanceof SiteDashboardPage);

        Assert.assertFalse(docPage.getSiteNav().isDashboardActive());
        Assert.assertTrue(docPage.getSiteNav().isDocumentLibraryActive());
        siteDash = docPage.getSiteNav().selectSiteDashBoard().render();
        Assert.assertTrue(docPage.getSiteNav().isDashboardActive());
        Assert.assertFalse(docPage.getSiteNav().isDocumentLibraryActive());
    }
    

    @Test(dependsOnMethods = "checkSiteNavigation")
    public void searchForSiteThatDoesntExists()
    {
        SiteFinderPage siteFinder = dashBoard.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite("xyz").render();
        Assert.assertFalse(siteFinder.hasResults());
    }

    /**
     * Test site deletion.
     * 
     * @throws IOException
     * @throws Exception if error found
     */

    @Test(dependsOnMethods = { "createPrivateModerateSiteShouldYeildPrivateSite" })
    public void deleteSite() throws Exception
    {
        SiteFinderPage siteFinder = dashBoard.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        siteFinder = siteUtil.siteSearchRetry(driver, siteFinder, siteName);
        boolean hasResults = siteFinder.hasResults();
        Assert.assertTrue(hasResults);
        siteFinder = siteFinder.deleteSite(siteName).render();
        hasResults = siteFinder.hasResults();
        List<String> sites = siteFinder.getSiteList();
        Assert.assertFalse(sites.contains(siteName));
    }



    @Test(dependsOnMethods = "searchForSiteThatDoesntExists")
    public void createPrivateSite()
    {
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        SiteDashboardPage site = createSite.createPrivateSite(privateSiteName).render();
        Assert.assertTrue(privateSiteName.equalsIgnoreCase(site.getPageTitle()));
        EditSitePage siteDetails = site.getSiteNav().selectEditSite().render();

        // checks for site visbility help text
        Assert.assertTrue(createSite.isPublicCheckboxHelpTextDisplayed());
        Assert.assertTrue(createSite.isPrivateCheckboxHelpTextDisplayed());
        Assert.assertTrue(createSite.isModeratedCheckboxHelpTextDisplayed());

        Assert.assertTrue(siteDetails.isPrivate());
        Assert.assertFalse(siteDetails.isModerate());
        siteDetails.cancel();
    }

    @Test(dependsOnMethods = "createPrivateSite")
    public void createPublicModerateSite()
    {
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        SiteDashboardPage site = createSite.createModerateSite(moderatedSiteName).render();
        Assert.assertTrue(moderatedSiteName.equalsIgnoreCase(site.getPageTitle()));
        EditSitePage siteDetails = site.getSiteNav().selectEditSite().render();
        Assert.assertFalse(siteDetails.isPrivate());
        Assert.assertTrue(siteDetails.isModerate());
        siteDetails.cancel();
    }
    
    @Test(dependsOnMethods = "createPublicModerateSite")
    public void nonMemberCanJoinModeratedSite() throws Exception
    {
        logout(driver);
        dashBoard = loginAs(testuser, "password");
        SiteFinderPage siteFinder = dashBoard.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(moderatedSiteName).render();
        siteFinder = siteUtil.siteSearchRetry(driver, siteFinder, moderatedSiteName);
        SiteDashboardPage siteDashboardPage = (SiteDashboardPage)siteFinder.selectSite(moderatedSiteName).render();
        
        //Check that moderated site dashboard header with site name is displayed
        Assert.assertEquals(siteDashboardPage.getPageTitle(), moderatedSiteName);
        Assert.assertEquals(siteDashboardPage.getPageTitleLabel(), "Moderated");
        
        //Check that non-member user can request to join moderated site
        dashBoard = siteDashboardPage.getSiteNav().joinSite().render();
        Assert.assertTrue(dashBoard.getPageTitle().contains(testuser));
        Assert.assertTrue(dashBoard.getPageTitle().contains("Dashboard"));
        logout(driver);
        dashBoard = loginAs(username, password);
    }

    @Test(dependsOnMethods = "nonMemberCanJoinModeratedSite")
    public void createPrivateModerateSiteShouldYeildPrivateSite() throws Exception
    {
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        SiteDashboardPage site = createSite.createNewSite(privateModSiteName, null, true, true).render();
        Assert.assertTrue(privateModSiteName.equalsIgnoreCase(site.getPageTitle()));
        EditSitePage siteDetails = site.getSiteNav().selectEditSite().render();
        Assert.assertTrue(siteDetails.isPrivate());
        Assert.assertFalse(siteDetails.isModerate());
        siteDetails.cancel();
    }

    @Test(dependsOnMethods = "createPrivateModerateSiteShouldYeildPrivateSite")
    public void isEditingEnabled()
    {
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        Assert.assertFalse(createSite.isNameEditingDisaabled(), "Name Should be enabled for editing.");
        Assert.assertFalse(createSite.isUrlNameEditingDisaabled(), "URL Name should be enabled for editing");
        createSite.cancel();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void checkSiteNaveActiveLinkWithNull()
    {
        SiteNavigation nav = new SiteNavigation();
        nav.isLinkActive(null);
    }

    @Test(dependsOnMethods = "deleteSite")
    public void checkSetSiteName()
    {
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        createSite.setSiteName(siteName);
        Assert.assertEquals(createSite.getSiteName(), siteName);
        createSite.cancel();
    }

    @Test(dependsOnMethods = "checkSetSiteName")
    public void checkSetSiteURL()
    {
        String siteURL = siteName + "URL";
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        createSite.setSiteName(siteURL);
        Assert.assertEquals(createSite.getSiteUrl(), siteURL.toLowerCase());
        createSite.cancel();

    }

    @Test(dependsOnMethods = "checkSetSiteURL")
    public void checkPublicSiteVisibilityLabel() throws Exception
    {
        siteUtil.createSite(driver, "admin", "admin", publicSiteNameLabel, "description", "Public");
        SiteDashboardPage siteDashBoard = resolvePage(driver).render();

        // Check site visibility label
        Assert.assertEquals(siteDashBoard.getPageTitleLabel(), "Public");

        DocumentLibraryPage documentLibraryPage = siteDashBoard.getSiteNav().selectDocumentLibrary().render();
        Assert.assertEquals(documentLibraryPage.getPageTitleLabel(), "Public");

        AddUsersToSitePage addUsersToSitePage = documentLibraryPage.getSiteNav().selectAddUser().render();
        Assert.assertEquals(addUsersToSitePage.getPageTitleLabel(), "Public");

        SiteMembersPage siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
        Assert.assertEquals(siteMembersPage.getPageTitleLabel(), "Public");

        SiteGroupsPage siteGroupsPage = siteMembersPage.navigateToSiteGroups().render();
        Assert.assertEquals(siteGroupsPage.getPageTitleLabel(), "Public");

    }

    @Test(dependsOnMethods = "checkPublicSiteVisibilityLabel")
    public void checkModeratedSiteVisibilityLabel() throws Exception
    {
        siteUtil.createSite(driver, "admin", "admin", moderatedSiteNameLabel, "description", "Moderated");
        SiteDashboardPage siteDashBoard = resolvePage(driver).render();

        // Check site visibility label
        Assert.assertEquals(siteDashBoard.getPageTitleLabel(), "Moderated");

        DocumentLibraryPage documentLibraryPage = siteDashBoard.getSiteNav().selectDocumentLibrary().render();
        Assert.assertEquals(documentLibraryPage.getPageTitleLabel(), "Moderated");

        AddUsersToSitePage addUsersToSitePage = documentLibraryPage.getSiteNav().selectAddUser().render();
        Assert.assertEquals(addUsersToSitePage.getPageTitleLabel(), "Moderated");

        SiteMembersPage siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
        Assert.assertEquals(siteMembersPage.getPageTitleLabel(), "Moderated");

        SiteGroupsPage siteGroupsPage = siteMembersPage.navigateToSiteGroups().render();
        Assert.assertEquals(siteGroupsPage.getPageTitleLabel(), "Moderated");

    }

    @Test(dependsOnMethods = "checkModeratedSiteVisibilityLabel")
    public void checkPrivateSiteVisibilityLabel() throws Exception
    {
        siteUtil.createSite(driver, "admin", "admin", privateSiteNameLabel, "description", "Private");
        SiteDashboardPage siteDashBoard = resolvePage(driver).render();

        // Check site visibility label
        Assert.assertEquals(siteDashBoard.getPageTitleLabel(), "Private");

        DocumentLibraryPage documentLibraryPage = siteDashBoard.getSiteNav().selectDocumentLibrary().render();
        Assert.assertEquals(documentLibraryPage.getPageTitleLabel(), "Private");

        AddUsersToSitePage addUsersToSitePage = documentLibraryPage.getSiteNav().selectAddUser().render();
        Assert.assertEquals(addUsersToSitePage.getPageTitleLabel(), "Private");

        SiteMembersPage siteMembersPage = addUsersToSitePage.navigateToMembersSitePage().render();
        Assert.assertEquals(siteMembersPage.getPageTitleLabel(), "Private");

        SiteGroupsPage siteGroupsPage = siteMembersPage.navigateToSiteGroups().render();
        Assert.assertEquals(siteGroupsPage.getPageTitleLabel(), "Private");

    }

    // /**
    // * A 4.2 bug ALF-18320
    // * https://issues.alfresco.com/jira/browse/ALF-18320
    // * Tests SiteResultsPage by searching from a site page.
    // * @throws IOException
    // */
    // @Test(dependsOnMethods = "searchForSiteThatDoesntExists")
    // public void searchInSite() throws Exception
    // {
    // try
    // {
    // SitePage site = getSiteDashboard(siteName);
    // SiteResultsPage serchResults = site.getSearch().search("*e*").render();
    // Assert.assertNotNull(serchResults);
    // Assert.assertTrue(serchResults.getResults().isEmpty());
    // }
    // catch (Exception e)
    // {
    // saveScreenShot(drone, "SiteTest.searchInSite");
    // throw new Exception("Unable to search in site", e);
    // }
    // }
}
