package org.alfresco.po.share.dashlet;

import static org.alfresco.common.Wait.WAIT_1;
import static org.alfresco.common.Wait.WAIT_60;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.site.dataLists.CreateDataListDialog;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SiteDataListsDashlet extends Dashlet<SiteDataListsDashlet>
{
    private final By dashletContainer = By.cssSelector("div.dashlet.site-data-lists");
    private final By siteDataListsItems = By.cssSelector(".site-data-lists .detail-list-item");
    private final By dataListsLinks = By.cssSelector(".site-data-lists .body a");
    private final By dataListTitle = By.cssSelector(".datagrid-meta h2");
    private final By newListDialogTitle = By.cssSelector(".hd");
    private final By listType = By.cssSelector(".item-types div");
    private final By listTitleTextInput = By.cssSelector("input[name$='prop_cm_title']");
    private final By listDescriptionTextAreaInput = By.cssSelector("textarea[title$='Content Description']");
    private final By newListSaveButton = By.cssSelector(".bdft button[id*='submit-button']");
    private final By newListCancelButton = By.cssSelector(".bdft button[id*='cancel-button']");
    private final By detailListItem = By.cssSelector(".detail-list-item.first-item");
    private final By message = By.cssSelector(".dashlet-padding>h3");
    private By descriptionElement = By.cssSelector(".description");

    private final By createDataListLinkLocator = By.cssSelector("a[href='data-lists#new']");
    private final String listLinkLocator = "//a[@title='%s']";
    private final String dataListRow = "//a[text()='%s']/..";
    private final String listItemDescriptionLocator = "//div[text()='%s']";

    public SiteDataListsDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.getElementText(webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    private WebElement getDataListRow(String dataListTitle)
    {
        LOG.info("Get data list row: {}", dataListTitle);
        return webElementInteraction.waitWithRetryAndReturnWebElement(By.xpath(String.format(dataListRow,
            dataListTitle)), WAIT_1.getValue(), WAIT_60.getValue());
    }

    public CreateDataListDialog clickOnCreateDataListLink()
    {
        LOG.info("Click New List button");
        webElementInteraction.findElement(createDataListLinkLocator).click();
        return new CreateDataListDialog(webDriver);
    }

    public SiteDataListsDashlet assertCreateDataListLinkDisplayed()
    {
        LOG.info("Assert create data list link displayed");
        webElementInteraction.waitUntilElementIsVisible(createDataListLinkLocator);
        assertTrue(webElementInteraction.isElementDisplayed(createDataListLinkLocator),
            "Create data list link is not displayed");

        return this;
    }

    public int getNumberOfSiteDataListsItemsDisplayed()
    {
        int numberOfListItemsDisplayed = 0;
        for (WebElement siteDataListsItem : webElementInteraction.waitUntilElementsAreVisible(siteDataListsItems))
        {
            if (siteDataListsItem.isDisplayed())
            {
                numberOfListItemsDisplayed = numberOfListItemsDisplayed + 1;
            }
        }
        return numberOfListItemsDisplayed;
    }

    public SiteDataListsDashlet assertDataListItemTitleIsDisplayed(String expectedDataListItemTitle)
    {
        LOG.info("Assert data list item title is displayed: {}", expectedDataListItemTitle);
        assertTrue(webElementInteraction.isElementDisplayed(getDataListRow(expectedDataListItemTitle)),
            String.format("Data list %s is not displayed", expectedDataListItemTitle));

        return this;
    }

    public DataListsPage clickListItemByTitle(String itemTitle)
    {
        LOG.info("Click list item with title: {}", itemTitle);
        getDataListRow(itemTitle).findElement(By.cssSelector("a")).click();
        return new DataListsPage(webDriver);
    }

    public void clickOnListLink(String listDescription)
    {
        By linkLocator = By.cssSelector(String.format(listLinkLocator, listDescription));
        WebElement listLinkTitle = webElementInteraction.findElement(linkLocator);
        listLinkTitle.click();
    }

    public SiteDataListsDashlet assertNewListDialogSaveButtonIsDisplayed()
    {
        LOG.info("Assert new list dialog \"Save\" button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(newListSaveButton), "New list dialog \"Save\" button is not displayed");
        return this;
    }

    public SiteDataListsDashlet assertDataListItemDescriptionEquals(String dataListTitle, String expectedItemDescription)
    {
        LOG.info("Assert dashlet data list item description equals: {}", expectedItemDescription);
        assertEquals(getDataListRow(dataListTitle).findElement(descriptionElement).getText(),
            expectedItemDescription, String.format("Site data list item description %s not equals expected",
                listItemDescriptionLocator));

        return this;
    }

    public SiteDataListsDashlet assertEmptyListMessageEquals(String emptyMessageExpected)
    {
        LOG.info("Assert empty list message equals: {}", emptyMessageExpected);
        assertEquals(webElementInteraction.getElementText(message), emptyMessageExpected,
            String.format("Empty list message not equals %s ", emptyMessageExpected));

        return this;
    }
}
