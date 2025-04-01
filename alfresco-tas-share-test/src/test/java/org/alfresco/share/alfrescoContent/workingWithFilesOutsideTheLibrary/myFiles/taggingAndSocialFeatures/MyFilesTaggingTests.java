package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles.taggingAndSocialFeatures;

import static org.testng.Assert.*;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

@Slf4j
public class MyFilesTaggingTests extends BaseTest
{
    private final String tagName = String.format("tag-%s", RandomData.getRandomAlphanumeric());
    private final String tagName2 = String.format("tag2-%s", RandomData.getRandomAlphanumeric());

    private DocumentLibraryPage documentLibraryPage;
    private MyFilesPage myFilesPage;
    private EditPropertiesDialog editPropertiesDialog;
    private SelectDialog selectDialog;
    private CopyMoveUnzipToDialog copyMoveUnzipToDialog;

    private FolderModel folderToCheck;
    private FileModel fileToCheck;
    private TagModel tagModel;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {

        log.info("Creating a random user and a random public site");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        getCmisApi().authenticateUser(user.get());

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        myFilesPage = new MyFilesPage(webDriver);
        editPropertiesDialog = new EditPropertiesDialog(webDriver);
        selectDialog = new SelectDialog(webDriver);
        copyMoveUnzipToDialog = new CopyMoveUnzipToDialog(webDriver);

        folderToCheck = FolderModel.getRandomFolderModel();
        fileToCheck = FileModel.getRandomFileModel(FileType.MSWORD2007);
        tagModel = new TagModel();

        authenticateUsingCookies(user.get());
        }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C7861")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesCreateFileTag()
    {
        log.info("Create File in document library and the move it to the myFiles.");
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(fileToCheck.getName())
            .selectItemAction(fileToCheck.getName(), ItemActions.MOVE_TO);

        copyMoveUnzipToDialog
            .selectMyFilesDestination()
            .clickMoveButton();

        log.info("Navigate to the myFilePage.");
        myFilesPage.navigate();
        log.info("STEP1: Hover over the text \"No Tags\" from the file.");
        myFilesPage
            .mouseOverNoTags(fileToCheck.getName());
        myFilesPage
            .assertIsEditTagIconDisplayed(fileToCheck.getName());

        log.info("STEP2: Click \"Edit Tag\" icon");
        myFilesPage
            .clickEditTagIcon(fileToCheck.getName());
        myFilesPage
            .assertIsEditTagInputFieldDisplayed();

        log.info("STEP3: Type any tag name in the input field and click \"Save\" link");
        myFilesPage
            .typeTagName(tagName);
        myFilesPage
            .clickEditTagLink(language.translate("documentLibrary.tag.link.save"))
            .assertCheckAddedTagsList(tagName, fileToCheck.getName());
    }


    @TestRail (id = "C7862")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesCreateFolderTag()
    {
        log.info("Create Folder in document library and the move it to the myFiles page.");
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(folderToCheck.getName())
            .selectItemAction(folderToCheck.getName(), ItemActions.MOVE_TO);

        copyMoveUnzipToDialog
            .selectMyFilesDestination()
            .clickMoveButton();

        log.info("Navigate to the myFiles Page.");
        myFilesPage.navigate();

        log.info("STEP1: Hover over the text \"No Tags\" from the folder.");
        myFilesPage
            .mouseOverNoTags(folderToCheck.getName());
        myFilesPage
            .assertIsEditTagIconDisplayed(folderToCheck.getName());

        log.info("STEP2: Click \"Edit Tag\" icon");
        myFilesPage
            .clickEditTagIcon(folderToCheck.getName());
        myFilesPage
            .isEditTagInputFieldDisplayed();

        log.info("STEP3: Type any tag name in the input field and click \"Save\" link");
        myFilesPage
            .typeTagName(tagName);
        myFilesPage
            .clickEditTagLink(language.translate("documentLibrary.tag.link.save"))
            .assertCheckAddedTagsList(tagName, folderToCheck.getName());
    }


