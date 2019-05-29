package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository.additionalActions;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CommentTests extends ContextAwareWebTest
{
    @Autowired
    private RepositoryPage repositoryPage;

    @Autowired
    CreateContent createContent;

    @Autowired
    private SocialFeatures socialFeatures;

    @Autowired
    private DocumentDetailsPage documentDetails;

    private final String user = String.format("C8305TestUser%s", RandomData.getRandomAlphanumeric());
    private final String fileNameC8305 = "C8305 file";
    private final String path = "User Homes/" + user;
    private final String fileContent = "test file content";
    private final String folderNameC8306 = "C8306 Folder";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileNameC8305, fileContent);
        contentService.createFolderInRepository(adminUser, adminPassword, folderNameC8306, path);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)

    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, path);
    }

    @TestRail (id = "C8305")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void addCommentToFile()
    {
        String comment = "test comment c8305";
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(fileNameC8305), fileNameC8305 + " is not available in Repository");

        LOG.info("Step 1: Add comment");
        socialFeatures.clickCommentLink(fileNameC8305);
        documentDetails.renderedPage();
        documentDetails.addComment(comment);
        Assert.assertEquals(documentDetails.getCommentContent(), comment, "Comment text is not correct");

        LOG.info("Step 2: Return to Repository, User Homes , User page and check that the comment counter has increased");
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(fileNameC8305), fileNameC8305 + " is not available in Repository");

        Assert.assertEquals(socialFeatures.getNumberOfComments(fileNameC8305), 1, "The number of comments is not increased");
    }

    @TestRail (id = "C8306")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void addCommentToFolder()
    {
        String comment = "test comment c8306";
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(folderNameC8306), folderNameC8306 + " is not available in Repository");

        LOG.info("Step 1: Add comment");
        socialFeatures.clickCommentLink(folderNameC8306);
        documentDetails.renderedPage();
        documentDetails.addComment(comment);
        Assert.assertEquals(documentDetails.getCommentContent(), comment, "Comment text is not correct");

        LOG.info("Step 2: Return to Repository, User Homes , User page and check that the comment counter has increased");
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(folderNameC8306), folderNameC8306 + " is not available in Repository");

        Assert.assertEquals(socialFeatures.getNumberOfComments(folderNameC8306), 1, "The number of comments is not increased");
    }
}
