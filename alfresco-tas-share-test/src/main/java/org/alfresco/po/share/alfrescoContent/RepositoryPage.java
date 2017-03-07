package org.alfresco.po.share.alfrescoContent;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageObject
public class RepositoryPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    @Autowired
    Toolbar toolbar;

    @RenderWebElement
    @FindBy(css = "[id$='default-fileUpload-button-button']")
    private WebElement uploadButton;

    @RenderWebElement 
    @FindBy(css ="div[id*='_default-paginatorBottom']")
    private WebElement pagination;

    @RenderWebElement
    @FindBy(css="div[id*='default-dl-body']")
    private WebElement repositoryContent;

    @FindBy(id = "HEADER_REPOSITORY")
    private WebElement repositoryButton;

    @FindBy(css = "button[id*='_default-createContent-button-button']")
    private WebElement createButton;

    @FindBy(css = ".filename a")
    private List<WebElement> contentItemsList;

    private By documentLibraryItemsList = By.cssSelector("[class*=data] tr");
    
    private WebElement selectContent(String contentName)
    {
        browser.waitUntilElementIsDisplayedWithRetry(documentLibraryItemsList, 6);
        List<WebElement> itemsList = browser.findElements(documentLibraryItemsList);
        return browser.findFirstElementWithValue(itemsList, contentName);
    }

    private WebElement contentName(String contentName)
    {
        browser.waitUntilElementClickable((By.xpath("//a[text()='" + contentName + "']")), 5L);
        return browser.findElement(By.xpath("//a[text()='" + contentName + "']"));
    }

    public WebElement subfolderDocListTree(String docListSubFolder)
    {
        browser.waitUntilElementClickable(
                (By.xpath("//div[@class ='breadcrumb hideable DocListTree DocListCategories']//a[text() ='" + docListSubFolder + "']")), 5L);
        return browser.findElement(By.xpath("//div[@class ='breadcrumb hideable DocListTree DocListCategories']//a[text() ='" + docListSubFolder + "']"));
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/repository";
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryPage navigateByMenuBar()
    {
        toolbar.clickRepository();
        return (RepositoryPage) renderedPage();
    }

    public boolean isUploadButtonDisplayed()
    {
        return browser.isElementDisplayed(uploadButton);
    }

    public boolean isRepositoryAvailableInToolbar()
    {
        return browser.isElementDisplayed(repositoryButton);
    }

    public void clickOnRepository()
    {
        repositoryButton.click();
    }

    public boolean isContentDisplayed(String contentName)
    {
        return selectContent(contentName).isDisplayed();
    }

    public void clickOnContent(String contentName)
    {
        contentName(contentName).click();
    }

    public void clickCreateButton()
    {
        browser.waitUntilElementClickable(createButton, 60).click();
    }

    public void clickCreateButtonWithoutWait()
    {
        createButton.click();
    }
    
    public WebElement editTag(String fileName)
    {
        return selectContent(fileName).findElement(By.xpath("//span[@title='Tag']"));               
    }
}
