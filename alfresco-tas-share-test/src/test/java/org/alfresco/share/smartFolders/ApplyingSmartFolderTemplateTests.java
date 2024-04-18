package org.alfresco.share.smartFolders;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertTrue;

import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.SmartFolders;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class ApplyingSmartFolderTemplateTests extends BaseTest
{
    //@Autowired
    DocumentLibraryPage documentLibraryPage;

    //@Autowired
    AspectsForm aspectsForm;

    //@Autowired
    EditPropertiesDialog editPropertiesDialog;

    //@Autowired
    EditPropertiesPage editPropertiesPage;

   // @Autowired
    NewFolderDialog newContentDialog;

    //@Autowired
    SmartFolders smartFolders;

    //@Autowired
    UploadContent uploadContent;

    //@Autowired
    RepositoryPage repositoryPage;

    //@Autowired
    SelectDialog selectDialog;
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameC8665 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameC8666 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameC8668 = new ThreadLocal<>();
    private String fileName1 = "testFile1";
    private String fileName2 = "testFile2";
    private String fileName3 = "testFile3";
    private String fileContent = "testContent";
    private String folderName = "testFolder";
    private String mainSmartFolder = "My content";

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        userName.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        siteNameC8665.set(getDataSite().usingUser(userName.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());

        siteNameC8666.set(getDataSite().usingUser(userName.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());

        siteNameC8668.set(getDataSite().usingUser(userName.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());

        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteNameC8665.get().getId());
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteNameC8668.get().getId());

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        aspectsForm = new AspectsForm(webDriver);
        editPropertiesDialog = new EditPropertiesDialog(webDriver);
        editPropertiesPage = new EditPropertiesPage(webDriver);
        smartFolders = new SmartFolders(webDriver);
        newContentDialog = new NewFolderDialog(webDriver);
        repositoryPage = new RepositoryPage(webDriver);
        uploadContent = new UploadContent(webDriver);
        selectDialog = new SelectDialog(webDriver);
        authenticateUsingLoginPage(userName.get());
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());

        deleteSitesIfNotNull(siteNameC8665.get());
        deleteSitesIfNotNull(siteNameC8666.get());
        deleteSitesIfNotNull(siteNameC8668.get());

        deleteUsersIfNotNull(userName.get());
    }


    @TestRail (id = "C8665")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "SmartFolders" })
    public void applySFTemplateToExistingFolder() throws InterruptedException {
        String filePath = String.format("Sites/%s/documentLibrary/%s", siteNameC8665.get().getTitle(), folderName);

        contentService.createDocumentInRepository(userName.get().getUsername(), userName.get().getPassword(), filePath, CMISUtil.DocumentType.TEXT_PLAIN, fileName1, fileContent);
        contentService.createDocumentInRepository(userName.get().getUsername(), userName.get().getPassword(), filePath, DocumentType.TEXT_PLAIN, fileName2, fileContent);
        contentService.createDocumentInRepository(userName.get().getUsername(), userName.get().getPassword(), filePath, DocumentType.TEXT_PLAIN, fileName3, fileContent);

        log.info("Preconditions: Navigate to Document Library for the page for the test site");
        documentLibraryPage.navigate(siteNameC8665.get());

        log.info("Step1: Click Actions -> Manage Aspects and verify Manage Aspects form");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_ASPECTS);
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel diaplyed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedPanel(), "Currently Selected panel diaplyed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "'Apply Changes' button displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "'Cancel' button displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "'Close' button displayed");

        log.info("Step2: Click 'Add' button next to 'System Smart Folder' template and verify it moves to 'Currently Selected'");
        aspectsForm.addAspect("System Smart Folder");
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("System Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        log.info("Step3: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton();

        log.info("Step4: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.selectItemAction(folderName, ItemActions.EDIT_PROPERTIES);
        //Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        log.info("Step5: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        Assert.assertTrue(editPropertiesPage.arePropertiesDisplayed("Name", "Title", "Description", "Tags", "Smart Folder Template"), "Displayed properties");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Save"), "Save button displayed");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Cancel"), "Cancel button displayed");

        log.info("Step6: Select 'smartFoldersExample.json template' and save.");
        editPropertiesPage.selectSFTemplate(0);
        editPropertiesPage.clickButtonForFolder("Save");

        log.info("Step7: Click on the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileName1), "File1 displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileName2), "File2 displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileName3), "File3 displayed");
//        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(mainSmartFolder), "The main smart folder displayed");
//        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(1), "The smart folder icon displayed");

    }

    @TestRail (id = "C8666")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "SmartFolders" }, enabled = false)
    public void applySFTemplateToCreatedFolder()
    {
        String filePath = String.format("Sites/%s/documentLibrary/%s", siteNameC8665.get().getTitle(), folderName);

        contentService.createDocumentInRepository(userName.get().getUsername(), userName.get().getPassword(), filePath, CMISUtil.DocumentType.TEXT_PLAIN, fileName1, fileContent);
        contentService.createDocumentInRepository(userName.get().getUsername(), userName.get().getPassword(), filePath, DocumentType.TEXT_PLAIN, fileName2, fileContent);
        contentService.createDocumentInRepository(userName.get().getUsername(), userName.get().getPassword(), filePath, DocumentType.TEXT_PLAIN, fileName3, fileContent);

        log.info("Preconditions: Navigate to Document Library for the page for the test site");
        documentLibraryPage.navigate(siteNameC8666.get());

        log.info("Step1: Click on 'Create' button and choose 'Folder'");
        documentLibraryPage.clickCreateButton();
        documentLibraryPage.clickFolderLink();
        /*assertTrue(newContentDialog.isNameFieldDisplayed(), "'Name' field displayed.");
        assertTrue(newContentDialog.isMandatoryIndicatorDisplayed(), "'Name' mandatory field.");
        assertTrue(newContentDialog.isTitleFieldDisplayed(), "'Title' field displayed.");
        assertTrue(newContentDialog.isDescriptionFieldDisplayed(), "'Description' field displayed.");
        assertTrue(newContentDialog.isSaveButtonDisplayed(), "'Save' button displayed.");
        assertTrue(newContentDialog.isCancelButtonDisplayed(), "'Cancel' button displayed.");*/

        log.info("Step2: Input 'Name', 'Title', 'Description' and click 'Save'");
        newContentDialog.fillInDetails(folderName, "Title", "Description");
        newContentDialog.clickSave();
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), folderName + " displayed in Documents list.");

        log.info("Step3: Hover over folder, click More -> Manage Aspects");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_ASPECTS);

        log.info("Step4: Click 'Add' button next to 'System Smart Folder' template and verify it moves to 'Currently Selected'");
        aspectsForm.addAspect("System Smart Folder");
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("System Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        log.info("Step5: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton();

        log.info("Step6: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.selectItemAction(folderName, ItemActions.EDIT_PROPERTIES);
        //Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        log.info("Step7: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        Assert.assertTrue(editPropertiesPage.arePropertiesDisplayed("Name", "Title", "Description", "Tags", "Smart Folder Template"), "Displayed properties");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Save"), "Save button displayed");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Cancel"), "Cancel button displayed");

        log.info("Step8: Select 'smartFoldersExample.json template' and save.");
        editPropertiesPage.selectSFTemplate(0);
        editPropertiesPage.clickButtonForFolder("Save");

        log.info("Step9: Click on the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(mainSmartFolder), "The main smart folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(1), "The smart folder icon displayed");

        log.info("Step10: Click on 'My content' the folder and verify it has 'Smart Folder' structure under it");
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
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "SmartFolders" }, enabled = false)
    public void applyCustomSmartFolder()
    {
        String customSmartFolderTemplate = "employeeSmartSimpleTemplate.json";
        String customSmartFolderTemplatePath = testDataFolder + customSmartFolderTemplate;

        log.info("Preconditions: Upload '.json file for custom smart folder' and navigate to Document Library for the page for the test site");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage.navigate();
        uploadContent.uploadContent(customSmartFolderTemplatePath);

        authenticateUsingLoginPage(userName.get());
        documentLibraryPage.navigate(siteNameC8668.get());

        log.info("Step1: Click Actions -> Manage Aspects and verify Manage Aspects form");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_ASPECTS);

        log.info("Step2: Click 'Add' button next to 'Custom Smart Folder' template and verify it moves to 'Currently Selected'");
        aspectsForm.addAspect("Custom Smart Folder");
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Custom Smart Folder"), "Aspect added to 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove button displayed for the selected aspect");

        log.info("Step3: Click 'Apply Changes'.");
        aspectsForm.clickApplyChangesButton();

        log.info("Step4: Hover over folder and click 'Edit Properties'.");
        documentLibraryPage.selectItemAction(folderName, ItemActions.EDIT_PROPERTIES);
        //Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        log.info("Step5: Click 'All Properties' link.");
        editPropertiesDialog.clickAllPropertiesLink();
        Assert.assertTrue(editPropertiesPage.arePropertiesDisplayed("Name", "Title", "Description", "Tags", "Smart Folder Template"), "Displayed properties");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Save"), "Save button displayed");
        Assert.assertTrue(editPropertiesPage.isButtonDisplayed("Cancel"), "Cancel button displayed");

        log.info("Step6: Select '.json' file for the custom folder template and save.");
        editPropertiesPage.clickSelectButtonForCustomSmartFolder();
        selectDialog.selectItems(Collections.singletonList("employeeSmartSimpleTemplate.json"));
        selectDialog.clickOk();
        editPropertiesPage.clickButtonForFolder("Save");

        log.info("Step7: Click on the folder and verify it has 'Smart Folder' structure under it");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("01 Administrative"), "'01 Administrative' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("02 Legal"), "'02 Legal' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("03 Personal"), "'03 Personal' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("04 Other documents"), "'04 Other documents' folder displayed");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("05 PDF Documents in path"), "'05 PDF Documents in path' folder displayed");
        Assert.assertTrue(smartFolders.areSmartFolderIconsDisplayed(5), "SF icon displayed for all folders");

    }

}
