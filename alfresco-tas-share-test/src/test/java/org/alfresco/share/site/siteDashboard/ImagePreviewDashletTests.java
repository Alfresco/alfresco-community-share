package org.alfresco.share.site.siteDashboard;

import static org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import static org.alfresco.common.Utils.testDataFolder;

import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.dashlet.ImagePreviewDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.DataContent;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.File;

public class ImagePreviewDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String JPG = "jpg";
    private static final String BMP = "bmp";
    private static final String GIF = "gif";
    private static final String PNG = "png";
    private static final String NEW_AVATAR_IMAGE = "newavatar.";
    private static final String LIGHTHOUSE_IMAGE = "Lighthouse.";
    private static final String ANIMATED_IMAGE = "gif_animated.";
    private static final String EXPECTED_IMAGE_PREVIEW_DASHLET_TITLE = "imagePreviewDashlet.title";
    private static final String EXPECTED_IMAGE_PREVIEW_HELP_BALLOON_MESSAGE = "imagePreviewDashlet.helpBalloonMessage";

    private File imageToUploadWithJpgExtension = new File(testDataFolder.concat(NEW_AVATAR_IMAGE.concat(JPG)));
    private File imageToUploadWithBmpExtension = new File(testDataFolder.concat(NEW_AVATAR_IMAGE.concat(BMP)));
    private File imageToUploadWithGifExtension = new File(testDataFolder.concat(NEW_AVATAR_IMAGE.concat(GIF)));
    private File imageToUploadWithPngExtension = new File(testDataFolder.concat(NEW_AVATAR_IMAGE.concat(PNG)));

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private final ThreadLocal<DataContent> dataContent = new ThreadLocal<>();

    private ImagePreviewDashlet imagePreviewDashlet;
    private DocumentDetailsPage documentDetailsPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        imagePreviewDashlet = new ImagePreviewDashlet(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        dataContent.set(applicationContext.getBean(DataContent.class));

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        addDashlet(user.get(), site.get(), SiteDashlet.IMAGE_PREVIEW, 1, 2);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C5414")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void checkDisplayImagePreviewDashletWithNoImage()
    {
        siteDashboardPage.navigate(site.get());
        imagePreviewDashlet
             .assertDashletTitleEquals(language.translate(EXPECTED_IMAGE_PREVIEW_DASHLET_TITLE))
             .clickOnHelpIcon(DashletHelpIcon.IMAGE_PREVIEW)
             .assertHelpBalloonMessageEquals(language.translate(EXPECTED_IMAGE_PREVIEW_HELP_BALLOON_MESSAGE))
             .closeHelpBalloon()
             .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail (id = "C5422")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayUploadedImagesInImagePreviewDashlet()
    {
        imageToUploadWithJpgExtension = new File(testDataFolder.concat(NEW_AVATAR_IMAGE.concat(JPG)));
        dataContent.get()
            .usingUser(user.get())
            .usingSite(site.get())
            .uploadDocument(imageToUploadWithJpgExtension);

        dataContent.get()
            .usingUser(user.get())
            .usingSite(site.get())
            .uploadDocument(imageToUploadWithBmpExtension);

        dataContent.get()
            .usingUser(user.get())
            .usingSite(site.get())
            .uploadDocument(imageToUploadWithGifExtension);

        dataContent.get()
            .usingUser(user.get())
            .usingSite(site.get())
            .uploadDocument(imageToUploadWithPngExtension);

        siteDashboardPage.navigate(site.get());
        imagePreviewDashlet
            .assertImagePreviewIsDisplayed(NEW_AVATAR_IMAGE.concat(JPG))
            .assertImagePreviewIsDisplayed(NEW_AVATAR_IMAGE.concat(BMP))
            .assertImagePreviewIsDisplayed(NEW_AVATAR_IMAGE.concat(GIF))
            .assertImagePreviewIsDisplayed(NEW_AVATAR_IMAGE.concat(PNG));
    }

    @TestRail (id = "C5421")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayUploadedImageInDocumentDetailsPage()
    {
        siteDashboardPage.navigate(site.get());
        imageToUploadWithJpgExtension = new File(testDataFolder.concat(LIGHTHOUSE_IMAGE.concat(JPG)));
        FileModel fileModel = dataContent.get()
            .usingUser(user.get())
            .usingSite(site.get())
            .uploadDocument(imageToUploadWithJpgExtension);

        imagePreviewDashlet.assertImagePreviewIsDisplayed(LIGHTHOUSE_IMAGE.concat(JPG))
            .clickViewDetailsIcon(LIGHTHOUSE_IMAGE.concat(JPG));

        documentDetailsPage
            .assertDocumentTitleEquals(fileModel)
            .assertUploadedDocumentImageIsDisplayed();
    }

    @TestRail (id = "C588494")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDownloadImageFilePreview()
    {
        siteDashboardPage.navigate(site.get());
        imageToUploadWithJpgExtension = new File(testDataFolder.concat(ANIMATED_IMAGE.concat(GIF)));
        dataContent.get()
            .usingUser(user.get())
            .usingSite(site.get())
            .uploadDocument(imageToUploadWithJpgExtension);

        imagePreviewDashlet
            .assertImagePreviewIsDisplayed(ANIMATED_IMAGE.concat(GIF))
            .clickDownloadIcon(ANIMATED_IMAGE.concat(GIF))
            .assertDownloadedDocumentExists(ANIMATED_IMAGE, GIF);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}