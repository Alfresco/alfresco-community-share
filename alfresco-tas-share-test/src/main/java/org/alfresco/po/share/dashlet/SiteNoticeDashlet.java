package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
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
        return getElementText(waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    public SiteNoticeDashlet openConfigurationDialog()
    {
        log.info("Open configuration dialog");
        mouseOver(titleBar);
        clickElement(editIcon);
        return this;
    }

    public SiteNoticeDashlet setSiteNoticeDashletTitle(String title)
    {
        log.info("Set site notice dashlet title: {}", title);
        clearAndType(titleInput, title);
        return this;
    }

    public SiteNoticeDashlet assertSiteNoticeMessageEquals(String expectedNoticeMessage)
    {
        log.info("Assert site notice message equals: {}", expectedNoticeMessage);
        String actualNoticeMessage = getElementText(noticeMessageLocator);
        assertEquals(actualNoticeMessage, expectedNoticeMessage,
            String.format("Notice message not equals %s ", expectedNoticeMessage));

        return this;
    }

    public SiteNoticeDashlet setSiteNoticeDashletDocumentText(String documentText)
    {
        log.info("Set site notice dashlet document text: {}", documentText);
        TinyMceEditor tinyMceEditor = new TinyMceEditor(webDriver);
        tinyMceEditor.setText(documentText);
        tinyMceEditor.selectTextFromEditor();

        return this;
    }

    public void clickDialogOkButton()
    {
        log.info("Click dialog Ok button");
        waitUntilElementIsVisible(dialogOkButton);
        clickElement(dialogOkButton);
        waitUntilElementDisappears(dialogOkButton);
    }

    public void clickDialogCancelButton()
    {
        log.info("Click Cancel button");
        waitUntilElementIsVisible(dialogCancelButton);
        clickElement(dialogCancelButton);
    }
}
