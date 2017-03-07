package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import java.io.File;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.openqa.selenium.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ActionsDownloadTests extends ContextAwareWebTest
{
    @Autowired
    ContentService contentService;

    @Autowired
    RepositoryPage repositoryPage;

    @Autowired
    DocumentCommon documentCommon;

    private String user = "C8240TestUser" + DataUtil.getUniqueIdentifier();
    private String fileNameC8240 = "C8240 file";
    private String folderNameC8243 = "folderNameC8243";
    private String fileContent = "test content";
    private String path = "User Homes/" + user;
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
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileNameC8240, fileContent);
        contentService.createFolderInRepository(adminUser, adminPassword, folderNameC8243, path);

        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C8240")
    @Test

    public void downloadFileFromAlfresco()
    {
        repositoryPage.navigate();
        repositoryPage.renderedPage();
        repositoryPage.clickOnContent("User Homes");
        repositoryPage.clickOnContent(user);
        browser.waitUntilElementClickable(repositoryPage.subfolderDocListTree(user), 10L);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(fileNameC8240), fileNameC8240 + " is not available in Repository");

        LOG.info("Step 1: Mouse over file, click Download");

        repositoryPage.mouseOverFileName(fileNameC8240);
        repositoryPage.clickDocumentLibraryItemAction(fileNameC8240, "Download", repositoryPage);

        if (documentCommon.isAlertPresent())
        {
            alert = browser.switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
        }
        LOG.info("Step 2: Check the file was saved locally");

        Assert.assertTrue(isFileInDirectory(fileNameC8240, null), "The file was not found in the specified location");
    }

    @TestRail(id = " C8243")
    @Test

    public void downloadFolder()
    {
        repositoryPage.navigate();
        repositoryPage.renderedPage();
        repositoryPage.clickOnContent("User Homes");
        repositoryPage.clickOnContent(user);
        browser.waitUntilElementClickable(repositoryPage.subfolderDocListTree(user), 10L);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(folderNameC8243), folderNameC8243 + " is not available in Repository");

        LOG.info("Step 1: Mouse over folder, click Download");
        repositoryPage.mouseOverContentItem(folderNameC8243);
        repositoryPage.clickDocumentLibraryItemAction(folderNameC8243, "Download as Zip", repositoryPage);

        if (documentCommon.isAlertPresent())
        {
            alert = browser.switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
        }
        LOG.info("Step 2: Check the folder was saved locally");

        Assert.assertTrue(isFileInDirectory(folderNameC8243, ".zip"), "The folder was not found in the specified location");
    }
}
