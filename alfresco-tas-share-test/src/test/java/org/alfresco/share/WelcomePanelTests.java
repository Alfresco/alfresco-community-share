package org.alfresco.share;

import org.alfresco.po.share.HideWelcomePanelDialogue;
import org.alfresco.po.share.WelcomePanel;
import org.alfresco.po.share.user.CustomizeUserDashboardPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
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

    private String welcomeMessage = "GET STARTED\nTake a quick tour of Alfresco Share or learn about advanced features.";
    private String userName1 = "User1" + RandomData.getRandomAlphanumeric();
    private String userName2 = "User2" + RandomData.getRandomAlphanumeric();
    private String userName3 = "User3" + RandomData.getRandomAlphanumeric();

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName1, password, "@tests.com", userName1, userName1);
        userService.create(adminUser, adminPassword, userName2, password, "@tests.com", userName2, userName2);
        userService.create(adminUser, adminPassword, userName3, password, "@tests.com", userName3, userName3);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1);
        userService.delete(adminUser, adminPassword, userName2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName2);
        userService.delete(adminUser, adminPassword, userName3);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName3);
    }

    @TestRail (id = "C202855")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
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

    @TestRail (id = "C202856")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void hideWelcomePanel()
    {
        logger.info("Step1: Login to Share and verify Get Started panel");
        setupAuthenticatedSession(userName2, password);
        Assert.assertTrue(welcomePanel.isWelcomePanelDisplayed(), "Welcome panel displayed");
        Assert.assertEquals(welcomePanel.getWelcomeMessage(), welcomeMessage, "Correct welcome message");
        Assert.assertTrue(welcomePanel.isHideButtonDisplayed(), "Hide button displayed");

        logger.info("Step2: Click on the Hide button and verify Get Started Panel is not displayed anymore");
        welcomePanel.clickOnHideButton();
        getBrowser().waitInSeconds(4);
        hideWelcomePanelDialogue.confirmHideWelcomePanel();
        Assert.assertFalse(welcomePanel.isWelcomePanelDisplayed(), "Welcome panel displayed");
    }

    @TestRail (id = "C202857")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
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
        getBrowser().waitInSeconds(3);
        Assert.assertTrue(welcomePanel.isWelcomePanelDisplayed(), "Welcome panel displayed");

        logger.info("Step4: Click on the Customise User Dashboard icon on the right, select Hide on Dashboard radio button and click OK button");
        userDashboard.clickCustomizeUserDashboard();
        customizeUserDashboard.activateGetStartedPanel(false);
        customizeUserDashboard.clickOk();
        Assert.assertFalse(welcomePanel.isWelcomePanelDisplayed(), "Welcome panel displayed");
    }
}
