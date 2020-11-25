package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.CheckBox;
import ru.yandex.qatools.htmlelements.element.Select;
import ru.yandex.qatools.htmlelements.element.TextInput;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by Claudia Agache on 7/7/2016.
 */
@PageObject
public class EnterFeedURLPopUp extends DashletPopUp<EnterFeedURLPopUp>
{
    @RenderWebElement
    @FindBy (css = "input[name='url']")
    private TextInput urlField;

    @FindBy (css = "input[id$='default-configDialog-new_window']")
    private CheckBox newWindowCheckbox;

    @FindBy (css = "select[id$='default-configDialog-limit']")
    private Select numberOfItems;

    @FindBy (css = "div[class^='config-feed']")
    private WebElement enterFeedURLPopUp;

    @FindBy (css = "input[id$='default-configDialog-url'][alf-validation-msg*='error']")
    private WebElement urlErrorMessage;

    @FindBy (css = "button[id$='configDialog-ok-button']")
    private WebElement okButton;

    public EnterFeedURLPopUp setUrlValue(String url)
    {
        LOG.info("Set url value: {}", url);
        urlField.clear();
        urlField.sendKeys(url);

        return this;
    }

    public EnterFeedURLPopUp selectNumberOfItemsToDisplay(String dropDownValue)
    {
        LOG.info("Select number of item to display from drop-down: {}", dropDownValue);
        numberOfItems.selectByValue(dropDownValue);

        return this;
    }

    public EnterFeedURLPopUp selectNumberOfItemsToDisplay(int dropDownValue)
    {
        LOG.info("Select number of items to display: {}", dropDownValue);
        numberOfItems.selectByValue(String.valueOf(dropDownValue));

        return this;
    }

    public EnterFeedURLPopUp selectOpenLinksInNewWindowCheckboxFromDialog()
    {
        LOG.info("Select open links in new window checkbox from dialog");
        newWindowCheckbox.select();

        return this;
    }

    public EnterFeedURLPopUp assertNumberOfItemsToDisplayFromDropDownIs(String expectedNumberOfItems)
    {
        assertEquals(numberOfItems.getFirstSelectedOption().getText(), expectedNumberOfItems,
            "Number of items to be displayed from drop down not equals with expected");

        return this;
    }

    public EnterFeedURLPopUp assertNewWindowIsChecked()
    {
        LOG.info("Assert new window is checked");
        assertTrue(newWindowCheckbox.isSelected(), "New window checkbox is not checked");

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
    public boolean isUrlErrorMessageDisplayed()
    {
        return urlErrorMessage.isDisplayed();
    }
}
