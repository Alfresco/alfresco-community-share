package org.alfresco.share.RebrandTests;

import org.alfresco.cmis.CmisWrapper;
import org.alfresco.po.share.AboutPopUpPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.report.Bug;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class RebrandTests extends ContextAwareWebTest
{
    @Autowired
    LoginPage login;

    @Autowired
    UserDashboardPage userDashboard;

    @Autowired
    AboutPopUpPage aboutPopup;

    @Autowired
    AdvancedSearchPage advancedSearch;

    @Autowired
    private CmisWrapper cmisApi;

    @TestRail (id = "C42575, C42576, C42577, C42578, C42580, C42579")
    @Test (groups = { TestGroup.SANITY, TestGroup.SHARE })
    public void checkLoginPage()
    {
        cleanupAuthenticatedSession();
        LOG.info("Verify old logo is replaced");
        login.navigate();

        assertTrue(login.isLogoDisplayed(), "New logo displayed on Sign In page");
        assertFalse(login.isOldLogoDisplayed(), "Old logo displayed");
        assertFalse(login.isSimpleSmartDisplayed(), "'Simple+Smart' displayed");
        assertEquals(login.getBackgroundColour(), new String[] { "rgb(12, 121, 191)", "rgb(136, 136, 136)"
        }, "Background colour is not blue!");
        assertEquals(login.getAlfrescoShareColour(), "rgb(12, 121, 191)", "Alfresco share color is not blue!");
        assertEquals(login.getCopyRightText(), String.format("© 2005-%s Alfresco Software Inc. All rights reserved.", DateTime.now().getYear()), "Correct copyright year");
        assertEquals(login.getSignInButtonColor(), "rgb(255, 255, 255)", "Correct color for Sign In");


    }

    @TestRail (id = "C42581")
    @Test (groups = { TestGroup.SANITY, TestGroup.SHARE })
    public void checkNewAlfrescoLogo()
    {
        SoftAssert softAssert = new SoftAssert();
        logger.info("Verify Alfresco One logo in the footer is replaced with new Alfresco logo");
        setupAuthenticatedSession(adminUser, adminPassword);
        softAssert.assertTrue(userDashboard.isNewAlfrescoLogoDisplayed(), "New Alfresco logo displayed");
        softAssert.assertFalse(userDashboard.isOldAlfrescoLogoDisplayed(), "Old Alfresco logo displayed");
        advancedSearch.navigate();
        softAssert.assertTrue(userDashboard.isNewAlfrescoLogoDisplayed(), "New Alfresco logo displayed");
        softAssert.assertFalse(userDashboard.isOldAlfrescoLogoDisplayed(), "Old Alfresco logo displayed");
        softAssert.assertAll();
        cleanupAuthenticatedSession();
    }

    @Bug (id = "SHA-2178", description = "Share version is not correct")
    @TestRail (id = "C42582, C42583")
    @Test (groups = { TestGroup.SANITY, TestGroup.SHARE })
    public void checkAlfrescoOneLogoInVersiondialog()
    {
        logger.info("Verify Alfresco Community logo in version dialog is replaced with new Alfresco logo");
        setupAuthenticatedSession(adminUser, adminPassword);
        userDashboard.openAboutPage();

        RepositoryInfo info = cmisApi.authenticateUser(new UserModel(adminUser, adminPassword)).getRepositoryInfo();
        Assert.assertTrue(aboutPopup.getAlfrescoVersion().contains(StringUtils.substring(info.getProductVersion(), 0, 5)));
        Assert.assertTrue(aboutPopup.getShareVersion().contains("Alfresco Share"));
        cleanupAuthenticatedSession();
    }
}
