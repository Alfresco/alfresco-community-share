package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.po.share.site.link.CreateLinkPage;
import org.alfresco.po.share.site.link.LinkDetailsViewPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.Link;

/**
 * Created by Claudia Agache on 7/22/2016.
 */
@PageObject
public class SiteLinksDashlet extends Dashlet<SiteLinksDashlet>
{
    //@Autowired
    CreateLinkPage createLinkPage;

    //@Autowired
    LinkDetailsViewPage linkDetailsViewPage;

    @RenderWebElement
    @FindBy (css = "div.dashlet.site-links")
    private HtmlElement dashletContainer;

    @FindBy (css = "a[href='links-linkedit']")
    private Link createLink;

    @FindAll (@FindBy (css = "div.dashlet.site-links .link>a"))
    private List<WebElement> siteLinksList;

    @FindBy (css = "div.dashlet.site-links .detail-list-item span")
    private HtmlElement emptyDashletMessage;

    private By linkDetails = By.xpath("../following-sibling::*[@class = 'actions']/a");

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    /**
     * Get the message when no links are displayed in Site Links Dashlet
     *
     * @return
     */
    public String getDashletMessage()
    {
        return emptyDashletMessage.getText();
    }

    private WebElement findLink(String link)
    {
        return browser.findFirstElementWithValue(siteLinksList, link);
    }

    /**
     * Check if the specific link is displayed in Site Links Dashlet
     *
     * @param link
     * @return true if it is displayed in Site Link Dashlet
     */
    public boolean isLinkPresentInList(String link)
    {
        return findLink(link) != null;
    }

    public boolean hasLinkDetailsButton(String link)
    {
        return findLink(link).findElement(linkDetails) != null;
    }

    /**
     * Click on the link's details button for the specified link
     *
     * @param link
     * @return LinkDetailsViewPage
     */
    public LinkDetailsViewPage clickLinkDetailsButton(String link)
    {
        findLink(link).findElement(linkDetails).click();
        return (LinkDetailsViewPage) linkDetailsViewPage.renderedPage();
    }

    /**
     * Click on the Create Link link
     *
     * @return CreateLinkPage
     */
    public CreateLinkPage clickCreateLink()
    {
        createLink.click();
        return (CreateLinkPage) createLinkPage.renderedPage();
    }

    /**
     * Check if Create Link link is displayed or not
     *
     * @return true if it is displayed
     */
    public boolean isCreateLinkDisplayed()
    {
        return createLink.isDisplayed();
    }

    public int getNumberOfLinks()
    {
        try
        {
            List<WebElement> linksList = browser.findElements(By.cssSelector("div.dashlet.site-links>div.scrollableList div[class='link']>a"));
            return linksList.size();
        } catch (StaleElementReferenceException ex)
        {
            LOG.info(ex.getStackTrace().toString());
        }
        return getNumberOfLinks();
    }
}
