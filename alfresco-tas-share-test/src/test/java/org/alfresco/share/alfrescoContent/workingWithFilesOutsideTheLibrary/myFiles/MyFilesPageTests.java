package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesPageTests extends ContextAwareWebTest
{
    @Autowired private MyFilesPage myFilesPage;

    @Autowired private DocumentsFilters filters;

    @Autowired private SiteDashboardPage sitePage;
    
    @Autowired
    private UploadContent uploadContent;

    private final String user = "C7659User" + DataUtil.getUniqueIdentifier();
    private final String nonAdminFile =  DataUtil.getUniqueIdentifier() + "nonAdminDoc.txt";
    private final String adminFile = DataUtil.getUniqueIdentifier() + "adminDoc.txt";
    private final String nonAdminFilePath = testDataFolder + nonAdminFile;
    private final String adminFilePath = testDataFolder + adminFile;
    private final String tag = "testTag" + DataUtil.getUniqueIdentifier();

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);

        LOG.info("Precondition: Login as admin and navigate to My Files page.");
        setupAuthenticatedSession(adminUser, adminPassword);
        sitePage.clickMyFilesLink();
        getBrowser().waitInSeconds(5);

        LOG.info("Precondition: Admin uploads a file and adds a tag to it.");
        uploadContent.uploadContent(adminFilePath);
        assertTrue(myFilesPage.isContentNameDisplayed(adminFile),String.format("The file [%s] is not present", adminFile));
        myFilesPage.mouseOverNoTags(adminFile);
        myFilesPage.clickEditTagIcon(adminFile);
        myFilesPage.typeTagName(tag);
        myFilesPage.clickEditTagLink("Save");

        LOG.info("Precondition: Login as user and navigate to My Files page.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        getBrowser().waitInSeconds(5);

        LOG.info("Precondition: User uploads a file and adds a tag to it.");
        uploadContent.uploadContent(nonAdminFilePath);
        assertTrue(myFilesPage.isContentNameDisplayed(nonAdminFile),String.format("The file [%s] is not present", nonAdminFile));
        myFilesPage.mouseOverNoTags(nonAdminFile);
        myFilesPage.clickEditTagIcon(nonAdminFile);
        myFilesPage.typeTagName(tag);
        myFilesPage.clickEditTagLink("Save");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7659")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void verifyMyFilesMainPage()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
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

    @TestRail(id = "C7660")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void verifyMyFilesPageTags()
    {
        LOG.info("Step1: Login as admin and navigate to My Files page.");
        setupAuthenticatedSession(adminUser, adminPassword);
        sitePage.clickMyFilesLink();
        getBrowser().waitInSeconds(5);

        LOG.info("Step2: Verify the list of tags in the tags section.");
        List<String> tags = myFilesPage.getAllTagNames();
        Assert.assertTrue(tags.contains(tag.toLowerCase()), String.format("Tag: %s is not found", tag));

        LOG.info("Step3: Click on the tag and verify the files are displayed.");
        myFilesPage.clickOnTag(tag);
        getBrowser().waitInSeconds(4);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(adminFile));
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(nonAdminFile));

        LOG.info("Step4: Login as user and navigate to My Files page.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        getBrowser().waitInSeconds(5);

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
