package org.alfresco.share.security;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.DataProviderClass;

import org.alfresco.po.share.dashlet.ConfigureWebViewDashletPopUp;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.WebViewDashlet;
import org.alfresco.po.share.site.EditSiteDetails;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.CustomizeUserDashboardPage;
import org.alfresco.po.share.user.UserDashboardPage;

import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Slf4j
public class DashboardXSSTests extends BaseTest
{
    CustomizeUserDashboardPage customizeUserDashboard;
    SiteDashboardPage siteDashboardPage;
    EditSiteDetails editSiteDetailsDialog;
    private WebViewDashlet webViewDashlet;
    private ConfigureWebViewDashletPopUp configureWebViewDashletPopUp;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();


    @BeforeMethod(alwaysRun = true)
    public void preConditions()
    {
        log.info("PreCondition: Creating a TestUser1");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a Random Site");
        site.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        webViewDashlet = new WebViewDashlet(webDriver);
        configureWebViewDashletPopUp = new ConfigureWebViewDashletPopUp(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        customizeUserDashboard = new CustomizeUserDashboardPage(webDriver);
        userDashboardPage = new UserDashboardPage(webDriver);
        editSiteDetailsDialog = new EditSiteDetails(webDriver);

        log.info("Precondition 1: Any user logged in Share.");
        authenticateUsingLoginPage(user1.get());
        addWebViewDashletToDashboard();
    }


    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user1.get());
        deleteSitesIfNotNull(site.get());
        deleteAllCookiesIfNotNull();
    }

    @TestRail (id = "C286554")
    @Test(groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void configureWebViewForUserDashboard(String XSSString)
    {
        log.info("Precondition 2: Add 'Web View' to user dashboard.");

        log.info("STEP 1: Click Configure icon on Web View dashlet.");
        webViewDashlet.clickConfigureDashlet();
        assertTrue(configureWebViewDashletPopUp.isConfigureWebViewDashletPopUpDisplayed(), "'Configure Web View Dashlet' PopUp could not be opened.");

        log.info("STEP 2: Enter into Link Title field XSS string, try the next cases: '" + XSSString + "'.");
        configureWebViewDashletPopUp.setLinkTitleField(XSSString);

        log.info("STEP 3: Enter into \"URL\" field XSS string, try the next cases: '" + XSSString + "'.");
        configureWebViewDashletPopUp.setUrlField(XSSString);

        log.info("STEP 4: Click OK button;");
        configureWebViewDashletPopUp.clickOkButtonSimple();
        assertTrue(configureWebViewDashletPopUp.isConfigureWebViewDashletPopUpDisplayed(), "'Configure Web View Dashlet' PopUp is not opened anymore.");
        assertTrue(configureWebViewDashletPopUp.isUrlErrorMessageDisplayed(), "Error message is not displayed");

        log.info("Close the PopUp.");
        configureWebViewDashletPopUp.clickClose();
    }


    @TestRail (id = "C286570")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void editingSiteDetailsWithXSS(String XSSString) {
        log.info("Precondition 2: Create a new site.");

        log.info("STEP 1: Open created site.");
        //Navigation to siteDashboard is performed in navigateToEditSiteDetailsDialog() method

        log.info("STEP 2: Navigate to 'More' menu and select 'Edit Site Details' button.");
        siteDashboardPage.navigateToEditSiteDetailsDialog(site.get().getId());
        assertTrue(editSiteDetailsDialog.isEditSiteDetailsDialogDisplayed(), "'Edit Site Details' dialog could not be opened.");

        log.info("STEP 3: Enter into \"Name \" and \"Description\" fields: '" + XSSString + "'.");
        editSiteDetailsDialog.typeDetails(XSSString, XSSString);
        assertEquals(editSiteDetailsDialog.getTitleInputText(), XSSString, "'Name' input was not filled with '" + XSSString + "'.");

        log.info("STEP 4: Click 'OK' button.");
        editSiteDetailsDialog.clickSaveButton();
        assertTrue(siteDashboardPage.isSiteVisibilityDisplayed(), "Site Dashboard page could not be rendered.");
        assertEquals(siteDashboardPage.getSiteName(), XSSString, "'Site Title' is not '" + XSSString + "' as expected.");

        log.info("STEP 4: Navigate to 'Edit Site Details' PopUp.");
        siteDashboardPage.navigateToEditSiteDetailsDialog(site.get().getId());

        log.info("STEP 5: Check if site 'Name' and 'Description' = '" + XSSString + "'.");
        assertEquals(editSiteDetailsDialog.getTitleInputText(), XSSString, "'Site Name' is not equal to '" + XSSString + "'.");
        assertEquals(editSiteDetailsDialog.getDescriptionInputText(), XSSString, "'Site Description' is not equal to '" + XSSString + "'.");

        log.info("Close 'Edit Site Details' PopUp");
        editSiteDetailsDialog.clickCloseCreateSitePopup();
    }

    /**
     * Add 'Web View' dashlet to user dashboard if it is not already displayed
     * And then check if it was successfully added.
     */
    private void addWebViewDashletToDashboard(){
        customizeUserDashboard.navigate()
            .clickAddDashlet()
            .addDashlet(Dashlets.WEB_VIEW, 1)
            .assertDashletIsAddedInColumn(Dashlets.WEB_VIEW, 1)
            .clickOk();
        userDashboardPage.assertCustomizeUserDashboardIsDisplayed()
            .assertDashletIsAddedInPosition(Dashlets.WEB_VIEW, 1, 3);
    }
}
