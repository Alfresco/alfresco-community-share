/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.dashlet;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.exception.ShareException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(xpath="//div[count(./div[@class='toolbar'])=1 and contains(@class,'rssfeed')]")
/**
 * @author Aliaksei Boole
 */
public class AddOnsRssFeedDashlet extends AbstractDashlet implements Dashlet
{
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.xpath("//div[count(./div[@class='toolbar'])=1 and contains(@class,'rssfeed')]");
    private static final By titleBarActions = By.cssSelector(".titleBarActions");
    protected static final By USER_DASH_DASHLET_TITLE = By.cssSelector(".title");
    protected static final By LOADING_ITEMS = By.cssSelector(".title");
    protected static String HEADER_INFO = "div[class$='dashlet rssfeed resizable yui-resize'] > div[class$='toolbar'] > div";

//    /**
//     * Constructor.
//     */
//    protected AddOnsRssFeedDashlet(WebDriver driver)
//    {
//        super(driver, DASHLET_CONTAINER_PLACEHOLDER);
//        setResizeHandle(By.xpath(".//div[contains (@class, 'yui-resize-handle')]"));
//    }
    @SuppressWarnings("unchecked")
    public  AddOnsRssFeedDashlet render(RenderTime timer)
    {
        try
        {
            while (true)
            {
                timer.start();
                synchronized (this)
                {
                    try
                    {
                        this.wait(50L);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                try
                {
                    this.dashlet = findAndWait((DASHLET_CONTAINER_PLACEHOLDER), 100L, 10L);
                    break;
                }
                catch (NoSuchElementException e)
                {

                }
                catch (StaleElementReferenceException ste)
                {
                    // DOM has changed therefore page should render once change
                    // is completed
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new NoSuchDashletExpection(this.getClass().getName() + " failed to find my profile dashlet", te);
        }
        return this;
    }
    /**
     * This method gets the focus by placing mouse over on Site RSS Feed Dashlet.
     */
    protected void getFocus()
    {
        mouseOver(dashlet.findElement(DASHLET_CONTAINER_PLACEHOLDER));
    }

    /**
     * Method to click Configure icon from RSS Feed dashlet
     * 
     * @return RssFeedUrlBoxPage
     */
    public RssFeedUrlBoxPage clickConfigure()
    {
        try
        {
            mouseOver(driver.findElement(titleBarActions));
            dashlet.findElement(CONFIGURE_DASHLET_ICON).click();
            return factoryPage.instantiatePage(driver, RssFeedUrlBoxPage.class).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find configure button");
        }
    }

    /**
     * This method gets details info from the dashlet
     * 
     * @return String
     */
    public String getHeaderInfo()
    {
        String fullDetail = "";

        try
        {
            fullDetail = dashlet.findElement(By.cssSelector(HEADER_INFO)).getText();
            return fullDetail;

        }
        catch (TimeoutException te)
        {
            throw new UnsupportedOperationException("Exceeded time to find the title.", te);
        }
    }

    /**
     * Method to verify if Configure icon from RSS Feed dashlet is present
     * 
     * @return boolean
     */
    public boolean isConfigurePresent()
    {
        try
        {
            mouseOver(driver.findElement(titleBarActions));
            return dashlet.findElement(CONFIGURE_DASHLET_ICON).isDisplayed();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find configure button");
        }
    }

    /**
     * Method to get the headline sites from the dashlet
     * 
     * @return List<String>
     */
    public List<ShareLink> getHeadlineLinksFromDashlet()
    {
        List<ShareLink> rssLinks = new ArrayList<ShareLink>();
        try
        {
            List<WebElement> links = driver.findElements(By.cssSelector(".headline>h4>a"));
            for (WebElement div : links)
            {
                rssLinks.add(new ShareLink(div, driver, factoryPage));
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access dashlet data", nse);
        }

        return rssLinks;
    }
    
    public void waitUntilLoadingDisappears()
    {
        waitUntilElementDisappears(By.cssSelector("div[class$='dashlet-padding'] > h3"), 30);
    }
    @SuppressWarnings("unchecked")
    @Override
    public AddOnsRssFeedDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

}
