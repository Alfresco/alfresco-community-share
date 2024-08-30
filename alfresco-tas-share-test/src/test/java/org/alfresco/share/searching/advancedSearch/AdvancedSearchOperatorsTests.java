package org.alfresco.share.searching.advancedSearch;

import static org.testng.Assert.assertTrue;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j

/**
 * Created by Claudia Agache on 8/29/2016.
 */
public class AdvancedSearchOperatorsTests extends BaseTest
{

    Toolbar toolbar;
    private DocumentLibraryPage documentLibraryPage;
    private CreateContentPage createContent;
    SearchPage searchPage;
    private String testSite;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    String identifier = RandomData.getRandomAlphanumeric();
    private String docC7210_content = "C7210 C7210 C7210";
    private String docC7212 = "file " + identifier;
    private String docC5991 = "11123 " + identifier;
    private String docC7407 = "this is an item";
    private String docC7110 = "sample " + identifier;
    private String docC7110_1 = "sample1 " + identifier;
    private String docC7110_2 = "sample2 " + identifier;
    private String docC7110_3 = "sample3 " + identifier;
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();


    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition1: Any test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        authenticateUsingCookies(user.get());

        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        testSite = site.get().getTitle();
        createContent = new CreateContentPage(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        toolbar = new Toolbar(webDriver);
        searchPage = new SearchPage(webDriver);
    }

    @AfterMethod
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C7210")
    @Test(groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void recurringPhrases()
    {
        log.info("STEP 1: Search with: \"file_name\"^number_of_repetitions (e.g: \"C7210\"^3)");
        documentLibraryPage
            .navigate(testSite);
        documentLibraryPage
            .clickCreateButton();
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docC7212)
            .typeContent(docC7210_content)
            .clickCreate();
        documentLibraryPage
            .navigate();
        toolbar
            .SearchAndEnter(docC7210_content);
        assertTrue(searchPage.isResultFound(docC7212), "Searched content is found and displayed in search results list");
    }

    @TestRail (id = "C7288")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "SearchTests" })
    public void extendedSearch()  {
        log.info("STEP 1: A file with name file is created");
        documentLibraryPage
            .navigate(testSite);
        documentLibraryPage
            .clickCreateButton();
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docC7212)
            .typeContent(docC7110)
            .clickCreate();
        documentLibraryPage
            .navigate();
        log.info("STEP 2: A file with content name 'file' is created");
        documentLibraryPage
            .navigate(testSite);
        documentLibraryPage
            .clickCreateButton();
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docC7110_1)
            .typeContent(docC7212)
            .clickCreate();
        documentLibraryPage
            .navigate();
        log.info("STEP 3: A file with title name 'file' is created");
        documentLibraryPage
            .navigate(testSite);
        documentLibraryPage
            .clickCreateButton();
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docC7110_2)
            .typeTitle(docC7212)
            .clickCreate();
        documentLibraryPage
            .navigate();
        log.info("STEP 4: A file with Description 'file' is created");
        documentLibraryPage
            .navigate(testSite);
        documentLibraryPage
            .clickCreateButton();
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docC7110_3)
            .typeDescription(docC7212)
            .clickCreate();
        documentLibraryPage
            .navigate();
        log.info("STEP 1: Search for \"file\"");
        toolbar
            .SearchAndEnter("file");
        assertTrue(searchPage.isResultFound(docC7212),"The file with name containing the searched query is displayed in search result list");
        assertTrue(searchPage.isResultFound(docC7110_2),"The file with title containing the searched query is displayed in search result list");
        assertTrue(searchPage.isResultFound(docC7110_1),"The file with content containing the searched query is displayed in search result list");
        assertTrue(searchPage.isResultFound(docC7110_3),"The file with description containing the searched query is displayed in search result list");

    }

    @TestRail (id = "C5991")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "SearchTests" })
    public void wildcards()
    {
        documentLibraryPage
            .navigate(testSite);
        documentLibraryPage
            .clickCreateButton();
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docC5991)
            .typeContent(docC5991)
            .clickCreate();
        documentLibraryPage
            .navigate();
        log.info("STEP 1: Search with '*' wildcard, e.g: *23");
        toolbar
            .SearchAndEnter("*23");
        assertTrue(searchPage.isResultFound(docC5991), "'*' wildcard replaces zero, one or more characters in the searched query");

        log.info("STEP 2: Search with '*' wildcard and quotes, e.g: \"**23\"");
        toolbar
            .SearchAndEnter("\"**23\"");
        assertTrue(searchPage.isResultFound(docC5991), "Searched content is displayed in results list");

        log.info("STEP 3: Search with one '?' wildcard, e.g: 11?23");
        toolbar
            .SearchAndEnter("11?23");
        assertTrue(searchPage.isResultFound(docC5991), "'?' wildcard replaces a single character in the searched query");

        log.info("STEP 4: Search with multiple '?' wildcard, e.g: 1???3");
        toolbar
            .SearchAndEnter("1???3");
        assertTrue(searchPage.isResultFound(docC5991), "Each '?' wildcard replaces a character in the searched query");
        log.info("STEP 5: Search by a query beginning with '='");
        toolbar
            .SearchAndEnter("\"=11123\"");
        assertTrue(searchPage.isResultFound(docC5991), "The file with name containing the searched query is displayed in search result list");
        log.info("STEP 6: Search by query in quotes, e.g: \"11123\"");
        toolbar
            .SearchAndEnter("\"11123\"");
        assertTrue(searchPage.isResultFound(docC5991), "Content corresponding to the exact phrase in quotes, e.g.:\"11123\", is displayed in search results list");

    }

    @TestRail (id = "C7407")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void explicitSpansPositions()
    {
        documentLibraryPage
            .navigate(testSite);
        documentLibraryPage
            .clickCreateButton();
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docC7407)
            .typeContent(docC7407)
            .clickCreate();
        documentLibraryPage
            .navigate();
        log.info("STEP 1: Fill in search field with any query containing span and position symbol, e.g: this[^] item[$] , and press 'Enter'");
        toolbar.SearchAndEnter("\"this[^] item[$]\"");
        assertTrue(searchPage.isResultFound(docC7407), "Item starting with word before [^] and ending by the word before [$] is displayed in result list, e.g: \"this is an item\"");

    }
}







