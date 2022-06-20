package org.alfresco.po.share.dashlet;

import static org.alfresco.common.Wait.WAIT_1;
import static org.alfresco.common.Wait.WAIT_5;
import static org.alfresco.common.Wait.WAIT_60;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.enums.ActivitiesDaysRangeFilter;
import org.alfresco.po.enums.ActivitiesFilter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
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
        return waitUntilElementIsVisible(dashletContainer).findElement(dashletTitle).getText();
    }

    protected WebElement getActivityRow(String expectedActivity)
    {
        waitForActivitiesToLoad();
        List<WebElement> rows = waitUntilElementsAreVisible(activityRows);
        List<String> activities = getTextFromElementList(rows);

        int retry = 0;
        while(!activities.contains(expectedActivity) && retry < WAIT_60.getValue())
        {
            retry++;
            waitToLoopTime(WAIT_1.getValue(), String.format("Wait for activity '%s' to be displayed", expectedActivity));
            refresh();
            waitUntilElementIsVisible(dashletContainer);
            rows = findElements(activityRows);
            activities = getTextFromElementList(rows);
        }
        return findFirstElementWithExactValue(rows, expectedActivity);
    }

    private void waitForActivitiesToLoad()
    {
        int retryCount = 0;
        while(retryCount < WAIT_60.getValue() && isElementDisplayed(activitiesEmptyList))
        {
            log.info("Wait for activity rows to be displayed");
            refresh();
            waitInSeconds(WAIT_1.getValue());
            waitUntilElementIsVisible(dashletContainer);
            retryCount++;
        }
    }

    public T assertActivitiesFilterHasAllOptions()
    {
        log.info("Assert all options are displayed in Activities filter");
        List<String> expectedUserActivities = Arrays.asList(language.translate("activitiesDashlet.filter.mine"),
            language.translate("activitiesDashlet.filter.everyoneElse"),
            language.translate("activitiesDashlet.filter.everyone"),
            language.translate("activitiesDashlet.filter.meFollowing"));
        clickElement(myActivitiesButton);
        List<WebElement> filterList = waitUntilElementsAreVisible(filters);
        assertEquals(expectedUserActivities, getTextFromElementList(filterList),
            "Not all options are found in activities filter");

        return (T) this;
    }

    public T assertItemsFilterHasAllOptions()
    {
        log.info("Assert all options are displayed in Items filter");
        List<String> expectedUserActivities = Arrays.asList(
            language.translate("activitiesDashlet.filter.allItems"),
            language.translate("activitiesDashlet.filter.comments"),
            language.translate("activitiesDashlet.filter.content"),
            language.translate("activitiesDashlet.filter.memberships"));
        clickElement(defaultActivitiesButton);
        List<WebElement> filterList = waitUntilElementsAreVisible(filters);
        assertEquals(expectedUserActivities, getTextFromElementList(filterList),
            "Not all options are found in activities filter");

        return (T) this;
    }

    public T assertHistoryFilterHasAllOptions()
    {
        log.info("Assert all options are displayed in History filter");
        List<String> expectedUserActivities = Arrays.asList(
            language.translate("activitiesDashlet.filter.today"),
            language.translate("activitiesDashlet.filter.last7days"),
            language.translate("activitiesDashlet.filter.last14days"),
            language.translate("activitiesDashlet.filter.last28days"));

        clickElement(daysRangeButton);
        List<WebElement> filterList = waitUntilElementsAreVisible(filters);
        assertEquals(expectedUserActivities, getTextFromElementList(filterList),
            "Not all options are found in activities filter");

        return (T) this;
    }

    public T assertSelectedActivityFilterContains(String expectedFilter)
    {
        log.info("Assert filter {} is selected", expectedFilter);
        assertTrue(getElementText(myActivitiesButton).contains(expectedFilter),
            String.format("Expected filter is %s ", expectedFilter));
        return (T) this;
    }

    public T assertSelectedHistoryOptionContains(String expectedValue)
    {
        log.info("Assert history filter {} is selected", expectedValue);
        assertTrue(getElementText(daysRangeButton).contains(expectedValue),
            String.format("Expected history filter is %s", expectedValue));
        return (T) this;
    }

    public T assertSelectedItemFilterContains(String expectedFilter)
    {
        log.info("Assert item filter {} is selected", expectedFilter);
        assertTrue(getElementText(defaultActivitiesButton)
            .contains(expectedFilter), String.format("Expected item filter is %s", expectedFilter));
        return (T) this;
    }

    public T assertRssFeedButtonIsDisplayed()
    {
        log.info("Assert Rss Feed button is displayed");
        mouseOver(activitiesDashletTitle);
        mouseOver(myActivitiesButton);
        assertTrue(isElementDisplayed(rssFeedButton), "Rss Feed button is displayed");

        return (T) this;
    }

    protected T assertRssFeedContainsExpectedUrl(String url)
    {
        log.info("Assert Rss Feed contains url {}", url);
        mouseOver(activitiesDashletTitle);
        clickElement(rssFeedButton);
        switchWindow(SECOND_TAB);
        waitUrlContains(url, WAIT_5.getValue());
        assertTrue(getCurrentUrl().contains(url), "Rss Feed is not opened with the correct url");
        closeWindowAndSwitchBack();

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
        log.info("Select activity filter {}", activitiesFilter.toString());
        mouseOver(myActivitiesButton);
        clickElement(myActivitiesButton);
        List<WebElement> options = waitUntilElementsAreVisible(dropDownOptionsList);
        selectOptionFromFilterOptionsList(getActivitiesFilterValue(activitiesFilter), options);
        waitInSeconds(WAIT_1.getValue());

        return (T) this;
    }

    public T selectOptionFromHistoryFilter(ActivitiesDaysRangeFilter noDaysOption)
    {
        log.info("Select history filter {}", noDaysOption.toString());
        clickElement(daysRangeButton);
        List<WebElement> options = waitUntilElementsAreVisible(dropDownOptionsList);
        selectOptionFromFilterOptionsList(getActivitiesDaysRangeFilter(noDaysOption), options);
        waitInSeconds(WAIT_1.getValue());

        return (T) this;
    }
}
