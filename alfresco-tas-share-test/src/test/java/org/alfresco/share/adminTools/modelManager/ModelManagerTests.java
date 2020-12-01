package org.alfresco.share.adminTools.modelManager;

import static org.alfresco.common.Utils.isFileInDirectory;
import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.alfresco.rest.model.RestCustomModel;
import org.alfresco.rest.model.RestCustomTypeModel;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.CustomAspectModel;
import org.alfresco.utility.model.CustomContentModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ModelManagerTests extends BaseTest
{
    private ModelManagerPage modelManagerPage;
    private DocumentDetailsPage documentDetailsPage;

    @Autowired
    private UserService userService;

    private UserModel user;
    private SiteModel site;
    private FileModel file;

    private static List<CustomContentModel> modelsToRemove = Collections.synchronizedList(new ArrayList<>());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
        file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "content");
        getCmisApi().authenticateUser(user).usingSite(site).createFile(file).assertThat().existsInRepo();

        getRestApi().authenticateUser(getAdminUser());
    }

    @BeforeMethod(alwaysRun = true)
    public void precondition()
    {
        modelManagerPage = new ModelManagerPage(browser);
        documentDetailsPage = new DocumentDetailsPage(browser);

        setupAuthenticatedSession(getAdminUser());
    }

    @AfterClass (alwaysRun = true)
    public void afterClass()
    {
        getCmisApi().authenticateUser(user).usingResource(file).delete();
        userService.emptyTrashcan(user.getUsername(), user.getPassword());

        removeUserFromAlfresco(user);
        dataSite.usingAdmin().deleteSite(site);
        modelsToRemove.forEach(this::deleteCustomModel);
    }

    private void deleteCustomModel(CustomContentModel customContentModel)
    {
        getRestApi().authenticateUser(dataUser.getAdminUser());

        RestCustomModel customModelToDelete = getRestApi().withPrivateAPI().usingCustomModel(customContentModel).getModel();
        if(customModelToDelete != null)
        {
            if(customModelToDelete.getStatus().equals("ACTIVE"))
            {
                getRestApi().withPrivateAPI().usingCustomModel(customModelToDelete).deactivateModel();
            }
            getRestApi().withPrivateAPI().usingCustomModel(customModelToDelete).deleteModel();
        }
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
        String name = String.format("C42565Name-%s", RandomData.getRandomAlphanumeric());
        String nameSpace = String.format("C42565Namespace-%s", RandomData.getRandomAlphanumeric());
        String prefix = String.format("C42565-%s", RandomData.getRandomAlphanumeric());
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

    @TestRail (id = "C9516, C9520")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void activateModel()
    {
        String name = String.format("C9516Model-%s", RandomData.getRandomAlphanumeric());
        CustomContentModel newModel = new CustomContentModel(name, name, name);
        modelManagerPage.navigate();
        modelsToRemove.add(newModel);
        modelManagerPage.createModel(newModel)
            .usingModel(newModel).assertModelIsDisplayed()
                .clickActions().activateModel()
                    .assertStatusIsActive()
                .clickActions()
                    .assertActionsAreAvailable(language.translate("modelManager.action.deactivate"),
                                               language.translate("modelManager.action.export"));
    }

    @TestRail (id = "C9517")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void editModel()
    {
        String name = String.format("C9517-%s", RandomData.getRandomAlphanumeric());
        CustomContentModel newModel = new CustomContentModel(name, name, name);
        createCustomModel(newModel);

        String editedNamespace = String.format("C9517editedNamespace%s", RandomData.getRandomAlphanumeric());
        String editedPrefix = String.format("C9517editedPrefix%s", RandomData.getRandomAlphanumeric());
        String editedCreator = String.format("EditedCreator%s", RandomData.getRandomAlphanumeric());
        modelsToRemove.add(new CustomContentModel(name));

        modelManagerPage.navigate();
        modelManagerPage.usingModel(newModel)
            .clickActions().clickEdit()
                .assertNameFieldIsDisabled()
                .editNamespace(editedNamespace)
                .editPrefix(editedPrefix)
                .editCreator(editedCreator)
                .clickSave();

        newModel.setNamespacePrefix(editedNamespace);
        newModel.setNamespacePrefix(editedPrefix);
        newModel.setAuthor(editedCreator);
        modelManagerPage.usingModel(newModel)
            .assertModelNameSpaceIs(editedNamespace)
            .clickActions().clickEdit()
                .assertNamespaceIs(editedNamespace)
                .assertPrefixIs(editedPrefix)
                .assertCreatorIs(editedCreator)
                .clickCancel();
    }

    @TestRail (id = "C9518")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteModel()
    {
        String name = String.format("C9518testModel%s", RandomData.getRandomAlphanumeric());
        CustomContentModel modelToDelete = new CustomContentModel(name, name, name);
        createCustomModel(modelToDelete);

        modelManagerPage.navigate();
        modelManagerPage.usingModel(modelToDelete)
            .clickActions().clickDelete()
                .assertDeleteModelDialogIsDisplayed()
                .assertDeleteModelDialogTextIsCorrect(modelToDelete.getName())
                .assertCancelButtonIsDisplayed()
                .assertDeleteButtonIsDisplayed()
                .clickDelete();
        modelManagerPage.usingModel(modelToDelete).assertModelIsNotDisplayed();
    }

    @TestRail (id = "C9521")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deactivateModel()
    {
        String name = String.format("C9521testModel%s", RandomData.getRandomAlphanumeric());
        CustomContentModel modelToDeactivate = new CustomContentModel(name, name, name);
        modelsToRemove.add(modelToDeactivate);
        createCustomModel(modelToDeactivate);
        getRestApi().withPrivateAPI().usingCustomModel(modelToDeactivate).activateModel();
        modelManagerPage.navigate();
        modelManagerPage.usingModel(modelToDeactivate)
            .clickActions()
                .deactivateModel()
                .assertStatusIsInactive();
    }

    @TestRail (id = "C9519")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void exportModel()
    {
        String name = String.format("C9517testModel%s", RandomData.getRandomAlphanumeric());
        CustomContentModel modelToExport = new CustomContentModel(name, name, name);
        createCustomModel(modelToExport);
        modelsToRemove.add(modelToExport);

        modelManagerPage.navigate();
        modelManagerPage.usingModel(modelToExport)
            .clickActions().exportModel();
        assertTrue(isFileInDirectory(name, ".zip"), "The file was not found in the specified location");
    }

    @TestRail (id = "C9509, C9511")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void importModel()
    {
        String filePath = testDataFolder + "C9509TestModelName.zip";
        String name = "C9509TestModelName";
        CustomContentModel importedModel = new CustomContentModel(name, name, name);
        modelsToRemove.add(importedModel);

        modelManagerPage.navigate();
        modelManagerPage.clickImportModel()
            .assertImportModelDialogOpened()
            .assertImportModelTitleIsCorrect()
            .assertBrowserButtonIsDisplayed()
            .assertImportButtonDisplayed()
            .assertCancelButtonDisplayed()
            .importFile(filePath)
            .clickImportButton();
        modelManagerPage.usingModel(importedModel)
            .assertModelIsDisplayed()
            .assertModelNameSpaceIs(name)
            .assertStatusIsInactive();
    }

    @TestRail (id = "C42566")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createCustomType()
    {
        String name = String.format("C42566testModel%s", RandomData.getRandomAlphanumeric());
        CustomContentModel modelForCustomType = new CustomContentModel(name, name, name);
        RestCustomTypeModel newCustomType = new RestCustomTypeModel("TestCustomTypeName", "cm:content", "CustomTypeLabel");
        modelsToRemove.add(modelForCustomType);
        createCustomModel(modelForCustomType);

        modelManagerPage.navigate();
        modelManagerPage.usingModel(modelForCustomType)
            .openCustomModel()
                .assertCreateAspectButtonIsDisplayed()
                .assertCreateCustomTypeButtonDisplayed()
                .assertShowModelsButtonDisplayed()
                .clickCreateCustomType()
                    .assertCreateCustomTypeWindowDisplayed()
                    .typeName(newCustomType.getName())
                    .typeDisplayLabel(newCustomType.getTitle())
                    .clickCreate();
        modelManagerPage.usingCustomType(modelForCustomType, newCustomType)
            .assertCustomTypeIsDisplayed()
            .assertDisplayLabelIs(newCustomType.getTitle())
            .assertParentIs(newCustomType.getParentName())
            .assertLayoutIsNo();
    }

    @TestRail (id = "C42567")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createAspect()
    {
        String name = String.format("C42567testModel%s", RandomData.getRandomAlphanumeric());
        CustomContentModel modelForAspect = new CustomContentModel(name, name, name);
        CustomAspectModel newAspect = new CustomAspectModel("TestAspectName", "aspectNameLabel");
        modelsToRemove.add(modelForAspect);
        createCustomModel(modelForAspect);

        modelManagerPage.navigate();
        modelManagerPage.usingModel(modelForAspect)
            .openCustomModel()
                .clickCreateAspect()
                    .assertCreateAspectDialogIsOpened()
                    .typeName(newAspect.getName())
                    .typeDisplayLabel(newAspect.getTitle())
                    .clickCreate();
        modelManagerPage.usingAspect(modelForAspect, newAspect)
            .assertAspectIsDisplayed()
            .assertDisplayLabelIs(newAspect.getTitle())
            .assertLayoutIsNo();
    }

    @TestRail (id = "C42568")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void useImportedModel()
    {
        String[] defaultProperties = {"Name", "Title", "Description", "Author", "Mimetype", "Size", "Creator", "Created Date", "Modifier", "Modified Date"};
        String[] modelProperties = {"Title", "Modifier", "Creator"};
        String filePath = testDataFolder + "Marketing_content.zip";
        String name = "Marketing_content";
        CustomContentModel importedModel = new CustomContentModel(name, name, name);
        modelsToRemove.add(importedModel);

        modelManagerPage.navigate();
        modelManagerPage.clickImportModel()
            .importFile(filePath)
            .clickImportButton();
        modelManagerPage.usingModel(importedModel).assertModelIsDisplayed()
            .clickActions().activateModel();

        setupAuthenticatedSession(user);
        documentDetailsPage.navigate(file)
            .assertPropertiesAreDisplayed(defaultProperties)
            .clickChangeType()
                .selectOption("Marketing content (MKT:Marketing)")
                .clickOkButton();

        documentDetailsPage.assertPropertiesAreDisplayed(modelProperties);
    }

    private void createCustomModel(CustomContentModel customModel)
    {
        try
        {
            getRestApi().authenticateUser(getAdminUser())
                .withPrivateAPI().usingCustomModel().createCustomModel(customModel);
        }
        catch (Exception e)
        {
            getRestApi().authenticateUser(getAdminUser())
                .withPrivateAPI().usingCustomModel().createCustomModel(customModel);
        }
    }
}
