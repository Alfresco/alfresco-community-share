package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.cmis.CmisWrapper;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.DataListsService.DataList;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet;
import org.alfresco.po.share.dashlet.SiteActivitiesDaysRangeFilter;
import org.alfresco.po.share.navigation.MenuNavigationBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.discussion.TopicListPage;
import org.alfresco.po.share.site.discussion.TopicViewPage;
import org.alfresco.po.share.site.link.LinkPage;
import org.alfresco.po.share.site.wiki.WikiListPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.Utility;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class MyActivitiesTests extends ContextAwareWebTest
{
    @Autowired
    MyActivitiesDashlet myActivitiesDashlet;

    @Autowired
    BlogPostListPage blogPostPage;

    @Autowired
    WikiListPage wikiListPage;

    @Autowired
    WikiPage wikiPage;

    @Autowired
    LinkPage linkPage;

    @Autowired
    DocumentDetailsPage docDetailsPage;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    TopicListPage topicListPage;

    @Autowired
    TopicViewPage topicViewPage;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    CalendarPage calendarPage;

    @Autowired
    MenuNavigationBar menuNavigationBar;

    @Autowired
    MyDocumentsDashlet myDocumentsDashlet;

    @Autowired
    private CmisWrapper cmisApi;

    private String siteName;
    private String userName;
    private String userNameB;
    private String linkTitle;
    private String blogTitle;
    private String eventName;
    private String datalistName;
    private String discussionTitle;
    private String fileName;
    private String documentName;
    private String documentNameB;
    private String wikiTitle;
    private DateTime today = new DateTime();

    private void createObjectsForUserName(String userName, String siteName, String linkTitle, String blogTitle, String eventName, String datalistName, String discussionTitle, String fileName, String documentName, String wikiTitle)
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, documentName, documentName + " content"); 
        sitePagesService.createBlogPost(userName, password, siteName, blogTitle, blogTitle + " content", false, null); //TODO check why blog creation isn't in My Activities dashlet
        sitePagesService.addCalendarEvent(userName, password, siteName, eventName, "Where " + eventName, "description " + eventName, today.toDate(), today.toDate(), "6:00 PM",
                "8:00 PM", false, null);
        dataListsService.createDataList(userName, password, siteName, DataList.CONTACT_LIST, datalistName, "Contact list for user " + userName); //TODO check why data list creation isn't in My Activities dashlet
        sitePagesService.createDiscussion(userName, password, siteName, discussionTitle, discussionTitle + " content", null);       
        //contentService.uploadFileInSite(userName, password, siteName, testDataFolder + fileName);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, fileName);

        sitePagesService.createLink(userName, password, siteName, linkTitle, "www.google.com", linkTitle + " description", true, null);
        sitePagesService.createWiki(userName, password, siteName, wikiTitle, wikiTitle + " content ", null);
    }

    private void updateObjectsForUserName(String userName, String siteName, String linkTitle, String blogTitle, String eventName, String datalistName, String discussionTitle, String fileName, String documentName, String wikiTitle) throws Exception
    {
        sitePagesService.updateBlogPost(userName, password, siteName, blogTitle, "New" + blogTitle, "New " + blogTitle + " content", false);
        sitePagesService.updateEvent(userName, password, siteName, eventName, eventName, "Where " + eventName, today.toDate(), today.toDate(), "7:00 PM", "9:00 PM", false); //TODO check why event update isn't in My Activities dashlet
        dataListsService.updateDataList(userName, password, siteName, datalistName, "New" + datalistName, "New contact list for user " + userName);
        sitePagesService.updateDiscussion(userName, password, siteName, discussionTitle, "New" + discussionTitle, "New " + discussionTitle + " content", null);
        contentService.updateDocumentContent(userName, password, siteName, DocumentType.TEXT_PLAIN, documentName, "New " + documentName + " content");
       contentService.updateDocumentContent(userName, password, siteName, DocumentType.TEXT_PLAIN, fileName, "New  content");

        FileModel fileNameModel = new FileModel(fileName, FileType.TEXT_PLAIN);
        fileNameModel.setCmisLocation(Utility.buildPath(cmisApi.getSitesPath(), siteName,"/documentLibrary/", fileName));
        FileModel documentModel = new FileModel(documentName, FileType.TEXT_PLAIN);
        documentModel.setCmisLocation(Utility.buildPath(cmisApi.getSitesPath(),  siteName, "/documentLibrary/", documentName));

        cmisApi.authenticateUser(new UserModel(userName, password))
                .usingResource(fileNameModel).setContent("New Content").assertThat().contentIs("New Content")
                    .then().usingResource(documentModel).setContent("New" + documentName + " content");



        sitePagesService.updateLink(userName, password, siteName, linkTitle, "New" + linkTitle, "www.google.com", linkTitle + " description", true, null);
        sitePagesService.updateWikiPage(userName, password, siteName, wikiTitle, "New " + wikiTitle, "New " + wikiTitle + " content ", null);
    }

    private void deleteObjectsForUserName(String userName, String siteName, String linkTitle, String blogTitle, String eventName, String datalistName, String discussionTitle, String fileName, String documentName, String wikiTitle)
    {
        sitePagesService.deleteBlogPost(userName, password, siteName, blogTitle, false);
        sitePagesService.removeEvent(userName, password, siteName, eventName, "Where " + eventName, today.toDate(), today.toDate(), "6:00 PM", "8:00 PM", false);
        dataListsService.deleteDataList(userName, password, siteName, datalistName);   
        contentService.deleteDocument(userName, password, siteName, documentName);
        contentService.deleteDocument(userName, password, siteName, fileName);
        sitePagesService.deleteLink(userName, password, siteName, linkTitle);
        sitePagesService.deleteWikiPage(userName, password, siteName, wikiTitle);
        sitePagesService.deleteDiscussion(userName, password, siteName, discussionTitle);
    }

    @TestRail(id = "C2111")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void noActivitiesCreated()
    {
        // preconditions
        String uniqueIdentifier = String.format("-C2111%s", RandomData.getRandomAlphanumeric());
        userName = "User-" + uniqueIdentifier;
        siteName = "Site-" + uniqueIdentifier;
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        userDashboardPage.navigate(userName);
        assertEquals(myActivitiesDashlet.getDashletTitle(), "My Activities", "My Activities dashlet is displayed");

        LOG.info("STEP 1-Verify 'My Activities' dashlet");
        assertEquals(myActivitiesDashlet.getEmptyDashletMessage(), language.translate("myactivities.empty"), "'My Activities' dashlet is empty.");

        LOG.info("STEP 2-Verify the available actions");
        assertTrue(myActivitiesDashlet.isRssFeedButtonDisplayed(), "Subscribe to RSS Feed button is displayed");
        assertTrue(myActivitiesDashlet.isHelpIconDisplayed(DashletHelpIcon.MY_ACTIVITIES), "Help icon is displayed.");
        List<String> expectedUserActivities = Arrays.asList(language.translate("siteActivities.filter.mine"), language.translate("siteActivities.filter.everyoneElse"),
                language.translate("siteActivities.filter.everyone"), language.translate("siteActivities.filter.meFollowing"));
        assertEquals(myActivitiesDashlet.getMyActivitiesFilterOptions(), expectedUserActivities,
                "User Activities dropdown options are as expected.");
        assertTrue(myActivitiesDashlet.isMyActivitiesOptionSelected(language.translate("siteActivities.filter.everyone")), "'Everyone's activities' option is selected by default.");
        assertTrue(myActivitiesDashlet.isHistoryOptionSelected(SiteActivitiesDaysRangeFilter.SEVEN_DAYS), "'in the last 7 days' option is selected by default.");

        LOG.info("STEP 3 - Click '?' icon");
        myActivitiesDashlet.clickOnHelpIcon(DashletHelpIcon.MY_ACTIVITIES);
        assertTrue(myActivitiesDashlet.isBalloonDisplayed(), "Help balloon is displayed");
        assertEquals(myActivitiesDashlet.getHelpBalloonMessage(), language.translate("myActivities.help"), "Help balloon text");

        LOG.info("Step 4: Click 'X' icon on balloon popup");
        myActivitiesDashlet.closeHelpBalloon();
        assertFalse(myActivitiesDashlet.isBalloonDisplayed(), "Help balloon isn't displayed");

        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

        siteService.delete(adminUser,adminPassword,siteName );
    }

    @TestRail(id = "C2112")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void someActivitiesCreated()
    {
        // preconditions
        String uniqueIdentifier = String.format("-C2112%s", RandomData.getRandomAlphanumeric());
        userName = "User" + uniqueIdentifier;
        siteName = "Site" + uniqueIdentifier;
        linkTitle = "Link" + uniqueIdentifier;
        blogTitle = "Blog" + uniqueIdentifier;
        eventName = "Event" + uniqueIdentifier;
        datalistName = "Datalist" + uniqueIdentifier;
        discussionTitle = "Disc" + uniqueIdentifier;
        fileName = "File-C2112.txt";
        documentName = "Doc" + uniqueIdentifier;
        wikiTitle = "Wiki" + uniqueIdentifier;
        Map<String, String> activitiesObjects = new HashMap<>();
        activitiesObjects.put("link", linkTitle);
        activitiesObjects.put("calendar event", eventName);
        activitiesObjects.put("discussion", discussionTitle);
        activitiesObjects.put("document", documentName);
        activitiesObjects.put("file", fileName);
        activitiesObjects.put("wiki page", wikiTitle);

        createObjectsForUserName(userName, siteName, linkTitle, blogTitle, eventName, datalistName, discussionTitle, fileName, documentName, wikiTitle);
        setupAuthenticatedSession(userName, password);
        userDashboardPage.navigate(userName);

        LOG.info("STEP 1-Verify type icon for every activity");
        for(String activityName: activitiesObjects.keySet())
        {
            String activity = String.format("%s %s created %s %s in %s", userName, userName, activityName, activitiesObjects.get(activityName), siteName);
            switch(activityName)
            {
                case "document":
                    activity = activity.replace("created", "added"); break;
                case "discussion":
                    activity = activity.replace("created", "started"); break;
                case "file":
                {
                    activity = activity.replace("created", "added");
                    activity = activity.replace("file", "document");
                    break;
                }
                case "data list":
                    activity = activity.replace(activitiesObjects.get(activityName), activitiesObjects.get(activityName) + " (Contact List)"); break;
            }
            assertTrue(myActivitiesDashlet.isActivityPresentInActivitiesDashlet(activity), "Activity link: " + activity + " is present in My Activities dashlet.");
        }

         LOG.info("STEP 3-Go back to User Dashboard. Click event's name link");

         myActivitiesDashlet.clickOnItemNameFromActivityList(eventName, calendarPage);
         assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/calendar?date=" + today.toString("YYYY-MM-dd")), "'Calendar' page is opened.");

         LOG.info("STEP5-Go back to User Dashboard. Click topic's name link");
         userDashboardPage.navigateByMenuBar();
         myActivitiesDashlet.clickOnItemNameFromActivityList(discussionTitle, topicViewPage);
         assertTrue(getBrowser().getCurrentUrl().contains(siteName + "/discussions-topicview"), "'Discussions' page is opened.");

         LOG.info("STEP6-Go back to User Dashboard. Click content's name link");
         userDashboardPage.navigateByMenuBar();
         myActivitiesDashlet.clickOnItemNameFromActivityList(fileName, docDetailsPage);
         assertTrue(getBrowser().getCurrentUrl().contains(siteName + "/document-details"), "'Document details' page is opened.");

         LOG.info("STEP7-Go back to User Dashboard. Click document's name link");
         menuNavigationBar.goTo(userDashboardPage);
         myActivitiesDashlet.clickOnItemNameFromActivityList(documentName, docDetailsPage);
         assertTrue(getBrowser().getCurrentUrl().contains(siteName + "/document-details"), "'Document details' page is opened.");

         LOG.info("STEP8-Go back to User Dashboard. Click link's name link");
         menuNavigationBar.goTo(userDashboardPage);
         myActivitiesDashlet.clickOnItemNameFromActivityList(linkTitle, linkPage);
         assertTrue(getBrowser().getCurrentUrl().contains(siteName + "/links"), "'Links' page is opened.");

         LOG.info("STEP9-Go back to User Dashboard. Click wiki page's name link");
         menuNavigationBar.goTo(userDashboardPage);
         myActivitiesDashlet.clickOnItemNameFromActivityList(wikiTitle, wikiPage);
         assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/wiki-page?title=" + wikiTitle), "'Wiki' page is opened.");

        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
         siteService.delete(adminUser,adminPassword,siteName );
    }


    @TestRail(id = "C2113")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void someActivitiesUpdated() throws Exception
    {
        // preconditions
        String uniqueIdentifier = String.format("-C2113%s", RandomData.getRandomAlphanumeric());
        userName = "User" + uniqueIdentifier;
        siteName = "Site" + uniqueIdentifier;
        linkTitle = "Link" + uniqueIdentifier;
        blogTitle = "Blog" + uniqueIdentifier;
        eventName = "Event" + uniqueIdentifier;
        datalistName = "Datalist" + uniqueIdentifier;
        discussionTitle = "Disc" + uniqueIdentifier;
        fileName = "File-C2113.txt";
        documentName = "Doc" + uniqueIdentifier + ".txt";
        wikiTitle = "Wiki" + uniqueIdentifier;
        Map<String, String> activitiesObjects = new HashMap<>();
        activitiesObjects.put("link", "New" + linkTitle);
        activitiesObjects.put("discussion", "New" + discussionTitle);
        activitiesObjects.put("document", documentName);
        activitiesObjects.put("file", fileName);
        activitiesObjects.put("wiki page", wikiTitle);

        createObjectsForUserName(userName, siteName, linkTitle, blogTitle, eventName, datalistName, discussionTitle, fileName, documentName, wikiTitle);
        updateObjectsForUserName(userName, siteName, linkTitle, blogTitle, eventName, datalistName, discussionTitle, fileName, documentName, wikiTitle);
        setupAuthenticatedSession(userName, password);
        userDashboardPage.navigate(userName);

        LOG.info("STEP 1-Verify type icon for every activity");
        for(String activityName: activitiesObjects.keySet())
        {
            String activity = String.format("%s %s updated %s %s in %s", userName, userName, activityName, activitiesObjects.get(activityName), siteName);
            if(activityName.equals("file"))
                activity = activity.replace("file", "document");

            assertTrue(myActivitiesDashlet.isActivityPresentInActivitiesDashlet(activity), "Activity link: " + activity + " is present in My Activities dashlet.");
        }

         LOG.info("STEP 3-Go back to User Dashboard. Click event's name link");
         myActivitiesDashlet.clickOnItemNameFromActivityList(eventName, calendarPage);
         assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/calendar?date=" + today.toString("YYYY-MM-dd")), "'Calendar' page is opened.");

         LOG.info("STEP5-Go back to User Dashboard. Click topic's name link");
         userDashboardPage.navigateByMenuBar();
         myActivitiesDashlet.clickOnItemNameFromActivityList("New" + discussionTitle, topicViewPage);
         assertTrue(getBrowser().getCurrentUrl().contains(siteName + "/discussions-topicview"), "'Discussions' page is opened.");

         LOG.info("STEP6-Go back to User Dashboard. Click content's name link");
         userDashboardPage.navigateByMenuBar();
         myActivitiesDashlet.clickOnItemNameFromActivityList(fileName, docDetailsPage);
         assertTrue(getBrowser().getCurrentUrl().contains(siteName + "/document-details"), "'Document details' page is opened.");

         LOG.info("STEP7-Go back to User Dashboard. Click document's name link");
         menuNavigationBar.goTo(userDashboardPage);
         myActivitiesDashlet.clickOnItemNameFromActivityList(documentName, docDetailsPage);
         assertTrue(getBrowser().getCurrentUrl().contains(siteName + "/document-details"), "'Document details' page is opened.");

         LOG.info("STEP8-Go back to User Dashboard. Click link's name link");
         menuNavigationBar.goTo(userDashboardPage);
         myActivitiesDashlet.clickOnItemNameFromActivityList("New" + linkTitle, linkPage);
         assertTrue(getBrowser().getCurrentUrl().contains(siteName + "/links"), "'Links' page is opened.");

         LOG.info("STEP9-Go back to User Dashboard. Click wiki page's name link");
         menuNavigationBar.goTo(userDashboardPage);
         myActivitiesDashlet.clickOnItemNameFromActivityList(wikiTitle, wikiPage);
         assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/wiki-page?title=" + wikiTitle), "'Wiki' page is opened.");

        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser,adminPassword,siteName );
    }


    @TestRail(id = "C2114")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void someActivitiesDeleted()
    {
        // preconditions
        String uniqueIdentifier = String.format("-C2114%s", RandomData.getRandomAlphanumeric());
        userName = "User" + uniqueIdentifier;
        siteName = "Site" + uniqueIdentifier;
        linkTitle = "Link" + uniqueIdentifier;
        blogTitle = "Blog" + uniqueIdentifier;
        eventName = "Event" + uniqueIdentifier;
        datalistName = "Datalist" + uniqueIdentifier;
        discussionTitle = "Disc" + uniqueIdentifier;
        fileName = "File-C2112.txt";
        documentName = "Doc" + uniqueIdentifier;
        wikiTitle = "Wiki" + uniqueIdentifier;
        Map<String, String> activitiesObjects = new HashMap<>();
        activitiesObjects.put("link", linkTitle);
        activitiesObjects.put("blog post", blogTitle);
        activitiesObjects.put("calendar event", eventName);
        activitiesObjects.put("discussion", discussionTitle);
        activitiesObjects.put("document", documentName);
        activitiesObjects.put("file", fileName);
        activitiesObjects.put("wiki page", wikiTitle);

        createObjectsForUserName(userName, siteName, linkTitle, blogTitle, eventName, datalistName, discussionTitle, fileName, documentName, wikiTitle);
        deleteObjectsForUserName(userName, siteName, linkTitle, blogTitle, eventName, datalistName, discussionTitle, fileName, documentName, wikiTitle);
        setupAuthenticatedSession(userName, password);
        userDashboardPage.navigate(userName);

        LOG.info("STEP 1-Verify type icon for every activity");
        for(String activityName: activitiesObjects.keySet())
        {
            String activity = String.format("%s %s deleted %s %s in %s", userName, userName, activityName, activitiesObjects.get(activityName), siteName);
            if(activityName.equals("file"))
                activity = activity.replace("file", "document");

            assertTrue(myActivitiesDashlet.isActivityPresentInActivitiesDashlet(activity), "Activity link: " + activity + " is present in My Activities dashlet.");
        }

         LOG.info("STEP 2-Click blog post's name link");
         myActivitiesDashlet.clickOnItemNameFromActivityList(blogTitle, blogPostPage);
         assertTrue(getBrowser().getCurrentUrl().contains("blog-postlist"), "'Blog' page is opened.");

         LOG.info("STEP 3-Go back to User Dashboard. Click event's name link");
         menuNavigationBar.goTo(userDashboardPage);
         myActivitiesDashlet.clickOnItemNameFromActivityList(eventName, calendarPage);
         assertTrue(getBrowser().getCurrentUrl().endsWith(siteName + "/calendar?date=" + today.toString("YYYY-MM-dd")), "'Calendar' page is opened.");

         LOG.info("STEP5-Go back to User Dashboard. Click topic's name link");
         menuNavigationBar.goTo(userDashboardPage);
         myActivitiesDashlet.clickOnItemNameFromActivityList(discussionTitle, topicListPage);
         assertTrue(getBrowser().getCurrentUrl().contains(siteName + "/discussions-topiclist"), "'Discussions' page is opened.");

         LOG.info("STEP6-Go back to User Dashboard. Click content's name link");
         userDashboardPage.navigateByMenuBar();
         myActivitiesDashlet.clickOnItemNameFromActivityList(fileName, documentLibraryPage);
         assertTrue(getBrowser().getCurrentUrl().contains(siteName + "/documentlibrary"), "'Document Library' page is opened.");

         LOG.info("STEP7-Go back to User Dashboard. Click document's name link");
         menuNavigationBar.goTo(userDashboardPage);
         myActivitiesDashlet.clickOnItemNameFromActivityList(documentName, documentLibraryPage);
         assertTrue(getBrowser().getCurrentUrl().contains(siteName + "/documentlibrary"), "'Document Library' page is opened.");

         LOG.info("STEP8-Go back to User Dashboard. Click link's name link");
         menuNavigationBar.goTo(userDashboardPage);
         myActivitiesDashlet.clickOnItemNameFromActivityList(linkTitle, linkPage);
         assertTrue(getBrowser().getCurrentUrl().contains(siteName + "/links"), "'Links' page is opened.");

         LOG.info("STEP9-Go back to User Dashboard. Click wiki page's name link");
         menuNavigationBar.goTo(userDashboardPage);
         myActivitiesDashlet.clickOnItemNameFromActivityList(wikiTitle, wikiListPage);
         assertTrue(getBrowser().getCurrentUrl().contains(siteName + "/wiki"), "'Wiki' page is opened.");

        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser,adminPassword,siteName );
    }

    @TestRail(id = "C2117")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void checkUsersFilter()
    {
        String uniqueIdentifier = String.format("-C2117%s", RandomData.getRandomAlphanumeric());
        userName = "User" + uniqueIdentifier;
        userNameB = "UserB" + uniqueIdentifier;
        siteName = "Site" + uniqueIdentifier;
        documentName = "Doc" + uniqueIdentifier;
        documentNameB = "DocB" + uniqueIdentifier;
        String activityUserA = String.format("%s %s added document %s in %s", userName, userName, documentName, siteName);
        String activityUserB = String.format("%s %s added document %s in %s", userNameB, userNameB, documentNameB, siteName);

        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        userService.create(adminUser, adminPassword, userNameB, password, userNameB + domain, userNameB, userNameB);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        userService.createSiteMember(userName, password, userNameB, siteName, "SiteManager");
        contentService.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, documentName, documentName + " content");
        contentService.createDocument(userNameB, password, siteName, DocumentType.TEXT_PLAIN, documentNameB, documentNameB + " content");
        setupAuthenticatedSession(userName, password);
        myDocumentsDashlet.waitForDocument();

        LOG.info("STEP 1 - Select My Activities value from drop-down menu");
        myActivitiesDashlet.selectOptionFromUserActivities("My activities");
        assertTrue(myActivitiesDashlet.isActivityPresentInActivitiesDashlet(activityUserA), "Activity link: " + activityUserA + " is present in My Activities dashlet.");
        assertFalse(myActivitiesDashlet.isActivityPresentInActivitiesDashlet(activityUserB), "Activity link: " + activityUserB + " should not be present in My Activities dashlet.");

        LOG.info("STEP 2 - Select Everyone else's activities value from drop-down menu");
        myActivitiesDashlet.selectOptionFromUserActivities("Everyone else's activities");
        assertFalse(myActivitiesDashlet.isActivityPresentInActivitiesDashlet(activityUserA), "Activity link: " + activityUserA + " should not be present in My Activities dashlet.");
        assertTrue(myActivitiesDashlet.isActivityPresentInActivitiesDashlet(activityUserB), "Activity link: " + activityUserB + " is present in My Activities dashlet.");

        LOG.info("STEP 3 - Select Everyone's activities value from drop-down menu");
        myActivitiesDashlet.selectOptionFromUserActivities("Everyone's activities");
        assertTrue(myActivitiesDashlet.isActivityPresentInActivitiesDashlet(activityUserA), "Activity link: " + activityUserA + " is present in My Activities dashlet.");
        assertTrue(myActivitiesDashlet.isActivityPresentInActivitiesDashlet(activityUserB), "Activity link: " + activityUserB + " is present in My Activities dashlet.");

        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        userService.delete(adminUser,adminPassword, userNameB);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userNameB);
        siteService.delete(adminUser,adminPassword,siteName );
    }
}
