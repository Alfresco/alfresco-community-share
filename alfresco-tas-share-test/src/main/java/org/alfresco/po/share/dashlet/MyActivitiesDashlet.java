package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.rest.requests.Site;
import org.alfresco.utility.Utility;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.Link;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * My activities dashlet page object, holds all elements of the HTML page relating to
 * share's my activities dashlet on user dashboard page.
 */
@PageObject
@Primary
public class MyActivitiesDashlet extends Dashlet<MyActivitiesDashlet>
{
    @Autowired
    private UserProfilePage userProfilePage;

    @Autowired
    private SiteDashboardPage siteDashboardPage;

    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    @RenderWebElement
    @FindBy (css = "div.dashlet.activities")
    protected WebElement dashletContainer;

    @FindBy (css = "div.dashlet.activities div.title")
    protected WebElement activitiesDashletTitle;

    @FindAll (@FindBy (xpath = "//div[@class='activity']//div[@class='hidden']/preceding-sibling::div[@class='more']/a"))
    private List<Link> linksMore;

    @FindAll (@FindBy (css = "div[id$='default-activityList'] > div.activity div:last-child[class$='content']"))
    private List<WebElement> activityLinks;

    @FindBy (css = "div[id$='default-activityList']")
    private WebElement activitiesEmptyList;

    @FindBy (css = "button[id$='default-user-button']")
    private WebElement myActivitiesButton;

    @FindAll (@FindBy (css = "div.activities div.visible ul.first-of-type li a"))
    private List<WebElement> dropDownOptionsList;

    @FindBy (css = "button[id$='default-range-button']")
    private Button daysRangeButton;

    @FindBy (css = "div[class$='yui-menu-button-menu visible'] a")
    private List<WebElement> filterOptions;

    @FindAll (@FindBy (css = "div[id$='default-activityList']>div.activity"))
    private List<WebElement> activitiesList;

    @FindBy (css = "button[id$='default-activities-button']")
    private WebElement defaultActivitiesButton;

    @FindBy (css = "button[id$='_default-user-button']")
    private WebElement userFilterButton;

    @FindAll (@FindBy (css = "div[class^='dashlet activities'] div[class$='visible'] a"))
    private List<WebElement> filters;

    @FindAll (@FindBy (css = ".activity .detail"))
    private List<WebElement> activityRows;

    @FindBy (css = "div[class='titleBarActionIcon rss']")
    private WebElement rssFeedButton;

    private By userLinkLocator = By.cssSelector("a:nth-of-type(1)");
    private By siteLinkLocator = By.cssSelector("span.detail>a[class^='site-link']");
    private By documentLinkLocator = By.cssSelector("a[class*='item-link']");
    private By detailLocator = By.cssSelector("span.detail");
    private By activityListCheckedForDisplay = By.cssSelector("div[id$='default-activityList']>div.activity");
    private By userActivitesList = By.cssSelector("ul.first-of-type>li>a");
    private List<ActivityLink> activities;

    private WebElement documentInActivities(String documentName)
    {
        return browser.findElement(By.xpath("//div[@class='content']//a[text()='" + documentName + "']"));
    }

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    private WebElement getActivityRow(String expectedActivity)
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

