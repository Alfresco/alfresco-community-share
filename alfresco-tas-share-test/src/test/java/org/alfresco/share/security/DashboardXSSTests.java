package org.alfresco.share.security;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.common.DataProviderClass;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.ConfigureWebViewDashletPopUp;
import org.alfresco.po.share.dashlet.WebViewDashlet;
import org.alfresco.po.share.site.EditSiteDetailsDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DashboardXSSTests extends ContextAwareWebTest
{
    //@Autowired
    SiteDashboardPage siteDashboardPage;
    @Autowired
    EditSiteDetailsDialog editSiteDetailsDialog;
    //@Autowired
    private WebViewDashlet webViewDashlet;
   // @Autowired
    private ConfigureWebViewDashletPopUp configureWebViewDashletPopUp;
    private String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private String testUser = String.format("TestUser_" + uniqueIdentifier);
    private String siteName = String.format("SiteName" + uniqueIdentifier);


    @BeforeClass (alwaysRun = true)
    public void beforeClass()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "testUser_firstName", "testUser_lastName");
        siteService.create(testUser, password, domain, siteName, siteName, "description", SiteService.Visibility.PUBLIC);

        LOG.info("Precondition 1: Any user logged in Share.");
        setupAuthenticatedSession(testUser, password);
        addWebViewDashletToDashboard();
    }


    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        siteService.delete(testUser, password, siteName);
        cleanupAuthenticatedSession();
        userService.delete(adminUser, adminPassword, testUser);
    }


    @TestRail (id = "C286554")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void configureWebViewForUserDashboard(String XSSString)
    {
        LOG.info("Precondition 2: Add 'Web View' to user dashboard.");

        LOG.info("STEP 1: Click Configure icon on Web View dashlet.");
        webViewDashlet.clickConfigureDashlet();
        assertTrue(configureWebViewDashletPopUp.isConfigureWebViewDashletPopUpDisplayed(), "'Configure Web View Dashlet' PopUp could not be opened.");

        LOG.info("STEP 2: Enter into Link Title field XSS string, try the next cases: '" + XSSString + "'.");
        configureWebViewDashletPopUp.setLinkTitleField(XSSString);

        LOG.info("STEP 3: Enter into \"URL\" field XSS string, try the next cases: '" + XSSString + "'.");
        configureWebViewDashletPopUp.setUrlField(XSSString);

        LOG.info("STEP 4: Click OK button;");
        configureWebViewDashletPopUp.clickOkButtonSimple();
        assertTrue(configureWebViewDashletPopUp.isConfigureWebViewDashletPopUpDisplayed(), "'Configure Web View Dashlet' PopUp is not opened anymore.");
        assertTrue(configureWebViewDashletPopUp.isUrlErrorMessageDisplayed(), "Error message is not displayed");

        LOG.info("Close the PopUp.");
        configureWebViewDashletPopUp.clickClose();
    }


    @TestRail (id = "C286570")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" }, dataProvider = "XSSSecurity", dataProviderClass = DataProviderClass.class)
    public void editingSiteDetailsWithXSS(String XSSString)
    {
        LOG.info("Precondition 2: Create a new site.");

        LOG.info("STEP 1: Open created site.");
        //Navigation to siteDashboard is performed in navigateToEditSiteDetailsDialog() method

        LOG.info("STEP 2: Navigate to 'More' menu and select 'Edit Site Details' button.");
        siteDashboardPage.navigateToEditSiteDetailsDialog(siteName);
        assertTrue(editSiteDetailsDialog.isEditSiteDetailsDialogDisplayed(), "'Edit Site Details' dialog could not be opened.");

        LOG.info("STEP 3: Enter into \"Name \" and \"Description\" fields: '" + XSSString + "'.");
        editSiteDetailsDialog.typeDetails(XSSString, XSSString);
        assertEquals(editSiteDetailsDialog.getTitleInputText(), XSSString, "'Name' input was not filled with '" + XSSString + "'.");

        LOG.info("STEP 4: Click 'OK' button.");
        editSiteDetailsDialog.clickSaveButton();
        assertTrue(siteDashboardPage.isSiteVisibilityDisplayed(), "Site Dashboard page could not be rendered.");
        assertEquals(siteDashboardPage.getSiteName(), XSSString, "'Site Title' is not '" + XSSString + "' as expected.");

        LOG.info("STEP 4: Navigate to 'Edit Site Details' PopUp.");
        siteDashboardPage.navigateToEditSiteDetailsDialog(siteName);

        LOG.info("STEP 5: Check if site 'Name' and 'Description' = '" + XSSString + "'.");
        assertEquals(editSiteDetailsDialog.getTitleInputText(), XSSString, "'Site Name' is not equal to '" + XSSString + "'.");
        assertEquals(editSiteDetailsDialog.getDescriptionInputText(), XSSString, "'Site Description' is not equal to '" + XSSString + "'.");

        LOG.info("Close 'Edit Site Details' PopUp");
        editSiteDetailsDialog.clickCloseCreateSitePopup();
    }

    /**
     * Add 'Web View' dashlet to user dashboard if it is not already displayed
     * And then check if it was successfully added.
     */
    private void addWebViewDashletToDashboard()
    {
        if (!webViewDashlet.isDashletDisplayed(DashletHelpIcon.WEB_VIEW))
        {
            userService.addDashlet(testUser, password, UserDashlet.WEB_VIEW, DashletLayout.THREE_COLUMNS, 3, 1);
        }
        Assert.assertEquals(webViewDashlet.getDashletTitle(), "Web View", "'Web View' dashlet is not displayed in user's dashboard.");
    }

}
