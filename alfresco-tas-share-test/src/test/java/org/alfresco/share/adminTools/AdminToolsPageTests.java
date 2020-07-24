package org.alfresco.share.adminTools;

import org.alfresco.po.share.SystemErrorPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/3/2017.
 */
public class AdminToolsPageTests extends ContextAwareWebTest
{
    @Autowired
    private AdminToolsPage adminToolsPage;

    @Autowired
    private SystemErrorPage systemErrorPage;

    private UserModel user;

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
    }

    @AfterClass (alwaysRun = true)
    public void cleanUp()
    {
        removeUserFromAlfresco(user);
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.ADMIN_TOOLS, "Acceptance" })
    public void adminToolsAccessTest()
    {
        LOG.info("Step 1: Authenticate as non-admin user and check Admin Tools page is not available");
        setupAuthenticatedSession(user);
        getBrowser().navigate().to(properties.getShareUrl() + "/page/console/admin-console/application");
        systemErrorPage.assertSomethingIsWrongWithThePageMessageIsDisplayed();

        cleanupAuthenticatedSession();
        LOG.info("Step 2: Authenticate as admin and check that Admin Tools page is available");
        setupAuthenticatedSession(getAdminUser());
        adminToolsPage.navigate();
        adminToolsPage.assertAdminToolsPageIsOpened();
    }
}
