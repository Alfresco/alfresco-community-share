package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.DocumentLibraryPage;
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
 * Created by Alex Argint
 */
public class DownloadingContentTests extends ContextAwareWebTest
{
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    private String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private String userName = "user" + uniqueIdentifier;
    private String siteName = "siteName" + uniqueIdentifier;
    private String description = "description" + uniqueIdentifier;
    private String docName = "PlainText" + uniqueIdentifier;
    private String folderName = "TestFolder" + uniqueIdentifier;

    @BeforeClass(alwaysRun = true)
    public void createPrecondition()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.createDocumentInFolder(userName, password, siteName, folderName, CMISUtil.DocumentType.TEXT_PLAIN, docName, description);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, description);
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C7080")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void downloadFileFromAlfresco()
    {
        LOG.info("Step 1: Hover file and click 'Download' button");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, ItemActions.DOWNLOAD, documentLibraryPage);
        documentLibraryPage.acceptAlertIfDisplayed();

        LOG.info("Step 2: Choose 'Save File' option and click 'OK' and verify that the file has been downloaded to the right location");
        Assert.assertTrue(isFileInDirectory(docName, null), "The file was not found in the specified location");
    }

    @TestRail (id = "C7087")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void nonEmptyFolderDownloadAsZip()
    {
        LOG.info("Hover folder and click 'Download as Zip' button from Actions options");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, ItemActions.DOWNLOAD_AS_ZIP, documentLibraryPage);
        documentLibraryPage.acceptAlertIfDisplayed();

        LOG.info("Choose Save option, location for folder to be downloaded and click OK button and verify folder is displayed in specified location");
        Assert.assertTrue(isFileInDirectory(folderName, ".zip"), "The zip archive was not found inthe specified location");
    }
}
