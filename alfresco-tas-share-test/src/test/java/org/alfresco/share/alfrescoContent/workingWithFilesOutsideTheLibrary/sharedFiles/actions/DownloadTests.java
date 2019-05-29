package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import java.io.File;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
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

public class DownloadTests extends ContextAwareWebTest
{
    private final String user = String.format("C8024TestUser%s", RandomData.getRandomAlphanumeric());
    private final String fileNameC8024 = "C8024 file2";
    private final String folderNameC8027 = "folderNameC80272";
    private final String fileContent = "test content";
    private final String path = "Shared";
    private final String downloadPath = srcRoot + "testdata";
    @Autowired
    private SharedFilesPage sharePage;
    @Autowired
    private DocumentCommon documentCommon;
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
            } else
            {
                if (aDirectoryContent.getName().equals(fileName + extension))
                    return true;
            }
        }

        return false;
    }

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileNameC8024, fileContent);
        contentService.createFolderInRepository(adminUser, adminPassword, folderNameC8027, path);

        setupAuthenticatedSession(user, password);
    }

    @AfterClass
    public void cleanUp()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);

        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + fileNameC8024);
        contentService.deleteTreeByPath(adminUser, adminPassword, path + "/" + folderNameC8027);
    }

    @TestRail (id = "C8024")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void downloadFileFromAlfresco()
    {
        sharePage.navigate();

        LOG.info("Step 1: Mouse over file, click Download");
        sharePage.clickDocumentLibraryItemAction(fileNameC8024, "Download", sharePage);

        if (documentCommon.isAlertPresent())
        {
            alert = getBrowser().switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
        }
        LOG.info("Step 2: Check the file was saved locally");

        Assert.assertTrue(isFileInDirectory(fileNameC8024, null), "The file was not found in the specified location");
    }

    @TestRail (id = " C8027")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void downloadFolder()
    {
        sharePage.navigate();

        LOG.info("Step 1: Mouse over folder, click Download");
        sharePage.clickDocumentLibraryItemAction(folderNameC8027, "Download as Zip", sharePage);
        getBrowser().waitInSeconds(10);
        if (documentCommon.isAlertPresent())
        {
            alert = getBrowser().switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
        }
        LOG.info("Step 2: Check the folder was saved locally");

        Assert.assertTrue(isFileInDirectory(folderNameC8027, ".zip"), "The folder was not found in the specified location");
    }
}

