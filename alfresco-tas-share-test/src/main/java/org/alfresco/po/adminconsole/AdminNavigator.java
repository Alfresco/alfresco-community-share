package org.alfresco.po.adminconsole;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.ApplicationContext;
import ru.yandex.qatools.htmlelements.element.Link;

@PageObject
public class AdminNavigator extends HtmlPage implements Navigator
{
    public static final String TenantConsole = "Tenant Console";
    @RenderWebElement
    @FindBy (linkText = "System Summary")
    Link systemSymmary;
    @RenderWebElement
    @FindBy (linkText = "Model and Messages Console")
    Link modelAndMessagesConsole;
    @FindBy (className = "selected")
    WebElement selected;

    @Override
    public Link getActiveLink()
    {
        return new Link(selected);
    }

    @SuppressWarnings ("rawtypes")
    public void goTo(NavigationLink link, ApplicationContext applicationContext)
    {

        By locator = By.cssSelector(String.format("a[title=\"%s\"]", link.getTitle()));
        if (!browser.isElementDisplayed(locator))
        {
            LOG.error("Cannot find navigation link identified by title: {}", link.getTitle());
        } else
        {
            WebElement e = browser.findElement(locator);
            e.click();

            Object pageObject = applicationContext.getBean(link.getBeanName());
            if (pageObject instanceof AdminConsolePage)
            {
                AdminConsolePage page = (AdminConsolePage) pageObject;
                page.setBrowser(getBrowser());
                page.renderedPage();
            }
        }
    }

    public enum NavigationLink
    {
        /**
         * Add here the exact name of your admin-console page starting with a lower case string
         */
        TenantConsole("Command console for Tenant admin.", "tenantConsolePage"),
        WorkflowConsole("Command console for Workflow admin.", "workflowConsolePage"),
        InboundEmailPage("Page to administer inbound emails.", "inboundEmailPage"),
        LicensePage("Page to view and administer the License.", "licensePage"),
        ActivitiesFeedPage("Page to administer the Activities Feed emails.", "activitiesFeedPage"),
        IndexServerShardingPage("Administer index server sharding.", "indexServerShardingPage"),
        DownloadJMXDumpPage("Export detailed system information (JMX dump) to a zip file.", "downloadJMXDumpPage"),
        NodeBrowserQueryPage("Repository Node Browser tool.", "nodeBrowserQueryPage");

        private String title, beanName;

        NavigationLink(String title, String beanName)
        {
            this.title = title;
            this.beanName = beanName;
        }

        public String getBeanName()
        {
            return this.beanName;
        }

        public String getTitle()
        {
            return title;
        }
    }
}
