package org.alfresco.share.alfrescoContent.viewingContent;

import static org.alfresco.common.Utils.srcRoot;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.*;

import java.io.File;

/**
 * @author iulia.cojocea
 */
@Slf4j
public class PreviewFileTest extends BaseTest
{
    private static final String docFileName = "MultiPageDocument.docx";
    private static final String imageName = "Tulips.jpg";
    private static final String videoName = "TestVideo.mp4";

    private final String testDataFolder = srcRoot + "testdata" + File.separator + "testDataC5884" + File.separator;

    private DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentDetailsPage;
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

        log.info("Create Folder in document library.");
        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C5884")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void previewFile()
    {
        log.info("Precondition : Uploading docFile, imageFile and videoFile in the folder");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(folderToCheck.getName())
            .clickOnFolderName(folderToCheck.getName())
            .clickUpload()
            .uploadFile(testDataFolder+docFileName);

        documentLibraryPage
            .clickUpload()
            .uploadFile(testDataFolder+imageName);

        documentLibraryPage
            .clickUpload()
            .uploadFile(testDataFolder+videoName);

        log.info("Precondition : Verify uploaded docFile, imageFile and videoFile in the folder is displayed and then click on docFile");
        documentLibraryPage
            .assertFileIsDisplayed(docFileName)
            .assertFileIsDisplayed(imageName)
            .assertFileIsDisplayed(videoName)
            .clickOnFile(docFileName);

        log.info("Step 1: On document details page verify the file name 'docFileName' and click on maximize button");
        documentDetailsPage
            .assertIsFileNameDisplayedOnPreviewPage(docFileName)
            .clickOnMaximizeMinimizeButton();

        log.info("STEP 2: Verify 'Minimize' button text");
        documentDetailsPage
            .assertVerifyMinimizeButtonTextOnPreviewPage("Minimize");

        log.info("STEP 3: Scroll between testDoc pages and get the actual page number.");
        String actualPageNo = documentDetailsPage.getCurrentPageNo();

        log.info("Step 4: Clink on Next Button to move next page....and get the new page number.");
        documentDetailsPage
            .clickOnNextButton();

        String newPageNo = documentDetailsPage.getCurrentPageNo();

        log.info("Step 5: Verify that the Page numbers should be different and then click on Previous button");
        documentDetailsPage
            .assertPageNoIsDifferent(actualPageNo, newPageNo)
            .clickOnPreviousButton();

        log.info("Step 6: Now get the current page no and verify that page number should be same");
        newPageNo = documentDetailsPage.getCurrentPageNo();
        documentDetailsPage
            .assertPageNoIsSame(newPageNo, actualPageNo);

        log.info("STEP 7: Click 'Zoom In' button, check that the Zoom level is saved for the next time you preview this item");
        String initialScaleValue = documentDetailsPage.getScaleValue();
        documentDetailsPage
            .clickOnZoomInButton();
        String newScaleValue = documentDetailsPage.getScaleValue();
        documentDetailsPage
            .assertScalValueIsDifferent(documentDetailsPage.getScaleValue(), initialScaleValue);

         documentLibraryPage
            .navigate(site.get().getTitle())
            .clickOnFolderName(folderToCheck.getName())
            .clickOnFile(docFileName);

         documentDetailsPage
            .assertScalValueIsSame(documentDetailsPage.getScaleValue(), newScaleValue);

         documentDetailsPage
            .clickOnZoomOutButton();
        documentDetailsPage
            .assertScalValueIsDifferent(documentDetailsPage.getScaleValue(), newScaleValue);

        log.info("STEP 8: Click 'Advanced Search' icon");
        documentDetailsPage
            .clickOnSearchButton();

        log.info("STEP 9: Go back to the folder content and then click on imageFile");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .clickOnFolderName(folderToCheck.getName())
            .clickOnFile(imageName);

        log.info("Step 10: Verify that the ZoomIn, ZoomOut, NextPage & PreviousPage button should not displayed for image");
        documentDetailsPage
            .assertIsZoomOutButtonNotDisplayed()
            .assertIsZoomInButtonNotDisplayed()
            .assertIsNextPageButtonNotDisplayed()
            .assertIsPreviousPageButtonNotDisplayed();

        log.info("STEP 11: Go back to the folder content and click on videoFile");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .clickOnFolderName(folderToCheck.getName())
            .clickOnFile(videoName);

        log.info("Step 12: Verify that the ZoomIn, ZoomOut, NextPage & PreviousPage button should not displayed for video");
        documentDetailsPage
            .assertIsZoomOutButtonNotDisplayed()
            .assertIsZoomInButtonNotDisplayed()
            .assertIsNextPageButtonNotDisplayed()
            .assertIsPreviousPageButtonNotDisplayed();
    }
}
