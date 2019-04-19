package org.alfresco.share.site;

import org.alfresco.po.share.Theme;
import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
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
    CustomizeSitePage customizeSite;
    @Autowired
    SiteDashboardPage siteDashboard;

    @Autowired
    SiteContentDashlet siteContentDashlet;

    @TestRail(id = "C2135")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void changeSiteTheme()
    {
        String userName = "user2135-" + RandomData.getRandomAlphanumeric() + domain;
        String siteName = String.format("C2135%s", RandomData.getRandomAlphanumeric());
        LOG.info("Step 1 / Step 2: Login to Share as any user. / Create any site");
        userService.create(adminUser, adminPassword, userName, password, userName, "C2135", "lname");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("Step 4 - Click 'Customize site'.");
        customizeSite.navigate(siteName);

        LOG.info("Step 5 - Click 'Set Application Theme' drop-down and verify available options.");
        Assert.assertFalse(customizeSite.getThemeOptions().isEmpty());

        LOG.info("Step 6, 7 - Select 'Green Theme' and click 'OK' button. / Repeat steps 4-6 for all the other themes.");
        for (Theme theme : Theme.values())
        {
            customizeSite.navigate(siteName);
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
    }

    @TestRail(id = "C2148")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelChangeSiteTheme()
    {
        String userName = "userC2148-" + RandomData.getRandomAlphanumeric() + domain;
        String siteName = String.format("C2148%s", RandomData.getRandomAlphanumeric());
        LOG.info("Step 1 / Step 2: Login to Share as any user. / Create any site");
        userService.create(adminUser, adminPassword, userName, password, userName, "C2148", "lname");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("Step 3 - Navigate to the newly created site and click on the 'Settings' icon -> 'Customize site'.");
        customizeSite.navigate(siteName);

        LOG.info("Step 4 - Select any theme except the default one");
        customizeSite.selectTheme(Theme.GREEN);

        LOG.info("Step 5 - Click 'Cancel' button.");
        customizeSite.clickCancel();
        siteDashboard.renderedPage();
        Assert.assertFalse(siteContentDashlet.getDashletColor().equals(Theme.GREEN.dashletHexColor));
    }

    @TestRail(id = "C2156")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyDefaultCurrentAndAvailablePages()
    {
        String userName = "user2156-" + RandomData.getRandomAlphanumeric() + domain;
        String siteName = String.format("C2156%s", RandomData.getRandomAlphanumeric());
        LOG.info("Step 1 / Step 2: Login to Share as any user. / Create any site");
        userService.create(adminUser, adminPassword, userName, password, userName, "C2156", "lname");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("Step 3 - Navigate to the newly created site and click on the 'Settings' icon -> 'Customize site'.");
        customizeSite.navigate(siteName);

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
    }

    @TestRail(id = "C2164")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addAvailablePagesToSite()
    {
        String userName = "userC2164-" + RandomData.getRandomAlphanumeric() + domain;
        String siteName = String.format("C2164%s", RandomData.getRandomAlphanumeric());
        LOG.info("Step 1 / Step 2: Login to Share as any user. / Create any site");
        userService.create(adminUser, adminPassword, userName, password, userName, "C2164", "lname");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("Step 3 - Navigate to the newly created site and click on the 'Settings' icon -> 'Customize site'.");
        customizeSite.navigate(siteName);

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
        Assert.assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.WIKI), SitePageType.WIKI.getDisplayText() + " is not added to site dashboard");
        Assert.assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.CALENDER), SitePageType.CALENDER.getDisplayText()
                + " is not added to site dashboard");
        Assert.assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.DATA_LISTS), SitePageType.DATA_LISTS.getDisplayText()
                + " is not added to site dashboard");
        Assert.assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.DISCUSSIONS), SitePageType.DISCUSSIONS.getDisplayText()
                + " is not added to site dashboard");
        Assert.assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.LINKS), SitePageType.LINKS.getDisplayText() + " is not added to site dashboard");
        Assert.assertTrue(siteDashboard.isPageAddedToDashboard(SitePageType.BLOG), SitePageType.BLOG.getDisplayText() + " is not added to site dashboard");
    }

    @TestRail(id = "C2171")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void removePageFromSite()
    {
        String userName = "userC2171-" + RandomData.getRandomAlphanumeric() + domain;
        String siteName = String.format("C2171%s", RandomData.getRandomAlphanumeric());
        LOG.info("Step 1: Login to Share as any user.");
        userService.create(adminUser, adminPassword, userName, password, userName, "C2171", "lname");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("Step 2 - Create any site and add several site pages to it.");
        customizeSite.navigate(siteName);
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
        customizeSite.navigate(siteName);

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
    }

    @TestRail(id = "C2173")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void renamePage()
    {
        String userName = "userC2173-" + RandomData.getRandomAlphanumeric() + domain;
        String siteName = String.format("C2173%s", RandomData.getRandomAlphanumeric());
        LOG.info("Step 1: Login to Share as any user.");
        userService.create(adminUser, adminPassword, userName, password, userName, "C2173", "lname");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("Step 2 - Create any site and add several site pages to it.");
        customizeSite.navigate(siteName);
        customizeSite.addPageToSite(SitePageType.WIKI);

        LOG.info("Step 4 / 5 / 6 - Click 'Rename' button under any page / Type any name in the 'Display Name' text field / Click OK");
        customizeSite.renamePage(SitePageType.WIKI, "Wiki-Edit");
        Assert.assertTrue(customizeSite.getPageDisplayName(SitePageType.WIKI).equals("Wiki-Edit"), "Wiki display page name is not modified");

        LOG.info("Step 7 - Click OK");
        customizeSite.clickOk();
        siteDashboard.renderedPage();
        Assert.assertTrue(siteDashboard.getPageDisplayName(SitePageType.WIKI).equals("Wiki-Edit"), "Wiki display page name is not modified");
    }
}
