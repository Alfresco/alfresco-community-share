package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SiteNoticeDashlet extends Dashlet<SiteNoticeDashlet>
{
    private final By dashletContainer = By.cssSelector("div[class*='notice-dashlet']");
    private final By editIcon = By.cssSelector("div[class*='notice-dashlet'] div[class*='edit']");
    private final By titleInput = By.cssSelector("input[name='title']");
    private final By titleBar = By.cssSelector("div[class*='notice-dashlet'] .title");
    private final By dialogOkButton = By.cssSelector("button[id*='configDialog-ok-button']");
    private final By dialogCancelButton = By.cssSelector("button[id*='configDialog-cancel-button']");
    private final By noticeMessageLocator = By.cssSelector("div[class*='notice-dashlet'] div[class*='text-content'] p");

    public SiteNoticeDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.getElementText(webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    public SiteNoticeDashlet openConfigurationDialog()
    {
        LOG.info("Open configuration dialog");
        webElementInteraction.mouseOver(titleBar);
        webElementInteraction.clickElement(editIcon);
        return this;
    }

    public SiteNoticeDashlet setSiteNoticeDashletTitle(String title)
    {
        LOG.info("Set site notice dashlet title: {}", title);
        webElementInteraction.clearAndType(titleInput, title);
        return this;
    }

    public SiteNoticeDashlet assertSiteNoticeMessageEquals(String expectedNoticeMessage)
    {
        LOG.info("Assert site notice message equals: {}", expectedNoticeMessage);
        String actualNoticeMessage = webElementInteraction.getElementText(noticeMessageLocator);
        assertEquals(actualNoticeMessage, expectedNoticeMessage,
            String.format("Notice message not equals %s ", expectedNoticeMessage));

        return this;
    }

    public SiteNoticeDashlet setSiteNoticeDashletDocumentText(String documentText)
    {
        LOG.info("Set site notice dashlet document text: {}", documentText);
        TinyMceEditor tinyMceEditor = new TinyMceEditor(webDriver);
        tinyMceEditor.setText(documentText);
        tinyMceEditor.selectTextFromEditor();

        return this;
    }

    public void clickDialogOkButton()
    {
        LOG.info("Click dialog Ok button");
        webElementInteraction.waitUntilElementIsVisible(dialogOkButton);
        webElementInteraction.clickElement(dialogOkButton);
        webElementInteraction.waitUntilElementDisappears(dialogOkButton);
    }

    public void clickDialogCancelButton()
    {
        LOG.info("Click Cancel button");
        webElementInteraction.waitUntilElementIsVisible(dialogCancelButton);
        webElementInteraction.clickElement(dialogCancelButton);
    }
}
