package org.alfresco.share.site.siteDashboard;

import static java.util.Arrays.asList;
import static org.alfresco.utility.model.FileModel.getRandomFileModel;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteSearchDashlet;
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

public class SiteSearchDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_TITLE = "siteSearchDashlet.title";
    private static final String EXPECTED_BALLOON_MESSAGE = "siteSearchDashlet.balloonMessage";
    private static final String RANDOM_ALPHANUMERIC = RandomData.getRandomAlphanumeric();
    private static final String EXPECTED_NO_RESULTS_FOUND_MESSAGE = "siteSearchDashlet.noResults";

    private static final String EXPECTED_10 = "10";
    private static final String EXPECTED_25 = "25";
    private static final String EXPECTED_50 = "50";
    private static final String EXPECTED_100 = "100";

    private UserModel userModel;
    private SiteModel siteModel;
    private FileModel fileModel;

    @Autowired
    private SiteSearchDashlet siteSearchDashlet;

    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();
        cmisApi.authenticateUser(userModel);

        setupAuthenticatedSession(userModel);
        addDashlet(siteModel, Dashlets.SITE_SEARCH, 1);
    }

    @TestRail (id = "C2775")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITE_DASHBOARD})
    public void checkDisplaySpecificMessageWhenNoSearchResultsReturned()
    {
        siteDashboardPage.navigate(siteModel);
        siteSearchDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_TITLE))
            .clickOnHelpIcon(DashletHelpIcon.SITE_SEARCH)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();

        siteSearchDashlet
            .openSearchFilterDropdown()
            .assertDropdownValuesEqual(asList(EXPECTED_10, EXPECTED_25, EXPECTED_50, EXPECTED_100))
            .typeInSearch(RANDOM_ALPHANUMERIC)
            .clickSearchButton()
            .assertNoResultsFoundMessageEquals(language.translate(EXPECTED_NO_RESULTS_FOUND_MESSAGE));
    }

    @TestRail (id = "C2424")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITE_DASHBOARD})
    public void shouldDisplaySearchResult()
    {
        fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(siteModel).createFile(fileModel);

        siteDashboardPage.navigate(siteModel);
        siteSearchDashlet
            .typeInSearch(fileModel.getName())
            .clickSearchButton()
            .assertReturnedSearchResultEquals(fileModel.getName());
    }

    @TestRail (id = "C588829")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITE_DASHBOARD})
    public void shouldDisplayFileNameInDocumentDetailsPageWhenAccessedFromSiteSearchDashlet()
    {
        fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(siteModel).createFile(fileModel);

        siteDashboardPage.navigate(siteModel);
        siteSearchDashlet
            .typeInSearch(fileModel.getName())
            .clickSearchButton()
            .clickFileLinkName(fileModel.getName());

        documentDetailsPage
            .assertDocumentTitleEquals(fileModel);
    }

    @AfterClass (alwaysRun = true)
    public void cleanupTest()
    {
        removeUserFromAlfresco(userModel);
        deleteSites(siteModel);
    }
}
