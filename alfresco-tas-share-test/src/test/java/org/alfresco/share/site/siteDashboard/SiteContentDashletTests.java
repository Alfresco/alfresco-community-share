package org.alfresco.share.site.siteDashboard;

import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.alfresco.utility.model.FileModel.getRandomFileModel;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class SiteContentDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String I_VE_RECENTLY_MODIFIED = "dashlet.filter.recentlyModified";
    private static final String I_M_EDITING = "dashlet.filter.editing";
    private static final String MY_FAVORITES = "dashlet.filter.favorites";
    private static final String EXPECTED_HELP_BALLOON_MESSAGE = "siteContentDashlet.helpBalloonMessage";
    private static final String DESCRIPTION = "Edited description ".concat(randomAlphabetic(3));
    private static final String EXPECTED_FILE_SIZE = "11 bytes";

    private UserModel userModel;
    private SiteModel siteModel;
    private FileModel fileModel;

    @Autowired
    private SiteContentDashlet siteContentDashlet;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();
        cmisApi.authenticateUser(userModel);

        setupAuthenticatedSession(userModel);
    }

    @TestRail (id = "C5413")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void checkSpecificMessageWhenSiteContentListIsEmpty()
    {
        siteDashboardPage.navigate(siteModel);
        siteContentDashlet
            .assertEmptySiteContentMessageIsCorrect();

        siteContentDashlet
            .openFilterDropdown()
            .assertFilterLabelEquals(language.translate(I_VE_RECENTLY_MODIFIED))
            .assertFilterLabelEquals(language.translate(I_M_EDITING))
            .assertFilterLabelEquals(language.translate(MY_FAVORITES))
            .assertDetailedViewIconIsDisplayed();

        siteContentDashlet
            .clickOnHelpIcon(Dashlet.DashletHelpIcon.SITE_CONTENT)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail (id = "C5425")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayCreatedFileInSiteContentDashlet()
    {
        fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(siteModel).createFile(fileModel);

        siteDashboardPage.navigate(siteModel);
        siteContentDashlet
            .clickSimpleViewIcon()
            .usingDocument(fileModel)
            .assertFileIsDisplayed()
            .assertSiteEqualsTo(siteModel)
            .assertThumbnailIsDisplayed();
    }

    @TestRail (id = "C588523")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayCreatedFileInDocumentDetailsPage()
    {
        fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(siteModel).createFile(fileModel);

        siteDashboardPage.navigate(siteModel);
        siteContentDashlet.usingDocument(fileModel)
            .clickFileName();
//            .assertDocumentDetailsPageIsOpened()
//            .assertDocumentTitleEquals(fileModel)
//            .assertFileContentEquals(FILE_CONTENT);
    }

    @TestRail (id = "C5457")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayEditedFileInSiteContentDashlet()
    {
        String newContent = "new content";
        fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(siteModel).createFile(fileModel)
            .usingResource(fileModel).setContent(newContent)
            .updateProperty("cm:description", DESCRIPTION);

        siteDashboardPage.navigate(siteModel);
        siteContentDashlet
            .clickDetailedViewButton()
            .usingDocument(fileModel)
            .assertFileIsDisplayed()
            .assertDescriptionEqualsTo(DESCRIPTION)
            .assertFileVersionEqualsTo(1.1)
            .assertFileSizeEqualsTo(EXPECTED_FILE_SIZE);
    }

    @TestRail (id = "C588522")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldLikeAndAddFileToFavorite()
    {
        fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(siteModel).createFile(fileModel);

        siteDashboardPage.navigate(siteModel);
        siteContentDashlet
            .clickDetailedViewButton()
            .usingDocument(fileModel)
            .clickLike()
            .assertUnlikeIsDisplayed()
            .assertNumberOfLikesEqualsTo(1)
            .addToFavorite().assertRemoveFromFavoriteIsDisplayed();
    }

    @TestRail (id = "C588521")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldUnlikeAndRemoveFileFromFavorite()
    {
        fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(siteModel).createFile(fileModel);

        siteDashboardPage.navigate(siteModel);
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
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldAddCommentOnListItem()
    {
        fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(siteModel).createFile(fileModel);

        siteDashboardPage.navigate(siteModel);
        siteContentDashlet
            .usingDocument(fileModel)
            .clickCommentLink();
//            .assertDocumentDetailsPageIsOpened()
//            .assertCommentsAreaIsOpened();
    }

    @AfterClass(alwaysRun = true)
    public void cleanupTest()
    {
        removeUserFromAlfresco(userModel);
        deleteSites(siteModel);
    }
}
