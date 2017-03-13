package org.alfresco.share.adminTools.categoryManager;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.user.admin.adminTools.CategoryManagerPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * @author Razvan.Dorobantu
 */
public class CategoryManagerTests extends ContextAwareWebTest
{
    @Autowired
    CategoryManagerPage categoryManagerPage;

    String category9295 = "categoryC9295" + DataUtil.getUniqueIdentifier();
    String category9301 = "categoryC9301" + DataUtil.getUniqueIdentifier();
    String category9298 = "categoryC9298" + DataUtil.getUniqueIdentifier();
    String categoryEdited = "categoryEdited" + DataUtil.getUniqueIdentifier();

    @BeforeClass
    public void beforeClass()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        categoryManagerPage.navigateByMenuBar();
        categoryManagerPage.addCategory(category9301);
        categoryManagerPage.addCategory(category9298);
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(adminUser, adminPassword);
        categoryManagerPage.navigateByMenuBar();
    }

    @AfterClass
    public void afterClassDeleteAddedCategories()
    {
        for (String categoryName : Arrays.asList(category9295, category9298, categoryEdited, category9301))
            if (categoryManagerPage.isCategoryDisplayed(categoryName))
                categoryManagerPage.deleteCategory(categoryName);
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C9294")
    @Test
    public void verifyCategoryManagerPage()
    {
        LOG.info("Step 1: Verify if the 'Category Manager' page has the specific links displayed.");
        Assert.assertTrue(categoryManagerPage.isCategoryRootLinkDisplayed(), "Category Root link is displayed.");
        Assert.assertTrue(categoryManagerPage.isLanguagesLinkDisplayed(), "Languages link is displayed.");
        Assert.assertTrue(categoryManagerPage.isRegionsLinkDisplayed(), "Regions link is displayed.");
        Assert.assertTrue(categoryManagerPage.isSoftwareDocumentClassificationLinkDisplayed(), "Software Document Classification link is displayed.");
        Assert.assertTrue(categoryManagerPage.isTagsLinkDisplayed(), "Tags link is displayed.");
    }

    @TestRail(id = "C9295")
    @Test
    public void addNewCategory()
    {
        LOG.info("Step 1: Add a new category in the 'Category Manager' page.");
        categoryManagerPage.addCategory(category9295);
        getBrowser().waitInSeconds(5); getBrowser().refresh();

        LOG.info("Step 2: Verify the category is added in the 'Category Manager' page.");
        Assert.assertTrue(categoryManagerPage.isCategoryDisplayed(category9295), "New category displayed" );
    }

    @TestRail(id = "C9301")
    @Test
    public void deleteCategory()
    {
        LOG.info("Step 1: Delete the category.");
        categoryManagerPage.deleteCategory(category9301);

        LOG.info("Step 2: Verify the delete category is no longer present in the 'Category Manager' page.");
        Assert.assertFalse(categoryManagerPage.isCategoryDisplayed(category9301));
    }

    @TestRail(id = "C9298")
    @Test
    public void editCategory()
    {
        LOG.info("Step 1: Edit the category.");
        categoryManagerPage.editCategory(category9298, categoryEdited);

        LOG.info("Step 2: Verify the edited category is displayed in the 'Category Manager' page.");
        Assert.assertTrue(categoryManagerPage.isCategoryDisplayed(categoryEdited));
        Assert.assertFalse(categoryManagerPage.isCategoryDisplayed(category9298));

    }


}
