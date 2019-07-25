package org.alfresco.po.share.user;

import java.util.List;

import org.alfresco.po.share.DashboardCustomization;
import org.alfresco.po.share.DashboardCustomizationImpl;
import org.alfresco.po.share.DashboardCustomizationImpl.Layout;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Radio;
import ru.yandex.qatools.htmlelements.element.TextBlock;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class CustomizeUserDashboardPage extends SharePage<CustomizeUserDashboardPage>
    implements DashboardCustomization
{

    @Autowired
    DashboardCustomizationImpl dashboardCustomization;

    @Autowired
    UserDashboardPage userDashboard;

    @RenderWebElement
    @FindBy (css = "div[id$='default-welcome-preference']")
    private TextBlock getStartedPanel;

    @RenderWebElement
    @FindBy (css = "div[id$='welcomePreferenceButtonWrapper-div'] > input")
    private Radio welcomePanel;

    @Override
    public String getRelativePath()
    {
        return "share/page/customise-user-dashboard";
    }

    /**
     * Verify if get started panel text is displayed
     *
     * @return
     */
    public boolean isGetStartedPanelDisplayed()
    {
        return getStartedPanel.isDisplayed();
    }

    public void activateGetStartedPanel(boolean activate)
    {
        if (activate)
        {
            welcomePanel.selectByIndex(0);
        } else
        {
            welcomePanel.selectByIndex(1);
        }
    }

    public boolean isShowOnDashboardSelected()
    {
        return welcomePanel.getButtons().get(0).isSelected();
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
        userDashboard.renderedPage();
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
    public List<WebElement> reorderDashletsInColumn(Dashlets dashletToMove, Dashlets dashletToReplace, int column)
    {
        List<WebElement> dashlets = dashboardCustomization.reorderDashletsInColumn(dashletToMove, dashletToReplace,
            column);
        browser.dragAndDrop(dashlets.get(0), dashlets.get(1));
        return dashlets;
    }
}
