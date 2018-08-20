package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Primary;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Claudia Agache on 7/25/2016.
 */
@PageObject
@Primary
public class SiteFileTypeBreakdownDashlet extends Dashlet<SiteFileTypeBreakdownDashlet>
{
    @RenderWebElement
    @FindBy(id = "DASHLET")
    private HtmlElement dashletContainer;

    @FindBy(css = "#DASHLET svg text")
    private HtmlElement dashletMessage;

    @FindAll(@FindBy(css= "div[id^='alfresco_charts_ccc_PieChart'] path[transform]"))
    private List<WebElement> pieChartSlices;

    @FindBy(css = "div[id^='tipsyPvBehavior']")
    private WebElement sliceTooltip;

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(By.cssSelector("div.alfresco-dashlets-Dashlet__title")).getText();
    }

    /**
     * Get Site File Type Breakdown dashlet message when no files are available in sites library
     *
     * @return
     */
    public String getDashletMessage()
    {
        return dashletMessage.getText();
    }

    public int getNumberOfPieChartSlices()
    {
        browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector("div[id^='alfresco_charts_ccc_PieChart'] path[transform]"));
        getBrowser().waitInSeconds(5);
        return pieChartSlices.size();
    }

    public Map<String, String> getPieChartSliceTooltip()
    {
        Map<String, String> slicesTooltip = new HashMap<>();
        for(WebElement slice : pieChartSlices){
            browser.mouseOver(slice);
            String tooltip = sliceTooltip.getAttribute("original-title");
            slicesTooltip.put(tooltip.substring(tooltip.indexOf("<strong>")+8, tooltip.indexOf("</strong>")), tooltip.substring(tooltip.indexOf("<br/>"), tooltip.indexOf("</div>")));
        }
        return slicesTooltip;
    }
}
