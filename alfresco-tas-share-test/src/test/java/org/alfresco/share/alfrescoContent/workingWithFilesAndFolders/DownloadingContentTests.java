package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by Alex Argint
 */
public class DownloadingContentTests extends ContextAwareWebTest
{

    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DocumentCommon documentCommon;

    private String uniqueIdentifier;
    private String userName;
    private String siteName;
    private String description;
    private String docName;
    //private String downloadPath;
    private String windowsUser;
    private File downloadDirectory;
    private String folderName;
    private final String downloadPath = srcRoot + "testdata";
    private Alert alert;

    private void setup(String id)
    {

        LOG.info("Preconditions for test " + id);
        uniqueIdentifier = "-" + id + "-" + RandomData.getRandomAlphanumeric();
        uniqueIdentifier = uniqueIdentifier.toLowerCase();
        siteName = "siteName" + uniqueIdentifier;
        userName = "User" + uniqueIdentifier;
        description = "description" + uniqueIdentifier;
        docName = "PlainText" + uniqueIdentifier;
        windowsUser = System.getProperty("user.name");
        
       // downloadPath = "C:\\Users\\" + windowsUser + "\\Downloads";
        folderName = "TestFolder" + uniqueIdentifier;

        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.createDocumentInFolder(userName, password, siteName, folderName, CMISUtil.DocumentType.TEXT_PLAIN, docName, description);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, description);
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
    }

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

    @TestRail(id = "C7080")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void downloadFileFromAlfresco()
    {
        Alert alert;
        setup("C7080");
        LOG.info("Starting test C7080");

        LOG.info("Step 1: Hover file and click 'Download' button");
        documentLibraryPage.mouseOverContentItem(docName);
        documentLibraryPage.clickDownloadForItem(docName);

        if (documentCommon.isAlertPresent())
        {
            alert = getBrowser().switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
        }
        getBrowser().waitInSeconds(2);

        LOG.info("Step 2: Choose 'Save File' option and click 'OK' and verify that the file has been downloaded to the right location");
        Assert.assertTrue(isFileInDirectory(docName, null), "The file was not found in the specified location");
    }

    @TestRail(id = "C7087")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void nonEmptyFolderDownloadAsZip()
    {
        Alert alert;
        setup("C7087");
        LOG.info("Starting test C7087");

        LOG.info("Hover folder and click 'Download as Zip' button from Actions options");
        documentLibraryPage.mouseOverContentItem(folderName);
        documentLibraryPage.clickDownloadAsZipForItem(folderName);
        if (documentCommon.isAlertPresent())
        {
            alert = getBrowser().switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
        }
        getBrowser().waitInSeconds(4);

        LOG.info("Choose Save option, location for folder to be downloaded and click OK button and verify folder is displayed in specified location");
        Assert.assertTrue(isFileInDirectory(folderName, ".zip"), "The zip archive was not found inthe specified location");
    }
}
