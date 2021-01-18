package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.additionalActions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.ItemActions;
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
 * @author Laura.Capsa
 */
public class TagTests extends ContextAwareWebTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String docName = "Doc-C8062-" + random;
    private final String docName2 = "Doc-C8074-" + random;
    private final String docName3 = "Doc-C8087-" + random;
    private final String docName4 = "Doc-C8096-" + random;
    private final String docName5 = "Doc-C13766" + random;
    private final String folderForTagTests = "folderForTagTests" + random;
    private final String folderName = "Folder-C8063-" + random;
    private final String folderName2 = "Folder-C8074-" + random;
    private final String folderName3 = "Folder-C13766-" + random;
    private final String tagNameFile = "tagFile-C8062-" + random;
    private final String tagName = "tag1-" + random;
    private final String tagName2 = "tag2-" + random;
    private final String tagNameFolder = "tagNameFolder-C8063-" + random;
    private final String editedTag = "editedTag-C8086-" + random;
    private final String sharedPath = "Shared/";
    private final String path = "Shared/" + folderForTagTests;
    private final String user = "User" + random;
    private final String path13766 = "Shared/" + folderName3;
    //@Autowired
    private SharedFilesPage sharedFilesPage;
    @Autowired
    private EditPropertiesDialog editPropertiesDialog;
   //@Autowired
    private SelectDialog selectDialog;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        contentService.createFolderInRepository(adminUser, adminPassword, folderForTagTests, sharedPath);
        contentService.createFolderInRepository(adminUser, adminPassword, folderName3, sharedPath);
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, path);
        contentService.createFolderInRepository(adminUser, adminPassword, folderName2, path);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, CMISUtil.DocumentType.TEXT_PLAIN, docName, "");
        contentService.createDocumentInRepository(adminUser, adminPassword, path, CMISUtil.DocumentType.TEXT_PLAIN, docName2, "");
        contentService.createDocumentInRepository(adminUser, adminPassword, path, CMISUtil.DocumentType.TEXT_PLAIN, docName3, "");
        contentService.createDocumentInRepository(adminUser, adminPassword, path, CMISUtil.DocumentType.TEXT_PLAIN, docName4, "");
        contentService.createDocumentInRepository(adminUser, adminPassword, path13766, CMISUtil.DocumentType.TEXT_PLAIN, docName5, "");
        contentAction.addSingleTag(adminUser, adminPassword, path + "/" + folderName2, tagName2);
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
    }

    @BeforeMethod (alwaysRun = true)
    public void beforeMethod()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        sharedFilesPage.navigate();
//        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        sharedFilesPage.clickOnFolderName(folderForTagTests);
    }

    @TestRail (id = "C8062")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void createTagForFile()
    {
        LOG.info("STEP1: Hover over one tag from the content name");
        sharedFilesPage.mouseOverNoTags(docName);

        LOG.info("STEP2: Click \"Tag\" icon");
        sharedFilesPage.clickEditTagIcon(docName);
        assertTrue(sharedFilesPage.isEditTagInputFieldDisplayed(), docName + " -> Edit tag text input field is displayed.");

        LOG.info("STEP3: Type any tag name in the input field and click \"Save\" link");
        sharedFilesPage.typeTagName(tagNameFile);
        sharedFilesPage.clickEditTagLink("Save");
        ArrayList<String> tagsList = new ArrayList<>(Collections.singletonList(tagNameFile.toLowerCase()));
        assertEquals(sharedFilesPage.getTags(docName), tagsList.toString(), docName + " -> tags=");
    }

    @TestRail (id = "C8063")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createTagForFolder()
    {
        LOG.info("STEP1: Hover over the text \"No Tags\" from the folder");
        sharedFilesPage.mouseOverNoTags(folderName);
        assertTrue(sharedFilesPage.isEditTagIconDisplayed(folderName), folderName + " -> \"Edit Tag\" icon is displayed");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        sharedFilesPage.clickEditTagIcon(folderName);
        assertTrue(sharedFilesPage.isEditTagInputFieldDisplayed(), folderName + " -> Edit tag text input field is displayed.");

        LOG.info("STEP3: Type any tag name in the input field and click \"Save\" link");
        sharedFilesPage.typeTagName(tagNameFolder);
        sharedFilesPage.clickEditTagLink("Save");
        ArrayList<String> tagsList = new ArrayList<>(Collections.singletonList(tagNameFolder.toLowerCase()));
        assertEquals(sharedFilesPage.getTags(folderName), tagsList.toString(), folderName + " -> tags=");
    }

    @TestRail (id = "C8074")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addExistingTag()
    {
        LOG.info("STEP1: Hover over " + docName2 + ".STEP2: Click \"Edit Properties\" option");
        sharedFilesPage.clickDocumentLibraryItemAction(docName2, ItemActions.EDIT_PROPERTIES);
        assertEquals(editPropertiesDialog.getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), docName2), "Displayed dialog=");
        assertTrue(editPropertiesDialog.isSelectTagsButtonDisplayed(), "'Select' tag button is displayed.");

        LOG.info("STEP3: Click \"Select\" button");
        editPropertiesDialog.clickSelectTags();
        assertEquals(selectDialog.getDialogTitle(), language.translate("selectDialog.title"), "Displayed dialog=");

        LOG.info("STEP4: Pick any tag from the available tags list and click \"Add\"");
        selectDialog.typeTag(tagName2.toLowerCase());
        selectDialog.selectItems(Collections.singletonList(tagName2.toLowerCase()));
        assertTrue(selectDialog.isItemSelected(tagName2.toLowerCase()), tagName2.toLowerCase() + " is displayed in selected categories list.");

        LOG.info("STEP5: Click  \"OK\" button from \"Select\" dialog.\n" + "Click  \"Save\" button from \"Edit Properties\" dialog");
        selectDialog.clickOk();
        editPropertiesDialog.clickSave();
        ArrayList<String> tagsList = new ArrayList<>(Collections.singletonList(tagName2.toLowerCase()));
