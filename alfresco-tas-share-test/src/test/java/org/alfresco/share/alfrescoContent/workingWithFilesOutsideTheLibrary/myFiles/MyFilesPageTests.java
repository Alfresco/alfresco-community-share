package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import java.util.List;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
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
 * @author Razvan.Dorobantu
 */
public class MyFilesPageTests extends ContextAwareWebTest
{
    private final String user = String.format("C7659User%s", RandomData.getRandomAlphanumeric());
    private final String nonAdminFile = String.format("nonAdminDoc%s", RandomData.getRandomAlphanumeric());
    private final String adminFile = String.format("adminDoc%s", RandomData.getRandomAlphanumeric());
    private final String tag = String.format("testTag%s", RandomData.getRandomAlphanumeric()).toLowerCase();
    @Autowired
    private MyFilesPage myFilesPage;
    @Autowired
    private DocumentsFilters filters;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);

        LOG.info("Precondition: Admin uploads a file in My Files and adds a tag to it.");
        contentService.createDocumentInRepository(adminUser, adminPassword, null, CMISUtil.DocumentType.TEXT_PLAIN, adminFile, "some content");
        contentAction.addSingleTag(adminUser, adminPassword, adminFile, tag);

        LOG.info("Precondition: User uploads a file in My Files and adds a tag to it.");
        String userMyFiles = "User Homes/" + user;
        contentService.createDocumentInRepository(user, password, userMyFiles, CMISUtil.DocumentType.TEXT_PLAIN, nonAdminFile, "some content");
        contentAction.addSingleTag(user, password, userMyFiles + "/" + nonAdminFile, tag);
    }

    @AfterClass (alwaysRun = false)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        contentService.deleteContentByPath(adminUser, adminPassword, "/" + adminFile);

    }

    @TestRail (id = "C7659")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyMyFilesMainPage()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        setupAuthenticatedSession(user, password);
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Verify the items displayed on the left panel from the My Files page.");
        Assert.assertTrue(filters.isDocumentsDropDownDisplayed(), "Documents link is not present");
        Assert.assertTrue(filters.isallDocumentsFilterDisplayed(), "All documents filter is not displayed");
        Assert.assertTrue(filters.isIMEditingFilterDisplayed(), "I'm editing filter is not present");
        Assert.assertTrue(filters.isOthersAreEditingFilterDisplayed(), "Others are editing filter is not present");
        Assert.assertTrue(filters.isRecentlyModifiedFilterDisplayed(), "Recently modified filter is not present");
        Assert.assertTrue(filters.isRecentlyAddedFilterDisplayed(), "Recently added filter is not present");
        Assert.assertTrue(filters.isMyFavoritesFilterDisplayed(), "My Favorites filter is not present");
        Assert.assertTrue(filters.isLibraryLinkDisplayed(), "Library link is not displayed");
        Assert.assertTrue(filters.isDocumentsLinkPresent(), "Documents link is not present under Library");
        Assert.assertTrue(filters.isCategorisFilterDisplayed(), "Categories filter is not displayed");
        Assert.assertTrue(filters.isCategoriesRootDisplayed(), "Categories root is not displayed");
        Assert.assertTrue(filters.checkIfTagsFilterIsPresent(), "Tags filter is not present");
    }

    @TestRail (id = "C7660")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyMyFilesPageTags()
    {
        LOG.info("Step1: Login as admin and navigate to My Files page.");
        setupAuthenticatedSession(adminUser, adminPassword);
        myFilesPage.navigate();

        LOG.info("Step2: Verify the list of tags in the tags section.");
        List<String> tags = myFilesPage.getAllTagNames();
        LOG.info("Tags: " + tags.toString());
        Assert.assertTrue(tags.contains(tag.toLowerCase()), String.format("Tag: %s is not found", tag));

        LOG.info("Step3: Click on the tag and verify the files are displayed.");
        myFilesPage.clickOnTag(tag);
        getBrowser().waitInSeconds(4);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(adminFile));
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(nonAdminFile));

        LOG.info("Step4: Login as user and navigate to My Files page.");
        setupAuthenticatedSession(user, password);
        myFilesPage.navigate();

        LOG.info("Step5: Verify the list of tags in the tags section.");
        tags = myFilesPage.getAllTagNames();
        Assert.assertTrue(tags.contains(tag.toLowerCase()), String.format("Tag: %s is not found", tag));

        LOG.info("Step6: Click on the tag and verify the files are displayed.");
        myFilesPage.clickOnTag(tag);
        getBrowser().waitInSeconds(4);
        Assert.assertFalse(myFilesPage.isContentNameDisplayed(adminFile));
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(nonAdminFile));
    }
}
