package org.alfresco.po.share.dashlet;

import static org.alfresco.common.Wait.WAIT_1;
import static org.alfresco.common.Wait.WAIT_5;
import static org.alfresco.common.Wait.WAIT_60;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class AbstractActivitiesDashlet<T> extends Dashlet<AbstractActivitiesDashlet<T>>
{
    private final int SECOND_TAB = 1;
    private final By dashletContainer = By.cssSelector("div.dashlet.activities");
    private final By activitiesDashletTitle = By.cssSelector("div.dashlet.activities div.title");
    private final By myActivitiesButton = By.cssSelector("button[id$='default-user-button']");
    private final By dropDownOptionsList = By.cssSelector("div.activities div.visible ul.first-of-type li a");
    private final By daysRangeButton = By.cssSelector("button[id$='default-range-button']");
    private final By defaultActivitiesButton = By.cssSelector("button[id$='default-activities-button']");;
    private final By filters = By.cssSelector("div[class^='dashlet activities'] div[class$='visible'] a");
    protected final By activityRows = By.cssSelector(".activity .detail");
    private final By rssFeedButton = By.cssSelector("div[class='titleBarActionIcon rss']");
    protected final By userLinkLocator = By.cssSelector("a:nth-of-type(1)");
    protected final By documentLinkLocator = By.cssSelector("a[class*='item-link']");
    protected final By activitiesEmptyList = By.cssSelector("div[id$='default-activityList'] .empty");

    protected AbstractActivitiesDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.waitUntilElementIsVisible(dashletContainer).findElement(dashletTitle).getText();
    }

    protected WebElement getActivityRow(String expectedActivity)
    {
        waitForActivitiesToLoad();
        List<WebElement> rows = webElementInteraction.waitUntilElementsAreVisible(activityRows);
        List<String> activities = webElementInteraction.getTextFromElementList(rows);

        int retry = 0;
        while(!activities.contains(expectedActivity) && retry < WAIT_60.getValue())
        {
            retry++;
            waitToLoopTime(WAIT_1.getValue(), String.format("Wait for activity '%s' to be displayed", expectedActivity));
            webElementInteraction.refresh();
            webElementInteraction.waitUntilElementIsVisible(dashletContainer);
            rows = webElementInteraction.findElements(activityRows);
            activities = webElementInteraction.getTextFromElementList(rows);
        }
        return webElementInteraction.findFirstElementWithExactValue(rows, expectedActivity);
    }

    private void waitForActivitiesToLoad()
    {
        int retryCount = 0;
        while(retryCount < WAIT_60.getValue() && webElementInteraction.isElementDisplayed(activitiesEmptyList))
        {
            LOG.info("Wait for activity rows to be displayed");
            webElementInteraction.refresh();
            webElementInteraction.waitInSeconds(WAIT_1.getValue());
            webElementInteraction.waitUntilElementIsVisible(dashletContainer);
            retryCount++;
        }
    }

    public T assertActivitiesFilterHasAllOptions()
    {
        LOG.info("Assert all options are displayed in Activities filter");
        List<String> expectedUserActivities = Arrays.asList(language.translate("activitiesDashlet.filter.mine"),
            language.translate("activitiesDashlet.filter.everyoneElse"),
            language.translate("activitiesDashlet.filter.everyone"),
            language.translate("activitiesDashlet.filter.meFollowing"));
        webElementInteraction.clickElement(myActivitiesButton);
        List<WebElement> filterList = webElementInteraction.waitUntilElementsAreVisible(filters);
        assertEquals(expectedUserActivities, webElementInteraction.getTextFromElementList(filterList),
            "Not all options are found in activities filter");

        return (T) this;
    }

    public T assertItemsFilterHasAllOptions()
    {
        LOG.info("Assert all options are displayed in Items filter");
        List<String> expectedUserActivities = Arrays.asList(
            language.translate("activitiesDashlet.filter.allItems"),
            language.translate("activitiesDashlet.filter.comments"),
            language.translate("activitiesDashlet.filter.content"),
            language.translate("activitiesDashlet.filter.memberships"));
        webElementInteraction.clickElement(defaultActivitiesButton);
        List<WebElement> filterList = webElementInteraction.waitUntilElementsAreVisible(filters);
        assertEquals(expectedUserActivities, webElementInteraction.getTextFromElementList(filterList),
            "Not all options are found in activities filter");

        return (T) this;
    }

    public T assertHistoryFilterHasAllOptions()
    {
        LOG.info("Assert all options are displayed in History filter");
        List<String> expectedUserActivities = Arrays.asList(
            language.translate("activitiesDashlet.filter.today"),
            language.translate("activitiesDashlet.filter.last7days"),
            language.translate("activitiesDashlet.filter.last14days"),
            language.translate("activitiesDashlet.filter.last28days"));

        webElementInteraction.clickElement(daysRangeButton);
        List<WebElement> filterList = webElementInteraction.waitUntilElementsAreVisible(filters);
        assertEquals(expectedUserActivities, webElementInteraction.getTextFromElementList(filterList),
            "Not all options are found in activities filter");

        return (T) this;
    }

    public T assertSelectedActivityFilterContains(String expectedFilter)
    {
        LOG.info("Assert filter {} is selected", expectedFilter);
        assertTrue(webElementInteraction.getElementText(myActivitiesButton).contains(expectedFilter),
            String.format("Expected filter is %s ", expectedFilter));
        return (T) this;
    }

    public T assertSelectedHistoryOptionContains(String expectedValue)
    {
        LOG.info("Assert history filter {} is selected", expectedValue);
        assertTrue(webElementInteraction.getElementText(daysRangeButton).contains(expectedValue),
            String.format("Expected history filter is %s", expectedValue));
        return (T) this;
    }

    public T assertSelectedItemFilterContains(String expectedFilter)
    {
        LOG.info("Assert item filter {} is selected", expectedFilter);
        assertTrue(webElementInteraction.getElementText(defaultActivitiesButton)
            .contains(expectedFilter), String.format("Expected item filter is %s", expectedFilter));
        return (T) this;
    }

    public T assertRssFeedButtonIsDisplayed()
    {
        LOG.info("Assert Rss Feed button is displayed");
        webElementInteraction.mouseOver(activitiesDashletTitle);
        webElementInteraction.mouseOver(myActivitiesButton);
        assertTrue(webElementInteraction.isElementDisplayed(rssFeedButton), "Rss Feed button is displayed");

        return (T) this;
    }

    protected T assertRssFeedContainsExpectedUrl(String url)
    {
        LOG.info("Assert Rss Feed contains url {}", url);
        webElementInteraction.mouseOver(activitiesDashletTitle);
        webElementInteraction.clickElement(rssFeedButton);
        webElementInteraction.switchWindow(SECOND_TAB);
        webElementInteraction.waitUrlContains(url, WAIT_5.getValue());
        assertTrue(webElementInteraction.getCurrentUrl().contains(url), "Rss Feed is not opened with the correct url");
        webElementInteraction.closeWindowAndSwitchBack();

        return (T) this;
    }

    private String getActivitiesFilterValue(ActivitiesFilter filter)
    {
        String filterValue = "";
        switch (filter)
        {
            case MY_ACTIVITIES:
                filterValue = language.translate("activitiesDashlet.filter.mine");
                break;
            case EVERYONE_ELSE_ACTIVITIES:
                filterValue = language.translate("activitiesDashlet.filter.everyoneElse");
                break;
            case EVERYONE_ACTIVITIES:
                filterValue = language.translate("activitiesDashlet.filter.everyone");
                break;
            case IM_FOLLOWING:
                filterValue = language.translate("activitiesDashlet.filter.meFollowing");
                break;
            default:
                break;
        }
        return filterValue;
    }

    private String getActivitiesDaysRangeFilter(ActivitiesDaysRangeFilter daysRangeFilter)
    {
        String filterValue = "";
        switch (daysRangeFilter)
        {
            case TODAY:
                filterValue = language.translate("activitiesDashlet.filter.today");
                break;
            case SEVEN_DAYS:
                filterValue = language.translate("activitiesDashlet.filter.last7days");
                break;
            case FOURTEEN_DAYS:
                filterValue = language.translate("activitiesDashlet.filter.last14days");
                break;
            case TWENTY_EIGHT_DAYS:
                filterValue = language.translate("activitiesDashlet.filter.last28days");
                break;
            default:
                break;
        }
        return filterValue;
    }

    public T selectActivityFilter(ActivitiesFilter activitiesFilter)
    {
        LOG.info("Select activity filter {}", activitiesFilter.toString());
        webElementInteraction.clickElement(myActivitiesButton);
        List<WebElement> options = webElementInteraction.waitUntilElementsAreVisible(dropDownOptionsList);
        webElementInteraction.selectOptionFromFilterOptionsList(getActivitiesFilterValue(activitiesFilter), options);
        webElementInteraction.waitInSeconds(WAIT_1.getValue());

        return (T) this;
    }

    public T selectOptionFromHistoryFilter(ActivitiesDaysRangeFilter noDaysOption)
    {
        LOG.info("Select history filter {}", noDaysOption.toString());
        webElementInteraction.clickElement(daysRangeButton);
        List<WebElement> options = webElementInteraction.waitUntilElementsAreVisible(dropDownOptionsList);
        webElementInteraction.selectOptionFromFilterOptionsList(getActivitiesDaysRangeFilter(noDaysOption), options);
        webElementInteraction.waitInSeconds(WAIT_1.getValue());

        return (T) this;
    }

    public enum ActivitiesFilter
    {
        MY_ACTIVITIES,
        EVERYONE_ELSE_ACTIVITIES,
        EVERYONE_ACTIVITIES,
        IM_FOLLOWING
    }

    public enum ActivitiesDaysRangeFilter
    {
        TODAY,
        SEVEN_DAYS,
        FOURTEEN_DAYS,
        TWENTY_EIGHT_DAYS
    }
}
