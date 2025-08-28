package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.site.ItemActions;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.apache.commons.lang3.RandomStringUtils;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Laura.Capsa
 */
public class NavigationPanelTests extends BaseTest
{
    private DocumentsFilters documentsFilters;
    private SharedFilesPage sharedFilesPage;
    private DeleteDialog deleteDialog;
    FileModel testFile;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        sharedFilesPage = new SharedFilesPage(webDriver);
        documentsFilters = new DocumentsFilters(webDriver);
        deleteDialog = new DeleteDialog(webDriver);

        log.info("Precondition: Test user is created & Navigate to SharedFiles page");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        authenticateUsingCookies(user.get());

        sharedFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");
    }

    @TestRail (id = "C7927")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyFilters()
    {
        log.info("STEP1: Verify the Sidebar");
        ArrayList<String> expectedFilters = new ArrayList<>(Arrays.asList("Documents", "Shared Files", "Categories", "Tags"));
        assertEquals(documentsFilters.getSidebarFilters(), expectedFilters, "Sidebar filters=");
    }

    @TestRail (id = "C7928")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyTags() throws Exception {

        log.info("Precondition1 : user is logged into the Share & Several content items are created");
        testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().authenticateUser(user.get()).usingUser(user.get()).usingShared().createFile(testFile).assertThat().existsInRepo();

        log.info("Precondition2 :Any Tag is Created & added to File");
        String tag = RandomStringUtils.randomAlphabetic(4).toLowerCase();

        setAuthorizationRequestHeader(getRestApi().authenticateUser(user.get()))
            .withCoreAPI().usingResource(testFile).addTag(tag);

        log.info("STEP1: Verify the list of tags in the Tags section");
        sharedFilesPage
            .navigateByMenuBar();
        documentsFilters
            .assertIsTagPresentInSideBar(tag);

        log.info("STEP2: Click on one tag name");
        sharedFilesPage
            .refreshSharedFilesPage()
            .clickOnTag(tag);
        sharedFilesPage
            .assertIsFileDisplayed(testFile.getName());

        log.info("Delete the file Created in Precondition1");
        sharedFilesPage
            .navigateByMenuBar()
            .selectItemAction(testFile.getName(), ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .confirmDeletion();

    }

    @AfterMethod
    public void cleanUp()
    {
        deleteUsersIfNotNull(user.get());
    }
}