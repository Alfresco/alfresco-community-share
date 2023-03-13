package org.alfresco.share.adminTools.modelManager;

import static org.alfresco.common.Utils.isFileInDirectory;
import static org.alfresco.common.Utils.testDataFolder;
import static org.alfresco.utility.data.RandomData.getRandomAlphanumeric;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateCustomTypeDialog;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.alfresco.rest.model.RestCustomModel;
import org.alfresco.rest.model.RestCustomTypeModel;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.CustomAspectModel;
import org.alfresco.utility.model.CustomContentModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class ModelManagerTests extends BaseTest
{
    private final String ACTIVE = "ACTIVE";

    private ModelManagerPage modelManagerPage;
    private DocumentDetailsPage documentDetailsPage;
    private CreateCustomTypeDialog createCustomTypeDialog;

    private UserModel user;
    private SiteModel site;

    private final List<CustomContentModel> modelsToRemove = Collections.synchronizedList(new ArrayList<>());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
    }

    @BeforeMethod(alwaysRun = true)
    public void precondition()
    {
        modelManagerPage = new ModelManagerPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        createCustomTypeDialog = new CreateCustomTypeDialog(webDriver);

        authenticateUsingLoginPage(getAdminUser());
    }

    @TestRail (id = "C42565")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createModel()
    {
        String name = String.format("C42565Name-%s", getRandomAlphanumeric());
        String nameSpace = String.format("C42565Namespace-%s", getRandomAlphanumeric());
        String prefix = String.format("C42565-%s", getRandomAlphanumeric());
        CustomContentModel newModel = new CustomContentModel(name, nameSpace, prefix);
        modelsToRemove.add(newModel);

        modelManagerPage.navigate();
        modelManagerPage.createModel(newModel)
            .usingModel(newModel)
            .assertModelIsDisplayed()
            .assertModelNameSpaceIs(nameSpace)
            .assertStatusIsInactive()
            .clickActions()
            .assertActionsAreAvailable(
                language.translate("modelManager.action.activate"),
                language.translate("modelManager.action.edit"),
                language.translate("modelManager.action.delete"),
                language.translate("modelManager.action.export"));
    }

    @TestRail (id = "C9516, C9520")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void activateModel()
    {
        String name = String.format("C9516Model-%s", getRandomAlphanumeric());
        CustomContentModel newModel = new CustomContentModel(name, name, name);
        modelManagerPage.navigate();
        modelsToRemove.add(newModel);
        modelManagerPage.createModel(newModel)
            .usingModel(newModel).assertModelIsDisplayed()
            .clickActions().activateModel()
            .assertStatusIsActive()
            .clickActions()
            .assertActionsAreAvailable(
                    language.translate("modelManager.action.deactivate"),
                    language.translate("modelManager.action.export"));
    }

    @TestRail (id = "C9517")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void editModel()
    {
        String name = String.format("C9517-%s", getRandomAlphanumeric());
        CustomContentModel newModel = new CustomContentModel(name, name, name);
        createCustomModel(newModel);

        String editedNamespace = String.format("C9517editedNamespace%s", getRandomAlphanumeric());
        String editedPrefix = String.format("C9517editedPrefix%s", getRandomAlphanumeric());
        String editedCreator = String.format("EditedCreator%s", getRandomAlphanumeric());
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
        String name = String.format("C9518testModel%s", getRandomAlphanumeric());
        CustomContentModel modelToDelete = new CustomContentModel(name, name, name);
        createCustomModel(modelToDelete);

        modelManagerPage.navigate();
        modelManagerPage.usingModel(modelToDelete)
            .clickActions().clickDelete()
            .assertDeleteModelDialogIsDisplayed()
            .assertDeleteModelDialogTextIsCorrect(modelToDelete.getName())
            .clickDelete();
        modelManagerPage.navigate()
            .usingModel(modelToDelete).assertModelIsNotDisplayed();
    }

    @TestRail (id = "C9521")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deactivateModel()
    {
        String name = String.format("C9521testModel%s", getRandomAlphanumeric());
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

    public void exportModel()
    {
        String name = String.format("C9517testModel%s", getRandomAlphanumeric());
        File exportedFile = new File(System.getProperty("user.dir")
            + File.separator + "testdata" + File.separator + name.concat(".zip"));
        exportedFile.deleteOnExit();
        CustomContentModel modelToExport = new CustomContentModel(name, name, name);
        createCustomModel(modelToExport);
        modelsToRemove.add(modelToExport);

        modelManagerPage.navigate();
        modelManagerPage.usingModel(modelToExport)
            .clickActions().exportModel();
        assertTrue(isFileInDirectory(exportedFile), "The file was not found in the specified location");
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
        String name = String.format("C42566testModel%s", getRandomAlphanumeric());
        CustomContentModel modelForCustomType = new CustomContentModel(name, name, name);
        RestCustomTypeModel newCustomType = new RestCustomTypeModel("TestCustomTypeName", "cm:content", "CustomTypeLabel");
        modelsToRemove.add(modelForCustomType);
        createCustomModel(modelForCustomType);

        modelManagerPage.navigate();
        modelManagerPage.usingModel(modelForCustomType)
            .openCustomModel()
            .clickCreateCustomType()
            .typeName(newCustomType.getName())
            .typeDisplayLabel(newCustomType.getTitle());

        createCustomTypeDialog.clickCreate();
        modelManagerPage.usingCustomType(modelForCustomType, newCustomType)
            .assertDisplayLabelIs(newCustomType.getTitle())
            .assertLayoutIsNo();
    }

    @TestRail (id = "C42567")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createAspect()
    {
        String name = String.format("C42567testModel%s", getRandomAlphanumeric());
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

        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "content");
        getCmisApi().authenticateUser(user)
            .usingSite(site).createFile(file).assertThat().existsInRepo();

        modelManagerPage.navigate().clickImportModel()
            .importFile(filePath)
            .clickImportButton();
        modelManagerPage.usingModel(importedModel).assertModelIsDisplayed()
            .clickActions().activateModel();

        authenticateUsingCookies(user);
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
     //       getRestApi().authenticateUser(getAdminUser())
      //          .withPrivateAPI().usingCustomModel().createCustomModel(customModel);
            setAuthorizationRequestHeader(getRestApi().authenticateUser(getAdminUser()))
                .withPrivateAPI().usingCustomModel().createCustomModel(customModel);

        }
        catch (Exception e)
        {
            log.error("Failed to create custom model {}. Error: {}", customModel.getName(), e.getMessage());
      //      getRestApi().authenticateUser(getAdminUser())
        //        .withPrivateAPI().usingCustomModel().createCustomModel(customModel);
            setAuthorizationRequestHeader(getRestApi().authenticateUser(getAdminUser()))
                .withPrivateAPI().usingCustomModel().createCustomModel(customModel);


        }
    }

    private void deleteCustomModel(CustomContentModel customContentModel)
    {
     //   getRestApi().authenticateUser(dataUser.getAdminUser());
        setAuthorizationRequestHeader(getRestApi().authenticateUser(getAdminUser()));

        RestCustomModel restCustomModel = getRestApi().withPrivateAPI().usingCustomModel(customContentModel).getModel();
        if (getRestApi().getStatusCode().equals(String.valueOf(HttpStatus.OK.value())))
        {
            if (restCustomModel.getStatus().equals(ACTIVE))
            {
                getRestApi().withPrivateAPI().usingCustomModel(restCustomModel).deactivateModel();
            }
            getRestApi().withPrivateAPI().usingCustomModel(restCustomModel).deleteModel();
        }
    }

    @AfterClass (alwaysRun = true)
    public void afterClass()
    {
        dataSite.usingUser(user).deleteSite(site);
        getUserService().emptyTrashcan(user.getUsername(), user.getPassword());

        deleteUsersIfNotNull(user);
//        modelsToRemove.forEach(this::deleteCustomModel);
    }
}
