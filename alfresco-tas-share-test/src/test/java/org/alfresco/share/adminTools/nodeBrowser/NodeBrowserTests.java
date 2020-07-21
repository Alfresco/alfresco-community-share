package org.alfresco.share.adminTools.nodeBrowser;

import org.alfresco.po.share.user.admin.adminTools.NodeBrowserPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * UI tests for Admin Tools > Node browser page
 */
public class NodeBrowserTests extends ContextAwareWebTest
{
    @Autowired
    private NodeBrowserPage nodeBrowserPage;

    private SiteModel site;
    private FileModel file;
    private final String content = RandomStringUtils.randomAlphanumeric(10);
    private String cmisSearchTerm;

    @BeforeClass (alwaysRun = true)
    public void beforeClass()
    {
        site = dataSite.usingAdmin().createPublicRandomSite();
        file = FileModel.getRandomFileModel(FileType.XML, content);

        cmisApi.authenticateUser(dataUser.getAdminUser()).usingSite(site)
            .createFile(file);
        cmisSearchTerm = String.format("SELECT * from cmis:document where cmis:name =  '%s'", file.getName());

        setupAuthenticatedSession(dataUser.getAdminUser());
        nodeBrowserPage.navigate();
    }

    @AfterClass (alwaysRun = true)
    public void afterClass()
    {
        dataSite.usingAdmin().deleteSite(site);
    }

    @TestRail (id = "C9309")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void luceneSearch()
    {
        LOG.info("Step 1: Do a 'lucene' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.LUCENE)
            .searchFor(content)
            .clickSearch()
            .assertParentForFileIsSite(file, site)
            .assertReferenceForFileIsCorrect(file);
    }

    @TestRail (id = "C9307")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void nodeRefSearch()
    {
        LOG.info("Step 1: Do a 'nodeRef' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.NODEREF)
            .searchFor("workspace://SpacesStore/" + file.getNodeRefWithoutVersion())
            .clickSearch()
            .assertParentForFileIsSite(file, site)
            .assertReferenceForFileIsCorrect(file);
    }

    @TestRail (id = "C9308")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void xpathSearch()
    {
        String xpathSearchTerm = String.format("/app:company_home/st:sites/cm:%s/cm:documentLibrary/cm:%s",
            site.getId(), file.getName());

        LOG.info("Step 1: Do a 'xpath' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.XPATH)
            .searchFor(xpathSearchTerm)
            .clickSearch()
            .assertParentForFileIsSite(file, site)
            .assertReferenceForFileIsCorrect(file);
    }

    @TestRail (id = "C9310")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void ftsAlfrescoSearch()
    {
        LOG.info("Step 1: Do a 'fts-alfresco' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.FTS_ALFRESCO)
            .searchFor("cm:name:" + file.getName())
            .clickSearch()
            .assertParentForFileIsSite(file, site)
            .assertReferenceForFileIsCorrect(file);
    }

    @TestRail (id = "C9311")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void cmisStrictSearch()
    {
        LOG.info("Step 1: Do a 'cmis-strict' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.CMIS_STRICT)
            .searchFor(cmisSearchTerm)
            .clickSearch()
            .assertParentForFileIsSite(file, site)
            .assertReferenceForFileIsCorrect(file);
    }

    @TestRail (id = "C9312")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void cmisAlfrescoSearch()
    {
        LOG.info("Step 1: Do a 'cmis-alfresco' search.");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.CMIS_ALFRESCO)
            .searchFor(cmisSearchTerm)
            .clickSearch()
            .assertParentForFileIsSite(file, site)
            .assertReferenceForFileIsCorrect(file);
    }

    @TestRail (id = "C9306")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void checkNodeBrowserPage()
    {
        nodeBrowserPage.navigate();
        nodeBrowserPage.assertSearchTypeIsSelected(NodeBrowserPage.SEARCH_TYPE.FTS_ALFRESCO)
            .assertStoreTypeIsSelected(NodeBrowserPage.SELECT_STORE.WORKSPACE_SPACES_STORE)
            .assertAllColumnsAreDisplayed()
            .assertSearchButtonIsDisplayed();
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.ADMIN_TOOLS, "Acceptance" })
    public void executeCustomNodeSearch()
    {
        LOG.info("Step 1: Navigate to NOde Browser and perform custom node search");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.STORE_ROOT)
            .clickSearch()
            .assertRowContains("workspace://SpacesStore/");
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.ADMIN_TOOLS, "Acceptance" })
    public void getSearchResultsNoResults()
    {
        LOG.info("Step 1: Navigate to Node Browser page and perform a search that will not return results");
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.LUCENE)
            .searchFor(String.valueOf(System.currentTimeMillis()))
            .clickSearch()
            .assertRowContains(language.translate("nodeBrowser.noItemsFound"));
    }
}
