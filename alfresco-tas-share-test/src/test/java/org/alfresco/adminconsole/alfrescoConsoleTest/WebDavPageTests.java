package org.alfresco.adminconsole.alfrescoConsoleTest;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.adminconsole.consoles.WebDavConsolePages;
import org.alfresco.share.BaseTest;
import org.alfresco.utility.model.TestGroup;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/1/2017.
 */
@Slf4j
public class WebDavPageTests extends BaseTest
{
    private WebDavConsolePages webDavConsolePages;

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void webDavPageTest()
    {
        webDavConsolePages = new WebDavConsolePages(webDriver);
        webDavConsolePages.navigate();
        webDavConsolePages.assertPageHeaderRootIsCorrect();
        webDavConsolePages.assertDirectoryIsDisplayed("Shared");
        webDavConsolePages.assertDirectoryIsDisplayed("Imap Attachments");
        webDavConsolePages.assertDirectoryIsDisplayed("Guest Home");
        webDavConsolePages.assertDirectoryIsDisplayed("User Homes");
        webDavConsolePages.assertDirectoryIsDisplayed("Sites");
        webDavConsolePages.assertDirectoryIsDisplayed("Data Dictionary");
        webDavConsolePages.assertDirectoryIsDisplayed("IMAP Home");
        webDavConsolePages.clickSharedLink();
        webDavConsolePages.assertPageHeaderForDirectoryIsCorrect("Shared");
        webDavConsolePages.assertUpALevelLinkIsDisplayed();
        webDavConsolePages.clickUpToLevel();
        webDavConsolePages.assertPageHeaderRootIsCorrect();
        webDavConsolePages.assertUpALevelLinkIsNotDisplayed();
    }
}
