package org.alfresco.share.adminTools.application;

import org.alfresco.po.share.Theme;
import org.alfresco.po.share.user.admin.adminTools.ApplicationPage;
import org.alfresco.share.BaseShareWebTests;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ApplicationTests extends BaseShareWebTests
{
    private ApplicationPage applicationPage;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest()
    {
        applicationPage = new ApplicationPage(browser);

        setupAuthenticatedSession(getAdminUser());
        applicationPage.navigate();
    }

    @TestRail (id = "C9292")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addAndResetNewLogo()
    {
        LOG.info("Step 1: Upload a new logo image in the 'Application' page.");
        applicationPage.uploadImage();
        LOG.info("Step 2: Verify the new logo image was uploaded successfully.");
        Assert.assertFalse(applicationPage.isAlfrescoDefaultImageDisplayed(), "Alfresco default image is displayed!");
        LOG.info("Step 3: Reset the new image to the default one.");
        applicationPage.resetImageToDefault();
        LOG.info("Step 4: Verify the new image was removed successfully.");
        Assert.assertTrue(applicationPage.isAlfrescoDefaultImageDisplayed(), "Alfresco default image is not displayed!");
    }

    @TestRail (id = "C9281")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void changeTheme()
    {
        try
        {
            applicationPage.selectTheme(Theme.YELLOW)
                .assertThemeOptionIsSelected(Theme.YELLOW)
                .assertBodyContainsTheme(Theme.YELLOW);
        }
        finally
        {
            applicationPage.selectTheme(Theme.LIGHT);
        }
    }

    @TestRail (id = "C299219")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void checkText()
    {
        Assert.assertEquals(applicationPage.checkText(), language.translate("aplicationPage.info"));
    }
}