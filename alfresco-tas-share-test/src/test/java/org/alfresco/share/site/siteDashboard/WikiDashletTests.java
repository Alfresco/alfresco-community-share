package org.alfresco.share.site.siteDashboard;

import static org.alfresco.dataprep.DashboardCustomization.SiteDashlet;

import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.WikiDashlet;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.DataWiki;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.WikiModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WikiDashletTests extends AbstractSiteDashboardDashletsTests
{
    private static final String EXPECTED_TITLE = "siteWikiDashlet.title";
    private static final String EXPECTED_EMPTY_MESSAGE = "siteWikiDashlet.emptyMessage";
    private static final String EXPECTED_BALLOON_MESSAGE = "siteWikiDashlet.balloonMessage";
    private static final String EXPECTED_DIALOG_EMPTY_BODY_MESSAGE = "siteWikiDashlet.dialogEmptyBodyMessage";
    private static final String EXPECTED_DIALOG_TITLE = "siteWikiDashlet.dialogTitle";

    private static final String UNDERLINE_REPLACEMENT = "\\_";
    private static final String EMPTY_SPACE_REPLACEMENT = " ";
    private static final String DASH = " - ";

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private WikiDashlet wikiDashlet;
    private WikiMainPage wikiMainPage;
    private ThreadLocal<DataWiki> dataWiki = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        wikiDashlet = new WikiDashlet(webDriver);
        wikiMainPage = new WikiMainPage(webDriver);
        dataWiki.set(applicationContext.getBean(DataWiki.class));

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        addDashlet(user.get(), site.get(), SiteDashlet.WIKI, 1, 2);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C5428")
    @Test (groups = {TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void checkDisplaySpecificMessageWhenWikiDashletIsEmpty()
    {
        siteDashboardPage.navigate(site.get());
        wikiDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_TITLE))
            .assertWikiDashletEmptyMessageEquals(language.translate(EXPECTED_EMPTY_MESSAGE))
            .clickOnHelpIcon(DashletHelpIcon.WIKI)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();

        wikiDashlet.clickOnConfigureDashletIcon()
            .assertDialogBodyMessageEquals(language.translate(EXPECTED_DIALOG_EMPTY_BODY_MESSAGE)).clickClose();
    }

    @TestRail (id = "C588833")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void checkDisplayNoWikiPageWhenCancelDashletConfiguration()
    {
        WikiModel wikiModel = dataWiki.get().usingUser(user.get()).usingSite(site.get()).createRandomWiki();
        siteDashboardPage.navigate(site.get());
        wikiDashlet.clickOnConfigureDashletIcon()
            .assertDialogTitleEquals(language.translate(EXPECTED_DIALOG_TITLE))
            .clickDialogDropdown()
            .assertDropdownOptionEquals(wikiModel.getTitle())
            .clickCancelButton();

        wikiDashlet
            .assertWikiDashletMessageEquals(language.translate(EXPECTED_EMPTY_MESSAGE));
    }

    @TestRail (id = "C588834")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayCreatedWikiPageWhenSaveDashletConfiguration()
    {
        WikiModel wikiModel = dataWiki.get().usingUser(user.get()).usingSite(site.get()).createRandomWiki();
        siteDashboardPage.navigate(site.get());
        wikiDashlet.clickOnConfigureDashletIcon()
            .assertDialogTitleEquals(language.translate(EXPECTED_DIALOG_TITLE))
            .clickDialogDropdown()
            .assertDropdownOptionEquals(wikiModel.getTitle())
            .clickOk();

        wikiDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_TITLE).concat(DASH).concat(
                wikiModel.getTitle().replaceAll(UNDERLINE_REPLACEMENT, EMPTY_SPACE_REPLACEMENT)))
            .assertWikiDashletMessageEquals(wikiModel.getContent());
    }

    @TestRail (id = "C588835")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayCreatedWikiInWikiPageDetailsWhenAccessedFromDashletTitle()
    {
        WikiModel wikiModel = dataWiki.get().usingUser(user.get()).usingSite(site.get()).createRandomWiki();
        siteDashboardPage.navigate(site.get());
        wikiDashlet.clickOnConfigureDashletIcon()
            .clickDialogDropdown()
            .clickOk();

        wikiDashlet
            .clickDashletLinkTitle(
                wikiModel.getTitle().replaceAll(UNDERLINE_REPLACEMENT, EMPTY_SPACE_REPLACEMENT));

        wikiMainPage
            .assertWikiMainPageTitleEquals(
                wikiModel.getTitle().replaceAll(UNDERLINE_REPLACEMENT, EMPTY_SPACE_REPLACEMENT))
            .assertWikiPageContentEquals(wikiModel.getContent());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
