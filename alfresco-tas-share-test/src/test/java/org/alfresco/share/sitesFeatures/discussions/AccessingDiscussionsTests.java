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
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.discussion.TopicListPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.*;

/**
 * Created by Claudia Agache on 8/9/2016.
 */
@Slf4j
public class AccessingDiscussionsTests extends BaseTest
{
    @Autowired
    SiteService siteService;
    @Autowired
    SitePagesService sitePagesService;
    private TopicListPage topicListPage;
    private SiteDashboardPage siteDashboardPage;
    DateFormat df = new SimpleDateFormat("EE d MMM yyyy");
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private String topicTitle = "Topic1";
    private String password = "password";
    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        topicListPage = new TopicListPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        siteService.addPageToSite(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), Page.DISCUSSIONS, null);
        sitePagesService.createDiscussion(user.get().getUsername(), password, siteName.get().getId(), topicTitle, "Topic content", Collections.singletonList("tag1"));
        authenticateUsingLoginPage(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        siteService.delete(user.get().getUsername(), user.get().getPassword(), siteName.get().getId());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user.get());
    }

    @TestRail (id = "6198")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "singlePipelineFailure" })
    public void accessTheDiscussionForum()
    {
        String today = df.format(new Date());
        log.info("STEP 1 - Open Site1's dashboard and click on 'Discussions' link.");
        siteDashboardPage.navigate(siteName.get().getId());
        siteDashboardPage.clickLinkFromHeaderNavigationMenu(SitePageType.DISCUSSIONS);
        assertTrue(topicListPage.isTopicDisplayed(topicTitle), "The list of topics contains 'Topic1'.");
        assertTrue(topicListPage.getTopicPublishedDetails(topicTitle).startsWith("Created on: " + today),
            "The topic was created today. Actual: [" + topicListPage.getTopicPublishedDetails(topicTitle)
                + "]. Expected date: [" + today + "]");
        assertEquals(topicListPage.getTopicContent(topicTitle), "Topic content", "Topic content is 'Topic content'.");
        assertEquals(topicListPage.getTopicReplies(topicTitle), "(0)", "Topic has 0 replies.");
        assertEquals(topicListPage.getTopicTags(topicTitle), "tag1", "The tags associated with the topic: tag1.");

        log.info("STEP 2 - Click on 'Simple View' button.");
        topicListPage.toggleBetweenSimpleAndDetailedView();
        assertTrue(topicListPage.isTopicDisplayed(topicTitle), "The list of topics contains 'Topic1'.");
        assertTrue(topicListPage.getTopicPublishedDetails(topicTitle).startsWith("Created on: " + today),
            "The topic was created today. Actual: [" + topicListPage.getTopicPublishedDetails(topicTitle)
                + "]. Expected date: [" + today + "]");
        assertFalse(topicListPage.isTopicContentDisplayed(topicTitle), "Content is not displayed in Simple View");
        assertFalse(topicListPage.areTopicRepliesDisplayed(topicTitle), "Replies are not displayed in Simple View");
        assertFalse(topicListPage.areTopicTagsDisplayed(topicTitle), "Tags are not displayed in Simple View");

        log.info("STEP 3 - Click on 'Detailed View' button.");
        topicListPage.toggleBetweenSimpleAndDetailedView();
        assertTrue(topicListPage.isTopicDisplayed(topicTitle), "The list of topics contains 'Topic1'.");
        assertTrue(topicListPage.getTopicPublishedDetails(topicTitle).startsWith("Created on: " + today),
            "The topic was created today. Actual: [" + topicListPage.getTopicPublishedDetails(topicTitle)
                + "]. Expected date: [" + today + "]");
        assertEquals(topicListPage.getTopicContent(topicTitle), "Topic content", "Topic content is 'Topic content'.");
        assertEquals(topicListPage.getTopicReplies(topicTitle), "(0)", "Topic has 0 replies.");
        assertEquals(topicListPage.getTopicTags(topicTitle), "tag1", "The tags associated with the topic: tag1.");
    }
}
