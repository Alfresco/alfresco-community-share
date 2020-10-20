package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UploadingFilesTests extends ContextAwareWebTest
{
    @Autowired
    private DocumentLibraryPage2 documentLibraryPage;

    private UserModel user;
    private SiteModel site;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
        setupAuthenticatedSession(user);
        documentLibraryPage.navigate(site);
    }

    @TestRail (id = "C6970")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void uploadFileInSite()
    {
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
}