package org.alfresco.share.alfrescoContent.documentLibrary;

import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ExploringTheLibraryCategoriesTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DocumentsFilters filters;

    private final String user = String.format("C6910User%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("C6910SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("C6910SiteName%s", RandomData.getRandomAlphanumeric());

    @BeforeClass(alwaysRun = true)

    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C6910")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})

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
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void expandCollapseNodeInCategoriesTree()
    {
        documentLibraryPage.navigate(siteName);
        
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
