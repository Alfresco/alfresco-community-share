package org.alfresco.po.share.site.wiki;

import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WikiPage extends SiteCommon<WikiPage>
{
    private final By wikiPageTitle1 = By.cssSelector("div.title-bar [id$=default-viewButtons]");
    private final By wikiPageTitle = By.cssSelector("div.title-bar [id$=default-viewButtons]");
    private final By wikiPageContent = By.cssSelector("div.yui-content [id$=default-page]");
    private final By wikiPageListLink = By.cssSelector("span.forwardLink:nth-of-type(1) a");
    private final By documentsLinkList = By.cssSelector("[id$=default-page] a");
    private final By deleteWikiPage = By.cssSelector("[id$=default-delete-button-button]");
    private final By deletePopUp = By.cssSelector("[id=prompt]");
    private final By wikiPageDetailsLink = By.cssSelector("a[href*='details']");

    public WikiPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    /**
     * Method used to get wiki page title
     *
     * @return wiki page title
     */

    public String getWikiPageTitle()
    {
        return findElement(wikiPageTitle).getText();
    }

    /**
     * Click on wiki page list link
     *
     * @return wiki page list page
     */

    public WikiListPage clickOnWikiListLink()
    {

        waitUntilWebElementIsDisplayedWithRetry(findElement(wikiPageListLink), 3);
        findElement(wikiPageListLink).click();
        return new WikiListPage(webDriver);
    }

    public WikiDocumentDetailsPage clickOnDocLink(String docName)
    {
        waitInSeconds(2);
        for (WebElement docLink : findElements(documentsLinkList))
        {
            if (docLink.getText().endsWith(docName))
                docLink.click();
        }
        return new WikiDocumentDetailsPage(webDriver);
    }

    public boolean deleteWikiPage()
    {
        findElement(deleteWikiPage).click();
        return findElement(deletePopUp).isDisplayed();
    }
    public WikiPage getWikiCurrentPageTitle()
    {
        findElement(wikiPageTitle1);
        return this;

    }

    public String getWikiPageContent()
    {
        return findElement(wikiPageContent).getText().trim();
    }

    public WikiDetailsPage clickOnDetailsLink()
    {
        findElement(wikiPageDetailsLink).click();
        return new WikiDetailsPage(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/wiki-page?title=%s", getCurrentSiteName(), getWikiPageTitle());
    }

}
