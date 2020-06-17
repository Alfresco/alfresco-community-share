package org.alfresco.share.alfrescoConsoleTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.adminconsole.consoles.ModelAndMessagesConsolePage;
import org.alfresco.share.ContextAwareWebAdminConsoleTest;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/1/2017.
 */
public class ModelAndMessagesConsoleTests extends ContextAwareWebAdminConsoleTest
{
    @Autowired
    ModelAndMessagesConsolePage modelAndMessagesConsolePage;

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void modelAndMessagesConsoleTest()
    {
        LOG.info("Step 1: Navigate to Model and messages console");
        modelAndMessagesConsolePage.navigate();
        assertTrue(modelAndMessagesConsolePage.getPageTitle().startsWith(language.translate("modelAndMessages.PageTitle")),
                "Page title is not empty");

        LOG.info("Step 2: Check elements present on page");
        assertTrue(modelAndMessagesConsolePage.isCommandFieldPresent(), "Command field is not present");
        assertTrue(modelAndMessagesConsolePage.isExecuteButtonPresent(), "Execute button is not present");

        LOG.info("Step 3: Send empty command");
        modelAndMessagesConsolePage.executeCommand("");
        assertEquals(modelAndMessagesConsolePage.getResults().trim(), "No such command, try 'help'.", "Results are not as expected");

        LOG.info("Step 4: Send help command");
        modelAndMessagesConsolePage.executeCommand("help");
        assertTrue(modelAndMessagesConsolePage.getResults().contains("Meta commands"), "Meta commands is not present in the result");
        assertTrue(modelAndMessagesConsolePage.getResults().contains("Quit / Exit"), "Quit / Exit commands is not present in the result");
        assertTrue(modelAndMessagesConsolePage.getResults().contains("General Repo Admin Commands"),
            "General Repo Admin Commands is not present in the result");
        assertTrue(modelAndMessagesConsolePage.getResults().contains("Model Admin Commands"), "Model Admin Commands is not present in the result");
        assertTrue(modelAndMessagesConsolePage.getResults().contains("Message Admin Commands"), "Message Admin Commands is not present in the result");
    }
}
