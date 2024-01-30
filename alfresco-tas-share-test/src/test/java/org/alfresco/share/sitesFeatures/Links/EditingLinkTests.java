package org.alfresco.share.sitesFeatures.Links;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.link.EditLinkPage;
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
 */
public class EditingLinkTests extends BaseTest
{
    @Autowired
    SitePagesService sitePagesService;
    @Autowired
    SiteService siteService;

    //@Autowired
    LinkPage linkPage;

    //@Autowired
    LinkDetailsViewPage linkDetailsViewPage;

    //@Autowired
    EditLinkPage editLinkPage;

    DateTime currentDate;
    private String linkTitle = String.format("Google%s", RandomData.getRandomAlphanumeric());
    private String linkURL = "https://www.google.com";
    private String linkDescription = String.format("Link description%s", RandomData.getRandomAlphanumeric());
    private List<String> linkTags = new ArrayList<>();
    private final ThreadLocal<UserModel> testUser = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Test users are created");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        linkPage = new LinkPage(webDriver);
        linkDetailsViewPage = new LinkDetailsViewPage(webDriver);
        editLinkPage = new EditLinkPage(webDriver);

        authenticateUsingLoginPage(testUser.get());

    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser.get().getUsername());
        deleteUsersIfNotNull(testUser.get());
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6184")
    public void editLinkFromLinkPageView() {
        currentDate = new DateTime();
        log.info("Precondition: Create site, add 'Links' page to it and create an external link");
        linkTags.add("tag1");
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());

        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), linkTitle, linkURL, linkDescription, false, linkTags);

        log.info("STEP 1: Navigate to 'Links' page for siteName. Click on the link created in precondition");
        linkPage.navigate(siteName.get());
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link is not displayed in the list!");

        log.info("STEP 2: Click on link's URL.");
        linkPage.clickOnLinkURL(linkURL);
        linkPage.switchToWindow(linkURL);
        Assert.assertTrue(linkPage.getCurrentUrl().contains("google"), "After clicking on the link, the title is: " + linkPage.getCurrentUrl());

        log.info("STEP 3: Close the opened window, go back to Link View page and click 'Edit' button.");
        linkPage.closeCurrentTabAndswitchToDefaultBrowserTab();
        linkPage.clickOnLinkName(linkTitle);
        linkDetailsViewPage.clickOnEditLink();

        log.info("STEP 4: Enter a new title, URL, description, check 'Internal' checkbox and click on 'Update' button");
        String newLinkTitle = "Bing";
        String newLinkURL = "http://www.bing.com/";
        String newLinkDescription = "another search engine";
        editLinkPage.updateLinkTitle(newLinkTitle);
        editLinkPage.updateLinkURL(newLinkURL);
        editLinkPage.updateLinkDescription(newLinkDescription);
        editLinkPage.checkLinkInternal();
        editLinkPage.clickOnUpdateButton();

        Assert.assertTrue(linkDetailsViewPage.getLinkTitle().equals(newLinkTitle), "Wrong link title! expected " + newLinkTitle + "but found "
            + linkDetailsViewPage.getLinkTitle());
        Assert.assertTrue(linkDetailsViewPage.getLinkURL().equals(newLinkURL),
            "Wrong link URL! expected" + newLinkURL + "but found " + linkDetailsViewPage.getLinkURL());
        Assert.assertTrue(linkDetailsViewPage.getCreationDate().contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertTrue(linkDetailsViewPage.getCreatedBy().equals(testUser.get().getFirstName() + " " + testUser.get().getLastName()), "Wrong author of the link!");
        Assert.assertTrue(linkDetailsViewPage.getDescription().equals(newLinkDescription), "Wrong link description! expected " + newLinkDescription
            + "but found" + linkDetailsViewPage.getDescription());
        Assert.assertTrue(linkDetailsViewPage.isTagDisplayedInTagsList("tag1"), "Tag is not displayed!");
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage().equals("No comments"), "'No comments' message should be displayed!");

        log.info("STEP 5: Click on link's URL.");
        linkDetailsViewPage.clickOnLinkURL(newLinkURL);

        // Switch to new window opened
        linkPage.switchToWindow(newLinkURL);
        Assert.assertTrue(linkPage.getCurrentUrl().contains("bing"), "After clicking on the link, the title is: " + linkPage.getCurrentUrl());

        deleteSitesIfNotNull(siteName.get());

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6185")
    public void editLinkFromLinksPage() {
        currentDate = new DateTime();
        log.info("Precondition: Create site, add 'Links' page to it and create an external link");
        linkTags.add("tag1");
        linkTags.add("tag2");

        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());

        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), linkTitle, linkURL, linkDescription, true, linkTags);

        log.info("STEP 1: Navigate to 'Links' page for siteName. Click on the link created in precondition");
        linkPage.navigate(siteName.get());
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link is not displayed in the list!");
        Assert.assertEquals(linkPage.getLinkTags(linkTitle).size(), 2, "Wrong no of tags for link title!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag1"), "Tag1 is not displayed in the list of tags!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag2"), "Tag2 is not displayed in the list of tags!");

        log.info("STEP 2: Click on link's URL.");
        linkDetailsViewPage.clickOnLinkURL(linkURL);
        Assert.assertTrue(linkPage.getCurrentUrl().contains("google"), "After clicking on the link, the title is: " + linkPage.getCurrentUrl());

        log.info("STEP 3: Go back in browser and click 'Edit' button for 'Google' link.");
        linkPage.navigateBackBrowser();
        linkPage.clickEditLink(linkTitle);

        log.info("STEP 4: Enter a new title, URL, description, check 'Internal' checkbox and click on 'Update' button");
        String newLinkTitle = "Bing";
        String newLinkURL = "http://www.bing.com/";
        String newLinkDescription = "another search engine";
        editLinkPage.updateLinkTitle(newLinkTitle);
        editLinkPage.updateLinkURL(newLinkURL);
        editLinkPage.updateLinkDescription(newLinkDescription);
        editLinkPage.uncheckLinkInternal();
        editLinkPage.removeTag("tag2");
        editLinkPage.clickOnUpdateButton();

        Assert.assertTrue(linkDetailsViewPage.getLinkTitle().equals(newLinkTitle), "Wrong link title! expected " + newLinkTitle + "but found "
            + linkDetailsViewPage.getLinkTitle());
        Assert.assertTrue(linkDetailsViewPage.getLinkURL().equals(newLinkURL),
            "Wrong link URL! expected" + newLinkURL + "but found " + linkDetailsViewPage.getLinkURL());
        Assert.assertTrue(linkDetailsViewPage.getCreationDate().contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertTrue(linkDetailsViewPage.getCreatedBy().equals(testUser.get().getFirstName() + " " + testUser.get().getLastName()), "Wrong author of the link!");
        Assert.assertTrue(linkDetailsViewPage.getDescription().equals(newLinkDescription), "Wrong link description! expected " + newLinkDescription
            + "but found" + linkDetailsViewPage.getDescription());
        Assert.assertTrue(linkDetailsViewPage.isTagDisplayedInTagsList("tag1"), "Tag1 is not displayed!");
        Assert.assertFalse(linkDetailsViewPage.isTagDisplayedInTagsList("tag2"), "Tag is not displayed!");
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage().equals("No comments"), "'No comments' message should be displayed!");

        log.info("STEP 4: Click on 'Links List' link.");
        linkDetailsViewPage.clickOnLinksListLink();
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(newLinkTitle), "Link is not displayed in the list!");
        Assert.assertEquals(linkPage.getLinkTags(newLinkTitle).size(), 1, "Only one tag should be displayed!");
        Assert.assertTrue(linkPage.getLinkTags(newLinkTitle).contains("tag1"), "Tag1 is not displayed in the list of tags!");

        log.info("STEP 5: Click on 'Bing' link's URL.");
        linkPage.clickOnLinkURL(newLinkURL);

        // Switch to new window opened
        linkPage.switchToWindow(newLinkURL);
        Assert.assertTrue(linkPage.getCurrentUrl().contains("bing"), "After clicking on the link, the title is: " + linkPage.getCurrentUrl());

        deleteSitesIfNotNull(siteName.get());
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6186")
    public void cancelEditingLink()
    {
        currentDate = new DateTime();
        log.info("Precondition: Create site, add 'Links' page to it and create an external link");
        linkTags.add("tag1");
        linkTags.add("tag2");

        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());

        siteService.addPageToSite(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), linkTitle, linkURL, linkDescription, true, linkTags);

        log.info("STEP 1: Navigate to 'Links' page for siteName. Click on the link created in precondition");
        linkPage.navigate(siteName.get());
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link is not displayed in the list!");
        Assert.assertEquals(linkPage.getLinkTags(linkTitle).size(), 2, "Wrong no of tags for link title!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag1"), "Tag1 is not displayed in the list of tags!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag2"), "Tag2 is not displayed in the list of tags!");

        log.info("STEP 2: Click on 'Edit' button for 'Google' link");
        linkPage.clickEditLink(linkTitle);

        log.info("STEP 3: Enter a new title, URL, description, check 'Internal' checkbox and click on 'Update' button");
        String newLinkTitle = "Bing";
        String newLinkURL = "http://www.bing.com/";
        String newLinkDescription = "another search engine";
        editLinkPage.updateLinkTitle(newLinkTitle);
        editLinkPage.updateLinkURL(newLinkURL);
        editLinkPage.updateLinkDescription(newLinkDescription);
        editLinkPage.uncheckLinkInternal();
        editLinkPage.removeTag("tag2");
        editLinkPage.clickOnCancelButton();
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link is not displayed in the list!");
        Assert.assertEquals(linkPage.getLinkTags(linkTitle).size(), 2, "Wrong no of tags for link title!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag1"), "Tag1 is not displayed in the list of tags!");
        Assert.assertTrue(linkPage.getLinkTags(linkTitle).contains("tag2"), "Tag2 is not displayed in the list of tags!");

        deleteSitesIfNotNull(siteName.get());
    }
}
