package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Laura.Capsa
 */
@PageObject
public class ChangeContentTypeDialog extends ShareDialog
{
    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @FindBy (css = "div[id*='changeType-dialogTitle']")
    private WebElement dialogTitle;

    @FindBy (css = "button[id*='changeType']")
    private List<WebElement> buttonsList;

    @RenderWebElement
    @FindBy (css = "select[id*='changeType']")
    private WebElement typeDropdown;

    @FindBy (css = "form div[class='yui-u']")
    private WebElement mandatory;

    /**
     * @return dialog's title
     */
    public String getDialogTitle()
    {
        return dialogTitle.getText();
    }

    /**
     * Click on given button
     *
     * @param buttonName to be clicked
     */
    public DocumentDetailsPage clickButton(String buttonName)
    {
        for (WebElement aButtonsList : buttonsList)
        {
            if (aButtonsList.getText().equals(buttonName))
                aButtonsList.click();
        }
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    /**
     * Select a value from 'New Type' dropdown
     *
     * @param optionName to be selected
     */
    public void selectOption(String optionName)
    {
        Select dropdown = new Select(typeDropdown);
        dropdown.selectByVisibleText(optionName);
    }

    /**
     * Verify mandatory "New Type" dropdown
     *
     * @return true if mandatory element is displayed
     */
    public boolean isDropdownMandatory()
    {
        return browser.isElementDisplayed(mandatory);
    }

    /**
     * Verify presence of button on form
     *
     * @param buttonName to be checked
     * @return true if button is displayed
     */
    public boolean isButtonDisplayed(String buttonName)
    {
        for (WebElement aButtonsList : buttonsList)
        {
            String button = aButtonsList.getText();
            if (button.equals(buttonName))
                return true;
        }
        return false;
    }
}
