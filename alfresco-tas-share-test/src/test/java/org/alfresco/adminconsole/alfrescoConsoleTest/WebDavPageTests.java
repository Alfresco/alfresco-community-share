package org.alfresco.adminconsole.alfrescoConsoleTest;

import org.alfresco.po.adminconsole.consoles.WebDavConsolePage;
import org.alfresco.adminconsole.ContextAwareWebAdminConsoleTest;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/1/2017.
 */
public class WebDavPageTests extends ContextAwareWebAdminConsoleTest
{
    @Autowired
    private WebDavConsolePage webDavConsolePage;

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void webDavPageTest()
    {
        webDavConsolePage.navigate()
            .assertPageHeaderRootIsCorrect()
            .assertDirectoryIsDisplayed("Shared")
            .assertDirectoryIsDisplayed("Imap Attachments")
            .assertDirectoryIsDisplayed("Guest Home")
            .assertDirectoryIsDisplayed("User Homes")
            .assertDirectoryIsDisplayed("Sites")
            .assertDirectoryIsDisplayed("Data Dictionary")
            .assertDirectoryIsDisplayed("IMAP Home")
                .clickSharedLink()
                    .assertPageHeaderForDirectoryIsCorrect("Shared")
                    .assertUpALevelLinkIsDisplayed()
                .clickUpToLevel()
                    .assertPageHeaderRootIsCorrect()
                    .assertUpALevelLinkIsNotDisplayed();
    }
}
