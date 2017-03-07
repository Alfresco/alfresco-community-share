package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles.taggingAndSocialFeatures;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesSocialFeaturesTests extends ContextAwareWebTest
{
    @Autowired
    MyFilesPage myFilesPage;

    @Autowired
    SiteDashboardPage sitePage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    NewContentDialog newContentDialog;

    @Autowired
    private UploadContent uploadContent;

    @Autowired
    SocialFeatures social;

    private String testFile =  DataUtil.getUniqueIdentifier() + "testFile.txt";
    private String testFilePath = testDataFolder + testFile;
    private String folderName = "testFolder" + DataUtil.getUniqueIdentifier();

    @TestRail(id="C12839")
    @Test
    public void myFilesLikeFile()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and upload a file.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(testFilePath);
        assertTrue(myFilesPage.isContentNameDisplayed(testFile),String.format("The file [%s] is not present", testFile));

        LOG.info("Step 1: Hover over the file Like link.");
        Assert.assertTrue(myFilesPage.isLikeButtonDisplayed(testFile), "Documents link is not present");
        Assert.assertEquals(social.getLikeButtonMessage(testFile), "Like this document", "Like Button message is not correct");
        Assert.assertEquals(social.getNumberOfLikes(testFile), 0, "The number of likes is not correct");

        LOG.info("Step 2: Click on the Like button");
        social.clickLikeButton(testFile);
        Assert.assertEquals(social.getNumberOfLikes(testFile), 1, "The number of likes is not correct");
        Assert.assertTrue(social.isLikeButtonEnabled(testFile), "Like button is not enabled");
    }

    @TestRail(id="C13661")
    @Test
    public void myFilesLikeFolder()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        myFilesPage.clickCreateButton();
        myFilesPage.clickFolderLink();
        newContentDialog.fillInNameField(folderName);
        newContentDialog.clickSaveButton();
        browser.waitInSeconds(3);
        assertTrue(myFilesPage.isContentNameDisplayed(folderName), folderName + " displayed in My Files documents list.");

        LOG.info("Step 1: Hover over the file Like link.");
        browser.waitUntilElementClickable(social.likeButton, 6L);
        Assert.assertTrue(myFilesPage.isLikeButtonDisplayed(folderName), "Documents link is not present");
        Assert.assertEquals(social.getLikeButtonMessage(folderName), "Like this folder", "Like Button message is not correct");
        Assert.assertEquals(social.getNumberOfLikes(folderName), 0, "The number of likes is not correct");

        LOG.info("Step 2: Click on the Like button");
        browser.waitUntilElementClickable(social.likeButton, 6L);
        social.clickLikeButton(folderName);
        Assert.assertEquals(social.getNumberOfLikes(folderName), 1, "The number of likes is not correct");
        Assert.assertTrue(social.isLikeButtonEnabled(folderName), "Like button is not enabled");
    }

    @TestRail(id="C13662")
    @Test
    public void myFilesUnlikeFile()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and upload a file.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(testFilePath);
        assertTrue(myFilesPage.isContentNameDisplayed(testFile),String.format("The file [%s] is not present", testFile));

        LOG.info("Precondition: Like the file.");
        social.clickLikeButton(testFile);
        browser.waitInSeconds(4);

        LOG.info("Step 1: Hover over the file Like link.");
        Assert.assertEquals(social.getLikeButtonEnabledText(testFile), "Unlike", "Unlike is not displayed");
        Assert.assertEquals(social.getNumberOfLikes(testFile), 1, "The number of likes is not correct");

        LOG.info("Step 2: Click on Unlike");
        social.clickUnlike(testFile);
        Assert.assertEquals(social.getNumberOfLikes(testFile), 0, "The number of likes is not correct");
    }

    @TestRail(id="C13663")
    @Test
    public void myFilesUnlikeFolder()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        myFilesPage.clickCreateButton();
        myFilesPage.clickFolderLink();
        newContentDialog.fillInNameField(folderName);
        newContentDialog.clickSaveButton();
        browser.waitInSeconds(3);
        assertTrue(myFilesPage.isContentNameDisplayed(folderName), folderName + " displayed in My Files documents list.");

        LOG.info("Precondition: Like the folder.");
        browser.waitUntilElementClickable(social.likeButton, 6L);
        social.clickLikeButton(folderName);
        browser.waitInSeconds(4);

        LOG.info("Step 1: Hover over the folder Like link.");
        Assert.assertEquals(social.getLikeButtonEnabledText(folderName), "Unlike", "Unlike is not displayed");
        Assert.assertEquals(social.getNumberOfLikes(folderName), 1, "The number of likes is not correct");

        LOG.info("Step 2: Click on Unlike");
        social.clickUnlike(folderName);
        Assert.assertEquals(social.getNumberOfLikes(folderName), 0, "The number of likes is not correct");
    }

    @TestRail(id="C12841")
    @Test
    public void myFilesAddCommentToFile()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        String comment = "Test comment" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and upload a file.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(testFilePath);
        assertTrue(myFilesPage.isContentNameDisplayed(testFile),String.format("The file [%s] is not present", testFile));

        LOG.info("STEP1: Hover over a document and press \"Comment\"");
        social.clickCommentLink(testFile);
        browser.waitInSeconds(4);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");

        LOG.info("STEP2: In the \"Comments\" area of Document Details page write a comment and press \"Add Comment\" button");
        documentDetailsPage.addComment(comment);
        browser.waitInSeconds(3);
        assertEquals(documentDetailsPage.getCommentContent(), comment, "Comment=");

        LOG.info("STEP3: Navigate to My Files page.");
        sitePage.clickMyFilesLink();
        assertEquals(social.getNumberOfComments(testFile), 1, "Number of comments=");
    }

    @TestRail(id="C13664")
    @Test
    public void myFilesAddCommentToFolder()
    {
        String user = "user" + DataUtil.getUniqueIdentifier();
        String comment = "Test comment" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        myFilesPage.clickCreateButton();
        myFilesPage.clickFolderLink();
        newContentDialog.fillInNameField(folderName);
        newContentDialog.clickSaveButton();
        browser.waitInSeconds(3);
        assertTrue(myFilesPage.isContentNameDisplayed(folderName), folderName + " displayed in My Files documents list.");

        LOG.info("STEP1: Hover over a document and press \"Comment\"");
        social.clickCommentLink(folderName);
        browser.waitInSeconds(4);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "Displayed page=");

        LOG.info("STEP2: In the \"Comments\" area of Document Details page write a comment and press \"Add Comment\" button");
        documentDetailsPage.addComment(comment);
        browser.waitInSeconds(3);
        assertEquals(documentDetailsPage.getCommentContent(), comment, "Comment=");

        LOG.info("STEP3: Navigate to My Files page");
        sitePage.clickMyFilesLink();
        assertEquals(social.getNumberOfComments(folderName), 1, "Number of comments=");
    }
}
