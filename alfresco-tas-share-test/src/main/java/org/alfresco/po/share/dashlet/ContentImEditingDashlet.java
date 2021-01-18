package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.web.annotation.PageObject;
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
    //@Autowired
    private SiteDashboardPage siteDashboardPage;

    @FindBy (css = "div[id$='_default-my-docs-dashlet']")
    private HtmlElement dashletContainer;

    @FindBy (css = "div[id$='_default-my-docs-dashlet'] div[class='titleBarActionIcon help']")
    private WebElement helpIcon;

    @FindAll (@FindBy (css = "div.hdr h3"))
    private List<WebElement> dashletContentHeaders;

    private String editedDocumentRow = "//div[@class='detail-list-item']//a[text()='%s']/../../..";
    private By editedDocumentName = By.cssSelector("h4 > a");
    private By editDocumentSite = By.cssSelector("a[class$='site-link']");

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
        assertNotNull(browser.findFirstElementWithExactValue(dashletContentHeaders,
            language.translate("contentImEditingDashlet.documents")));
        assertNotNull(browser.findFirstElementWithExactValue(dashletContentHeaders,
            language.translate("contentImEditingDashlet.blogPosts")));
        assertNotNull(browser.findFirstElementWithExactValue(dashletContentHeaders,
            language.translate("contentImEditingDashlet.wikiPages")));
        assertNotNull(browser.findFirstElementWithExactValue(dashletContentHeaders,
            language.translate("contentImEditingDashlet.forumPosts")));
        return this;
    }

    public void clickDocument(FileModel document)
    {
        waitForDocumentToBeDisplayed(document).findElement(editedDocumentName).click();
    }

    public void clickSite(FileModel document)
    {
        waitForDocumentToBeDisplayed(document).findElement(editDocumentSite).click();
    }

    public boolean isDocumentDisplayedInDashlet(FileModel file)
    {
        return browser.isElementDisplayed(By.xpath(String.format(editedDocumentRow, file.getName())));
    }

    public WebElement waitForDocumentToBeDisplayed(FileModel file)
    {
        boolean found = isDocumentDisplayedInDashlet(file);
        int i = 0;
        while(i < WAIT_60 && !found)
        {
            i++;
            LOG.error("Wait for document {} to be displayed: {}", file.getName(), i);
            browser.refresh();
            renderedPage();
            found = isDocumentDisplayedInDashlet(file);
        }
        return browser.findElement(By.xpath(String.format(editedDocumentRow, file.getName())));
    }

    public ContentImEditingDashlet assertDocumentIsDisplayed(FileModel file)
    {
        LOG.info("Assert document {} is displayed", file.getName());
        waitForDocumentToBeDisplayed(file);
        assertTrue(isDocumentDisplayedInDashlet(file),
            String.format("File %s is not displayed in content I'm editing dashlet", file.getName()));
        return this;
    }

    public ContentImEditingDashlet assertDocumentIsNotDisplayed(FileModel file)
    {
        LOG.info("Assert document {} is noy displayed", file.getName());
        assertFalse(isDocumentDisplayedInDashlet(file),
            String.format("File %s is displayed in content I'm editing dashlet", file.getName()));
        return this;
    }
}
