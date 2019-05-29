package org.alfresco.po.share.alfrescoContent.pageCommon;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;

@PageObject
public class DocumentsFilters extends SiteCommon<DocumentsFilters>
{
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
        return browser.findElement(By.xpath("//div[@class ='category']//div[@class ='ygtvchildren']//span[text() = '" + filterName + "']"));
    }

    private WebElement selectTagByTagName(String tagName)
    {
        return browser.findElement(By.xpath("//span[@class ='tag']//a[text()='" + tagName + "']/.."));
    }

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method to check that the Documents filters drop-down is displayed
     *
     * @return
     */
    public boolean isDocumentsDropDownDisplayed()
    {
        return documentsDropDown.isDisplayed();
    }

    /**
     * Method to click on All Documents Filter
     */
    public void clickAllDocumentsFilter()
    {
        allDocumentsFilter.click();
    }

    public void clickIMEditingFilter()
    {
        iMEditingFilter.click();
    }

    /**
     * Method to click on Recently Modified Filter
     */
    public void clickRecentlyModifiedFilter()
    {
        recentlyModifiedFilter.click();
    }

    /**
     * Method to click on Recently Added Filter
     */
    public void clickRecentlyAddedFilter()
    {
        recentlyAddedFiler.click();
    }

    /**
     * Method to click on My Favorites Filter
     */
    public void clickMyFavouritesFilter()
    {
        myFavouritesFilter.click();
    }

    /**
     * Method to get the no content text
     *
     * @return
     */
    public String getNoContentText()
    {
        browser.waitInSeconds(1);
        return noContentText.getText();
    }

    /**
     * Method to click Others are editing filter
     */
    public void clickOthersAreEditing()
    {
        othersAreEditingFilter.click();
    }

    /**
     * Method to get the text for Library Documents link
     *
     * @return
     */
    public String getDocumentsLinkText()
    {
        return documentsLink.getText();
    }

    /**
     * Method to check that the documents link is present under Library
     */
    public boolean isDocumentsLinkPresent()
    {
        return documentsLink.isDisplayed();
    }

    /**
     * Method to get the first folder under Library name
     */
    public String getFirstFolderName()
    {
        return firstFolderInLibrary.getText();
    }

    /**
     * Method to get the second folder in Library name
     *
     * @return
     */
    public String getSecondFolderName()
    {
        return secondFolderInLibrary.getText();
    }

    /**
     * Method to click on the first folder
     */
    public void clickFirstFolder()
    {
        firstFolderInLibrary.click();
    }

    /**
     * Method to click on the second folder
     */
    public void clickSecondFolder()
    {
        secondFolderInLibrary.click();
    }

    /**
     * Method to check that the Categories link is present
     */
    public boolean isCategorisFilterDisplayed()
    {
        return categoriesFilter.isDisplayed();
    }

    /**
     * Method to click on Categories Root
     */
    public void clickCategoriesRoot()
    {
        categoriesRoot.click();
    }

    /**
     * Method to check if Categories Root is displayed under Categories
     *
     * @return
     */
    public boolean isCategoriesRootDisplayed()
    {
        return categoriesRoot.isDisplayed();
    }

    /**
     * Method to check that languages, regions, Software Document Classification or Tags categories are displayed
     */
    public boolean isCategoryDisplayed(String filterName)
    {
        return selectCategoriesFilter(filterName).isDisplayed();
    }

    /**
     * Method to click on the Category Root icon to expand/colapse category root
     */
    public void clickCategoryRootIcon()
    {
        categoryRootIcon.click();
    }

    /**
     * Method to check if the Tags filter is displayed
     */
    public boolean checkIfTagsFilterIsPresent()
    {
        return tagsFilter.isDisplayed();
    }

    /**
     * Method to click Tags link
     */
    public void clickTagsLink()
    {
        tagsFilter.click();
    }

    int counter = 0;

    /**
     * Click a tag from sidebar filter "Tags"
     *
     * @param tagName to be clicked on
     */
    public void clickSidebarTag(String tagName)
    {
        refresh();
        clickTagsLink();

        try
        {
            List<WebElement> tags = browser.waitUntilElementsVisible(By.cssSelector(".filter .tag-link"));

            for (WebElement tag : tags)
            {
                if (tag.getText().equals(tagName))
                {
                    tag.click();
                    browser.waitUntilElementContainsText(headerAfterFilter, tagName);
                    break;
                }
            }
        } catch (TimeoutException e)
        {
            while (counter < 3)
            {
                counter++;
                browser.refresh();
                renderedPage();
                clickSidebarTag(tagName);
            }
        }
    }

    /**
     * Method to check the presence of tags content
     */
    public boolean areTagsPresent()
    {
        return browser.isElementDisplayed(By.cssSelector("span[class ='tag']"));
    }

    /**
     * Method to get the tag content
     */
    public String getSidebarTag(String tagName)
    {
        Parameter.checkIsMandotary("Tag", selectTagByTagName(tagName));
        return selectTagByTagName(tagName).getText();
    }

    /**
     * @return List of tags from left sidebar filter Tags section
     */
    public String getElementsFromTagsFilter()
    {
        ArrayList<String> tagsList = new ArrayList<>();
        for (WebElement tag : tagsFromFilter)
        {
            tagsList.add(tag.getText());
        }
        return tagsList.toString();
    }

    /**
     * Method to click on tag from tag list
     */
    public void clickSelectedTag(String tagName)
    {
        selectTagByTagName(tagName).findElement(By.xpath(".//a")).click();
    }

    /**
     * Method to check that the All Documents filter is displayed
     */
    public boolean isallDocumentsFilterDisplayed()
    {
        return allDocumentsFilter.isDisplayed();
    }

    /**
     * Method to check that the iMEditingFilter is displayed
     */
    public boolean isIMEditingFilterDisplayed()
    {
        return iMEditingFilter.isDisplayed();
    }

    /**
     * Method to check that the Others are editing is displayed
     */
    public boolean isOthersAreEditingFilterDisplayed()
    {
        return othersAreEditingFilter.isDisplayed();
    }

    /**
     * Method to check that the recently modified filter is displayed
     */
    public boolean isRecentlyModifiedFilterDisplayed()
    {
        return recentlyModifiedFilter.isDisplayed();
    }

    /**
     * Method to check that the recently added is displayed
     */
    public boolean isRecentlyAddedFilterDisplayed()
    {
        return recentlyAddedFiler.isDisplayed();
    }

    /**
     * Method to check that My Favorites filter is displayed
     */
    public boolean isMyFavoritesFilterDisplayed()
    {
        return myFavouritesFilter.isDisplayed();
    }

    /**
     * Method to check that the library filter us displayed
     */
    public boolean isLibraryLinkDisplayed()
    {
        return libraryFilter.isDisplayed();
    }
}