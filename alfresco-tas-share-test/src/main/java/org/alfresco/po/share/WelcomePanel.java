package org.alfresco.po.share;

import org.alfresco.utility.web.HtmlPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@org.alfresco.utility.web.annotation.PageObject
public class WelcomePanel extends org.alfresco.utility.web.HtmlPage
{
    @Autowired
    HideWelcomePanelDialogue hideWelcomePanelDialogue;

    @org.alfresco.utility.web.annotation.RenderWebElement
    @FindBy(css = "[id$=get-started-panel-container]")
    private WebElement welcomePanel;

    @FindBy(css = ".welcome-info")
    private WebElement welcomeInfo;

    @FindBy(css = "button[id$='_default-hide-button-button']")
    private WebElement hideButton;

    /**
     * Method used to verify if Share Welcome Panel is successfully displayed
     */

    public boolean isWelcomePanelDisplayed()
    {
        return browser.isElementDisplayed(welcomePanel) || browser.isElementDisplayed(welcomeInfo);
    }

    /**
     * Method used to click on Get Started link
     */

    public void clickGetStartedLink()
    {
        welcomeInfo.click();

    }

    /**
     * Method used to verify if Alfresco tutorial page is opened
     */

    public boolean isAlfrescoTutorialDisplayed()
    {
        String url = browser.getCurrentUrl();
        return url.contains("alfresco") || url.contains("tutorial");
    }

    /**
     * Method used to retrieve the message from the Welcome panel
     */

    public String getWelcomeMessage()
    {
        String welcomeMessage = welcomeInfo.getText();
        return welcomeMessage;
    }

    /**
     * Method used to verify if Hide button from Welcome panel is displayed
     */

    public boolean isHideButtonDisplayed()
    {
        return hideButton.isDisplayed();
    }

    /**
     * Method used to click on Hide button
     */

    public HideWelcomePanelDialogue clickOnHideButton()
    {
        browser.waitUntilElementClickable(hideButton).click();
        getBrowser().clickJS(hideButton);
        return (HideWelcomePanelDialogue) hideWelcomePanelDialogue.renderedPage();
    }

}
