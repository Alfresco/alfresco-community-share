package org.alfresco.po.share.dashlet;

import org.alfresco.common.Utils;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.TextInput;

@PageObject
public class ConfigureWebViewDashletPopUp extends DashletPopUp
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
        Utils.clearAndType(linkTitleField, linkTitle);
    }

    public void setUrlField(String url)
    {
        Utils.clearAndType(urlField, url);
    }

    /**
     * Verify presence of "Link Title" field in "Configure Web View Dashlet" popup.
     *
     * @return true if displayed.
     */
    public boolean isLinkTitleFieldDisplayed()
    {
        return linkTitleField.isDisplayed();
    }

    /**
     * Verify presence of "URL" field in "Configure Web View Dashlet" popup.
     *
     * @return true if displayed.
     */
    public boolean isUrlFieldDisplayed()
    {
        return urlField.isDisplayed();
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
