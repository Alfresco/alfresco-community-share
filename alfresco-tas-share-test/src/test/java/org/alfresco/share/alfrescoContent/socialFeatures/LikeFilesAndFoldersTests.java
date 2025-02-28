package org.alfresco.share.alfrescoContent.socialFeatures;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;
import org.testng.annotations.*;

@Slf4j
public class LikeFilesAndFoldersTests extends BaseTest
{
    private static final String LIKE_THIS_DOCUMENT_MESSAGE = "documentLibrary.socialFeatures.likeDocument";
    private static final String LIKE_THIS_FOLDER_MESSAGE = "documentLibrary.socialFeatures.likeFolder";

    private final String random = RandomData.getRandomAlphanumeric();
    private final String description = "description-" + random;
    private final String testComment = "Test comment-" + random;

    private FolderModel folderToCheck;
    private FileModel fileToCheck;
    private DocumentLibraryPage documentLibraryPage;
    private SocialFeatures      social;
    private DocumentDetailsPage documentDetailsPage;


    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        social = new SocialFeatures(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C7906")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void likeFile()
    {
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, description);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        log.info("Step 1: Navigate to the Document Library.");
        documentLibraryPage
            .navigate(site.get().getTitle());

        log.info("Step 2: Hover over the file/folder Like link and verify like button is displayed.");
        documentLibraryPage
            .assertLikeButtonIsDisplayed(fileToCheck.getName());

        log.info("Step 3: Verify the like button message and count of like should be 0.");
        social
            .assertLikeButtonMessage(fileToCheck.getName(), language.translate(LIKE_THIS_DOCUMENT_MESSAGE))
            .assertNoOfLikesVerify(fileToCheck.getName(), 0);

        log.info("Step 4: Click on the Like button");
        social
            .clickLikeButton(fileToCheck.getName());

        log.info("Step 5: Verify the number of likes should be 1 and like button should be enabled.");
        social
            .assertNoOfLikesVerify(fileToCheck.getName(), 1)
            .assertIsLikeButtonEnabled(fileToCheck.getName());
    }

    @TestRail (id = "7907")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void likeFolder()
    {
        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        log.info("Step 1: Navigate to the Document Library.");
        documentLibraryPage
            .navigate(site.get().getTitle());

        log.info("Step 2: Hover over the file/folder Like link and verify like button is displayed.");
        documentLibraryPage
            .assertLikeButtonIsDisplayed(folderToCheck.getName());

        log.info("Step 3: Verify the like button message and count of like should be 0.");
        social
            .assertLikeButtonMessage(folderToCheck.getName(), language.translate(LIKE_THIS_FOLDER_MESSAGE))
            .assertNoOfLikesVerify(folderToCheck.getName(), 0);

        log.info("Step 4: Click on the Like button");
        social
            .clickLikeButton(folderToCheck.getName());

        log.info("Step 5: Verify the number of likes should be 1 and like button should be enabled.");
        social
            .assertNoOfLikesVerify(folderToCheck.getName(), 1)
            .assertIsLikeButtonEnabled(folderToCheck.getName());
    }

    @TestRail (id = "C7908")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void unlikeFile()
    {
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, description);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        log.info("Step 1: Navigate to the Document Library.");
        documentLibraryPage
            .navigate(site.get().getTitle());

        log.info("Step 2: Hover over the file/folder Like link and verify like button is displayed.");
        documentLibraryPage
            .assertLikeButtonIsDisplayed(fileToCheck.getName());

        log.info("Step 3: Verify the like button message and count of like should be 0.");
        social
            .assertLikeButtonMessage(fileToCheck.getName(), language.translate(LIKE_THIS_DOCUMENT_MESSAGE))
            .assertNoOfLikesVerify(fileToCheck.getName(), 0);

        log.info("Step 4: Click on the Like button");
        social
            .clickLikeButton(fileToCheck.getName());

        log.info("Step 5: Verify the number of likes should be 1 and like button should be enabled.");
        social
            .assertNoOfLikesVerify(fileToCheck.getName(), 1)
            .assertIsLikeButtonEnabled(fileToCheck.getName());

        log.info("Step 6: Click on Unlike");
        social
            .clickUnlike(fileToCheck.getName());

