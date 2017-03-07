package org.alfresco.share;

import org.alfresco.po.share.AboutPopUpPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Calendar;

import static org.testng.Assert.*;

public class LogoTests extends ContextAwareWebTest
{
    @Autowired
    UserDashboardPage userDashboard;

    @Autowired
    AboutPopUpPage aboutPage;

    String user1 = "user1" + System.currentTimeMillis();
    int year = Calendar.getInstance().get(Calendar.YEAR);
    
 //   private String copyRightText = "© 2005-" + year + " Alfresco Software Inc. All rights reserved. www.alfresco.com Legal and License";  
    private String copyRightText = "© 2005-2016" + " Alfresco Software Inc. All rights reserved. www.alfresco.com Legal and License";
    

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, user1, user1 + domain, user1, user1);
        setupAuthenticatedSession(user1, user1);
        assertEquals(userDashboard.getPageTitle(), "Alfresco » User Dashboard", "Displayed page= ");
    }

    @TestRail(id = "C2088")
    @Test
    public void checkAboutBox()
    {
        LOG.info("STEP 1: Verify bottom of User Dashboard page");
        assertTrue(userDashboard.isAlfrescoLogoDisplayed(), "Alfresco Logo is displayed.");

        LOG.info("STEP 2: Click Alfresco logo from bottom page");
        userDashboard.openAboutPage();
        assertEquals(aboutPage.getShareVersion(), language.translate("shareVersion"), "Share version=");
        assertEquals(aboutPage.getAlfrescoVersion(), language.translate("alfrescoVersion"), "Alfresco version=");
        assertFalse(aboutPage.getLicenseHolder().isEmpty(), "License holder is empty.");
        assertTrue(aboutPage.isContributionsDisplayed(), "Contributions is displayed.");
        assertEquals(aboutPage.getCopyRight(), copyRightText, "Copy right=");

        LOG.info("STEP 3: Click 'www.alfresco.com'");
        // Store the current window handle

        String currentWindow = browser.getWindowHandle();

        aboutPage.clickAlfrescoLink();
        browser.waitInSeconds(2);

        // Switch to new window opened
        for (String winHandle : browser.getWindowHandles())
        {
            browser.switchTo().window(winHandle);
            if (browser.getCurrentUrl().contains("alfresco.com"))
            {
                break;
            }
            else
            {
                browser.switchTo().window(currentWindow);
            }
        }

        assertEquals(browser.getCurrentUrl(), "https://www.alfresco.com/", "Displayed URL=");
        switchWindow();

        closeWindowAndSwitchBack();

        LOG.info("STEP 4: Click Legal and License link");
        setupAuthenticatedSession(user1, user1);

        userDashboard.openAboutPage();

        // Store the current window handle
        currentWindow = browser.getWindowHandle();
        aboutPage.clickLegalAndLicenceLink();

        browser.waitInSeconds(2);
        // Switch to new window opened
        for (String winHandle : browser.getWindowHandles())
        {
            browser.switchTo().window(winHandle);
            if (browser.getCurrentUrl().contains("alfresco.com"))
            {
                break;
            }
            else
            {
                browser.switchTo().window(currentWindow);
            }
        }
        assertEquals(browser.getCurrentUrl(), "https://www.alfresco.com/alfresco-agreements", "Displayed URL=");

    }

}
