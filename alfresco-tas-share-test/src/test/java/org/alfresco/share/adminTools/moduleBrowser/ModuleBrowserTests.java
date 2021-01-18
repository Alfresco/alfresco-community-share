package org.alfresco.share.adminTools.moduleBrowser;

import org.alfresco.po.share.user.admin.adminTools.ModuleBrowserPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ModuleBrowserTests extends BaseTest
{
    private ModuleBrowserPage moduleBrowserPage;

    private UserModel user;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        moduleBrowserPage = new ModuleBrowserPage(webDriver);

        setupAuthenticatedSession(getAdminUser());
    }

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
    }

    @TestRail (id = "C9498")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void accessModuleBrowserPage()
    {
        moduleBrowserPage.navigate().assertModuleBrowserPageIsOpened();
        setupAuthenticatedSession(user);
        userDashboardPage.navigate(user);
        toolbar.assertAdminToolsIsNotDisplayed();
        webDriver.get().manage().deleteAllCookies();
    }

    @TestRail (id = "C9499")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifyTheAccessModuleBrowserPageInfo()
    {
        moduleBrowserPage.navigate();
        moduleBrowserPage.assertModuleTableHeadersAreDisplayed()
            .assertGoogleDocsModuleIsPresent();
    }

    @AfterClass (alwaysRun = true)
    public void afterClass()
    {
        deleteUsersIfNotNull(user);
    }
}
