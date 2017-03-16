package org.alfresco.share.userDashboard;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.*;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowsIveStartedPage;
import org.alfresco.po.share.toolbar.ToolbarUserMenu;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.profile.ChangePasswordPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class SettingHomePageTest extends ContextAwareWebTest
{
    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    ToolbarUserMenu toolbarUserMenu;

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

    @TestRail(id = "C2858")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void useCurrentPage()
    {
        String folderName = "TestFolder";
        String userName1 = "User1" + DataUtil.getUniqueIdentifier();
        String userName2 = "User2" + DataUtil.getUniqueIdentifier();
        String siteName = "Site1" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName1, DataUtil.PASSWORD, userName1 + domain, userName1, userName1);
        userService.create(adminUser, adminPassword, userName2, DataUtil.PASSWORD, userName2 + domain, userName2, userName2);
        siteService.create(userName1, DataUtil.PASSWORD, domain, siteName, "description", Site.Visibility.PUBLIC);
        userService.createSiteMember(userName1, DataUtil.PASSWORD, userName2, siteName, "SiteCollaborator");
        contentService.createFolder(userName2, DataUtil.PASSWORD, folderName, siteName);
        setupAuthenticatedSession(userName1, DataUtil.PASSWORD);

        LOG.info("STEP 1 - Create a folder in site’s document library");
        documentLibraryPage.navigate(siteName);
        assertTrue(documentLibraryPage.isDocumentListDisplayed(), "Document List is displayed");

        LOG.info("STEP 2 - Go to the folder details page, click on the user drop down in the header bar and select Use Current Page link");
        documentLibraryPage.mouseOverContentItem(folderName);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "View Details", documentDetailsPage);
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");
        toolbarUserMenu.clickSetCurrentPageAsHome();
        documentDetailsPage.renderedPage();
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");

        LOG.info("STEP 3 - Click on the Home link in the header bar");
        toolbarUserMenu.clickHome();
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");

        LOG.info("STEP 4 - Click on the user drop down in the header bar, log out and go to the alfresco log in page");
        cleanupAuthenticatedSession();

        LOG.info("STEP 5 - Log into Alfresco Share as a site member created in preconditions");
        setupAuthenticatedSession(userName1, DataUtil.PASSWORD);
        getBrowser().refresh();
        documentDetailsPage.renderedPage();
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");

        LOG.info("STEP 6 - Repeat steps 1-5 for all Alfresco Share pages");
        LOG.info("STEP 6.1 - User Dashboard Page");
        userDashboardPage.navigate(userName1);
        assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");
        toolbarUserMenu.clickSetCurrentPageAsHome();
        userDashboardPage.renderedPage();
        assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, DataUtil.PASSWORD);
        getBrowser().refresh();
        userDashboardPage.renderedPage();
        assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");

        LOG.info("STEP 6.2 - My Files Page");
        myFilesPage.navigate();
        assertTrue(myFilesPage.isUploadButtonDisplayed(), "Upload button is displayed");
        toolbarUserMenu.clickSetCurrentPageAsHome();
        myFilesPage.renderedPage();
        assertTrue(myFilesPage.isUploadButtonDisplayed(), "Upload button is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, DataUtil.PASSWORD);
        getBrowser().refresh();
        myFilesPage.renderedPage();
        assertTrue(myFilesPage.isUploadButtonDisplayed(), "Upload button is displayed");

        LOG.info("STEP 6.3 - Shared Files Page");
        sharedFilesPage.navigate();
        assertTrue(sharedFilesPage.isUploadButtonDisplayed(), "Upload button is displayed");
        toolbarUserMenu.clickSetCurrentPageAsHome();
        sharedFilesPage.renderedPage();
        assertTrue(sharedFilesPage.isUploadButtonDisplayed(), "Upload button is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, DataUtil.PASSWORD);
        getBrowser().refresh();
        sharedFilesPage.renderedPage();
        assertTrue(sharedFilesPage.isUploadButtonDisplayed(), "Upload button is displayed");

        LOG.info("STEP 6.4 - Site Finder Page");
        siteFinderPage.navigate();
        assertTrue(siteFinderPage.isSearchFieldDisplayed(), "Search field is displayed");
        toolbarUserMenu.clickSetCurrentPageAsHome();
        siteFinderPage.renderedPage();
        assertTrue(siteFinderPage.isSearchFieldDisplayed(), "Search field is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, DataUtil.PASSWORD);
        getBrowser().refresh();
        siteFinderPage.renderedPage();
        assertTrue(siteFinderPage.isSearchFieldDisplayed(), "Search field is displayed");

        LOG.info("STEP 6.5 - My Tasks Page");
        myTasksPage.navigateByMenuBar();
        assertTrue(myTasksPage.isStartWorkflowDisplayed(), "\"Start Workflow\" button is displayed");
        toolbarUserMenu.clickSetCurrentPageAsHome();
        myTasksPage.renderedPage();
        assertTrue(myTasksPage.isStartWorkflowDisplayed(), "\"Start Workflow\" button is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, DataUtil.PASSWORD);
        getBrowser().refresh();
        myTasksPage.renderedPage();
        assertTrue(myTasksPage.isStartWorkflowDisplayed(), "\"Start Workflow\" button is displayed");

        LOG.info("STEP 6.6 - Workflows I've Started Page");
        workflowsIveStartedPage.navigateByMenuBar();
        assertTrue(workflowsIveStartedPage.isStartWorkflowDisplayed(), "\"Start Workflow\" button is displayed");
        toolbarUserMenu.clickSetCurrentPageAsHome();
        workflowsIveStartedPage.renderedPage();
        assertTrue(workflowsIveStartedPage.isStartWorkflowDisplayed(), "\"Start Workflow\" button is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, DataUtil.PASSWORD);
        getBrowser().refresh();
        workflowsIveStartedPage.renderedPage();
        assertTrue(workflowsIveStartedPage.isStartWorkflowDisplayed(), "\"Start Workflow\" button is displayed");

        LOG.info("STEP 6.7 - People Finder Page");
        peopleFinderPage.navigate();
        assertTrue(peopleFinderPage.isSearchButtonDisplayed(), "\"Search\" button is displayed");
        toolbarUserMenu.clickSetCurrentPageAsHome();
        peopleFinderPage.renderedPage();
        assertTrue(peopleFinderPage.isSearchButtonDisplayed(), "\"Search\" button is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, DataUtil.PASSWORD);
        getBrowser().refresh();
        peopleFinderPage.renderedPage();
        assertTrue(peopleFinderPage.isSearchButtonDisplayed(), "\"Search\" button is displayed");

        LOG.info("STEP 6.8 - Repository Page");
        repositoryPage.navigate();
        assertTrue(repositoryPage.isUploadButtonDisplayed(), "\"Upload\" button is displayed");
        toolbarUserMenu.clickSetCurrentPageAsHome();
        repositoryPage.renderedPage();
        assertTrue(repositoryPage.isUploadButtonDisplayed(), "\"Upload\" button is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, DataUtil.PASSWORD);
        getBrowser().refresh();
        repositoryPage.renderedPage();
        assertTrue(repositoryPage.isUploadButtonDisplayed(), "\"Upload\" button is displayed");

        LOG.info("STEP 6.9 - User Profile Page");
        userProfilePage.navigateByMenuBar();
        assertTrue(userProfilePage.isAboutHeaderDisplayed(), "\"About\" header is displayed");
        toolbarUserMenu.clickSetCurrentPageAsHome();
        userProfilePage.renderedPage();
        assertTrue(userProfilePage.isAboutHeaderDisplayed(), "\"About\" header is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, DataUtil.PASSWORD);
        getBrowser().refresh();
        userProfilePage.renderedPage();
        assertTrue(userProfilePage.isAboutHeaderDisplayed(), "\"About\" header is displayed");

        LOG.info("STEP 6.10 - Change Password Page");
        changePasswordPage.navigateByMenuBar();
        assertTrue(changePasswordPage.isOldPasswordInputDisplayed(), "Old password input is displayed");
        toolbarUserMenu.clickSetCurrentPageAsHome();
        changePasswordPage.renderedPage();
        assertTrue(changePasswordPage.isOldPasswordInputDisplayed(), "Old password input is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, DataUtil.PASSWORD);
        getBrowser().refresh();
        changePasswordPage.renderedPage();
        assertTrue(changePasswordPage.isOldPasswordInputDisplayed(), "Old password input is displayed");

        LOG.info("STEP 6.11 - Site Dashboard Page");
        siteDashboardPage.navigate(siteName);
        assertTrue(siteDashboardPage.isAlfrescoLogoDisplayed(), "Alfresco logo is displayed");
        toolbarUserMenu.clickSetCurrentPageAsHome();
        siteDashboardPage.renderedPage();
        assertTrue(siteDashboardPage.isAlfrescoLogoDisplayed(), "Alfresco logo is displayed");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, DataUtil.PASSWORD);
        getBrowser().refresh();
        siteDashboardPage.renderedPage();
        assertTrue(siteDashboardPage.isAlfrescoLogoDisplayed(), "Alfresco logo is displayed");
    }

    @TestRail(id = "C2859")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void useMyDashboard()
    {
        String folderName = "TestFolder";
        String userName = "User1" + DataUtil.getUniqueIdentifier();
        String siteName = "Site1" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + domain, userName, userName);
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, "description", Site.Visibility.PUBLIC);
        contentService.createFolder(userName, DataUtil.PASSWORD, folderName, siteName);
        setupAuthenticatedSession(userName, DataUtil.PASSWORD);

        LOG.info("STEP 1 - Create a folder in site’s document library");
        documentLibraryPage.navigate(siteName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), folderName + " is displayed");

        LOG.info("STEP 2 - Go to the folder details page, click on the user drop down in the header bar and select Use Current Page link");
        documentLibraryPage.mouseOverContentItem(folderName);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "View Details", documentDetailsPage);
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");
        toolbarUserMenu.clickSetCurrentPageAsHome();
        documentDetailsPage.renderedPage();
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");

        LOG.info("STEP 3 - Click on the Home link in the header bar");
        toolbarUserMenu.clickHome();
        documentDetailsPage.renderedPage();
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");

        LOG.info("STEP 4 - Click on the user drop down in the header bar, log out and go to the alfresco log in page");
        toolbarUserMenu.clickLogout();

        LOG.info("STEP 5 - Log into Alfresco Share");
        setupAuthenticatedSession(userName, DataUtil.PASSWORD);
        getBrowser().refresh();
        documentDetailsPage.renderedPage();
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");

        LOG.info("STEP 6 - Click on the user drop down in the header bar and select Use My Dashboard link");
        toolbarUserMenu.clickSetDashBoardAsHome();
        documentDetailsPage.renderedPage();
        assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "\"Add Comment\" is displayed");

        LOG.info("STEP 7 - Click on the Home link in the header bar");
        toolbarUserMenu.clickHome();
        userDashboardPage.renderedPage();
        assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");

        LOG.info("STEP 8 - Click on the user drop down in the header bar, log out and log in again");
        toolbarUserMenu.clickLogout();
        setupAuthenticatedSession(userName, DataUtil.PASSWORD);
        getBrowser().refresh();
        assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");
    }
}
