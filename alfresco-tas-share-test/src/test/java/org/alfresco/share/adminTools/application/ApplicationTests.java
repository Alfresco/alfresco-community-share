package org.alfresco.share.adminTools.application;

import org.alfresco.po.enums.Theme;
import org.alfresco.po.share.user.admin.adminTools.ApplicationPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ApplicationTests extends BaseTest
{
    private ApplicationPage applicationPage;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest()
    {
        applicationPage = new ApplicationPage(webDriver);
        authenticateUsingLoginPage(getAdminUser());
    }

    @TestRail (id = "C9292")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addAndResetNewLogo()
    {
        applicationPage.navigate();
        applicationPage.uploadImage()
            .assertDefaultAlfrescoImageIsNotDisplayed()
            .resetImageToDefault()
            .assertDefaultAlfrescoImageIsDisplayed();
    }

    @TestRail (id = "C9281")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void changeTheme()
    {
        applicationPage.navigate();
        try
        {
            applicationPage.selectTheme(Theme.YELLOW)
                .assertThemeOptionIsSelected(Theme.YELLOW)
                .clickApply()
                .assertBodyContainsTheme(Theme.YELLOW);
        }
        finally
        {
            applicationPage.navigate();
            applicationPage.selectTheme(Theme.LIGHT).clickApply();
        }
    }

    @TestRail (id = "C299219")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void checkText()
    {
        applicationPage.navigate();
        Assert.assertEquals(applicationPage.checkText(), language.translate("aplicationPage.info"));
    }
}