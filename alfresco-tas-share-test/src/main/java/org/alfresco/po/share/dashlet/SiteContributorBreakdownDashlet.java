package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.user.profile.UserProfilePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.alfresco.common.Wait.*;
import static org.testng.Assert.assertEquals;

public class SiteContributorBreakdownDashlet extends Dashlet<SiteContributorBreakdownDashlet>
{
    private final By dashletContainer = By.id("DASHLET");
    private final By pieChartSlices = By.cssSelector("div[class='alfresco-charts-ccc-Chart'] path[transform]");
    private final By dashletTitle = By.cssSelector("div.alfresco-dashlets-Dashlet__title");
    private final By sliceTooltip = By.cssSelector("div[id^='tipsyPvBehavior']");
    private final By optionsListLocator = By.cssSelector("div[id$='_CONTROL_dropdown'] tr[id^='dijit_MenuItem'] td[id$='_text']");
    private final By dropDownLocator = By.cssSelector("td[data-dojo-attach-point='titleNode']");
    private final By dashletEmptyMessageLocator = By.cssSelector("#DASHLET svg text");
    private final By userLocator = By.cssSelector("div[class='alfresco-charts-ccc-Chart'] text");
    private final By pieChartLocator = By.cssSelector("div[class='alfresco-charts-ccc-Chart'] path[transform]");

    public SiteContributorBreakdownDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public SiteContributorBreakdownDashlet assertPieChartSizeEquals(int expectedPieChartSize)
    {
        LOG.info("Assert pie chart size equals: {}", expectedPieChartSize);
        webElementInteraction.waitUntilElementIsDisplayedWithRetry(pieChartLocator, WAIT_2.getValue(), WAIT_40.getValue());
        assertEquals(webElementInteraction.waitUntilElementsAreVisible(pieChartSlices).size(), expectedPieChartSize);
        return this;
    }

    public Map<String, String> getPieChartSliceTooltip()
    {
        HashMap<String, String> slicesTooltip = new HashMap<>();
        for (WebElement slice : webElementInteraction.waitUntilElementsAreVisible(pieChartSlices))
        {
            webElementInteraction.mouseOver(slice);
            String tooltip = webElementInteraction.findElement(sliceTooltip).getAttribute("original-title");
            slicesTooltip.put(tooltip.substring(tooltip.indexOf("<strong>") + 8,
                tooltip.indexOf("</strong>")), tooltip.substring(tooltip.indexOf("<br/>"), tooltip.indexOf("</div>")));
        }
        return slicesTooltip;
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.getElementText(webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    private List<String> addOptionsToList()
    {
        List<String> optionsList = new ArrayList<>();
        for (WebElement option : webElementInteraction.waitUntilElementsAreVisible(optionsListLocator))
        {
            LOG.info("Add option to list: {}", option.getText());
            optionsList.add(option.getText());
        }
        return optionsList;
    }

    public UserProfilePage clickPieChartUsername()
    {
        LOG.info("Click pie chart username");
        webElementInteraction.scrollIntoView(webElementInteraction.waitUntilElementIsVisible(userLocator));
        webElementInteraction.clickElement(userLocator);

        return new UserProfilePage(webDriver);
    }

    public SiteContributorBreakdownDashlet assertDropdownFilterEquals(List<String> expectedDropdownFilter)
    {
        LOG.info("Assert dropdown filter equals: {}", expectedDropdownFilter);
        List<String> actualDropDownOptions = addOptionsToList();
        assertEquals(actualDropDownOptions, expectedDropdownFilter,
            String.format("Dropdown filter not equals %s", expectedDropdownFilter));

        return this;
    }

    public SiteContributorBreakdownDashlet assertDashletEmptyMessageEquals(String expectedEmptyMessage)
    {
        LOG.info("Assert dashlet empty message equals: {}", expectedEmptyMessage);
        webElementInteraction.waitUntilElementIsPresent(dashletEmptyMessageLocator);
        assertEquals(webElementInteraction.getElementText(dashletEmptyMessageLocator), expectedEmptyMessage);
        return this;
    }

    public SiteContributorBreakdownDashlet openFilterDropDown()
    {
        LOG.info("Open filters dropdown");
        webElementInteraction.clickElement(dropDownLocator);
        return this;
    }
}
