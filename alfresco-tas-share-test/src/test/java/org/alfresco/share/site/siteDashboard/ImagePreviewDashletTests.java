package org.alfresco.share.site.siteDashboard;

import static org.alfresco.dataprep.DashboardCustomization.DashletLayout.TWO_COLUMNS_WIDE_RIGHT;
import static org.alfresco.dataprep.DashboardCustomization.SiteDashlet.IMAGE_PREVIEW;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.dashlet.ImagePreviewDashlet;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ImagePreviewDashletTests extends ContextAwareWebTest
{
    private final String fileName = "newavatar.jpg";
    protected String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    @Autowired
    SiteDashboardPage siteDashboard;
    @Autowired
    ImagePreviewDashlet imagePreviewDashlet;
    @Autowired
    DocumentLibraryPage documentLibraryPage;
    @Autowired
    DocumentDetailsPage documentDetailsPage;

    private String siteName1 = String.format("Site1-%s", RandomData.getRandomAlphanumeric());
    private String siteName2 = String.format("Site2-%s", RandomData.getRandomAlphanumeric());
    private String siteName3 = String.format("Site3-%s", RandomData.getRandomAlphanumeric());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName3, siteName3, SiteService.Visibility.PUBLIC);
        contentService.uploadFileInSite(userName, password, siteName3, testDataFolder + fileName);
        siteService.create(userName, password, domain, siteName1, siteName1, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteName2, siteName2, SiteService.Visibility.PUBLIC);
        siteService.addDashlet(userName, password, siteName1, IMAGE_PREVIEW, TWO_COLUMNS_WIDE_RIGHT, 1, 1);
        siteService.addDashlet(userName, password, siteName2, IMAGE_PREVIEW, TWO_COLUMNS_WIDE_RIGHT, 1, 1);
        siteService.addDashlet(userName, password, siteName3, IMAGE_PREVIEW, TWO_COLUMNS_WIDE_RIGHT, 1, 1);

        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = false)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName3);
    }

    @TestRail (id = "C5414")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void imagePreviewDashletWithNoImage()
    {
        LOG.info("STEP 1 - View \"Image Preview\" dashlet");
        siteDashboard.navigate(siteName1);
        assertEquals(siteDashboard.getPageTitle(), "Alfresco » Site Dashboard", "Displayed page=");
        assertEquals(imagePreviewDashlet.getDashletTitleText(), "Image Preview");
        assertTrue(imagePreviewDashlet.isHelpIconDisplayed(), "Help icon is displayed.");

        LOG.info("STEP 2 - Click  \"Help\" icon and verify it's contents");
        imagePreviewDashlet.clickHelpIcon();
        assertTrue(imagePreviewDashlet.isBalloonDisplayed(), "Help Balloon is displayed.");
        assertEquals(imagePreviewDashlet.getHelpBalloonText(), language.translate("imagePreviewDashlet.helpBalloonMessage"), "Help balloon message=");

        LOG.info("STEP 3 - Close  \"Help\" icon");
        imagePreviewDashlet.CloseHelpBalloon();
        assertFalse(imagePreviewDashlet.isBallonDisplayedNoWait(), "Help Balloon is displayed.");
    }

    @TestRail (id = "C5421")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES , "tobefixed"})
    public void imagePreviewDashletWithSomeImages()
    {
        LOG.info("STEP 1 - Create images in the \"Document Library\" of the site");
        documentLibraryPage.navigate(siteName2);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("STEP 2 - Upload some images in \"Document Library\" of the site");
        documentLibraryPage.uploadNewImage(testDataFolder + fileName);
        documentLibraryPage.uploadNewImage(testDataFolder + "newavatar.bmp");
        documentLibraryPage.uploadNewImage(testDataFolder + "newavatar.gif");
        documentLibraryPage.uploadNewImage(testDataFolder + "newavatar.png");

        LOG.info("STEP 3 - Navigate to site's dashboard");
        siteDashboard.navigate(siteName2);
        getBrowser().refresh();
        assertEquals(siteDashboard.getPageTitle(), "Alfresco » Site Dashboard", "Displayed page=");
        assertTrue(imagePreviewDashlet.isImageDisplayed(fileName), ".jpg image displayed");
        assertTrue(imagePreviewDashlet.isImageDisplayed("newavatar.bmp"), ".bmp image displayed");
        assertTrue(imagePreviewDashlet.isImageDisplayed("newavatar.gif"), ".gif image displayed");
        assertTrue(imagePreviewDashlet.isImageDisplayed("newavatar.png"), ".png image displayed");
    }

    @TestRail (id = "C5422")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES, "tobefixed" })
    public void imagePreviewAvailableActions()
    {
        LOG.info("Precondition: Navigate to site's dashboard");
        siteDashboard.navigate(siteName3);
        assertEquals(siteDashboard.getPageTitle(), "Alfresco » Site Dashboard", "Displayed page=");
        assertTrue(imagePreviewDashlet.isImageDisplayed(fileName), ".jpg image displayed");

        LOG.info("Step1: Click 'View Details' icon");
        imagePreviewDashlet.clickViewDetailsIcon(fileName);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");

        LOG.info("Step2: Navigate to Site Dashboard");
        siteDashboard.navigate(siteName3);
        assertEquals(siteDashboard.getPageTitle(), "Alfresco » Site Dashboard", "Displayed page=");

        LOG.info("Step3: Click 'Download' icon");
        imagePreviewDashlet.clickDownloadIcon(fileName);
        assertTrue(isFileInDirectory(fileName, null), fileName + " is downloaded to computer.");
    }
}