package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import org.alfresco.po.share.site.dataLists.CreateDataListDialog;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

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

    @FindBy (css = ".detail-list-item.first-item")
    protected WebElement detailListItem;

    @FindBy (css = ".dashlet-padding>h3")
    protected WebElement message;

    protected By newListWindowLocator = By.cssSelector(".hd");
    protected By createDataListLinkLocator = By.cssSelector("a[href='data-lists#new']");
    protected String listLinkLocator = "//a[@title='%s']";
    private By descriptionElement = By.cssSelector(".description");

    private final String dataListRow = "//a[text()='%s']/..";
    private final String listItemDescriptionLocator = "//div[text()='%s']";

    //@Autowired
    private DataListsPage dataListsPage;

    @Autowired
    private CreateDataListDialog createDataListDialog;

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

    private WebElement getDataListRow(String dataListTitle)
    {
        LOG.info("Get data list row: {}", dataListTitle);

        return browser.waitWithRetryAndReturnWebElement(By.xpath(String.format(dataListRow,
            dataListTitle)), 1, WAIT_30);
    }

    public CreateDataListDialog clickOnCreateDataListLink()
    {
        LOG.info("Click \"New List\" button");
        browser.findElement(createDataListLinkLocator).click();

        return (CreateDataListDialog) createDataListDialog.renderedPage();
    }

    public SiteDataListsDashlet assertCreateDataListLinkDisplayed()
    {
        LOG.info("Assert create data list link displayed");
        assertTrue(browser.isElementDisplayed(createDataListLinkLocator),
            "Create data list link is not displayed");

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

    public SiteDataListsDashlet assertDataListItemTitleIsDisplayed(String expectedDataListItemTitle)
    {
        LOG.info("Assert data list item title is displayed: {}", expectedDataListItemTitle);
        assertTrue(browser.isElementDisplayed(getDataListRow(expectedDataListItemTitle)),
            String.format("Data list %s is not displayed", expectedDataListItemTitle));

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

    public DataListsPage clickListItemByTitle(String itemTitle)
    {
        LOG.info("Click list item with title: {}", itemTitle);
        getDataListRow(itemTitle).findElement(By.cssSelector("a")).click();

        return (DataListsPage) dataListsPage.renderedPage();
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
        assertEquals(message.getText(), emptyMessageExpected,
            String.format("Empty list message not equals %s ", emptyMessageExpected));

        return this;
    }
}
