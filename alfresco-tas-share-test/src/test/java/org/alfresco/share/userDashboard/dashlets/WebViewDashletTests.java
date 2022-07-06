package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.WebViewDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WebViewDashletTests extends AbstractUserDashboardDashletsTests
{
    private WebViewDashlet webViewDashlet;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        webViewDashlet = new WebViewDashlet(webDriver);

        user.set(dataUser.usingAdmin().createRandomTestUser());
        addDashlet(user.get(), DashboardCustomization.UserDashlet.WEB_VIEW, 1, 3);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2143")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.USER_DASHBOARD })
    public void webViewDashlet()
    {
        userDashboardPage.navigate(user.get());
        webViewDashlet.assertDashletTitleEquals(language.translate("webViewDashlet.title"))
            .assertNoWebPageMessageIsDisplayed()
            .assertConfigureDashletIconDisplayed()
            .clickOnHelpIcon(DashletHelpIcon.WEB_VIEW)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageEquals(language.translate("webViewDashlet.balloonMessage"))
            .closeHelpBalloon()
                .clickConfigureDashlet()
                    .assertDialogTitleEquals(language.translate("webViewDashlet.configure.title"))
                    .assertLinkTitleFieldIsDisplayed()
                    .assertUrlFieldIsDisplayed()
                    .assertOKButtonIsDisplayed()
                    .assertCancelButtonIsDisplayed()
                    .assertUrlFieldIsMandatory()
                    .assertUrlFieldEmptyValidationMessageIsCorrect()
                    .setUrlField(RandomData.getRandomAlphanumeric())
                    .assertInvalidUrlFieldValidationMessageIsCorrect();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }
}
