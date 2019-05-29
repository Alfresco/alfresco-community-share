package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author bogdan.simion
 */

@PageObject
public class SiteDataListsDashlet extends Dashlet<SiteDataListsDashlet>
{
    @Autowired
    DataListsPage dataListsPage;

    @RenderWebElement
    @FindBy (css = "div.dashlet.site-data-lists")
    protected WebElement dashletContainer;

    @FindBy (css = ".site-data-lists .detail-list-item")
    protected List<WebElement> siteDataListsItems;

    @FindBy (css = ".site-data-lists .body a")
    protected List<WebElement> dataListsLinks;

    @FindBy (css = ".dashlet-padding>h3")
    protected WebElement message;

    @FindBy (css = ".datagrid-meta h2")
    protected WebElement dataListTitle;

    @FindBy (css = ".hd")
    protected WebElement newListWindow;

    @FindBy (css = ".item-types div")
    protected WebElement listType;

    @FindBy (css = "input[name$='prop_cm_title']")
    protected WebElement listTitleTextInput;

    @FindBy (css = "textarea[title$='Content Description']")
    protected WebElement listDescriptionTextAreaInput;

    @FindBy (css = ".bdft button[id*='submit-button']")
    protected WebElement newListSaveButton;

    @FindBy (css = ".bdft button[id*='cancel-button']")
    protected WebElement newListCancelButton;

    protected By newListWindowLocator = By.cssSelector(".hd");

    protected By createDataListLinkLocator = By.cssSelector("a[href='data-lists#new']");

    protected String listlinkLocator = "a[title='%s']";

    @FindBy (css = ".detail-list-item.first-item")
    protected WebElement detailListItem;


    /**
     * Method to get the title of the current dashlet
     *
     * @return String
     */
    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }


    /**
     * Method to test if the Create Data List link is displayed
     *
     * @return boolean
     */
    public boolean isCreateDataListLinkDisplayed()
    {
        return browser.isElementDisplayed(createDataListLinkLocator);
    }

    /**
     * Method to get the text that appears when there are no Data Lists created
     *
     * @return String
     */
    public String getMessageDisplayed()
    {
        return message.getText();
    }

    /**
     * Method to get the number of list items displayed
     *
     * @return int
     */
    public int getNumberOfSiteDataListsItemsDisplayed()
    {
        int numberOfListItemsDisplayed = 0;
        for (WebElement siteDataListsItem : siteDataListsItems)
        {
            if (siteDataListsItem.isDisplayed())
            {
                numberOfListItemsDisplayed = numberOfListItemsDisplayed + 1;
            }
        }
        return numberOfListItemsDisplayed;
    }

    public boolean areSiteDataListsItemsDisplayed()
    {
        return siteDataListsItems.size() == getNumberOfSiteDataListsItemsDisplayed();
    }

    /**
     * Method to get the text that is displayed when the New List window is opened
     *
     * @return String
     */
    public String getNewListWindowText()
    {
        return newListWindow.getText();
    }

    /**
     * Method to test if New List window is opened
     *
     * @return boolean
     */
    public boolean isNewListWindowOpened()
    {
        return (newListWindow.getText().equals("New List"));
    }

    /**
     * Method to get the number of Site Data Lists
     *
     * @return boolean
     */
    public int numberOfListItems()
    {
        return siteDataListsItems.size();
    }

    /**
     * Click on the first item in the site data lists
     */
    public DataListsPage clickOnFirstListItem()
    {
        dataListsLinks.get(0).click();
        return (DataListsPage) dataListsPage.renderedPage();
    }

    public void clickOnCreateDataListLink()
    {
        WebElement createDataListLink = browser.findElement(createDataListLinkLocator);
        createDataListLink.click();
    }


    public void selectContactListFromTypesOfListsAvailable()
    {
        listType.click();
    }

    public void insertTitleAndDescriptionForDataList(String title, String description)
    {
        listTitleTextInput.sendKeys(title);
        listDescriptionTextAreaInput.sendKeys(description);
    }

    public String getListTitleTextInput()
    {
        return listTitleTextInput.getAttribute("value");
    }

    public String getListDescriptionTextInput()
    {
        return listDescriptionTextAreaInput.getAttribute("value");
    }

    public void clickOnNewListSaveButton()
    {
        newListSaveButton.click();
    }

    public void clickOnNewListCancelButton()
    {
        newListCancelButton.click();
    }

    public void clickOnListLink(String listDesctiption)
    {
        By linkLocator = By.cssSelector(String.format(listlinkLocator, listDesctiption));
        WebElement listLinkTitle = browser.findElement(linkLocator);
        listLinkTitle.click();
    }

    public boolean isNewListWindowDisplayed()
    {
        return browser.isElementDisplayed(newListWindowLocator);
    }

    public boolean areSaveAndCancelButtonDisplayed()
    {
        return newListSaveButton.isDisplayed() && newListCancelButton.isDisplayed();
    }

    public boolean isDataListPageTheCurrentPage()
    {
        return browser.getCurrentUrl().contains("data-lists");
    }

    public boolean isSiteDashboardPageTheCurrentPage(String siteName)
    {
        return browser.getCurrentUrl().contains(siteName + "/dashboard");
    }

    public void returnToSiteDashboardPage()
    {
        browser.getPreviousUrl();
        browser.navigate();
    }

    public boolean isDataListLinkDisplayed(String listDescription)
    {
        By linkLocator = By.cssSelector(String.format(listlinkLocator, listDescription));
        return browser.isElementDisplayed(linkLocator);
    }

    public boolean isDataListItemDisplayed()
    {
        return browser.isElementDisplayed(detailListItem);
    }

    public void clickOnTheFirstDataListTitleLink()
    {
        dataListsLinks.get(0).click();
    }

    public boolean isDataListTitleDisplayed()
    {
        return browser.isElementDisplayed(dataListTitle);
    }

}
