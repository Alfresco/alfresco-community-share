package org.alfresco.share.site.siteDashboard;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.alfresco.dataprep.DashboardCustomization.SiteDashlet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;

import org.alfresco.dataprep.SitePagesService;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SiteLinksDashlet;
import org.alfresco.po.share.site.link.CreateLinkPage;
import org.alfresco.po.share.site.link.LinkDetailsViewPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SiteLinksDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_EMPTY_MESSAGE = "siteLinksDashlet.noLinks";
    private static final String EXPECTED_HELP_BALLOON_MESSAGE = "siteLinksDashlet.helpBalloonMessage";
    private static final String LINK_URL = "www.google.com";
    private static final String LINK_DESCRIPTION = "Search engine".concat(randomAlphabetic(3));
    private static final String LINK_TAG = "tag".concat(randomAlphabetic(3));
    private static final String LINK_NAME = "google";

    private final DateFormat dateFormat = new SimpleDateFormat("EE d MMM yyyy");

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private SiteLinksDashlet siteLinksDashlet;
    private CreateLinkPage createLinkPage;
    private LinkDetailsViewPage linkDetailsViewPage;

    @Autowired
    private SitePagesService sitePagesService;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteLinksDashlet = new SiteLinksDashlet(webDriver);
        linkDetailsViewPage = new LinkDetailsViewPage(webDriver);
        createLinkPage = new CreateLinkPage(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        addDashlet(user.get(), site.get(), SiteDashlet.SITE_LINKS, 1, 2);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C5525")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void checkSpecificMessageWhenSiteLinksDashletIsEmpty()
    {
        siteDashboardPage.navigate(site.get());
        siteLinksDashlet
            .assertDashletEmptyMessageEquals(language.translate(EXPECTED_EMPTY_MESSAGE))
            .clickOnHelpIcon(DashletHelpIcon.SITE_LINKS)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail (id = "C5534")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayCreatedLinkInSiteLinksDashlet()
    {
        siteDashboardPage.navigate(site.get());
        siteLinksDashlet.clickCreateLinkButton();
        createLinkPage
            .typeLinkTitle(LINK_NAME)
            .typeLinkUrl(LINK_URL)
            .typeLinkDescription(LINK_DESCRIPTION)
            .clickSaveButton();

        linkDetailsViewPage
            .assertLinkTitleEquals(LINK_NAME)
            .assertLinkUrlEquals(LINK_URL)
            .assertLinkDescriptionEquals(LINK_DESCRIPTION)
            .assertLinkCreationDateContains(dateFormat)
            .assertCreatedByLabelEqualsFullUserName(user.get().getFirstName(), user.get().getLastName());

        siteDashboardPage.navigate(site.get());
        siteLinksDashlet.assertDashletLinkNameEquals(LINK_NAME);
    }

    @TestRail (id = "C588528")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldOpenNewBrowserTabWhenNavigateToCreatedLink()
    {
        siteDashboardPage.navigate(site.get());
        sitePagesService
            .createLink(
                user.get().getUsername(),
                user.get().getPassword(),
                site.get().getTitle(),
                LINK_NAME, LINK_URL, LINK_DESCRIPTION,
                false, Collections.singletonList(LINK_TAG));

        siteDashboardPage.navigate(site.get());
        siteLinksDashlet
            .clickLinkByName(LINK_NAME)
            .assertUrlContains(LINK_NAME);
    }

    @TestRail (id = "C5804")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayLinkDetailsInLinkDetailsViewPageWhenDetailsButtonIsClicked()
    {
        siteDashboardPage.navigate(site.get());
        sitePagesService
            .createLink(
                user.get().getUsername(),
                user.get().getPassword(),
                site.get().getTitle(),
                LINK_NAME, LINK_URL, LINK_DESCRIPTION,
                false, Collections.singletonList(LINK_TAG));

        siteDashboardPage.navigate(site.get());
        siteLinksDashlet.clickLinkDetailsButton(LINK_NAME);
        linkDetailsViewPage
            .assertLinkTitleEquals(LINK_NAME)
            .assertLinkUrlEquals(LINK_URL)
            .assertLinkDescriptionEquals(LINK_DESCRIPTION)
            .assertLinkCreationDateContains(dateFormat)
            .assertCreatedByLabelEqualsFullUserName(user.get().getFirstName(), user.get().getLastName());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
