package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import org.alfresco.common.Utils;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

/**
 * Created by Argint Alex
 */
@PageObject
public class SiteNoticeDashlet extends Dashlet<SiteNoticeDashlet>
{
    @RenderWebElement
    @FindBy (css = "div[class*='notice-dashlet']")
    protected HtmlElement dashletContainer;

    @FindBy (css = "div[class*='notice-dashlet'] div[class*='edit']")
    private WebElement editIcon;

    @FindBy (css = "input[name='title']")
    private WebElement titleInput;

    @FindBy (css = "button[id*='configDialog-ok-button']")
    private WebElement dialogOkButton;

    @FindBy (css = "button[id*='configDialog-cancel-button']")
    private WebElement dialogCancelButton;

    private By noticeMessageLocator = By.cssSelector("div[class*='notice-dashlet'] div[class*='text-content'] p");

    @Autowired
    private TinyMceEditor tinyMceEditor;

    @Autowired
    private SiteDashboardPage siteDashboardPage;

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public SiteNoticeDashlet openConfigurationDialog()
    {
        LOG.info("Open configuration dialog");
        editIcon.click();
        return this;
    }

    public SiteNoticeDashlet setSiteNoticeDashletTitle(String title)
    {
        LOG.info("Set site notice dashlet title: {}", title);
        Utils.clearAndType(titleInput, title);
        return this;
    }

    public SiteNoticeDashlet assertSiteNoticeMessageEquals(String expectedNoticeMessage)
    {
        LOG.info("Assert site notice message equals: {}", expectedNoticeMessage);
        String actualNoticeMessage = browser.findElement(noticeMessageLocator).getText();
        assertEquals(actualNoticeMessage, expectedNoticeMessage,
            String.format("Notice message not equals %s ", expectedNoticeMessage));

        return this;
    }

    public SiteNoticeDashlet setSiteNoticeDashletDocumentText(String documentText)
    {
        LOG.info("Set site notice dashlet document text: {}", documentText);
        tinyMceEditor.setText(documentText);
        tinyMceEditor.selectTextFromEditor();

        return this;
    }

    public SiteDashboardPage clickDialogOkButton()
    {
        LOG.info("Click dialog Ok button");
        browser.waitUntilElementVisible(dialogOkButton);
        browser.waitUntilElementClickable(dialogOkButton).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }

    public SiteDashboardPage clickDialogCancelButton()
    {
        LOG.info("Click Cancel button");
        browser.waitUntilElementVisible(dialogCancelButton);
        browser.waitUntilElementClickable(dialogCancelButton).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }
}
