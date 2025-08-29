package org.alfresco.share.adminTools.categoryManager;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.user.admin.adminTools.CategoryManagerPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.TestGroup;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class CategoryManagerTests extends BaseTest
{
    private CategoryManagerPage categoryManagerPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        categoryManagerPage = new CategoryManagerPage(webDriver);
        authenticateUsingLoginPage(dataUser.getAdminUser());
    }

    @TestRail (id = "C9294")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.ADMIN_TOOLS })
    public void verifyCategoryManagerPage()
    {
        categoryManagerPage.navigate();
        asList("Category Root", "Languages", "Regions", "Software Document Classification", "Tags")
            .forEach(defaultCategory ->
                assertTrue(categoryManagerPage.isCategoryDisplayed(defaultCategory), defaultCategory + " is not displayed."));
    }

    @TestRail (id = "C9295")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.ADMIN_TOOLS })
    public void addNewCategory()
    {
        String category9295 = RandomStringUtils.randomAlphabetic(4);
        categoryManagerPage.navigate();
        categoryManagerPage.addCategory(category9295);

        assertTrue(categoryManagerPage.isCategoryDisplayed(category9295), "New category is not displayed");
        getUserService().deleteCategory(getAdminUser().getUsername(), getAdminUser().getPassword(), category9295);
    }

    @TestRail (id = "C9301")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.ADMIN_TOOLS })
    public void deleteCategory()
    {
        String category9301 = RandomStringUtils.randomAlphabetic(4);
        createCategory(category9301);

        categoryManagerPage.navigate();
        categoryManagerPage.deleteCategory(category9301)
            .assertCategoryIsNotDisplayed(category9301);
    }

    @TestRail (id = "C9298")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.ADMIN_TOOLS })
    public void editCategory()
    {
        String category9298 = RandomStringUtils.randomAlphabetic(4);
        String categoryToEdit = RandomStringUtils.randomAlphabetic(4);
        createCategory(category9298);

        categoryManagerPage.navigate()
            .editCategory(category9298, categoryToEdit);
        assertTrue(categoryManagerPage.isCategoryDisplayed(categoryToEdit));
        categoryManagerPage.assertCategoryIsNotDisplayed(category9298);

        getUserService().deleteCategory(getAdminUser().getUsername(), getAdminUser().getPassword(), categoryToEdit);
    }

    @Test (groups = { TestGroup.REGRESSION, TestGroup.ADMIN_TOOLS })
    public void addAndOpenSubCategory()
    {
        String subCategoryName = RandomStringUtils.randomAlphabetic(4);
        categoryManagerPage.navigate();
        categoryManagerPage.addSubCategory("Languages", subCategoryName);
        assertTrue(categoryManagerPage.isSubcategoryDisplayed("Languages", subCategoryName),
            String.format("Subcategory %s is not displayed in the list", subCategoryName));

        getUserService().deleteCategory(getAdminUser().getUsername(), getAdminUser().getPassword(), subCategoryName);
    }

    private void createCategory(String category)
    {
        boolean created = getUserService().createRootCategory(
            getAdminUser().getUsername(), getAdminUser().getPassword(), category);
        if(!created)
        {
            log.error("Retry create category {}", category);
            Utility.waitToLoopTime(2);
            getUserService().createRootCategory(
                getAdminUser().getUsername(), getAdminUser().getPassword(), category);
        }
    }
}
