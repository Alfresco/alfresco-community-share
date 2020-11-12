package org.alfresco.share;

import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LogoTests extends BaseShareWebTests
{
    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        setupAuthenticatedSession(getAdminUser());
    }

    @TestRail (id = "C2088")
    @Test (groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void checkAboutBox()
    {
        userDashboardPage.assertAlfrescoLogoIsDisplayedInPageFooter()
            .openAboutPage()
            .assertAlfrescoVersionIsDisplayed()
                .assertShareVersionIsDisplayed()
                .assertLicenseHolderIsNotEmpty()
                .assertCopyrightIsCorrect()
                .assertContributionSectionIsDisplayed()
                .assertClickAlfrescoLink()
                .assertClickLegalAndLicenseLink();
    }
}
