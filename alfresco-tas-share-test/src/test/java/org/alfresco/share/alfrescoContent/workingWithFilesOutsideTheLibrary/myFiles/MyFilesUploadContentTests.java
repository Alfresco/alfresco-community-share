package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesUploadContentTests extends ContextAwareWebTest
{
    @Autowired
    private MyFilesPage myFilesPage;
    @Autowired
    private DocumentDetailsPage documentDetailsPage;
    @Autowired
    private UploadContent uploadContent;

    private String user = String.format("user%s", RandomData.getRandomAlphanumeric());
    private String testFile = RandomData.getRandomAlphanumeric() + "testFile.txt";
    private String testFilePath = testDataFolder + testFile;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
    }


    @TestRail (id = "C7651")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void myFilesUploadDocument()
    {
        myFilesPage.navigate();
        LOG.info("STEP1: On the My Files page upload a file.");
        uploadContent.uploadContent(testFilePath);
        LOG.info("STEP2: Verify if the file is uploaded successfully.");
        assertTrue(myFilesPage.isContentNameDisplayed(testFile), String.format("The file [%s] is not present", testFile));
    }

    //    @Bug (id = "MNT-18059", status = Bug.Status.FIXED)
    @TestRail (id = "C7792")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void myFilesUpdateDocumentNewVersion()
    {
        String testFile = RandomData.getRandomAlphanumeric() + "testFile.txt";
        String newVersionFile = RandomData.getRandomAlphanumeric() + "newVersionFile.txt";
        String testFilePath = testDataFolder + testFile;
        String newVersionFilePath = testDataFolder + newVersionFile;
        myFilesPage.navigate();
        uploadContent.uploadContent(testFilePath);
        assertTrue(myFilesPage.isContentNameDisplayed(testFile), String.format("The file [%s] is not present", testFile));
        LOG.info("STEP1: Click on the file and check contents.");
        myFilesPage.clickOnFile(testFile);
        Assert.assertEquals(documentDetailsPage.getContentText(), "contents", String.format("Contents of %s are wrong.", testFile));
        LOG.info("STEP2: Navigate back to My Files page and click on upload new version for the file.");
        myFilesPage.navigate();
        myFilesPage.clickDocumentLibraryItemAction(testFile, ItemActions.UPLOAD_NEW_VERSION, uploadContent);
        LOG.info("STEP3: Update the file with major version.");
        uploadContent.updateDocumentVersion(newVersionFilePath, "comments", UploadContent.Version.Major);
        getBrowser().waitInSeconds(4);
        assertTrue(myFilesPage.isContentNameDisplayed(newVersionFile), String.format("The file [%s] is not present", newVersionFile));
        Assert.assertFalse(myFilesPage.isContentNameDisplayed(testFile), String.format("The file [%s] is not present", testFile));
        LOG.info("STEP4: Click on the file and check the version and contents are updated.");
        myFilesPage.clickOnFile(newVersionFile);
        Assert.assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFile));
        Assert.assertEquals(documentDetailsPage.getFileVersion(), "2.0", String.format("Version of %s is wrong.", newVersionFile));
    }
}
