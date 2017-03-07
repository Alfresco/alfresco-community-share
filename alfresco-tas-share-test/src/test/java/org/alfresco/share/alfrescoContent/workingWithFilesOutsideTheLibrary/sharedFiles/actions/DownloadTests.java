package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import java.io.File;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.openqa.selenium.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DownloadTests  extends ContextAwareWebTest
{
    @Autowired
    ContentService contentService;

    @Autowired
    SharedFilesPage sharePage;

    @Autowired
    DocumentCommon documentCommon;

    private String user = "C8024TestUser" + DataUtil.getUniqueIdentifier();
    private String fileNameC8024 = "C8024 file2";
    private String folderNameC8027 = "folderNameC80272";
    private String fileContent = "test content";
    private String path = "Shared";
    private String downloadPath = srcRoot + "testdata";
    private File downloadDirectory;
    private Alert alert;

    private boolean isFileInDirectory(String fileName, String extension)
    {
        downloadDirectory = new File(downloadPath);
        File[] directoryContent = downloadDirectory.listFiles();

        for (int i = 0; i < directoryContent.length; i++)
        {
            if (extension == null)
            {
                if (directoryContent[i].getName().equals(fileName))
                    return true;
            }
            else
            {
                if (directoryContent[i].getName().equals(fileName + extension))
                    return true;
            }
        }

        return false;
    }

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileNameC8024, fileContent);
        contentService.createFolderInRepository(adminUser, adminPassword, folderNameC8027, path);

        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C8024")
    @Test

    public void downloadFileFromAlfresco()
    {
        sharePage.navigate();
        sharePage.renderedPage();

        LOG.info("Step 1: Mouse over file, click Download");

        sharePage.mouseOverFileName(fileNameC8024);
        sharePage.clickDocumentLibraryItemAction(fileNameC8024, "Download", sharePage);

        if (documentCommon.isAlertPresent())
        {
            alert = browser.switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
        }
        LOG.info("Step 2: Check the file was saved locally");

        Assert.assertTrue(isFileInDirectory(fileNameC8024, null), "The file was not found in the specified location");
    }

    @TestRail(id = " C8027")
    @Test

    public void downloadFolder()
    {
        sharePage.navigate();
        sharePage.renderedPage();
      
        LOG.info("Step 1: Mouse over folder, click Download");
        sharePage.mouseOverFolder(folderNameC8027);
        sharePage.clickAction(folderNameC8027, "Download as Zip");

        if (documentCommon.isAlertPresent())
        {
            alert = browser.switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
        }
        LOG.info("Step 2: Check the folder was saved locally");

        Assert.assertTrue(isFileInDirectory(folderNameC8027, ".zip"), "The folder was not found in the specified location");
    }
}

