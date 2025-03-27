package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.alfresco.common.Utils.testDataFolder;

@Slf4j
/**
 * @author Razvan.Dorobantu
 */
public class MyFilesPageTests extends BaseTest
{
    private final String nonAdminFile = String.format("nonAdminDoc%s", RandomData.getRandomAlphanumeric());
    private final String adminFile = String.format("adminDoc%s", RandomData.getRandomAlphanumeric());
    private final String tag1 = String.format("testTag%s", RandomData.getRandomAlphanumeric()).toLowerCase();
    private MyFilesPage myFilesPage;
    private DocumentsFilters filters;
    private SitesManagerPage sitesManagerPage;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private UploadContent uploadContent;
    private final String testFilePath1 = testDataFolder + nonAdminFile + "testFile.txt" ;
    private final String testFilePath2 = testDataFolder + adminFile + "testFile.txt" ;

    @BeforeMethod(alwaysRun = true)
    public void setup()
    {
        filters = new DocumentsFilters(webDriver);
        sitesManagerPage = new SitesManagerPage(webDriver);
        myFilesPage = new MyFilesPage(webDriver);
        uploadContent = new UploadContent(webDriver);
        filters = new DocumentsFilters(webDriver);
    }
    @TestRail (id = "C7659")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyMyFilesMainPage()  {

        log.info("Precondition: Login as user");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(user.get());
        authenticateUsingCookies(user.get());

        log.info("Precondition: Navigate to MyFiles Page.");
        myFilesPage
            .navigate();

        log.info("Step 1: Verify the items displayed on the left panel from the My Files page.");

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
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT }, enabled = false)
    public void verifyMyFilesPageTags() {

        log.info("Precondition : Login as user and navigate to My Files page.");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(user.get());
        authenticateUsingCookies(user.get());

        log.info("Precondition: User uploads a file in My Files and adds a tag to it.");
        myFilesPage.navigate();
        uploadContent.uploadContent(testFilePath1);
        myFilesPage
            .mouseOverNoTags(nonAdminFile);
        myFilesPage
            .clickEditTagIcon(nonAdminFile);
        myFilesPage
            .type_TagName("TestTag1");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");

        log.info("Step1: Verify the list of tags in the tags section.");
        List<String> tags = myFilesPage.getAllTagNames();

        log.info("Tags: " + tags.toString());
        Assert.assertTrue(tags.contains("TestTag1".toLowerCase()), "TestTag is Not Present");

        log.info("Step2: Click on the tag and verify the Non Admin files are displayed & admin files are not Displayed.");
        myFilesPage.clickOnTag("testtag1");
        Assert.assertTrue(myFilesPage.isFileNameDisplayed(nonAdminFile));
        Assert.assertFalse(myFilesPage.isFileNameDisplayed(adminFile));


        log.info("PreCondition: Login as Admin User");
        authenticateUsingLoginPage(getAdminUser());

        log.info("Precondition: Admin uploads a file in My Files and adds a tag to it.");
        myFilesPage.navigate();
        uploadContent.uploadContent(testFilePath2);
        myFilesPage
            .mouseOverNoTags(adminFile);
        myFilesPage
            .clickEditTagIcon(adminFile);
        myFilesPage
            .type_TagName("TestTag1");

        log.info("Step3: Verify the list of tags in the tags section.");
        tags = myFilesPage.getAllTagNames();
        log.info("Tags: " + tags.toString());
        Assert.assertTrue(tags.contains("TestTag1".toLowerCase()), "TestTag is Not Present");

        log.info("Step4: Click on the tag and verify both Admin & Non Admin files are displayed.");
        myFilesPage.clickOnTag("testtag1");
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(adminFile));
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(nonAdminFile));

        deleteUsersIfNotNull(user.get());

    }
}