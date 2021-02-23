package org.alfresco.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.*;
import java.util.Collections;

public class CategorizingContentTests extends BaseTest
{
    private final String CLASSIFIABLE_ASPECT = "P:cm:generalclassifiable";
    private final String LANGUAGES_CATEGORY = "Languages";
    private final String REGIONS_CATEGORY = "Regions";
    private final String NODE_REF_WORKSPACE = "workspace://SpacesStore/";
    private final String CATEGORIES_PROPERTY = "cm:categories";

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
    public void shouldAddSecondCategory()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(testFile)
            .usingResource(testFile)
            .addSecondaryTypes(CLASSIFIABLE_ASPECT)
            .assertThat().secondaryTypeIsAvailable(CLASSIFIABLE_ASPECT);

        String languagesNodeRef = getUserService()
            .getCategoryNodeRef(getAdminUser().getUsername(), getAdminUser().getPassword(), LANGUAGES_CATEGORY);
        getCmisApi().usingResource(testFile)
            .updateProperty(CATEGORIES_PROPERTY, Collections.singletonList(NODE_REF_WORKSPACE.concat(languagesNodeRef)));

        documentLibraryPage.navigate(site.get())
            .usingContent(testFile)
            .clickEditProperties();

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
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(testFile)
            .usingResource(testFile)
            .addSecondaryTypes(CLASSIFIABLE_ASPECT)
            .assertThat().secondaryTypeIsAvailable(CLASSIFIABLE_ASPECT);

        String languagesNodeRef = getUserService()
            .getCategoryNodeRef(getAdminUser().getUsername(), getAdminUser().getPassword(), LANGUAGES_CATEGORY);
        getCmisApi().usingResource(testFile)
            .updateProperty(CATEGORIES_PROPERTY, Collections.singletonList(NODE_REF_WORKSPACE.concat(languagesNodeRef)));

        documentLibraryPage.navigate(site.get())
            .usingContent(testFile)
            .clickEditProperties();

        editPropertiesDialog
            .assertCategoryIsSelected(LANGUAGES_CATEGORY)
            .clickSelectCategories();
        selectDialog.removeCategory(LANGUAGES_CATEGORY)
            .clickOk();
        editPropertiesDialog.clickSave();
        documentLibraryPage.usingContent(testFile).assertNoCategoriesIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
