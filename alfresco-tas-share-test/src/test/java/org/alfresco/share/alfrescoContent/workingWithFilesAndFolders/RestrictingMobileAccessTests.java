package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.share.BaseTest;

import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;

import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.*;

@Slf4j
public class RestrictingMobileAccessTests extends BaseTest
{
    private DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentDetailsPage;
    private AspectsForm aspectsForm;
    private EditPropertiesPage editPropertiesPage;
    private FileModel fileToCheck;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private static final String helpMessage = "This field must contain a number.";
    private static final String restrictableAspect = "Restrictable";

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
        editPropertiesPage = new EditPropertiesPage(webDriver);

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C7111")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addRestrictableAspect()
    {
        log.info("Create file under document library in site");
        fileToCheck = FileModel.getRandomFileModel(FileType.MSWORD2007);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        log.info("Preconditions: Navigate to Document Details page for the test file");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName());

        log.info("Step1: Click Actions -> Manage Aspects option");
        documentDetailsPage
            .clickManageAspects();

        log.info("Step2: From 'Available to Add' list, click 'Add' icon next to 'Restrictable' aspect and verify it's displayed in 'Currently Selected' list");
        aspectsForm
            .addAspect(restrictableAspect);
        aspectsForm
            .assertAspactPresentInCurrentlySelectedList(restrictableAspect);

        log.info("Step3: Click 'Apply Changes' and verify the restrictions are placed on the file");
        aspectsForm
            .clickApplyChangesButton();

        documentDetailsPage
            .assertIsAspectDisplayedOnDetailsPage(restrictableAspect);
    }


    @TestRail (id = "C7112")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editRestrictableProperty()
    {
        log.info("Create file under document library in site");
        fileToCheck = FileModel.getRandomFileModel(FileType.MSWORD2007);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        log.info("Preconditions: Navigate to Document Details page for the test file add Restrictable Aspect");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName());

        documentDetailsPage
            .clickManageAspects();

        aspectsForm
            .addAspect(restrictableAspect);
        aspectsForm
            .assertAspactPresentInCurrentlySelectedList(restrictableAspect)
            .clickApplyChangesButton();

        documentDetailsPage
            .assertIsAspectDisplayedOnDetailsPage(restrictableAspect);

        log.info("Step1: Click Actions -> Edit Properties option");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName());

        documentDetailsPage
            .clickEditProperties();

        log.info("Step2: Click '?' icon and verify the help message");
        editPropertiesPage
            .clickHelpIconForRestrictableAspect();

        log.info("Step3: Verify the help message for the Restrictable Aspect");
        editPropertiesPage
            .assertHelpMessageForRestrictableAspectIsEquals(helpMessage);

        log.info("Step4: Fill in 'Offline Expires After (hours) and verify the change is saved");

        editPropertiesPage
            .addOfflineExpiresAfterValue("48");
        editPropertiesPage
            .clickButton("Save");

        log.info("Step5: Verify the value for Offline Expires After (hours) has been updated");
        documentDetailsPage
            .assertIsRestrictableValueIsEquals("48");
    }


    @TestRail (id = "C7113")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void removeRestrictableProperty()
    {
        log.info("Create file under document library in site");
        fileToCheck = FileModel.getRandomFileModel(FileType.MSWORD2007);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        log.info("Preconditions: Navigate to Document Details page for the test file add Restrictable Aspect");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName());

        documentDetailsPage
            .clickManageAspects();

        aspectsForm
            .addAspect(restrictableAspect);
        aspectsForm
            .assertAspactPresentInCurrentlySelectedList(restrictableAspect)
            .clickApplyChangesButton();

        documentDetailsPage
            .assertIsAspectDisplayedOnDetailsPage(restrictableAspect);

        log.info("Step1: Click Actions -> Manage Aspects option");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName());

        documentDetailsPage
            .clickManageAspects();

        log.info("Step2: Click 'Remove' icon next to 'Restrictable' aspect");
        aspectsForm
            .assertAspactPresentInCurrentlySelectedList(restrictableAspect);
        aspectsForm
            .removeAspect(restrictableAspect);
        aspectsForm
            .assertAspactPresentInAvailableList(restrictableAspect);

        log.info("Step3: Click 'Apply changes' button and verify the 'Restrictable' property is removed from 'Properties' section");
        aspectsForm
            .clickApplyChangesButton();

        log.info("Step:3 Verify that Aspect should not present on the document details page");
        documentDetailsPage
            .assertIsAspectNotDisplayedOnDetailsPage(restrictableAspect);
    }
}
