package org.alfresco.share.adminTools;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.user.admin.adminTools.EditTagDialog;
import org.alfresco.po.share.user.admin.adminTools.TagManagerPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * UI tests for Admin Tools > Tag Manager page
 */
public class TagManagerTests extends ContextAwareWebTest
{
    private final String uniqueIdentifier = RandomData.getRandomAlphanumeric().toLowerCase();
    private final String user = "user-" + uniqueIdentifier;
    private final String userAdmin = "userAdmin-" + uniqueIdentifier;
    private final String site = "site-" + uniqueIdentifier;
    private final String siteDescription = "site Description " + uniqueIdentifier;
    private final String content = "content" + uniqueIdentifier;
    private final String updatedTag = "updated" + uniqueIdentifier;
    private final String fileName = "file-" + uniqueIdentifier;
    private final String tag1 = "tag1" + uniqueIdentifier;
    private final String tag2 = "tag2" + uniqueIdentifier;
    private final String tag3 = "tag3" + uniqueIdentifier;
    @Autowired
    private TagManagerPage tagManagerPage;
    @Autowired
    private DeleteDialog deleteDialog;
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private EditTagDialog editTagDialog;

    @BeforeClass (alwaysRun = true)
    public void setupClass()
    {
        userService.create(adminUser, adminPassword, user, password, domain, user, user);
        userService.create(adminUser, adminPassword, userAdmin, password, domain, userAdmin, userAdmin);
        groupService.addUserToGroup(adminUser, adminPassword, "ALFRESCO_ADMINISTRATORS", userAdmin);
        siteService.create(user, password, domain, site, siteDescription, SiteService.Visibility.PUBLIC);
        contentService.createDocument(user, password, site, CMISUtil.DocumentType.TEXT_PLAIN, fileName, content);
        contentAction.addMultipleTags(user, password, site, fileName, Arrays.asList(tag1, tag2, tag3));
        setupAuthenticatedSession(userAdmin, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        userService.delete(adminUser, adminPassword, userAdmin);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userAdmin);
        siteService.delete(adminUser, adminPassword, site);
    }

    @BeforeMethod (alwaysRun = true)
    private void preconditionsTest()
    {
        tagManagerPage.navigate();
    }

    @TestRail (id = "C9383")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void renamingTag()
    {
        LOG.info("STEP1: Click 'Edit tag' for content");
        tagManagerPage.clickEditTagIcon(tag1);

        LOG.info("STEP2: Type tag in dialog, and click 'Ok' button");
        editTagDialog.renameTag(updatedTag);
        assertTrue(tagManagerPage.searchTag(updatedTag), "Tag for " + fileName + " was not updated to value=" + updatedTag);

        LOG.info("STEP3: Login as user who created content. Navigate to Document Library page");
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(site);
        assertTrue(documentLibraryPage.getTags(fileName).contains(updatedTag), "Tags found =" + documentLibraryPage.getTags(fileName));
        setupAuthenticatedSession(userAdmin, password);
    }

    @TestRail (id = "C9385")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifyTagManagerPage()
    {
        LOG.info("STEP1: Verify 'Tag Manager' page");
        assertTrue(tagManagerPage.isSearchButtonDisplayed(), "'Search' button is displayed.");
        assertTrue(tagManagerPage.isSearchInputFieldDisplayed(), "'Search' input field is displayed.");
        assertEquals(tagManagerPage.getTableTitle(), language.translate("tagManager.tableTitle"), "Tags List section-> Table title= ");
        assertEquals(tagManagerPage.getTableHead(), language.translate("tagManager.tableHead"), "Tags List section-> Table head=");

        LOG.info("STEP2: Click \"edit tag\" icon for any content");
        tagManagerPage.clickEditTagIcon(tag2);
        assertEquals(editTagDialog.getRenameLabel(), language.translate("editTag.renameLabel"), "'Edit Tag' dialog: rename label=");
        assertTrue(editTagDialog.isOkButtonDisplayed(), "'Edit Tag' dialog: Ok button is displayed.");
        assertTrue(editTagDialog.isCancelButtonDisplayed(), "'Edit Tag' dialog: Cancel button is displayed.");
        assertEquals(editTagDialog.getRequiredSymbol(), " *", "'Edit Tag' dialog: Rename tag is marked as required field with=");
    }

    @TestRail (id = "C9388")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteTag()
    {
        LOG.info("STEP1: Hover any tag and click \"Delete\" button");
        tagManagerPage.clickDeleteTagIcon(tag3);
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), tag3), "'Delete Tag' dialog message=");
        assertTrue(deleteDialog.isDeleteButtonDisplayed(), "'Delete Tag' dialog: Delete button is displayed.");
        assertTrue(deleteDialog.isCancelButtonDisplayed(), "'Delete Tag' dialog: Cancel button is displayed.");

        LOG.info("STEP2: Click 'Delete' button");
        deleteDialog.clickDelete(tagManagerPage);
        assertFalse(tagManagerPage.isTagDisplayed(tag3), tag3 + " is displayed.");
    }
}