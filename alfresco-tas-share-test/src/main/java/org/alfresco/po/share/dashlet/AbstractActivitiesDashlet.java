package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.Utility;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.Link;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractActivitiesDashlet<T> extends Dashlet<AbstractActivitiesDashlet<T>>
{
    @Autowired
    protected UserProfilePage userProfilePage;

    @Autowired
    protected SiteDashboardPage siteDashboardPage;

    @Autowired
    protected DocumentDetailsPage documentDetailsPage;

    @RenderWebElement
    @FindBy(css = "div.dashlet.activities")
    protected WebElement dashletContainer;

    @FindBy (css = "div.dashlet.activities div.title")
    protected WebElement activitiesDashletTitle;

    @FindAll(@FindBy (xpath = "//div[@class='activity']//div[@class='hidden']/preceding-sibling::div[@class='more']/a"))
    protected List<Link> linksMore;

    @FindAll (@FindBy (css = "div[id$='default-activityList'] > div.activity div:last-child[class$='content']"))
    protected List<WebElement> activityLinks;

    @FindBy (css = "div[id$='default-activityList']")
    protected WebElement activitiesEmptyList;

    @FindBy (css = "button[id$='default-user-button']")
    protected WebElement myActivitiesButton;

    @FindAll (@FindBy (css = "div.activities div.visible ul.first-of-type li a"))
    protected List<WebElement> dropDownOptionsList;

    @FindBy (css = "button[id$='default-range-button']")
    protected Button daysRangeButton;

    @FindBy (css = "div[class$='yui-menu-button-menu visible'] a")
    protected List<WebElement> filterOptions;

    @FindAll (@FindBy (css = "div[id$='default-activityList']>div.activity"))
    protected List<WebElement> activitiesList;

    @FindBy (css = "button[id$='default-activities-button']")
    protected WebElement defaultActivitiesButton;

    @FindBy (css = "button[id$='_default-user-button']")
    protected WebElement userFilterButton;

    @FindAll (@FindBy (css = "div[class^='dashlet activities'] div[class$='visible'] a"))
    protected List<WebElement> filters;

    @FindAll (@FindBy (css = ".activity .detail"))
    protected List<WebElement> activityRows;

    @FindBy (css = "div[class='titleBarActionIcon rss']")
    protected WebElement rssFeedButton;

    protected By userLinkLocator = By.cssSelector("a:nth-of-type(1)");
    protected By siteLinkLocator = By.cssSelector("span.detail>a[class^='site-link']");
    protected By documentLinkLocator = By.cssSelector("a[class*='item-link']");
    protected By detailLocator = By.cssSelector("span.detail");
    protected By activityListCheckedForDisplay = By.cssSelector("div[id$='default-activityList']>div.activity");
    protected By userActivitesList = By.cssSelector("ul.first-of-type>li>a");
    protected List<ActivityLink> activities;

    protected WebElement documentInActivities(String documentName)
    {
        return browser.findElement(By.xpath("//div[@class='content']//a[text()='" + documentName + "']"));
    }

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    protected WebElement getActivityRow(String expectedActivity)
    {
        List<String> activities;
        try
        {
            activities = browser.getTextFromElementList(activityRows);
        }
        catch (StaleElementReferenceException e)
        {
            Utility.waitToLoopTime(1);
            activities = browser.getTextFromElementList(activityRows);
        }
        int retry = 0;
        while(!activities.contains(expectedActivity) && retry < 60)
        {
            Utility.waitToLoopTime(1, String.format("Wait for activity '%s' to be displayed", expectedActivity));
            browser.refresh();
            browser.waitUntilElementVisible(dashletContainer);
            retry++;
            activities = browser.getTextFromElementList(activityRows);
        }
        return browser.findFirstElementWithExactValue(activityRows, expectedActivity);
    }

    public T assertActivitiesFilterHasAllOptions()
    {
        List<String> expectedUserActivities = Arrays.asList(language.translate("activitiesDashlet.filter.mine"),
            language.translate("activitiesDashlet.filter.everyoneElse"),
            language.translate("activitiesDashlet.filter.everyone"),
            language.translate("activitiesDashlet.filter.meFollowing"));
        myActivitiesButton.click();
        browser.waitUntilElementsVisible(filters);
        Assert.assertTrue(expectedUserActivities.equals(browser.getTextFromElementList(filters)));
        return (T) this;
    }

    public T assertItemsFilterHasAllOptions()
    {
        List<String> expectedUserActivities = Arrays.asList(
            language.translate("activitiesDashlet.filter.allItems"),
            language.translate("activitiesDashlet.filter.comments"),
            language.translate("activitiesDashlet.filter.content"),
            language.translate("activitiesDashlet.filter.memberships"));
        defaultActivitiesButton.click();
        browser.waitUntilElementsVisible(filters);
        Assert.assertTrue(expectedUserActivities.equals(browser.getTextFromElementList(filters)));
        return (T) this;
    }

    public T assertHistoryFilterHasAllOptions()
    {
        List<String> expectedUserActivities = Arrays.asList(
            language.translate("activitiesDashlet.filter.today"),
            language.translate("activitiesDashlet.filter.last7days"),
            language.translate("activitiesDashlet.filter.last14days"),
            language.translate("activitiesDashlet.filter.last28days"));
        daysRangeButton.click();
        browser.waitUntilElementsVisible(filters);
        Assert.assertTrue(expectedUserActivities.equals(browser.getTextFromElementList(filters)));
        return (T) this;
    }

    public T assertSelectedActivityFilterIs(String expectedFilter)
    {
        LOG.info(String.format("Assert filter '%s' is selected", expectedFilter));
        Assert.assertTrue(myActivitiesButton.getText().contains(expectedFilter), String.format("Expected filter is %s", expectedFilter));
        return (T) this;
    }

    public T assertSelectedHistoryOptionIs(String expectedValue)
    {
        LOG.info(String.format("Assert history filter '%s' is selected", expectedValue));
        Assert.assertTrue(daysRangeButton.getText().contains(expectedValue), String.format("Expected history filter is %s", expectedValue));
        return (T) this;
    }

    public T assertSelectedItemFilterIs(String expectedFilter)
    {
        LOG.info(String.format("Assert item filter '%s' is selected", expectedFilter));
        Assert.assertTrue(defaultActivitiesButton.getText().contains(expectedFilter), String.format("Expected item filter is %s", expectedFilter));
        return (T) this;
    }

    public T assertRssFeedButtonIsDisplayed()
    {
        browser.mouseOver(activitiesDashletTitle);
        browser.mouseOver(myActivitiesButton);
        Assert.assertTrue(browser.isElementDisplayed(rssFeedButton), "Rss Feed button is displayed");
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
        myActivitiesButton.click();
        browser.waitUntilElementsVisible(dropDownOptionsList);
        browser.selectOptionFromFilterOptionsList(getActivitiesFilterValue(activitiesFilter), dropDownOptionsList);
        return (T) this;
    }

    public T selectOptionFromHistoryFilter(ActivitiesDaysRangeFilter noDaysOption)
    {
        daysRangeButton.click();
        browser.waitUntilElementsVisible(dropDownOptionsList);
        browser.selectOptionFromFilterOptionsList(getActivitiesDaysRangeFilter(noDaysOption), dropDownOptionsList);
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
