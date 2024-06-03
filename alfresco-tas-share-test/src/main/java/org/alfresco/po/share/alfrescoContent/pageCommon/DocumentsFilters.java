package org.alfresco.po.share.alfrescoContent.pageCommon;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static org.testng.Assert.assertTrue;
public class DocumentsFilters extends SiteCommon<DocumentsFilters>
{
    private By documents_DropDown = By.cssSelector("h2[class ='alfresco-twister alfresco-twister-open']");
    private By all_DocumentsFilter = By.cssSelector("span.all a");
    private By im_EditingFilter = By.cssSelector("span.editingMe a");
    private By others_AreEditingFilter =By.cssSelector("span.editingOthers a");
    private By recently_ModifiedFilter = By.cssSelector("span.recentlyModified a");
    private By recently_AddedFiler = By.cssSelector("span.recentlyAdded a");
    private By my_FavouritesFilter = By.cssSelector("span.favourites a");
    private By documents_Link = By.cssSelector(" td[id='ygtvcontentel1'] span[id*='ygtvlabelel'] ");
    private By library_Filter = By.cssSelector("div[class ='treeview filter']");
    private By categories_Filter = By.cssSelector("div[class ='categoryview filter']");
    private By categories_Root = By.cssSelector("td[id ='ygtvcontentel3'] span[id ='ygtvlabelel3']");
    private By tag_Filter = By.xpath("//div[@class='filter']");


    int counter = 0;
    @FindBy (css = "h2[class ='alfresco-twister alfresco-twister-open']")
    private WebElement documentsDropDown;
    @FindBy (css = "span.all a")
    private WebElement allDocumentsFilter;
    @FindBy (css = "span.editingMe a")
    private WebElement iMEditingFilter;
    @FindBy (css = "span.editingOthers a")
    private WebElement othersAreEditingFilter;
    @FindBy (css = "span.recentlyModified a")
    private WebElement recentlyModifiedFilter;
    @FindBy (css = "span.recentlyAdded a")
    private WebElement recentlyAddedFiler;
    @FindBy (css = "span.favourites a")
    private WebElement myFavouritesFilter;
    @FindBy (css = "div[class='docListInstructions'] div[id*='_default-no-items-template'] div[class ='docListInstructionTitle']")
    private WebElement noContentText;
    @FindBy (css = " td[id='ygtvcontentel1'] span[id*='ygtvlabelel'] ")
    private WebElement documentsLink;
    @FindBy (css = " td[id='ygtvcontentel4'] span[id*='ygtvlabelel'] ")
    private WebElement firstFolderInLibrary;
    @FindBy (css = " td[id='ygtvcontentel5'] span[id*='ygtvlabelel'] ")
    private WebElement secondFolderInLibrary;
    @FindBy (css = "div[class ='treeview filter']")
    private WebElement libraryFilter;
    @FindBy (css = "div[class ='categoryview filter']")
    private WebElement categoriesFilter;
    @FindBy (css = "td[id ='ygtvcontentel3'] span[id ='ygtvlabelel3']")
    private WebElement categoriesRoot;
    @FindBy (xpath = "//div[contains(@id, 'categories')]//a[@class ='ygtvspacer']")
    private WebElement categoryRootIcon;
    @FindBy (css = "div[class='filter']")
    private WebElement tagsFilter;
    @FindBy (css = "span[class ='tag']")
    private WebElement tagContent;
    @FindBy (css = ".alfresco-twister")
    private List<WebElement> sidebarFiltersList;
    @FindBy (css = ".message .more")
    private WebElement headerAfterFilter;
    @FindAll (@FindBy (css = ".filter .tag-link"))
    private List<WebElement> tagsFromFilter;
    private By sideBarFilterOptions = By.cssSelector(".alfresco-twister");
    public DocumentsFilters(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
        PageFactory.initElements(getWebDriver(),this);
    }

    /**
     * @return list of sidebar filters
     */
    public ArrayList<String> getSidebarFilters()
    {
        ArrayList<String> sidebarText = new ArrayList<>();
        List<WebElement> filterOptions = findElements(sideBarFilterOptions);
        for (WebElement aSidebarFiltersList : filterOptions)
        {
            sidebarText.add(aSidebarFiltersList.getText());
        }
        return sidebarText;
    }

    private WebElement selectCategoriesFilter(String filterName)
    {
        return findElement(By.xpath("//div[@class ='category']//div[@class ='ygtvchildren']//span[text() = '" + filterName + "']"));
    }

