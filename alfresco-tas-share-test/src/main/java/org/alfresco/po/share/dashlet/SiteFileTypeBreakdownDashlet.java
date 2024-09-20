package org.alfresco.po.share.dashlet;

import static org.alfresco.common.RetryTime.RETRY_TIME_40;
import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.*;
import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SiteFileTypeBreakdownDashlet extends Dashlet<SiteFileTypeBreakdownDashlet>
{
    private final By dashletContainer = By.id("DASHLET");
    private final By dashletEmptyMessage = By.cssSelector("#DASHLET svg text");
    private final By pieChartSlices = By.cssSelector("div[id^='alfresco_charts_ccc_PieChart'] path[transform]");
    private final By sliceTooltip = By.cssSelector("div[id^='tipsyPvBehavior']");
    private final By title = By.cssSelector("div.alfresco-dashlets-Dashlet__title");

    private final String fileTypeNameLocator = "//*[text()='%s']";
    private final String pieChartSlice = "path[transform]:nth-child";
    private final String ORIGINAL_TITLE_ATTRIBUTE = "original-title";
    private final String REMOVE_HTML_TAGS = "<[^>]*>";
    private final String EMPTY = "";

    public SiteFileTypeBreakdownDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return getElementText(waitUntilElementIsVisible(dashletContainer)
            .findElement(title));
    }

    public SiteFileTypeBreakdownDashlet assertDashletEmptyMessageEquals(String expectedEmptyMessage)
    {
        log.info("Assert dashlet empty message equals: {}", expectedEmptyMessage);
        assertEquals(getElementText(dashletEmptyMessage), expectedEmptyMessage,
            String.format("Empty message not equals %s ", expectedEmptyMessage));

        return this;
    }

    public SiteFileTypeBreakdownDashlet assertPieChartSizeEquals(int expectedPieChartSize)
    {
        log.info("Assert pie chart size equals: {}", expectedPieChartSize);
        waitWithRetryAndReturnWebElement(pieChartSlices, WAIT_2.getValue(),
            RETRY_TIME_80.getValue());

        assertEquals(waitUntilElementsAreVisible(pieChartSlices).size(), expectedPieChartSize,
            String.format("Pie chart size not equals %d ", expectedPieChartSize));
        return this;
    }
    public SiteFileTypeBreakdownDashlet assert_PieChartSizeEquals(int expectedPieChartSize)
    {
        log.info("Assert pie chart size equals: {}", expectedPieChartSize);
        waitWithRetryAndReturnWebElement(pieChartSlices, WAIT_10.getValue(),
            RETRY_TIME_40.getValue());

        assertEquals(waitUntilElementsAreVisible(pieChartSlices).size(), expectedPieChartSize,
            String.format("Pie chart size not equals %d ", expectedPieChartSize));
        return this;
    }

    public SiteFileTypeBreakdownDashlet assertPieChartTooltipTextEquals(String expectedTooltipText,String pieChartIndex)
    {
        log.info("Assert pie chart tooltip text equals: {}", expectedTooltipText);
        String formattedCssSelector = pieChartSlice.concat("(").concat(pieChartIndex).concat(")");
        scrollToElement(findElement(By.cssSelector(formattedCssSelector)));
        mouseOverViaJavascript(findElement(By.cssSelector(formattedCssSelector)));

        assertEquals(getAttributeValueWithoutHtmlTags(), expectedTooltipText,
            String.format("Pie chart tooltip text not equals %s ", expectedTooltipText));
        return this;
    }

    private String getAttributeValueWithoutHtmlTags()
    {
        log.info("Get original title attribute without html tags");
        return waitUntilElementIsVisible(sliceTooltip)
            .getAttribute(ORIGINAL_TITLE_ATTRIBUTE).replaceAll(REMOVE_HTML_TAGS, EMPTY);
    }

    public SiteFileTypeBreakdownDashlet assertPieChartFileTypeNameEquals(String expectedFileTypeName)
    {
        waitWithRetryAndReturnWebElement(pieChartSlices, WAIT_10.getValue(), RETRY_TIME_80.getValue());

        log.info("Assert pie chart file type name equals: {}", expectedFileTypeName);
        assertEquals(findElement(By.xpath(String.format(fileTypeNameLocator, expectedFileTypeName))).getText(),
            expectedFileTypeName, String.format("Pie chart file type name not equals %s", expectedFileTypeName));

        return this;
    }
    public Map<String, String> getPieChartSliceTooltip()
    {
        HashMap<String, String> slicesTooltip = new HashMap<>();
        for (WebElement slice : waitUntilElementsAreVisible(pieChartSlices))
        {
            mouseOver(slice);
            String tooltip = findElement(sliceTooltip).getAttribute("original-title");
            slicesTooltip.put(tooltip.substring(tooltip.indexOf("<strong>") + 8,
                tooltip.indexOf("</strong>")), tooltip.substring(tooltip.indexOf("<br/>"), tooltip.indexOf("</div>")));
        }
        return slicesTooltip;
    }
}
