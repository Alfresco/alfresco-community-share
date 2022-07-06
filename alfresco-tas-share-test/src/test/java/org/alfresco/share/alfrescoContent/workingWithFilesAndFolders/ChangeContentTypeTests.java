package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ChangeContentTypeDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Laura.Capsa
 */
@Slf4j
public class ChangeContentTypeTests extends BaseTest
{
    ArrayList<String> propertiesList;

    private DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentDetailsPage;
    private ChangeContentTypeDialog changeContentTypeDialog;
    private EditPropertiesPage editPropertiesPage;

    private FileModel fileToCheck;
    private FolderModel folderToCheck;

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
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        changeContentTypeDialog = new ChangeContentTypeDialog(webDriver);
        editPropertiesPage = new EditPropertiesPage(webDriver);

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C7163")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelChangeType()
    {
        log.info("Create a file in the site under document library.");
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertDocumentLibraryPageTitleEquals("Alfresco » Document Library")
            .clickOnFile(fileToCheck.getName());

        log.info("STEP1: Click 'Change Type' option from 'Document Actions' list");
        documentDetailsPage
            .clickDocumentActionsOption("Change Type");

        changeContentTypeDialog
            .assertVerifyChangeTypeDialogTitle("Change Type");

        log.info("STEP2: Select 'Article' from 'New Type' dropdown and click 'Cancel' button");
        changeContentTypeDialog
            .selectOption("Smart Folder Template")
            .clickCancelButton();

        log.info("STEP3: Verify that the properties not added into the document details page");
        documentDetailsPage
            .assertPropertiesAreNotDisplayed("Template Name:", "Primary Image:", "Secondary Image:", "Related Articles:");

        log.info("STEP4: Click 'Edit Properties' option from 'Document Actions' list");
        documentDetailsPage
            .clickEditProperties();

        log.info("STEP5: Verify that the properties for changes type not displayed");
        editPropertiesPage
            .assertPropertiesAreNotDisplayed("Auto Version - on update properties only", "Created Date", "Title", "Description", "Creator", "Name",
                "Content", "Locale", "Version Label", "Modifier", "Modified Date", "Auto Version", "Version Type", "Initial Version", "Last Accessed Date", "Encoding", "Size", "Mimetype");
    }


    @TestRail (id = "C7166")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, enabled = false)
    public void changeTypeFolder()
    {
        String folderName = String.format("Folder-C7166-%s", RandomData.getRandomAlphanumeric());
        // contentService.createFolder(userName, password, folderName, siteName);
        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        documentLibraryPage.navigate(site.get().getTitle());
        documentLibraryPage
            .selectItemActionFormFirstThreeAvailableOptions(folderToCheck.getName(), ItemActions.VIEW_DETAILS);
        //documentLibraryPage.selectItemAction(folderToCheck.getName(), ItemActions.VIEW_DETAILS);
        //        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "Displayed page:");

        log.info("STEP1: Verify folder's Properties list from 'Folder Actions' section");
        assertTrue(documentDetailsPage.arePropertiesDisplayed("Name", "Title", "Description", "Creator", "Created Date", "Modifier", "Modified Date"), "Displayed properties:");

        log.info("STEP2: Click 'Edit Properties' option from 'Document Actions' list");
        documentDetailsPage.clickEditProperties();
        //        assertEquals(editPropertiesPage.getPageTitle(), "Alfresco » Edit Properties", "Page displayed:");

        assertTrue(editPropertiesPage.arePropertiesDisplayed("Name", "Title", "Description", "Tags"), "Displayed properties:");

        log.info("STEP3: Cancel 'Edit Properties'.");
        editPropertiesPage.clickButton("Cancel");
        //        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "Displayed page:");

        log.info("STEP4: From 'Document Actions' list click 'Change Type' option");
        documentDetailsPage.clickDocumentActionsOption("Change Type");
        assertEquals(changeContentTypeDialog.getDialogTitle(), "Change Type", "Displayed dialog: ");
        assertTrue(changeContentTypeDialog.isDropdownMandatory(), "'New Type' dropdown is mandatory.");

        log.info("STEP5: Select 'ws:website' from 'New Type' dropdown and click 'Ok' button");
        changeContentTypeDialog.selectOption("ws:website");
        changeContentTypeDialog.clickOkButton();
        // getBrowser().refresh();

        log.info("STEP6: Click 'Edit Properties' option from 'Folder Actions' section");
        documentDetailsPage.clickEditProperties();
        //        assertEquals(editPropertiesPage.getPageTitle(), "Alfresco » Edit Properties", "Page displayed:");
        assertTrue(editPropertiesPage.arePropertiesDisplayed("Name", "Title", "Description", "Host Name", "Host Port ", "Web App Context ",
            "Site Configuration", "Site Languages", "Feedback Configuration", "Publish Target"), "Displayed properties:");
    }

    @TestRail (id = "C7167")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void changeTypeFile()
    {
        log.info("Create a file in the site under document library.");
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertDocumentLibraryPageTitleEquals("Alfresco » Document Library")
            .clickOnFile(fileToCheck.getName());

        log.info("STEP1: Verify document's Properties list");
        propertiesList = new ArrayList<>(Arrays.asList("Name", "Title", "Description", "Author", "Mimetype", "Size", "Creator", "Created Date", "Modifier", "Modified Date"));
        documentDetailsPage
            .assertPropertiesAreDisplayed_(propertiesList);

        log.info("STEP2: Click 'Edit Properties' option from 'Document Actions' list");
        documentDetailsPage
            .clickEditProperties();
        propertiesList = new ArrayList<>(Arrays.asList("Name", "Title", "Description", "Author", "Tags", "Mimetype"));
        editPropertiesPage
            .assertPropertiesAreDisplayed(propertiesList);

        log.info("STEP3: Cancel 'Edit Properties'.");
        editPropertiesPage
            .clickButton("Cancel");

        log.info("STEP4: From 'Document Actions' list click 'Change Type' option");
        documentDetailsPage
            .assertBrowserPageTitleIs("Alfresco » Document Details")
            .clickDocumentActionsOption("Change Type");

        changeContentTypeDialog
            .assertVerifyChangeTypeDialogTitle("Change Type");

        log.info("STEP5: Select 'Article' from 'New Type' dropdown and click 'Ok' button");
        changeContentTypeDialog
            .selectOption("Smart Folder Template")
            .clickOkButton();

        propertiesList = new ArrayList<>(Arrays.asList("Auto Version - on update properties only", "Created Date", "Title", "Description", "Creator", "Name",
            "Locale", "Version Label", "Modifier", "Modified Date", "Auto Version", "Version Type", "Initial Version", "Last Accessed Date", "Encoding", "Size", "Mimetype"));
        documentDetailsPage
            .assertPropertiesAreDisplayed_(propertiesList);

      log.info("STEP6: Click 'Edit Properties' option from 'Document Actions' section");
        documentDetailsPage
            .clickEditProperties();

        log.info("STEP7: Verify that the properties for changes type are displayed");
        propertiesList = new ArrayList<>(Arrays.asList("Auto Version - on update properties only", "Created Date", "Title", "Description", "Creator", "Name",
            "Content", "Locale", "Version Label", "Modifier", "Modified Date", "Auto Version", "Version Type", "Initial Version", "Last Accessed Date", "Encoding", "Size", "Mimetype"));
        editPropertiesPage
            .assertPropertiesAreDisplayed(propertiesList);
    }
}
