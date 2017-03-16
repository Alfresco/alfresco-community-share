package org.alfresco.share.alfrescoContent.documentLibrary;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ExploringTheLibraryCategoriesTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DocumentsFilters filters;

    private final String user = "C6910User" + DataUtil.getUniqueIdentifier();
    private final String description = "C6910SiteDescription" + DataUtil.getUniqueIdentifier();
    private final String siteName = "C6910SiteName" + DataUtil.getUniqueIdentifier();

    @BeforeClass

    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C6910")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})

    public void verifyCategoryRootTreeNodes()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Verify Categories section");
        Assert.assertTrue(filters.isCategorisFilterDisplayed(), "Categories filter is not displayed");
        Assert.assertTrue(filters.isCategoriesRootDisplayed(), "Categories Root is not displayed.");

        LOG.info("Step 2: Click Categories Root");
        filters.clickCategoriesRoot();
        getBrowser().waitInSeconds(1);
        Assert.assertTrue(filters.isCategoryDisplayed("Languages"), "Languages category is not displayed");
        Assert.assertTrue(filters.isCategoryDisplayed("Regions"), "Regions category is not displayed");
        Assert.assertTrue(filters.isCategoryDisplayed("Software Document Classification"), "Software Document Classification category is not displayed");
        Assert.assertTrue(filters.isCategoryDisplayed("Tags"), "Tags category is not displayed");
    }
    
    @TestRail(id="C10595")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    
    public void expandColapseNodeInCategoriesTree()
    {
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.renderedPage();
        
        LOG.info("Step 1: Click on Category Root");
        filters.clickCategoryRootIcon();
        getBrowser().waitInSeconds(1);
        Assert.assertTrue(filters.isCategoryDisplayed("Languages"), "Languages category is not displayed");
        Assert.assertTrue(filters.isCategoryDisplayed("Regions"), "Regions category is not displayed");
        Assert.assertTrue(filters.isCategoryDisplayed("Software Document Classification"), "Software Document Classification category is not displayed");
        Assert.assertTrue(filters.isCategoryDisplayed("Tags"), "Tags category is not displayed");
        
        LOG.info("Step 2: Click on Category Root again to collapse folders");
        filters.clickCategoryRootIcon();
        getBrowser().waitInSeconds(1);
        Assert.assertFalse(filters.isCategoryDisplayed("Languages"), "Languages category is displayed");
        Assert.assertFalse(filters.isCategoryDisplayed("Regions"), "Regions category is displayed");
        Assert.assertFalse(filters.isCategoryDisplayed("Software Document Classification"), "Software Document Classification category is displayed");
        Assert.assertFalse(filters.isCategoryDisplayed("Tags"), "Tags category is displayed");
    }
}
