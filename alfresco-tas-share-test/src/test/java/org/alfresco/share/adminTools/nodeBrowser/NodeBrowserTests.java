package org.alfresco.share.adminTools.nodeBrowser;

import static org.alfresco.po.enums.SearchType.CMIS_ALFRESCO;
import static org.alfresco.po.enums.SearchType.CMIS_STRICT;
import static org.alfresco.po.enums.SearchType.FTS_ALFRESCO;
import static org.alfresco.po.enums.SearchType.LUCENE;
import static org.alfresco.po.enums.SearchType.NODEREF;
import static org.alfresco.po.enums.SearchType.STORE_ROOT;
import static org.alfresco.po.enums.SearchType.XPATH;
import static org.alfresco.po.enums.StoreType.WORKSPACE_SPACES_STORE;
import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.user.admin.adminTools.NodeBrowserPage;
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

    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        nodeBrowserPage = new NodeBrowserPage(webDriver);

        site.set(dataSite.usingAdmin().createPublicRandomSite());
        authenticateUsingLoginPage(dataUser.getAdminUser());
    }

    @TestRail (id = "C9309")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void luceneSearch()
    {
        String content = RandomStringUtils.randomAlphanumeric(10);
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, content);
        getCmisApi().authenticateUser(getAdminUser()).usingSite(site.get()).createFile(file);

        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(LUCENE)
            .searchFor(content)
            .clickSearch()
            .assertParentForFileIsSite(file, site.get())
            .assertReferenceForFileIsCorrect(file);
    }

    @TestRail (id = "C9307")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void nodeRefSearch()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(getAdminUser()).usingSite(site.get()).createFile(file);

        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(NODEREF)
            .searchFor("workspace://SpacesStore/" + file.getNodeRefWithoutVersion())
            .clickSearch()
            .assertParentForFileIsSite(file, site.get())
            .assertReferenceForFileIsCorrect(file);
    }

    @TestRail (id = "C9308")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void xpathSearch()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(getAdminUser()).usingSite(site.get()).createFile(file);
        String xpathSearchTerm = String.format("/app:company_home/st:sites/cm:%s/cm:documentLibrary/cm:%s",
            site.get().getId(), file.getName());

        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(XPATH)
            .searchFor(xpathSearchTerm)
            .clickSearch()
            .assertParentForFileIsSite(file, site.get())
            .assertReferenceForFileIsCorrect(file);
    }

    @TestRail (id = "C9310")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void ftsAlfrescoSearch()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(getAdminUser()).usingSite(site.get()).createFile(file);

        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(FTS_ALFRESCO)
            .searchFor("cm:name:" + file.getName())
            .clickSearch()
            .assertParentForFileIsSite(file, site.get())
            .assertReferenceForFileIsCorrect(file);
    }

    @TestRail (id = "C9311")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void cmisStrictSearch()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(getAdminUser()).usingSite(site.get()).createFile(file);
        String cmisSearchTerm = String.format("SELECT * from cmis:document where cmis:name =  '%s'", file.getName());

        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(CMIS_STRICT)
            .searchFor(cmisSearchTerm)
            .clickSearch()
            .assertParentForFileIsSite(file, site.get())
            .assertReferenceForFileIsCorrect(file);
    }

    @TestRail (id = "C9312")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void cmisAlfrescoSearch()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(getAdminUser()).usingSite(site.get()).createFile(file);
        String cmisSearchTerm = String.format("SELECT * from cmis:document where cmis:name =  '%s'", file.getName());

        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(CMIS_ALFRESCO)
            .searchFor(cmisSearchTerm)
            .clickSearch()
            .assertParentForFileIsSite(file, site.get())
            .assertReferenceForFileIsCorrect(file);
    }

    @TestRail (id = "C9306")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void checkNodeBrowserPage()
    {
        nodeBrowserPage.navigate();
        nodeBrowserPage.assertSearchTypeIsSelected(FTS_ALFRESCO)
            .assertStoreTypeIsSelected(WORKSPACE_SPACES_STORE)
            .assertAllColumnsAreDisplayed()
            .assertSearchButtonIsDisplayed();
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.ADMIN_TOOLS })
    public void executeCustomNodeSearch()
    {
        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(STORE_ROOT)
            .clickSearch()
            .assertReferenceContainsValue("workspace://SpacesStore/");
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.ADMIN_TOOLS })
    public void getSearchResultsNoResults()
    {
        nodeBrowserPage.navigate();
        nodeBrowserPage.selectSearchType(LUCENE)
            .searchFor(String.valueOf(System.currentTimeMillis()))
            .clickSearch()
            .assertNoItemsFoundLabelEquals(language.translate("nodeBrowser.noItemsFound"));
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteSitesIfNotNull(site.get());
    }
}
