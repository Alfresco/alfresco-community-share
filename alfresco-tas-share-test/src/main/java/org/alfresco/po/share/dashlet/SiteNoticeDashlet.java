package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.TinyMce.TinyMceColourCode;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import java.util.List;

/**
 * Created by Argint Alex
 */
@PageObject
public class SiteNoticeDashlet extends Dashlet<SiteNoticeDashlet>
{
    @Autowired
    TinyMceEditor tinyMceEditor;

    @RenderWebElement
    @FindBy (css = "div[class*='notice-dashlet']")
    protected HtmlElement dashletContainer;

    @FindBy (css = "div[class*='notice-dashlet'] div[class*='edit']")
    private WebElement editIcon;

    @FindBy (xpath = "//div[contains(text(),'Configure Site Notice')]")
    private WebElement configurePanelTitle;

    @FindBy (css = "a.container-close")
    private WebElement configurePanelCloseButton;

    @FindBy (css = "input[name='title']")
    private WebElement titleInput;

    @FindAll (@FindBy (css = "div[class*='notice-dashlet'] div[class*='text-content'] p"))
    private List<WebElement> noticeText;

    @FindBy (css = "button[id*='configDialog-ok-button']")
    private WebElement configPanelOk;

    @FindBy (css = "button[id*='configDialog-cancel-button']")
    private WebElement configPanelCancel;

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public void openConfigurePanel()
    {
        editIcon.click();
    }

    public boolean isConfigurePanelOpened()
    {
        return browser.isElementDisplayed(configurePanelTitle);
    }

    public void closeConfigurePanel()
    {
        configurePanelCloseButton.click();
        browser.refresh();
    }

    /**
     * Method clicks on the title input and types in the title parameter
     *
     * @param title
     */
    public void setSiteNoticeDashletTitle(String title)
    {
        titleInput.clear();
        titleInput.sendKeys(title);
    }

    public String getSiteNoticeText()
    {

        String text = "";
        String text2;
        for (WebElement aNoticeText : noticeText)
        {
            text2 = aNoticeText.getText();
            text = text + text2;
        }
        return text;
    }

    public void setSiteNoticeText(String text)
    {
        tinyMceEditor.setText(text);
        tinyMceEditor.selectTextFromEditor();
        getBrowser().waitInSeconds(2);
    }

    public void clickOkButton()
    {
        getBrowser().waitUntilElementVisible(configPanelOk);
        getBrowser().waitUntilElementClickable(configPanelOk).click();

    }

    public void clickCancelbutton()
    {
        getBrowser().waitUntilElementVisible(configPanelCancel);
        getBrowser().waitUntilElementClickable(configPanelCancel).click();
    }

    public void setTextColour(String colour)
    {
        switch (colour)
        {
            case "BLUE":
                tinyMceEditor.clickColorCode(TinyMceColourCode.BLUE);
                break;
            case "YELLOW":
                tinyMceEditor.clickColorCode(TinyMceColourCode.YELLOW);
                break;
            default:
                tinyMceEditor.clickColorCode(TinyMceColourCode.BLACK);
        }
    }

    public void setTextBackgroundColour(String colour)
    {
        switch (colour)
        {
            case "BLUE":
                tinyMceEditor.clickBackgroundColorCode(TinyMceColourCode.BLUE);
                break;
            case "YELLOW":
                tinyMceEditor.clickBackgroundColorCode(TinyMceColourCode.YELLOW);
                break;
            default:
                tinyMceEditor.clickBackgroundColorCode(TinyMceColourCode.BLACK);
        }
    }

    public String siteNoticeGetText()
    {
        editIcon.click();
        return tinyMceEditor.getText();
    }
}
