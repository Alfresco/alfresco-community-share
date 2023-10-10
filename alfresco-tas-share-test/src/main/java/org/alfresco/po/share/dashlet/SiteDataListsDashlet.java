package org.alfresco.po.share.dashlet;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_2;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.dataLists.CreateDataListDialog;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SiteDataListsDashlet extends Dashlet<SiteDataListsDashlet>
{
    private final By dashletContainer = By.cssSelector("div.dashlet.site-data-lists");
    private final By message = By.cssSelector(".dashlet-padding>h3");
    private final By descriptionElement = By.cssSelector(".description");
    private final By createDataListLinkLocator = By.cssSelector("a[href='data-lists#new']");

    private final String dataListRow = "//a[text()='%s']/..";
    private final String listItemDescriptionLocator = "//div[text()='%s']";

    public SiteDataListsDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return getElementText(waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    private WebElement getDataListRow(String dataListTitle)
    {
        log.info("Get data list row: {}", dataListTitle);
        return waitWithRetryAndReturnWebElement(By.xpath(String.format(dataListRow,
            dataListTitle)), WAIT_2.getValue(), RETRY_TIME_80.getValue());
    }

    public CreateDataListDialog clickOnCreateDataListLink()
    {
        log.info("Click New List button");
        findElement(createDataListLinkLocator).click();
        return new CreateDataListDialog(webDriver);
    }

    public SiteDataListsDashlet assertCreateDataListLinkDisplayed()
    {
        log.info("Assert create data list link displayed");
        waitUntilElementIsVisible(createDataListLinkLocator);
        assertTrue(isElementDisplayed(createDataListLinkLocator),
            "Create data list link is not displayed");

        return this;
    }

    public SiteDataListsDashlet assertDataListItemTitleIsDisplayed(String expectedDataListItemTitle)
    {
        log.info("Assert data list item title is displayed: {}", expectedDataListItemTitle);
        assertTrue(isElementDisplayed(getDataListRow(expectedDataListItemTitle)),
            String.format("Data list %s is not displayed", expectedDataListItemTitle));

        return this;
    }

    public DataListsPage clickListItemByTitle(String itemTitle)
    {
        log.info("Click list item with title: {}", itemTitle);
        getDataListRow(itemTitle).findElement(By.cssSelector("a")).click();
        return new DataListsPage(webDriver);
    }

    public SiteDataListsDashlet assertDataListItemDescriptionEquals(String dataListTitle, String expectedItemDescription)
    {
        log.info("Assert dashlet data list item description equals: {}", expectedItemDescription);
        assertEquals(getDataListRow(dataListTitle).findElement(descriptionElement).getText(),
            expectedItemDescription, String.format("Site data list item description %s not equals expected",
                listItemDescriptionLocator));

        return this;
    }

    public SiteDataListsDashlet assertEmptyListMessageEquals(String emptyMessageExpected)
    {
        log.info("Assert empty list message equals: {}", emptyMessageExpected);
        assertEquals(getElementText(message), emptyMessageExpected,
            String.format("Empty list message not equals %s ", emptyMessageExpected));

        return this;
    }
}
