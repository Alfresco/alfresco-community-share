package org.alfresco.share.adminTools.application;

import org.alfresco.po.share.Theme;
import org.alfresco.po.share.user.admin.adminTools.ApplicationPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * UI tests for Admin Tools > Application page
 */
public class ApplicationTests extends ContextAwareWebTest
{
    @Autowired
    private ApplicationPage applicationPage;

    @BeforeClass (alwaysRun = true)
    public void beforeClass()
    {
        setupAuthenticatedSession(getAdminUser());
        applicationPage.navigate();
    }

    @AfterClass (alwaysRun = true)
    public void afterClass()
    {
        applicationPage.selectTheme(Theme.LIGHT);
        if (!applicationPage.isAlfrescoDefaultImageDisplayed())
        {
            applicationPage.resetImageToDefault();
        }
        cleanupAuthenticatedSession();
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
        applicationPage.selectTheme(Theme.YELLOW)
            .assertThemeOptionIsSelected(Theme.YELLOW)
            .assertBodyContainsTheme(Theme.YELLOW);
    }

    @TestRail (id = "C299219")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void checkText()
    {
        Assert.assertEquals(applicationPage.checkText(), language.translate("aplicationPage.info"));
    }
}