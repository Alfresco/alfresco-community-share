package org.alfresco.po.share.searching;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class LiveSearchPageSupport extends DocumentLibraryPage {
    public LiveSearchPageSupport(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }
    private By documentsTitle = By.cssSelector("div[class$='alf-live-search-documents-title']");
    private By peopleTitle = By.cssSelector("div[class$='alf-live-search-people-title']");
    private By sitesTitle = By.cssSelector("div[class$='alf-live-search-sites-title']");
    private By documentsElement = By.cssSelector("div[class$='alf-live-search-documents-list']");
    private By peopleElement = By.cssSelector("div[class$='alf-live-search-people-list']");
    private By sitesElement = By.cssSelector("div[class$='alf-live-search-sites-list']");
    private By scopeSiteText = By.cssSelector("div[class^='alf-livesearch-context__site'] a");
    private By documentsList = By.cssSelector("div[class$='alf-live-search-documents-list'] div.alf-livesearch-item");
    private final By searchBoxInput = By.cssSelector("input[id='HEADER_SEARCHBOX_FORM_FIELD']");
    private final By moreButton = By.cssSelector("div.alf-livesearch-more a span");
    private final By closeButton = By.cssSelector("div.alfresco-header-SearchBox-clear a");
    private final By peopleList = By.cssSelector("div[class$='alf-live-search-people-list'] div.alf-livesearch-item a.alf-livesearch-item__name");
    private final By siteList = By.xpath("//a[@class=\"alf-livesearch-item__name\"]");
    private final By scopeSites = By.cssSelector(".alf-livesearch-context__site");
    private final By scopeRepository = By.cssSelector(".alf-livesearch-context__repo");

    public boolean isDocumentsTitleDisplayed()
    {
        return findElement(documentsTitle).isDisplayed();
    }
    public boolean isPeopleTitleDisplayed()
    {
        return findElement(peopleTitle).isDisplayed();
    }
    public boolean isSitesTitleDisplayed()
    {
        return findElement(sitesTitle).isDisplayed();
    }
    public boolean areAnyDocumentElementsDisplayed()
    {
        return findElement(documentsElement).isDisplayed();
    }
    public boolean areAnyPeopleElementsDisplayed()
    {
        return findElement(peopleElement).isDisplayed();
    }
    public boolean areAnySiteElementsDisplayed()
    {
        return findElement(sitesElement).isDisplayed();
    }
    public void clickMore()
    {
        clickElement(moreButton, 15);

    }
    public void closeLiveSearchResults()
    {
        waitUntilElementClickable(closeButton).click();
    }
    public void clickDocumentName(String documentName)
    {
        waitUntilElementIsVisibleWithRetry(By.xpath("//div[@class='alf-livesearch-item']//a[text()='" + documentName + "']"), 4);
        findElement(By.xpath("//div[@class='alf-livesearch-item']//a[text()='" + documentName + "']")).click();
    }
    public void clickUserName(String userName)
    {
        waitUntilElementIsVisibleWithRetry(By.xpath("//div[@class='alf-livesearch-item']//a[text()='" + userName + "']"), 4);
        findElement(By.xpath("//div[@class='alf-livesearch-item']//a[text()='" + userName + "']")).click();
    }
    public void clickSiteName(String siteName)
    {
        waitUntilElementIsVisibleWithRetry(By.xpath("//div[@class='alf-livesearch-item']//a[text()='" + siteName + "']"), 4);
        findElement(By.xpath("//div[@class='alf-livesearch-item']//a[text()='" + siteName + "']")).click();
    }
    public boolean isScopeRepositoryDisplayed(String searchTerm)
    {

        {
            waitInSeconds(3);
            clearAndType(searchBoxInput, searchTerm);
            waitInSeconds(10);
            clearAndType(searchBoxInput, searchTerm);
            waitInSeconds(10);
            clearAndType(searchBoxInput, searchTerm);
            waitInSeconds(3);
            clearAndType(searchBoxInput, searchTerm);
            waitInSeconds(10);
            clearAndType(searchBoxInput, searchTerm);
            waitInSeconds(10);
            clearAndType(searchBoxInput, searchTerm);
            WebElement scopeRepository = findElement(By.cssSelector("div[class^='alf-livesearch-context__repo']"));
            return scopeRepository.isDisplayed();
        }
    }
    public boolean isScopeSitesDisplayed()
    {

        {
            waitInSeconds(5);
            WebElement scopeSites = findElement(By.cssSelector(".alf-livesearch-context__site"));
            return scopeSites.isDisplayed();
        }
    }
    public String getScopeSiteText(String siteName)

    {
        waitInSeconds(3);
        clearAndType(searchBoxInput, siteName);
        waitInSeconds(10);
        clearAndType(searchBoxInput, siteName);
        waitInSeconds(10);
        clearAndType(searchBoxInput, siteName);
        WebElement scopeText = findElement(scopeSiteText);
        return scopeText.getText() ;
    }


    public String getDocumentDetails(String docName)
    {
        waitInSeconds(3);
        clearAndType(searchBoxInput, docName);
        waitInSeconds(10);
        clearAndType(searchBoxInput, docName);
        waitInSeconds(10);
        clearAndType(searchBoxInput, docName);
        waitInSeconds(3);
        clearAndType(searchBoxInput, docName);
        waitInSeconds(10);
        clearAndType(searchBoxInput, docName);
        waitInSeconds(10);
        clearAndType(searchBoxInput, docName);

        waitUntilElementIsDisplayedWithRetry(documentsList);
        return findFirstElementWithValue(documentsList, docName).getText();
    }

    public int getResultsListSize()
    {
        return findElements(documentsList).size();
    }
    public void clickPeopleUserName(String userName)
    {
        findFirstElementWithValue(peopleList, userName).click();
    }
    public String getPeopleResults()
    {
        List<String> actualItemTitle = new ArrayList<>();
        waitInSeconds(4);

        for (WebElement item : findElements(peopleList))
        {
            actualItemTitle.add(item.getText());
        }
        System.out.println(actualItemTitle + " this is the list returned");
        return actualItemTitle.toString();
    }
    public void selectSiteContext()
    {
        clickElement(scopeSites);
        waitInSeconds(5);
    }
    public List<String> getSites()
    {
        List<String> sitesNameList = new ArrayList<>();
        waitInSeconds(6);

        for (WebElement siteName : findElements(siteList))
        {
            sitesNameList.add(siteName.getText());
        }
        return sitesNameList;
    }
    public void selectRepoContext()
    {
        clickElement(scopeRepository);
        waitInSeconds(3);
    }
    public boolean areResultsFromOtherSitesReturned(String expected)
    {
        boolean state = false;
        if (!getSites().contains(expected))
        {
            state = false;

        } else
        {
            if (getSites().toString().equals(expected))
                state = true;
        }
        return state;
    }
    public String getSiteResults()
    {
        List<String> actualItemTitle = new ArrayList<>();
        waitInSeconds(2);

        for (WebElement item : findElements(siteList))
        {
            actualItemTitle.add(item.getText());
        }
        System.out.println(actualItemTitle + " this is the list returned");
        return actualItemTitle.toString();
    }
    public void clickSiteNameLiveSearch(String siteName)
    {
        findFirstElementWithValue(siteList, siteName).click();
    }
}
