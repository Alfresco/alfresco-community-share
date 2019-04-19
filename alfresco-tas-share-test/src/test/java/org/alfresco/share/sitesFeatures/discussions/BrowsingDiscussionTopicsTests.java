package org.alfresco.share.sitesFeatures.discussions;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.discussion.TopicListPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.*;

/**
 * Created by Claudia Agache on 8/11/2016.
 */
public class BrowsingDiscussionTopicsTests extends ContextAwareWebTest
{
    @Autowired
    TopicListPage topicListPage;

    private String user1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
    private String user2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
    private String topic1Title = "Topic1";
    private String topic2Title = "Topic2";
    private String topic3Title = "Topic3";
    private String topicContent = "Some content";
    private String topicReply1 = "Reply1 content";
    private String topicReply2 = "Reply2 content";
    private String topicTag1 = "tag1";
    private String topicTag2 = "tag2";

    private DateTime today = new DateTime();
    private DateTime eightDaysAgo = today.minusDays(8);

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1, "lName1");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, user2, "lName2");
        siteService.create(user1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user1, password, siteName, Page.DISCUSSIONS, null);
        userService.createSiteMember(user1, password, user2, siteName, "SiteManager");
        sitePagesService.createDiscussion(user1, password, siteName, topic1Title, topicContent, Collections.singletonList(topicTag1));
        sitePagesService.createDiscussion(user2, password, siteName, topic2Title, topicContent, Arrays.asList(topicTag1, topicTag2));
        changeTopicDate(eightDaysAgo);
        sitePagesService.createDiscussion(user1, password, siteName, topic3Title, topicContent, null);
        changeTopicDate(today);
        sitePagesService.replyToDiscussion(user1, password, siteName, topic2Title, topicReply1);
        sitePagesService.replyToDiscussion(user2, password, siteName, topic2Title, topicReply2);
        setupAuthenticatedSession(user1, password);
    }

    private void changeTopicDate(DateTime date)
    {
        //TODO Find a way to change topic date: change date in database or running ssh commands on server
    }

    @TestRail(id = "6199")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browseByTopicsFilter()
    {
        topicListPage.navigate(siteName);
        LOG.info("STEP 1 - Click to see 'All' topics.");
        topicListPage.filterTopicsBy("All");
        assertEquals(topicListPage.getTopicListTitle(), "All Posts", "All three topics are displayed in the list.");
        assertTrue(topicListPage.isTopicDisplayed(topic1Title), "Topic1 is displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic2Title), "Topic2 is displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic3Title), "Topic3 is displayed.");

        LOG.info("STEP 2 - Click to see 'My Topics' topics.");
        topicListPage.filterTopicsBy("My Topics");
        assertEquals(topicListPage.getTopicListTitle(), "My Topics", "Only the topics created by User1 are displayed(Topic1 and Topic3).");
        assertTrue(topicListPage.isTopicDisplayed(topic1Title), "Topic1 is displayed.");
        assertFalse(topicListPage.isTopicDisplayed(topic2Title), "Topic2 should not be displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic3Title), "Topic3 is displayed.");

        LOG.info("STEP 3 - Click to see 'Most active' topics.");
        topicListPage.filterTopicsBy("Most Active");
        assertEquals(topicListPage.getTopicListTitle(), "Most Active", "The topics with the most replies are displayed, in this case only Topic2.");
        assertFalse(topicListPage.isTopicDisplayed(topic1Title), "Topic1 should not be displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic2Title), "Topic2 is displayed.");
        assertFalse(topicListPage.isTopicDisplayed(topic3Title), "Topic3 should not be displayed.");

        LOG.info("STEP 4 - Click to see 'New' topics.");
        topicListPage.filterTopicsBy("New");
        assertEquals(topicListPage.getTopicListTitle(), "New Topics", "Only topics created in the past 7 days are displayed, in this case Topic1 and Topic2.");
        assertTrue(topicListPage.isTopicDisplayed(topic1Title), "Topic1 is displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic2Title), "Topic2 is displayed.");
//        assertFalse(topicListPage.isTopicDisplayed(topic3Title), "Topic3 should not be  displayed.");
    }

    @TestRail(id = "6204")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browseTopicsByTags()
    {
        topicListPage.navigate(siteName);

        LOG.info("STEP 1 - Check that in the tags list tag1 and tag2 are displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic1Title), "Topic1 is displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic2Title), "Topic2 is displayed.");
        assertTrue(topicListPage.isTagDisplayed(topicTag1), "Tag1 is displayed in te tags list.");
        assertEquals(topicListPage.getTagAssociatedTopicsNo(topicTag1), "(2)", "Tag1 has 2 topics associated.");
        assertTrue(topicListPage.isTagDisplayed(topicTag2), "Tag2 is displayed in te tags list.");
        assertEquals(topicListPage.getTagAssociatedTopicsNo(topicTag2), "(1)", "Tag1 has 1 topic associated.");

        LOG.info("STEP 2 - Click on tag1.");
        topicListPage.clickTag(topicTag1);
        assertEquals(topicListPage.getTopicListTitle(), "Posts with Tag '"+topicTag1+"'", "Only topics associated with tag1 are displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic1Title), "Topic1 is associated with tag1 and it is displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic2Title), "Topic2 is associated with tag1 and it is displayed.");

        LOG.info("STEP 3 - Click on tag2.");
        topicListPage.clickTag(topicTag2);
        assertEquals(topicListPage.getTopicListTitle(), "Posts with Tag '"+topicTag2+"'", "Only topics associated with tag2 are displayed.");
        assertFalse(topicListPage.isTopicDisplayed(topic1Title), "Topic1 is not associated with tag2 and it is not displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic2Title), "Topic2 is associated with tag2 and it is displayed.");
    }
}
