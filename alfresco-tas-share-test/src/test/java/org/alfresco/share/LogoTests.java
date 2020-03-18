package org.alfresco.share;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.AboutPopUpPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LogoTests extends ContextAwareWebTest
{
    @Autowired
    UserDashboardPage userDashboard;

    @Autowired
    AboutPopUpPage aboutPage;

    String user1 = "user1" + System.currentTimeMillis();
    private String copyRightText = "© 2005-2019" + " Alfresco Software Inc. All rights reserved. www.alfresco.com Legal and License";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, user1, user1 + domain, user1, user1);
        setupAuthenticatedSession(user1, user1);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
    }

    //copyRightText is hardcoded with year 2019, so running on older builds will make this test fail
    @TestRail (id = "C2088")
    @Test (groups = { TestGroup.SANITY, TestGroup.AUTH, "tobefixed" })
    public void checkAboutBox()
    {
        LOG.info("STEP 1: Verify bottom of User Dashboard page");
        assertEquals(userDashboard.getPageTitle(), language.translate("userDashboard.PageTitle"), "Displayed page= ");
        assertTrue(userDashboard.isAlfrescoLogoDisplayed(), "Alfresco Logo is displayed.");
        LOG.info("STEP 2: Click Alfresco logo from bottom page");
        userDashboard.openAboutPage();
//        assertEquals(aboutPage.getShareVersion(), language.translate("shareVersion"), "Share version=");
//        assertEquals(aboutPage.getAlfrescoVersion(), language.translate("alfrescoVersion"), "Alfresco version=");
        assertEquals(aboutPage.getShareVersion().substring(0, 14), "Alfresco Share", "Share version=");
        assertEquals(aboutPage.getAlfrescoVersion().substring(0, 19), "Alfresco Enterprise", "Alfresco version=");
        assertFalse(aboutPage.getLicenseHolder().isEmpty(), "License holder is empty.");
        assertTrue(aboutPage.isContributionsDisplayed(), "Contributions is displayed.");
        assertEquals(aboutPage.getCopyRight(), copyRightText, "Copy right=");
        LOG.info("STEP 3: Click 'www.alfresco.com'");
        aboutPage.clickAlfrescoLink();
        getBrowser().switchWindow(1);
        assertEquals(getBrowser().getCurrentUrl(), "https://www.alfresco.com/", "Displayed URL=");
        closeWindowAndSwitchBack();
        LOG.info("STEP 4: Click Legal and License link");
        setupAuthenticatedSession(user1, user1);
        userDashboard.openAboutPage();
        aboutPage.clickLegalAndLicenceLink();
        getBrowser().switchWindow(1);
        assertEquals(getBrowser().getCurrentUrl(), "https://www.alfresco.com/legal/agreements", "Displayed URL=");
        closeWindowAndSwitchBack();
    }
}
