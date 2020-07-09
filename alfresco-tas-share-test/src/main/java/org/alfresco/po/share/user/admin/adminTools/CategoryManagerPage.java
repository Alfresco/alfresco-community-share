package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.AddCategoryDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class CategoryManagerPage extends AdminToolsPage
{
    @RenderWebElement
    @FindBy (css = "div[id$=default-category-manager]")
    private WebElement categoryManagerDiv;

    @FindBy (xpath = "//form[@class = 'insitu-edit' and @style = 'display: inline;']/input")
    private WebElement editCategoryNameInput;

    @FindBy (xpath = "//form[@class = 'insitu-edit' and @style = 'display: inline;']/a[text() = 'Save']")
    private WebElement editCategorySaveButton;

    @FindBy (xpath = "//form[@class = 'insitu-edit' and @style = 'display: inline;']/a[text() = 'Cancel']")
    private WebElement editCategoryCancelButton;

    @Autowired
    AddCategoryDialog addCategoryDialog;

    @Autowired
    DeleteDialog deleteDialog;

    private By addCategoryButton = By.cssSelector("span[class*=insitu-add][style*='visibility: visible']");

    private By editCategoryButton = By.cssSelector("span.insitu-edit-category[style*='visibility: visible']");

    private By deleteCategoryButton = By.cssSelector("span.insitu-delete-category[style*='visibility: visible']");

    private String categoryLocator = "//span[contains(@id, 'labelel') and text()='%s']";

    private WebElement category(String categoryLabel)
    {
        By categoryBy = By.xpath(String.format(categoryLocator, categoryLabel));
        return browser.waitUntilElementVisible(categoryBy);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/category-manager";
    }

    public AddCategoryDialog clickAddButton()
    {
        getBrowser().waitUntilElementVisible(addCategoryButton).click();
        return (AddCategoryDialog) addCategoryDialog.renderedPage();
    }

    public CategoryManagerPage addCategory(String categoryName)
    {
        return addSubCategory("Category Root", categoryName);
    }

    public CategoryManagerPage addSubCategory(String parentCategory, String childCategoryName)
    {
        browser.mouseOver(category(parentCategory));
        return clickAddButton().addCategory(childCategoryName);
    }

    public DeleteDialog clickDeleteButton(String categoryName)
    {
        mouseOverOnCategory(categoryName);
        browser.waitUntilElementVisible(deleteCategoryButton).click();
        return (DeleteDialog) deleteDialog.renderedPage();
    }

    public CategoryManagerPage deleteCategory(String categoryName)
    {
        return (CategoryManagerPage) clickDeleteButton(categoryName).clickDelete(this);
    }

    public CategoryManagerPage editCategory(String categoryName, String newCategoryName)
    {
        mouseOverOnCategory(categoryName);
        browser.waitUntilElementVisible(editCategoryButton).click();

        browser.waitUntilElementVisible(editCategoryNameInput);
        editCategoryNameInput.sendKeys(newCategoryName);
        editCategorySaveButton.click();
        return (CategoryManagerPage) this.renderedPage();
    }

    private void mouseOverOnCategory(String categoryName)
    {
        WebElement category = browser.waitWithRetryAndReturnWebElement(
            By.xpath(String.format(categoryLocator, categoryName)), 1, 15);
        browser.mouseOver(category);
        browser.waitUntilElementVisible(addCategoryButton);
    }

    public boolean isCategoryDisplayed(String categoryName)
    {
        By category = By.xpath(String.format(categoryLocator, categoryName));
        browser.waitUntilElementIsDisplayedWithRetry(category, 1, 10);
        return browser.isElementDisplayed(category);
    }

    public boolean isSubcategoryDisplayed(String parentCategory, String expectedSubCategory)
    {
        By category = By.xpath(String.format(categoryLocator, expectedSubCategory));
        boolean isSubCatDisplayed = getBrowser().isElementDisplayed(category);
        int retryCount = 0;
        while (retryCount < WAIT_15_SEC && isSubCatDisplayed == false)
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
        browser.waitUntilElementDisappearsWithRetry(category, (int) properties.getImplicitWait());
        return !browser.isElementDisplayed(category);
    }
}
