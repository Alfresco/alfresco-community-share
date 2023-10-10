package org.alfresco.po.share.site.wiki;

import java.util.List;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

public class WikiPage extends SiteCommon<WikiPage>
{
    @FindBy (css = "div.title-bar [id$=default-viewButtons]")
    private WebElement wikiPageTitle;

    @FindBy (css = "div.yui-content [id$=default-page]")
    private WebElement wikiPageContent;

    @FindBy (css = "span.forwardLink:nth-of-type(1) a")
    private WebElement wikiPageListLink;

    @FindAll (@FindBy (css = "[id$=default-page] a"))
    private List<WebElement> documentsLinkList;

    @FindBy (css = "[id$=default-delete-button-button]")
    private WebElement deleteWikiPage;

    @FindBy (css = "[id=prompt]")
    private WebElement deletePopUp;

    @FindBy (css = "a[href*='details']")
    private WebElement wikiPageDetailsLink;

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
        return wikiPageTitle.getText();
    }

    /**
     * Click on wiki page list link
     *
     * @return wiki page list page
     */

    public WikiListPage clickOnWikiListLink()
    {

        waitUntilWebElementIsDisplayedWithRetry(wikiPageListLink, 3);
        wikiPageListLink.click();
        return new WikiListPage(webDriver);
    }

    public WikiDocumentDetailsPage clickOnDocLink(String docName)
    {
        for (WebElement docLink : documentsLinkList)
        {
            if (docLink.getText().endsWith(docName))
                docLink.click();
        }
        return new WikiDocumentDetailsPage(webDriver);
    }

    public boolean deleteWikiPage()
    {
        deleteWikiPage.click();
        return deletePopUp.isDisplayed();
    }

    public String getWikiPageContent()
    {
        return wikiPageContent.getText().trim();
    }

    public WikiDetailsPage clickOnDetailsLink()
    {
        wikiPageDetailsLink.click();
        return new WikiDetailsPage(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/wiki-page?title=%s", getCurrentSiteName(), getWikiPageTitle());
    }

}
