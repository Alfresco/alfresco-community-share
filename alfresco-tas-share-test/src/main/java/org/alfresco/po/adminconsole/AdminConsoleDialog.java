package org.alfresco.po.adminconsole;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Claudia Agache on 5/9/2017.
 */
public abstract class AdminConsoleDialog extends HtmlPage
{
    @RenderWebElement
    @FindBy(id = "admin-dialog")
    protected WebElement dialogFrame;

    private By closeButtonLocator = By.className("cancel");
    private By titleLocator = By.className("title");
    private By introLocator = By.className("intro");

    public HtmlPage clickClose()
    {
        browser.switchTo().frame(dialogFrame);
        browser.waitUntilElementClickable(closeButtonLocator, properties.getExplicitWait()).click();
        browser.switchTo().defaultContent();
        return renderedPage();
    }

    public boolean isCloseButtonDisplayed()
    {
        browser.switchTo().frame(dialogFrame);
        boolean isDisplayed = browser.isElementDisplayed(closeButtonLocator);
        browser.switchTo().defaultContent();
        return isDisplayed;
    }

    public String getIntro()
    {
        browser.switchTo().frame(dialogFrame);
        String intro = browser.waitUntilElementVisible(introLocator).getText().trim();
        browser.switchTo().defaultContent();
        return intro;
    }

    public String getTitle()
    {
        browser.switchTo().frame(dialogFrame);
        String title = browser.waitUntilElementVisible(titleLocator).getText().trim();
        browser.switchTo().defaultContent();
        return title;
    }

    public boolean isDialogDisplayed()
    {
        return browser.isElementDisplayed(dialogFrame);
    }

}