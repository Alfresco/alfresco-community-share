package org.alfresco.share.sitesFeatures.discussions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.discussion.*;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by Claudia Agache on 8/11/2016.
 */
@Slf4j
public class ReplyingToDiscussionTests extends BaseTest
{
    @Autowired
    SiteService siteService;
    @Autowired
    SitePagesService sitePagesService;
    private TopicViewPage topicViewPage;
    private TopicListPage topicListPage;
    private InsertImagePopUp insertImagePopUp;
    private InsertLinkPopUp insertLinkPopUp;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    DateFormat df = new SimpleDateFormat("EE d MMM yyyy");
    String today;
    private String topicTitle;
    private String topicContent = "Some content";
    private String topicReply = "Reply content";
    private String reply1 = "Reply1";
    private String password = "password";

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());
        siteService.addPageToSite(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), Page.DISCUSSIONS, null);

        topicListPage = new TopicListPage(webDriver);
        insertImagePopUp = new InsertImagePopUp(webDriver);
        insertLinkPopUp = new InsertLinkPopUp(webDriver);
        topicViewPage = new TopicViewPage(webDriver);

        authenticateUsingLoginPage(user1.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user1.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user1.get());
    }

    @TestRail (id = "6214")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createReplyToDiscussion()
    {
        today = df.format(new Date());
        topicTitle = String.format("Topic1%s", RandomData.getRandomAlphanumeric());
        sitePagesService.createDiscussion(user1.get().getUsername(), password, siteName.get().getId(), topicTitle, topicContent, null);
        topicListPage.navigate(siteName.get().getId());

        log.info("STEP 1 - In the Discussions feature click the name of a topic to open it.");
        topicListPage.clickTopicTitle(topicTitle);
        assertEquals(topicViewPage.getTopicTitle(), topicTitle, "Title is displayed.");
        assertTrue(topicViewPage.getTopicPublished().startsWith("Created on: " + today),
            "Topic was created today. Actual: [" + topicViewPage.getTopicPublished() + "]. Expected: [" + today + "]");
        assertEquals(topicViewPage.getTopicContent(), topicContent, "Content is displayed.");
        assertEquals(topicViewPage.getTopicReplies(), "(0)", "Topic has 0 reply.");
        assertEquals(topicViewPage.getTopicTags(), "(None)", "Topic has 0 tags.");

        log.info("STEP 2 - Click on 'Reply' link. ");
        topicViewPage.clickReply();
        assertEquals(topicViewPage.getReplyBoxTitle(), "Add Reply", "The Add Reply box is displayed.");

        log.info("STEP 3 - Add some text in the reply box 'Reply content' and click 'Create'.");
        topicViewPage.typeReply(topicReply);
        topicViewPage.submitReply();
        assertEquals(topicViewPage.getTopicReplies(), "(1)", "Topic has 1 reply.");
        assertTrue(topicViewPage.getReplyDate(topicReply).contains(today), "Reply was posted today.");
        assertEquals(topicViewPage.getReplyNoReplies(topicReply), "(0)", "Reply has 0 replies.");
    }

    @TestRail (id = "6215")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelCreatingReplyToDiscussion()
    {
        today = df.format(new Date());
        topicTitle = String.format("Topic1%s", RandomData.getRandomAlphanumeric());
        sitePagesService.createDiscussion(user1.get().getUsername(), password, siteName.get().getId(), topicTitle, topicContent, null);
        topicListPage.navigate(siteName.get().getId());

        log.info("STEP 1 - In the Discussions feature click the name of a topic to open it.");
        topicListPage.clickTopicTitle(topicTitle);
        assertEquals(topicViewPage.getTopicTitle(), topicTitle, "Title is displayed.");
        assertTrue(topicViewPage.getTopicPublished().startsWith("Created on: " + today),
            "Topic was created today. Actual: [" + topicViewPage.getTopicPublished() + "]. Expected: [" + today + "]");
        assertEquals(topicViewPage.getTopicContent(), topicContent, "Content is displayed.");
        assertEquals(topicViewPage.getTopicReplies(), "(0)", "Topic has 0 reply.");
        assertEquals(topicViewPage.getTopicTags(), "(None)", "Topic has 0 tags.");

        log.info("STEP 2 - Click on 'Reply' link. ");
        topicViewPage.clickReply();
        assertEquals(topicViewPage.getReplyBoxTitle(), "Add Reply", "The Add Reply box is displayed.");

        log.info("STEP 3 - Add some text in the reply box 'Reply content' and click 'Cancel'.");
        topicViewPage.typeReply(topicReply);
        topicViewPage.cancelReply();
        assertEquals(topicViewPage.getTopicReplies(), "(0)", "Topic has 0 reply.");
    }

    @TestRail (id = "6216")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void insertLinkInReplyToDiscussion()
    {
        today = df.format(new Date());
        String linkUrl = "https://www.alfresco.com/";
        String linkText = "Alfresco site";
        String linkTitle = "Alfresco";
        topicTitle = String.format("Topic1%s", RandomData.getRandomAlphanumeric());
        sitePagesService.createDiscussion(user1.get().getUsername(), password, siteName.get().getId(), topicTitle, topicContent, null);
        topicListPage.navigate(siteName.get().getId());
        topicListPage.clickTopicTitle(topicTitle);

        log.info("STEP 1 - Click on 'Reply' link. ");
        topicViewPage.clickReply();
        assertEquals(topicViewPage.getTopicReplyHeader(), "Add Reply", "The Add Reply box is displayed.");

        log.info("STEP 2 - Click on 'insert/edit link'.");
        topicViewPage.selectOptionFromInsertMenus("Link");
        assertTrue(insertLinkPopUp.isTextPresent("Insert link"), "'Insert link' pop-up is displayed.");

        log.info("STEP 3 - Add the following in the 'Insert Link' pop-up: Url: https://www.alfresco.com/\n" + "Text to display: Alfresco site\n"
            + "Title: Alfresco\n" + "Target: None\n" + "Click on 'Ok' button.");
        insertLinkPopUp.insertLink(linkUrl, linkText, linkTitle, "None");

        log.info("STEP 4 - Click on 'Create' button.");
        topicViewPage.submitReply();
        assertEquals(topicViewPage.getTopicReplies(), "(1)", "Topic has 1 reply.");
    }

    @TestRail (id = "6217")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void insertImageInReplyToDiscussion()
    {
        today = df.format(new Date());
        String imageSource = "https://www.alfresco.com/sites/www.alfresco.com/files/alfresco-logo.png";
        String imageDescription = "Alfresco logo";
        topicTitle = String.format("Topic1%s", RandomData.getRandomAlphanumeric());
        sitePagesService.createDiscussion(user1.get().getUsername(), password, siteName.get().getId(), topicTitle, topicContent, null);
        topicListPage.navigate(siteName.get().getId());
        topicListPage.clickTopicTitle(topicTitle);

        log.info("STEP 1 - Click on 'Reply' link. ");
        topicViewPage.clickReply();
        assertEquals(topicViewPage.getTopicReplyHeader(), "Add Reply", "The Add Reply box is displayed.");

        log.info("STEP 2 - Click on 'insert/edit image'.");
        topicViewPage.typeReply(topicReply);
        topicViewPage.selectOptionFromInsertMenu("Image");
        assertEquals(insertImagePopUp.getPopupTitle(), "Insert/edit image", "'Insert/edit image' pop-up is displayed.");

        log.info("STEP 3 - Add the following in the 'Insert/edit Image' pop-up:\n"
            + "Source: https://www.alfresco.com/sites/www.alfresco.com/files/alfresco-logo.png\n" + "Image description: Alfresco logo\n"
            + "Click on 'OK' button.");
        insertImagePopUp.insertImage(imageSource, imageDescription);

        log.info("STEP 4 - Click on 'Create' button.");
        topicViewPage.submitReply();
        assertEquals(topicViewPage.getTopicReplies(), "(1)", "Topic has 1 reply.");
        assertTrue(topicViewPage.isImageDisplayedInReply(topicReply, imageSource), "Image is displayed.");
    }

    @TestRail (id = "6218")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES})
    public void replyToAReply()
    {
        today = df.format(new Date());
        topicTitle = String.format("Topic1%s", RandomData.getRandomAlphanumeric());
        sitePagesService.createDiscussion(user1.get().getUsername(), password, siteName.get().getId(), topicTitle, topicContent, null);
        sitePagesService.replyToDiscussion(user1.get().getUsername(), password, siteName.get().getId(), topicTitle, topicReply);
        topicListPage.navigate(siteName.get().getId());
        topicListPage.clickTopicTitle(topicTitle);

        log.info("STEP 1 - Add some content in the reply box and click 'Create'.");
        topicViewPage.replyToReply(topicReply);
        topicViewPage.typeReply(reply1);
        topicViewPage.submitReply();
        topicViewPage.refreshPage();
        assertEquals(topicViewPage.getTopicReplies(), "(2)", "Topic has 2 replies.");
        assertEquals(topicViewPage.getReplyNoReplies(topicReply), "(1)", "Topic reply has now 1 reply.");
        assertTrue(topicViewPage.isReplyIndentedFromItsParent(reply1, topicReply), "The reply appears indented from its reply.");

        log.info("STEP 2 - Click on 'Hide replies'.");
        topicViewPage.showHideReplies(topicReply);
        assertFalse(topicViewPage.is_ReplyVisible(reply1), "The reply to topic reply is no longer displayed.");

        log.info("STEP 3 - Click on 'Show replies'.");
        topicViewPage.showHideReplies(topicReply);
        assertTrue(topicViewPage.is_ReplyVisible(reply1), "The reply to topic reply is no longer displayed.");
    }

    @TestRail (id = "6219")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editReplyAddedToTopic()
    {
        today = df.format(new Date());
        topicTitle = String.format("Topic1%s", RandomData.getRandomAlphanumeric());
        sitePagesService.createDiscussion(user1.get().getUsername(), password, siteName.get().getId(), topicTitle, topicContent, null);
        sitePagesService.replyToDiscussion(user1.get().getUsername(), password, siteName.get().getId(), topicTitle, topicReply);
        topicListPage.navigate(siteName.get().getId());
        topicListPage.clickTopicTitle(topicTitle);

        log.info("STEP 1 - Click on 'Edit' link for Reply1.");
        topicViewPage.editReply(topicReply);
        assertEquals(topicViewPage.getReplyBoxTitle(), "Edit Reply", "The Edit Reply box is displayed.");
        assertEquals(topicViewPage.getReplyBoxContent(), topicReply, "The Edit Reply box has reply content.");

        log.info("STEP 2 - Add a new content in the add reply box: 'reply1'. Click on 'Update' button.");
        topicViewPage.typeReply(reply1);
        topicViewPage.submitReply();
        assertEquals(topicViewPage.getReplyStatus(reply1), "(Updated)", "(Updated) text appears near reply author.");
    }
}
