package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository.additionalActions;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.FileType;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
@Slf4j
public class LikeTests extends BaseTest
{
    private final String user = String.format("TestUser%s", RandomData.getRandomAlphanumeric());
    private final String password = "password";
    //@Autowired
    private RepositoryPage repositoryPage;
    private FileModel fileToCheck;
    //@Autowired
    private SocialFeatures socialFeatures;
    private UserModel testUser1;
    private FolderModel folderToCheck;

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
            .click_FolderName("User Homes")
            .select_ItemsAction(user, ItemActions.DELETE_FOLDER)
            .clickOnDeleteButtonOnDeletePrompt();
    }

    @TestRail (id = "C8301")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void likeFile() {
        log.info("Precondition: Login as User and Check Created File is Available in User Repository-> User Homes");
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName("User Homes")
            .click_FolderName(user)
            .assertFileIsDisplayed(fileToCheck.getName());

        log.info("Step 1: Hover over the file Like link.");
        repositoryPage
            .assertIsLikeButtonDisplayed(fileToCheck.getName());
        socialFeatures
            .assertLikeButtonMessage(fileToCheck.getName(),"Like this document")
            .assertNoOfLikesVerify(fileToCheck.getName(),0);

        log.info("Step 2: Click on the Like button");
        socialFeatures
            .clickLikeButton(fileToCheck.getName())
            .assertNoOfLikesVerify(fileToCheck.getName(),1)
            .assertIsLikeButtonEnabled(fileToCheck.getName());
    }

    @TestRail (id = "C8302")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void likeFolder()
    {
        log.info("Precondition: Login as User and Check Created File is Available in User Repository-> User Homes");
        authenticateUsingLoginPage(testUser1);
        repositoryPage.navigate();
        repositoryPage
            .click_FolderName("User Homes")
            .click_FolderName(user)
            .assertFileIsDisplayed(folderToCheck.getName());

        log.info("Step 1: Hover over the file Like link.");
        repositoryPage
            .assertIsLikeButtonDisplayed(folderToCheck.getName());
        socialFeatures
            .assertLikeButtonMessage(folderToCheck.getName(),"Like this folder")
            .assertNoOfLikesVerify(folderToCheck.getName(),0);

        log.info("Step 2: Click on the Like button");
        socialFeatures
            .clickLikeButton(folderToCheck.getName())
            .assertNoOfLikesVerify(folderToCheck.getName(),1)
            .assertIsLikeButtonEnabled(folderToCheck.getName());
    }

    @TestRail (id = "C8303")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void unlikeFile()
    {
        log.info("Precondition: Login as User and Check Created File is Available in User Repository-> User Homes");
        authenticateUsingLoginPage(testUser1);
        repositoryPage.navigate();
        repositoryPage
            .click_FolderName("User Homes")
            .click_FolderName(user)
            .assertFileIsDisplayed(fileToCheck.getName());

        log.info("PreCondition: Click Like Button");
        socialFeatures.clickLikeButton(fileToCheck.getName());

        log.info("Step 1: Hover over the file Like link.");
        Assert.assertEquals(socialFeatures.getLikeButtonEnabledText(fileToCheck.getName()), "Unlike", "Unlike is not displayed");
        Assert.assertEquals(socialFeatures.getNumberOfLikes(fileToCheck.getName()), 1, "The number of likes is not correct");

        log.info("Step 2: Click on Unlike");
        socialFeatures
            .clickUnlike(fileToCheck.getName())
            .assertNoOfLikesVerify(fileToCheck.getName(),0);
    }

    @TestRail (id = "C8304")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void unlikeFolder()
    {
        log.info("Precondition: Login as User and Check Created File is Available in User Repository-> User Homes");
        authenticateUsingLoginPage(testUser1);
        repositoryPage.navigate();
        repositoryPage
            .click_FolderName("User Homes")
            .click_FolderName(user)
            .assertFileIsDisplayed(folderToCheck.getName());

        log.info("PreCondition: Click Like Button");
        socialFeatures.clickLikeButton(folderToCheck.getName());

        log.info("Step 1: Hover over the folder Like link.");
        Assert.assertEquals(socialFeatures.getLikeButtonEnabledText(folderToCheck.getName()), "Unlike", "Unlike is not displayed");
        Assert.assertEquals(socialFeatures.getNumberOfLikes(folderToCheck.getName()), 1, "The number of likes is not correct");

        log.info("Step 2: Click on Unlike");
        socialFeatures
            .clickUnlike(folderToCheck.getName())
            .assertNoOfLikesVerify(folderToCheck.getName(),0);
    }
}
