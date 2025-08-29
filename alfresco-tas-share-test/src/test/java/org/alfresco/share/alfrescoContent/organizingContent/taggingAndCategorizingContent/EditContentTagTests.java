package org.alfresco.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EditContentTagTests extends BaseTest
{
    private DocumentLibraryPage2 documentLibraryPage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C7460")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editTagFromDocument() throws Exception
    {
        String originalTag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        String editedTag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        FileModel fileWithTag = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(fileWithTag);
     //   getRestApi().authenticateUser(getAdminUser())
       //     .withCoreAPI().usingResource(fileWithTag).addTags(originalTag);
        setAuthorizationRequestHeader(getRestApi().authenticateUser(getAdminUser()))
            .withCoreAPI().usingResource(fileWithTag).addTags(originalTag);

        documentLibraryPage.navigate(site.get())
            .usingContent(fileWithTag)
            .clickTagEditIcon()
            .clickTag(originalTag)
            .setTag(editedTag)
            .clickSave();
        documentLibraryPage.usingContent(fileWithTag)
            .assertTagIsDisplayed(editedTag)
            .assertTagIsNotDisplayed(originalTag);
    }

    @TestRail (id = "C10529")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editTagFromFolder() throws Exception
    {
        String originalTag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        String editedTag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        FolderModel folderWithTag = FolderModel.getRandomFolderModel();
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFolder(folderWithTag);
  //      getRestApi().authenticateUser(getAdminUser())
   //         .withCoreAPI().usingResource(folderWithTag).addTags(originalTag);
        setAuthorizationRequestHeader(getRestApi().authenticateUser(user.get()))
            .withCoreAPI().usingResource(folderWithTag).addTags(originalTag);

        documentLibraryPage.navigate(site.get())
            .usingContent(folderWithTag)
            .clickTagEditIcon()
            .clickTag(originalTag)
            .setTag(editedTag)
            .clickSave();
        documentLibraryPage.usingContent(folderWithTag)
            .assertTagIsDisplayed(editedTag)
            .assertTagIsNotDisplayed(originalTag);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}