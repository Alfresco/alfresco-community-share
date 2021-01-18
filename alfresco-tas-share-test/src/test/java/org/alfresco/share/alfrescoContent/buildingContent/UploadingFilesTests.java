package org.alfresco.share.alfrescoContent.buildingContent;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class UploadingFilesTests extends BaseTest
{
    private UserModel user;
    private SiteModel site;

    private DocumentLibraryPage2 documentLibraryPage;

    @BeforeClass(alwaysRun = true)
    public void dataPrep()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
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
        getCmisApi().authenticateUser(user).usingSite(site).createFolder(folder);
        documentLibraryPage.navigate(site)
            .usingContent(folder).selectFolder()
                .uploadContent(fileToUpload);
        documentLibraryPage.usingContent(fileToUpload).assertContentIsDisplayed();
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user);
        deleteSitesIfNotNull(site);
    }
}