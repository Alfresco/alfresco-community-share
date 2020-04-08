package org.alfresco.share.adminTools.application;

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
 * @author Razvan.Dorobantu
 */
public class ApplicationTests extends ContextAwareWebTest
{
    @Autowired
    ApplicationPage applicationPage;

    @BeforeClass (alwaysRun = true)
    public void beforeClass()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        applicationPage.navigate();
    }

    @AfterClass
    public void afterClass()
    {
        applicationPage.selectTheme(ApplicationPage.Theme.BLUE_THEME);
        if (!applicationPage.isAlfrescoDefaultImageDisplayed())
            applicationPage.resetImageToDefault();
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C9292")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, "tobefixed" })
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
        LOG.info("Step 4: Verify the new image was removed successfully.");
        Assert.assertTrue(applicationPage.isAlfrescoDefaultImageDisplayed(), "Alfresco default image is displayed.");
    }

    @TestRail (id = "C9281")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, "tobefixed" })
    public void changeTheme()
    {
        LOG.info("Step 1: Select a new theme in the 'Application' page.");
        applicationPage.selectTheme(ApplicationPage.Theme.YELLOW_THEME);
        LOG.info("Step 2: Verify the new theme was successfully saved.");
        Assert.assertTrue(applicationPage.isThemeOptionSelected(ApplicationPage.Theme.YELLOW_THEME), "New theme is not selected");
        applicationPage.refresh();
        applicationPage.renderedPage();
        Assert.assertTrue(applicationPage.doesBodyContainTheme(ApplicationPage.Theme.YELLOW_THEME), "New theme is not in body");
    }

    @TestRail (id = "C299219")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, "tobefixed" })
    //this will fail on certain builds, because the text is changed for various versions of 6.n. we need to split tse into branches for each release
    public void checkText()
    {
        Assert.assertEquals(applicationPage.checkText(),
            "You can find further administration tools in the Repository Admin Console, find out more information in the Alfresco Documentation.");

    }

}