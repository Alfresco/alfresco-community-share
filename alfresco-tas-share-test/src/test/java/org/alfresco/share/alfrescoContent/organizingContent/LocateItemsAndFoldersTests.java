package org.alfresco.share.alfrescoContent.organizingContent;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.alfrescoContent.AlfrescoContentPage.DocumentsFilter;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.report.Bug;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LocateItemsAndFoldersTests extends BaseTest
{
    private DocumentLibraryPage2 documentLibraryPage;

    private UserModel user;
    private ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(browser);
        site.set(dataSite.usingUser(user).createPublicRandomSite());

        getCmisApi().authenticateUser(user);
        getRestApi().authenticateUser(user);
        setupAuthenticatedSession(user);
    }

    @Bug (id = "MNT-17556")
    @TestRail (id = "C7516")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyLocateFile()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get())
            .createFile(file).assertThat().existsInRepo();

        documentLibraryPage.navigate(site.get())
            .selectFromDocumentsFilter(DocumentsFilter.RECENTLY_ADDED)
            .assertDocumentsFilterHeaderTitleEqualsTo(language.translate("documentLibrary.documentsFilter.recentlyAdded.title"))
            .usingContent(file)
            .clickLocate().assertDocumentsRootBreadcrumbIsDisplayed();
        documentLibraryPage.usingContent(file).assertContentIsHighlighted();
    }

    @Bug (id = "MNT-17556")
    @TestRail (id = "C7517")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyLocateFolderDetailedView() throws Exception
    {
        FolderModel folder = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folder).assertThat().existsInRepo();
        getRestApi().withCoreAPI().usingAuthUser().addFolderToFavorites(folder);

        documentLibraryPage.navigate(site.get())
            .selectFromDocumentsFilter(DocumentsFilter.FAVORITES)
            .assertDocumentsFilterHeaderTitleEqualsTo(language.translate("documentLibrary.documentsFilter.favorites.title"))
            .usingContent(folder)
            .clickLocate().assertDocumentsRootBreadcrumbIsDisplayed();
        documentLibraryPage.usingContent(folder).assertContentIsHighlighted();
    }

    @AfterMethod(alwaysRun = true)
    public void deleteSite()
    {
        deleteSites(site.get());
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
    }
}
