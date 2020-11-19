package org.alfresco.share.adminTools.categoryManager;

import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.SystemErrorPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.po.share.user.admin.adminTools.CategoryManagerPage;
import org.alfresco.share.BaseShareWebTests;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.awt.desktop.UserSessionEvent;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertTrue;

public class CategoryManagerTests extends BaseShareWebTests
{
    private final String category9295 = String.format("categoryC9295%s", RandomData.getRandomAlphanumeric());
    private final String category9301 = String.format("categoryC9301%s", RandomData.getRandomAlphanumeric());
    private final String category9298 = String.format("categoryC9298%s", RandomData.getRandomAlphanumeric());
    private final String categoryEdited = String.format("categoryEdited%s", RandomData.getRandomAlphanumeric());
    private final String subCategoryName = String.format("testSubCategory%s", RandomData.getRandomAlphanumeric());

    private CategoryManagerPage categoryManagerPage;

    @Autowired
    private UserService userService;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        categoryManagerPage = new CategoryManagerPage(browser);
        setupAuthenticatedSession(dataUser.getAdminUser());
    }

    @AfterClass (alwaysRun = true)
    public void afterClassDeleteAddedCategories()
    {
        Stream.of(category9295, categoryEdited, subCategoryName).filter(categoryName
            -> userService.categoryExists(getAdminUser().getUsername(), getAdminUser().getPassword(), categoryName)).forEach(categoryName
            -> userService.deleteCategory(getAdminUser().getUsername(), getAdminUser().getPassword(), categoryName));
    }

    @TestRail (id = "C9294")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifyCategoryManagerPage()
    {
        categoryManagerPage.navigate();
        LOG.info("Step 1: Verify if the 'Category Manager' page has the specific links displayed.");
        asList("Category Root", "Languages", "Regions", "Software Document Classification", "Tags")
            .forEach(defaultCategory ->
                assertTrue(categoryManagerPage.isCategoryDisplayed(defaultCategory), defaultCategory + " is displayed."));
    }

    @TestRail (id = "C9295")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addNewCategory()
    {
        categoryManagerPage.navigate();
        LOG.info("Step 1: Add a new category in the 'Category Manager' page.");
        categoryManagerPage.addCategory(category9295);

        LOG.info("Step 2: Verify the category is added in the 'Category Manager' page.");
        assertTrue(categoryManagerPage.isCategoryDisplayed(category9295), "New category displayed");
    }

    @TestRail (id = "C9301")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteCategory()
    {
        categoryManagerPage.navigate();
        LOG.info("Step 1: Delete the category.");
        userService.createRootCategory(getAdminUser().getUsername(), getAdminUser().getPassword(), category9301);
        categoryManagerPage.navigate();
        categoryManagerPage.deleteCategory(category9301);

        LOG.info("Step 2: Verify the delete category is no longer present in the 'Category Manager' page.");
        assertTrue(categoryManagerPage.isCategoryNotDisplayed(category9301));
    }

    @TestRail (id = "C9298")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void editCategory()
    {
        categoryManagerPage.navigate();
        userService.createRootCategory(getAdminUser().getUsername(), getAdminUser().getPassword(), category9298);
        categoryManagerPage.navigate();
        categoryManagerPage.editCategory(category9298, categoryEdited);
        assertTrue(categoryManagerPage.isCategoryDisplayed(categoryEdited));
        assertTrue(categoryManagerPage.isCategoryNotDisplayed(category9298));
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.ADMIN_TOOLS, "Acceptance" })
    public void addAndOpenSubCategory()
    {
        categoryManagerPage.navigate();
        categoryManagerPage.addSubCategory("Languages", subCategoryName);
        assertTrue(categoryManagerPage.isSubcategoryDisplayed("Languages", subCategoryName),
            String.format("Subcategory %s is not displayed in the list", subCategoryName));
    }
}
