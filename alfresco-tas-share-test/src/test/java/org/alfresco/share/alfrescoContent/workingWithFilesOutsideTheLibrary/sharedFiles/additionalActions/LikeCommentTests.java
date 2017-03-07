package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.additionalActions;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class LikeCommentTests extends ContextAwareWebTest
{
    @Autowired
    HeaderMenuBar headerMenuBar;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    SharedFilesPage sharedFilesPage;

    @Autowired
    SocialFeatures social;

    @Autowired
    UploadContent uploadContent;

    @Autowired
    DeleteDialog deleteDialog;

    String uniqueId = DataUtil.getUniqueIdentifier();
    String fileName1 = uniqueId + "testFile1.txt";
    String fileName2 = uniqueId + "testFile2.txt";
    String path = "Shared";
    String testFilePath1 = testDataFolder + fileName1;
    String testFilePath2 = testDataFolder + fileName2;

    @BeforeClass
    public void setupTest()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");
        uploadContent.uploadContent(testFilePath1);
        uploadContent.uploadContent(testFilePath2);
    }

    @TestRail(id = "C8097")
    @Test
    public void likeFile()
    {
        LOG.info("Step 1: Hover over the file Like link.");
        assertTrue(sharedFilesPage.isLikeButtonDisplayed(fileName1), "Documents link is not present");
        assertEquals(social.getLikeButtonMessage(fileName1), "Like this document", "Like Button message=");
        assertEquals(social.getNumberOfLikes(fileName1), 0, "The number of likes=");

        LOG.info("Step 2: Click on the Like button");
        social.clickLikeButton(fileName1);
        assertEquals(social.getNumberOfLikes(fileName1), 1, fileName1 + "The number of likes=");
        assertTrue(social.isLikeButtonEnabled(fileName1), "Like button is enabled");
        assertEquals(social.getLikeButtonMessage(fileName1), "Unlike", "Like Button message=");
    }

    @TestRail(id = "C8098")
    @Test
    public void likeFolder()
    {
        String folderName = "testFolder" + "-C8098-" + uniqueId;

        LOG.info("Test C8098: Create a folder in 'Shared Files'");
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, path);

        LOG.info("Step 1: Hover over the folder's Like link.");
        sharedFilesPage.navigate();
        browser.waitUntilElementClickable(social.likeButton, 6L);
        assertTrue(sharedFilesPage.isLikeButtonDisplayed(folderName), "Documents link is displayed");
        assertEquals(social.getLikeButtonMessage(folderName), "Like this folder", "Like Button message=");
        assertEquals(social.getNumberOfLikes(folderName), 0, "The number of likes=");

        LOG.info("Step 2: Click on the Like button");
        browser.waitUntilElementClickable(social.likeButton, 6L);
        social.clickLikeButton(folderName);
        assertEquals(social.getNumberOfLikes(folderName), 1, "The number of likes=");
        assertTrue(social.isLikeButtonEnabled(folderName), "Like button is enabled");
        assertEquals(social.getLikeButtonMessage(folderName), "Unlike", "Like Button message=");
    }

    @TestRail(id = "C8099")
    @Test
    public void unlikeFile()
    {
        social.clickLikeButton(fileName2);

        LOG.info("Step 1: Hover over the file Like link.");
        assertEquals(social.getLikeButtonEnabledText(fileName2), "Unlike", "Unlike is displayed");
        assertEquals(social.getNumberOfLikes(fileName2), 1, "The number of likes=");

        LOG.info("Step 2: Click on Unlike");
        social.clickUnlike(fileName2);
        assertEquals(social.getNumberOfLikes(fileName2), 0, "The number of likes=");
    }

    @TestRail(id = "C8100")
    @Test
    public void unlikeFolder()
    {
        String folderName = "testFolder" + "-C8100-" + uniqueId;

        contentService.createFolderInRepository(adminUser, adminPassword, folderName, path);

        LOG.info("Like folder " + folderName);
        sharedFilesPage.navigate();
        social.clickLikeButton(folderName);

        LOG.info("Step 1: Hover over the folder's Like link.");
        assertEquals(social.getLikeButtonEnabledText(folderName), "Unlike", "Unlike is displayed");
        assertEquals(social.getNumberOfLikes(folderName), 1, "The number of likes=");

        LOG.info("Step 2: Click on Unlike");
        social.clickUnlike(folderName);
        assertEquals(social.getNumberOfLikes(folderName), 0, "The number of likes=");
    }

    @TestRail(id = "C8101")
    @Test
    public void addCommentToFile()
    {
        String fileName = uniqueId + "testFile-C8101.txt";
        String testFilePath = testDataFolder + fileName;
        uploadContent.uploadContent(testFilePath);

        String comment = "Comment " + uniqueId;

        LOG.info("STEP1: Hover over a document and press \"Comment\"");
        social.clickCommentLink(fileName);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");

        LOG.info("STEP2: In the \"Comments\" area of Document Details page write a comment and press \"Add Comment\" button");
        documentDetailsPage.addComment(comment);
        assertEquals(documentDetailsPage.getCommentContent(), comment, "Comment=");

        LOG.info("STEP3: Navigate to Shared Files page");
        sharedFilesPage.navigate();
        assertEquals(social.getNumberOfComments(fileName), 1, "Number of comments=");
    }

    @TestRail(id = "C8102")
    @Test
    public void addCommentToFolder()
    {
        String folderName = "testFolder" + "-C8102-" + uniqueId;
        String comment = "Comment " + uniqueId;

        contentService.createFolderInRepository(adminUser, adminPassword, folderName, path);

        LOG.info("STEP1: Hover over a document and press \"Comment\"");
        sharedFilesPage.navigate();
        social.clickCommentLink(folderName);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "Displayed page=");

        LOG.info("STEP2: In the \"Comments\" area of Document Details page write a comment and press \"Add Comment\" button");
        documentDetailsPage.addComment(comment);
        assertEquals(documentDetailsPage.getCommentContent(), comment, "Comment=");

        LOG.info("STEP3: Navigate to Shared Files page");
        sharedFilesPage.navigate();
        assertEquals(social.getNumberOfComments(folderName), 1, "Number of comments=");
    }

    @AfterClass
    public void cleanup()
    {
        LOG.info("Delete All from 'Shared Files'");
        sharedFilesPage.navigate();
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.all"));
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption(language.translate("documentLibrary.breadcrumb.selectedItems.delete"));
        deleteDialog.clickDelete();

        cleanupAuthenticatedSession();
    }
}