package org.alfresco.share.sitesFeatures.Links;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.link.LinkDetailsViewPage;
import org.alfresco.po.share.site.link.LinkPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */
public class ViewLinkDetailsTest extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    LinkPage linkPage;

    @Autowired
    CustomizeSitePage customizeSitePage;

    @Autowired
    LinkDetailsViewPage linkDetailsViewPage;

    private String testUser = "testUser" + DataUtil.getUniqueIdentifier();
    private String siteName = "siteName" + DataUtil.getUniqueIdentifier();
    private List<String> tags = new ArrayList<String>();
    private DateTime currentDate = new DateTime();
    private String linkTitle = "Link1";

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", "firstName", "lastName");
        siteService.create(testUser, password, domain, siteName, siteName, Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        tags.add("tag1");
        sitePagesService.createLink(testUser, password, siteName, linkTitle, "link1.com", "link1 description", true, tags);
        setupAuthenticatedSession(testUser, password);
    }

    @Test
    @TestRail(id = "C6179")
    public void viewLinkDetails()
    {
        LOG.info("Step 1 - Navigate to 'Links' page for Site1.");
        linkPage.navigate(siteName);
        linkPage.clickOnLinkName(linkTitle);
        Assert.assertTrue(linkDetailsViewPage.getLinkTitle().equals(linkTitle),
                "Wrong link title! expected " + linkTitle + "but found " + linkDetailsViewPage.getLinkTitle());
        Assert.assertTrue(linkDetailsViewPage.getLinkURL().equals("link1.com"),
                "Wrong link URL! expected link1.com but found " + linkDetailsViewPage.getLinkURL());
        Assert.assertTrue(linkDetailsViewPage.getCreationDate().contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertTrue(linkDetailsViewPage.getCreatedBy().equals("firstName" + " " + "lastName"), "Wrong author of the link!");
        Assert.assertTrue(linkDetailsViewPage.getDescription().equals("link1 description"), "Wrong link description! expected link1 description but found"
                + linkDetailsViewPage.getDescription());
        Assert.assertTrue(linkDetailsViewPage.isTagDisplayedInTagsList(tags.get(0)), "Tag is not displayed!");
        Assert.assertTrue(linkDetailsViewPage.isAddCommentButtonDisplayed(), "Add comment button is not displayed!");
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage().equals("No comments"), "'No comments' message should be displayed!");
        Assert.assertTrue(linkDetailsViewPage.isEditLinkDisplayed(), "Edit action is not displayed!");
        Assert.assertTrue(linkDetailsViewPage.isDeleteLinkDisplayed(), "Delete action is not displayed!");

        LOG.info("Step 2 - Click 'Links List' link.");
        linkDetailsViewPage.clickOnLinksListLink();
        Assert.assertTrue(linkPage.getLinksListTitle().equals("All Links"), "All links should be displayed after clicking on 'Links' link!");
    }
}
