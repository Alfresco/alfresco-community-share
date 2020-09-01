package org.alfresco.po.share.dashlet;


import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import java.util.List;

/**
 * Created by Mirela Tifui on 11/23/2017.
 */
@PageObject
public class ContentImEditingDashlet extends Dashlet<ContentImEditingDashlet>
{
    @Autowired
    private SiteDashboardPage siteDashboardPage;

    @RenderWebElement
    @FindBy (css = "div[id$='_default-my-docs-dashlet']")
    private HtmlElement dashletContainer;

    @FindBy (css = "div[id$='_default-my-docs-dashlet'] div[class='titleBarActionIcon help']")
    private WebElement helpIcon;

    @FindAll (@FindBy (css = "div.hdr h3"))
    private List<WebElement> dashletContentHeaders;

    private String editedDocumentRow = "//div[@class='detail-list-item']//a[text()='%s']/../../..";
    private By editedDocumentName = By.cssSelector("h4 > a");
    private By editDocumentSite = By.cssSelector("a[class$='site-link']");

    private WebElement selectSiteNameLink(String siteName)
    {
        return browser.findElement(By.xpath("//div[contains(@id, '_default-document-template')]//div[@class='details']//a[text()='" + siteName + "']"));
    }

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public ContentImEditingDashlet clickHelpIcon()
    {
        helpIcon.click();
        return this;
    }

    public ContentImEditingDashlet assertAllHeadersAreDisplayed()
    {
        getBrowser().waitUntilElementsVisible(dashletContentHeaders);
        Assert.assertNotNull(browser.findFirstElementWithExactValue(dashletContentHeaders,
            language.translate("contentImEditingDashlet.documents")));
        Assert.assertNotNull(browser.findFirstElementWithExactValue(dashletContentHeaders,
            language.translate("contentImEditingDashlet.blogPosts")));
        Assert.assertNotNull(browser.findFirstElementWithExactValue(dashletContentHeaders,
            language.translate("contentImEditingDashlet.wikiPages")));
        Assert.assertNotNull(browser.findFirstElementWithExactValue(dashletContentHeaders,
            language.translate("contentImEditingDashlet.forumPosts")));
        return this;
    }

    public void clickDocument(FileModel document)
    {
        waitForDocumentToBeDisplayed(document).findElement(editedDocumentName).click();
    }

    public SiteDashboardPage clickSite(FileModel document)
    {
        waitForDocumentToBeDisplayed(document).findElement(editDocumentSite).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }

    public boolean isDocumentDisplayedInDashlet(FileModel file)
    {
        return browser.isElementDisplayed(By.xpath(String.format(editedDocumentRow, file.getName())));
    }

    public WebElement waitForDocumentToBeDisplayed(FileModel file)
    {
        boolean found = isDocumentDisplayedInDashlet(file);
        int i = 0;
        while(i < WAIT_30 && !found)
        {
            i++;
            LOG.info(String.format("Wait for document to be displayed: %s", i));
            browser.refresh();
            renderedPage();
            found = isDocumentDisplayedInDashlet(file);
        }
        return browser.findElement(By.xpath(String.format(editedDocumentRow, file.getName())));
    }

    public ContentImEditingDashlet assertDocumentIsDisplayed(FileModel file)
    {
        waitForDocumentToBeDisplayed(file);
        Assert.assertTrue(isDocumentDisplayedInDashlet(file),
            String.format("File %s is displayed in content I'm editing dashlet", file.getName()));
        return this;
    }

    public ContentImEditingDashlet assertDocumentIsNotDisplayed(FileModel file)
    {
        Assert.assertFalse(isDocumentDisplayedInDashlet(file),
            String.format("File %s is displayed in content I'm editing dashlet", file.getName()));
        return this;
    }
}
