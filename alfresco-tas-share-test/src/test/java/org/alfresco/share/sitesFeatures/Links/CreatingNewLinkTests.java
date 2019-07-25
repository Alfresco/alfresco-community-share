package org.alfresco.share.sitesFeatures.Links;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.link.CreateLinkPage;
import org.alfresco.po.share.site.link.LinkDetailsViewPage;
import org.alfresco.po.share.site.link.LinkPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */

public class CreatingNewLinkTests extends ContextAwareWebTest
{
    @Autowired
    LinkPage linkPage;

    @Autowired
    CreateLinkPage createLinkPage;

    @Autowired
    LinkDetailsViewPage linkDetailsViewPage;
    DateTime currentDate;
    private String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private String siteName = "";
    private String linkTitle = String.format("Link%s", RandomData.getRandomAlphanumeric());
    private String linkURL = "https://www.google.com";
    private String linkDescription = String.format("Link description%s", RandomData.getRandomAlphanumeric());

    @BeforeMethod (alwaysRun = true)
    public void setupMethod()
    {
        currentDate = new DateTime();
    }


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
    @TestRail (id = "C6180")
    public void createExternalLink()
    {
        LOG.info("Precondition: Create site and add 'Links' page to it");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName);
        Assert.assertTrue(linkPage.getNoLinksFoundMsg().equals("No links found."), "No link should be displayed!");

        LOG.info("STEP 2: Click 'New Link' button");
        linkPage.createLink();
        Assert.assertTrue(createLinkPage.isLinkTitleDisplayed(), "Link title input is not displayed!");
        Assert.assertTrue(createLinkPage.isLinkURLDisplayed(), "Link URL input is not displayed!");
        Assert.assertTrue(createLinkPage.isLinkDescriptionDisplayed(), "Link description input is not displayed!");
        Assert.assertFalse(createLinkPage.isLinkInternalChecked(), "Internal checkbox should be unchecked by default!");
        Assert.assertTrue(createLinkPage.isLinkTagDisplayed(), "Tags area is not displayed!");
        Assert.assertTrue(createLinkPage.isAddTagButtonDisplayed(), "Add tag button is not displayed!");
        Assert.assertTrue(createLinkPage.isChoosePopularTagsLinkDisplayed(), "'Choose from popular tags in this list' link is not displayed!");
        Assert.assertTrue(createLinkPage.isSaveButtonDisplayed(), "Save button is not displayed!");
        Assert.assertTrue(createLinkPage.isCancelButtonDisplayed(), "Cancel button is not displayed!");

        LOG.info("STEP 3: Enter a title, URL, description and click on 'Save' button");
        createLinkPage.typeLinkTitle(linkTitle);
        createLinkPage.typeLinkURL(linkURL);
        createLinkPage.typeLinkDescription(linkDescription);
        createLinkPage.clickSaveButton();

        Assert.assertTrue(linkDetailsViewPage.getLinkTitle().equals(linkTitle),
            "Wrong link title! expected " + linkTitle + "but found " + linkDetailsViewPage.getLinkTitle());
        Assert.assertTrue(linkDetailsViewPage.getLinkURL().equals(linkURL),
            "Wrong link URL! expected" + linkURL + "but found " + linkDetailsViewPage.getLinkURL());
        Assert.assertTrue(linkDetailsViewPage.getCreationDate().contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertTrue(linkDetailsViewPage.getCreatedBy().equals("firstName" + " " + "lastName"), "Wrong author of the link!");
        Assert.assertTrue(linkDetailsViewPage.getDescription().equals(linkDescription), "Wrong link description! expected " + linkDescription + "but found"
            + linkDetailsViewPage.getDescription());
        Assert.assertTrue(linkDetailsViewPage.getTagsList().isEmpty(), "No tag should be displayed!");
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage().equals("No comments"), "'No comments' message should be displayed!");

        LOG.info("STEP 4: Click 'Links List' link.");
        linkDetailsViewPage.clickOnLinksListLink();
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link title is not in the list!");

        LOG.info("STEP 5: Click on link's URL");
        // Store the current window handle
        String currentWindow = getBrowser().getWindowHandle();
        linkDetailsViewPage.clickOnLinkURL(linkURL);
        getBrowser().waitInSeconds(5);

        // Switch to new window opened
        for (String winHandle : getBrowser().getWindowHandles())
        {
            getBrowser().switchTo().window(winHandle);
            if (getBrowser().getCurrentUrl().contains("google"))
            {
                break;
            } else
            {
                getBrowser().switchTo().window(currentWindow);
            }
        }
        Assert.assertTrue(getBrowser().getCurrentUrl().contains("google"), "After clicking on the link, the title is: " + getBrowser().getCurrentUrl());

