package org.alfresco.share.alfrescoContent.documentLibrary;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.FileModel;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.alfresco.share.TestUtils.FILE_CONTENT;
import static org.testng.Assert.assertEquals;

@Slf4j
public class DocumentDetailsTest extends BaseTest
{
    @Autowired
    UserService userService;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private DocumentLibraryPage2 documentLibraryPage;
    private DocumentLibraryPage documentLibraryPages;
    private DocumentDetailsPage documentPreviewPage;
    private final DateTime currentDate = new DateTime();
    FolderModel folder = FolderModel.getRandomFolderModel();
    FileModel file = FileModel.getRandomFileModel(FileType.HTML, FILE_CONTENT);

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), user2.get().getUsername(), site.get().getId(), "SiteCollaborator");
        getCmisApi().authenticateUser(user.get()).usingSite(site.get()).createFile(file);
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        documentLibraryPages = new DocumentLibraryPage(webDriver);
        documentPreviewPage = new DocumentDetailsPage(webDriver);

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C2275")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void documentPathLink()
    {
        getCmisApi().authenticateUser(user.get()).usingSite(site.get())
            .createFolder(folder).usingResource(folder)
            .createFile(file);

        documentLibraryPages.navigate(site.get())
            .clickOnFolderName(folder.getName())
            .assertFileIsDisplayed(file.getName());
        documentLibraryPage.usingContentFilters()
            .clickFolderFromFilter(folder)
            .usingContent(file).assertContentIsDisplayed();
        documentLibraryPage.usingContentFilters();
        documentLibraryPages.assertFileIsDisplayed(file.getName());
    }

    @TestRail (id = "C2276")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyModifierField()
    {
        documentLibraryPages.navigate(site.get())
            .assertFileIsDisplayed(file.getName());
        documentLibraryPages.clickOnFile(file.getName());
        documentPreviewPage
            .assertVerifyItemModifire(user.get().getFirstName(), user.get().getLastName())
            .assertVerifyModifiedDate(currentDate.toString("EEE d MMM yyyy"));
        assertEquals(documentPreviewPage.getModifierValue(), "Modifier: " + user.get().getUsername());
        authenticateUsingLoginPage(user2.get());
        documentLibraryPages.navigate(site.get())
            .assertFileIsDisplayed(file.getName())
            .mouseOverNoTags(file.getName());
        log.info("STEP2: Click \"Edit Tag\" icon");
        documentLibraryPage.usingContent(file)
            .clickTagEditIcon();
        documentLibraryPage.usingContent(file).setTag("check tag")
            .clickSave();
        documentLibraryPages.assertFileIsDisplayed(file.getName())
            .clickOnFile(file.getName());
        assertEquals(documentPreviewPage.getModifierValue(), "Modifier: " + user2.get().getUsername());
    }

    @TestRail (id = "C2277")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void tagsToggle()
    {
        documentLibraryPages.navigate(site.get())
            .assertFileIsDisplayed(file.getName())
            .clickOnFile(file.getName());
        Assert.assertTrue(documentLibraryPages.assertTagDisplayed(), "Available tag value");
        documentLibraryPages.clickOnTagToggle();
        Assert.assertFalse(documentLibraryPages.assertTagDisplayed(), "Available tag value");
    }

    @TestRail (id = "C2278")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editTagIcon()
    {
        documentLibraryPages.navigate(site.get())
            .assertFileIsDisplayed(file.getName())
                .mouseOverNoTags(file.getName());
        log.info("STEP2: Click \"Edit Tag\" icon");
        documentLibraryPage.usingContent(file)
            .clickTagEditIcon();
        documentLibraryPage.usingContent(file).setTag("check tag")
            .clickSave();
        documentLibraryPages.clickOnFile(file.getName());
        assertEquals(documentLibraryPages.getTagValue(), "check tag", "tag value");
        documentLibraryPages.clickOnEditTagIcon();
        documentLibraryPages.updateTag();
        documentLibraryPages.clickOnSaveTag();
        assertEquals(documentLibraryPages.getTagValue(), "update tag", "tag value");
    }

    @TestRail (id = "C2279")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editTagCancel()
    {
        documentLibraryPages.navigate(site.get())
            .assertFileIsDisplayed(file.getName())
            .mouseOverNoTags(file.getName());
        log.info("STEP2: Click \"Edit Tag\" icon");
        documentLibraryPage.usingContent(file)
            .clickTagEditIcon();
        documentLibraryPage.usingContent(file).setTag("check tag")
            .clickSave();
        documentLibraryPages.clickOnFile(file.getName());
        assertEquals(documentLibraryPages.getTagValue(), "check tag", "tag value");
        documentLibraryPages.clickOnEditTagIcon();
        documentLibraryPages.updateTag();
        documentLibraryPages.ClickOnCancelTag();
        assertEquals(documentLibraryPages.getTagValue(), "check tag", "tag value");
    }
}
