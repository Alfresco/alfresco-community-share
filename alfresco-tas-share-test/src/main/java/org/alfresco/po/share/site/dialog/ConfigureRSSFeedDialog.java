package org.alfresco.po.share.site.dialog;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Select;

/**
 * Created by Mirela Tifui on 11/24/2017.
 */
@PageObject
public class ConfigureRSSFeedDialog extends ShareDialog
{
    //@Autowired
    SiteDashboardPage siteDashboardPage;

    @RenderWebElement
    @FindBy (css = "div[id$='_default-configDialog-configDialog']")
    private WebElement editRssDialog;

    @FindBy (css = "input[id$='_default-configDialog-url']")
    private WebElement urlInputField;

    @FindBy (css = "button[id$='_default-configDialog-ok-button']")
    private WebElement okButton;

    @FindBy (css = "select[id$='3_default-configDialog-limit']")
    private Select selectNumberOfItemsToDisplay;

    public void typeInURL(String Url)
    {
        urlInputField.clear();
        urlInputField.sendKeys(Url);
    }

    public SiteDashboardPage clickOkButton()
    {
        getBrowser().waitUntilElementClickable(okButton).click();
        getBrowser().waitUntilElementDisappears(By.cssSelector("div[id$='_default-configDialog-configDialog_c']"));
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }

    public void selectNumberOfItemsToDisplay(String selectValue)
    {
        selectNumberOfItemsToDisplay.selectByValue(selectValue);
    }
}
