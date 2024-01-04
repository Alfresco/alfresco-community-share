package org.alfresco.share.sitesFeatures.Links;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.UserService;

import org.alfresco.po.share.site.link.LinkPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
@Slf4j
/**
 * @author iulia.cojocea
 */
public class BrowsingTheSiteLinksTests extends BaseTest
{
    //@Autowired
    LinkPage linkPage;

    @Autowired
    protected UserService userService;

    @Autowired
    protected SiteService siteService;

    @Autowired
    protected SitePagesService sitePagesService;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Test users are created");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        linkPage = new LinkPage(webDriver);
        authenticateUsingLoginPage(user1.get());
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user1.get().getUsername());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user2.get().getUsername());
        deleteUsersIfNotNull(user1.get());
        deleteUsersIfNotNull(user2.get());
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6252")
    public void browseLinksByTags() {

        log.info("Precondition - Several links are added to site: Link1, Link2 have 'test_tag' tag. Link3 has 'l3' tag.");
        List<String> linkTags1 = new ArrayList<>();
        linkTags1.add("test_tag");
        List<String> linkTags2 = new ArrayList<>();
        linkTags2.add("l3");

        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        siteService.addPageToSite(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), "Link1", "link1.com", "link1 description", true, linkTags1);
        sitePagesService.createLink(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), "Link2", "link2.com", "link2 description", true, linkTags1);
        sitePagesService.createLink(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), "Link3", "link3.com", "link3 description", true, linkTags2);

        log.info("Step 1:  - Navigate to 'Links' page for Site1.");
        linkPage.navigate(siteName.get().getId());
        Assert.assertTrue(linkPage.get_LinksListTitle().equals("All Links"), "All links should be displayed after clicking on 'Links' link!");
        List<String> linksList = Arrays.asList("Link3", "Link2", "Link1");
        Assert.assertTrue(CollectionUtils.isEqualCollection(linkPage.getLinksTitlesList(), linksList), "Not all links are displayed!");

        log.info("Step 2:  - Click test_tag(2).");
        linkPage.clickSpecificTag("test_tag");
        linksList = Arrays.asList("Link2", "Link1");
        int i = 0;
        while (linkPage.getLinksTitlesList().size() < 1 && i < 5)
        {
            linkPage.clickSpecificTag("test_tag");
            i++;
        }
        linkPage.browserRefresh();
        linkPage.clickSpecificTag("test_tag");
        Assert.assertTrue(CollectionUtils.isEqualCollection(linkPage.getLinksTitlesList(), linksList), "Only 'Link1' and 'Link2' should be displayed!");

        log.info("Step 3:  - Click l3(1).");
        linkPage.clickSpecificTag("l3");
        linksList = Collections.singletonList("Link3");
        Assert.assertTrue(CollectionUtils.isEqualCollection(linkPage.getLinksTitlesList(), linksList), "Only 'Link3' should be displayed!");
        deleteSitesIfNotNull(siteName.get());

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES } )
    @TestRail (id = "C6253")
    public void browseLinksByLinksMenu()
    {

        log.info("Precondition - Several links are added to site: Link1, Link2 are created by testUser1 and Link3 by testUser2.");
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        userService.createSiteMember(user1.get().getUsername(), user1.get().getPassword(), user2.get().getUsername(), siteName.get().getId(), "SiteCollaborator");
        siteService.addPageToSite(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), "Link1", "link1.com", "link1 description", true, null);
        sitePagesService.createLink(user2.get().getUsername(), user2.get().getPassword(), siteName.get().getId(), "Link2", "link2.com", "link2 description", true, null);
        sitePagesService.createLink(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), "Link3", "link3.com", "link3 description", true, null);

        log.info("Step 1:  - Navigate to 'Links' page for Site1.");
        linkPage.navigate(siteName.get());
        Assert.assertTrue(linkPage.get_LinksListTitle().equals("All Links"), "All links should be displayed after clicking on 'Links' link!");
        List<String> linksList = Arrays.asList("Link3", "Link2", "Link1");
        Assert.assertTrue(CollectionUtils.isEqualCollection(linkPage.getLinksTitlesList(), linksList), "Not all links are displayed!");

        log.info("Step 2:  - Click on 'My links'");
        linkPage.filterLinksBy("My Links");
        linksList = Arrays.asList("Link3", "Link1");
        Assert.assertTrue(CollectionUtils.isEqualCollection(linkPage.getLinksTitlesList(), linksList), "Only Link1 and Link3 should be displayed!");
        Assert.assertFalse(linkPage.is_LinkDisplayed("Link2"), "Link2 sahould not be displayed!");

        log.info("Step 3:  - Click on 'Recently Added'");
        linkPage.filterLinksBy("Recently Added");
        Assert.assertTrue(linkPage.is_LinkDisplayed("Link1"), "Link1 should be displayed!");
//        Assert.assertFalse(linkPage.is_LinkDisplayed("Link3"), "Link3 should not be displayed!"); - in case Link3 is created in the past

        deleteSitesIfNotNull(siteName.get());

    }
}
