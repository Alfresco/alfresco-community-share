package org.alfresco.share.site;

import static org.alfresco.dataprep.SiteService.Visibility.PUBLIC;
import static org.alfresco.dataprep.SiteService.Visibility.MODERATED;
import static org.alfresco.dataprep.SiteService.Visibility.PRIVATE;

import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.site.CreateSiteDialog;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreateSiteTests extends BaseTest
{
    private final String SITE_ID_ERROR_MESSAGE = "createSiteDialog.siteId.availableError";
    private final String SITE_NAME_WARNING_MESSAGE = "createSiteDialog.siteName.warning";

    private CreateSiteDialog createSiteDialog;
    private MySitesDashlet mySitesDashlet;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        createSiteDialog = new CreateSiteDialog(webDriver);
        mySitesDashlet = new MySitesDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2103, C2104")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void createPublicSiteFromToolbar()
    {
        String description = RandomStringUtils.randomAlphanumeric(15);
        String title =  RandomStringUtils.randomAlphanumeric(7);
        site.set(new SiteModel(title, PUBLIC));
        site.get().setDescription(description);

        userDashboardPage.navigate(user.get());
        createSiteDialog.navigateByMenuBar()
            .typeInNameInput(title)
            .assertSiteIdValueEquals(title)
            .typeInDescription(description)
            .assertPublicVisibilityIsSelected()
            .clickCreateButton()
                .assertSiteDashboardPageIsOpened()
                .assertSiteHeaderTitleIs(site.get())
                .assertSiteVisibilityEqualsTo(PUBLIC);

        userDashboardPage.navigate(user.get());
        mySitesDashlet.assertSiteDescriptionEqualsTo(site.get(), description);
    }

    @TestRail (id = "C43380")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void createSiteWithoutDescription()
    {
        site.set(SiteModel.getRandomSiteModel());

        userDashboardPage.navigate(user.get());
        createSiteDialog.navigateByMenuBar()
            .typeInNameInput(site.get().getTitle())
            .assertSiteIdValueEquals(site.get().getTitle())
            .assertPublicVisibilityIsSelected()
            .clickCreateButton()
            .assertSiteDashboardPageIsOpened()
            .assertSiteHeaderTitleIs(site.get())
            .assertSiteVisibilityEqualsTo(PUBLIC);
    }

    @TestRail (id = "C2105")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void createModeratedSiteFromToolbar()
    {
        site.set(new SiteModel(RandomStringUtils.randomAlphanumeric(7), MODERATED));

        userDashboardPage.navigate(user.get());
        createSiteDialog.navigateByMenuBar()
            .typeInNameInput(site.get().getTitle())
            .assertSiteIdValueEquals(site.get().getTitle())
            .selectModeratedVisibility()
            .clickCreateButton()
            .assertSiteDashboardPageIsOpened()
            .assertSiteHeaderTitleIs(site.get())
            .assertSiteVisibilityEqualsTo(MODERATED);
    }

    @TestRail (id = "C2106")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void createPrivateSiteFromToolbar()
    {
        site.set(new SiteModel(RandomStringUtils.randomAlphanumeric(7), PRIVATE));

        userDashboardPage.navigate(user.get());
        createSiteDialog.navigateByMenuBar()
            .typeInNameInput(site.get().getTitle())
            .assertSiteIdValueEquals(site.get().getTitle())
            .selectPrivateVisibility()
            .clickCreateButton()
            .assertSiteDashboardPageIsOpened()
            .assertSiteHeaderTitleIs(site.get())
            .assertSiteVisibilityEqualsTo(PRIVATE);
    }

    @TestRail (id = "C2107, C2108, C2109")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void createSiteFromMySitesDashlet()
    {
        site.set(SiteModel.getRandomSiteModel());

        userDashboardPage.navigate(user.get());
        mySitesDashlet.clickCreateSiteButton();

        createSiteDialog
            .typeInNameInput(site.get().getTitle())
            .assertSiteIdValueEquals(site.get().getTitle())
            .clickCreateButton()
            .assertSiteDashboardPageIsOpened()
            .assertSiteHeaderTitleIs(site.get())
            .assertSiteVisibilityEqualsTo(PUBLIC);
    }

    @TestRail (id = "C2124")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelCreatingSite()
    {
        userDashboardPage.navigate(user.get());
        createSiteDialog.navigateByMenuBar()
            .typeInNameInput(RandomStringUtils.randomAlphanumeric(7))
            .assertPublicVisibilityIsSelected()
            .clickCancelButton();
        userDashboardPage.assertUserDashboardPageIsOpened();
    }

    @TestRail (id = "C2125")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyCloseButton()
    {
        userDashboardPage.navigate(user.get());
        createSiteDialog.navigateByMenuBar()
            .typeInNameInput(RandomStringUtils.randomAlphanumeric(7))
            .assertPublicVisibilityIsSelected()
            .clickCloseXButton();
        userDashboardPage.assertUserDashboardPageIsOpened();
    }

    @TestRail (id = "C2130")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void shouldDisplayErrorWhenSiteIdIsInUse()
    {
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        new SiteFinderPage(webDriver).navigate()
            .searchSiteWithName(site.get().getId());

        createSiteDialog.navigateByMenuBar()
            .typeInNameInput(site.get().getTitle())
            .typeInSiteID(site.get().getId().toLowerCase())
            .assertSiteIdErrorMessageEqualsWithExpected(language.translate(SITE_ID_ERROR_MESSAGE));

    }

    @TestRail (id = "C14004")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void shouldDisplayWarningWhenSiteNameIsAlreadyInUse()
    {
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        new SiteFinderPage(webDriver).navigate()
            .searchSiteWithName(site.get().getId());

        createSiteDialog.navigateByMenuBar()
            .typeInNameInput(site.get().getTitle())
            .assertSiteNameInUseWarningMessageEquals(language.translate(SITE_NAME_WARNING_MESSAGE));
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}