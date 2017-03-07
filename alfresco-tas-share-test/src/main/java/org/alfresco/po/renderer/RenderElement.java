package org.alfresco.po.renderer;

import org.alfresco.browser.WebBrowser;
import org.alfresco.common.EnvProperties;
import org.alfresco.common.LogFactory;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;

/**
 * @author Paul.Brodner
 */
public abstract class RenderElement implements Renderer
{
    Logger LOG = LogFactory.getLogger();
    protected RenderWebElement renderAnnotation;

    /**
     * Builder mechanism for web element locator
     * 
     * @param findBy
     * @return
     */
    protected By buildFromFindBy(FindBy findBy)
    {
        if (!"".equals(findBy.className()))
        {
            return By.className(findBy.className());
        }
        if (!"".equals(findBy.css()))
        {
            return By.cssSelector(findBy.css());
        }
        if (!"".equals(findBy.id()))
        {
            return By.id(findBy.id());
        }
        if (!"".equals(findBy.linkText()))
        {
            return By.linkText(findBy.linkText());
        }

        if (!"".equals(findBy.name()))
        {
            return By.name(findBy.name());
        }

        if (!"".equals(findBy.partialLinkText()))
        {
            return By.partialLinkText(findBy.partialLinkText());
        }

        if (!"".equals(findBy.tagName()))
        {
            return By.tagName(findBy.tagName());
        }

        if (!"".equals(findBy.xpath()))
        {
            return By.xpath(findBy.xpath());
        }

        // Fall through
        return null;
    }

    public void render(RenderWebElement renderAnnotation, FindBy findByAnnotation, WebBrowser browser, EnvProperties properties)
    {
        this.renderAnnotation = renderAnnotation;
        By locator = buildFromFindBy((FindBy) findByAnnotation);
        LOG.info("Waiting to render element {} using {} worker", locator.toString(), this.getClass().getSimpleName());
        try
        {
            doWork(locator, browser, properties.getImplicitWait());
        }
        catch (TimeoutException | NoSuchElementException e)
        {
            LOG.error("Unable to render the element : " + locator.toString(), e);
            throw new PageRenderTimeException("Element was not rendered in time. Locator: " + locator.toString());
        }
    }

    /**
     * This will perform the actual action of rendering the element
     * 
     * @param findByAnnotation
     * @param browser
     * @param properties
     */
    protected abstract void doWork(By locator, WebBrowser browser, long timeOutInSeconds);
}
