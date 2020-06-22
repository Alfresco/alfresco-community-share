package org.alfresco.share.searching;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.blog.CreateBlogPostPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
public class SearchHighlightTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;
    @Autowired
    EditPropertiesDialog editFilePropertiesDialog;
    @Autowired
    BlogPostListPage blogPage;
    @Autowired
    CreateBlogPostPage createBlogPost;
    String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    String userName = "highlightSearch-" + uniqueIdentifier;
    String firstName = "FirstName";
    String lastName = "LastName";
    String siteName = "HighlightSite" + uniqueIdentifier;
    String description = "HighlightDescription-" + uniqueIdentifier;
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
    @Autowired
    private AdvancedSearchPage advancedSearchPage;
    @Autowired
    private SearchPage searchPage;
    private String blogPostTitle = "HighlightBlogTitle" + uniqueIdentifier;

    @BeforeClass (alwaysRun = true)
    public void createPrecondition()
    {
        List<DashboardCustomization.Page> pagesToAdd = new ArrayList<DashboardCustomization.Page>();
        pagesToAdd.add(DashboardCustomization.Page.BLOG);
        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
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
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(docName, ItemActions.EDIT_PROPERTIES, editFilePropertiesDialog);
        editFilePropertiesDialog.setTitle(docTitle);
        editFilePropertiesDialog.setDescription(docDescription);
        editFilePropertiesDialog.clickSave();
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass
    public void removeAddedFiles()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C42544")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByName()
    {
        LOG.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        LOG.info("Step 2: Type the name of the file and click search.");
        advancedSearchPage.typeName(docName);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the file is found and the name is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(docName));
        assertTrue(searchPage.isNameHighlighted(docName));
    }

    @TestRail (id = "C42545")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByTitle()
    {
        LOG.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        LOG.info("Step 2: Type the title of the file and click search.");
        advancedSearchPage.typeTitle(docTitle);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the file is found and the title is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(docName));
        assertTrue(searchPage.isTitleHighlighted(docTitle));
    }

    @TestRail (id = "C42546")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })

    public void highlightSearchByDescription()
    {
        LOG.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        LOG.info("Step 2: Type the description of the file and click search.");
        advancedSearchPage.typeDescription(docDescription);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the file is found and the description is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(docName));
        assertTrue(searchPage.isDescriptionHighlighted(docDescription));
    }

    @TestRail (id = "C42547")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByContent()
    {
        LOG.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        LOG.info("Step 2: Type the content of the file and click search.");
        advancedSearchPage.typeKeywords(docContent);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the file is found and the content is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(docName));
        assertTrue(searchPage.isContentHighlighted(docContent));
    }

    @TestRail (id = "C42548")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByNameOfBlog()
    {
        LOG.info("Step 1: Login with username and navigate to Blog page.");
        blogPage.navigate(siteName);
        LOG.info("Step 2: Click 'New Post' button.");
        blogPage.clickNewPostButton();
        Assert.assertEquals(createBlogPost.getPageTitle(), "Create Blog Post");
        LOG.info("Step 3: Type a Title for the post.");
        createBlogPost.sendTitleInput(blogPostTitle);
        LOG.info("Step 4: Click publish internally button.");
        createBlogPost.clickPublishInternally();
        LOG.info("Step 5: Navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        LOG.info("Step 6: Type the title of the Blog and click search.");
        advancedSearchPage.typeKeywords(blogPostTitle);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 7: Verify that the Blog is found and the name is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(blogPostTitle));
        assertTrue(searchPage.isNameHighlighted(blogPostTitle));
    }

    @TestRail (id = "C42550")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, "tobefixed" })
    public void highlightSearchByContentOnDifferentPage()
    {
        LOG.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        LOG.info("Step 2: Type one of the contents of the doc file and click search.");
        advancedSearchPage.typeKeywords("Page2");
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the file is found and the content is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(C42550testFile));
        assertTrue(searchPage.isContentHighlighted("Page2"));
        assertFalse(searchPage.isContentHighlighted("Page1"));
        assertFalse(searchPage.isContentHighlighted("Page3"));
        assertFalse(searchPage.isContentHighlighted("Page4"));
    }

    @TestRail (id = "C42556")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByPropertyName()
    {
        LOG.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        LOG.info("Step 2: Type the name of the file and click search.");
        advancedSearchPage.typeKeywords("name:" + docName);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the file is found and the name is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(docName));
        assertTrue(searchPage.isNameHighlighted(docName));
    }

    @TestRail (id = "C42557")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByPropertyTitle()
    {
        LOG.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        LOG.info("Step 2: Type the title of the file and click search.");
        advancedSearchPage.typeKeywords("title:" + docTitle);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the file is found and the title is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(docName));
        assertTrue(searchPage.isTitleHighlighted(docTitle));
    }

    @TestRail (id = "C42558")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByPropertyDescription()
    {
        LOG.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        LOG.info("Step 2: Type the description of the file and click search.");
        advancedSearchPage.typeKeywords("description:" + docDescription);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the file is found and the description is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(docName));
        assertTrue(searchPage.isDescriptionHighlighted(docDescription));
        LOG.info("Step 4: Type some text that is found in the file and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords("TEXT:" + C42558file);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 5: Verify that the file is found and the text is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(C42558file));
        assertTrue(searchPage.isContentHighlighted(C42558file));
        assertTrue(searchPage.isNameHighlighted(C42558file));
    }

    @TestRail (id = "C42560")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchWithWildcards()
    {
        LOG.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        LOG.info("Step 2: Type the '?' wildcard followed by the name of the file and click search.");
        advancedSearchPage.typeKeywords("?" + "42560file");
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the file is found and the name is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(C42560file));
        assertTrue(searchPage.isNameHighlighted(C42560file));
        LOG.info("Step 4: Type the '*' wildcard followed by a part of the name of the file and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords("*" + "42560");
        advancedSearchPage.click1stSearch();
        LOG.info("Step 5: Verify that the files are found and the name is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(C42560file));
        assertTrue(searchPage.isResultFoundWithRetry(C42560file2));
        assertTrue(searchPage.isNameHighlighted("42560"));
        LOG.info("Step 6: Type the '=' wildcard followed by the name of the file and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords("=" + "C42560file");
        advancedSearchPage.click1stSearch();
        LOG.info("Step 7: Verify that the file is found and the name is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(C42560file));
        assertFalse(searchPage.isResultFoundWithRetry(C42560file2));
        assertTrue(searchPage.isNameHighlighted(C42560file));
        LOG.info("Step 8: Type the name of the file and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords(C42560file);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 9: Verify that the files are found and the name is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(C42560file));
        assertTrue(searchPage.isResultFoundWithRetry(C42560file2));
        assertTrue(searchPage.isNameHighlighted(C42560file));
    }

    @TestRail (id = "C42561")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchCMISStyleProperty()
    {
        LOG.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        LOG.info("Step 2: Type 'cm_name:' followed by the name of the file and click search.");
        advancedSearchPage.typeKeywords("cm_name:" + docName);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the file is found and the content is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(docName));
        assertTrue(searchPage.isNameHighlighted(docName));
        LOG.info("Step 4: Type '@cm_name:' followed by the name of the file and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords("@cm_name:" + docName);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 5: Verify that the file is found and the content is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(docName));
        assertTrue(searchPage.isNameHighlighted(docName));
    }

    @Bug (id = "SHA-2221")
    @TestRail (id = "C42562")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchWithConjunctions()
    {
        LOG.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        LOG.info("Step 2: Type 'AND' conjunction between 2 search terms and click search.");
        advancedSearchPage.typeKeywords(C42562SearchTerm + " AND " + C42562file1);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the file is found and the content is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(C42562file1));
        assertTrue(searchPage.isNameHighlighted(C42562file1));
        LOG.info("Step 4: Type 'and' conjunction between 2 search terms and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords(C42562SearchTerm + " and " + C42562file2);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the file is found and the content is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(C42562file2));
        assertTrue(searchPage.isNameHighlighted(C42562file2));
        LOG.info("Step 5: Type 2 search terms and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords(C42562file1 + " " + C42562SearchTerm);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 6: Verify that the file is found and the content is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(C42562file1));
        assertTrue(searchPage.isNameHighlighted(C42562file1));
    }

    @TestRail (id = "C42563")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchWithDisjunctions()
    {
        LOG.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        LOG.info("Step 2: Type 'OR' disjunction between 2 search terms and click search.");
        advancedSearchPage.typeKeywords(C42562file1 + " OR " + C42562file2);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the files are found and the names are highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(C42562file1));
        assertTrue(searchPage.isResultFoundWithRetry(C42562file2));
        assertTrue(searchPage.isNameHighlighted(C42562file1));
        assertTrue(searchPage.isNameHighlighted(C42562file2));
        LOG.info("Step 4: Type 'or' disjunction between 2 search terms and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords(C42562file1 + " or " + C42562file2);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 5: Verify that the files are found and the names are highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(C42562file1));
        assertTrue(searchPage.isResultFoundWithRetry(C42562file2));
        assertTrue(searchPage.isNameHighlighted(C42562file1));
        assertTrue(searchPage.isNameHighlighted(C42562file2));
    }

    @TestRail (id = "C42564")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchWithNegation()
    {
        LOG.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        LOG.info("Step 2: Type 'NOT' negation between 2 search terms and click search.");
        advancedSearchPage.typeKeywords(C42564file1 + " NOT " + "big");
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the file is found and the title is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(C42564file1));
        assertFalse(searchPage.isResultFoundWithRetry(C42564file2));
        assertTrue(searchPage.isNameHighlighted(C42564file1));
        LOG.info("Step 4: Type '!' negation between 2 search terms and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords(C42564file1 + " !" + "big");
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the file is found and the title is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(C42564file1));
        assertFalse(searchPage.isResultFoundWithRetry(C42564file2));
        assertTrue(searchPage.isNameHighlighted(C42564file1));
        LOG.info("Step 5: Type '-' negation between 2 search terms and click search.");
        advancedSearchPage.navigate();
        advancedSearchPage.typeKeywords(C42564file1 + " -" + "big");
        advancedSearchPage.click1stSearch();
        LOG.info("Step 6: Verify that the file is found and the title is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(C42564file1));
        assertFalse(searchPage.isResultFoundWithRetry(C42564file2));
        assertTrue(searchPage.isNameHighlighted(C42564file1));
    }

    @TestRail (id = "C42549")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void highlightSearchByTermFoundInSiteName()
    {
        LOG.info("Step 1: Login with username and navigate to Advanced Search page.");
        advancedSearchPage.navigate();
        LOG.info("Step 2: Type the name of the file and click search.");
        advancedSearchPage.typeName(C42549file);
        advancedSearchPage.click1stSearch();
        LOG.info("Step 3: Verify that the file is found and the name is highlighted.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(searchPage.searchResult);
        assertTrue(searchPage.isResultFoundWithRetry(C42549file));
        assertTrue(searchPage.isNameHighlighted(C42549file));
    }
}
