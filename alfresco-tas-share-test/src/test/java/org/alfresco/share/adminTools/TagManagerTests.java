package org.alfresco.share.adminTools;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.user.admin.adminTools.EditTagDialog;
import org.alfresco.po.share.user.admin.adminTools.TagManagerPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class TagManagerTests extends ContextAwareWebTest
{
    @Autowired
    private TagManagerPage tagManagerPage;

    @Autowired
    private DeleteDialog deleteDialog;

    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    
    @Autowired
    private EditTagDialog editTagDialog;

    private final String uniqueIdentifier = DataUtil.getUniqueIdentifier();
    private final String user = "user-" + uniqueIdentifier;
    private final String userAdmin = "userAdmin-" + uniqueIdentifier;
    private final String site = "site-" + uniqueIdentifier;
    private final String siteDescription = "site Description " + uniqueIdentifier;
    private final String content = "content" + uniqueIdentifier;
    private final String updatedTag = "updated" + uniqueIdentifier;
    private String fileName;
    private String tag;

    @BeforeClass(alwaysRun = true)
    public void setupClass()
    {
        String name = "name";
        userService.create(adminUser, adminPassword, user, password, domain, name, user);
        userService.create(adminUser, adminPassword, userAdmin, password, domain, name, userAdmin);
        groupService.addUserToGroup(adminUser, adminPassword, "ALFRESCO_ADMINISTRATORS", userAdmin);
        siteService.create(user, password, domain, site, siteDescription, Site.Visibility.PUBLIC);
    }

    private void preconditionsTest(String id)
    {
        fileName = "file-" + id + "-" + uniqueIdentifier;
        tag = id + "-" + uniqueIdentifier;
        contentService.createDocument(user, password, site, CMISUtil.DocumentType.TEXT_PLAIN, fileName, content);
        contentAction.addSingleTag(user, password, site, fileName, tag);

        setupAuthenticatedSession(userAdmin, password);
    }

    @TestRail(id = "C9383")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void renamingTag()
    {
        preconditionsTest("c9383");

        tagManagerPage.navigate();
        assertEquals(tagManagerPage.getRelativePath(), "share/page/console/admin-console/tag-management", "Displayed url=");

        LOG.info("STEP1: Click 'Edit tag' for content");
        assertEquals(tagManagerPage.clickEditTagIcon(tag), "'Edit tag' icon clicked for " + tag, "Result on click edit tag icon=");
        assertTrue(tagManagerPage.isEditTagDialogDisplayed(), "'Edit Tag' dialog is displayed for " + fileName + " -> tag " + tag);

        LOG.info("STEP2: Type tag in dialog, and click 'Ok' button");
        editTagDialog.renameTag(updatedTag);
        assertTrue(tagManagerPage.isTagDisplayed(updatedTag), tag + " for " + fileName + " updated to= " + updatedTag);

        LOG.info("STEP3: Login as user who created content. Navigate to Document Library page");
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(site);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertEquals(documentLibraryPage.getTags(fileName), Collections.singletonList(updatedTag).toString(), fileName + " 's tags=");
    }

    @TestRail(id = "C9385")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifyTagManagerPage()
    {
        preconditionsTest("c9385");

        tagManagerPage.navigate();
        assertEquals(tagManagerPage.getRelativePath(), "share/page/console/admin-console/tag-management", "Displayed url=");

        LOG.info("STEP1: Verify 'Tag Manager' page");
        assertTrue(tagManagerPage.isSearchButtonDisplayed(), "'Search' button is displayed.");
        assertTrue(tagManagerPage.isSearchInputFieldDisplayed(), "'Search' input field is displayed.");
        assertEquals(tagManagerPage.getTableTitle(), language.translate("tagManager.tableTitle"), "Tags List section-> Table title= ");
        assertEquals(tagManagerPage.getTableHead(), language.translate("tagManager.tableHead"), "Tags List section-> Table head=");

        LOG.info("STEP2: Click \"edit tag\" icon for any content");
        assertEquals(tagManagerPage.clickEditTagIcon(tag), "'Edit tag' icon clicked for " + tag, "Result on click edit tag icon=");
        assertTrue(tagManagerPage.isEditTagDialogDisplayed(), "'Edit Tag' input field is displayed for " + fileName + " -> tag " + tag);
        assertEquals(tagManagerPage.getRenameLabel(), language.translate("editTag.renameLabel"), "'Edit Tag' dialog: rename label=");
        assertTrue(tagManagerPage.isOkButtonDisplayed(), "'Edit Tag' dialog: Ok button is displayed.");
        assertTrue(tagManagerPage.isCancelButtonDisplayed(), "'Edit Tag' dialog: Cancel button is displayed.");
        assertEquals(tagManagerPage.getRequiredInput(), " *", "'Edit Tag' dialog: Rename tag is marked as required field with=");
    }

    @TestRail(id = "C9388")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteTag()
    {
        preconditionsTest("c9388");

        tagManagerPage.navigate();
        assertEquals(tagManagerPage.getRelativePath(), "share/page/console/admin-console/tag-management", "Displayed url=");

        LOG.info("STEP1: Hover any tag and click \"Delete\" button");
        assertEquals(tagManagerPage.clickDeleteTagIcon(tag), "'Delete tag' icon clicked for " + tag, "Result on click Delete tag icon=");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), tag), "'Delete Tag' dialog message=");
        assertTrue(deleteDialog.isDeleteButtonDisplayed(), "'Delete Tag' dialog: Delete button is displayed.");
        assertTrue(deleteDialog.isCancelButtonDisplayed(), "'Delete Tag' dialog: Cancel button is displayed.");

        LOG.info("STEP2: Click 'Delete' button");
        deleteDialog.clickDelete();
        assertFalse(tagManagerPage.isTagDisplayed(tag), tag + " is displayed.");
    }
}