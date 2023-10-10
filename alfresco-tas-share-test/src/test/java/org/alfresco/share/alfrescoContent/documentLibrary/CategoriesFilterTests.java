package org.alfresco.share.alfrescoContent.documentLibrary;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.Collections;

public class CategoriesFilterTests extends BaseTest
{
    private final String CLASSIFIABLE_ASPECT = "P:cm:generalclassifiable";
    private final String LANGUAGE_CATEGORY = "Languages";
    private final String ENGLISH_CATEGORY = "English";
    private final String FRENCH_CATEGORY = "French";
    private final String NODE_REF_WORKSPACE = "workspace://SpacesStore/";
    private final String CATEGORIES_PROPERTY = "cm:categories";

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

    @TestRail (id = "C6910, C10595")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyCategoryRootTreeNodes()
    {
        documentLibraryPage.navigate(site.get())
            .usingContentFilters()
                .assertCategoriesAreNotExpanded()
                .expandCategoryRoot()
                .assertCategoriesAreExpanded()
                .collapseCategoryRoot()
                .assertCategoriesAreNotExpanded();
    }

    @TestRail (id = "C6910, C10595")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void filterDocumentsByCategories()
    {
        FileModel englishFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel frenchFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);

        createFileAndSetLanguageCategory(englishFile, site.get(), ENGLISH_CATEGORY);
        createFileAndSetLanguageCategory(frenchFile, site.get(), FRENCH_CATEGORY);

        documentLibraryPage.navigate(site.get())
            .usingContentFilters()
                .expandCategoryRoot()
                .expandCategory(LANGUAGE_CATEGORY)
                .selectCategory(ENGLISH_CATEGORY);

        documentLibraryPage.usingContent(englishFile).assertContentIsDisplayed();
        documentLibraryPage.usingContent(frenchFile).assertContentIsNotDisplayed();

        documentLibraryPage.usingContentFilters().selectCategory(FRENCH_CATEGORY);

        documentLibraryPage.usingContent(englishFile).assertContentIsNotDisplayed();
        documentLibraryPage.usingContent(frenchFile).assertContentIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    private void createFileAndSetLanguageCategory(FileModel fileModel, SiteModel site, String languageCategory)
    {
        getCmisApi().authenticateUser(user.get())
            .usingSite(site).createFile(fileModel).assertThat().existsInRepo()
            .usingResource(fileModel)
            .addSecondaryTypes(CLASSIFIABLE_ASPECT)
                .assertThat().secondaryTypeIsAvailable(CLASSIFIABLE_ASPECT);
        String englishNodeRef = getUserService()
            .getCategoryNodeRef(getAdminUser().getUsername(), getAdminUser().getPassword(), languageCategory);
        getCmisApi().usingResource(fileModel)
            .updateProperty(CATEGORIES_PROPERTY, Collections.singletonList(NODE_REF_WORKSPACE.concat(englishNodeRef)));
    }
}
