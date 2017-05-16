package org.alfresco.share.searching.facetedSearch;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.Download;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


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
    Download download;

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

    String uniqueIdentifier = DataUtil.getUniqueIdentifier();
    String userName = "facetedUser-" + uniqueIdentifier;
    String firstName = "FirstName";
    String lastName = "LastName";
    String siteName = "FacetedSite-" + uniqueIdentifier;
    String siteForCopy = "FacetedCopy-" + uniqueIdentifier;
    String siteForMove = "FacetedMove-" + uniqueIdentifier;
    String description = "FacetedDescription-" + uniqueIdentifier;
    String docName1 = "FacetedDoc1-" + uniqueIdentifier;
    String docName2 = "FacetedDoc2-" + uniqueIdentifier;
    String docName3 = "FacetedDoc3-" + uniqueIdentifier;
    String docForMove = "FacetedDoc4-" + uniqueIdentifier;
    String docForDelete = "FacetedDoc5-" + uniqueIdentifier;
    String docContent = "content of file.";
    String searchTerm = "FacetedDoc";

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + domain, firstName, lastName);
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);
        siteService.create(userName, DataUtil.PASSWORD, domain, siteForCopy, description, Site.Visibility.PUBLIC);
        siteService.create(userName, DataUtil.PASSWORD, domain, siteForMove, description, Site.Visibility.PUBLIC);
        contentService.createDocument(userName, DataUtil.PASSWORD, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName1, docContent);
        contentService.createDocument(userName, DataUtil.PASSWORD, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName2, docContent);
        contentService.createDocument(userName, DataUtil.PASSWORD, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName3, docContent);
        contentService.createDocument(userName, DataUtil.PASSWORD, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docForMove, docContent);
        contentService.createDocument(userName, DataUtil.PASSWORD, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docForDelete, docContent);
    }

    @BeforeMethod
    public void beforeMethod()
    {
        setupAuthenticatedSession(userName, password);
        toolbar.search(searchTerm);
    }

    @TestRail(id = "C12816")
    @Test
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
        searchPage.clickSelectItemsListCheckbox();
        assertTrue(searchPage.isSelectedItemsListOptionDisplayed("All"));
        assertTrue(searchPage.isSelectedItemsListOptionDisplayed("None"));
        assertTrue(searchPage.isSelectedItemsListOptionDisplayed("Invert"));

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C12817")
    @Test
    public void facetedSearchALLOption()
    {
        LOG.info("STEP1: Select ALL option from the Select Items List checkbox.");
        searchPage.clickSelectAll();

        LOG.info("STEP2: Select Gallery View option.");
        searchPage.clickGalleryView();

        LOG.info("STEP3: Verify All items are selected checkbox.");

        assertTrue(searchPage.isALLItemsCheckboxChecked());

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C12818")
    @Test
    public void facetedSearchNoneOption()
    {
        LOG.info("STEP1: Select ALL option from the Select Items List checkbox.");
        searchPage.clickOptionFromSelectedItemsListCheckbox("All");
        assertTrue(searchPage.isALLItemsCheckboxChecked());
      //  getBrowser().waitInSeconds(3);

        LOG.info("STEP2: Select None option from the Select Items List checkbox.");
        searchPage.clickDetailedView();
      //  getBrowser().waitInSeconds(2);
        searchPage.clickOptionFromSelectedItemsListCheckbox("None");

        LOG.info("STEP3: Select Gallery View option.");
        searchPage.clickGalleryView();

        LOG.info("STEP4: Verify All items are selected checkbox.");
        assertTrue(searchPage.isNoneItemsCheckboxChecked());

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C12819")
    @Test
    public void facetedSearchInvertOption()
    {
        LOG.info("STEP1: Select ALL option from the Select Items List checkbox.");
        searchPage.clickOptionFromSelectedItemsListCheckbox("All");
        assertTrue(searchPage.isALLItemsCheckboxChecked());
      //  getBrowser().waitInSeconds(3);

        LOG.info("STEP2: Select Invert option from the Select Items List checkbox.");
        searchPage.clickDetailedView();
      //  getBrowser().waitInSeconds(2);
        searchPage.clickOptionFromSelectedItemsListCheckbox("Invert");

        LOG.info("STEP3: Verify All items are selected checkbox.");
        assertTrue(searchPage.isNoneItemsCheckboxChecked());

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C12821")
    @Test
    public void facetedSearchDownloadAsZipAction()
    {
        LOG.info("STEP1: Select 'ALL' option from the Select Items List checkbox.");
        searchPage.clickOptionFromSelectedItemsListCheckbox("All");
        assertTrue(searchPage.isALLItemsCheckboxChecked());
     //   getBrowser().waitInSeconds(3);

        LOG.info("STEP2: Click on 'Download as Zip' option from 'Selected Items...' dropdown.");
        searchPage.clickOptionFromSelectedItemsDropdown("Download as Zip");
      //  getBrowser().waitInSeconds(5);

        LOG.info("STEP3: Choose Save option and verify archive is displayed in specified location.");
        download.acceptAlertIfDisplayed();
     //   getBrowser().waitInSeconds(4);
        assertTrue(download.isFileInDirectory("Archive", ".zip"), "The zip archive was not found in the specified location");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C12823")
    @Test
    public void facetedSearchCopyToAction()
    {
        LOG.info("STEP1: Select the documents to be copied.");
        searchPage.clickCheckbox(docName1);
        searchPage.clickCheckbox(docName2);

        LOG.info("STEP2: Click on 'Copy to...' option from 'Selected Items...' dropdown.");
        searchPage.clickSelectedItemsDropdown();
        searchPage.clickCopyTo();
      //  getBrowser().waitInSeconds(4);

        LOG.info("STEP3: Copy the selected files to destination site.");
       // searchPage.copyOrMoveToSiteFromFacetedSearch("All Sites", siteForCopy, "Copy");
        copyMoveUnzipToDialog.clickDestinationButton("All Sites");
        copyMoveUnzipToDialog.clickSite(siteForCopy);
        copyMoveUnzipToDialog.clickPathFolder("documentLibrary");
        copyMoveUnzipToDialog.clickButtton("Copy");
      //  getBrowser().waitInSeconds(4);

        LOG.info("STEP7: Verify that the files have been copied");
        documentLibraryPage.navigate(siteForCopy);
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName1));
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName2));

        contentService.deleteDocument(userName, password, siteForCopy, docName1);
        contentService.deleteDocument(userName, password, siteForCopy, docName2);
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C12825")
    @Test
    public void facetedSearchMoveToAction()
    {
        LOG.info("STEP1: Select the document to move.");
        searchPage.clickCheckbox(docForMove);
      //  getBrowser().waitInSeconds(3);

        LOG.info("STEP2: Click on 'Move to...' option from 'Selected Items...' dropdown.");
        searchPage.clickOptionFromSelectedItemsDropdown("Move to...");
      //  getBrowser().waitInSeconds(4);

        LOG.info("STEP3: Move the selected files to destination site.");
        searchPage.copyOrMoveToSiteFromFacetedSearch("All Sites", siteForMove, "Move");
     //   getBrowser().waitInSeconds(4);

        LOG.info("STEP7: Verify that the files has been moved");
        documentLibraryPage.navigate(siteForMove);
        assertTrue(documentLibraryPage.isContentNameDisplayed(docForMove));

        documentLibraryPage.navigate(siteName);
        assertFalse(documentLibraryPage.isContentNameDisplayed(docForMove));

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C12826")
    @Test
    public void facetedSearchStartWorkflowAction()
    {
        LOG.info("STEP1: Select a document to start workflow.");
        searchPage.clickDetailedView();
    //    getBrowser().waitInSeconds(3);
        searchPage.clickCheckbox(docName1);
     //   getBrowser().waitInSeconds(2);

        LOG.info("STEP2: Click on 'Start Workflow...' option from 'Selected Items...' dropdown.");
        searchPage.clickOptionFromSelectedItemsDropdown("Start Workflow...");
    //    getBrowser().waitInSeconds(4);

        LOG.info("STEP 2: Click on 'Please select a workflow' button");
        startWorkflowPage.clickOnSelectButton();

        LOG.info("STEP 3: Select the workflow 'New Task' from the drop-down list.");
        startWorkflowPage.selectAWorkflow("New Task");

        LOG.info("STEP 4: Add message, select a Due date, priority, assign it to you and click Start Workflow");
        startWorkflowPage.addWorkflowDescription("FacetedWorkflowDescription");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("High");
        startWorkflowPage.clickAddUsersIcon();
        selectAssigneeToWorkflowPopUp.searchUser(userName);
    //    getBrowser().waitInSeconds(3);
        selectPopUpPage.clickAddIcon("FirstName LastName (" + userName + ")");
        selectAssigneeToWorkflowPopUp.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
    //    getBrowser().waitInSeconds(15);
        userDashboardPage.navigate(userName);
        assertTrue(myTasksDashlet.isTaskPresent("FacetedWorkflowDescription"), "Task is not present in Active tasks");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C12828")
    @Test
    public void facetedSearchDeleteAction()
    {
        LOG.info("STEP1: Select the document to delete.");
        searchPage.clickCheckbox(docForDelete);
     //   getBrowser().waitInSeconds(3);

        LOG.info("STEP2: Click on 'Delete' option from 'Selected Items...' dropdown and confirm deletion.");
        searchPage.clickOptionFromSelectedItemsDropdown("Delete");
       // getBrowser().waitInSeconds(2);
        searchPage.deleteDocuments(true);
       // getBrowser().waitInSeconds(4);

        LOG.info("STEP3: Verify that the file has been deleted.");
        assertFalse(searchPage.isResultFound(docForDelete));

        LOG.info("STEP4: Verify that the deleted file is present in Trashcan.");
        userTrashcanPage.navigate(userName);
        assertTrue(userTrashcanPage.getItemsNamesList().contains(docForDelete), docForDelete + " isn't displayed in Trashcan.");
    }

    @TestRail(id = "C12831")
    @Test
    public void stateOfCheckboxForNoSelectedItems()
    {
        LOG.info("STEP1: Observe that Selected Items' drop down menu is disabled; 'Check box' drop down menu is enabled");
        assertFalse(searchPage.isSelectedItemsDropdownEnabled());
        assertTrue(searchPage.isNoneItemsCheckboxChecked());
    }

    @TestRail(id = "C12832")
    @Test
    public void stateOfCheckboxForSomeSelectedItems()
    {
        LOG.info("STEP1: Select 'ALL' option from the Select Items List checkbox.");
        searchPage.clickOptionFromSelectedItemsListCheckbox("All");

        LOG.info("STEP2: Observe that Selected Items' drop down menu is enabled; 'Check box' drop down menu is enabled.");
        assertTrue(searchPage.isSelectedItemsDropdownEnabled());
        assertFalse(searchPage.isNoneItemsCheckboxChecked());
        assertTrue(searchPage.isALLItemsCheckboxChecked());
    }
}
