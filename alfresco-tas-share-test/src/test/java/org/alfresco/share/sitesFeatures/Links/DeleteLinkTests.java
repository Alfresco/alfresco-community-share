package org.alfresco.share.sitesFeatures.Links;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.share.site.link.DeleteLinkPopUp;
import org.alfresco.po.share.site.link.LinkDetailsViewPage;
import org.alfresco.po.share.site.link.LinkPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author iulia.cojocea
 */
public class DeleteLinkTests extends ContextAwareWebTest
{
    @Autowired
    LinkPage linkPage;

    @Autowired
    LinkDetailsViewPage linkDetailsViewPage;

    @Autowired
    DeleteLinkPopUp deleteLinkPopUp;

    private String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private String siteName = "";
    private String linkTitle = String.format("Link%s", RandomData.getRandomAlphanumeric());
    private String linkURL = "LinkURL.com";
    private String linkDescription = String.format("Link description%s", RandomData.getRandomAlphanumeric());
    private List<String> linkTags = new ArrayList<>();

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "firstName", "lastName");
        setupAuthenticatedSession(testUser, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
    }


    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6187")
    public void deleteLinkFromLinkDetailsPage()
    {
        LOG.info("Precondition: Create site and add 'Links' page to it");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser, password, siteName, linkTitle, linkURL, linkDescription, false, null);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName an click on the link name");
        linkPage.navigate(siteName);
        linkPage.clickOnLinkName(linkTitle);

        LOG.info("STEP 2: Click on 'Delete' button for link");
        linkDetailsViewPage.clickOnDeleteLink();
        deleteLinkPopUp.isDeleteButtonDisplayed();
        deleteLinkPopUp.isCancelDeleteLinkButtonDisplayed();
        Assert.assertTrue(deleteLinkPopUp.getDeleteLinkMessage().equals("Do you really want to delete link " + "'" + linkTitle + "'?"),
            "Wrong delete link message!");
        deleteLinkPopUp.clickOnDeleteLinkButtonLinkDetailsPage();
        Assert.assertTrue(linkPage.getNoLinksFoundMsg().equals("No links found."), "No link should be displayed!");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6188")
    public void deleteLinkFromLinksPage()
    {
        LOG.info("Precondition: Create site and add 'Links' page to it");
        linkTags.add("tag1");
        linkTags.add("tag2");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser, password, siteName, linkTitle, linkURL, linkDescription, true, linkTags);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName);
        Assert.assertTrue(linkPage.getLinksListTitle().equals("All Links"), "All links should be displayed after clicking on 'Links' link!");
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link is not displayed in the list!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag1"), "tag1 is not displayed in the list!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag2"), "tag1 is not displayed in the list!");

        LOG.info("STEP 2: Hover link and click 'Delete' button");
        linkPage.clickDeleteLink(linkTitle);
        Assert.assertTrue(deleteLinkPopUp.getDeleteLinkMessage().equals("Are you sure you want to delete " + "\"" + linkTitle + "\"?"),
            "Wrong delete link message!");
        deleteLinkPopUp.clickOnDeleteLinkButtonLinksPage();
        Assert.assertTrue(linkPage.getNoLinksFoundMsg().equals("No links found."), "No link should be displayed!");
        Assert.assertTrue(linkPage.getTagsFromTagsSection().isEmpty(), "No tags should be displayed!");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6189")
    public void cancelDeletingLink()
    {
        LOG.info("Precondition: Create site and add 'Links' page to it");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser, password, siteName, linkTitle, linkURL, linkDescription, false, null);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName);
        Assert.assertTrue(linkPage.getLinksListTitle().equals("All Links"), "All links should be displayed after clicking on 'Links' link!");
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link is not displayed in the list!");

        LOG.info("STEP 1: Hover link and click 'Delete' button");
        linkPage.clickDeleteLink(linkTitle);
        Assert.assertTrue(deleteLinkPopUp.getDeleteLinkMessage().equals("Are you sure you want to delete " + "\"" + linkTitle + "\"?"),
            "Wrong delete link message!");
        deleteLinkPopUp.clickOnCancelDeleteLink();
        Assert.assertTrue(linkPage.getLinksListTitle().equals("All Links"), "All links should be displayed after clicking on 'Links' link!");
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link is not displayed in the list!");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6190")
    public void deleteMultipleLinks()
    {
        LOG.info("Precondition: Create site and add 'Links' page to it");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser, password, siteName, "link1", "link1URL", "link1Description", false, null);
        sitePagesService.createLink(testUser, password, siteName, "link2", "link2URL", "link2Description", false, null);
        sitePagesService.createLink(testUser, password, siteName, "link3", "link3URL", "link3Description", false, null);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName);
        Assert.assertTrue(linkPage.getLinksListTitle().equals("All Links"), "All links should be displayed after clicking on 'Links' link!");
        Assert.assertTrue(linkPage.getLinksTitlesList().contains("link1"), "Link1 is not in the list!");
        Assert.assertTrue(linkPage.getLinksTitlesList().contains("link2"), "Link2 is not in the list!");
        Assert.assertTrue(linkPage.getLinksTitlesList().contains("link3"), "Link3 is not in the list!");
        Assert.assertFalse(linkPage.isSelectedItemsButtonEnabled(), "'Selected Items' button should be disabled!");

        LOG.info("STEP 2: Select 'link1' by clicking the check box near it");
        linkPage.selectLinkCheckBox("link1");
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link2"), "Checkbox for link2 should not be checked!");
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link3"), "Checkbox for link3 should not be checked!");

        LOG.info("STEP 3: Click 'Select' button and select 'Invert Selection' option");
        linkPage.clickSelectButton();
        linkPage.clickInvertSelectionOption();
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link2"), "Checkbox for link2 should be checked!");
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link3"), "Checkbox for link3 should be checked!");
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link1"), "Checkbox for link1 should not be checked!");

        LOG.info("STEP 4: Click 'Select' button and select 'None' option");
        linkPage.clickSelectButton();
        linkPage.clickNoneOption();
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link1"), "Checkbox for link1 should not be checked!");
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link2"), "Checkbox for link2 should not be checked!");
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link3"), "Checkbox for link3 should not be checked!");
        Assert.assertFalse(linkPage.isSelectedItemsButtonEnabled(), "'Selected Items' button should be disabled!");

        LOG.info("STEP 5: Click 'Select' button and select 'All' option");
        linkPage.clickSelectButton();
        linkPage.clickAllOption();
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link1"), "Checkbox for link1 should be checked!");
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link2"), "Checkbox for link2 should be checked!");
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link3"), "Checkbox for link3 should be checked!");
        Assert.assertTrue(linkPage.isSelectedItemsButtonEnabled(), "'Selected Items' button should be enabled!");

        LOG.info("STEP 6: Click 'Selected Items' button and select 'Deselect All' option.");
        linkPage.clickOnSelectedItemsButton();
        linkPage.clickOnDeselectAllOption();
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link1"), "Checkbox for link1 should not be checked!");
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link2"), "Checkbox for link2 should not be checked!");
        Assert.assertFalse(linkPage.isSelectLinkCheckBoxChecked("link3"), "Checkbox for link3 should not be checked!");
        Assert.assertFalse(linkPage.isSelectedItemsButtonEnabled(), "'Selected Items' button should be disabled!");

        LOG.info("STEP 7: Click 'Select' button and select 'All' option");
        linkPage.clickSelectButton();
        linkPage.clickAllOption();
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link1"), "Checkbox for link1 should be checked!");
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link2"), "Checkbox for link2 should be checked!");
        Assert.assertTrue(linkPage.isSelectLinkCheckBoxChecked("link3"), "Checkbox for link3 should be checked!");
        Assert.assertTrue(linkPage.isSelectedItemsButtonEnabled(), "'Selected Items' button should be enabled!");

        LOG.info("STEP 8: Click 'Selected Items' button and select 'Delete' option");
        linkPage.clickOnSelectedItemsButton();
        linkPage.clickOnSelectDeleteOption();
        Assert.assertTrue(deleteLinkPopUp.getDeleteLinkMessage().equals("Delete selected links?"), "Wrong delete link message!");

        LOG.info("STEP 9: Click 'Delete' button.");
        deleteLinkPopUp.clickOnDeleteLinkButtonLinksPage();
        Assert.assertTrue(linkPage.getNoLinksFoundMsg().equals("No links found."), "No link should be displayed!");
        siteService.delete(adminUser, adminPassword, siteName);

    }
}