    public MyActivitiesDashlet assertAddDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        Assert.assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.createActivity"),
            user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))));
        return this;
    }

    public MyActivitiesDashlet assertAddDocumentActivityIsNotDisplayedFor(UserModel user, FileModel file, SiteModel site)
    {
        Assert.assertFalse(browser.getTextFromElementList(activityRows).contains(
            String.format(language.translate("activitiesDashlet.document.createActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle())));
        return this;
    }

    public MyActivitiesDashlet assertUpdateDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        Assert.assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.updateActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))));
        return this;
    }

    public MyActivitiesDashlet assertPreviewedDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        Assert.assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.previewedActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))));
        return this;
    }

    public MyActivitiesDashlet assertDeleteDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        Assert.assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.deleteActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))));
        return this;
    }

    public MyActivitiesDashlet assertAddedFolderActivityIsDisplayed(UserModel user, FolderModel folder, SiteModel site)
    {
        Assert.assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.folder.createActivity"),
            user.getFirstName(), user.getLastName(), folder.getName(), site.getTitle()))));
        return this;
    }

    public MyActivitiesDashlet assertDeletedFolderActivityIsDisplayed(UserModel user, FolderModel folder, SiteModel site)
    {
        Assert.assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.folder.deleteActivity"),
                user.getFirstName(), user.getLastName(), folder.getName(), site.getTitle()))));
        return this;
    }

    public UserProfilePage clickUserFromAddedDocumentActivity(UserModel user, FileModel file, SiteModel site)
    {
        getActivityRow(String.format(language.translate("activitiesDashlet.document.createActivity"),
            user.getFirstName(), user.getLastName(), file.getName(), site.getTitle())).findElement(userLinkLocator).click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }

    public DocumentDetailsPage clickDocumentLinkForAddActivity(UserModel user, FileModel file, SiteModel site)
    {
        getActivityRow(String.format(language.translate("activitiesDashlet.document.createActivity"),
            user.getFirstName(), user.getLastName(), file.getName(), site.getTitle())).findElement(documentLinkLocator).click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
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
        LOG.info("doc name: " + name);
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

    public MyActivitiesDashlet assertActivitiesFilterHasAllOptions()
    {
        List<String> expectedUserActivities = Arrays.asList(language.translate("activitiesDashlet.filter.mine"),
            language.translate("activitiesDashlet.filter.everyoneElse"),
            language.translate("activitiesDashlet.filter.everyone"),
            language.translate("activitiesDashlet.filter.meFollowing"));
        myActivitiesButton.click();
        browser.waitUntilElementsVisible(filters);
        Assert.assertTrue(expectedUserActivities.equals(browser.getTextFromElementList(filters)));
        return this;
    }

    public MyActivitiesDashlet assertItemsFilterHasAllOptions()
    {
        List<String> expectedUserActivities = Arrays.asList(language.translate("activitiesDashlet.filter.allItems"),
            language.translate("activitiesDashlet.filter.comments"),
            language.translate("activitiesDashlet.filter.content"),
            language.translate("activitiesDashlet.filter.memberships"));
        defaultActivitiesButton.click();
        browser.waitUntilElementsVisible(filters);
        Assert.assertTrue(expectedUserActivities.equals(browser.getTextFromElementList(filters)));
        return this;
    }

    public MyActivitiesDashlet assertHistoryFilterHasAllOptions()
    {
        List<String> expectedUserActivities = Arrays.asList(language.translate("activitiesDashlet.filter.today"),
            language.translate("activitiesDashlet.filter.last7days"),
            language.translate("activitiesDashlet.filter.last14days"),
            language.translate("activitiesDashlet.filter.last28days"));
        daysRangeButton.click();
        browser.waitUntilElementsVisible(filters);
        Assert.assertTrue(expectedUserActivities.equals(browser.getTextFromElementList(filters)));
        return this;
    }

    public MyActivitiesDashlet selectActivityFilter(ActivitiesFilter activitiesFilter)
    {
        myActivitiesButton.click();
        browser.waitUntilElementsVisible(dropDownOptionsList);
        browser.selectOptionFromFilterOptionsList(getActivitiesFilterValue(activitiesFilter), dropDownOptionsList);
        return this;
    }

    public MyActivitiesDashlet selectOptionFromHistoryFilter(SiteActivitiesDaysRangeFilter noDaysOption)
    {
        daysRangeButton.click();
        browser.waitUntilElementsVisible(dropDownOptionsList);
        browser.selectOptionFromFilterOptionsList(noDaysOption.getDescription(), dropDownOptionsList);
        return this;
    }

    public MyActivitiesDashlet assertSelectedActivityFilterIs(String expectedFilter)
    {
        LOG.info(String.format("Assert filter '%s' is selected", expectedFilter));
        Assert.assertTrue(myActivitiesButton.getText().contains(expectedFilter), String.format("Expected filter is %s", expectedFilter));
        return this;
    }

    public MyActivitiesDashlet assertSelectedHistoryOptionIs(String expectedValue)
    {
        LOG.info(String.format("Assert history filter '%s' is selected", expectedValue));
        Assert.assertTrue(daysRangeButton.getText().contains(expectedValue), String.format("Expected history filter is %s", expectedValue));
        return this;
    }

    public MyActivitiesDashlet assertSelectedItemFilterIs(String expectedFilter)
    {
        LOG.info(String.format("Assert item filter '%s' is selected", expectedFilter));
        Assert.assertTrue(defaultActivitiesButton.getText().contains(expectedFilter), String.format("Expected item filter is %s", expectedFilter));
        return this;
    }

    public String getEmptyDashletMessage()
    {
        return activitiesEmptyList.getText();
    }

    public MyActivitiesDashlet assertEmptyDashletMessageIsCorrect()
    {
        Assert.assertEquals(activitiesEmptyList.getText(), language.translate("myactivitiesDashlet.empty"));
        return this;
    }

    public MyActivitiesDashlet assertRssFeedButtonIsDisplayed()
    {
        browser.mouseOver(activitiesDashletTitle);
        browser.mouseOver(myActivitiesButton);
        Assert.assertTrue(browser.isElementDisplayed(rssFeedButton), "Rss Feed button is displayed");
        return this;
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

    public HtmlPage clickOnDocumentLinkInActivities(String docName, HtmlPage pageToRender)
    {
        browser.waitUntilElementClickable(documentInActivities(docName)).click();
        return pageToRender.renderedPage();
    }

    public void enableRSSFeed(String partialUnchangedUrl, String siteName)
    {
        String ipAddress = "http://" + properties.getServer();
        String url = String.format("s%s%s%", ipAddress, partialUnchangedUrl, siteName) + "&dateFilter=7&userFilter=all&activityFilter=";
        LOG.info("url " + url);
        browser.navigate().to(url);
    }

    public List<String> getItemTypeFilterOptionAvailable()
    {
        defaultActivitiesButton.click();
        getBrowser().waitUntilElementsVisible(filterOptions);
        List<String> actualOptions = new ArrayList<>();
        for (WebElement option : filterOptions)
        {
            actualOptions.add(option.getText());
        }
        return actualOptions;
    }

    public List<String> getRangeFilterOptions()
    {
        daysRangeButton.click();
        getBrowser().waitUntilElementsVisible(filterOptions);
        List<String> actualOptions = new ArrayList<>();
        for (WebElement option : filterOptions)
        {
            actualOptions.add(option.getText());
        }
        return actualOptions;
    }

    public SiteDashboardPage selectUserFilterOption(String filterOption)
    {
        userFilterButton.click();
        getBrowser().waitUntilElementsVisible(filterOptions);
        browser.findFirstElementWithExactValue(filterOptions, filterOption).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }

    public SiteDashboardPage selectItemTypeFilterOption(String filterOption)
    {
        defaultActivitiesButton.click();
        getBrowser().waitUntilElementsVisible(filterOptions);
        browser.findFirstElementWithExactValue(filterOptions, filterOption).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }

    public SiteDashboardPage selectRangeFilterOption(String filterOption)
    {
        daysRangeButton.click();
        getBrowser().waitUntilElementsVisible(filterOptions);
        browser.findFirstElementWithExactValue(filterOptions, filterOption).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }

    public String getActivitiesDashletResultsText()
    {
        return getBrowser().findElement(By.cssSelector("div[id$='_default-activityList'] div.empty")).getText();
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

    public enum LinkType
    {
        User, Document, Site
    }

    public enum ActivitiesFilter
    {
        MY_ACTIVITIES,
        EVERYONE_ELSE_ACTIVITIES,
        EVERYONE_ACTIVITIES,
        IM_FOLLOWING
    }
}

