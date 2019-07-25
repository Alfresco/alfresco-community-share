package org.alfresco.share.alfrescoConsoleTest;

import org.alfresco.po.alfrescoconsoles.WebDavConsolePage;
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
public class WebDavPageTests extends ContextAwareWebTest
{
    @Autowired
    WebDavConsolePage webDavConsolePage;

    SoftAssert softAssert = new SoftAssert();
    private String userName = "webDavUser" + RandomData.getRandomAlphanumeric();

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + "@test.com", "Web", "Dav");
    }

    @AfterClass (alwaysRun = true)
    public void cleanupTest()
    {
        userService.delete(adminUser, adminPassword, userName);
    }

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void webDavPageTest()
    {
        LOG.info("Step 1: Navigate to WebDav page");
        webDavConsolePage.setBrowser(getBrowser());
        webDavConsolePage.navigate();
        softAssert.assertEquals(webDavConsolePage.getPageHeaderText(), "Directory listing for /", "webDav console page does not have the expected text ");
        LOG.info("Step 2: Check directories displayed");
        softAssert.assertTrue(webDavConsolePage.isDirectoryPresent("Shared"), "Shared directory is not present");
        softAssert.assertTrue(webDavConsolePage.isDirectoryPresent("Imap Attachments"), "Imap Attachments directory is not present");
        softAssert.assertTrue(webDavConsolePage.isDirectoryPresent("Guest Home"), "Guest Home directory is not present");
        softAssert.assertTrue(webDavConsolePage.isDirectoryPresent("User Homes"), "User Homes directory is not present");
        softAssert.assertTrue(webDavConsolePage.isDirectoryPresent("Sites"), "Sites directory is not present");
        softAssert.assertTrue(webDavConsolePage.isDirectoryPresent("Data Dictionary"), "Data Dictionary directory is not present");
        softAssert.assertTrue(webDavConsolePage.isDirectoryPresent("IMAP Home"), "IMAP Home directory is not present");
        LOG.info("Step 3: Access directory");
        webDavConsolePage.clickSharedLink();
        softAssert.assertEquals(webDavConsolePage.getPageHeaderText(), "Directory listing for /Shared", "Directory listing for Shared is not displayed");
        softAssert.assertTrue(webDavConsolePage.isUpALevelLinkDisplayed(), "[Up a level] is not displayed");
        LOG.info("Step 4: Click Up to level");
        webDavConsolePage.clickUpToLevel();
        softAssert.assertEquals(webDavConsolePage.getPageHeaderText(), "Directory listing for /", "webdav console main page is not displayed");
        softAssert.assertFalse(webDavConsolePage.isUpALevelLinkDisplayed(), "Up a level link os still dispayed");
        softAssert.assertAll();
    }
}
