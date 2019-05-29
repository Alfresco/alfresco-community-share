package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import java.util.List;

@PageObject
public class SavedSearchDashlet extends Dashlet<SavedSearchDashlet>
{
    @RenderWebElement
    @FindBy (css = "div.dashlet.savedsearch")
    protected HtmlElement dashletContainer;

    @FindBy (css = "div.dashlet.savedsearch td div[class$='yui-dt-liner']")
    protected static HtmlElement defaultDashletMessage;

    @FindBy (css = "div.dashlet.savedsearch div[class$='titleBarActionIcon edit']")
    protected static List<WebElement> configureDashletIcon;

    @FindBy (css = "div.dashlet.savedsearch div[class$='titleBarActions']")
    protected static WebElement titleBar;

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    /**
     * Retrieves the default dashlet message.
     *
     * @return String
     */
    public String getDefaultMessage()
    {
        return defaultDashletMessage.getText();
    }

    /**
     * Returns if configure dashlet icon is displayed on this dashlet.
     *
     * @return True if the configure dashlet icon displayed else false.
     */
    public boolean isConfigureDashletIconDisplayed()
    {
        browser.mouseOver(titleBar);
        return configureDashletIcon.size() > 0;
    }

    /**
     * Click on configure dashlet icon.
     */
    public void clickOnConfigureDashletIcon()
    {
        configureDashletIcon.get(0).click();
    }
}
