package org.alfresco.share.searching.peopleFinder;

import org.alfresco.po.share.Notification;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PeopleFinderPageTest extends ContextAwareWebTest
{
    @Autowired
    PeopleFinderPage peopleFinderPage;

    @Autowired
    UserProfilePage userProfilePage;

    @Autowired
    Notification notification;

    @TestRail (id = "C5823")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void verifyPeopleFinderPage()
    {
        String userName1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String userName2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName1, password, userName1 + domain, userName1, userName1);
        userService.create(adminUser, adminPassword, userName2, password, userName2 + domain, userName2, userName2);
        setupAuthenticatedSession(userName1, password);
        peopleFinderPage.navigate();

        LOG.info("STEP 1 - Verify page title");
        Assert.assertEquals(peopleFinderPage.getPageTitle(), language.translate("peopleFinder.pageTitle"), "Page title");

        LOG.info("STEP 2 - Verify Search section");
        Assert.assertTrue(peopleFinderPage.isSearchInputFieldDisplayed(), "Search input is displayed");
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldPlaceholder(),
            language.translate("peopleFinder.searchPlaceholder"), "Search input placeholder");
        Assert.assertTrue(peopleFinderPage.isSearchButtonDisplayed(), "\"Search\" button is displayed");
        Assert.assertTrue(peopleFinderPage.isHelpMessageDisplayed(), "Help message is displayed");
        Assert.assertEquals(peopleFinderPage.getSearchHelpMessage(),
            language.translate("peopleFinder.searchResultsMessage"), "Search results message");

        LOG.info("STEP 3 - Fill in search field (e.g: user2) and click \"search\" button");
        peopleFinderPage.search(userName2);
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), userName2, "1"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isFollowButtonDisplayed(userName2), "\"Follow\" button is displayed");
        Assert.assertEquals(peopleFinderPage.getNumberOfSearchResults(), 1, "Number of search results");
        Assert.assertTrue(peopleFinderPage.isUserAvatarDisplayed(userName2), userName2 + " avatar is displayed");

        LOG.info("STEP 4 - Click a user link from search results (e.g: user2)");
        peopleFinderPage.clickUserLink(userName2);
        Assert.assertTrue(userProfilePage.isAboutHeaderDisplayed(), "\"About\" header is displayed");
        userService.delete(adminUser, adminPassword, userName1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1);
        userService.delete(adminUser, adminPassword, userName2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName2);

    }

    @TestRail (id = "C5824")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void noResultsFound()
    {
        String userName1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName1, password, userName1 + domain, userName1, userName1);
        setupAuthenticatedSession(userName1, password);
        peopleFinderPage.navigate();

        LOG.info("STEP 1 - Fill in 'search' field with an nonexistent user");
        peopleFinderPage.typeSearchInput("TestDocument");
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "TestDocument", "Search input value");

        LOG.info("STEP 2 - Click \"Search\" button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getNoResultsText(), language.translate("peopleFinder.noResults"), "No results found translation");
        userService.delete(adminUser, adminPassword, userName1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1);
    }

    @TestRail (id = "C5825")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void emptyInputSearchField()
    {
        String userName1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName1, password, userName1 + domain, userName1, userName1);
        setupAuthenticatedSession(userName1, password);
        peopleFinderPage.navigate();

        LOG.info("STEP 1 - Click \"Search\" button");
        peopleFinderPage.clickSearch();
        Assert.assertEquals(notification.getDisplayedNotification(), language.translate("peopleFinder.emptyValueSearchNotification"), "Empty search input field search error");
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "", "Search input value");
        Assert.assertEquals(peopleFinderPage.getSearchHelpMessage(), language.translate("peopleFinder.searchResultsMessage"), "Search help message");
        userService.delete(adminUser, adminPassword, userName1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1);
    }
}
