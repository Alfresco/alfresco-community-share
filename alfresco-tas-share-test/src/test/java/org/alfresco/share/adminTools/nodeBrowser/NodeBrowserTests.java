package org.alfresco.share.adminTools.nodeBrowser;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.user.admin.adminTools.NodeBrowserPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * @author Razvan.Dorobantu
 */
public class NodeBrowserTests extends ContextAwareWebTest
{
    @Autowired
    NodeBrowserPage nodeBrowserPage;

    private String description = "nodeBrowserTests" + DataUtil.getUniqueIdentifier();
    private String siteName = "nodeBrowserTests" + DataUtil.getUniqueIdentifier();
    private String fileName = "nodeBrowserTests.xml" + DataUtil.getUniqueIdentifier();
    private String content = "nodeBrowserTestsContent";
    private String xpathSearchTerm = String.format("/app:company_home/st:sites/cm:%s/cm:documentLibrary/cm:%s",siteName,fileName);
    private String cmisSearchTerm = String.format("SELECT * from cmis:document where cmis:name =  '%s'",fileName);

    @BeforeClass(alwaysRun = true)
    public void beforeClass()
    {
        siteService.create(adminUser, adminPassword, domain, siteName, description, Site.Visibility.PUBLIC);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.XML, fileName, content);
        LOG.info("Step 1: Login as administrator and navigate to Admin Tools - Node Browser page.");
        setupAuthenticatedSession(adminUser, adminPassword);
        nodeBrowserPage.navigate();
    }

    @TestRail(id = "C9309")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void luceneSearch()
    {
        LOG.info("Step 1: Do a 'lucene' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.LUCENE);
        nodeBrowserPage.selectStoreType(NodeBrowserPage.SELECT_STORE.WORKSPACE_SPACES_STORE);
        nodeBrowserPage.writeInSearchInput(content);
        nodeBrowserPage.clickSearchButton();

        LOG.info("Step 2: Verify if the file created in precondition is displayed and its parent is correct.");
        assertTrue(nodeBrowserPage.getParentFor(fileName).contains(siteName), String.format("Parent result for %s is wrong.", fileName));
    }

    @TestRail(id = "C9307")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void nodeRefSearch()
    {
        LOG.info("Step 1: Do a 'nodeRef' search.");
        String nodeRef = contentService.getNodeRef(adminUser, adminPassword, siteName, fileName);
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.NODEREF);
        nodeBrowserPage.selectStoreType(NodeBrowserPage.SELECT_STORE.WORKSPACE_SPACES_STORE);
        nodeBrowserPage.writeInSearchInput("workspace://SpacesStore/" + nodeRef);
        nodeBrowserPage.clickSearchButton();

        LOG.info("Step 2: Verify if the file created in precondition is displayed and its parent is correct.");
        assertTrue(nodeBrowserPage.getParentFor(fileName).contains(siteName), String.format("Parent result for %s is wrong.", fileName));
    }

    @TestRail(id = "C9308")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void xpathSearch()
    {
        LOG.info("Step 1: Do a 'xpath' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.XPATH);
        nodeBrowserPage.selectStoreType(NodeBrowserPage.SELECT_STORE.WORKSPACE_SPACES_STORE);
        nodeBrowserPage.writeInSearchInput(xpathSearchTerm);
        nodeBrowserPage.clickSearchButton();

        LOG.info("Step 2: Verify if the file created in precondition is displayed and its parent is correct.");
        assertTrue(nodeBrowserPage.getParentFor(fileName).contains(siteName), String.format("Parent result for %s is wrong.", fileName));
    }

    @TestRail(id = "C9310")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void ftsAlfrescoSearch()
    {
        LOG.info("Step 1: Do a 'fts-alfresco' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.FTS_ALFRESCO);
        nodeBrowserPage.selectStoreType(NodeBrowserPage.SELECT_STORE.WORKSPACE_SPACES_STORE);
        nodeBrowserPage.writeInSearchInput("cm:name:" + fileName);
        nodeBrowserPage.clickSearchButton();

        LOG.info("Step 2: Verify if the file created in precondition is displayed and its parent is correct.");
        assertTrue(nodeBrowserPage.getParentFor(fileName).contains(siteName), String.format("Parent result for %s is wrong.", fileName));
    }

    @TestRail(id = "C9311")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void cmisStrictSearch()
    {
        LOG.info("Step 1: Do a 'cmis-strict' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.CMIS_STRICT);
        nodeBrowserPage.selectStoreType(NodeBrowserPage.SELECT_STORE.WORKSPACE_SPACES_STORE);
        nodeBrowserPage.writeInSearchInput(cmisSearchTerm);
        nodeBrowserPage.clickSearchButton();

        LOG.info("Step 2: Verify if the file created in precondition is displayed and its parent is correct.");
        assertTrue(nodeBrowserPage.getParentFor(fileName).contains(siteName), String.format("Parent result for %s is wrong.", fileName));
    }

    @TestRail(id = "C9312")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void cmisAlfrescoSearch()
    {
        LOG.info("Step 1: Do a 'cmis-alfresco' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.CMIS_ALFRESCO);
        nodeBrowserPage.selectStoreType(NodeBrowserPage.SELECT_STORE.WORKSPACE_SPACES_STORE);
        nodeBrowserPage.writeInSearchInput(cmisSearchTerm);
        nodeBrowserPage.clickSearchButton();

        LOG.info("Step 2: Verify if the file created in precondition is displayed and its parent is correct.");
        assertTrue(nodeBrowserPage.getParentFor(fileName).contains(siteName), String.format("Parent result for %s is wrong.", fileName));
    }

    @TestRail(id = "C9306")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void checkNodeBrowserPage()
    {
        LOG.info("Step 2: Verify if the items on the page are displayed correctly.");
        assertTrue(nodeBrowserPage.isSearchTypeSelected(NodeBrowserPage.SEARCH_TYPE.FTS_ALFRESCO));
        assertTrue(nodeBrowserPage.isStoreTypeSelected(NodeBrowserPage.SELECT_STORE.WORKSPACE_SPACES_STORE));
        assertTrue(nodeBrowserPage.isNameColumnPresent());
        assertTrue(nodeBrowserPage.isParentColumnPresent());
        assertTrue(nodeBrowserPage.isReferenceColumnPresent());
        assertTrue(nodeBrowserPage.isSearchButtonPresent());
    }
}
