package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.po.share.dashlet.ConfigureWebViewDashletPopUp;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.WebViewDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WebViewDashletTests extends AbstractUserDashboardDashletsTests
{
    @Autowired
    private WebViewDashlet webViewDashlet;

    @Autowired
    ConfigureWebViewDashletPopUp configureWebViewPopUp;

    private UserModel user;

    @BeforeClass (alwaysRun = true)
    public void setup() throws DataPreparationException
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(user);
        addDashlet(Dashlets.WEB_VIEW, 1);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C2143")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void webViewDashlet()
    {
        webViewDashlet.assertDashletTitleIs(language.translate("webViewDashlet.title"))
            .assertNoWebPageMessageIsDisplayed()
            .assertConfigureDashletIconDisplayed()
            .clickOnHelpIcon(DashletHelpIcon.WEB_VIEW)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageIs(language.translate("webViewDashlet.balloonMessage"))
            .closeHelpBalloon()
                .clickConfigureDashlet()
                    .assertDialogTitleEqualsWithExpected(language.translate("webViewDashlet.configure.title"))
                    .assertLinkTitleFieldIsDisplayed()
                    .assertUrlFieldIsDisplayed()
                    .assertOKButtonIsDisplayed()
                    .assertCancelButtonIsDisplayed()
                    .assertUrlFieldIsMandatory()
                    .assertUrlFieldEmptyValidationMessageIsCorrect()
                    .setUrlField(RandomData.getRandomAlphanumeric())
                    .assertInvalidUrlFieldValidationMessageIsCorrect();
    }
}
