package org.alfresco.share.userRolesAndPermissions.consumer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.ContentActions;
import org.alfresco.dataprep.UserService;
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
public class ConsumerFoldersOnlyTests extends BaseTest
{
    @Autowired
    UserService userService;
    @Autowired
    ContentActions contentAction;
    private DocumentLibraryPage documentLibraryPage;
    private DocumentsFilters documentsFilters;
    private final String uniqueId = RandomData.getRandomAlphanumeric();
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private final String folderName = "Folder-" + uniqueId;
    private final String subFolderName = "subFolder-" + uniqueId;
    private final String tag = "tag-" + uniqueId.toLowerCase();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(getAdminUser()).createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), user.get().getUsername(), siteName.get().getId(), "SiteConsumer");
        String path = "Sites/" + siteName.get().getTitle() + "/documentLibrary/" + folderName;
        contentService.createFolder(getAdminUser().getUsername(), getAdminUser().getPassword(), folderName, siteName.get().getId());
        contentService.createFolderInRepository(getAdminUser().getUsername(), getAdminUser().getPassword(), subFolderName, path);
        contentAction.addSingleTag(getAdminUser().getUsername(), getAdminUser().getPassword(), path + "/" + subFolderName, tag);

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentsFilters = new DocumentsFilters(webDriver);

        authenticateUsingLoginPage(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user.get());
    }

    @TestRail (id = "C8867")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_ROLES, TestGroup.INTEGRATION })
    public void locateFolder()
    {
        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page= ");

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

    @TestRail (id = "C8869")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void manageRulesFolderCreatedByOther()
    {
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.clickFolderFromExplorerPanel(folderName);
        log.info("STEP1: Mouse over folder and verify presence of \"Manage Rules\" option");
        assertFalse(documentLibraryPage.isActionAvailableForConsumerLibraryItem(subFolderName, ItemActions.MANAGE_RULES),
            "'Manage Rules' option is displayed for " + subFolderName);
    }
}
