package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class CategoryManagerPage extends AdminToolsPage
{
    @RenderWebElement
    @FindBy(css = "div[id$=default-category-manager]")
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

    @FindBy(css = "div#userInput input[id*=alf-id]")
    private WebElement addCategoryNameInput;

    @FindBy(xpath = "//div[@id='userInput']//button[text()='OK']")
    private WebElement addCategoryNameOKButton;

    @FindBy(xpath = "//div[@id='userInput']//button[text()='Cancel']")
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

    private By addCategoryButton = By.cssSelector("span[class*=insitu-add]");
    
    private By editCategoryButton = By.cssSelector("span.insitu-edit-category");
    
    private By deleteCategoryButton = By.cssSelector("span.insitu-delete-category");
    
    private String categoryLocator = "//span[contains(@id, 'labelel') and text()='%s']";

    @Override
    public String getRelativePath() {return "share/page/console/admin-console/category-manager";}

    public void addCategory(String categoryName)
    {
        browser.waitUntilElementVisible(categoryRoot);

        browser.mouseOver(categoryRoot);
        getBrowser().waitUntilElementVisible(addCategoryButton);
        browser.findFirstDisplayedElement(addCategoryButton).click();

        browser.waitUntilElementVisible(addCategoryNameInput);
        addCategoryNameInput.sendKeys(categoryName);
        getBrowser().waitUntilElementClickable(addCategoryNameOKButton);
        addCategoryNameOKButton.click();
        getBrowser().waitUntilElementDisappears(By.cssSelector("div.bd span.message"));
        renderedPage();
    }

    public void deleteCategory(String categoryName)
    {
        mouseOverOnCategory(categoryName);
        
        browser.findFirstDisplayedElement(deleteCategoryButton).click();
        browser.waitUntilElementVisible(deleteCategoryOKButton);
        deleteCategoryOKButton.click();
        renderedPage();
    }

    public void editCategory(String categoryName, String newCategoryName)
    {
        mouseOverOnCategory(categoryName);
        browser.findFirstDisplayedElement(editCategoryButton).click();
        
        browser.waitUntilElementVisible(editCategoryNameInput);
        editCategoryNameInput.sendKeys(newCategoryName);
        editCategorySaveButton.click();
        renderedPage();
    }

    private void mouseOverOnCategory(String categoryName)
    {
        By categoryBy = By.xpath(String.format(categoryLocator, categoryName));
        
        browser.waitUntilElementIsDisplayedWithRetry(categoryBy, ((int) properties.getImplicitWait()));
        WebElement elem = categoryManagerDiv.findElement(categoryBy);
        browser.mouseOver(elem);       
        browser.waitInSeconds(2);
    }
    
    public boolean isCategoryDisplayed(String categoryName)
    {
        By category = By.xpath(String.format(categoryLocator, categoryName));
        browser.waitUntilElementIsDisplayedWithRetry(category, ((int) properties.getImplicitWait()));
        return browser.isElementDisplayed(category);
    }
    
    public boolean isCategoryNotDisplayed(String categoryName)
    {
        By category = By.xpath(String.format("//span[contains(@id, 'labelel') and text()='%s']", categoryName));
        browser.waitUntilElementDisappearsWithRetry(category, 1);
        return !browser.isElementDisplayed(category);
    }
    
    public boolean isCategoryRootLinkDisplayed() { return categoryRoot.isDisplayed(); }

    public boolean isRegionsLinkDisplayed() { return regions.isDisplayed(); }

    public boolean isLanguagesLinkDisplayed() { return languages.isDisplayed(); }

    public boolean isSoftwareDocumentClassificationLinkDisplayed() { return softwareDocumentClassification.isDisplayed(); }

    public boolean isTagsLinkDisplayed() { return tags.isDisplayed(); }
}
