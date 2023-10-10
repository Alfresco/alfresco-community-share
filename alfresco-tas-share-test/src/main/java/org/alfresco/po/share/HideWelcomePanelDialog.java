package org.alfresco.po.share;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
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
        log.info("Click OK");
        clickElement(panelText);
        clickElement(okButton);
        waitUntilElementDisappears(startedPanelContainer);
    }

    public HideWelcomePanelDialog assertHideWelcomePanelDialogContentIsCorrect()
    {
        log.info("Assert Hide welcome panel dialog content is correct");
        assertEquals(findElement(panelText).getText(), language.translate("hideWelcomePanelDialog.message"),
                "Dialog message is correct");

       assertTrue(isElementDisplayed(configIcon), "Config icon is displayed");
       assertEquals(findElement(panelTextSettingsIcon).getText(), language.translate("hideWelcomePanelDialog.selectSettingIcon"),
                "Select setting icon message is correct");

        return this;
    }
}
