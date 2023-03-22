package org.alfresco.share.security;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.common.DataProviderClass;
import org.alfresco.po.share.LoginPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
@Slf4j
/**
 * @author Iulia.Burca
 */
public class LoginXSSTests extends BaseTest
{
    private LoginPage loginPage;
    @TestRail (id = "C286512, C286520")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" }, dataProvider = "XSSCredentials", dataProviderClass = DataProviderClass.class)
    public void loginWithXssStrings(String XSSUsername, String XSSPassword)
    {
        loginPage = new LoginPage(webDriver);

        log.info("STEP 1: Navigate to Login page");
        loginPage
            .navigate().assertLoginPageTitleIsCorrect();

        log.info("STEP 2: Enter into User name field text: '" + XSSUsername + "'.");
        loginPage
            .typeUserName(XSSUsername);

        log.info("STEP 3: Enter into Password field text: '" + XSSPassword + "'.");
        loginPage
            .typePassword(XSSPassword);

        log.info("STEP 4: Press 'Login' button;");
        loginPage
            .clickLogin();
        loginPage
            .assertLoginPageTitleIsCorrect();

        log.info("STEP 5: Verify Notification Failed to Login appears");
        assertTrue(loginPage.isAuthenticationErrorDisplayed(), "Authentication Error message is not displayed.");
    }
}
