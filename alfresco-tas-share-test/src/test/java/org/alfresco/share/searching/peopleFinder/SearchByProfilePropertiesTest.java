package org.alfresco.share.searching.peopleFinder;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.constants.ShareGroups;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.EditUserPage;
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
public class SearchByProfilePropertiesTest extends BaseTest
{
    private UserModel testUser;
    private UserModel testUser1;
    private UserModel testUser2;
    private final String password = "password";
    PeopleFinderPage peopleFinderPage;
    private EditUserPage editUserPage;
    @BeforeMethod (alwaysRun = true)
    public void beforeTest()
    {
        editUserPage = new EditUserPage(webDriver);
        peopleFinderPage = new PeopleFinderPage(webDriver);
    }
    @AfterMethod(alwaysRun = true)
    public void cleanUp()
    {
        deleteUsersIfNotNull(testUser);
        deleteUsersIfNotNull(testUser1);
        deleteUsersIfNotNull(testUser2);
    }
    @TestRail (id = "C6655")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE })
    public void searchByUsernameOrName()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        log.info("PreCondition: Test users are created");
        final String user = "test" + identifier;
        final String user1 = "user1" + identifier;
        final String user2 = "user2" + identifier;
        testUser = dataUser.usingAdmin().createUser(user, password);
        testUser1 = dataUser.usingAdmin().createUser(user1, password);
        testUser2 = dataUser.usingAdmin().createUser(user2, password);
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingLoginPage(getAdminUser());

        log.info("Edit user data & give specific name to " + testUser);
        UserModel editUser = testUser;
        String firstName = "firstName" + identifier;
        String lastName = "lastName" + identifier;
        String email = "test" + identifier + "@test.com";
        editUserPage.navigate(editUser)
            .editFirstName(firstName)
            .editLastNameField(lastName)
            .editEmailField(email)
            .clickSaveChanges();

        log.info("Edit user data & give specific name to " + testUser1);
        UserModel editUser1 = testUser1;
        String firstName1 = "test" + identifier;
        String lastName1 = "lastName" + identifier;
        String email1 = "user1" + identifier + "@test.com";
        editUserPage.navigate(editUser1)
            .editFirstName(firstName1)
            .editLastNameField(lastName1)
            .editEmailField(email1)
            .clickSaveChanges();

        log.info("Edit user data & give specific name to " + testUser2);
        UserModel editUser2 = testUser2;
        String firstName2 = "firstname" + identifier;
        String lastName2 = "test" + identifier;
        String email2 = "user2" + identifier + "@test.com";

        editUserPage.navigate(editUser2)
            .editFirstName(firstName2)
            .editLastNameField(lastName2)
            .editEmailField(email2)
            .clickSaveChanges();

        authenticateUsingLoginPage(testUser);
        peopleFinderPage.navigate();

        log.info("STEP 1 - Enter a First Name (e.g.: firstName) into Search field");
        peopleFinderPage.typeSearchInput("firstName" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "firstName" + identifier, "Search input value");

        log.info("STEP 2 - Click \"Search\" button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "firstName" + identifier, "2"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed(testUser.getUsername()), "User " + "test" + identifier + " is displayed");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed(testUser2.getUsername()), "User " + "user2" + identifier + " is displayed");

        log.info("STEP 3 - Enter a Last Name (e.g.: lastName) into Search field");
        peopleFinderPage.typeSearchInput("lastName" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "lastName" + identifier, "Search input value");

        log.info("STEP 4 - Click \"Search\" button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "lastName" + identifier, "2"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed(testUser.getUsername()), "User " + "test" + identifier + " is displayed");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed(testUser1.getUsername()), "User " + "user1" + identifier + " is displayed");

        log.info("STEP 5 - Enter a User Name (e.g.: test) into Search field");
        peopleFinderPage.typeSearchInput("test" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "test" + identifier, "Search input value");

        log.info("STEP 6 - Click \"Search\" button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "test" + identifier, "3"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed(testUser.getUsername()), "User " + "test" + identifier + " is displayed");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed(testUser1.getUsername()), "User " + "user1" + identifier + " is displayed");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed(testUser2.getUsername()), "User " + "user2" + identifier + " is displayed");

    }

    @TestRail (id = "C6451")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, ShareGroups.TOBEFIXED, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE })
    public void searchByFullUsername()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        log.info("PreCondition1: Any test users are created");
        final String user1 = "user1" + identifier;
        final String user2 = "user2" + identifier;

        testUser1 = dataUser.usingAdmin().createUser(user1, password);
        testUser2 = dataUser.usingAdmin().createUser(user2, password);
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingLoginPage(getAdminUser());

        log.info("Edit user data & give specific name to " + testUser1);
        UserModel editUser1 = testUser1;
        String firstName1 = "firstname" + identifier;
        String lastName1 = "lastName" + identifier;
        String email1 = "user1" + identifier + "@test.com";

        editUserPage.navigate(editUser1)
            .editFirstName(firstName1)
            .editLastNameField(lastName1)
            .editEmailField(email1)
            .clickSaveChanges();

        log.info("Edit user data & give specific name to " + testUser2);
        UserModel editUser2 = testUser2;
        String firstName2 = "firstname" + identifier;
        String lastName2 = "lastname" + identifier;
        String email2 = "user2" + identifier + "@test.com";

        editUserPage.navigate(editUser2)
            .editFirstName(firstName2)
            .editLastNameField(lastName2)
            .editEmailField(email2)
            .clickSaveChanges();

        authenticateUsingLoginPage(testUser1);
        peopleFinderPage.navigate();

        log.info("STEP 1 - Fill in search field with username (e.g: user2)");
        peopleFinderPage.typeSearchInput("user2" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "user2" + identifier, "Search input value");

        log.info("STEP 2 - Click 'Search' button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "user2" + identifier, "1"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user2" + identifier), "User " + "user2" + identifier + " is displayed");

    }

    @TestRail (id = "C6455")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE })
    public void searchByPartialUsername()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        log.info("PreCondition1: Any test user is created");
        final String user1 = "user" + identifier + "1";
        final String user2 = "user" + identifier + "2";

        testUser1 = dataUser.usingAdmin().createUser(user1, password);
        testUser2 = dataUser.usingAdmin().createUser(user2, password);
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingLoginPage(getAdminUser());

        log.info("Edit user data & give specific name to " + testUser1);
        UserModel editUser1 = testUser1;
        String firstName1 = "firstname" + identifier;
        String lastName1 = "lastName" + identifier;
        String email1 = "user" + identifier + "1" + "@test.com";

        editUserPage.navigate(editUser1)
            .editFirstName(firstName1)
            .editLastNameField(lastName1)
            .editEmailField(email1)
            .clickSaveChanges();

        log.info("Edit user data & give specific name to " + testUser1);
        UserModel editUser2 = testUser2;
        String firstName2 = "firstname" + identifier;
        String lastName2 = "lastname" + identifier;
        String email2 = "user" + identifier + "2" + "@test.com";

        editUserPage.navigate(editUser2)
            .editFirstName(firstName2)
            .editLastNameField(lastName2)
            .editEmailField(email2)
            .clickSaveChanges();

        authenticateUsingLoginPage(testUser1);
        peopleFinderPage.navigate();

        log.info("STEP 1 - Fill in search field with a partial username (e.g: \"us\")");
        peopleFinderPage.typeSearchInput("user" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "user" + identifier, "Search input value");

        log.info("STEP 2 - Click \"Search\" button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "user" + identifier, "2"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user" + identifier + "1"), "User " + "user" + identifier + "1" + " is displayed");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user" + identifier + "2"), "User " + "user" + identifier + "2" + " is displayed");

    }

    @TestRail (id = "C5822")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE })
    public void searchByFullName()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        log.info("PreCondition1: Any test users are created");
        final String user1 = "user1" + identifier;
        final String user2 = "user2" + identifier;

        testUser1 = dataUser.usingAdmin().createUser(user1, password);
        testUser2 = dataUser.usingAdmin().createUser(user2, password);
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingLoginPage(getAdminUser());

        log.info("Edit user data & give specific name to " + testUser1);
        UserModel editUser1 = testUser1;
        String firstName1 = identifier;
        String lastName1 = identifier;
        String email1 = "user1" + identifier + "@test.com";

        editUserPage.navigate(editUser1)
            .editFirstName(firstName1)
            .editLastNameField(lastName1)
            .editEmailField(email1)
            .clickSaveChanges();

        log.info("Edit user data & give specific name to " + testUser2);
        UserModel editUser2 = testUser2;
        String firstName2 = "firstname" + identifier;
        String lastName2 = "lastname" + identifier;
        String email2 = "user2" + identifier + "@test.com";

        editUserPage.navigate(editUser2)
            .editFirstName(firstName2)
            .editLastNameField(lastName2)
            .editEmailField(email2)
            .clickSaveChanges();

        authenticateUsingLoginPage(testUser1);
        peopleFinderPage.navigate();

        log.info("STEP 1 - Fill in search field with user's full name (e.g.: \"firstName lastName\")");
        peopleFinderPage.typeSearchInput("firstName" + identifier + " lastName" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "firstName" + identifier + " lastName" + identifier, "Search input value");

        log.info("STEP 2 - Click \"Search\" button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "firstName" + identifier + " lastName" + identifier, "1"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user2" + identifier), "User " + "user2" + identifier + " is displayed");

        log.info("STEP 3 - Fill in search field with user's full name (e.g.: \"lastName firstName\")");
        peopleFinderPage.typeSearchInput("lastName" + identifier + " firstName" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "lastName" + identifier + " firstName" + identifier, "Search input value");

        log.info("STEP 4 - Click \"Search\" button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "lastName" + identifier + " firstName" + identifier, "1"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user2" + identifier), "User " + "user2" + identifier + " is displayed");

    }

    @TestRail (id = "C5832")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE })
    public void searchByPartialName()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        log.info("PreCondition1: Any test users are created");
        final String user1 = "user" + identifier + "1";
        final String user2 = "user" + identifier + "2";

        testUser1 = dataUser.usingAdmin().createUser(user1, password);
        testUser2 = dataUser.usingAdmin().createUser(user2, password);
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingLoginPage(getAdminUser());

        log.info("Edit user data & give specific name to " + testUser1);
        UserModel editUser1 = testUser1;
        String firstName1 = "firstname" + identifier;
        String lastName1 = "lastName" + identifier;
        String email1 = "user" + identifier + "1" + "@test.com";

        editUserPage.navigate(editUser1)
            .editFirstName(firstName1)
            .editLastNameField(lastName1)
            .editEmailField(email1)
            .clickSaveChanges();

        log.info("Edit user data & give specific name to " + testUser1);
        UserModel editUser2 = testUser2;
        String firstName2 = "firstname" + identifier;
        String lastName2 = "lastname" + identifier;
        String email2 = "user" + identifier + "2" + "@test.com";

        editUserPage.navigate(editUser2)
            .editFirstName(firstName2)
            .editLastNameField(lastName2)
            .editEmailField(email2)
            .clickSaveChanges();

        authenticateUsingLoginPage(testUser1);
        peopleFinderPage.navigate();

        log.info("STEP 1 - Fill in search field with a partial name (e.g: \"firstname\")");
        peopleFinderPage.typeSearchInput("firstName" + identifier);
        Assert.assertEquals(peopleFinderPage.getSearchInputFieldValue(), "firstName" + identifier, "Search input value");

        log.info("STEP 2 - Click \"Search\" button");
        peopleFinderPage.clickSearchAndWaitForResults();
        Assert.assertEquals(peopleFinderPage.getSearchResultsInfo(),
            String.format(language.translate("peopleFinder.searchResultsInfo"), "firstName" + identifier, "2"), "Search results info");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user" + identifier + "1"), "User " + "user" + identifier + "1" + " is displayed");
        Assert.assertTrue(peopleFinderPage.isUserDisplayed("user" + identifier + "2"), "User " + "user" + identifier + "2" + " is displayed");
    }
}
