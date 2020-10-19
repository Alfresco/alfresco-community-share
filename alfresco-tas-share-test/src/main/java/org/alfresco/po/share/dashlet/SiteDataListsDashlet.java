package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author bogdan.simion
 */

@PageObject
public class SiteDataListsDashlet extends Dashlet<SiteDataListsDashlet>
{
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
    protected WebElement newListDialogTitle;
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
    protected String listLinkLocator = "//a[@title='%s']";
    @FindBy (css = ".detail-list-item.first-item")
    protected WebElement detailListItem;
    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    private final String listItemTitleLocator = "//a[text()='%s']";
    private final String listItemDescriptionLocator = "//div[text()='%s']";

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

    public SiteDataListsDashlet assertDashletTitleEquals(String expectedDashletTitle)
    {
        LOG.info("Assert site data dashlet title equals: {}", expectedDashletTitle);
        assertEquals(getDashletTitle(), expectedDashletTitle,
            String.format("Site data dashlet title not equals %s", expectedDashletTitle));

        return this;
    }

    public SiteDataListsDashlet assertHelpBalloonMessageEquals(String expectedHelpBalloonMessage)
    {
        LOG.info("Assert site data dashlet title equals: {}", expectedHelpBalloonMessage);
        assertEquals(getHelpBalloonMessage(), expectedHelpBalloonMessage,
            String.format("Site data help balloon message dashlet not equals %s", expectedHelpBalloonMessage));

        return this;
    }

    public SiteDataListsDashlet assertCreateDataListLinkDisplayed()
    {
        LOG.info("Assert is create data list link displayed");
        assertTrue(browser.isElementDisplayed(createDataListLinkLocator),
            "Create data list link is not displayed");

        return this;
    }

    public SiteDataListsDashlet assertDisplayedMessageIs(String dataListExpectedMessage)
    {
        LOG.info("Assert displayed message is: {}", dataListExpectedMessage);
        assertEquals(message.getText(), dataListExpectedMessage,
            "Data list message is not displayed");

        return this;
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

    public SiteDataListsDashlet assertSiteDataListsItemsAreEqual(int expectedNumberOfListItems)
    {
        LOG.info("Assert site data list items equal: {}", expectedNumberOfListItems);
        assertEquals(getNumberOfSiteDataListsItemsDisplayed(), expectedNumberOfListItems,
            String.format("Data list items number not equal %d ", expectedNumberOfListItems));

        return this;
    }

    public SiteDataListsDashlet assertDataListItemTitleIsDisplayed(String expectedDataListItemTitle)
    {
        LOG.info("Wait until data list item title is displayed: {}", expectedDataListItemTitle);
        browser.waitUntilElementIsDisplayedWithRetry(By.xpath(String.format(listItemTitleLocator, expectedDataListItemTitle)));

        return this;
    }

    /**
     * Method to test if New List window is opened
     *
     * @return boolean
     */
    public boolean isNewListWindowOpened()
    {
        return (newListDialogTitle.getText().equals("New List"));
    }

    public SiteDataListsDashlet clickListItemByTitle(String itemTitle)
    {
        LOG.info("Click list item with title: {}", itemTitle);
        WebElement listItemTitle = browser.findElement(By.xpath(String.format(listItemTitleLocator, itemTitle)));
        listItemTitle.click();

        return this;
    }

    public void clickOnListLink(String listDescription)
    {
        By linkLocator = By.cssSelector(String.format(listLinkLocator, listDescription));
        WebElement listLinkTitle = browser.findElement(linkLocator);
        listLinkTitle.click();
    }

    public SiteDataListsDashlet assertNewListDialogSaveButtonIsDisplayed()
    {
        LOG.info("Assert new list dialog \"Save\" button is displayed");
        assertTrue(newListSaveButton.isDisplayed(), "New list dialog \"Save\" button is not displayed");

        return this;
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

    public SiteDataListsDashlet assertDataListLinkDescriptionContains(String expectedListDescription)
    {
        LOG.info("Assert data list link description equals: {}", expectedListDescription);
        browser.waitInSeconds(5);
        WebElement linkLocator = browser.findElement(By.xpath(String.format(listLinkLocator, expectedListDescription)));
        assertTrue(linkLocator.getText().contains(expectedListDescription),
            String.format("List link description not equals %s", linkLocator.getText()));

        return this;
    }

    public SiteDataListsDashlet assertDataListItemTitleEquals(String expectedItemTitle)
    {
        LOG.info("Assert dashlet data list item title equals: {}", expectedItemTitle);
        WebElement siteDataListItemTitle =  browser.findElement(By.xpath(String.format(listItemTitleLocator, expectedItemTitle)));
        assertEquals(siteDataListItemTitle.getText(), expectedItemTitle, String
            .format("Site data list item title %s not equals expected", listItemTitleLocator));

        return this;
    }

    public SiteDataListsDashlet assertDataListItemDescriptionEquals(String expectedItemDescription)
    {
        LOG.info("Assert dashlet data list item description equals: {}", expectedItemDescription);
        WebElement siteDataListItemDescription =  browser.findElement(By.xpath(String.format(listItemDescriptionLocator, expectedItemDescription)));
        assertEquals(siteDataListItemDescription.getText(), expectedItemDescription, String
            .format("Site data list item description %s not equals expected", listItemDescriptionLocator));

        return this;
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
