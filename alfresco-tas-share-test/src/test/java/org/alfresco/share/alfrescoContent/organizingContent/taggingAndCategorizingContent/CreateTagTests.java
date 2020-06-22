package org.alfresco.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
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
public class CreateTagTests extends ContextAwareWebTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String userName = "profileUser-" + random;
    private final String siteName = "site" + random;
    private final String fileName = "fileC10209-" + random;
    private final String folderName = "folderC10210-" + random;
    private final String fileTag = "tagC10209-" + random;
    private final String folderTag = "tagC10210-" + random;
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private EditPropertiesDialog editPropertiesDialog;
    @Autowired
    private SelectDialog selectDialog;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "FirstName", "LastName");
        siteService.create(userName, password, domain, siteName, "Description", SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, "content of the file.");
        contentService.createFolder(userName, password, folderName, siteName);

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


    @TestRail (id = "C10209")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createTagEditPropertiesDialog()
    {
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Hover over the file name. STEP2: Click \"Edit Properties\" link");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, ItemActions.EDIT_PROPERTIES, documentLibraryPage);
        assertEquals(editPropertiesDialog.getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), fileName), "Displayed dialog=");

        LOG.info("STEP3: Click \"Select\" button");
        editPropertiesDialog.clickSelectTags();
        assertEquals(selectDialog.getDialogTitle(), language.translate("selectDialog.title"), "Displayed dialog=");

        LOG.info("STEP4: Type any tag name in the input field and click \"Create new item\" icon");
        selectDialog.typeTag(fileTag);
        selectDialog.clickCreateNewIcon();
        assertTrue(selectDialog.isItemSelected(fileTag.toLowerCase()),
            "'Select...' dialog -> added tags section: '" + fileTag.toLowerCase() + "' is displayed.");
        assertTrue(selectDialog.isItemRemovable(fileTag.toLowerCase()), "'Select...' dialog -> 'Remove' icon is displayed for: " + fileTag.toLowerCase());

        LOG.info("STEP5: Click \"Ok\" button");
        selectDialog.clickOk();
        assertTrue(editPropertiesDialog.isTagSelected(fileTag.toLowerCase()), "'Tags:' section=");

        LOG.info("STEP6: Click \"Save\" button");
        editPropertiesDialog.clickSave();
        ArrayList<String> tagsList = new ArrayList<>(Collections.singletonList(fileTag.toLowerCase()));
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertEquals(documentLibraryPage.getTags(fileName), tagsList.toString(), fileName + " -> tags=");
    }

    @TestRail (id = "C10210")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createTagEditTagIcon()
    {
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Hover over the text \"No Tags\" from the folder");
        documentLibraryPage.mouseOverNoTags(folderName);
        assertTrue(documentLibraryPage.isEditTagIconDisplayed(folderName), folderName + " -> \"Edit Tag\" icon is displayed");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        documentLibraryPage.clickEditTagIcon(folderName);
        assertTrue(documentLibraryPage.isEditTagInputFieldDisplayed(), folderName + " -> Edit tag text input field is displayed.");

        LOG.info("STEP3: Type any tag name in the input field and click \"Save\" link");
        documentLibraryPage.typeTagName(folderTag);
        documentLibraryPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        ArrayList<String> tagsList = new ArrayList<>(Collections.singletonList(folderTag.toLowerCase()));
        assertEquals(documentLibraryPage.getTags(folderName), tagsList.toString(), folderName + " -> tags=");
    }
}