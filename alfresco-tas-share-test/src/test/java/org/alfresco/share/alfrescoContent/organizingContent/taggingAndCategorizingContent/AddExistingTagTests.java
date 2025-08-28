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

public class AddExistingTagTests extends BaseTest
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

    @TestRail (id = "C7464")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addExistingTagFromEditPropertiesDialog() throws Exception
    {
        String tag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        FileModel fileWithTag = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel fileToAddTag = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(fileWithTag).createFile(fileToAddTag);
    //    getRestApi().authenticateUser(getAdminUser())
      //      .withCoreAPI().usingResource(fileWithTag).addTags(tag);

        setAuthorizationRequestHeader(getRestApi().authenticateUser(getAdminUser()))
            .withCoreAPI().usingResource(fileWithTag).addTags(tag);

        documentLibraryPage.navigate(site.get())
            .usingContent(fileToAddTag).clickEditProperties()
                .clickSelectTags();

        selectDialog.typeTagWithRetry(tag)
            .selectTag(tag)
            .assertTagIsSelected(tag).clickOk();
        selectDialog
            .clickSelectButton()
            .assertTagIsNotSelectable(tag)
            .clickOk();

        editPropertiesDialog.assertTagIsSelected(tag)
            .clickSave();

        documentLibraryPage.usingContent(fileToAddTag).assertTagIsDisplayed(tag);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}