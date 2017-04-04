package org.alfresco.share.site.siteDashboard;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.Download;
import org.alfresco.po.share.dashlet.ImagePreviewDashlet;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.alfresco.dataprep.DashboardCustomization.DashletLayout.TWO_COLUMNS_WIDE_RIGHT;
import static org.alfresco.dataprep.DashboardCustomization.SiteDashlet.IMAGE_PREVIEW;
import static org.springframework.social.alfresco.api.entities.Site.Visibility.PUBLIC;
import static org.testng.Assert.*;

public class ImagePreviewDashletTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboard;

    @Autowired
    ImagePreviewDashlet imagePreviewDashlet;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    Download download;

    private String siteName1 = String.format("Site1-%s", DataUtil.getUniqueIdentifier());
    private String siteName2 = String.format("Site2-%s", DataUtil.getUniqueIdentifier());
    private String siteName3 = String.format("Site3-%s", DataUtil.getUniqueIdentifier());
    private final String fileName = "newavatar.jpg";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        String userName = String.format("User%s", DataUtil.getUniqueIdentifier());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName3, siteName3, PUBLIC);
        contentService.uploadFileInSite(userName, password, siteName3, testDataFolder + fileName);
        siteService.create(userName, password, domain, siteName1, siteName1, PUBLIC);
        siteService.create(userName, password, domain, siteName2, siteName2, PUBLIC);
        siteService.addDashlet(userName, password, siteName1, IMAGE_PREVIEW, TWO_COLUMNS_WIDE_RIGHT, 1, 1);
        siteService.addDashlet(userName, password, siteName2, IMAGE_PREVIEW, TWO_COLUMNS_WIDE_RIGHT, 1, 1);
        siteService.addDashlet(userName, password, siteName3, IMAGE_PREVIEW, TWO_COLUMNS_WIDE_RIGHT, 1, 1);

        setupAuthenticatedSession(userName, password);
    }

    @TestRail(id = "C5414")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
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

    @TestRail(id = "C5421")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
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

    @TestRail(id = "C5422")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
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
        download.acceptAlertIfDisplayed();
        getBrowser().waitInSeconds(2);
        assertTrue(download.isFileInDirectory(fileName, null), fileName + " is downloaded to computer.");
    }
}