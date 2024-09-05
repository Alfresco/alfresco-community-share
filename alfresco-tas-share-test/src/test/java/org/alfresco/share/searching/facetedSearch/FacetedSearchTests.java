package org.alfresco.share.searching.facetedSearch;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.ContentService;
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
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.SiteModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Razvan.Dorobantu
 */
public class FacetedSearchTests extends BaseTest
{
    Toolbar toolbar;
    SearchPage searchPage;
    DocumentLibraryPage documentLibraryPage;
    StartWorkflowPage startWorkflowPage;
    SelectAssigneeToWorkflowPopUp selectAssigneeToWorkflowPopUp;
    SelectPopUpPage selectPopUpPage;
    UserDashboardPage userDashboardPage;
    MyTasksDashlet myTasksDashlet;
    UserTrashcanPage userTrashcanPage;
    CopyMoveUnzipToDialog copyMoveUnzipToDialog;
    @Autowired
    private ContentService contentService;

    String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private final String password = "password";
    String docName1 = "FacetedTestDoc1-" + uniqueIdentifier;
    String docName2 = "FacetedTestDoc2-" + uniqueIdentifier;
    String docName3 = "FacetedTestDoc3-" + uniqueIdentifier;
    String docWorkflow = "FacetedTestDoc6-" + uniqueIdentifier;
    String docForMove = "FacetedTestDoc4-" + uniqueIdentifier;
    String docForDelete = "FacetedTestDoc5-" + uniqueIdentifier;
    String searchTerm = "FacetedTestDoc";
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site1 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site3 = new ThreadLocal<>();
    private String siteName;
    private String siteForCopy;
    private String siteForMove;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition1: Any test user is created & Sites are Created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        authenticateUsingCookies(user.get());

        site1.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        siteName = site1.get().getTitle();

        site2.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        siteForCopy = site2.get().getTitle();

        site3.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        siteForMove = site3.get().getTitle();

