package org.alfresco.share.adminTools.categoryManager;

import java.util.Arrays;

import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.user.admin.adminTools.CategoryManagerPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
public class CategoryManagerTests extends ContextAwareWebTest
{
    @Autowired
    CategoryManagerPage categoryManagerPage;

    @Autowired
    UserService userService;

    String category9295 = String.format("categoryC9295%s", RandomData.getRandomAlphanumeric());
    String category9301 = String.format("categoryC9301%s", RandomData.getRandomAlphanumeric());
    String category9298 = String.format("categoryC9298%s", RandomData.getRandomAlphanumeric());
    String categoryEdited = String.format("categoryEdited%s", RandomData.getRandomAlphanumeric());

    @BeforeClass (alwaysRun = true)
    public void beforeClass()
    {
        userService.createRootCategory(adminUser, adminPassword, category9301);
        userService.createRootCategory(adminUser, adminPassword, category9298);

        setupAuthenticatedSession(adminUser, adminPassword);
        categoryManagerPage.navigate();
    }


    @AfterClass
    public void afterClassDeleteAddedCategories()
    {
        userService.deleteCategory(adminUser, adminPassword, category9295);
        userService.deleteCategory(adminUser, adminPassword, categoryEdited);

        for (String categoryName : Arrays.asList(category9295, categoryEdited))
            if (userService.categoryExists(adminUser, adminPassword, categoryName))
                userService.deleteCategory(adminUser, adminPassword, categoryName);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C9294")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifyCategoryManagerPage()
    {
        LOG.info("Step 1: Verify if the 'Category Manager' page has the specific links displayed.");

        Assert.assertTrue(categoryManagerPage.isCategoryRootLinkDisplayed(), "Category Root link is displayed.");
        Assert.assertTrue(categoryManagerPage.isLanguagesLinkDisplayed(), "Languages link is displayed.");
        Assert.assertTrue(categoryManagerPage.isRegionsLinkDisplayed(), "Regions link is displayed.");
        Assert.assertTrue(categoryManagerPage.isSoftwareDocumentClassificationLinkDisplayed(), "Software Document Classification link is displayed.");
        Assert.assertTrue(categoryManagerPage.isTagsLinkDisplayed(), "Tags link is displayed.");
    }

    @TestRail (id = "C9295")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addNewCategory()
    {
        LOG.info("Step 1: Add a new category in the 'Category Manager' page.");
        categoryManagerPage.addCategory(category9295);

        LOG.info("Step 2: Verify the category is added in the 'Category Manager' page.");
        categoryManagerPage.navigate();
        getBrowser().waitInSeconds(8);
        Assert.assertTrue(categoryManagerPage.isCategoryDisplayed(category9295), "New category displayed");
    }

    @TestRail (id = "C9301")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteCategory()
    {
        LOG.info("Step 1: Delete the category.");
        categoryManagerPage.deleteCategory(category9301);

        LOG.info("Step 2: Verify the delete category is no longer present in the 'Category Manager' page.");
        Assert.assertTrue(categoryManagerPage.isCategoryNotDisplayed(category9301));
    }

    @TestRail (id = "C9298")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void editCategory()
    {
        LOG.info("Step 1: Edit the category.");
        categoryManagerPage.editCategory(category9298, categoryEdited);

        LOG.info("Step 2: Verify the edited category is displayed in the 'Category Manager' page.");

        Assert.assertTrue(categoryManagerPage.isCategoryDisplayed(categoryEdited));
        Assert.assertTrue(categoryManagerPage.isCategoryNotDisplayed(category9298));

    }
}
