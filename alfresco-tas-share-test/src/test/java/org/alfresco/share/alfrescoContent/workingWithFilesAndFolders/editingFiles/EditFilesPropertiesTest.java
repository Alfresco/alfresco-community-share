package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders.editingFiles;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class EditFilesPropertiesTest extends BaseTest
{
    private String uniqueIdentifier;
    private final String contantEditName = "ItemEditName";
    private final String contantEditTitle = "ItemEditTitle";
    private final String contantEditDescription = "ItemEditDescription";
    private final String editTag = "edittag";

    private DocumentLibraryPage documentLibraryPage;
    private EditPropertiesDialog editFilePropertiesDialog;
    private SelectDialog selectDialog;
    private FolderModel folderToCheck;
    private FileModel fileToCheck;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();


    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        uniqueIdentifier = "-C7005-" + RandomData.getRandomAlphanumeric();
        uniqueIdentifier = uniqueIdentifier.toLowerCase();

        log.info("Creating a random user and a random public site");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        getCmisApi().authenticateUser(user.get());

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        editFilePropertiesDialog = new EditPropertiesDialog(webDriver);
        selectDialog = new SelectDialog(webDriver);

        folderToCheck = FolderModel.getRandomFolderModel();
        fileToCheck = FileModel.getRandomFileModel(FileType.MSWORD2007);

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C7005")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editFileProperties()
    {
        log.info("Create File in document library.");
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        log.info("Navigate to the site document library and verify file present in the document library");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(fileToCheck.getName());

        log.info("Step 1: Hover over a file and click 'Edit Properties'");
        documentLibraryPage
            .selectItemActionFormFirstThreeAvailableOptions(fileToCheck.getName(), ItemActions.EDIT_PROPERTIES);

        log.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog
            .setName(contantEditName);

        log.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog
            .setTitle(contantEditTitle);

        log.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog
            .setDescription(contantEditDescription);

        log.info("Step 5: Click the 'Select' button in the tags section");
        editFilePropertiesDialog
            .clickSelectTags();

        log.info("Step 6: Type a tag name and click create and verify tag is created and then click Ok Button");
        selectDialog
            .typeTag(editTag + uniqueIdentifier)
            .clickCreateNewIcon()
            .assertTagIsSelected(editTag + uniqueIdentifier)
            .clickOk();

        log.info("Step 7: Click 'Save' And verify that document details have been updated");
        editFilePropertiesDialog
            .clickSave();

        documentLibraryPage
            .assertIsContantNameDisplayed(contantEditName)
            .assertItemTitleEquals(contantEditName, contantEditTitle)
            .assertItemDescriptionEquals(contantEditName, contantEditDescription)
            .assertItemTagEquals(contantEditName, editTag + uniqueIdentifier);
    }

    @TestRail (id = "C7013")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editFolderProperties()
    {
        log.info("Creating folder into the document library...");
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        log.info("Navigate to the site document library and verify folder present in the document library");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(folderToCheck.getName());

        log.info("Step 1: Hover over a folder and click 'Edit Properties'");
        documentLibraryPage
            .selectItemActionFormFirstThreeAvailableOptions(folderToCheck.getName(), ItemActions.EDIT_PROPERTIES);

        log.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog
            .setName(contantEditName);

        log.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog
            .setTitle(contantEditTitle);

        log.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog
            .setDescription(contantEditDescription);

        log.info("Step 5: Click the 'Select' button in the tags section");
        editFilePropertiesDialog
            .clickSelectTags();

        log.info("Step 6: Type a tag name and click create and verify tag is created and then click Ok Button");
        selectDialog
            .typeTag(editTag + uniqueIdentifier)
            .clickCreateNewIcon()
            .assertTagIsSelected(editTag + uniqueIdentifier)
            .clickOk();

        log.info("Step 7: Click 'Save' And verify that folder details have been updated");
        editFilePropertiesDialog
            .clickSave();

        documentLibraryPage
            .assertIsContantNameDisplayed(contantEditName)
            .assertItemTitleEquals(contantEditName, contantEditTitle)
            .assertItemDescriptionEquals(contantEditName, contantEditDescription)
            .assertItemTagEquals(contantEditName, editTag + uniqueIdentifier);
    }
}
