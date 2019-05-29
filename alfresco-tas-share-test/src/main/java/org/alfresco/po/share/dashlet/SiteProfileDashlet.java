package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.Link;

import java.util.List;

@PageObject
public class SiteProfileDashlet extends Dashlet<SiteProfileDashlet>
{
    @Autowired
    UserProfilePage userProfilePage;

    @RenderWebElement
    @FindBy (css = "div.dashlet.site-profile")
    protected HtmlElement dashletContainer;

    @RenderWebElement
    @FindBy (css = ".msg.dashlet-padding>h2")
    private WebElement welcomeMessage;

    @RenderWebElement
    @FindBy (xpath = "//span[normalize-space(.) = 'Site Manager(s):']")
    private WebElement siteManagersLabel;

    @RenderWebElement
    @FindBy (xpath = "//span[normalize-space(.) = 'Visibility:']")
    private WebElement visibilityLabel;

    @RenderWebElement
    @FindBy (xpath = "//div[normalize-space(.) = 'This dashlet displays the site details. Only the site manager can change this information.']")
    private WebElement helpText;

    @RenderWebElement
    @FindAll (@FindBy (css = "p"))
    protected List<WebElement> sitesProfileLabels;

    @RenderWebElement
    @FindAll (@FindBy (css = "p a"))
    protected List<WebElement> siteManagersList;

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    /**
     * Get the "Welcome" message from "Site Profile" dashlet
     *
     * @return String message
     */
    public String getWelcomeMessageText()
    {
        return welcomeMessage.getText();
    }

    /**
     * Get list of labels displayed in Site Profile dashlet
     */

    public List<WebElement> getSiteProfileLabels()
    {
        return sitesProfileLabels;
    }

    /**
     * Get list of users links displayed in Site Profile dashlet
     */

    public List<WebElement> getSiteManagersList()
    {
        return siteManagersList;
    }

    /**
     * Retrieves the label that match the site description.
     *
     * @param description
     * @return {@link Link} that matches description
     */

    public WebElement getSiteDescription(final String description)
    {
        return browser.findFirstElementWithValue(sitesProfileLabels, description);
    }

    /**
     * Verify if a site description is displayed in User Profile --> Sites
     *
     * @param description
     * @return True if description exists
     */

    public boolean isSiteDescriptionPresent(String description)
    {
        return browser.isElementDisplayed(getSiteDescription(description));
    }

    /**
     * Retrieves the link that match the user's name.
     *
     * @param name identifier
     * @return {@link Link} that matches userName
     */

    public WebElement getSiteMember(final String siteManager)
    {
        return browser.findFirstElementWithValue(siteManagersList, siteManager);
    }

    /**
     * Verify if a user name is displayed in Site Profile dashlet
     *
     * @param siteMAnager
     * @return True if user exists
     */

    public boolean isMemberPresent(String siteManager)
    {
        return browser.isElementDisplayed(getSiteMember(siteManager));
    }

    /**
     * Verify if site "Site Managers" label is displayed in Site Profile dashlet
     *
     * @return True if "Site Managers" label exists
     */

    public boolean isSiteManagersLabelDisplayed()
    {
        return browser.isElementDisplayed(siteManagersLabel);
    }

    /**
     * Verify if site visibility label is displayed in Site Profile dashlet
     *
     * @return True if site visibility exists
     */

    public boolean isVisibilityLabelDisplayed()
    {
        return browser.isElementDisplayed(visibilityLabel);
    }

    /**
     * Retrieves the label that match the site name.
     *
     * @param siteVisibility @return label that matches site's visibility
     */

    public WebElement getSiteVisibility(final String siteVisibility)
    {
        return browser.findFirstElementWithValue(sitesProfileLabels, siteVisibility);
    }

    /**
     * Verify if site visibility is displayed in Site Profile dashlet
     *
     * @param siteVisibility
     * @return True if Site exists
     */
    public boolean isSiteVisibilityPresent(String siteVisibility)
    {
        return browser.isElementDisplayed(getSiteVisibility(siteVisibility));
    }

    /**
     * Open user profile page
     *
     * @param managerName
     * @return
     */
    public UserProfilePage clickSiteManager(final String managerName)
    {
        getSiteMember(managerName).click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }

}
