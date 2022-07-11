package org.alfresco.share.alfrescoContent.socialFeatures;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;
import org.testng.annotations.*;

@Slf4j
public class ManagingCommentsTests extends BaseTest
{
    private static final String NO_COMMENTS = "documentLibrary.documentDetailsPage.commentSection.noComment";
    private final String random = RandomData.getRandomAlphanumeric();
    private final String description = "description-" + random;
    private final String comment = "Test comment for C9934" + random;
    private final String editedComment = "Test comment edited for C9934" + random;

    private FileModel fileToCheck;
    private DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentPreviewPage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, description);

        getCmisApi()
            .usingSite(site.get())
            .createFile(fileToCheck)
            .assertThat()
            .existsInRepo();

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentPreviewPage = new DocumentDetailsPage(webDriver);

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C9934")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void commentsDeleteComment()
    {
        log.info("Precondition: click on the file created in the site where comment to be added.");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName())
            .clickOnCommentDocument();

        log.info("Step 1: Add comment to the file");
        documentPreviewPage
            .addComment(comment)
            .assertVerifyCommentContent(comment);

        log.info("Step 2: Hover mouse over the comment and check that the Delete button is displayed");
        documentPreviewPage
            .assertIsDeleteButtonDisplayedForComment(comment);

        log.info("Step 3: Click Delete and check that the Delete Prompt is displayed");
        documentPreviewPage
            .clickDeleteComment(comment);

        documentPreviewPage
            .assertIsDeleteCommentPromptDisplayed();

        log.info("Step 4: Click Delete button on the Delete Comment prompt");
        documentPreviewPage
            .clickDeleteOnDeleteComment();

        log.info("Step 5: Verify the 'No Comment' notification text after deleting the comment");
        documentPreviewPage
            .assertNoCommentNotificationText(language.translate(NO_COMMENTS));
    }

    @TestRail (id = "C9935")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void commentsEditComment()
    {
        log.info("Precondition: click on the file created in the site where comment to be added.");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName())
            .clickOnCommentDocument();

        log.info("Step 1: Add comment to the file and then verify the comment ");
        documentPreviewPage
            .addComment(comment)
            .assertVerifyCommentContent(comment);

        log.info("Step 2: Hover mouse over the comment and check that the Edit button is displayed");
        documentPreviewPage
            .assertIsEditButtonDisplayedForComment(comment);

        log.info("Step 3: Click the Edit button and verify edit comment displayed");
        documentPreviewPage
            .clickOnEditComment(comment);

        log.info("Step 4: Verify 'Edit Comment' title of the comment box is displayed");
        documentPreviewPage
            .assertIsEditCommentTitleDisplayed();

        log.info("Step 5: Edit comment text and click on Save");
        documentPreviewPage
            .editComment(editedComment);
        documentPreviewPage
            .clickOnSaveButtonEditComment();

        log.info("Step 6: Verify the edited comment content");
        documentPreviewPage
            .assertVerifyCommentContent(editedComment);
    }
}
