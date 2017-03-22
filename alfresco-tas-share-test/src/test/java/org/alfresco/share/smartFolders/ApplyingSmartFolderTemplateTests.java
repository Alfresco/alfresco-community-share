package org.alfresco.share.smartFolders;

import org.alfresco.common.DataUtil;
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
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

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

    private String userName;
    private String siteNameC8665;
    private String siteNameC8666;
    private String siteNameC8668;
    private String fileName1;
    private String fileName2;
    private String fileName3;
    private String fileContent;
    private String folderName;
    private String filesPath;
    private String mainSmartFolder;

    @BeforeClass(alwaysRun = true)
    public void setupTest()

    {
        userName = "User" + DataUtil.getUniqueIdentifier();
        siteNameC8665 = "SiteNameC8665" + DataUtil.getUniqueIdentifier();
        siteNameC8666 = "SiteNameC8666" + DataUtil.getUniqueIdentifier();
        siteNameC8668 = "SiteNameC8668" + DataUtil.getUniqueIdentifier();
        fileName1 = "testFile1";
        fileName2 = "testFile2";
        fileName3 = "testFile3";
        mainSmartFolder = "My content";
        fileContent = "testContent";
        folderName = "testFolder";
        filesPath = "Sites/" + siteNameC8665 + "/" + "documentLibrary" + "/" + folderName;
        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        siteService.create(userName, password, domain, siteNameC8665, siteNameC8665, Site.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteNameC8666, siteNameC8666, Site.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteNameC8668, siteNameC8668, Site.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteNameC8665);
        contentService.createFolder(userName, password, folderName, siteNameC8668);
        contentService.createDocumentInRepository(userName, password, filesPath, DocumentType.TEXT_PLAIN, fileName1, fileContent);
        contentService.createDocumentInRepository(userName, password, filesPath, DocumentType.TEXT_PLAIN, fileName2, fileContent);
        contentService.createDocumentInRepository(userName, password, filesPath, DocumentType.TEXT_PLAIN, fileName3, fileContent);
        setupAuthenticatedSession(userName, password);
    }

    @TestRail(id = "C8665")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void applySFTemplateToExistingFolder()
    {

        logger.info("Preconditions: Navigate to Document Library for the page for the test site");
        documentLibraryPage.navigate(siteNameC8665);

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
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("System Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        logger.info("Step3: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton();
        getBrowser().refresh();

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
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileName1), "File1 displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileName2), "File2 displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileName3), "File3 displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(mainSmartFolder), "The main smart folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(1), "The smart folder icon displayed");

    }

    @TestRail(id = "C8666")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void applySFTemplateToCreatedFolder()
    {
        logger.info("Preconditions: Navigate to Document Library for the page for the test site");
        documentLibraryPage.navigate(siteNameC8666);

        logger.info("Step1: Click on 'Create' button and choose 'Folder'");
        documentLibraryPage.clickCreateButton();
        documentLibraryPage.clickFolderLink();
        assertTrue(newContentDialog.isNameFieldDisplayed(), "'Name' field displayed.");
        assertTrue(newContentDialog.isMandatoryIndicatorDisplayed(), "'Name' mandatory field.");
        assertTrue(newContentDialog.isTitleFieldDisplayed(), "'Title' field displayed.");
        assertTrue(newContentDialog.isDescriptionFieldDisplayed(), "'Description' field displayed.");
        assertTrue(newContentDialog.isSaveButtonDisplayed(), "'Save' button displayed.");
        assertTrue(newContentDialog.isCancelButtonDisplayed(), "'Cancel' button displayed.");

        logger.info("Step2: Input 'Name', 'Title', 'Description' and click 'Save'");
        newContentDialog.fillInDetails(folderName, "Title", "Description");
        newContentDialog.clickSaveButton();
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), folderName + " displayed in Documents list.");

        logger.info("Step3: Hover over folder, click More -> Manage Aspects and verify the displayed elements");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel diaplyed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedtPanel(), "Currently Selected panel diaplyed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "'Apply Changes' button displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "'Cancel' button displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "'Close' button displayed");

        logger.info("Step4: Click 'Add' button next to 'System Smart Folder' template and verify it moves to moves to 'Currently Selected'");
        aspectsForm.addElement(17);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("System Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        logger.info("Step5: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton();
        getBrowser().refresh();

        logger.info("Step6: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editPropertiesDialog);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        logger.info("Step7: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        ArrayList<String> properties = new ArrayList<>(Arrays.asList("Name", "Title", "Description", "Owner", "Tags", "Smart Folder Template"));
        Assert.assertEquals(editPropertiesPage.checkDisplayedProperties(properties), properties.toString());
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Save"), "Save button displayed");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Cancel"), "Cancel button displayed");

        logger.info("Step8: Select 'smartFoldersExample.json template' and save.");
        editPropertiesPage.selectSFTemplate(0);
        editPropertiesPage.clickButtonForFolder("Save");

        logger.info("Step9: Click on the folder and verify it has 'Smart Folder' structure under it");
        getBrowser().waitInSeconds(1);
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(mainSmartFolder), "The main smart folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(1), "The smart folder icon displayed");

        logger.info("Step10: Click on 'My content' the folder and verify it has 'Smart Folder' structure under it");
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

    @TestRail(id = "C8668")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void applyCustomSmartFolder()
    {
        String customSmartFolderTemplate = "employeeSmartSimpleTemplate.json";
        String customSmartFolderTemplatePath = testDataFolder + customSmartFolderTemplate;

        logger.info("Preconditions: Upload '.json file for custom smart folder' and navigate to Document Library for the page for the test site");
        setupAuthenticatedSession(adminUser, adminPassword);
        repositoryPage.navigate();
        uploadContent.uploadContent(customSmartFolderTemplatePath);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteNameC8668);

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
        aspectsForm.addElement(16);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Custom Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        logger.info("Step3: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton();
        getBrowser().refresh();

        logger.info("Step4: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editPropertiesDialog);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        logger.info("Step5: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        ArrayList<String> properties = new ArrayList<>(Arrays.asList("Name", "Title", "Description", "Owner", "Tags", "Smart Folder Template"));
        Assert.assertEquals(editPropertiesPage.checkDisplayedProperties(properties), properties.toString());
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Save"), "Save button displayed");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Cancel"), "Cancel button displayed");

        logger.info("Step6: Select '.json' file for the custom folder template and save.");
        editPropertiesPage.clickSelectButtonForCustomSmartFolder();
        getBrowser().waitInSeconds(2);
        selectDialog.clickAdd();
        selectDialog.clickOk();
        editPropertiesPage.clickButtonForFolder("Save");

        logger.info("Step7: Click on the folder and verify it has 'Smart Folder' structure under it");
        getBrowser().waitInSeconds(2);
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("01 Administrative"), "'01 Administrative' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("02 Legal"), "'02 Legal' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("03 Personal"), "'03 Personal' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("04 Other documents"), "'04 Other documents' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("05 PDF Documents in path"), "'05 PDF Documents in path' folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(5), "SF icon displayed for all folders");

    }

}
