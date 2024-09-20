package org.alfresco.share.site;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization;

import org.alfresco.po.share.dashlet.SiteProfileDashlet;
import org.alfresco.po.share.site.EditSiteDetails;
import org.alfresco.po.share.site.SiteDashboardPage;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Laura.Capsa
 */

public class EditSiteDetailsTests extends BaseTest
{
    //@Autowired
    SiteDashboardPage siteDashboardPage;
    EditSiteDetails editSiteDetailsDialog;
    SiteProfileDashlet siteProfileDashlet;
    private String newSiteName = String.format("New Site Name %s", RandomData.getRandomAlphanumeric());
    private String newDescription = String.format("New description %s", RandomData.getRandomAlphanumeric());
    private static final String VISIBILITY_LABEL = "siteProfileDashlet.visibilityLabel";
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteDashboardPage = new SiteDashboardPage(webDriver);
        editSiteDetailsDialog = new EditSiteDetails(webDriver);
        siteProfileDashlet = new SiteProfileDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        addDashlet(user.get(), site.get(), DashboardCustomization.SiteDashlet.SITE_PROFILE, 1, 2);

        authenticateUsingLoginPage(user.get());
    }


    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C2210")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyEditSiteDetailsForm()
    {
        log.info("STEP1: Go to the created site. Click 'Settings' icon -> 'Edit Site Details'");
        siteDashboardPage.navigateToEditSiteDetailsDialog(site.get().getId());

        log.info("STEP2: Verify the items from 'Edit Site Details' form");
        assertTrue(editSiteDetailsDialog.isNameInputDisplayed(), "Name field is displayed");
        assertEquals(editSiteDetailsDialog.getNameLabel(), language.translate("siteDetails.title"), "Name label is correct");

        assertEquals(editSiteDetailsDialog.isDescriptionInputDisplayed(), true, "Description field is displayed");
        assertEquals(editSiteDetailsDialog.getDescriptionLabel(), language.translate("siteDetails.description"), "Description label is correct");

        assertEquals(editSiteDetailsDialog.getVisibilityLabel(), language.translate("siteDetails.visibility"), "Visibility label is correct");

        assertTrue(editSiteDetailsDialog.isPublicVisibilityRadioButtonDisplayed(), "Public option: radio button is displayed");

        assertTrue(editSiteDetailsDialog.isModeratedVisibilityRadioButtonDisplayed(), "Moderated option: radio button is displayed");

        assertTrue(editSiteDetailsDialog.isPrivateVisibilityRadioButtonDisplayed(), "Private option: radio button is displayed");

        assertTrue(editSiteDetailsDialog.isSaveButtonDisplayed(), "Save button is displayed");
        assertTrue(editSiteDetailsDialog.isCancelButtonDisplayed(), "Cancel button is displayed");

        log.info("STEP3: Verify the description for the visibility options");
        assertEquals(editSiteDetailsDialog.getPublicVisibilityDescription(), language.translate("siteDetails.publicVisibilityDescription"),
            "Public option has correct description");
        assertEquals(editSiteDetailsDialog.getModeratedVisibilityDescription(), language.translate("siteDetails.moderatedVisibilityDescription"),
            "Moderated option has correct description");
        assertEquals(editSiteDetailsDialog.getPrivateVisibilityDescription(), language.translate("siteDetails.privateVisibilityDescription"),
            "Private option has correct description");
    }

    @TestRail (id = "C2211")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelEditSiteDetails()
    {
        log.info("STEP1: Go to the created site. Click 'Settings' icon -> 'Edit Site Details'");
        siteDashboardPage.navigateToEditSiteDetailsDialog(site.get().getId());

        log.info("STEP2: Type new name and description for the site");
        editSiteDetailsDialog.typeDetails(newSiteName, newDescription);
        assertEquals(editSiteDetailsDialog.getTitleInputText(), newSiteName, "The new site title is filled in.");

        log.info("STEP3: Select \"Private\" visibility for the site");
        editSiteDetailsDialog.selectPrivateVisibility();
        assertEquals(editSiteDetailsDialog.isPrivateVisibilitySelected(), true, "Private visibility is selected.");

        log.info("STEP4: Click \"Cancel\" button");
        editSiteDetailsDialog.clickCancelButton();
        siteProfileDashlet.assertSiteWelcomeMessageEquals("Welcome to " + site.get().getId());
        siteProfileDashlet.assertSiteDescriptionEquals(site.get().getDescription());
        siteProfileDashlet.assert_SiteVisibilityEquals(language.translate(VISIBILITY_LABEL)+" ", site.get().getVisibility().name(), 2);
    }
}