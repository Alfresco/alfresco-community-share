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
        webDavConsolePages.isNameHighlighted("Shared");
        webDavConsolePages.isNameHighlighted("Imap Attachments");
        webDavConsolePages.isNameHighlighted("Guest Home");
        webDavConsolePages.isNameHighlighted("User Homes");
        webDavConsolePages.isNameHighlighted("Sites");
        webDavConsolePages.isNameHighlighted("Data Dictionary");
        webDavConsolePages.isNameHighlighted("IMAP Home");
        webDavConsolePages.clickSharedLink();
        webDavConsolePages.assertPageHeaderForDirectoryIsCorrect("Shared");
        webDavConsolePages.assertUpALevelLinkIsDisplayed();
        webDavConsolePages.clickUpToLevel();
        webDavConsolePages.assertPageHeaderRootIsCorrect();
        webDavConsolePages.assertUpALevelLinkIsNotDisplayed();
    }
}
