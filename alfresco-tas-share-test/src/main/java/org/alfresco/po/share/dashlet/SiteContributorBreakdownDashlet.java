package org.alfresco.po.share.dashlet;

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
    @Autowired
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
    private List<WebElement> optionsListText;

    @FindBy (css = "span[class$='dijitValidationTextBoxLabel ']")
    private WebElement selectedFilterOption;

    public int getNumberOfPieChartSlices()
    {
        browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector("div[class='alfresco-charts-ccc-Chart'] path[transform]"));
        return pieChartSlices.size();
    }

    public Map<String, String> getPieChartSliceTooltip()
    {
        Map<String, String> slicesTooltip = new HashMap<>();
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

    public boolean isPeriodFilterDisplayed()
    {
        browser.waitUntilElementVisible(periodFilter);
        return browser.isElementDisplayed(periodFilter);
    }

    public ArrayList<String> getOptionText()
    {
        periodFilter.click();
        ArrayList<String> optionText = new ArrayList<>();
        for (WebElement anOptionsListText : optionsListText)
        {
            optionText.add(anOptionsListText.getText());
        }
        System.out.println("Text: " + optionText);
        return optionText;
    }

    public String getSelectedFilterOption()
    {
        return browser.waitUntilElementVisible(selectedFilterOption).getText();
    }

    public UserProfilePage clickOnUserSection(String userName)
    {
        List<WebElement> testUsers = browser.findElements(By.cssSelector("div[class='alfresco-charts-ccc-Chart'] text"));
        List<WebElement> pieChartSlices = browser.findElements(By.cssSelector("div[class='alfresco-charts-ccc-Chart'] path[transform]"));
        for (int i = 0; i < testUsers.size(); i++)
        {
            if (testUsers.get(i).getText().contains(userName))
            {
                pieChartSlices.get(i).click();
                break;
            }
        }

        return (UserProfilePage) userProfilePage.renderedPage();
    }
}
