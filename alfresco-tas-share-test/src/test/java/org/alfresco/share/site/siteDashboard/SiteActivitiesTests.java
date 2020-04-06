package org.alfresco.share.site.siteDashboard;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.SiteActivitiesDashlet;
import org.alfresco.po.share.dashlet.SiteActivitiesDaysRangeFilter;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.members.SiteGroupsPage;
import org.alfresco.po.share.site.members.SiteMembersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */

public class SiteActivitiesTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    SiteActivitiesDashlet siteActivitiesDashlet;

    @Autowired
    SiteGroupsPage siteGroupsPage;

    @Autowired
    SiteMembersPage siteMembersPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    String siteName = "siteActivities" + RandomData.getRandomAlphanumeric();
    String userName = "siteActivitiesUser" + RandomData.getRandomAlphanumeric();
    String docName = "activityDoc" + RandomData.getRandomAlphanumeric();
    String firstName = "site";
    String lastName = "activities";
    String docContent = "This is a test for the Activities dashlet.";

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
        LOG.info(userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
    }

    @TestRail (id = "C2803")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void noActivitiesCreated()
    {
        String uniqueIdentifier = String.format("-C2803-%s", RandomData.getRandomAlphanumeric());
        String user = "profileUser" + uniqueIdentifier;
        String siteName = "Site" + uniqueIdentifier;
        String description = "Description" + uniqueIdentifier;

        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);

        setupAuthenticatedSession(user, password);
        siteDashboardPage.navigate(siteName);
        assertEquals(siteActivitiesDashlet.getDashletTitle(), "Site Activities", "Dashlet title-");

        LOG.info("STEP1: Verify 'Site Activities' dashlet");
        assertEquals(siteActivitiesDashlet.getEmptyDashletMessage(), language.translate("activities.empty"), "Empty dashlet message-");

        LOG.info("STEP2: Verify available actions");
        assertEquals(siteActivitiesDashlet.isRssFeedButtonDisplayed(), true, "'Subscribe to RSS feed' icon is displayed.");

        assertEquals(siteActivitiesDashlet.isHelpIconDisplayed(Dashlet.DashletHelpIcon.SITE_ACTIVITIES), true, "'Help' icon is displayed.");

        String[] expectedUserActivities = { language.translate("siteActivities.filter.mine"), language.translate("siteActivities.filter.everyoneElse"),
            language.translate("siteActivities.filter.everyone"), language.translate("siteActivities.filter.meFollowing") };
        assertEquals(DataUtil.areListsEquals(siteActivitiesDashlet.getMyActivitiesFilterOptions(), expectedUserActivities), true,
            "Site Activities dropdown options-");

        LOG.info("STEP3: Click '?' icon");
        siteActivitiesDashlet.clickOnHelpIcon(Dashlet.DashletHelpIcon.SITE_ACTIVITIES);
        assertEquals(siteActivitiesDashlet.getHelpBalloonMessage(), language.translate("siteActivities.help"), "'Help' balloon message-");

        LOG.info("STEP4: Click 'X' icon");
        siteActivitiesDashlet.closeHelpBalloon();
        assertEquals(siteActivitiesDashlet.isBalloonDisplayed(), false, "'Help' balloon is closed.");
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2809")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void dateFilter()
    {
        String uniqueIdentifier = String.format("-C2809-%s", RandomData.getRandomAlphanumeric());
        String user = "profileUser" + uniqueIdentifier;
        String siteName = "Site" + uniqueIdentifier;
        String description = "Description" + uniqueIdentifier;
        String documentName = "Document" + uniqueIdentifier;

        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createDocument(user, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, documentName, documentName + " content");

        setupAuthenticatedSession(user, password);
        siteDashboardPage.navigate(siteName);
        getBrowser().refresh();
        assertEquals(siteActivitiesDashlet.getDashletTitle(), "Site Activities", "Dashlet title is: " + siteActivitiesDashlet.getDashletTitle());

        LOG.info("STEP1: Select 'today' value from drop-down menu");
        siteActivitiesDashlet.selectOptionFromHistoryFilter(SiteActivitiesDaysRangeFilter.TODAY);
        assertEquals(siteActivitiesDashlet.isHistoryOptionSelected(SiteActivitiesDaysRangeFilter.TODAY), true, "Option 'today' is selected.");
        assertEquals(siteActivitiesDashlet.isTimeRangeAccurateForAllActivities(0, "now"), true, "Only activities from today are displayed.");

        LOG.info("STEP2: Select 'in the last 7 days' value from drop-down menu");
        siteActivitiesDashlet.selectOptionFromHistoryFilter(SiteActivitiesDaysRangeFilter.SEVEN_DAYS);
        assertEquals(siteActivitiesDashlet.isHistoryOptionSelected(SiteActivitiesDaysRangeFilter.SEVEN_DAYS), true, "Option 'in the last 7 days' is selected.");
        assertEquals(siteActivitiesDashlet.isTimeRangeAccurateForAllActivities(7, "now"), true, "Only activities in the last 7 days are displayed.");

        LOG.info("STEP3: Select 'in the last 14 days' value from drop-down menu");
        siteActivitiesDashlet.selectOptionFromHistoryFilter(SiteActivitiesDaysRangeFilter.FOURTEEN_DAYS);
        assertEquals(siteActivitiesDashlet.isHistoryOptionSelected(SiteActivitiesDaysRangeFilter.FOURTEEN_DAYS), true,
            "Option 'in the last 14 days' is selected.");
        assertEquals(siteActivitiesDashlet.isTimeRangeAccurateForAllActivities(14, "now"), true, "Only activities in the last 14 days are displayed.");

        LOG.info("STEP4: Select 'in the last 28 days' value from drop-down menu");
        siteActivitiesDashlet.selectOptionFromHistoryFilter(SiteActivitiesDaysRangeFilter.TWENTY_EIGHT_DAYS);
        assertEquals(siteActivitiesDashlet.isHistoryOptionSelected(SiteActivitiesDaysRangeFilter.TWENTY_EIGHT_DAYS), true,
            "Option 'in the last 28 days' is selected.");
        assertEquals(siteActivitiesDashlet.isTimeRangeAccurateForAllActivities(28, "now"), true, "Only activities in the last 28 days are displayed.");

        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C12833")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })

    public void activitiesDashletDisplaysGroupNameWhenLoggedInAsAdmin()
    {
        String user = String.format("C12833User%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site%s", RandomData.getRandomAlphanumeric());
        String description = String.format("description%s", RandomData.getRandomAlphanumeric());
        String groupName = "C12833Group";
        String managerRole = "Manager";
        String activity = "\"" + groupName + "\"" + " group added to site " + siteName + " with role " + managerRole;
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        groupService.createGroup(adminUser, adminPassword, groupName);
        siteService.create(adminUser, adminPassword, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("Precondition: Add group to site");
        siteGroupsPage.navigate(siteName);
        siteGroupsPage.clickAddGroupsButton();
        siteGroupsPage.typeInSearchGroup(groupName);
        siteGroupsPage.clickSearchButton();
        siteGroupsPage.clickTheAddButton();
        siteGroupsPage.clickSelectRoleButton();
        siteGroupsPage.selectRole(managerRole);
        getBrowser().waitInSeconds(3);
        siteGroupsPage.clickTheAddGroupsButton();
        getBrowser().waitInSeconds(5);

        LOG.info("Step 1: Navigate to site dashboard and check Activities dashlet");

        siteDashboardPage.navigate(siteName);
        getBrowser().waitUntilElementIsDisplayedWithRetry(siteActivitiesDashlet.getActivitiElement(), 6);
        Assert.assertTrue(siteActivitiesDashlet.isActivityPresentInActivitiesDashlet(activity), "Activity is not present on dashlet");
        cleanupAuthenticatedSession();

        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C12834")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void activitiesDashletDisplaysGroupNameWhenLoginWithOtherUserThanAdmin()
    {
        String user = String.format("C12834User%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site%s", RandomData.getRandomAlphanumeric());
        String description = String.format("description%s", RandomData.getRandomAlphanumeric());
        String groupName = "C12834Group";
        String managerRole = "Manager";
        String activity = "\"" + groupName + "\"" + " group added to site " + siteName + " with role " + managerRole;
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        groupService.createGroup(adminUser, adminPassword, groupName);
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);

        LOG.info("Precondition: Add group to site");
        siteGroupsPage.navigate(siteName);
        siteGroupsPage.clickAddGroupsButton();
        siteGroupsPage.typeInSearchGroup(groupName);
        siteGroupsPage.clickSearchButton();
        siteGroupsPage.clickTheAddButton();
        siteGroupsPage.clickSelectRoleButton();
        siteGroupsPage.selectRole(managerRole);
        getBrowser().waitInSeconds(3);
        siteGroupsPage.clickTheAddGroupsButton();
        getBrowser().waitInSeconds(5);

        siteDashboardPage.navigate(siteName);
        getBrowser().waitUntilElementIsDisplayedWithRetry(siteActivitiesDashlet.getActivitiElement(), 6);
        Assert.assertTrue(siteActivitiesDashlet.isActivityPresentInActivitiesDashlet(activity), "Activity is not present on dashlet");

        cleanupAuthenticatedSession();

        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C12835")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })

    public void groupNameIsDisplayedInGroupsTabSiteMembersGroup()
    {
        String user = String.format("C12835User%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site%s", RandomData.getRandomAlphanumeric());
        String description = String.format("description%s", RandomData.getRandomAlphanumeric());
        String groupName = "C12835Group";
        String groupId = "GROUP_" + groupName;
        ArrayList<String> expectedGroupName = new ArrayList<>();
        expectedGroupName.add(groupName);
        ArrayList<String> expectedId = new ArrayList<>();
        expectedId.add(groupId);
        String managerRole = "Manager";
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        groupService.createGroup(adminUser, adminPassword, groupName);
        siteService.create(adminUser, adminPassword, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("Precondition");
        siteGroupsPage.navigate(siteName);
        siteGroupsPage.clickAddGroupsButton();
        siteGroupsPage.typeInSearchGroup(groupName);
        siteGroupsPage.clickSearchButton();
        siteGroupsPage.clickTheAddButton();
        siteGroupsPage.clickSelectRoleButton();
        siteGroupsPage.selectRole(managerRole);
        getBrowser().waitInSeconds(3);
        siteGroupsPage.clickTheAddGroupsButton();
        getBrowser().waitInSeconds(5);

        LOG.info("Step 1: Navigate to site members page, groups tab ");

        siteMembersPage.openSiteGroupsPage();
        Assert.assertEquals(siteGroupsPage.getSiteMembersList(), expectedGroupName, groupName + " is not displayed");
        Assert.assertEquals(siteGroupsPage.getIds(), expectedId, groupId + " is not displayed");

        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C12836")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })

    public void groupNameIsDisplayedInGroupsTabAddGroup()
    {
        String user = String.format("C12836User%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site%s", RandomData.getRandomAlphanumeric());
        String description = String.format("description%s", RandomData.getRandomAlphanumeric());
        String groupName = "C12836Group";
        String idLabelText = "ID:";
        String groupId = idLabelText + " GROUP_" + groupName;
        ArrayList<String> expectedGroupName = new ArrayList<>();
        expectedGroupName.add(groupName);
        ArrayList<String> expectedId = new ArrayList<>();
        expectedId.add(groupId);
        String managerRole = "Manager";
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        groupService.createGroup(adminUser, adminPassword, groupName);
        siteService.create(adminUser, adminPassword, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(adminUser, adminPassword);

        siteGroupsPage.navigate(siteName);
        siteGroupsPage.clickAddGroupsButton();

        LOG.info("Step 1&2: Search for group " + groupName + " and check that it is displayed correctly in search results");
        siteGroupsPage.typeInSearchGroup(groupName);
        siteGroupsPage.clickSearchButton();
        Assert.assertEquals(siteGroupsPage.getSearchResultsGroupName(), expectedGroupName, expectedGroupName + " is not displayed in search results");
        Assert.assertEquals(siteGroupsPage.getIdInSearchResults(), expectedId, expectedId + " is not displayed in search results");

        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C12837")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES, "tobefixed"})
    public void activitiesDashletDisplaysGroupNameWhenGroupsRoleIsChanged()
    {
        String user = String.format("C12837User%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site%s", RandomData.getRandomAlphanumeric());
        String description = String.format("description%s", RandomData.getRandomAlphanumeric());
        String groupName = "C12837Group";
        String groupId = "GROUP_" + groupName;
        ArrayList<String> expectedGroupName = new ArrayList<>();
        expectedGroupName.add(groupName);
        String managerRole = "Manager";
        String collaboratorRole = "Collaborator";
        String activity = "\"" + groupName + "\"" + " group role changed to " + collaboratorRole;
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        groupService.createGroup(adminUser, adminPassword, groupName);
        siteService.create(adminUser, adminPassword, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("Precondition");
        siteGroupsPage.navigate(siteName);
        siteGroupsPage.clickAddGroupsButton();
        siteGroupsPage.typeInSearchGroup(groupName);
        siteGroupsPage.clickSearchButton();
        siteGroupsPage.clickTheAddButton();
        siteGroupsPage.clickSelectRoleButton();
        siteGroupsPage.selectRole(managerRole);
        getBrowser().waitInSeconds(3);
        siteGroupsPage.clickTheAddGroupsButton();
        getBrowser().waitInSeconds(5);

        LOG.info("Step 1: Navigate to site members page, groups tab ");

        siteMembersPage.openSiteGroupsPage();
        Assert.assertEquals(siteGroupsPage.getSiteMembersList(), expectedGroupName, groupName + " is not displayed");

        LOG.info("Step 2: Change the role of the group");
        Assert.assertEquals(siteGroupsPage.getRole(groupName), "Manager ▾", "Manager ▾" + "is not the role of the group");
        siteGroupsPage.changeRoleForMember("Collaborator", groupName);
        getBrowser().waitUntilElementDisappears(siteGroupsPage.waitMessage, 30L);
        Assert.assertEquals(siteGroupsPage.getRole(groupName), "Collaborator ▾", "Collaborator ▾" + "is not the role of the group");

        LOG.info("Step 3: Go to Site Dashboard and observe the Site Activities dashlet");
        siteDashboardPage.navigate(siteName);

        getBrowser().waitUntilElementIsDisplayedWithRetry(siteActivitiesDashlet.getActivitiElement(), 6);
        Assert.assertTrue(siteActivitiesDashlet.isActivityPresentInActivitiesDashlet(activity), "Activity is not present on dashlet");
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);


    }

    @TestRail (id = "C12838")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES, "tobefixed" })

    public void activitiesDashletDisplaysGroupNameWhenGroupIsRemovedFromSite()
    {
        String user = String.format("C12838User%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site%s", RandomData.getRandomAlphanumeric());
        String description = String.format("description%s", RandomData.getRandomAlphanumeric());
        String groupName = "C12838Group";
        String groupId = "GROUP_" + groupName;
        ArrayList<String> expectedGroupName = new ArrayList<>();
        expectedGroupName.add(groupName);
        ArrayList<String> expectedId = new ArrayList<>();
        String managerRole = "Manager";
        String activity = "\"" + groupName + "\"" + " group removed from site " + siteName;
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        groupService.createGroup(adminUser, adminPassword, groupName);
        siteService.create(adminUser, adminPassword, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("Precondition");
        siteGroupsPage.navigate(siteName);
        siteGroupsPage.clickAddGroupsButton();
        siteGroupsPage.typeInSearchGroup(groupName);
        siteGroupsPage.clickSearchButton();
        siteGroupsPage.clickTheAddButton();
        siteGroupsPage.clickSelectRoleButton();
        siteGroupsPage.selectRole(managerRole);
        getBrowser().waitInSeconds(3);
        siteGroupsPage.clickTheAddGroupsButton();
        getBrowser().waitInSeconds(5);

        LOG.info("Step 1: Navigate to site members page, groups tab and remove group from site");

        siteMembersPage.openSiteGroupsPage();
        Assert.assertEquals(siteGroupsPage.getSiteMembersList(), expectedGroupName, groupName + " is not displayed");
        siteGroupsPage.removeGroup(groupName);

        getBrowser().waitInSeconds(10);
        LOG.info("Step 2: Go to Site Dashboard and observe the Site Activities dashlet");
        siteDashboardPage.navigate(siteName);

        getBrowser().waitUntilElementIsDisplayedWithRetry(siteActivitiesDashlet.getActivitiElement(), 6);
        Assert.assertTrue(siteActivitiesDashlet.isActivityPresentInActivitiesDashlet(activity), "Activity is not present on dashlet");
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", "SiteDashboard" })
    public void selectActivityAndAccessData()
    {
        LOG.info("Step 1: Log in and check that activiti is displayed in site activities dashlet");
        setupAuthenticatedSession(userName, password);
        siteDashboardPage.navigate(siteName);
        Assert.assertTrue(siteActivitiesDashlet.isActivityPresentInActivitiesDashlet(firstName + " " + lastName + " added document " + docName), " is not displayed in activities list");
        LOG.info("Step 2: Click on document name and check that you are redirected to Document Details and content is displayed");
        siteActivitiesDashlet.clickOnDocumentLinkInActivities(docName, documentDetailsPage);
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » Document Details");
        Assert.assertEquals(documentDetailsPage.getContentText(), docContent, "Content is either not displayed or not correct");
        Assert.assertEquals(documentDetailsPage.getFileName(), docName, "document name is not correct");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", "SiteDashboard" }, enabled = false)
    public void verifyRSSFeedFunctionalityForActivitiesDashlet()
    {
        String partialUnchangedUrl = "/share/feedservice/components/dashlets/activities/list?format=atomfeed&mode=site&site=";
        setupAuthenticatedSession(userName, password);
        siteDashboardPage.navigate(siteName);
        siteActivitiesDashlet.enableRSSFeed(partialUnchangedUrl, siteName);
        Assert.assertTrue(getBrowser().getCurrentUrl().contains(partialUnchangedUrl), "User is not redirected to RSS Feed page " + userName);
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", "SiteDashboard" })
    public void checkFilterOptionsAvailability()
    {
        setupAuthenticatedSession(userName, password);
        siteDashboardPage.navigate(siteName);
        LOG.info("Step 1: Check options available for activities type filter");
        Assert.assertTrue(siteActivitiesDashlet.getMyActivitiesFilterOptions().contains("My activities"), "My Activities is not available");
        Assert.assertTrue(siteActivitiesDashlet.getMyActivitiesFilterOptions().contains("Everyone else's activities"), "Everyone else's activities is not available");
        Assert.assertTrue(siteActivitiesDashlet.getMyActivitiesFilterOptions().contains("Everyone's activities"), "Everyone's activities is not available");
        Assert.assertTrue(siteActivitiesDashlet.getMyActivitiesFilterOptions().contains("I'm following"), "I'm following is not available");

        LOG.info("Step 2: Check options available for item type filter");
        List<String> actualOptionsItemType = siteActivitiesDashlet.getItemTypeFilterOptionAvailable();
        Assert.assertTrue(actualOptionsItemType.contains("all items"), "all items is not available");
        Assert.assertTrue(actualOptionsItemType.contains("comments"), "comments is not available");
        Assert.assertTrue(actualOptionsItemType.contains("content"), "content is not available");
        Assert.assertTrue(actualOptionsItemType.contains("memberships"), "memberships is not available");

        LOG.info("Step 3: Check range filter options");
        List<String> actualOptionsRange = siteActivitiesDashlet.getRangeFilterOptions();
        Assert.assertTrue(actualOptionsRange.contains("today"), "today option is not available");
        Assert.assertTrue(actualOptionsRange.contains("in the last 7 days"), "in the last 7 days option is not available");
        Assert.assertTrue(actualOptionsRange.contains("in the last 14 days"), "in the last 14 days option is not available");
        Assert.assertTrue(actualOptionsRange.contains("in the last 28 days"), "in the last 28 days option is not available");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", "SiteDashboard" })
    public void checkFilteringOptionsFunctional()
    {
        setupAuthenticatedSession(userName, password);
        siteDashboardPage.navigate(siteName);
        LOG.info("Step 1: Select Everyone's activities filter option and check results");
        siteActivitiesDashlet.selectUserFilterOption("Everyone's activities");
        Assert.assertTrue(siteActivitiesDashlet.getActivitiesDashletResultsText().contains(language.translate("activitiesDashlet.EmptyText")));
        LOG.info("Step 2: Select comments filter option and check results");
        siteActivitiesDashlet.selectItemTypeFilterOption("comments");
        Assert.assertTrue(siteActivitiesDashlet.getActivitiesDashletResultsText().contains(language.translate("activitiesDashlet.EmptyText")));
        LOG.info("Step 3: Select today filter option and check results");
        siteActivitiesDashlet.selectRangeFilterOption("today");
        Assert.assertTrue(siteActivitiesDashlet.getActivitiesDashletResultsText().contains(language.translate("activitiesDashlet.EmptyText")));
        LOG.info("Step 4: Reset filters and check results.");
        siteDashboardPage.navigate(siteName);
        siteActivitiesDashlet.selectUserFilterOption("My activities");
        siteActivitiesDashlet.selectItemTypeFilterOption("all items");
        siteActivitiesDashlet.selectRangeFilterOption("today");
        Assert.assertTrue(siteActivitiesDashlet.isActivityPresentInActivitiesDashlet(firstName + " " + lastName + " added document " + docName), " is not displayed in activities list");
        cleanupAuthenticatedSession();
    }
}
