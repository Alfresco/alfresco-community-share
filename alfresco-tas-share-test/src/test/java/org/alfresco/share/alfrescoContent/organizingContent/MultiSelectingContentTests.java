package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class MultiSelectingContentTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private HeaderMenuBar headerMenuBar;

    @Autowired private StartWorkflowPage startWorkflowPage;

    @Autowired private DeleteDialog deleteDialog;

    private final String userName = String.format("profileUser-%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("Description-%s", RandomData.getRandomAlphanumeric());
    private final String fileContent = "content of the file.";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "FirstName", "LastName");
        setupAuthenticatedSession(userName, password);
    }

    @TestRail(id = "C7546")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void selectItemsByCheckbox()
    {
        String random = RandomData.getRandomAlphanumeric();
        String siteName = String.format("C7546-site-%s", random);
        String folderName1 = "C7546-folder1-" + random;
        String folderName2 = "C7546-folder2-" + random;
        String docName = "C7546-document-" + random;
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName1, siteName);
        contentService.createFolder(userName, password, folderName2, siteName);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, fileContent);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("STEP1: Click on the folder checkbox");
        documentLibraryPage.clickCheckBox(folderName1);
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP2: Click again on the same folder's checkbox");
        documentLibraryPage.clickCheckBox(folderName1);
        assertFalse(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP3: Click on text doc's checkbox");
        documentLibraryPage.clickCheckBox(docName);
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP4: Click on the folder2 checkbox");
        documentLibraryPage.clickCheckBox(folderName2);
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");
    }

    @TestRail(id = "C7548")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void selectItemsByMenu()
    {
        String random = RandomData.getRandomAlphanumeric();
        String siteName = String.format("C7548-site-%s", random);
        String folderName1 = "C7548-folder1-" + random;
        String folderName2 = "C7548-folder2-" + random;
        String textFile = "C7548-textDocument-" + random;
        String htmlFile = "C7548-htmlFile-" + random;
        String xmlFile = "C7548-xmlFile-" + random;
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName1, siteName);
        contentService.createFolder(userName, password, folderName2, siteName);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, textFile, fileContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.HTML, htmlFile, fileContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.XML, xmlFile, fileContent);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("STEP1: Click 'Select' button and choose 'Documents'");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("Documents");
        ArrayList<String> expectedContentList1 = new ArrayList<>(Arrays.asList(textFile, htmlFile, xmlFile));
        assertEquals(documentLibraryPage.verifyContentItemsSelected(expectedContentList1), expectedContentList1.toString(), "Selected content=");
        ArrayList<String> expectedContentList2 = new ArrayList<>(Arrays.asList(folderName1, folderName2));
        assertEquals(documentLibraryPage.verifyContentItemsNotSelected(expectedContentList2), expectedContentList2.toString(), "Content not selected=");
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP2: Click 'Selected Items...'");
        headerMenuBar.clickSelectedItemsMenu();
        ArrayList<String> expectedSelectedItemsOptions1 = new ArrayList<>(
                Arrays.asList("Download as Zip", "Copy to...", "Move to...", "Start Workflow...", "Delete", "Deselect All"));
        assertEquals(headerMenuBar.verifySelectedItemsValues(expectedSelectedItemsOptions1), expectedSelectedItemsOptions1.toString(),
                "'Selected Items...' options=");

        LOG.info("STEP3: Click 'Select' button and choose 'Folders'");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("Folders");
        ArrayList<String> expectedContentList3 = new ArrayList<>(Arrays.asList(folderName1, folderName2));
        assertEquals(documentLibraryPage.verifyContentItemsSelected(expectedContentList3), expectedContentList3.toString(), "Selected content=");
        ArrayList<String> expectedContentList4 = new ArrayList<>(Arrays.asList(textFile, htmlFile, xmlFile));
        assertEquals(documentLibraryPage.verifyContentItemsNotSelected(expectedContentList4), expectedContentList4.toString(), "Content not selected=");
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP4: Click 'Selected Items...'");
        headerMenuBar.clickSelectedItemsMenu();
        ArrayList<String> expectedSelectedItemsOptions2 = new ArrayList<>(
                Arrays.asList("Download as Zip", "Copy to...", "Move to...", "Delete", "Deselect All"));
        assertEquals(headerMenuBar.verifySelectedItemsValues(expectedSelectedItemsOptions2), expectedSelectedItemsOptions2.toString(),
                "'Selected Items...' options=");

        LOG.info("STEP5: Unselect folder2 and select text1 by clicking on their corresponding check-boxes.");
        documentLibraryPage.clickCheckBox(folderName2);
        documentLibraryPage.clickCheckBox(textFile);
        ArrayList<String> expectedContentList5 = new ArrayList<>(Arrays.asList(folderName1, textFile));
        assertEquals(documentLibraryPage.verifyContentItemsSelected(expectedContentList5), expectedContentList5.toString(), "Selected content=");
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP6: Click 'Selected Items...' menu");
        headerMenuBar.clickSelectedItemsMenu();
        ArrayList<String> expectedSelectedItemsOptions3 = new ArrayList<>(
                Arrays.asList("Download as Zip", "Copy to...", "Move to...", "Delete", "Deselect All"));
        assertEquals(headerMenuBar.verifySelectedItemsValues(expectedSelectedItemsOptions3), expectedSelectedItemsOptions3.toString(),
                "'Selected Items...' options=");

        LOG.info("STEP7: Click 'Select' button and choose 'Invert Selection' option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("Invert Selection");
        ArrayList<String> expectedContentList6 = new ArrayList<>(Arrays.asList(folderName2, htmlFile, xmlFile));
        assertEquals(documentLibraryPage.verifyContentItemsSelected(expectedContentList6), expectedContentList6.toString(), "Selected content=");
        ArrayList<String> expectedContentList7 = new ArrayList<>(Arrays.asList(folderName1, textFile));
        assertEquals(documentLibraryPage.verifyContentItemsNotSelected(expectedContentList7), expectedContentList7.toString(), "Content not selected=");
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP8: Click 'Select' button and choose 'None'");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("None");
        ArrayList<String> expectedContentList8 = new ArrayList<>(Arrays.asList(folderName1, folderName2, textFile, htmlFile, xmlFile));
        assertEquals(documentLibraryPage.verifyContentItemsNotSelected(expectedContentList8), expectedContentList8.toString(), "Content not selected=");
        assertFalse(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP9: Click 'Select' button and choose 'All'");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("All");
        ArrayList<String> expectedContentList9 = new ArrayList<>(Arrays.asList(folderName1, folderName2, textFile, htmlFile, xmlFile));
        assertEquals(documentLibraryPage.verifyContentItemsSelected(expectedContentList9), expectedContentList9.toString(), "Content not selected=");
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");
    }

    @TestRail(id = "C8410")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void selectMultipleDocumentsStartWorkflow()
    {
        String random = RandomData.getRandomAlphanumeric();
        String siteName = String.format("C8410-site-%s", random);
        String folderName1 = "C8410-folder1-" + random;
        String folderName2 = "C8410-folder2-" + random;
        String textFile = "C8410-textDocument-" + random;
        String htmlFile = "C8410-htmlFile-" + random;
        String xmlFile = "C8410-xmlFile-" + random;

        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName1, siteName);
        contentService.createFolder(userName, password, folderName2, siteName);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, textFile, fileContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.HTML, htmlFile, fileContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.XML, xmlFile, fileContent);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("STEP1: Click 'Select' menu  and choose 'Documents' option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("Documents");
        ArrayList<String> expectedContentList1 = new ArrayList<>(Arrays.asList(textFile, htmlFile, xmlFile));
        assertEquals(documentLibraryPage.verifyContentItemsSelected(expectedContentList1), expectedContentList1.toString(), "Selected content=");
        ArrayList<String> expectedContentList2 = new ArrayList<>(Arrays.asList(folderName1, folderName2));
        assertEquals(documentLibraryPage.verifyContentItemsNotSelected(expectedContentList2), expectedContentList2.toString(), "Content not selected=");
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP2: Click 'Selected Items...' menu and choose 'Start Workflow...' option");
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption(language.translate("documentLibrary.contentActions.startWorkflow"));
        startWorkflowPage.renderedPage();
        assertEquals(startWorkflowPage.getPageTitle(), "Alfresco » Start Workflow", "Page displayed=");

        LOG.info("STEP3: Select 'New Task' option from 'Workflow:' dropdown");
        startWorkflowPage.selectAWorkflow("New Task");
        startWorkflowPage.renderedPage();
        ArrayList<String> expectedItemsList = new ArrayList<>(Arrays.asList(htmlFile, textFile, xmlFile));
        assertEquals(startWorkflowPage.getItemsList(), expectedItemsList.toString(), "Start Workflow Page- 'Items' list=");
    }

    @TestRail(id = "C7554")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void selectedItemsDelete()
    {
        String random = RandomData.getRandomAlphanumeric();
        String siteName = String.format("C8410-site-%s", random);
        String folderName1 = "C8410-folder1-" + random;
        String textFile = "C8410-textDocument-" + random;
        String htmlFile = "C8410-htmlFile-" + random;
        String xmlFile = "C8410-xmlFile-" + random;

        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName1, siteName);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, textFile, fileContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.HTML, htmlFile, fileContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.HTML, xmlFile, fileContent);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("STEP1: Click 'Select' menu and choose 'All' option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("All");
        ArrayList<String> expectedContentList1 = new ArrayList<>(Arrays.asList(folderName1, textFile, htmlFile, xmlFile));
        assertEquals(documentLibraryPage.verifyContentItemsSelected(expectedContentList1), expectedContentList1.toString(), "Selected content=");
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP2: Click 'Selected Items...' menu and choose 'Delete' option");
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption("Delete");
        ArrayList<String> expectedContentList2 = new ArrayList<>(Arrays.asList(folderName1 + "\n", htmlFile + "\n", textFile + "\n", xmlFile));
        String expectedItemsString = expectedContentList2.toString().replace(", ", "").replace("[", "").replace("]", "");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmMultipleDeleteDialog.message"), 4, expectedItemsString),
                "'Confirm Multiple Delete' dialog message=");

        LOG.info("STEP3: Click 'Cancel' button");
        deleteDialog.clickCancel();
        ArrayList<String> expectedContentList3 = new ArrayList<>(Arrays.asList(folderName1, textFile, htmlFile, xmlFile));
        assertEquals(documentLibraryPage.verifyContentItemsSelected(expectedContentList3), expectedContentList3.toString(), "Selected content=");
        assertTrue(headerMenuBar.isSelectedItemsMenuEnabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP4: Click 'Selected Items...' menu and choose 'Delete' option");
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption("Delete");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmMultipleDeleteDialog.message"), 4, expectedItemsString),
                "'Confirm Multiple Delete' dialog message=");

        LOG.info("STEP5: Click 'Delete' button");
        deleteDialog.clickDelete();
        expectedContentList3.clear();
        documentLibraryPage.renderedPage();
        assertEquals(documentLibraryPage.getFoldersList().toString(), expectedContentList3.toString(), "Document Library - displayed folders=");
        assertEquals(documentLibraryPage.getFilesList().toString(), expectedContentList3.toString(), "Document Library - displayed files=");
    }
}