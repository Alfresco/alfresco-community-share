package org.alfresco.share.alfrescoContent;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.enums.FormatType;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.CommentsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;

import org.alfresco.share.BaseTest;

import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * Created by Mirela Tifui on 2/22/2018.
 */
public class ContentFormatPageTests extends BaseTest
{
    DocumentLibraryPage documentLibraryPage;
    DocumentDetailsPage documentDetailsPage;
    TinyMceEditor commentBox;
    CommentsPage commentsPage;
    private FileModel testFile;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void testSetup()
    {
        log.info("Precondition1: Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        authenticateUsingCookies(user.get());

        log.info("Precondition2: Test Site is created");
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        log.info("Precondition3: Create a file in the site under document library.");
        testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN);
        getCmisApi().usingSite(site.get()).createFile(testFile).assertThat().existsInRepo();

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        commentBox = new TinyMceEditor(webDriver);
        commentsPage = new CommentsPage(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void testCleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @Test(groups = { TestGroup.SHARE, TestGroup.CONTENT })
    public void testBoldFontOfRichTextFormatter() throws InterruptedException {
        String text = "test Bold text content";
        documentLibraryPage
            .navigate(site.get())
            .clickOnFile(testFile.getName());
        documentDetailsPage
            .clickAddCommentButton();
        commentBox
            .addContent(text)
            .clickTextFormatter(FormatType.BOLD);
        Assert.assertTrue(commentsPage.isTextEditorButtonEnabled(commentsPage.boldButtonEnabled()), "Bold button is not pressed");
        Assert.assertTrue(commentBox.verifyBoldText(), "Text is Not Bold");
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.CONTENT })
    public void testItalicFontOfRichTextFormatter()
    {
        String text = "test Italic text content";
        documentLibraryPage
            .navigate(site.get())
            .clickOnFile(testFile.getName());
        documentDetailsPage
            .clickAddCommentButton();
        commentBox
            .addContent(text)
            .clickTextFormatter(FormatType.ITALIC);
        Assert.assertTrue(commentsPage.isTextEditorButtonEnabled(commentsPage.italicButtonEnabled()), "Italic button is not pressed");
        Assert.assertTrue(commentBox.verifyItalicText(), "Text is Not Italic");
    }
}
