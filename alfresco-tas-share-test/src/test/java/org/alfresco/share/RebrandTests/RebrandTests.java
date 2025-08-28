package org.alfresco.share.RebrandTests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.AboutPopUpPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;

import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
@Slf4j
public class RebrandTests extends BaseTest
{
    LoginPage login;
    UserDashboardPage userDashboard;
    AboutPopUpPage aboutPopup;
    AdvancedSearchPage advancedSearch;

    @TestRail (id = "C42575, C42576, C42577, C42578, C42580, C42579")
    @Test (groups = { TestGroup.SANITY, TestGroup.SHARE, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE})
    public void checkLoginPage()
    {
        login = new LoginPage(webDriver);
        log.info("Verify old logo is replaced");
        login.navigate();
        assertTrue(login.isLogoDisplayed(), "New logo displayed on Sign In page");
        assertFalse(login.isSimpleSmartDisplayed(), "'Simple+Smart' displayed");
        assertEquals(login.getBackgroundColour(), new String[] { "rgba(12, 121, 191, 1)", "rgba(136, 136, 136, 1)"
        }, "Background colour is not blue!");
        assertEquals(login.getAlfrescoShareColour(), "rgba(12, 121, 191, 1)", "Alfresco share color is not blue!");
        assertEquals(login.getCopyRightText(), String.format("© 2005-%s Alfresco Software Inc. All rights reserved.", DateTime.now().getYear()), "Correct copyright year");
        assertEquals(login.getSignInButtonColor(), "rgba(255, 255, 255, 1)", "Correct color for Sign In");


    }

    @TestRail (id = "C42581")
    @Test (groups = { TestGroup.SANITY, TestGroup.SHARE })
    public void checkNewAlfrescoLogo()
    {
        userDashboard = new UserDashboardPage(webDriver);
        advancedSearch = new AdvancedSearchPage(webDriver);
        SoftAssert softAssert = new SoftAssert();
        log.info("Verify Alfresco One logo in the footer is replaced with new Alfresco logo");
        authenticateUsingLoginPage(getAdminUser());
        softAssert.assertTrue(userDashboard.isNewAlfrescoLogoDisplayed(), "New Alfresco logo displayed");
        advancedSearch.navigate();
        softAssert.assertTrue(userDashboard.isNewAlfrescoLogoDisplayed(), "New Alfresco logo displayed");
        softAssert.assertAll();
    }

    //    @Bug (id = "SHA-2178", description = "Share version is not correct")
    @TestRail (id = "C42582, C42583")
    @Test (groups = { TestGroup.SANITY, TestGroup.SHARE })
    public void checkAlfrescoOneLogoInVersiondialog() {
        log.info("Verify Alfresco Community logo in version dialog is replaced with new Alfresco logo");
        userDashboard= new UserDashboardPage(webDriver);
        aboutPopup = new AboutPopUpPage(webDriver);
        authenticateUsingLoginPage(getAdminUser());
        userDashboard.openAboutPage();

        RepositoryInfo info = getCmisApi().authenticateUser(getAdminUser()).getRepositoryInfo();
        Assert.assertTrue(aboutPopup.getAlfrescoVersion().contains(StringUtils.substring(info.getProductVersion(), 0, 5)));
        Assert.assertTrue(aboutPopup.getShareVersion().contains("Alfresco Share"));
    }
}
