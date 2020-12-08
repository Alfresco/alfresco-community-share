package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.TextInput;

@PageObject
public class ConfigureWebViewDashletPopUp extends DashletPopUp<ConfigureWebViewDashletPopUp>
{
    @FindBy (css = "input[id$='webviewTitle']")
    protected TextInput linkTitleField;

    @RenderWebElement
    @FindBy (css = "input[id$='url']")
    protected TextInput urlField;

    @FindBy (css = "div[id$='configDialog_c']")
    private WebElement configureWebViewDashletPopUp;

    @FindBy (css = "input[class='invalid']")
    private WebElement urlErrorMessage;

    public void setLinkTitleField(String linkTitle)
    {
        linkTitleField.clear();
        linkTitleField.sendKeys(linkTitle);
    }

    public ConfigureWebViewDashletPopUp setUrlField(String url)
    {
        urlField.clear();
        urlField.sendKeys(url);
        return this;
    }

    public ConfigureWebViewDashletPopUp assertLinkTitleFieldIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(linkTitleField), "Link field is displayed");
        return this;
    }

    public ConfigureWebViewDashletPopUp assertUrlFieldIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(urlField), "URL field is displayed");
        return this;
    }

    public ConfigureWebViewDashletPopUp assertUrlFieldIsMandatory()
    {
        browser.focusOnWebElement(urlField);
        Assert.assertTrue(browser.isElementDisplayed(urlErrorMessage), "Url field is mandatory");
        return this;
    }

    public ConfigureWebViewDashletPopUp assertUrlFieldEmptyValidationMessageIsCorrect()
    {
        Assert.assertTrue(urlField.getAttribute("title").equals(language.translate("webViewDashlet.configure.emptyUrlValidation")));
        return this;
    }

    public ConfigureWebViewDashletPopUp assertInvalidUrlFieldValidationMessageIsCorrect()
    {
        Assert.assertTrue(urlField.getAttribute("title").equals(language.translate("webViewDashlet.configure.invalidUrlValidation")));
        return this;
    }

    /**
     * Verify presence of "Configure Web View Dashlet" popup in the page.
     *
     * @return true if displayed.
     */
    public boolean isConfigureWebViewDashletPopUpDisplayed()
    {
        return configureWebViewDashletPopUp.isDisplayed();
    }

    /**
     * Verify presence of error message when "URL" field is populated with invalid characters.
     *
     * @return true if displayed.
     */
    public boolean isUrlErrorMessageDisplayed()
    {
        return urlErrorMessage.isDisplayed();
    }
}
