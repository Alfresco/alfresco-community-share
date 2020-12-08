package org.alfresco.po.share;

import static org.alfresco.common.Wait.WAIT_5;

import org.alfresco.common.EnvProperties;
import org.alfresco.common.Language;
import org.alfresco.common.ShareTestContext;
import org.alfresco.utility.TasProperties;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.alfresco.utility.web.renderer.Renderer;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a template which should be inherit by each page object/component class.
 *
 * So for example we are going to have:
 *
 * 1.<PageObjectClass> extends BasePage
 *    e.g. UploadUserResultsPage extends BasePage
 *
 * 2.<PageComponentClass> extends BaseComponent
 *    e.g. DeleteDialog extends BaseDialogComponent
 *    e.g. BaseDialog extends BasePage
 *
 * In the base pages/components class we will store only common members/methods which are in each child classes.
 */
public abstract class BasePage
{
    protected final Logger LOG = LoggerFactory.getLogger(BasePage.class);

    public static ThreadLocal<String> notificationMessageThread = new ThreadLocal<>();
    public final By MESSAGE_LOCATOR = By.cssSelector("div.bd span.message");

    public static TasProperties tasProperties;
    public static EnvProperties properties;
    public static Language language;

    protected ThreadLocal<WebBrowser> browser;

    public BasePage(ThreadLocal<WebBrowser> browser)
    {
        if(properties == null)
        {
            ApplicationContext context = new AnnotationConfigApplicationContext(ShareTestContext.class);
            properties = context.getBean(EnvProperties.class);
            tasProperties = context.getBean(TasProperties.class);
            language = context.getBean(Language.class);
        }
        this.browser = browser;
    }

    //todo: access should be protected
    public WebBrowser getBrowser()
    {
        return browser.get();
    }

    public ThreadLocal<String>  waitUntilNotificationMessageDisappears()
    {
        try
        {
            notificationMessageThread.set(
                getBrowser().waitUntilElementVisible(MESSAGE_LOCATOR, WAIT_5.getValue()).getText());
            getBrowser().waitUntilElementDisappears(MESSAGE_LOCATOR);
        }
        catch (TimeoutException | StaleElementReferenceException exception)
        {
            LOG.info("Failed to get notification message {}", exception.getMessage());
        }
        return notificationMessageThread;
    }

    //todo: should be moved in WebBrowser class
    public void clearAndType(WebElement webElement, String value)
    {
        webElement.clear();
        webElement.sendKeys(value);
    }

    //todo: should be moved in WebBrowser class
    public void clearAndType(By byElement, String value)
    {
        clearAndType(getBrowser().findElement(byElement), value);
    }

    //todo: should be moved in WebBrowser class
    public void clickElement(By elementToClick)
    {
        getBrowser().waitUntilElementClickable(elementToClick).click();
    }

    //todo: should be moved in WebBrowser class
    public String getElementText(By selector)
    {
        return getBrowser().waitUntilElementVisible(selector).getText();
    }

    public String getPageTitle()
    {
        return getBrowser().getTitle();
    }

    public BasePage renderedPage()
    {
        /*
         * get the RenderWebElement annotation of all declared fields and
         * render them based on the rules defined
         */
        List<Field> allFields = getAllDeclaredFields(Collections.synchronizedList(new ArrayList<>()), this.getClass());
        for (Field field : allFields)
        {
            for (Annotation annotation : field.getAnnotationsByType(RenderWebElement.class))
            {
                RenderWebElement renderAnnotation = (RenderWebElement) annotation;
                Renderer renderer = renderAnnotation.state().toInstance();

                if(field.getType() == By.class)
                {
                    field.setAccessible(true);
                    Object by = null;
                    try
                    {
                        by = field.get(this);
                    }
                    catch (IllegalAccessException e)
                    {
                        LOG.error("Failed to get field {}", field.getName());
                    }
                    renderer.render(renderAnnotation, (By) by, getBrowser(), tasProperties);
                }
            }
        }
        return this;
    }

    private List<Field> getAllDeclaredFields(List<Field> fields, Class<?> type)
    {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != null)
        {
            fields = getAllDeclaredFields(fields, type.getSuperclass());
        }
        return fields;
    }
}
