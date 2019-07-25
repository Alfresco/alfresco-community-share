package org.alfresco.share.security;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.DataProviderClass;
import org.alfresco.po.share.site.CreateSiteDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SiteXSSTests extends ContextAwareWebTest
{
    @Autowired
    private CreateSiteDialog createSiteDialog;

    @Autowired
    private SiteDashboardPage siteDashboardPage;

    @Autowired
    private UserDashboardPage userDashboardPage;

    private String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private String testUser = "TestUser_" + uniqueIdentifier;

    @BeforeClass (alwaysRun = true)
    private void beforeClass()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "testUser_firstName", "testUser_lastName");

        LOG.info("Precondition 1: Any user logged in Share.");
        setupAuthenticatedSession(testUser, password);
    }

    @AfterClass (alwaysRun = true)
    private void afterClass()
    {
        cleanupAuthenticatedSession();
        userService.delete(adminUser, adminPassword, testUser);
    }


    @TestRail (id = "C286607")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void creatingSiteWithXSSSiteID(String XSSSiteID)
    {
        LOG.info("STEP 1: Navigate to the Sites drop-down menu and select Create site link;");
        createSiteDialog.navigateByMenuBar();
        assertTrue(createSiteDialog.isSiteIDInputFieldDisplayed(), "'SiteID' field is not displayed.");

        LOG.info("STEP 2: Enter \"test\" in Name field;");
        createSiteDialog.typeInNameInput("test");
        assertEquals(createSiteDialog.getNameInputText(), "test", "Text could not be entered in 'Name' field.");

        LOG.info("STEP 3: Enter into \"Site ID\" field XSS string, try the next cases: '" + XSSSiteID + "'.");
        createSiteDialog.typeInSiteID(XSSSiteID);
        assertEquals(createSiteDialog.getSiteIdInputText(), XSSSiteID, "Text could not be entered in 'Site ID' field.");

        LOG.info("STEP 4: Check that OK button is disabled;");
        assertEquals(createSiteDialog.getCreateButtonState(), "true", "'Create Button' is not disabled.");

        LOG.info("Close PopUp.");
        createSiteDialog.clickCloseXButton();
    }


    @TestRail (id = "C286608")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void creatingSiteWithXSSName(String XSSName)
    {
        String siteId = "test" + RandomData.getRandomAlphanumeric();
        String expectedRelativePath = "share/page/site/" + XSSName + "/dashboard";

        userDashboardPage.navigate(testUser);

        LOG.info("STEP 1: Navigate to the Sites drop-down menu and select Create site link;");
        createSiteDialog.navigateByMenuBar();
        assertTrue(createSiteDialog.isSiteIDInputFieldDisplayed(), "'SiteID' field is not displayed.");

        LOG.info("STEP 2: Enter into \"Name\" field XSS string, try the next cases: '" + XSSName + "'.");
        createSiteDialog.typeInNameInput(XSSName);
        assertEquals(createSiteDialog.getNameInputText(), XSSName, "Text could not be entered in 'Name' field.");

        LOG.info("STEP 3.1: Enter \"test\" in 'Site ID' field;");
        createSiteDialog.typeInSiteID(siteId);
        assertEquals(createSiteDialog.getSiteIdInputText(), siteId, "Text could not be entered in 'SiteID' field.");

        LOG.info("STEP 3.2: Enter 'description' in 'Description' field.");
        createSiteDialog.typeInDescription("description");

        LOG.info("STEP 3.3: Select visibility to 'public'.");
        createSiteDialog.selectPublicVisibility();

        LOG.info("STEP 4: Press 'Create' button;");
        createSiteDialog.clickCreateButton(siteDashboardPage);
        siteDashboardPage.setCurrentSiteName(XSSName);
        assertEquals(siteDashboardPage.getRelativePath(), expectedRelativePath, "User is not redirected to the created site.");
        assertEquals(siteDashboardPage.getSiteName(), XSSName, "'Site Title' is not '" + XSSName + "' as expected.");
    }

    @TestRail (id = "C286609")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void creatingSiteWithXSSDescription(String XSSDescription)
    {
        String siteName = "test" + RandomData.getRandomAlphanumeric();

        userDashboardPage.navigate(testUser);

        LOG.info("STEP 1: Navigate to the Sites drop-down menu and select 'Create site' link;");
        createSiteDialog.navigateByMenuBar();
        assertTrue(createSiteDialog.isDescriptionInputFieldDisplayed(), "'Description' field is not displayed.");

        LOG.info("STEP 2: Enter " + siteName + " in 'Name' field;");
        createSiteDialog.typeInNameInput(siteName);
        assertEquals(createSiteDialog.getNameInputText(), siteName, "Text '" + siteName + "' could not be entered in 'Name' field.");

        LOG.info("STEP 3: Enter " + siteName + " in 'Site ID' field;");
        createSiteDialog.typeInSiteID(siteName);
        assertEquals(createSiteDialog.getSiteIdInputText(), siteName, "Text could not be entered in 'SiteID' field.");

        LOG.info("STEP 4: Select visibility to 'public'.");
        createSiteDialog.selectPublicVisibility();

        LOG.info("STEP 5: Enter XSS text in 'Description' field , try the next cases: '" + XSSDescription + "'.");
        createSiteDialog.typeInDescription(XSSDescription);
        assertEquals(createSiteDialog.getDescriptionInputText(), XSSDescription, "Text '" + XSSDescription + "' could not be entered in 'Description' field.");

        LOG.info("STEP 4: Press 'Create' button;");
        createSiteDialog.clickCreateButton(siteDashboardPage);
        siteDashboardPage.setCurrentSiteName(siteName);
    }
}
