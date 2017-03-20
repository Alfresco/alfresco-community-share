package org.alfresco.po.share.site;

import org.alfresco.po.share.DashboardCustomization;
import org.alfresco.po.share.DashboardCustomizationImpl;
import org.alfresco.po.share.DashboardCustomizationImpl.Layout;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.asserts.Assertion;

import junit.framework.Assert;
import ru.yandex.qatools.htmlelements.element.TextBlock;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class CustomizeSiteDashboardPage extends SiteCommon<CustomizeSiteDashboardPage> implements DashboardCustomization
{
    @Autowired
    DashboardCustomizationImpl dashboardCustomization;
    @Autowired
    SiteDashboardPage siteDashboardPage;

    @RenderWebElement
    @FindBy(css = ".sub-title")
    protected TextBlock subTitle;
    
    @RenderWebElement
    @FindBy(css = ".trashcan")
    protected WebElement trashcan;

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/customise-site-dashboard", getCurrentSiteName());
    }

    @Override
    public String getSubTitle()
    {
        return dashboardCustomization.getSubTitle();
    }

    @Override
    public String getCurrentLayout()
    {
        return dashboardCustomization.getCurrentLayout();
    }

    @Override
    public void clickChangeLayout()
    {
        dashboardCustomization.clickChangeLayout();
    }

    @Override
    public String getNewLayoutInstructions()
    {
        return dashboardCustomization.getNewLayoutInstructions();
    }

    @Override
    public String getDashletsInstructions()
    {
        return dashboardCustomization.getDashletsInstructions();
    }

    @Override
    public void selectLayout(Layout layout)
    {
        dashboardCustomization.selectLayout(layout);
    }

    @Override
    public void clickCancelNewLayout()
    {
        dashboardCustomization.clickCancelNewLayout();
    }

    @Override
    public void clickAddDashlet()
    {
        dashboardCustomization.clickAddDashlet();
    }

    @Override
    public void clickOk()
    {
        dashboardCustomization.clickOk();
        siteDashboardPage.renderedPage();
    }

    @Override
    public void addDashlet(Dashlets dashlet, int columnNumber)
    {
        dashboardCustomization.addDashlet(dashlet, columnNumber);
    }

    @Override
    public boolean isDashletAddedInColumn(Dashlets dashlet, int columnNumber)
    {
        return dashboardCustomization.isDashletAddedInColumn(dashlet, columnNumber);
    }

    @Override
    public void removeDashlet(Dashlets dashlet, int columnNumber)
    {
        dashboardCustomization.removeDashlet(dashlet, columnNumber);
    }

    @Override
    public boolean isOneColumnLayoutDisplayed()
    {
        return dashboardCustomization.isOneColumnLayoutDisplayed();
    }

    @Override
    public boolean isTwoColumnsLayoutWideRightDisplayed()
    {
        return dashboardCustomization.isTwoColumnsLayoutWideRightDisplayed();
    }

    @Override
    public boolean isTwoColumnsLayoutWideLeftDisplayed()
    {
        return dashboardCustomization.isTwoColumnsLayoutWideLeftDisplayed();
    }

    @Override
    public boolean isThreeColumnsLayoutDisplayed()
    {
        return dashboardCustomization.isThreeColumnsLayoutDisplayed();
    }

    @Override
    public boolean isFourColumnsLayoutDisplayed()
    {
        return dashboardCustomization.isFourColumnsLayoutDisplayed();
    }

    @Override
    public boolean isChangeLayoutButtonDisplayed()
    {
        return dashboardCustomization.isChangeLayoutButtonDisplayed();
    }
    
    @Override
    public boolean isChangeLayoutSectionDisplayed()
    {
        return dashboardCustomization.isChangeLayoutSectionDisplayed();
    }

    @Override
    public boolean isDashletSectionDisplayed()
    {
        return dashboardCustomization.isDashletSectionDisplayed();
    }

    @Override
    public boolean isAddDashletButtonDisplayed()
    {
        return dashboardCustomization.isAddDashletButtonDisplayed();
    }

    @Override
    public List<String> getDashletsFromColumn(int column)
    {
        return dashboardCustomization.getDashletsFromColumn(column);
    }

    @Override
    public List<String> getAvailableDashlets()
    {
        return dashboardCustomization.getAvailableDashlets();
    }
    
    @Override
    public void clickCloseAvailabeDashlets()
    {
        dashboardCustomization.clickCloseAvailabeDashlets();
    }
    
    @Override
    public boolean isAvailableDashletListDisplayed()
    {
        return dashboardCustomization.isAvailableDashletListDisplayed();
    }
    
    @Override
    public void moveAddedDashletInColumn(Dashlets dashlet, int fromColumn, int toColumn)
    {
        dashboardCustomization.moveAddedDashletInColumn(dashlet, fromColumn, toColumn);
    }

    @Override
    public void reorderDashletsInColumn(Dashlets dashletToMove, Dashlets dashletToReplace, int column)
    {
        WebElement dashToMove = null;
        WebElement dashToReplace = null;
        String addedDashlet = "//ul[contains(@id,'default-column-ul-%d')]/li//span[text()='%s']/following-sibling::div";
        try
        {
            dashToMove = browser.waitUntilElementVisible(By.xpath(String.format(addedDashlet, column, dashletToMove.getDashletName())));
        }
        catch(NoSuchElementException ns)
        {
            throw new PageOperationException(dashletToMove.getDashletName() + " not found in column " + column);
        }
        try
        {
            dashToReplace = browser.waitUntilElementVisible(By.xpath(String.format(addedDashlet, column, dashletToReplace.getDashletName())));
        }
        catch(NoSuchElementException ns)
        {
            throw new PageOperationException(dashletToReplace.getDashletName() + " not found in column " +  column);
        }
        dashToMove.click();
        browser.dragAndDrop(dashToMove, dashToReplace);
        browser.dragAndDrop(dashToMove, dashToReplace);
    }
    
}
