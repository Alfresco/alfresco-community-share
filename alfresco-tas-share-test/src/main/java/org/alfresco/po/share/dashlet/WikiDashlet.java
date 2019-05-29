package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class WikiDashlet extends Dashlet<WikiDashlet>
{
    @RenderWebElement
    @FindBy (css = "div.dashlet.wiki")
    private WebElement dashletContainer;

    private By configureDashlet = By.cssSelector("div.dashlet.wiki [class$='edit']");

    @FindBy (css = "div.dashlet.wiki [class$='rich-content dashlet-padding']")
    private WebElement defaultDashletMessage;

    private By wikiDashletTitle = By.cssSelector("div.dashlet.wiki [class$='title']");

    private By wikiDashletText = By.cssSelector("div.dashlet.wiki [class$='body scrollablePanel']");

    @Override
    protected String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    /**
     * Method to verify that configure this dashlet icon is displayed
     *
     * @return true if Configure Dashlet icon is displayed on the Wiki dashlet.
     */

    public boolean isConfigureDashletIconDisplayed()
    {
        browser.mouseOver(browser.findElement(configureDashlet));
        return browser.waitUntilElementVisible(configureDashlet).isDisplayed();
    }

    /**
     * Method to get the text displayed on Wiki dashlet
     */

    public String getWikiMessage()
    {
        return defaultDashletMessage.getText();
    }

    /**
     * Method to click the Configure dashlet (Edit) icon on Wiki dashlet
     */

    public void clickOnConfigureDashletIcon()
    {
        browser.findElement(configureDashlet).click();
    }

    /**
     * Method to get the Wiki dashlet title.
     *
     * @return the Wiki Dashlet title displayed
     */
    public String getWikiDashletTitle()

    {

        return browser.findElement(wikiDashletTitle).getText();
    }

    /**
     * Method to get the text displayed on the Wiki Dashlet content.
     *
     * @return the text displayed on the Wiki dashlet on Site Dashboard.
     */
    public String getWikiDashletContentText()
    {

        return browser.findElement(wikiDashletText).getText();
    }

}
