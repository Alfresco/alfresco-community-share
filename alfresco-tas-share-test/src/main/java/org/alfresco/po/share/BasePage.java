
package org.alfresco.po.share;

import static org.alfresco.common.Wait.WAIT_5;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.DefaultProperties;
import org.alfresco.common.Language;
import org.alfresco.common.ShareTestContext;
import org.alfresco.common.WebElementInteraction;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * This class represents a template which should be inherited by each page object/component class.
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
@Slf4j
public abstract class BasePage extends WebElementInteraction
{
    protected static final ThreadLocal<String> notificationMessageThread = new ThreadLocal<>();
    protected final By notificationMessageLocator = By.cssSelector("div.bd span.message");
    protected final Language language;

    protected BasePage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);

        createAppContextAndSetPropertiesIfNull();
        language = getLanguage();
        waitUntilDomReadyStateIsComplete();
    }

    private Language getLanguage()
    {
        return new Language("language/page_labels",
            defaultProperties.getLanguage(),
            defaultProperties.getCountry());
    }

    private void createAppContextAndSetPropertiesIfNull()
    {
        if(defaultProperties == null)
        {
            ApplicationContext context = new AnnotationConfigApplicationContext(ShareTestContext.class);
            defaultProperties = context.getBean(DefaultProperties.class);
        }
    }

    public ThreadLocal<String> waitUntilNotificationMessageDisappears()
    {
        try
        {
            notificationMessageThread.set(getElementText(notificationMessageLocator, WAIT_5.getValue()));
            waitUntilElementDisappears(notificationMessageLocator, WAIT_5.getValue());
        }
        catch (TimeoutException | StaleElementReferenceException exception)
        {
            log.info("Failed to get notification message {}", exception.getMessage());
        }
        return notificationMessageThread;
    }
}
