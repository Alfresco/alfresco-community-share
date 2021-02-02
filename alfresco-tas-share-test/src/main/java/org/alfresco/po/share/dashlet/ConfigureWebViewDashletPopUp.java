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

    protected ConfigureWebViewDashletPopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public ConfigureWebViewDashletPopUp setLinkTitleField(String linkTitle)
    {
        webElementInteraction.clearAndType(linkTitleField, linkTitle);
        return this;
    }

    public ConfigureWebViewDashletPopUp setUrlField(String url)
    {
        webElementInteraction.clearAndType(urlField, url);
        return this;
    }

    public ConfigureWebViewDashletPopUp assertLinkTitleFieldIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(linkTitleField);
        assertTrue(webElementInteraction.isElementDisplayed(linkTitleField), "Link field is displayed");
        return this;
    }

    public ConfigureWebViewDashletPopUp assertUrlFieldIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(urlField);
        assertTrue(webElementInteraction.isElementDisplayed(urlField), "URL field is displayed");
        return this;
    }

    public ConfigureWebViewDashletPopUp assertUrlFieldIsMandatory()
    {
        WebElement url = webElementInteraction.waitUntilElementIsVisible(urlField);
        webElementInteraction.focusOnWebElement(url);
        webElementInteraction.waitUntilElementIsVisible(urlErrorMessage);
        assertTrue(webElementInteraction.isElementDisplayed(urlErrorMessage), "Url field is mandatory");
        return this;
    }

    public ConfigureWebViewDashletPopUp assertUrlFieldEmptyValidationMessageIsCorrect()
    {
        assertEquals(language.translate("webViewDashlet.configure.emptyUrlValidation"),
            webElementInteraction.waitUntilElementIsVisible(urlField).getAttribute("title"));
        return this;
    }

    public ConfigureWebViewDashletPopUp assertInvalidUrlFieldValidationMessageIsCorrect()
    {
        assertEquals(language.translate("webViewDashlet.configure.invalidUrlValidation"),
            webElementInteraction.waitUntilElementIsVisible(urlField).getAttribute("title"));
        return this;
    }

    public boolean isConfigureWebViewDashletPopUpDisplayed()
    {
        return webElementInteraction.isElementDisplayed(configureWebViewDashletPopUp);
    }

    public boolean isUrlErrorMessageDisplayed()
    {
        return webElementInteraction.isElementDisplayed(urlErrorMessage);
    }
}
