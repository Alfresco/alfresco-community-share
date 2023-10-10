package org.alfresco.share.alfrescoContent.viewingContent;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.ContentService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class AddAndEditCommentsToAFileTest extends BaseTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String description = "description-" + random;
    private final String testComment = "Test comment for C5885";
    private final String editedComment = "Test comment edited for C5885";


    private FolderModel folderToCheck;
    private FileModel fileToCheck;
    private DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentPreviewPage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true) public void setupTest()
    {
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        folderToCheck = FolderModel.getRandomFolderModel();
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, description);

        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        getCmisApi().usingSite(site.get()).usingResource(folderToCheck).createFile(fileToCheck);

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentPreviewPage = new DocumentDetailsPage(webDriver);

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true) public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail(id = "C5885")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyAddAndEditCommentsToAFile()
    {

        log.info("Precondition: click on the folder created in the site where comment to be added.");
        documentLibraryPage.navigate(site.get().getTitle())
            .clickOnFolderName(folderToCheck.getName());

        log.info("Precondition: click on the file created in the site where comment to be added.");
        documentLibraryPage
            .clickOnFile(fileToCheck.getName())
            .clickOnCommentDocument();

        log.info("STEP 3: Click on 'Add Comment' and type a comment. Add 11 such comments");
        for (int i = 0; i < 11; i++)
        {
            documentPreviewPage
                .clickOnCommentDocument();
            documentPreviewPage
                .addComment(testComment + i)
                .assertVerifyCommentContent(testComment + i);
        }
        documentPreviewPage
            .assertVerifyCommentNumber("1 - 10 of 11");

        log.info("STEP 4: Click previous (<<) and next (>>) to see more comments");
        documentPreviewPage
            .clickOnNextPage();
        documentPreviewPage
            .assertNextPageCommentContent(1);

        documentPreviewPage
            .clickOnPreviousPage();
        documentPreviewPage
            .assertNextPageCommentContent(10);

        log.info("STEP 5: Edit the first comment");
        documentPreviewPage
            .clickOnEditComment(testComment+"10");
        documentPreviewPage
            .modifyCommContent(editedComment);
        documentPreviewPage
            .assertVerifyCommentContent(editedComment+testComment+"10");
    }
}

