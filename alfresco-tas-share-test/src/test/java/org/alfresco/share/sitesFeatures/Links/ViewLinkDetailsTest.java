package org.alfresco.share.sitesFeatures.Links;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;

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

@Slf4j
/**
 * @author iulia.cojocea
 */ public class ViewLinkDetailsTest extends BaseTest {
    private final ThreadLocal<UserModel> testUser = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    //@Autowired
    LinkPage linkPage;
    //@Autowired
    LinkDetailsViewPage linkDetailsViewPage;
    @Autowired
    SitePagesService sitePagesService;
    @Autowired
    SiteService siteService;
    DateTime currentDate;
    private List<String> tags = new ArrayList<>();
    private String linkTitle = "Link1";
    private String linkURL = "https://www.google.com";
    private List<String> linkTags = new ArrayList<>();
    private String linkDescription = String.format("Link description%s", RandomData.getRandomAlphanumeric());

    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        log.info("Precondition: Test users are created");
        testUser.set(getDataUser().usingAdmin()
            .createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("Precondition: Test Site is created");
        siteName.set(getDataSite().usingUser(testUser.get())
            .createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());

        siteService.addPageToSite(testUser.get()
            .getUsername(), testUser.get()
            .getPassword(), siteName.get()
            .getId(), DashboardCustomization.Page.LINKS, null);
        tags.add("tag1");
        sitePagesService.createLink(testUser.get()
            .getUsername(), testUser.get()
            .getPassword(), siteName.get()
            .getId(), linkTitle, "link1.com", "link1 description", true, tags);

        linkPage = new LinkPage(webDriver);
        linkDetailsViewPage = new LinkDetailsViewPage(webDriver);

        authenticateUsingLoginPage(testUser.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(),
            "/User Homes/" + testUser.get()
                .getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(testUser.get());
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    @TestRail(id = "C6179")
    public void viewLinkDetails() {
        currentDate = new DateTime();
        log.info("Step 1 - Navigate to 'Links' page for Site1.");
        linkPage.navigate(siteName.get());
        linkPage.clickOnLinkName(linkTitle);
        Assert.assertEquals(linkDetailsViewPage.getLinkTitle(), linkTitle,
            "Wrong link title! expected " + linkTitle + "but found "
                + linkDetailsViewPage.getLinkTitle());
        Assert.assertEquals(linkDetailsViewPage.getLinkURL(), "link1.com",
            "Wrong link URL! expected link1.com but found " + linkDetailsViewPage.getLinkURL());
        Assert.assertTrue(linkDetailsViewPage.getCreationDate()
            .contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertTrue(linkDetailsViewPage.getCreatedBy()
            .equals(testUser.get()
                .getFirstName() + " " + testUser.get()
                .getLastName()), "Wrong author of the link!");
        Assert.assertEquals(linkDetailsViewPage.getDescription(), "link1 description",
            "Wrong link description! expected link1 description but found"
                + linkDetailsViewPage.getDescription());
        Assert.assertTrue(linkDetailsViewPage.isTagDisplayedInTagsList(tags.get(0)), "Tag is not displayed!");
        Assert.assertTrue(linkDetailsViewPage.isAddCommentButtonDisplayed(), "Add comment button is not displayed!");
        Assert.assertEquals(linkDetailsViewPage.getNoCommentsMessage(), "No comments",
            "'No comments' message should be displayed!");
        Assert.assertTrue(linkDetailsViewPage.isEditLinkDisplayed(), "Edit action is not displayed!");
        Assert.assertTrue(linkDetailsViewPage.isDeleteLinkDisplayed(), "Delete action is not displayed!");

        log.info("Step 2 - Click 'Links List' link.");
        linkDetailsViewPage.clickOnLinksListLink();
        Assert.assertEquals(linkPage.getLinksListTitle(), "All Links",
            "All links should be displayed after clicking on 'Links' link!");
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    @TestRail(id = "C6888")
    public void verifyPossibilityToDisplayAndVisitCreatedSiteLinks() {
        currentDate = new DateTime();
        log.info("Precondition: Create site, add 'Links' page to it and create an external link");
        linkTags.add("tag1");

        siteName.set(getDataSite().usingUser(testUser.get())
            .createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());

        siteService.addPageToSite(testUser.get()
            .getUsername(), testUser.get()
            .getPassword(), siteName.get()
            .getId(), DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser.get()
            .getUsername(), testUser.get()
            .getPassword(), siteName.get()
            .getId(), linkTitle, linkURL, linkDescription, false, linkTags);

        log.info("STEP 1: Navigate to 'Links' page for siteName. Click on the link created in precondition");
        linkPage.navigate(siteName.get());
        Assert.assertTrue(linkPage.getLinksTitlesList()
            .contains(linkTitle), "Link is not displayed in the list!");
        Assert.assertEquals(linkPage.getLinkTags(linkTitle)
            .size(), 1, "Wrong no of tags for link title!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle)
            .contains("tag1"), "Tag1 is not displayed in the list of tags!");

        log.info("STEP 2: Click on link's URL.");
        linkDetailsViewPage.clickOnLinkURL(linkURL);

        linkPage.switchToWindow(linkURL);
        Assert.assertTrue(linkPage.getCurrentUrl()
                .contains("google"),
            "After clicking on the link, the title is: " + linkPage.getCurrentUrl());
    }
}

