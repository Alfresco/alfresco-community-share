package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
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

    public void setLinkTitleField(String linkTitle)
    {
        linkTitleField.clear();
        linkTitleField.sendKeys(linkTitle);
    }

    public void setUrlField(String url)
    {
        urlField.clear();
        urlField.sendKeys(url);
    }

    public boolean isLinkTitleFieldDisplayed()
    {
        return linkTitleField.isDisplayed();
    }

    public boolean isUrlFieldDisplayed()
    {
        return urlField.isDisplayed();
    }

}
