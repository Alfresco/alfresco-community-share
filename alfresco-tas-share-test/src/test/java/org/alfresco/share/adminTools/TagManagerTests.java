package org.alfresco.share.adminTools;

import org.alfresco.po.share.user.admin.adminTools.TagManagerPage;
import org.alfresco.rest.model.RestTagModelsCollection;
import org.alfresco.share.BaseTests;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TagManagerTests extends BaseTests
{
    private TagManagerPage tagManagerPage;

    private final String uniqueIdentifier = RandomData.getRandomAlphanumeric().toLowerCase();
    private final String updatedTag = "updated" + uniqueIdentifier;
    private final String tag1 = "tag1" + uniqueIdentifier;
    private final String tag2 = "tag2" + uniqueIdentifier;
    private final String tag3 = "tag3" + uniqueIdentifier;
    private SiteModel site;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        tagManagerPage = new TagManagerPage(browser);
        getCmisApi().authenticateUser(getAdminUser());
        setupAuthenticatedSession(getAdminUser());
    }

    @BeforeClass (alwaysRun = true)
    public void setupClass()
    {
        site = dataSite.usingAdmin().createPublicRandomSite();
    }

    @TestRail (id = "C9383")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void renamingTag() throws Exception
    {
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site).createFile(file);
        getRestApi().authenticateUser(getAdminUser())
            .withCoreAPI().usingResource(file).addTags(tag1, tag2, tag3);

        tagManagerPage.navigate();
        tagManagerPage.searchTagWithRetry(tag1)
            .clickEdit(tag1)
            .renameTag(updatedTag)
            .searchTagWithRetry(updatedTag)
            .assertTagIsDisplayed(updatedTag);
        RestTagModelsCollection tags = getRestApi().withCoreAPI().usingResource(file).getNodeTags();
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
            .searchTagWithRetry(tag2)
                .assertTableHeadersAreCorrect()
                .clickEdit(tag2)
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
        tagManagerPage.searchTagWithRetry(tag3)
            .clickDelete(tag3)
                .assertConfirmDeleteMessageForContentEqualsTo(tag3)
                .assertDeleteButtonIsDisplayed()
                .assertCancelButtonIsDisplayed()
                .clickDelete();
        tagManagerPage.assertNoTagFoundMessageIsDisplayed()
            .search(tag3).assertTagIsNotDisplayed(tag3);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        dataSite.usingAdmin().deleteSite(site);
    }
}