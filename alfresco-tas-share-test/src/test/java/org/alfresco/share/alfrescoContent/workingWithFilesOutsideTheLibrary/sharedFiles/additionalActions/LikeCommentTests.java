package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.additionalActions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.site.ItemActions;

import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.FileType;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;


@Slf4j
/**
 * @author Laura.Capsa
 */
public class LikeCommentTests extends BaseTest
{
    private final String uniqueId = RandomData.getRandomAlphanumeric();
    private final String comment = "Comment " + uniqueId;
    private DocumentDetailsPage documentDetailsPage;
    private SharedFilesPage sharedFilesPage;
    private DeleteDialog deleteDialog;
    private SocialFeatures social;
    private UserModel testUser1;
    private FolderModel testFolder;
    private FileModel testFile;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        sharedFilesPage = new SharedFilesPage(webDriver);
        social = new SocialFeatures(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);

        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createRandomTestUser();
        getCmisApi().authenticateUser(getAdminUser());

        log.info("Create Folder and File in Admin Repository-> User Homes ");
        authenticateUsingLoginPage(testUser1);

        testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().usingUser(testUser1).usingShared().createFolder(testFolder).assertThat().existsInRepo();

        testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().usingUser(testUser1).usingShared().createFile(testFile).assertThat().existsInRepo();
        authenticateUsingCookies(getAdminUser());

        sharedFilesPage
            .navigateByMenuBar()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");
    }

    @TestRail (id = "C8097")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT }, priority = 1)
    public void likeFile()
    {
        log.info("Step 1: Hover over the file Like link.");
        assertTrue(sharedFilesPage.isLikeButtonDisplayed(testFile.getName()), "Documents link is not present");
        assertEquals(social.getLikeButtonMessage(testFile.getName()), "Like this document", "Like Button message=");
        assertEquals(social.getNumberOfLikes(testFile.getName()), 0, "The number of likes=");

        log.info("Step 2: Click on the Like button");
        social.clickLikeButton(testFile.getName());
        assertEquals(social.getNumberOfLikes(testFile.getName()), 1, testFile + "The number of likes=");
        assertTrue(social.isLikeButtonEnabled(testFile.getName()), "Like button is enabled");
        assertEquals(social.getLikeButtonMessage(testFile.getName()), "Unlike", "Like Button message=");
    }

    @TestRail(id = "C8098")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, priority = 2)
    public void likeFolder()
    {
        log.info("Step 1: Hover over the folder's Like link.");
        assertTrue(sharedFilesPage.isLikeButtonDisplayed(testFolder.getName()), "Documents link is displayed");
        assertEquals(social.getLikeButtonMessage(testFolder.getName()), "Like this folder", "Like Button message=");
        assertEquals(social.getNumberOfLikes(testFolder.getName()), 0, "The number of likes=");

        log.info("Step 2: Click on the Like button");
        social.clickLikeButton(testFolder.getName());
        assertEquals(social.getNumberOfLikes(testFolder.getName()), 1, "The number of likes=");
        assertTrue(social.isLikeButtonEnabled(testFolder.getName()), "Like button is enabled");
        assertEquals(social.getLikeButtonMessage(testFolder.getName()), "Unlike", "Like Button message=");
    }

    @TestRail (id = "C8099")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, priority = 3)
    public void unlikeFile()
    {
        log.info("Precondition: Click on the Like button for File Created");
        assertTrue(sharedFilesPage.isLikeButtonDisplayed(testFile.getName()), "Documents link is displayed");
        social.clickLikeButton(testFile.getName());

        log.info("Step 1: Hover over the file Like link.");
        assertEquals(social.getLikeButtonEnabledText(testFile.getName()), "Unlike", "Unlike is displayed");
        assertEquals(social.getNumberOfLikes(testFile.getName()), 1, "The number of likes=");

        log.info("Step 2: Click on Unlike");
        social.clickUnlike(testFile.getName());
        assertEquals(social.getNumberOfLikes(testFile.getName()), 0, "The number of likes=");
    }

    @TestRail (id = "C8100")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, priority = 4)
    public void unlikeFolder()
    {
        log.info("Precondition: Click on the Like button for Folder");
        assertTrue(sharedFilesPage.isLikeButtonDisplayed(testFolder.getName()), "Documents link is displayed");
        social.clickLikeButton(testFolder.getName());

        log.info("Step 1: Hover over the folder's Like link.");
        assertEquals(social.getLikeButtonEnabledText(testFolder.getName()), "Unlike", "Unlike is displayed");
        assertEquals(social.getNumberOfLikes(testFolder.getName()), 1, "The number of likes=");

        log.info("Step 2: Click on Unlike");
        social.clickUnlike(testFolder.getName());
        assertEquals(social.getNumberOfLikes(testFolder.getName()), 0, "The number of likes=");
    }

    @TestRail (id = "C8101")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, priority = 5)
    public void addCommentToFile()
    {
        log.info("STEP1: Hover over a document and press \"Comment\"");
        social
            .clickCommentLink(testFile.getName());
        documentDetailsPage
            .assertBrowserPageTitleIs("Alfresco » Document Details");

        log.info("STEP2: In the \"Comments\" area of Document Details page write a comment and press \"Add Comment\" button");
        documentDetailsPage.addComment(comment);
        assertEquals(documentDetailsPage.getCommentContent(), comment, "Comment=");

        log.info("STEP3: Navigate to Shared Files page");
        sharedFilesPage.navigateByMenuBar();
        social
            .assertNoOfCommentsVerify(testFile.getName(),1);
    }

    @TestRail (id = "C8102")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, priority = 6)
    public void addCommentToFolder()
    {
        log.info("STEP1: Hover over a document and press \"Comment\"");
        sharedFilesPage.navigateByMenuBar();
        social.clickCommentLink(testFolder.getName());
        documentDetailsPage
            .assertBrowserPageTitleIs("Alfresco » Folder Details");

        log.info("STEP2: In the \"Comments\" area of Document Details page write a comment and press \"Add Comment\" button");
        documentDetailsPage.addComment(comment);
        assertEquals(documentDetailsPage.getCommentContent(), comment, "Comment=");

        log.info("STEP3: Navigate to Shared Files page");
        sharedFilesPage.navigateByMenuBar();
        social
            .assertNoOfCommentsVerify(testFolder.getName(),1);
    }

    @AfterMethod
    public void cleanup()
    {
        log.info("Delete the File Created in Precondition");
        authenticateUsingLoginPage(getAdminUser());
        sharedFilesPage.navigateByMenuBar();
        sharedFilesPage.selectItemAction(testFile.getName(), ItemActions.DELETE_DOCUMENT);
        deleteDialog.confirmDeletion();

        log.info("Delete the Folder Created in Precondition");
        sharedFilesPage.navigateByMenuBar();
        sharedFilesPage.selectItemAction(testFolder.getName(), ItemActions.DELETE_FOLDER);
        deleteDialog.confirmDeletion();

        log.info("Delete the User Created in Precondition");
        deleteUsersIfNotNull(testUser1);
    }
}