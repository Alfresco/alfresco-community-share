package org.alfresco.share.site;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.share.dashlet.SiteProfileDashlet;
import org.alfresco.po.share.site.EditSiteDetailsDialog;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */

public class EditSiteDetailsTests extends ContextAwareWebTest
{
    @Autowired
    EditSiteDetailsDialog editSiteDetailsDialog;

    @Autowired
    SiteProfileDashlet siteProfileDashlet;

    private String user = String.format("profileUser%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s",RandomData.getRandomAlphanumeric());
    private String description = String.format("description%s",RandomData.getRandomAlphanumeric());
    private String newSiteName = String.format("New Site Name %s",RandomData.getRandomAlphanumeric());
    private String newDescription = String.format("New description %s",RandomData.getRandomAlphanumeric());

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        siteService.addDashlet(user, password, siteName, DashboardCustomization.SiteDashlet.SITE_PROFILE,
                DashboardCustomization.DashletLayout.TWO_COLUMNS_WIDE_RIGHT, 1, 1);
        setupAuthenticatedSession(user, password);
    }


    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail(id = "C2210")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyEditSiteDetailsForm()
    {
        LOG.info("STEP1: Go to the created site. Click 'Settings' icon -> 'Edit Site Details'");
        editSiteDetailsDialog.navigateToDialog(siteName);

        LOG.info("STEP2: Verify the items from 'Edit Site Details' form");
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

        LOG.info("STEP3: Verify the description for the visibility options");
        assertEquals(editSiteDetailsDialog.getPublicVisibilityDescription(), language.translate("siteDetails.publicVisibilityDescription"),
                "Public option has correct description");
        assertEquals(editSiteDetailsDialog.getModeratedVisibilityDescription(), language.translate("siteDetails.moderatedVisibilityDescription"),
                "Moderated option has correct description");
        assertEquals(editSiteDetailsDialog.getPrivateVisibilityDescription(), language.translate("siteDetails.privateVisibilityDescription"),
                "Private option has correct description");
    }

    @TestRail(id = "C2211")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelEditSiteDetails()
    {
        LOG.info("STEP1: Go to the created site. Click 'Settings' icon -> 'Edit Site Details'");
        editSiteDetailsDialog.navigateToDialog(siteName);

        LOG.info("STEP2: Type new name and description for the site");
        editSiteDetailsDialog.typeDetails(newSiteName, newDescription);
        assertEquals(editSiteDetailsDialog.getTitleInputText(), newSiteName, "The new site title is filled in.");

        LOG.info("STEP3: Select \"Private\" visibility for the site");
        editSiteDetailsDialog.selectPrivateVisibility();
        assertEquals(editSiteDetailsDialog.isPrivateVisibilitySelected(), true, "Private visibility is selected.");

        LOG.info("STEP4: Click \"Cancel\" button");
        editSiteDetailsDialog.clickCancelButton();
        assertEquals(siteProfileDashlet.getWelcomeMessageText(), "Welcome to " + siteName, "Site name is not updated.");
        assertEquals(siteProfileDashlet.getSiteDescription(description).getText(), description, "Description is not updated.");
        assertEquals(siteProfileDashlet.getSiteVisibility(language.translate("siteProfile.PublicVisibility")).getText()
                .equals(language.translate("siteProfile.PublicVisibility")), true, "Visibility is not updated.");
    }
}