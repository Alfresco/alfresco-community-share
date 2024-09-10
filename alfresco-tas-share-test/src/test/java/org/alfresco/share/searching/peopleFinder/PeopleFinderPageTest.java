package org.alfresco.share.searching.peopleFinder;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.PageNotification;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j

public class PeopleFinderPageTest extends BaseTest
{

    UserProfilePage userProfilePage;
    SearchPage searchPage;
    Toolbar toolbar;
    PageNotification pageNotification;
    PeopleFinderPage getPeopleFinderPage;
    private final String password = "password";
    private UserModel testUser1;
    private UserModel testUser2;

    String userName2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
    String userName1 = String.format("User1%s", RandomData.getRandomAlphanumeric());

    @BeforeMethod(alwaysRun = true)
    public void testSetup()  {
        log.info("Precondition1: Test user is created");
        testUser1 = dataUser.usingAdmin().createUser(userName1, password);
        testUser2 = dataUser.usingAdmin().createUser(userName2, password);
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingLoginPage(getAdminUser());
        searchPage = new SearchPage(webDriver);
        userProfilePage = new UserProfilePage(webDriver);
        toolbar = new Toolbar(webDriver);
        getPeopleFinderPage = new PeopleFinderPage(webDriver);
        pageNotification = new PageNotification(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(testUser1);
        deleteUsersIfNotNull(testUser2);
    }

    @TestRail (id = "C5823")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void verifyPeopleFinderPage()
    {

        authenticateUsingCookies(testUser2);

        getPeopleFinderPage.navigateToPeopleFinderPage();

        log.info("STEP 1 - Verify page title");
        Assert.assertEquals(getPeopleFinderPage.getPageTitle(),language.newTranslate("People Finder"));

        log.info("STEP 2 - Verify Search section");
        Assert.assertTrue(getPeopleFinderPage.isSearchInputFieldDisplayed(), "Search input is displayed");
        Assert.assertEquals(getPeopleFinderPage.getSearchInputFieldPlaceholder(),
            language.translate("peopleFinder.searchPlaceholder"), "Search input placeholder");
        Assert.assertTrue(getPeopleFinderPage.isSearchButtonDisplayed(), "\"Search\" button is displayed");
        Assert.assertTrue(getPeopleFinderPage.isHelpMessageDisplayed(), "Help message is displayed");
        Assert.assertEquals(getPeopleFinderPage.getSearchHelpMessage(),
            language.translate("peopleFinder.searchResultsMessage"), "Search results message");

        log.info("STEP 3 - Fill in search field (e.g: user2) and click \"search\" button");
        authenticateUsingCookies(testUser1);
        getPeopleFinderPage.navigateToPeopleFinderPage();
        getPeopleFinderPage.search(userName2);
        Assert.assertEquals(getPeopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), userName2, "1"), "Search results info");
        Assert.assertTrue(getPeopleFinderPage.isFollowButtonDisplayed(userName2), "\"Follow\" button is displayed");
        Assert.assertEquals(getPeopleFinderPage.getNumberOfSearchResults(), 1, "Number of search results");
        Assert.assertTrue(getPeopleFinderPage.isUserAvatarDisplayed(userName2), userName2 + " avatar is displayed");

        log.info("STEP 4 - Click a user link from search results (e.g: user2)");
        getPeopleFinderPage.clickUserLink(userName2);
        Assert.assertTrue(userProfilePage.isAboutHeaderDisplayed(), "\"About\" header is displayed");

    }

    @TestRail (id = "C5824")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void noResultsFound()
    {

        authenticateUsingCookies(testUser1);
        getPeopleFinderPage.navigateToPeopleFinderPage();

        log.info("STEP 1 - Fill in 'search' field with an nonexistent user");
        getPeopleFinderPage.typeSearchInput("TestDocument");
        Assert.assertEquals(getPeopleFinderPage.getSearchInputFieldValue(), "TestDocument", "Search input value");

        log.info("STEP 2 - Click \"Search\" button");
        getPeopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(getPeopleFinderPage.getNoResultsText(), language.translate("peopleFinder.noResults"), "No results found translation");

    }

    @TestRail (id = "C5825")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void emptyInputSearchField()
    {

        authenticateUsingCookies(testUser1);
        getPeopleFinderPage.navigateToPeopleFinderPage();

        log.info("STEP 1 - Click \"Search\" button");
        getPeopleFinderPage.clickSearch();

        Assert.assertEquals(pageNotification.getDisplayedNotification(), language.translate("peopleFinder.emptyValueSearchNotification"), "Empty search input field search error");
        Assert.assertEquals(getPeopleFinderPage.getSearchInputFieldValue(), "", "Search input value");
        Assert.assertEquals(getPeopleFinderPage.getSearchHelpMessage(), language.translate("peopleFinder.searchResultsMessage"), "Search help message");

    }
}
