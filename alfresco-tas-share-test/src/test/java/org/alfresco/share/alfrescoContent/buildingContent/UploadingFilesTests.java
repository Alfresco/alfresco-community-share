package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.Test;

import java.awt.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UploadingFilesTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired
    private UploadContent uploadContent;

    private final String random = RandomData.getRandomAlphanumeric();

    @TestRail(id = "C6970")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void UploadASingleFileToSite()
    {
        String user = "user" + random;
        String siteName = "site-C6970-" + random;
        String testFile = random + "-C6970-File.txt";
        String testFilePath = testDataFolder + testFile;
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);

        LOG.info("Precondition: Login as user and navigate to My Files page.");
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library");

        LOG.info("STEP1: On the My Files page upload a file.");
        uploadContent.uploadContent(testFilePath);

        LOG.info("STEP2: Verify if the file is uploaded successfully.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFile), String.format("The file [%s] is not present", testFile));
    }

    @TestRail(id = "C11833")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void UploadFileInFolder() throws AWTException
    {
        String testUser = "user" + random;
        String siteName = "site-C11833-" + random;
        String folderName = "Folder-C11833-";
        String testFileName = "testDoc.txt";
        String testFilePath = testDataFolder + testFileName;

        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "firstName", "lastName");
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        contentService.createFolder(testUser, password, folderName, siteName);
        setupAuthenticatedSession(testUser, password);
        documentLibraryPage.navigate(siteName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), folderName + " displayed in Documents list.");

        LOG.info("STEP1: On the Document Library page click on the folder.");
        documentLibraryPage.clickOnFolderName(folderName);

        LOG.info("STEP2: Inside the folder click the Upload button.");
        uploadContent.uploadContent(testFilePath);
        getBrowser().waitInSeconds(6);
        LOG.info("STEP3: Verify if the file is uploaded successfully.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFileName), String.format("The file [%s] is not present", testFileName));
    }
}