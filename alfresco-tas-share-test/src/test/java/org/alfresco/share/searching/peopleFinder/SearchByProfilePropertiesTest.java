package org.alfresco.share.searching.peopleFinder;

import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchByProfilePropertiesTest extends ContextAwareWebTest
{
    //@Autowired
    PeopleFinderPage peopleFinderPage;

    @TestRail (id = "C6655")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByUsernameOrName()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        userService.create(adminUser, adminPassword, "test" + identifier, password, "test" + identifier + domain, "firstName" + identifier, "lastName" + identifier);
        userService.create(adminUser, adminPassword, "user1" + identifier, password, "user1" + identifier + domain, "test" + identifier, "lastName" + identifier);
        userService.create(adminUser, adminPassword, "user2" + identifier, password, "user2" + identifier + domain, "firstName" + identifier, "test" + identifier);
        setupAuthenticatedSession("test" + identifier, password);
        peopleFinderPage.navigate();

        LOG.info("STEP 1 - Enter a First Name (e.g.: firstName) into Search field");
        peopleFinderPage.typeSearchInput("firstName" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "firstName" + identifier, "Search input value");

        LOG.info("STEP 2 - Click \"Search\" button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "firstName" + identifier, "2"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("test" + identifier), "User " + "test" + identifier + " is displayed");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user2" + identifier), "User " + "user2" + identifier + " is displayed");

        LOG.info("STEP 3 - Enter a Last Name (e.g.: lastName) into Search field");
        peopleFinderPage.typeSearchInput("lastName" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "lastName" + identifier, "Search input value");

        LOG.info("STEP 4 - Click \"Search\" button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "lastName" + identifier, "2"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("test" + identifier), "User " + "test" + identifier + " is displayed");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user1" + identifier), "User " + "user1" + identifier + " is displayed");

        LOG.info("STEP 5 - Enter a User Name (e.g.: test) into Search field");
        peopleFinderPage.typeSearchInput("test" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "test" + identifier, "Search input value");

        LOG.info("STEP 6 - Click \"Search\" button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "test" + identifier, "3"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("test" + identifier), "User " + "test" + identifier + " is displayed");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user1" + identifier), "User " + "user1" + identifier + " is displayed");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user2" + identifier), "User " + "user2" + identifier + " is displayed");
        userService.delete(adminUser, adminPassword, "test" + identifier);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + "test" + identifier);
        userService.delete(adminUser, adminPassword, "user1" + identifier);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + "user1" + identifier);
        userService.delete(adminUser, adminPassword, "user2" + identifier);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + "user2" + identifier);

    }

    @TestRail (id = "C6451")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "tobefixed" })
    public void searchByFullUsername()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        userService.create(adminUser, adminPassword, "user1" + identifier, password, "user1" + identifier + domain, "firstName" + identifier, "lastName" + identifier);
        userService.create(adminUser, adminPassword, "user2" + identifier, password, "user2" + identifier + domain, "firstName" + identifier, "lastName" + identifier);
        setupAuthenticatedSession("user1" + identifier, password);
        peopleFinderPage.navigate();

        LOG.info("STEP 1 - Fill in search field with username (e.g: user2)");
        peopleFinderPage.typeSearchInput("user2" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "user2" + identifier, "Search input value");

        LOG.info("STEP 2 - Click 'Search' button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "user2" + identifier, "1"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user2" + identifier), "User " + "user2" + identifier + " is displayed");

        userService.delete(adminUser, adminPassword, "user1" + identifier);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + "user1" + identifier);
        userService.delete(adminUser, adminPassword, "user2" + identifier);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + "user2" + identifier);
    }

    @TestRail (id = "C6455")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPartialUsername()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        userService.create(adminUser, adminPassword, "user" + identifier + "1", password, "user" + identifier + "1" + domain, "firstName" + identifier, "lastName" + identifier);
        userService.create(adminUser, adminPassword, "user" + identifier + "2", password, "user" + identifier + "2" + domain, "firstName" + identifier, "lastName" + identifier);
        setupAuthenticatedSession("user" + identifier + "1", password);
        peopleFinderPage.navigate();

        LOG.info("STEP 1 - Fill in search field with a partial username (e.g: \"us\")");
        peopleFinderPage.typeSearchInput("user" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "user" + identifier, "Search input value");

        LOG.info("STEP 2 - Click \"Search\" button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "user" + identifier, "2"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user" + identifier + "1"), "User " + "user" + identifier + "1" + " is displayed");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user" + identifier + "2"), "User " + "user" + identifier + "2" + " is displayed");
        userService.delete(adminUser, adminPassword, "user" + identifier + "1");
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + "user" + identifier + "1");
        userService.delete(adminUser, adminPassword, "user" + identifier + "2");
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + "user" + identifier + "2");
    }

    @TestRail (id = "C5822")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByFullName()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        userService.create(adminUser, adminPassword, "user1" + identifier, password, "user1" + identifier + domain, identifier, identifier);
        userService.create(adminUser, adminPassword, "user2" + identifier, password, "user2" + identifier + domain, "firstName" + identifier, "lastName" + identifier);
        setupAuthenticatedSession("user1" + identifier, password);
        peopleFinderPage.navigate();

        LOG.info("STEP 1 - Fill in search field with user's full name (e.g.: \"firstName lastName\")");
        peopleFinderPage.typeSearchInput("firstName" + identifier + " lastName" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "firstName" + identifier + " lastName" + identifier, "Search input value");

        LOG.info("STEP 2 - Click \"Search\" button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "firstName" + identifier + " lastName" + identifier, "1"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user2" + identifier), "User " + "user2" + identifier + " is displayed");

        LOG.info("STEP 3 - Fill in search field with user's full name (e.g.: \"lastName firstName\")");
        peopleFinderPage.typeSearchInput("lastName" + identifier + " firstName" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "lastName" + identifier + " firstName" + identifier, "Search input value");

        LOG.info("STEP 4 - Click \"Search\" button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "lastName" + identifier + " firstName" + identifier, "1"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user2" + identifier), "User " + "user2" + identifier + " is displayed");

        userService.delete(adminUser, adminPassword, "user1" + identifier);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + "user1" + identifier);
        userService.delete(adminUser, adminPassword, "user2" + identifier);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + "user2" + identifier);
    }

    @TestRail (id = "C5832")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void searchByPartialName()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        userService.create(adminUser, adminPassword, "user" + identifier + "1", password, "user" + identifier + "1" + domain, "firstName" + identifier, "lastName" + identifier);
        userService.create(adminUser, adminPassword, "user" + identifier + "2", password, "user" + identifier + "2" + domain, "firstName" + identifier, "lastName" + identifier);
        setupAuthenticatedSession("user" + identifier + "1", password);
        peopleFinderPage.navigate();

        LOG.info("STEP 1 - Fill in search field with a partial name (e.g: \"firstname\")");
        peopleFinderPage.typeSearchInput("firstName" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "firstName" + identifier, "Search input value");

        LOG.info("STEP 2 - Click \"Search\" button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "firstName" + identifier, "2"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user" + identifier + "1"), "User " + "user" + identifier + "1" + " is displayed");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user" + identifier + "2"), "User " + "user" + identifier + "2" + " is displayed");
        userService.delete(adminUser, adminPassword, "user" + identifier + "1");
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + "user" + identifier + "1");
        userService.delete(adminUser, adminPassword, "user" + identifier + "2");
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + "user" + identifier + "2");
    }
}
