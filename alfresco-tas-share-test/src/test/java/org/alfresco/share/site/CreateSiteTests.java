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

public class CreateSiteTests extends ContextAwareWebTest
{
    @Autowired
    CreateSiteDialog createSiteDialog;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    UserDashboardPage userDashboardPage;

    String user = "user" + DataUtil.getUniqueIdentifier();
    String testSiteName = "testsite" + DataUtil.getUniqueIdentifier();

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, DataUtil.PASSWORD, user + domain, "firstName", "lastName");
        siteService.create(user, DataUtil.PASSWORD, domain, testSiteName, "description", Site.Visibility.PUBLIC);
        setupAuthenticatedSession(user, DataUtil.PASSWORD);
    }

    @TestRail(id = "C2103")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyItemsPresentOnForm()
    {
        userDashboardPage.navigate(user);

        LOG.info("STEP1: Open the \"Sites\" menu on the toolbar and click on \"Create Site\"");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Verify the available fields from \"Create Site\" form");
        assertEquals(createSiteDialog.isTitleInputDisplayed(), true, "Name field is displayed.");
        assertEquals(createSiteDialog.getTitleLabel(), language.translate("siteDetails.title"), "Name label-");
        assertEquals(createSiteDialog.isTitleMandatory(), true, "Name is mandatory.");

        assertEquals(createSiteDialog.isUrlNameInputDisplayed(), true, "URL Name field is displayed.");
        assertEquals(createSiteDialog.getUrlNameLabel(), language.translate("siteDetails.urlName"), "URL Name label is correct.");
        assertEquals(createSiteDialog.getUrlNameDescriptionText(), language.translate("siteDetails.urlNameDescription"), "URL name description-");
        assertEquals(createSiteDialog.isUrlNameMandatory(), true, "URL Name is mandatory.");

        assertEquals(createSiteDialog.isDescriptionInputDisplayed(), true, "Description field is displayed");
        assertEquals(createSiteDialog.getDescriptionLabel(), language.translate("siteDetails.description"), "Description label-");

        assertEquals(createSiteDialog.getVisibilityLabel(), language.translate("siteDetails.visibility"), "Visibility label-");

        assertEquals(createSiteDialog.isPublicVisibilityRadioButtonDisplayed(), true, "Public option: radio button is displayed.");

        assertEquals(createSiteDialog.isModeratedVisibilityRadioButtonDisplayed(), true, "Moderated option: radio button is displayed.");

        assertEquals(createSiteDialog.isPrivateVisibilityRadioButtonDisplayed(), true, "Private option: radio button is displayed.");

        LOG.info("STEP3: Verify the available \"Visibility\" options");
        assertEquals(createSiteDialog.getPublicVisibilityDescription(), language.translate("siteDetails.publicVisibilityDescription"),
                "Public option description-");
        assertEquals(createSiteDialog.getModeratedVisibilityDescription(), language.translate("siteDetails.moderatedVisibilityDescription"),
                "Moderated option description-");
        assertEquals(createSiteDialog.getPrivateVisibilityDescription(), language.translate("siteDetails.privateVisibilityDescription"),
                "Private option description-");

        LOG.info("STEP4: Verify the available buttons from \"Create Site\" form");
        assertEquals(createSiteDialog.isSaveButtonDisplayed(), true, "Save button is displayed.");
        assertEquals(createSiteDialog.isCancelButtonDisplayed(), true, "Cancel button is displayed.");
        assertEquals(createSiteDialog.isCloseButtonDisplayed(), true, "Close button is displayed.");
    }

    @TestRail(id = "C2104")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void createPublicSiteFromToolbar()
    {
        String siteName = "siteC2104-" + DataUtil.getUniqueIdentifier();
        String description = "description-C2104-" + DataUtil.getUniqueIdentifier();
        userDashboardPage.navigate(user);

        LOG.info("STEP1: Open the \"Sites\" menu on the toolbar and click on \"Create Site\"");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter any \"Name\", \"URL Name\" and \"Description\" for the site");
        createSiteDialog.typeDetails(siteName, description);
        assertEquals(createSiteDialog.getTitleInputText(), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Select \"Public\" visibility");
        createSiteDialog.selectPublicVisibility();
        assertEquals(createSiteDialog.isPublicVisibilitySelected(), true, "Public visibility selected.");

        LOG.info("STEP4: Click \"Save\" button");
        createSiteDialog.clickSaveButton();
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
        String siteName = "site-" + DataUtil.getUniqueIdentifier();
        userDashboardPage.navigate(user);

        LOG.info("STEP1: \"Go to User's dashboard page - \"My sites\" dashlet.\n" + "Click \"Create site\".");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter any \"Name\" for the site");
        createSiteDialog.typeName(siteName);
        getBrowser().waitInSeconds(2);
        assertEquals(createSiteDialog.getNameInputText(), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Click \"Create\" button");
        createSiteDialog.clickCreateButton();
        getBrowser().waitInSeconds(8);
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
        String siteName = "siteC2105-" + DataUtil.getUniqueIdentifier();
        String description = "description-C2105-" + DataUtil.getUniqueIdentifier();
        userDashboardPage.navigate(user);

        LOG.info("STEP1: Open the \"Sites\" menu on the toolbar and click on \"Create Site\"");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter any \"Name\", \"URL Name\" and \"Description\" for the site");
        createSiteDialog.typeDetails(siteName, description);
        assertEquals(createSiteDialog.getTitleInputText(), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Select \"Moderated\" visibility");
        createSiteDialog.selectModeratedVisibility();
        assertEquals(createSiteDialog.isModeratedVisibilitySelected(), true, "Moderated visibility selected.");

        LOG.info("STEP4: Click \"Save\" button");
        createSiteDialog.clickSaveButton();
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
        String siteName = "siteC2106-" + DataUtil.getUniqueIdentifier();
        String description = "description-C2106-" + DataUtil.getUniqueIdentifier();
        userDashboardPage.navigate(user);

        LOG.info("STEP1: Open the \"Sites\" menu on the toolbar and click on \"Create Site\"");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter any \"Name\", \"URL Name\" and \"Description\" for the site");
        createSiteDialog.typeDetails(siteName, description);
        assertEquals(createSiteDialog.getTitleInputText(), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Select \"Private\" visibility");
        createSiteDialog.selectPrivateVisibility();
        assertEquals(createSiteDialog.isPrivateVisibilitySelected(), true, "Private visibility selected.");

        LOG.info("STEP4: Click \"Save\" button");
        createSiteDialog.clickSaveButton();
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
        String siteName = "siteC2107-" + DataUtil.getUniqueIdentifier();
        String description = "description-C2107-" + DataUtil.getUniqueIdentifier();
        userDashboardPage.navigate(user);

        LOG.info("STEP1: \"Go to User's dashboard page - \"My sites\" dashlet.\n" + "Click \"Create site\".");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter any \"Name\", \"URL Name\" and \"Description\" for the site");
        createSiteDialog.typeDetails(siteName, description);
        assertEquals(createSiteDialog.getTitleInputText(), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Select \"Public\" visibility");
        createSiteDialog.selectPublicVisibility();
        assertEquals(createSiteDialog.isPublicVisibilitySelected(), true, "Public visibility selected.");

        LOG.info("STEP4: Click \"Save\" button");
        createSiteDialog.clickSaveButton();
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
        String siteName = "siteC2108-" + DataUtil.getUniqueIdentifier();
        String description = "description-C2108-" + DataUtil.getUniqueIdentifier();
        userDashboardPage.navigate(user);

        LOG.info("STEP1: \"Go to User's dashboard page - \"My sites\" dashlet.\n" + "Click \"Create site\".");
        createSiteDialog.navigateFromDashlet();

        LOG.info("STEP2: Enter any \"Name\", \"URL Name\" and \"Description\" for the site");
        createSiteDialog.typeDetails(siteName, description);
        assertEquals(createSiteDialog.getTitleInputText(), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Select \"Moderated\" visibility");
        createSiteDialog.selectModeratedVisibility();
        assertEquals(createSiteDialog.isModeratedVisibilitySelected(), true, "Moderated visibility selected.");

        LOG.info("STEP4: Click \"Save\" button");
        createSiteDialog.clickSaveButton();
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
        String siteName = "siteC2109-" + DataUtil.getUniqueIdentifier();
        String description = "description-C2109-" + DataUtil.getUniqueIdentifier();
        userDashboardPage.navigate(user);

        LOG.info("STEP1: \"Go to User's dashboard page - \"My sites\" dashlet.\n" + "Click \"Create site\".");
        createSiteDialog.navigateFromDashlet();

        LOG.info("STEP2: Enter any \"Name\", \"URL Name\" and \"Description\" for the site");
        createSiteDialog.typeDetails(siteName, description);
        assertEquals(createSiteDialog.getTitleInputText(), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Select \"Private\" visibility");
        createSiteDialog.selectPrivateVisibility();
        assertEquals(createSiteDialog.isPrivateVisibilitySelected(), true, "Private visibility selected.");

        LOG.info("STEP4: Click \"Save\" button");
        createSiteDialog.clickSaveButton();
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
        String siteName = "siteC2124-" + DataUtil.getUniqueIdentifier();
        String description = "description-C2124-" + DataUtil.getUniqueIdentifier();
        userDashboardPage.navigate(user);

        LOG.info("STEP1: Open the \"Sites\" menu on the toolbar and click on \"Create Site\"");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter any \"Name\", \"URL Name\" and \"Description\" for the site");
        createSiteDialog.typeDetails(siteName, description);
        assertEquals(createSiteDialog.getTitleInputText(), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Click \"Cancel\" button");
        createSiteDialog.clickCancelButton();
        assertEquals(createSiteDialog.isTitleInputDisplayed(), false, "'Create site' form is closed.");
        assertEquals(siteService.exists(siteName, user, password), false, "Site isn't created.");
        assertEquals(createSiteDialog.getPageTitle(), "Alfresco » User Dashboard", "User is on Home page");
    }

    @TestRail(id = "C2125")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyCloseButton()
    {
        String siteName = "siteC2125-" + DataUtil.getUniqueIdentifier();
        String description = "description-C2125-" + DataUtil.getUniqueIdentifier();
        userDashboardPage.navigate(user);

        LOG.info("STEP1: Open the \"Sites\" menu on the toolbar and click on \"Create Site\"");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter any \"Name\", \"URL Name\" and \"Description\" for the site");
        createSiteDialog.typeDetails(siteName, description);
        assertEquals(createSiteDialog.getTitleInputText(), siteName, "The new site title is filled in.");

        LOG.info("STEP3: Click \"Close\" button");
        createSiteDialog.clickClose();
        assertEquals(createSiteDialog.isTitleInputDisplayed(), false, "Form is closed.");
    }

    @TestRail(id = "C2130")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void urlNameAlreadyExists()
    {
        String siteName = "siteC2130-" + DataUtil.getUniqueIdentifier();
        String description = "description-C2130-" + DataUtil.getUniqueIdentifier();
        userDashboardPage.navigate(user);

        LOG.info("STEP1: Open the \"Sites\" menu on the toolbar and click on \"Create Site\"");
        createSiteDialog.navigateByMenuBar();

        LOG.info("STEP2: Enter values for \"Name\" and \"Description\" fields");
        createSiteDialog.typeDetails(siteName, description);
        assertEquals(createSiteDialog.getTitleInputText(), siteName, "Site title is filled in-");
        assertEquals(createSiteDialog.getUrlNameInputText(), siteName.toLowerCase(), "Url name filled in-");

        LOG.info("STEP3: Delete the pre-populated value from the \"URL Name\" field");
        createSiteDialog.clearUrlNameInput();
        assertEquals(createSiteDialog.isUrlNameInputEmpty(), true, "URL Name field is empty.");

        LOG.info("STEP4: Fill in \"URL Name\" field with an existing site name and click \"Save\" button");
        createSiteDialog.typeUrlName(testSiteName);
        createSiteDialog.clickSaveButton();
        assertEquals(createSiteDialog.getUrlErrorMessage(), language.translate("siteDetails.urlError"), "Create site: Existent url error message displayed-");

        LOG.info("STEP5: Click \"OK\" button.");
        createSiteDialog.clickOkFromErrorPopup();
        assertEquals(createSiteDialog.isTitleInputDisplayed(), true, "Create site dialog is displayed.");
        assertEquals(siteService.exists(siteName, user, password), false, "Site isn't created.");
    }
}
