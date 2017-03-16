package org.alfresco.share.site.siteDashboard;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SiteLinksDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.link.CreateLinkPage;
import org.alfresco.po.share.site.link.LinkDetailsViewPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.testng.Assert.*;

/**
 * Created by Claudia Agache on 7/22/2016.
 */
public class SiteLinksDashletTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboard;

    @Autowired
    SiteLinksDashlet siteLinksDashlet;

    @Autowired
    CreateLinkPage createLinkPage;

    @Autowired
    LinkDetailsViewPage linkDetailsViewPage;

    private String user = "user-" + DataUtil.getUniqueIdentifier();
    private String siteName;
    private String linkTitle = "google";
    DateFormat df = new SimpleDateFormat("EE d MMM yyyy");

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C5525")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void siteLinksDashletHelpNotification()
    {
        siteName = "SiteName-C5525-" + DataUtil.getUniqueIdentifier();
        siteService.create(user, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addDashlet(user, password, siteName, DashboardCustomization.SiteDashlet.SITE_LINKS, DashboardCustomization.DashletLayout.THREE_COLUMNS, 3, 1);

        LOG.info("STEP 1: Navigate to " + siteName + " dashboard and check the 'Site Links' dashlet.");
        siteDashboard.navigate(siteName);
        assertEquals(siteLinksDashlet.getDashletMessage(), "No links to display", "The text: 'No links to display' is displayed.");
        assertTrue(siteLinksDashlet.isCreateLinkDisplayed(), "'Create Link' button is displayed.");

        LOG.info("STEP 2: Hover mouse over 'Site Links' dashlet");
        assertTrue(siteLinksDashlet.isHelpIconDisplayed(DashletHelpIcon.SITE_LINKS), "Help icon is displayed");

        LOG.info("STEP 3: Click on the '?' icon");
        siteLinksDashlet.clickOnHelpIcon(DashletHelpIcon.SITE_LINKS);
        assertTrue(siteLinksDashlet.isBalloonDisplayed(), "Help balloon is displayed");
        assertEquals(siteLinksDashlet.getHelpBalloonMessage(), language.translate("siteLinksDashlet.helpBalloonMessage"), "Help balloon text is correct.");

        logger.info("Step 3: Click 'X' icon on balloon popup");
        siteLinksDashlet.closeHelpBalloon();
        assertFalse(siteLinksDashlet.isBalloonDisplayed(), "Help balloon isn't displayed");
    }

    @TestRail(id = "C5534")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void siteLinksDashletCreateLink()
    {
        siteName = "SiteName-C5534-" + DataUtil.getUniqueIdentifier();
        siteService.create(user, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addDashlet(user, password, siteName, DashboardCustomization.SiteDashlet.SITE_LINKS, DashboardCustomization.DashletLayout.THREE_COLUMNS, 3, 1);
        siteDashboard.navigate(siteName);

        LOG.info("STEP 1: Click on 'Create Link' button.");
        siteLinksDashlet.clickCreateLink();
        assertTrue(createLinkPage.isLinkTitleDisplayed() && createLinkPage.isLinkTitleMandatory(), "'Title' field is displayed and mandatory.");
        assertTrue(createLinkPage.isLinkURLDisplayed() && createLinkPage.isLinkURLMandatory(), "'URL' field is displayed and mandatory.");
        assertTrue(createLinkPage.isLinkDescriptionDisplayed(), "'Description' field is displayed.");
        assertTrue(createLinkPage.isLinkInternalDisplayed(), "'Internal' checkbox is displayed.");
        assertTrue(createLinkPage.isLinkTagDisplayed() && createLinkPage.isAddTagButtonDisplayed(), "'Tags' field  with 'Add' button is displayed.");
        assertTrue(createLinkPage.isSaveButtonDisplayed(), "'Save' button is displayed.");
        assertTrue(createLinkPage.isCancelButtonDisplayed(), "'Cancel' button is displayed.");

        LOG.info("STEP 2: Fill in the fields and click 'Save'.");
        createLinkPage.typeLinkTitle(linkTitle);
        createLinkPage.typeLinkURL("www.google.com");
        createLinkPage.typeLinkDescription("Search engine");
        createLinkPage.addTag("tag1");
        createLinkPage.clickSaveButton();
        assertEquals(linkDetailsViewPage.getLinkTitle(), linkTitle, "Link title is correct.");
        assertEquals(linkDetailsViewPage.getLinkURL(), "www.google.com", "Link URL is correct.");
        assertEquals(linkDetailsViewPage.getDescription(), "Search engine", "Link description is correct.");
        assertTrue(linkDetailsViewPage.getCreationDate().contains(df.format(new Date())), "Date of creation is today.");
        assertEquals(linkDetailsViewPage.getCreatedBy(), user + " " + user, "User that created the link is current user.");

        LOG.info("STEP 3: Return to site dashboard and check the site links dashlet.");
        siteDashboard.navigate(siteName);
        assertTrue(siteLinksDashlet.isLinkPresentInList(linkTitle), "Link title is displayed in dashlet.");
        assertTrue(siteLinksDashlet.hasLinkDetailsButton(linkTitle));
    }

    @TestRail(id = "C5804")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void siteLinksDashletDetailsButton()
    {
        siteName = "SiteName-C5804-" + DataUtil.getUniqueIdentifier();
        siteService.create(user, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addDashlet(user, password, siteName, DashboardCustomization.SiteDashlet.SITE_LINKS, DashboardCustomization.DashletLayout.THREE_COLUMNS, 3, 1);
        sitePagesService.createLink(user, password, siteName, linkTitle, "www.google.com", "Search engine", false, Arrays.asList("tag1"));
        siteDashboard.navigate(siteName);

        LOG.info("STEP 1: Check the 'Site Links' dashlet.");
        assertTrue(siteLinksDashlet.isLinkPresentInList(linkTitle), "Link title is displayed in dashlet.");

        LOG.info("STEP 2: Click on the details button next to link 'Title'");
        siteLinksDashlet.clickLinkDetailsButton(linkTitle);
        assertEquals(linkDetailsViewPage.getLinkTitle(), linkTitle, "Link title is correct.");
        assertEquals(linkDetailsViewPage.getLinkURL(), "www.google.com", "Link URL is correct.");
        assertEquals(linkDetailsViewPage.getDescription(), "Search engine", "Link description is correct.");
        assertTrue(linkDetailsViewPage.getCreationDate().contains(df.format(new Date())), "Date of creation is today.");
        assertEquals(linkDetailsViewPage.getCreatedBy(), user + " " + user, "User that created the link is current user.");
        assertTrue(linkDetailsViewPage.isTagDisplayedInTagsList("tag1"), "Tag1 is displayed.");
    }
}
