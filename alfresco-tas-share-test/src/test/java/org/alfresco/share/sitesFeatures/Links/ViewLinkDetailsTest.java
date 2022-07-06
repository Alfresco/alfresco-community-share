package org.alfresco.share.sitesFeatures.Links;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
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
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */
public class ViewLinkDetailsTest extends ContextAwareWebTest
{
    //@Autowired
    LinkPage linkPage;

    //@Autowired
    LinkDetailsViewPage linkDetailsViewPage;

    DateTime currentDate;
    private String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private List<String> tags = new ArrayList<>();
    private String linkTitle = "Link1";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "firstName", "lastName");
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        tags.add("tag1");
        sitePagesService.createLink(testUser, password, siteName, linkTitle, "link1.com", "link1 description", true, tags);
        setupAuthenticatedSession(testUser, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
        siteService.delete(adminUser, adminPassword, siteName);
    }


    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6179")
    public void viewLinkDetails()
    {
        currentDate = new DateTime();
        LOG.info("Step 1 - Navigate to 'Links' page for Site1.");
        linkPage.navigate(siteName);
        linkPage.clickOnLinkName(linkTitle);
//        Assert.assertEquals(linkDetailsViewPage.getLinkTitle(), linkTitle,
//            "Wrong link title! expected " + linkTitle + "but found " + linkDetailsViewPage.getLinkTitle());
//        Assert.assertEquals(linkDetailsViewPage.getLinkURL(), "link1.com", "Wrong link URL! expected link1.com but found " + linkDetailsViewPage.getLinkURL());
//        Assert.assertTrue(linkDetailsViewPage.getCreationDate().contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
//        Assert.assertEquals(linkDetailsViewPage.getCreatedBy(), "firstName lastName", "Wrong author of the link!");
//        Assert.assertEquals(linkDetailsViewPage.getDescription(), "link1 description",
//            "Wrong link description! expected link1 description but found" + linkDetailsViewPage.getDescription());
//        Assert.assertTrue(linkDetailsViewPage.isTagDisplayedInTagsList(tags.get(0)), "Tag is not displayed!");
//        Assert.assertTrue(linkDetailsViewPage.isAddCommentButtonDisplayed(), "Add comment button is not displayed!");
//        Assert.assertEquals(linkDetailsViewPage.getNoCommentsMessage(), "No comments", "'No comments' message should be displayed!");
//        Assert.assertTrue(linkDetailsViewPage.isEditLinkDisplayed(), "Edit action is not displayed!");
//        Assert.assertTrue(linkDetailsViewPage.isDeleteLinkDisplayed(), "Delete action is not displayed!");

        LOG.info("Step 2 - Click 'Links List' link.");
        linkDetailsViewPage.clickOnLinksListLink();
        Assert.assertEquals(linkPage.getLinksListTitle(), "All Links", "All links should be displayed after clicking on 'Links' link!");
    }
}
