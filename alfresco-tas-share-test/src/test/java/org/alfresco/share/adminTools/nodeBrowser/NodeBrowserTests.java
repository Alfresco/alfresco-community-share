package org.alfresco.share.adminTools.nodeBrowser;

import org.alfresco.po.share.user.admin.adminTools.NodeBrowserPage;
import org.alfresco.share.BaseShareWebTests;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NodeBrowserTests extends BaseShareWebTests
{
    private NodeBrowserPage nodeBrowserPage;

    private SiteModel site;
    private FileModel file;
    private final String content = RandomStringUtils.randomAlphanumeric(10);
    private String cmisSearchTerm;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        nodeBrowserPage = new NodeBrowserPage(browser);
        setupAuthenticatedSession(dataUser.getAdminUser());
    }

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        site = dataSite.usingAdmin().createPublicRandomSite();
        file = FileModel.getRandomFileModel(FileType.XML, content);

        cmisApi.authenticateUser(dataUser.getAdminUser())
            .usingSite(site).createFile(file);
        cmisSearchTerm = String.format("SELECT * from cmis:document where cmis:name =  '%s'", file.getName());
    }

    @TestRail (id = "C9309")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void luceneSearch()
    {
        nodeBrowserPage.navigate();
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
        nodeBrowserPage.navigate();
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

        nodeBrowserPage.navigate();
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
        nodeBrowserPage.navigate();
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
        nodeBrowserPage.navigate();
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
        nodeBrowserPage.navigate();
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

    @Test (groups = { TestGroup.SHARE, TestGroup.ADMIN_TOOLS })
    public void executeCustomNodeSearch()
    {
        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.STORE_ROOT)
            .clickSearch()
            .assertReferenceContainsValue("workspace://SpacesStore/");
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.ADMIN_TOOLS })
    public void getSearchResultsNoResults()
    {
        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(NodeBrowserPage.SEARCH_TYPE.LUCENE)
            .searchFor(String.valueOf(System.currentTimeMillis()))
            .clickSearch()
            .assertNoItemsFoundIsDisplayed()
            .assertNoItemsFoundLabelIsCorrect();
    }

    @AfterClass (alwaysRun = true)
    public void afterClass()
    {
        dataSite.usingAdmin().deleteSite(site);
    }
}
