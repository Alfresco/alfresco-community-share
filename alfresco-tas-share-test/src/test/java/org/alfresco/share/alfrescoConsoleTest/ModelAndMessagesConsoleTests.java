package org.alfresco.share.alfrescoConsoleTest;

import org.alfresco.po.adminconsole.consoles.ModelAndMessagesConsolePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Created by Mirela Tifui on 11/1/2017.
 */
public class ModelAndMessagesConsoleTests extends ContextAwareWebTest
{
    @Autowired
    ModelAndMessagesConsolePage modelAndMessagesConsolePage;
    SoftAssert softAssert = new SoftAssert();
    private String userName = "modelAndMessagesUser" + RandomData.getRandomAlphanumeric();

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + "@test.com", "model", "messages");
    }

    @AfterClass (alwaysRun = true)
    public void testCleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
    }

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void modelAndMessagesConsoleTest()
    {
        LOG.info("Step 1: Navigate to Model and messages console");
        modelAndMessagesConsolePage.setBrowser(getBrowser());
        modelAndMessagesConsolePage.navigate();
        softAssert.assertNotNull(modelAndMessagesConsolePage.getPageTitle(), "Page title is not empty");

        LOG.info("Step 2: Check elements present on page");
        softAssert.assertTrue(modelAndMessagesConsolePage.isCommandFieldPresent(), "Command field is not present");
        softAssert.assertTrue(modelAndMessagesConsolePage.isExecuteButtonPresent(), "Execute button is not present");

        LOG.info("Step 3: Send empty command");
        modelAndMessagesConsolePage.executeCommand("");
        softAssert.assertEquals(modelAndMessagesConsolePage.getResults().trim(), "No such command, try 'help'.", "Results are not as expected");

        LOG.info("Step 4: Send help command");
        modelAndMessagesConsolePage.executeCommand("help");
        softAssert.assertTrue(modelAndMessagesConsolePage.getResults().contains("Meta commands"), "Meta commands is not present in the result");
        softAssert.assertTrue(modelAndMessagesConsolePage.getResults().contains("Quit / Exit"), "Quit / Exit commands is not present in the result");
        softAssert.assertTrue(modelAndMessagesConsolePage.getResults().contains("General Repo Admin Commands"),
            "General Repo Admin Commands is not present in the result");
        softAssert.assertTrue(modelAndMessagesConsolePage.getResults().contains("Model Admin Commands"), "Model Admin Commands is not present in the result");
        softAssert
            .assertTrue(modelAndMessagesConsolePage.getResults().contains("Message Admin Commands"), "Message Admin Commands is not present in the result");
        softAssert.assertAll();
    }
}
