package org.alfresco.po.share.user.admin.adminTools;

import static org.alfresco.common.Wait.WAIT_10;
import static org.alfresco.common.Wait.WAIT_20;
import static org.testng.Assert.assertFalse;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.AddCategoryDialog;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

public class CategoryManagerPage extends SharePage2<CategoryManagerPage>
{
    private final By editCategoryNameInput = By.cssSelector(".insitu-edit input[type='text']");
    private final By editCategorySaveButton = By.cssSelector(".insitu-edit a:nth-of-type(1)");
    private final By addCategoryButton = By.cssSelector("span[class*=insitu-add][style*='visibility: visible']");
    private final By editCategoryButton = By.cssSelector("span.insitu-edit-category[style*='visibility: visible']");
    private final By deleteCategoryButton = By.cssSelector("span.insitu-delete-category[style*='visibility: visible']");
    private final String categoryLocator = "//span[contains(@id, 'labelel') and text()='%s']";

    public CategoryManagerPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
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
        return (AddCategoryDialog) new AddCategoryDialog(browser).renderedPage();
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
        deleteCat.click();
        return (DeleteDialog) new DeleteDialog(browser).renderedPage();
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
        WebElement input = getBrowser().waitUntilElementVisible(editCategoryNameInput);
        input.sendKeys(newCategoryName);

        WebElement saveBtn = getBrowser().waitUntilElementClickable(editCategorySaveButton);
        getBrowser().mouseOver(saveBtn);
        saveBtn.click();
        waitUntilNotificationMessageDisappears();
        return (CategoryManagerPage) this.renderedPage();
    }

    private void mouseOverOnCategory(String categoryName)
    {
        WebElement category = getBrowser().waitWithRetryAndReturnWebElement(
            By.xpath(String.format(categoryLocator, categoryName)), 1, WAIT_20.getValue());
        int i = 0;
        while(i < WAIT_10.getValue())
        {
            try
            {
                getBrowser().mouseOver(category);
                break;
            }
            catch (StaleElementReferenceException e)
            {
                LOG.error("Retry mouse over on category. Error {}", e.getMessage());
                i++;
                getBrowser().mouseOver(category);
            }
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
            navigate();
            retryCount++;
        }
        return isSubCatDisplayed;
    }

    public CategoryManagerPage assertCategoryIsNotDisplayed(String categoryName)
    {
        LOG.info("Assert category {} is not displayed", categoryName);
        By category = By.xpath(String.format(categoryLocator, categoryName));
        int i = 0;
        while(i < WAIT_10.getValue())
        {
            LOG.info("Wait until category {} is deleted", categoryName);
            if(!getBrowser().isElementDisplayed(category))
            {
                break;
            }
            i++;
        }
        assertFalse(getBrowser().isElementDisplayed(category), String.format("Category %s is displayed", category));
        return this;
    }
}
