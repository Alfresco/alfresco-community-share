package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.TestGroup;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.alfresco.share.TestUtils.FILE_CONTENT;

@Slf4j

public class RepositoryTests extends BaseTest
{
    private final ThreadLocal<SiteModel> site1 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site2 = new ThreadLocal<>();
    //@Autowired
    private RepositoryPage repositoryPage;
    private MyFilesPage myFilesPage;
    private final String dockLibraryFoldername = "documentLibrary";
    private final String siteFoldername = "Sites";
    private final String site1name = "Site1" + RandomStringUtils.randomAlphanumeric(7);
    private final String site2name = "Site2" + RandomStringUtils.randomAlphanumeric(7);
    private final FileModel site1File = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
    private final FolderModel site1Folder = FolderModel.getRandomFolderModel();
    private final FileModel site2File = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
    private final FolderModel site2Folder = FolderModel.getRandomFolderModel();
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        log.info("PreCondition1: Any test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(user.get());

        authenticateUsingCookies(user.get());

        repositoryPage = new RepositoryPage(webDriver);
        repositoryPage = new RepositoryPage(webDriver);
        myFilesPage = new MyFilesPage(webDriver);

    }
    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }

    @TestRail (id = "C8154")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkTheRepositoryIsAvailableInTheToolBar()
    {
        repositoryPage
            .navigate();
        repositoryPage
            .assertRepositoryPageIsOpened();
    }
    @TestRail (id = "C8155")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "FlakyTests" })
    public void checkTheFilesAndFoldersAvailabilityInRepository() {
        log.info("Precondition : create 2 sites and add file and folder in both sites");
        site1.set(getDataSite().usingUser(user.get()).createSite(new SiteModel(site1name)));
        getCmisApi().usingSite(site1.get()).createFile(site1File).assertThat().existsInRepo();
        getCmisApi().usingSite(site1.get()).createFolder(site1Folder).assertThat().existsInRepo();
        site2.set(getDataSite().usingUser(user.get()).createSite(new SiteModel(site2name)));
        getCmisApi().usingSite(site2.get()).createFile(site2File).assertThat().existsInRepo();
        getCmisApi().usingSite(site2.get()).createFolder(site2Folder).assertThat().existsInRepo();

        log.info(" Navigate to the Repository Page and click on Sites");
        repositoryPage
            .navigate();
        repositoryPage
            .assertRepositoryPageIsOpened();
        repositoryPage
            .click_FolderName(siteFoldername);
        log.info(" Click on your first created site.");
        repositoryPage
            .click_FolderName(site1name);
        log.info(" Verify documentLibrary folder and Click on documentLibrary folder.");
        myFilesPage
            .assertIsContantNameDisplayed(dockLibraryFoldername);
        repositoryPage
            .click_FolderName(dockLibraryFoldername);
        log.info("Verify site1 file and folder are available in documentLibrary");
        myFilesPage
            .assertIsContantNameDisplayed(site1File.getName());
        myFilesPage
            .assertIsContantNameDisplayed(site1Folder.getName());
        log.info(" Return to Repository and click on Sites");
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(siteFoldername);
        log.info(" Click on your second created site.");
        repositoryPage
            .click_FolderName(site2name);
        log.info(" Verify documentLibrary folder and Click on documentLibrary folder.");
        myFilesPage
            .assertIsContantNameDisplayed(dockLibraryFoldername);
        repositoryPage
            .click_FolderName(dockLibraryFoldername);
        log.info("Verify site1 file and folder are available in documentLibrary");
        myFilesPage
            .assertIsContantNameDisplayed(site2File.getName());
        myFilesPage
            .assertIsContantNameDisplayed(site2Folder.getName());

        log.info("Delete created site ");
        getDataSite().usingUser(user.get()).deleteSite(new SiteModel(site1name));
        getDataSite().usingUser(user.get()).deleteSite(new SiteModel(site2name));
    }
}
