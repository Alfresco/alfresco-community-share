package org.alfresco.share.adminTools.nodeBrowser;

import org.alfresco.po.share.user.admin.adminTools.NodeBrowserPage;
import org.alfresco.po.share.user.admin.adminTools.NodeBrowserPage.SearchType;
import org.alfresco.po.share.user.admin.adminTools.NodeBrowserPage.StoreType;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;

public class NodeBrowserTests extends BaseTest
{
    private NodeBrowserPage nodeBrowserPage;

    private ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private ThreadLocal<FileModel> file = new ThreadLocal<>();
    private final String content = RandomStringUtils.randomAlphanumeric(10);

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        nodeBrowserPage = new NodeBrowserPage(webDriver);

        site.set(dataSite.usingAdmin().createPublicRandomSite());
        file.set(FileModel.getRandomFileModel(FileType.XML, content));
        getCmisApi().authenticateUser(getAdminUser()).usingSite(site.get()).createFile(file.get());

        setupAuthenticatedSession(dataUser.getAdminUser());
    }

    @TestRail (id = "C9309")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void luceneSearch()
    {
        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(SearchType.LUCENE)
            .searchFor(content)
            .clickSearch()
            .assertParentForFileIsSite(file.get(), site.get())
            .assertReferenceForFileIsCorrect(file.get());
    }

    @TestRail (id = "C9307")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void nodeRefSearch()
    {
        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(SearchType.NODEREF)
            .searchFor("workspace://SpacesStore/" + file.get().getNodeRefWithoutVersion())
            .clickSearch()
            .assertParentForFileIsSite(file.get(), site.get())
            .assertReferenceForFileIsCorrect(file.get());
    }

    @TestRail (id = "C9308")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void xpathSearch()
    {
        String xpathSearchTerm = String.format("/app:company_home/st:sites/cm:%s/cm:documentLibrary/cm:%s",
            site.get().getId(), file.get().getName());

        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(SearchType.XPATH)
            .searchFor(xpathSearchTerm)
            .clickSearch()
            .assertParentForFileIsSite(file.get(), site.get())
            .assertReferenceForFileIsCorrect(file.get());
    }

    @TestRail (id = "C9310")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void ftsAlfrescoSearch()
    {
        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(SearchType.FTS_ALFRESCO)
            .searchFor("cm:name:" + file.get().getName())
            .clickSearch()
            .assertParentForFileIsSite(file.get(), site.get())
            .assertReferenceForFileIsCorrect(file.get());
    }

    @TestRail (id = "C9311")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void cmisStrictSearch()
    {
        String cmisSearchTerm = String.format("SELECT * from cmis:document where cmis:name =  '%s'", file.get().getName());
        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(SearchType.CMIS_STRICT)
            .searchFor(cmisSearchTerm)
            .clickSearch()
            .assertParentForFileIsSite(file.get(), site.get())
            .assertReferenceForFileIsCorrect(file.get());
    }

    @TestRail (id = "C9312")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void cmisAlfrescoSearch()
    {
        String cmisSearchTerm = String.format("SELECT * from cmis:document where cmis:name =  '%s'", file.get().getName());
        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(SearchType.CMIS_ALFRESCO)
            .searchFor(cmisSearchTerm)
            .clickSearch()
            .assertParentForFileIsSite(file.get(), site.get())
            .assertReferenceForFileIsCorrect(file.get());
    }

    @TestRail (id = "C9306")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void checkNodeBrowserPage()
    {
        nodeBrowserPage.navigate();
        nodeBrowserPage.assertSearchTypeIsSelected(SearchType.FTS_ALFRESCO)
            .assertStoreTypeIsSelected(StoreType.WORKSPACE_SPACES_STORE)
            .assertAllColumnsAreDisplayed()
            .assertSearchButtonIsDisplayed();
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.ADMIN_TOOLS })
    public void executeCustomNodeSearch()
    {
        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(SearchType.STORE_ROOT)
            .clickSearch()
            .assertReferenceContainsValue("workspace://SpacesStore/");
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.ADMIN_TOOLS })
    public void getSearchResultsNoResults()
    {
        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(SearchType.LUCENE)
            .searchFor(String.valueOf(System.currentTimeMillis()))
            .clickSearch()
            .assertNoItemsFoundLabelEquals(language.translate("nodeBrowser.noItemsFound"));
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        dataSite.usingAdmin().deleteSite(site.get());
    }
}
