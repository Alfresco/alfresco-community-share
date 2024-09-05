package org.alfresco.share.userRolesAndPermissions.contributor;

import static org.alfresco.common.Utils.isFileInDirectory;
import static org.alfresco.common.Utils.testDataFolder;
import static org.alfresco.utility.web.AbstractWebTest.getBrowser;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.ContentActions;

import org.alfresco.dataprep.UserService;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.Assert;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class ContributorFilesCreatedByOthersTests extends BaseTest
{
    private final String adminFile = String.format("AdminFile%s", RandomData.getRandomAlphanumeric());
    private final String lockedFileByAdmin = String.format("LockedFileByAdmin%s", RandomData.getRandomAlphanumeric());
    private final String adminWordFile = String.format("WordFile%s", RandomData.getRandomAlphanumeric());
    private final String fileContent = "FileContent";
    private final ThreadLocal<UserModel> userContributor = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private final String deletePath = String.format("Sites/%s/documentLibrary", siteName);
    UploadContent uploadContent;
    DocumentLibraryPage documentLibraryPage;
    DocumentDetailsPage documentDetailsPage;
    @Autowired
    GoogleDocsCommon docs;
    @Autowired
    UserService userService;
    @Autowired
    ContentActions contentAction;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        userContributor.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(getAdminUser()).createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), userContributor.get().getUsername(), siteName.get().getId(), "SiteContributor");

        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, adminFile, fileContent);
        contentService.createDocument(userContributor.get().getUsername(), userContributor.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, lockedFileByAdmin, fileContent);
        contentAction.checkOut(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), lockedFileByAdmin);
        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DocumentType.MSWORD, adminWordFile, fileContent);

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);

        authenticateUsingLoginPage(userContributor.get());
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userContributor.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userContributor.get());

    }

    @TestRail (id = "C8912")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "DownloadTest" })
    public void downloadContent()
    {
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get());
        log.info("Step1, 2: Mouse over the test file  from Document Library and click 'Download'.");
        documentLibraryPage.selectItemActionFormFirstThreeAvailableOptions(adminFile, ItemActions.DOWNLOAD);
        documentLibraryPage.acceptAlertIfDisplayed();
        Assert.assertTrue(isFileInDirectory(adminFile, null), "The file was not found in the specified location");
    }

    @TestRail (id = "C8913")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "DownloadTest" })
    public void viewInBrowser()
    {
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get());
        log.info("Step1: Mouse over testFile and check 'View in Browser' is available.");
        Assert.assertTrue(documentLibraryPage.checkActionAvailableForLibraryItem(adminFile, ItemActions.VIEW_IN_BROWSER), "View in browser available");
        log.info("Step2: Click View in browser and verify the file is opened in a new browser window.");
        documentLibraryPage.selectItemActionFormFirstThreeAvailableOptions(adminFile, ItemActions.VIEW_IN_BROWSER);
        Assert.assertTrue(isFileInDirectory(adminFile, null), "The file was not found in the specified location");

    }

    @TestRail (id = "C8915")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void uploadNewVersionForItemCreatedByOthers()
    {
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get());
        log.info("Step1: Mouse over test File and check 'Upload new version' action is not available.");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(adminFile, ItemActions.UPLOAD_NEW_VERSION),
            "Upload New Version available for Contributor user");
    }

    @TestRail (id = "C8916")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void uploadNewVersionForItemLockedByUser()
    {
        log.info("Steps1:  Navigate to test site's doc lib and verify the lockedFileByAdmin is locked by admin");
        documentLibraryPage.navigate(siteName.get());
        Assert.assertEquals(documentLibraryPage.getLockedByUserName(lockedFileByAdmin), "Administrator",
                "Document appears to be locked by admin");
        log.info("Steps2: Verify 'Upload new Version' option is not available for Contributor user, since the file is locked by admin");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(lockedFileByAdmin, ItemActions.UPLOAD_NEW_VERSION),
            "Upload New Version available for Contributor user");
    }

    @TestRail (id = "C8918")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "office" })
    public void editOnlineForContentCreatedByOthers()
    {
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get());
        log.info("Steps1: Mouse over file and check 'Edit in Microsoft Office' action is available.");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(adminWordFile, ItemActions.EDIT_IN_MICROSOFT_OFFICE),
            "Edit in Microsoft Office available for Contributor user");
    }

    @TestRail (id = "C8920")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void editInlineForContentCreatedByOthers()
    {
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get());
        log.info("Steps1: Mouse over file and check 'Edit in Alfresco' action is available.");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(adminFile, ItemActions.EDIT_IN_ALFRESCO),
            "Edit in Alfresco available for Contributor user");
    }

    @TestRail (id = "C8922")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void editOfflineForContentCreatedByOthers()
    {
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get());
        log.info("Steps1: Mouse over file and check 'Edit Offline' action is available.");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(adminFile, ItemActions.EDIT_OFFLINE), "Edit Offline available for Contributor user");
    }

    @TestRail (id = "C8926")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS }, enabled = false)
    public void editInGoogleDocForContentCreatedByOthers()
    {
        docs.loginToGoogleDocs();
        String googleDocName = RandomData.getRandomAlphanumeric() + "googleDoc.docx";
        String googleDocPath = testDataFolder + googleDocName;
        log.info(
            "Preconditions: Create test site, add contributor member to site. As admin, navigate to Document Library page for the test site and create Google Doc file");
        docs.loginToGoogleDocs();
        authenticateUsingLoginPage(getAdminUser());
        documentLibraryPage.navigate(siteName.get());
        uploadContent.uploadContent(googleDocPath);
        log.info("Steps1: Login as Contributor user, go to site's doc lib and check whether 'Edit in Google Docs' action is available.");
        authenticateUsingLoginPage(userContributor.get());
        documentLibraryPage.navigate(siteName.get());
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(googleDocName, ItemActions.EDIT_IN_GOOGLE_DOCS),
            "Edit in Google Docs available for Contributor user");
        contentService.deleteContentByPath(getAdminUser().getUsername(),getAdminUser().getPassword(), String.format("%s/%s", deletePath, googleDocName));

    }

    @TestRail (id = "C8928")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS }, enabled = false)
    public void checkInGoogleDocForContentCreatedByOthers() throws Exception
    {
        docs.loginToGoogleDocs();
        String googleDocName = RandomData.getRandomAlphanumeric() + "googleDoc.docx";
        String googleDocPath = testDataFolder + googleDocName;
        log.info(
            "Preconditions: Create test site and add contributor member to site. As Admin user, navigate to Document Library page for the test site and create Google Doc file. Check out the file in google Docs.");
        authenticateUsingLoginPage(getAdminUser());
        documentLibraryPage.navigate(siteName.get());
        uploadContent.uploadContent(googleDocPath);
        documentLibraryPage.navigate(siteName.get());
        documentLibraryPage.selectItemAction(googleDocName, ItemActions.EDIT_IN_GOOGLE_DOCS);
        getBrowser().waitInSeconds(5);
        docs.clickOkButtonOnTheAuthPopup();
        docs.switchToGoogleDocsWindowandAndEditContent("GDTitle", "Google Doc test content");
        log.info("Steps1: Login as Contributor user, go to site's doc lib and check whether 'Edit in Google Docs' action is available.");
        authenticateUsingLoginPage(userContributor.get());
        documentLibraryPage.navigate(siteName.get());
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(googleDocName, ItemActions.CHECK_IN_GOOGLE_DOC),
            "Check In Google Doc available for Contributor user");
    }

    @TestRail (id = "C8930")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void cancelEditingContentLockedByOthers()
    {
        log.info("Step2: Login as Contributor user and check whether the file appears as locked by Admin");
        documentLibraryPage.navigate(siteName.get());
        Assert.assertEquals(documentLibraryPage.getLockedByUserName(lockedFileByAdmin), "Administrator", "The document is not locked");
        log.info("Step3: Hover over test file and check whether 'Cancel Editing' action is missing");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(lockedFileByAdmin, ItemActions.CANCEL_EDITING),
                "Cancel Editing available for Contributor user");
    }

    @TestRail (id = "C8931, C8932")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void viewOriginalDocumentAndWorkingCopy()
    {
        log.info("Steps2: Logout and login as Contributor user; hover over testFile and check whether 'View Original Document' action is available");
        documentLibraryPage.navigate(siteName.get());
        Assert.assertTrue(documentLibraryPage.checkActionAvailableForLibraryItem(lockedFileByAdmin, ItemActions.VIEW_ORIGINAL),
            "View Original Document' action available for Contributor user");
        log.info("Steps3: Click 'View Original Document' action.");
        documentLibraryPage.selectItemActionFormFirstThreeAvailableOptions(lockedFileByAdmin, ItemActions.VIEW_ORIGINAL);
        Assert.assertEquals(documentDetailsPage.getLockedMessage(), "This document is locked by Administrator.", "Document appears to be locked by admin user");
        Assert.assertTrue(documentDetailsPage.isActionAvailable("View Working Copy"));
        log.info("Steps3: Click 'View Working Copy' action");
        documentDetailsPage.clickDocumentActionsOption("View Working Copy");
        Assert.assertTrue(documentDetailsPage.isActionAvailable("View Original Document"));
    }
}
