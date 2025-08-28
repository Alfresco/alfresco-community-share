package org.alfresco.share.adminTools;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.user.admin.adminTools.TagManagerPage;
import org.alfresco.rest.model.RestTagModelsCollection;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.TestGroup;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TagManagerTests extends BaseTest
{
    private TagManagerPage tagManagerPage;

    private final ThreadLocal<String> tag = new ThreadLocal<>();
    private final ThreadLocal<FileModel> tagFile = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception
    {
        tagManagerPage = new TagManagerPage(webDriver);

        tagFile.set(FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT));
        tag.set("tag" + RandomStringUtils.randomAlphabetic(4).toLowerCase());
        getCmisApi().authenticateUser(getAdminUser()).usingShared().createFile(tagFile.get());
  //      getRestApi().authenticateUser(getAdminUser())
    //        .withCoreAPI().usingResource(tagFile.get()).addTags(tag.get());
        setAuthorizationRequestHeader(getRestApi().authenticateUser(getAdminUser()))
            .withCoreAPI().usingResource(tagFile.get()).addTags(tag.get());

        authenticateUsingCookies(getAdminUser());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUp()
    {
        getCmisApi().authenticateUser(getAdminUser())
            .usingResource(tagFile.get()).delete();
    }

    @TestRail (id = "C9383")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void renamingTag() throws Exception
    {
        String updatedTag = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        tagManagerPage.navigate();
        tagManagerPage.searchTagWithRetry(tag.get())
            .clickEdit(tag.get())
            .renameTag(updatedTag)
            .searchTagWithRetry(updatedTag)
            .assertTagIsDisplayed(updatedTag);
        RestTagModelsCollection tags =  setAuthorizationRequestHeader(getRestApi().authenticateUser(getAdminUser())).withCoreAPI().usingResource(tagFile.get()).getNodeTags();
        tags.assertThat()
            .entriesListContains("tag", updatedTag);
    }

    @TestRail (id = "C9385")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifyTagManagerPage()
    {
        tagManagerPage.navigate();
        tagManagerPage.assertSearchButtonIsDisplayed()
            .assertSearchInputFieldDisplayed()
            .assertTableTitleIsCorrect()
            .searchTagWithRetry(tag.get())
            .assertTableHeadersEqual(language.translate("tagManager.tableHead"))
            .clickEdit(tag.get())
            .assertRenameTagLabelIsCorrect()
            .assertOkButtonIsDisplayed()
            .assertCancelButtonIsDisplayed()
            .assertRequiredSymbolIsDisplayed();
    }

    @TestRail (id = "C9388")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteTag()
    {
        tagManagerPage.navigate();
        tagManagerPage.searchTagWithRetry(tag.get())
            .clickDelete(tag.get())
            .assertConfirmDeleteMessageForContentEqualsTo(tag.get())
            .assertDeleteButtonIsDisplayed()
            .assertCancelButtonIsDisplayed()
            .confirmDeletion();
        tagManagerPage.assertNoTagFoundMessageIsDisplayed()
            .typeInSearch(tag.get()).clickSearch()
            .assertTagIsNotDisplayed(tag.get());
    }
}