package org.alfresco.share.site.siteDashboard;

import static org.alfresco.utility.model.FileModel.getRandomFileModel;

import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SavedSearchDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_DASHLET_TITLE = "savedSearchDashlet.title";
    private static final String EXPECTED_NO_RESULTS_FOUND_MESSAGE = "savedSearchDashlet.noResults";
    private static final String EXPECTED_SEARCH_BALLOON_MESSAGE = "savedSearchDashlet.balloonMessage";
    private static final String EXPECTED_FOLDER_LABEL = "savedSearchDashlet.item.inFolderPath";
    private static final String DIALOG_TITLE_INPUT_VALUE = "valid search";
    private static final String EXPECTED_DIALOG_TITLE = "savedSearchDashlet.config.title";
    private static final String FOLDER_LINK_PATH = "/";

    private UserModel userModel;
    private SiteModel siteModel;
    private FileModel fileModel;

    @Autowired
    private SavedSearchDashlet savedSearchDashlet;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(userModel);

        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();
        addDashlet(siteModel, Dashlets.SAVED_SEARCH, 1);
    }

    @TestRail (id = "C2787")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void checkNotDisplayResultsWhenDashletConfigurationIsCancelled()
    {
        savedSearchDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_DASHLET_TITLE));
        savedSearchDashlet
            .configureDashlet()
            .assertDialogTitleEquals(language.translate(EXPECTED_DIALOG_TITLE))
            .setTitleField(RandomData.getRandomAlphanumeric())
            .setSearchTermField(RandomData.getRandomAlphanumeric())
            .clickCancelButton();
        savedSearchDashlet
            .assertNoResultsFoundMessageEquals(language.translate(EXPECTED_NO_RESULTS_FOUND_MESSAGE))
            .clickOnHelpIcon(DashletHelpIcon.SAVED_SEARCH)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_SEARCH_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail (id = "C588500")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplaySearchResultsWhenDashletConfigurationIsSaved()
    {
        fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi
            .authenticateUser(userModel)
            .usingSite(siteModel)
            .createFile(fileModel)
            .assertThat()
            .existsInRepo();
        savedSearchDashlet
            .configureDashlet()
            .setTitleField(DIALOG_TITLE_INPUT_VALUE)
            .setSearchTermField(fileModel.getName())
            .clickOk();
        savedSearchDashlet
            .assertFileIsDisplayed(fileModel.getName())
            .assertInFolderPathEquals(fileModel.getName(),
                String.format(language.translate(EXPECTED_FOLDER_LABEL), FOLDER_LINK_PATH));
    }

    @AfterClass (alwaysRun = true)
    public void cleanupTest()
    {
        removeUserFromAlfresco(userModel);
        deleteSites(siteModel);
    }
}
