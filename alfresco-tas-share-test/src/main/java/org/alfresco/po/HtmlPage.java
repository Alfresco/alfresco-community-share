package org.alfresco.po;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.alfresco.browser.WebBrowser;
import org.alfresco.common.EnvProperties;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.renderer.Renderer;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Basic implementation of HtmlPage
 * 
 * @author Paul.Brodner
 */
public abstract class HtmlPage
{
    protected final Logger LOG = LoggerFactory.getLogger(HtmlPage.class);

    @Autowired
    protected EnvProperties properties;

    @Autowired
    @Qualifier("webBrowserInstance")
    protected WebBrowser browser;

    public HtmlPage renderedPage()
    {
        /*
         * get the RenderWebElement annotation of all declared fields and
         * render them based on the rules defined
         */
        List<Field> allFields = getAllDeclaredFields(new LinkedList<Field>(), this.getClass());
        for (Field field : allFields)
        {
            for (Annotation annotation : field.getAnnotationsByType(RenderWebElement.class))
            {
                RenderWebElement renderAnnotation = (RenderWebElement) annotation;
                Renderer renderer = renderAnnotation.state().toInstance();

                Annotation[] allAnnotation = field.getAnnotations();


                for(Annotation tmpAnnotation : allAnnotation)
                {
                    if (tmpAnnotation instanceof FindBy)
                    {
                        FindBy findBy =  (FindBy) tmpAnnotation;
                        renderer.render(renderAnnotation, findBy, browser, properties);
                    }
                    else  if (tmpAnnotation instanceof FindAll)
                    {
                        FindBy[] allFindBy =  ((FindAll) tmpAnnotation).value();
                        for (FindBy by : allFindBy)
                        {
                            renderer.render(renderAnnotation, by, browser, properties);
                        }

                    }

                }
            }
        }
        return this;
    }

    /**
     * Backtrack algorithm to gather all declared fields within SuperClasse-es
     * but stopping on HtmlPage.class
     * 
     * @param fields
     * @param type
     * @return
     */
    private List<Field> getAllDeclaredFields(List<Field> fields, Class<?> type)
    {
        if (type.isAssignableFrom(HtmlPage.class))
        {
            return fields;
        }

        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null)
        {
            fields = getAllDeclaredFields(fields, type.getSuperclass());
        }
        return fields;
    }

    /**
     * Get the title of the current page
     * 
     * @return
     */
    public String getPageTitle()
    {
        return browser.getTitle();
    }

    /**
     * Method for wait while balloon message about some changes hide.
     */
    public void waitUntilMessageDisappears()
    {
        browser.waitUntilElementDeletedFromDom(By.cssSelector("div[id='message_c'] span[class='message']"), 15);
    }

    public void refresh(){ browser.refresh();}
}
