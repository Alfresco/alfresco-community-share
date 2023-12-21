package org.alfresco.share.sitesFeatures.discussions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Collections;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.DeleteDialog;
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
public class DeleteTopicTests extends BaseTest
{
    //@Autowired
    TopicListPage topicListPage;

    //@Autowired
    DeleteDialog deleteDialog;

    //@Autowired
    TopicViewPage topicViewPage;

    @Autowired
    protected SitePagesService sitePagesService;

    @Autowired
    SiteService siteService;

    private String topic1Title = "Topic1";
    private String topic2Title = "Topic2";
    private String topicContent = "Some content";
    private String topicTag1 = "tag1";
    private String topicTag2 = "tag2";
    private String topicReply = "Reply content";
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        siteService.addPageToSite(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), Page.DISCUSSIONS, null);

        sitePagesService.createDiscussion(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), topic1Title, topicContent, Collections.singletonList(topicTag1));
        sitePagesService.replyToDiscussion(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), topic1Title, topicReply);
        sitePagesService.createDiscussion(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), topic2Title, topicContent, Collections.singletonList(topicTag2));
        sitePagesService.replyToDiscussion(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), topic2Title, topicReply);

        topicListPage = new TopicListPage(webDriver);
        topicViewPage = new TopicViewPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);

        authenticateUsingLoginPage(user1.get());
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user1.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user1.get());
    }

    @TestRail (id = "6244")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void deleteTopicFromDiscussionsTopicListPage()
    {
        //precondition
        topicListPage.navigate(siteName.get());

        log.info("STEP 1 - Click on 'Delete' link to delete Topic1.");
        topicListPage.deleteTopic(topic1Title);
        assertEquals(deleteDialog.getHeader(), "Delete Topic", "'Delete Topic' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("discussions.deleteTopic.message"), topic1Title));

        log.info("STEP 2 - Click on 'Delete' button.");
        deleteDialog.confirmDelete();
        deleteDialog.assertVerifyDisplayedNotification("Topic deleted");
        assertFalse(topicListPage.is_TopicDisplayed(topic1Title), "Topic1 is deleted.");
    }

    @TestRail (id = "6246")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteTopicFromTopicViewPage()
    {
        //precondition
        topicListPage.navigate(siteName.get());
        topicListPage.viewTopic(topic2Title);

        log.info("STEP 1 - Click on 'Delete' link to delete Topic2.");
        topicViewPage.deleteTopic();
        assertEquals(deleteDialog.getHeader(), "Delete Topic", "'Delete Topic' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("discussions.deleteTopic.message"), topic2Title));

        log.info("STEP 2 - Click on 'Delete' button.");
        deleteDialog.confirmDeletion();
//        deleteDialog.assertVerifyDisplayedNotification("Topic deleted");
        assertFalse(topicListPage.is_TopicDisplayed(topic2Title), "Topic2 is deleted.");
        assertFalse(topicListPage.isTagDisplayed(topicTag2), "tag1 is not displayed, the list of tags is empty.");
    }

    @TestRail (id = "6245")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelDeletingTopic()
    {
        //precondition
        topicListPage.navigate(siteName.get());

        log.info("STEP 1 - Click on 'Delete' link to delete Topic1.");
        topicListPage.deleteTopic(topic1Title);
        assertEquals(deleteDialog.getHeader(), "Delete Topic", "'Delete Topic' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("discussions.deleteTopic.message"), topic1Title));

        log.info("STEP 2 - Click on 'Cancel' button.");
        deleteDialog.clickCancel();
        assertTrue(topicListPage.isTopicDisplayed(topic1Title), "Topic is not deleted, it is displayed in the list of topics.");
        assertTrue(topicListPage.isTagDisplayed(topicTag1), "tag1 is displayed on the list of tags.");
        assertEquals(topicListPage.getTopicReplies(topic1Title), "(1)", "No of replies is 1.");
    }

}
