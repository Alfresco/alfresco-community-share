package org.alfresco.share;

import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LogoTests extends ContextAwareWebTest
{
    @Autowired
    private UserDashboardPage userDashboard;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        setupAuthenticatedSession(dataUser.getAdminUser());
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
}
