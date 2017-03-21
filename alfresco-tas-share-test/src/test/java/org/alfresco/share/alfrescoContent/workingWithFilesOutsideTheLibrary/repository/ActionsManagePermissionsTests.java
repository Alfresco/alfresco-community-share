package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ManagePermissionsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 3/20/2017.
 */
public class ActionsManagePermissionsTests extends ContextAwareWebTest
{
    @Autowired
    UserService userService;

    @Autowired
    RepositoryPage repositoryPage;

    @Autowired
    ManagePermissionsPage managePermissionsPage;

    private String userName = "0_0C202757User" + DataUtil.getUniqueIdentifier();

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName+"@test.com", userName, userName);
        setupAuthenticatedSession(userName, password);
    }

    @TestRail(id="C202757")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void managePermissionOption()
    {
        String identifier = userName+" "+ userName;
        LOG.info("Step 1: Click on Repository link in the toolbar");
        repositoryPage.navigate();
        Assert.assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "User is not on the Repository Page");

        LOG.info("Step 2: Navigate to " +userName+" folder;");
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(userName), userName+" is not displayed in Repository Page");

        LOG.info("Step 3: Click Manage Permissions link in More menu for user's home folder;");
        repositoryPage.clickDocumentLibraryItemAction(userName, "Manage Permissions", managePermissionsPage);
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » Manage Permissions", "User is not on Manage Permissions Page");

        LOG.info("Step 4: Verify Manage Permissions page");
        Assert.assertTrue(managePermissionsPage.isAddUserGroupButtonDisplayed(), "Add User/Group button is not displayed");
        Assert.assertTrue(managePermissionsPage.isTheSaveButtonDisplayed(), "The Save button is not displayed");
        Assert.assertTrue(managePermissionsPage.isCancelButtonDisplayed(), "The Cancel button is not displayed");
        Assert.assertTrue(managePermissionsPage.isLocallySetPermissionsListDisplayed(), "Locally Set Permissions is not displayed on the Manage Permissions page");
        Assert.assertTrue(managePermissionsPage.getRowDetails(identifier).contains("All"), "All Role is not available for "+ identifier);
        Assert.assertTrue(managePermissionsPage.getRowDetails("ROLE_OWNER").contains("All"), "All Role is not available for ROLE_OWNER");
        Assert.assertTrue(managePermissionsPage.isDeleteButtonAvailable(identifier), "Delete button is not available for "+ identifier);
        //Assert.assertTrue(managePermissionsPage.isDeleteButtonAvailable("ROLE_OWNER"), "Delete button is not available for ROLE_OWNER");
    }
}
