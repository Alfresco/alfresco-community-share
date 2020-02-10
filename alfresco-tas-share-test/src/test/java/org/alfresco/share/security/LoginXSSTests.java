package org.alfresco.share.security;

import org.alfresco.common.DataProviderClass;
import org.alfresco.po.share.LoginPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Iulia.Burca
 */
public class LoginXSSTests extends ContextAwareWebTest
{
    @Autowired
    private LoginPage loginPage;


    @TestRail (id = "C286512, C286520")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY }, dataProvider = "XSSCredentials", dataProviderClass = DataProviderClass.class)
    public void loginWithXssStrings(String XSSUsername, String XSSPassword)
    {
        LOG.info("STEP 1: Navigate to Login page");
        loginPage.navigate();
        assertEquals(loginPage.getPageTitle(), "Alfresco » Login", "Displayed page=");

        LOG.info("STEP 2: Enter into User name field text: '" + XSSUsername + "'.");
        loginPage.typeUserName(XSSUsername);

        LOG.info("STEP 3: Enter into Password field text: '" + XSSPassword + "'.");
        loginPage.typePassword(XSSPassword);

        LOG.info("STEP 4: Press 'Login' button;");
        loginPage.clickLogin();
        assertEquals(loginPage.getPageTitle(), "Alfresco » Login", "Displayed page=");
        assertTrue(loginPage.isAuthenticationErrorDisplayed(), "Authentication Error message is not displayed.");

        cleanupAuthenticatedSession();
    }

}
