package org.alfresco.po.share.site.wiki;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

public class WikiListPage extends SiteCommon<WikiListPage>
{
    //@Autowired
    EditWikiPage editWikiPage;

    //@Autowired
    WikiPage wikiPage;

    //@Autowired
    WikiDetailsPage wikiDetailsPage;

    @RenderWebElement
    @FindBy (css = "span.forwardLink>a")
    private WebElement mainPageLink;

    @RenderWebElement
    @FindBy (css = "span.first-child button[id$=default-create-button-button]")
    private WebElement newPage;

    @FindAll (@FindBy (css = "div.wikipage"))
    private List<WebElement> wikiPagesList;

    @FindAll (@FindBy (css = "div.pageTitle a"))
    private List<WebElement> wikiPagesTitleList;

    @RenderWebElement
    @FindBy (css = "[id$=default-pagelist] div")
    private WebElement noWikiPage;

    @FindAll (@FindBy (css = "[id$=default-ul] li:not(:first-child)"))
    private List<WebElement> tagsList;

    @FindBy (css = "[id=prompt]")
    private WebElement deletePopUp;

    @FindBy (xpath = "//a[text()='Show All Items']")
    private WebElement showAllTagsFilter;

    @FindBy (css = "span[class='all'] a")
    private WebElement allPagesFilter;

    @FindBy (css = "span[class='myPages'] a")
    private WebElement myPagesFilter;


    private By editPage = By.cssSelector("div.editPage a");
    private By pageName = By.cssSelector("[class=pageTitle] a");
    private By deletePage = By.cssSelector(".deletePage a");
    private By missingWikiPage = By.cssSelector(".rich-content a");
    private By pageDetails = By.cssSelector("div.detailsPage a");
    private By wikiRowDetails = By.cssSelector("div[class='publishedDetails'] span");
    private By wikiPageContent = By.cssSelector("div[class='pageCopy rich-content']");
    private By wikiPageTags = By.cssSelector("div[class=pageTags] a");

    public WikiListPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    /**
     * This method returns the list of wiki page titles
     *
     * @return list of wiki page titles
     */

    public List<String> getWikiPageTitlesList()
    {
        List<String> wikiPageTitles = new ArrayList<>();
        for (WebElement wikiPageTitle : wikiPagesTitleList)
        {
            wikiPageTitles.add(wikiPageTitle.getText());
        }
        return wikiPageTitles;
    }

    /**
     * This method returns the list of wiki page titles
     *
     * @return list of wiki page titles
     */

    public int getWikiPageTitlesListSize()
    {
        return getWikiPageTitlesList().size();
    }

    /**
     * This method is used to get the message displayed
     * in case no wiki page is in the list
     *
     * @return
     */
    public String noWikiPageDisplayed()
    {
        return noWikiPage.getText();
    }

    /**
     * This method is used to get the list of tags
     * for each wiki page
     *
     * @return
     */
    public List<String> getTagsList()
    {
        List<String> tags = new ArrayList<>();
        for (WebElement tag : tagsList)
        {
            tags.add(tag.getText());
        }
        return tags;
    }

    public WebElement selectWikiDetailsRow(String wikiPage)
    {
        return getBrowser().findFirstElementWithValue(wikiPagesList, wikiPage);
    }

    public EditWikiPage clickEdit(String wikiPage)
    {
        selectWikiDetailsRow(wikiPage).findElement(editPage).click();
        return (EditWikiPage) editWikiPage.renderedPage();
    }

    public WikiPage clickPageName(String wikiPageTitle)
    {
        selectWikiDetailsRow(wikiPageTitle).findElement(pageName).click();
        return (WikiPage) wikiPage.renderedPage();
    }

    public boolean clickDeletePage(String wikiPage)
    {
        selectWikiDetailsRow(wikiPage).findElement(deletePage).click();
        return deletePopUp.isDisplayed();
    }

    /**
     * This method returns the details about a specific wiki page in a list of strings. On each position there is
     * 0 - creator of the wiki page
     * 1 - date of creation
     * 2 - last user to modify the wiki page
     * 3 - date of the last modification
     *
     * @param wikiPage The name of the specific wiki page
     * @return
     */
    private List<String> getDetailsOfWikiPage(String wikiPage)
    {
        List<String> stringDetails = new ArrayList<>();
        List<WebElement> webDetails = selectWikiDetailsRow(wikiPage).findElements(wikiRowDetails);
        for (int i = 0; i < webDetails.size(); i++)
        {
            if (webDetails.get(i).getText().equals("Created by:"))
                stringDetails.add(webDetails.get(i + 1).findElement(By.cssSelector("a")).getText());
            if (webDetails.get(i).getText().equals("Created on:"))
                stringDetails.add(webDetails.get(i + 1).getText());
            if (webDetails.get(i).getText().equals("Modified by:"))
                stringDetails.add(webDetails.get(i + 1).findElement(By.cssSelector("a")).getText());
            if (webDetails.get(i).getText().equals("Modified on:"))
                stringDetails.add(webDetails.get(i + 1).getText());
        }
        return stringDetails;
    }

