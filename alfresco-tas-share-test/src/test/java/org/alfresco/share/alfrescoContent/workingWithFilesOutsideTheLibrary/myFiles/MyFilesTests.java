package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyFilesTests extends ContextAwareWebTest
{
    @Autowired private MyFilesPage myFilesPage;

    @Autowired private CreateContent create;
    
    @Autowired private UserDashboardPage userDashboard;
    
    @Autowired private Toolbar toolbar;
    
    private final String user = "C7648TestUser" + DataUtil.getUniqueIdentifier();
    private final String user1 = "C7648TestUser1" + DataUtil.getUniqueIdentifier();
    private final String siteName = "C7658SiteName"+DataUtil.getUniqueIdentifier();
    private final String description ="C7658SiteDescription" + DataUtil.getUniqueIdentifier();
    private final String C7648name ="C7648 name";
    private final String C7648title = "C7648 title";
    private final String C7648content = "C7648 content";
    private final String C7648description = "C7648 description";
    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1, user1);
        setupAuthenticatedSession(user, password);
    }
    
    @TestRail (id ="C7648")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void myFilesContentAvailability()
    {
        LOG.info("Step 1: While logged in with user create Plain Text document in My Files");
        myFilesPage.navigate();
        
        myFilesPage.clickCreateButton();
        create.clickPlainTextButton();
        create.sendInputForName(C7648name);
        create.sendInputForContent(C7648content);
        create.sendInputForTitle(C7648title);
        create.sendInputForDescription(C7648description);
        create.clickCreateButton();
        myFilesPage.navigate();
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(C7648name), "C7648 name is not displayed in My Files");
        userService.logout();
        
        LOG.info("Step 2: Login with user1 and check that the file create by user is not visible in My Files");
        setupAuthenticatedSession(user1, password);
        myFilesPage.navigate();
        Assert.assertFalse(myFilesPage.isContentNameDisplayed(C7648name), "C7648 name is displayed in My Files");
    }
    
    @TestRail(id ="C7658")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void verifyPresenceOfMyFilesInHeaderBar()
    {
        setupAuthenticatedSession(user, password);
        userDashboard.navigate(siteName);
        
        LOG.info("Step 1: Check that the My Files link is available in the toolbar");
        Assert.assertTrue(toolbar.isMyFilesLinkDisplayed(), "The My Files link is not available in Toolbar");
        
        LOG.info("Step 2: Access the My Files via link in toolbar");
        toolbar.clickMyFilesInToolbar();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files", "User is not redirected to the repository page");     
    }
}