package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by p3700681 on 2/24/2017.
 */
@PageObject
public class SiteContributorBreakdownDashlet extends SiteFileTypeBreakdownDashlet
{
    @RenderWebElement
    @FindBy(id="DASHLET")
    private WebElement dashletContainer;

    public By tooltipMessage = By.cssSelector("div[id^='tipsyPvBehavior']");
}
