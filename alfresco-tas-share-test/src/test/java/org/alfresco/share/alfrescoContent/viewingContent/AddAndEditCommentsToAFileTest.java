package org.alfresco.share.alfrescoContent.viewingContent;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.ParseException;

/**
 * @author iulia.cojocea
 *         This test doesn't work with with selenium version 2.46.0. I should be enabled on 2.53.0 version.
 */
public class AddAndEditCommentsToAFileTest extends ContextAwareWebTest
{
    @Autowired private SiteDashboardPage siteDashboardPage;

    @Autowired private DocumentDetailsPage documentDetailsPage;
    
    @Autowired private DocumentLibraryPage documentLibraryPage;

    private final String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private final String folderName = "testFolder";
    private final String docName = String.format("testDoc%s", RandomData.getRandomAlphanumeric());

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "firstName", "lastName");
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        contentService.createFolder(testUser, password, folderName, siteName);
        contentService.createDocumentInFolder(testUser, password, siteName, folderName, DocumentType.TEXT_PLAIN, docName, "Document content");
        setupAuthenticatedSession(testUser, password);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
        siteService.delete(adminUser, adminPassword,siteName);
    }

    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT}, enabled = false)
    @TestRail(id = "C5885")
    public void addAndEditCommentsToAFile() throws ParseException
    {

        LOG.info("STEP 1: Navigate to 'Document Library' page for 'siteName'");
        siteDashboardPage.navigate(siteName);
        siteDashboardPage.clickDocumentLibrary();
        Assert.assertTrue(documentDetailsPage.isDocumentLibraryOpened(siteName), "Document Library is not opened!");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), "Folder is not displayed!");

        LOG.info("STEP 2: Click on folder name then on file name");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "Document is not displayed!");
        documentLibraryPage.clickOnFile(docName);

        LOG.info("STEP 3: Click on 'Add Comment' and type a comment. Add 11 such comments");
        for (int i = 0; i < 2; i++)
        {
            documentDetailsPage.clickOnCommentDocument();
            documentDetailsPage.addComment("test comment" + i);
        }
        Assert.assertTrue(documentDetailsPage.getPageNoReport().equals("1 - 10 of 11"), "Wrong page report!");

        LOG.info("STEP 4: Click previous (<<) and next (>>) to see more comments");
        documentDetailsPage.clickOnNextPage();
        Assert.assertEquals(documentDetailsPage.getCommentsListSize(), 1, "Only a comment should be displayed!");
        documentDetailsPage.clickOnPreviousPage();
        Assert.assertEquals(documentDetailsPage.getCommentsListSize(), 10, "Wrong number of comments displayed in the list!");

        LOG.info("STEP 5: Edit the first comment");
        documentDetailsPage.clickOnEditComment("test comment1");
        documentDetailsPage.modifyCommContent("modification");
        Assert.assertTrue(documentDetailsPage.getCommentContent("test comment1 modification").equals("test comment1 modification"), "Wrong comment content!");
    }
}
