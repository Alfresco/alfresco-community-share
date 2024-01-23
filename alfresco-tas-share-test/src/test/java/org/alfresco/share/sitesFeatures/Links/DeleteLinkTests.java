package org.alfresco.share.sitesFeatures.Links;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.link.DeleteLinkPopUp;
import org.alfresco.po.share.site.link.LinkDetailsViewPage;
import org.alfresco.po.share.site.link.LinkPage;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.Assert;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
@Slf4j

/**
 * @author iulia.cojocea
 */
public class DeleteLinkTests extends BaseTest
{
    //@Autowired
    LinkPage linkPage;

   // @Autowired
    LinkDetailsViewPage linkDetailsViewPage;
    @Autowired
    SitePagesService sitePagesService;
    @Autowired
    SiteService siteService;
    DeleteLinkPopUp deleteLinkPopUp;

    private String linkTitle = String.format("Link%s", RandomData.getRandomAlphanumeric());
    private String linkURL = "LinkURL.com";
    private String linkDescription = String.format("Link description%s", RandomData.getRandomAlphanumeric());
    private List<String> linkTags = new ArrayList<>();
    private final ThreadLocal<UserModel> testUser = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        linkPage = new LinkPage(webDriver);
        linkDetailsViewPage = new LinkDetailsViewPage(webDriver);
        deleteLinkPopUp = new DeleteLinkPopUp(webDriver);

