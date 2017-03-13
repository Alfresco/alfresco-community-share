package org.alfresco.share.adminTools.application;

import org.alfresco.po.share.user.admin.adminTools.ApplicationPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
public class ApplicationTests extends ContextAwareWebTest
{
    @Autowired
    ApplicationPage applicationPage;

    @BeforeClass
    public void beforeClass()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        applicationPage.navigateByMenuBar();
    }

    @AfterClass
    public void afterClass()
    {
        applicationPage.selectTheme(ApplicationPage.Theme.BLUE_THEME);
        if (!applicationPage.isAlfrescoDefaultImageDisplayed())
            applicationPage.resetImageToDefault();
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C9292")
    @Test
    public void addAndResetNewLogo()
    {
        LOG.info("Step 1: Upload a new logo image in the 'Application' page.");
        if (!applicationPage.isAlfrescoDefaultImageDisplayed())
            applicationPage.resetImageToDefault();
        applicationPage.uploadImage();

        LOG.info("Step 2: Verify the new logo image was uploaded successfully.");
        Assert.assertFalse(applicationPage.isAlfrescoDefaultImageDisplayed(), "Alfresco default image is not displayed.");

        LOG.info("Step 3: Reset the new image to the default one.");
        applicationPage.resetImageToDefault();
        getBrowser().waitInSeconds(9);

        LOG.info("Step 4: Verify the new image was removed successfully.");
        Assert.assertTrue(applicationPage.isAlfrescoDefaultImageDisplayed(), "Alfresco default image is displayed.");
    }

    @TestRail(id = "C9281")
    @Test
    public void changeTheme()
    {
        LOG.info("Step 1: Select a new theme in the 'Application' page.");
        applicationPage.selectTheme(ApplicationPage.Theme.YELLOW_THEME);
        getBrowser().waitInSeconds(9);

        LOG.info("Step 2: Verify the new theme was successfully saved.");
        Assert.assertTrue(applicationPage.isThemeOptionSelected(ApplicationPage.Theme.YELLOW_THEME), "New theme is not selected");
        Assert.assertTrue(applicationPage.doesBodyContainTheme(ApplicationPage.Theme.YELLOW_THEME), "New theme is not in body");
    }
}
