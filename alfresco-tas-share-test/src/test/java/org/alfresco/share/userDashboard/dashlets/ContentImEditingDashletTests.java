package org.alfresco.share.userDashboard.dashlets;

import static org.alfresco.share.TestUtils.FILE_CONTENT;
import static org.alfresco.dataprep.DashboardCustomization.UserDashlet;

import org.alfresco.po.share.dashlet.ContentImEditingDashlet;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.utility.model.*;
import org.testng.annotations.*;

public class ContentImEditingDashletTests extends AbstractUserDashboardDashletsTests
{
    private ContentImEditingDashlet contentImEditingDashlet;
    private DocumentLibraryPage2 documentLibraryPage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        contentImEditingDashlet = new ContentImEditingDashlet(webDriver);
        documentLibraryPage = new DocumentLibraryPage2(webDriver);

        user.set(dataUser.usingAdmin().createRandomTestUser());
        addDashlet(user.get(), UserDashlet.CONTENT_EDITING, 1, 3);
        authenticateUsingCookies(user.get());
    }

    @Test (groups = { TestGroup.REGRESSION, TestGroup.USER_DASHBOARD })
    public void checkContentImEditingTests()
    {
        userDashboardPage.navigate(user.get())
            .assertDashletIsAddedInPosition(Dashlets.CONTENT_I_AM_EDITING,1, 3);
        contentImEditingDashlet.assertDashletTitleEquals(language.translate("contentImEditingDashlet.title"))
            .clickHelpIcon()
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageEquals(language.translate("contentImEditingDashlet.HelpBalloonText"))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed()
            .assertAllHeadersAreDisplayed();
    }

    @Test (groups = { TestGroup.REGRESSION, TestGroup.USER_DASHBOARD })
    public void checkDocumentIsDisplayedWhenEdited()
    {
        SiteModel site = getDataSite().usingUser(user.get()).createPublicRandomSite();
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site).createFile(testFile)
                .usingResource(testFile).checkOut().assertThat().documentIsCheckedOut();

        userDashboardPage.navigate(user.get());
        contentImEditingDashlet.assertDocumentIsDisplayed(testFile)
            .clickDocument(testFile);
        documentLibraryPage.usingContent(testFile).assertContentIsDisplayed();

        userDashboardPage.navigate(user.get());
        contentImEditingDashlet.clickSite(testFile)
            .assertSiteDashboardPageIsOpened()
            .assertSiteHeaderTitleIs(site);

        dataSite.usingAdmin().deleteSite(site);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }
}