        log.info("Step 7: Verify the number of likes should be 0.");
        social
            .assertNoOfLikesVerify(fileToCheck.getName(), 0);
        }

    @TestRail (id = "C7909")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void unlikeFolder()
    {
        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        log.info("Step 1: Navigate to the Document Library.");
        documentLibraryPage
            .navigate(site.get().getTitle());

        log.info("Step 2: Hover over the file/folder Like link and verify like button is displayed.");
        documentLibraryPage
            .assertLikeButtonIsDisplayed(folderToCheck.getName());

        log.info("Step 3: Verify the like button message and count of like should be 0.");
        social
            .assertLikeButtonMessage(folderToCheck.getName(), language.translate(LIKE_THIS_FOLDER_MESSAGE))
            .assertNoOfLikesVerify(folderToCheck.getName(), 0);

        log.info("Step 4: Click on the Like button");
        social
            .clickLikeButton(folderToCheck.getName());

        log.info("Step 5: Verify the number of likes should be 1 and like button should be enabled.");
        social
            .assertNoOfLikesVerify(folderToCheck.getName(), 1)
            .assertIsLikeButtonEnabled(folderToCheck.getName());

        log.info("Step 6: Click on Unlike");
        social
            .clickUnlike(folderToCheck.getName());

        log.info("Step 7: Verify the number of likes should be 0.");
        social
            .assertNoOfLikesVerify(folderToCheck.getName(), 0);
    }

    @TestRail (id = "C7910")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addCommentToFile()
    {
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, description);
        getCmisApi().usingSite(site.get())
            .createFile(fileToCheck)
            .assertThat()
            .existsInRepo();

        log.info("Step 1: Navigate to Document Library.");
        documentLibraryPage.navigate(site.get().getTitle())
            .isFileDisplayed(fileToCheck.getName());

        log.info("Step 2: User clicks on \"Comment\" link for the file");
        social.clickCommentLink(fileToCheck.getName());
        documentDetailsPage.assertBrowserPageTitleIs("Alfresco » Document Details");

        log.info("Step 3: User types some text in the \"Add Your Comment...\" section and clicks on \"Add Comment\" button.");
        documentDetailsPage.addComment(testComment)
            .assertVerifyCommentContent(testComment);

        log.info("Step 4: User navigates to site1 Document Library.");
        documentLibraryPage.navigate(site.get().getTitle())
            .assertVerifyNoOfComments(1);
    }

    @TestRail (id = "C7911")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addCommentToFolder()
    {
        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        log.info("Step 1: Navigate to Document Library");
        documentLibraryPage.navigate(site.get().getTitle())
            .isFileDisplayed(folderToCheck.getName());

        log.info("Step 2: User clicks on \"Comment\" link for the file");
        social.clickCommentLink(folderToCheck.getName());
        documentDetailsPage.assertBrowserPageTitleIs("Alfresco » Folder Details");

        log.info("Step 3: User types some text in the \"Add Your Comment...\" section and clicks on \"Add Comment\" button.");
        documentDetailsPage.addComment(testComment)
            .assertVerifyCommentContent(testComment);

        log.info("Step 4: User navigates to site1 Document Library");
        documentLibraryPage.navigate(site.get().getTitle())
            .assertVerifyNoOfComments(1);
    }

    @TestRail (id = "C7912")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addCommentCancel()
    {
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, description);
        getCmisApi().usingSite(site.get())
            .createFile(fileToCheck)
            .assertThat()
            .existsInRepo();

        log.info("Step 1: Navigate to Document Library");
        documentLibraryPage.navigate(site.get().getTitle())
            .isFileDisplayed(fileToCheck.getName());

        log.info("Step 2: User clicks on \"Comment\" link for the folder");
        social.clickCommentLink(fileToCheck.getName());
        documentDetailsPage.assertBrowserPageTitleIs("Alfresco » Document Details");

        log.info("Step 3: User types some text in the \"Add Your Comment...\" section and clicks on \"Cancel\" button.");
        documentDetailsPage.addCommentAndCancel(testComment)
            .assertVerifyNoCommentsIsDisplayed();

        log.info("Step 4: User navigates to site1 Document Library & verify Comments count.");
        documentLibraryPage.navigate(site.get().getTitle())
            .assertVerifyNoCommentNumbers();
    }
}