package org.alfresco.po.share.dashlet;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.blog.BlogPostListPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.discussion.TopicViewPage;
import org.alfresco.po.share.site.link.LinkPage;
import org.alfresco.po.share.site.wiki.WikiMainPage;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.Link;

/**
 * My activities dashlet page object, holds all elements of the HTML page relating to
 * share's my activities dashlet on user dashboard page.
 */
@PageObject
@Primary
public class MyActivitiesDashlet extends Dashlet<MyActivitiesDashlet>
{
    private static By userActivitesList = By.cssSelector("ul.first-of-type>li>a");
    @RenderWebElement
    @FindBy (css = "div.dashlet.activities")
    protected HtmlElement dashletContainer;
    @FindBy (css = "div.dashlet.activities div.title")
    protected WebElement activitiesDashletTitle;
    @Autowired
    WikiMainPage wikiPage;
    @Autowired
    LinkPage linkPage;
    @Autowired
    DocumentDetailsPage documentDetailsPage;
    @Autowired
    TopicViewPage discussionsPage;
    @Autowired
    CalendarPage calendarPage;
    @Autowired
    BlogPostListPage blogPostListPage;
    @Autowired
    DataListsPage dataListsPage;
    @FindAll (@FindBy (xpath = "//div[@class='activity']//div[@class='hidden']/preceding-sibling::div[@class='more']/a"))
    private List<Link> linksMore;
    @FindAll (@FindBy (css = "div[id$='default-activityList'] > div.activity div:last-child[class$='content']"))
    private List<WebElement> activityLinks;
    @FindBy (css = "div[id$='default-activityList']")
    private WebElement activitiesEmptyList;
    @FindBy (css = "button[id$='default-user-button']")
    private Button myActivitiesButton;
    @FindAll (@FindBy (css = "div.activities div.visible ul.first-of-type li a"))
    private List<WebElement> dropDownOptionsList;
    @FindBy (css = "button[id$='default-range-button']")
    private Button daysRangeButton;
    @FindAll (@FindBy (css = "div[id$='default-activityList']>div.activity"))
    private List<WebElement> activitiesList;
    private By rssFeedButton = By.cssSelector("div[class='titleBarActionIcon rss']");
    private By userLinkLocator = By.cssSelector("span.detail>a[class^='theme-color']");
    private By siteLinkLocator = By.cssSelector("span.detail>a[class^='site-link']");
    private By documentLinkLocator = By.cssSelector("span.detail>a[class*='item-link']");
    private By detailLocator = By.cssSelector("span.detail");

