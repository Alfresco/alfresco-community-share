package org.alfresco.share.alfrescoContent.organizingContent;

import static org.alfresco.common.Utils.testDataFolder;

import java.io.File;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.DataContent;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.io.FilenameUtils;
import org.testng.annotations.*;

public class UnzippingContentTests extends BaseTest
{
    private DocumentLibraryPage2 documentLibraryPage;

    private final String zipFileName = "archiveC7409.zip";
    private final String acpFileName = "archiveC7410.acp";
    private final String acpCancelUnzip = "archiveC8041.acp";
    private final File zipFile = new File(testDataFolder.concat(zipFileName));
    private final File acpFile = new File(testDataFolder.concat(acpFileName));
    private final File acpCancelFile = new File(testDataFolder.concat(acpCancelUnzip));

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private final ThreadLocal<DataContent> dataContent = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        dataContent.set(applicationContext.getBean(DataContent.class));

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());
        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C7409")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyUnzipFileToDocumentLibraryOfTheSameSite()
    {
        FileModel zipFileModel = dataContent.get().usingUser(user.get())
            .usingSite(site.get()).uploadDocument(zipFile);

        documentLibraryPage.navigate(site.get())
            .usingContent(zipFileModel)
            .selectFile()
            .clickUnzipTo()
            .selectRecentSitesDestination()
            .selectSite(site.get())
            .clickUnzipButton();
        FolderModel unzipFolder = new FolderModel(FilenameUtils.getBaseName(zipFileModel.getName()));
        FileModel unzipFileName = new FileModel("fileC7409");
        documentLibraryPage.navigate(site.get())
            .usingContent(unzipFolder).assertContentIsDisplayed()
            .selectFolder()
            .usingContent(unzipFileName).assertContentIsDisplayed();
    }

    @TestRail (id = "C7410")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyUnzipAcpFileToDocumentLibraryOfTheSameSite()
    {
        FileModel acpFileModel = dataContent.get().usingUser(user.get())
            .usingSite(site.get())
            .uploadDocument(acpFile);

        documentLibraryPage.navigate(site.get())
            .usingContent(acpFileModel)
            .selectFile()
            .clickUnzipTo()
            .selectRecentSitesDestination()
            .selectSite(site.get())
            .clickUnzipButton();
        FolderModel unzipFolder = new FolderModel(FilenameUtils.getBaseName(acpFileModel.getName()));
        FileModel unzipFileName = new FileModel("fileC7410");
        documentLibraryPage.navigate(site.get())
            .usingContent(unzipFolder).assertContentIsDisplayed()
            .selectFolder()
            .usingContent(unzipFileName).assertContentIsDisplayed();
    }

    @TestRail (id = "C202869")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCancelUnzipAcpFile()
    {
        FileModel acpFileModel = dataContent.get().usingUser(user.get())
            .usingSite(site.get())
            .uploadDocument(acpCancelFile);

        documentLibraryPage.navigate(site.get())
            .usingContent(acpFileModel)
            .selectFile()
            .clickUnzipTo()
            .selectRecentSitesDestination()
            .selectSite(site.get())
            .clickCancelButton();
        FolderModel unzipFolder = new FolderModel(FilenameUtils.getBaseName(acpFileModel.getName()));
        documentLibraryPage.navigate(site.get())
            .usingContent(unzipFolder).assertContentIsNotDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
