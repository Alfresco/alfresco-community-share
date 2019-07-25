package org.alfresco.share.site;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.SiteMembersDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by P3700681 on 6/19/2018.
 */
public class TestsFromBugs extends ContextAwareWebTest
{
    String siteName = "siteName" + RandomData.getRandomAlphanumeric();

    @Autowired
    SiteMembersDashlet siteMembersDashlet;
    @Autowired
    SiteDashboardPage siteDashboardPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        siteService.create(adminUser, adminPassword, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        for (int i = 0; i < 101; i++)
        {
            String userName = "user" + RandomData.getRandomAlphanumeric();
            userService.create(adminUser, adminPassword, userName, password, userName + domain, "fName", "lName");
            userService.createSiteMember(adminUser, adminPassword, userName, siteName, "SiteManager");
        }
        setupAuthenticatedSession(adminUser, adminPassword);
    }

    @TestRail (id = "C277721")
    @Test (groups = { TestGroup.SHARE, TestGroup.SITES })
    public void moreThan100MembersAddedToSiteTest()
    {
        siteDashboardPage.navigate(siteName);
        LOG.info("Step 1: Check the pagination text is correct when more than 100 site members are added");
        Assert.assertEquals(siteMembersDashlet.getPaginationText(), "Showing the first 100 members", "Text is not correct");
        Assert.assertTrue(siteMembersDashlet.getPaginationText().contains("Showing the first 100 members"), "Text is not correct");
        Assert.assertFalse(siteMembersDashlet.getPaginationText().contains("Shown the first 100 members"), "Text is not correct");
    }
}
