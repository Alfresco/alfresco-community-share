package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.dashlet.SiteActivitiesDashlet;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class CreateLinksTests extends ContextAwareWebTest
{
    @Autowired
    private DocumentLibraryPage documentLibraryPage;

    @Autowired
    private HeaderMenuBar headerMenuBar;

    @Autowired
    private CopyMoveUnzipToDialog copyMoveUnzipToDialog;

    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    @Autowired
    private SharedFilesPage sharedFilesPage;

    @Autowired
    private UserDashboardPage userDashboardPage;

    @Autowired
    private SiteDashboardPage siteDashboardPage;

    @Autowired
    private MyActivitiesDashlet myActivitiesDashlet;

    @Autowired
    private SiteActivitiesDashlet siteActivitiesDashlet;

    @Autowired
    private Toolbar toolbar;

    @Autowired
    private SearchPage searchPage;



    private final String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private final String userName = "user" + uniqueIdentifier;
    private final String firstName = "user";
    private final String lastName = uniqueIdentifier;
    private final String description = "description" + uniqueIdentifier;
    private final String content = "content" + uniqueIdentifier;
    private final String siteName1 = "1site" + uniqueIdentifier;
    private final String siteName2 = "2site" + uniqueIdentifier;
    private final String fileName1 = "file1-" + uniqueIdentifier;
    private final String fileName2 = "file2-" + uniqueIdentifier;
    private final String fileName3 = "file3-" + uniqueIdentifier;
    private final String linkFile1 = "Link to " + fileName1;
    private final String linkFile2 = "Link to " + fileName2;
    private final String linkFile3 = "Link to " + fileName3;

    @BeforeClass(alwaysRun = true)
    public void setupTest() {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
        siteService.create(userName, password, domain, siteName1, description, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteName2, description, SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName1, CMISUtil.DocumentType.TEXT_PLAIN, fileName1, content);
        contentService.createDocument(userName, password, siteName1, CMISUtil.DocumentType.TEXT_PLAIN, fileName2, content);
        contentService.createDocument(userName, password, siteName1, CMISUtil.DocumentType.TEXT_PLAIN, fileName3, content);
        contentService.createDocument(userName, password, siteName2, CMISUtil.DocumentType.TEXT_PLAIN, fileName1, content);
        contentService.createDocument(userName, password, siteName2, CMISUtil.DocumentType.TEXT_PLAIN, fileName2, content);
        contentService.createDocument(userName, password, siteName2, CMISUtil.DocumentType.TEXT_PLAIN, fileName3, content);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword,siteName1);
        siteService.delete(adminUser, adminPassword,siteName2);

    }

    @TestRail(id = "C42605")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromDocLibraryActions() {
        documentLibraryPage.navigate(siteName1);
        LOG.info("STEP1: From Document actions, click on \"Copy to\" option");
        documentLibraryPage.mouseOverContentItem(fileName1);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName1, language.translate("documentLibrary.contentActions.copyTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileName1 + " to...", "Displayed dialog=");
        LOG.info("STEP2: Verify \"Copy to\" dialog");
        assertTrue(copyMoveUnzipToDialog.isCreateLinkButtonDisplayedCopyToDialog(),
                "'Copy to...' dialog: 'Create Link' button is displayed.");
    }

    @TestRail(id = "C42606")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromDocLibrarySelectedItemsSingleItem() {
        documentLibraryPage.navigate(siteName1);
        LOG.info("STEP1: Select a file/folder and from 'Selected Items' menu, click on \"Copy to\" option");
        documentLibraryPage.clickCheckBox(fileName1);
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption(language.translate("documentLibrary.contentActions.copyTo"));
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy 1 items to...", "Displayed dialog=");
        LOG.info("STEP2: Verify \"Copy to\" dialog");
        assertTrue(copyMoveUnzipToDialog.isCreateLinkButtonDisplayedCopyToDialog(),
                "'Copy to...' dialog: 'Create Link' button is displayed.");
    }

    @TestRail(id = "C42607")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromDocLibrarySelectedItemsMultipleItems() {
        documentLibraryPage.navigate(siteName1);
        LOG.info("STEP1: Select multiple content and from 'Selected Items' menu, click on \"Copy to\" option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.all"));
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption(language.translate("documentLibrary.contentActions.copyTo"));
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy 3 items to...", "Displayed dialog=");
        LOG.info("STEP2: Verify \"Copy to\" dialog");
        assertTrue(copyMoveUnzipToDialog.isCreateLinkButtonDisplayedCopyToDialog(),
                "'Copy to...' dialog: 'Create Link' button is displayed.");
    }

    @TestRail(id = "C42608")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromDocumentDetailsActions() {
        documentLibraryPage.navigate(siteName1);
        LOG.info("STEP1: Open Document Details for a file");
        documentLibraryPage.clickOnFile(fileName1);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");
        LOG.info("STEP2: From Document actions, click \"Copy to\" option");
        documentDetailsPage.clickDocumentActionsOption(language.translate("documentLibrary.contentActions.copyTo"));
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileName1 + " to...", "Displayed dialog=");
        assertTrue(copyMoveUnzipToDialog.isCreateLinkButtonDisplayedCopyToDialog(),
                "'Copy to...' dialog: 'Create Link' button is displayed.");
    }

    @TestRail(id = "C42609")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromSearchResultsActions() {
        documentLibraryPage.navigate(siteName1);
        LOG.info("STEP1: Search for a document");
        toolbar.search(fileName2);
        assertEquals(searchPage.getPageTitle(), "Alfresco » Search", "Displayed page=");
        LOG.info("STEP2: Verify 'Copy to...' dialog from Search Results -> Actions");
        searchPage.clickOptionFromActionsMenu(fileName2, language.translate("documentLibrary.contentActions.copyTo"));
        assertEquals(copyMoveUnzipToDialog.getCopyToDialogTitle(), "Copy " + fileName2 + " to...", "Displayed dialog=");
        assertTrue(copyMoveUnzipToDialog.isCreateLinkDisplayedInCopyToDialogFromSearchPage(), "Create link button displayed in 'Copy to' dialog.");
    }

    @TestRail(id = "C42610")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromSearchResultsSingleItemSelected() {
        documentLibraryPage.navigate(siteName1);
        LOG.info("STEP1: Search for a document");
        toolbar.search(fileName2);
        assertEquals(searchPage.getPageTitle(), "Alfresco » Search", "Displayed page=");
        LOG.info("STEP2: In the Search Results page, select a few documents");
        searchPage.clickCheckbox(fileName2);
        LOG.info("STEP3: Click on Select Items and then click on \"Copy to\" option");
        searchPage.clickCopyToSelectedItemsOption();
        assertEquals(copyMoveUnzipToDialog.getCopyToDialogTitle(), "Copy files to...", "Displayed dialog=");
        assertTrue(copyMoveUnzipToDialog.isCreateLinkDisplayedInCopyToDialogFromSearchPage(), "Create link button displayed in 'Copy to' dialog.");
    }

    @TestRail(id = "C42611")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonFromSearchResultsMultipleItemsSelected() {
        documentLibraryPage.navigate(siteName1);
        LOG.info("STEP1: Search for a document");
        toolbar.search(uniqueIdentifier);
        assertEquals(searchPage.getPageTitle(), "Alfresco » Search", "Displayed page=");
        LOG.info("STEP2: In the Search Results page, select a few documents");
        searchPage.clickCheckbox(fileName1);
        searchPage.clickCheckbox(fileName2);
        searchPage.clickCheckbox(fileName3);
        LOG.info("STEP3: Click on Select Items and then click on \"Copy to\" option");
        searchPage.clickCopyToSelectedItemsOption();
        assertEquals(copyMoveUnzipToDialog.getCopyToDialogTitle(), "Copy files to...", "Displayed dialog=");
        assertTrue(copyMoveUnzipToDialog.isCreateLinkDisplayedInCopyToDialogFromSearchPage(), "Create link button displayed in 'Copy to' dialog.");
    }

    @TestRail(id = "C42613")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCreateLinkButtonInMoveToDialog() {
        documentLibraryPage.navigate(siteName1);
        LOG.info("STEP1: From Document actions, click \"Move to\" option");
        documentLibraryPage.mouseOverContentItem(fileName1);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName1, language.translate("documentLibrary.contentActions.moveTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Move " + fileName1 + " to...", "Displayed dialog=");
        LOG.info("STEP2: Verify \"Move to\" dialog");
        assertFalse(copyMoveUnzipToDialog.isCreateLinkButtonDisplayed(), "'Move to...' dialog: 'Create Link' button is not displayed.");
    }

    @TestRail(id = "C42614")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyLinkIsCreatedAtDestination() {
        documentLibraryPage.navigate(siteName1);
        LOG.info("STEP1: From Document actions, click on \"Copy to\" option");
        documentLibraryPage.mouseOverContentItem(fileName1);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName1, language.translate("documentLibrary.contentActions.copyTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileName1 + " to...", "Displayed dialog=");
        LOG.info("STEP2: Select a destination folder and click \"Create Link\" button");
        copyMoveUnzipToDialog.clickDestinationButton(language.translate("documentLibrary.sharedFiles"));
        copyMoveUnzipToDialog.clickCreateLink();
        LOG.info("STEP3: Go to the destination location and verify the content");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(linkFile1), linkFile1 + " is displayed in destination of copy file, Shared Files.");
    }

    @TestRail(id = "C42620")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void duplicateLinksAreNotAllowed() {
        documentLibraryPage.navigate(siteName2);
        LOG.info("STEP1: For a file/folder, click 'Copy to' option, select a destination folder and click 'Create Link' button");
        documentLibraryPage.mouseOverContentItem(fileName2);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName2, language.translate("documentLibrary.contentActions.copyTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileName2 + " to...", "Displayed dialog=");
        copyMoveUnzipToDialog.clickDestinationButton(language.translate("documentLibrary.sharedFiles"));
        copyMoveUnzipToDialog.clickCreateLink();
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(linkFile2), linkFile2 + " is displayed in destination of copy file, Shared Files.");
        LOG.info("STEP2: For the same content, click again 'Copy to' option, select a destination folder and click 'Create Link' button");
        documentLibraryPage.navigate(siteName2);
        documentLibraryPage.mouseOverContentItem(fileName2);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName2, language.translate("documentLibrary.contentActions.copyTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileName2 + " to...", "Displayed dialog=");
        copyMoveUnzipToDialog.clickDestinationButton(language.translate("documentLibrary.sharedFiles"));
        copyMoveUnzipToDialog.clickCreateLink();
        assertEquals(copyMoveUnzipToDialog.getMessage2(), language.translate("documentLibrary.contentActions.createLink.errorMessage"), "Displayed message= ");

    }

    @TestRail(id = "C42621")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createdLinkDisplayedInMyActivitiesDashlet() {
        String name = firstName + " " + lastName;
        String activity = name + " created link to " + fileName3 + " in " + siteName2;
        documentLibraryPage.navigate(siteName2);
        LOG.info("STEP1: For a file/folder, click 'Copy to' option, select a destination folder and click 'Create Link' button");
        documentLibraryPage.mouseOverContentItem(fileName3);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName3, language.translate("documentLibrary.contentActions.copyTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileName3 + " to...", "Displayed dialog=");
        copyMoveUnzipToDialog.clickDestinationButton("Recent Sites");
        copyMoveUnzipToDialog.clickSite(siteName2);
        copyMoveUnzipToDialog.clickCreateLink();
        documentLibraryPage.selectDocumentLibraryItemRow(linkFile3);
        assertTrue(documentLibraryPage.isContentNameDisplayed(linkFile3),
                linkFile3 + " is displayed in destination of copy file, Document Library of " + siteName2);
        LOG.info("STEP2: Navigate to User Dashboard page");
        userDashboardPage.navigate(userName);
        assertEquals(userDashboardPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");
        assertTrue(myActivitiesDashlet.isActivityPresentInActivitiesDashlet(activity), activity + " is displayed in 'My Activities' dashlet.");
    }

    @TestRail(id = "C42622")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createdLinkDisplayedInSiteActivitiesDashlet() {
        String name = firstName + " " + lastName;
        String activity = name + " created link to " + fileName3;
        documentLibraryPage.navigate(siteName2);
        LOG.info("STEP1: For a file/folder, click 'Copy to' option, select a destination folder and click 'Create Link' button");
        documentLibraryPage.mouseOverContentItem(fileName3);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName3, language.translate("documentLibrary.contentActions.copyTo"), copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + fileName3 + " to...", "Displayed dialog=");
        copyMoveUnzipToDialog.clickDestinationButton("Recent Sites");
        copyMoveUnzipToDialog.clickSite(siteName2);
        copyMoveUnzipToDialog.clickCreateLink();
        documentLibraryPage.selectDocumentLibraryItemRow(linkFile3);
        assertTrue(documentLibraryPage.isContentNameDisplayed(linkFile3),
                linkFile3 + " is displayed in destination of copy file, Document Library of " + siteName2);
        LOG.info("STEP2: Navigate to Site Dashboard page and verify Site Activities dashlet");
        siteDashboardPage.navigate(siteName2);
        assertEquals(siteDashboardPage.getPageTitle(), "Alfresco » Site Dashboard", "Displayed page=");
        assertTrue(siteActivitiesDashlet.isActivityPresentInActivitiesDashlet(activity),
                "Activity: '" + activity + "' is displayed in 'Site Activities' dashlet.");
    }
}