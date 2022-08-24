package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage.CreateMenuOption;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.FolderModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class ActionsCreateTests extends BaseTest
{
    private final String user = String.format("C8156User%s", RandomData.getRandomAlphanumeric());
    private final String userHomeFolderName = "User Homes";
    //@Autowired
    private NewFolderDialog createFolderFromTemplate;
    private RepositoryPage repositoryPage;
    //@Autowired
    private DocumentDetailsPage documentDetails;
    private MyFilesPage myFilesPage;
    //@Autowired
    private NewFolderDialog newFolderDialog;
    private UploadContent uploadContent;
    private final String password = "password";
    private UserModel testUser1;
    private DocumentLibraryPage2 documentLibraryPage;
    private DocumentLibraryPage documentLibrary;
    private final String templateContent = "template content";
    private final String folederNameC8158 = "Test Folder C8158";
    private final String foledeTitleC8158 = "Test Title C8158";
    private final String foledeDiscriptionC8158 = "C8158 FOlder Discription ";
    private final String folderTemplateName = "Software Engineering Project";
    String fileTitleC8156 = "C8156 file title";
    String fileTitleC8161 = "C8161 file title";
    String fileDiscriptionC8156 = "C8156 file Discription";
    String fileDiscriptionC8161 = "C8161 file Discription";
    String fileTitleC8162 = "C8162 file title";
    String fileDiscriptionC8162 = "C8162 file Discription";

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        repositoryPage = new RepositoryPage(webDriver);

        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createUser(user, password);
        getCmisApi().authenticateUser(getAdminUser());

        log.info("Create Folder and File in Admin Repository-> User Homes ");
        authenticateUsingLoginPage(getAdminUser());

       createFolderFromTemplate = new  NewFolderDialog(webDriver);
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
         documentLibrary = new DocumentLibraryPage(webDriver);
        documentDetails = new DocumentDetailsPage(webDriver);
        repositoryPage = new RepositoryPage(webDriver);
        myFilesPage = new MyFilesPage(webDriver);
        uploadContent = new UploadContent(webDriver);
        newFolderDialog = new NewFolderDialog(webDriver);
    }
    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(testUser1);
        log.info("Delete the Created Folder from Admin Page");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(userHomeFolderName);
        repositoryPage
            .select_ItemsAction(user, ItemActions.DELETE_FOLDER)
            .clickOnDeleteButtonOnDeletePrompt();
    }


    @TestRail (id = "C8156")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createPlainTextDocumentInRepository()
    {
        FileModel txtFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);

        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(userHomeFolderName);
        repositoryPage
            .click_FolderName(user);
        log.info("Click Create button -> click on plain text button and enter required details");
        documentLibraryPage
            .clickCreate()
            .clickTextPlain()
            .assertCreateContentPageIsOpened()
            .typeName(txtFile.getName())
            .typeTitle(fileTitleC8156)
            .typeDescription(fileDiscriptionC8156)
            .typeContent(txtFile.getContent())
            .clickCreate()
            .assertDocumentTitleEquals(txtFile)
            .assertFileContentEquals(FILE_CONTENT)
            .assertPropertyValueEquals(language.translate("property.mimetype"), "Plain Text");
    }


    @TestRail (id = "C8161")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createHTMLDocumentInRepository()
    {
        FileModel htmlFile = FileModel.getRandomFileModel(FileType.HTML, FILE_CONTENT);
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(userHomeFolderName);
        repositoryPage
            .click_FolderName(user);
        log.info("Click Create button -> click on HTML button and enter required details");
        documentLibraryPage
            .clickCreate()
            .clickHtml()
            .assertCreateContentPageIsOpened()
            .typeName(htmlFile.getName())
            .sendInputForHTMLContent(FILE_CONTENT)
            .typeTitle(fileTitleC8161)
            .typeDescription(fileDiscriptionC8161)
            .clickCreate()
            .assertDocumentTitleEquals(htmlFile)
            .assertFileContentEquals(FILE_CONTENT)
            .assertPropertyValueEquals(language.translate("property.mimetype"), "HTML");

    }


    @TestRail (id = "C8162")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createXMLFile()
    {
        FileModel xmlFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(userHomeFolderName);
        repositoryPage
            .click_FolderName(user);
        log.info("Click Create button -> click on XML File button and enter required details");
        documentLibraryPage
            .clickCreate()
            .clickXml()
            .assertCreateContentPageIsOpened()
            .typeName(xmlFile.getName())
            .typeTitle(fileTitleC8162)
            .typeDescription(fileDiscriptionC8162)
            .typeContent(FILE_CONTENT)
            .clickCreate()
            .assertDocumentTitleEquals(xmlFile)
            .assertFileContentEquals(FILE_CONTENT)
            .assertPropertyValueEquals(language.translate("property.mimetype"), "XML");

    }

    @TestRail (id = "C8159")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createDocumentFromTemplate()
    {
        authenticateUsingLoginPage(testUser1);
        log.info("Precondition: To Create a Template File");
        FolderModel nodeTemplates = new FolderModel("Node Templates");
        nodeTemplates.setCmisLocation("/Data Dictionary/Node Templates");
        FileModel templateFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, templateContent);
        getCmisApi().usingResource(nodeTemplates).createFile(templateFile);

        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(userHomeFolderName);
        repositoryPage
            .click_FolderName(user);

        log.info("STEP 1: Click 'Create' then 'Create file from template'.");
        myFilesPage
            .click_CreateButton()
            .click_CreateFromTemplateOption(CreateMenuOption.CREATE_DOC_FROM_TEMPLATE)
            .isTemplateDisplayed(templateFile.getName());;

        log.info("STEP 2: Select the template: 'templateFile'");
        myFilesPage
            .create_FileFromTemplate(templateFile);

        log.info("STEP 3: Verify the File is Present in Repositry");
        myFilesPage
            .assertIsContantNameDisplayed(templateFile.getName());

        log.info("Delete the Template File'");
        getCmisApi().usingResource(templateFile).delete();

    }

    @TestRail (id = "C8158")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void createFolderFromTemplateInRepository()
    {
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(userHomeFolderName);
        repositoryPage.click_FolderName(user);

        log.info(" Click 'Create' then 'Create folder from template'.");
        myFilesPage
            .click_CreateButton()
            .click_CreateFromTemplateOption(CreateMenuOption.CREATE_FOLDER_FROM_TEMPLATE)
            .isTemplateDisplayed("Software Engineering Project");

        log.info("Select the template: 'Software Engineering Project'");
        myFilesPage
            .clickOnTemplate(folderTemplateName);
        createFolderFromTemplate
            .assertIsNameFieldValueEquals(folderTemplateName);

        log.info("Insert data into input fields and save.");
        createFolderFromTemplate
            .fillInDetails(folederNameC8158, foledeTitleC8158, foledeDiscriptionC8158)
            .clickSave();
        log.info("erify the Folder is Present in Repository.");
        myFilesPage
            .assertIsFolderPresentInList(folederNameC8158);
    }

    @TestRail (id = "C13745")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkThatUserWithoutAdminPermissionsCannotCreateInMainRepository()
    {

        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        log.info("Step 1: Check the Create button.");
        documentLibrary
            .assertCreateButtonStatusDisabled()
            .assertUploadButtonStatusDisabled();
        log.info("Step 2: Click the Create button");
        documentLibrary
            .clickCreateButtonWithoutWait();
        documentLibrary
            .assertCreateContentMenuIsNotDisplayed();

    }

    @TestRail (id = " C13746")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkThatTheCreateOptionIsAvailableForAdminInMainRepository() {
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        log.info(" Check the Create button.");
        repositoryPage
            .assertCreateButtonStatusEnabled()
            .assertUploadButtonStatusEnabled();
        log.info("Click the Create button");
        documentLibraryPage
            .clickCreate();
        repositoryPage
            .assertCreateContentMenuIsDisplayed();
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkAllCreateAvailableActions()
    {
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(userHomeFolderName);
        repositoryPage
            .click_FolderName(user);
        log.info("Step 1: Click Create... button");
        documentLibraryPage
            .clickCreate();
        repositoryPage
            .assertareCreateOptionsAvailable();

    }
}