    private By activityListCheckedForDisplay = By.cssSelector("div[id$='default-activityList']>div.activity");
    private List<ActivityLink> activities;

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    /**
     * Populates all the possible links that appear on the dashlet
     * data view, the links are of user, document or site.
     */
    private void populateData()
    {
        activities = new ArrayList<>();
        ArrayList<WebElement> shareLinks = new ArrayList<>();

        try
        {
            if (!linksMore.isEmpty())
            {
                for (Link link : linksMore)
                {
                    link.click();
                }
            }
            for (WebElement activityLink : activityLinks)
            {
                String description = activityLink.findElement(detailLocator).getText();

                WebElement user = activityLink.findElement(userLinkLocator);

                int noOfLinksFromActivity = activityLink.findElements(By.cssSelector("div.content>span.detail>a")).size();
                if (noOfLinksFromActivity < 2)
                {
                    activities.add(new ActivityLink(user, description));
                } else if (noOfLinksFromActivity == 2)
                {
                    if (activityLink.findElements(By.cssSelector("div.content>span.detail>a[class^='theme-color']")).size() > 1)
                    {
                        List<WebElement> userLinks = activityLink.findElements(By.cssSelector("div.content>span.detail>a[class^='theme-color']"));
                        for (WebElement element : userLinks)
                        {
                            shareLinks.add(element);
                        }
                        activities.add(new ActivityLink(shareLinks.get(0), shareLinks.get(1), description));
                    } else
                    {
                        WebElement site = activityLink.findElement(siteLinkLocator);
                        activities.add(new ActivityLink(user, site, description));
                    }
                } else
                {
                    WebElement site = activityLink.findElement(siteLinkLocator);
                    WebElement document = activityLink.findElement(documentLinkLocator);
                    activities.add(new ActivityLink(user, document, site, description));
                }
            }
        } catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to access dashlet data", nse);
        }
    }

    /**
     * Get Activities based on the link type.
     *
     * @return {@link ActivityLink} collection
     */
    public synchronized List<ActivityLink> getActivities()
    {
        browser.waitUntilElementIsDisplayedWithRetry(activityListCheckedForDisplay, 5);
        populateData();
        return activities;
    }

    /**
     * Click on item from activity list by its name
     *
     * @param name identifier to match against link title
     */
    public HtmlPage clickOnItemNameFromActivityList(final String name, HtmlPage pageToBeRendered)
    {
        browser.scrollIntoView(selectActivityDocument(name));
        browser.waitUntilElementClickable(selectActivityDocument(name)).click();
        return pageToBeRendered.renderedPage();
    }

    /**
     * Find the match and selects on the link.
     *
     * @param name identifier to match against link title
     * @param type LinkType that determines document, site or user type link
     */
    private synchronized WebElement selectLink(final String name, LinkType type)
    {
        Parameter.checkIsMandotary("Name value of link", name);
        activities = getActivities();
        for (ActivityLink activity : activities)
        {
            WebElement theLink = null;
            switch (type)
            {
                case Document:
                    theLink = activity.getDocument();
                    break;
                case Site:
                    theLink = activity.getSite();
                    break;
                case User:
                    theLink = activity.getUser();
                    break;
            }
            if (theLink != null && name.equalsIgnoreCase(theLink.getText()))
            {
                return theLink;
            }
        }
        throw new PageOperationException("Link searched for can not be found on the page");
    }

    /**
     * Selects the document link on the activity that appears on my activities dashlet
     * by matching the name to the link.
     *
     * @param name identifier
     */
    public WebElement selectActivityDocument(final String name)
    {
        try
        {
            return selectLink(name, LinkType.Document);
        } catch (Exception e)
        {
            throw new PageOperationException("no documents found matching the given title: " + name);
        }
    }

    /**
     * Gets the list of options from Site User Activities filter dropdown
     *
     * @return
     */
    public List<String> getMyActivitiesFilterOptions()
    {
        myActivitiesButton.click();

        List<String> userActivitiesFilterOptions = new ArrayList<>();

        try
        {
            for (WebElement element : browser.findDisplayedElementsFromLocator(userActivitesList))
            {
                String text = element.getText();
                if (text != null)
                {
                    userActivitiesFilterOptions.add(text.trim());
                }
            }
        } catch (NoSuchElementException nse)
        {
            LOG.error("Unable to access My Discussions dashlet user filters data", nse);
        }
        myActivitiesButton.click();
        return userActivitiesFilterOptions;

    }

    /**
     * Select option from "My Activities" drop down
     *
     * @param myActivitiesOption String
     * @return {@link ActivityLink} collection
     * author Cristina.Axinte
     */
    public MyActivitiesDashlet selectOptionFromUserActivities(String myActivitiesOption)
    {
        Parameter.checkIsMandotary("User activities option", myActivitiesOption);
        try
        {
            myActivitiesButton.click();
            browser.selectOptionFromFilterOptionsList(myActivitiesOption, dropDownOptionsList);
            return (MyActivitiesDashlet) this.renderedPage();
        } catch (NoSuchElementException nse)
        {
            LOG.error("My Activities option not present" + nse.getMessage());
            throw new PageOperationException(myActivitiesOption + " option not present.");
        }
    }

    /**
     * Select option from history filter drop down
     *
     * @param noDaysOption SiteActivitiesHistoryFilter
     * @return {@link ActivityLink} collection
     * author Cristina.Axinte
     */
    public MyActivitiesDashlet selectOptionFromHistoryFilter(SiteActivitiesDaysRangeFilter noDaysOption)
    {
        Parameter.checkIsMandotary("User activities option", noDaysOption);
        try
        {
            daysRangeButton.click();
            browser.selectOptionFromFilterOptionsList(noDaysOption.getDescription(), dropDownOptionsList);
            return this;
        } catch (NoSuchElementException nse)
        {
            LOG.error("My days range option not present" + nse.getMessage());
            throw new PageOperationException(noDaysOption + " option not present.");
        }
    }

    /**
     * Method returns if the specified option is selected in My Activities button
     *
     * @param myActivitiesOption String
     * @return boolean
     */
    public boolean isMyActivitiesOptionSelected(String myActivitiesOption)
    {
        return browser.isOptionSelectedForFilter(myActivitiesOption, myActivitiesButton.getWrappedElement());
    }

    // /**
    // * Method for navigate to RSS Feed Page from site activity dashlet.
    // *
    // * @param username String
    // * @param password String
    // * @return RssFeedPage
    // * author Cristina.Axinte
    // */
    // public HtmlPage selectRssFeedPage(String username, String password)
    // {
    // try
    // {
    // String currentUrl = driver.getCurrentUrl();
    // String rssUrlPart = (String) executeJavaScript("return activities.link");
    // String protocolVar = PageUtils.getProtocol(currentUrl);
    // String address = PageUtils.getAddress(currentUrl);
    // String rssUrl = String.format("%s%s:%s@%s%s", protocolVar, username, password, address, rssUrlPart);
    // driver.navigate().to(rssUrl);
    // return factoryPage.instantiatePage(driver, RssFeedPage.class);
    // }
    // catch (NoSuchElementException nse)
    // {
    // logger.error("Exceeded the time to find css.", nse);
    // }
    // catch (TimeoutException e)
    // {
    // logger.error("Exceeded the time to find css.", e);
    // }
    // throw new PageOperationException("Not able to select RSS Feed option");
    // }

    /**
     * Method returns if the specified option is selected in history button
     *
     * @param noDaysOption SiteActivitiesHistoryFilter
     * @return boolean
     */
    public boolean isHistoryOptionSelected(SiteActivitiesDaysRangeFilter noDaysOption)
    {
        return browser.isOptionSelectedForFilter(noDaysOption.getDescription(), daysRangeButton.getWrappedElement());
    }

    public String getEmptyDashletMessage()
    {
        return activitiesEmptyList.getText();
    }

    public boolean isRssFeedButtonDisplayed()
    {
        browser.mouseOver(activitiesDashletTitle);
        return browser.waitUntilElementVisible(rssFeedButton).isDisplayed();
    }

    public void clickRssFeedButton()
    {
        browser.mouseOver(activitiesDashletTitle);
        browser.waitUntilElementVisible(rssFeedButton).click();
    }

    /**
     * Search for an activity in the list of activity links
     *
     * @param entry String entry to be found in the ShareLinks' list
     * @return Boolean true if entry is found, false if not
     */
    public Boolean isActivityPresentInActivitiesDashlet(String entry)
    {
        int counter = 0;
        boolean found = false;
        while (!found && counter < 5)
        {
            List<ActivityLink> activityLinks = getActivities();
            for (ActivityLink link : activityLinks)
            {
                if (entry.equalsIgnoreCase(link.getDescription()))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                browser.refresh();
                counter++;
            }
        }
        return found;
    }

    public By getActivitiElement()
    {
        return activityListCheckedForDisplay;
    }

    public enum LinkType
    {
        User, Document, Site
    }
}

