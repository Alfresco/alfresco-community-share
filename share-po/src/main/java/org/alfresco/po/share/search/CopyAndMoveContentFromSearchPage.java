/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.po.share.search;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

@SuppressWarnings("unchecked")
public class CopyAndMoveContentFromSearchPage extends ShareDialogue
{

    private static Log logger = LogFactory.getLog(CopyAndMoveContentFromSearchPage.class);

    private final RenderElement FOOTER_ELEMENT = getVisibleRenderElement(By.cssSelector("div[class='footer']"));
    private final RenderElement HEADER_ELEMENT = getVisibleRenderElement(By.cssSelector("div[class='dijitDialogTitleBar']"));
    private final RenderElement DOC_LIST_VIEW = getVisibleRenderElement((By.cssSelector("div[id^='alfresco_documentlibrary_views_AlfDocumentListView']")));
    private final By destinationListCss = By
            .cssSelector("div[class='sub-pickers']>div[class^='alfresco-menus-AlfMenuBar']>div>div[class^='dijitReset dijitInline']>span");
    private final By copyMoveOkOrCancelButtonCss = By.cssSelector("div[class='footer']>span");
    private final By copyMoveDialogCloseButtonCss = By.cssSelector("div[class='dijitDialogTitleBar']>span[class^=dijitDialogCloseIcon ]");
    private final By copyMoveDialogTitleCss = By.cssSelector("div[class='dijitDialogTitleBar']>span[class='dijitDialogTitle']");
    private String PathFolderCss = "//div[starts-with(@id,'alfresco_documentlibrary_views_AlfDocumentListView')] //tr/td/span/span/span[@class='value'][text()='%s']";
    private String adButton = "//div[starts-with(@id,'alfresco_documentlibrary_views_AlfDocumentListView')] //tr/td/span/span/span[@class='value'][text()='%s']/../../../../td/span[starts-with(@id, 'alfresco_renderers_PublishAction')]";

    // private final By disabledBackCss = By.cssSelector("div[class$='dijitMenuItem dijitMenuItemDisabled dijitDisabled']>span[id$='PAGE_BACK_text']");
    // private final By disabledNextCss = By.cssSelector("div[class$='dijitMenuItem dijitMenuItemDisabled dijitDisabled']>span[id$='PAGE_FORWARD_text']");
    private final By nextCss = By.cssSelector("div[class$='dijitReset dijitInline dijitMenuItemLabel dijitMenuItem']>span[id$='PAGE_FORWARD_text']");
    private final By backCss = By.cssSelector("div[class$='dijitReset dijitInline dijitMenuItemLabel dijitMenuItem']>span[id$='PAGE_BACK_text']");


//FIXME render checking for different elements
    @Override
    public CopyAndMoveContentFromSearchPage render(RenderTime timer)
    {
        elementRender(timer, HEADER_ELEMENT, FOOTER_ELEMENT);
        return this;
    }
    
    public CopyAndMoveContentFromSearchPage renderDocumentListView(long timeInMilliSceonds)
    {
        elementRender(new RenderTime(timeInMilliSceonds), DOC_LIST_VIEW);
        return this;
    }

    /**
     * This method returns the Copy/Move Dialog title.
     * 
     * @return String
     */

