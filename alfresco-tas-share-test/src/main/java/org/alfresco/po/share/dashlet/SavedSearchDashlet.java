package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.Table;

@PageObject
public class SavedSearchDashlet extends Dashlet<SavedSearchDashlet>
{
    @FindBy (css = "div.dashlet.savedsearch td div[class$='yui-dt-liner']")
    protected static HtmlElement defaultDashletMessage;
    @FindBy (css = "div.dashlet.savedsearch div[class$='titleBarActionIcon edit']")
    protected static List<WebElement> configureDashletIcon;
    @FindBy (css = "div.dashlet.savedsearch div[class$='titleBarActions']")
    protected static WebElement titleBar;
    @RenderWebElement
    @FindBy (css = "div.dashlet.savedsearch")
    protected HtmlElement dashletContainer;
    @FindBy (css = "div[id$='_default-search-results'] tbody div ")
    private WebElement resultsText;

    @FindBy (css = "div[id$='_default-search-results'] table")
    private Table searchResults;

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

    public String getResultsText()
    {
        return resultsText.getText();
    }

    public boolean isSearchResultItemDisplayed(String expectedItem)
    {
        String actualItems = searchResults.getRowsAsString().toString();
        try
        {
            int retryCount = 0;
            if (!actualItems.contains(expectedItem) && retryCount < 3)
            {
                browser.refresh();
                retryCount++;
                return actualItems.contains(expectedItem);
                //String actualItems = searchResults.getRowsAsString().toString();
            }
        } catch (Exception ex)
        {
            LOG.info(ex.getStackTrace().toString());
        }
        return actualItems.contains(expectedItem);
    }
}
