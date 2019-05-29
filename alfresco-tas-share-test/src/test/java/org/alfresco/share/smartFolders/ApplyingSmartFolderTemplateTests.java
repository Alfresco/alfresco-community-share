package org.alfresco.share.smartFolders;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.SmartFolders;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.testng.Assert.assertTrue;

public class ApplyingSmartFolderTemplateTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    AspectsForm aspectsForm;

    @Autowired
    EditPropertiesDialog editPropertiesDialog;

    @Autowired
    EditPropertiesPage editPropertiesPage;

    @Autowired
    NewContentDialog newContentDialog;

    @Autowired
    SmartFolders smartFolders;

    @Autowired
    UploadContent uploadContent;

    @Autowired
    RepositoryPage repositoryPage;

    @Autowired
    SelectDialog selectDialog;

    private String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    private String siteNameC8665 = String.format("SiteNameC8665%s", RandomData.getRandomAlphanumeric());
    private String siteNameC8666 = String.format("SiteNameC8666%s", RandomData.getRandomAlphanumeric());
    private String siteNameC8668 = String.format("SiteNameC8668%s", RandomData.getRandomAlphanumeric());
    private String fileName1 = "testFile1";
    private String fileName2 = "testFile2";
    private String fileName3 = "testFile3";
    private String fileContent = "testContent";
    private String folderName = "testFolder";
    private String filesPath = String.format("Sites/%s/documentLibrary/%s", siteNameC8665, folderName);
    private String mainSmartFolder = "My content";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteNameC8665, siteNameC8665, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteNameC8666, siteNameC8666, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteNameC8668, siteNameC8668, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteNameC8665);
        contentService.createFolder(userName, password, folderName, siteNameC8668);
        contentService.createDocumentInRepository(userName, password, filesPath, DocumentType.TEXT_PLAIN, fileName1, fileContent);
        contentService.createDocumentInRepository(userName, password, filesPath, DocumentType.TEXT_PLAIN, fileName2, fileContent);
        contentService.createDocumentInRepository(userName, password, filesPath, DocumentType.TEXT_PLAIN, fileName3, fileContent);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

        siteService.delete(adminUser, adminPassword, siteNameC8665);
        siteService.delete(adminUser, adminPassword, siteNameC8666);
        siteService.delete(adminUser, adminPassword, siteNameC8668);
    }


    @TestRail (id = "C8665")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "SmartFolders" })
    public void applySFTemplateToExistingFolder()
    {
        LOG.info("Preconditions: Navigate to Document Library for the page for the test site");
        documentLibraryPage.navigate(siteNameC8665);

        LOG.info("Step1: Click Actions -> Manage Aspects and verify Manage Aspects form");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel diaplyed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedPanel(), "Currently Selected panel diaplyed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "'Apply Changes' button displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "'Cancel' button displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "'Close' button displayed");

        LOG.info("Step2: Click 'Add' button next to 'System Smart Folder' template and verify it moves to 'Currently Selected'");
        aspectsForm.addAspect("System Smart Folder");
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("System Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        LOG.info("Step3: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton(documentLibraryPage);

        LOG.info("Step4: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editPropertiesDialog);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        LOG.info("Step5: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        Assert.assertTrue(editPropertiesPage.arePropertiesDisplayed("Name", "Title", "Description", "Tags", "Smart Folder Template"), "Displayed properties");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Save"), "Save button displayed");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Cancel"), "Cancel button displayed");

        LOG.info("Step6: Select 'smartFoldersExample.json template' and save.");
        editPropertiesPage.selectSFTemplate(0);
        editPropertiesPage.clickButtonForFolder("Save");

        LOG.info("Step7: Click on the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileName1), "File1 displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileName2), "File2 displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileName3), "File3 displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(mainSmartFolder), "The main smart folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(1), "The smart folder icon displayed");

    }

    @TestRail (id = "C8666")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "SmartFolders" })
    public void applySFTemplateToCreatedFolder()
    {
        LOG.info("Preconditions: Navigate to Document Library for the page for the test site");
        documentLibraryPage.navigate(siteNameC8666);

        LOG.info("Step1: Click on 'Create' button and choose 'Folder'");
        documentLibraryPage.clickCreateButton();
        documentLibraryPage.clickFolderLink();
        assertTrue(newContentDialog.isNameFieldDisplayed(), "'Name' field displayed.");
        assertTrue(newContentDialog.isMandatoryIndicatorDisplayed(), "'Name' mandatory field.");
        assertTrue(newContentDialog.isTitleFieldDisplayed(), "'Title' field displayed.");
        assertTrue(newContentDialog.isDescriptionFieldDisplayed(), "'Description' field displayed.");
        assertTrue(newContentDialog.isSaveButtonDisplayed(), "'Save' button displayed.");
        assertTrue(newContentDialog.isCancelButtonDisplayed(), "'Cancel' button displayed.");

        LOG.info("Step2: Input 'Name', 'Title', 'Description' and click 'Save'");
        newContentDialog.fillInDetails(folderName, "Title", "Description");
        newContentDialog.clickSaveButton();
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), folderName + " displayed in Documents list.");

        LOG.info("Step3: Hover over folder, click More -> Manage Aspects");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);

        LOG.info("Step4: Click 'Add' button next to 'System Smart Folder' template and verify it moves to 'Currently Selected'");
        aspectsForm.addAspect("System Smart Folder");
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("System Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        LOG.info("Step5: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton(documentLibraryPage);

        LOG.info("Step6: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editPropertiesDialog);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        LOG.info("Step7: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        Assert.assertTrue(editPropertiesPage.arePropertiesDisplayed("Name", "Title", "Description", "Tags", "Smart Folder Template"), "Displayed properties");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Save"), "Save button displayed");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Cancel"), "Cancel button displayed");

        LOG.info("Step8: Select 'smartFoldersExample.json template' and save.");
        editPropertiesPage.selectSFTemplate(0);
        editPropertiesPage.clickButtonForFolder("Save");

        LOG.info("Step9: Click on the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(mainSmartFolder), "The main smart folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(1), "The smart folder icon displayed");

        LOG.info("Step10: Click on 'My content' the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(mainSmartFolder);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("All site content"), "'All site content' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("This folder's content"), "'This folder's content' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Contributions"), "'Contributions' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("My content modified by other users"),
            "'My content modified by other users' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("User home"), "'User home' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Tagged 'Confidential'"), "'Tagged 'Confidential'' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("My Categorized Files"), "'My Categorized Files' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Recent updates"), "'Recent updates' folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(8), "SF icon displayed for all folders");

    }

    @TestRail (id = "C8668")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "SmartFolders" })
    public void applyCustomSmartFolder()
    {
        String customSmartFolderTemplate = "employeeSmartSimpleTemplate.json";
        String customSmartFolderTemplatePath = testDataFolder + customSmartFolderTemplate;

        LOG.info("Preconditions: Upload '.json file for custom smart folder' and navigate to Document Library for the page for the test site");
        setupAuthenticatedSession(adminUser, adminPassword);
        repositoryPage.navigate();
        uploadContent.uploadContent(customSmartFolderTemplatePath);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteNameC8668);

        LOG.info("Step1: Click Actions -> Manage Aspects and verify Manage Aspects form");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);

        LOG.info("Step2: Click 'Add' button next to 'Custom Smart Folder' template and verify it moves to 'Currently Selected'");
        aspectsForm.addAspect("Custom Smart Folder");
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Custom Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        LOG.info("Step3: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton(documentLibraryPage);

        LOG.info("Step4: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editPropertiesDialog);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        LOG.info("Step5: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        Assert.assertTrue(editPropertiesPage.arePropertiesDisplayed("Name", "Title", "Description", "Tags", "Smart Folder Template"), "Displayed properties");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Save"), "Save button displayed");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Cancel"), "Cancel button displayed");

        LOG.info("Step6: Select '.json' file for the custom folder template and save.");
        editPropertiesPage.clickSelectButtonForCustomSmartFolder();
        selectDialog.selectItems(Collections.singletonList("employeeSmartSimpleTemplate.json"));
        selectDialog.clickOkAndRenderPropertiesPage();
        editPropertiesPage.clickButtonForFolder("Save");

        LOG.info("Step7: Click on the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("01 Administrative"), "'01 Administrative' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("02 Legal"), "'02 Legal' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("03 Personal"), "'03 Personal' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("04 Other documents"), "'04 Other documents' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("05 PDF Documents in path"), "'05 PDF Documents in path' folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(5), "SF icon displayed for all folders");

    }

}
