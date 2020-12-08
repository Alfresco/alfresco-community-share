package org.alfresco.po.share;

import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.testng.Assert;

public class HideWelcomePanelDialog extends BaseDialogComponent
{
    private final By okButton = By.cssSelector("span[class$='alf-primary-button']>span>button");
    @RenderWebElement
    private final By panelText = By.cssSelector(".alf-confirmation-panel-text");
    private final By panelTextSettingsIcon = By.cssSelector(" #prompt p");
    private final By configIcon = By.cssSelector(" #prompt .alf-configure-icon");
    private final By startedPanelContainer = By.cssSelector("[id$=get-started-panel-container]");

    public HideWelcomePanelDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
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
