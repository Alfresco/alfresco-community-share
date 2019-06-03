package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Laura.Capsa
 */
@PageObject
public class EditRulesPage extends SiteCommon<EditRulesPage>
{
    @Autowired
    RuleDetailsPage ruleDetailsPage;

    @FindBy (css = ".rule-edit .edit-header")
    private WebElement pageHeader;

    @RenderWebElement
    @FindBy (css = "input[id*='title']")
    private WebElement nameInputField;

    @RenderWebElement
    @FindBy (css = "textarea[id*='description']")
    private WebElement descriptionInputField;

    @RenderWebElement
    @FindBy (css = "input[id*='ruleConfigIfCondition']")
    private WebElement ifCheckbox;

    @FindBy (css = ".main-buttons button[id*='create-button']")
    private WebElement createButton;

    @FindBy (css = ".edit-buttons button[id*='save-button']")
    private WebElement saveButton;

    @FindBy (css = ".main-buttons button[id*='createAnother']")
    private WebElement createAndCreateAnotherButton;

    @FindBy (css = ".main-buttons button[id*='cancel']")
    private WebElement cancelButton;

    @FindBy (css = ".disabled input")
    private WebElement disableRuleCheckbox;

    @FindBy (css = "input[id*='default-applyToChildren']")
    private WebElement ruleAppliesToSubfoldersCheckbox;

    @FindBy (css = "input[id*='ruleConfigUnlessCondition']")
    private WebElement unlessCheckbox;

    private String dropdownSelector = "div[id*='%s'] select[class='config-name']";

    private String secondDropdownSelector = "div[id*='%s'] select[class='suppress-validation']";

    private String inputConfigText = "ul[id*='%s'] input[type='text']";

    private By ifConditionCompareSelector = By.cssSelector("div[id*='ruleConfigIfCondition'] span[class*='compare-property'] select");

    private By copySelectButtonSelector = By.cssSelector("div[id*='ruleConfigAction'] .parameters button");

    private ArrayList<String> selectedValues = new ArrayList<>();

