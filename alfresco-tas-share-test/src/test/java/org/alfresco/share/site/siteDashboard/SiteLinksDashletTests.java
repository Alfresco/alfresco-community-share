package org.alfresco.share.site.siteDashboard;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteLinksDashlet;
import org.alfresco.po.share.site.link.CreateLinkPage;
import org.alfresco.po.share.site.link.LinkDetailsViewPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Claudia Agache on 7/22/2016.
 */
public class SiteLinksDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_EMPTY_MESSAGE = "siteLinksDashlet.noLinks";
    private static final String EXPECTED_HELP_BALLOON_MESSAGE = "siteLinksDashlet.helpBalloonMessage";
    private static final String LINK_URL = "www.google.com";
    private static final String LINK_DESCRIPTION = "Search engine".concat(randomAlphabetic(3));
    private static final String LINK_TAG = "tag".concat(randomAlphabetic(3));
    private static final String LINK_NAME = "google";

    private final DateFormat dateFormat = new SimpleDateFormat("EE d MMM yyyy");

    private UserModel userModel;
    private SiteModel siteModel;

    @Autowired
    private SiteLinksDashlet siteLinksDashlet;

    //@Autowired
    private CreateLinkPage createLinkPage;

   // @Autowired
    private LinkDetailsViewPage linkDetailsViewPage;

    @Autowired
    private SitePagesService sitePagesService;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();

        setupAuthenticatedSession(userModel);
        addDashlet(siteModel, Dashlets.SITE_LINKS, 1);
    }

    @TestRail (id = "C5525")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void checkSpecificMessageWhenSiteLinksDashletIsEmpty()
    {
        siteDashboardPage.navigate(siteModel);
        siteLinksDashlet
            .assertDashletEmptyMessageEquals(language.translate(EXPECTED_EMPTY_MESSAGE))
            .clickOnHelpIcon(DashletHelpIcon.SITE_LINKS)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE));

        siteLinksDashlet
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail (id = "C5534")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayCreatedLinkInSiteLinksDashlet()
    {
        siteDashboardPage.navigate(siteModel);
        siteLinksDashlet.clickCreateLinkButton();

        createLinkPage
            .typeLinkTitle(LINK_NAME)
            .typeLinkUrl(LINK_URL)
            .typeLinkDescription(LINK_DESCRIPTION)
            .addTag(LINK_TAG)
            .clickSaveButton();

        linkDetailsViewPage
            .assertLinkTitleEquals(LINK_NAME)
            .assertLinkUrlEquals(LINK_URL)
            .assertLinkDescriptionEquals(LINK_DESCRIPTION)
            .assertLinkCreationDateContains(dateFormat)
            .assertCreatedByLabelEqualsFullUserName(userModel.getFirstName(), userModel.getLastName());

        siteDashboardPage.navigate(siteModel);
        siteLinksDashlet.assertDashletLinkNameEquals(LINK_NAME);
    }

    @TestRail (id = "C588528")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldOpenNewBrowserTabWhenNavigateToCreatedLink()
    {
        siteDashboardPage.navigate(siteModel);
        sitePagesService
            .createLink(
                userModel.getUsername(),
                userModel.getPassword(),
                siteModel.getTitle(),
                LINK_NAME, LINK_URL, LINK_DESCRIPTION,
                false, Collections.singletonList(LINK_TAG));

        siteDashboardPage.navigate(siteModel);
        siteLinksDashlet
            .clickLinkByName(LINK_NAME)
            .assertUrlContains(LINK_NAME);
    }

    @TestRail (id = "C5804")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayLinkDetailsInLinkDetailsViewPageWhenDetailsButtonIsClicked()
    {
        siteDashboardPage.navigate(siteModel);
        sitePagesService
            .createLink(
                userModel.getUsername(),
                userModel.getPassword(),
                siteModel.getTitle(),
                LINK_NAME, LINK_URL, LINK_DESCRIPTION,
                false, Collections.singletonList(LINK_TAG));

        siteDashboardPage.navigate(siteModel);
        siteLinksDashlet.clickLinkDetailsButton(LINK_NAME);
        linkDetailsViewPage
            .assertLinkTitleEquals(LINK_NAME)
            .assertLinkUrlEquals(LINK_URL)
            .assertLinkDescriptionEquals(LINK_DESCRIPTION)
            .assertLinkCreationDateContains(dateFormat)
            .assertCreatedByLabelEqualsFullUserName(userModel.getFirstName(), userModel.getLastName());
    }

    @AfterClass (alwaysRun = true)
    public void cleanupTest()
    {
        removeUserFromAlfresco(userModel);
        deleteSites(siteModel);
    }
}
