package org.alfresco.share.sitesFeatures.discussions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.UserService;

import org.alfresco.po.share.site.discussion.TopicListPage;
import org.alfresco.po.share.site.discussion.TopicViewPage;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j

/**
 * Created by Claudia Agache on 8/10/2016.
 */
public class ViewTopicTests extends BaseTest
{
    //@Autowired
    TopicListPage topicListPage;

    //@Autowired
    TopicViewPage topicViewPage;

    @Autowired
    protected UserService userService;

    @Autowired
    protected SiteService siteService;

    @Autowired
    protected SitePagesService sitePagesService;

    DateFormat df = new SimpleDateFormat("EE d MMM yyyy");
    String today;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private String topicTitle = "Topic1";
    private String topicContent = "Some content";
    private String topicTag = "tag1";
    private String topicReply = "Some reply from user2.";

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        userService.createSiteMember(user1.get().getUsername(), user1.get().getPassword(), user2.get().getUsername(), siteName.get().getId(), "SiteManager");
        siteService.addPageToSite(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), Page.DISCUSSIONS, null);
        sitePagesService.createDiscussion(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), topicTitle, topicContent, Collections.singletonList(topicTag));
        sitePagesService.replyToDiscussion(user2.get().getUsername(), user2.get().getPassword(), siteName.get().getId(), topicTitle, topicReply);

        topicListPage = new TopicListPage(webDriver);
        topicViewPage = new TopicViewPage(webDriver);

        authenticateUsingLoginPage(user1.get());
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user1.get().getUsername());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user2.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user1.get());
        deleteUsersIfNotNull(user2.get());
    }

    @TestRail (id = "6211")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewTopicFromDiscussionsTopicListPage()
    {
        today = df.format(new Date());
        //precondition
        topicListPage.navigate(siteName.get());

        log.info("STEP 1 - Click on 'View' topic link.");
        topicListPage.viewTopic(topicTitle);

        log.info("Expected Result: Information about that topic is displayed.");
        assertEquals(topicViewPage.getTopicTitle(), topicTitle, "Title is displayed.");
        assertTrue(topicViewPage.getTopicPublished().startsWith("Created on: " + today),
            "Topic was created today. Actual: [" + topicViewPage.getTopicPublished() + "]. Expected: [" + today + "]");
        assertTrue(topicViewPage.getTopicPublished().contains("Author: " + user1.get().getFirstName() + " " + user1.get().getLastName()), "The user who created the topic is User1.");
        assertTrue(topicViewPage.getTopicPublished().contains("Last reply by: " + user2.get().getFirstName() + " " + user2.get().getLastName()), "The user who last replied the topic is User2.");
        assertTrue(topicViewPage.getTopicPublished().contains("Last reply on: " + today), "Last reply was today.");
        assertEquals(topicViewPage.getTopicContent(), topicContent, "Content is displayed.");
        assertEquals(topicViewPage.getTopicReplies(), "(1)", "Topic has 1 reply.");
        assertEquals(topicViewPage.getTopicTags(), topicTag, "Tag is displayed.");

        log.info("Expected Result: Information about the reply is displayed.");
        assertEquals(topicViewPage.getReplyAuthor(topicReply), user2.get().getFirstName() + " " + user2.get().getLastName(), user2 + " posted that reply.");
        assertTrue(topicViewPage.getReplyDate(topicReply).contains(today), "Reply was posted today.");
        assertEquals(topicViewPage.getReplyNoReplies(topicReply), "(0)", "Reply has 0 replies.");

        log.info("STEP 2 - Click 'Discussions Topic List' to return to the main view.");
        topicViewPage.clickDiscussionsTopicListLink();
        assertTrue(topicListPage.isTopicDisplayed(topicTitle), "Topic is displayed on the list.");
    }

    @TestRail (id = "6212")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewTopicUsingReadAction()
    {
        today = df.format(new Date());
        //precondition
        topicListPage.navigate(siteName.get());

        log.info("STEP 1 - Click on 'Read' link.");
        topicListPage.readTopic(topicTitle);

        log.info("Expected Result: Information about that topic is displayed.");
        assertEquals(topicViewPage.getTopicTitle(), topicTitle, "Title is displayed.");
        assertTrue(topicViewPage.getTopicPublished().startsWith("Created on: " + today),
            "Topic was created today. Actual: [" + topicViewPage.getTopicPublished() + "]. Expected: [" + today + "]");
        assertTrue(topicViewPage.getTopicPublished().contains("Author: " + user1.get().getFirstName() + " " + user1.get().getLastName()), "The user who created the topic is User1.");
        assertTrue(topicViewPage.getTopicPublished().contains("Last reply by: " + user2.get().getFirstName() + " " + user2.get().getLastName()), "The user who last replied the topic is User2.");
        assertTrue(topicViewPage.getTopicPublished().contains("Last reply on: " + today), "Last reply was today.");
        assertEquals(topicViewPage.getTopicContent(), topicContent, "Content is displayed.");
        assertEquals(topicViewPage.getTopicReplies(), "(1)", "Topic has 1 reply.");
        assertEquals(topicViewPage.getTopicTags(), topicTag, "Tag is displayed.");

        log.info("Expected Result: Information about the reply is displayed.");
        assertEquals(topicViewPage.getReplyAuthor(topicReply), user2.get().getFirstName() + " " + user2.get().getLastName(), user2 + " posted that reply.");
        assertTrue(topicViewPage.getReplyDate(topicReply).contains(today), "Reply was posted today.");
        assertEquals(topicViewPage.getReplyNoReplies(topicReply), "(0)", "Reply has 0 replies.");

        log.info("STEP 2 - Click 'Discussions Topic List' to return to the main view. Choose 'Simple View' to display the page.");
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
        topicListPage.navigate(siteName.get());

        log.info("STEP 1 - Click on the name of the topic to open it.");
        topicListPage.clickTopicTitle(topicTitle);

        log.info("Expected Result: Information about that topic is displayed.");
        assertEquals(topicViewPage.getTopicTitle(), topicTitle, "Title is displayed.");
        assertTrue(topicViewPage.getTopicPublished().startsWith("Created on: " + today),
            "Topic was created today. Actual: [" + topicViewPage.getTopicPublished() + "]. Expected: [" + today + "]");
        assertTrue(topicViewPage.getTopicPublished().contains("Author: " + user1.get().getFirstName() + " " + user1.get().getLastName()), "The user who created the topic is User1.");
        assertTrue(topicViewPage.getTopicPublished().contains("Last reply by: " + user2.get().getFirstName() + " " + user2.get().getLastName()), "The user who last replied the topic is User2.");
        assertTrue(topicViewPage.getTopicPublished().contains("Last reply on: " + today), "Last reply was today.");
        assertEquals(topicViewPage.getTopicContent(), topicContent, "Content is displayed.");
        assertEquals(topicViewPage.getTopicReplies(), "(1)", "Topic has 1 reply.");
        assertEquals(topicViewPage.getTopicTags(), topicTag, "Tag is displayed.");

        log.info("Expected Result: Information about the reply is displayed.");
        assertEquals(topicViewPage.getReplyAuthor(topicReply), user2.get().getFirstName() + " " + user2.get().getLastName(), user2 + " posted that reply.");
        assertTrue(topicViewPage.getReplyDate(topicReply).contains(today), "Reply was posted today.");
        assertEquals(topicViewPage.getReplyNoReplies(topicReply), "(0)", "Reply has 0 replies.");
    }
}
