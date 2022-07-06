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
import org.alfresco.po.share.site.discussion.TopicListPage;
import org.alfresco.po.share.site.discussion.TopicViewPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Claudia Agache on 8/10/2016.
 */
public class ViewTopicTests extends ContextAwareWebTest
{
    //@Autowired
    TopicListPage topicListPage;

    //@Autowired
    TopicViewPage topicViewPage;

    DateFormat df = new SimpleDateFormat("EE d MMM yyyy");
    String today;
    private String user1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
    private String user2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
    private String topicTitle = "Topic1";
    private String topicContent = "Some content";
    private String topicTag = "tag1";
    private String topicReply = "Some reply from user2.";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1, "lName1");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, user2, "lName2");
        siteService.create(user1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, user2, siteName, "SiteManager");
        siteService.addPageToSite(user1, password, siteName, Page.DISCUSSIONS, null);
        sitePagesService.createDiscussion(user1, password, siteName, topicTitle, topicContent, Collections.singletonList(topicTag));
        sitePagesService.replyToDiscussion(user2, password, siteName, topicTitle, topicReply);
        setupAuthenticatedSession(user1, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "6211")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewTopicFromDiscussionsTopicListPage()
    {
        today = df.format(new Date());
        //precondition
        topicListPage.navigate(siteName);

        LOG.info("STEP 1 - Click on 'View' topic link.");
        topicListPage.viewTopic(topicTitle);

        LOG.info("Expected Result: Information about that topic is displayed.");
        assertEquals(topicViewPage.getTopicTitle(), topicTitle, "Title is displayed.");
        assertTrue(topicViewPage.getTopicPublished().startsWith("Created on: " + today),
            "Topic was created today. Actual: [" + topicViewPage.getTopicPublished() + "]. Expected: [" + today + "]");
        assertTrue(topicViewPage.getTopicPublished().contains("Author: " + user1 + " lName1"), "The user who created the topic is User1.");
        assertTrue(topicViewPage.getTopicPublished().contains("Last reply by: " + user2 + " lName2"), "The user who last replied the topic is User2.");
        assertTrue(topicViewPage.getTopicPublished().contains("Last reply on: " + today), "Last reply was today.");
        assertEquals(topicViewPage.getTopicContent(), topicContent, "Content is displayed.");
        assertEquals(topicViewPage.getTopicReplies(), "(1)", "Topic has 1 reply.");
        assertEquals(topicViewPage.getTopicTags(), topicTag, "Tag is displayed.");

        LOG.info("Expected Result: Information about the reply is displayed.");
        assertEquals(topicViewPage.getReplyAuthor(topicReply), user2 + " lName2", user2 + " posted that reply.");
        assertTrue(topicViewPage.getReplyDate(topicReply).contains(today), "Reply was posted today.");
        assertEquals(topicViewPage.getReplyNoReplies(topicReply), "(0)", "Reply has 0 replies.");

        LOG.info("STEP 2 - Click 'Discussions Topic List' to return to the main view.");
        topicViewPage.clickDiscussionsTopicListLink();
        assertTrue(topicListPage.isTopicDisplayed(topicTitle), "Topic is displayed on the list.");
    }

    @TestRail (id = "6212")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewTopicUsingReadAction()
    {
        today = df.format(new Date());
        //precondition
        topicListPage.navigate(siteName);

        LOG.info("STEP 1 - Click on 'Read' link.");
        topicListPage.readTopic(topicTitle);

        LOG.info("Expected Result: Information about that topic is displayed.");
        assertEquals(topicViewPage.getTopicTitle(), topicTitle, "Title is displayed.");
        assertTrue(topicViewPage.getTopicPublished().startsWith("Created on: " + today),
            "Topic was created today. Actual: [" + topicViewPage.getTopicPublished() + "]. Expected: [" + today + "]");
        assertTrue(topicViewPage.getTopicPublished().contains("Author: " + user1 + " lName1"), "The user who created the topic is User1.");
        assertTrue(topicViewPage.getTopicPublished().contains("Last reply by: " + user2 + " lName2"), "The user who last replied the topic is User2.");
        assertTrue(topicViewPage.getTopicPublished().contains("Last reply on: " + today), "Last reply was today.");
        assertEquals(topicViewPage.getTopicContent(), topicContent, "Content is displayed.");
        assertEquals(topicViewPage.getTopicReplies(), "(1)", "Topic has 1 reply.");
        assertEquals(topicViewPage.getTopicTags(), topicTag, "Tag is displayed.");

        LOG.info("Expected Result: Information about the reply is displayed.");
        assertEquals(topicViewPage.getReplyAuthor(topicReply), user2 + " lName2", user2 + " posted that reply.");
        assertTrue(topicViewPage.getReplyDate(topicReply).contains(today), "Reply was posted today.");
        assertEquals(topicViewPage.getReplyNoReplies(topicReply), "(0)", "Reply has 0 replies.");

        LOG.info("STEP 2 - Click 'Discussions Topic List' to return to the main view. Choose 'Simple View' to display the page.");
        topicViewPage.clickDiscussionsTopicListLink();
        topicListPage.toggleBetweenSimpleAndDetailedView();
        assertFalse(topicListPage.isReadLinkDisplayed(topicTitle), "The Read action is not available if 'Simple View' is selected.");
    }

    @TestRail (id = "6213")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewTopicByClickingOnItsName()
    {
        today = df.format(new Date());
        //precondition
        topicListPage.navigate(siteName);

        LOG.info("STEP 1 - Click on the name of the topic to open it.");
        topicListPage.clickTopicTitle(topicTitle);

        LOG.info("Expected Result: Information about that topic is displayed.");
        assertEquals(topicViewPage.getTopicTitle(), topicTitle, "Title is displayed.");
        assertTrue(topicViewPage.getTopicPublished().startsWith("Created on: " + today),
            "Topic was created today. Actual: [" + topicViewPage.getTopicPublished() + "]. Expected: [" + today + "]");
        assertTrue(topicViewPage.getTopicPublished().contains("Author: " + user1 + " lName1"), "The user who created the topic is User1.");
        assertTrue(topicViewPage.getTopicPublished().contains("Last reply by: " + user2 + " lName2"), "The user who last replied the topic is User2.");
        assertTrue(topicViewPage.getTopicPublished().contains("Last reply on: " + today), "Last reply was today.");
        assertEquals(topicViewPage.getTopicContent(), topicContent, "Content is displayed.");
        assertEquals(topicViewPage.getTopicReplies(), "(1)", "Topic has 1 reply.");
        assertEquals(topicViewPage.getTopicTags(), topicTag, "Tag is displayed.");

        LOG.info("Expected Result: Information about the reply is displayed.");
        assertEquals(topicViewPage.getReplyAuthor(topicReply), user2 + " lName2", user2 + " posted that reply.");
        assertTrue(topicViewPage.getReplyDate(topicReply).contains(today), "Reply was posted today.");
        assertEquals(topicViewPage.getReplyNoReplies(topicReply), "(0)", "Reply has 0 replies.");
    }
}
