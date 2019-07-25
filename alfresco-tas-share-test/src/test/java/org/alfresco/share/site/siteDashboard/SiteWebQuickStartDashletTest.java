package org.alfresco.share.site.siteDashboard;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.data.RandomData;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;

/**
 * Created by Mirela Tifui on 12/11/2017.
 */
public class SiteWebQuickStartDashletTest extends ContextAwareWebTest
{
    @Autowired
    SiteWebQuickStartDashlet siteWebQuickStartDashlet;

    private String userName = "testUser" + RandomData.getRandomAlphanumeric();
    private String siteName = "siteName" + RandomData.getRandomAlphanumeric();

    @BeforeClass
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "WebQuickStart", "User");
        siteService.create(userName, password, siteName, domain, "test site", SiteService.Visibility.PUBLIC);
    }
}
