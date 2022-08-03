package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository.additionalActions;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.FileType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;



@Slf4j
public class CommentTests extends BaseTest
{

    private RepositoryPage repositoryPage;
    //@Autowired
    private SocialFeatures socialFeatures;
    //@Autowired
    private DocumentDetailsPage documentDetails;
    private MyFilesPage myFilesPage;
    //@Autowired
    private NewFolderDialog newFolderDialog;
    private UploadContent uploadContent;
    private final String password = "password";
    private final String user = String.format("TestUser%s", RandomData.getRandomAlphanumeric());
    private UserModel testUser1;
    private FolderModel folderToCheck;
    private FileModel fileToCheck;
    private  String username="";
    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        repositoryPage = new RepositoryPage(webDriver);
        socialFeatures = new SocialFeatures(webDriver);

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
            .click_FolderName("User Homes");
        repositoryPage.select_ItemsAction(user, ItemActions.DELETE_FOLDER)
            .clickOnDeleteButtonOnDeletePrompt();
    }

    @TestRail (id = "C8305")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addCommentToFile() throws InterruptedException {
        String comment = "test comment c8305";
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName("User Homes");
        repositoryPage.click_FolderName(user);
        repositoryPage.assertFileIsDisplayed(fileToCheck.getName());
        log.info("Step 1: Add comment");
        socialFeatures
            .assertCommentButtonMessage(fileToCheck.getName(),"Comment on this document");
        socialFeatures
            .clickCommentLink(fileToCheck.getName());
        documentDetails
            .addComment(comment);
        socialFeatures.assertCommentContent(comment);
        log.info("Step 2: Return to Repository, User Homes , User page and check that the comment counter has increased");
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName("User Homes");
        repositoryPage
            .clickOnFolderName(user)
            .assertFileIsDisplayed(fileToCheck.getName());
        socialFeatures
            .assertCommentButtonMessage(fileToCheck.getName(),"Comment on this document")
            .assertNoOfCommentsVerify(fileToCheck.getName(),1);

    }


    @TestRail (id = "C8306")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void addCommentToFolder()  {
        String comment = "test comment c8306";

        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName("User Homes");
        repositoryPage.click_FolderName(user);
        repositoryPage.assertFileIsDisplayed(folderToCheck.getName());
        log.info("Step 1: Add comment");
        socialFeatures
            .assertCommentButtonMessage(folderToCheck.getName(),"Comment on this folder");
        socialFeatures
            .clickCommentLink(folderToCheck.getName());
        documentDetails
            .addComment(comment);
        socialFeatures.assertCommentContent(comment);
        log.info("Step 2: Return to Repository, User Homes , User page and check that the comment counter has increased");
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName("User Homes");
        repositoryPage
            .clickOnFolderName(user)
            .assertFileIsDisplayed(folderToCheck.getName());
        socialFeatures
            .assertNoOfCommentsVerify(folderToCheck.getName(),1);

    }


}
