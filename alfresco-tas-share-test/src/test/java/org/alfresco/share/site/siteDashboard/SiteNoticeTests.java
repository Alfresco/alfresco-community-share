package org.alfresco.share.site.siteDashboard;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteNoticeDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.Test;

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

    public void setup(String id)
    {
        super.setup();

        uniqueIdentifier = "-" + id + "-" + DataUtil.getUniqueIdentifier();
        siteName = "siteName" + uniqueIdentifier;
        userName = "User" + uniqueIdentifier;
        description = "description" + uniqueIdentifier;

        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
        siteService.addDashlet(userName, DataUtil.PASSWORD, siteName, DashboardCustomization.SiteDashlet.SITE_NOTICE,
                DashboardCustomization.DashletLayout.TWO_COLUMNS_WIDE_RIGHT, 1, 2);
        siteDashboardPage.navigate(siteName);
    }

    @TestRail(id = "C5556")
    @Test
    public void addSiteNoticeDashlet()
    {

        LOG.info("Starting C5556");

        // preconditions
        setup("C5556");
        Assert.assertTrue(siteDashboardPage.isDashletAddedInPosition(Dashlets.SITE_NOTICE, 1, 2), "Dashlet is not added in the correct position ");

    }

    @TestRail(id = "C5557")
    @Test
    public void noticeDashletActions()
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
    }

    @TestRail(id = "C5558")
    @Test
    public void customizeDashlet()
    {
        LOG.info("Starting C5558");

        setup("C5558");

        String text = "test";

        siteNoticeDashlet.openConfigurePanel();
        Assert.assertTrue(siteNoticeDashlet.isConfigurePanelOpened(), "Site notice configure panel is not opened");

        siteNoticeDashlet.setSiteNoticeDashletTitle(text);
        siteNoticeDashlet.setSiteNoticeText(text);
        siteNoticeDashlet.clickOkButton();

        browser.waitUntilElementContainsText(browser.findElement(By.cssSelector("div.dashlet.notice-dashlet div.title")), text);
        Assert.assertEquals(siteNoticeDashlet.getDashletTitle(), text, "Site Notice title is not the same as the title that was set");
        Assert.assertEquals(siteNoticeDashlet.getSiteNoticeText(), text, "Site Notice text is not the same as the text that was set");
    }

    @TestRail(id = "C5559")
    @Test
    public void customizeDashletCancel()
    {

        LOG.info("Starting C5559");

        setup("C5559");

        String text = "test";

        siteNoticeDashlet.openConfigurePanel();
        Assert.assertTrue(siteNoticeDashlet.isConfigurePanelOpened(), "Site notice configure panel is not opened");

        siteNoticeDashlet.setSiteNoticeDashletTitle(text);
        siteNoticeDashlet.setSiteNoticeText(text);
        siteNoticeDashlet.clickCancelbutton();

        Assert.assertNotEquals(siteNoticeDashlet.getDashletTitle(), text, "Site Notice title is the same as the title that was set");
        Assert.assertNotEquals(siteNoticeDashlet.getSiteNoticeText(), text, "Site Notice text is the same as the text that was set");
    }
}