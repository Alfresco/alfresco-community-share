package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.AddCategoryDialog;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

public class CategoryManagerPage extends AdminToolsPage
{
    private AddCategoryDialog addCategoryDialog;
    private DeleteDialog deleteDialog;

    @RenderWebElement
    private By categoryManagerDiv = By.cssSelector("div[id$=default-category-manager]");
    private By editCategoryNameInput = By.cssSelector(".insitu-edit input[type='text']");
    private By editCategorySaveButton = By.cssSelector(".insitu-edit a:nth-of-type(1)");
    private By editCategoryCancelButton = By.cssSelector(".insitu-edit a:nth-of-type(2)");
    private By addCategoryButton = By.cssSelector("span[class*=insitu-add][style*='visibility: visible']");
    private By editCategoryButton = By.cssSelector("span.insitu-edit-category[style*='visibility: visible']");
    private By deleteCategoryButton = By.cssSelector("span.insitu-delete-category[style*='visibility: visible']");
    private String categoryLocator = "//span[contains(@id, 'labelel') and text()='%s']";

    public CategoryManagerPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
        addCategoryDialog = new AddCategoryDialog(browser);
        deleteDialog = new DeleteDialog(browser);
    }

    private WebElement category(String categoryLabel)
    {
        By categoryBy = By.xpath(String.format(categoryLocator, categoryLabel));
        return getBrowser().waitUntilElementVisible(categoryBy);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/category-manager";
    }

    public AddCategoryDialog clickAddButton()
    {
        WebElement addButton = getBrowser().waitUntilElementVisible(addCategoryButton);
        getBrowser().mouseOver(addButton);
        addButton.click();
        return (AddCategoryDialog) addCategoryDialog.renderedPage();
    }

    public CategoryManagerPage addCategory(String categoryName)
    {
        return addSubCategory("Category Root", categoryName);
    }

    public CategoryManagerPage addSubCategory(String parentCategory, String childCategoryName)
    {
        mouseOverOnCategory(parentCategory);
        return clickAddButton().addCategory(childCategoryName);
    }

    public DeleteDialog clickDeleteButton(String categoryName)
    {
        mouseOverOnCategory(categoryName);
        WebElement deleteCat = getBrowser().waitUntilElementVisible(deleteCategoryButton);
        getBrowser().mouseOver(deleteCat);
        getBrowser().clickJS(deleteCat);
        return (DeleteDialog) deleteDialog.renderedPage();
    }

    public CategoryManagerPage deleteCategory(String categoryName)
    {
        clickDeleteButton(categoryName).clickDelete();
        return (CategoryManagerPage) this.renderedPage();
    }

    public void clickEditCategory(String categoryName)
    {
        mouseOverOnCategory(categoryName);
        WebElement ediCat = getBrowser().waitUntilElementVisible(editCategoryButton);
        getBrowser().mouseOver(ediCat);
        getBrowser().clickJS(ediCat);
    }

    public CategoryManagerPage editCategory(String categoryName, String newCategoryName)
    {
        clickEditCategory(categoryName);
        if(!getBrowser().isElementDisplayed(editCategoryNameInput))
        {
            LOG.info("Click Edit category again");
            clickEditCategory(categoryName);
        }
        getBrowser().findElement(editCategoryNameInput).sendKeys(newCategoryName);
        clickElement(editCategorySaveButton);
        return (CategoryManagerPage) this.renderedPage();
    }

    private void mouseOverOnCategory(String categoryName)
    {
        WebElement category = getBrowser().waitWithRetryAndReturnWebElement(
            By.xpath(String.format(categoryLocator, categoryName)), 2, 20);
        try
        {
            getBrowser().mouseOver(category);
        }
        catch (StaleElementReferenceException e)
        {
           WebElement cat = getBrowser().waitUntilElementIsPresent(By.xpath(String.format(categoryLocator, categoryName)));
            getBrowser().mouseOver(cat);
        }
        getBrowser().waitUntilElementVisible(addCategoryButton);
    }

    public boolean isCategoryDisplayed(String categoryName)
    {
        By category = By.xpath(String.format(categoryLocator, categoryName));
        getBrowser().waitUntilElementIsDisplayedWithRetry(category, 1, 20);
        return getBrowser().isElementDisplayed(category);
    }

    public boolean isSubcategoryDisplayed(String parentCategory, String expectedSubCategory)
    {
        By category = By.xpath(String.format(categoryLocator, expectedSubCategory));
        boolean isSubCatDisplayed = getBrowser().isElementDisplayed(category);
        int retryCount = 0;
        while (retryCount < 30 && isSubCatDisplayed == false)
        {
            boolean isExpanded = getBrowser().isElementDisplayed(By.cssSelector("div.ygtvchildren table[class*='ygtvdepth1 ygtv-expanded']"));
            if (!isExpanded)
            {
                category(parentCategory).findElement(By.xpath("../..//a[@class='ygtvspacer']")).click();
            }
            isSubCatDisplayed = getBrowser().isElementDisplayed(category);
            getBrowser().refresh();
            retryCount++;
        }
        return isSubCatDisplayed;
    }

    public boolean isCategoryNotDisplayed(String categoryName)
    {
        By category = By.xpath(String.format(categoryLocator, categoryName));
        getBrowser().waitUntilElementDisappearsWithRetry(category, (int) properties.getImplicitWait());
        return !getBrowser().isElementDisplayed(category);
    }
}
