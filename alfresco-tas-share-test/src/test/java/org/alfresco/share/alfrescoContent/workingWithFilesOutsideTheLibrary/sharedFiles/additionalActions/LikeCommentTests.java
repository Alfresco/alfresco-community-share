package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.additionalActions;

import static org.alfresco.dataprep.CMISUtil.DocumentType.TEXT_PLAIN;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class LikeCommentTests extends ContextAwareWebTest
{
    private final String uniqueId = RandomData.getRandomAlphanumeric();
    private final String user = "user" + uniqueId;
    private final String fileName1 = uniqueId + "testFile1.txt";
    private final String fileName2 = uniqueId + "testFile2.txt";
    private final String folderName1 = uniqueId + "folder1";
    private final String folderName2 = uniqueId + "folder2";
    private final String path = "Shared/";
    private final String comment = "Comment " + uniqueId;
    @Autowired
    private DocumentDetailsPage documentDetailsPage;
    @Autowired
    private SharedFilesPage sharedFilesPage;
    @Autowired
    private SocialFeatures social;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstname", user);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, TEXT_PLAIN, fileName2, fileName2 + " Content");
        contentAction.likeContent(user, password, path + fileName2);
        contentService.createFolderInRepository(adminUser, adminPassword, folderName2, path);
        contentAction.likeContent(user, password, path + folderName2);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, TEXT_PLAIN, fileName1, fileName1 + " Content");
        contentService.createFolderInRepository(adminUser, adminPassword, folderName1, path);

        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");
    }

    @TestRail (id = "C8097")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, priority = 1)
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

    @TestRail (id = "C8098")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, priority = 2)
    public void likeFolder()
    {
        LOG.info("Step 1: Hover over the folder's Like link.");
        sharedFilesPage.navigate();
        assertTrue(sharedFilesPage.isLikeButtonDisplayed(folderName1), "Documents link is displayed");
        assertEquals(social.getLikeButtonMessage(folderName1), "Like this folder", "Like Button message=");
        assertEquals(social.getNumberOfLikes(folderName1), 0, "The number of likes=");

        LOG.info("Step 2: Click on the Like button");
        social.clickLikeButton(folderName1);
        assertEquals(social.getNumberOfLikes(folderName1), 1, "The number of likes=");
        assertTrue(social.isLikeButtonEnabled(folderName1), "Like button is enabled");
        assertEquals(social.getLikeButtonMessage(folderName1), "Unlike", "Like Button message=");
    }

    @TestRail (id = "C8099")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, priority = 3)
    public void unlikeFile()
    {
        LOG.info("Step 1: Hover over the file Like link.");
        assertEquals(social.getLikeButtonEnabledText(fileName2), "Unlike", "Unlike is displayed");
        assertEquals(social.getNumberOfLikes(fileName2), 1, "The number of likes=");

        LOG.info("Step 2: Click on Unlike");
        social.clickUnlike(fileName2);
        assertEquals(social.getNumberOfLikes(fileName2), 0, "The number of likes=");
    }

    @TestRail (id = "C8100")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, priority = 4)
    public void unlikeFolder()
    {
        LOG.info("Step 1: Hover over the folder's Like link.");
        assertEquals(social.getLikeButtonEnabledText(folderName2), "Unlike", "Unlike is displayed");
        assertEquals(social.getNumberOfLikes(folderName2), 1, "The number of likes=");

        LOG.info("Step 2: Click on Unlike");
        social.clickUnlike(folderName2);
        assertEquals(social.getNumberOfLikes(folderName2), 0, "The number of likes=");
    }

    @TestRail (id = "C8101")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, priority = 5)
    public void addCommentToFile()
    {
        LOG.info("STEP1: Hover over a document and press \"Comment\"");
        social.clickCommentLink(fileName1);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");

        LOG.info("STEP2: In the \"Comments\" area of Document Details page write a comment and press \"Add Comment\" button");
        documentDetailsPage.addComment(comment);
        assertEquals(documentDetailsPage.getCommentContent(), comment, "Comment=");

        LOG.info("STEP3: Navigate to Shared Files page");
        sharedFilesPage.navigate();
        assertEquals(social.getNumberOfComments(fileName1), 1, "Number of comments=");
    }

    @TestRail (id = "C8102")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, priority = 6)
    public void addCommentToFolder()
    {
        LOG.info("STEP1: Hover over a document and press \"Comment\"");
        sharedFilesPage.navigate();
        social.clickCommentLink(folderName1);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "Displayed page=");

        LOG.info("STEP2: In the \"Comments\" area of Document Details page write a comment and press \"Add Comment\" button");
        documentDetailsPage.addComment(comment);
        assertEquals(documentDetailsPage.getCommentContent(), comment, "Comment=");

        LOG.info("STEP3: Navigate to Shared Files page");
        sharedFilesPage.navigate();
        social.renderedPage();
        assertEquals(social.getNumberOfComments(folderName1), 1, "Number of comments=");
    }

    @AfterClass
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);

        contentService.deleteContentByPath(adminUser, adminPassword, path + fileName1);
        contentService.deleteContentByPath(adminUser, adminPassword, path + fileName2);
        contentService.deleteContentByPath(adminUser, adminPassword, path + folderName1);
        contentService.deleteContentByPath(adminUser, adminPassword, path + folderName2);
    }
}