package org.alfresco.share.searching;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.blog.CreateBlogPostPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Razvan.Dorobantu
 */
public class SearchHighlightTests extends BaseTest
{
    @Autowired
    private SiteService siteService;
    @Autowired
    private ContentService contentService;
    DocumentLibraryPage documentLibraryPage;
    EditPropertiesDialog editFilePropertiesDialog;
    BlogPostListPage blogPage;
    CreateBlogPostPage createBlogPost;
    private AdvancedSearchPage advancedSearchPage;
    private SearchPage searchPage;
    String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    String password = "password";
    String siteName = "HighlightSite" + uniqueIdentifier;
    String docName = "docName" + uniqueIdentifier;
    String docTitle = "docTitle" + uniqueIdentifier;
    String docContent = "docContent" + uniqueIdentifier;
    String docDescription = "docDescription" + uniqueIdentifier;
    String C42550testFile = "C42550Doc.docx";
    String C42550testFilePath = testDataFolder + C42550testFile;
    String C42558file = "C42558file";
    String C42560file = "C42560file";
    String C42560file2 = "C42560file2";
    String C42562file1 = "C42562file1";
    String C42562file2 = "C42562file2";
    String C42562SearchTerm = "C42562file";
    String C42564file1 = "C42564file";
    String C42564file2 = "big C42564file";
    String C42549file = siteName;
    private String blogPostTitle = "HighlightBlogTitle" + uniqueIdentifier;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void createPrecondition()
    {

        log.info("Precondition1: Any test user is created & Sites are Created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        String userName = user.get().getUsername();
        authenticateUsingCookies(user.get());

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        editFilePropertiesDialog = new EditPropertiesDialog(webDriver);
        advancedSearchPage = new AdvancedSearchPage(webDriver);
        searchPage = new SearchPage(webDriver);
        blogPage = new BlogPostListPage(webDriver);
        createBlogPost = new CreateBlogPostPage(webDriver);

        authenticateUsingCookies(user.get());
        List<DashboardCustomization.Page> pagesToAdd = new ArrayList<>();
        pagesToAdd.add(DashboardCustomization.Page.BLOG);

        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        String siteName = site.get().getId();

        siteService.addPagesToSite(userName, password, siteName, pagesToAdd);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, C42558file, C42558file);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, C42560file, docContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, C42560file2, docContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, C42562file1, docContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, C42562file2, docContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, C42564file1, docContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, C42564file2, docContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, C42549file, C42549file);
        contentService.uploadFileInSite(userName, password, siteName, C42550testFilePath);

