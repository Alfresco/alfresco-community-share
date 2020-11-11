package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.alfrescoContent.AlfrescoContentPage.DocumentsFilter;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LocateItemsAndFoldersTests extends ContextAwareWebTest
{
    @Autowired
    private DocumentLibraryPage2 documentLibraryPage2;

    private UserModel user;
    private SiteModel site;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
        cmisApi.authenticateUser(user);
        restApi.authenticateUser(user);
        setupAuthenticatedSession(user);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
        deleteSites(site);
    }

    @Bug (id = "MNT-17556")
    @TestRail (id = "C7516")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyLocateFile()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(site).createFile(file).assertThat().existsInRepo();

        documentLibraryPage2.navigate(site)
            .selectFromDocumentsFilter(DocumentsFilter.RECENTLY_ADDED)
                .assertDocumentsFilterHeaderTitleEqualsTo(language.translate("documentLibrary.documentsFilter.recentlyAdded.title"))
            .usingContent(file)
                .clickLocate().assertDocumentsRootBreadcrumbIsDisplayed();
        documentLibraryPage2.usingContent(file)
            .assertContentIsDisplayed().assertContentIsHighlighted();
    }

    @Bug (id = "MNT-17556")
    @TestRail (id = "C7517")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyLocateFolderDetailedView() throws Exception
    {
        FolderModel folder = FolderModel.getRandomFolderModel();
        cmisApi.usingSite(site).createFolder(folder).assertThat().existsInRepo();
        restApi.withCoreAPI().usingAuthUser().addFolderToFavorites(folder);

        documentLibraryPage2.navigate(site)
            .selectFromDocumentsFilter(DocumentsFilter.FAVORITES)
                .assertDocumentsFilterHeaderTitleEqualsTo(language.translate("documentLibrary.documentsFilter.favorites.title"))
            .usingContent(folder)
                .clickLocate().assertDocumentsRootBreadcrumbIsDisplayed();
        documentLibraryPage2.usingContent(folder)
            .assertContentIsDisplayed().assertContentIsHighlighted();
    }
}
