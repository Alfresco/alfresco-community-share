package org.alfresco.po.share.alfrescoContent.pageCommon;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.browser.WebBrowser;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

public class DocumentsFilters extends SiteCommon<DocumentsFilters>
{
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

    public DocumentsFilters(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    /**
     * @return list of sidebar filters
     */
    public ArrayList<String> getSidebarFilters()
    {
        ArrayList<String> sidebarText = new ArrayList<>();
        for (WebElement aSidebarFiltersList : sidebarFiltersList)
        {
            sidebarText.add(aSidebarFiltersList.getText());
        }
        return sidebarText;
    }

    private WebElement selectCategoriesFilter(String filterName)
    {
        return getBrowser().findElement(By.xpath("//div[@class ='category']//div[@class ='ygtvchildren']//span[text() = '" + filterName + "']"));
    }

    private WebElement selectTagByTagName(String tagName)
    {
        return getBrowser().findElement(By.xpath("//span[@class ='tag']//a[text()='" + tagName + "']/.."));
    }

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isDocumentsDropDownDisplayed()
    {
        return documentsDropDown.isDisplayed();
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
        return documentsLink.isDisplayed();
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
        return categoriesFilter.isDisplayed();
    }

    public void clickCategoriesRoot()
    {
        categoriesRoot.click();
    }

    public boolean isCategoriesRootDisplayed()
    {
        return categoriesRoot.isDisplayed();
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
        return tagsFilter.isDisplayed();
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
            List<WebElement> tags = getBrowser().waitUntilElementsVisible(By.cssSelector(".filter .tag-link"));

            for (WebElement tag : tags)
            {
                if (tag.getText().equals(tagName))
                {
                    tag.click();
                    getBrowser().waitUntilElementContainsText(headerAfterFilter, tagName);
                    break;
                }
            }
        } catch (TimeoutException e)
        {
            while (counter < 3)
            {
                counter++;
                getBrowser().refresh();
                renderedPage();
                clickSidebarTag(tagName);
            }
        }
    }

    public boolean areTagsPresent()
    {
        return getBrowser().isElementDisplayed(By.cssSelector("span[class ='tag']"));
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
        return allDocumentsFilter.isDisplayed();
    }

    public boolean isIMEditingFilterDisplayed()
    {
        return iMEditingFilter.isDisplayed();
    }

    public boolean isOthersAreEditingFilterDisplayed()
    {
        return othersAreEditingFilter.isDisplayed();
    }

    public boolean isRecentlyModifiedFilterDisplayed()
    {
        return recentlyModifiedFilter.isDisplayed();
    }

    public boolean isRecentlyAddedFilterDisplayed()
    {
        return recentlyAddedFiler.isDisplayed();
    }

    public boolean isMyFavoritesFilterDisplayed()
    {
        return myFavouritesFilter.isDisplayed();
    }

    public boolean isLibraryLinkDisplayed()
    {
        return libraryFilter.isDisplayed();
    }
}