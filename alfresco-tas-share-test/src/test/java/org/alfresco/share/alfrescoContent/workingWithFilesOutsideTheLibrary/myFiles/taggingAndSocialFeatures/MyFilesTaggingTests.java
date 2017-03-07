package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles.taggingAndSocialFeatures;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesTaggingTests extends ContextAwareWebTest
{
    @Autowired
    MyFilesPage myFilesPage;

    @Autowired
    SiteDashboardPage sitePage;
    
    @Autowired
    EditPropertiesDialog editPropertiesDialog;

    @Autowired
    NewContentDialog newContentDialog;

    @Autowired
    SelectDialog selectDialog;

    @Autowired
    private UploadContent uploadContent;

    private String testFile =  DataUtil.getUniqueIdentifier() + "testFile.txt";
    private String testFilePath = testDataFolder + testFile;
    private String tagName = "tag-" + DataUtil.getUniqueIdentifier();
    private String tagName2 = "tag2-" + DataUtil.getUniqueIdentifier();
    private String folderName = "testFolder" + DataUtil.getUniqueIdentifier();

    @TestRail(id = "C7861")
    @Test
    public void myFilesCreateFileTag()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and upload a file.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(testFilePath);
        assertTrue(myFilesPage.isContentNameDisplayed(testFile),String.format("The file [%s] is not present", testFile));

        LOG.info("STEP1: Hover over the text \"No Tags\" from the file.");
        myFilesPage.mouseOverNoTags(testFile);
        browser.waitInSeconds(3);
        assertTrue(myFilesPage.isEditTagIconDisplayed(testFile), testFile + " -> \"Edit Tag\" icon is displayed");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        myFilesPage.clickEditTagIcon(testFile);
        assertTrue(myFilesPage.isEditTagInputFieldDisplayed(), testFile + " -> Edit tag text input field is displayed.");

        LOG.info("STEP3: Type any tag name in the input field and click \"Save\" link");
        myFilesPage.typeTagName(tagName);
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        browser.waitInSeconds(3);
        ArrayList<String> tagsList = new ArrayList<>(Arrays.asList(tagName.toLowerCase()));
        assertEquals(myFilesPage.getTags(testFile), tagsList.toString(), testFile + " -> tags=");
    }

    @TestRail(id = "C7862")
    @Test
    public void myFilesCreateFolderTag()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        myFilesPage.clickCreateButton();
        myFilesPage.clickFolderLink();
        newContentDialog.fillInNameField(folderName);
        newContentDialog.clickSaveButton();
        browser.waitInSeconds(3);
        assertTrue(myFilesPage.isContentNameDisplayed(folderName), folderName + " displayed in My Files documents list.");

        LOG.info("STEP1: Hover over the text \"No Tags\" from the folder.");
        myFilesPage.mouseOverNoTags(folderName);
        browser.waitInSeconds(3);
        assertTrue(myFilesPage.isEditTagIconDisplayed(folderName), folderName + " -> \"Edit Tag\" icon is displayed");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        myFilesPage.clickEditTagIcon(folderName);
        assertTrue(myFilesPage.isEditTagInputFieldDisplayed(), folderName + " -> Edit tag text input field is displayed.");

        LOG.info("STEP3: Type any tag name in the input field and click \"Save\" link");
        myFilesPage.typeTagName(tagName);
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        browser.waitInSeconds(3);
        ArrayList<String> tagsList = new ArrayList<>(Arrays.asList(tagName.toLowerCase()));
        assertEquals(myFilesPage.getTags(folderName), tagsList.toString(), folderName + " -> tags=");
    }

    @TestRail(id = "C7873")
    @Test
    public void myFilesAddExistingTag()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and create a file and folder.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(testFilePath);
        assertTrue(myFilesPage.isContentNameDisplayed(testFile),String.format("The file [%s] is not present", testFile));
        myFilesPage.clickCreateButton();
        myFilesPage.clickFolderLink();
        newContentDialog.fillInNameField(folderName);
        newContentDialog.clickSaveButton();
        browser.waitInSeconds(3);
        assertTrue(myFilesPage.isContentNameDisplayed(folderName), folderName + " displayed in My Files documents list.");

        LOG.info("Precondition: Add a tag to the file and folder.");
        myFilesPage.mouseOverNoTags(testFile);
        browser.waitInSeconds(3);
        myFilesPage.clickEditTagIcon(testFile);
        browser.waitInSeconds(3);
        myFilesPage.typeTagName(tagName);
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        browser.waitInSeconds(3);

        myFilesPage.mouseOverNoTags(folderName);
        browser.waitInSeconds(1);
        myFilesPage.clickEditTagIcon(folderName);
        browser.waitInSeconds(2);
        myFilesPage.typeTagName(tagName2);
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        browser.waitInSeconds(3);

        LOG.info("STEP1: Hover over the content created in the preconditions");
        myFilesPage.mouseOverFileName(testFile);
        browser.waitInSeconds(3);
        assertTrue(myFilesPage.isMoreMenuDisplayed(testFile), "'More' menu is displayed.");

        LOG.info("STEP2: Click \"Edit Properties\" option");
        myFilesPage.clickDocumentLibraryItemAction(testFile, "Edit Properties", myFilesPage);
        browser.waitInSeconds(3);
        assertEquals(editPropertiesDialog.getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), testFile), "Displayed dialog=");
        assertTrue(editPropertiesDialog.isSelectTagsButtonDisplayed(), "'Select' tag button is displayed.");

        LOG.info("STEP3: Click \"Select\" button");
        editPropertiesDialog.clickSelectTags();
        assertEquals(selectDialog.getDialogTitle(), language.translate("selectDialog.title"), "Displayed dialog=");

        LOG.info("STEP4: Pick any tag from the available tags list and click \"Add\"");
        selectDialog.typeTag(tagName2.toLowerCase());
        browser.waitInSeconds(4);
        selectDialog.selectItems(Arrays.asList(tagName2.toLowerCase()));
        assertTrue(selectDialog.isItemSelected(tagName2.toLowerCase()), tagName2.toLowerCase() + " is displayed in selected categories list.");
        assertFalse(selectDialog.isItemSelectable(tagName2.toLowerCase()), tagName2.toLowerCase() + " -> 'Add' icon isn't displayed.");

        LOG.info("STEP5: Click \"Ok\" button");
        selectDialog.clickOk();
        browser.waitInSeconds(4);
        assertTrue(editPropertiesDialog.isTagSelected(tagName.toLowerCase()), "'Tags:' section=");

        LOG.info("STEP6: Click \"Save\" button");
        editPropertiesDialog.clickSave();
        browser.waitInSeconds(3);
        ArrayList<String> tagsList = new ArrayList<>(Arrays.asList(tagName.toLowerCase(), tagName2.toLowerCase()));
        assertEquals(myFilesPage.getTags(testFile), tagsList.toString(), testFile + " -> tags=");
    }

    @TestRail(id = "C7885")
    @Test()
    public void myFilesEditTagFile()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and create a file.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(testFilePath);
        assertTrue(myFilesPage.isContentNameDisplayed(testFile),String.format("The file [%s] is not present", testFile));

        LOG.info("Precondition: Add a tag to the file.");
        myFilesPage.mouseOverNoTags(testFile);
        browser.waitInSeconds(3);
        myFilesPage.clickEditTagIcon(testFile);
        browser.waitInSeconds(4);
        myFilesPage.typeTagName(tagName);
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        browser.waitInSeconds(3);

        LOG.info("STEP1: Hover over the tag(s) from the content");
        myFilesPage.mouseOverTags(testFile);
        browser.waitInSeconds(3);
        assertTrue(myFilesPage.isEditTagIconDisplayed(testFile), testFile + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        myFilesPage.clickEditTagIcon(testFile);
        assertTrue(myFilesPage.isEditTagInputFieldDisplayed(), testFile + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Click on any tag and type a valid tag name");
        myFilesPage.editTag(testFile, tagName.toLowerCase(), tagName2);

        LOG.info("STEP4: Click \"Save\" link and verify the content tags");
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        assertEquals(myFilesPage.getTags(testFile), Arrays.asList(tagName2.toLowerCase()).toString(),
                tagName.toLowerCase() + " is updated with value:");
    }

    @TestRail(id = "C7886")
    @Test()
    public void myFilesRemoveTag()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and create a file.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(testFilePath);
        assertTrue(myFilesPage.isContentNameDisplayed(testFile),String.format("The file [%s] is not present", testFile));

        LOG.info("Precondition: Add a tag to the file.");
        myFilesPage.mouseOverNoTags(testFile);
        browser.waitInSeconds(3);
        myFilesPage.clickEditTagIcon(testFile);
        browser.waitInSeconds(3);
        myFilesPage.typeTagName(tagName);
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        browser.waitInSeconds(3);

        LOG.info("STEP1: Hover over the tag from the folder");
        myFilesPage.mouseOverTags(testFile);
        browser.waitInSeconds(3);
        assertTrue(myFilesPage.isEditTagIconDisplayed(testFile), testFile + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tags\" icon");
        myFilesPage.clickEditTagIcon(testFile);
        assertTrue(myFilesPage.isEditTagInputFieldDisplayed(), testFile + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Hover over the tag and click 'Remove' icon");
        assertEquals(myFilesPage.removeTag(tagName.toLowerCase()), tagName.toLowerCase(), "Removed ");
        browser.waitInSeconds(3);

        LOG.info("STEP4: Click 'Save' link");
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        if (!myFilesPage.isNoTagsTextDisplayed(testFile))
            myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        assertTrue(myFilesPage.isNoTagsTextDisplayed(testFile), testFile + " -> " + tagName + " is removed.");
    }

    @TestRail(id = "C7895")
    @Test()
    public void myFilesUpdateTag()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and create a file.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(testFilePath);
        assertTrue(myFilesPage.isContentNameDisplayed(testFile),String.format("The file [%s] is not present", testFile));

        LOG.info("Precondition: Add a tag to the file.");
        myFilesPage.mouseOverNoTags(testFile);
        browser.waitInSeconds(3);
        myFilesPage.clickEditTagIcon(testFile);
        browser.waitInSeconds(3);
        myFilesPage.typeTagName(tagName);
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        browser.waitInSeconds(3);

        LOG.info("STEP1: Hover over the tag from the file.");
        myFilesPage.mouseOverTags(testFile);
        browser.waitInSeconds(3);
        assertTrue(myFilesPage.isEditTagIconDisplayed(testFile), testFile + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tags\" icon");
        myFilesPage.clickEditTagIcon(testFile);
        browser.waitInSeconds(4);
        assertTrue(myFilesPage.isEditTagInputFieldDisplayed(), testFile + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Hover over the tag. Click 'Remove' icon. Click 'Save' link");
        assertEquals(myFilesPage.removeTag(tagName.toLowerCase()), tagName.toLowerCase(), "Removed ");
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        browser.waitInSeconds(4);
        assertTrue(myFilesPage.isNoTagsTextDisplayed(testFile), testFile + " -> " + tagName + " is removed.");

        LOG.info("STEP4: Click \"Edit Tag\" icon");
        myFilesPage.mouseOverNoTags(testFile);
        browser.waitInSeconds(3);
        myFilesPage.clickEditTagIcon(testFile);
        browser.waitInSeconds(4);

        LOG.info("STEP5: Type any tag name in the input field. Click \"Save\" link");
        myFilesPage.typeTagName(tagName2);
        myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        browser.waitInSeconds(4);
        assertEquals(myFilesPage.getTags(testFile), Arrays.asList(tagName2.toLowerCase()).toString(), testFile + " -> tags=");

    }
}
