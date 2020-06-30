package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.common.Language;
import org.alfresco.common.Utils;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Laura.Capsa
 */
@PageObject
public class EditRulesPage extends SiteCommon<EditRulesPage>
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    ManageRulesPage manageRulesPage;

    @Autowired
    protected Language language;

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

    private By whenDropDown = By.cssSelector("div[id*=ruleConfigType] .config-name");
    private By ifAllCriteriaAreMetDropDown = By.cssSelector("div[id*=ruleConfigIfCondition] .config-name");
    private By performActionDropDown = By.cssSelector("div[id*=ruleConfigAction] .config-name");

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
        Utils.clearAndType(nameInputField, name);
    }

    public void typeDescription(String description)
    {
        Utils.clearAndType(descriptionInputField, description);
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
        Utils.clearAndType(element, condition);
    }

    public String getInputConfigText(String textBoxId)
    {
        WebElement element = browser.findElement(By.cssSelector(String.format(inputConfigText, textBoxId)));
        return element.getAttribute("value");
    }

    /**
     * Select value from `When` dropdown.
     *
     * @param whenRuleName  value from WhenRule enum representing the choice from dropdown
     */
    public void selectWhenDropDownCondition(WhenRule whenRuleName) {
        Select whenType = new Select(browser.findElement(whenDropDown));
        whenType.selectByValue(whenRuleName.getValue());
    }

    /**
     * Select value from `If all criteria are met` dropdown.
     *
     * @param ifAllCriteriaAreMetRuleName   value from IfAllCriteriaAreMetRule enum representing the condition from dropdown
     */
    public void selectIfDropDownCondition(IfAllCriteriaAreMetRule ifAllCriteriaAreMetRuleName) {
        Select ifType = new Select(browser.findElement(ifAllCriteriaAreMetDropDown));
        ifType.selectByVisibleText(ifAllCriteriaAreMetRuleName.getName());
    }

    /**
     * Select value from `Perform Action` dropdown
     *
     * @param performActionName value from PerformActionList enum representing the type of action for the rule from dropdown
     */
    public void selectPerformActionDropDown(PerformActionList performActionName) {
        Select performAction = new Select(browser.findElement(performActionDropDown));
        performAction.selectByValue(performActionName.getValue());
    }

    /**
     * Method that creates a rule for a folder
     *
     * @param ruleName                      the given name of the rule
     * @param siteName                      the site in which is the folder that will have the rule
     * @param sourceFolder                  the folder that will have the rule
     * @param whenRuleName                  value of When from WhenRule enum
     * @param ifAllCriteriaAreMetRuleName   value of If all criteria are met from IfAllCriteriaAreMetRule enum
     * @param performActionName             value of Perform Action from PerformActionList enum
     */
    public void defineRule(String ruleName, String siteName, String sourceFolder, WhenRule whenRuleName, IfAllCriteriaAreMetRule ifAllCriteriaAreMetRuleName, PerformActionList performActionName) {
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(sourceFolder, ItemActions.MANAGE_RULES, manageRulesPage);
        manageRulesPage.clickCreateRules();
        typeName(ruleName);
        selectWhenDropDownCondition(whenRuleName);
        selectIfDropDownCondition(ifAllCriteriaAreMetRuleName);
        selectPerformActionDropDown(performActionName);
    }

    /**
     * List of values from `When` dropdown
     */
    public enum WhenRule {
        itemsCreatedOrEnterFolder("Items are created or enter this folder", "inbound"),
        itemsUpdated("Items are updated", "update"),
        itemsDeletedOrLeaveFolder("Items are deleted or leave this folder", "outbound");

        private String name;
        private String value;

        WhenRule(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * List of values from `If All Criteria Are Met Rule` dropdown
     */
    public enum IfAllCriteriaAreMetRule {
        ALL_ITEMS("All Items", "no-condition", "condition"),
        SIZE("Size", "compare-property-value", "SIZE"),
        CREATED_DATE("Created Date", "compare-property-value", "created"),
        MODIFIED_DATE("Modified Date", "compare-property-value", "modified"),
        CREATOR("Creator", "compare-property-value", "creator"),
        MODIFIER("Modifier", "compare-property-value", "modifier"),
        AUTHOR("Author", "compare-property-value", "author"),
        MIMETYPE("Mimetype", "compare-mime-type", "MIME_TYPE"),
        ENCONDING("Encoding", "compare-property-value", "ENCODING"),
        DESCRIPTION("Description", "compare-property-value", "description"),
        NAME("Name", "compare-property-value", "name"),
        TITLE("Title", "compare-property-value", "title"),
        HAS_TAG("Has tag", "has-tag", "condition"),
        HAS_CATEGORY("Has category", "in-category", "condition"),
        CONTENT_TYPE_SUBTYPE("Content of type or sub-type", "is-subtype", "condition"),
        HAS_ASPECT("Has aspect", "has-aspect", "condition"),
        SHOW_MORE("Show more...", "show-more", "item");

        private String name;
        private String value;
        private String rel;

        IfAllCriteriaAreMetRule(String name, String value, String rel) {
            this.name = name;
            this.value = value;
            this.rel = rel;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public String getRel() {
            return rel;
        }
    }

    /**
     * List of values from `Perform Action` dropdown
     */
    public enum PerformActionList {
        SELECT("Select...", "select"),
        EXECUTE_SCRIPT("Execute script", "script"),
        COPY("Copy", "copy"),
        MOVE("Move", "move"),
        CHECK_IN("Check in", "check-in"),
        CHECK_OUT("Check out", "check-out"),
        LINK_TO_CATEGORY("Link to category", "link-category"),
        ADD_ASPECT("Add aspect", "add-features"),
        REMOVE_ASPECT("Remove aspect", "remove-features"),
        ADD_SIMPLE_WORKFLOW("Add simple workflow", "simple-workflow"),
        SEND_EMAIL("Send email", "mail"),
        TRANSFORM_AND_COPY_CONTENT("Transform and copy content", "transform"),
        TRANSFORM_AND_COPY_IMAGE("Transform and copy image", "transform-image"),
        EXTRACT_COMMON_METADATA_FIELDS("Extract common metadata fields", "extract-metadata"),
        IMPORT("Import", "import"),
        SPECIALISE_TYPE("Specialise type", "specialise-type"),
        INCREMENT_COUNTER("Increment Counter", "counter"),
        SET_PROPERTY_VALUE("Set property value", "set-property-value"),
        EMBED_PROPERTY_AS_METADATA_IN_CONTENT("Embed properties as metadata in content", "embed-metadata");

        private String name;
        private String value;

        PerformActionList(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}