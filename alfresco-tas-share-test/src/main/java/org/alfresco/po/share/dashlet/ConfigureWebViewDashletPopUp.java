package org.alfresco.po.share.dashlet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ConfigureWebViewDashletPopUp extends DashletPopUp<ConfigureWebViewDashletPopUp>
{
    private final By linkTitleField = By.cssSelector("input[id$='webviewTitle']");
    private final By urlField = By.cssSelector("input[id$='url']");
    private final By configureWebViewDashletPopUp = By.cssSelector("div[id$='configDialog_c']");
    private final By urlErrorMessage = By.cssSelector("input[class='invalid']");

    public ConfigureWebViewDashletPopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public ConfigureWebViewDashletPopUp setLinkTitleField(String linkTitle)
    {
        clearAndType(linkTitleField, linkTitle);
        return this;
    }

    public ConfigureWebViewDashletPopUp setUrlField(String url)
    {
        clearAndType(urlField, url);
        return this;
    }

    public ConfigureWebViewDashletPopUp assertLinkTitleFieldIsDisplayed()
    {
        waitUntilElementIsVisible(linkTitleField);
        assertTrue(isElementDisplayed(linkTitleField), "Link field is displayed");
        return this;
    }

    public ConfigureWebViewDashletPopUp assertUrlFieldIsDisplayed()
    {
        waitUntilElementIsVisible(urlField);
        assertTrue(isElementDisplayed(urlField), "URL field is displayed");
        return this;
    }

    public ConfigureWebViewDashletPopUp assertUrlFieldIsMandatory()
    {
        WebElement url = waitUntilElementIsVisible(urlField);
        focusOnWebElement(url);
        waitUntilElementIsVisible(urlErrorMessage);
        assertTrue(isElementDisplayed(urlErrorMessage), "Url field is mandatory");
        return this;
    }

    public ConfigureWebViewDashletPopUp assertUrlFieldEmptyValidationMessageIsCorrect()
    {
        assertEquals(language.translate("webViewDashlet.configure.emptyUrlValidation"),
            waitUntilElementIsVisible(urlField).getAttribute("title"));
        return this;
    }

    public ConfigureWebViewDashletPopUp assertInvalidUrlFieldValidationMessageIsCorrect()
    {
        assertEquals(language.translate("webViewDashlet.configure.invalidUrlValidation"),
            waitUntilElementIsVisible(urlField).getAttribute("title"));
        return this;
    }

    public boolean isConfigureWebViewDashletPopUpDisplayed()
    {
        waitInSeconds(3);
        return isElementDisplayed(configureWebViewDashletPopUp);
    }

    public boolean isUrlErrorMessageDisplayed()
    {
        return isElementDisplayed(urlErrorMessage);
    }
}
