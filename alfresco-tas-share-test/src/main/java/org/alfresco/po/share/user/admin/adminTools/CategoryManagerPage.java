package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Razvan.Dorobantu
 */
@PageObject
public class CategoryManagerPage extends SharePage<CategoryManagerPage>implements AccessibleByMenuBar
{
    @Autowired
    private Toolbar toolbar;

    @Autowired
    AdminToolsPage adminToolsPage;

    @RenderWebElement
    @FindBy(xpath = "//div[@class='title' and text()='Category Manager']")
    private WebElement categoryManagerDiv;

    @FindBy(xpath = "//span[contains(@id, 'labelel') and text()='Category Root']")
    private WebElement categoryRoot;

    @FindBy(xpath = "//span[contains(@id, 'labelel') and text()='Languages']")
    private WebElement languages;

    @FindBy(xpath = "//span[contains(@id, 'labelel') and text()='Regions']")
    private WebElement regions;

    @FindBy(xpath = "//span[contains(@id, 'labelel') and text()='Software Document Classification']")
    private WebElement softwareDocumentClassification;

    @FindBy(xpath = "//span[contains(@id, 'labelel') and text()='Tags']")
    private WebElement tags;

    @FindBy(xpath = "//label[text() = 'Category name:']/../input")
    private WebElement addCategoryNameInput;

    @FindBy(xpath = "//div[text() = 'Add Category']/../div/span[@class = 'button-group']//button[text() = 'OK']")
    private WebElement addCategoryNameOKButton;

    @FindBy(xpath = "//div[text() = 'Add Category']/../div/span[@class = 'button-group']//button[text() = 'Cancel']")
    private WebElement addCategoryNameCancelButton;

    @FindBy(xpath = "//div[text() = 'Delete Category']/../div/span[@class = 'button-group']//button[text() = 'Delete']")
    private WebElement deleteCategoryOKButton;

    @FindBy(xpath = "//div[text() = 'Delete Category']/../div/span[@class = 'button-group']//button[text() = 'Cancel']")
    private WebElement deleteCategoryCancelButton;

    @FindBy(xpath = "//form[@class = 'insitu-edit' and @style = 'display: inline;']/input")
    private WebElement editCategoryNameInput;

    @FindBy(xpath = "//form[@class = 'insitu-edit' and @style = 'display: inline;']/a[text() = 'Save']")
    private WebElement editCategorySaveButton;

    @FindBy(xpath = "//form[@class = 'insitu-edit' and @style = 'display: inline;']/a[text() = 'Cancel']")
    private WebElement editCategoryCancelButton;

    private By addCategoryButton = By.cssSelector("span[title='Add Category']");
    private By editCategoryButton = By.cssSelector("span[title='Edit Category']");
    private By deleteCategoryButton = By.cssSelector("span[title='Delete Category']");

    @Override
    public String getRelativePath() {return "share/page/console/admin-console/category-manager";}

    @SuppressWarnings("unchecked")
    @Override
    public CategoryManagerPage navigateByMenuBar()
    {
        toolbar.clickAdminTools();
        browser.waitInSeconds(5);
        adminToolsPage.navigateToNodeFromToolsPanel("Category Manager");
        return (CategoryManagerPage) renderedPage();
    }

    public void addCategory(String categoryName)
    {
        int counter = 1;
        while (counter < 3)
        {
            try
            {
                browser.mouseOver(categoryRoot);
                browser.waitInSeconds(2);
                List<WebElement> addButtons = browser.findElements(addCategoryButton);
                for (WebElement visibleAddButton : addButtons)
                    if (visibleAddButton.getAttribute("style").contains("visible"))
                    {
                        visibleAddButton.click();
                        break;
                    }
                addCategoryNameInput.clear();
                addCategoryNameInput.sendKeys(categoryName);
                addCategoryNameOKButton.click();
                browser.waitInSeconds(6);
                break;
            }
            catch (NoSuchElementException | TimeoutException e)
            {
                counter++;
                browser.refresh();
                browser.waitInSeconds(6);
            }
        }
    }

    public void deleteCategory(String categoryName)
    {
        int counter = 1;
        while (counter < 3)
        {
            try
            {
                browser.mouseOver(browser.findElement(By.xpath(String.format("//span[contains(@id, 'labelel') and text()='%s']", categoryName))));
                browser.waitInSeconds(2);
                List<WebElement> deleteButtons = browser.findElements(deleteCategoryButton);
                for (WebElement visibleDeleteButton : deleteButtons)
                    if (visibleDeleteButton.getAttribute("style").contains("visible"))
                    {
                        visibleDeleteButton.click();
                        break;
                    }
                browser.waitInSeconds(2);
                deleteCategoryOKButton.click();
                browser.waitInSeconds(5);
                break;
            }
            catch (NoSuchElementException | TimeoutException e)
            {
                counter++;
                browser.refresh();
                browser.waitInSeconds(6);
            }
        }
    }

    public void editCategory(String categoryName, String newCategoryName)
    {
        int counter = 1;
        while (counter < 3)
        {
            try
            {
                browser.mouseOver(browser.findElement(By.xpath(String.format("//span[contains(@id, 'labelel') and text()='%s']", categoryName))));
                browser.waitInSeconds(2);
                List<WebElement> editButtons = browser.findElements(editCategoryButton);
                for (WebElement visibleEditButton : editButtons)
                    if (visibleEditButton.getAttribute("style").contains("visible"))
                    {
                        visibleEditButton.click();
                        break;
                    }
                browser.waitInSeconds(2);
                editCategoryNameInput.clear();
                browser.waitInSeconds(2);
                editCategoryNameInput.sendKeys(newCategoryName);
                editCategorySaveButton.click();
                browser.waitInSeconds(5);
                break;
            }
            catch (NoSuchElementException | TimeoutException e)
            {
                counter++;
                browser.refresh();
                browser.waitInSeconds(6);
            }
        }
    }

    public boolean isCategoryDisplayed(String categoryName)
    {
        By category = By.xpath(String.format("//span[contains(@id, 'labelel') and text()='%s']", categoryName));
        int counter = 1;
        while (counter < 4)
        {
            if (!browser.isElementDisplayed(category))
            {
                counter++;
                browser.refresh();
            }
            else { break; }
        }
        return browser.isElementDisplayed(category);
    }

    public boolean isCategoryRootLinkDisplayed() { return categoryRoot.isDisplayed(); }

    public boolean isRegionsLinkDisplayed() { return regions.isDisplayed(); }

    public boolean isLanguagesLinkDisplayed() { return languages.isDisplayed(); }

    public boolean isSoftwareDocumentClassificationLinkDisplayed() { return softwareDocumentClassification.isDisplayed(); }

    public boolean isTagsLinkDisplayed() { return tags.isDisplayed(); }
}
