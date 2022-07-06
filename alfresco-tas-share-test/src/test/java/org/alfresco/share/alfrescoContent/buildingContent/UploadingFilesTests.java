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
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private DocumentLibraryPage2 documentLibraryPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C6970")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void uploadFileInSite()
    {
        FileModel uploadFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);

        documentLibraryPage.navigate(site.get());
        documentLibraryPage.uploadContent(uploadFile)
            .usingContent(uploadFile).assertContentIsDisplayed();
    }

    @TestRail (id = "C11833")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void uploadFileInSiteFolder()
    {
        FolderModel folder = FolderModel.getRandomFolderModel();
        FileModel fileToUpload = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);

        getCmisApi().authenticateUser(user.get()).usingSite(site.get()).createFolder(folder);
        documentLibraryPage.navigate(site.get())
            .usingContent(folder).selectFolder()
                .uploadContent(fileToUpload);
        documentLibraryPage.usingContent(fileToUpload).assertContentIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}