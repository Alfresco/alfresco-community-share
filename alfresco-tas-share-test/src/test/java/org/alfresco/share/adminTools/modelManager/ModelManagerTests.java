package org.alfresco.share.adminTools.modelManager;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ChangeContentTypeDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.*;
import org.alfresco.po.share.user.admin.adminTools.ModelDetailsPage;
import org.alfresco.po.share.user.admin.adminTools.ModelManagerPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Document;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;

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

    @Autowired
    EditPropertiesPage editPropertiesPage;

    private String userName = "ModelManagerUser"+ DataUtil.getUniqueIdentifier();
    private String description = "C42568SiteDescription" + DataUtil.getUniqueIdentifier();
    private String siteName = "C42568SiteName" + DataUtil.getUniqueIdentifier();
    private String fileName = "C42568TestFile" +DataUtil.getUniqueIdentifier();
    private String fileContent ="C42568 content";

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, password, domain, siteName, description, Site.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, fileContent);
        setupAuthenticatedSession(adminUser, adminPassword);
    }

    @TestRail(id = "C9500")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })

    public void checkModelManagerPage()
    {
        LOG.info("Step 1: Navigate to Admin Tools page, check and confirm that Model Manager is available under Admin tools");
        adminToolsPage.navigateByMenuBar();
        Assert.assertEquals(adminToolsPage.getPageTitle(), "Alfresco » Admin Tools", "Admin Tools page is not displayed");
        Assert.assertTrue(adminToolsPage.isToolAvailable("Model Manager"), "Model Manager is not displayed");

        LOG.info("Step 2: Click Model Manager on the Admin Tools page;");
        adminToolsPage.navigateToNodeFromToolsPanel("Model Manager");
        Assert.assertEquals(modelManagerPage.getPageTitle(), "Alfresco » Model Manager", "Alfresco » Model Manager page is not displayed");

        LOG.info("Step 3: Check available items on the Model Manager Page");
        Assert.assertTrue(modelManagerPage.isCreateModelButtonDisplayed(), "Create Model button is not displayed");
        Assert.assertTrue(modelManagerPage.isImportModelButtonDisplayed(), "Import model button is not displayed");
        Assert.assertTrue(modelManagerPage.isNameColumnDisplayed(), "Name column is not displayed");
        Assert.assertTrue(modelManagerPage.isNamespaceColumnDisplayed(), "Namespace column is not displayed");
        Assert.assertTrue(modelManagerPage.isStatusColumnDisplayed(), "Status column is not displayed");
        Assert.assertTrue(modelManagerPage.isActionsColumnDisplayed(), "Actions column is not displayed");
        //Assert.assertEquals(modelManagerPage.getNoModelsFoundText(), "No models found. Click Create Model to get started.", "No models found text is not correct");
    }

    @TestRail(id ="C42565")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })

    public void createModel()
    {
        String nameSpace = "C42565Namespace";
        String prefix = "C42565";
        String name = "C42565Name";
        String creator = "C42565Creator";
        String description ="C42565 this is a test model";

        LOG.info("Step 1: Navigate to Model Manager page");
        modelManagerPage.navigate();
        Assert.assertEquals(modelManagerPage.getPageTitle(), "Alfresco » Model Manager", "Alfresco » Model Manager page is not displayed");
        Assert.assertTrue(modelManagerPage.isCreateModelButtonDisplayed(), "Create Model button is not displayed");

        LOG.info("Step 2: On the Model Manager Page click on the Create Model button");
        modelManagerPage.clickCreateModelButton(createModelDialogPage);
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.sendCreatorText(creator);
        createModelDialogPage.sendDescription(description);
        createModelDialogPage.clickCreateButton();

        // modelManagerPage.navigate();

        Assert.assertEquals(modelManagerPage.getModelDetails(name), "C42565Name C42565Namespace Inactive\n" +
                "Actions▾", "Model details are not correct");
        Assert.assertTrue(modelManagerPage.isModelDisplayed(name), "C42565Name model is not displayed");
    }

    @TestRail(id ="C9511")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })

    public void checkImportModelForm()
    {
        modelManagerPage.navigate();
        Assert.assertEquals(modelManagerPage.getPageTitle(), "Alfresco » Model Manager", "Alfresco » Model Manager page is not displayed");
        Assert.assertTrue(modelManagerPage.isImportModelButtonDisplayed(), "Import Model button is not displayed");

        LOG.info("Step 1: On the Model Manager page click on Import Model button");
        modelManagerPage.clickImportModel(importModelDialogPage);
        Assert.assertTrue(importModelDialogPage.isImportModelWindowDisplayed(), "Import model window is not displayed");

        LOG.info("Step 2: Check the Import Model Window");
        Assert.assertEquals(importModelDialogPage.getImportModelWindowTitle(), "Import Model", "Import Model window title is not correct");
        Assert.assertTrue(importModelDialogPage.isCloseButtonDisplayed(), "Close 'x' button is not displayed");
        Assert.assertTrue(importModelDialogPage.isBrowserButtonDisplayed(), "Browser button is not displayed");
        Assert.assertTrue(importModelDialogPage.isImportButtonDisplayed(), "Import button is not displayed");
        Assert.assertTrue(importModelDialogPage.isCancelButtonDisplayed(), "Cancel button is not displayed");
    }

    @TestRail(id ="C9516")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })

    public void activateModel()
    {
        //Preconditions
        String name = "C9516testModel"+DataUtil.getUniqueIdentifier();
        String nameSpace = "C9516nameSpace"+ DataUtil.getUniqueIdentifier();
        String prefix = "C9516"+DataUtil.getUniqueIdentifier();

        modelManagerPage.navigate();
        getBrowser().waitInSeconds(2);
        modelManagerPage.clickCreateModelButton(createModelDialogPage);
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        modelManagerPage.renderedPage();

        LOG.info("Step 1: On the Model Manager Page click Actions for C9516testModel and check available actions");

        getBrowser().waitInSeconds(5);
        //getBrowser().waitUntilWebElementIsDisplayedWithRetry(modelManagerPage.selectModelByName(name));
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Activate"), "Activate is not available for C9516testModel");
        Assert.assertTrue(modelManagerPage.isActionAvailable("Edit"), "Edit is not available for C9516testModel");
        Assert.assertTrue(modelManagerPage.isActionAvailable("Delete"), "Delete is not available for C9516testModel");
        Assert.assertTrue(modelManagerPage.isActionAvailable("Export"), "Export is not available for C9516testModel");

        LOG.info("Step 2: Click on Activate button");
        modelManagerPage.clickOnActionToChangeStatus("Activate");
        modelManagerPage.renderedPage();
        getBrowser().waitInSeconds(2);
        Assert.assertEquals(modelManagerPage.getModelStatus(name),"Active", "C9516testModel status is not Active");
    }

    @TestRail(id ="C9517")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })

    public void editModel()
    {
        //Preconditions
        String name = "C9517testModel";
        String nameSpace = "C9517nameSpace";
        String prefix = "C9517";
        String editedNamespace ="C9517editedNamespace";
        String editedPrefix = "C9517editedPrefix";
        String editedCreator = "EditedCreator";
        String editedDescription ="edited Description C9517";

        modelManagerPage.navigate();
        modelManagerPage.clickCreateModelButton(createModelDialogPage);
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        //modelManagerPage.navigate();

        LOG.info("Step 1: On the Model Manager Page click Actions for C9516testModel and check available actions");
        getBrowser().waitUntilElementVisible(modelManagerPage.actionsButton);
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Edit"), "Edit is not available for C9516testModel");

        LOG.info("Step 2: Click on Edit action");
        modelManagerPage.clickOnAction("Edit");
        Assert.assertTrue(editModelDialogPage.isEditModelDialogDisplayed(), "Edit Model dialog is not displayed");

        LOG.info("Step 3: On the Edit Model form provide edited input");
        Assert.assertEquals(editModelDialogPage.getNameFieldStatus(), "true", "Name field is not disable on the Edit form");

        editModelDialogPage.editNamespace(editedNamespace);
        editModelDialogPage.editPrefix(editedPrefix);
        editModelDialogPage.editCreator(editedCreator);
        editModelDialogPage.editDescription(editedDescription);
        editModelDialogPage.clickSaveButton();
        modelManagerPage.renderedPage();

        Assert.assertEquals(modelManagerPage.getModelDetails(name), "C9517testModel C9517editedNamespace Inactive\n" +
                "Actions▾", "Model details have not been edited successfully");
    }

    @TestRail(id="C9518")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })

    public void deleteModel()
    {
        //Preconditions
        String name = "C9518testModel";
        String nameSpace = "C9518nameSpace";
        String prefix = "C9518";
        String expectedDialogText = "Are you sure you want to delete model '"+name + "'? All custom types, aspects and properties in the model will also be deleted.";
        modelManagerPage.navigate();
        modelManagerPage.clickCreateModelButton(createModelDialogPage);
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        modelManagerPage.renderedPage();

        LOG.info("Step 1: On the Model Manager click on Actions for C9518testModel");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(modelManagerPage.selectModelByName(name), 6);
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Delete"), "Delete is not available for C9518testModel");

        LOG.info("Step 2: Click Delete button");
        modelManagerPage.clickOnAction("Delete");
        deleteModelDialogPage.renderedPage();
        Assert.assertTrue(deleteModelDialogPage.isDeleteModelDialogDisplayed(), "Delete Model dialog is not displayed");

        LOG.info("Step 3: Check the Delete Model window");
        Assert.assertTrue(deleteModelDialogPage.isCloseXButtonDisplayedOnDeleteModelDialog(), "The Close X button is not displayed on the Delete Model dialog page");
        Assert.assertEquals(deleteModelDialogPage.getDeleteModelDialogText(),expectedDialogText, "The dialog text is not correct");
        Assert.assertTrue(deleteModelDialogPage.isButtonDisplayed("Delete"), "Delete button is not displayed");
        Assert.assertTrue(deleteModelDialogPage.isButtonDisplayed("Cancel"), "Cancel button is not displayed");

        LOG.info("Step 4: Click the Delete button");
        deleteModelDialogPage.clickButton("Delete");
        modelManagerPage.renderedPage();
        Assert.assertFalse(modelManagerPage.isModelDisplayed(name));
    }

    @TestRail(id ="C9520")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })

    public void checkAvailableActionsForActiveModel()
    {
        //Preconditions
        String name = "C9520testModel";
        String nameSpace = "C9520nameSpace";
        String prefix = "C9520";
        modelManagerPage.navigate();
        modelManagerPage.clickCreateModelButton(createModelDialogPage);
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        modelManagerPage.renderedPage();
        modelManagerPage.clickActionsButtonForModel(name);
        modelManagerPage.clickOnActionToChangeStatus("Activate");
        modelManagerPage.renderedPage();

        LOG.info("Step 1: On the Model Manager click on Actions for C9520testModel");
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Deactivate"), "Deactivate is not available for active model");
        Assert.assertTrue(modelManagerPage.isActionAvailable("Export"), "Export is not available for an active model");
        Assert.assertFalse(modelManagerPage.isActionAvailable("Activate"), "Activate is still available for an active model");
        Assert.assertFalse(modelManagerPage.isActionAvailable("Edit"), "Edit is still available for an active model");
        Assert.assertFalse(modelManagerPage.isActionAvailable("Delete"), "Delete is still available for an active model");
    }

    @TestRail(id ="C9521")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })

    public void deactivateModel()
    {
        //Preconditions
        String name = "C9521testModel"+DataUtil.getUniqueIdentifier();
        String nameSpace = "C9521nameSpace"+ DataUtil.getUniqueIdentifier();
        String prefix = "C9521"+ DataUtil.getUniqueIdentifier();
        modelManagerPage.navigate();
        modelManagerPage.clickCreateModelButton(createModelDialogPage);
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        modelManagerPage.renderedPage();
        modelManagerPage.clickActionsButtonForModel(name);
        modelManagerPage.clickOnActionToChangeStatus("Activate");
        modelManagerPage.renderedPage();

        LOG.info("Step 1: On the Model Manager click on Actions for C9521testModel");
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Deactivate"), "Deactivate is not available for active model");
        Assert.assertTrue(modelManagerPage.isActionAvailable("Export"), "Export is not available for an active model");

        LOG.info("Step 2: Click on Deactivate action");
        modelManagerPage.clickOnActionToChangeStatus("Deactivate");
        modelManagerPage.renderedPage();
        Assert.assertEquals(modelManagerPage.getModelStatus(name),"Inactive", "C9521testModel status is not Active");
    }

    @TestRail(id ="C9519")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })

    public void exportModel()
    {
        //Preconditions
        String name = "C9517testModel";
        String nameSpace = "C9517nameSpace";
        String prefix = "C9517";
        String editedNamespace ="C9517editedNamespace";
        String editedPrefix = "C9517editedPrefix";
        String editedCreator = "EditedCreator";
        String editedDescription ="edited Description C9517";

        modelManagerPage.navigate();
        modelManagerPage.clickCreateModelButton(createModelDialogPage);
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        modelManagerPage.navigate();

        LOG.info("Step 1: On the Model Manager click on Actions for C9519testModel");
        modelManagerPage.clickActionsButtonForModel(name);
        Assert.assertTrue(modelManagerPage.isActionAvailable("Export"), "Export is not available for active model");

        LOG.info("Step 2: Click on Export action");
        modelManagerPage.clickOnAction("Export");
        export.checkIfAlertIsPresentAndIfTrueAcceptAlert();
        getBrowser().waitInSeconds(3);
        Assert.assertTrue(export.isFileInDirectory(name, ".zip"), "The file was not found in the specified location");
    }

    @TestRail(id="C9509")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void importModel()
    {
        String srcRoot = System.getProperty("user.dir") + File.separator;
        String testDataFolder = srcRoot + "testdata" + File.separator;
        String filePath = testDataFolder + "C9509TestModelName.zip";
        String modelName = "C9509TestModelName";
        modelManagerPage.navigate();
        getBrowser().waitUntilElementClickable(modelManagerPage.importModelButton, 5L);
        Assert.assertTrue(modelManagerPage.isImportModelButtonDisplayed(), "Import model button is not available on the Model Manager Page");

        LOG.info("Step 1: Click import model button");
        modelManagerPage.clickImportModel(importModelDialogPage);
        Assert.assertTrue(importModelDialogPage.isImportModelWindowDisplayed(), "Import Model window is not displayed");

        LOG.info("Step 2&3: Click the Choose Files button, navigate to the location where the testModel file is available locally and select file to import then click open;");
        importModelDialogPage.importFile(filePath);
        importModelDialogPage.clickImportButton();
        modelManagerPage.renderedPage();

        getBrowser().waitUntilElementIsDisplayedWithRetry(By.xpath("//tr[contains(@id, 'alfresco_lists_views_layouts_Row')]//span[text()='" + modelName + "']"), 6);
        Assert.assertTrue(modelManagerPage.isModelDisplayed(modelName), "Imported model is not present on the Model Manager Page");

        LOG.info("Step 4: Check the Model details displayed on the Model Manager page");
        Assert.assertEquals(modelManagerPage.getModelDetails(modelName), "C9509TestModelName C9509TestModelName Inactive\n" +
                "Actions▾","Imported Model Details are not correct" );
    }

    @TestRail(id ="C42566")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })

    public void createCustomType()
    {
        //Preconditions
        String name = "C42566testModel"+DataUtil.getUniqueIdentifier();
        String nameSpace = "C42566nameSpace"+ DataUtil.getUniqueIdentifier();
        String prefix = "C42566"+ DataUtil.getUniqueIdentifier();
        String customTypeName = "TestCustomTypeName";
        String displayLabel = "CustomTypeLabel";
        String description = "Custom type description";
        String displayedTypeName = prefix +":"+ customTypeName;

        modelManagerPage.navigate();
        modelManagerPage.clickCreateModelButton(createModelDialogPage);
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        //modelManagerPage.navigate();
        modelManagerPage.renderedPage();

        LOG.info("Step 1: On the Model Manager page click C42566testModel name link.");
        getBrowser().waitUntilElementVisible(modelManagerPage.selectRow(name));
        modelManagerPage.clickModelName(name, modelDetailsPage);
        Assert.assertTrue(modelDetailsPage.isCreateAspectButtonDisplayed(), "Create Aspect button is not displayed");
        Assert.assertTrue(modelDetailsPage.isCreateCustomTypeButtonDisplayed(),"Create Custom Type button is not displayed");
        Assert.assertTrue(modelDetailsPage.isShowModelsButtonDisplayed(), "Show models button is not displayed");

        LOG.info("Step 2: On the Model Details Page click on create custom type button");
        modelDetailsPage.clickCreateCustomTypeButton(createCustomTypeDialog);
        Assert.assertTrue(createCustomTypeDialog.isCreateCustomTypeWindowDisplayed(), "Create Custom Type window is not displayed");

        LOG.info("Step 3: On the Create Custom Type form provide input for name, display label and description");
        createCustomTypeDialog.sendNameInput(customTypeName);
        createCustomTypeDialog.sendDisplayLabelInput(displayLabel);
        createCustomTypeDialog.sendDescriptionFieldInput(description);
        createCustomTypeDialog.clickCreateButton(modelDetailsPage);
        Assert.assertEquals(modelDetailsPage.getTypeDetails(displayedTypeName), prefix +":TestCustomTypeName CustomTypeLabel cm:content No\n" +
                "Actions▾", "Details for the created type are not correct");
    }

    @TestRail(id="C42567")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })

    public void createAspect()
    {
        //Preconditions
        String name = "C42567testModel"+DataUtil.getUniqueIdentifier();
        String nameSpace = "C42567nameSpace"+DataUtil.getUniqueIdentifier();
        String prefix = "C42567"+ DataUtil.getUniqueIdentifier();
        String aspectName = "TestAspectName";
        String displayLabel = "aspectNameLabel";
        String description = "Aspect description";
        String displayedAspectName = prefix +":"+ aspectName;

        modelManagerPage.navigate();
        modelManagerPage.clickCreateModelButton(createModelDialogPage);
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        //modelManagerPage.navigate();

        LOG.info("Step 1: On the Model Manager page click C42567testModel name link.");
        getBrowser().waitUntilElementVisible(modelManagerPage.selectRow(name));
        modelManagerPage.clickModelName(name, modelDetailsPage);
        Assert.assertTrue(modelDetailsPage.isCreateAspectButtonDisplayed(), "Create Aspect button is not displayed");
        Assert.assertTrue(modelDetailsPage.isCreateCustomTypeButtonDisplayed(),"Create Custom Type button is not displayed");
        Assert.assertTrue(modelDetailsPage.isShowModelsButtonDisplayed(), "Show models button is not displayed");

        LOG.info("Step 2: Click on Create Aspect button");
        modelDetailsPage.clickOnCreateAspectButton(createAspectDialogPage);
        Assert.assertTrue(createAspectDialogPage.isCreateAspectWindowDisplayed(), "Create Aspect window is not displayed");

        LOG.info("Step 3: On the Create Aspect form provide input for name, display label and description");
        createAspectDialogPage.sendNameInput(aspectName);
        createAspectDialogPage.sendDisplayLabelInput(displayLabel);
        createAspectDialogPage.sendDescriptionFieldInput(description);
        createAspectDialogPage.clickCreateButton(modelDetailsPage);
        Assert.assertEquals(modelDetailsPage.getTypeDetails(displayedAspectName), prefix+":TestAspectName aspectNameLabel No\n" +
                "Actions▾", "Details for the created aspect are not correct");
    }

    @Bug(id="TBD")
    @TestRail(id="C42568")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })

    public void useCreatedModel()
    {
        String srcRoot = System.getProperty("user.dir") + File.separator;
        String testDataFolder = srcRoot + "testdata" + File.separator;
        String filePath = testDataFolder + "Marketing_content.zip";
        String modelName = "Marketing_content";
        //Precondition
        modelManagerPage.navigate();
        modelManagerPage.clickImportModel(importModelDialogPage);
        importModelDialogPage.importFile(filePath);
        importModelDialogPage.clickImportButton();
        modelManagerPage.renderedPage();
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(modelManagerPage.selectModelByName(modelName));
        modelManagerPage.clickActionsButtonForModel(modelName);
        modelManagerPage.clickOnActionToChangeStatus("Activate");
        modelManagerPage.renderedPage();
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: On the Document Library page click the name of the testDocument to open the file in Preview and check default properties");
        documentLibraryPage.clickOnFile(fileName);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details");
        ArrayList<String> expectedProperties = new ArrayList<>(Arrays.asList("Name:", "Title:", "Description:", "Author:", "Mimetype:", "Size:", "Creator:",
                "Created Date:", "Modifier:", "Modified Date:"));
        assertEquals(documentDetailsPage.checkDisplayedProperties(expectedProperties), expectedProperties.toString(), "Displayed properties:");

        LOG.info("Step 2: On the Document Details page click Change Type action;");
        documentDetailsPage.clickDocumentActionsOption("Change Type");
        changeContentTypeDialog.renderedPage();
        assertEquals(changeContentTypeDialog.getDialogTitle(), "Change Type", "Displayed dialog: ");

        LOG.info("Step 3: Select the Marketing content (MKT:Marketing) type and apply it to the testDocument");

        changeContentTypeDialog.selectOption("Marketing content (MKT:Marketing)");
        changeContentTypeDialog.clickButton("OK");
        expectedProperties.clear();
        expectedProperties = new ArrayList<>(Arrays.asList("Title:", "Modifier:" , "Creator:"));
        assertEquals(documentDetailsPage.checkDisplayedProperties(expectedProperties), expectedProperties.toString(), "Displayed properties:");
    }
}
