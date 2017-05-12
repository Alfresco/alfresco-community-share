package org.alfresco.share.site;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.site.CreateSiteDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class CreateSiteTests extends ContextAwareWebTest
{
    @Autowired
    CreateSiteDialog createSiteDialog;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    UserDashboardPage userDashboardPage;

    String user = String.format("user%s", DataUtil.getUniqueIdentifier());
    String testSiteName = String.format("siteName%s", DataUtil.getUniqueIdentifier());

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        siteService.create(user, password, domain, testSiteName, "description", Site.Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C2103")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyItemsPresentOnForm()
    {
        userDashboardPage.navigate(user);

        LOG.info("STEP1: Open the \"Sites\" menu on the toolbar and click on \"Create Site\"");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Verify the available fields from \"Create Site\" form");
        assertEquals(createSiteDialog.isNameInputFieldDisplayed(), true, "Name field is not displayed.");
        assertEquals(createSiteDialog.getNameFieldLabel(), language.translate("siteDetails.title"), "Name label-");
        assertEquals(createSiteDialog.isTitleMandatory(), true, "Name is not mandatory.");

        assertEquals(createSiteDialog.isSiteIDInputFieldDisplayed(), true, "URL Name field is displayed.");
        assertEquals(createSiteDialog.getSiteIDFieldLabel(), language.translate("siteDetails.urlName"), "URL Name label is correct.");
        assertEquals(createSiteDialog.getSiteIDDescriptionText(), language.translate("siteDetails.urlNameDescription"), "URL name description-");
        assertEquals(createSiteDialog.isSiteIDMandatory(), true, "URL Name is mandatory.");

        assertEquals(createSiteDialog.isDescriptionInputFieldDisplayed(), true, "Description field is not displayed");
        assertEquals(createSiteDialog.getDescriptionLabel(), language.translate("siteDetails.description"), "Description label-");

        assertEquals(createSiteDialog.getVisibilityLabel(), language.translate("siteDetails.visibility"), "Visibility label-");

        assertEquals(createSiteDialog.isPublicVisibilityButtonDisplayed(), "PUBLIC", "Public option: radio button is displayed.");

        assertEquals(createSiteDialog.isModeratedVisibilityButtonDisplayed(), "MODERATED", "Moderated option: radio button is displayed.");

        assertEquals(createSiteDialog.isPrivateVisibilityButtonDisplayed(), "PRIVATE", "Private option: radio button is displayed.");

        LOG.info("STEP3: Verify the available \"Visibility\" options");
        assertEquals(createSiteDialog.getPublicVisibilityDescription(), language.translate("siteDetails.publicVisibilityDescription"),
                "Public option description-");
        assertEquals(createSiteDialog.getModeratedVisibilityDescription(), language.translate("siteDetails.moderatedVisibilityDescription"),
                "Moderated option description-");
        assertEquals(createSiteDialog.getPrivateVisibilityDescription(), language.translate("siteDetails.privateVisibilityDescription"),
                "Private option description-");

        LOG.info("STEP4: Verify the available buttons from \"Create Site\" form");
        assertTrue(createSiteDialog.isCreateButtonDisplayed(), "Save button is displayed.");
        assertTrue(createSiteDialog.isCancelButtonDisplayed(), "Cancel button is displayed.");
        assertEquals(createSiteDialog.isCloseButtonDisplayed(), true, "Close button is displayed.");
    }

    @TestRail(id = "C2104")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void createPublicSiteFromToolbar()
    {
        String siteName = String.format("siteC2104-%s", DataUtil.getUniqueIdentifier());
        String description = String.format("description-C2104-%s", DataUtil.getUniqueIdentifier());
        userDashboardPage.navigate(user);

        LOG.info("STEP1: Open the \"Sites\" menu on the toolbar and click on \"Create Site\"");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter  \"Name\", \"SiteID Name\" and \"Description\" for the site");
        createSiteDialog.typeInNameInput(siteName);
        createSiteDialog.typeInSiteID(siteName);
        createSiteDialog.typeInDescription(description);
       // assertEquals(createSiteDialog.getTitleInputText(), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Select \"Public\" visibility");
        createSiteDialog.selectPublicVisibility();
        assertTrue(createSiteDialog.isPublicVisibilityRadioButtonChecked(), "Public visibility is not selected.");

        LOG.info("STEP4: Click \"Save\" button");
        createSiteDialog.clickCreateButton(siteDashboardPage);
        siteDashboardPage.setCurrentSiteName(siteName);
        String expectedRelativePath = "share/page/site/" + siteName + "/dashboard";
        assertEquals(siteDashboardPage.getRelativePath(), expectedRelativePath, "User is successfully redirected to the created site.");

        LOG.info("STEP5: Check visibility for the site");
        assertEquals(siteDashboardPage.getSiteVisibility(), "Public", "\"Public\" visibility is displayed next to the site name.");
    }

    @TestRail (id = "C43380")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void createSiteWithoutDescription()
    {
        String siteName = String.format("siteName%s", DataUtil.getUniqueIdentifier());
        userDashboardPage.navigate(user);

        LOG.info("STEP1: \"Go to User's dashboard page - \"My sites\" dashlet.\n" + "Click \"Create site\".");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter any \"Name\" for the site");
        createSiteDialog.typeInNameInput(siteName);
        //getBrowser().waitInSeconds(2);
        assertEquals(createSiteDialog.getNameInputText(), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Click \"Create\" button");
        createSiteDialog.clickCreateButton(siteDashboardPage);
        //getBrowser().waitInSeconds(8);
        siteDashboardPage.setCurrentSiteName(siteName);
        String expectedRelativePath = "share/page/site/" + siteName + "/dashboard";
        assertEquals(siteDashboardPage.getRelativePath(), expectedRelativePath, "User is successfully redirected to the created site.");

        LOG.info("STEP4: Check visibility for the site");
        assertEquals(siteDashboardPage.getSiteVisibility(), "Public", "\"Public\" visibility is displayed next to the site name.");
    }

    @TestRail(id = "C2105")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void createModeratedSiteFromToolbar()
    {
        String siteName = String.format("siteC2105-%s", DataUtil.getUniqueIdentifier());
        String description = String.format("description-C2105-%s", DataUtil.getUniqueIdentifier());
        userDashboardPage.navigate(user);

        LOG.info("STEP1: Open the \"Sites\" menu on the toolbar and click on \"Create Site\"");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter any \"Name\", \"URL Name\" and \"Description\" for the site");
        createSiteDialog.typeInNameInput(siteName);
        createSiteDialog.typeInSiteID(siteName);
        createSiteDialog.typeInDescription(description);
        //assertEquals(createSiteDialog.getTitleInputText(siteName), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Select \"Moderated\" visibility");
        createSiteDialog.selectModeratedVisibility();
        assertTrue(createSiteDialog.isModeratedVisibilityRadioButtonChecked(), "Moderated visibility is not selected.");

        LOG.info("STEP4: Click \"Save\" button");
        createSiteDialog.clickCreateButton(siteDashboardPage);
        siteDashboardPage.setCurrentSiteName(siteName);
        String expectedRelativePath = "share/page/site/" + siteName + "/dashboard";
        assertEquals(siteDashboardPage.getRelativePath(), expectedRelativePath, "User is successfully redirected to the created site.");

        LOG.info("STEP5: Check visibility for the site");
        getBrowser().waitInSeconds(5);
        assertEquals(siteDashboardPage.getSiteVisibility(), "Moderated", "\"Moderated\" visibility is displayed next to the site name.");
    }

    @TestRail(id = "C2106")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void createPrivateSiteFromToolbar()
    {
        String siteName = String.format("siteC2106-%s", DataUtil.getUniqueIdentifier());
        String description = String.format("description-C2106-%s", DataUtil.getUniqueIdentifier());
        userDashboardPage.navigate(user);

        LOG.info("STEP1: Open the \"Sites\" menu on the toolbar and click on \"Create Site\"");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter any \"Name\", \"URL Name\" and \"Description\" for the site");
        createSiteDialog.typeInNameInput(siteName);
        createSiteDialog.typeInSiteID(siteName);
        createSiteDialog.typeInDescription(description);
        //assertEquals(createSiteDialog.getTitleInputText(siteName), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Select \"Private\" visibility");
        createSiteDialog.selectPrivateVisibility();
        assertTrue(createSiteDialog.isPrivateVisibilityRadioButtonChecked(), "Private visibility selected.");

        LOG.info("STEP4: Click \"Save\" button");
        createSiteDialog.clickCreateButton(siteDashboardPage);
        siteDashboardPage.setCurrentSiteName(siteName);
        String expectedRelativePath = "share/page/site/" + siteName + "/dashboard";
        assertEquals(siteDashboardPage.getRelativePath(), expectedRelativePath, "User is successfully redirected to the created site.");

        LOG.info("STEP5: Check visibility for the site");
        assertEquals(siteDashboardPage.getSiteVisibility(), "Private", "\"Private\" visibility is displayed next to the site name.");
    }

    @TestRail(id = "C2107")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void createPublicSiteFromDashlet()
    {
        String siteName = String.format("siteC2107-%s", DataUtil.getUniqueIdentifier());
        String description = String.format("description-C2107-%s", DataUtil.getUniqueIdentifier());
        userDashboardPage.navigate(user);

        LOG.info("STEP1: \"Go to User's dashboard page - \"My sites\" dashlet.\n" + "Click \"Create site\".");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter any \"Name\", \"URL Name\" and \"Description\" for the site");
        createSiteDialog.typeInNameInput(siteName);
        //createSiteDialog.typeInSiteID(siteName);
        createSiteDialog.typeInDescription(description);
        //assertEquals(createSiteDialog.getTitleInputText(siteName), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Select \"Public\" visibility");
        createSiteDialog.selectPublicVisibility();
        assertTrue(createSiteDialog.isPublicVisibilityRadioButtonChecked(), "Public visibility selected.");

        LOG.info("STEP4: Click \"Save\" button");
        createSiteDialog.clickCreateButton(siteDashboardPage);
        siteDashboardPage.setCurrentSiteName(siteName);
        String expectedRelativePath = "share/page/site/" + siteName + "/dashboard";
        assertEquals(siteDashboardPage.getRelativePath(), expectedRelativePath, "User is successfully redirected to the created site.");

        LOG.info("STEP5: Check visibility for the site");
        assertEquals(siteDashboardPage.getSiteVisibility(), "Public", "\"Public\" visibility is displayed next to the site name.");
    }

    @TestRail(id = "C2108")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void createModeratedSiteFromDashlet()
    {
        String siteName = String.format("siteC2108-%s", DataUtil.getUniqueIdentifier());
        String description = String.format("description-C2108-%s", DataUtil.getUniqueIdentifier());
        userDashboardPage.navigate(user);

        LOG.info("STEP1: \"Go to User's dashboard page - \"My sites\" dashlet.\n" + "Click \"Create site\".");
        createSiteDialog.navigateFromDashlet();

        LOG.info("STEP2: Enter any \"Name\", \"URL Name\" and \"Description\" for the site");
        createSiteDialog.typeInNameInput(siteName);
        createSiteDialog.typeInSiteID(siteName);
        createSiteDialog.typeInDescription(description);
        //assertEquals(createSiteDialog.getTitleInputText(siteName), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Select \"Moderated\" visibility");
        createSiteDialog.selectModeratedVisibility();
        assertTrue(createSiteDialog.isModeratedVisibilityRadioButtonChecked(), "Moderated visibility is not selected.");

        LOG.info("STEP4: Click \"Save\" button");
        createSiteDialog.clickCreateButton(siteDashboardPage);
        siteDashboardPage.setCurrentSiteName(siteName);
        String expectedRelativePath = "share/page/site/" + siteName + "/dashboard";
        assertEquals(siteDashboardPage.getRelativePath(), expectedRelativePath, "User is successfully redirected to the created site.");

        LOG.info("STEP5: Check visibility for the site");
        assertEquals(siteDashboardPage.getSiteVisibility(), "Moderated", "\"Moderated\" visibility is displayed next to the site name.");
    }

    @TestRail(id = "C2109")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void createPrivateSiteFromDashlet()
    {
        String siteName = String.format("siteC2109-%s", DataUtil.getUniqueIdentifier());
        String description = String.format("description-C2109-%s", DataUtil.getUniqueIdentifier());
        userDashboardPage.navigate(user);

        LOG.info("STEP1: \"Go to User's dashboard page - \"My sites\" dashlet.\n" + "Click \"Create site\".");
        createSiteDialog.navigateFromDashlet();

        LOG.info("STEP2: Enter any \"Name\", \"URL Name\" and \"Description\" for the site");
        createSiteDialog.typeInNameInput(siteName);
        createSiteDialog.typeInSiteID(siteName);
        createSiteDialog.typeInDescription(description);
        //assertEquals(createSiteDialog.getTitleInputText(siteName), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Select \"Private\" visibility");
        createSiteDialog.selectPrivateVisibility();
        assertTrue(createSiteDialog.isPrivateVisibilityRadioButtonChecked(), "Private visibility is not selected.");

        LOG.info("STEP4: Click \"Save\" button");
        createSiteDialog.clickCreateButton(siteDashboardPage);
        siteDashboardPage.setCurrentSiteName(siteName);
        String expectedRelativePath = "share/page/site/" + siteName + "/dashboard";
        assertEquals(siteDashboardPage.getRelativePath(), expectedRelativePath, "User is successfully redirected to the created site.");

        LOG.info("STEP5: Check visibility for the site");
        assertEquals(siteDashboardPage.getSiteVisibility(), "Private", "\"Private\" visibility is displayed next to the site name.");
    }

    @TestRail(id = "C2124")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelCreatingSite()
    {
        String siteName = String.format("siteC2124-%s", DataUtil.getUniqueIdentifier());
        String description = String.format("description-C2124-%s", DataUtil.getUniqueIdentifier());
        userDashboardPage.navigate(user);

        LOG.info("STEP1: Open the \"Sites\" menu on the toolbar and click on \"Create Site\"");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter any \"Name\", \"URL Name\" and \"Description\" for the site");
        createSiteDialog.typeInNameInput(siteName);
        createSiteDialog.typeInSiteID(siteName);
        createSiteDialog.typeInDescription(description);
        //assertEquals(createSiteDialog.getTitleInputText(siteName), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Click \"Cancel\" button");
        createSiteDialog.clickCancelButton();
        assertFalse(createSiteDialog.isNameInputFieldDisplayed(), "'Create site' form is not closed.");
        assertFalse(siteService.exists(siteName, user, password), "Site isn't created.");
        assertEquals(getBrowser().getTitle(), "Alfresco » User Dashboard", "User is on Home page");
    }

    @TestRail(id = "C2125")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyCloseButton()
    {
        String siteName = String.format("siteC2125-%s", DataUtil.getUniqueIdentifier());
        String description = String.format("description-C2125-%s", DataUtil.getUniqueIdentifier());
        userDashboardPage.navigate(user);

        LOG.info("STEP1: Open the \"Sites\" menu on the toolbar and click on \"Create Site\"");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter any \"Name\", \"URL Name\" and \"Description\" for the site");
        createSiteDialog.typeInNameInput(siteName);
        createSiteDialog.typeInSiteID(siteName);
        createSiteDialog.typeInDescription(description);
       // assertEquals(createSiteDialog.getTitleInputText(), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Click \"Close\" button");
        createSiteDialog.clickCloseXButton();
        assertFalse(createSiteDialog.isNameInputFieldDisplayed(), "Form is not closed.");
    }

    @TestRail(id = "C2130")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void urlNameAlreadyExists()
    {
        String siteName = String.format("siteC2130-%s", DataUtil.getUniqueIdentifier());
        String description = String.format("description-C2130-%s", DataUtil.getUniqueIdentifier());
        userDashboardPage.navigate(user);

        LOG.info("STEP1: Open the \"Sites\" menu on the toolbar and click on \"Create Site\"");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter values for \"Name\" and \"Description\" fields");
        createSiteDialog.typeInNameInput(siteName);
        createSiteDialog.typeInSiteID(siteName);
        createSiteDialog.typeInDescription(description);
        //assertEquals(createSiteDialog.getTitleInputText(siteName), siteName, "Site title is filled in-");
        //assertEquals(createSiteDialog.getUrlNameInputText(), siteName.toLowerCase(), "Url name filled in-");

        LOG.info("STEP3: Delete the pre-populated value from the \"URL Name\" field");
        createSiteDialog.clearUrlNameInput();
        assertEquals(createSiteDialog.isUrlNameInputEmpty(), true, "URL Name field is empty.");

        LOG.info("STEP4: Fill in \"URL Name\" field with an existing site name and click \"Save\" button");
        createSiteDialog.typeUrlName(testSiteName);
        createSiteDialog.clickCreateButtonWithoutRenderer();
        assertEquals(createSiteDialog.getUrlErrorMessage(), language.translate("siteDetails.urlError"), "Create site: Existent url error message displayed-");

        LOG.info("STEP5: Click \"OK\" button.");
        createSiteDialog.clickOkFromErrorPopup();
        assertTrue(createSiteDialog.isNameInputFieldDisplayed(), "Create site dialog is displayed.");
        assertFalse(siteService.exists(siteName, user, password), "Site isn't created.");
    }
}