        authenticateUsingCookies(user.get());
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.selectItemActionFormFirstThreeAvailableOptions(docName, ItemActions.EDIT_PROPERTIES);
        editFilePropertiesDialog.setTitle(docTitle);
        editFilePropertiesDialog.setDescription(docDescription);
        editFilePropertiesDialog.clickSave();
    }

    @AfterMethod
    public void removeAddedFiles()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C42544")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByName()
    {
        log.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        log.info("Step 2: Type the name of the file and click search.");
        advancedSearchPage.typeName(docName);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the file is found and the name is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(docName));
        assertTrue(searchPage.isNameHighlighted(docName));
    }

    @TestRail (id = "C42545")
    @Test(groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByTitle()
    {
        log.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        log.info("Step 2: Type the title of the file and click search.");
        advancedSearchPage.typeTitle(docTitle);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the file is found and the title is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(docName));
        assertTrue(searchPage.isTitleHighlighted(docTitle));
    }

    @TestRail (id = "C42546")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })

    public void highlightSearchByDescription()
    {
        log.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        log.info("Step 2: Type the description of the file and click search.");
        advancedSearchPage.typeDescription(docDescription);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the file is found and the description is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(docName));
        assertTrue(searchPage.isDescriptionHighlighted(docDescription));
    }

    @TestRail (id = "C42547")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByContent()
    {
        log.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        log.info("Step 2: Type the content of the file and click search.");
        advancedSearchPage.typeKeywords(docContent);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the file is found and the content is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(docName));
        assertTrue(searchPage.isContentHighlighted(docContent));
    }

    @TestRail (id = "C42548")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByNameOfBlog()
    {
        String siteName = site.get().getId();
        log.info("Step 1: Login with username and navigate to Blog page.");
        blogPage.navigate(siteName);
        log.info("Step 2: Click 'New Post' button.");
        blogPage.openCreateNewPostForm();
        Assert.assertEquals(createBlogPost.getPageTitle(), "Create Blog Post");
        log.info("Step 3: Type a Title for the post.");
        createBlogPost.setTitle(blogPostTitle);
        log.info("Step 4: Click publish internally button.");
        createBlogPost.publishPostInternally();
        log.info("Step 5: Navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        log.info("Step 6: Type the title of the Blog and click search.");
        advancedSearchPage.typeKeywords(blogPostTitle);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 7: Verify that the Blog is found and the name is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(blogPostTitle));
        assertTrue(searchPage.isNameHighlighted(blogPostTitle));
    }

    @TestRail (id = "C42550")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "tobefixed" })
    public void highlightSearchByContentOnDifferentPage()
    {
        log.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        log.info("Step 2: Type one of the contents of the doc file and click search.");
        advancedSearchPage.typeKeywords("Page2");
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the file is found and the content is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(C42550testFile));
        assertTrue(searchPage.is_ContentHighlighted("Page2"));
        assertFalse(searchPage.is_ContentHighlighted("Page1"));
        assertFalse(searchPage.is_ContentHighlighted("Page3"));
        assertFalse(searchPage.is_ContentHighlighted("Page4"));
    }

    @TestRail (id = "C42556")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByPropertyName()
    {
        log.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        log.info("Step 2: Type the name of the file and click search.");
        advancedSearchPage.typeKeywords("name:" + docName);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the file is found and the name is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(docName));
        assertTrue(searchPage.isNameHighlighted(docName));
    }

    @TestRail (id = "C42557")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByPropertyTitle()
    {
        log.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        log.info("Step 2: Type the title of the file and click search.");
        advancedSearchPage.typeKeywords("title:" + docTitle);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the file is found and the title is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(docName));
        assertTrue(searchPage.isTitleHighlighted(docTitle));
    }

    @TestRail (id = "C42558")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByPropertyDescription()
    {
        log.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        log.info("Step 2: Type the description of the file and click search.");
        advancedSearchPage.typeKeywords("description:" + docDescription);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the file is found and the description is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(docName));
        assertTrue(searchPage.isDescriptionHighlighted(docDescription));
        log.info("Step 4: Type some text that is found in the file and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords("TEXT:" + C42558file);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 5: Verify that the file is found and the text is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(C42558file));
        assertTrue(searchPage.isContentHighlighted(C42558file));
        assertTrue(searchPage.isNameHighlighted(C42558file));
    }

    @TestRail (id = "C42560")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "SearchTests" })
    public void highlightSearchWithWildcards()
    {
        log.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        log.info("Step 2: Type the '?' wildcard followed by the name of the file and click search.");
        advancedSearchPage.typeKeywords("?" + "42560file");
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the file is found and the name is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(C42560file));
        assertTrue(searchPage.isNameHighlighted(C42560file));
        log.info("Step 4: Type the '*' wildcard followed by a part of the name of the file and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords("*" + "42560");
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 5: Verify that the files are found and the name is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(C42560file));
        assertTrue(searchPage.isResultFoundWithList(C42560file2));
        assertTrue(searchPage.isNameHighlighted("42560"));
        log.info("Step 6: Type the '=' wildcard followed by the name of the file and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords("=" + "C42560file");
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 7: Verify that the file is found and the name is highlighted.");
//        assertTrue(searchPage.isResultFoundWithRetry(C42560file));
        assertFalse(searchPage.isResultsDisplayedInSearch(C42560file2));
//        assertTrue(searchPage.isNameHighlighted(C42560file));
        log.info("Step 8: Type the name of the file and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords(C42560file);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 9: Verify that the files are found and the name is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(C42560file));
        assertTrue(searchPage.isResultFoundWithList(C42560file2));
        assertTrue(searchPage.isNameHighlighted(C42560file));
    }

    @TestRail (id = "C42561")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchCMISStyleProperty()
    {
        log.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        log.info("Step 2: Type 'cm_name:' followed by the name of the file and click search.");
        advancedSearchPage.typeKeywords("cm_name:" + docName);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the file is found and the content is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(docName));
        assertTrue(searchPage.isNameHighlighted(docName));
        log.info("Step 4: Type '@cm_name:' followed by the name of the file and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords("@cm_name:" + docName);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 5: Verify that the file is found and the content is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(docName));
        assertTrue(searchPage.isNameHighlighted(docName));
    }

    @TestRail (id = "C42562")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchWithConjunctions()
    {
        log.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        log.info("Step 2: Type 'AND' conjunction between 2 search terms and click search.");
        advancedSearchPage.typeKeywords(C42562SearchTerm + " AND " + C42562file1);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the file is found and the content is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(C42562file1));
        assertTrue(searchPage.isNameHighlighted(C42562file1));
        log.info("Step 4: Type 'and' conjunction between 2 search terms and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords(C42562SearchTerm + " and " + C42562file2);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the file is found and the content is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(C42562file2));
        assertTrue(searchPage.isNameHighlighted(C42562file2));
        log.info("Step 5: Type 2 search terms and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords(C42562file1 + " " + C42562SearchTerm);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 6: Verify that the file is found and the content is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(C42562file1));
        assertTrue(searchPage.isNameHighlighted(C42562file1));
    }

    @TestRail (id = "C42563")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "SearchTests" })
    public void highlightSearchWithDisjunctions()
    {
        log.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        log.info("Step 2: Type 'OR' disjunction between 2 search terms and click search.");
        advancedSearchPage.typeKeywords(C42562file1 + " OR " + C42562file2);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the files are found and the names are highlighted.");
        assertTrue(searchPage.isResultFoundWithList(C42562file1));
        assertTrue(searchPage.isResultFoundWithList(C42562file2));
        assertTrue(searchPage.isNameHighlighted(C42562file1));
        assertTrue(searchPage.isNameHighlighted(C42562file2));
        log.info("Step 4: Type 'or' disjunction between 2 search terms and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords(C42562file1 + " or " + C42562file2);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 5: Verify that the files are found and the names are highlighted.");
        assertTrue(searchPage.isResultFoundWithList(C42562file1));
        assertTrue(searchPage.isResultFoundWithList(C42562file2));
        assertTrue(searchPage.isNameHighlighted(C42562file1));
        assertTrue(searchPage.isNameHighlighted(C42562file2));
    }

    @TestRail (id = "C42564")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchWithNegation()
    {
        log.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        log.info("Step 2: Type 'NOT' negation between 2 search terms and click search.");
        advancedSearchPage.typeKeywords(C42564file1 + " NOT " + "big");
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the file is found and the title is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(C42564file1));
        assertFalse(searchPage.isResultsDisplayedInSearch(C42564file2));
        assertTrue(searchPage.isNameHighlighted(C42564file1));
        log.info("Step 4: Type '!' negation between 2 search terms and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords(C42564file1 + " !" + "big");
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the file is found and the title is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(C42564file1));
        assertFalse(searchPage.isResultsDisplayedInSearch(C42564file2));
        assertTrue(searchPage.isNameHighlighted(C42564file1));
        log.info("Step 5: Type '-' negation between 2 search terms and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords(C42564file1 + " -" + "big");
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 6: Verify that the file is found and the title is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(C42564file1));
        assertFalse(searchPage.isResultsDisplayedInSearch(C42564file2));
        assertTrue(searchPage.isNameHighlighted(C42564file1));
    }

    @TestRail (id = "C42549")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByTermFoundInSiteName()
    {
        log.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        log.info("Step 2: Type the name of the file and click search.");
        advancedSearchPage.typeName(C42549file);
        advancedSearchPage.clickFirstSearchButton();
        log.info("Step 3: Verify that the file is found and the name is highlighted.");
        assertTrue(searchPage.isResultFoundWithList(C42549file));
        assertTrue(searchPage.isNameHighlighted(C42549file));
    }
}
