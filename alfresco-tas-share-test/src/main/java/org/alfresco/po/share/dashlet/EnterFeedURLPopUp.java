package org.alfresco.po.share.dashlet;

import org.alfresco.common.Utils;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.CheckBox;
import ru.yandex.qatools.htmlelements.element.Select;
import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * Created by Claudia Agache on 7/7/2016.
 */
@PageObject
public class EnterFeedURLPopUp extends DashletPopUp
{
    @RenderWebElement
    @FindBy (css = "input[id$='url']")
    private TextInput urlField;

    @FindBy (css = "input[id$='default-configDialog-new_window']")
    private CheckBox newWindowCheckbox;

    @FindBy (css = "select[id$='default-configDialog-limit']")
    private Select noItemsToDisplay;

    @FindBy (css = "div[class^='config-feed']")
    private WebElement enterFeedURLPopUp;

    @FindBy (css = "input[id$='default-configDialog-url'][alf-validation-msg*='error']")
    private WebElement urlErrorMessage;

    @FindBy (css = "button[id$='configDialog-ok-button']")
    private WebElement okButton;

    public void fillUrlField(String URL)
    {
        Utils.clearAndType(urlField, URL);
    }

    public void selectNumberOfItemsToDisplay(String value)
    {
        noItemsToDisplay.selectByValue(value);
    }

    public void checkNewWindowCheckbox()
    {
        newWindowCheckbox.select();
    }

    public String getUrlFieldText()
    {
        return urlField.getText();
    }

    public boolean isValueSelectedFromNoItemsToDisplayDropDown(String value)
    {
        return noItemsToDisplay.getFirstSelectedOption().getText().equals(value);
    }

    public boolean isNewWindowCheckBoxChecked()
    {
        return newWindowCheckbox.isSelected();
    }

    /**
     * Verify presence of "Enter Feed URL" popup in the page.
     *
     * @return true if displayed
     */
    public boolean isEnterFeedURLPopUpDisplayed()
    {
        return browser.waitUntilElementVisible(enterFeedURLPopUp).isDisplayed();
    }

    /**
     * Verify presence of error message when "URL" field is populated with invalid characters
     *
     * @return true if displayed
     */
    public boolean isUrlErrorMessageDispalyed()
    {
        return urlErrorMessage.isDisplayed();
    }
}
