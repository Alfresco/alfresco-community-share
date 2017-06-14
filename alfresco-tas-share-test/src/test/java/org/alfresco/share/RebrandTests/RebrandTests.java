package org.alfresco.share.RebrandTests;

import org.alfresco.po.share.AboutPopUpPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class RebrandTests extends ContextAwareWebTest {
	@Autowired
    LoginPage login;

	@Autowired
    UserDashboardPage userDashboard;

	@Autowired
    AboutPopUpPage aboutPopup;

	@Autowired
    AdvancedSearchPage advancedSearch;

	@TestRail(id = "C42575, C42576, C42577, C42578, C42580, C42579")
	@Test(groups = { TestGroup.SANITY, TestGroup.SHARE })
	public void checkLoginPage() {
		logger.info("Verify old logo is replaced");
		login.navigate();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(login.isLogoDisplayed(), "New logo displayed on Sign In page");
		softAssert.assertTrue(login.isMakeBusinessFlowDisplayed(), "'make business flow' displayed");
		softAssert.assertFalse(login.isOldLogoDisplayed(), "Old logo displayed");
		softAssert.assertTrue(login.isMakeBusinessFlowDisplayed(), "'make business flow' displayed");
		softAssert.assertFalse(login.isSimpleSmartDisplayed(), "'Simple+Smart' displayed");
		softAssert.assertEquals(login.getBackgroundColour(), new String[] { "rgba(102, 102, 102, 1)", "rgba(102, 102, 102, 1)",
				"rgba(102, 102, 102, 1)" }, "Background colour is not blue!");
		softAssert.assertEquals(login.getAlfrescoShareColour(), "rgba(12, 121, 191, 1)", "Alfresco share color is not blue!");
		softAssert.assertEquals(login.getCopyRightText(), String.format("© 2005-%s Alfresco Software Inc. All rights reserved.", DateTime.now().getYear()), "Correct copyright year");
		softAssert.assertEquals(login.getSignInButtonColor(), "rgba(255, 255, 255, 1)", "Correct color for Sign In");
		softAssert.assertAll();
	}

	@TestRail(id = "C42581")
	@Test(groups = { TestGroup.SANITY, TestGroup.SHARE })
	public void checkNewAlfrescoLogo() {
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

	@TestRail(id = "C42582, C42583")
	@Test(groups = { TestGroup.SANITY, TestGroup.SHARE })
	public void checkAlfrescoOneLogoInVersiondialog() {
		SoftAssert softAssert = new SoftAssert();
		logger.info("Verify Alfresco Community logo in version dialog is replaced with new Alfresco logo");
		setupAuthenticatedSession(adminUser, adminPassword);
		userDashboard.openAboutPage();
		softAssert.assertEquals(aboutPopup.getAlfrescoVersion(), "Alfresco Enterprise v5.2.1", "Correct Alfresco version!");
		softAssert.assertEquals(aboutPopup.getShareVersion(), "Alfresco Share v5.2.1", "Correct Share version!");
		softAssert.assertAll();
		cleanupAuthenticatedSession();
	}
}
