package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.openqa.selenium.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by Alex Argint
 */
public class DownloadingContentTests extends ContextAwareWebTest
{

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentCommon documentCommon;

    private String uniqueIdentifier;
    private String userName;
    private String siteName;
    private String description;
    private String docName;
    //private String downloadPath;
    private String windowsUser;
    private File downloadDirectory;
    private String folderName;
    private String downloadPath = srcRoot + "testdata";  
    private Alert alert;

    public void setup(String id)
    {

        LOG.info("Preconditions for test " + id);
        uniqueIdentifier = "-" + id + "-" + DataUtil.getUniqueIdentifier();
        uniqueIdentifier = uniqueIdentifier.toLowerCase();
        siteName = "siteName" + uniqueIdentifier;
        userName = "User" + uniqueIdentifier;
        description = "description" + uniqueIdentifier;
        docName = "PlainText" + uniqueIdentifier;
        windowsUser = System.getProperty("user.name");
        
       // downloadPath = "C:\\Users\\" + windowsUser + "\\Downloads";
        folderName = "TestFolder" + uniqueIdentifier;

        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);
        content.createFolder(userName, DataUtil.PASSWORD, folderName, siteName);
        content.createDocumentInFolder(userName, DataUtil.PASSWORD, siteName, folderName, CMISUtil.DocumentType.TEXT_PLAIN, docName, description);
        content.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, description);
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
    }

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

    @TestRail(id = "C7080")
    @Test
    public void downloadFileFromAlfresco()
    {
        Alert alert;
        setup("C7080");
        LOG.info("Starting test C7080");

        LOG.info("Step 1: Hover file and click 'Download' button");
        documentLibraryPage.mouseOverFileName(docName);
        documentLibraryPage.clickDownloadForItem(docName);

        if (documentCommon.isAlertPresent())
        {
            alert = browser.switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
        }
        browser.waitInSeconds(2);

        LOG.info("Step 2: Choose 'Save File' option and click 'OK' and verify that the file has been downloaded to the right location");
        Assert.assertTrue(isFileInDirectory(docName, null), "The file was not found in the specified location");
    }

    @TestRail(id = "C7087")
    @Test
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
            alert = browser.switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
        }
        browser.waitInSeconds(4);

        LOG.info("Choose Save option, location for folder to be downloaded and click OK button and verify folder is displayed in specified location");
        Assert.assertTrue(isFileInDirectory(folderName, ".zip"), "The zip archive was not found inthe specified location");
    }
}
