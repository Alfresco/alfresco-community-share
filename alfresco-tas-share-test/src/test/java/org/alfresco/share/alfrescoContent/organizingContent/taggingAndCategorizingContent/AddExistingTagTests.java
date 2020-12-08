package org.alfresco.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class AddExistingTagTests extends ContextAwareWebTest
{
    private final String userName = String.format("profileUser-%s", RandomData.getRandomAlphanumeric());
    private final String fileContent = "content of the file.";
    private final String random = RandomData.getRandomAlphanumeric();
    private final String siteName = "site-C7464-" + random;
    private final String fileName = "file-C7464-" + random;
    private final String folderName = "folder-C7464-" + random;
    private final String tagName1 = "C7464-1" + random;
    private final String tagName2 = "C7464-2" + random;
    //@Autowired
    private DocumentLibraryPage documentLibraryPage;
    //@Autowired
    private EditPropertiesDialog editPropertiesDialog;
    //@Autowired
    private SelectDialog selectDialog;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "FirstName", "LastName");
        siteService.create(userName, password, domain, siteName, "Description", SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentService.createFolder(userName, password, folderName, siteName);
        contentAction.addSingleTag(userName, password, siteName, fileName, tagName1);
        contentAction.addSingleTag(userName, password, siteName, folderName, tagName2);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C7464")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addExistingTagFromEditPropertiesDialog()
    {
        LOG.info("STEP1: Hover over the content created in the preconditions. STEP2: Click \"Edit Properties\" option");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, ItemActions.EDIT_PROPERTIES);
        assertEquals(editPropertiesDialog.getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), fileName), "Displayed dialog=");
        assertTrue(editPropertiesDialog.isSelectTagsButtonDisplayed(), "'Select' tag button is displayed.");

        LOG.info("STEP3: Click \"Select\" button");
        editPropertiesDialog.clickSelectTags();

        LOG.info("STEP4: Pick any tag from the available tags list and click \"Add\"");
        selectDialog.typeTag(tagName2.toLowerCase());
        selectDialog.selectItems(Collections.singletonList(tagName2.toLowerCase()));
        assertTrue(selectDialog.isItemSelected(tagName2.toLowerCase()), tagName2.toLowerCase() + " is displayed in selected categories list.");
        assertFalse(selectDialog.isItemSelectable(tagName2.toLowerCase()), tagName2.toLowerCase() + " -> 'Add' icon isn't displayed.");

        LOG.info("STEP5: Click \"Ok\" button");
        selectDialog.clickOk();
        assertTrue(editPropertiesDialog.isTagSelected(tagName1.toLowerCase()), "'Tags:' section=");

        LOG.info("STEP6: Click \"Save\" button");
        editPropertiesDialog.clickSave();
        ArrayList<String> tagsList = new ArrayList<>(Arrays.asList(tagName1.toLowerCase(), tagName2.toLowerCase()));
        assertEquals(documentLibraryPage.getTags(fileName), tagsList.toString(), fileName + " -> tags=");

        cleanupAuthenticatedSession();
    }
}