package org.alfresco.share.adminTools.modelManager;

import static org.testng.Assert.assertEquals;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ChangeContentTypeDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateAspectDialogPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateCustomTypeDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateModelDialogPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.DeleteModelDialogPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.EditModelDialogPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.Export;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.ImportModelDialogPage;
import org.alfresco.po.share.user.admin.adminTools.ModelDetailsPage;
import org.alfresco.po.share.user.admin.adminTools.ModelManagerPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/28/2016.
 */
public class ModelManagerTests extends ContextAwareWebTest
{
    @Autowired
    ModelManagerPage modelManagerPage;

    @Autowired
    AdminToolsPage adminToolsPage;

    @Autowired
    CreateModelDialogPage createModelDialogPage;

    @Autowired
    ImportModelDialogPage importModelDialogPage;

    @Autowired
    EditModelDialogPage editModelDialogPage;

    @Autowired
    DeleteModelDialogPage deleteModelDialogPage;

    @Autowired
    Export export;

    @Autowired
    ModelDetailsPage modelDetailsPage;

    @Autowired
    CreateCustomTypeDialog createCustomTypeDialog;

    @Autowired
    CreateAspectDialogPage createAspectDialogPage;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    ChangeContentTypeDialog changeContentTypeDialog;

    private String userName = String.format("ModelManagerUser%s", RandomData.getRandomAlphanumeric());
    private String description = String.format("C42568SiteDescription%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("C42568SiteName%s", RandomData.getRandomAlphanumeric());
    private String fileName = String.format("C42568TestFile%s", RandomData.getRandomAlphanumeric());
    private String fileContent = "C42568 content";
    private String name;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, fileContent);
        setupAuthenticatedSession(adminUser, adminPassword);

    }

    @BeforeMethod (alwaysRun = true)
    public void naming()
    {
        name = String.format("C42565Name%s", RandomData.getRandomAlphanumeric());
    }

    @AfterMethod (alwaysRun = true)
    public void afterMethod()
    {


        if (modelManagerPage.isModelDisplayed(name))
        {
            if (modelManagerPage.getModelStatus(name).equals("Active"))
            {
                modelManagerPage.clickActionsButtonForModel(name);
                modelManagerPage.clickOnAction("Deactivate", modelManagerPage);

            }

            modelManagerPage.clickActionsButtonForModel(name);
            modelManagerPage.clickOnAction("Delete", deleteModelDialogPage);
            deleteModelDialogPage.clickDelete();


        }
    }

    @AfterClass (alwaysRun = true)
    public void afterClass()
    {
        siteService.delete(adminUser, adminPassword, siteName);

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

    }

    @TestRail (id = "C9500")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void checkModelManagerPage()
    {
        LOG.info("Step 1: Navigate to Admin Tools page, check and confirm that Model Manager is available under Admin tools");
        adminToolsPage.navigate();
        Assert.assertEquals(adminToolsPage.getPageTitle(), "Alfresco » Admin Tools", "Admin Tools page is not displayed");
        Assert.assertTrue(adminToolsPage.isToolAvailable("Model Manager"), "Model Manager is not displayed");

        LOG.info("Step 2: Click Model Manager on the Admin Tools page;");
        adminToolsPage.navigateToNodeFromToolsPanel("Model Manager", modelManagerPage);
        Assert.assertEquals(modelManagerPage.getPageTitle(), "Alfresco » Model Manager", "Alfresco » Model Manager page is not displayed");

        LOG.info("Step 3: Check available items on the Model Manager Page");
        Assert.assertTrue(modelManagerPage.isCreateModelButtonDisplayed(), "Create Model button is not displayed");
        Assert.assertTrue(modelManagerPage.isImportModelButtonDisplayed(), "Import model button is not displayed");
        Assert.assertTrue(modelManagerPage.isNameColumnDisplayed(), "Name column is not displayed");
        Assert.assertTrue(modelManagerPage.isNamespaceColumnDisplayed(), "Namespace column is not displayed");
        Assert.assertTrue(modelManagerPage.isStatusColumnDisplayed(), "Status column is not displayed");
        Assert.assertTrue(modelManagerPage.isActionsColumnDisplayed(), "Actions column is not displayed");
        // Assert.assertEquals(modelManagerPage.getNoModelsFoundText(), "No models found. Click Create Model to get started.", "No models found text is not
        // correct");
    }

    @TestRail (id = "C42565")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createModel()
    {
        String nameSpace = String.format("C42565Namespace%s", RandomData.getRandomAlphanumeric());
        String prefix = String.format("C42565%s", RandomData.getRandomAlphanumeric());
        String name = String.format("C42565Name%s", RandomData.getRandomAlphanumeric());
        String creator = String.format("C42565Creator%s", RandomData.getRandomAlphanumeric());
        String description = "C42565 this is a test model";

        LOG.info("Step 1: Navigate to Model Manager page");
        modelManagerPage.navigate();
        Assert.assertEquals(modelManagerPage.getPageTitle(), "Alfresco » Model Manager", "Alfresco » Model Manager page is not displayed");
        Assert.assertTrue(modelManagerPage.isCreateModelButtonDisplayed(), "Create Model button is not displayed");

        LOG.info("Step 2: On the Model Manager Page click on the Create Model button");
        modelManagerPage.clickCreateModelButton();
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.sendCreatorText(creator);
        createModelDialogPage.sendDescription(description);
        createModelDialogPage.clickCreateButton();
        modelManagerPage.waitForModel(name);

        Assert.assertTrue(modelManagerPage.isModelDisplayed(name), "C42565Name model is not displayed");
        Assert.assertEquals(modelManagerPage.getModelDetails(name), String.format("%s %s Inactive\nActions▾", name, nameSpace),
                "Model details are not correct");
    }

    @TestRail (id = "C9511")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void checkImportModelForm()
    {
        modelManagerPage.navigate();
        Assert.assertEquals(modelManagerPage.getPageTitle(), "Alfresco » Model Manager", "Alfresco » Model Manager page is not displayed");
        Assert.assertTrue(modelManagerPage.isImportModelButtonDisplayed(), "Import Model button is not displayed");

        LOG.info("Step 1: On the Model Manager page click on Import Model button");
        modelManagerPage.clickImportModel();
        Assert.assertTrue(importModelDialogPage.isImportModelWindowDisplayed(), "Import model window is not displayed");

        LOG.info("Step 2: Check the Import Model Window");
        Assert.assertEquals(importModelDialogPage.getImportModelWindowTitle(), "Import Model", "Import Model window title is not correct");
        Assert.assertTrue(importModelDialogPage.isCloseButtonDisplayed(), "Close 'x' button is not displayed");
        Assert.assertTrue(importModelDialogPage.isBrowserButtonDisplayed(), "Browser button is not displayed");
        Assert.assertTrue(importModelDialogPage.isImportButtonDisplayed(), "Import button is not displayed");
        Assert.assertTrue(importModelDialogPage.isCancelButtonDisplayed(), "Cancel button is not displayed");
    }

    @TestRail (id = "C9516")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void activateModel()
    {
        // Preconditions
        String name = String.format("C9516testModel%s", RandomData.getRandomAlphanumeric());
        String nameSpace = String.format("C9516nameSpace%s", RandomData.getRandomAlphanumeric());
        String prefix = String.format("C9516%s", RandomData.getRandomAlphanumeric());

        modelManagerPage.createModel(name, nameSpace, prefix);

        LOG.info("Step 1: On the Model Manager Page click Actions for C9516testModel and check available actions");

        Assert.assertTrue(modelManagerPage.isModelDisplayed(name), "Model should be displayed");
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Activate"), "Activate is not available for C9516testModel");
        Assert.assertTrue(modelManagerPage.isActionAvailable("Edit"), "Edit is not available for C9516testModel");
        Assert.assertTrue(modelManagerPage.isActionAvailable("Delete"), "Delete is not available for C9516testModel");
        Assert.assertTrue(modelManagerPage.isActionAvailable("Export"), "Export is not available for C9516testModel");

        LOG.info("Step 2: Click on Activate button");
        modelManagerPage.clickOnAction("Activate", modelManagerPage);
        Assert.assertEquals(modelManagerPage.getModelStatus(name), "Active", "C9516testModel status is not Active");
    }

    @TestRail (id = "C9517")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void editModel()
    {
        // Preconditions
        String name = String.format("C9517testModel%s", RandomData.getRandomAlphanumeric());
        String nameSpace = String.format("C9517nameSpace%s", RandomData.getRandomAlphanumeric());
        String prefix = String.format("C9517%s", RandomData.getRandomAlphanumeric());
        String editedNamespace = String.format("C9517editedNamespace%s", RandomData.getRandomAlphanumeric());
        String editedPrefix = String.format("C9517editedPrefix%s", RandomData.getRandomAlphanumeric());
        String editedCreator = String.format("EditedCreator%s", RandomData.getRandomAlphanumeric());
        String editedDescription = String.format("edited Description C9517%s", RandomData.getRandomAlphanumeric());

        modelManagerPage.createModel(name, nameSpace, prefix);

        LOG.info("Step 1: On the Model Manager Page click Actions for C9516testModel and check available actions");
        getBrowser().waitUntilElementVisible(modelManagerPage.actionsButton);
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Edit"), "Edit is not available for C9516testModel");

        LOG.info("Step 2: Click on Edit action");
        modelManagerPage.clickOnAction("Edit", editModelDialogPage);
        Assert.assertTrue(editModelDialogPage.isEditModelDialogDisplayed(), "Edit Model dialog is not displayed");

        LOG.info("Step 3: On the Edit Model form provide edited input");
        Assert.assertEquals(editModelDialogPage.getNameFieldStatus(), "true", "Name field is not disable on the Edit form");

        editModelDialogPage.editNamespace(editedNamespace);
        editModelDialogPage.editPrefix(editedPrefix);
        editModelDialogPage.editCreator(editedCreator);
        editModelDialogPage.editDescription(editedDescription);
        editModelDialogPage.clickSaveButton();
        modelManagerPage.renderedPage();

        Assert.assertEquals(modelManagerPage.getModelDetails(name), String.format("%s %s Inactive\nActions▾", name, editedNamespace),
                "Model details have not been edited successfully");
    }

    @TestRail (id = "C9518")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteModel()
    {
        // Preconditions
        String name = String.format("C9518testModel%s", RandomData.getRandomAlphanumeric());
        String nameSpace = String.format("C9518nameSpace%s", RandomData.getRandomAlphanumeric());
        String prefix = String.format("C9518%s", RandomData.getRandomAlphanumeric());
        String expectedDialogText = "Are you sure you want to delete model ''" + name
                + "''? All custom types, aspects and properties in the model will also be deleted.";
        modelManagerPage.navigate();
        modelManagerPage.clickCreateModelButton();
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        modelManagerPage.waitForModel(name);

        LOG.info("Step 1: On the Model Manager click on Actions for C9518testModel");
        Assert.assertTrue(modelManagerPage.isModelDisplayed(name), "Model should be displayed");
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Delete"), "Delete is not available for C9518testModel");

        LOG.info("Step 2: Click Delete button");
        modelManagerPage.clickOnAction("Delete", deleteModelDialogPage);
        Assert.assertTrue(deleteModelDialogPage.isDeleteModelDialogDisplayed(), "Delete Model dialog is not displayed");

        LOG.info("Step 3: Check the Delete Model window");
        Assert.assertTrue(deleteModelDialogPage.isCloseXButtonDisplayedOnDeleteModelDialog(),
                "The Close X button is not displayed on the Delete Model dialog page");
        Assert.assertEquals(deleteModelDialogPage.getDeleteModelDialogText(), expectedDialogText, "The dialog text is not correct");
        Assert.assertTrue(deleteModelDialogPage.isButtonDisplayed("Delete"), "Delete button is not displayed");
        Assert.assertTrue(deleteModelDialogPage.isButtonDisplayed("Cancel"), "Cancel button is not displayed");

        LOG.info("Step 4: Click the Delete button");
        deleteModelDialogPage.clickButton("Delete");
        modelManagerPage.renderedPage();
        Assert.assertFalse(modelManagerPage.isModelDisplayed(name));
    }

    @TestRail (id = "C9520")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void checkAvailableActionsForActiveModel()
    {
        // Preconditions
        String name = String.format("C9520testModel%s", RandomData.getRandomAlphanumeric());
        String nameSpace = String.format("C9520nameSpace%s", RandomData.getRandomAlphanumeric());
        String prefix = String.format("C9520%s", RandomData.getRandomAlphanumeric());
        modelManagerPage.navigate();
        modelManagerPage.clickCreateModelButton();
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        modelManagerPage.renderedPage();
        modelManagerPage.clickActionsButtonForModel(name);
        modelManagerPage.clickOnAction("Activate", modelManagerPage);

        LOG.info("Step 1: On the Model Manager click on Actions for C9520testModel");
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Deactivate"), "Deactivate is not available for active model");
        Assert.assertTrue(modelManagerPage.isActionAvailable("Export"), "Export is not available for an active model");
        Assert.assertFalse(modelManagerPage.isActionAvailable("Activate"), "Activate is still available for an active model");
        Assert.assertFalse(modelManagerPage.isActionAvailable("Edit"), "Edit is still available for an active model");
        Assert.assertFalse(modelManagerPage.isActionAvailable("Delete"), "Delete is still available for an active model");
    }

    @TestRail (id = "C9521")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deactivateModel()
    {
        // Preconditions
        String name = String.format("C9521testModel%s", RandomData.getRandomAlphanumeric());
        String nameSpace = String.format("C9521nameSpace%s", RandomData.getRandomAlphanumeric());
        String prefix = String.format("C9521%s", RandomData.getRandomAlphanumeric());
        modelManagerPage.navigate();
        modelManagerPage.clickCreateModelButton();
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        modelManagerPage.waitForModel(name);
        modelManagerPage.clickActionsButtonForModel(name);
        modelManagerPage.clickOnAction("Activate", modelManagerPage);

        LOG.info("Step 1: On the Model Manager click on Actions for C9521testModel");
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Deactivate"), "Deactivate is not available for active model");
        Assert.assertTrue(modelManagerPage.isActionAvailable("Export"), "Export is not available for an active model");

        LOG.info("Step 2: Click on Deactivate action");
        modelManagerPage.clickOnAction("Deactivate", modelManagerPage);
        Assert.assertEquals(modelManagerPage.getModelStatus(name), "Inactive", "C9521testModel status is not Active");
    }

    @TestRail (id = "C9519")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void exportModel()
    {
        // Preconditions
        String name = String.format("C9517testModel%s", RandomData.getRandomAlphanumeric());
        String nameSpace = String.format("C9517nameSpace%s", RandomData.getRandomAlphanumeric());
        String prefix = String.format("C9517%s", RandomData.getRandomAlphanumeric());

        modelManagerPage.navigate();
        modelManagerPage.clickCreateModelButton();
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        modelManagerPage.waitForModel(name);


        LOG.info("Step 1: On the Model Manager click on Actions for C9519testModel");
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Export"), "Export is not available for active model");

        LOG.info("Step 2: Click on Export action");
        modelManagerPage.clickOnAction("Export", export);
        export.checkIfAlertIsPresentAndIfTrueAcceptAlert();
        Assert.assertTrue(export.isFileInDirectory(name, ".zip"), "The file was not found in the specified location");
    }

    @TestRail (id = "C9509")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void importModel()
    {
        String filePath = testDataFolder + "C9509TestModelName.zip";
        String modelName = "C9509TestModelName";
        modelManagerPage.navigate();
        Assert.assertTrue(modelManagerPage.isImportModelButtonDisplayed(), "Import model button is not available on the Model Manager Page");

        LOG.info("Step 1: Click import model button");
        modelManagerPage.clickImportModel();
        Assert.assertTrue(importModelDialogPage.isImportModelWindowDisplayed(), "Import Model window is not displayed");

        LOG.info(
                "Step 2&3: Click the Choose Files button, navigate to the location where the testModel file is available locally and select file to import then click open;");
        importModelDialogPage.importFile(filePath);
        importModelDialogPage.clickImportButton();
        modelManagerPage.renderedPage();
        Assert.assertTrue(modelManagerPage.isModelDisplayed(modelName), "Imported model is not present on the Model Manager Page");

        LOG.info("Step 4: Check the Model details displayed on the Model Manager page");
        Assert.assertEquals(modelManagerPage.getModelDetails(modelName), "C9509TestModelName C9509TestModelName Inactive\n" + "Actions▾",
                "Imported Model Details are not correct");
    }

    @TestRail (id = "C42566")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createCustomType()
    {
        // Preconditions
        String name = String.format("C42566testModel%s", RandomData.getRandomAlphanumeric());
        String nameSpace = String.format("C42566nameSpace%s", RandomData.getRandomAlphanumeric());
        String prefix = String.format("C42566%s", RandomData.getRandomAlphanumeric());
        String customTypeName = "TestCustomTypeName";
        String displayLabel = "CustomTypeLabel";
        String description = "Custom type description";
        String displayedTypeName = prefix + ":" + customTypeName;

        modelManagerPage.navigate();
        modelManagerPage.clickCreateModelButton();
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        // modelManagerPage.navigate();
        modelManagerPage.renderedPage();

        LOG.info("Step 1: On the Model Manager page click C42566testModel name link.");
        modelManagerPage.clickModelNameWithoutRender(name);
        Assert.assertTrue(modelDetailsPage.isCreateAspectButtonDisplayed(), "Create Aspect button is not displayed");
        Assert.assertTrue(modelDetailsPage.isCreateCustomTypeButtonDisplayed(), "Create Custom Type button is not displayed");
        Assert.assertTrue(modelDetailsPage.isShowModelsButtonDisplayed(), "Show models button is not displayed");

        LOG.info("Step 2: On the Model Details Page click on create custom type button");
        modelDetailsPage.clickCreateCustomTypeButton();
        Assert.assertTrue(createCustomTypeDialog.isCreateCustomTypeWindowDisplayed(), "Create Custom Type window is not displayed");

        LOG.info("Step 3: On the Create Custom Type form provide input for name, display label and description");
        createCustomTypeDialog.sendNameInput(customTypeName);
        createCustomTypeDialog.sendDisplayLabelInput(displayLabel);
        createCustomTypeDialog.sendDescriptionFieldInput(description);
        createCustomTypeDialog.clickCreateButton();
        Assert.assertEquals(modelDetailsPage.getTypeDetails(displayedTypeName), prefix + ":TestCustomTypeName CustomTypeLabel cm:content No\n" + "Actions▾",
                "Details for the created type are not correct");
    }

    @TestRail (id = "C42567")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createAspect()
    {
        // Preconditions
        String name = String.format("C42567testModel%s", RandomData.getRandomAlphanumeric());
        String nameSpace = String.format("C42567nameSpace%s", RandomData.getRandomAlphanumeric());
        String prefix = String.format("C42567%s", RandomData.getRandomAlphanumeric());
        String aspectName = "TestAspectName";
        String displayLabel = "aspectNameLabel";
        String description = "Aspect description";
        String displayedAspectName = prefix + ":" + aspectName;

        modelManagerPage.navigate();
        modelManagerPage.clickCreateModelButton();
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        modelManagerPage.waitForModel(name);
        LOG.info("Step 1: On the Model Manager page click C42567testModel name link.");
        getBrowser().waitUntilElementVisible(modelManagerPage.selectRow(name));
        modelManagerPage.clickModelNameWithoutRender(name);
        Assert.assertTrue(modelDetailsPage.isCreateAspectButtonDisplayed(), "Create Aspect button is not displayed");
        Assert.assertTrue(modelDetailsPage.isCreateCustomTypeButtonDisplayed(), "Create Custom Type button is not displayed");
        Assert.assertTrue(modelDetailsPage.isShowModelsButtonDisplayed(), "Show models button is not displayed");

        LOG.info("Step 2: Click on Create Aspect button");
        modelDetailsPage.clickOnCreateAspectButton();
        Assert.assertTrue(createAspectDialogPage.isCreateAspectWindowDisplayed(), "Create Aspect window is not displayed");

        LOG.info("Step 3: On the Create Aspect form provide input for name, display label and description");
        createAspectDialogPage.sendNameInput(aspectName);
        createAspectDialogPage.sendDisplayLabelInput(displayLabel);
        createAspectDialogPage.sendDescriptionFieldInput(description);
        createAspectDialogPage.clickCreateButton();

        Assert.assertEquals(modelDetailsPage.getAspectDetails(displayedAspectName), prefix + ":TestAspectName aspectNameLabel No\n" + "Actions▾",
                "Details for the created aspect are not correct");
    }

    @Bug (id = "TBD")
    @TestRail (id = "C42568")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void useCreatedModel()
    {
        String filePath = testDataFolder + "Marketing_content.zip";
        String modelName = "Marketing_content";
        // Precondition
        modelManagerPage.navigate();
        modelManagerPage.clickImportModel();
        importModelDialogPage.importFile(filePath);
        importModelDialogPage.clickImportButton();
        modelManagerPage.renderedPage();
        modelManagerPage.waitForModel(modelName);
        Assert.assertTrue(modelManagerPage.isModelDisplayed(modelName), "Model should be displayed");
        modelManagerPage.clickActionsButtonForModel(modelName);
        modelManagerPage.clickOnAction("Activate", modelManagerPage);
        modelManagerPage.clickModelName("Marketing_content");


        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: On the Document Library page click the name of the testDocument to open the file in Preview and check default properties");
        documentLibraryPage.clickOnFile(fileName);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details");
        Assert.assertTrue(documentDetailsPage.arePropertiesDisplayed("Name", "Title", "Description", "Author", "Mimetype", "Size", "Creator", "Created Date", "Modifier", "Modified Date"), "Displayed properties:");

        LOG.info("Step 2: On the Document Details page click Change Type action;");
        documentDetailsPage.clickDocumentActionsOption("Change Type");
        changeContentTypeDialog.renderedPage();
        assertEquals(changeContentTypeDialog.getDialogTitle(), "Change Type", "Displayed dialog: ");

        LOG.info("Step 3: Select the Marketing content (MKT:Marketing) type and apply it to the testDocument");

        changeContentTypeDialog.selectOption("Marketing content (MKT:Marketing)");
        changeContentTypeDialog.clickButton("OK");
        documentDetailsPage.waitUntilMessageDisappears();
        Assert.assertTrue(documentDetailsPage.arePropertiesDisplayed("Title", "Modifier", "Creator"), "Displayed properties:");
    }
}
