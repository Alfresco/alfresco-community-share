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

public class RemoveContentTagsTests extends BaseTest
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

    @TestRail (id = "C10530")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void removeTagFromDocument() throws Exception
    {
        String tag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        FileModel fileWithTag = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(fileWithTag);
    //    getRestApi().authenticateUser(getAdminUser())
      //      .withCoreAPI().usingResource(fileWithTag).addTags(tag);
        setAuthorizationRequestHeader(getRestApi().authenticateUser(user.get()))
            .withCoreAPI().usingResource(fileWithTag).addTags(tag);

        documentLibraryPage.navigate(site.get())
            .usingContent(fileWithTag)
            .assertTagIsDisplayed(tag)
            .clickTagEditIcon()
            .removeTag(tag)
            .clickSave();
        documentLibraryPage.usingContent(fileWithTag)
            .assertTagIsNotDisplayed(tag);
    }

    @TestRail (id = "C7443")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void removeTagFromFolder() throws Exception
    {
        String tag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        FolderModel folderWithTag = FolderModel.getRandomFolderModel();
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFolder(folderWithTag);
   //     getRestApi().authenticateUser(getAdminUser())
     //       .withCoreAPI().usingResource(folderWithTag).addTags(tag);
        setAuthorizationRequestHeader(getRestApi().authenticateUser(user.get()))
            .withCoreAPI().usingResource(folderWithTag).addTags(tag);

        documentLibraryPage.navigate(site.get())
            .usingContent(folderWithTag)
            .clickTagEditIcon()
            .removeTag(tag)
            .clickSave();
        documentLibraryPage.usingContent(folderWithTag)
            .assertTagIsNotDisplayed(tag);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}