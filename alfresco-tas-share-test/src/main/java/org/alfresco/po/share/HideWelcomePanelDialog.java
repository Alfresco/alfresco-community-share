package org.alfresco.po.share;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HideWelcomePanelDialog extends BaseDialogComponent
{
    private final By okButton = By.cssSelector("span[class$='alf-primary-button']>span>button");
    private final By panelText = By.cssSelector(".alf-confirmation-panel-text");
    private final By panelTextSettingsIcon = By.cssSelector(" #prompt p");
    private final By configIcon = By.cssSelector(" #prompt .alf-configure-icon");
    private final By startedPanelContainer = By.cssSelector("[id$=get-started-panel-container]");

    public HideWelcomePanelDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void clickOK()
    {
        LOG.info("Click OK");
        webElementInteraction.clickElement(panelText);
        webElementInteraction.clickElement(okButton);
        webElementInteraction.waitUntilElementDisappears(startedPanelContainer);
    }

    public HideWelcomePanelDialog assertHideWelcomePanelDialogContentIsCorrect()
    {
        LOG.info("Assert Hide welcome panel dialog content is correct");
        assertEquals(webElementInteraction.findElement(panelText).getText(), language.translate("hideWelcomePanelDialog.message"),
                "Dialog message is correct");

       assertTrue(webElementInteraction.isElementDisplayed(configIcon), "Config icon is displayed");
       assertEquals(webElementInteraction.findElement(panelTextSettingsIcon).getText(), language.translate("hideWelcomePanelDialog.selectSettingIcon"),
                "Select setting icon message is correct");

        return this;
    }
}
