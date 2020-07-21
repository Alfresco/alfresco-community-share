package org.alfresco.share.adminTools.modelManager;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ChangeContentTypeDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.*;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelDetailsPage;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * UI tests for Admin Tools > Model Manager page
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

    private UserModel user;
    private SiteModel site;
    private FileModel file;

    private String name, nameSpace, prefix;
    private List<CustomContentModel> modelsToRemove = new ArrayList<>();

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
        file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "content");
        cmisApi.authenticateUser(user).usingSite(site).createFile(file).assertThat().existsInRepo();

        setupAuthenticatedSession(adminUser, adminPassword);
    }

    @AfterClass (alwaysRun = true)
    public void afterClass()
    {
        cmisApi.authenticateUser(user).usingResource(file).delete();
        userService.emptyTrashcan(user.getUsername(), user.getPassword());

        removeUserFromAlfresco(user);
        dataSite.usingAdmin().deleteSite(site);
        modelsToRemove.forEach(this::deleteCustomModel);
    }

    private void deleteCustomModel(CustomContentModel customContentModel)
    {
        restApi.authenticateUser(dataUser.getAdminUser());
        if(restApi.withPrivateAPI().usingCustomModel(customContentModel).getModel().getStatus().equals("ACTIVE"))
        {
            restApi.withPrivateAPI().usingCustomModel(customContentModel).deactivateModel();
        }
        restApi.withPrivateAPI().usingCustomModel(customContentModel).deleteModel();
    }

    @TestRail (id = "C9500")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void checkModelManagerPage()
    {
        modelManagerPage.navigate();
        modelManagerPage.assertCreateModelButtonIsDisplayed()
            .assertImportModelButtonIsDisplayed()
            .assertAllColumnsAreDisplayed();
    }

    @TestRail (id = "C42565")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createModel()
    {
        name = String.format("C42565Name%s", RandomData.getRandomAlphanumeric());
        nameSpace = String.format("C42565Namespace%s", RandomData.getRandomAlphanumeric());
        prefix = String.format("C42565%s", RandomData.getRandomAlphanumeric());
        CustomContentModel newModel = new CustomContentModel(name, nameSpace, prefix);
        modelsToRemove.add(newModel);

        modelManagerPage.navigate();
        modelManagerPage.createModel(newModel)
            .usingModel(newModel)
                .assertModelIsDisplayed()
                .assertModelNameSpaceIs(nameSpace)
                .assertStatusIsInactive()
                    .clickActions()
                        .assertActionsAreAvailable(language.translate("modelManager.action.activate"),
                                                   language.translate("modelManager.action.edit"),
                                                   language.translate("modelManager.action.delete"),
                                                   language.translate("modelManager.action.export"));
    }

    @TestRail (id = "C9516")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void activateModel()
    {
        // Preconditions
        name = String.format("C9516testModel%s", RandomData.getRandomAlphanumeric());
        nameSpace = String.format("C9516nameSpace%s", RandomData.getRandomAlphanumeric());
        prefix = String.format("C9516%s", RandomData.getRandomAlphanumeric());
        modelsToRemove.add(new CustomContentModel(name));
        modelManagerPage.createModel(name, nameSpace, prefix);

        LOG.info("Step 1: On the Model Manager Page click Actions for C9516testModel and check available actions");
        Assert.assertTrue(modelManagerPage.isModelDisplayed(name), "Model should be displayed");
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Activate"), "Activate is not available for C9516testModel");

        LOG.info("Step 2: Click on Activate button");
        modelManagerPage.clickOnAction("Activate", modelManagerPage);
        Assert.assertEquals(modelManagerPage.getModelStatus(name), "Active", "C9516testModel status is not Active");
    }

    @TestRail (id = "C9517")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void editModel()
    {
        // Preconditions
        name = String.format("C9517testModel%s", RandomData.getRandomAlphanumeric());
        nameSpace = String.format("C9517nameSpace%s", RandomData.getRandomAlphanumeric());
        prefix = String.format("C9517%s", RandomData.getRandomAlphanumeric());
        String editedNamespace = String.format("C9517editedNamespace%s", RandomData.getRandomAlphanumeric());
        String editedPrefix = String.format("C9517editedPrefix%s", RandomData.getRandomAlphanumeric());
        String editedCreator = String.format("EditedCreator%s", RandomData.getRandomAlphanumeric());
        modelsToRemove.add(new CustomContentModel(name));
        modelManagerPage.createModel(name, nameSpace, prefix);

        LOG.info("Step 1: On the Model Manager Page click Actions for C9516testModel and check available actions");
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Edit"), "Edit is not available for C9516testModel");

        LOG.info("Step 2: Click on Edit action");
        modelManagerPage.clickOnAction("Edit", editModelDialogPage);

        LOG.info("Step 3: On the Edit Model form provide edited input");
        Assert.assertEquals(editModelDialogPage.getNameFieldStatus(), "true", "Name field is not disable on the Edit form");

        editModelDialogPage.editNamespace(editedNamespace);
        editModelDialogPage.editPrefix(editedPrefix);
        editModelDialogPage.editCreator(editedCreator);
        editModelDialogPage.clickSaveButton();

        Assert.assertEquals(modelManagerPage.getModelNamespace(name), editedNamespace, "Model namespace is not correct");
        Assert.assertEquals(modelManagerPage.getModelStatus(name), "Inactive", "Model status is not correct");
    }

    @TestRail (id = "C9518")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteModel()
    {
        // Preconditions
        name = String.format("C9518testModel%s", RandomData.getRandomAlphanumeric());
        nameSpace = String.format("C9518nameSpace%s", RandomData.getRandomAlphanumeric());
        prefix = String.format("C9518%s", RandomData.getRandomAlphanumeric());
        String expectedDialogText = "Are you sure you want to delete model ''" + name
            + "''? All custom types, aspects and properties in the model will also be deleted.";

        modelManagerPage.createModel(name, nameSpace, prefix);

        LOG.info("Step 1: On the Model Manager click on Actions for C9518testModel");
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Delete"), "Delete is not available for C9518testModel");

        LOG.info("Step 2: Click Delete button");
        modelManagerPage.clickOnAction("Delete", deleteModelDialogPage);
        Assert.assertTrue(deleteModelDialogPage.isDeleteModelDialogDisplayed(), "Delete Model dialog is not displayed");

        LOG.info("Step 3: Check the Delete Model window");
        Assert.assertEquals(deleteModelDialogPage.getDeleteModelDialogText(), expectedDialogText, "The dialog text is not correct");
        Assert.assertTrue(deleteModelDialogPage.isButtonDisplayed("Delete"), "Delete button is not displayed");
        Assert.assertTrue(deleteModelDialogPage.isButtonDisplayed("Cancel"), "Cancel button is not displayed");
        Assert.assertTrue(deleteModelDialogPage.isCloseButtonDisplayed(), "The Close X button is not displayed on the Delete Model dialog page");

        LOG.info("Step 4: Click the Delete button");
        deleteModelDialogPage.clickDelete();
        Assert.assertFalse(modelManagerPage.isModelDisplayed(name));
    }

    @TestRail (id = "C9520")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void checkAvailableActionsForActiveModel()
    {
        // Preconditions
        name = String.format("C9520testModel%s", RandomData.getRandomAlphanumeric());
        nameSpace = String.format("C9520nameSpace%s", RandomData.getRandomAlphanumeric());
        prefix = String.format("C9520%s", RandomData.getRandomAlphanumeric());
        modelsToRemove.add(new CustomContentModel(name));
        modelManagerPage.createModel(name, nameSpace, prefix);
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
        name = String.format("C9521testModel%s", RandomData.getRandomAlphanumeric());
        nameSpace = String.format("C9521nameSpace%s", RandomData.getRandomAlphanumeric());
        prefix = String.format("C9521%s", RandomData.getRandomAlphanumeric());
        modelsToRemove.add(new CustomContentModel(name));
        modelManagerPage.createModel(name, nameSpace, prefix);
        modelManagerPage.clickActionsButtonForModel(name);
        LOG.info("activate model");
        modelManagerPage.clickOnAction("Activate", modelManagerPage);

        LOG.info("Step 1: On the Model Manager click on Actions for C9521testModel");
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Deactivate"), "Deactivate is not available for active model");

        LOG.info("Step 2: Click on Deactivate action");
        modelManagerPage.clickOnAction("Deactivate", modelManagerPage);
        Assert.assertEquals(modelManagerPage.getModelStatus(name), "Inactive", "C9521testModel status is not Active");
    }

    @TestRail (id = "C9519")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void exportModel()
    {
        // Preconditions
        name = String.format("C9517testModel%s", RandomData.getRandomAlphanumeric());
        nameSpace = String.format("C9517nameSpace%s", RandomData.getRandomAlphanumeric());
        prefix = String.format("C9517%s", RandomData.getRandomAlphanumeric());
        modelsToRemove.add(new CustomContentModel(name));

        modelManagerPage.createModel(name, nameSpace, prefix);

        LOG.info("Step 1: On the Model Manager click on Actions for C9519testModel");
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Export"), "Export is not available for active model");

        LOG.info("Step 2: Click on Export action");
        modelManagerPage.clickOnAction("Export", modelManagerPage);
        Assert.assertTrue(isFileInDirectory(name, ".zip"), "The file was not found in the specified location");
    }

    @TestRail (id = "C9509, C9511")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void importModel()
    {
        String filePath = testDataFolder + "C9509TestModelName.zip";
        name = "C9509TestModelName";
        modelsToRemove.add(new CustomContentModel(name));

        LOG.info("Step 1: Click import model button");
        modelManagerPage.navigate();
        modelManagerPage.clickImportModel();

        LOG.info("Step 2: Check the Import Model Window");
        Assert.assertTrue(importModelDialogPage.isImportModelWindowDisplayed(), "Import model window is not displayed");
        Assert.assertEquals(importModelDialogPage.getImportModelWindowTitle(), "Import Model", "Import Model window title is not correct");
        Assert.assertTrue(importModelDialogPage.isCloseButtonDisplayed(), "Close 'x' button is not displayed");
        Assert.assertTrue(importModelDialogPage.isBrowserButtonDisplayed(), "Browser button is not displayed");
        Assert.assertTrue(importModelDialogPage.isImportButtonDisplayed(), "Import button is not displayed");
        Assert.assertTrue(importModelDialogPage.isCancelButtonDisplayed(), "Cancel button is not displayed");

        LOG.info(
            "Step 2&3: Click the Choose Files button, navigate to the location where the testModel file is available locally and select file to import then click open;");
        importModelDialogPage.importFile(filePath);
        importModelDialogPage.clickImportButton();

        LOG.info("Step 4: Check the Model details displayed on the Model Manager page");
        Assert.assertTrue(modelManagerPage.isModelDisplayed(name), "Imported model is not present on the Model Manager Page");
        Assert.assertEquals(modelManagerPage.getModelNamespace(name), name, "Imported Model namespace is not correct");
        Assert.assertEquals(modelManagerPage.getModelStatus(name), "Inactive", "Imported Model status is not correct");
    }

    @TestRail (id = "C42566")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createCustomType()
    {
        // Preconditions
        name = String.format("C42566testModel%s", RandomData.getRandomAlphanumeric());
        nameSpace = String.format("C42566nameSpace%s", RandomData.getRandomAlphanumeric());
        prefix = String.format("C42566%s", RandomData.getRandomAlphanumeric());
        String customTypeName = "TestCustomTypeName";
        String displayLabel = "CustomTypeLabel";
        String displayedTypeName = prefix + ":" + customTypeName;
        modelsToRemove.add(new CustomContentModel(name));

        modelManagerPage.createModel(name, nameSpace, prefix);

        LOG.info("Step 1: On the Model Manager page click C42566testModel name link.");
        modelManagerPage.clickModelName(name);
        Assert.assertTrue(modelDetailsPage.isCreateAspectButtonDisplayed(), "Create Aspect button is not displayed");
        Assert.assertTrue(modelDetailsPage.isCreateCustomTypeButtonDisplayed(), "Create Custom Type button is not displayed");
        Assert.assertTrue(modelDetailsPage.isShowModelsButtonDisplayed(), "Show models button is not displayed");

        LOG.info("Step 2: On the Model Details Page click on create custom type button");
        modelDetailsPage.clickCreateCustomTypeButton();
        Assert.assertTrue(createCustomTypeDialog.isCreateCustomTypeWindowDisplayed(), "Create Custom Type window is not displayed");

        LOG.info("Step 3: On the Create Custom Type form provide input for name, display label and description");
        createCustomTypeDialog.sendNameInput(customTypeName);
        createCustomTypeDialog.sendDisplayLabelInput(displayLabel);
        createCustomTypeDialog.clickCreateButton();
        Assert.assertEquals(modelDetailsPage.getTypeDetails(displayedTypeName), prefix + ":TestCustomTypeName CustomTypeLabel cm:content No\n" + "Actions▾",
            "Details for the created type are not correct");
    }

    @TestRail (id = "C42567")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createAspect()
    {
        // Preconditions
        name = String.format("C42567testModel%s", RandomData.getRandomAlphanumeric());
        nameSpace = String.format("C42567nameSpace%s", RandomData.getRandomAlphanumeric());
        prefix = String.format("C42567%s", RandomData.getRandomAlphanumeric());
        String aspectName = "TestAspectName";
        String displayLabel = "aspectNameLabel";
        String description = "Aspect description";
        String displayedAspectName = prefix + ":" + aspectName;
        modelsToRemove.add(new CustomContentModel(name));
        modelManagerPage.createModel(name, nameSpace, prefix);

        LOG.info("Step 1: On the Model Manager page click C42567testModel name link.");
        modelManagerPage.clickModelName(name);

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

    @TestRail (id = "C42568")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void useCreatedModel()
    {
        String filePath = testDataFolder + "Marketing_content.zip";
        name = "Marketing_content";
        modelsToRemove.add(new CustomContentModel(name));

        // Precondition
        modelManagerPage.navigate();
        modelManagerPage.clickImportModel();
        importModelDialogPage.importFile(filePath);
        importModelDialogPage.clickImportButton();
        Assert.assertTrue(modelManagerPage.isModelDisplayed(name), "Model should be displayed");
        modelManagerPage.clickActionsButtonForModel(name);
        modelManagerPage.clickOnAction("Activate", modelManagerPage);

        setupAuthenticatedSession(user);
        documentLibraryPage.navigate(site);

        LOG.info("Step 1: On the Document Library page click the name of the testDocument to open the file in Preview and check default properties");
        documentLibraryPage.clickOnFile(file.getName());
        Assert.assertTrue(documentDetailsPage.arePropertiesDisplayed("Name", "Title", "Description", "Author", "Mimetype", "Size", "Creator", "Created Date", "Modifier", "Modified Date"), "Displayed properties:");

        LOG.info("Step 2: On the Document Details page click Change Type action;");
        documentDetailsPage.clickDocumentActionsOption("Change Type", changeContentTypeDialog);

        LOG.info("Step 3: Select the Marketing content (MKT:Marketing) type and apply it to the testDocument");
        changeContentTypeDialog.selectOption("Marketing content (MKT:Marketing)");
        changeContentTypeDialog.clickButton("OK");
        Assert.assertTrue(documentDetailsPage.arePropertiesDisplayed("Title", "Modifier", "Creator"), "Displayed properties:");
    }
}
