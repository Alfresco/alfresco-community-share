package org.alfresco.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import static org.alfresco.share.TestUtils.FILE_CONTENT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.*;

public class CategorizingContentTests extends BaseTest
{
    private final static String CLASSIFIABLE_ASPECT = "P:cm:generalclassifiable";
    private final static String LANGUAGES_CATEGORY = "Languages";
    private final static String REGIONS_CATEGORY = "Regions";

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

        setupAuthenticatedSession(user.get());
    }

    @TestRail (id = "C7484")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void shouldAddCategoryForFile()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(testFile)
                .usingResource(testFile)
                .addSecondaryTypes(CLASSIFIABLE_ASPECT)
                    .assertThat().secondaryTypeIsAvailable(CLASSIFIABLE_ASPECT);

        documentLibraryPage.navigate(site.get())
            .usingContent(testFile)
                .clickEditProperties()
                .clickSelectCategories();

        selectDialog
            .selectCategory(LANGUAGES_CATEGORY)
            .assertCategoryIsSelected(LANGUAGES_CATEGORY)
            .assertCategoryIsNotSelectable(LANGUAGES_CATEGORY)
            .clickOk();

        editPropertiesDialog
            .assertCategoryIsSelected(LANGUAGES_CATEGORY)
            .clickSave();

        documentLibraryPage.usingContent(testFile).assertCategoryIsDisplayed(LANGUAGES_CATEGORY);
    }

    @TestRail (id = "C7485")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void shouldAddCategoryForFolder()
    {
        FolderModel testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFolder(testFolder)
            .usingResource(testFolder)
            .addSecondaryTypes(CLASSIFIABLE_ASPECT)
            .assertThat().secondaryTypeIsAvailable(CLASSIFIABLE_ASPECT);

        documentLibraryPage.navigate(site.get())
            .usingContent(testFolder)
            .clickEditProperties()
            .clickSelectCategories();

        selectDialog
            .selectCategory(LANGUAGES_CATEGORY)
            .assertCategoryIsSelected(LANGUAGES_CATEGORY)
            .assertCategoryIsNotSelectable(LANGUAGES_CATEGORY)
            .clickOk();

        editPropertiesDialog
            .assertCategoryIsSelected(LANGUAGES_CATEGORY)
            .clickSave();

        documentLibraryPage.usingContent(testFolder).assertCategoryIsDisplayed(LANGUAGES_CATEGORY);
    }

    @TestRail (id = "C7487")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void addCategory_ContentWithCategory()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(testFile)
            .usingResource(testFile)
            .addSecondaryTypes(CLASSIFIABLE_ASPECT)
            .assertThat().secondaryTypeIsAvailable(CLASSIFIABLE_ASPECT);

        documentLibraryPage.navigate(site.get())
            .usingContent(testFile)
            .clickEditProperties()
            .clickSelectCategories();

        selectDialog
            .selectCategory(LANGUAGES_CATEGORY)
            .assertCategoryIsSelected(LANGUAGES_CATEGORY)
            .clickOk();

        editPropertiesDialog
            .assertCategoryIsSelected(LANGUAGES_CATEGORY)
            .clickSelectCategories();
        selectDialog
            .assertCategoryIsNotSelectable(LANGUAGES_CATEGORY)
            .selectCategory(REGIONS_CATEGORY)
            .assertCategoryIsSelected(REGIONS_CATEGORY)
            .clickOk();
        editPropertiesDialog
            .assertCategoryIsSelected(LANGUAGES_CATEGORY)
            .assertCategoryIsSelected(REGIONS_CATEGORY)
            .clickSave();

        documentLibraryPage.usingContent(testFile)
            .assertCategoryIsDisplayed(LANGUAGES_CATEGORY)
            .assertCategoryIsDisplayed(REGIONS_CATEGORY);
    }

    @TestRail (id = "C7486")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void removeCategory()
    {
        /*documentLibraryPage.navigate(siteName);
        LOG.info("STEP1: Hover over the file. STEP2: Click 'Edit Properties' link");
        documentLibraryPage.clickDocumentLibraryItemAction(removeCategoryDoc, ItemActions.EDIT_PROPERTIES);
        assertEquals(editPropertiesDialog.getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), removeCategoryDoc), "'Edit Properties' pop-up is displayed");
        assertTrue(editPropertiesDialog.isCategorySelected(category), "Precondition category is displayed in selected categories list.");

        LOG.info("STEP3: Click 'Select' button from 'Categories'");
        editPropertiesDialog.clickSelectCategories();
        assertTrue(selectDialog.isItemSelected(category), "Precondition category is displayed in selected categories list.");

        LOG.info("STEP4: For any item from the selected categories list, click 'Remove' icon");
        selectDialog.removeItems(Collections.singletonList(category));
        assertFalse(selectDialog.isItemSelected(category), "The removed category isn't displayed in selected categories list.");
        assertTrue(selectDialog.isItemSelectable(category), "In the left categories picker, 'Add' icon is displayed next to the removed item");

        LOG.info("STEP5: Click 'OK' button");
        selectDialog.clickOk();
        assertFalse(editPropertiesDialog.isCategorySelected(category),
                "The removed category isn't displayed in 'Categories' section from 'Edit Properties' dialog");

        LOG.info("STEP5: Click 'Save' button");
        editPropertiesDialog.clickSave();
        assertFalse(documentLibraryPage.getItemCategories(removeCategoryDoc).contains(category),
                "The removed category isn't displayed in Document Library, as detail below content name");*/
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