    public String getWikiPageCreator(String wikiPage)
    {
        List<String> details = getDetailsOfWikiPage(wikiPage);
        return details.get(0);
    }

    /**
     * This method returns the date of creation of the wiki page without the seconds
     *
     * @param wikiPage
     * @return
     */
    public String getWikiPageCreationDate(String wikiPage)
    {
        List<String> details = getDetailsOfWikiPage(wikiPage);
        String date = details.get(1);
        int indexofLast = date.lastIndexOf(":");
        date = date.substring(0, indexofLast);
        return date;
    }

    public String getWikiPageModifier(String wikiPage)
    {
        List<String> details = getDetailsOfWikiPage(wikiPage);
        return details.get(2);
    }

    /**
     * This method returns the date of modification of the wiki page without the seconds
     *
     * @param wikiPage
     * @return
     */
    public String getWikiPageModificationDate(String wikiPage)
    {
        List<String> details = getDetailsOfWikiPage(wikiPage);
        String date = details.get(3);
        int indexofLast = date.lastIndexOf(":");
        date = date.substring(0, indexofLast);
        return date;
    }

    public String getWikiPageContent(String wikiPage)
    {
        return selectWikiDetailsRow(wikiPage).findElement(wikiPageContent).getText();
    }

    public List<String> getWikiPageTags(String wikiPage)
    {
        List<String> stringTags = new ArrayList<>();
        List<WebElement> webTags = selectWikiDetailsRow(wikiPage).findElements(wikiPageTags);
        for (WebElement webTag : webTags)
        {
            stringTags.add(webTag.getText());
        }

        return stringTags;
    }

    public String getMissingPageTextColor(String wikiPage)
    {
        String color = selectWikiDetailsRow(wikiPage).findElement(missingWikiPage).getCssValue("color");

        String[] hexValue = color.replace("rgb(", "").replace(")", "").split(",");

        int hexValue1 = Integer.parseInt(hexValue[0]);
        hexValue[1] = hexValue[1].trim();
        int hexValue2 = Integer.parseInt(hexValue[1]);
        hexValue[2] = hexValue[2].trim();
        int hexValue3 = Integer.parseInt(hexValue[2]);

        return String.format("#%02x%02x%02x", hexValue1, hexValue2, hexValue3);
    }

    public WikiDetailsPage clickDetails(String wikiPage)
    {
        selectWikiDetailsRow(wikiPage).findElement(pageDetails).click();
        return (WikiDetailsPage) wikiDetailsPage.renderedPage();
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/wiki", getCurrentSiteName());
    }

    /**
     * Method veryfies if the actions 'Details' ,'Edit' and 'Delete' are available for a wiki page
     *
     * @param wikiPage
     * @return 'true' if all actions are available
     */
    public boolean areActionsAvailableForPage(String wikiPage)
    {

        return selectWikiDetailsRow(wikiPage).findElement(editPage).isDisplayed() &&
            selectWikiDetailsRow(wikiPage).findElement(deletePage).isDisplayed() &&
            selectWikiDetailsRow(wikiPage).findElement(pageDetails).isDisplayed();
    }

    public void clickMainPageButton()
    {
        mainPageLink.click();
    }

    /**
     * This method clicks on a specific tag filter
     *
     * @param tagName
     */
    public void clickSpecificTag(String tagName)
    {
        getBrowser().findElement(By.cssSelector("a[rel='" + tagName + "']")).click();
    }

    /**
     * This method verifies if a specific wiki page is displayed
     *
     * @param wikiPageName
     * @return
     */
    public boolean isWikiPageDisplayed(String wikiPageName)
    {
        return getBrowser().isElementDisplayed(getBrowser().findElement(By.xpath("//a[text()='" + wikiPageName + "']")));
    }

    public void clickShowAllTags()
    {
        showAllTagsFilter.click();
    }

    public void clickAllPagesFilter()
    {
        allPagesFilter.click();
    }

    public void clickMyPagesFilter()
    {
        myPagesFilter.click();
    }
}
