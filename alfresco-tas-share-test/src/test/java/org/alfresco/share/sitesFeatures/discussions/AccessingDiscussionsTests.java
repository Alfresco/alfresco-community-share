package org.alfresco.share.sitesFeatures.discussions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.discussion.TopicListPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Claudia Agache on 8/9/2016.
 */
public class AccessingDiscussionsTests extends ContextAwareWebTest
{
    @Autowired
    TopicListPage topicListPage;
    @Autowired
    SiteDashboardPage siteDashboardPage;
    DateFormat df = new SimpleDateFormat("EE d MMM yyyy");
    private String user = String.format("User1%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
    private String topicTitle = "Topic1";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        siteService.create(user, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user, password, siteName, Page.DISCUSSIONS, null);
        sitePagesService.createDiscussion(user, password, siteName, topicTitle, "Topic content", Collections.singletonList("tag1"));
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
    }


    @TestRail (id = "6198")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed"  })
    public void accessTheDiscussionForum()
    {
        String today = df.format(new Date());
        LOG.info("STEP 1 - Open Site1's dashboard and click on 'Discussions' link.");
        siteDashboardPage.navigate(siteName);
        siteDashboardPage.clickLinkFromHeaderNavigationMenu(SitePageType.DISCUSSIONS);
        assertTrue(getBrowser().getCurrentUrl().endsWith("/discussions-topiclist"), "'Discussions' page, which defaults to New Topics view, is opened.");
        assertTrue(topicListPage.isTopicDisplayed(topicTitle), "The list of topics contains 'Topic1'.");
        assertTrue(topicListPage.getTopicPublishedDetails(topicTitle).startsWith("Created on: " + today),
            "The topic was created today. Actual: [" + topicListPage.getTopicPublishedDetails(topicTitle)
                + "]. Expected date: [" + today + "]");
        assertTrue(topicListPage.getTopicPublishedDetails(topicTitle).contains("Author: " + "firstName lastName"), "The user who created the topic is User1.");
        assertEquals(topicListPage.getTopicContent(topicTitle), "Topic content", "Topic content is 'Topic content'.");
        assertEquals(topicListPage.getTopicReplies(topicTitle), "(0)", "Topic has 0 replies.");
        assertEquals(topicListPage.getTopicTags(topicTitle), "tag1", "The tags associated with the topic: tag1.");

        LOG.info("STEP 2 - Click on 'Simple View' button.");
        topicListPage.toggleBetweenSimpleAndDetailedView();
        assertTrue(topicListPage.isTopicDisplayed(topicTitle), "The list of topics contains 'Topic1'.");
        assertTrue(topicListPage.getTopicPublishedDetails(topicTitle).startsWith("Created on: " + today),
            "The topic was created today. Actual: [" + topicListPage.getTopicPublishedDetails(topicTitle)
                + "]. Expected date: [" + today + "]");
        assertTrue(topicListPage.getTopicPublishedDetails(topicTitle).contains("Author: " + "firstName lastName"), "The user who created the topic is User1.");
        assertFalse(topicListPage.isTopicContentDisplayed(topicTitle), "Content is not displayed in Simple View");
        assertFalse(topicListPage.areTopicRepliesDisplayed(topicTitle), "Replies are not displayed in Simple View");
        assertFalse(topicListPage.areTopicTagsDisplayed(topicTitle), "Tags are not displayed in Simple View");

        LOG.info("STEP 3 - Click on 'Detailed View' button.");
        topicListPage.toggleBetweenSimpleAndDetailedView();
        assertTrue(topicListPage.isTopicDisplayed(topicTitle), "The list of topics contains 'Topic1'.");
        assertTrue(topicListPage.getTopicPublishedDetails(topicTitle).startsWith("Created on: " + today),
            "The topic was created today. Actual: [" + topicListPage.getTopicPublishedDetails(topicTitle)
                + "]. Expected date: [" + today + "]");
        assertTrue(topicListPage.getTopicPublishedDetails(topicTitle).contains("Author: " + "firstName lastName"), "The user who created the topic is User1.");
        assertEquals(topicListPage.getTopicContent(topicTitle), "Topic content", "Topic content is 'Topic content'.");
        assertEquals(topicListPage.getTopicReplies(topicTitle), "(0)", "Topic has 0 replies.");
        assertEquals(topicListPage.getTopicTags(topicTitle), "tag1", "The tags associated with the topic: tag1.");
    }
}
