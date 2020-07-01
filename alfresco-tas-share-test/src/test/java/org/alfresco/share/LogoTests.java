package org.alfresco.share;

import org.alfresco.po.share.AboutPopUpPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class LogoTests extends ContextAwareWebTest
{
    @Autowired
    private UserDashboardPage userDashboard;

    @Autowired
    AboutPopUpPage aboutPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        setupAuthenticatedSession(dataUser.getAdminUser());
    }

    @TestRail (id = "C2088")
    @Test (groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void checkAboutBox()
    {
        assertTrue(userDashboard.isAlfrescoLogoDisplayed(), "Alfresco Logo is displayed.");
        userDashboard.openAboutPage()
            .assertAlfrescoVersionIsDisplayed()
            .assertShareVersionIsDisplayed()
            .assertLicenseHolderIsNotEmpty()
            .assertCopyrightIsCorrect()
            .assertContributionSectionIsDisplayed()
            .assertClickAlfrescoLink()
            .assertClickLegalAndLicenseLink();
    }
}
