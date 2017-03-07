package org.alfresco.po;

import org.alfresco.browser.WebBrowser;
import org.alfresco.po.annotation.PageObject;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

/**
 * Just @Autowire and initialize yandex web elements of each PageObject annotated class
 * Using a custom {@link HtmlElementDecorator}
 * 
 * @author Paul.Brodner
 */
@Component
public class HtmlPageBeanPostProcessor implements BeanPostProcessor
{
    @Autowired
    @Qualifier("webBrowserInstance")
    WebBrowser browser;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
        if (bean.getClass().isAnnotationPresent(PageObject.class))
        {
            PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(browser)), bean);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
        return bean;
    }
}
