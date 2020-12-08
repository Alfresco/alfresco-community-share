package org.alfresco.share.adminTools.users;

import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UsersPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.alfresco.share.TestUtils.PASSWORD;

public class UserToolTests extends BaseTest
{
    private UsersPage usersPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        usersPage = new UsersPage(browser);
        setupAuthenticatedSession(getAdminUser());
    }

    @TestRail (id = "C9392")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void accessingUsersTool()
    {
        usersPage.navigate();
        usersPage.assertSearchButtonIsDisplayed()
            .assertSearchInputIsDisplayed()
            .assertNewUserButtonIsDisplayed()
            .assertImportUsersButtonIsDisplayed()
            .assertAllTableHeadersAreDisplayed();
    }

    @TestRail (id = "C9393")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifySearch()
    {
        String identifier = RandomData.getRandomAlphanumeric();
        UserModel user1 = new UserModel(identifier + "C9392User1", PASSWORD);
        UserModel user2 = new UserModel(identifier + "C9392User2", PASSWORD);
        dataUser.usingAdmin().createUser(user1);
        dataUser.usingAdmin().createUser(user2);

        usersPage.navigate();

        usersPage.searchForWithRetry(identifier, user2.getUsername())
            .assertSearchTextIsCorrect(identifier, 2)
            .usingUser(user1).assertUserIsFound();
        usersPage.usingUser(user2).assertUserIsFound();
        usersPage.searchUserWithRetry(user1.getUsername())
            .assertSearchTextIsCorrect(user1.getUsername(), 1)
            .usingUser(user1).assertUserIsFound();
        usersPage.usingUser(user2).assertUserIsNotFound();

        removeUserFromAlfresco(user1, user2);
    }
}
