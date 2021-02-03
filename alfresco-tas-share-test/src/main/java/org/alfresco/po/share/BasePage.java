
package org.alfresco.po.share;

import static org.alfresco.common.Wait.WAIT_5;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.DefaultProperties;
import org.alfresco.common.Language;
import org.alfresco.common.ShareTestContext;
import org.alfresco.common.WebElementInteraction;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
@Slf4j
public abstract class BasePage
{
    protected static final ThreadLocal<String> notificationMessageThread = new ThreadLocal<>();
    public static final ThreadLocal<DefaultProperties> defaultProperties = new ThreadLocal<>();

    public final Language language;
    protected final By notificationMessageLocator = By.cssSelector("div.bd span.message");
    protected WebElementInteraction webElementInteraction;
    public final ThreadLocal<WebDriver> webDriver;

    protected BasePage(ThreadLocal<WebDriver> webDriver)
    {
        if(defaultProperties.get() == null)
        {
            @SuppressWarnings("resource")
            ApplicationContext context = new AnnotationConfigApplicationContext(ShareTestContext.class);
            defaultProperties.set(context.getBean(DefaultProperties.class));
        }

        language = new Language("language/page_labels",
            defaultProperties.get().getBrowserLanguage(),
            defaultProperties.get().getBrowserLanguageCountry());

        this.webDriver = webDriver;
        waitUntilDomReadyStateIsComplete(webDriver);
        webElementInteraction = new WebElementInteraction(webDriver, defaultProperties.get());
    }

    private void waitUntilDomReadyStateIsComplete(ThreadLocal<WebDriver> webDriver)
    {
        new WebDriverWait(webDriver.get(), defaultProperties.get().getExplicitWait())
            .until(driver -> ((JavascriptExecutor) webDriver.get())
                .executeScript("return document.readyState").equals("complete"));
    }

    public ThreadLocal<String> waitUntilNotificationMessageDisappears()
    {
        try
        {
            notificationMessageThread.set(webElementInteraction.getElementText(
                notificationMessageLocator, WAIT_5.getValue()));
            webElementInteraction.waitUntilElementDisappears(notificationMessageLocator, WAIT_5.getValue());
        }
        catch (TimeoutException | StaleElementReferenceException exception)
        {
            log.info("Failed to get notification message {}", exception.getMessage());
        }
        return notificationMessageThread;
    }
}
