package org.alfresco.share.searching.advancedSearch;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.apache.chemistry.opencmis.client.api.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertTrue;

/**
 * Created by Claudia Agache on 8/29/2016.
 */
public class AdvancedSearchOperatorsTests extends ContextAwareWebTest
{
    @Autowired
    Toolbar toolbar;

    @Autowired
    MyDocumentsDashlet myDocumentsDashlet;

    @Autowired
    SearchPage searchPage;

    @Autowired
    UserDashboardPage userDashboardPage;

    private String user1 = String.format("user1%s", RandomData.getRandomAlphanumeric());
    private String site1 = String.format("Site1%s", RandomData.getRandomAlphanumeric());
    private String docC7210 = site1 + "docC7210";
    private String docC7210_content = "C7210 C7210 C7210";
    private String docC5991 = "11123";
    private String docC7407 = "this is an item";
    private String specificWord = site1 + "docC7288";
    private String docC7288_2 = site1 + "Doc2";
    private String docC7288_3 = site1 + "Doc3";
    private String docC7288_4 = site1 + "Doc4";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1, user1);
        siteService.create(user1, password, domain, site1, site1 + " description", SiteService.Visibility.PUBLIC);

        contentService.createDocument(user1, password, site1, CMISUtil.DocumentType.TEXT_PLAIN, docC7210, docC7210_content);
        contentService.createDocument(user1, password, site1, CMISUtil.DocumentType.TEXT_PLAIN, specificWord, "Create file with specific name");
        contentService.createDocument(user1, password, site1, CMISUtil.DocumentType.TEXT_PLAIN, docC7288_2, "Create file with specific title");
        contentService.createDocument(user1, password, site1, CMISUtil.DocumentType.TEXT_PLAIN, docC7288_3, "Create file with specific description");
        contentService.createDocument(user1, password, site1, CMISUtil.DocumentType.TEXT_PLAIN, docC7288_4, specificWord);
        contentService.createDocument(user1, password, site1, CMISUtil.DocumentType.TEXT_PLAIN, docC5991, "wildcards");
        contentService.createDocument(user1, password, site1, CMISUtil.DocumentType.TEXT_PLAIN, docC7407, "Explicit spans/positions");

        //add specific title for docC7288_2 file
        Session session = contentAction.getCMISSession(user1, password);
        String contentNodeRef = contentAction.getNodeRef(session, site1, docC7288_2);
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("cm:title", specificWord);
        contentAction.addProperties(session, contentNodeRef, propertiesMap);

        //add specific description for docC7288_3
        propertiesMap.remove("cm:title", specificWord);
        contentNodeRef = contentAction.getNodeRef(session, site1, docC7288_3);
        propertiesMap.put("cm:description", specificWord);
        contentAction.addProperties(session, contentNodeRef, propertiesMap);

        setupAuthenticatedSession(user1, password);
        myDocumentsDashlet.waitForDocument();
    }

    @AfterClass
    public void removeAddedFiles()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
        siteService.delete(adminUser, adminPassword, site1);

    }

    @TestRail (id = "C7210")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void recurringPhrases()
    {
        userDashboardPage.navigate(user1);

        LOG.info("STEP 1: Search with: \"file_name\"^number_of_repetitions (e.g: \"C7210\"^3)");
        toolbar.search("\"C7210\"^3");
        assertTrue(searchPage.isResultFound(docC7210), "Searched content is found and displayed in search results list");
    }

    @TestRail (id = "C7288")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void extendedSearch()
    {
        userDashboardPage.navigate(user1);
        LOG.info("STEP 1: Search for " + specificWord);
        toolbar.search(specificWord);
        assertTrue(searchPage.isResultFound(specificWord), "The file with name containing the searched query is displayed in search result list");
        assertTrue(searchPage.isResultFound(docC7288_2), "The file with title containing the searched query is displayed in search result list");
        assertTrue(searchPage.isResultFound(docC7288_3), "The file with description containing the searched query is displayed in search result list");
        assertTrue(searchPage.isResultFound(docC7288_4), "The file with content containing the searched query is displayed in search result list");
    }

    @TestRail (id = "C5991")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void wildcards()
    {
        userDashboardPage.navigate(user1);
        LOG.info("STEP 1: Search with '*' wildcard, e.g: *23");
        toolbar.search("*23");
        assertTrue(searchPage.isResultFound(docC5991), "'*' wildcard replaces zero, one or more characters in the searched query");

        LOG.info("STEP 2: Search with '*' wildcard and quotes, e.g: \"**23\"");
        toolbar.search("\"**23\"");
        assertTrue(searchPage.isResultFound(docC5991), "Searched content is displayed in results list");

        LOG.info("STEP 3: Search with one '?' wildcard, e.g: 11?23");
        toolbar.search("11?23");
        assertTrue(searchPage.isResultFound(docC5991), "'?' wildcard replaces a single character in the searched query");

        LOG.info("STEP 4: Search with multiple '?' wildcard, e.g: 1???3");
        toolbar.search("1???3");
        assertTrue(searchPage.isResultFound(docC5991), "Each '?' wildcard replaces a character in the searched query");

        LOG.info("STEP 5: Search by a query beginning with '='");
        toolbar.search("=" + specificWord);
        assertTrue(searchPage.isResultFound(specificWord), "The file with name containing the searched query is displayed in search result list");
        assertTrue(searchPage.isResultFound(docC7288_2), "The file with title containing the searched query is displayed in search result list");
        assertTrue(searchPage.isResultFound(docC7288_3), "The file with description containing the searched query is displayed in search result list");
        assertTrue(searchPage.isResultFound(docC7288_4), "The file with content containing the searched query is displayed in search result list");

        LOG.info("STEP 6: Search by query in quotes, e.g: \"11123\"");
        toolbar.search("\"11123\"");
        assertTrue(searchPage.isResultFound(docC5991), "Content corresponding to the exact phrase in quotes, e.g.:\"11123\", is displayed in search results list");
    }

    @TestRail (id = "C7407")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void explicitSpansPositions()
    {
        userDashboardPage.navigate(user1);
        LOG.info("STEP 1: Fill in search field with any query containing span and position symbol, e.g: this[^] item[$] , and press 'Enter'");
        toolbar.search("\"this[^] item[$]\"");
        assertTrue(searchPage.isResultFound(docC7407), "Item starting with word before [^] and ending by the word before [$] is displayed in result list, e.g: \"this is an item\"");
    }
}
