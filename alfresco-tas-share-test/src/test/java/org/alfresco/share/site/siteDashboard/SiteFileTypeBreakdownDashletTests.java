package org.alfresco.share.site.siteDashboard;

import static org.alfresco.common.Utils.testDataFolder;
import static org.alfresco.share.TestUtils.FILE_CONTENT;
import static org.alfresco.utility.model.FileModel.getRandomFileModel;

import java.io.File;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.share.dashlet.SiteFileTypeBreakdownDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.DataContent;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.*;

public class SiteFileTypeBreakdownDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_EMPTY_MESSAGE = "siteFileContributorBreakdown.emptyMessage";
    private static final String EXPECTED_TITLE = "siteFileTypeBreakdown.dashletTitle";
    private static final String EXPECTED_JPEG_IMAGE = "siteFileTypeBreakdown.fileType.jpeg";
    private static final String EXPECTED_PLAIN_TEXT = "siteFileTypeBreakdown.fileType.plainText";
    private static final String EXPECTED_JPEG_TOOLTIP = "siteFileTypeBreakdown.fileTypeJpeg.tooltip";
    private static final String EXPECTED_PLAIN_TEXT_TOOLTIP = "siteFileTypeBreakdown.fileTypePlainText.tooltip";

    private static final int EXPECTED_PIE_CHART_SIZE = 2;
    private static final String PIE_CHART_FIRST_SLICE = "1";
    private static final String PIE_CHART_SECOND_SLICE = "2";

    private static final String JPG = "jpg";
    private static final String NEW_AVATAR_IMAGE = "newavatar.";

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private SiteFileTypeBreakdownDashlet siteFileTypeBreakdownDashlet;

    @Autowired
    private DataContent dataContent;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteFileTypeBreakdownDashlet = new SiteFileTypeBreakdownDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        addDashlet(user.get(), site.get(), DashboardCustomization.SiteDashlet.FILE_TYPE_BREAKDOWN, 1, 2);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C5783")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void checkSpecificMessageWhenSiteFileTypeBreakdownDashletIsEmpty()
    {
        siteDashboardPage.navigate(site.get());
        siteFileTypeBreakdownDashlet
            .assertDashletEmptyMessageEquals(language.translate(EXPECTED_EMPTY_MESSAGE));
    }

    @TestRail (id = "C5785")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD, "PieChartTests" })
    public void shouldDisplayCreatedFilesInPieChart()
    {
        File imageToUploadWithJpgExtension = new File(testDataFolder.concat(NEW_AVATAR_IMAGE.concat(JPG)));
        dataContent.usingUser(user.get())
            .usingSite(site.get())
            .uploadDocument(imageToUploadWithJpgExtension);

        FileModel fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(fileModel);

        siteDashboardPage.navigate(site.get());
        siteFileTypeBreakdownDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_TITLE))
            .assertPieChartSizeEquals(EXPECTED_PIE_CHART_SIZE)
            .assertPieChartFileTypeNameEquals(language.translate(EXPECTED_JPEG_IMAGE))
            .assertPieChartFileTypeNameEquals(language.translate(EXPECTED_PLAIN_TEXT))
            .assertPieChartTooltipTextEquals(language.translate(EXPECTED_JPEG_TOOLTIP), PIE_CHART_FIRST_SLICE)
            .assertPieChartTooltipTextEquals(language.translate(EXPECTED_PLAIN_TEXT_TOOLTIP), PIE_CHART_SECOND_SLICE);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
