package org.alfresco.share;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.HideWelcomePanelDialogue;
import org.alfresco.po.share.WelcomePanel;
import org.alfresco.po.share.user.CustomizeUserDashboardPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WelcomePanelTests extends ContextAwareWebTest
{

    @Autowired
    WelcomePanel welcomePanel;

    @Autowired
    UserDashboardPage userDashboard;

    @Autowired
    HideWelcomePanelDialogue hideWelcomePanelDialogue;

    @Autowired
    CustomizeUserDashboardPage customizeUserDashboard;

    private String welcomeMessage = "GET STARTED\nTake a quick tour of Alfresco or learn about advanced features.";
    private String userName1 = "User1" + DataUtil.getUniqueIdentifier();
    private String userName2 = "User2" + DataUtil.getUniqueIdentifier();
    private String userName3 = "User3" + DataUtil.getUniqueIdentifier();

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName1, password, "@tests.com", userName1, userName1);
        userService.create(adminUser, adminPassword, userName2, password, "@tests.com", userName2, userName2);
        userService.create(adminUser, adminPassword, userName3, password, "@tests.com", userName3, userName3);
    }

    @TestRail(id = "C202855")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void verifyWelcomePanel()
    {

        logger.info("Step1: Login to Share and verify Get Started panel");
        setupAuthenticatedSession(userName1, password);
        Assert.assertTrue(welcomePanel.isWelcomePanelDisplayed(), "Welcome panel displayed");
        Assert.assertEquals(welcomeMessage, welcomePanel.getWelcomeMessage(), "Correct welcome message");
        Assert.assertTrue(welcomePanel.isHideButtonDisplayed(), "Hide button displayed");

        logger.info("Step2: Click Get Started link and verify Alfresco tutorial page is displayed");
        welcomePanel.clickGetStartedLink();
        Assert.assertTrue(welcomePanel.isAlfrescoTutorialDisplayed(), "Alfresco tutorial page displayed");
    }

    @TestRail(id = "C202856")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void hideWelcomePanel()
    {
        logger.info("Step1: Login to Share and verify Get Started panel");
        setupAuthenticatedSession(userName2, password);
        Assert.assertTrue(welcomePanel.isWelcomePanelDisplayed(), "Welcome panel displayed");
        Assert.assertEquals(welcomeMessage, welcomePanel.getWelcomeMessage(), "Correct welcome message");
        Assert.assertTrue(welcomePanel.isHideButtonDisplayed(), "Hide button displayed");

        logger.info("Step2: Click on the Hide button and verify Get Started Panel is not displayed anymore");
        welcomePanel.clickOnHideButton();
        hideWelcomePanelDialogue.confirmHideWelcomePanel();
        Assert.assertFalse(welcomePanel.isWelcomePanelDisplayed(), "Welcome panel displayed");
    }

    @TestRail(id = "C202857")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void restoreWelcomePanel()
    {
        logger.info("Step1: Login to Share and verify Get Started panel");
        setupAuthenticatedSession(userName3, password);
        Assert.assertTrue(welcomePanel.isWelcomePanelDisplayed(), "Welcome panel displayed");
        Assert.assertEquals(welcomeMessage, welcomePanel.getWelcomeMessage(), "Correct welcome message");
        Assert.assertTrue(welcomePanel.isHideButtonDisplayed(), "Hide button displayed");

        logger.info("Step2: Click on the Hide button and verify Get Started Panel is not displayed anymore");
        welcomePanel.clickOnHideButton();
        hideWelcomePanelDialogue.confirmHideWelcomePanel();
        Assert.assertFalse(welcomePanel.isWelcomePanelDisplayed(), "Welcome panel displayed");

        logger.info("Step3: Click on the Customise User Dashboard icon on the right, select Show on Dashboard radio button and click OK button");
        userDashboard.clickCustomizeUserDashboard();
        customizeUserDashboard.activateGetStartedPanel(true);
        customizeUserDashboard.clickOk();
        Assert.assertTrue(welcomePanel.isWelcomePanelDisplayed(), "Welcome panel displayed");

        logger.info("Step4: Click on the Customise User Dashboard icon on the right, select Hide on Dashboard radio button and click OK button");
        userDashboard.clickCustomizeUserDashboard();
        customizeUserDashboard.activateGetStartedPanel(false);
        customizeUserDashboard.clickOk();
        Assert.assertFalse(welcomePanel.isWelcomePanelDisplayed(), "Welcome panel displayed");
    }
}
