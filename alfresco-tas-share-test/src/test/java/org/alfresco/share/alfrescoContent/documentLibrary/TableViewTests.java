package org.alfresco.share.alfrescoContent.documentLibrary;

import static org.alfresco.share.ContextAwareWebTest.FILE_CONTENT;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.TestGroup;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.commons.lang3.RandomStringUtils;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

@Slf4j
public class TableViewTests extends BaseTest {
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private final String SET_DETAILED_VIEW_AS_DEFAULT = "documentLibrary.options.defaultDetailedView";
    private final String REMOVE_DETAILED_VIEW_AS_DEFAULT = "documentLibrary.options.removeDetailedView";
    private final String REMOVE_TABLE_VIEW_AS_DEFAULT = "documentLibrary.options.removeTableView";
    private DocumentLibraryPage2 documentLibraryPage;
    private DocumentLibraryPage documentLibrary;
    private FolderModel folder1;
    private FileModel fileA;

    private FolderModel folder2;
    private FileModel fileB;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        documentLibrary = new DocumentLibraryPage(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        authenticateUsingCookies(user.get());
    }

    @TestRail(id = "C2266, C2267")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void checkContentInTableView() {
        String fileDescription = RandomStringUtils.randomAlphabetic(10);
        String fileTitle = RandomStringUtils.randomAlphabetic(5);
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(file)
            .usingResource(file)
            .updateProperty("cmis:description", fileDescription)
            .updateProperty("cm:title", fileTitle);

        documentLibraryPage.navigate(site.get())
            .clickOptions()
            .selectTableView()
            .assertContentIsDisplayed(file)
            .assertTitleEqualsTo(file, fileTitle)
            .assertDescriptionEqualsTo(file, fileDescription)
            .assertCreatorEqualsTo(file, user.get().getFirstName().concat(" ").concat(user.get().getLastName()))
            .assertCreatedDateEqualsTo(file, getFormattedContentDateFromServer(file, "cmis:creationDate"))
            .assertModifierEqualsTo(file, user.get().getFirstName().concat(" ").concat(user.get().getLastName()))
            .assertModifiedDateEqualsTo(file, getFormattedContentDateFromServer(file, "cmis:lastModificationDate"));
    }

    private String getFormattedContentDateFromServer(ContentModel content, String dateType) {
        SimpleDateFormat shareDateFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss");
        CmisObject obj = getCmisApi().authenticateUser(user.get())
            .usingResource(content).withCMISUtil().getCmisObject(content.getCmisLocation());
        GregorianCalendar date = obj.getPropertyValue(dateType);
        return shareDateFormat.format(date.getTime());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail(id = "C2269")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void tableViewEditSupportForName() {
        String fileDescription = RandomStringUtils.randomAlphabetic(10);
        String fileTitle = RandomStringUtils.randomAlphabetic(5);
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        String newContent = "Edited" + RandomStringUtils.randomAlphabetic(10);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(file)
            .usingResource(file)
            .updateProperty("cmis:description", fileDescription)
            .updateProperty("cm:title", fileTitle);

        documentLibraryPage.navigate(site.get())
            .clickOptions()
            .selectTableView();

        documentLibraryPage.assertTableViewIsSet();

        documentLibrary.mouseOverContentRow(file.getName())
            .clickNameRenameIcon(file.getName())
            .typeContentName(newContent)
            .clickButtonFromRenameContent("Cancel")
            .assertContentIsDisplayed(file)
            .clickNameRenameIcon(file.getName())
            .typeContentName(newContent)
            .clickButtonFromRenameContent("Save")
            .assertFileIsDisplayed(newContent);
    }

    @TestRail(id = "C2271")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void defaultViewAdditionalOptions() {

        log.info("Pre-Conditions - Document is created");
        String fileDescription = RandomStringUtils.randomAlphabetic(10);
        String fileTitle = RandomStringUtils.randomAlphabetic(5);
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(file)
            .usingResource(file)
            .updateProperty("cmis:description", fileDescription)
            .updateProperty("cm:title", fileTitle);

        log.info("STEP1 - Expand the \"Options\" menu, click the \"Table View\" link and verify displaying of the items on the page.");
        documentLibraryPage.navigate(site.get())
            .clickOptions()
            .assertSetDefaultViewForFolderEqualsTo(language.translate(SET_DETAILED_VIEW_AS_DEFAULT))
            .setDefaultView();
        documentLibraryPage.clickOptions()
            .selectTableView().assertTableViewIsSet();

        log.info("STEP2 - Refresh/Navigate document library page and view is in detailed view");
        documentLibraryPage.navigate(site.get())
            .assertDetailedViewIsSet();
        documentLibraryPage.clickOptions()
            .assertRemoveDefaultViewForFolderEqualsTo(language.translate(REMOVE_DETAILED_VIEW_AS_DEFAULT));

        log.info("STEP3 - set table view as default view");
        documentLibraryPage.selectTableView()
            .clickOptions()
            .setDefaultView()
            .clickOptions()
            .assertRemoveDefaultViewForFolderEqualsTo(language.translate(REMOVE_TABLE_VIEW_AS_DEFAULT));

        log.info("STEP4 - Refresh/Navigate document library page and view is in table view");
        documentLibraryPage.navigate(site.get())
            .assertTableViewIsSet();
    }

    @TestRail(id = "C2272")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void tableViewWithTooLargeData() {

        log.info("Pre-Conditions - Document is created with Long names");
        String fileName = "Long Data Name" + RandomStringUtils.randomAlphabetic(300);
        String fileDescription = "Long Data Description" + RandomStringUtils.randomAlphabetic(1100);
        String fileTitle = "Long Data Title" + RandomStringUtils.randomAlphabetic(1100);

        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(file)
            .usingResource(file)
            .updateProperty("cmis:description", fileDescription)
            .updateProperty("cm:title", fileTitle)
            .updateProperty("cmis:name", fileName);

        log.info("STEP1 - Expand the \"Options\" menu, click the \"Table View\" link");
        documentLibraryPage.navigate(site.get());

        documentLibraryPage.clickOptions()
            .selectTableView().assertTableViewIsSet();
    }

    @TestRail(id = "C2274")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void filtersUseCurrentViewSetting() {

        log.info("Precondition: Create Folder1 & file inside folder in document library.");
        folder1 = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folder1).assertThat().existsInRepo();

        fileA = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().usingSite(site.get()).usingResource(folder1).createFile(fileA).assertThat().existsInRepo();

        log.info("Precondition: Create Folder2 & file inside folder in document library.");
        folder2 = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folder2).assertThat().existsInRepo();

        fileB = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().usingSite(site.get()).usingResource(folder2).createFile(fileB).assertThat().existsInRepo();

        log.info("STEP1: Open folder 1 and define for it Table view");
        documentLibraryPage.navigate(site.get());
        documentLibrary.clickOnFolderName(folder1.getName());
        documentLibraryPage.clickOptions()
            .selectTableView()
            .assertTableViewIsSet();

        log.info("STEP2: Open folder B (no explicit view was set for this folder)");
        documentLibrary.navigate(site.get())
            .clickFolderNameOnTableView(folder2.getName());

        log.info("STEP3: Verify All items in folder B are displayed in the same view as in previous folder A");
        documentLibraryPage.assertTableViewIsSet();
    }
}
