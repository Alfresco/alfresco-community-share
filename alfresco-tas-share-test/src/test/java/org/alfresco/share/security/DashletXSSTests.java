package org.alfresco.share.security;

import static org.testng.Assert.assertTrue;

import org.alfresco.common.DataProviderClass;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.EnterFeedURLPopUp;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.po.share.user.CustomizeUserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class DashletXSSTests extends ContextAwareWebTest
{
    //@Autowired
    private CustomizeUserDashboardPage customizeUserDashboardPage;

    //@Autowired
    private RssFeedDashlet rssFeedDashlet;

    //@Autowired
    private EnterFeedURLPopUp enterFeedURLPopUp;

    private String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private String testUser = "TestUser_" + uniqueIdentifier;

    @BeforeClass (alwaysRun = true)
    public void beforeClass()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "testUser_firstName", "testUser_lastName");
        setupAuthenticatedSession(testUser, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, testUser);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C286547")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void configuringRssFeedWithXssUrl(String XSSUrl)
    {
        LOG.info("STEP 1: Any user logged in Share.");

        LOG.info("STEP 2: Add 'RSS Feed' dashlet to My Dashboard.");
        addRssFeedDashletToDashboard();

        LOG.info("STEP 3: Press Configure icon on 'RSS Feed' daslet.");
        rssFeedDashlet.configureDashlet();
        assertTrue(enterFeedURLPopUp.isEnterFeedURLPopUpDisplayed(), "'Enter Feed URL PopUp' couldn't be opened.");

        LOG.info("STEP 4: Enter into URL field the next XSS text: '" + XSSUrl + "'.");
        enterFeedURLPopUp.setUrlValue(XSSUrl);

        LOG.info("STEP 5: Press 'OK' button;");
        enterFeedURLPopUp.clickOkButtonSimple();
        assertTrue(enterFeedURLPopUp.isEnterFeedURLPopUpDisplayed(), "'Enter Feed URL PopUp' is not opened anymore.");
        assertTrue(enterFeedURLPopUp.isUrlErrorMessageDisplayed(), "Error message is not displayed");

        LOG.info("Close the PopUp.");
        enterFeedURLPopUp.clickClose();
    }


    /**
     * Add 'RSS Feed' dashlet to user dashboard if it is not already displayed
     * And then check if it was successfully added.
     */
    private void addRssFeedDashletToDashboard()
    {
        if (!rssFeedDashlet.isDashletDisplayed(DashletHelpIcon.RSS_FEED))
        {
            userService.addDashlet(testUser, password, UserDashlet.RSS_FEED, DashletLayout.THREE_COLUMNS, 3, 1);
        }
        Assert.assertTrue(rssFeedDashlet.isDashletDisplayed(DashletHelpIcon.RSS_FEED), "'RSS Feed' ('Alfresco Blog') dashlet is not displayed in user's dashboard.");
    }
}
