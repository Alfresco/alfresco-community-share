package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.additionalActions;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.FileType;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Laura.Capsa
 */
public class TagTests extends BaseTest
{
    private final String user = "User" + RandomData.getRandomAlphanumeric();
    private UserModel testUser2;
    private final String user2 = "User2" + RandomData.getRandomAlphanumeric();

  //  @Autowired
    private EditPropertiesDialog editPropertiesDialog;
   //@Autowired
    private SelectDialog selectDialog;
    private final String password = "password";
    private UserModel testUser1;
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private RepositoryPage repositoryPage;
    //@Autowired
    private DocumentLibraryPage2 documentLibraryPage;
    private final String sharedFolderName = "Shared";
    private final String tagC8096 = RandomStringUtils.randomAlphabetic(4).toLowerCase();
    private final String anothertagC8096 = RandomStringUtils.randomAlphabetic(4).toLowerCase();
    private FolderModel folderToCheck;
    FileModel fileToCheck;
    private final String tagC8063 = RandomStringUtils.randomAlphabetic(4).toLowerCase();
    private final String tagC8062 = RandomStringUtils.randomAlphabetic(4).toLowerCase();
    private final String tagC8074 = RandomStringUtils.randomAlphabetic(4).toLowerCase();

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {

        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createUser(user, password);
        getCmisApi().authenticateUser(getAdminUser());
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().usingUser(testUser1).usingShared().createFile(fileToCheck).assertThat().existsInRepo();

         editPropertiesDialog = new EditPropertiesDialog(webDriver);
        selectDialog = new  SelectDialog(webDriver);
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        repositoryPage = new RepositoryPage(webDriver);
        repositoryPage = new RepositoryPage(webDriver);
    }
    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(testUser1);
    }

    @TestRail (id = "C8062")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createTagForFile() {

        log.info("Login User1 with admin permissions and navigate to shared folder");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);

        repositoryPage
            .assertFileIsDisplayed(fileToCheck.getName());
        log.info("STEP1: Hover over one tag from the content name");
        repositoryPage
            .mouseOverNoTags(fileToCheck.getName());
        log.info("STEP2: Click \"Edit Tag\" icon");
        documentLibraryPage
            .usingContent(fileToCheck)
            .clickTagEditIcon();
        repositoryPage
            .assertEditTagInputFieldDisplayed(fileToCheck.getName());
        documentLibraryPage.usingContent(fileToCheck)
            .setTag(tagC8062)
            .clickSave();
        documentLibraryPage.usingContent(fileToCheck)
            .assertTagIsDisplayed(tagC8062);
        log.info(" Delete File");
        repositoryPage
            .select_ItemsAction(fileToCheck.getName(), ItemActions.DELETE_DOCUMENT)
            .clickOnDeleteButtonOnDeletePrompt();

    }


    @TestRail (id = "C8063")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createTagForFolder() throws Exception {
        log.info("Precondition: create folder in Shared folder from user2 ");
        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingUser(testUser1).usingShared().createFolder(folderToCheck).assertThat().existsInRepo();

        log.info("Login User1 with admin permissions and navigate to shared folder");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        repositoryPage
            .assertFileIsDisplayed(folderToCheck.getName());


        repositoryPage
            .mouseOverNoTags(folderToCheck.getName());
        log.info("Click Edit Tag icon");
        documentLibraryPage
            .usingContent(folderToCheck)
            .clickTagEditIcon();
        repositoryPage
            .assertEditTagInputFieldDisplayed(folderToCheck.getName());
        log.info("Add tag and click save");
        documentLibraryPage.usingContent(folderToCheck)
            .setTag(tagC8063)
            .clickSave();
        documentLibraryPage.usingContent(folderToCheck)
            .assertTagIsDisplayed(tagC8063);
        log.info(" Delete Folder");
        repositoryPage
            .select_ItemsAction(folderToCheck.getName(), ItemActions.DELETE_DOCUMENT)
            .clickOnDeleteButtonOnDeletePrompt();
        repositoryPage
            .select_ItemsAction(fileToCheck.getName(), ItemActions.DELETE_DOCUMENT)
            .clickOnDeleteButtonOnDeletePrompt();

    }

    @TestRail (id = "C8074")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addExistingTag() {
        log.info("Login User1 with admin permissions and navigate to shared folder");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);

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
        selectDialog.typeTag(tagC8074)
            .clickCreateNewIcon()
            .assertTagIsSelected(tagC8074)
            .clickOk();
        log.info("STEP5: Click \"Save\" button");
        editPropertiesDialog
            .clickSave();
        documentLibraryPage.usingContent(fileToCheck)
            .assertTagIsDisplayed(tagC8074);

        log.info(" Delete File");
        repositoryPage
            .select_ItemsAction(fileToCheck.getName(), ItemActions.DELETE_DOCUMENT)
            .clickOnDeleteButtonOnDeletePrompt();
    }


    @TestRail (id = "C8086")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editTagForASharedFile()  {
        String originalTag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        String editedTag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        setAuthorizationRequestHeader(getRestApi().authenticateUser(getAdminUser()))
            .withCoreAPI().usingResource(fileToCheck).addTags(originalTag);

        log.info("Login User1 with admin permissions and navigate to shared folder");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);

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

        log.info(" Delete File");
        repositoryPage
            .select_ItemsAction(fileToCheck.getName(), ItemActions.DELETE_DOCUMENT)
            .clickOnDeleteButtonOnDeletePrompt();

    }

    @TestRail (id = "C8087")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void removeTag() {
        String originalTag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        setAuthorizationRequestHeader(getRestApi().authenticateUser(getAdminUser()))
            .withCoreAPI().usingResource(fileToCheck).addTags(originalTag);

        log.info("Login User1 with admin permissions and navigate to shared folder");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);

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
        repositoryPage
            .removeTag(originalTag);
        log.info("STEP4: Click 'Save' link");
        documentLibraryPage
            .usingContent(fileToCheck)
            .clickSave();
        repositoryPage
            .assertIsNoTagsTextDisplayed(fileToCheck.getName());
        log.info(" Delete File");
        repositoryPage
            .select_ItemsAction(fileToCheck.getName(), ItemActions.DELETE_DOCUMENT)
            .clickOnDeleteButtonOnDeletePrompt();

    }

    @TestRail (id = "C8096")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void updateTags() {

        setAuthorizationRequestHeader(getRestApi().authenticateUser(getAdminUser()))
            .withCoreAPI().usingResource(fileToCheck).addTags(tagC8096);

        log.info("Login User1 with admin permissions and navigate to shared folder");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);

        repositoryPage
            .assertFileIsDisplayed(fileToCheck.getName());
        log.info(" Hover over one tag from the content name");
        repositoryPage
            .mouseOverTags(fileToCheck.getName());
        log.info(" Click Edit Tag icon");
        documentLibraryPage
            .usingContent(fileToCheck)
            .clickTagEditIcon();
        repositoryPage
            .assertEditTagInputFieldDisplayed(fileToCheck.getName());
        log.info(" Add another tag");
        documentLibraryPage
            .usingContent(fileToCheck)
            .setTag(anothertagC8096)
            .clickSave();
        documentLibraryPage
            .usingContent(fileToCheck)
            .assertTagIsDisplayed(anothertagC8096);
        log.info("S Click 'Remove' icon");
        documentLibraryPage
            .usingContent(fileToCheck)
            .clickTagEditIcon();
        repositoryPage
            .removeTag(tagC8096);
        log.info(" Click 'Save' link");
        documentLibraryPage
            .usingContent(fileToCheck)
            .clickSave();
        documentLibraryPage
            .usingContent(fileToCheck)
            .assertTagIsDisplayed(anothertagC8096)
            .assertTagIsNotDisplayed(tagC8096);
        log.info(" Delete File");
        repositoryPage
            .select_ItemsAction(fileToCheck.getName(), ItemActions.DELETE_DOCUMENT)
            .clickOnDeleteButtonOnDeletePrompt();
    }

    @TestRail (id = "C13766")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void noTagsOptionDisplayed() {
        testUser2 = dataUser.usingAdmin().createUser(user2, password);

        authenticateUsingLoginPage(testUser2);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        repositoryPage
            .assertFileIsDisplayed(fileToCheck.getName());
        log.info("STEP1: Hover over no tag and verify Edit icon is not displayed ");
        repositoryPage
            .mouseOverNoTagsWithNoEditIcon(fileToCheck.getName());
        repositoryPage
            .assertIsEditTagIconNotDisplayed(fileToCheck.getName());
        log.info("Delete File and Delete User2 ");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        repositoryPage
            .select_ItemsAction(fileToCheck.getName(), ItemActions.DELETE_DOCUMENT)
            .clickOnDeleteButtonOnDeletePrompt();
        deleteUsersIfNotNull(testUser2);

    }

}