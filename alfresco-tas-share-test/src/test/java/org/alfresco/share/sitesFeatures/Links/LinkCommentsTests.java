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
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * @author iulia.cojocea
 */
@Slf4j
public class LinkCommentsTests extends BaseTest
{
    @Autowired
    SitePagesService sitePagesService;
    @Autowired
    SiteService siteService;
    private LinkPage linkPage;
    private CreateLinkPage createLinkPage;
    private LinkDetailsViewPage linkDetailsViewPage;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private String linkTitle = String.format("Link%s", RandomData.getRandomAlphanumeric());
    private String linkURL = "LinkURL.com";
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
        sitePagesService.createLink(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), linkTitle, linkURL, linkDescription, false, null);

        linkPage = new LinkPage(webDriver);
        createLinkPage = new CreateLinkPage(webDriver);
        linkDetailsViewPage = new LinkDetailsViewPage(webDriver);

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
    @TestRail (id = "C6230")
    public void addingACommentToALink()
    {
        log.info("STEP 1: Navigate to 'Links' page for siteName an click on the link name");
        linkPage.navigate(siteName.get().getId());
        linkPage.clickOnLinkName(linkTitle);
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage().equals("No comments"), "'No comments' message should be displayed!");

        log.info("STEP 2: Click 'Add Comment' button");
        linkDetailsViewPage.clickOnAddCommentButton();

        log.info("STEP 3: Enter comment and click add comment");
        String comment = "“[|]’~< !--@/*$%^&#*/()?>,.*/\'";
        linkDetailsViewPage.addComment(comment);
        List<String> expectedList = Arrays.asList(comment);
        for (String anExpectedList : expectedList)
        {
            assertTrue(linkDetailsViewPage.getCommentsList().contains(anExpectedList), "Comment is not displayed!");
        }

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6231")
    public void cancelAddingACommentToALink()
    {
        log.info("STEP 1: Navigate to 'Links' page for siteName an click on the link name");
        linkPage.navigate(siteName.get().getId());
        linkPage.clickOnLinkName(linkTitle);
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage().equals("No comments"), "'No comments' message should be displayed!");

        log.info("STEP 2: Click 'Add Comment' button");
        linkDetailsViewPage.clickOnAddCommentButton();

        log.info("STEP 3: Enter comment and click add comment");
        String comment = "some content";
        linkDetailsViewPage.cancelAddComment(comment);
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage().equals("No comments"), "'No comments' message should be displayed!");

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6232")
    public void editLinkComment()
    {
        log.info("Precondition: Create site and add 'Links' page to it");
        String comment = "comment1";
        sitePagesService.commentLink(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), linkTitle, comment);

        log.info("STEP 1: Navigate to 'Links' page for siteName an click on the link name");
        linkPage.navigate(siteName.get().getId());
        linkPage.clickOnLinkName(linkTitle);
        Assert.assertTrue(linkDetailsViewPage.getCommentsList().contains(comment), "Comment is not displayed!");

        log.info("STEP 2: Hover the 'comment1' comment and click 'Edit Comment' button.");
        linkDetailsViewPage.clickEditComment(comment);

        log.info("STEP 3: Enter 'comment1 edited' in the box provided. Click 'Save' button.");
        linkDetailsViewPage.clearEditCommentContent();
        linkDetailsViewPage.editComment("comment1 edited");
        List<String> expectedList = Arrays.asList("comment1 edited");
        for (String anExpectedList : expectedList)
        {
            assertTrue(linkDetailsViewPage.getCommentsList().contains(anExpectedList), "Comment is not displayed!");
        }
        Assert.assertTrue(linkDetailsViewPage.getCommentCreationTime(comment).equals("just now"), "Wrong comment creation time!");

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6233")
    public void cancelEditingLinkComment()
    {
        log.info("Precondition: Create site and add 'Links' page to it");
        String comment = "comment1";
        sitePagesService.commentLink(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), linkTitle, comment);

        log.info("STEP 1: Navigate to 'Links' page for siteName an click on the link name");
        linkPage.navigate(siteName.get().getId());
        linkPage.clickOnLinkName(linkTitle);
        Assert.assertTrue(linkDetailsViewPage.getCommentsList().contains(comment), "Comment is not displayed!");

        log.info("STEP 2: Hover the 'comment1' comment and click 'Edit Comment' button.");
        linkDetailsViewPage.clickEditComment(comment);

        log.info("STEP 3: Enter 'comment1 edited' in the box provided. Click 'Save' button.");
        linkDetailsViewPage.clearCommentContent();
        linkDetailsViewPage.cancelAddComment("comment1 edited");
        Assert.assertTrue(linkDetailsViewPage.getCommentsList().contains(comment), "Comment is not displayed!");

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6234")
    public void deleteLinkComment()
    {
        log.info("Precondition: Create site and add 'Links' page to it");
        String comment = "comment1";
        sitePagesService.commentLink(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), linkTitle, comment);

        log.info("STEP 1: Navigate to 'Links' page for siteName an click on the link name");
        linkPage.navigate(siteName.get().getId());
        linkPage.clickOnLinkName(linkTitle);
        Assert.assertTrue(linkDetailsViewPage.getCommentsList().contains(comment), "Comment is not displayed!");

        log.info("STEP 2: Hover the 'comment1' comment and click 'Delete Comment' button.");
        linkDetailsViewPage.clickDeleteCommentLink(comment);
        Assert.assertTrue(linkDetailsViewPage.getDeleteMessage().equals("Are you sure you want to delete this comment?"), "Wrong delete link message!");

        log.info("STEP 3: Click 'Delete' button.");
        linkDetailsViewPage.clickDelete();
        List<String> expectedList = Arrays.asList();
        for (String anExpectedList : expectedList)
        {
            assertTrue(linkDetailsViewPage.getCommentsList().contains(anExpectedList), "No Comment should be displayed!");
        }

    }

    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    @TestRail (id = "C6235")
    public void cancelDeletingLinkComment()
    {
        log.info("Precondition: Create site and add 'Links' page to it");
        String comment = "comment1";
        sitePagesService.commentLink(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), linkTitle, comment);

        log.info("STEP 1: Navigate to 'Links' page for siteName an click on the link name");
        linkPage.navigate(siteName.get().getId());
        linkPage.clickOnLinkName(linkTitle);
        Assert.assertTrue(linkDetailsViewPage.getCommentsList().contains(comment), "Comment is not displayed!");

        log.info("STEP 2: Hover the 'comment1' comment and click 'Delete Comment' button.");
        linkDetailsViewPage.clickDeleteCommentLink(comment);
        Assert.assertTrue(linkDetailsViewPage.getDeleteMessage().equals("Are you sure you want to delete this comment?"), "Wrong delete link message!");

        log.info("STEP 3: Click 'Cancel' button.");
        linkDetailsViewPage.clickCancel();
        List<String> expectedList = Arrays.asList("comment1");
        for (String anExpectedList : expectedList)
        {
            assertTrue(linkDetailsViewPage.getCommentsList().contains(anExpectedList), "Comment is not displayed!");
        }
    }
}
