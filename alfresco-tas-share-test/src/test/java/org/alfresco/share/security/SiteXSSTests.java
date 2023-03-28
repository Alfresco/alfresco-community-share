package org.alfresco.share.security;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.common.DataProviderClass;
import org.alfresco.po.share.site.CreateSiteDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Slf4j
public class SiteXSSTests extends BaseTest
{
    private CreateSiteDialog createSiteDialog;
    private SiteDashboardPage siteDashboardPage;
    private UserDashboardPage userDashboardPage;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private String testUser = "TestUser_" + uniqueIdentifier;

    @BeforeMethod(alwaysRun = true)
    private void beforeClass()
    {
        log.info("Precondition 1: Create a new user");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        createSiteDialog = new CreateSiteDialog(webDriver);
        userDashboardPage = new UserDashboardPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);


        log.info("Precondition 2: Any user logged in Share.");
        authenticateUsingLoginPage(user1.get());
    }

    @AfterMethod(alwaysRun = true)
    private void afterClass()
    {
        deleteUsersIfNotNull(user1.get());
    }


    @TestRail (id = "C286607")
    @Test(groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void creatingSiteWithXSSSiteID(String XSSSiteID)
    {
        log.info("STEP 1: Navigate to the Sites drop-down menu and select Create site link;");
        createSiteDialog.navigateByMenuBar();
        assertTrue(createSiteDialog.isSiteIDInputFieldDisplayed(), "'SiteID' field is not displayed.");

        log.info("STEP 2: Enter \"test\" in Name field;");
        createSiteDialog.typeInNameInput("test");
        assertEquals(createSiteDialog.getNameInputText(), "test", "Text could not be entered in 'Name' field.");

        log.info("STEP 3: Enter into \"Site ID\" field XSS string, try the next cases: '" + XSSSiteID + "'.");
        createSiteDialog.typeInSiteID(XSSSiteID);
        assertEquals(createSiteDialog.getSiteIdInputText(), XSSSiteID, "Text could not be entered in 'Site ID' field.");

        log.info("STEP 4: Check that OK button is disabled;");
        assertEquals(createSiteDialog.getCreateButtonState(), "true", "'Create Button' is not disabled.");

        log.info("Close PopUp.");
        createSiteDialog.clickCloseXButton();
    }


    @TestRail (id = "C286608")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void creatingSiteWithXSSName(String XSSName)
    {
        String siteId = "test" + RandomData.getRandomAlphanumeric();
        String expectedRelativePath = "share/page/site/" + XSSName + "/dashboard";

        userDashboardPage.navigate(testUser);

        log.info("STEP 1: Navigate to the Sites drop-down menu and select Create site link;");
        createSiteDialog.navigateByMenuBar();
        assertTrue(createSiteDialog.isSiteIDInputFieldDisplayed(), "'SiteID' field is not displayed.");

        log.info("STEP 2: Enter into \"Name\" field XSS string, try the next cases: '" + XSSName + "'.");
        createSiteDialog.typeInNameInput(XSSName);
        assertEquals(createSiteDialog.getNameInputText(), XSSName, "Text could not be entered in 'Name' field.");

        log.info("STEP 3.1: Enter \"test\" in 'Site ID' field;");
        createSiteDialog.typeInSiteID(siteId);
        assertEquals(createSiteDialog.getSiteIdInputText(), siteId, "Text could not be entered in 'SiteID' field.");

        log.info("STEP 3.2: Enter 'description' in 'Description' field.");
        createSiteDialog.typeInDescription("description");

        log.info("STEP 3.3: Select visibility to 'public'.");
        createSiteDialog.selectPublicVisibility();

        log.info("STEP 4: Press 'Create' button;");
        createSiteDialog.clickCreateButton();
        siteDashboardPage.setCurrentSiteName(XSSName);
        assertEquals(siteDashboardPage.getRelativePath(), expectedRelativePath, "User is not redirected to the created site.");
        assertEquals(siteDashboardPage.getSiteName(), XSSName, "'Site Title' is not '" + XSSName + "' as expected.");
    }

    @TestRail (id = "C286609")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void creatingSiteWithXSSDescription(String XSSDescription)
    {
        String siteName = "test" + RandomData.getRandomAlphanumeric();

        userDashboardPage.navigate(testUser);

        log.info("STEP 1: Navigate to the Sites drop-down menu and select 'Create site' link;");
        createSiteDialog.navigateByMenuBar();
        assertTrue(createSiteDialog.isDescriptionInputFieldDisplayed(), "'Description' field is not displayed.");

        log.info("STEP 2: Enter " + siteName + " in 'Name' field;");
        createSiteDialog.typeInNameInput(siteName);
        assertEquals(createSiteDialog.getNameInputText(), siteName, "Text '" + siteName + "' could not be entered in 'Name' field.");

        log.info("STEP 3: Enter " + siteName + " in 'Site ID' field;");
        createSiteDialog.typeInSiteID(siteName);
        assertEquals(createSiteDialog.getSiteIdInputText(), siteName, "Text could not be entered in 'SiteID' field.");

        log.info("STEP 4: Select visibility to 'public'.");
        createSiteDialog.selectPublicVisibility();

        log.info("STEP 5: Enter XSS text in 'Description' field , try the next cases: '" + XSSDescription + "'.");
        createSiteDialog.typeInDescription(XSSDescription);
        assertEquals(createSiteDialog.getDescriptionInputText(), XSSDescription, "Text '" + XSSDescription + "' could not be entered in 'Description' field.");

        log.info("STEP 4: Press 'Create' button;");
        createSiteDialog.clickCreateButton();
        siteDashboardPage.setCurrentSiteName(siteName);
    }
}
