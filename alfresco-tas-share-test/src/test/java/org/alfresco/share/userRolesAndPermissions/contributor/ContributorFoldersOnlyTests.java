package org.alfresco.share.userRolesAndPermissions.contributor;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.ContentActions;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
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

/**
 * @author Laura.Capsa
 */
@Slf4j
public class ContributorFoldersOnlyTests extends BaseTest
{
    @Autowired
    UserService userService;
    @Autowired
    ContentActions contentAction;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private final String uniqueId = RandomData.getRandomAlphanumeric();
    private final String description = "Description";
    private final String folderName = "Folder-" + uniqueId;
    private final String folderName2 = "Folder2-" + uniqueId;
    private final String folderName3 = "Folder3-" + uniqueId;
    private final String subFolderName = "subFolder-" + uniqueId;
    private final String tag = "tag-" + uniqueId.toLowerCase();
    private final String title = "Title-" + uniqueId;
    private DocumentsFilters documentsFilters;
    private DocumentLibraryPage documentLibraryPage;
    private NewFolderDialog newContentDialog;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        site.set(getDataSite().usingAdmin().createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), user.get().getUsername(), site.get().getId(), "SiteContributor");

        String path = "Sites/" + site.get().getTitle() + "/documentLibrary/" + folderName;
        contentService.createFolder(getAdminUser().getUsername(), getAdminUser().getPassword(), folderName, site.get().getId());
        contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName3, site.get().getId());
        contentService.createFolderInRepository(getAdminUser().getUsername(), getAdminUser().getPassword(), subFolderName, path);
        contentAction.addSingleTag(getAdminUser().getUsername(), getAdminUser().getPassword(), path + "/" + subFolderName, tag);

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentsFilters = new DocumentsFilters(webDriver);
        newContentDialog = new NewFolderDialog(webDriver);

        authenticateUsingLoginPage(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user.get().getUsername());
        deleteSitesIfNotNull(site.get());
        deleteUsersIfNotNull(user.get());
    }


    @TestRail (id = "C8874")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void collaboratorCreateFolder()
    {
        log.info("STEP1: Click on 'Create' button");
        documentLibraryPage.navigate(site.get().getId());
        documentLibraryPage.clickCreateButton();

        log.info("STEP2: Select 'Folder' option");
        documentLibraryPage.clickFolderLink();
        assertTrue(newContentDialog.isNewFolderPopupDisplayed(), "'Create folder' dialog is displayed.");

        log.info("STEP3: Set input for name, title, description and click on Save button");
        newContentDialog.fillInDetails(folderName2, title, description);
        newContentDialog.clickSave();
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName2), String.format("Folder [%s] is displayed in Document Library.", folderName2));
    }

    @TestRail (id = "C8875")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void locateFolder()
    {
        documentLibraryPage.navigate(site.get().getId());
        log.info("STEP1: From \"Document View\" left side panel, click 'My Favorites'");
        documentsFilters.clickSidebarTag(tag);
        assertTrue(documentLibraryPage.isContentNameDisplayed(subFolderName), subFolderName + " is displayed in 'My Favorites'.");

        log.info("STEP2: Click 'More' menu for " + subFolderName + ", and verify presence of \"Locate Folder\" option");
        assertTrue(documentLibraryPage.isActionAvailableForConsumerLibraryItem(subFolderName, ItemActions.LOCATE_FOLDER),
            "'Locate Folder' option is displayed for " + subFolderName);

        log.info("STEP3: Click \"Locate Folder\" option");
        documentLibraryPage.selectConsumerItemAction(subFolderName, ItemActions.LOCATE_FOLDER);
        assertEquals(documentLibraryPage.getBreadcrumb(), Arrays.asList("Documents", folderName).toString(), "Breadcrumb=");
    }

    @TestRail (id = "C8876")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void manageRulesFolderSelfCreated()
    {
        documentLibraryPage.navigate(site.get().getId());

        log.info("STEP1: Mousfe over folder and verify presence of \"Manage Rules\" option");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(folderName3, ItemActions.MANAGE_RULES),
            "'Manage Rules' option is displayed for " + folderName3);

        log.info("STEP2: Click 'Manage Rules' option for " + folderName3);
        documentLibraryPage.selectItemAction(folderName3, ItemActions.MANAGE_RULES);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
    }

    @TestRail (id = "C8877")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void manageRulesFolderCreatedByOther()
    {
        documentLibraryPage.navigate(site.get().getId());
        documentLibraryPage.clickFolderFromExplorerPanel(folderName);
        log.info("STEP1: Mouse over folder and verify presence of \"Manage Rules\" option");
        assertFalse(documentLibraryPage.isActionAvailableForConsumerLibraryItem(subFolderName, ItemActions.MANAGE_RULES),
            "'Manage Rules' option is displayed for " + subFolderName);
    }
}