        authenticateUsingLoginPage(testUser.get());
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(testUser.get());
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6187")
    public void deleteLinkFromLinkDetailsPage()
    {
        log.info("Precondition: Create site and add 'Links' page to it");
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());
        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.LINKS, null);

        sitePagesService.createLink(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), linkTitle, linkURL, linkDescription, false, null);

        log.info("STEP 1: Navigate to 'Links' page for siteName an click on the link name");
        linkPage.navigate(siteName.get());
        linkPage.clickOnLinkName(linkTitle);

        log.info("STEP 2: Click on 'Delete' button for link");
        linkDetailsViewPage.clickOnDeleteLink();
        deleteLinkPopUp.isDeleteButtonDisplayed();
        deleteLinkPopUp.isCancelDeleteLinkButtonDisplayed();
        Assert.assertTrue(deleteLinkPopUp.getDeleteLinkMessage().equals("Do you really want to delete link " + "'" + linkTitle + "'?"),
            "Wrong delete link message!");
        deleteLinkPopUp.clickOnDeleteLinkButtonLinkDetailsPage();
        Assert.assertTrue(linkPage.get_NoLinksFoundMsg().equals("No links found."), "No link should be displayed!");

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6188")
    public void deleteLinkFromLinksPage()
    {
        log.info("Precondition: Create site and add 'Links' page to it");
        linkTags.add("tag1");
        linkTags.add("tag2");
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());
        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), linkTitle, linkURL, linkDescription, true, linkTags);

        log.info("STEP 1: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName.get());
        Assert.assertTrue(linkPage.getLinksListTitle().equals("All Links"), "All links should be displayed after clicking on 'Links' link!");
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link is not displayed in the list!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag1"), "tag1 is not displayed in the list!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag2"), "tag1 is not displayed in the list!");

        log.info("STEP 2: Hover link and click 'Delete' button");
        linkPage.clickDeleteLink(linkTitle);
        Assert.assertTrue(deleteLinkPopUp.getDeleteLinkMessage().equals("Are you sure you want to delete " + "\"" + linkTitle + "\"?"),
            "Wrong delete link message!");
        deleteLinkPopUp.clickOnDeleteLinkButtonLinksPage();
        Assert.assertTrue(linkPage.getNoLinksFoundMsg().equals("No links found."), "No link should be displayed!");
        Assert.assertTrue(linkPage.getTagsFromTagsSection().isEmpty(), "No tags should be displayed!");

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6189")
    public void cancelDeletingLink()
    {
        log.info("Precondition: Create site and add 'Links' page to it");
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());

        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), linkTitle, linkURL, linkDescription, false, null);

        log.info("STEP 1: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName.get());
        Assert.assertTrue(linkPage.getLinksListTitle().equals("All Links"), "All links should be displayed after clicking on 'Links' link!");
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link is not displayed in the list!");

        log.info("STEP 1: Hover link and click 'Delete' button");
        linkPage.clickDeleteLink(linkTitle);
        Assert.assertTrue(deleteLinkPopUp.getDeleteLinkMessage().equals("Are you sure you want to delete " + "\"" + linkTitle + "\"?"),
            "Wrong delete link message!");
        deleteLinkPopUp.clickOnCancelDeleteLink();
        Assert.assertTrue(linkPage.getLinksListTitle().equals("All Links"), "All links should be displayed after clicking on 'Links' link!");
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link is not displayed in the list!");

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6190")
    public void deleteMultipleLinks()
    {
        log.info("Precondition: Create site and add 'Links' page to it");
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());
        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), "link1", "link1URL", "link1Description", false, null);
        sitePagesService.createLink(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), "link2", "link2URL", "link2Description", false, null);
        sitePagesService.createLink(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), "link3", "link3URL", "link3Description", false, null);

        log.info("STEP 1: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName.get());
        Assert.assertTrue(linkPage.getLinksListTitle().equals("All Links"), "All links should be displayed after clicking on 'Links' link!");
        Assert.assertTrue(linkPage.getLinksTitlesList().contains("link1"), "Link1 is not in the list!");
        Assert.assertTrue(linkPage.getLinksTitlesList().contains("link2"), "Link2 is not in the list!");
        Assert.assertTrue(linkPage.getLinksTitlesList().contains("link3"), "Link3 is not in the list!");
        Assert.assertFalse(linkPage.isSelectedItemsButtonEnabled(), "'Selected Items' button should be disabled!");

        log.info("STEP 2: Select 'link1' by clicking the check box near it");
        linkPage.selectLinkCheckBox("link1");
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link2"), "Checkbox for link2 should not be checked!");
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link3"), "Checkbox for link3 should not be checked!");

        log.info("STEP 3: Click 'Select' button and select 'Invert Selection' option");
        linkPage.clickSelectButton();
        linkPage.clickInvertSelectionOption();
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link2"), "Checkbox for link2 should be checked!");
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link3"), "Checkbox for link3 should be checked!");
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link1"), "Checkbox for link1 should not be checked!");

        log.info("STEP 4: Click 'Select' button and select 'None' option");
        linkPage.clickSelectButton();
        linkPage.clickNoneOption();
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link1"), "Checkbox for link1 should not be checked!");
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link2"), "Checkbox for link2 should not be checked!");
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link3"), "Checkbox for link3 should not be checked!");
        Assert.assertFalse(linkPage.isSelectedItemsButtonEnabled(), "'Selected Items' button should be disabled!");

        log.info("STEP 5: Click 'Select' button and select 'All' option");
        linkPage.clickSelectButton();
        linkPage.clickAllOption();
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link1"), "Checkbox for link1 should be checked!");
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link2"), "Checkbox for link2 should be checked!");
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link3"), "Checkbox for link3 should be checked!");
        Assert.assertTrue(linkPage.isSelectedItemsButtonEnabled(), "'Selected Items' button should be enabled!");

        log.info("STEP 6: Click 'Selected Items' button and select 'Deselect All' option.");
        linkPage.clickOnSelectedItemsButton();
        linkPage.clickOnDeselectAllOption();
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link1"), "Checkbox for link1 should not be checked!");
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link2"), "Checkbox for link2 should not be checked!");
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link3"), "Checkbox for link3 should not be checked!");
        Assert.assertFalse(linkPage.isSelectedItemsButtonEnabled(), "'Selected Items' button should be disabled!");

        log.info("STEP 7: Click 'Select' button and select 'All' option");
        linkPage.clickSelectButton();
        linkPage.clickAllOption();
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link1"), "Checkbox for link1 should be checked!");
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link2"), "Checkbox for link2 should be checked!");
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link3"), "Checkbox for link3 should be checked!");
        Assert.assertTrue(linkPage.isSelectedItemsButtonEnabled(), "'Selected Items' button should be enabled!");

        log.info("STEP 8: Click 'Selected Items' button and select 'Delete' option");
        linkPage.clickOnSelectedItemsButton();
        linkPage.clickOnSelectDeleteOption();
        Assert.assertTrue(deleteLinkPopUp.getDeleteLinkMessage().equals("Delete selected links?"), "Wrong delete link message!");

        log.info("STEP 9: Click 'Delete' button.");
        deleteLinkPopUp.clickOnDeleteLinkButtonLinksPage();
        Assert.assertTrue(linkPage.getNoLinksFoundMsg().equals("No links found."), "No link should be displayed!");
    }
}
