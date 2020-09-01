package org.alfresco.po.share.dashlet;

import org.alfresco.common.Utils;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
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
    @FindBy (css = "input[name='url']")
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

    public EnterFeedURLPopUp setUrlField(String URL)
    {
        Utils.clearAndType(urlField, URL);
        return this;
    }

    public EnterFeedURLPopUp selectNumberOfItemsToDisplay(String value)
    {
        noItemsToDisplay.selectByValue(value);
        return this;
    }

    public EnterFeedURLPopUp selectNumberOfItemsToDisplay(int value)
    {
        noItemsToDisplay.selectByValue(String.valueOf(value));
        return this;
    }

    public EnterFeedURLPopUp checkNewWindow()
    {
        newWindowCheckbox.select();
        return this;
    }

    public boolean isValueSelectedFromNoItemsToDisplayDropDown(String value)
    {
        return noItemsToDisplay.getFirstSelectedOption().getText().equals(value);
    }

    public EnterFeedURLPopUp assertValueSelectInNrOfItemsToDisplayIs(int value)
    {
        Assert.assertEquals(noItemsToDisplay.getFirstSelectedOption().getText(), String.valueOf(value),
            "Selected nr of items to display is correct");
        return this;
    }

    public boolean isNewWindowCheckBoxChecked()
    {
        return newWindowCheckbox.isSelected();
    }

    public EnterFeedURLPopUp assertNewWindowIsChecked()
    {
        Assert.assertTrue(newWindowCheckbox.isSelected(), "New window checkbox is selected");
        return this;
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
