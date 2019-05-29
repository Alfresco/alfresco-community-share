package org.alfresco.share.sitesFeatures.discussions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.discussion.EditTopicPage;
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
public class EditTopicTests extends ContextAwareWebTest
{
    @Autowired
    TopicListPage topicListPage;

    @Autowired
    EditTopicPage editTopicPage;

    @Autowired
    TopicViewPage topicViewPage;

    private String user1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
    private String topic1Title = "Topic1";
    private String topic2Title = "Topic2";
    private String topicContent = "Some content";
    private String topicNewTitle = "new title";
    private String topicNewContent = "new content";
    private String topicTag1 = "tag1";
    private String topicTag2 = "tag2";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1, "lName1");
        siteService.create(user1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user1, password, siteName, Page.DISCUSSIONS, null);
        sitePagesService.createDiscussion(user1, password, siteName, topic1Title, topicContent, null);
        sitePagesService.createDiscussion(user1, password, siteName, topic2Title, topicContent, Arrays.asList(topicTag1, topicTag2));
        setupAuthenticatedSession(user1, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "6220")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editTopicFromDiscussionsTopicListPage()
    {
        //precondition
        topicListPage.navigate(siteName);

        LOG.info("STEP 1 - Click on 'Edit' link to edit Topic1.");
        topicListPage.editTopic(topic1Title);
        assertTrue(editTopicPage.isPageDisplayed(), "The Edit Topic page is displayed.");
        assertEquals(editTopicPage.getTopicTitle(), topic1Title, "The Edit Topic page displays the selected topic title");
        assertEquals(editTopicPage.getTopicContent(), topicContent, "The Edit Topic page displays the selected topic content");

        LOG.info("STEP 2 - Modify Topic1 content: 'new content' and add also a tag, 'tag1'. Click on 'Save' button.");
        editTopicPage.typeTopicContent(topicNewContent);
        editTopicPage.addTag(topicTag1);
        editTopicPage.clickSaveButton();
        assertEquals(topicViewPage.getTopicTitle(), topic1Title + " (Updated)", "The text (Updated) appears after the title.");
        assertEquals(topicViewPage.getTopicContent(), topicNewContent, "The updated topic appears in the topic view page with the new content.");
        assertEquals(topicViewPage.getTopicTags(), topicTag1, "tag1 is also displayed in the tags list.");

        LOG.info("STEP 3 - Click 'Discussions Topic List' link.");
        topicViewPage.clickDiscussionsTopicListLink();
        assertEquals(topicListPage.getTopicStatus(topic1Title), "(Updated)", "The text (Updated) appears after the title.");
        assertEquals(topicListPage.getTopicContent(topic1Title), topicNewContent, "The updated topic appears in the topic view page with the new content.");
        assertEquals(topicListPage.getTopicTags(topic1Title), topicTag1, "tag1 is also displayed in the tags list.");
    }

    @TestRail (id = "6336")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editTopicFromTopicViewPage()
    {
        //precondition
        topicListPage.navigate(siteName);

        LOG.info("STEP 1 - Click on 'View' link to view Topic2 details.");
        topicListPage.viewTopic(topic2Title);

        LOG.info("STEP 2 - Click on 'Edit' link.");
        topicViewPage.editTopic();
        assertTrue(editTopicPage.isPageDisplayed(), "The Edit Topic page is displayed.");
        assertEquals(editTopicPage.getTopicTitle(), topic2Title, "The Edit Topic page displays the selected topic title");
        assertEquals(editTopicPage.getTopicContent(), topicContent, "The Edit Topic page displays the selected topic content");
        assertEquals(editTopicPage.getTopicCurrentTagsList(), Arrays.asList(topicTag1, topicTag2), "'tag1' and 'tag2' are visible in tags area.");

        LOG.info("STEP 3 - Change topic title with 'new topic title' and also remove tag1. Click on 'Save' button. ");
        editTopicPage.typeTopicTitle(topicNewTitle);
        editTopicPage.removeTag(topicTag1);
        editTopicPage.clickSaveButton();
        assertEquals(topicViewPage.getTopicTitle(), topicNewTitle + " (Updated)", "Topic appears in the topic view page with the new title and text (Updated) after the title.");
        assertEquals(topicViewPage.getTopicContent(), topicContent, "The updated topic content didn't change.");
        assertEquals(topicViewPage.getTopicTags(), topicTag2, "Only tag2 is displayed in the tags list.");

        LOG.info("STEP 4 - Click 'Discussions Topic List' link.");
        topicViewPage.clickDiscussionsTopicListLink();
        assertEquals(topicListPage.getTopicStatus(topicNewTitle), "(Updated)", "The text (Updated) appears after the new title.");
        assertEquals(topicListPage.getTopicTags(topicNewTitle), topicTag2, "Only tag2 is displayed in the tags list.");
    }

    @TestRail (id = "6221")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelEditingTopic()
    {
        //precondition
        topicListPage.navigate(siteName);

        LOG.info("STEP 1 - Click on 'Edit' link to edit Topic1.");
        topicListPage.editTopic(topic1Title);
        assertTrue(editTopicPage.isPageDisplayed(), "The Edit Topic page is displayed.");
        assertEquals(editTopicPage.getTopicTitle(), topic1Title, "The Edit Topic page displays the selected topic title");
        assertEquals(editTopicPage.getTopicContent(), topicContent, "The Edit Topic page displays the selected topic content");

        LOG.info("STEP 2 - Modify Topic1 content: 'new content' and add also a tag, 'tag1'. Click on 'Save' button.");
        editTopicPage.typeTopicContent(topicNewContent);
        editTopicPage.addTag(topicTag1);
        editTopicPage.clickCancelButton();
        assertEquals(topicListPage.getTopicContent(topic1Title), topicContent, "Content was not updated.");
        assertEquals(topicListPage.getTopicTags(topic1Title), "(None)", "tag1 is not displayed in the tags list.");
    }
}