    private By aspectDropdownList = By.cssSelector("select[title='aspect-name']>option");

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/rule-edit", getCurrentSiteName());
    }

    public String getRulePageHeader()
    {
        return pageHeader.getText();
    }

    public void typeName(String name)
    {
        nameInputField.clear();
        nameInputField.sendKeys(name);
    }

    public void typeDescription(String description)
    {
        descriptionInputField.clear();
        descriptionInputField.sendKeys(description);
    }

    public void clickIfCheckbox()
    {
        browser.waitUntilElementClickable(ifCheckbox, 40).click();
    }

    /**
     * @param dropdownId    used for dropdownSelector:
     *                      = "ruleConfigType" for "When" dropdown;
     *                      = "ruleConfigIfCondition" for "If all criteria are met" dropdown;
     *                      = "ruleConfigAction" for "Perform Action" dropdown
     * @param indexOfOption index of the option to be selected from dropdown (index starting from 0)
     */
    public void selectOptionFromDropdown(String dropdownId, int indexOfOption)
    {
        Select dropdown = new Select(browser.findElement(By.cssSelector(String.format(dropdownSelector, dropdownId))));
        dropdown.selectByIndex(indexOfOption);
        selectedValues.add(dropdown.getFirstSelectedOption().getText());
    }

    public void selectOptionFromSecondDropdown(String dropdownId, int indexOfOption)
    {
        Select dropdown = new Select(browser.findElement(By.cssSelector(String.format(secondDropdownSelector, dropdownId))));
        dropdown.selectByIndex(indexOfOption);
        selectedValues.add(dropdown.getFirstSelectedOption().getText());
    }

    /**
     * @return list of selected dropdown values=
     * index -> corresponding dropdown:
     * 0 -> "When";
     * 1 -> "If all criteria are met"
     */
    public ArrayList<String> getSelectedOptionFromDropdown()
    {
        return selectedValues;
    }

    /**
     * Select comparison operator option from "If all criteria are met" dropdown = "Size"
     *
     * @param indexOfOption index of the option to be selected from dropdown (index starting from 0)
     */
    public void selectOptionFromIfConditionCompareOperator(int indexOfOption)
    {
        Select dropdown = new Select(browser.findElement(ifConditionCompareSelector));
        dropdown.selectByIndex(indexOfOption);
        selectedValues.add(dropdown.getFirstSelectedOption().getText());
    }

    /**
     * Clear selectedValues list, list of selected values from all dropdown.
     * This method needs to be called after each verification of displayed details of rule
     */
    public void cleanupSelectedValues()
    {
        selectedValues.clear();
    }

    /**
     * Click "Select" button from "Perform Action" section -> "Copy" option
     */
    public void clickCopySelectButton()
    {
        browser.waitUntilElementClickable(copySelectButtonSelector, 40).click();
    }

    public void typeRuleDetails(String ruleName, String description, List<Integer> indexOfOptionFromDropdown)
    {
        this.renderedPage();
        typeName(ruleName);
        typeDescription(description);
        selectOptionFromDropdown("ruleConfigType", indexOfOptionFromDropdown.get(0));
        selectOptionFromDropdown("ruleConfigIfCondition", indexOfOptionFromDropdown.get(1));
        selectOptionFromDropdown("ruleConfigAction", indexOfOptionFromDropdown.get(2));
        if (indexOfOptionFromDropdown.get(2).equals(2))
            clickCopySelectButton();
    }

    public void clickDisableRuleCheckbox()
    {
        disableRuleCheckbox.click();
    }

    public void clickRulesAppliesToSubfoldersCheckbox()
    {
        browser.waitUntilElementVisible(ruleAppliesToSubfoldersCheckbox).click();
    }

    public RuleDetailsPage clickCreateButton()
    {
        getBrowser().waitUntilElementClickable(createButton, 10L).click();
        return (RuleDetailsPage) ruleDetailsPage.renderedPage();
    }

    public EditRulesPage clickCreateAndCreateAnotherButton()
    {
        createAndCreateAnotherButton.click();
        browser.refresh();
        return (EditRulesPage) this.renderedPage();
    }

    public RuleDetailsPage clickSaveButton()
    {
        saveButton.click();
        return (RuleDetailsPage) ruleDetailsPage.renderedPage();
    }

    /**
     * @param dropdownId used for dropdownSelector:
     *                   = "ruleConfigType" for "When" dropdown;
     *                   = "ruleConfigIfCondition" for "If all criteria are met" dropdown;
     *                   = "ruleConfigAction" for "Perform Action" dropdown
     */
    public ArrayList<String> verifyDropdownOptions(String dropdownId, ArrayList<String> expectedOptionsList)
    {
        Select dropdown = new Select(browser.findElement(By.cssSelector(String.format(dropdownSelector, dropdownId))));
        List<WebElement> options = dropdown.getOptions();
        ArrayList<String> optionsTextList = new ArrayList<>();
        for (int i = 0; i < options.size(); i++)
        {
            String optionText = options.get(i).getText();
            assertEquals(optionText, expectedOptionsList.get(i), dropdownId + " option");
        }
        return optionsTextList;
    }

    public void selectAspect(String aspectName)
    {
        browser.selectOptionFromFilterOptionsList(aspectName, browser.findElements(aspectDropdownList));
    }

    /**
     * Click on checkbox  "Unless all criteria are met:".
     */
    public void clickUnlessCheckbox()
    {
        browser.waitUntilElementClickable(unlessCheckbox).click();
    }

    /**
     * Verify if "Unless all criteria are met:" is checked.
     *
     * @return true if is checked.
     */
    public boolean isUnlessCheckboxSelected()
    {
        return unlessCheckbox.isSelected();
    }

    /**
     * Method typeInputConfigText used for write the condtion on "Unless all criteria are met:" textbox.
     *
     * @param textBoxId used for
     */
    public void typeInputConfigText(String textBoxId, String condition)
    {
        WebElement element = browser.findElement(By.cssSelector(String.format(inputConfigText, textBoxId)));
        element.clear();
        element.sendKeys(condition);

    }

    public String getInputConfigText(String textBoxId)
    {
        WebElement element = browser.findElement(By.cssSelector(String.format(inputConfigText, textBoxId)));
        return element.getAttribute("value");
    }
}