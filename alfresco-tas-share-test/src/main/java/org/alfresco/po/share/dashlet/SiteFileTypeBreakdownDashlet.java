package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import java.util.List;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Primary;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

/**
 * Created by Claudia Agache on 7/25/2016.
 */
@PageObject
@Primary
public class SiteFileTypeBreakdownDashlet extends Dashlet<SiteFileTypeBreakdownDashlet>
{
    private static final String ORIGINAL_TITLE_ATTRIBUTE = "original-title";
    private static final String REMOVE_HTML_TAGS = "<[^>]*>";
    private static final String EMPTY = "";

    @RenderWebElement
    @FindBy (id = "DASHLET")
    private HtmlElement dashletContainer;

    @FindBy (css = "#DASHLET svg text")
    private HtmlElement dashletEmptyMessage;

    @FindAll (@FindBy (css = "div[id^='alfresco_charts_ccc_PieChart'] path[transform]"))
    private List<WebElement> pieChartSlices;

    @FindBy (css = "div[id^='tipsyPvBehavior']")
    private WebElement sliceTooltip;

    private static final String fileTypeNameLocator = "//*[text()='%s']";
    private static final String pieChartSlice = "path[transform]:nth-child";

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(By.cssSelector("div.alfresco-dashlets-Dashlet__title")).getText();
    }

    public SiteFileTypeBreakdownDashlet assertDashletEmptyMessageEquals(String expectedEmptyMessage)
    {
        LOG.info("Assert dashlet empty message equals: {}", expectedEmptyMessage);
        assertEquals(dashletEmptyMessage.getText(), expectedEmptyMessage,
            String.format("Empty message not equals %s ", expectedEmptyMessage));

        return this;
    }

    public SiteFileTypeBreakdownDashlet assertPieChartSizeEquals(String fileName, int expectedPieChartSize)
    {
        LOG.info("Assert pie chart size equals: {}", expectedPieChartSize);
        browser.waitWithRetryAndReturnWebElement(
            By.xpath(String.format(fileTypeNameLocator, fileName)), WAIT_1, RETRY_TIMES);

        assertEquals(pieChartSlices.size(), expectedPieChartSize,
            String.format("Pie chart size not equals %d ", expectedPieChartSize));
        return this;
    }

    public SiteFileTypeBreakdownDashlet assertPieChartTooltipTextEquals(String expectedTooltipText,String pieChartIndex)
    {
        LOG.info("Assert pie chart tooltip text equals: {}", expectedTooltipText);
        String formattedCssSelector = pieChartSlice.concat("(").concat(pieChartIndex).concat(")");
        browser.scrollToElement(browser.findElement(By.cssSelector(formattedCssSelector)));
        browser.mouseOverViaJavascript(browser.findElement(By.cssSelector(formattedCssSelector)));

        assertEquals(getAttributeValueWithoutHtmlTags(), expectedTooltipText,
            String.format("Pie chart tooltip text not equals %s ", expectedTooltipText));
        return this;
    }

    private String getAttributeValueWithoutHtmlTags()
    {
        LOG.info("Get original title attribute without html tags");
        return sliceTooltip.getAttribute(ORIGINAL_TITLE_ATTRIBUTE).replaceAll(REMOVE_HTML_TAGS, EMPTY);
    }

    public SiteFileTypeBreakdownDashlet assertPieChartFileTypeNameEquals(String expectedFileTypeName)
    {
        LOG.info("Assert pie chart file type name equals: {}", expectedFileTypeName);
        assertEquals(browser.findElement(By.xpath(String.format(fileTypeNameLocator, expectedFileTypeName))).getText(),
            expectedFileTypeName, String.format("Pie chart file type name not equals %s", expectedFileTypeName));

        return this;
    }
}