    public String getDialogTitle()
    {
        String title = "";
        try
        {
            title = findAndWait(copyMoveDialogTitleCss).getText();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the Copy/Move Dialog Css : ", e);
            }
        }
        return title;
    }

    /**
     * This method finds the list of destinations and return those as list of
     * string values.
     * 
     * @return List<String>
     */
    public List<String> getDestinations()
    {
        List<String> destinations = new ArrayList<String>();
        try
        {
            for (WebElement destination : findAndWaitForElements(destinationListCss))
            {
                destinations.add(destination.getText());
            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to get the list of destionations : ", e);
            }
        }
        return destinations;
    }

    /**
     * This method finds the clicks on copy/move/cancel button.
     * 
     * @param buttonName String
     * @return HtmlPage FacetedSerachResultsPage
     */
    private FacetedSearchPage selectCopyOrMoveOrCancelButton(String buttonName)
    {
        if (StringUtils.isEmpty(buttonName))
        {
            throw new IllegalArgumentException("button name is required");
        }

        try
        {
            for (WebElement button : findAndWaitForElements(copyMoveOkOrCancelButtonCss))
            {
                if (button.getText() != null)
                {
                    if (button.getText().equalsIgnoreCase(buttonName))
                    {
                        button.click();
                        //FIXME
                        waitForPageLoad(getDefaultWaitTime());
                        return getCurrentPage().render();

                    }
                }
            }
            throw new PageOperationException("Unable to find the button: " + buttonName);
        }
        catch (NoSuchElementException ne)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the inner text of button" + buttonName, ne);
            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the button" + buttonName, e);
            }
        }

        throw new PageOperationException("Unable to select button." + buttonName);
    }

    /**
     * This method finds the clicks on 'Copy' button in Copy/Move pop up page
     */
    public FacetedSearchPage selectCopyButton()
    {
        return selectCopyOrMoveOrCancelButton("Copy");
    }

    public FacetedSearchPage selectCancelButton()
    {
        return selectCopyOrMoveOrCancelButton("Cancel");
    }

    public FacetedSearchPage selectMoveButton()
    {
        return selectCopyOrMoveOrCancelButton("Move");
    }

    /**
     * This method finds the clicks on close button in copy and move dialog page
     * 
     * @return FacetedSearchPage
     */
    public HtmlPage selectCloseButton()
    {
        try
        {
            findAndWait(copyMoveDialogCloseButtonCss).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the close button Css : ", e);
            }
            throw new PageException("Unable to find the close button on Copy/Move Dialog.");
        }
        return getCurrentPage();
    }

    /**
     * This method finds and selects the given destination from the
     * displayed list of destinations in CopyAndMoveContentFromSearchPage
     * 
     * @param destinationName String
     * @return CopyOrMoveContentPage
     */
    public CopyAndMoveContentFromSearchPage selectDestination(String destinationName)
    {
        PageUtils.checkMandotaryParam("destinationName", destinationName);
        try
        {
            for (WebElement destination : findAndWaitForElements(destinationListCss))
            {
                if (destination.getText() != null)
                {
                    if (destination.getText().equalsIgnoreCase(destinationName))
                    {
                        destination.click();
                    }

                }

            }

            return this;
        }
        catch (NoSuchElementException ne)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the inner text of destionation", ne);
            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to get the list of destionations", e);
            }
        }

        throw new PageOperationException("Unable to select Destination : " + destinationName);
    }

    /**
     * This method finds and selects any folder from repository in CopyAndMoveContentFromSearchPage
     * This method is used when Repository is selected as destination
     * 
     * @param repoFolder String
     * @return CopyOrMoveContentPage
     */
    public CopyAndMoveContentFromSearchPage selectFolderInRepo(String repoFolder)
    {

        PageUtils.checkMandotaryParam("repoFolder", repoFolder);

        try
        {

            By finalFolderElement = By.xpath(String.format(PathFolderCss, repoFolder));
            WebElement element = findAndWait(finalFolderElement);
            if (!element.isDisplayed())
            {
                element.click();
            }
            element.click();

            return this;
        }
        catch (NoSuchElementException ne)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the inner text of destionation", ne);
            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to get the list of destionations", e);
            }
        }

        throw new PageOperationException("Unable to select sites : ");
    }

    /**
     * This method can be used to select any folder in the site, given destination only from repository/Sites
     * This method is used when Repository/Sites are selected in CopyAndMoveContentFromSearchPage
     * 
     * @param paths String
     * @return CopyOrMoveContentPage
     */
    public CopyAndMoveContentFromSearchPage selectFolder(String... paths)
    {
        PageUtils.checkMandotaryParam("paths", paths);

        try
        {
            int pathsLength = paths.length;
            int pathCount = 1;
            String finalPath = "";
            for (String path : paths)
            {
                finalPath = path;
                if (pathCount >= pathsLength)
                {
                    break;
                }
                By subpath = By.xpath(String.format(PathFolderCss, path));
                scrollDwon();
                if ((!isNextButtonEnabled()) && (!isBackButtonEnabled()))
                {
                    waitForElement(subpath, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                    findAndWait(subpath).click();
                }
                else if (!driver.findElement(subpath).isDisplayed())
                {
                    while (isNextButtonEnabled() || !(driver.findElement(subpath).isDisplayed()))
                    {
                        scrollDwon();
                        selectNextButton();
                        if (driver.findElement(subpath).isDisplayed())
                        {
                           break;
                        }
                    }
                }
                else if (!driver.findElement(subpath).isDisplayed() && (!isNextButtonEnabled()))
                {
                    throw new PageOperationException("Next button enabled when site is displayed");
                }
                if (driver.findElement(subpath).isDisplayed())
                {
                    waitForElement(subpath, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                    findAndWait(subpath).click();
                }
                pathCount++;
            }

            if (!finalPath.isEmpty())
            {
                By finalFolderElement = By.xpath(String.format(PathFolderCss, finalPath));
                waitForElement(finalFolderElement, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                waitForElement(By.xpath(String.format(adButton, finalPath)), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                driver.findElement(By.xpath(String.format(adButton, finalPath))).click();
            }

            return this;

        }

        catch (NoSuchElementException ne)
        {
                 System.out.println(ne.getMessage());
                logger.trace("Unable to select the final folder", ne);
        }
        catch (TimeoutException e)
        {
                System.out.println(e.getMessage());
                logger.trace("Unable to select the final folder ", e);
        }

        throw new PageOperationException("Unable to select the final folder");
    }

    /**
     * This method finds the clicks on next button in CopyAndMoveContentFromSearchPage
     */
    public CopyAndMoveContentFromSearchPage selectNextButton()
    {
        selectButton(nextCss, "Unable to find the Next button on Copy/Move Dialog.");
        return this;
    }

    /**
     * This method finds the clicks on back button in CopyAndMoveContentFromSearchPage
     */
    public CopyAndMoveContentFromSearchPage selectBackButton()
    {
        selectButton(backCss, "Unable to find the close button on Copy/Move Dialog.");
        return this;
    }

    /**
     * This helper method finds the clicks on next/back button in CopyAndMoveContentFromSearchPage
     */
    private void selectButton(By css, String message)
    {
        try
        {
            if (findAndWait(css).isEnabled())
            {
                findAndWait(css).click();
            }

        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace(message, e);
            }
            throw new PageException(message);
        }
    }

    /**
     * Helper method to return true if Next Button is displayed and enabled
     * 
     * @return boolean <tt>true</tt> is Next Button is displayed and enabled
     */
    public boolean isNextButtonEnabled()
    {
        try
        {
            if (findAndWait(nextCss).isDisplayed() && findAndWait(nextCss).isEnabled())
            {
                return true;
            }

        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    /**
     * Helper method to return true if Back Button is displayed and enabled
     * 
     * @return boolean <tt>true</tt> is Back Button is displayed and enabled
     */
    public boolean isBackButtonEnabled()
    {
        try
        {
            if (findAndWait(backCss).isDisplayed() && findAndWait(backCss).isEnabled())
            {
                return true;
            }
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    /**
     * Helper method to click on the page marker in copy and move dialog page
     * 
     * @return CopyAndMoveContentFromSearchPage
     */
    public CopyAndMoveContentFromSearchPage scrollDwon()
    {              
        By paginatorCss = By.cssSelector("div[id^='alfresco_documentlibrary_AlfDocumentListPaginator'] div[id$='PAGE_MARKER']");
        try
        {
            WebElement paginator = findAndWait(paginatorCss);
            paginator.click();
            return this;
        }
        catch (TimeoutException e)
        {
           throw new PageOperationException("Unable to click on paginator", e);
        }
    }

}