    private WebElement selectTagByTagName(String tagName)
    {
        return findElement(By.xpath("//span[@class ='tag']//a[text()='" + tagName + "']/.."));
    }

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isDocumentsDropDownDisplayed()
    {
        return isElementDisplayed(documents_DropDown);
    }

    public void clickAllDocumentsFilter()
    {
        allDocumentsFilter.click();
    }

    public void clickIMEditingFilter()
    {
        iMEditingFilter.click();
    }

    public void clickRecentlyModifiedFilter()
    {
        recentlyModifiedFilter.click();
    }

    public void clickRecentlyAddedFilter()
    {
        recentlyAddedFiler.click();
    }

    public void clickMyFavouritesFilter()
    {
        myFavouritesFilter.click();
    }

    public String getNoContentText()
    {
        return noContentText.getText();
    }

    public void clickOthersAreEditing()
    {
        othersAreEditingFilter.click();
    }

    public String getDocumentsLinkText()
    {
        return documentsLink.getText();
    }

    public boolean isDocumentsLinkPresent()
    {
        return isElementDisplayed(documents_Link);
    }

    public String getFirstFolderName()
    {
        return firstFolderInLibrary.getText();
    }

    public String getSecondFolderName()
    {
        return secondFolderInLibrary.getText();
    }

    public void clickFirstFolder()
    {
        firstFolderInLibrary.click();
    }

    public void clickSecondFolder()
    {
        secondFolderInLibrary.click();
    }

    public boolean isCategorisFilterDisplayed()
    {
        return isElementDisplayed(categories_Filter);
    }

    public void clickCategoriesRoot()
    {
        categoriesRoot.click();
    }

    public boolean isCategoriesRootDisplayed()
    {
        return isElementDisplayed(categories_Root);
    }

    public boolean isCategoryDisplayed(String filterName)
    {
        return selectCategoriesFilter(filterName).isDisplayed();
    }

    public void clickCategoryRootIcon()
    {
        categoryRootIcon.click();
    }

    public boolean checkIfTagsFilterIsPresent()
    {
        return isElementDisplayed(tag_Filter);
    }

    public void clickTagsLink()
    {
        tagsFilter.click();
    }

    public void clickSidebarTag(String tagName)
    {
        clickTagsLink();

        try
        {
            List<WebElement> tags = waitUntilElementsAreVisible(By.cssSelector(".filter .tag-link"));

            for (WebElement tag : tags)
            {
                if (tag.getText().equals(tagName))
                {
                    tag.click();
                    waitUntilElementContainsText(headerAfterFilter, tagName);
                    break;
                }
            }
        } catch (TimeoutException e)
        {
            while (counter < 3)
            {
                counter++;
                refresh();
                clickSidebarTag(tagName);
            }
        }
    }

    public boolean areTagsPresent()
    {
        return isElementDisplayed(By.cssSelector("span[class ='tag']"));
    }
    public void assertIsTagPresentInSideBar(String tagName)
    {
        assertTrue(getSidebarTag(tagName).contains(tagName), "Sidebar Tags filter contains " + tagName);
    }
    public String getSidebarTag(String tagName)
    {
        Parameter.checkIsMandotary("Tag", selectTagByTagName(tagName));
        return selectTagByTagName(tagName).getText();
    }

    public String getElementsFromTagsFilter()
    {
        ArrayList<String> tagsList = new ArrayList<>();
        for (WebElement tag : tagsFromFilter)
        {
            tagsList.add(tag.getText());
        }
        return tagsList.toString();
    }

    public void clickSelectedTag(String tagName)
    {
        selectTagByTagName(tagName).findElement(By.xpath(".//a")).click();
    }

    public boolean isallDocumentsFilterDisplayed()
    {
        return isElementDisplayed(all_DocumentsFilter);
    }

    public boolean isIMEditingFilterDisplayed()
    {
        return isElementDisplayed(im_EditingFilter);
    }

    public boolean isOthersAreEditingFilterDisplayed()
    {
        return isElementDisplayed(others_AreEditingFilter);
    }

    public boolean isRecentlyModifiedFilterDisplayed()
    {
        return isElementDisplayed(recently_ModifiedFilter);
    }

    public boolean isRecentlyAddedFilterDisplayed()
    {
        return isElementDisplayed(recently_AddedFiler);
    }

    public boolean isMyFavoritesFilterDisplayed()
    {
        return isElementDisplayed(my_FavouritesFilter);
    }

    public boolean isLibraryLinkDisplayed()
    {
        return isElementDisplayed(library_Filter);
    }
}