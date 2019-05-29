package org.alfresco.share.sitesFeatures.discussions;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.discussion.CreateNewTopicPage;
import org.alfresco.po.share.site.discussion.TopicListPage;
import org.alfresco.po.share.site.discussion.TopicViewPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by Claudia Agache on 8/9/2016.
 */
public class CreateNewTopicTests extends ContextAwareWebTest
{
    @Autowired
    TopicListPage topicListPage;

    @Autowired
    CreateNewTopicPage createNewTopicPage;

    @Autowired
    TopicViewPage topicViewPage;

    private String random = RandomData.getRandomAlphanumeric();
    private String user = "User" + random;
    private String siteName = "Site-C6206-" + random;
    private String topicTitle = "Topic-" + random;
    private String topicContent = "Some content";
    private String topicTag = "tag1";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        siteService.create(user, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user, password, siteName, Page.DISCUSSIONS, null);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C6206")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewTopic()
    {
        topicListPage.navigate(siteName);

        LOG.info("STEP 1 - Click on 'New topic' button.");
        topicListPage.clickNewTopicButton();
        assertTrue(createNewTopicPage.isPageDisplayed(), "'Create New Topic' page is opened.");

        LOG.info("STEP 2 - Add title(e.g: Topic1), text (e.g: Some content) and a tag(e.g: tag1). Click on 'Save' button.");
        createNewTopicPage.typeTopicTitle(topicTitle);
        createNewTopicPage.typeTopicContent(topicContent);
        createNewTopicPage.addTag(topicTag);
        createNewTopicPage.clickSaveButton();
        assertEquals(topicViewPage.getTopicTitle(), topicTitle, "Title is displayed.");
        assertEquals(topicViewPage.getTopicContent(), topicContent, "Content is displayed.");
        assertEquals(topicViewPage.getTopicTags(), topicTag, "Tag is displayed.");

        LOG.info("STEP 3 - Click 'Discussions Topic List' link.");
        topicViewPage.clickDiscussionsTopicListLink();
        assertTrue(topicListPage.isTopicDisplayed(topicTitle), topicTitle + " is listed in 'New Topics' list.");

    }

    @TestRail (id = "C6207")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelCreatingNewTopic()
    {
        topicListPage.navigate(siteName);

        LOG.info("STEP 1 - Click on 'New topic' button.");
        topicListPage.clickNewTopicButton();
        assertTrue(createNewTopicPage.isPageDisplayed(), "'Create New Topic' page is opened.");

        LOG.info("STEP 2 - Add title, text and a tag. Click  'Cancel' button");
        createNewTopicPage.typeTopicTitle(topicTitle);
        createNewTopicPage.typeTopicContent(topicContent);
        createNewTopicPage.addTag(topicTag);
        createNewTopicPage.clickCancelButton();
        getBrowser().waitInSeconds(3);
        getBrowser().refresh();
        assertEquals(topicListPage.getMessageDisplayed(), language.translate("discussions.noTopicsFound"), "'No topics found' message=");
    }
}