package org.alfresco.share.sitesFeatures.discussions;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.discussion.CreateNewTopicPage;
import org.alfresco.po.share.site.discussion.TopicListPage;
import org.alfresco.po.share.site.discussion.TopicViewPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

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

    private String random = DataUtil.getUniqueIdentifier();
    private String user = "User" + random;
    private String siteName = "Site-C6206-" + random;
    private String topicTitle = "Topic-" + random;
    private String topicContent = "Some content";
    private String topicTag = "tag1";

    @BeforeClass
    public void setupTest()
    {
        List<Page> pagesToAdd = new ArrayList<>();
        pagesToAdd.add(Page.DISCUSSIONS);
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        siteService.create(user, password, domain, siteName, "description", Site.Visibility.PUBLIC);
        siteService.addPagesToSite(user, password, siteName, pagesToAdd);
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C6206")
    @Test
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

    @TestRail(id = "C6207")
    @Test
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
        browser.waitInSeconds(3);
        browser.refresh();
        assertEquals(topicListPage.getMessageDisplayed(), language.translate("discussions.noTopicsFound"), "'No topics found' message=");
    }
}