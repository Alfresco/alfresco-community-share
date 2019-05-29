package org.alfresco.share.sitesFeatures.Links;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.link.DeleteCommentPopUp;
import org.alfresco.po.share.site.link.LinkDetailsViewPage;
import org.alfresco.po.share.site.link.LinkPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */
public class LinkCommentsTests extends ContextAwareWebTest
{
    @Autowired
    LinkPage linkPage;

    @Autowired
    LinkDetailsViewPage linkDetailsViewPage;

    @Autowired
    DeleteCommentPopUp deleteCommentPopUp;

    private String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private String siteName = "";
    private String linkTitle = String.format("Link%s", RandomData.getRandomAlphanumeric());
    private String linkURL = "LinkURL.com";
    private String linkDescription = String.format("Link description%s", RandomData.getRandomAlphanumeric());

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
    @TestRail (id = "C6230")
    public void addingACommentToALink()
    {
        LOG.info("Precondition: Create site and add 'Links' page to it");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser, password, siteName, linkTitle, linkURL, linkDescription, false, null);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName an click on the link name");
        linkPage.navigate(siteName);
        linkPage.clickOnLinkName(linkTitle);
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage().equals("No comments"), "'No comments' message should be displayed!");

        LOG.info("STEP 2: Click 'Add Comment' button");
        linkDetailsViewPage.clickOnAddCommentButton();

        LOG.info("STEP 3: Enter comment and click add comment");
        String comment = "“[|]’~< !--@/*$%^&#*/()?>,.*/\'";
        linkDetailsViewPage.addComment(comment);
        Assert.assertTrue(linkDetailsViewPage.getCommentsList().contains(comment), "Comment is not displayed!");
        Assert.assertTrue(linkDetailsViewPage.getCommentAuthor(comment).equals("firstName" + " " + "lastName"), "Wrong comment author!");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6231")
    public void cancelAddingACommentToALink()
    {
        LOG.info("Precondition: Create site and add 'Links' page to it");
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser, password, siteName, linkTitle, linkURL, linkDescription, false, null);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName an click on the link name");
        linkPage.navigate(siteName);
        linkPage.clickOnLinkName(linkTitle);
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage().equals("No comments"), "'No comments' message should be displayed!");

        LOG.info("STEP 2: Click 'Add Comment' button");
        linkDetailsViewPage.clickOnAddCommentButton();

        LOG.info("STEP 3: Enter comment and click add comment");
        String comment = "some content";
        linkDetailsViewPage.cancelAddComment(comment);
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage().equals("No comments"), "'No comments' message should be displayed!");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES }, enabled = false)
    @TestRail (id = "C6232")
    // This test doesn't work with with selenium version 2.46.0. It should be enabled on 2.53.0 version.
    public void editLinkComment()
    {
        LOG.info("Precondition: Create site and add 'Links' page to it");
        String comment = "comment1";
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser, password, siteName, linkTitle, linkURL, linkDescription, false, null);
        sitePagesService.commentLink(testUser, password, siteName, linkTitle, comment);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName an click on the link name");
        linkPage.navigate(siteName);
        linkPage.clickOnLinkName(linkTitle);
        Assert.assertTrue(linkDetailsViewPage.getCommentsList().contains(comment), "Comment is not displayed!");

        LOG.info("STEP 2: Hover the 'comment1' comment and click 'Edit Comment' button.");
        linkDetailsViewPage.clickEditComment(comment);

        LOG.info("STEP 3: Enter 'comment1 edited' in the box provided. Click 'Save' button.");
        linkDetailsViewPage.clearCommentContent();
        linkDetailsViewPage.addComment("comment1 edited");
        Assert.assertTrue(linkDetailsViewPage.getCommentsList().contains("comment1 edited"), "Comment is not displayed!");
        Assert.assertTrue(linkDetailsViewPage.getCommentAuthor(comment).equals("firstName" + " " + "lastName"), "Wrong comment author!");
        Assert.assertTrue(linkDetailsViewPage.getCommentCreationTime(comment).equals("just now"), "Wrong comment creation time!");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES }, enabled = false)
    @TestRail (id = "C6233")
    // This test doesn't work with with selenium version 2.46.0. It should be enabled on 2.53.0 version.
    public void cancelEditingLinkComment()
    {
        LOG.info("Precondition: Create site and add 'Links' page to it");
        String comment = "comment1";
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser, password, siteName, linkTitle, linkURL, linkDescription, false, null);
        sitePagesService.commentLink(testUser, password, siteName, linkTitle, comment);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName an click on the link name");
        linkPage.navigate(siteName);
        linkPage.clickOnLinkName(linkTitle);
        Assert.assertTrue(linkDetailsViewPage.getCommentsList().contains(comment), "Comment is not displayed!");

        LOG.info("STEP 2: Hover the 'comment1' comment and click 'Edit Comment' button.");
        linkDetailsViewPage.clickEditComment(comment);

        LOG.info("STEP 3: Enter 'comment1 edited' in the box provided. Click 'Save' button.");
        linkDetailsViewPage.clearCommentContent();
        linkDetailsViewPage.cancelAddComment("comment1 edited");
        Assert.assertTrue(linkDetailsViewPage.getCommentsList().contains(comment), "Comment is not displayed!");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6234")
    public void deleteLinkComment()
    {
        LOG.info("Precondition: Create site and add 'Links' page to it");
        String comment = "comment1";
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser, password, siteName, linkTitle, linkURL, linkDescription, false, null);
        sitePagesService.commentLink(testUser, password, siteName, linkTitle, comment);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName an click on the link name");
        linkPage.navigate(siteName);
        linkPage.clickOnLinkName(linkTitle);
        Assert.assertTrue(linkDetailsViewPage.getCommentsList().contains(comment), "Comment is not displayed!");

        LOG.info("STEP 2: Hover the 'comment1' comment and click 'Delete Comment' button.");
        linkDetailsViewPage.clickDeleteCommentLink(comment);
        Assert.assertTrue(deleteCommentPopUp.getDeleteMessage().equals("Are you sure you want to delete this comment?"), "Wrong delete link message!");

        LOG.info("STEP 3: Click 'Delete' button.");
        deleteCommentPopUp.clickDelete();
        Assert.assertTrue(linkDetailsViewPage.getCommentsList().isEmpty(), "No Comment should be displayed!");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6235")
    public void cancelDeletingLinkComment()
    {
        LOG.info("Precondition: Create site and add 'Links' page to it");
        String comment = "comment1";
        siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(testUser, password, siteName, DashboardCustomization.Page.LINKS, null);
        sitePagesService.createLink(testUser, password, siteName, linkTitle, linkURL, linkDescription, false, null);
        sitePagesService.commentLink(testUser, password, siteName, linkTitle, comment);

        LOG.info("STEP 1: Navigate to 'Links' page for siteName an click on the link name");
        linkPage.navigate(siteName);
        linkPage.clickOnLinkName(linkTitle);
        Assert.assertTrue(linkDetailsViewPage.getCommentsList().contains(comment), "Comment is not displayed!");

        LOG.info("STEP 2: Hover the 'comment1' comment and click 'Delete Comment' button.");
        linkDetailsViewPage.clickDeleteCommentLink(comment);
        Assert.assertTrue(deleteCommentPopUp.getDeleteMessage().equals("Are you sure you want to delete this comment?"), "Wrong delete link message!");

        LOG.info("STEP 3: Click 'Cancel' button.");
        deleteCommentPopUp.clickCancel();
        Assert.assertTrue(linkDetailsViewPage.getCommentsList().contains(comment), "Comment is not displayed!");
        siteService.delete(adminUser, adminPassword, siteName);

    }
}
