package org.alfresco.po.share.site.wiki;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageObject
public class WikiPage extends SiteCommon<WikiPage>
{
    @Autowired
    WikiListPage wikiListPage;

    @Autowired
    WikiDocumentDetailsPage docDetailsPage;

    @Autowired
    WikiDetailsPage wikiDetailsPage;

    @RenderWebElement
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

        browser.waitUntilWebElementIsDisplayedWithRetry(wikiPageListLink, 3);
        wikiPageListLink.click();
        return (WikiListPage) wikiListPage.renderedPage();
    }

    public WikiDocumentDetailsPage clickOnDocLink(String docName)
    {
        for (WebElement docLink : documentsLinkList)
        {
            if (docLink.getText().endsWith(docName))
                docLink.click();
        }
        return (WikiDocumentDetailsPage) docDetailsPage.renderedPage();
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
        return (WikiDetailsPage) wikiDetailsPage.renderedPage();
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/wiki-page?title=%s", getCurrentSiteName(), getWikiPageTitle());
    }

}
