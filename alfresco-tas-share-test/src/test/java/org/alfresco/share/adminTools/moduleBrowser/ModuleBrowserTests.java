package org.alfresco.share.adminTools.moduleBrowser;

import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.admin.adminTools.ModuleBrowserPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ModuleBrowserTests extends BaseTest
{
    private ModuleBrowserPage moduleBrowserPage;
    private Toolbar toolbar;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        moduleBrowserPage = new ModuleBrowserPage(webDriver);
        toolbar = new Toolbar(webDriver);

        user.set(dataUser.usingAdmin().createRandomTestUser());
        authenticateUsingLoginPage(getAdminUser());
    }

    @TestRail (id = "C9498")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void accessModuleBrowserPage()
    {
        moduleBrowserPage.navigate().assertModuleBrowserPageIsOpened();
        authenticateUsingCookies(user.get());
        userDashboardPage.navigate(user.get());
        toolbar.assertAdminToolsIsNotDisplayed();
        webDriver.get().manage().deleteAllCookies();
    }

    @AfterClass (alwaysRun = true)
    public void afterClass()
    {
        deleteUsersIfNotNull(user.get());
    }
}
