package org.alfresco.po.share.site.wiki;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WikiListPage extends SiteCommon<WikiListPage>
{
    private final By myPagesFilter = By.cssSelector("span[class='myPages'] a");
    private final By allPagesFilter = By.cssSelector("span[class='all'] a");
    private final By tagsList = By.cssSelector("[id$=default-ul] li:not(:first-child)");
    private final By showAllTagsFilter = By.xpath("//a[text()='Show All Items']");
    private final By noWikiPage = By.cssSelector("[id$=default-pagelist] div");
    private final By editPage = By.cssSelector("div.editPage a");
    private final By pageName = By.cssSelector("[class=pageTitle] a");
    private final By deletePage = By.cssSelector(".deletePage a");
    private final By missingWikiPage = By.cssSelector(".rich-content a");
    private final By pageDetails = By.cssSelector("div.detailsPage a");
    private final By wikiRowDetails = By.cssSelector("div[class='publishedDetails'] span");
    private final By wikiPageContent = By.cssSelector("div[class='pageCopy rich-content']");
    private final By wikiPageTags = By.cssSelector("div[class=pageTags] a");
    private final By wikiPagesTitleList = By.cssSelector("div.pageTitle a");
    private final By wikiPagesList = By.cssSelector("div.wikipage");
    private final By mainPageLink = By.cssSelector("span.forwardLink>a");
    private final By deletePopUp = By.cssSelector("[id=prompt]");
    private final By renameInput = By.cssSelector("[id$=default-renameTo]");
    private final By savePageMainNameButton = By.cssSelector("[id$=default-rename-save-button-button]");
    private final By closePopup = By.cssSelector("[class=container-close]");


    public WikiListPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    /**
     * This method returns the list of wiki page titles
     *
     * @return list of wiki page titles
     */

    public List<String> getWikiPageTitlesList()
    {
        List<String> wikiPageTitles = new ArrayList<>();
        for (WebElement wikiPageTitle : findElements(wikiPagesTitleList))
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
        waitUntilElementIsDisplayedWithRetry(By.cssSelector(".datatable-msg-empty"));
        return findElement(noWikiPage).getText();
    }

    /**
     * This method is used to get the list of tags
     * for each wiki page
     *
     * @return
     */
    public List<String> getTagsList()
    {
        waitInSeconds(2);
        List<String> tags = new ArrayList<>();
        for (WebElement tag : findElements(tagsList))
        {
            tags.add(tag.getText());
        }
        return tags;
    }

    public WebElement selectWikiDetailsRow(String wikiPage)
    {
        return findFirstElementWithValue(findElements(wikiPagesList), wikiPage);
    }

    public EditWikiPage clickEdit(String wikiPage)
    {
        selectWikiDetailsRow(wikiPage).findElement(editPage).click();
        return new EditWikiPage(webDriver);
    }

    public WikiPage clickPageName(String wikiPageTitle)
    {
        selectWikiDetailsRow(wikiPageTitle).findElement(pageName).click();
        return new WikiPage(webDriver);
    }

    public boolean clickDeletePage(String wikiPage)
    {
        selectWikiDetailsRow(wikiPage).findElement(deletePage).click();
        return findElement(deletePopUp).isDisplayed();
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
        return new WikiDetailsPage(webDriver);
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
        findElement(mainPageLink).click();
    }

    /**
     * This method clicks on a specific tag filter
     *
     * @param tagName
     */
    public void clickSpecificTag(String tagName)
    {
        findElement(By.cssSelector("a[rel='" + tagName + "']")).click();
    }

    /**
     * This method verifies if a specific wiki page is displayed
     *
     * @param wikiPageName
     * @return
     */
    public boolean isWikiPageDisplayed(String wikiPageName)
    {
        return isElementDisplayed(
            findElement(By.xpath("//a[text()='" + wikiPageName + "']")));
    }

    public void clickShowAllTags()
    {
        clickElement(showAllTagsFilter);
    }

    public void clickAllPagesFilter()
    {
        clickElement(allPagesFilter);
    }

    public void clickMyPagesFilter()
    {
       clickElement(myPagesFilter);
    }

    public String noWikiPageDisplay()
    {
        waitUntilElementIsDisplayedWithRetry(By.cssSelector(".datatable-msg-empty"));
        return findElement(noWikiPage).getText();
    }
    public void clearWikiTitle()
    {
        findElement(renameInput).clear();
    }

    public void typeNewMainPageName(String newName)
    {
        findElement(renameInput).sendKeys(newName);
    }

    public void closeRenamePopup()
    {
        waitInSeconds(3);
        findElement(closePopup).click();
    }

    public void clickOnSaveButton()
    {
        findElement(savePageMainNameButton).click();
        waitInSeconds(3);
    }
}
