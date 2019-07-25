package org.alfresco.share.site.siteDashboard;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteNoticeDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Created by Argint Alex
 */
public class SiteNoticeTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    SiteNoticeDashlet siteNoticeDashlet;

    private String uniqueIdentifier;
    private String userName;
    private String siteName;
    private String description;

    public void setup(String id) throws DataPreparationException
    {
        super.setup();

        uniqueIdentifier = String.format("-" + id + "-%s", RandomData.getRandomAlphanumeric());
        siteName = "siteName" + uniqueIdentifier;
        userName = "User" + uniqueIdentifier;
        description = "description" + uniqueIdentifier;

        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
        siteService.addDashlet(userName, password, siteName, DashboardCustomization.SiteDashlet.SITE_NOTICE,
            DashboardCustomization.DashletLayout.TWO_COLUMNS_WIDE_RIGHT, 1, 2);
        siteDashboardPage.navigate(siteName);
    }

    @TestRail (id = "C5556")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addSiteNoticeDashlet() throws DataPreparationException
    {

        LOG.info("Starting C5556");

        // preconditions
        setup("C5556");
        Assert.assertTrue(siteDashboardPage.isDashletAddedInPosition(Dashlets.SITE_NOTICE, 1, 2), "Dashlet is not added in the correct position ");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C5557")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void noticeDashletActions() throws DataPreparationException
    {
        LOG.info("Starting C5557");

        String helpMessage = "This dashlet displays a custom message on the dashboard, specified by the site manager";
        setup("C5557");

        Assert.assertTrue(siteNoticeDashlet.isHelpIconDisplayed(Dashlet.DashletHelpIcon.SITE_NOTICE), "Site notice help icon is not displayed");
        siteNoticeDashlet.clickOnHelpIcon(Dashlet.DashletHelpIcon.SITE_NOTICE);
        Assert.assertEquals(siteNoticeDashlet.getHelpBalloonMessage(), helpMessage, "Site notice dashlet help message is not correct");
        siteNoticeDashlet.closeHelpBalloon();

        LOG.info("Open site notice configure panel");
        siteNoticeDashlet.openConfigurePanel();
        Assert.assertTrue(siteNoticeDashlet.isConfigurePanelOpened(), "Configure site notice panel is not opened");

        LOG.info("Closing the configure panel");
        siteNoticeDashlet.closeConfigurePanel();
        Assert.assertFalse(siteNoticeDashlet.isConfigurePanelOpened(), "Configure site notice panel is opened");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C5558")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void customizeDashlet() throws DataPreparationException
    {
        LOG.info("Starting C5558");

        setup("C5558");

        String text = "test";

        siteNoticeDashlet.openConfigurePanel();
        Assert.assertTrue(siteNoticeDashlet.isConfigurePanelOpened(), "Site notice configure panel is not opened");

        siteNoticeDashlet.setSiteNoticeDashletTitle(text);
        siteNoticeDashlet.setSiteNoticeText(text);
        siteNoticeDashlet.clickOkButton();

        getBrowser().waitUntilElementContainsText(getBrowser().findElement(By.cssSelector("div.dashlet.notice-dashlet div.title")), text);
        Assert.assertEquals(siteNoticeDashlet.getDashletTitle(), text, "Site Notice title is not the same as the title that was set");
        Assert.assertEquals(siteNoticeDashlet.getSiteNoticeText(), text, "Site Notice text is not the same as the text that was set");
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C5559")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void customizeDashletCancelClose() throws DataPreparationException
    {
        SoftAssert softAssert = new SoftAssert();
        LOG.info("Starting C5559");

        setup("C5559");

        String text = "test";
        LOG.info("Step 1: Configure dashlet and click Cancel, check thta no changes are saved");
        siteNoticeDashlet.openConfigurePanel();
        softAssert.assertTrue(siteNoticeDashlet.isConfigurePanelOpened(), "Site notice configure panel is not opened");
        siteNoticeDashlet.setSiteNoticeDashletTitle(text);
        siteNoticeDashlet.setSiteNoticeText(text);
        siteNoticeDashlet.clickCancelbutton();
        softAssert.assertNotEquals(siteNoticeDashlet.getDashletTitle(), text, "Site Notice title is the same as the title that was set");
        softAssert.assertNotEquals(siteNoticeDashlet.getSiteNoticeText(), text, "Site Notice text is the same as the text that was set");
        LOG.info("Step 2: Configure dahslet and click close, check that no changes are made");
        siteNoticeDashlet.openConfigurePanel();
        softAssert.assertTrue(siteNoticeDashlet.isConfigurePanelOpened(), "Configure panel is not opened.");
        siteNoticeDashlet.setSiteNoticeDashletTitle(text);
        siteNoticeDashlet.setSiteNoticeText(text);
        siteNoticeDashlet.clickCancelbutton();

        softAssert.assertNotEquals(siteNoticeDashlet.getDashletTitle(), text, "Site Notice title is the same as the title that was set");
        softAssert.assertNotEquals(siteNoticeDashlet.getSiteNoticeText(), text, "Site Notice text is the same as the text that was set");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }
}