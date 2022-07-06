package org.alfresco.share.site.siteDashboard;

import static org.alfresco.share.TestUtils.FILE_CONTENT;
import static org.alfresco.utility.model.FileModel.getRandomFileModel;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.*;

public class SiteContentDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String I_VE_RECENTLY_MODIFIED = "dashlet.filter.recentlyModified";
    private static final String I_M_EDITING = "dashlet.filter.editing";
    private static final String MY_FAVORITES = "dashlet.filter.favorites";
    private static final String EXPECTED_HELP_BALLOON_MESSAGE = "siteContentDashlet.helpBalloonMessage";
    private static final String DESCRIPTION = "Edited description ".concat(randomAlphabetic(3));
    private static final String EXPECTED_FILE_SIZE = "11 bytes";

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private SiteContentDashlet siteContentDashlet;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteContentDashlet = new SiteContentDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C5413")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void checkSpecificMessageWhenSiteContentListIsEmpty()
    {
        siteDashboardPage.navigate(site.get());
        siteContentDashlet
            .assertEmptySiteContentMessageIsCorrect()
            .openFilterDropdown()
                .assertFilterLabelEquals(language.translate(I_VE_RECENTLY_MODIFIED))
                .assertFilterLabelEquals(language.translate(I_M_EDITING))
                .assertFilterLabelEquals(language.translate(MY_FAVORITES))
                .assertDetailedViewIconIsDisplayed();

        siteContentDashlet
            .clickOnHelpIcon(DashletHelpIcon.SITE_CONTENT)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail (id = "C5425")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayCreatedFileInSiteContentDashlet()
    {
        FileModel fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(fileModel);

        siteDashboardPage.navigate(site.get());
        siteContentDashlet
            .clickSimpleViewIcon()
            .usingDocument(fileModel)
                .assertFileIsDisplayed()
                .assertSiteEqualsTo(site.get())
                .assertThumbnailIsDisplayed();
    }

    @TestRail (id = "C588523")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayCreatedFileInDocumentDetailsPage()
    {
        FileModel fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(fileModel);

        siteDashboardPage.navigate(site.get());
        siteContentDashlet.usingDocument(fileModel)
            .clickFileName()
                .assertDocumentTitleEquals(fileModel)
                .assertFileContentEquals(FILE_CONTENT);
    }

    @TestRail (id = "C5457")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayEditedFileInSiteContentDashlet()
    {
        String newContent = "new content";
        FileModel fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(fileModel)
                .usingResource(fileModel).setContent(newContent)
                    .updateProperty("cm:description", DESCRIPTION);

        siteDashboardPage.navigate(site.get());
        siteContentDashlet
            .clickDetailedViewButton()
            .usingDocument(fileModel)
            .assertFileIsDisplayed()
            .assertDescriptionEqualsTo(DESCRIPTION)
            .assertFileVersionEqualsTo(1.1)
            .assertFileSizeEqualsTo(EXPECTED_FILE_SIZE);
    }

    @TestRail (id = "C588522")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void shouldLikeAndAddFileToFavorite()
    {
        FileModel fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(fileModel);

        siteDashboardPage.navigate(site.get());
        siteContentDashlet
            .clickDetailedViewButton()
            .usingDocument(fileModel)
            .clickLike()
            .assertUnlikeIsDisplayed()
            .assertNumberOfLikesEqualsTo(1)
            .addToFavorite().assertRemoveFromFavoriteIsDisplayed();
    }

    @TestRail (id = "C588521")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void shouldUnlikeAndRemoveFileFromFavorite()
    {
        FileModel fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(fileModel);

        siteDashboardPage.navigate(site.get());
        siteContentDashlet
            .clickDetailedViewButton()
            .usingDocument(fileModel)
            .clickLike().assertUnlikeIsDisplayed()
            .assertUnlikeIsDisplayed()
            .clickUnlike()
            .assertLikeIsDisplayed()
            .assertNumberOfLikesEqualsTo(0);
        siteContentDashlet.usingDocument(fileModel)
            .addToFavorite().assertRemoveFromFavoriteIsDisplayed()
            .removeFromFavorite().assertAddToFavoriteIsDisplayed();
    }

    @TestRail (id = "C5458")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void shouldAddCommentOnListItem()
    {
        FileModel fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(fileModel);

        siteDashboardPage.navigate(site.get());
        siteContentDashlet
            .usingDocument(fileModel)
            .clickCommentLink()
                .assertCommentsAreaIsOpened();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
