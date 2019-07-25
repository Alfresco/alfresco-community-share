package org.alfresco.po.share;

import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@org.alfresco.utility.web.annotation.PageObject
public class HideWelcomePanelDialogue extends ShareDialog
{
    @RenderWebElement
    @FindBy (xpath = "//button[text() = 'OK']")
    private WebElement okButton;

    private By startedPanelContainer = By.cssSelector("[id$=get-started-panel-container]");

    /**
     * Method used to confirm 'Hide Welcome Panel' action
     */

    public void confirmHideWelcomePanel()
    {
        browser.waitUntilElementClickable(okButton).click();
        browser.waitUntilElementDisappears(startedPanelContainer, 10);
    }

}
