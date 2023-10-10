package org.alfresco.share.alfrescoContent.documentLibrary;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FilmStripViewTests extends BaseTest
{
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

    @TestRail (id = "C2247, C2246")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkContentsInFilmstripView()
    {
        FileModel txtFile = new FileModel("a-text-file.txt", FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel htmlFile = new FileModel("b-html-file.html", FileType.HTML, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get())
            .createFile(txtFile)
            .createFile(htmlFile);

        documentLibraryPage.navigate(site.get())
            .usingContent(txtFile).assertContentIsDisplayed()
            .usingContent(htmlFile).assertContentIsDisplayed()
            .clickOptions()
            .selectFilmstripView()
                .assertFilmstripDocumentsCarouselIsDisplayed()
                .assertContentsAreDisplayInFilmstripCarousel(txtFile, htmlFile)
                .assertSelectedContentInFilmstripViewIs(txtFile)
                .clickNextArrow()
                .assertSelectedContentInFilmstripViewIs(htmlFile)
                .clickPreviousArrow()
                .assertSelectedContentInFilmstripViewIs(txtFile);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}