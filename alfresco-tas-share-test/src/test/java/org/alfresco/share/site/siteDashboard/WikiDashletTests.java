package org.alfresco.share.site.siteDashboard;

import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SelectWikiPagePopUp;
import org.alfresco.po.share.dashlet.WikiDashlet;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.DataWiki;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.WikiModel;
import org.springframework.beans.factory.annotation.Autowired;
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

    private UserModel userModel;
    private SiteModel siteModel;
    private WikiModel wikiModel;

    @Autowired
    private WikiDashlet wikiDashlet;

    @Autowired
    private SelectWikiPagePopUp selectWikiPage;

    //@Autowired
    private WikiMainPage wikiMainPage;

    @Autowired
    private DataWiki dataWiki;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        siteModel = dataSite.usingUser(userModel).createPublicRandomSite();

        setupAuthenticatedSession(userModel);
        addDashlet(siteModel, Dashlets.WIKI, 1);
    }

    @TestRail (id = "C5428")
    @Test (groups = {TestGroup.SANITY, TestGroup.SITES})
    public void checkDisplaySpecificMessageWhenWikiDashletIsEmpty()
    {
        siteDashboardPage.navigate(siteModel);
        wikiDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_TITLE))
            .assertWikiDashletEmptyMessageEquals(language.translate(EXPECTED_EMPTY_MESSAGE))
            .clickOnHelpIcon(DashletHelpIcon.WIKI)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();

        wikiDashlet.clickOnConfigureDashletIcon();
        selectWikiPage
            .assertDialogBodyMessageEquals(language.translate(EXPECTED_DIALOG_EMPTY_BODY_MESSAGE));
        selectWikiPage.clickClose();
    }

    @TestRail (id = "C588833")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void checkDisplayNoWikiPageWhenCancelDashletConfiguration()
    {
        wikiModel = dataWiki.usingSite(siteModel).createRandomWiki();
        siteDashboardPage.navigate(siteModel);
        wikiDashlet.clickOnConfigureDashletIcon();

        selectWikiPage
            .assertDialogTitleEquals(language.translate(EXPECTED_DIALOG_TITLE))
            .clickDialogDropdown()
            .assertDropdownOptionEquals(wikiModel.getTitle())
            .clickCancelButton();

            wikiDashlet
                .assertWikiDashletMessageEquals(language.translate(EXPECTED_EMPTY_MESSAGE));
    }

    @TestRail (id = "C588834")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayCreatedWikiPageWhenSaveDashletConfiguration()
    {
        wikiModel = dataWiki.usingSite(siteModel).createRandomWiki();
        siteDashboardPage.navigate(siteModel);
        wikiDashlet.clickOnConfigureDashletIcon();

        selectWikiPage
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
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES})
    public void shouldDisplayCreatedWikiInWikiPageDetailsWhenAccessedFromDashletTitle()
    {
        wikiModel = dataWiki.usingSite(siteModel).createRandomWiki();
        siteDashboardPage.navigate(siteModel);
        wikiDashlet.clickOnConfigureDashletIcon();

        selectWikiPage
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
        removeUserFromAlfresco(userModel);
        deleteSites(siteModel);
    }
}