//        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertEquals(sharedFilesPage.getTags(docName2), tagsList.toString(), docName2 + " -> tags=");
    }

    @TestRail (id = "C8086")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editTagForASharedFile()
    {
        LOG.info("STEP1: Hover over the tag(s) from the content");
        sharedFilesPage.mouseOverTags(folderName2);
        assertTrue(sharedFilesPage.isEditTagIconDisplayed(folderName2), folderName2 + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        sharedFilesPage.clickEditTagIcon(folderName2);
        assertTrue(sharedFilesPage.isEditTagInputFieldDisplayed(), folderName2 + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Click on any tag and type a valid tag name");
        sharedFilesPage.editTag(tagName2.toLowerCase(), editedTag);

        LOG.info("STEP4: Click \"Save\" link and verify the content tags");
        sharedFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        getBrowser().waitInSeconds(5);
        assertTrue(sharedFilesPage.getTags(folderName2).contains(editedTag.toLowerCase()));
        assertFalse(sharedFilesPage.getTags(folderName2).contains(tagName.toLowerCase()));
    }

    @TestRail (id = "C8087")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void removeTag()
    {
        LOG.info("  Hover over the text \"No Tags\" from " + docName3);
        sharedFilesPage.mouseOverNoTags(docName3);
        assertTrue(sharedFilesPage.isEditTagIconDisplayed(docName3), docName3 + " -> \"Edit Tag\" icon is displayed");

        LOG.info("  Click \"Edit Tag\" icon");
        sharedFilesPage.clickEditTagIcon(docName3);
        assertTrue(sharedFilesPage.isEditTagInputFieldDisplayed(), docName3 + " -> Edit tag text input field is displayed.");

        LOG.info("  Type any tag name in the input field and click \"Save\" link");
        sharedFilesPage.typeTagName(tagName2);
        sharedFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        getBrowser().waitInSeconds(3);
        ArrayList<String> tagsList = new ArrayList<>(Collections.singletonList(tagName2.toLowerCase()));
        assertEquals(sharedFilesPage.getTags(docName3), tagsList.toString(), docName3 + " -> tags=");

        LOG.info("STEP1: Hover over the tag from " + docName3);
        sharedFilesPage.mouseOverTags(docName3);
        assertTrue(sharedFilesPage.isEditTagIconDisplayed(docName3), docName3 + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tags\" icon");
        sharedFilesPage.clickEditTagIcon(docName3);
        getBrowser().waitInSeconds(3);
        assertTrue(sharedFilesPage.isEditTagInputFieldDisplayed(), docName3 + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Hover over the tag and click 'Remove' icon");
        sharedFilesPage.removeTag(tagName2.toLowerCase());

        LOG.info("STEP4: Click 'Save' link");
        sharedFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        assertTrue(sharedFilesPage.isNoTagsTextDisplayed(docName3), docName3 + " -> " + tagName2 + " is removed.");
    }

    @TestRail (id = "C8096")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void updateTags()
    {
        LOG.info("STEP1: Hover over the text \"No Tags\" from " + docName4);
        sharedFilesPage.mouseOverNoTags(docName4);
        assertTrue(sharedFilesPage.isEditTagIconDisplayed(docName4), docName4 + " -> \"Edit Tag\" icon is displayed");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        sharedFilesPage.clickEditTagIcon(docName4);
        assertTrue(sharedFilesPage.isEditTagInputFieldDisplayed(), docName4 + " -> Edit tag text input field is displayed.");

        LOG.info("STEP3: Add a tag");
        sharedFilesPage.typeTagName(tagName2);

        LOG.info("STEP3: Click 'Remove' icon");
        sharedFilesPage.removeTag(tagName2.toLowerCase());

        LOG.info("STEP4: Click 'Save' link");
        sharedFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        assertTrue(sharedFilesPage.isNoTagsTextDisplayed(docName4), docName4 + " -> " + tagName2 + " is removed.");
    }

    @TestRail (id = "C13766")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void noTagsOptionDisplayed()
    {
        LOG.info("Preconditions: Test user with no admin permissions.");
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();

        LOG.info("STEP1: Hover over 'No Tags' for file, e.g: 'testFile'");
        sharedFilesPage.clickOnFolderName(folderName3);
        assertFalse(sharedFilesPage.isEditTagIconDisplayed(docName5), "Edit tag icon is displayed");
    }

    @AfterClass
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);

        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + docName);
        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + docName2);
        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + docName3);
        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + docName4);
        contentService.deleteContentByPath(adminUser, adminPassword, path13766 + "/" + docName5);
        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + folderName);
        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + folderName2);
        contentService.deleteContentByPath(adminUser, adminPassword, sharedPath + "/" + folderName3);
        contentService.deleteContentByPath(adminUser, adminPassword, path);
    }
}