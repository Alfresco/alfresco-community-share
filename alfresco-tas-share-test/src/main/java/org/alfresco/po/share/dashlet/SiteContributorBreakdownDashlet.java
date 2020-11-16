package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Mirela Tifui on 2/24/2017.
 */
@PageObject
public class SiteContributorBreakdownDashlet extends Dashlet<SiteContributorBreakdownDashlet>
{
    public By tooltipMessage = By.cssSelector("div[id^='tipsyPvBehavior']");
    //@Autowired
    UserProfilePage userProfilePage;

    @RenderWebElement
    @FindBy (id = "DASHLET")
    private WebElement dashletContainer;

    @FindAll (@FindBy (css = "div[class='alfresco-charts-ccc-Chart'] path[transform]"))
    private List<WebElement> pieChartSlices;

    @FindBy (css = "div.alfresco-dashlets-Dashlet__title")
    private WebElement dashletTitle;

    @FindBy (css = "div[id^='tipsyPvBehavior']")
    private WebElement sliceTooltip;

    @FindBy (css = "div[class$='alfresco-reports-TopSiteContributorReport'] div.control table")
    private WebElement periodFilter;

    @FindBy (css = "div[id$='_CONTROL_dropdown'] tr[id^='dijit_MenuItem'] td[id$='_text']")
    private List<WebElement> optionsListLocator;

    private final By dropDownLocator = By.cssSelector("td[data-dojo-attach-point='titleNode']");
    private final By dashletEmptyMessageLocator = By.cssSelector("#DASHLET svg text");
    private final By userLocator = By.cssSelector("div[class='alfresco-charts-ccc-Chart'] text");
    private final By pieChartLocator = By.cssSelector("div[class='alfresco-charts-ccc-Chart'] path[transform]");

    public SiteContributorBreakdownDashlet assertPieChartSizeEquals(int expectedPieChartSize)
    {
        LOG.info("Assert pie chart size equals: {}", expectedPieChartSize);
        browser.waitUntilElementIsDisplayedWithRetry(pieChartLocator, WAIT_1, RETRY_TIMES);
        assertEquals(pieChartSlices.size(), expectedPieChartSize);
        return this;
    }

    public Map<String, String> getPieChartSliceTooltip()
    {
        HashMap<String, String> slicesTooltip = new HashMap<>();
        for (WebElement slice : pieChartSlices)
        {
            browser.mouseOver(slice);
            String tooltip = sliceTooltip.getAttribute("original-title");
            slicesTooltip.put(tooltip.substring(tooltip.indexOf("<strong>") + 8, tooltip.indexOf("</strong>")), tooltip.substring(tooltip.indexOf("<br/>"), tooltip.indexOf("</div>")));
        }
        System.out.println("Tooltip: " + slicesTooltip);
        return slicesTooltip;
    }

    @Override
    public String getDashletTitle()
    {
        return browser.waitUntilElementVisible(dashletTitle).getText();
    }

    private List<String> addOptionsToList()
    {
        List<String> optionsList = new ArrayList<>();
        for (WebElement option : optionsListLocator)
        {
            LOG.info("Add option to list: {}", option.getText());
            optionsList.add(option.getText());
        }
        return optionsList;
    }

    public UserProfilePage clickPieChartUsername()
    {
        LOG.info("Click pie chart username");
        browser.scrollIntoView(browser.findElement(userLocator));
        browser.waitUntilElementClickable(userLocator);
        browser.findElement(userLocator).click();

        return (UserProfilePage) userProfilePage.renderedPage();
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
        assertEquals(browser.findElement(dashletEmptyMessageLocator).getText(), expectedEmptyMessage);
        return this;
    }

    public SiteContributorBreakdownDashlet openFilterDropDown() {
        LOG.info("Open filters dropdown");
        browser.waitUntilElementClickable(dropDownLocator, WAIT_10);
        browser.findElement(dropDownLocator).click();

        return this;
    }
}
