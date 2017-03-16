package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesDownloadTests  extends ContextAwareWebTest
{
    @Autowired private DocumentCommon documentCommon;

    @Autowired private MyFilesPage myFilesPage;

    @Autowired private SiteDashboardPage sitePage;

    @Autowired private NewContentDialog newContentDialog;

    @Autowired
    private UploadContent uploadContent;

    private final String fileNameC7799 = "C7799 file";
    private final String folderNameC7802 = "folderNameC7802";
    private final String fileContent = "test content";
    private final String filePath = testDataFolder + fileNameC7799;
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

    @TestRail(id = "C7799")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void myFilesDownloadFileFromAlfresco()
    {
        LOG.info("Precondition: Login as user, navigate to My Files page and upload a file.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        uploadContent.uploadContent(filePath, fileContent);

        LOG.info("Step 1: Mouse over file, click Download");
        myFilesPage.mouseOverFileName(fileNameC7799);
        myFilesPage.clickDocumentLibraryItemAction(fileNameC7799, "Download", myFilesPage);

        if (documentCommon.isAlertPresent())
        {
            alert = getBrowser().switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
        }

        LOG.info("Step 2: Check the file was saved locally");
        Assert.assertTrue(isFileInDirectory(fileNameC7799, null), "The file was not found in the specified location");
    }

    @TestRail(id = "C7802")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void downloadFolder()
    {
        LOG.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        myFilesPage.clickCreateButton();
        myFilesPage.clickFolderLink();
        newContentDialog.fillInNameField(folderNameC7802);
        newContentDialog.clickSaveButton();
        getBrowser().waitInSeconds(3);
        assertTrue(myFilesPage.isContentNameDisplayed(folderNameC7802), folderNameC7802 + " displayed in My Files documents list.");

        LOG.info("Step 1: Mouse over folder, click Download");
        myFilesPage.mouseOverFolder(folderNameC7802);
        myFilesPage.clickAction(folderNameC7802, "Download as Zip");

        if (documentCommon.isAlertPresent())
        {
            alert = getBrowser().switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
        }

        LOG.info("Step 2: Check the folder was saved locally");
        Assert.assertTrue(isFileInDirectory(folderNameC7802, ".zip"), "The folder was not found in the specified location");

    }
}
