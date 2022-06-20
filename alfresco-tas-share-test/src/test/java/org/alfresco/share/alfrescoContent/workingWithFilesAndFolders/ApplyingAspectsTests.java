package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.*;

import org.testng.annotations.*;

@Slf4j
public class ApplyingAspectsTests extends BaseTest
{
    private static final String audioAspect = "Audio";

    private DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentDetailsPage;
    private AspectsForm aspectsForm;

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
        aspectsForm = new AspectsForm(webDriver);

        fileToCheck = FileModel.getRandomFileModel(FileType.MSWORD2007);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C7109")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkAspectsForm()
    {
        log.info("Precondition: Navigate to Document Details page for the test file");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(fileToCheck.getName())
            .clickOnFile(fileToCheck.getName());

        log.info("Step1: Click Actions -> Manage Aspects and verify Manage Aspects form");

        documentDetailsPage
            .clickManageAspects()
            .assertIsAspectsFormTitleDisplayed()
            .assertIsAvailableToAddPanelDisplayed()
            .assertIsCurrentlySelectedPanel()
            .assertAreAddButtonsDisplayed()
            .assertAreRemoveButtonsDisplayed()
            .assertmIsApplyChangesButtonDisplayed()
            .assertmIsCancelButtonDisplayed()
            .assertmIsCloseButtonDisplayed();
    }

    @TestRail (id = "C7105")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void manageAspectsApplyChanges()
    {
        log.info("Preconditions: Navigate to Document Details page for the test file");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(fileToCheck.getName())
            .clickOnFile(fileToCheck.getName());

        log.info("Step1: Click Actions -> Manage Aspects option");
        documentDetailsPage
            .clickManageAspects();

        log.info("Step2: From 'Available to Add' list, click 'Add' icon next to an aspect and verify it's displayed in 'Currently Selected' list");
        aspectsForm
            .isAspectPresentOnAvailableAspectList(audioAspect);

        aspectsForm
            .addAspect(audioAspect);

        aspectsForm
            .assertAspactPresentInCurrentlySelectedList(audioAspect);

        log.info("Step3: Click 'Apply Changes' and verify the aspect is added");
        aspectsForm
            .clickApplyChangesButton();

        documentDetailsPage
            .assertIsAspectDisplayedOnDetailsPage(audioAspect);

        log.info("Step4: Now verify again aspect is available in the currently added list in Aspect form");
        documentDetailsPage
            .clickManageAspects();

        aspectsForm
            .assertAspactPresentInCurrentlySelectedList(audioAspect);
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.CONTENT })
    public void addAspectCancelCloseTest()
    {
        log.info("Preconditions: Navigate to Document Details page for the test file");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(fileToCheck.getName())
            .clickOnFile(fileToCheck.getName());

        log.info("Step1: Click Actions -> Manage Aspects option");
        documentDetailsPage
            .clickManageAspects();

        log.info("Step2: From 'Available to Add' list, click 'Add' icon next to an aspect and verify it's displayed in 'Currently Selected' list");
        aspectsForm
            .isAspectPresentOnAvailableAspectList(audioAspect);
        aspectsForm
            .addAspect(audioAspect);
        aspectsForm
            .assertAspactPresentInCurrentlySelectedList(audioAspect);

        log.info("Step3: Click on close button and verify Aspects Form should not be displayed");
        aspectsForm
            .clickCloseButton();

        aspectsForm
            .assertIsAspectsFormTitleNotDisplayed();

        log.info("Step4: Verify that the aspect should not be added on details page and click on Mange Aspect");

        documentDetailsPage
            .assertIsAspectNotDisplayedOnDetailsPage(audioAspect)
            .clickManageAspects();

        log.info("Step5: Add aspect and click on cancel button");
        aspectsForm
            .addAspect(audioAspect);
        aspectsForm
            .clickCancelButton();

        log.info("Step6: Verify that the aspect form should be closed and Aspect not added to the details page");
        aspectsForm
            .assertIsAspectsFormTitleNotDisplayed();

        documentDetailsPage
            .assertIsAspectNotDisplayedOnDetailsPage(audioAspect);
    }
}