        getBrowser().close();
        getBrowser().switchTo().window(currentWindow);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6182")
    public void createInternalLink()
    {
        LOG.info("Precondition: Create site and add 'Links' page to it");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName);
        Assert.assertTrue(linkPage.getNoLinksFoundMsg().equals("No links found."), "No link should be displayed!");

        LOG.info("STEP 2: Click 'New Link' button");
        linkPage.createLink();
        Assert.assertTrue(createLinkPage.isLinkTitleDisplayed(), "Link title input is not displayed!");
        Assert.assertTrue(createLinkPage.isLinkURLDisplayed(), "Link URL input is not displayed!");
        Assert.assertTrue(createLinkPage.isLinkDescriptionDisplayed(), "Link description input is not displayed!");
        Assert.assertFalse(createLinkPage.isLinkInternalChecked(), "Internal checkbox should be unchecked by default!");
        Assert.assertTrue(createLinkPage.isLinkTagDisplayed(), "Tags area is not displayed!");
        Assert.assertTrue(createLinkPage.isAddTagButtonDisplayed(), "Add tag button is not displayed!");
        Assert.assertTrue(createLinkPage.isChoosePopularTagsLinkDisplayed(), "'Choose from popular tags in this list' link is not displayed!");
        Assert.assertTrue(createLinkPage.isSaveButtonDisplayed(), "Save button is not displayed!");
        Assert.assertTrue(createLinkPage.isCancelButtonDisplayed(), "Cancel button is not displayed!");

        LOG.info("STEP 3: Enter a title, URL, description and click on 'Save' button");
        createLinkPage.typeLinkTitle(linkTitle);
        createLinkPage.typeLinkURL(linkURL);
        createLinkPage.typeLinkDescription(linkDescription);
        createLinkPage.checkLinkInternal();
        createLinkPage.clickSaveButton();

        Assert.assertTrue(linkDetailsViewPage.getLinkTitle().equals(linkTitle),
            "Wrong link title! expected " + linkTitle + "but found " + linkDetailsViewPage.getLinkTitle());
        Assert.assertTrue(linkDetailsViewPage.getLinkURL().equals(linkURL),
            "Wrong link URL! expected" + linkURL + "but found " + linkDetailsViewPage.getLinkURL());
        Assert.assertTrue(linkDetailsViewPage.getCreationDate().contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertTrue(linkDetailsViewPage.getCreatedBy().equals("firstName" + " " + "lastName"), "Wrong author of the link!");
        Assert.assertTrue(linkDetailsViewPage.getDescription().equals(linkDescription), "Wrong link description! expected " + linkDescription + "but found"
            + linkDetailsViewPage.getDescription());
        Assert.assertTrue(linkDetailsViewPage.getTagsList().isEmpty(), "No tag should be displayed!");
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage().equals("No comments"), "'No comments' message should be displayed!");

        LOG.info("STEP 4: Click 'Links List' link.");
        linkDetailsViewPage.clickOnLinksListLink();
        Assert.assertTrue(linkPage.getLinksTitlesList().contains(linkTitle), "Link title is not in the list!");

        LOG.info("STEP 5: Click on link's URL");
        // Store the current window handle
        String currentWindow = getBrowser().getWindowHandle();

        linkDetailsViewPage.clickOnLinkURL(linkURL);
        getBrowser().waitInSeconds(3);

        // Switch to new window opened
        for (String winHandle : getBrowser().getWindowHandles())
        {
            getBrowser().switchTo().window(winHandle);
            if (getBrowser().getCurrentUrl().contains("google"))
            {
                break;
            } else
            {
                getBrowser().switchTo().window(currentWindow);
            }
        }
        Assert.assertTrue(getBrowser().getCurrentUrl().contains("google"), "After clicking on the link, the title is: " + getBrowser().getCurrentUrl());

        LOG.info("STEP 6: Click Back in browser and then click 'Links List' link.");
        getBrowser().navigate().back();
        linkPage.renderedPage();
        Assert.assertEquals(linkPage.getLinkTitle(), linkTitle, "Link not displayed in Liks list");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6182")
    public void cancelCreatingNewLink()
    {
        LOG.info("Precondition: Create site and add 'Links' page to it");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName);
        Assert.assertTrue(linkPage.getNoLinksFoundMsg().equals("No links found."), "No link should be displayed!");

        LOG.info("STEP 2: Click 'New Link' button");
        linkPage.createLink();

        LOG.info("STEP 3: Enter a title, URL, description and click on 'Cancel' button");
        createLinkPage.typeLinkTitle(linkTitle);
        createLinkPage.typeLinkURL(linkURL);
        createLinkPage.clickCancelButton();
        Assert.assertTrue(linkPage.getNoLinksFoundMsg().equals("No links found."), "No link should be displayed!");
        siteService.delete(adminUser, adminPassword, siteName);

    }
}
