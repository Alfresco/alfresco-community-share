package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.ContentImEditingDashlet;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/23/2017.
 */
public class ContentImEditingDashletTests extends ContextAwareWebTest
{
    @Autowired
    UserDashboardPage userDashboardPage;
    @Autowired
    ContentImEditingDashlet contentImEditingDashlet;
    @Autowired
    DocumentLibraryPage documentLibraryPage;
    @Autowired
    SiteDashboardPage siteDashboardPage;

    private String testUserName = "TestUser" + RandomData.getRandomAlphanumeric();
    private String siteName = "contentImEdititngSite" + RandomData.getRandomAlphanumeric();
    private String documentName = "TestDocument";
    private String documentContent = "Test document for content i'm editing dashlet.";

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, testUserName, password, testUserName + domain, "Valid", "User");
        userService
            .addDashlet(testUserName, password, DashboardCustomization.UserDashlet.CONTENT_EDITING, DashboardCustomization.DashletLayout.THREE_COLUMNS, 1,
                3);
        siteService.create(testUserName, password, domain, siteName, "test site", SiteService.Visibility.PUBLIC);
        contentService.createDocument(testUserName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, documentName, documentContent);
        setupAuthenticatedSession(testUserName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, testUserName);
        siteService.delete(adminUser, adminPassword, domain, siteName);
    }

    @Test (groups = { TestGroup.SHARE, "UserDashboard", "Acceptance" })
    public void accessContentImEditingTests()
    {
        LOG.info("Step 1: Navigate to user dashboard and check that Content I'm editing dashlet is displayed");
        userDashboardPage.navigate(testUserName);
        Assert.assertEquals(contentImEditingDashlet.getDashletTitle(), "Content I'm Editing");
        Assert.assertTrue(userDashboardPage.isDashletAddedInPosition(Dashlets.CONTENT_I_AM_EDITING, 1, 3));
    }

    @Test (groups = { TestGroup.SHARE, "UserDashboard", "Acceptance" })
    public void verifyHelpIcon()
    {
        LOG.info("Step 1: Navigate to User dashboard and check the help option availability");
        userDashboardPage.navigate(testUserName);
        contentImEditingDashlet.clickHelpIcon();
        Assert.assertEquals(contentImEditingDashlet.getHelpBalloonMessage(), language.translate("contentImEditingDashlet.HelpBalloonText"),
            language.translate("contentImEditingDashlet.HelpBalloonText") + " is not the text displayed");
        contentImEditingDashlet.closeHelpBalloon();
        Assert.assertFalse(contentImEditingDashlet.isBalloonDisplayed(), "Help balloon is still displayed.");
    }

    @Test (groups = { TestGroup.SHARE, "UserDashboard", "Acceptance" })
    public void checkDashletContent()
    {
        LOG.info("Step 1: Navigate to User Dashboard page and check the Content I'm Editing dashlet content");
        userDashboardPage.navigate(testUserName);
        Assert.assertTrue(contentImEditingDashlet.isHeaderDisplayed("Documents"), "Documents is not available");
        Assert.assertTrue(contentImEditingDashlet.isHeaderDisplayed("Blog posts"), "Blog posts is not available");
        Assert.assertTrue(contentImEditingDashlet.isHeaderDisplayed("Wiki pages"), "Wiki pages is not available");
        Assert.assertTrue(contentImEditingDashlet.isHeaderDisplayed("Forum posts"), "Forum posts is not available");
    }

    @Test (groups = { TestGroup.SHARE, "UserDashboard", "Acceptance" })
    public void checkContentImEditingDashletMajorFunctionality()
    {
        LOG.info("Step 1: Start editing document and check that document is displayed on dashlet");
        Assert.assertFalse(contentImEditingDashlet.isDocumentDisplayedInDashlet(documentName), documentName + " is displayed");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(documentName, ItemActions.EDIT_OFFLINE, documentLibraryPage);
        userDashboardPage.navigate(testUserName);
        Assert.assertTrue(contentImEditingDashlet.isDocumentDisplayedInDashlet(documentName), documentName + " is not displayed");

        LOG.info("Step 2: Click document link and check that user is redirected to the document library page");
        contentImEditingDashlet.clickDocumentLink(documentName, documentLibraryPage);
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » Document Library", "User is not redirected to Document Library");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(documentName), documentName + " is not displayed");

        LOG.info("Step 3: Click site name link and check that user is redirected to the site dashboard page");
        userDashboardPage.navigate(testUserName);
        contentImEditingDashlet.clickSiteLink(siteName, siteDashboardPage);
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » Site Dashboard", "Alfresco » Site Dashboard is not displayed");
        Assert.assertEquals(siteDashboardPage.getSiteName(), siteName, siteName + " is not the site name displayed");
    }
}
