package org.alfresco.adminconsole.alfrescoConsoleTest;

import org.alfresco.po.adminconsole.consoles.ModelAndMessagesConsolePage;
import org.alfresco.adminconsole.ContextAwareWebAdminConsoleTest;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/1/2017.
 */
public class ModelAndMessagesConsoleTests extends ContextAwareWebAdminConsoleTest
{
    @Autowired
    private ModelAndMessagesConsolePage modelAndMessagesConsolePage;

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void modelAndMessagesConsoleTest()
    {
        LOG.info("Step 1: Navigate to Model and messages console");
        modelAndMessagesConsolePage.navigate()
            .assertPageTitleIs(language.translate("modelAndMessages.PageTitle"))
            .assertCommandFieldIsDisplayed()
            .assertExecuteButtonIsDisplayed()
            .executeCommand("")
            .assertResultIs(language.translate("admin.consoles.noSuchCommand"))
            .executeCommand("help")
            .assertHelpCommandIsCorrect();
    }
}
