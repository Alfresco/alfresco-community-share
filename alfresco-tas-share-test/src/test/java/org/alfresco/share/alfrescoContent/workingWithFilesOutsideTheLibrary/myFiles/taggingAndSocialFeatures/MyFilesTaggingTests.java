package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles.taggingAndSocialFeatures;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.*;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesTaggingTests extends ContextAwareWebTest
{
    private final String testFileC7861 = RandomData.getRandomAlphanumeric() + "testFileC7861.txt";
    private final String testFileC7873 = RandomData.getRandomAlphanumeric() + "testFileC7873.txt";
    private final String testFileC7885 = RandomData.getRandomAlphanumeric() + "testFileC7885.txt";
    private final String testFileC7886 = RandomData.getRandomAlphanumeric() + "testFileC7886.txt";
    private final String testFileC7895 = RandomData.getRandomAlphanumeric() + "testFileC7895.txt";
    private final String tagName = String.format("tag-%s", RandomData.getRandomAlphanumeric());
    private final String tagName2 = String.format("tag2-%s", RandomData.getRandomAlphanumeric());
    private final String folderNameC7862 = String.format("testFolderC7862%s", RandomData.getRandomAlphanumeric());
    private final String folderNameC7873 = String.format("testFolderC7873%s", RandomData.getRandomAlphanumeric());
    private String user = String.format("user%s", RandomData.getRandomAlphanumeric());
    private final String myFilesPath = "User Homes/" + user;

    @Autowired
    private MyFilesPage myFilesPage;
    @Autowired
    private EditPropertiesDialog editPropertiesDialog;
    @Autowired
    private SelectDialog selectDialog;

    @BeforeClass (alwaysRun = true)
    public void precondition()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);

        contentService.createDocumentInRepository(user, password, myFilesPath, CMISUtil.DocumentType.TEXT_PLAIN, testFileC7873, "some content");
        contentAction.addSingleTag(user, password, myFilesPath + "/" + testFileC7873, tagName);
        contentService.createFolderInRepository(user, password, folderNameC7873, myFilesPath);
        contentAction.addSingleTag(user, password, myFilesPath + "/" + folderNameC7873, tagName2);
        contentService.createDocumentInRepository(user, password, myFilesPath, CMISUtil.DocumentType.TEXT_PLAIN, testFileC7885, "some content");
        contentAction.addSingleTag(user, password, myFilesPath + "/" + testFileC7885, tagName);
        contentService.createDocumentInRepository(user, password, myFilesPath, CMISUtil.DocumentType.TEXT_PLAIN, testFileC7886, "some content");
        contentAction.addSingleTag(user, password, myFilesPath + "/" + testFileC7886, tagName);
        contentService.createDocumentInRepository(user, password, myFilesPath, CMISUtil.DocumentType.TEXT_PLAIN, testFileC7895, "some content");
        contentAction.addSingleTag(user, password, myFilesPath + "/" + testFileC7895, tagName);
        contentService.createDocumentInRepository(user, password, myFilesPath, CMISUtil.DocumentType.TEXT_PLAIN, testFileC7861, "some content");
        contentService.createFolderInRepository(user, password, folderNameC7862, myFilesPath);

        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);

    }

    @TestRail (id = "C7861")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesCreateFileTag()
    {
        myFilesPage.navigate();
        LOG.info("STEP1: Hover over the text \"No Tags\" from the file.");
        myFilesPage.mouseOverNoTags(testFileC7861);
        assertTrue(myFilesPage.isEditTagIconDisplayed(testFileC7861), testFileC7861 + " -> \"Edit Tag\" icon is displayed");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        myFilesPage.clickEditTagIcon(testFileC7861);
        assertTrue(myFilesPage.isEditTagInputFieldDisplayed(), testFileC7861 + " -> Edit tag text input field is displayed.");

        LOG.info("STEP3: Type any tag name in the input field and click \"Save\" link");
        myFilesPage.typeTagName(tagName);
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        ArrayList<String> tagsList = new ArrayList<>(Collections.singletonList(tagName.toLowerCase()));
        assertEquals(myFilesPage.getTags(testFileC7861), tagsList.toString(), testFileC7861 + " -> tags=");
    }

    @TestRail (id = "C7862")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesCreateFolderTag()
    {
        myFilesPage.navigate();
        LOG.info("STEP1: Hover over the text \"No Tags\" from the folder.");
        myFilesPage.mouseOverNoTags(folderNameC7862);
        assertTrue(myFilesPage.isEditTagIconDisplayed(folderNameC7862), folderNameC7862 + " -> \"Edit Tag\" icon is displayed");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        myFilesPage.clickEditTagIcon(folderNameC7862);
        assertTrue(myFilesPage.isEditTagInputFieldDisplayed(), folderNameC7862 + " -> Edit tag text input field is displayed.");

        LOG.info("STEP3: Type any tag name in the input field and click \"Save\" link");
        myFilesPage.typeTagName(tagName);
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        myFilesPage.renderedPage();
        ArrayList<String> tagsList = new ArrayList<>(Collections.singletonList(tagName.toLowerCase()));
        assertEquals(myFilesPage.getTags(folderNameC7862), tagsList.toString(), folderNameC7862 + " -> tags=");
    }

    @TestRail (id = "C7873")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesAddExistingTag()
    {
        myFilesPage.navigate();

        LOG.info("STEP1: Click \"Edit Properties\" option");
        myFilesPage.clickDocumentLibraryItemAction(testFileC7873, "Edit Properties", editPropertiesDialog);
        assertEquals(editPropertiesDialog.getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), testFileC7873),
            "Displayed dialog=");
        assertTrue(editPropertiesDialog.isSelectTagsButtonDisplayed(), "'Select' tag button is displayed.");

        LOG.info("STEP2: Click \"Select\" button");
        editPropertiesDialog.clickSelectTags();
        assertEquals(selectDialog.getDialogTitle(), language.translate("selectDialog.title"), "Displayed dialog=");

        LOG.info("STEP3: Pick any tag from the available tags list and click \"Add\"");
        selectDialog.typeTag(tagName2.toLowerCase());
        selectDialog.selectItems(Collections.singletonList(tagName2.toLowerCase()));
        assertTrue(selectDialog.isItemSelected(tagName2.toLowerCase()), tagName2.toLowerCase() + " is displayed in selected categories list.");
        assertFalse(selectDialog.isItemSelectable(tagName2.toLowerCase()), tagName2.toLowerCase() + " -> 'Add' icon isn't displayed.");

        LOG.info("STEP4: Click \"Ok\" button");
        selectDialog.clickOk();
        assertTrue(editPropertiesDialog.isTagSelected(tagName.toLowerCase()), "'Tags:' section=");

        LOG.info("STEP5: Click \"Save\" button");
        editPropertiesDialog.clickSave();
        ArrayList<String> tagsList = new ArrayList<>(Arrays.asList(tagName.toLowerCase(), tagName2.toLowerCase()));
        assertEquals(myFilesPage.getTags(testFileC7873), tagsList.toString(), testFileC7873 + " -> tags=");
    }

    @TestRail (id = "C7885")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesEditTagFile()
    {
        myFilesPage.navigate();
        LOG.info("STEP1: Hover over the tag(s) from the content");
        myFilesPage.mouseOverTags(testFileC7885);
        assertTrue(myFilesPage.isEditTagIconDisplayed(testFileC7885), testFileC7885 + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        myFilesPage.clickEditTagIcon(testFileC7885);
        assertTrue(myFilesPage.isEditTagInputFieldDisplayed(), testFileC7885 + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Click on any tag and type a valid tag name");
        myFilesPage.editTag(tagName.toLowerCase(), tagName2);

        LOG.info("STEP4: Click \"Save\" link and verify the content tags");
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        assertEquals(myFilesPage.getTags(testFileC7885), Collections.singletonList(tagName2.toLowerCase()).toString(),
            tagName.toLowerCase() + " is updated with value:");
    }

    @TestRail (id = "C7886")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesRemoveTag()
    {
        myFilesPage.navigate();
        LOG.info("STEP1: Hover over the tag from the folder");
        myFilesPage.mouseOverTags(testFileC7886);
        assertTrue(myFilesPage.isEditTagIconDisplayed(testFileC7886), testFileC7886 + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tags\" icon");
        myFilesPage.clickEditTagIcon(testFileC7886);
        assertTrue(myFilesPage.isEditTagInputFieldDisplayed(), testFileC7886 + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Hover over the tag and click 'Remove' icon");
        myFilesPage.removeTag(tagName.toLowerCase());

        LOG.info("STEP4: Click 'Save' link");
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        if (!myFilesPage.isNoTagsTextDisplayed(testFileC7886))
            myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        assertTrue(myFilesPage.isNoTagsTextDisplayed(testFileC7886), testFileC7886 + " -> " + tagName + " is removed.");
    }

    @TestRail (id = "C7895")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesUpdateTag()
    {
        myFilesPage.navigate();
        LOG.info("STEP1: Hover over the tag from the file.");
        myFilesPage.mouseOverTags(testFileC7895);
        assertTrue(myFilesPage.isEditTagIconDisplayed(testFileC7895), testFileC7895 + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tags\" icon");
        myFilesPage.clickEditTagIcon(testFileC7895);
        assertTrue(myFilesPage.isEditTagInputFieldDisplayed(), testFileC7895 + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Hover over the tag. Click 'Remove' icon. Click 'Save' link");
        myFilesPage.removeTag(tagName.toLowerCase());
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        assertTrue(myFilesPage.isNoTagsTextDisplayed(testFileC7895), testFileC7895 + " -> " + tagName + " is removed.");

        LOG.info("STEP4: Click \"Edit Tag\" icon");
        myFilesPage.mouseOverNoTags(testFileC7895);
        myFilesPage.clickEditTagIcon(testFileC7895);

        LOG.info("STEP5: Type any tag name in the input field. Click \"Save\" link");
        myFilesPage.typeTagName(tagName2);
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        myFilesPage.renderedPage();
        assertEquals(myFilesPage.getTags(testFileC7895), Collections.singletonList(tagName2.toLowerCase()).toString(), testFileC7895 + " -> tags=");
    }
}
