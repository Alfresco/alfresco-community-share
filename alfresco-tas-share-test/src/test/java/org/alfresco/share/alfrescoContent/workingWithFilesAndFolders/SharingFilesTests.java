package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import java.net.URL;

import org.alfresco.common.EnvProperties;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SharingFilesTests extends ContextAwareWebTest
{
    private final String user = String.format("C7095User%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("C7095SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("C7095SiteName%s", RandomData.getRandomAlphanumeric());
    private final String fileNameC7095 = "C7095title";
    private final String fileContent = "content";
    private final String fileNameC7096 = "file C7076";
    private final String fielNameC7097 = "File C7097";
    private final String fileNameC7099 = "File C7099";
    private final String fileNameC7649 = String.format("fileC7649%s", RandomData.getRandomAlphanumeric());
    private final String folderName = "C7093 folder";
    private final String siteNameC7093 = String.format("C7093%s", RandomData.getRandomAlphanumeric());
    private final String siteNameC7649 = String.format("siteNameC7649%s", RandomData.getRandomAlphanumeric());
    //@Autowired
    private DocumentLibraryPage documentLibraryPage;
    //@Autowired
    private SocialFeatures social;
    //@Autowired
    private LoginPage loginPage;
    //@Autowired
    private DocumentDetailsPage documentDetails;
    @Autowired
    private EnvProperties envProperties;
    private String sharedUrl;
    private String windowToSwitchToAlfresco;
    private String windowToCloseGPlus;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteNameC7093, description, SiteService.Visibility.PUBLIC);
        siteService.create(user, password, domain, siteNameC7649, description, SiteService.Visibility.PUBLIC);
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileNameC7095, fileContent);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fielNameC7097, fileContent);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileNameC7096, fileContent);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileNameC7099, fileContent);
        contentService.createDocument(user, password, siteNameC7649, DocumentType.TEXT_PLAIN, fileNameC7649, fileContent);
        contentService.createFolder(user, password, folderName, siteNameC7093);
        //setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteNameC7093);
        siteService.delete(adminUser, adminPassword, siteNameC7649);
        siteService.delete(adminUser, adminPassword, siteName);
    }


    @TestRail (id = "C7095")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void shareWithFacebook()
    {
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Check that the Share button is available and click Share");
        Assert.assertTrue(social.isShareButtonDisplayed(fileNameC7095), "The Share button is not displayed");
        Assert.assertEquals(social.getShareButtonTooltip(fileNameC7095), "Share document", "Share button tooltip is not correct");
        social.clickShareButton(fileNameC7095);
        getBrowser().waitUntilElementClickable(social.quickShareWindow, 10L);
        Assert.assertTrue(social.isQuickshareWindowDisplayed(), "Quickshare window is not displayed");
        Assert.assertTrue(social.isPublicLinkDisplayed(), "public link is not displayed");

        LOG.info("Step 2: Click Facebook icon");
        social.clickShareWithFacebook();
        getBrowser().switchWindow();
        getBrowser().waitUntilElementIsDisplayedWithRetry(social.facebookHomeLink, 2);
        Assert.assertEquals(social.getFacebookWindowTitle(), "Facebook", "User is not redirected to the Facebook page");
        social.loginFacebook();
        Assert.assertTrue(social.isShareLinkDisplayedOnFacebook(), "Share link is not displayed on Facebook");
        getBrowser().closeWindowAcceptingModalDialog();
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C7096")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void shareWithTwitter()
    {
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(siteName);
        URL url2 = envProperties.getShareUrl();
        String expectedLink = "File" + " " + fileNameC7096 + " " + "shared from Alfresco Content Services " + url2.toString().replace(":80/", "/") + "/s";
        documentLibraryPage.getRelativePath();

        LOG.info("Step 1: Check that the Share button is available and click Share");
        Assert.assertTrue(social.isShareButtonDisplayed(fileNameC7096), "The Share button is not displayed");
        Assert.assertEquals(social.getShareButtonTooltip(fileNameC7096), "Share document", "Share button tooltip is not correct");
        social.clickShareButton(fileNameC7096);
        getBrowser().waitUntilElementClickable(social.quickShareWindow, 10L);
        Assert.assertTrue(social.isQuickshareWindowDisplayed(), "Quickshare window is not displayed");
        Assert.assertTrue(social.isPublicLinkDisplayed(), "public link is not displayed");

        LOG.info("Step 2: Click Twitter icon");
        social.clickTwitterIcon();
        getBrowser().waitInSeconds(5);
        //Switch to new window opened
        getBrowser().switchWindow("https://twitter.com");
//        Assert.assertEquals(social.getPageTitle(), "Share a link on Twitter", "User is not redirected to Twitter");
        Assert.assertEquals(social.getTwitterShareLink(), expectedLink, "Share link is not correct");
        Assert.assertEquals(social.getTwitterPageTitle(), "Share a link with your followers");
        getBrowser().closeWindowAndSwitchBack();
        cleanupAuthenticatedSession();
    }


    // The consumer version of Google+ is shutting down in April 2019. This test is now disabled.
    @Bug (id = "ACE-5768")
    @TestRail (id = "C7097")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, enabled = false)

    public void shareWithGooglePlus()
    {

        {
            try
            {
                setupAuthenticatedSession(user, password);
                documentLibraryPage.navigate(siteName);
                String url = getBrowser().getCurrentUrl();
                String server = url.substring(7, 26);
                String expectedLink = "http://" + server + "/share/s";
                windowToSwitchToAlfresco = getBrowser().getWindowHandle();
                LOG.info("Step 1: For file click Share icon");
                social.clickShareButton(fielNameC7097);
                getBrowser().waitUntilElementClickable(social.quickShareWindow, 10L);
                Assert.assertTrue(social.isQuickshareWindowDisplayed(), "Quickshare window is not displayed");
                Assert.assertTrue(social.isPublicLinkDisplayed(), "public link is not displayed");

                LOG.info("Step 2: Click Google+ icon");
                social.clickGooglePlus();

                getBrowser().switchWindow();
                social.loginToGoogleAccount();
                windowToCloseGPlus = getBrowser().getWindowHandle();
                getBrowser().waitInSeconds(6);
                Assert.assertEquals(getBrowser().getCurrentUrl().substring(0, 24), "https://plus.google.com/");
                Assert.assertEquals(social.getLinkSharedWithGooglePlus(), expectedLink, "Link shared on Google Plus is not corerct");
                getBrowser().closeWindowAndSwitchBack();
                cleanupAuthenticatedSession();
            } finally
            {
                getBrowser().closeWindowAndSwitchBackParametrized(windowToSwitchToAlfresco, windowToCloseGPlus);
            }
        }
    }

    @TestRail (id = "C7099")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void unshareDocument()
    {
        /**
         * Precondition - File is shared
         */

        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(siteName);
        social.clickShareButton(fileNameC7099);

        LOG.info("Step 1: Mouse over Share icon and get the Share tooltip message");
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(social.getShareButtonTooltip(fileNameC7099), "This document is shared (click for more options)", "Tootltip message is not correct");

        LOG.info("Step 2: Click the Share icon");
        social.clickShareButton(fileNameC7099);
        getBrowser().waitUntilElementClickable(social.quickShareWindow, 10L);
        Assert.assertTrue(social.isQuickshareWindowDisplayed(), "Quickshare window is not displayed");

        LOG.info("Step 3: Click Unshare link");
        social.clickUnshareButton();
        Assert.assertEquals(social.getShareButtonTooltip(fileNameC7099), "Share document", "Share button tooltip is not correct");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C7093")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void shareFolder()

    {
        setupAuthenticatedSession(user, password);
        LOG.info("Step 1: Check that Share is not available for folders");
        documentLibraryPage.navigate(siteNameC7093);
        Assert.assertFalse(social.checkShareButtonAvailability(), "The Share button is displayed for folder");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C7649")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void sharedFilesContentAvailability()

    {
        setupAuthenticatedSession(user, password);
        LOG.info("Preconditions: Navigate to Document Library ppage for the created site");
        documentLibraryPage.navigate(siteNameC7649);

        LOG.info("Step1: Click Share icon on the file and verify a pop-up displaying the URL for the file is opened.");

        social.clickShareButton(fileNameC7649);
        Assert.assertTrue(social.isShareUrlDisplayed(), "Shared Url is not displayed");

        LOG.info("Step2: Save the URL for the file and logout from Share");
        social.clickPublicLinkViewButton();
        sharedUrl = getBrowser().getCurrentUrl();
        getBrowser().cleanUpAuthenticatedSession();

        LOG.info("Step3: Access the saved URL and verify the file is successfully displayed and 'Login' button is availbale on the page");
        getBrowser().get(sharedUrl);
        Assert.assertEquals(social.getContentTextFromSharedFilePage(), fileContent);
        Assert.assertTrue(social.isLoginButtonOnSharedFilePage(), "Login button is not displayed");

        LOG.info("Step4: Click 'Login' button and login to Share");
        social.clickLoginButtonOnSharedFilePage();
        loginPage.typeUserName(user);
        loginPage.typePassword(password);
        loginPage.clickLogin();

        LOG.info("Step5: Access the URL for the shared file and verify it is successfully displayed and 'Document Details' button is available on the page");
        getBrowser().get(sharedUrl);
        Assert.assertEquals(social.getContentTextFromSharedFilePage(), fileContent);
        Assert.assertTrue(social.isDocumentDetailsButtonOnSharedFilePageDisplayed(), "Document Details button is not displayed");

        LOG.info("Step6: Click 'Document details' button and verify the document's details page with correct preview is displayed");
        social.clickDocumentDetailsButtonOnSharedFilePage();
        Assert.assertEquals(documentDetails.getContentText(), fileContent);
        cleanupAuthenticatedSession();
    }
}
