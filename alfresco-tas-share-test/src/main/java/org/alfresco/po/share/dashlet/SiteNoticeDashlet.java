package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.po.share.TinyMce.TinyMceColourCode;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
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
    @Autowired
    TinyMceEditor tinyMceEditor;
    @Autowired
    SiteDashboardPage siteDashboardPage;
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

    @FindBy (css = "div[id$='_default-configDialog-configDialog'] a.container-close")
    private WebElement closeConfigPanel;

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
    }

    public SiteDashboardPage clickOkButton()
    {
        getBrowser().waitUntilElementVisible(configPanelOk);
        getBrowser().waitUntilElementClickable(configPanelOk).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }

    public SiteDashboardPage clickCancelbutton()
    {
        getBrowser().waitUntilElementVisible(configPanelCancel);
        getBrowser().waitUntilElementClickable(configPanelCancel).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }

    public SiteDashboardPage clickCloseButton()
    {
        closeConfigPanel.click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
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