    @TestRail (id = "C7873")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesAddExistingTag()
    {
        log.info("Create Folder/File in document library.");
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(folderToCheck.getName());

        documentLibraryPage
            .mouseOverNoTags(folderToCheck.getName());
        documentLibraryPage
            .assertIsEditTagIconDisplayed(folderToCheck.getName())
            .clickEditTagIcon(folderToCheck.getName());
        documentLibraryPage
            .assertIsEditTagInputFieldDisplayed();

        documentLibraryPage
            .typeTagName(tagName2);
        documentLibraryPage
            .clickEditTagLink(language.translate("documentLibrary.tag.link.save"))
            .assertCheckAddedTagsList(tagName2, folderToCheck.getName());

        log.info("Move file to the myFile Page.");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(fileToCheck.getName())
            .selectItemAction(fileToCheck.getName(), ItemActions.MOVE_TO);

        copyMoveUnzipToDialog
            .selectMyFilesDestination()
            .clickMoveButton();

        log.info("Navigate to the my file page.");
        myFilesPage.navigate();

        log.info("STEP1: Click \"Edit Properties\" option");
        myFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(fileToCheck.getName(), ItemActions.EDIT_PROPERTIES);

        assertEquals(editPropertiesDialog.getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), fileToCheck.getName()),
            "Displayed dialog=");

        editPropertiesDialog
            .assertIseditPropertiesDialogEquals(fileToCheck.getName())
            .assertIsSelectTagsButtonDisplayed();

        assertEquals(editPropertiesDialog.getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), fileToCheck.getName()),
            "Displayed dialog=");

        log.info("STEP2: Click \"Select\" button");
        editPropertiesDialog
            .clickSelectTags();

        assertEquals(selectDialog.getDialogTitle(), language.translate("selectDialog.title"), "Displayed dialog=");

        log.info("STEP3: Pick any tag from the available tags list and click \"Add\"");
        selectDialog
            .typeTag(tagName2.toLowerCase())
            .selectItems(Collections.singletonList(tagName2.toLowerCase()));

        selectDialog
            .assertIsItemSelected(tagName2.toLowerCase())
            .assertTagIsNotSelectable(tagName2.toLowerCase());

        log.info("STEP4: Click \"Ok\" button");
        selectDialog.clickOk();

        editPropertiesDialog
            .assertTagIsSelected(tagName2.toLowerCase());

        log.info("STEP5: Click \"Save\" button");
        editPropertiesDialog
            .clickSave();

        myFilesPage
            .assertCheckAddedTagsList(tagName2.toLowerCase(), fileToCheck.getName());
    }

    @TestRail (id = "C7885")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesEditTagFile()
    {
        log.info("Create File in document library and move it to the my file page.");
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(fileToCheck.getName())
            .selectItemAction(fileToCheck.getName(), ItemActions.MOVE_TO);

        copyMoveUnzipToDialog
            .selectMyFilesDestination()
            .clickMoveButton();

        log.info("Navigate to the my file page.");
        myFilesPage.navigate();

        myFilesPage
            .mouseOverNoTags(fileToCheck.getName());

        myFilesPage
            .assertIsEditTagIconDisplayed(fileToCheck.getName());

        myFilesPage
            .clickEditTagIcon(fileToCheck.getName());
        myFilesPage
            .isEditTagInputFieldDisplayed();

        myFilesPage
            .typeTagName(tagName);
        myFilesPage
            .clickEditTagLink(language.translate("documentLibrary.tag.link.save"))
            .assertCheckAddedTagsList(tagName.toLowerCase(), fileToCheck.getName());

        myFilesPage.navigate();
        log.info("STEP1: Hover over the tag(s) from the content");
        myFilesPage
            .mouseOverTags(fileToCheck.getName());
        myFilesPage
            .assertIsEditTagIconDisplayed(fileToCheck.getName());

        log.info("STEP2: Click \"Edit Tag\" icon");
        myFilesPage
            .clickEditTagIcon(fileToCheck.getName());
        myFilesPage
            .isEditTagInputFieldDisplayed();

        log.info("STEP3: Click on any tag and type a valid tag name");
        myFilesPage
            .editTag(tagName.toLowerCase(), tagName2);

        log.info("STEP4: Click \"Save\" link and verify the content tags");
        myFilesPage
            .clickEditTagLink(language.translate("documentLibrary.tag.link.save"))
            .assertCheckAddedTagsList(tagName2.toLowerCase(), fileToCheck.getName());
    }

    @TestRail (id = "C7886")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesRemoveTag()
    {
        log.info("Create File in document library and move it to myFiles Page.");
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(fileToCheck.getName())
            .selectItemAction(fileToCheck.getName(), ItemActions.MOVE_TO);

        copyMoveUnzipToDialog
            .selectMyFilesDestination()
            .clickMoveButton();

        log.info("Navigate to the MyFile Page.");
        myFilesPage.navigate();

        myFilesPage
            .mouseOverNoTags(fileToCheck.getName());
        myFilesPage
            .assertIsEditTagIconDisplayed(fileToCheck.getName());

        myFilesPage
            .clickEditTagIcon(fileToCheck.getName());
        myFilesPage
            .assertIsEditTagInputFieldDisplayed();

        myFilesPage
            .typeTagName(tagName);
        myFilesPage
            .clickEditTagLink(language.translate("documentLibrary.tag.link.save"))
            .assertCheckAddedTagsList(tagName.toLowerCase(), fileToCheck.getName());

        myFilesPage.navigate();
        log.info("STEP1: Hover over the tag from the folder");
        myFilesPage
            .mouseOverTags(fileToCheck.getName());
        myFilesPage
            .assertIsEditTagIconDisplayed(fileToCheck.getName());

        log.info("STEP2: Click \"Edit Tags\" icon");
        myFilesPage
            .clickEditTagIcon(fileToCheck.getName());
        myFilesPage
            .assertIsEditTagInputFieldDisplayed();

        log.info("STEP3: Hover over the tag and click 'Remove' icon");
        myFilesPage
            .removeTag(tagName.toLowerCase());

        log.info("STEP4: Click 'Save' link");
        myFilesPage
            .clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        if (!myFilesPage.isNoTagsTextDisplayed(fileToCheck.getName()))
            myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        myFilesPage
            .assertIsNoTagsTextDisplayed(fileToCheck.getName());
    }

    @TestRail (id = "C7895")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "FlakyTests" })
    public void myFilesUpdateTag()
    {
        log.info("Create File in document library and move it to MyFiles Page.");
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(fileToCheck.getName())
            .selectItemAction(fileToCheck.getName(), ItemActions.MOVE_TO);;

        copyMoveUnzipToDialog
            .selectMyFilesDestination()
            .clickMoveButton();

        myFilesPage.navigate();

        myFilesPage
            .mouseOverNoTags(fileToCheck.getName());
        myFilesPage
            .assertIsEditTagIconDisplayed(fileToCheck.getName());

        myFilesPage
            .clickEditTagIcon(fileToCheck.getName());
        myFilesPage
            .assertIsEditTagInputFieldDisplayed();

        myFilesPage
            .typeTagName(tagName);
        myFilesPage
            .clickAction(language.translate("documentLibrary.tag.link.save"))
            .assertCheckAddedTagsList(tagName.toLowerCase(), fileToCheck.getName());

        myFilesPage.navigate();
        log.info("STEP1: Hover over the tag from the folder");
        myFilesPage
            .mouseOverTags(fileToCheck.getName());
        myFilesPage
            .assertIsEditTagIconDisplayed(fileToCheck.getName());

        log.info("STEP2: Click \"Edit Tags\" icon");
        myFilesPage
            .clickEditTagIcon(fileToCheck.getName());
        myFilesPage
            .assertIsEditTagInputFieldDisplayed();

        log.info("STEP3: Hover over the tag and click 'Remove' icon");
        myFilesPage
            .removeTag(tagName.toLowerCase());

        log.info("STEP4: Click 'Save' link");
        myFilesPage
            .clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        if (!myFilesPage.isNoTagsTextDisplayed(fileToCheck.getName()))
            myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        myFilesPage
            .assertIsNoTagsTextDisplayed(fileToCheck.getName());

        log.info("STEP5: Click \"Edit Tag\" icon");
        myFilesPage
            .mouseOverNoTags(fileToCheck.getName());
        myFilesPage
            .clickEditTagIcon(fileToCheck.getName());

        log.info("STEP6: Type any tag name in the input field. Click \"Save\" link");
        myFilesPage
            .typeTagName(tagName2);
        myFilesPage
            .clickAction(language.translate("documentLibrary.tag.link.save"))
            .assertCheckAddedTagsList(tagName2.toLowerCase(), fileToCheck.getName());
    }
}
