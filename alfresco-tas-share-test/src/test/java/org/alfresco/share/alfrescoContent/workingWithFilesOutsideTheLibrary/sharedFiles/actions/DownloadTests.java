package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
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

public class DownloadTests extends ContextAwareWebTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String user = String.format("C8024TestUser%s", RandomData.getRandomAlphanumeric());
    private final String fileNameC8024 = "C8024 file2 " + random;
    private final String folderNameC8027 = "folderNameC80272 " + random;
    private final String fileContent = "test content";
    private final String path = "Shared";
    //@Autowired
    private SharedFilesPage sharePage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, fileNameC8024, fileContent);
        contentService.createFolderInRepository(adminUser, adminPassword, folderNameC8027, path);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanUp()
    {
        cleanupAuthenticatedSession();
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
        sharePage.clickDocumentLibraryItemAction(fileNameC8024, ItemActions.DOWNLOAD);
        sharePage.acceptAlertIfDisplayed();

        LOG.info("Step 2: Check the file was saved locally");
        Assert.assertTrue(isFileInDirectory(fileNameC8024, null), "The file was not found in the specified location");
    }

    @TestRail (id = "C8027")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void downloadFolder()
    {
        sharePage.navigate();

        LOG.info("Step 1: Mouse over folder, click Download");
        sharePage.clickDocumentLibraryItemAction(folderNameC8027, ItemActions.DOWNLOAD_AS_ZIP);
        sharePage.acceptAlertIfDisplayed();
        LOG.info("Step 2: Check the folder was saved locally");
        Assert.assertTrue(isFileInDirectory(folderNameC8027, ".zip"), "The folder was not found in the specified location");
    }
}

