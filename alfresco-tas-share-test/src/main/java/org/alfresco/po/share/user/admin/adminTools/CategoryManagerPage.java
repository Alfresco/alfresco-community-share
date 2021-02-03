package org.alfresco.po.share.user.admin.adminTools;

import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.common.Wait.WAIT_60;
import static org.alfresco.common.Wait.WAIT_80;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertFalse;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.AddCategoryDialog;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class CategoryManagerPage extends SharePage2<CategoryManagerPage>
{
    private final By editCategoryNameInput = By.cssSelector(".insitu-edit input[type='text']");
    private final By editCategorySaveButton = By.cssSelector(".insitu-edit a:nth-of-type(1)");
    private final By addCategoryButton = By.cssSelector("span[class*=insitu-add][style*='visibility: visible; opacity: 1;");
    private final By editCategoryButton = By.cssSelector("span.insitu-edit-category[style*='visibility: visible; opacity: 1;']");
    private final By deleteCategoryButton = By.cssSelector("span.insitu-delete-category[style*='visibility: visible; opacity: 1;']");
    private final String categoryLocator = "//td[@class='ygtvcell  ygtvcontent']//span[contains(@id, 'labelel') and text()='%s']";

    public CategoryManagerPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    private WebElement category(String categoryLabel)
    {
        return webElementInteraction
            .waitUntilElementIsVisible(By.xpath(String.format(categoryLocator, categoryLabel)));
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/category-manager";
    }

    public AddCategoryDialog clickAddButton()
    {
        webElementInteraction.clickElement(addCategoryButton);
        return new AddCategoryDialog(webDriver);
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
        WebElement deleteCategoryElement = webElementInteraction.findElement(deleteCategoryButton);
        webElementInteraction.mouseOver(deleteCategoryElement);
        webElementInteraction.clickElement(deleteCategoryElement, 2000);
        return new DeleteDialog(webDriver);
    }

    public CategoryManagerPage deleteCategory(String categoryName)
    {
        clickDeleteButton(categoryName).clickDelete();
        return this;
    }

    public void clickEditCategory(String categoryName)
    {
        mouseOverOnCategory(categoryName);
        WebElement editCategoryElement = webElementInteraction.findElement(editCategoryButton);
        webElementInteraction.mouseOver(editCategoryElement);
        webElementInteraction.clickElement(editCategoryElement);
    }

    public CategoryManagerPage editCategory(String categoryName, String newCategoryName)
    {
        clickEditCategory(categoryName);
        if(!webElementInteraction.isElementDisplayed(editCategoryNameInput))
        {
            log.info("Click Edit category again");
            clickEditCategory(categoryName);
        }
        WebElement input = webElementInteraction.findElement(editCategoryNameInput);
        webElementInteraction.clearAndType(input, newCategoryName);

        WebElement saveButton = webElementInteraction.findElement(editCategorySaveButton);
        webElementInteraction.clickElement(saveButton);
        waitUntilNotificationMessageDisappears();
        return this;
    }

    private void waitForCategory(String categoryName)
    {
        By category = By.xpath(String.format(categoryLocator, categoryName));
        webElementInteraction.waitInSeconds(WAIT_2.getValue());

        int i = 0;
        while (i < WAIT_60.getValue() && !webElementInteraction.isElementDisplayed(category))
        {
            log.error("Wait for category {}", categoryName);
            webElementInteraction.refresh();
            waitToLoopTime(WAIT_2.getValue());
            i++;
        }
    }

    private void mouseOverOnCategory(String categoryName)
    {
        waitForCategory(categoryName);
        WebElement category = webElementInteraction.findElement(By.xpath(String.format(categoryLocator, categoryName)));
        int i = 0;
        while(i < WAIT_60.getValue())
        {
            waitToLoopTime(WAIT_2.getValue());
            if (webElementInteraction.isElementDisplayed(category))
            {
                webElementInteraction.mouseOver(category, 2000);
                break;
            }
            i++;
        }
    }

    public boolean isCategoryDisplayed(String categoryName)
    {
        By category = By.xpath(String.format(categoryLocator, categoryName));
        waitForCategory(categoryName);
        return webElementInteraction.isElementDisplayed(category);
    }

    public boolean isSubcategoryDisplayed(String parentCategory, String expectedSubCategory)
    {
        By category = By.xpath(String.format(categoryLocator, expectedSubCategory));
        boolean isSubCatDisplayed = webElementInteraction.isElementDisplayed(category);
        int retryCount = 0;
        while (retryCount < WAIT_80.getValue() && !isSubCatDisplayed)
        {
            navigate();
            waitToLoopTime(WAIT_2.getValue());
            boolean isExpanded = webElementInteraction.isElementDisplayed(By.cssSelector("div.ygtvchildren table[class*='ygtvdepth1 ygtv-expanded']"));
            if (!isExpanded)
            {
                WebElement parent = category(parentCategory);
                webElementInteraction.waitUntilChildElementIsPresent(parent, By.xpath("../..//a[@class='ygtvspacer']")).click();
                webElementInteraction.waitInSeconds(WAIT_2.getValue());
            }
            isSubCatDisplayed = webElementInteraction.isElementDisplayed(category);
            if(isSubCatDisplayed)
            {
                break;
            }

            retryCount++;
        }
        return isSubCatDisplayed;
    }

    public CategoryManagerPage assertCategoryIsNotDisplayed(String categoryName)
    {
        log.info("Assert category {} is not displayed", categoryName);
        By category = By.xpath(String.format(categoryLocator, categoryName));
        int i = 0;
        while(i < WAIT_60.getValue() && webElementInteraction.isElementDisplayed(category))
        {
            waitToLoopTime(WAIT_2.getValue());
            webElementInteraction.refresh();
            i++;
        }
        assertFalse(webElementInteraction.isElementDisplayed(category), String.format("Category %s is displayed", category));
        return this;
    }
}
