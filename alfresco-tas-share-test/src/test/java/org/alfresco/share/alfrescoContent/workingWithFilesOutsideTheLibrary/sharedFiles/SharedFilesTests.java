package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;

import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.TestGroup;

import org.alfresco.utility.model.UserModel;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Laura.Capsa
 */
public class SharedFilesTests extends BaseTest
{
    private SharedFilesPage sharedFilesPage;
    private SocialFeatures socialFeatures;
    private DeleteDialog deleteDialog;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    FileModel testFile;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        sharedFilesPage = new SharedFilesPage(webDriver);
        socialFeatures = new SocialFeatures(webDriver);
        deleteDialog = new DeleteDialog(webDriver);

        log.info("Precondition1: Test user is created & Navigate to SharedFiles page");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        authenticateUsingCookies(user.get());

        log.info("Precondition2 : user is logged into the Share & content items are created");
        testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().authenticateUser(user.get()).usingUser(user.get()).usingShared().createFile(testFile).assertThat().existsInRepo();

        sharedFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");
    }

    @TestRail (id = "C7661")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyShareButton()
    {
        log.info("STEP1: Hover over a file and click on the \"Share\" button.");
        sharedFilesPage
            .mouseOverContentItem(testFile.getName());
        socialFeatures
            .assertIsShareButtonAvailable()
            .clickShareButton(testFile.getName());
        socialFeatures
            .assertShareButtonEnabled(testFile.getName())
            .assertIsPublicLinkInputFieldDisplayed();

        log.info("Delete the file Created in Precondition1");
        sharedFilesPage
            .navigateByMenuBar()
            .selectItemAction(testFile.getName(), ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .confirmDeletion();
    }

    @AfterMethod
    public void cleanUp()
    {
        deleteUsersIfNotNull(user.get());
    }
}