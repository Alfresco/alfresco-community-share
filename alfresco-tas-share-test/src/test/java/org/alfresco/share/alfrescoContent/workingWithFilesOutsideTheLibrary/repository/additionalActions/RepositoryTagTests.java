package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository.additionalActions;


import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;

import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class RepositoryTagTests extends BaseTest
{
    private final String tagC8266 = RandomStringUtils.randomAlphabetic(4).toLowerCase();
    private final String anothertagC8266 = RandomStringUtils.randomAlphabetic(4).toLowerCase();
    private final String tagC8267 = RandomStringUtils.randomAlphabetic(4).toLowerCase();
    private final String tagC8278 = RandomStringUtils.randomAlphabetic(4).toLowerCase();

    //@Autowired
    //private RepositoryPage repositoryPage;
    //@Autowired
    private EditPropertiesDialog editPropertiesDialog;
    //@Autowired
    private SelectDialog selectDialog;
    private RepositoryPage repositoryPage;
    private final String password = "password";
    private final String user = String.format("TestUser%s", RandomData.getRandomAlphanumeric());
    private UserModel testUser1;
    private FolderModel folderToCheck;
     FileModel fileToCheck;
    private DocumentLibraryPage2 documentLibraryPage;


    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        repositoryPage = new RepositoryPage(webDriver);

        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createUser(user, password);
        getCmisApi().authenticateUser(getAdminUser());

        log.info("Create Folder and File in Admin Repository-> User Homes ");
        authenticateUsingLoginPage(getAdminUser());

        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingAdmin().usingUserHome(user).createFolder(folderToCheck).assertThat().existsInRepo();

        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().usingAdmin().usingUserHome(user).createFile(fileToCheck).assertThat().existsInRepo();
        authenticateUsingCookies(getAdminUser());

       editPropertiesDialog = new  EditPropertiesDialog(webDriver);
        selectDialog = new SelectDialog(webDriver);
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        repositoryPage = new RepositoryPage(webDriver);

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
            .click_FolderName("User Homes");
        repositoryPage.select_ItemsAction(user, ItemActions.DELETE_FOLDER)
            .clickOnDeleteButtonOnDeletePrompt();
    }


    @TestRail (id = "C8266")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createTagForFile()  {
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName("User Homes");
        repositoryPage
            .click_FolderName(user);
        repositoryPage
            .assertFileIsDisplayed(fileToCheck.getName());
        log.info("STEP1: Hover over one tag from the content name");
        repositoryPage
            .mouseOverNoTags(fileToCheck.getName());
        log.info("STEP2: Click \"Edit Tag\" icon");
        documentLibraryPage.usingContent(fileToCheck)
            .clickTagEditIcon();
        repositoryPage
            .assertEditTagInputFieldDisplayed(fileToCheck.getName());
        documentLibraryPage.usingContent(fileToCheck)
             .setTag(tagC8266)
            .clickSave();
        documentLibraryPage.usingContent(fileToCheck)
            .assertTagIsDisplayed(tagC8266);

    }


    @TestRail (id = "C8267")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createTagForFolder()
    {
     authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName("User Homes");
        repositoryPage
            .click_FolderName(user);
        repositoryPage
            .assertFileIsDisplayed(folderToCheck.getName());
        log.info(" Hover over one tag from the content name");
        repositoryPage
            .mouseOverNoTags(folderToCheck.getName());
        log.info("Click Edit Tag icon");
        documentLibraryPage.usingContent(folderToCheck)
            .clickTagEditIcon();
        repositoryPage
            .assertEditTagInputFieldDisplayed(folderToCheck.getName());
        log.info("Add tag and click save");
        documentLibraryPage.usingContent(folderToCheck)
            .setTag(tagC8267)
            .clickSave();
        documentLibraryPage.usingContent(folderToCheck)
            .assertTagIsDisplayed(tagC8267);

    }

    @TestRail (id = "C8278")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addExistingTag()  {
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName("User Homes");
        repositoryPage
            .click_FolderName(user);
        repositoryPage
            .assertFileIsDisplayed(fileToCheck.getName());
        log.info("STEP1: Hover over one tag from the content name");
        repositoryPage
            .mouseOverNoTags(fileToCheck.getName());
        log.info("STEP2: Click Edit Properties option and click on select button");
        documentLibraryPage.usingContent(fileToCheck)
            .clickEditProperties()
            .clickSelectTags();
        log.info("STEP4: Pick tag from the available tags list and click \"Add\" then click OK");
        selectDialog.typeTag(tagC8278.toLowerCase())
            .clickCreateNewIcon()
            .assertTagIsSelected(tagC8278.toLowerCase())
            .clickOk();
        log.info("STEP5: Click \"Save\" button");
        editPropertiesDialog
            .clickSave();
        documentLibraryPage.usingContent(fileToCheck)
            .assertTagIsDisplayed(tagC8278);
}


    @TestRail (id = "C8290")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editTag() throws Exception {
        String originalTag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        String editedTag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        setAuthorizationRequestHeader(getRestApi().authenticateUser(getAdminUser()))
            .withCoreAPI().usingResource(fileToCheck).addTags(originalTag);
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName("User Homes");
        repositoryPage
            .click_FolderName(user);
        repositoryPage
            .assertFileIsDisplayed(fileToCheck.getName());
        log.info("STEP1: Hover over the tag(s) from the content");
        repositoryPage
            .mouseOverTags(fileToCheck.getName());
        log.info("STEP2: Click \"Edit Tag\" icon");
        repositoryPage
            .clickEditTagIcon(fileToCheck.getName());
        repositoryPage
            .assertEditTagInputFieldDisplayed(fileToCheck.getName());
        log.info("STEP3: Click on any tag and type a valid tag name");
        repositoryPage
            .editTag(originalTag, editedTag);
        log.info("STEP4: Click \"Save\" link and verify the content tags");
        documentLibraryPage.usingContent(fileToCheck)
            .clickSave();
        documentLibraryPage.usingContent(fileToCheck)
            .assertTagIsDisplayed(editedTag)
            .assertTagIsNotDisplayed(originalTag);
    }

    @TestRail (id = "C8291")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void removeTag() throws Exception {
        String originalTag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        setAuthorizationRequestHeader(getRestApi().authenticateUser(getAdminUser()))
            .withCoreAPI().usingResource(fileToCheck).addTags(originalTag);
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName("User Homes");
        repositoryPage
            .click_FolderName(user);
        repositoryPage
            .assertFileIsDisplayed(fileToCheck.getName());
        log.info("STEP1: Hover over the tag(s) from the content");
        repositoryPage
            .mouseOverTags(fileToCheck.getName());
        log.info("STEP2: Click \"Edit Tag\" icon");
        repositoryPage
            .clickEditTagIcon(fileToCheck.getName());
        repositoryPage
            .assertEditTagInputFieldDisplayed(fileToCheck.getName());
        log.info("STEP3: Hover over the tag and click 'Remove' icon");
        repositoryPage.removeTag(originalTag);
        log.info("STEP4: Click 'Save' link");
        documentLibraryPage.usingContent(fileToCheck)
            .clickSave();
        repositoryPage.assertIsNoTagsTextDisplayed(fileToCheck.getName());

    }

    @TestRail (id = "C8300")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void updateTags() throws Exception {
        setAuthorizationRequestHeader(getRestApi().authenticateUser(getAdminUser()))
            .withCoreAPI().usingResource(fileToCheck).addTags(tagC8266);
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName("User Homes");
        repositoryPage
            .click_FolderName(user);
        repositoryPage
            .assertFileIsDisplayed(fileToCheck.getName());
        log.info(" Hover over one tag from the content name");
        repositoryPage
            .mouseOverTags(fileToCheck.getName());
        log.info(" Click Edit Tag icon");
        documentLibraryPage.usingContent(fileToCheck)
            .clickTagEditIcon();
        repositoryPage
            .assertEditTagInputFieldDisplayed(fileToCheck.getName());
        log.info(" Add another tag");
        documentLibraryPage.usingContent(fileToCheck)
            .setTag(anothertagC8266)
            .clickSave();
        documentLibraryPage.usingContent(fileToCheck)
            .assertTagIsDisplayed(anothertagC8266);
        log.info("S Click 'Remove' icon");
        documentLibraryPage.usingContent(fileToCheck)
            .clickTagEditIcon();
        repositoryPage
            .removeTag(tagC8266);
        log.info(" Click 'Save' link");
        documentLibraryPage.usingContent(fileToCheck)
            .clickSave();
        documentLibraryPage.usingContent(fileToCheck)
            .assertTagIsDisplayed(anothertagC8266)
            .assertTagIsNotDisplayed(tagC8266);

    }

}
