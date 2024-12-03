package org.alfresco.share.sitesFeatures.Links;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.link.LinkPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */
@Slf4j
public class AccessingLinkTests extends BaseTest
{
    private final ThreadLocal<UserModel> user1    = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    @Autowired
    SitePagesService sitePagesService;
    @Autowired
    SiteService      siteService;
    DateTime currentDate;
    private LinkPage          linkPage;
    private SiteDashboardPage siteDashboardPage;
    private CustomizeSitePage customizeSitePage;
    private List<String>      tags     = new ArrayList<>();
    private String            password = "password";

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user1.set(getDataUser().usingAdmin()
                      .createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(user1.get())
                         .createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());
        siteService.addPageToSite(user1.get()
                                      .getUsername(), user1.get()
                                      .getPassword(), siteName.get()
                                      .getId(), DashboardCustomization.Page.LINKS, null);

        linkPage = new LinkPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        customizeSitePage = new CustomizeSitePage(webDriver);
        currentDate = new DateTime();

        authenticateUsingLoginPage(user1.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(),
                                        "/User Homes/" + user1.get()
                                            .getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user1.get());
    }

    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "singlePipelineFailure" })
    @TestRail(id = "C6250")
    public void accessingTheSiteLinks()
    {
        String newLinksPageName = "newLinks";
        log.info("Step 1 - Open Site1's dashboard and click on 'Links' link.");
        siteDashboardPage.navigate(siteName.get()
                                       .getId());
        siteDashboardPage.clickLinkFromHeaderNavigationMenu(SitePageType.LINKS);
        Assert.assertTrue(linkPage.getLinksListTitle()
                              .equals("All Links"), "All links should be displayed after clicking on 'Links' link!");

        log.info("Step 2 : Open customize site and rename 'Links' to 'newLinks'");
        customizeSitePage.navigate(siteName.get()
                                       .getId());
        customizeSitePage.renameSitePage(SitePageType.LINKS, newLinksPageName);
        Assert.assertEquals(customizeSitePage.getPageDisplayName(SitePageType.LINKS), newLinksPageName,
                            "Wrong page name after it was renamed!");

        log.info("Step 3 : Click 'Ok' button");
        customizeSitePage.saveChanges();
        siteDashboardPage.navigate(siteName.get()
                                       .getId());
        Assert.assertEquals(siteDashboardPage.getPageDisplayName(SitePageType.LINKS), newLinksPageName,
                            "Wrong 'Links' page name on site dashboard!");

        log.info("Step 4 : Click 'newLinks' link");
        siteDashboardPage.clickLinkFromHeaderNavigationMenu(SitePageType.LINKS);
        Assert.assertTrue(linkPage.getLinksListTitle()
                              .equals("All Links"), "All links should be displayed after clicking on 'Links' link!");
    }

    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail(id = "C6251")
    public void toggleBetweenSimpleAndDetailedView()
    {
        String linkTitle = "Title link1";
        log.info(
            "Precondition - Any link is created e.g.: title Link1 URL link1.com description: link1 description tags: tag1");
        tags.add("tag1");
        sitePagesService.createLink(user1.get()
                                        .getUsername(), password, siteName.get()
                                        .getId(), linkTitle, "link1.com", "link1 description", true, tags);

        log.info("Step 1 - Navigate to 'Links' page for Site1.");
        linkPage.navigate(siteName.get()
                              .getId());
        Assert.assertTrue(linkPage.getLinksTitlesList()
                              .contains(linkTitle), "Link title is not in the list!");
        Assert.assertEquals(linkPage.getLinkURL(linkTitle), "link1.com", "Link URL is not correct!");
        Assert.assertEquals(linkPage.getLinkDescription(linkTitle), "link1 description",
                            "Wrong description of the link!");
        Assert.assertTrue(linkPage.getLinkCreationDate(linkTitle)
                              .contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle)
                              .contains("tag1"), "Tag not available for the specified link!");

        log.info("Step 2 - Click 'Simple View' button.");
        linkPage.changeViewMode();
        Assert.assertTrue(linkPage.getLinksTitlesList()
                              .contains(linkTitle), "Link title is not in the list!");
        Assert.assertTrue(linkPage.getLinksURL()
                              .contains("link1.com"), "Link URL is not in the list!");

        log.info("Step 3 - Click 'Detailed View' button.");
        linkPage.changeViewMode();
        Assert.assertTrue(linkPage.getLinksTitlesList()
                              .contains(linkTitle), "Link title is not in the list!");
        Assert.assertEquals(linkPage.getLinkURL(linkTitle), "link1.com", "Link URL is not correct!");
        Assert.assertEquals(linkPage.getLinkDescription(linkTitle), "link1 description",
                            "Wrong description of the link!");
        Assert.assertTrue(linkPage.getLinkCreationDate(linkTitle)
                              .contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle)
                              .contains("tag1"), "Tag not available for the specified link!");
    }

    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail(id = "C6895")
    public void presenceOfSiteLinksAfterDeletingUser()
    {
        log.info(
            "Precondition - Any link is created e.g.: title Link1 URL link1.com description: link1 description tags: tag1");
        String linkTitle = "Title link1";
        tags.add("tag1");
        sitePagesService.createLink(user1.get()
                                        .getUsername(), password, siteName.get()
                                        .getId(), linkTitle, "link1.com", "link1 description", true, tags);
        log.info("Step1: Delete the user created in pre-condition");
        deleteUsersIfNotNull(user1.get());

        log.info("Step1: Login through Admin user & access the link created by deleted user");
        authenticateUsingLoginPage(getAdminUser());
        linkPage.navigate(siteName.get()
                              .getId());
        Assert.assertTrue(linkPage.getLinksTitlesList()
                              .contains(linkTitle), "Link title is not in the list!");
    }
}
