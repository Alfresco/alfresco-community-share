package org.alfresco.share.userRolesAndPermissions.contributor;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.springframework.social.alfresco.api.entities.Role.SiteContributor;
import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class FoldersOnlyTests extends ContextAwareWebTest
{
    @Autowired
    private DocumentsFilters documentsFilters;

    @Autowired
    private DocumentLibraryPage documentLibraryPage;

    @Autowired
    private CreateContent createContent;

    @Autowired
    private ManageRulesPage manageRulesPage;

    private final String uniqueId = DataUtil.getUniqueIdentifier();
    private final String user = "User-" + uniqueId;
    private final String site = "site-" + uniqueId;
    private final String name = "name";
    private final String description = "Description";
    private final String folderName = "Folder-" + uniqueId;
    private final String folderName2 = "Folder2-" + uniqueId;
    private final String folderName3 = "Folder3-" + uniqueId;
    private final String subFolderName = "subFolder-" + uniqueId;
    private final String path = "Sites/" + site + "/documentLibrary/" + folderName;
    private final String tag = "tag-" + uniqueId;
    private final String title = "Title-" + uniqueId;

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, domain, name, user);
        siteService.create(adminUser, adminPassword, domain, site, description, Site.Visibility.PUBLIC);
        userService.inviteUserToSiteAndAccept(adminUser, adminPassword, user, site, String.valueOf(SiteContributor));
        contentService.createFolder(adminUser, adminPassword, folderName, site);
        contentService.createFolder(user, password, folderName3, site);
        contentService.createFolderInRepository(adminUser, adminPassword, subFolderName, path);
        contentAction.addSingleTag(adminUser, adminPassword, path + "/" + subFolderName, tag);

        setupAuthenticatedSession(user, password);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");
    }

    @TestRail(id = "C8874")
    @Test
    public void collaboratorCreateFolder()
    {
        documentLibraryPage.navigate(site);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("STEP1: Click on 'Create' button");
        documentLibraryPage.clickCreateButton();
        assertTrue(documentLibraryPage.isCreateContentMenuDisplayed(), "'Create content' menu is displayed.");

        LOG.info("STEP2: Select 'Folder' option");
        documentLibraryPage.clickFolderLink();
        assertTrue(createContent.isCreateFolderDialogDisplayed(), "'Create folder' dialog is displayed.");

        LOG.info("STEP3: Set input for name, title, description and click on Save button");
        createContent.sendInputForName(folderName2);
        createContent.sendInputForTitle(title);
        createContent.sendInputForDescription(description);
        createContent.clickSaveButtonOnCreateFormTemplate();
        createContent.clickCreateButton();
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName2), String.format("Folder [%s] is displayed in Document Library.", folderName2));
    }

    @TestRail(id = "C8875")
    @Test
    public void locateFolder()
    {
        documentLibraryPage.navigate(site);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page= ");

        LOG.info("STEP1: From \"Document View\" left side panel, click 'My Favorites'");
        documentsFilters.clickSidebarTag(tag);
        assertTrue(documentLibraryPage.isContentNameDisplayed(subFolderName), subFolderName + " is displayed in 'My Favorites'.");

        LOG.info("STEP2: Click 'More' menu for " + subFolderName + ", and verify presence of \"Locate Folder\" option");
        documentLibraryPage.clickMoreMenu(subFolderName);
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(subFolderName, language.translate("documentLibrary.contentActions.locateFolder")),
                "'Locate Folder' option is displayed for " + subFolderName);

        LOG.info("STEP3: Click \"Locate Folder\" option");
        documentLibraryPage.clickOnAction(subFolderName, language.translate("documentLibrary.contentActions.locateFolder"));
        assertEquals(documentLibraryPage.getBreadcrumbList(), Arrays.asList("Documents", folderName).toString(), "Breadcrumb=");
    }

    @TestRail(id = "C8876")
    @Test()
    public void manageRulesFolderSelfCreated()
    {
        documentLibraryPage.navigate(site);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("STEP1: Mouse over folder and verify presence of \"Manage Rules\" option");
        documentLibraryPage.clickMoreMenu(folderName3);
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(folderName3, language.translate("documentLibrary.contentActions.manageRules")),
                "'Manage Rules' option is displayed for " + folderName3);

        LOG.info("STEP2: Click 'Manage Rules' option for " + folderName3);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName3, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
    }

    @TestRail(id = "C8877")
    @Test()
    public void manageRulesFolderCreatedByOther()
    {
        LOG.info("STEP1: Mouse over folder and verify presence of \"Manage Rules\" option");
        documentLibraryPage.clickMoreMenu(subFolderName);
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(subFolderName, language.translate("documentLibrary.contentActions.manageRules")),
                "'Manage Rules' option is displayed for " + subFolderName);
    }
}
