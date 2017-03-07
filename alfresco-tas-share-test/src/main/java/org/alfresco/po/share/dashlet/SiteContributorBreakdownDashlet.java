package org.alfresco.po.share.dashlet;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
