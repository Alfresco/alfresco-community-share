package org.alfresco.share.userDashboard;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowsIveStartedPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.profile.ChangePasswordPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class SettingHomePageTest extends ContextAwareWebTest
{
    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    MyFilesPage myFilesPage;

    @Autowired
    MyTasksPage myTasksPage;

    @Autowired
    WorkflowsIveStartedPage workflowsIveStartedPage;

    @Autowired
    SharedFilesPage sharedFilesPage;

    @Autowired
    SiteFinderPage siteFinderPage;

    @Autowired
    PeopleFinderPage peopleFinderPage;

    @Autowired
    RepositoryPage repositoryPage;

    @Autowired
    UserProfilePage userProfilePage;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    ChangePasswordPage changePasswordPage;

    @TestRail (id = "C2858")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD, "tobefixed" })
    public void useCurrentPage()
    {
        String folderName = "TestFolder";
        String userName1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String userName2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName1, password, userName1 + domain, userName1, userName1);
        userService.create(adminUser, adminPassword, userName2, password, userName2 + domain, userName2, userName2);
        siteService.create(userName1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        userService.createSiteMember(userName1, password, userName2, siteName, "SiteCollaborator");
        contentService.createFolder(userName2, password, folderName, siteName);
        setupAuthenticatedSession(userName1, password);

        LOG.info("STEP 1 - Create a folder in site’s document library");
        documentLibraryPage.navigate(siteName);
        assertTrue(documentLibraryPage.isDocumentListDisplayed(), "Document List is displayed");

        LOG.info("STEP 2 - Go to the folder details page, click on the user drop down in the header bar and select Use Current Page link");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, ItemActions.VIEW_DETAILS, documentDetailsPage);
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        documentDetailsPage.renderedPage();
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");

        LOG.info("STEP 3 - Click on the Home link in the header bar");
        toolbar.clickHome();
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");

        LOG.info("STEP 4 - Click on the user drop down in the header bar, log out and go to the alfresco log in page");
        cleanupAuthenticatedSession();

        LOG.info("STEP 5 - Log into Alfresco Share as a site member created in preconditions");
        setupAuthenticatedSession(userName1, password);
        getBrowser().refresh();
        documentDetailsPage.renderedPage();
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");

        LOG.info("STEP 6 - Repeat steps 1-5 for all Alfresco Share pages");
        LOG.info("STEP 6.1 - User Dashboard Page");
        userDashboardPage.navigate(userName1);
        assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        userDashboardPage.renderedPage();
        assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, password);
        getBrowser().refresh();
        userDashboardPage.renderedPage();
        assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");

        LOG.info("STEP 6.2 - My Files Page");
        myFilesPage.navigate();
        assertTrue(myFilesPage.isUploadButtonDisplayed(), "Upload button is displayed");
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        myFilesPage.renderedPage();
        assertTrue(myFilesPage.isUploadButtonDisplayed(), "Upload button is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, password);
        getBrowser().refresh();
        myFilesPage.renderedPage();
        assertTrue(myFilesPage.isUploadButtonDisplayed(), "Upload button is displayed");

        LOG.info("STEP 6.3 - Shared Files Page");
        sharedFilesPage.navigate();
        assertTrue(sharedFilesPage.isUploadButtonDisplayed(), "Upload button is displayed");
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        sharedFilesPage.renderedPage();
        assertTrue(sharedFilesPage.isUploadButtonDisplayed(), "Upload button is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, password);
        getBrowser().refresh();
        sharedFilesPage.renderedPage();
        assertTrue(sharedFilesPage.isUploadButtonDisplayed(), "Upload button is displayed");

        LOG.info("STEP 6.4 - Site Finder Page");
        siteFinderPage.navigate();
        assertTrue(siteFinderPage.isSearchFieldDisplayed(), "Search field is displayed");
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        siteFinderPage.renderedPage();
        assertTrue(siteFinderPage.isSearchFieldDisplayed(), "Search field is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, password);
        getBrowser().refresh();
        siteFinderPage.renderedPage();
        assertTrue(siteFinderPage.isSearchFieldDisplayed(), "Search field is displayed");

        LOG.info("STEP 6.5 - My Tasks Page");
        myTasksPage.navigateByMenuBar();
        myTasksPage.assertStartWorkflowIsDisplayed();
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        myTasksPage.renderedPage();
        myTasksPage.assertStartWorkflowIsDisplayed();
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, password);
        getBrowser().refresh();
        myTasksPage.renderedPage();
        myTasksPage.assertStartWorkflowIsDisplayed();

        LOG.info("STEP 6.6 - Workflows I've Started Page");
        workflowsIveStartedPage.navigateByMenuBar();
        workflowsIveStartedPage.assertStartWorkflowIsDisplayed();
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        workflowsIveStartedPage.renderedPage();
        workflowsIveStartedPage.assertStartWorkflowIsDisplayed();
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, password);
        getBrowser().refresh();
        workflowsIveStartedPage.renderedPage();
        workflowsIveStartedPage.assertStartWorkflowIsDisplayed();

        LOG.info("STEP 6.7 - People Finder Page");
        peopleFinderPage.navigate();
        assertTrue(peopleFinderPage.isSearchButtonDisplayed(), "\"Search\" button is displayed");
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        peopleFinderPage.renderedPage();
        assertTrue(peopleFinderPage.isSearchButtonDisplayed(), "\"Search\" button is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, password);
        getBrowser().refresh();
        peopleFinderPage.renderedPage();
        assertTrue(peopleFinderPage.isSearchButtonDisplayed(), "\"Search\" button is displayed");

        LOG.info("STEP 6.8 - Repository Page");
        repositoryPage.navigate();
        assertTrue(repositoryPage.isUploadButtonDisplayed(), "\"Upload\" button is displayed");
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        repositoryPage.renderedPage();
        assertTrue(repositoryPage.isUploadButtonDisplayed(), "\"Upload\" button is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, password);
        getBrowser().refresh();
        repositoryPage.renderedPage();
        assertTrue(repositoryPage.isUploadButtonDisplayed(), "\"Upload\" button is displayed");

        LOG.info("STEP 6.9 - User Profile Page");
        userProfilePage.navigateByMenuBar();
        assertTrue(userProfilePage.isAboutHeaderDisplayed(), "\"About\" header is displayed");
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        userProfilePage.renderedPage();
        assertTrue(userProfilePage.isAboutHeaderDisplayed(), "\"About\" header is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, password);
        getBrowser().refresh();
        userProfilePage.renderedPage();
        assertTrue(userProfilePage.isAboutHeaderDisplayed(), "\"About\" header is displayed");

        LOG.info("STEP 6.10 - Change Password Page");
        changePasswordPage.navigateByMenuBar();
        assertTrue(changePasswordPage.isOldPasswordInputDisplayed(), "Old password input is displayed");
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        changePasswordPage.renderedPage();
        assertTrue(changePasswordPage.isOldPasswordInputDisplayed(), "Old password input is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, password);
        getBrowser().refresh();
        changePasswordPage.renderedPage();
        assertTrue(changePasswordPage.isOldPasswordInputDisplayed(), "Old password input is displayed");

        LOG.info("STEP 6.11 - Site Dashboard Page");
        siteDashboardPage.navigate(siteName);
        assertTrue(siteDashboardPage.isAlfrescoLogoDisplayed(), "Alfresco logo is displayed");
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        siteDashboardPage.renderedPage();
        assertTrue(siteDashboardPage.isAlfrescoLogoDisplayed(), "Alfresco logo is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, password);
        getBrowser().refresh();
        siteDashboardPage.renderedPage();
        assertTrue(siteDashboardPage.isAlfrescoLogoDisplayed(), "Alfresco logo is displayed");

        userService.delete(adminUser, adminPassword, userName1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1);
        userService.delete(adminUser, adminPassword, userName2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName2);

        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2859")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD, "tobefixed" })
    public void useMyDashboard()
    {
        String folderName = "TestFolder";
        String userName = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteName);
        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Create a folder in site’s document library");
        documentLibraryPage.navigate(siteName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), folderName + " is displayed");

        LOG.info("STEP 2 - Go to the folder details page, click on the user drop down in the header bar and select Use Current Page link");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, ItemActions.VIEW_DETAILS, documentDetailsPage);
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        documentDetailsPage.renderedPage();
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");

        LOG.info("STEP 3 - Click on the Home link in the header bar");
        toolbar.clickUserMenu().clickHome();
        documentDetailsPage.renderedPage();
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");

        LOG.info("STEP 4 - Click on the user drop down in the header bar, log out and go to the alfresco log in page");
        toolbar.clickUserMenu().clickLogout();

        LOG.info("STEP 5 - Log into Alfresco Share");
        setupAuthenticatedSession(userName, password);
        getBrowser().refresh();
        documentDetailsPage.renderedPage();
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");

        LOG.info("STEP 6 - Click on the user drop down in the header bar and select Use My Dashboard link");
        toolbar.clickUserMenu().clickSetDashBoardAsHome();
        documentDetailsPage.renderedPage();
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");

        LOG.info("STEP 7 - Click on the Home link in the header bar");
        toolbar.clickUserMenu().clickHome();
        userDashboardPage.renderedPage();
        assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");

        LOG.info("STEP 8 - Click on the user drop down in the header bar, log out and log in again");
        toolbar.clickUserMenu().clickLogout();
        setupAuthenticatedSession(userName, password);
        getBrowser().refresh();
        assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

        siteService.delete(adminUser, adminPassword, siteName);
    }
}
