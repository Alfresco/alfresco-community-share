package org.alfresco.share.sitesFeatures.discussions;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.site.discussion.InsertImagePopUp;
import org.alfresco.po.share.site.discussion.InsertLinkPopUp;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.testng.Assert.*;

/**
 * Created by Claudia Agache on 8/11/2016.
 */
public class ReplyingToDiscussionTests extends ContextAwareWebTest
{
    @Autowired
    TopicViewPage topicViewPage;

    @Autowired
    TopicListPage topicListPage;

    @Autowired
    Notification notification;

    @Autowired
    InsertLinkPopUp insertLinkPopUp;

    @Autowired
    InsertImagePopUp insertImagePopUp;

    DateFormat df = new SimpleDateFormat("EE d MMM yyyy");
    String today;
    private String user1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
    private String topicTitle;
    private String topicContent = "Some content";
    private String topicReply = "Reply content";
    private String reply1 = "Reply1";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1, "lName1");
        siteService.create(user1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user1, password, siteName, Page.DISCUSSIONS, null);
        setupAuthenticatedSession(user1, password);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
        siteService.delete(adminUser,adminPassword,siteName );
    }

    @TestRail(id = "6214")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createReplyToDiscussion()
    {
        today = df.format(new Date());
        topicTitle = String.format("Topic1%s", RandomData.getRandomAlphanumeric());
        sitePagesService.createDiscussion(user1, password, siteName, topicTitle, topicContent, null);
        topicListPage.navigate(siteName);

        LOG.info("STEP 1 - In the Discussions feature click the name of a topic to open it.");
        topicListPage.clickTopicTitle(topicTitle);
        assertEquals(topicViewPage.getTopicTitle(), topicTitle, "Title is displayed.");
        assertTrue(topicViewPage.getTopicPublished().startsWith("Created on: " + today),
                "Topic was created today. Actual: [" + topicViewPage.getTopicPublished() + "]. Expected: [" + today + "]");
        assertTrue(topicViewPage.getTopicPublished().contains("Author: " + user1 + " lName1"), "The user who created the topic is User1.");
        assertEquals(topicViewPage.getTopicContent(), topicContent, "Content is displayed.");
        assertEquals(topicViewPage.getTopicReplies(), "(0)", "Topic has 0 reply.");
        assertEquals(topicViewPage.getTopicTags(), "(None)", "Topic has 0 tags.");

        LOG.info("STEP 2 - Click on 'Reply' link. ");
        topicViewPage.clickReply();
        assertEquals(topicViewPage.getReplyBoxTitle(), "Add Reply", "The Add Reply box is displayed.");

        LOG.info("STEP 3 - Add some text in the reply box 'Reply content' and click 'Create'.");
        topicViewPage.typeReply(topicReply);
        topicViewPage.submitReply();
        assertEquals(notification.getDisplayedNotification(), language.translate("discussions.replySavedNotification"), "Reply saved notification appears.");
        assertEquals(topicViewPage.getTopicReplies(), "(1)", "Topic has 1 reply.");
        assertEquals(topicViewPage.getReplyAuthor(topicReply), user1 + " lName1", user1 + " posted that reply.");
        assertTrue(topicViewPage.getReplyDate(topicReply).contains(today), "Reply was posted today.");
        assertEquals(topicViewPage.getReplyNoReplies(topicReply), "(0)", "Reply has 0 replies.");
    }

    @TestRail(id = "6215")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelCreatingReplyToDiscussion()
    {
        today = df.format(new Date());
        topicTitle = String.format("Topic1%s", RandomData.getRandomAlphanumeric());
        sitePagesService.createDiscussion(user1, password, siteName, topicTitle, topicContent, null);
        topicListPage.navigate(siteName);

        LOG.info("STEP 1 - In the Discussions feature click the name of a topic to open it.");
        topicListPage.clickTopicTitle(topicTitle);
        assertEquals(topicViewPage.getTopicTitle(), topicTitle, "Title is displayed.");
        assertTrue(topicViewPage.getTopicPublished().startsWith("Created on: " + today),
                "Topic was created today. Actual: [" + topicViewPage.getTopicPublished() + "]. Expected: [" + today + "]");
        assertTrue(topicViewPage.getTopicPublished().contains("Author: " + user1 + " lName1"), "The user who created the topic is User1.");
        assertEquals(topicViewPage.getTopicContent(), topicContent, "Content is displayed.");
        assertEquals(topicViewPage.getTopicReplies(), "(0)", "Topic has 0 reply.");
        assertEquals(topicViewPage.getTopicTags(), "(None)", "Topic has 0 tags.");

        LOG.info("STEP 2 - Click on 'Reply' link. ");
        topicViewPage.clickReply();
        assertEquals(topicViewPage.getReplyBoxTitle(), "Add Reply", "The Add Reply box is displayed.");

        LOG.info("STEP 3 - Add some text in the reply box 'Reply content' and click 'Cancel'.");
        topicViewPage.typeReply(topicReply);
        topicViewPage.cancelReply();
        assertEquals(topicViewPage.getTopicReplies(), "(0)", "Topic has 0 reply.");
    }

    @TestRail(id = "6216")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void insertLinkInReplyToDiscussion()
    {
        today = df.format(new Date());
        String linkUrl = "https://www.alfresco.com/";
        String linkText = "Alfresco site";
        String linkTitle = "Alfresco";
        topicTitle = String.format("Topic1%s", RandomData.getRandomAlphanumeric());
        sitePagesService.createDiscussion(user1, password, siteName, topicTitle, topicContent, null);
        topicListPage.navigate(siteName);
        topicListPage.clickTopicTitle(topicTitle);

        LOG.info("STEP 1 - Click on 'Reply' link. ");
        topicViewPage.clickReply();
        assertEquals(topicViewPage.getTopicReplyHeader(), "Add Reply", "The Add Reply box is displayed.");

        LOG.info("STEP 2 - Click on 'insert/edit link'.");
        topicViewPage.selectOptionFromInsertMenu("Insert link");
        assertTrue(insertLinkPopUp.isTextPresent("Insert link"), "'Insert link' pop-up is displayed.");

        LOG.info("STEP 3 - Add the following in the 'Insert Link' pop-up: Url: https://www.alfresco.com/\n" + "Text to display: Alfresco site\n"
                + "Title: Alfresco\n" + "Target: None\n" + "Click on 'Ok' button.");
        insertLinkPopUp.insertLink(linkUrl, linkText, linkTitle, "None");

        LOG.info("STEP 4 - Click on 'Create' button.");
        topicViewPage.submitReply();
        assertEquals(notification.getDisplayedNotification(), language.translate("discussions.replySavedNotification"), "Reply saved notification appears.");
        assertEquals(topicViewPage.getTopicReplies(), "(1)", "Topic has 1 reply.");

        LOG.info("STEP 5 - Click on the site link.");
        topicViewPage.clickLinkInReply(linkText, linkTitle);
        assertEquals(getBrowser().getCurrentUrl(), linkUrl, "User is redirected to: https://www.alfresco.com");
    }

    @TestRail(id = "6217")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void insertImageInReplyToDiscussion()
    {
        today = df.format(new Date());
        String imageSource = "https://www.alfresco.com/sites/www.alfresco.com/files/alfresco-logo.png";
        String imageDescription = "Alfresco logo";
        topicTitle = String.format("Topic1%s", RandomData.getRandomAlphanumeric());
        sitePagesService.createDiscussion(user1, password, siteName, topicTitle, topicContent, null);
        topicListPage.navigate(siteName);
        topicListPage.clickTopicTitle(topicTitle);

        LOG.info("STEP 1 - Click on 'Reply' link. ");
        topicViewPage.clickReply();
        assertEquals(topicViewPage.getTopicReplyHeader(), "Add Reply", "The Add Reply box is displayed.");

        LOG.info("STEP 2 - Click on 'insert/edit image'.");
        topicViewPage.typeReply(topicReply);
        topicViewPage.selectOptionFromInsertMenu("Insert image");
        assertEquals(insertImagePopUp.getPopupTitle(), "Insert/edit image", "'Insert/edit image' pop-up is displayed.");

        LOG.info("STEP 3 - Add the following in the 'Insert/edit Image' pop-up:\n"
                + "Source: https://www.alfresco.com/sites/www.alfresco.com/files/alfresco-logo.png\n" + "Image description: Alfresco logo\n"
                + "Click on 'OK' button.");
        insertImagePopUp.insertImage(imageSource, imageDescription);

        LOG.info("STEP 4 - Click on 'Create' button.");
        topicViewPage.submitReply();
        assertEquals(notification.getDisplayedNotification(), language.translate("discussions.replySavedNotification"), "Reply saved notification appears.");
        assertEquals(topicViewPage.getTopicReplies(), "(1)", "Topic has 1 reply.");
        assertTrue(topicViewPage.isImageDisplayedInReply(topicReply, imageSource), "Image is displayed.");
    }

    @TestRail(id = "6218")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void replyToAReply()
    {
        today = df.format(new Date());
        topicTitle = String.format("Topic1%s", RandomData.getRandomAlphanumeric());
        sitePagesService.createDiscussion(user1, password, siteName, topicTitle, topicContent, null);
        sitePagesService.replyToDiscussion(user1, password, siteName, topicTitle, topicReply);
        topicListPage.navigate(siteName);
        topicListPage.clickTopicTitle(topicTitle);

        LOG.info("STEP 1 - Click on 'Reply' link for topic reply.");
        topicViewPage.replyToReply(topicReply);
        assertEquals(topicViewPage.getReplyBoxTitle(), "Reply to " + user1 + " lName1", "The Reply to box is displayed.");

        LOG.info("STEP 2 - Add some content in the reply box and click 'Create'.");
        topicViewPage.typeReply(reply1);
        topicViewPage.submitReply();
        getBrowser().refresh(); // necessary to appear Hide replies/Show replies link
        assertEquals(topicViewPage.getTopicReplies(), "(2)", "Topic has 2 replies.");
        assertEquals(topicViewPage.getReplyNoReplies(topicReply), "(1)", "Topic reply has now 1 reply.");
        assertTrue(topicViewPage.isReplyIndentedFromItsParent(reply1, topicReply), "The reply appears indented from its reply.");

        LOG.info("STEP 3 - Click on 'Hide replies'.");
        topicViewPage.showHideReplies(topicReply);
        assertFalse(topicViewPage.isReplyVisible(reply1), "The reply to topic reply is no longer displayed.");

        LOG.info("STEP 4 - Click on 'Show replies'.");
        topicViewPage.showHideReplies(topicReply);
        assertTrue(topicViewPage.isReplyVisible(reply1), "The reply to topic reply is no longer displayed.");
    }

    @TestRail(id = "6219")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editReplyAddedToTopic()
    {
        today = df.format(new Date());
        topicTitle = String.format("Topic1%s", RandomData.getRandomAlphanumeric());
        sitePagesService.createDiscussion(user1, password, siteName, topicTitle, topicContent, null);
        sitePagesService.replyToDiscussion(user1, password, siteName, topicTitle, topicReply);
        topicListPage.navigate(siteName);
        topicListPage.clickTopicTitle(topicTitle);

        LOG.info("STEP 1 - Click on 'Edit' link for Reply1.");
        topicViewPage.editReply(topicReply);
        assertEquals(topicViewPage.getReplyBoxTitle(), "Edit Reply", "The Edit Reply box is displayed.");
        assertEquals(topicViewPage.getReplyBoxContent(), topicReply, "The Edit Reply box has reply content.");

        LOG.info("STEP 2 - Add a new content in the add reply box: 'reply1'. Click on 'Update' button.");
        topicViewPage.typeReply(reply1);
        topicViewPage.submitReply();
        assertEquals(notification.getDisplayedNotification(), language.translate("discussions.replySavedNotification"), "Reply saved notification appears.");
        assertEquals(topicViewPage.getReplyStatus(reply1), "(Updated)", "(Updated) text appears near reply author.");
    }
}
