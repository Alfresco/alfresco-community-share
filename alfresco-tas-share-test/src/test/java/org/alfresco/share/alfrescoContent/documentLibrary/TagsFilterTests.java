package org.alfresco.share.alfrescoContent.documentLibrary;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.rest.core.RestAisAuthentication;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Base64;

public class TagsFilterTests extends BaseTest
{
    private final String CONTENTS_TAGGED_PAGE_HEADER = "documentLibrary.filters.tagPageHeader";

    private DocumentLibraryPage2 documentLibraryPage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C6939")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyTagsFilterWhenNoTagsAreAvailable()
    {
        documentLibraryPage.navigate(site.get())
            .usingContentFilters()
            .openTagsFilter()
            .assertNoTagsAreDisplayed();
    }

    @TestRail (id = "C6940")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void shouldDisplayContentWhenTagIsSelected() throws Exception
    {
        String tagFolder = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        String tagFile = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        FolderModel testFolder = FolderModel.getRandomFolderModel();
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);

        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get())
            .createFile(testFile)
            .createFolder(testFolder);

//        getRestApi().authenticateUser(user.get())
//            .withCoreAPI().usingResource(testFile).addTag(tagFile);
        setAuthorizationRequestHeader(getRestApi().authenticateUser(user.get()))
            .withCoreAPI().usingResource(testFile).addTag(tagFile);

//        getRestApi().withCoreAPI().usingResource(testFolder).addTag(tagFolder);
        setAuthorizationRequestHeader(getRestApi().authenticateUser(user.get()))
            .withCoreAPI().usingResource(testFolder).addTag(tagFolder);

        documentLibraryPage.navigate(site.get())
            .usingContentFilters()
            .clickTag(tagFile);
        documentLibraryPage
            .assertDocumentsFilterHeaderTitleEqualsTo(String.format(language.translate(CONTENTS_TAGGED_PAGE_HEADER), tagFile))
            .usingContent(testFile).assertContentIsDisplayed()
            .usingContent(testFolder).assertContentIsNotDisplayed();

        documentLibraryPage.usingContentFilters()
            .clickTag(tagFolder);
        documentLibraryPage
            .assertDocumentsFilterHeaderTitleEqualsTo(String.format(language.translate(CONTENTS_TAGGED_PAGE_HEADER), tagFolder))
            .usingContent(testFile).assertContentIsNotDisplayed()
            .usingContent(testFolder).assertContentIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
