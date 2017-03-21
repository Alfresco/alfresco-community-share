package org.alfresco.share.smartFolders;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.SmartFolders;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;

public class UsingSmartFoldersTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    AspectsForm aspectsForm;

    @Autowired
    EditPropertiesDialog editPropertiesDialog;

    @Autowired
    EditPropertiesPage editPropertiesPage;

    @Autowired
    SmartFolders smartFolders;

    @Autowired
    UploadContent uploadContent;

    @Autowired
    GoogleDocsCommon googleDocs;

    @Autowired
    CreateContent createContent;

    private String userName;
    private String siteName;
    private String folderName = "testFolder";
    private String mainSmartFolder = "My content";
    private String testFileName = "test.pdf";
    private String testFilePath;
    private String newVersionFileName = "EditedTestFile8650.docx";
    private String newVersionFilePath;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userName = "User" + DataUtil.getUniqueIdentifier();
        siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        testFilePath = testDataFolder + testFileName;
        newVersionFilePath = testDataFolder + newVersionFileName;
        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteName);
        setupAuthenticatedSession(userName, password);
    }

    @TestRail(id = "C8646")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void smartFolderIcon()

    {

        logger.info("Preconditions: Navigate to Document Library for the page for the test site");
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: Verify that the new created folder has no magnifying glass next to it.");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(0), "SF icon missing");

        logger.info("Step2: Hover over folder and click More -> Manage Aspects.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel diaplyed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedtPanel(), "Currently Selected panel diaplyed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "'Apply Changes' button displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "'Cancel' button displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "'Close' button displayed");

        logger.info("Step3: Click Add button next to System Smart Folder template.");
        aspectsForm.addElement(17);
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("System Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        logger.info("Step4: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton();
        getBrowser().waitInSeconds(1);

        logger.info("Step5: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editPropertiesDialog);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        logger.info("Step6: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        ArrayList<String> properties = new ArrayList<>(Arrays.asList("Name", "Title", "Description", "Owner", "Tags", "Smart Folder Template"));
        Assert.assertEquals(editPropertiesPage.checkDisplayedProperties(properties), properties.toString());
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Save"), "Save button displayed");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Cancel"), "Cancel button displayed");

        logger.info("Step7: Select 'smartFoldersExample.json template' and save.");
        editPropertiesPage.selectSFTemplate(0);
        editPropertiesPage.clickButtonForFolder("Save");

        logger.info("Step8: Click on the folder and verify it has 'Smart Folder' structure under it");
        getBrowser().waitInSeconds(1);
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(mainSmartFolder), "The main smart folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(1), "The smart folder icon displayed");

    }

    @TestRail(id = "C8648")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void addFileToSmartFolder()

    {
        logger.info("Preconditions: Navigate to Document Library for the page for the test site");
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: Hover over folder and click More -> Manage Aspects.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel diaplyed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedtPanel(), "Currently Selected panel diaplyed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "'Apply Changes' button displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "'Cancel' button displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "'Close' button displayed");

        logger.info("Step2: Click Add button next to System Smart Folder template.");
        aspectsForm.addElement(17);
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("System Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        logger.info("Step3: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton();
        getBrowser().waitInSeconds(1);
        // getBrowser().refresh();

        logger.info("Step4: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editPropertiesDialog);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        logger.info("Step5: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        ArrayList<String> properties = new ArrayList<>(Arrays.asList("Name", "Title", "Description", "Owner", "Tags", "Smart Folder Template"));
        Assert.assertEquals(editPropertiesPage.checkDisplayedProperties(properties), properties.toString());
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Save"), "Save button displayed");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Cancel"), "Cancel button displayed");

        logger.info("Step6: Select 'smartFoldersExample.json template' and save.");
        editPropertiesPage.selectSFTemplate(0);
        editPropertiesPage.clickButtonForFolder("Save");

        logger.info("Step7: Click on the folder and verify it has 'Smart Folder' structure under it");
        getBrowser().waitInSeconds(1);
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(mainSmartFolder), "The main smart folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(1), "The smart folder icon displayed");

        logger.info("Steps8, 9: Click Upload button and select a pdf file.");
        uploadContent.uploadContent(testFilePath);

        logger.info("Step10: Go to My Content -> All site content -> Documents -> PDF Documents and verify that the uploaded file is displayed");
        documentLibraryPage.clickOnFolderName("My content");
        documentLibraryPage.clickOnFolderName("All site content");
        documentLibraryPage.clickOnFolderName("Documents");
        documentLibraryPage.clickOnFolderName("PDF Documents");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("test.pdf"), "The uploaded file displayed in PDF Documents list");
    }

    @TestRail(id = "C8649")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void createFileInSmartFolder() throws Exception

    {

        logger.info("Preconditions: Navigate to Document Library for the page for the test site");
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: Hover over folder and click More -> Manage Aspects.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel diaplyed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedtPanel(), "Currently Selected panel diaplyed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "'Apply Changes' button displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "'Cancel' button displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "'Close' button displayed");

        logger.info("Step2: Click Add button next to System Smart Folder template.");
        aspectsForm.addElement(17);
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("System Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        logger.info("Step3: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton();
        getBrowser().waitInSeconds(2);

        logger.info("Step4: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editPropertiesDialog);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        logger.info("Step5: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        ArrayList<String> properties = new ArrayList<>(Arrays.asList("Name", "Title", "Description", "Owner", "Tags", "Smart Folder Template"));
        Assert.assertEquals(editPropertiesPage.checkDisplayedProperties(properties), properties.toString());
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Save"), "Save button displayed");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Cancel"), "Cancel button displayed");

        logger.info("Step6: Select 'smartFoldersExample.json template' and save.");
        editPropertiesPage.selectSFTemplate(0);
        editPropertiesPage.clickButtonForFolder("Save");

        logger.info("Step7: Click on the folder and verify it has 'Smart Folder' structure under it");
        getBrowser().waitInSeconds(1);
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(mainSmartFolder), "The main smart folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(1), "The smart folder icon displayed");

        logger.info("Step8: Press Create button -> Google Docs Document");
        googleDocs.loginToGoogleDocs();
        documentLibraryPage.clickCreateButton();
        createContent.clickGoogleDocsDoc();
        getBrowser().waitInSeconds(1);
        googleDocs.clickTheOkButtonOnTheAuthorizeWithGoogleDocsPopup();
        getBrowser().waitInSeconds(5);

        logger.info("Step9: Input some texts in this Google Doc file and exit.");
        googleDocs.switchToGoogleDocsWindowandAndEditContent("Test", "Test Content");

        logger.info("Step10: Hover over document and press Check in Google Doc.");
        googleDocs.checkInGoogleDoc("Untitled Document");
        getBrowser().waitInSeconds(10);

        logger.info("Step11: Go to My Content -> All site content -> Documents -> Office Documents and verify the created file is displayed");
        documentLibraryPage.clickOnFolderName("My content");
        documentLibraryPage.clickOnFolderName("All site content");
        documentLibraryPage.clickOnFolderName("Documents");
        documentLibraryPage.clickOnFolderName("Office Documents");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Test.docx"), "The uploaded file displayed in Office Documents list");
    }

    @TestRail(id = "C8650")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void updateFileInSmartFolder() throws Exception
    {
        logger.info("Preconditions: Navigate to Document Library for the page for the test site");
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: Hover over folder and click More -> Manage Aspects.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel diaplyed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedtPanel(), "Currently Selected panel diaplyed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "'Apply Changes' button displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "'Cancel' button displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "'Close' button displayed");

        logger.info("Step2: Click Add button next to System Smart Folder template.");
        aspectsForm.addElement(17);
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("System Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        logger.info("Step3: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton();
        getBrowser().waitInSeconds(2);

        logger.info("Step4: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editPropertiesDialog);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        logger.info("Step5: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        ArrayList<String> properties = new ArrayList<>(Arrays.asList("Name", "Title", "Description", "Owner", "Tags", "Smart Folder Template"));
        Assert.assertEquals(editPropertiesPage.checkDisplayedProperties(properties), properties.toString());
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Save"), "Save button displayed");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Cancel"), "Cancel button displayed");

        logger.info("Step6: Select 'smartFoldersExample.json template' and save.");
        editPropertiesPage.selectSFTemplate(0);
        editPropertiesPage.clickButtonForFolder("Save");
        getBrowser().waitInSeconds(1);

        logger.info("Step7: Click on the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(mainSmartFolder), "The main smart folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(1), "The smart folder icon displayed");

        logger.info("Step8: Press Create button -> Google Docs Document");
        googleDocs.loginToGoogleDocs();
        documentLibraryPage.clickCreateButton();
        createContent.clickGoogleDocsDoc();
        getBrowser().waitInSeconds(1);
        googleDocs.clickTheOkButtonOnTheAuthorizeWithGoogleDocsPopup();
        getBrowser().waitInSeconds(5);

        logger.info("Step9: Input some texts in this Google Doc file and exit.");
        googleDocs.switchToGoogleDocsWindowandAndEditContent("Test", "Test Content");

        logger.info("Step10: Hover over document and press Check in Google Doc.");
        getBrowser().waitInSeconds(2);
        googleDocs.checkInGoogleDoc("Untitled Document");
        getBrowser().waitInSeconds(10);

        logger.info("Step11: Go to My Content -> All site content -> Documents -> Office Documents and verify the created file is displayed");
        documentLibraryPage.clickOnFolderName("My content");
        documentLibraryPage.clickOnFolderName("All site content");
        documentLibraryPage.clickOnFolderName("Documents");
        documentLibraryPage.clickOnFolderName("Office Documents");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Test.docx"), "The uploaded file displayed in Office Documents list");

        logger.info("Step12: Hover over the created file and click 'Upload new version'");
        documentLibraryPage.clickDocumentLibraryItemAction("Test.docx", "Upload New Version", uploadContent);
        uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Minor);
        getBrowser().waitInSeconds(2);

        logger.info("Step13: Verify the document is updated and the version is increased");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(newVersionFileName), "The uploaded file displayed in Office Documents list");
        documentLibraryPage.clickOnFile(newVersionFileName);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Content of %s is wrong.", newVersionFileName));
        assertEquals(documentDetailsPage.getFileVersion(), "1.1", String.format("Version of %s is wrong.", newVersionFileName));

    }

    @TestRail(id = "C8663")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void checkSmartFolderStructure()

    {

        logger.info("Preconditions: Navigate to Document Library for the page for the test site");
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: Click Actions -> Manage Aspects and verify Manage Aspects form");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel diaplyed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedtPanel(), "Currently Selected panel diaplyed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "'Apply Changes' button displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "'Cancel' button displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "'Close' button displayed");

        logger.info("Step2: Click 'Add' button next to 'System Smart Folder' template and verify it moves to moves to 'Currently Selected'");
        aspectsForm.addElement(17);
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("System Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        logger.info("Step3: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton();
        getBrowser().waitInSeconds(2);

        logger.info("Step4: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editPropertiesDialog);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        logger.info("Step5: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        ArrayList<String> properties = new ArrayList<>(Arrays.asList("Name", "Title", "Description", "Owner", "Tags", "Smart Folder Template"));
        Assert.assertEquals(editPropertiesPage.checkDisplayedProperties(properties), properties.toString());
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Save"), "Save button displayed");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Cancel"), "Cancel button displayed");

        logger.info("Step6: Select 'smartFoldersExample.json template' and save.");
        editPropertiesPage.selectSFTemplate(0);
        editPropertiesPage.clickButtonForFolder("Save");

        logger.info("Step7: Click on the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(mainSmartFolder), "The main smart folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(1), "The smart folder icon displayed");

        logger.info("Step8: Click on 'My content' the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(mainSmartFolder);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("All site content"), "'All site content' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Contributions"), "'Contributions' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("My content modified by other users"),
                "'My content modified by other users' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("User home"), "'User home' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Tagged 'Confidential'"), "'Tagged 'Confidential'' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("My Categorized Files"), "'My Categorized Files' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Recent updates"), "'Recent updates' folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(8), "SF icon displayed for all folders");
    }

    @TestRail(id = "C8664")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void checkFilesAreCorrectlyFilled() throws Exception
    {

        logger.info("Preconditions: Navigate to Document Library for the page for the test site");
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: Click Actions -> Manage Aspects and verify Manage Aspects form");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel diaplyed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedtPanel(), "Currently Selected panel diaplyed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "'Apply Changes' button displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "'Cancel' button displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "'Close' button displayed");

        logger.info("Step2: Click 'Add' button next to 'System Smart Folder' template and verify it moves to moves to 'Currently Selected'");
        aspectsForm.addElement(17);
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("System Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        logger.info("Step3: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton();
        getBrowser().waitInSeconds(2);

        logger.info("Step4: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editPropertiesDialog);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        logger.info("Step5: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        ArrayList<String> properties = new ArrayList<>(Arrays.asList("Name", "Title", "Description", "Owner", "Tags", "Smart Folder Template"));
        Assert.assertEquals(editPropertiesPage.checkDisplayedProperties(properties), properties.toString());
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Save"), "Save button displayed");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Cancel"), "Cancel button displayed");

        logger.info("Step6: Select 'smartFoldersExample.json template' and save.");
        editPropertiesPage.selectSFTemplate(0);
        editPropertiesPage.clickButtonForFolder("Save");

        logger.info("Step7: Click on the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(mainSmartFolder), "The main smart folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(1), "The smart folder icon displayed");

        logger.info("Step8: Press Create button -> Google Docs Document");
        googleDocs.loginToGoogleDocs();
        documentLibraryPage.clickCreateButton();
        createContent.clickGoogleDocsSpreadsheet();
        getBrowser().waitInSeconds(1);
        googleDocs.clickTheOkButtonOnTheAuthorizeWithGoogleDocsPopup();
        getBrowser().waitInSeconds(5);

        logger.info("Step9: Input some texts in this Google Doc file and exit.");
        googleDocs.switchToGoogleSheetsWindowandAndEditContent("Test", "Test Content");

        logger.info("Step10: Hover over document and press Check in Google Doc.");
        documentLibraryPage.renderedPage();
        googleDocs.checkInGoogleDoc("Untitled Spreadsheet.xlsx");
        getBrowser().waitInSeconds(10);

        logger.info("Step11: Go to My Content -> All site content -> Documents -> Office Documents and verify the created file is displayed");
        documentLibraryPage.clickOnFolderName("My content");
        documentLibraryPage.clickOnFolderName("All site content");
        documentLibraryPage.clickOnFolderName("Documents");
        documentLibraryPage.clickOnFolderName("Office Documents");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Test.xlsx"), "The uploaded file displayed in Office Documents list");

    }

    @TestRail(id = "C8647")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void checkAvailableActions() throws Exception

    {

        logger.info("Preconditions: Navigate to Document Library for the page for the test site");
        documentLibraryPage.navigate(siteName);

        logger.info("Step1: Hover over folder and click More -> Manage Aspects.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel diaplyed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedtPanel(), "Currently Selected panel diaplyed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "'Apply Changes' button displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "'Cancel' button displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "'Close' button displayed");

        logger.info("Step2: Click Add button next to System Smart Folder template.");
        aspectsForm.addElement(17);
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("System Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        logger.info("Step3: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton();
        getBrowser().waitInSeconds(2);

        logger.info("Step4: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editPropertiesDialog);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");
        logger.info("Step5: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        ArrayList<String> properties = new ArrayList<>(Arrays.asList("Name", "Title", "Description", "Owner", "Tags", "Smart Folder Template"));
        Assert.assertEquals(editPropertiesPage.checkDisplayedProperties(properties), properties.toString());
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Save"), "Save button displayed");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Cancel"), "Cancel button displayed");

        logger.info("Step6: Select 'smartFoldersExample.json template' and save.");
        editPropertiesPage.selectSFTemplate(0);
        editPropertiesPage.clickButtonForFolder("Save");

        logger.info("Step7: Click on the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(folderName);
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(mainSmartFolder), "The main smart folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(1), "The smart folder icon displayed");

        logger.info("Step8: Verify that 'Create' button is available");
        Assert.assertTrue(documentLibraryPage.isCreateButtonDisplayed(), "Create button displayed");
        Assert.assertTrue(documentLibraryPage.isCreateButtonDisplayed(), "Create button enabled");

        logger.info("Step9: Press Create button -> Google Docs Document");
        googleDocs.loginToGoogleDocs();
        documentLibraryPage.clickCreateButton();
        createContent.clickGoogleDocsDoc();
        getBrowser().waitInSeconds(1);
        googleDocs.clickTheOkButtonOnTheAuthorizeWithGoogleDocsPopup();
        getBrowser().waitInSeconds(5);

        logger.info("Step10: Input some texts in this Google Doc file and exit.");
        googleDocs.switchToGoogleDocsWindowandAndEditContent("Test", "Test Content");

        logger.info("Step11: Hover over document and press Check in Google Doc.");
        getBrowser().waitInSeconds(2);
        googleDocs.checkInGoogleDoc("Untitled Document");
        getBrowser().waitInSeconds(10);

        logger.info("Step12: Go to My Content -> All site content -> Documents -> Office Documents and verify the created file is displayed");
        documentLibraryPage.clickOnFolderName("My content");
        documentLibraryPage.clickOnFolderName("All site content");
        documentLibraryPage.clickOnFolderName("Documents");
        documentLibraryPage.clickOnFolderName("Office Documents");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Test.docx"), "The uploaded file displayed in Office Documents list");

        logger.info("Step13: Hover over the created document and check available actions");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem("Test.docx", "Download"), "Download action available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem("Test.docx", "View In Browser"), "View In Browser action available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem("Test.docx", "Edit Properties"), "Edit Properties action available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem("Test.docx", "Upload New Version"), "Upload New Version action available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem("Test.docx", "Edit in Microsoft Office™"),
                "Edit in Microsoft Office™ action available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem("Test.docx", "Edit Offline"), "Edit Offline action available");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem("Test.docx", "Start Workflow"), "Start Workflow action available");

        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem("Test.docx", "Delete Document"), "Delete action available");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem("Test.docx", "Unzip to..."), "Unzip To action available");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem("Test.docx", "Sync to Cloud"), "Sync action available");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem("Test.docx", "Locate File"), "Locate To action available");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem("Test.docx", "Move to..."), "Move action available");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem("Test.docx", "Copy to..."), "Copy action available");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem("Test.docx", "Manage permissions"), "Copy action available");

    }
}
