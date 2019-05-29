package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles.taggingAndSocialFeatures;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesSocialFeaturesTests extends ContextAwareWebTest
{
    private final String testFile = RandomData.getRandomAlphanumeric() + "testFile.txt";
    private final String testFilePath = testDataFolder + testFile;
    private final String folderName = String.format("testFolder%s", RandomData.getRandomAlphanumeric());
    @Autowired
    private MyFilesPage myFilesPage;
    @Autowired
    private SiteDashboardPage sitePage;
    @Autowired
    private DocumentDetailsPage documentDetailsPage;
    @Autowired
    private NewContentDialog newContentDialog;
    @Autowired
    private UploadContent uploadContent;
    @Autowired
    private SocialFeatures social;

    @TestRail (id = "C12839")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesLikeFile()
    {
        String user = String.format("user%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and upload a file.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(testFilePath);
        assertTrue(myFilesPage.isContentNameDisplayed(testFile), String.format("The file [%s] is not present", testFile));

        LOG.info("Step 1: Hover over the file Like link.");
        Assert.assertTrue(myFilesPage.isLikeButtonDisplayed(testFile), "Documents link is not present");
        Assert.assertEquals(social.getLikeButtonMessage(testFile), "Like this document", "Like Button message is not correct");
        Assert.assertEquals(social.getNumberOfLikes(testFile), 0, "The number of likes is not correct");

        LOG.info("Step 2: Click on the Like button");
        social.clickLikeButton(testFile);
        Assert.assertEquals(social.getNumberOfLikes(testFile), 1, "The number of likes is not correct");
        Assert.assertTrue(social.isLikeButtonEnabled(testFile), "Like button is not enabled");

        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
    }

    @TestRail (id = "C13661")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesLikeFolder()
    {
        String user = String.format("user%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        myFilesPage.clickCreateButton();
        myFilesPage.clickFolderLink();
        newContentDialog.fillInNameField(folderName);
        newContentDialog.clickSaveButton();
        getBrowser().waitInSeconds(3);
        assertTrue(myFilesPage.isContentNameDisplayed(folderName), folderName + " displayed in My Files documents list.");

        LOG.info("Step 1: Hover over the file Like link.");
        Assert.assertTrue(myFilesPage.isLikeButtonDisplayed(folderName), "Documents link is not present");
        Assert.assertEquals(social.getLikeButtonMessage(folderName), "Like this folder", "Like Button message is not correct");
        Assert.assertEquals(social.getNumberOfLikes(folderName), 0, "The number of likes is not correct");

        LOG.info("Step 2: Click on the Like button");
        social.clickLikeButton(folderName);
        Assert.assertEquals(social.getNumberOfLikes(folderName), 1, "The number of likes is not correct");
        Assert.assertTrue(social.isLikeButtonEnabled(folderName), "Like button is not enabled");
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);

    }

    @TestRail (id = "C13662")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesUnlikeFile()
    {
        String user = String.format("user%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and upload a file.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(testFilePath);
        assertTrue(myFilesPage.isContentNameDisplayed(testFile), String.format("The file [%s] is not present", testFile));

        LOG.info("Precondition: Like the file.");
        social.clickLikeButton(testFile);
        getBrowser().waitInSeconds(4);

        LOG.info("Step 1: Hover over the file Like link.");
        Assert.assertEquals(social.getLikeButtonEnabledText(testFile), "Unlike", "Unlike is not displayed");
        Assert.assertEquals(social.getNumberOfLikes(testFile), 1, "The number of likes is not correct");

        LOG.info("Step 2: Click on Unlike");
        social.clickUnlike(testFile);
        Assert.assertEquals(social.getNumberOfLikes(testFile), 0, "The number of likes is not correct");
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
    }

    @TestRail (id = "C13663")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesUnlikeFolder()
    {
        String user = String.format("user%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        myFilesPage.clickCreateButton();
        myFilesPage.clickFolderLink();
        newContentDialog.fillInNameField(folderName);
        newContentDialog.clickSaveButton();
        getBrowser().waitInSeconds(3);
        assertTrue(myFilesPage.isContentNameDisplayed(folderName), folderName + " displayed in My Files documents list.");

        LOG.info("Precondition: Like the folder.");
        social.clickLikeButton(folderName);
        getBrowser().waitInSeconds(4);

        LOG.info("Step 1: Hover over the folder Like link.");
        Assert.assertEquals(social.getLikeButtonEnabledText(folderName), "Unlike", "Unlike is not displayed");
        Assert.assertEquals(social.getNumberOfLikes(folderName), 1, "The number of likes is not correct");

        LOG.info("Step 2: Click on Unlike");
        social.clickUnlike(folderName);
        Assert.assertEquals(social.getNumberOfLikes(folderName), 0, "The number of likes is not correct");
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
    }

    @TestRail (id = "C12841")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesAddCommentToFile()
    {
        String user = String.format("user%s", RandomData.getRandomAlphanumeric());
        String comment = String.format("Test comment%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and upload a file.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(testFilePath);
        getBrowser().waitInSeconds(9);
        getBrowser().refresh();
        assertTrue(myFilesPage.isContentNameDisplayed(testFile), String.format("The file [%s] is not present", testFile));

        LOG.info("STEP1: Hover over a document and press \"Comment\"");
        social.clickCommentLink(testFile);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");

        LOG.info("STEP2: In the \"Comments\" area of Document Details page write a comment and press \"Add Comment\" button");
        documentDetailsPage.addComment(comment);
        assertEquals(documentDetailsPage.getCommentContent(), comment, "Comment=");

        LOG.info("STEP3: Navigate to My Files page.");
        sitePage.clickMyFilesLink();
        assertEquals(social.getNumberOfComments(testFile), 1, "Number of comments=");
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
    }

    @TestRail (id = "C13664")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesAddCommentToFolder()
    {
        String user = String.format("user%s", RandomData.getRandomAlphanumeric());
        String comment = String.format("Test comment%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);

        LOG.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        myFilesPage.clickCreateButton();
        myFilesPage.clickFolderLink();
        newContentDialog.fillInNameField(folderName);
        newContentDialog.clickSaveButton();
        getBrowser().waitInSeconds(3);
        assertTrue(myFilesPage.isContentNameDisplayed(folderName), folderName + " displayed in My Files documents list.");

        LOG.info("STEP1: Hover over a document and press \"Comment\"");
        social.clickCommentLink(folderName);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "Displayed page=");

        LOG.info("STEP2: In the \"Comments\" area of Document Details page write a comment and press \"Add Comment\" button");
        documentDetailsPage.addComment(comment);
        getBrowser().waitInSeconds(3);
        assertEquals(documentDetailsPage.getCommentContent(), comment, "Comment=");

        LOG.info("STEP3: Navigate to My Files page");
        sitePage.clickMyFilesLink();
        assertEquals(social.getNumberOfComments(folderName), 1, "Number of comments=");
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
    }
}
