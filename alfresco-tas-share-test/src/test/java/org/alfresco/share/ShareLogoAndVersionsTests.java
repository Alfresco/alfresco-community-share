package org.alfresco.share;

import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ShareLogoAndVersionsTests extends ContextAwareWebTest
{
    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        setupAuthenticatedSession(getAdminUser());
    }

    @TestRail (id = "C2088")
    @Test (groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void checkAboutBox()
    {
        userDashboard.assertAlfrescoLogoIsDisplayedInPageFooter()
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
        userDashboard.assertShareVersionWarningIsNotDisplayed();
    }
}
