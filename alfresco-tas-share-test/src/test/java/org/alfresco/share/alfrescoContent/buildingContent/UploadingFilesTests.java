package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseShareWebTests;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UploadingFilesTests extends BaseShareWebTests
{
    private DocumentLibraryPage2 documentLibraryPage;

    private UserModel user;
    private SiteModel site;

    @BeforeClass(alwaysRun = true)
    public void dataPrep()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(browser);
        setupAuthenticatedSession(user);
    }

    @TestRail (id = "C6970")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void uploadFileInSite()
    {
        documentLibraryPage.navigate(site);
        FileModel uploadFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        documentLibraryPage.uploadContent(uploadFile)
            .usingContent(uploadFile).assertContentIsDisplayed();
    }

    @TestRail (id = "C11833")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void uploadFileInSiteFolder()
    {
        FolderModel folder = FolderModel.getRandomFolderModel();
        FileModel fileToUpload = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        cmisApi.authenticateUser(user).usingSite(site).createFolder(folder);
        documentLibraryPage.navigate(site)
            .usingContent(folder).selectFolder()
                .uploadContent(fileToUpload);
        documentLibraryPage.usingContent(fileToUpload).assertContentIsDisplayed();
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
        deleteSites(site);
    }
}