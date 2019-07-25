package org.alfresco.po.share.dashlet;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

/**
 * Created by Mirela Tifui on 11/23/2017.
 */
@PageObject
public class ContentImEditingDashlet extends Dashlet<ContentImEditingDashlet>
{

    @RenderWebElement
    @FindBy (css = "div[id$='_default-my-docs-dashlet']")
    private HtmlElement dashletContainer;

    @FindBy (css = "div[id$='_default-my-docs-dashlet'] div[class='titleBarActionIcon help']")
    private WebElement helpIcon;

    @FindBy (css = "div.balloon div.text ")
    private WebElement helpBalloonText;

    @FindAll (@FindBy (css = "div.hdr h3"))
    private List<WebElement> dashletContentHeaders;

    private WebElement selectDocumentLink(String documentName)
    {
        return browser.findElement(By.xpath("//div[contains(@id, '_default-document-template')]//a[text()='" + documentName + "']"));
    }

    private WebElement selectSiteNameLink(String siteName)
    {
        return browser.findElement(By.xpath("//div[contains(@id, '_default-document-template')]//div[@class='details']//a[text()='" + siteName + "']"));
    }

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public boolean isContentImEditingDashletDisplayed()
    {
        return browser.isElementDisplayed(dashletContainer);
    }

    public void clickHelpIcon()
    {
        helpIcon.click();
    }

    public boolean isHeaderDisplayed(String headerName)
    {
        getBrowser().waitUntilElementsVisible(dashletContentHeaders);
        List<String> presentHeaders = new ArrayList<>();
        for (WebElement item : dashletContentHeaders)
        {
            String headerText = item.getText();
            presentHeaders.add(headerText);
        }
        return presentHeaders.contains(headerName);
    }

    public HtmlPage clickDocumentLink(String documentLink, HtmlPage page)
    {
        getBrowser().waitUntilElementVisible(selectDocumentLink(documentLink), 10);
        selectDocumentLink(documentLink).click();
        return page.renderedPage();
    }

    public HtmlPage clickSiteLink(String siteName, HtmlPage page)
    {
        getBrowser().waitUntilElementVisible(selectSiteNameLink(siteName), 10);
        selectSiteNameLink(siteName).click();
        return page.renderedPage();
    }

    public boolean isDocumentDisplayedInDashlet(String documentName)
    {
        try
        {
            browser.waitUntilElementIsDisplayedWithRetry(By.xpath("//div[contains(@id, '_default-document-template')]//a[text()='" + documentName + "']"), 7);
        } catch (Exception ex)
        {
            LOG.info(ex.getStackTrace().toString());
        }
        return getBrowser().isElementDisplayed(By.xpath("//div[contains(@id, '_default-document-template')]//a[text()='" + documentName + "']"));
    }
}
