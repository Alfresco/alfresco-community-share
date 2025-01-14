package org.alfresco.adminconsole.alfrescoConsoleTest;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.adminconsole.consoles.ModelAndMessagesConsolePages;
import org.alfresco.share.BaseTest;
import org.alfresco.utility.model.TestGroup;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/1/2017.
 */
@Slf4j
public class ModelAndMessagesConsoleTests extends BaseTest
{
    private ModelAndMessagesConsolePages modelAndMessagesConsolePages;

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void modelAndMessagesConsoleTest()
    {
        modelAndMessagesConsolePages = new ModelAndMessagesConsolePages(webDriver);
        log.info("Step 1: Navigate to Model and messages console");
        modelAndMessagesConsolePages.navigate();
        modelAndMessagesConsolePages.assertPageTitleIs(language.translate("modelAndMessages.PageTitle"));
        modelAndMessagesConsolePages.assertCommandFieldIsDisplayed();
        modelAndMessagesConsolePages.assertExecuteButtonIsDisplayed();
        modelAndMessagesConsolePages.executeCommand("");
        modelAndMessagesConsolePages.assertResultIs(language.translate("admin.consoles.noSuchCommand"));
        modelAndMessagesConsolePages.executeCommand("help");
        modelAndMessagesConsolePages.assertHelpCommandIsCorrect();
    }
}
