package org.alfresco.po.share;

import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@org.alfresco.utility.web.annotation.PageObject
public class HideWelcomePanelDialogue extends ShareDialog
{
    @RenderWebElement
    @FindBy (xpath = "//*[text() = 'OK']")
    private WebElement okButton;

    /**
     * Method used to confirm 'Hide Welcome Panel' action
     */

    public void confirmHideWelcomePanel()
    {
        browser.waitUntilElementVisible(okButton);
        okButton.click();
        browser.waitUntilElementDisappears(By.cssSelector("[id$=get-started-panel-container]"), 5);
    }

}
