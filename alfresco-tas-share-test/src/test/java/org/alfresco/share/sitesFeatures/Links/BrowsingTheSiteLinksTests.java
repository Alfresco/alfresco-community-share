package org.alfresco.share.sitesFeatures.Links;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.link.LinkPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */
public class BrowsingTheSiteLinksTests extends ContextAwareWebTest
{
    //@Autowired
    LinkPage linkPage;

    private String user1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
    private String user2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
    private String siteName = "";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1, "lastName1");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, user2, "lastName2");
        setupAuthenticatedSession(user1, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6252")
    public void browseLinksByTags()
    {

        LOG.info("Precondition - Several links are added to site: Link1, Link2 have 'test_tag' tag. Link3 has 'l3' tag.");
        List<String> linkTags1 = new ArrayList<>();
        linkTags1.add("test_tag");
        List<String> linkTags2 = new ArrayList<>();
        linkTags2.add("l3");
        siteName = String.format("Site%s", RandomData.getRandomAlphanumeric());
        siteService.create(user1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user1, password, siteName, DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(user1, password, siteName, "Link1", "link1.com", "link1 description", true, linkTags1);
        sitePagesService.createLink(user1, password, siteName, "Link2", "link2.com", "link2 description", true, linkTags1);
        sitePagesService.createLink(user1, password, siteName, "Link3", "link3.com", "link3 description", true, linkTags2);

        LOG.info("Step 1:  - Navigate to 'Links' page for Site1.");
        linkPage.navigate(siteName);
        Assert.assertTrue(linkPage.getLinksListTitle().equals("All Links"), "All links should be displayed after clicking on 'Links' link!");
        List<String> linksList = Arrays.asList("Link3", "Link2", "Link1");
        Assert.assertTrue(CollectionUtils.isEqualCollection(linkPage.getLinksTitlesList(), linksList), "Not all links are displayed!");

        LOG.info("Step 2:  - Click test_tag(2).");
        linkPage.clickSpecificTag("test_tag");
        linksList = Arrays.asList("Link2", "Link1");
        int i = 0;
        while (linkPage.getLinksTitlesList().size() < 1 && i < 5)
        {
            linkPage.clickSpecificTag("test_tag");
            i++;
        }
        Assert.assertTrue(CollectionUtils.isEqualCollection(linkPage.getLinksTitlesList(), linksList), "Only 'Link1' and 'Link2' should be displayed!");

        LOG.info("Step 3:  - Click l3(1).");
        linkPage.clickSpecificTag("l3");
        linksList = Collections.singletonList("Link3");
        Assert.assertTrue(CollectionUtils.isEqualCollection(linkPage.getLinksTitlesList(), linksList), "Only 'Link3' should be displayed!");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6253")
    public void browseLinksByLinksMenu()
    {

        LOG.info("Precondition - Several links are added to site: Link1, Link2 are created by testUser1 and Link3 by testUser2.");
        siteName = String.format("Site%s", RandomData.getRandomAlphanumeric());
        siteService.create(user1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, user2, siteName, "SiteCollaborator");
        siteService.addPageToSite(user1, password, siteName, DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(user1, password, siteName, "Link1", "link1.com", "link1 description", true, null);
        sitePagesService.createLink(user2, password, siteName, "Link2", "link2.com", "link2 description", true, null);
        sitePagesService.createLink(user1, password, siteName, "Link3", "link3.com", "link3 description", true, null);
        // TODO: Create Link3 in the past(more than 7 days ago)

        LOG.info("Step 1:  - Navigate to 'Links' page for Site1.");
        linkPage.navigate(siteName);
        Assert.assertTrue(linkPage.getLinksListTitle().equals("All Links"), "All links should be displayed after clicking on 'Links' link!");
        List<String> linksList = Arrays.asList("Link3", "Link2", "Link1");
        Assert.assertTrue(CollectionUtils.isEqualCollection(linkPage.getLinksTitlesList(), linksList), "Not all links are displayed!");

        LOG.info("Step 2:  - Click on 'My links'");
        linkPage.filterLinksBy("My Links");
        linksList = Arrays.asList("Link3", "Link1");
        Assert.assertTrue(CollectionUtils.isEqualCollection(linkPage.getLinksTitlesList(), linksList), "Only Link1 and Link3 should be displayed!");
        Assert.assertFalse(linkPage.isLinkDisplayed("Link2"), "Link2 sahould not be displayed!");

        LOG.info("Step 3:  - Click on 'Recently Added'");
        linkPage.filterLinksBy("Recently Added");
        Assert.assertTrue(linkPage.isLinkDisplayed("Link1"), "Link1 should be displayed!");
        // Assert.assertFalse(linkPage.isLinkDisplayed("Link3"), "Link3 should not be displayed!"); - in case Link3 is created in the past
        siteService.delete(adminUser, adminPassword, siteName);

    }
}
