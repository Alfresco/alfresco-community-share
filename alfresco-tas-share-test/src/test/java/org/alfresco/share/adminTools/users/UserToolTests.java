package org.alfresco.share.adminTools.users;

import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UsersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UserToolTests extends ContextAwareWebTest
{

    @Autowired
    AdminToolsPage adminTools;

    @Autowired
    UsersPage usersPage;

    @TestRail(id = "C9392")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void accessingUsersTool()
    {
        String userName = String.format("userName%s", RandomData.getRandomAlphanumeric());
        String groupName = "ALFRESCO_ADMINISTRATORS";

        LOG.info("Preconditions: User with administrator rights is created and logged into Share.");
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        groupService.addUserToGroup(adminUser, adminPassword, groupName, userName);
        setupAuthenticatedSession(userName, password);

        LOG.info("Step1: Click 'Admin Tools' link on the header bar.");
        adminTools.navigateByMenuBar();

        LOG.info("Step2: Click 'Users' link on 'Tools' pane.");
        adminTools.navigateToNodeFromToolsPanel("Users", usersPage);

        LOG.info("Step3: Click 'Users' link on 'Tools' pane and verify 'User Search' page contains correct data");
        assertTrue(usersPage.isSearchBoxDisplayed(), "Search box displayed");
        assertTrue(usersPage.isSearchButtonDisplayed(), "Search button displayed");
        assertTrue(usersPage.isNewUserButtonDisplayed(), "New User displayed");
        assertTrue(usersPage.isImportUsersButtonDisplayed(), "Import User CSV File");
        assertTrue(usersPage.isSpecificUserDataDisplayed("Name"), "Name column displayed");
        assertTrue(usersPage.isSpecificUserDataDisplayed("User Name"), "User Name column displayed");
        assertTrue(usersPage.isSpecificUserDataDisplayed("Job Title"), "Job Title column displayed");
        assertTrue(usersPage.isSpecificUserDataDisplayed("Email"), "Email column displayed");
        assertTrue(usersPage.isSpecificUserDataDisplayed("Usage"), "Usage column displayed");
        assertTrue(usersPage.isSpecificUserDataDisplayed("Quota"), "Quota column displayed");
//        assertTrue(usersPage.isSpecificUserDataDisplayed("Authorization State"), "Authorization State column displayed");
//        assertTrue(usersPage.isSpecificUserDataDisplayed("Deleted?"), "Deleted? column displayed");
 //       assertTrue(usersPage.isSpecificUserDataDisplayed("Action"), "Action column displayed");

    }

    @TestRail(id = "C9393")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void searchingForUser()
    {

        String uniqueIdentifier = RandomData.getRandomAlphanumeric();
        String userName1 = "C9392User" + uniqueIdentifier + "1";
        String userName2 = "C9392User" + uniqueIdentifier + "2";
        String groupName = "ALFRESCO_ADMINISTRATORS";

        LOG.info("Preconditions: Two users are created; a user with administrator rights is logged into Share.");
        userService.create(adminUser, adminPassword, userName1, password, "alf@test.com", userName1, userName1);
        userService.create(adminUser, adminPassword, userName2, password, "alf@test.com", userName2, userName2);
        groupService.addUserToGroup(adminUser, adminPassword, groupName, userName1);
        setupAuthenticatedSession(userName1, password);

        LOG.info("Step1: Navigate to 'Admin Tools' and click 'Users' link on 'Tools' pane.");
        adminTools.navigate();
        adminTools.navigateToNodeFromToolsPanel("Users", usersPage);

        LOG.info("Step2: Type a string contained by both usernames on the search box; click 'Search' button.");
        usersPage.searchUser("C9392User" + uniqueIdentifier);
        assertEquals(usersPage.verifyUserIsFound(userName1), true, "User " + userName1 + " found");
        assertEquals(usersPage.verifyUserIsFound(userName2), true, "User " + userName2 + " found");

        LOG.info("Step3: Type a string contained only by one of the created users into the search box and click 'Search' button.");
        usersPage.searchUser(userName1);
        assertEquals(usersPage.verifyUserIsFound(userName1), true, "User " + userName1 + " found");
        assertEquals(usersPage.verifyUserIsFound(userName2), false, "User " + userName2 + " found");

    }
}
