package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import static org.testng.Assert.assertEquals;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.alfrescoContent.CreateFolderFromTemplate;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage.CreateMenuOption;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class ActionsCreateTests extends ContextAwareWebTest
{
    private final String user = String.format("C8156User%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("C8156SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("C8156SiteName%s", RandomData.getRandomAlphanumeric());
    private final String path = "Data Dictionary/Node Templates";
    private final String docName = String.format("C8159template2%s", RandomData.getRandomAlphanumeric());
    private final String docContent = "C8159 template content";
    private final String pathFolderTemplate = "Data Dictionary/Space Templates";
    private final String folderName = String.format("C8158%s", RandomData.getRandomAlphanumeric());
    @Autowired
    private CreateContent create;
    @Autowired
    private RepositoryPage repository;
    @Autowired
    private DocumentDetailsPage documentDetailsPage;
    @Autowired
    private CreateFolderFromTemplate createFolderFromTemplate;
    @Autowired
    private Notification notification;

    @BeforeClass (alwaysRun = true)

    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, docName, docContent);
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, pathFolderTemplate);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = false)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + docName);
        contentService.deleteContentByPath(adminUser, adminPassword, pathFolderTemplate + "/" + folderName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
    }

    @TestRail (id = "C8156")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createPlainTextDocumentInRepository()
    {
        // Preconditions
        setupAuthenticatedSession(user, password);
        repository.navigate();
        repository.clickFolderFromExplorerPanel("User Homes");
        repository.clickOnFolderName(user);

        LOG.info("Step 1: Click Create... button.");
        repository.clickCreateButton();

        LOG.info("Step 2: Click \"Plain Text...\" option.");
        repository.clickCreateContentOption(CreateMenuOption.PLAIN_TEXT);
        Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        create.sendInputForName("C8156 name");
        create.sendInputForContent("C8156 content");
        create.sendInputForTitle("C8156 title");
        create.sendInputForDescription("C8156 description");

        LOG.info("Step 4: Click the Create button");
        create.clickCreateButton();
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "Plain Text", "Mimetype property is not Plain Text");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getContentText(), "C8156 content", "\"C8156 content \" is not the content displayed in preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C8156 name", "\"C8156 name\" is not the file name for the file in preview");
    }

    @TestRail (id = "C8161")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createHTMLDocumentInRepository()
    {
        // Preconditions
        setupAuthenticatedSession(user, password);
        repository.navigate();
        repository.clickFolderFromExplorerPanel("User Homes");
        repository.clickOnFolderName(user);

        LOG.info("Step 1: Click Create... button");
        repository.clickCreateButton();

        LOG.info("Step 2: Click \"HTML...\" option.");
        repository.clickCreateContentOption(CreateMenuOption.HTML);
        Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        create.sendInputForName("C8161 test name");
        create.sendInputForHTMLContent("C8161 test content");
        create.sendInputForTitle("C8161 test title");
        create.sendInputForDescription("C8161 test description");

        LOG.info("Step 4: Click the Create button");
        create.clickCreateButton();
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "HTML", "Mimetype property is not HTML");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C8161 test name", "\"C8161 test name\" is not the file name for the file in preview");
    }

    @TestRail (id = "C8162")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void createXMLFile()
    {
        // Preconditions
        setupAuthenticatedSession(user, password);
        repository.navigate();
        repository.clickFolderFromExplorerPanel("User Homes");
        repository.clickOnFolderName(user);

        LOG.info("Step 1: Click Create... button");
        repository.clickCreateButton();

        LOG.info("Step 2: Click \"XML...\" option.");
        repository.clickCreateContentOption(CreateMenuOption.XML);
        Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        create.sendInputForName("C8162 test name");
        create.sendInputForContent("C8162 test content");
        create.sendInputForTitle("C8162 test title");
        create.sendInputForDescription("C8162 test description");

        LOG.info("Step 4: Click the Create button");
        create.clickCreateButton();
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "XML", "Mimetype property is not Plain Text");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getContentText().trim(), "C8162 test content",
            "\"C8162 test content \" is not the content displayed in preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C8162 test name", "\"C8162 test name\" is not the file name for the file in preview");
    }

    @TestRail (id = "C8159")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void createDocumentFromTemplate()
    {
        // Preconditions
        setupAuthenticatedSession(user, password);
        repository.navigate();
        repository.clickFolderFromExplorerPanel("User Homes");
        repository.clickOnFolderName(user);

        LOG.info("Step 1:Click 'Create' then click 'Create document from template'.");
        repository.clickCreateButton();
        create.clickCreateFromTemplateButton("Create document from template");
        Assert.assertTrue(create.isTemplateDisplayed(docName), "Template is not displayed");

        LOG.info("Step 2: Select the template and check that the new file is created with the content from the template used");
        create.clickOnTemplate(docName, repository);
        Assert.assertTrue(repository.isContentNameDisplayed(docName), "Newly created document is not displayed in Repository/UserHomes ");
        repository.clickOnFile(docName);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Document is not previewed");
        Assert.assertEquals(documentDetailsPage.getContentText(), docContent);
    }

    @TestRail (id = "C8158")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void createFolderFromTemplateInRepository()
    {
        // Preconditions
        setupAuthenticatedSession(user, password);
        repository.navigate();
        repository.clickFolderFromExplorerPanel("User Homes");
        repository.clickOnFolderName(user);

        LOG.info("Step 1: Select the Create menu > Create folder from templates");
        repository.clickCreateButton();
        create.clickCreateFromTemplateButton("Create folder from template");
        Assert.assertTrue(create.isTemplateDisplayed(folderName), "Template is not displayed");

        LOG.info("Step 2: Select the test template, provide title and description and check that the new folder is created");

        create.clickOnTemplate(folderName, createFolderFromTemplate);
        Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed());
        Assert.assertEquals(createFolderFromTemplate.getNameFieldValue(), folderName);

        LOG.info("Step 3: Provide data for Create Folder From Template Form");
        createFolderFromTemplate.fillInDetails(folderName, "C8158 Test Title", "C8158 Test Description");
        createFolderFromTemplate.clickSaveButton();

        assertEquals(notification.getDisplayedNotification(), String.format("Folder '%s' created", folderName));
        notification.waitUntilNotificationDisappears();
        Assert.assertTrue(repository.getFoldersList().contains(folderName), "Subfolder not found");
        Assert.assertTrue(repository.getExplorerPanelDocuments().contains(folderName), "Subfolder not found in Documents explorer panel");
    }

    @TestRail (id = "C13745")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkThatUserWithoutAdminPermissionsCannotCreateInMainRepository()
    {
        setupAuthenticatedSession(user, password);
        repository.navigate();
        LOG.info("Step 1: Check the Create button.");
        Assert.assertEquals(repository.getCreateButtonStatusDisabled(), "true", "The Create Button is not disabled");
        Assert.assertEquals(repository.getUploadButtonStatusDisabled(), "true", "The Upload Button is not disabled");

        LOG.info("Step 2: Click the Create button");
        repository.clickCreateButtonWithoutWait();
        Assert.assertFalse(repository.isCreateContentMenuDisplayed(), "Create Content menu is displayed when the Create button is clicked");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = " C13746")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkThatTheCreateOptionIsAvailableForAdminInMainRepository()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        repository.navigate();
        LOG.info("Step 1: Check the Create button.");
        Assert.assertEquals(repository.getCreateButtonStatusDisabled(), null, "The Create Button is disabled");
        Assert.assertEquals(repository.getUploadButtonStatusDisabled(), null, "The Upload Button is disabled");

        LOG.info("Step 2: Click the Create button");
        repository.clickCreateButtonWithoutWait();
        Assert.assertTrue(repository.isCreateContentMenuDisplayed(), "Create Content menu is not displayed when the Create button is clicked");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkAllCreateAvailableActions()
    {
        SoftAssert softAssert = new SoftAssert();
        // Preconditions
        setupAuthenticatedSession(user, password);
        repository.navigate();
        repository.clickFolderFromExplorerPanel("User Homes");
        repository.clickOnFolderName(user);

        LOG.info("Step 1: Click Create... button");
        repository.clickCreateButton();
        softAssert.assertTrue(repository.areCreateOptionsAvailable(), "Create menu options are not available");
        softAssert.assertTrue(create.isCreateFromTemplateAvailable("Create document from template"), "Create... Create document from template is not displayed");
        softAssert.assertTrue(create.isCreateFromTemplateAvailable("Create folder from template"), "Create... Create folder from template is not displayed");

        softAssert.assertAll();
    }
}
