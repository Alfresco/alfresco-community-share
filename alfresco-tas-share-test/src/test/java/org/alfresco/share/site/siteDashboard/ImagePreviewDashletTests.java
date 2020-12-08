package org.alfresco.share.site.siteDashboard;

import static org.alfresco.common.Utils.testDataFolder;

import java.io.File;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.ImagePreviewDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.DataContent;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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

    private UserModel userModel;
    private SiteModel siteModel;
    private FileModel fileModel;

    @Autowired
    private ImagePreviewDashlet imagePreviewDashlet;

    //@Autowired
    private DocumentDetailsPage documentDetailsPage;

    @Autowired
    private DataContent dataContent;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(userModel);

        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();
        addDashlet(siteModel, Dashlets.IMAGE_PREVIEW, 1);
    }

    @TestRail (id = "C5414")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void checkDisplayImagePreviewDashletWithNoImage()
    {
        siteDashboardPage.navigate(siteModel);
        imagePreviewDashlet
             .assertDashletTitleEquals(language.translate(EXPECTED_IMAGE_PREVIEW_DASHLET_TITLE))
             .clickOnHelpIcon(DashletHelpIcon.IMAGE_PREVIEW)
             .assertHelpBalloonMessageEquals(language.translate(EXPECTED_IMAGE_PREVIEW_HELP_BALLOON_MESSAGE))
             .closeHelpBalloon()
             .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail (id = "C5422")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayUploadedImagesInImagePreviewDashlet()
    {
        siteDashboardPage.navigate(siteModel);
        imageToUploadWithJpgExtension = new File(testDataFolder.concat(NEW_AVATAR_IMAGE.concat(JPG)));
        fileModel = dataContent
            .usingUser(userModel)
            .usingSite(siteModel)
            .uploadDocument(imageToUploadWithJpgExtension);

        fileModel = dataContent
            .usingUser(userModel)
            .usingSite(siteModel)
            .uploadDocument(imageToUploadWithBmpExtension);

        fileModel = dataContent
            .usingUser(userModel)
            .usingSite(siteModel)
            .uploadDocument(imageToUploadWithGifExtension);

        fileModel = dataContent
            .usingUser(userModel)
            .usingSite(siteModel)
            .uploadDocument(imageToUploadWithPngExtension);

        imagePreviewDashlet
            .assertImagePreviewIsDisplayed(NEW_AVATAR_IMAGE.concat(JPG))
            .assertImagePreviewIsDisplayed(NEW_AVATAR_IMAGE.concat(BMP))
            .assertImagePreviewIsDisplayed(NEW_AVATAR_IMAGE.concat(GIF))
            .assertImagePreviewIsDisplayed(NEW_AVATAR_IMAGE.concat(PNG));
    }

    @TestRail (id = "C5421")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayUploadedImageInDocumentDetailsPage()
    {
        siteDashboardPage.navigate(siteModel);
        imageToUploadWithJpgExtension = new File(testDataFolder.concat(LIGHTHOUSE_IMAGE.concat(JPG)));
        fileModel = dataContent
            .usingUser(userModel)
            .usingSite(siteModel)
            .uploadDocument(imageToUploadWithJpgExtension);

        imagePreviewDashlet.assertImagePreviewIsDisplayed(LIGHTHOUSE_IMAGE.concat(JPG));
        imagePreviewDashlet.clickViewDetailsIcon(LIGHTHOUSE_IMAGE.concat(JPG));

        documentDetailsPage
            .assertDocumentTitleEquals(fileModel)
            .assertUploadedDocumentImageIsDisplayed();
    }

    @TestRail (id = "C588494")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDownloadImageFilePreview()
    {
        siteDashboardPage.navigate(siteModel);
        imageToUploadWithJpgExtension = new File(testDataFolder.concat(ANIMATED_IMAGE.concat(GIF)));
        fileModel = dataContent
            .usingUser(userModel)
            .usingSite(siteModel)
            .uploadDocument(imageToUploadWithJpgExtension);

        imagePreviewDashlet
            .assertImagePreviewIsDisplayed(ANIMATED_IMAGE.concat(GIF))
            .clickDownloadIcon(ANIMATED_IMAGE.concat(GIF))
            .assertDownloadedDocumentExists(ANIMATED_IMAGE, GIF);
    }

    @AfterClass (alwaysRun = true)
    public void cleanupTest()
    {
        removeUserFromAlfresco(userModel);
        deleteSites(siteModel);
    }
}