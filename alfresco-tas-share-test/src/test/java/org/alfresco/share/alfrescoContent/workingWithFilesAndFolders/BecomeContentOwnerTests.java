package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import static org.alfresco.utility.constants.UserRole.SiteContributor;
import static org.alfresco.utility.constants.UserRole.SiteManager;
import static org.alfresco.utility.constants.UserRole.SiteCollaborator;
import static org.testng.Assert.*;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.BecomeContentOwnerDialog_;
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
public class BecomeContentOwnerTests extends BaseTest
{
    String random = RandomData.getRandomAlphanumeric();
    String userSiteManager = "user-SiteManager-C7152-" + random;

    private DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentDetailsPage;
    private BecomeContentOwnerDialog_ becomeContentOwnerDialog;

    private FileModel fileToCheck;
    private FolderModel folderToCheck;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Creating a random user and a random public site");
        user.set(getDataUser().createUser(userSiteManager));
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        getCmisApi().authenticateUser(user.get());

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        becomeContentOwnerDialog = new BecomeContentOwnerDialog_(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C7152")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void becomeFileOwner()
    {
        UserModel contributer = dataUser.usingAdmin().createRandomTestUser();
        dataUser.addUserToSite(contributer, site.get(), SiteContributor);

        fileToCheck = FileModel.getRandomFileModel(FileType.MSWORD2007);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        UserModel manager = dataUser.usingAdmin().createRandomTestUser();
        dataUser.addUserToSite(manager, site.get(), SiteManager);

        authenticateUsingCookies(manager);

        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertDocumentLibraryPageTitleEquals("Alfresco » Document Library")
            .clickOnFile(fileToCheck.getName());

        log.info("STEP1: From 'Document Actions' section, click 'Become Owner' option");
        documentDetailsPage
            .assertBrowserPageTitleIs("Alfresco » Document Details")
            .clickDocumentActionsOption("Become Owner");

        becomeContentOwnerDialog
            .assertIsBecomeContentOwnerDialogDisplayed()
            .assertVerifyDialogHeader(language.translate("becomeContentOwner.header"))
            .assertVerifyDialogMessage(fileToCheck.getName());

        log.info("STEP2: Click 'OK' button");
        becomeContentOwnerDialog
            .clickButton("OK");

        documentDetailsPage
            .assertIsPropertyDisplayed("Owner:")
            .assertPropertyValueEquals("Owner", manager.getUsername());

        log.info("STEP3: Logout and login as userContributor. Navigate to site's Document Library page");

        authenticateUsingCookies(contributer);

        documentLibraryPage
            .navigate(site.get().getTitle());

        log.info("STEP4: Mouse over file's name link");
        documentLibraryPage
            .mouseOverContentItem(fileToCheck.getName());
        documentLibraryPage
            .assertisMoreMenuNotDisplayed(fileToCheck.getName());
    }


    @TestRail (id = "C7153")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void becomeFolderOwner()
    {
        UserModel contributer = dataUser.usingAdmin().createRandomTestUser();
        dataUser.addUserToSite(contributer, site.get(), SiteContributor);

        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        UserModel manager = dataUser.usingAdmin().createRandomTestUser();
        dataUser.addUserToSite(manager, site.get(), SiteManager);

        authenticateUsingCookies(manager);

        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertDocumentLibraryPageTitleEquals("Alfresco » Document Library");

        log.info("STEP1: Hover over 'Folder1' folder from 'Documents' list, then select 'View Details' option");
        documentLibraryPage
            .selectItemActionFormFirstThreeAvailableOptions(folderToCheck.getName(), ItemActions.VIEW_DETAILS);

        documentDetailsPage
            .assertBrowserPageTitleIs("Alfresco » Folder Details");

        log.info("STEP2: From 'Document Actions' section, click 'Become Owner' option");
        documentDetailsPage
            .clickDocumentActionsOption("Become Owner");

        becomeContentOwnerDialog
            .assertIsBecomeContentOwnerDialogDisplayed()
            .assertVerifyDialogHeader(language.translate("becomeContentOwner.header"))
            .assertVerifyDialogMessage(folderToCheck.getName());

        log.info("STEP3: Click 'OK' button");
        becomeContentOwnerDialog
            .clickButton("OK");

        documentDetailsPage
            .assertIsPropertyDisplayed("Owner:")
            .assertPropertyValueEquals("Owner", manager.getUsername());

        log.info("STEP4: Logout and login as userContributor. Navigate to site's Document Library page");
        authenticateUsingCookies(contributer);

        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertDocumentLibraryPageTitleEquals("Alfresco » Document Library");

        log.info("STEP5: Mouse over folder name");
        documentLibraryPage
            .mouseOverContentItem(folderToCheck.getName());
        documentLibraryPage
            .assertisMoreMenuNotDisplayed(folderToCheck.getName());
    }

    @TestRail (id = "C7154")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelBecomeOwner()
    {
        UserModel collaborator = dataUser.usingAdmin().createRandomTestUser();
        dataUser.addUserToSite(collaborator, site.get(), SiteCollaborator);

        fileToCheck = FileModel.getRandomFileModel(FileType.MSWORD2007);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        UserModel manager = dataUser.usingAdmin().createRandomTestUser();
        dataUser.addUserToSite(manager, site.get(), SiteManager);

        authenticateUsingCookies(manager);

        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertDocumentLibraryPageTitleEquals("Alfresco » Document Library")
            .clickOnFile(fileToCheck.getName());

        log.info("STEP1: From 'Document Actions' section, click 'Become Owner' option");
        documentDetailsPage
            .assertBrowserPageTitleIs("Alfresco » Document Details")
            .clickDocumentActionsOption("Become Owner");

        becomeContentOwnerDialog
            .assertIsBecomeContentOwnerDialogDisplayed()
            .assertVerifyDialogHeader(language.translate("becomeContentOwner.header"))
            .assertVerifyDialogMessage(fileToCheck.getName());

        log.info("STEP2: Click 'Cancel' button");
        becomeContentOwnerDialog
            .clickCancelButton();

        becomeContentOwnerDialog
            .assertIsBecomeContentOwnerDialogNotDisplayed();

        documentDetailsPage
            .assertIsPropertyNotDisplayed("Owner:");
        assertFalse(becomeContentOwnerDialog.isDialogDisplayed(), "'Become Owner' dialog is displayed.");
        assertFalse(documentDetailsPage.isPropertyDisplayed("Owner:"), "'Owner' property is displayed.");

        log.info("STEP3: Logout and login as userContributor. Navigate to site's Document Library page");
        authenticateUsingCookies(collaborator);
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertDocumentLibraryPageTitleEquals("Alfresco » Document Library");

        log.info("STEP4: Mouse over file's name link");
        documentLibraryPage
            .mouseOverContentItem(fileToCheck.getName());
        documentLibraryPage
            .assertisMoreMenuDisplayed(fileToCheck.getName());
    }
}