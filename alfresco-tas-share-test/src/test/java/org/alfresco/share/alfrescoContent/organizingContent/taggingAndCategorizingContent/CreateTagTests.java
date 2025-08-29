package org.alfresco.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreateTagTests extends BaseTest
{
    private EditPropertiesDialog editPropertiesDialog;
    private SelectDialog selectDialog;
    private DocumentLibraryPage2 documentLibraryPage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        selectDialog = new SelectDialog(webDriver);
        editPropertiesDialog = new EditPropertiesDialog(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C10209")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createTagFromEditPropertiesDialog()
    {
        String tag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(testFile);

        documentLibraryPage.navigate(site.get())
            .usingContent(testFile)
                .clickEditProperties()
                .clickSelectTags();
        selectDialog.typeTag(tag)
            .clickCreateNewIcon()
            .assertTagIsSelected(tag)
            .clickOk();
        editPropertiesDialog.clickSave();
        documentLibraryPage.usingContent(testFile).assertTagIsDisplayed(tag);
    }

    @TestRail (id = "C10210")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createTagFromEditTagIcon()
    {
        String tag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
                .usingSite(site.get()).createFile(testFile);

        documentLibraryPage.navigate(site.get())
            .usingContent(testFile)
                .clickTagEditIcon()
                .setTag(tag)
                .clickSave();
        documentLibraryPage.usingContent(testFile).assertTagIsDisplayed(tag);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}