package org.alfresco.po.share;

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

public abstract class SharePageObject2
{
    protected final Logger LOG = LoggerFactory.getLogger(SharePageObject2.class);

    public static final int WAIT_1 = 1;
    public static final int WAIT_5 = 5;
    public static final int WAIT_10 = 10;
    public static final int WAIT_15 = 15;
    public static final int WAIT_30 = 30;
    public static final int WAIT_60 = 60;
    public static final int DEFAULT_RETRY = 3;
    public static ThreadLocal<String> notificationMessageThread = new ThreadLocal<>();
    public static final By MESSAGE_LOCATOR = By.cssSelector("div.bd span.message");

    public static TasProperties tasProperties;
    public static EnvProperties properties;
    public static Language language;

    protected ThreadLocal<WebBrowser> browser = new ThreadLocal<>();

    public SharePageObject2()
    {
        if(properties == null)
        {
            ApplicationContext context = new AnnotationConfigApplicationContext(ShareTestContext.class);
            properties = context.getBean(EnvProperties.class);
            tasProperties = context.getBean(TasProperties.class);
            language = context.getBean(Language.class);
        }
    }

    public WebBrowser getBrowser()
    {
        return browser.get();
    }

    public ThreadLocal<String>  waitUntilNotificationMessageDisappears()
    {
        try
        {
            notificationMessageThread.set(getBrowser().waitUntilElementVisible(MESSAGE_LOCATOR, 5).getText());
            getBrowser().waitUntilElementDisappears(MESSAGE_LOCATOR);
        }
        catch (TimeoutException | StaleElementReferenceException exception)
        {
            LOG.info("Failed to get notification message {}", exception.getMessage());
        }
        return notificationMessageThread;
    }

    public void clearAndType(WebElement webElement, String value)
    {
        webElement.clear();
        webElement.sendKeys(value);
    }

    public void clearAndType(By byElement, String value)
    {
        clearAndType(getBrowser().findElement(byElement), value);
    }

    public void clickElement(By elementToClick)
    {
        getBrowser().findElement(elementToClick).click();
    }

    public String getElementText(By selector)
    {
        return getBrowser().findElement(selector).getText();
    }

    public String getPageTitle()
    {
        return getBrowser().getTitle();
    }

    public SharePageObject2 renderedPage()
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
