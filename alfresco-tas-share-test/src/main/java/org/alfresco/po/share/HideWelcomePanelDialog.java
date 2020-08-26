package org.alfresco.po.share;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

@PageObject
public class HideWelcomePanelDialog extends ShareDialog
{
    @FindBy (css = "span[class$='alf-primary-button']>span>button")
    private WebElement okButton;

    @RenderWebElement
    @FindBy (css = ".alf-confirmation-panel-text")
    private WebElement panelText;

    @RenderWebElement
    @FindBy (css = " #prompt p")
    private WebElement panelTextSettingsIcon;

    @FindBy (css = " #prompt .alf-configure-icon")
    private WebElement configIcon;

    private By startedPanelContainer = By.cssSelector("[id$=get-started-panel-container]");

    /**
     * Method used to confirm 'Hide Welcome Panel' action
     */

    public void clickOK()
    {
        LOG.info("Click OK");
        panelText.click();
        browser.waitUntilElementClickable(okButton).click();
        browser.waitUntilElementDisappears(startedPanelContainer, 10);
    }

    public HideWelcomePanelDialog assertHideWelcomePanelDialogContentIsCorrect()
    {
        LOG.info("Assert Hide welcome panel dialog content is correct");
        Assert.assertEquals(panelText.getText(), language.translate("hideWelcomePanelDialog.message"),
            "Dialog message is correct");
        Assert.assertTrue(browser.isElementDisplayed(configIcon), "Config icon is displayed");
        Assert.assertEquals(panelTextSettingsIcon.getText(), language.translate("hideWelcomePanelDialog.selectSettingIcon"),
            "Select setting icon message is correct");
        return this;
    }
}
