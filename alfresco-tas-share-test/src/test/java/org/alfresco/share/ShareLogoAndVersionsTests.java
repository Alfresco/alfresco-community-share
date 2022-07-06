package org.alfresco.share;

import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ShareLogoAndVersionsTests extends BaseTest
{
    @BeforeMethod(alwaysRun = true)
    public void beforeTest()
    {
        authenticateUsingCookies(getAdminUser());
    }

    @TestRail (id = "C2088")
    @Test (groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void checkAboutBox()
    {
        userDashboardPage.navigate(getAdminUser())
            .assertAlfrescoLogoIsDisplayedInPageFooter()
            .openAboutPage()
            .assertAlfrescoVersionIsDisplayed()
                .assertShareVersionIsDisplayed()
                .assertLicenseHolderIsNotEmpty()
                .assertCopyrightIsCorrect()
                .assertContributionSectionIsDisplayed()
                .assertClickAlfrescoLink()
                .assertClickLegalAndLicenseLink();
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.AUTH }, enabled = false)
    public void checkThatShareVersionWarningIsNotDisplayed()
    {
        userDashboardPage.navigate(getAdminUser())
            .assertShareVersionWarningIsNotDisplayed();
    }
}
