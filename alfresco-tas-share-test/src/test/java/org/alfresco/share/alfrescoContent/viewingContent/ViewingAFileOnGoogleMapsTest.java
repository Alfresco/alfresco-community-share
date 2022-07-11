package org.alfresco.share.alfrescoContent.viewingContent;

import static org.alfresco.po.share.site.ItemActions.EDIT_PROPERTIES;
import static org.alfresco.po.share.site.ItemActions.MANAGE_ASPECTS;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
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
public class ViewingAFileOnGoogleMapsTest extends BaseTest
{
    private final String description = String.format("C5920SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String geoAspactName = "Geographic (cm:geographic)";
    private final double longitude = 47.1585;
    private final double latitude = 27.6014;

    private DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentDetailsPage;
    private AspectsForm aspacts;
    private EditPropertiesDialog editPropertiesDialog;

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
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        aspacts = new AspectsForm(webDriver);
        editPropertiesDialog = new EditPropertiesDialog(webDriver);

        log.info("Create Folder in document library.");
        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        log.info("Create a file into the folder");
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, description);
        getCmisApi().usingSite(site.get()).usingResource(folderToCheck).createFile(fileToCheck).assertThat().existsInRepo();

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C5921")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void viewAFileOnGoogleMaps()
    {
        log.info("Step 1: Navigate to Document Library page and verify the folder present");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(folderToCheck.getName());

        log.info("Step 2: Click on the folder name and verify the file present in the folder and then select manage aspects option fot the file");
        documentLibraryPage
            .clickOnFolderName(folderToCheck.getName())
            .assertFileIsDisplayed(fileToCheck.getName())
            .selectItemAction(fileToCheck.getName(), MANAGE_ASPECTS);

        log.info("Step 3: Verify the 'Geographic' aspect present in the Available list and click on add button");
        aspacts
            .assertAspactPresentInAvailableList(geoAspactName)
            .addAspect(geoAspactName);

        log.info("Step 4: Verify the 'Geographic' aspect in the currently added aspect list and click on the 'Apply Changes' Button" );
        aspacts
            .assertAspactPresentInCurrentlySelectedList(geoAspactName)
            .clickApplyChangesButton();

        log.info("Step 5: Hover a file and verify it has the Geolocation Metadata available icon and verify the actions available for the document.");
        documentLibraryPage
            .assertIsGioLocationMetadataIconDisplayed()
            .assertAreActionsAvailableForLibraryItemsInPreviewPage(
            fileToCheck.getName());

        log.info("Step 6: Click on the 'Edit Properties' action for the file");
        documentLibraryPage
            .selectItemActionFormFirstThreeAvailableOptions(fileToCheck.getName(), EDIT_PROPERTIES);

        log.info("Step 7: Click on the 'All Properties' link in Edit Properties Doalog and set the 'Longitude' & 'Lattitude' and save");
        editPropertiesDialog
            .clickAllPropertiesLink();
        editPropertiesDialog
            .setLongitudeLattitude(longitude, latitude);

        log.info("Step 8: Click on View on Google Maps");
        documentLibraryPage
            .selectItemAction(fileToCheck.getName(), ItemActions.VIEW_ON_GOOGLE_MAPS);

        log.info("Step 9: Verify that the file open in Google Map and the Document Thumbnail is Displayed");
        documentLibraryPage
            .assertIsFileOpenInGoogleMap()
            .assertisDocumentThumbnailDisplayedOnGoogleMaps();

        log.info("Step 10: Click on file in Google Maps to open file preview and verify file name displayed on preview page");
        documentLibraryPage
            .clickOnFileInGoogleMaps()
            .assertIsFileNameDisplayedOnPreviewPage(
            fileToCheck.getName());
        }
}
