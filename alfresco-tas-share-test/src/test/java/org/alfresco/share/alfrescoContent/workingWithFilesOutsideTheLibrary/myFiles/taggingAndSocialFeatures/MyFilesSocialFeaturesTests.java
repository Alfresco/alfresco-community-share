package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles.taggingAndSocialFeatures;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.BaseTest;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
@Slf4j
/**
 * @author Razvan.Dorobantu
 */
public class MyFilesSocialFeaturesTests extends BaseTest {
    private final String testFile = RandomData.getRandomAlphanumeric() + "testFile.txt";
    private final String testFilePath = testDataFolder + testFile;
    private final String testFolder = String.format("testFolder%s", RandomData.getRandomAlphanumeric());

    private MyFilesPage myFilesPage;

    private SiteDashboardPage sitePage;
    private DocumentDetailsPage documentDetailsPage;

    private NewFolderDialog newContentDialog;
    private DocumentLibraryPage documentLibraryPage;
    private UploadContent uploadContent;

    private SocialFeatures social;
    private UserModel testUser;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        log.info("PreCondition: Creating a TestUser");
        testUser = dataUser.usingAdmin().createRandomTestUser();
        getCmisApi().authenticateUser(testUser);
        authenticateUsingCookies(testUser);

        uploadContent = new UploadContent(webDriver);
        myFilesPage = new MyFilesPage(webDriver);
        social = new SocialFeatures(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        newContentDialog = new NewFolderDialog(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        deleteUsersIfNotNull(user.get());
    }

    @TestRail(id = "C12839")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void myFilesLikeFile()
    {
        log.info("Precondition: Navigate to My Files page and upload a file.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        uploadContent
            .uploadContent(testFilePath);
        myFilesPage
            .assertIsContantNameDisplayed(testFile);

        log.info("Step 1: Hover over the file Like link.");
        myFilesPage
            .isLikeButtonDisplayed(testFile);
        social
            .assertLikeButtonMessage(testFile, "Like this document")
            .assertNoOfLikesVerify(testFile, 0);

        log.info("Step 2: Click on the Like button");
        social.
            clickLikeButton(testFile);

        log.info("Step 3: Verify the number of likes should be 1 and Unlike button should be enabled.");
        social
            .assertNoOfLikesVerify(testFile, 1)
            .assertLikeButtonMessage(testFile, "Unlike");
    }

    @TestRail(id = "C13661")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void myFilesLikeFolder() {
        log.info("Precondition: Navigate to My Files page and create a folder.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        myFilesPage
            .click_CreateButton()
            .click_FolderLink()
            .assertDialogTitleEquals("New Folder");
        newContentDialog
            .typeName(testFolder)
            .clickSave();
        myFilesPage
            .assertIsContantNameDisplayed(testFolder);

        log.info("Step 1: Hover over the file Like link.");
        myFilesPage
            .isLikeButtonDisplayed(testFolder);
        social
            .assertLikeButtonMessage(testFolder, "Like this folder")
            .assertNoOfLikesVerify(testFolder, 0);

        log.info("Step 2: Click on the Like button");
        social.
            clickLikeButton(testFolder);

        log.info("Step 3: Verify the number of likes should be 1 and Unlike button should be enabled.");
        social
            .assertNoOfLikesVerify(testFolder, 1)
            .assertLikeButtonMessage(testFolder, "Unlike");
    }

    @TestRail(id = "C13662")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void myFilesUnlikeFile() {
        log.info("Precondition: Navigate to My Files page and upload a file");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        uploadContent
            .uploadContent(testFilePath);
        myFilesPage
            .assertIsContantNameDisplayed(testFile);

        log.info("Precondition: Like the file");
        social
            .clickLikeButton(testFile);

        log.info("Precondition: Verify the number of likes should be 1 and Unlike button should be enabled.");
        social
            .assertNoOfLikesVerify(testFile, 1)
            .assertLikeButtonMessage(testFile, "Unlike");

        log.info("Step 1: Click on Unlike");
        social
            .clickUnlike(testFile);

        log.info("Step 2: Verify the number of likes should be 0 ");
        social
            .assertNoOfLikesVerify(testFile, 0);
    }

    @TestRail(id = "C13663")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void myFilesUnlikeFolder() {
        log.info("Precondition: Navigate to My Files page and create a folder.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        myFilesPage
            .click_CreateButton()
            .click_FolderLink()
            .assertDialogTitleEquals("New Folder");
        newContentDialog
            .typeName(testFolder)
            .clickSave();
        myFilesPage
            .assertIsContantNameDisplayed(testFolder);

        log.info("PreCondition: Click on the Like button");
        social.
            clickLikeButton(testFolder);

        log.info("Hover over the folder Like link & Verify the number of likes should be 1 and Unlike button should be enabled.");
        social
            .assertNoOfLikesVerify(testFolder, 1)
            .assertLikeButtonMessage(testFolder, "Unlike");

        log.info("Step 1: Click on Unlike");
        social
            .clickUnlike(testFolder);

        log.info("Step 2: Verify the number of likes should be 0 ");
        social
            .assertNoOfLikesVerify(testFolder, 0);
    }

    @TestRail(id = "C12841")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void myFilesAddCommentToFile() {
        log.info("Precondition: Navigate to My Files page and upload a file");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        uploadContent
            .uploadContent(testFilePath);
        myFilesPage
            .isContentNameDisplayed(testFile);

        log.info("STEP1: Hover over a document and press \"Comment\"");
        social
            .clickCommentLink(testFile)
            .assertPageTitleEquals("Alfresco » Document Details");

        log.info("STEP2: In the \"Comments\" area of Document Details page write a comment and press \"Add Comment\" button");
        documentDetailsPage
            .addComment("File Comment")
            .refreshpage()
            .assertVerifyCommentContent("File Comment");

        log.info("STEP3: Navigate to My Files page.");
        myFilesPage
            .navigate();

        log.info("Step 4: Verify the number of Comments should be 1 ");
        social
            .assertNoOfCommentsVerify(testFile,1);
    }

    @TestRail (id = "C13664")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void myFilesAddCommentToFolder() {
        log.info("Precondition: Navigate to My Files page and create a folder.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        myFilesPage
            .click_CreateButton()
            .click_FolderLink()
            .assertDialogTitleEquals("New Folder");
        newContentDialog
            .typeName(testFolder)
            .clickSave();
        myFilesPage
            .assertIsContantNameDisplayed(testFolder);

        log.info("STEP1: Hover over a document and press Comment");
        social
            .clickCommentLink(testFolder)
            .assertPageTitleEquals("Alfresco » Folder Details");

        log.info("STEP2: In the Comments area of Document Details page write a comment and press Add Comment button");
        documentDetailsPage
            .addComment("Folder comment")
            .refreshpage()
            .assertVerifyCommentContent("Folder comment");

        log.info("STEP3: Navigate to My Files page");
        myFilesPage
            .navigate();

        log.info("Step 4: Verify the number of Comments should be 1 ");
        social
            .assertNoOfCommentsVerify(testFolder,1);
    }
}
