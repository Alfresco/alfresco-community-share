package org.alfresco.po.share.searching;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Mirela Tifui on 2/16/2018.
 */
@PageObject
public class LiveSearchPage extends HtmlPage
{
    @FindBy (css = "div[class$='alf-live-search-sites-list'] div[id^='uniqName']")
    private List<WebElement> sitesList;

    @FindBy (css = "div[class$='alf-live-search-sites-list']")
    private WebElement sitesElement;

    @FindBy (css = "div[class$='alf-live-search-documents-list']")
    private WebElement documentsElement;

    @FindBy (css = "div[class$='alf-live-search-people-list']")
    private WebElement peopleElement;

    @FindBy (css = "div[class$='alf-live-search-documents-title']")
    private WebElement documentsTitle;

    @FindBy (css = "div[class$='alf-live-search-people-title']")
    private WebElement peopleTitle;

    @FindBy (css = "div[class$='alf-live-search-sites-title']")
    private WebElement sitesTitle;

    @FindBy (css = ".alf-livesearch-context__repo")
    private WebElement scopeRepository;

    @FindBy (css = ".alf-livesearch-context__site")
    private WebElement scopeSites;

    @FindBy (css = "div[class^='alf-livesearch-context__site'] a")
    private WebElement scopeSiteText;

    @FindAll (@FindBy (css = "div[class$='alf-live-search-documents-list'] div.alf-livesearch-item"))
    private List<WebElement> documentsList;

    @FindBy (css = "div.alf-livesearch-more a span")
    private WebElement moreButton;

    @FindBy (css = "div.alfresco-header-SearchBox-clear a")
    private WebElement closeButton;

    @FindAll (@FindBy (css = "div[class$='alf-live-search-people-list'] div.alf-livesearch-item a.alf-livesearch-item__name"))
    private List<WebElement> peopleList;

    @FindAll (@FindBy (css = "div[class$='alf-live-search-documents-list'] div.alf-livesearch-item a[href$='/documentlibrary']"))
    private List<WebElement> siteList;

    @FindAll (@FindBy (css = "div.alf-livesearch-item a[href$='documentlibrary']"))
    private WebElement sitesNameList;

    public boolean areAnySiteElementsDisplayed()
    {
        return browser.isElementDisplayed(sitesElement);
    }

    public boolean areAnyDocumentElementsDisplayed()
    {
        return browser.isElementDisplayed(documentsElement);
    }

    public boolean areAnyPeopleElementsDisplayed()
    {
        return browser.isElementDisplayed(peopleElement);
    }

    public boolean isPeopleTitleDisplayed()
    {
        return browser.isElementDisplayed(peopleTitle);
    }

    public boolean isDocumentsTitleDisplayed()
    {
        return browser.isElementDisplayed(documentsTitle);
    }

    public boolean isSitesTitleDisplayed()
    {
        return browser.isElementDisplayed(sitesTitle);
    }

    public boolean isScopeSitesDisplayed()
    {
        try
        {
            getBrowser().waitInSeconds(5);
            WebElement scopeSites = browser.findElement(By.cssSelector(".alf-livesearch-context__site"));
            return scopeSites.isDisplayed();
        } catch (Exception ex)
        {
            return false;
        }
    }

    public boolean isScopeRepositoryDisplayed()
    {
        try
        {
            getBrowser().waitInSeconds(5);
            WebElement scopeRepository = browser.findElement(By.cssSelector("div[class^='alf-livesearch-context__repo']"));
            return scopeRepository.isDisplayed();
        } catch (Exception ex)
        {
            return false;
        }
    }

    public String getScopeSiteText(String expectedText)
    {
        browser.waitUntilElementContainsText(scopeSiteText, expectedText);
        return scopeSiteText.getText();
    }

    public String getDocumentDetails(String docName)
    {
        browser.waitUntilElementsVisible(documentsList);
        return browser.findFirstElementWithValue(documentsList, docName).getText();
    }

    public void clickMore()
    {
        browser.waitUntilElementVisible(moreButton, 15);
        moreButton.click();
    }

    public int getResultsListSize()
    {
        return documentsList.size();
    }

    public void closeLiveSearchResults()
    {
        browser.waitUntilElementClickable(closeButton).click();
    }

    public void clickDocumentName(String documentName)
    {
        browser.waitUntilElementIsVisibleWithRetry(By.xpath("//div[@class='alf-livesearch-item']//a[text()='" + documentName + "']"), 4);
        browser.findElement(By.xpath("//div[@class='alf-livesearch-item']//a[text()='" + documentName + "']")).click();
    }

    public void clickSiteName(String siteName)
    {
        browser.waitUntilElementIsVisibleWithRetry(By.xpath("//div[@class='alf-livesearch-item']//a[text()='" + siteName + "']"), 4);
        browser.findElement(By.xpath("//div[@class='alf-livesearch-item']//a[text()='" + siteName + "']")).click();
    }

    public void clickUserName(String userName)
    {
        browser.waitUntilElementIsVisibleWithRetry(By.xpath("//div[@class='alf-livesearch-item']//a[text()='" + userName + "']"), 4);
        browser.findElement(By.xpath("//div[@class='alf-livesearch-item']//a[text()='" + userName + "']")).click();
    }

    public String getPeopleResults()
    {
        List<String> actualItemTitle = new ArrayList<>();
        browser.waitUntilElementsVisible(peopleList);
        for (WebElement item : peopleList)
        {
            actualItemTitle.add(item.getText());
        }
        System.out.println(actualItemTitle + " this is the list returned");
        return actualItemTitle.toString();
    }

    public String getSiteResults()
    {
        List<String> actualItemTitle = new ArrayList<>();
        browser.waitUntilElementsVisible(siteList);
        for (WebElement item : siteList)
        {
            actualItemTitle.add(item.getText());
        }
        System.out.println(actualItemTitle + " this is the list returned");
        return actualItemTitle.toString();
    }

    public void clickPeopleUserName(String userName)
    {
        browser.findFirstElementWithValue(peopleList, userName).click();
    }

    public void clickSiteNameLiveSearch(String siteName)
    {
        browser.findFirstElementWithValue(siteList, siteName).click();
    }

    public void selectSiteContext()
    {
        browser.waitUntilElementVisible(scopeSites).click();
        browser.waitInSeconds(5);
    }

    public List<String> getSites()
    {
        List<String> sitesNameList = new ArrayList<>();
        getBrowser().waitUntilElementsVisible(siteList);
        for (WebElement siteName : siteList)
        {
            sitesNameList.add(siteName.getText());
        }
        return sitesNameList;
    }

    public boolean areResultsFromOtherSitesReturned(String expected)
    {
        boolean state = false;
        if (!getSites().contains(expected))
        {
            state = true;
        } else
        {
            if (getSites().toString().equals(expected))
                state = false;
        }
        return state;
    }

    public void selectRepoContext()
    {
        browser.waitUntilElementVisible(scopeRepository).click();
        browser.waitInSeconds(3);
    }
}
