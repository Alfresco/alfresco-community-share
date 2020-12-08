package org.alfresco.share.site.siteDashboard;

import static org.alfresco.common.Utils.testDataFolder;
import static org.alfresco.utility.model.FileModel.getRandomFileModel;

import java.io.File;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteFileTypeBreakdownDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.DataContent;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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

    private File imageToUploadWithJpgExtension = new File(testDataFolder.concat(NEW_AVATAR_IMAGE.concat(JPG)));

    private UserModel userModel;
    private SiteModel siteModel;
    private FileModel fileModel;

    @Autowired
    private SiteFileTypeBreakdownDashlet siteFileTypeBreakdownDashlet;

    @Autowired
    private DataContent dataContent;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();
        cmisApi.authenticateUser(userModel);

        setupAuthenticatedSession(userModel);
        addDashlet(siteModel, Dashlets.SITE_FILE_TYPE_BREAKDOWN, 1);
    }

    @TestRail (id = "C5783")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void checkSpecificMessageWhenSiteFileTypeBreakdownDashletIsEmpty()
    {
        siteDashboardPage.navigate(siteModel);
        siteFileTypeBreakdownDashlet
            .assertDashletEmptyMessageEquals(language.translate(EXPECTED_EMPTY_MESSAGE));
    }

    @TestRail (id = "C5785")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayCreatedFilesInPieChart()
    {
        imageToUploadWithJpgExtension = new File(testDataFolder.concat(NEW_AVATAR_IMAGE.concat(JPG)));
        fileModel = dataContent
            .usingUser(userModel)
            .usingSite(siteModel)
            .uploadDocument(imageToUploadWithJpgExtension);

        fileModel = getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.usingSite(siteModel).createFile(fileModel);

        siteDashboardPage.navigate(siteModel);
        siteFileTypeBreakdownDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_TITLE))
            .assertPieChartSizeEquals(fileModel.getName(), EXPECTED_PIE_CHART_SIZE)
            .assertPieChartFileTypeNameEquals(language.translate(EXPECTED_JPEG_IMAGE))
            .assertPieChartFileTypeNameEquals(language.translate(EXPECTED_PLAIN_TEXT))
            .assertPieChartTooltipTextEquals(language.translate(EXPECTED_JPEG_TOOLTIP), PIE_CHART_FIRST_SLICE)
            .assertPieChartTooltipTextEquals(language.translate(EXPECTED_PLAIN_TEXT_TOOLTIP), PIE_CHART_SECOND_SLICE);
    }

    @AfterClass (alwaysRun = true)
    public void cleanupTest()
    {
        removeUserFromAlfresco(userModel);
        deleteSites(siteModel);
    }
}
