package org.alfresco.share.sitesFeatures.discussions;

import java.util.Arrays;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.site.discussion.TopicListPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Slf4j
public class BrowsingDiscussionTopicsTests extends BaseTest
{
    @Autowired
    SiteService siteService;
    @Autowired
    SitePagesService sitePagesService;
    @Autowired
    UserService userService;
    private TopicListPage topicListPage;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private String topic1Title = "Topic1";
    private String topic2Title = "Topic2";
    private String topic3Title = "Topic3";
    private String topicContent = "Some content";
    private String topicReply1 = "Reply1 content";
    private String topicReply2 = "Reply2 content";
    private String topicTag1 = "tag1";
    private String topicTag2 = "tag2";
    private String password = "password";

    @BeforeMethod(alwaysRun = true)
    public void preConditions()
    {
        log.info("Precondition: Any Test user is created");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        topicListPage = new TopicListPage(webDriver);
        siteService.addPageToSite(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), Page.DISCUSSIONS, null);
        userService.createSiteMember(user1.get().getUsername(), password, user2.get().getUsername(), siteName.get().getId(), "SiteManager");
        sitePagesService.createDiscussion(user1.get().getUsername(), password, siteName.get().getId(), topic1Title, topicContent, Collections.singletonList(topicTag1));
        sitePagesService.createDiscussion(user2.get().getUsername(), password, siteName.get().getId(), topic2Title, topicContent, Arrays.asList(topicTag1, topicTag2));
        sitePagesService.createDiscussion(user1.get().getUsername(), password, siteName.get().getId(), topic3Title, topicContent, null);
        sitePagesService.replyToDiscussion(user1.get().getUsername(), password, siteName.get().getId(), topic2Title, topicReply1);
        sitePagesService.replyToDiscussion(user2.get().getUsername(), password, siteName.get().getId(), topic2Title, topicReply2);
        authenticateUsingLoginPage(user1.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        siteService.delete(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user1.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user1.get());
        siteService.delete(user2.get().getUsername(), user2.get().getPassword(), siteName.get().getId());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user2.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user2.get());
    }

    @TestRail (id = "6199")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browseByTopicsFilter()
    {
        topicListPage.navigate(siteName.get().getId());
        log.info("STEP 1 - Click to see 'All' topics.");
        topicListPage.filterTopicsBy("All");
        assertEquals(topicListPage.getTopicListTitle(), "All Posts", "All three topics are displayed in the list.");
        assertTrue(topicListPage.isTopicDisplayed(topic1Title), "Topic1 is displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic2Title), "Topic2 is displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic3Title), "Topic3 is displayed.");

        log.info("STEP 2 - Click to see 'My Topics' topics.");
        topicListPage.filterTopicsBy("My Topics");
        assertEquals(topicListPage.getTopicListTitle(), "My Topics", "Only the topics created by User1 are displayed(Topic1 and Topic3).");
        assertTrue(topicListPage.isTopicDisplayed(topic1Title), "Topic1 is displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic3Title), "Topic3 is displayed.");

        log.info("STEP 3 - Click to see 'Most active' topics.");
        topicListPage.filterTopicsBy("Most Active");
        assertEquals(topicListPage.getTopicListTitle(), "Most Active", "The topics with the most replies are displayed, in this case only Topic2.");
        assertTrue(topicListPage.isTopicDisplayed(topic2Title), "Topic2 is displayed.");

        log.info("STEP 4 - Click to see 'New' topics.");
        topicListPage.filterTopicsBy("New");
        assertEquals(topicListPage.getTopicListTitle(), "New Topics", "Only topics created in the past 7 days are displayed, in this case Topic1 and Topic2.");
        assertTrue(topicListPage.isTopicDisplayed(topic1Title), "Topic1 is displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic2Title), "Topic2 is displayed.");
    }

    @TestRail (id = "6204")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "singlePipelineFailure" })
    public void browseTopicsByTags()
    {
        topicListPage.navigate(siteName.get().getId());

        log.info("STEP 1 - Check that in the tags list tag1 and tag2 are displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic1Title), "Topic1 is displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic2Title), "Topic2 is displayed.");
        assertTrue(topicListPage.isTagDisplayed(topicTag1), "Tag1 is displayed in te tags list.");
        assertEquals(topicListPage.getTagAssociatedTopicsNo(topicTag1), "(2)", "Tag1 has 2 topics associated.");
        assertTrue(topicListPage.isTagDisplayed(topicTag2), "Tag2 is displayed in te tags list.");
        assertEquals(topicListPage.getTagAssociatedTopicsNo(topicTag2), "(1)", "Tag1 has 1 topic associated.");

        log.info("STEP 2 - Click on tag1.");
        topicListPage.refresh(topicTag1);
        topicListPage.clickTag(topicTag1);
        assertEquals(topicListPage.getTopicListTitle(), "Posts with Tag '" + topicTag1 + "'", "Only topics associated with tag1 are displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic1Title), "Topic1 is associated with tag1 and it is displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic2Title), "Topic2 is associated with tag1 and it is displayed.");

        log.info("STEP 3 - Click on tag2.");
        topicListPage.clickTag(topicTag2);
        assertEquals(topicListPage.getTopicListTitle(), "Posts with Tag '" + topicTag2 + "'", "Only topics associated with tag2 are displayed.");
        assertTrue(topicListPage.isTopicDisplayed(topic2Title), "Topic2 is associated with tag2 and it is displayed.");
    }
}
