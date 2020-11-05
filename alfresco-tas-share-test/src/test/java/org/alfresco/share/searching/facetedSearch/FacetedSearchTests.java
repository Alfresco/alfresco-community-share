package org.alfresco.share.searching.facetedSearch;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.tasksAndWorkflows.SelectAssigneeToWorkflowPopUp;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.profile.UserTrashcanPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
public class FacetedSearchTests extends ContextAwareWebTest
{
    @Autowired
    Toolbar toolbar;

    @Autowired
    SearchPage searchPage;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    StartWorkflowPage startWorkflowPage;

    @Autowired
    SelectAssigneeToWorkflowPopUp selectAssigneeToWorkflowPopUp;

    @Autowired
    SelectPopUpPage selectPopUpPage;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    MyTasksDashlet myTasksDashlet;

    @Autowired
    UserTrashcanPage userTrashcanPage;

    @Autowired
    CopyMoveUnzipToDialog copyMoveUnzipToDialog;

    String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    String userName = "facetedTestUser-" + uniqueIdentifier;
    String firstName = "FirstName";
    String lastName = "LastName";
    String siteName = "FacetedSite-" + uniqueIdentifier;
    String siteForCopy = "FacetedCopy-" + uniqueIdentifier;
    String siteForMove = "FacetedMove-" + uniqueIdentifier;
    String description = "FacetedDescription-" + uniqueIdentifier;
    String docName1 = "FacetedTestDoc1-" + uniqueIdentifier;
    String docName2 = "FacetedTestDoc2-" + uniqueIdentifier;
    String docName3 = "FacetedTestDoc3-" + uniqueIdentifier;
    String docWorkflow = "FacetedTestDoc6-" + uniqueIdentifier;
    String docForMove = "FacetedTestDoc4-" + uniqueIdentifier;
    String docForDelete = "FacetedTestDoc5-" + uniqueIdentifier;
    String docContent = "content of file.";
    String searchTerm = "FacetedTestDoc";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteForCopy, description, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteForMove, description, SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName1, docContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName2, docContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName3, docContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docForMove, docContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docForDelete, docContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docWorkflow, docContent);
    }

    @BeforeMethod (alwaysRun = true)
    public void beforeMethod()
    {
        setupAuthenticatedSession(userName, password);
        toolbar.search(searchTerm);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
        siteService.delete(adminUser, adminPassword, siteForCopy);
        siteService.delete(adminUser, adminPassword, siteForMove);
    }

    @TestRail (id = "C12816")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "tobefixed" }, priority = 2)
    public void facetedSearchBulkActionsTest()
    {
        LOG.info("STEP1: Verify search items are displayed.");
        searchPage.clickDetailedView();
        assertTrue(searchPage.getNumberOfResultsText().contains(" - results found"), "Section with number of results is displayed");
        assertTrue(searchPage.isResultFoundWithRetry(docName1));
        assertTrue(searchPage.isResultFoundWithRetry(docName2));
        assertTrue(searchPage.isResultFoundWithRetry(docName3));
        LOG.info("STEP2: Verify options from the \"Selected Items...\" dropdown");
        searchPage.clickSelectAll();
        searchPage.clickSelectedItemsDropdown();
        assertTrue(searchPage.isSelectedItemsOptionDisplayed("Download as Zip"));
        assertTrue(searchPage.isSelectedItemsOptionDisplayed("Copy to..."));
        assertTrue(searchPage.isSelectedItemsOptionDisplayed("Move to..."));
        assertTrue(searchPage.isSelectedItemsOptionDisplayed("Start Workflow..."));
        assertTrue(searchPage.isSelectedItemsOptionDisplayed("Delete"));
        LOG.info("STEP3: Verify options from the \"Selected Items List...\" checkbox");
        searchPage.clickSelectedItemsListDropdownArrow();
        assertTrue(searchPage.isSelectedItemsListOptionDisplayed("All"));
        assertTrue(searchPage.isSelectedItemsListOptionDisplayed("None"));
        assertTrue(searchPage.isSelectedItemsListOptionDisplayed("Invert"));
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C12817")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "tobefixed" }, priority = 3)
    public void facetedSearchALLOption()
    {
        LOG.info("STEP1&2&3: Select ALL option from the Select Items List checkbox and Gallery View");
        searchPage.clickSelectAll();
        searchPage.clickGalleryView();
        assertTrue(searchPage.isALLItemsCheckboxChecked());
        searchPage.clickDetailedView();
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C12818")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "tobefixed" }, priority = 4)
    public void facetedSearchNoneOption()
    {
        LOG.info("STEP1: Select ALL option from the Select Items List checkbox.");
        searchPage.clickSelectAll();
        assertTrue(searchPage.isALLItemsCheckboxChecked());
        LOG.info("STEP2: Select None option from the Select Items List checkbox.");
        searchPage.clickDetailedView();
        searchPage.clickSelectedItemsListDropdownArrow();
        searchPage.clickOptionFromSelectedItemsListCheckbox("None");
        LOG.info("STEP3: Select Gallery View option and check all items are selected");
        searchPage.clickGalleryView();
        assertTrue(searchPage.isNoneItemsCheckboxChecked());
        searchPage.clickDetailedView();
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C12819")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "tobefixed"}, priority = 2)
    public void facetedSearchInvertOption()
    {
        LOG.info("STEP1: Select ALL option from the Select Items List checkbox.");
        searchPage.clickSelectAll();
        assertTrue(searchPage.isALLItemsCheckboxChecked());
        LOG.info("STEP2: Select Invert option from the Select Items List checkbox.");
        searchPage.clickDetailedView();
        searchPage.clickSelectedItemsListDropdownArrow();
        searchPage.clickOptionFromSelectedItemsListCheckbox("Invert");
        LOG.info("STEP3: Verify All items are selected checkbox.");
        assertTrue(searchPage.isNoneItemsCheckboxChecked());
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C12821")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "tobefixed" }, priority = 5)
    public void facetedSearchDownloadAsZipAction()
    {
        LOG.info("STEP1: Select 'ALL' option from the Select Items List checkbox.");
        searchPage.clickSelectAll();
        assertTrue(searchPage.isALLItemsCheckboxChecked());
        LOG.info("STEP2: Click on 'Download as Zip' option from 'Selected Items...' dropdown.");
        searchPage.clickSelectedItemsDropdown();
        searchPage.clickOptionFromSelectedItemsDropdown("Download as Zip");
        LOG.info("STEP3: Choose Save option and verify archive is displayed in specified location.");
        searchPage.acceptAlertIfDisplayed();
        assertTrue(isFileInDirectory("Archive", ".zip"), "The zip archive was not found in the specified location");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C12823")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "tobefixed" }, priority = 6)
    public void facetedSearchCopyToAction()
    {
        LOG.info("STEP1: Select the documents to be copied.");
        searchPage.clickCheckbox(docName1);
        LOG.info("STEP2: Click on 'Copy to...' option from 'Selected Items...' dropdown.");
        searchPage.clickSelectedItemsDropdown();
        searchPage.clickOptionFromSelectedItemsDropdown("Copy to...");
        LOG.info("STEP3: Copy the selected files to destination site.");
        copyMoveUnzipToDialog.selectAllSitesDestination();
        copyMoveUnzipToDialog.selectSite(siteForCopy);
        copyMoveUnzipToDialog.clickDocumentLibrary();
        copyMoveUnzipToDialog.clickButton("Copy");
        LOG.info("STEP7: Verify that the files have been copied");
        documentLibraryPage.navigate(siteForCopy);
        getBrowser().waitInSeconds(1);
        assertTrue(documentLibraryPage.isFileDisplayed(docName1));
        contentService.deleteDocument(userName, password, siteForCopy, docName1);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C12825")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "tobefixed" }, priority = 1)
    public void facetedSearchMoveToAction()
    {
        LOG.info("STEP1: Select the document to move.");
        searchPage.clickCheckbox(docForMove);
        LOG.info("STEP2: Click on 'Move to...' option from 'Selected Items...' dropdown.");
        searchPage.clickSelectedItemsDropdown();
        searchPage.clickOptionFromSelectedItemsDropdown("Move to...");
        LOG.info("STEP3: Move the selected files to destination site.");
        copyMoveUnzipToDialog.selectAllSitesDestination();
        copyMoveUnzipToDialog.selectSite(siteForMove);
        copyMoveUnzipToDialog.clickDocumentLibrary();
        copyMoveUnzipToDialog.clickButton("Move");
        LOG.info("STEP7: Verify that the files has been moved");
        documentLibraryPage.navigate(siteForMove);
        assertTrue(documentLibraryPage.isContentNameDisplayed(docForMove));
        documentLibraryPage.navigate(siteName);
        assertFalse(documentLibraryPage.isContentNameDisplayed(docForMove));
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C12826")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "tobefixed" }, priority = 8)
    public void facetedSearchStartWorkflowAction()
    {
        LOG.info("STEP1: Select a document to start workflow.");
        searchPage.clickDetailedView();
        searchPage.clickCheckbox(docName1);
        LOG.info("STEP2: Click on 'Start Workflow...' option from 'Selected Items...' dropdown.");
        searchPage.clickSelectedItemsDropdown();
        searchPage.clickOptionFromSelectedItemsDropdown("Start Workflow...");
        LOG.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("New Task");
        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("FacetedWorkflowDescription");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectAssigneeToWorkflowPopUp.searchUser(userName);
        selectPopUpPage.clickAddIcon("FirstName LastName (" + userName + ")");
        selectAssigneeToWorkflowPopUp.clickOkButton();
        startWorkflowPage.clickStartWorkflow(searchPage);
        userDashboardPage.navigate(userName);
        assertTrue(myTasksDashlet.isTaskPresent("FacetedWorkflowDescription"), "Task is not present in Active tasks");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C12828")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "tobefixed" }, priority = 10)
    public void facetedSearchDeleteAction()
    {
        int retry = 0;
        boolean status = false;

        LOG.info("STEP1: Select the document to delete.");
        searchPage.clickCheckbox(docForDelete);
        LOG.info("STEP2: Click on 'Delete' option from 'Selected Items...' dropdown and confirm deletion.");
        searchPage.clickSelectedItemsDropdown();
        searchPage.clickOptionFromSelectedItemsDropdown("Delete");
        searchPage.deleteDocuments(true);
        getBrowser().waitInSeconds(10);
        LOG.info("STEP3: Verify that the file has been deleted.");
        toolbar.search(docForDelete);
        status = searchPage.isResultFound(docForDelete);
        if (retry < 5 && status == true)
        {
            toolbar.search(docForDelete);
            status = searchPage.isResultFound(docForDelete);
            retry++;
            getBrowser().waitInSeconds(2);
        }
        assertFalse(status, docForDelete + " is still found by search");
        LOG.info("STEP4: Verify that the deleted file is present in Trashcan.");
        userTrashcanPage.navigate(userName);
        assertTrue(userTrashcanPage.getItemsNamesList().contains(docForDelete), docForDelete + " isn't displayed in Trashcan.");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C12832, C12831")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "tobefixed" }, priority = 9)
    public void stateOfCheckbox()
    {
        LOG.info("STEP1: Observe that Selected Items' drop down menu is disabled; 'Check box' drop down menu is enabled");
        assertEquals(searchPage.getSelectedItemsState(), "true", "Selected Items menu is not disabled");
        assertTrue(searchPage.isNoneItemsCheckboxChecked());
        LOG.info("STEP1: Select 'ALL' option from the Select Items List checkbox.");
        searchPage.clickSelectAll();
        LOG.info("STEP2: Observe that Selected Items' drop down menu is enabled; 'Check box' drop down menu is enabled.");
        assertEquals(searchPage.getSelectedItemsState(), "false", "Selected Items menu is disabled");
        assertFalse(searchPage.isNoneItemsCheckboxChecked());
        assertTrue(searchPage.isALLItemsCheckboxChecked());
        cleanupAuthenticatedSession();
    }
}
