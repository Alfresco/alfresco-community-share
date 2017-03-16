package org.alfresco.share.sitesFeatures.discussions;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.site.discussion.DeleteTopicDialog;
import org.alfresco.po.share.site.discussion.TopicListPage;
import org.alfresco.po.share.site.discussion.TopicViewPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by Claudia Agache on 8/10/2016.
 */
public class DeleteTopicTests extends ContextAwareWebTest
{
    @Autowired
    TopicListPage topicListPage;

    @Autowired
    DeleteTopicDialog deleteDialog;

    @Autowired
    TopicViewPage topicViewPage;

    @Autowired
    Notification notification;

    private String user1 = "User1" + DataUtil.getUniqueIdentifier();
    private String siteName = "Site1" + DataUtil.getUniqueIdentifier();
    private String topic1Title = "Topic1";
    private String topic2Title = "Topic2";
    private String topicContent = "Some content";
    private String topicTag1 = "tag1";
    private String topicTag2 = "tag2";
    private String topicReply = "Reply content";

    @BeforeClass
    public void setupTest()
    {
        List<Page> pagesToAdd = new ArrayList<Page>();
        pagesToAdd.add(Page.DISCUSSIONS);
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1, "lName1");
        siteService.create(user1, password, domain, siteName, "description", Site.Visibility.PUBLIC);
        siteService.addPagesToSite(user1, password, siteName, pagesToAdd);
        sitePagesService.createDiscussion(user1, password, siteName, topic1Title, topicContent, Collections.singletonList(topicTag1));
        sitePagesService.replyToDiscussion(user1, password, siteName, topic1Title, topicReply);
        sitePagesService.createDiscussion(user1, password, siteName, topic2Title, topicContent, Collections.singletonList(topicTag2));
        sitePagesService.replyToDiscussion(user1, password, siteName, topic2Title, topicReply);
        setupAuthenticatedSession(user1, password);
    }

    @TestRail(id = "6244")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteTopicFromDiscussionsTopicListPage()
    {
        //precondition
        topicListPage.navigate(siteName);

        LOG.info("STEP 1 - Click on 'Delete' link to delete Topic1.");
        topicListPage.deleteTopic(topic1Title);
        assertEquals(deleteDialog.getHeader(), "Delete Topic", "'Delete Topic' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("discussions.deleteTopic.message"), topic1Title));

        LOG.info("STEP 2 - Click on 'Delete' button.");
        deleteDialog.confirmTopicDelete();
        //assertEquals(notification.getDisplayedNotification(), language.translate("discussions.topicDeletedNotification"), "Topic deleted notification appears.");
        assertFalse(topicListPage.isTopicDisplayed(topic1Title), "Topic1 is deleted.");
    }

    @TestRail(id = "6246")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteTopicFromTopicViewPage()
    {
        //precondition
        topicListPage.navigate(siteName);
        topicListPage.viewTopic(topic2Title);

        LOG.info("STEP 1 - Click on 'Delete' link to delete Topic2.");
        topicViewPage.deleteTopic();
        assertEquals(deleteDialog.getHeader(), "Delete Topic", "'Delete Topic' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("discussions.deleteTopic.message"), topic2Title));

        LOG.info("STEP 2 - Click on 'Delete' button.");
        deleteDialog.confirmTopicDelete();
//        assertEquals(notification.getDisplayedNotification(), language.translate("discussions.topicDeletedNotification"), "Topic deleted notification appears.");
        assertFalse(topicListPage.isTopicDisplayed(topic2Title), "Topic2 is deleted.");
        assertFalse(topicListPage.isTagDisplayed(topicTag2), "tag1 is not displayed, the list of tags is empty.");
    }

    @TestRail(id = "6245")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelDeletingTopic()
    {
        //precondition
        topicListPage.navigate(siteName);

        LOG.info("STEP 1 - Click on 'Delete' link to delete Topic1.");
        topicListPage.deleteTopic(topic1Title);
        assertEquals(deleteDialog.getHeader(), "Delete Topic", "'Delete Topic' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("discussions.deleteTopic.message"), topic1Title));

        LOG.info("STEP 2 - Click on 'Cancel' button.");
        deleteDialog.clickCancel();
        assertTrue(topicListPage.isTopicDisplayed(topic1Title), "Topic is not deleted, it is displayed in the list of topics.");
        assertTrue(topicListPage.isTagDisplayed(topicTag1), "tag1 is displayed on the list of tags.");
        assertEquals(topicListPage.getTopicReplies(topic1Title), "(1)", "No of replies is 1.");
    }

}
