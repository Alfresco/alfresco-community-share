package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

public class ActionsDownloadTests extends ContextAwareWebTest
{
    @Autowired private RepositoryPage repositoryPage;

    @Autowired private DocumentCommon documentCommon;

    private final String user = String.format("C8240TestUser%s", RandomData.getRandomAlphanumeric());
    private final String fileNameC8240 = "C8240 file";
    private final String folderNameC8243 = "folderNameC8243";
    private final String fileContent = "test content";
    private final String path = "User Homes/" + user;
    private final String downloadPath = srcRoot + "testdata";
    private File downloadDirectory;
    private Alert alert;

    private boolean isFileInDirectory(String fileName, String extension)
    {
        downloadDirectory = new File(downloadPath);
        File[] directoryContent = downloadDirectory.listFiles();

        for (File aDirectoryContent : directoryContent)
        {
            if (extension == null)
            {
                if (aDirectoryContent.getName().equals(fileName))
                    return true;
            }
            else
            {
                if (aDirectoryContent.getName().equals(fileName + extension))
                    return true;
            }
        }

        return false;
    }

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileNameC8240, fileContent);
        contentService.createFolderInRepository(adminUser, adminPassword, folderNameC8243, path);

        setupAuthenticatedSession(user, password);
    }


    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
    }

    @TestRail(id = "C8240")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})

    public void downloadFileFromAlfresco()
    {
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(fileNameC8240), fileNameC8240 + " is not available in Repository");

        LOG.info("Step 1: Mouse over file, click Download");
        repositoryPage.clickDocumentLibraryItemAction(fileNameC8240, "Download", repositoryPage);

        if (documentCommon.isAlertPresent())
        {
            alert = getBrowser().switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
        }
        LOG.info("Step 2: Check the file was saved locally");

        Assert.assertTrue(isFileInDirectory(fileNameC8240, null), "The file was not found in the specified location");
    }

    @TestRail(id = " C8243")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})

    public void downloadFolder()
    {
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(user);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(folderNameC8243), folderNameC8243 + " is not available in Repository");

        LOG.info("Step 1: Mouse over folder, click Download");
        repositoryPage.clickDocumentLibraryItemAction(folderNameC8243, "Download as Zip", repositoryPage);

        if (documentCommon.isAlertPresent())
        {
            alert = getBrowser().switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
        }
        LOG.info("Step 2: Check the folder was saved locally");

        Assert.assertTrue(isFileInDirectory(folderNameC8243, ".zip"), "The folder was not found in the specified location");
    }
}
