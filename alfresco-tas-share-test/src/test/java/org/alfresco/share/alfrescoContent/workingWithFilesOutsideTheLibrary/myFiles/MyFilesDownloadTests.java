package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesDownloadTests extends ContextAwareWebTest
{
    private final String fileNameC7799 = "C7799 file";
    private final String folderNameC7802 = "folderNameC7802";
    private final String fileContent = "test content";

   // @Autowired
    private MyFilesPage myFilesPage;
    //@Autowired
    private SiteDashboardPage sitePage;
    //@Autowired
    private NewFolderDialog newContentDialog;
   // @Autowired
    private UploadContent uploadContent;

    @TestRail (id = "C7799")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void myFilesDownloadFileFromAlfresco()
    {
        LOG.info("Precondition: Login as user, navigate to My Files page and upload a file.");
        String user = String.format("user%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        setupAuthenticatedSession(user, password);
        myFilesPage.navigate();
        uploadContent.uploadContent(testDataFolder + fileNameC7799, fileContent);

        LOG.info("Step 1: Mouse over file, click Download");
        myFilesPage.clickDocumentLibraryItemAction(fileNameC7799, ItemActions.DOWNLOAD);
        myFilesPage.acceptAlertIfDisplayed();

        LOG.info("Step 2: Check the file was saved locally");
        Assert.assertTrue(isFileInDirectory(fileNameC7799, null), "The file was not found in the specified location");

        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
    }

    @TestRail (id = "C7802")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void downloadFolder()
    {
        LOG.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        String user = String.format("user%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        setupAuthenticatedSession(user, password);
        myFilesPage.navigate();
        myFilesPage.clickCreateButton();
        myFilesPage.clickFolderLink();
        newContentDialog.typeName(folderNameC7802);
        newContentDialog.clickSave();
        assertTrue(myFilesPage.isContentNameDisplayed(folderNameC7802), folderNameC7802 + " displayed in My Files documents list.");

        LOG.info("Step 1: Mouse over folder, click Download");
        myFilesPage.clickDocumentLibraryItemAction(folderNameC7802, ItemActions.DOWNLOAD_AS_ZIP);
        myFilesPage.acceptAlertIfDisplayed();

        LOG.info("Step 2: Check the folder was saved locally");
        Assert.assertTrue(isFileInDirectory(folderNameC7802, ".zip"), "The folder was not found in the specified location");
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
    }
}
