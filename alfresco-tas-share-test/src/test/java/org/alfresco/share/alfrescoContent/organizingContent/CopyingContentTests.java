package org.alfresco.share.alfrescoContent.organizingContent;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class CopyingContentTests extends BaseTest
{
    private DocumentLibraryPage2 documentLibraryPage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private final FolderModel SHARED_FILES = new FolderModel("Shared Files");

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        getCmisApi().authenticateUser(user.get());
        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C7377")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkCopyFileToSharedFiles()
    {
        FileModel fileToCopy = FileModel.getRandomFileModel(FileType.HTML, FILE_CONTENT);
        getCmisApi().usingSite(site.get()).createFile(fileToCopy).assertThat().existsInRepo();

        documentLibraryPage.navigate(site.get())
            .usingContent(fileToCopy).clickCopyTo()
            .selectSharedFilesDestination()
            .selectFolder(SHARED_FILES)
            .clickCopyToButton();
        FileModel copiedFile = new FileModel(fileToCopy.getName());
        copiedFile.setCmisLocation(Utility.buildPath(getCmisApi().getSharedPath(), fileToCopy.getName()));
        getCmisApi().usingResource(copiedFile).assertThat().existsInRepo()
            .and().delete();
    }

    @TestRail (id = "C7378")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkCancelCopyFileToSharedFiles()
    {
        FileModel fileToCopy = FileModel.getRandomFileModel(FileType.HTML, FILE_CONTENT);
        getCmisApi().usingSite(site.get()).createFile(fileToCopy).assertThat().existsInRepo();

        documentLibraryPage.navigate(site.get())
            .usingContent(fileToCopy).clickCopyTo()
            .selectSharedFilesDestination()
            .clickCancelButton();
        FileModel copiedFile = new FileModel(fileToCopy.getName());
        copiedFile.setCmisLocation(Utility.buildPath(getCmisApi().getSharedPath(), fileToCopy.getName()));

        getCmisApi().usingResource(copiedFile).assertThat().doesNotExistInRepo();
    }

    @TestRail (id = "C7388")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkCopyFolderToPublicSite()
    {
        SiteModel siteDestination = getDataSite().usingUser(user.get()).createPublicRandomSite();
        FolderModel folderToCopy = FolderModel.getRandomFolderModel();
        FileModel subFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);

        getCmisApi().usingSite(site.get()).createFolder(folderToCopy)
            .usingResource(folderToCopy).createFile(subFile);
        documentLibraryPage.navigate(site.get())
            .usingContent(folderToCopy).clickCopyTo()
            .selectAllSitesDestination()
            .selectSite(siteDestination).clickCopyToButton();

        documentLibraryPage.navigate(siteDestination)
            .usingContent(folderToCopy).assertContentIsDisplayed()
            .selectFolder()
            .usingContent(subFile).assertContentIsDisplayed();

        dataSite.usingAdmin().deleteSite(siteDestination);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
