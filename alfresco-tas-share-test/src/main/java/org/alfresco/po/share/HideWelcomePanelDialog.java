package org.alfresco.po.share;

import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

public class HideWelcomePanelDialog extends ShareDialog2
{
    private By okButton = By.cssSelector("span[class$='alf-primary-button']>span>button");
    @RenderWebElement
    private By panelText = By.cssSelector(".alf-confirmation-panel-text");
    private By panelTextSettingsIcon = By.cssSelector(" #prompt p");
    private By configIcon = By.cssSelector(" #prompt .alf-configure-icon");
    private By startedPanelContainer = By.cssSelector("[id$=get-started-panel-container]");

    public HideWelcomePanelDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public void clickOK()
    {
        LOG.info("Click OK");
        getBrowser().findElement(panelText).click();
        getBrowser().waitUntilElementClickable(okButton).click();
        getBrowser().waitUntilElementDisappears(startedPanelContainer, 10);
    }

    public HideWelcomePanelDialog assertHideWelcomePanelDialogContentIsCorrect()
    {
        LOG.info("Assert Hide welcome panel dialog content is correct");
        Assert.assertEquals(getBrowser().findElement(panelText).getText(), language.translate("hideWelcomePanelDialog.message"),
                "Dialog message is correct");
        Assert.assertTrue(getBrowser().isElementDisplayed(configIcon), "Config icon is displayed");
        Assert.assertEquals(getBrowser().findElement(panelTextSettingsIcon).getText(), language.translate("hideWelcomePanelDialog.selectSettingIcon"),
                "Select setting icon message is correct");

        return this;
    }
}
