package org.alfresco.share.sitesFeatures.discussions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.discussion.CreateNewTopicPage;
import org.alfresco.po.share.site.discussion.TopicListPage;
import org.alfresco.po.share.site.discussion.TopicViewPage;

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

@Slf4j

/**
 * Created by Claudia Agache on 8/9/2016.
 */
public class CreateNewTopicTests extends BaseTest
{
    //@Autowired
    TopicListPage topicListPage;

    //@Autowired
    CreateNewTopicPage createNewTopicPage;

    // @Autowired
    TopicViewPage topicViewPage;
    @Autowired
    SiteService siteService;
    private String random = RandomData.getRandomAlphanumeric();
    private String topicTitle = "Topic-" + random;
    private String topicContent = "Some content";
    private String topicTag = "tag1";
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        userName.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(userName.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());

        siteService.addPageToSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), Page.DISCUSSIONS, null);

        topicListPage = new TopicListPage(webDriver);
        createNewTopicPage = new CreateNewTopicPage(webDriver);
        topicViewPage = new TopicViewPage(webDriver);

        authenticateUsingLoginPage(userName.get());
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());
    }

    @TestRail (id = "C6206")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void createNewTopic()
    {
        topicListPage.navigate(siteName.get());

        log.info("STEP 1 - Click on 'New topic' button.");
        topicListPage.clickNewTopicButton();
        assertTrue(createNewTopicPage.isPageDisplayed(), "'Create New Topic' page is opened.");
        log.info("STEP 2 - Add title(e.g: Topic1), text (e.g: Some content) and a tag(e.g: tag1). Click on 'Save' button.");
        createNewTopicPage.typeTopicTitle(topicTitle);
        createNewTopicPage.typeTopicContent(topicContent);
        createNewTopicPage.addTag(topicTag);
        createNewTopicPage.clickSaveButton();
        assertEquals(topicViewPage.getTopicTitle(), topicTitle, "Title is displayed.");
        assertEquals(topicViewPage.getTopicContent(), topicContent, "Content is displayed.");
        assertEquals(topicViewPage.getTopicTags(), topicTag, "Tag is displayed.");

        log.info("STEP 3 - Click 'Discussions Topic List' link.");
        topicViewPage.clickDiscussionsTopicListLink();
        assertTrue(topicListPage.isTopicDisplayed(topicTitle), topicTitle + " is listed in 'New Topics' list.");

    }

    @TestRail (id = "C6207")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelCreatingNewTopic()
    {
        topicListPage.navigate(siteName.get());

        log.info("STEP 1 - Click on 'New topic' button.");
        topicListPage.clickNewTopicButton();
        assertTrue(createNewTopicPage.isPageDisplayed(), "'Create New Topic' page is opened.");

        log.info("STEP 2 - Add title, text and a tag. Click  'Cancel' button");
        createNewTopicPage.typeTopicTitle(topicTitle);
        createNewTopicPage.typeTopicContent(topicContent);
        createNewTopicPage.addTag(topicTag);
        createNewTopicPage.clickCancelButton();
        assertEquals(topicListPage.getMessageDisplayed(), language.translate("discussions.noTopicsFound"), "'No topics found' message=");
    }
}