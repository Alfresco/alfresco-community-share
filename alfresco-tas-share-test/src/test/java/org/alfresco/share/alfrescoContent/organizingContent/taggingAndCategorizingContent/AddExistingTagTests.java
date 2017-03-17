package org.alfresco.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class AddExistingTagTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private EditPropertiesDialog editPropertiesDialog;

    @Autowired private SelectDialog selectDialog;

    private final String userName = "profileUser-" + DataUtil.getUniqueIdentifier();
    private final String firstName = "FirstName";
    private final String lastName = "LastName";
    private final String description = "Description-" + DataUtil.getUniqueIdentifier();
    private final String fileContent = "content of the file.";
    private final String random = DataUtil.getUniqueIdentifier();
    private final String siteName = "site-C7464-" + random;
    private final String fileName = "file-C7464-" + random;
    private final String folderName = "folder-C7464-" + random;
    private final String tagName1 = "tagName1-C7464-" + random;
    private final String tagName2 = "tagName2-C7464-" + random;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
        siteService.create(userName, password, domain, siteName, description, Site.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentService.createFolder(userName, password, folderName, siteName);
        contentAction.addSingleTag(userName, password, siteName, fileName, tagName1);
        contentAction.addSingleTag(userName, password, siteName, folderName, tagName2);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");
    }

    @TestRail(id = "C7464")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void addExistingTagFromEditPropertiesDialog()
    {
        LOG.info("STEP1: Hover over the content created in the preconditions");
        documentLibraryPage.mouseOverContentItem(fileName);
        assertTrue(documentLibraryPage.isMoreMenuDisplayed(fileName), "'More' menu is displayed.");
        documentLibraryPage.clickMore();

        LOG.info("STEP2: Click \"Edit Properties\" option");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit Properties", documentLibraryPage);
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
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertEquals(documentLibraryPage.getTags(fileName), tagsList.toString(), fileName + " -> tags=");

        cleanupAuthenticatedSession();
    }
}