        String userName = user.get().getUsername();
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            docName1, "Test DocName1");
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            docName2, "Test DocName2");
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            docName3, "Test DocName3");
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            docForMove, "Test Doc For Move");
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            docForDelete, "Test Doc For Delete");
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN,
            docWorkflow, "Test docWorkflow");

        toolbar = new Toolbar(webDriver);
        searchPage = new SearchPage(webDriver);
        copyMoveUnzipToDialog = new CopyMoveUnzipToDialog(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        startWorkflowPage = new StartWorkflowPage(webDriver);
        selectAssigneeToWorkflowPopUp = new SelectAssigneeToWorkflowPopUp(webDriver);
        selectPopUpPage = new SelectPopUpPage(webDriver);
        userDashboardPage = new UserDashboardPage(webDriver);
        myTasksDashlet = new MyTasksDashlet(webDriver);
        userTrashcanPage = new UserTrashcanPage(webDriver);
    }
    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site1.get());
        deleteSitesIfNotNull(site2.get());
        deleteSitesIfNotNull(site3.get());
    }

    @TestRail (id = "C12816")
    @Test(groups = { TestGroup.SANITY, TestGroup.SEARCH, "Searching"}, priority = -1)
    public void facetedSearchBulkActionsTest()
    {
        toolbar.search(searchTerm);
        log.info("STEP1: Verify search items are displayed.");
        searchPage.clickDetailedView();
        assertTrue(searchPage.getNumberOfResultsText().contains(" - results found"), "Section with number of results is displayed");
        searchPage
            .clickSortDropDown_CreatedDate()
            .assertCreatedDataIsDisplayed(docName1)
            .assertCreatedDataIsDisplayed(docName2)
            .assertCreatedDataIsDisplayed(docName3);

        log.info("STEP2: Verify options from the \"Selected Items...\" dropdown");
        searchPage
            .click_checkBox(5)
            .click_checkBox(4)
            .click_checkBox(3)
            .click_checkBox(2)
            .click_checkBox(1)
            .click_checkBox(0);
        searchPage
            .clickSelectedItemsDropdown();
        searchPage
            .assert_IsSelectedItemsOptionDisplayed("Download as Zip")
            .assert_IsSelectedItemsOptionDisplayed("Copy to...")
            .assert_IsSelectedItemsOptionDisplayed("Move to...")
            .assert_IsSelectedItemsOptionDisplayed("Start Workflow...")
            .assert_IsSelectedItemsOptionDisplayed("Delete");

        log.info("STEP3: Verify options from the \"Selected Items List...\" checkbox");
        searchPage
            .clickSelectedItemsListDropdownArrow();
        searchPage
            .assert_isSelectedItemsListOptionDisplayed("All")
            .assert_isSelectedItemsListOptionDisplayed("None")
            .assert_isSelectedItemsListOptionDisplayed("Invert");
    }

    @TestRail (id = "C12817")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH }, priority = 2)
    public void facetedSearchALLOption()
    {
        toolbar
            .search(searchTerm);
        log.info("Verify search items are displayed.");
        searchPage
            .clickSortDropDown_CreatedDate()
            .assertCreatedDataIsDisplayed(docName1)
            .assertCreatedDataIsDisplayed(docName2)
            .assertCreatedDataIsDisplayed(docName3);
        log.info("STEP1&2&3: Select ALL option from the Select Items List checkbox and Gallery View");
        searchPage
            .clickSelectAll()
            .clickGalleryView();
        searchPage
            .assert_isAllItemsCheckBoxChecked()
            .clickDetailedView();
    }

    @TestRail (id = "C12818")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void facetedSearchNoneOption()
    {
        toolbar
            .search(searchTerm);
        log.info("Verify search items are displayed.");
        searchPage
            .clickSortDropDown_CreatedDate()
            .assertCreatedDataIsDisplayed(docName1)
            .assertCreatedDataIsDisplayed(docName2)
            .assertCreatedDataIsDisplayed(docName3);
        log.info("STEP1: Select ALL option from the Select Items List checkbox.");
        searchPage
            .clickSelectAll()
            .assert_isAllItemsCheckBoxChecked();
        log.info("STEP2: Select None option from the Select Items List checkbox.");
        searchPage
            .clickDetailedView()
            .clickSelectedItemsListDropdownArrow()
            .click_OptionFromSelectedListItemsDropdown("None");
        log.info("STEP3: Select Gallery View option and check all items are selected");
        searchPage
            .clickGalleryView()
            .assert_isNoneItemsCheckBoxChecked()
            .clickDetailedView();
    }

    @TestRail (id = "C12819")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH})
    public void facetedSearchInvertOption()
    {
        toolbar
            .search(searchTerm);
        log.info("Verify search items are displayed.");
        searchPage
            .clickSortDropDown_CreatedDate()
            .assertCreatedDataIsDisplayed(docName1)
            .assertCreatedDataIsDisplayed(docName2)
            .assertCreatedDataIsDisplayed(docName3);
        log.info("STEP1: Select ALL option from the Select Items List checkbox.");
        searchPage
            .clickSelectAll()
            .assert_isAllItemsCheckBoxChecked();
        log.info("STEP2: Select Invert option from the Select Items List checkbox.");
        searchPage
            .clickDetailedView()
            .clickSelectedItemsListDropdownArrow();
        searchPage
            .click_OptionFromSelectedListItemsDropdown("Invert");
        log.info("STEP3: Verify All items are selected checkbox.");
        searchPage
            .assert_isNoneItemsCheckBoxChecked();
    }

    @TestRail (id = "C12821")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void facetedSearchDownloadAsZipAction()
    {
        toolbar
            .search(searchTerm);
        log.info("Verify search items are displayed.");
        searchPage
            .clickSortDropDown_CreatedDate()
            .assertCreatedDataIsDisplayed(docName1)
            .assertCreatedDataIsDisplayed(docName2)
            .assertCreatedDataIsDisplayed(docName3);
        log.info("STEP1: Select 'ALL' option from the Select Items List checkbox.");
        searchPage
            .clickSelectAll()
            .assert_isAllItemsCheckBoxChecked();
        log.info("STEP2: Click on 'Download as Zip' option from 'Selected Items...' dropdown.");
        searchPage
            .clickSelectedItemsDropdown()
            .click_OptionFromSelectedItemsDropdown("Download as Zip");
        log.info("STEP3: Choose Save option and verify archive is displayed in specified location.");
        searchPage
            .acceptAlertIfDisplayed();
    }

    @TestRail (id = "C12823")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "Searching" })
    public void facetedSearchCopyToAction() {
        toolbar
            .search(searchTerm);
        log.info("Verify search items are displayed.");
        searchPage
            .clickSortDropDown_CreatedDate()
            .assertCreatedDataIsDisplayed(docName1)
            .assertCreatedDataIsDisplayed(docName2)
            .assertCreatedDataIsDisplayed(docName3);

        String userName = user.get().getUsername();
        log.info("STEP1: Select the documents to be copied.");
        searchPage.click_checkBox(5);
        log.info("STEP2: Click on 'Copy to...' option from 'Selected Items...' dropdown.");
        searchPage
            .clickSelectedItemsDropdown()
            .click_OptionFromSelectedItemsDropdown("Copy to...");
        log.info("STEP3: Copy the selected files to destination site.");
        copyMoveUnzipToDialog
            .select_AllSitesDestination()
            .select_Site(new SiteModel(siteForCopy)).clickDocumentsFolder();
        copyMoveUnzipToDialog
            .clickCopyButton();
        log.info("STEP7: Verify that the files have been copied");
        documentLibraryPage
            .navigate(siteForCopy);
        documentLibraryPage
            .assertFileIsDisplayed(docName1);
        contentService.deleteDocument(userName, password, siteForCopy, docName1);
    }

    @TestRail (id = "C12825")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "Searching" })
    public void facetedSearchMoveToAction() {
        toolbar
            .search(searchTerm);
        log.info("Verify search items are displayed.");
        searchPage
            .clickSortDropDown_CreatedDate()
            .assertCreatedDataIsDisplayed(docForMove);
        log.info("STEP1: Select the document to move.");
        searchPage
            .click_checkBox(2);
        log.info("STEP2: Click on 'Move to...' option from 'Selected Items...' dropdown.");
        searchPage
            .clickSelectedItemsDropdown()
            .click_OptionFromSelectedItemsDropdown("Move to...");
        log.info("STEP3: Move the selected files to destination site.");
        copyMoveUnzipToDialog
            .select_AllSitesDestination()
            .select_Site(new SiteModel(siteForMove))
            .clickDocumentsFolder()
            .click_MoveButton();
        log.info("STEP7: Verify that the files has been moved");
        documentLibraryPage
            .navigate(siteForMove);
        documentLibraryPage
            .assertFileIsDisplayed(docForMove);
        documentLibraryPage
            .navigate(siteName)
            .assertFileIsNotDisplayed(docForMove);
    }

    @TestRail (id = "C12826")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "Searching" })
    public void facetedSearchStartWorkflowAction()
    {
        toolbar
            .search(searchTerm);
        log.info("Verify search items are displayed.");
        searchPage
            .clickSortDropDown_CreatedDate()
            .assertCreatedDataIsDisplayed(docName1)
            .assertCreatedDataIsDisplayed(docName2)
            .assertCreatedDataIsDisplayed(docName3);

        String userName = user.get().getUsername();
        log.info("STEP1: Select a document to start workflow.");
        searchPage.clickDetailedView().click_checkBox(5);
        log.info("STEP2: Click on 'Start Workflow...' option from 'Selected Items...' dropdown.");
        searchPage
            .clickSelectedItemsDropdown()
            .click_OptionFromSelectedItemsDropdown("Start Workflow...");
        log.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage
            .selectAWorkflow("New Task");
        log.info("STEP 4: Add message, select a Due date, priority, assign it to you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("FacetedWorkflowDescription");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectAssigneeToWorkflowPopUp.searchUser(userName);
        selectPopUpPage.clickAddIcon("FN-"+(userName) +" "+ "LN-"+(userName)+" "+"("+(userName)+")");
        selectAssigneeToWorkflowPopUp.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        userDashboardPage.navigate(userName);
        myTasksDashlet.assertTaskNameEqualsTo("FacetedWorkflowDescription");
    }

    @TestRail (id = "C12828")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "Searching" })
    public void facetedSearchDeleteAction() throws InterruptedException {
        toolbar
            .search(searchTerm);
        log.info("Verify search items are displayed.");
        searchPage
            .clickSortDropDown_CreatedDate()
            .assertCreatedDataIsDisplayed(docName1)
            .assertCreatedDataIsDisplayed(docName2)
            .assertCreatedDataIsDisplayed(docName3)
            .assertCreatedDataIsDisplayed(docForDelete);

        String userName = user.get().getUsername();
        int retry = 0;
        boolean status;

        log.info("STEP1: Select the document to delete.");
        searchPage.click_checkBox(1);
        log.info("STEP2: Click on 'Delete' option from 'Selected Items...' dropdown and confirm deletion.");
        searchPage
            .clickSelectedItemsDropdown()
            .click_OptionFromSelectedItemsDropdown("Delete");
        searchPage
            .deleteDocuments(true);
        log.info("STEP3: Verify that the file has been deleted.");
        toolbar.search(docForDelete);
        status = searchPage.is_ResultFound();
        if (retry < 5 && status == true)
        {
            toolbar.search(docForDelete);
            status = searchPage.isResultFound(docForDelete);
            retry++;
        }
        assertFalse(status, docForDelete + " is still found by search");
        log.info("STEP4: Verify that the deleted file is present in Trashcan.");
        userTrashcanPage.navigate(userName);
        assertTrue(userTrashcanPage.getItemsNamesList().contains(docForDelete), docForDelete + " isn't displayed in Trashcan.");
    }

    @TestRail (id = "C12832, C12831")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void stateOfCheckbox()
    {
        toolbar
            .search(searchTerm);
        log.info("Verify search items are displayed.");
        searchPage
            .clickSortDropDown_CreatedDate()
            .assertCreatedDataIsDisplayed(docName1)
            .assertCreatedDataIsDisplayed(docName2)
            .assertCreatedDataIsDisplayed(docName3)
            .assertCreatedDataIsDisplayed(docForDelete);

        String userName = user.get().getUsername();
        log.info("STEP1: Observe that Selected Items' drop down menu is disabled; 'Check box' drop down menu is enabled");
        assertEquals(searchPage.getSelectedItemsState(), "true", "Selected Items menu is not disabled");
        assertTrue(searchPage.isNoneItemsCheckboxChecked());
        log.info("STEP1: Select 'ALL' option from the Select Items List checkbox.");
        searchPage.clickSelectAll();
        log.info("STEP2: Observe that Selected Items' drop down menu is enabled; 'Check box' drop down menu is enabled.");
        assertEquals(searchPage.getSelectedItemsState(), "false", "Selected Items menu is disabled");
        assertFalse(searchPage.isNoneItemsCheckboxChecked());
        assertTrue(searchPage.isALLItemsCheckboxChecked());
    }
}
