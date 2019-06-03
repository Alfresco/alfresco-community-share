package org.alfresco.share.alfrescoConsoleTest;

import org.alfresco.po.alfrescoconsoles.WebScriptsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/1/2017.
 */
public class WebScriptsPageTests extends ContextAwareWebTest
{
    @Autowired
    WebScriptsPage webScriptsPage;

    private String userName = "webScriptsUser" + RandomData.getRandomAlphanumeric();

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + "@test.com", "web", "scripts");
    }

    @AfterClass
    public void testCleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
    }

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void checkWebScriptsPage()
    {
        LOG.info("Step 1: Navigate to Webscripts page");
        webScriptsPage.setBrowser(getBrowser());
        webScriptsPage.navigate();
        Assert.assertEquals(webScriptsPage.getPageTitle(), "Web Scripts Home", "Page title is not as expected");
    }
}

