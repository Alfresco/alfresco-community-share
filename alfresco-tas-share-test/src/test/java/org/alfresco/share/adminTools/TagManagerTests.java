package org.alfresco.share.adminTools;

import org.alfresco.po.share.user.admin.adminTools.TagManagerPage;
import org.alfresco.rest.model.RestTagModelsCollection;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * UI tests for Admin Tools > Tag Manager page
 */
public class TagManagerTests extends ContextAwareWebTest
{
    private final String uniqueIdentifier = RandomData.getRandomAlphanumeric().toLowerCase();
    private final String updatedTag = "updated" + uniqueIdentifier;
    private final String tag1 = "tag1" + uniqueIdentifier;
    private final String tag2 = "tag2" + uniqueIdentifier;
    private final String tag3 = "tag3" + uniqueIdentifier;
    private SiteModel site;
    private FileModel file;

    @Autowired
    private TagManagerPage tagManagerPage;

    @BeforeClass (alwaysRun = true)
    public void setupClass() throws Exception
    {
        site = dataSite.usingAdmin().createPublicRandomSite();
        file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.authenticateUser(getAdminUser()).usingSite(site).createFile(file);

        restApi.authenticateUser(getAdminUser())
            .withCoreAPI().usingResource(file).addTags(tag1, tag2, tag3);

        setupAuthenticatedSession(getAdminUser());
        tagManagerPage.navigate();
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        dataSite.usingAdmin().deleteSite(site);
    }

    @TestRail (id = "C9383")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void renamingTag() throws Exception
    {
        tagManagerPage.searchTagWithRetry(tag1)
            .clickEdit(tag1)
            .renameTag(updatedTag)
            .searchTagWithRetry(updatedTag)
            .assertTagIsDisplayed(updatedTag);
        RestTagModelsCollection tags = restApi.withCoreAPI().usingResource(file).getNodeTags();
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
            .assertTableHeadersAreCorrect()
            .searchTagWithRetry(tag2)
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
        tagManagerPage.searchTagWithRetry(tag3)
            .clickDelete(tag3)
                .assertConfirmDeleteMessageEqualsTo(tag3)
                .assertDeleteButtonIsDisplayed()
                .assertCancelButtonIsDisplayed()
                .clickDelete();
        tagManagerPage.search(tag3).assertTagIsNotDisplayed(tag3);
    }
}