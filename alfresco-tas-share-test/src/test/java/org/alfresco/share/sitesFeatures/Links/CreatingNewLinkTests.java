package org.alfresco.share.sitesFeatures.Links;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.link.CreateLinkPage;
import org.alfresco.po.share.site.link.LinkDetailsViewPage;
import org.alfresco.po.share.site.link.LinkPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */
@Slf4j
public class CreatingNewLinkTests extends BaseTest
{
    @Autowired
    SitePagesService sitePagesService;
    @Autowired
    SiteService siteService;
    private LinkPage linkPage;
    private CreateLinkPage createLinkPage;
    private LinkDetailsViewPage linkDetailsViewPage;
    DateTime currentDate;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private String linkTitle = String.format("Link%s", RandomData.getRandomAlphanumeric());
    private String linkURL = "https://www.google.com";
    private String linkDescription = String.format("Link description%s", RandomData.getRandomAlphanumeric());

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());
        siteService.addPageToSite(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.LINKS, null);

        linkPage = new LinkPage(webDriver);
        createLinkPage = new CreateLinkPage(webDriver);
        linkDetailsViewPage = new LinkDetailsViewPage(webDriver);
        currentDate = new DateTime();

        authenticateUsingLoginPage(user1.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user1.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user1.get());
    }

    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6180")
    public void createExternalLink()
    {
        log.info("STEP 1: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName.get().getId());
        Assert.assertTrue(linkPage.getNoLinksFoundMsg().equals("No links found."), "No link should be displayed!");

        log.info("STEP 2: Click 'New Link' button");
        linkPage.createLink();
        Assert.assertTrue(createLinkPage.isLinkTitleDisplayed(), "Link title input is displayed!");
        Assert.assertTrue(createLinkPage.isLinkURLDisplayed(), "Link URL input is displayed!");
        Assert.assertTrue(createLinkPage.isLinkDescriptionDisplayed(), "Link description input is displayed!");
        Assert.assertTrue(createLinkPage.isLinkInternalChecked(), "Internal checkbox should be unchecked by default!");
        Assert.assertTrue(createLinkPage.isLinkTagDisplayed(), "Tags area is displayed!");
        Assert.assertTrue(createLinkPage.isAddTagButtonDisplayed(), "Add tag button is displayed!");
        Assert.assertTrue(createLinkPage.isChoosePopularTagsLinkDisplayed(), "'Choose from popular tags in this list' link is displayed!");
        Assert.assertTrue(createLinkPage.isSaveButtonDisplayed(), "Save button is displayed!");
        Assert.assertTrue(createLinkPage.isCancelButtonDisplayed(), "Cancel button is displayed!");

        log.info("STEP 3: Enter a title, URL, description and click on 'Save' button");
        createLinkPage.typeLinkTitle(linkTitle);
        createLinkPage.typeLinkUrl(linkURL);
        createLinkPage.typeLinkDescription(linkDescription);
        createLinkPage.clickSaveButton();

        Assert.assertTrue(linkDetailsViewPage.getLinkTitle().equals(linkTitle),
           "Wrong link title! expected " + linkTitle + "but found " + linkDetailsViewPage.getLinkTitle());
        Assert.assertTrue(linkDetailsViewPage.getLinkURL().equals(linkURL),
            "Wrong link URL! expected" + linkURL + "but found " + linkDetailsViewPage.getLinkURL());
        Assert.assertTrue(linkDetailsViewPage.getCreationDate().contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertFalse(linkDetailsViewPage.getCreatedBy().equals("firstName" + " " + "lastName"), "Wrong author of the link!");
        Assert.assertTrue(linkDetailsViewPage.getDescription().equals(linkDescription), "Wrong link description! expected " + linkDescription + "but found"
            + linkDetailsViewPage.getDescription());
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage().equals("No comments"), "'No comments' message should be displayed!");

        log.info("STEP 4: Click 'Links List' link.");
        linkDetailsViewPage.clickOnLinksListLink();
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link title is not in the list!");

        log.info("STEP 5: Click on link's URL");
        linkPage.switchWindow();
        Assert.assertTrue(linkPage.getCurrentUrl().contains("google"), "After clicking on the link, the title is: " + linkPage.getCurrentUrl());
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6182")
    public void createInternalLink()
    {
        log.info("STEP 1: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName.get().getId());
        Assert.assertTrue(linkPage.getNoLinksFoundMsg().equals("No links found."), "No link should be displayed!");

        log.info("STEP 2: Click 'New Link' button");
        linkPage.createLink();
        Assert.assertTrue(createLinkPage.isLinkTitleDisplayed(), "Link title input is not displayed!");
        Assert.assertTrue(createLinkPage.isLinkURLDisplayed(), "Link URL input is not displayed!");
        Assert.assertTrue(createLinkPage.isLinkDescriptionDisplayed(), "Link description input is not displayed!");
        Assert.assertTrue(createLinkPage.isLinkInternalChecked(), "Internal checkbox should be unchecked by default!");
        Assert.assertTrue(createLinkPage.isLinkTagDisplayed(), "Tags area is not displayed!");
        Assert.assertTrue(createLinkPage.isAddTagButtonDisplayed(), "Add tag button is not displayed!");
        Assert.assertTrue(createLinkPage.isChoosePopularTagsLinkDisplayed(), "'Choose from popular tags in this list' link is not displayed!");
        Assert.assertTrue(createLinkPage.isSaveButtonDisplayed(), "Save button is not displayed!");
        Assert.assertTrue(createLinkPage.isCancelButtonDisplayed(), "Cancel button is not displayed!");

        log.info("STEP 3: Enter a title, URL, description and click on 'Save' button");
        createLinkPage.typeLinkTitle(linkTitle);
        createLinkPage.typeLinkUrl(linkURL);
        createLinkPage.typeLinkDescription(linkDescription);
        createLinkPage.checkLinkInternal();
        createLinkPage.clickSaveButton();

        Assert.assertTrue(linkDetailsViewPage.getLinkTitle().equals(linkTitle),
            "Wrong link title! expected " + linkTitle + "but found " + linkDetailsViewPage.getLinkTitle());
        Assert.assertTrue(linkDetailsViewPage.getLinkURL().equals(linkURL),
            "Wrong link URL! expected" + linkURL + "but found " + linkDetailsViewPage.getLinkURL());
        Assert.assertTrue(linkDetailsViewPage.getCreationDate().contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertFalse(linkDetailsViewPage.getCreatedBy().equals("firstName" + " " + "lastName"), "Wrong author of the link!");
        Assert.assertTrue(linkDetailsViewPage.getDescription().equals(linkDescription), "Wrong link description! expected " + linkDescription + "but found"
            + linkDetailsViewPage.getDescription());
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage().equals("No comments"), "'No comments' message should be displayed!");

        log.info("STEP 4: Click 'Links List' link.");
        linkDetailsViewPage.clickOnLinksListLink();
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link title is not in the list!");

        log.info("STEP 5: Click on link's URL");
        linkPage.switchWindow();
        Assert.assertTrue(linkPage.getCurrentUrl().contains("google"), "After clicking on the link, the title is: " + linkPage.getCurrentUrl());

        log.info("STEP 6: Click Back in browser and then click 'Links List' link.");
        linkPage.navigateBackBrowser();
        Assert.assertEquals(linkPage.getLinkTitle(), linkTitle, "Link not displayed in Liks list");
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6182")
    public void cancelCreatingNewLink()
    {
        log.info("STEP 1: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName.get().getId());
        Assert.assertTrue(linkPage.getNoLinksFoundMsg().equals("No links found."), "No link should be displayed!");

        log.info("STEP 2: Click 'New Link' button");
        linkPage.createLink();

        log.info("STEP 3: Enter a title, URL, description and click on 'Cancel' button");
        createLinkPage.typeLinkTitle(linkTitle);
        createLinkPage.typeLinkUrl(linkURL);
        createLinkPage.clickCancelButton();
        Assert.assertTrue(linkPage.getNoLinksFoundMsg().equals("No links found."), "No link should be displayed!");
    }
}
