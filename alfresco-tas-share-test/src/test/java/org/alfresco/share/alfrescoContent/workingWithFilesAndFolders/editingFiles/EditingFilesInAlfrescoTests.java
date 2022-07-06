package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders.editingFiles;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.*;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class EditingFilesInAlfrescoTests extends BaseTest
{
    private final String contentEditName = "ItemEditName";
    private final String contentEditTitle = "ItemEditTitle";
    private final String contentEditDescription = "ItemEditDescription";
    private final String description = "contentAddedInDescription";

    private DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentDetailsPage;
    private EditPropertiesDialog editFilePropertiesDialog;
    private FolderModel folderToCheck;
    private FileModel fileToCheck;

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
        documentDetailsPage=new DocumentDetailsPage(webDriver);
        editFilePropertiesDialog = new EditPropertiesDialog(webDriver);

        folderToCheck = FolderModel.getRandomFolderModel();
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN,description);

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C7036")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyEditFileProperties()
    {
        log.info("Create File in document library.");
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        log.info("Navigate to the site document library and verify file present in the document library");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(fileToCheck.getName());

        log.info("Step 1: Hover over a file and click 'EDIT_IN_ALFRESCO'");
        documentLibraryPage
            .selectItemAction(fileToCheck.getName(), ItemActions.EDIT_IN_ALFRESCO);

        log.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog
            .setName(contentEditName);

        log.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog
            .setTitle(contentEditTitle);

        editFilePropertiesDialog
            .setContent(contentEditDescription);

        log.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog
            .setDescription(contentEditDescription);

        log.info("Step3: Click Save button");
        editFilePropertiesDialog.clickSave();

        log.info("Step4: Verify the edited name and edited tittle");
        documentLibraryPage
            .assertIsContantNameDisplayed(contentEditName)
            .assertItemTitleEquals(contentEditName, contentEditTitle);

        log.info("Step5: Click on document title to open the document's details page");
        documentLibraryPage.clickOnFile(contentEditName);

        log.info("Step6: Verify the document's content");
        documentDetailsPage
            .assertContentDescriptionEquals(contentEditDescription);
    }

}
