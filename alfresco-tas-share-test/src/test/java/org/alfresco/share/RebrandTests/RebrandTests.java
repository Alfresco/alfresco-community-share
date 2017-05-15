package org.alfresco.share.RebrandTests;

import org.alfresco.po.share.AboutPopUpPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class RebrandTests extends ContextAwareWebTest {
	@Autowired
    LoginPage login;

	@Autowired
    UserDashboardPage userDashboard;

	@Autowired
    AboutPopUpPage aboutPopup;

	@Autowired
    AdvancedSearchPage advancedSearch;

	private final String[] expectedBackgroundColor = new String[] { "rgba(102, 102, 102, 1)", "rgba(102, 102, 102, 1)",
			"rgba(102, 102, 102, 1)" };
	private final String expectedAlfrescoShareColor = "rgba(12, 121, 191, 1)";

	@TestRail(id = "C42575")
	@Test(groups = { TestGroup.SANITY, TestGroup.SHARE })
	public void replaceLogo()

	{
		logger.info("Verify old logo is replaced");
		login.navigate();
		assertTrue(login.isLogoDisplayed(), "New logo displayed on Sign In page");
		try {
			assertFalse(login.isOldLogoDisplayed(), "Old logo displayed");
		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			cleanupAuthenticatedSession();
		}
	}

	@TestRail(id = "C42576")
	@Test(groups = { TestGroup.SANITY, TestGroup.SHARE })
	public void simpleSmartContentReplaced()

	{
		logger.info("Step1: Verify 'Simple+Smart' is replaced with 'make business flow' on Sign In page");
		login.navigate();
		assertTrue(login.isMakeBusinessFlowDisplayed(), "'make business flow' displayed");
		try {
			assertFalse(login.isSimpleSmartDisplayed(), "'Simple+Smart' displayed");

		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			cleanupAuthenticatedSession();
		}
	}

	@TestRail(id = "C42577")
	@Test(groups = { TestGroup.SANITY, TestGroup.SHARE })
	public void checkSignInBackgroundColour()

	{
		logger.info("Verify the background colour of the 'Sign In' page is blue");
		login.navigate();
		assertEquals(login.getBackgroundColour(), expectedBackgroundColor, "Background colour is not blue!");
		cleanupAuthenticatedSession();

	}

	@TestRail(id = "C42578")
	@Test(groups = { TestGroup.SANITY, TestGroup.SHARE })
	public void checkFontColourForAlfrescoShare() {
		logger.info("Verify the font colour of 'Alfresco Share' in the sign in form is Blue");
		login.navigate();
		assertEquals(login.getAlfrescoShareColour(), expectedAlfrescoShareColor, "Alfresco share color is not blue!");
		cleanupAuthenticatedSession();

	}

	@TestRail(id = "C42580")
	@Test(groups = { TestGroup.SANITY, TestGroup.SHARE })
	public void verifyCopyrightYear() {
		logger.info("Verify Copyright year in the Sign in page");
		login.navigate();

		assertTrue(login.isCopyRightYearCorrect(2017), "Correct copyright year");
		cleanupAuthenticatedSession();
	}

	@TestRail(id = "C42581")
	@Test(groups = { TestGroup.SANITY, TestGroup.SHARE })
	public void checkNewAlfrescoLogo()

	{
		logger.info("Verify Alfresco One logo in the footer is replaced with new Alfresco logo");
		setupAuthenticatedSession(adminUser, adminPassword);

		assertTrue(userDashboard.isNewAlfrescoLogoDisplayed(), "New Alfresco logo displayed");
		assertFalse(userDashboard.isOldAlfrescoLogoDisplayed(), "Old Alfresco logo displayed");
		cleanupAuthenticatedSession();

	}

	@TestRail(id = "C42582")
	@Test(groups = { TestGroup.SANITY, TestGroup.SHARE })
	public void checkAlfrescoOneLogoInVersiondialog()

	{
		logger.info("Verify Alfresco Community logo in version dialog is replaced with new Alfresco logo");
		setupAuthenticatedSession(adminUser, adminPassword);
		userDashboard.openAboutPage();

		assertEquals(aboutPopup.getAlfrescoVersion(), "Alfresco Enterprise v5.2.1", "Correct Alfresco version!");
		assertEquals(aboutPopup.getShareVersion(), "Alfresco Share v5.2.1-SNAPSHOT", "Correct Share version!");
		cleanupAuthenticatedSession();

	}

	@TestRail(id = "C42583")
	@Test(groups = { TestGroup.SANITY, TestGroup.SHARE })
	public void checkAlfrescoLogoOnSearchResultsPage()

	{
		logger.info(
				"Verify Alfresco One logo in the footer on aikau search results page is replaced with new Alfresco logo");
		setupAuthenticatedSession(adminUser, adminPassword);
		advancedSearch.navigate();

		assertTrue(userDashboard.isNewAlfrescoLogoDisplayed(), "New Alfresco logo displayed");
		assertFalse(userDashboard.isOldAlfrescoLogoDisplayed(), "Old Alfresco logo displayed");
		cleanupAuthenticatedSession();

	}

	@TestRail(id = "C42579")
	@Test(groups = { TestGroup.SANITY, TestGroup.SHARE })
	public void checkSignInButtonColor()

	{
		logger.info("Verify the color for Sign in button");
		login.navigate();
		assertEquals(login.getSignInButtonColor(), "rgba(255, 255, 255, 1)", "Correct color for Sign In");
	}
}
