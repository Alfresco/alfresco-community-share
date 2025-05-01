package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class MyFilesActions extends BaseTest
{
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private Toolbar toolbar;
    private MyFilesPage myFilesPage;
    private DocumentDetailsPage documentDetailsPage;
    private DocumentLibraryPage2 documentLibraryPage;
    private CreateContentPage createContent;
    private NewFolderDialog createFolder;
    private DocumentLibraryPage documentLibraryPages;
    private final String folderName = "Software Engineering Project";
    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        toolbar = new Toolbar(webDriver);
        myFilesPage = new MyFilesPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        createContent = new CreateContentPage(webDriver);
        createFolder = new NewFolderDialog(webDriver);
        documentLibraryPages = new DocumentLibraryPage(webDriver);


        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());
        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C7656")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkMoveFileToMyFiles()
    {
        FileModel fileToMove = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get()).createFile(fileToMove);

        documentLibraryPage.navigate(site.get())
            .usingContent(fileToMove)
            .clickMoveTo();
        documentLibraryPage.clickOnMyFiles();
        documentLibraryPage.clickOkButton();
        toolbar.clickMyFiles();

        documentLibraryPage.usingContent(fileToMove).assertContentIsDisplayed();
    }

    @TestRail (id = "C7657")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelMoveFileToMyFiles()
    {
        FileModel fileToMove = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get()).createFile(fileToMove);

        documentLibraryPage.navigate(site.get())
            .usingContent(fileToMove)
            .clickMoveTo();
        documentLibraryPage.clickOnMyFiles();
        documentLibraryPage.clickOnCancelMyFiles();

        documentLibraryPage.usingContent(fileToMove).assertContentIsDisplayed();
    }

    @TestRail (id = "C7654")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void copyFileToMyFiles()
    {
        FileModel fileToCopy = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get()).createFile(fileToCopy);

        documentLibraryPage.navigate(site.get())
            .usingContent(fileToCopy)
            .clickCopyTo();
        documentLibraryPage.clickOnMyFiles();
        documentLibraryPage.clickOkButton();

        toolbar.clickMyFiles();
        documentLibraryPage.usingContent(fileToCopy).assertContentIsDisplayed();
    }

    @TestRail (id = "C7655")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelCopyFileToMyFiles()
    {
        FileModel fileToCopy = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get()).createFile(fileToCopy);

        documentLibraryPage.navigate(site.get())
            .usingContent(fileToCopy)
            .clickCopyTo();
        documentLibraryPage.clickOnMyFiles();
        documentLibraryPage.clickOnCancelMyFiles();

        documentLibraryPage.usingContent(fileToCopy).assertContentIsDisplayed();
    }

    @TestRail (id = "C7652")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void createFolderAndContentInMyFiles()
    {
        log.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");

        log.info("STEP 1: Click 'Create' then 'Create folder'.");
        myFilesPage
            .click_CreateButton()
            .click_CreateFromTemplateOption(DocumentLibraryPage.CreateMenuOption.CREATE_FOLDER_FROM_TEMPLATE)
            .isTemplateDisplayed("Software Engineering Project");

        log.info("STEP 2: Select the template: 'Software Engineering Project'");
        myFilesPage
            .clickOnTemplate(folderName);
        createFolder
            .assertIsNameFieldValueEquals(folderName);

        log.info("STEP 3: Insert data into input fields and save.");
        createFolder
            .fillInDetails("Test Folder", "Test Title", "Test Description")
            .clickSave();

        documentLibraryPages.clickOnFolderName("Test Folder");

        log.info("Step 4: Click Create... button");
        myFilesPage.click_CreateButton();

        log.info("Step 5: Click \"Plain Text...\" option.");
        myFilesPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .assertBrowserPageTitleIs("Alfresco » Create Content");

        log.info("Step 6: Fill in the name, content, title and description fields");
        createContent
            .typeName("C7650 test name")
            .typeContent("C7650 test content")
            .typeTitle("C7650 test title")
            .typeDescription("C7650 test description");

        log.info("Step 7: Click the Create button");
        createContent
            .clickCreate();
        documentDetailsPage
            .assertPageTitleEquals("Alfresco » Document Details");

        log.info("Step 8 : Verify the mimetype for the created file.");
        documentDetailsPage
            .assertPropertyValueEquals("Mimetype", "Plain Text");

        log.info("Step 9: Verify the document's preview");
        documentDetailsPage
            .assertFileContentEquals("C7650 test content")
            .assertIsFileNameDisplayedOnPreviewPage("C7650 test name");
    }

    @TestRail (id = "C7674")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkMyFilesDocumentFilter()
    {
        FileModel fileToMove = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get()).createFile(fileToMove);

        documentLibraryPage.navigate(site.get())
            .usingContent(fileToMove)
            .clickMoveTo();
        documentLibraryPage.clickOnMyFiles();
        documentLibraryPage.clickOkButton();
        toolbar.clickMyFiles();
        documentLibraryPage.clickOnAllDocuments();
        documentLibraryPage.usingContent(fileToMove).assertContentIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}