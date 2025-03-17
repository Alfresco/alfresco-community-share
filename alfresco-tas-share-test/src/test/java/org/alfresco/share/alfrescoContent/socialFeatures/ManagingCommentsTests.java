package org.alfresco.share.alfrescoContent.socialFeatures;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.*;

@Slf4j
public class ManagingCommentsTests extends BaseTest
{
    @Autowired
    UserService userService;
    private static final String NO_COMMENTS = "documentLibrary.documentDetailsPage.commentSection.noComment";
    private final String random = RandomData.getRandomAlphanumeric();
    private final String description = "description-" + random;
    private final String comment = "Test comment for C9934" + random;
    private final String editedComment = "Test comment edited for C9934" + random;
    private final String nativeCharacters = "désir Bedürfnis è il あなたの名前は何ですか ¿Cuál";
    private final String specialCharacters = "<>?:\"|{}+_)(*&^%$#@!~";

    private FileModel fileToCheck;
    private DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentPreviewPage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user3 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user4 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user5 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), user2.get().getUsername(), site.get().getId(), "SiteManager");

        user3.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), user3.get().getUsername(), site.get().getId(), "SiteConsumer");

        user4.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), user4.get().getUsername(), site.get().getId(), "SiteCollaborator");

        user5.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), user5.get().getUsername(), site.get().getId(), "SiteContributor");

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
        deleteUsersIfNotNull(user2.get());
        deleteUsersIfNotNull(user3.get());
        deleteUsersIfNotNull(user4.get());
        deleteUsersIfNotNull(user5.get());
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

    @TestRail (id = "C7584")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editCommentAndSave()
    {
        log.info("Precondition: click on the file created in the site where comment to be added.");
        documentLibraryPage.navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName())
            .clickOnCommentDocument();

        log.info("Step 1: Add comment to the file and then verify the comment ");
        documentPreviewPage.addComment(comment)
            .assertVerifyCommentContent(comment);

        log.info("Step 2: Hover mouse over the comment and check that the Edit button is displayed");
        documentPreviewPage.assertIsEditButtonDisplayedForComment(comment);

        log.info("Step 3: Click the Edit button and verify edit comment displayed");
        documentPreviewPage.clickOnEditComment(comment);

        log.info("Step 4: Remove all content & Save with empty string");
        documentPreviewPage.clearCommentBoxAndSave("Field contains an error.");
    }

    @TestRail (id = "C7585")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addCommentSpecialAndNativeCharacters()
    {
        log.info("Precondition: click on the file created in the site where comment to be added.");
        documentLibraryPage.navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName())
            .clickOnCommentDocument();

        log.info("Step 1: Add Native Characters comment to the file and then verify the comment ");
        documentPreviewPage.addComment(nativeCharacters)
            .assertVerifyCommentContent(nativeCharacters);

        log.info("Step 2: Add Special Characters comment to the file and then verify the comment ");
        documentPreviewPage.clickOnCommentDocument();
        documentPreviewPage.addComment(specialCharacters)
            .assertVerifyCommentContent(specialCharacters);
    }

    @TestRail (id = "C7594")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addCommentSiteManager()
    {
        authenticateUsingCookies(user2.get());
        log.info("Precondition: click on the file created in the site where comment to be added.");
        documentLibraryPage.navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName())
            .clickOnCommentDocument();

        log.info("Step 1: Add Native Characters comment to the file and then verify the comment ");
        documentPreviewPage.addComment(nativeCharacters)
            .assertVerifyCommentContent(nativeCharacters);
    }

    @TestRail (id = "C7595")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addCommentSiteConsumer()
    {
        authenticateUsingCookies(user3.get());
        log.info("Precondition: click on the file created in the site where comment to be added.");
        documentLibraryPage.navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName());

        log.info("Step 1: Verify the user does not have access to add comments to content.");
        Assert.assertFalse(documentLibraryPage.isCommentButtonDisplayed(fileToCheck.getName()), "Comment button is not available.");
    }

    @TestRail (id = "C7596")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addCommentSiteCollaborator()
    {
        authenticateUsingCookies(user4.get());
        log.info("Precondition: click on the file created in the site where comment to be added.");
        documentLibraryPage.navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName())
            .clickOnCommentDocument();

        log.info("Step 1: Add Native Characters comment to the file and then verify the comment ");
        documentPreviewPage.addComment(nativeCharacters)
            .assertVerifyCommentContent(nativeCharacters);
    }

    @TestRail (id = "C7597")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addCommentSiteContributor()
    {
        authenticateUsingCookies(user5.get());
        log.info("Precondition: click on the file created in the site where comment to be added.");
        documentLibraryPage.navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName())
            .clickOnCommentDocument();

        log.info("Step 1: Add Native Characters comment to the file and then verify the comment ");
        documentPreviewPage.addComment(nativeCharacters)
            .assertVerifyCommentContent(nativeCharacters);
    }

    @TestRail (id = "C7598, C7599")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editCommentSiteManager()
    {
        log.info("Precondition: click on the file created in the site where comment to be added.");
        documentLibraryPage.navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName())
            .clickOnCommentDocument();

        log.info("Step 1: Add comment to the file and then verify the comment ");
        documentPreviewPage.addComment(comment)
            .assertVerifyCommentContent(comment);

        log.info("Step 2: login with site manager");
        authenticateUsingCookies(user2.get());

        log.info("Step 3: click on the file created in the site where comment to be added.");
        documentLibraryPage.navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName())
            .clickOnCommentDocument();

        log.info("Step 4: Hover mouse over the comment and check that the Edit button is displayed");
        documentPreviewPage
            .assertIsEditButtonDisplayedForComment(comment);

        log.info("Step 5: Click the Edit button and verify edit comment displayed");
        documentPreviewPage
            .clickOnEditComment(comment);

        log.info("Step 6: verify edit comment help icon");
       // Assert.assertTrue(documentLibraryPage.isCommentEditHelpIconDisplayed(), "Edit comment help icon is available.");

        log.info("Step 7: Verify 'Edit Comment' title of the comment box is displayed");
        documentPreviewPage
            .assertIsEditCommentTitleDisplayed();

        log.info("Step 8: Edit comment text and click on Save");
        documentPreviewPage
            .editComment(editedComment);
        documentPreviewPage
            .clickOnSaveButtonEditComment();

        log.info("Step 9: Verify the edited comment content");
        documentPreviewPage
            .assertVerifyCommentContent(editedComment);
    }
}