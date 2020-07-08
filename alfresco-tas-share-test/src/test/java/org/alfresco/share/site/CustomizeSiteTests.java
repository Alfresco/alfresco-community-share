package org.alfresco.share.site;

import org.alfresco.po.share.Theme;
import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author bogdan.bocancea
 */
public class CustomizeSiteTests extends ContextAwareWebTest
{
    @Autowired
    private CustomizeSitePage customizeSite;

    @Autowired
    private SiteDashboardPage siteDashboard;

    @Autowired
    private SiteContentDashlet siteContentDashlet;

    private UserModel user;

    @BeforeClass(alwaysRun = true)
    public void setup()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(user);
    }

    @AfterClass(alwaysRun = true)
    public void removeUser()
    {
        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C2135")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void changeSiteTheme()
    {
        SiteModel site2135 = dataSite.usingUser(user).createPublicRandomSite();

        LOG.info("Step 4 - Click 'Customize site'.");
        customizeSite.navigate(site2135);

        LOG.info("Step 5 - Click 'Set Application Theme' drop-down and verify available options.");
        Assert.assertFalse(customizeSite.getThemeOptions().isEmpty());

        LOG.info("Step 6, 7 - Select 'Green Theme' and click 'OK' button. / Repeat steps 4-6 for all the other themes.");
        for (Theme theme : Theme.values())
        {
            customizeSite.navigate(site2135);
            customizeSite.selectTheme(theme);
            customizeSite.clickOk();
            siteDashboard.renderedPage();
            if (theme.equals(Theme.APPLICATION_SET))
            {
                continue;
            }
            else
            {
                Assert.assertTrue(siteContentDashlet.getDashletColor().equals(theme.dashletHexColor), theme.name + " is not set");
            }
        }
        dataSite.usingAdmin().deleteSite(site2135);
    }

    @TestRail (id = "C2148")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelChangeSiteTheme()
    {
        SiteModel site2148 = dataSite.usingUser(user).createPublicRandomSite();

        LOG.info("Step 3 - Navigate to the newly created site and click on the 'Settings' icon -> 'Customize site'.");
        customizeSite.navigate(site2148);

        LOG.info("Step 4 - Select any theme except the default one");
        customizeSite.selectTheme(Theme.GREEN);

        LOG.info("Step 5 - Click 'Cancel' button.");
        customizeSite.clickCancel();
        siteDashboard.renderedPage();
        Assert.assertFalse(siteContentDashlet.getDashletColor().equals(Theme.GREEN.dashletHexColor));

        dataSite.usingAdmin().deleteSite(site2148);
    }

    @TestRail (id = "C2156")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyDefaultCurrentAndAvailablePages()
    {
        SiteModel site2156 = dataSite.usingUser(user).createPublicRandomSite();

        LOG.info("Step 3 - Navigate to the newly created site and click on the 'Settings' icon -> 'Customize site'.");
        customizeSite.navigate(site2156);

        LOG.info("Step 4 - Verify 'Current Site Pages' section.");
        Assert.assertTrue(customizeSite.getCurrentPages().get(0).equals(SitePageType.DOCUMENT_LIBRARY), "Document Library is missing from Current Site Pages");
        Assert.assertTrue(customizeSite.isRenameDisplayed(SitePageType.DOCUMENT_LIBRARY), "Rename action is missing from Document Library page");
        Assert.assertTrue(customizeSite.isRemoveDisplayed(SitePageType.DOCUMENT_LIBRARY), "Remove action is missing from Document Library page");

        LOG.info("Step 5 - Verify 'Available Site Pages' section.");
        List<SitePageType> availablePages = customizeSite.getAvailablePages();
        List<SitePageType> pages = new ArrayList<>();
        for (SitePageType page : SitePageType.values())
        {
            if (page.equals(SitePageType.DOCUMENT_LIBRARY))
            {
                continue;
            }
            else
            {
                pages.add(page);
            }
        }
        Collections.sort(availablePages);
        Collections.sort(pages);
        Assert.assertTrue(availablePages.equals(pages));

        dataSite.usingAdmin().deleteSite(site2156);
    }

    @TestRail (id = "C2164")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addAvailablePagesToSite()
    {
        SiteModel site2164 = dataSite.usingUser(user).createPublicRandomSite();

        LOG.info("Step 3 - Navigate to the newly created site and click on the 'Settings' icon -> 'Customize site'.");
        customizeSite.navigate(site2164);

        LOG.info("Step 4 - Drag any page from the 'Available Site Pages' section and drop it to the 'Current Site Pages' section.");
        customizeSite.addPageToSite(SitePageType.WIKI);
        customizeSite.addPageToSite(SitePageType.CALENDER);
        customizeSite.addPageToSite(SitePageType.DATA_LISTS);
        customizeSite.addPageToSite(SitePageType.DISCUSSIONS);
        customizeSite.addPageToSite(SitePageType.LINKS);
        customizeSite.addPageToSite(SitePageType.BLOG);

        Assert.assertTrue(customizeSite.isPageAddedToCurrentPages(SitePageType.WIKI), SitePageType.WIKI.getDisplayText() + " is not added");
        Assert.assertTrue(customizeSite.isPageAddedToCurrentPages(SitePageType.CALENDER), SitePageType.CALENDER.getDisplayText() + " is not added");
        Assert.assertTrue(customizeSite.isPageAddedToCurrentPages(SitePageType.DATA_LISTS), SitePageType.DATA_LISTS.getDisplayText() + " is not added");
        Assert.assertTrue(customizeSite.isPageAddedToCurrentPages(SitePageType.DISCUSSIONS), SitePageType.DISCUSSIONS.getDisplayText() + " is not added");
        Assert.assertTrue(customizeSite.isPageAddedToCurrentPages(SitePageType.LINKS), SitePageType.LINKS.getDisplayText() + " is not added");
        Assert.assertTrue(customizeSite.isPageAddedToCurrentPages(SitePageType.BLOG), SitePageType.BLOG.getDisplayText() + " is not added");

        LOG.info("Step 5 - Click 'Ok' button.");
        customizeSite.clickOk();
        siteDashboard.renderedPage();
        Assert.assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.WIKI), SitePageType.WIKI.getDisplayText() + " is not added to site dashboard");
        Assert.assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.CALENDER), SitePageType.CALENDER.getDisplayText()
            + " is not added to site dashboard");
        Assert.assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.DATA_LISTS), SitePageType.DATA_LISTS.getDisplayText()
            + " is not added to site dashboard");
        Assert.assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.DISCUSSIONS), SitePageType.DISCUSSIONS.getDisplayText()
            + " is not added to site dashboard");
        Assert.assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.LINKS), SitePageType.LINKS.getDisplayText() + " is not added to site dashboard");
        Assert.assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.BLOG), SitePageType.BLOG.getDisplayText() + " is not added to site dashboard");

        dataSite.usingAdmin().deleteSite(site2164);
    }

    @TestRail (id = "C2171")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void removePageFromSite()
    {
        SiteModel site2171 = dataSite.usingUser(user).createPublicRandomSite();

        LOG.info("Step 2 - Create any site and add several site pages to it.");
        customizeSite.navigate(site2171);
        customizeSite.addPageToSite(SitePageType.WIKI);
        customizeSite.addPageToSite(SitePageType.CALENDER);
        Assert.assertTrue(customizeSite.isPageAddedToCurrentPages(SitePageType.WIKI), SitePageType.WIKI.getDisplayText() + " is not added");
        Assert.assertTrue(customizeSite.isPageAddedToCurrentPages(SitePageType.CALENDER), SitePageType.CALENDER.getDisplayText() + " is not added");
        customizeSite.clickOk();
        siteDashboard.renderedPage();
        Assert.assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.WIKI), SitePageType.WIKI.getDisplayText() + " is not added to site dashboard");
        Assert.assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.CALENDER), SitePageType.CALENDER.getDisplayText()
            + " is not added to site dashboard");

        LOG.info("Step 3 - Navigate to 'Customize site' page");
        customizeSite.navigate(site2171);

        LOG.info("Step 4 - Click 'Remove' button under any page available.");
        customizeSite.removePage(SitePageType.WIKI);
        customizeSite.removePage(SitePageType.CALENDER);
        Assert.assertFalse(customizeSite.isPageAddedToCurrentPages(SitePageType.WIKI), SitePageType.WIKI.getDisplayText() + " is not removed");
        Assert.assertFalse(customizeSite.isPageAddedToCurrentPages(SitePageType.CALENDER), SitePageType.CALENDER.getDisplayText() + " is not removed");

        LOG.info("Step 5 - Click 'Ok' button.");
        customizeSite.clickOk();
        siteDashboard.renderedPage();
        Assert.assertFalse(siteDashboard.isPageAddedToDashboard(SitePageType.WIKI), SitePageType.WIKI.getDisplayText() + " is not removed from site dashboard");
        Assert.assertFalse(siteDashboard.isPageAddedToDashboard(SitePageType.CALENDER), SitePageType.CALENDER.getDisplayText()
            + " is not removed from site dashboard");

        dataSite.usingAdmin().deleteSite(site2171);
    }

    @TestRail (id = "C2173")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void renamePage()
    {
        SiteModel site2173 = dataSite.usingUser(user).createPublicRandomSite();

        LOG.info("Step 2 - Create any site and add several site pages to it.");
        customizeSite.navigate(site2173);
        customizeSite.addPageToSite(SitePageType.WIKI);

        LOG.info("Step 4 / 5 / 6 - Click 'Rename' button under any page / Type any name in the 'Display Name' text field / Click OK");
        customizeSite.renamePage(SitePageType.WIKI, "Wiki-Edit");
        Assert.assertTrue(customizeSite.getPageDisplayName(SitePageType.WIKI).equals("Wiki-Edit"), "Wiki display page name is not modified");

        LOG.info("Step 7 - Click OK");
        customizeSite.clickOk();
        siteDashboard.renderedPage();
        Assert.assertTrue(siteDashboard.getPageDisplayName(SitePageType.WIKI).equals("Wiki-Edit"), "Wiki display page name is not modified");

        dataSite.usingAdmin().deleteSite(site2173);
    }
}
