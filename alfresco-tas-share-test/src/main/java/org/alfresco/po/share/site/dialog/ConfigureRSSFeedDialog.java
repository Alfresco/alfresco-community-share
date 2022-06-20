package org.alfresco.po.share.site.dialog;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Select;

/**
 * Created by Mirela Tifui on 11/24/2017.
 */
@PageObject
public class ConfigureRSSFeedDialog extends ShareDialog
{
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

    public void clickOkButton()
    {
        getBrowser().waitUntilElementClickable(okButton).click();
        getBrowser().waitUntilElementDisappears(By.cssSelector("div[id$='_default-configDialog-configDialog_c']"));
    }

    public void selectNumberOfItemsToDisplay(String selectValue)
    {
        selectNumberOfItemsToDisplay.selectByValue(selectValue);
    }
}
