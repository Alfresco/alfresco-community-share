package org.alfresco.share.site.siteDashboard;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.share.dashlet.SiteFileTypeBreakdownDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by Claudia Agache on 7/25/2016.
 */
public class SiteFileTypeBreakdownDashletTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboard;

    @Autowired
    SiteFileTypeBreakdownDashlet siteFileTypeBreakdownDashlet;

    private String user = String.format("user%s", DataUtil.getUniqueIdentifier());
    private String siteName;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
    }

    @TestRail(id = "C5783")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void noFilesAvailableInTheSiteLibrary()
    {
        siteName = String.format("SiteName-C5783-%s", DataUtil.getUniqueIdentifier());
        siteService.create(user, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addDashlet(user, password, siteName, DashboardCustomization.SiteDashlet.FILE_TYPE_BREAKDOWN, DashboardCustomization.DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(user, password);

        LOG.info("STEP 1:Check the dashlet title for Site File Type Breakdown.");
        siteDashboard.navigate(siteName);
        assertEquals(siteFileTypeBreakdownDashlet.getDashletMessage(), "No data found", "The text: 'No data found' is displayed.");
    }

    @TestRail(id = "C5785")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void multipleFileTypesAvailableInTheSiteLibrary()
    {
        String fileName = "File-C5785";
        siteName = String.format("SiteName-C5785-%s", DataUtil.getUniqueIdentifier());
        siteService.create(user, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addDashlet(user, password, siteName, DashboardCustomization.SiteDashlet.FILE_TYPE_BREAKDOWN, DashboardCustomization.DashletLayout.THREE_COLUMNS, 3, 1);
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + fileName+".docx");
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + fileName+".txt");
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + "newavatar.jpg");
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + "newavatar.bmp");
        setupAuthenticatedSession(user, password);

        siteDashboard.navigate(siteName);
        getBrowser().refresh();

        LOG.info("STEP 1: Check the content displayed by the Site File Type Breakdown dashlet.");
        assertEquals(siteFileTypeBreakdownDashlet.getNumberOfPieChartSlices(), 4, "In Site's library there are only 4 file types.");

        LOG.info("STEP 2: Hover over each section of the breakdown chart.");
        Map<String, String> fileDetails = siteFileTypeBreakdownDashlet.getPieChartSliceTooltip();
        assertTrue(fileDetails.containsKey("JPEG Image"), "Document type is displayed.");
        assertTrue(fileDetails.get("JPEG Image").contains("1 items (25%)"), "Only 1 jpeg file is in site's library.");
        assertTrue(fileDetails.get("JPEG Image").contains("Size: 65 KB"), "Size of the file is displayed.");

        assertTrue(fileDetails.containsKey("Bitmap Image"), "Document type is displayed.");
        assertTrue(fileDetails.get("Bitmap Image").contains("1 items (25%)"), "Only 1 docx file is in site's library.");
        assertTrue(fileDetails.get("Bitmap Image").contains("Size: 732 KB"), "Size of the file is displayed.");

        assertTrue(fileDetails.containsKey("Plain Text"), "Document type is displayed.");
        assertTrue(fileDetails.get("Plain Text").contains("1 items (25%)"), "Only 1 txt file is in site's library.");
        assertTrue(fileDetails.get("Plain Text").contains("Size: 27 bytes"), "Size of the file is displayed.");

        assertTrue(fileDetails.containsKey("Microsoft Word 2007"), "Document type is displayed.");
        assertTrue(fileDetails.get("Microsoft Word 2007").contains("1 items (25%)"), "Only 1 docx file is in site's library.");
        assertTrue(fileDetails.get("Microsoft Word 2007").contains("Size: 12 KB"), "Size of the file is displayed.");
    }
}
