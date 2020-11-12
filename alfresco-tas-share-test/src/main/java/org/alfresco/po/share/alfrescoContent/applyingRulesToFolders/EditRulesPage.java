package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.common.Utils;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class EditRulesPage extends SiteCommon<EditRulesPage>
{
    private DocumentLibraryPage documentLibraryPage;
    private ManageRulesPage manageRulesPage;
    private RuleDetailsPage ruleDetailsPage;

    private By pageHeader = By.cssSelector(".rule-edit .edit-header");

    @RenderWebElement
    private By nameInputField = By.cssSelector("input[id*='title']");
    @RenderWebElement
    private By descriptionInputField = By.cssSelector("textarea[id*='description']");
    @RenderWebElement
    private By ifCheckbox = By.cssSelector("input[id*='ruleConfigIfCondition']");
    private By createButton = By.cssSelector(".main-buttons button[id*='create-button']");
    private By saveButton = By.cssSelector(".edit-buttons button[id*='save-button']");
    private By createAndCreateAnotherButton = By.cssSelector(".main-buttons button[id*='createAnother']");
    private By cancelButton = By.cssSelector(".main-buttons button[id*='cancel']");
    private By disableRuleCheckbox = By.cssSelector(".disabled input");
    private By ruleAppliesToSubfoldersCheckbox = By.cssSelector("input[id*='default-applyToChildren']");
    private By unlessCheckbox = By.cssSelector("input[id*='ruleConfigUnlessCondition']");
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

    public EditRulesPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
        documentLibraryPage = new DocumentLibraryPage(browser);
        manageRulesPage = new ManageRulesPage(browser);
        ruleDetailsPage = new RuleDetailsPage(browser);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/rule-edit", getCurrentSiteName());
    }

    public String getRulePageHeader()
    {
        return getBrowser().findElement(pageHeader).getText();
    }

    public void typeName(String name)
    {
        clearAndType(getBrowser().findElement(nameInputField), name);
    }

    public void typeDescription(String description)
    {
        clearAndType(getBrowser().findElement(descriptionInputField), description);
    }

    public void clickIfCheckbox()
    {
        getBrowser().waitUntilElementClickable(ifCheckbox, 40).click();
    }

    public void selectOptionFromDropdown(String dropdownId, int indexOfOption)
    {
        Select dropdown = new Select(getBrowser().findElement(By.cssSelector(String.format(dropdownSelector, dropdownId))));
        dropdown.selectByIndex(indexOfOption);
        selectedValues.add(dropdown.getFirstSelectedOption().getText());
    }

    public void selectOptionFromSecondDropdown(String dropdownId, int indexOfOption)
    {
        Select dropdown = new Select(getBrowser().findElement(By.cssSelector(String.format(secondDropdownSelector, dropdownId))));
        dropdown.selectByIndex(indexOfOption);
        selectedValues.add(dropdown.getFirstSelectedOption().getText());
    }

    public ArrayList<String> getSelectedOptionFromDropdown()
    {
        return selectedValues;
    }

    public void selectOptionFromIfConditionCompareOperator(int indexOfOption)
    {
        Select dropdown = new Select(getBrowser().findElement(ifConditionCompareSelector));
        dropdown.selectByIndex(indexOfOption);
        selectedValues.add(dropdown.getFirstSelectedOption().getText());
    }

    public void cleanupSelectedValues()
    {
        selectedValues.clear();
    }

    public void clickCopySelectButton()
    {
        getBrowser().waitUntilElementClickable(copySelectButtonSelector, 40).click();
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
        getBrowser().findElement(disableRuleCheckbox).click();
    }

    public void clickRulesAppliesToSubfoldersCheckbox()
    {
        getBrowser().waitUntilElementVisible(ruleAppliesToSubfoldersCheckbox).click();
    }

    public RuleDetailsPage clickCreateButton()
    {
        getBrowser().waitUntilElementClickable(createButton, 10L).click();
        return (RuleDetailsPage) ruleDetailsPage.renderedPage();
    }

    public EditRulesPage clickCreateAndCreateAnotherButton()
    {
        getBrowser().findElement(createAndCreateAnotherButton).click();
        return (EditRulesPage) this.renderedPage();
    }

    public RuleDetailsPage clickSaveButton()
    {
        getBrowser().findElement(saveButton).click();
        return (RuleDetailsPage) ruleDetailsPage.renderedPage();
    }

    public ArrayList<String> verifyDropdownOptions(String dropdownId, ArrayList<String> expectedOptionsList)
    {
        Select dropdown = new Select(getBrowser().findElement(By.cssSelector(String.format(dropdownSelector, dropdownId))));
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
        getBrowser().selectOptionFromFilterOptionsList(aspectName, getBrowser().findElements(aspectDropdownList));
    }

    public void clickUnlessCheckbox()
    {
        getBrowser().waitUntilElementClickable(unlessCheckbox).click();
    }

    public boolean isUnlessCheckboxSelected()
    {
        return getBrowser().findElement(unlessCheckbox).isSelected();
    }

    public void typeInputConfigText(String textBoxId, String condition)
    {
        WebElement element = getBrowser().findElement(By.cssSelector(String.format(inputConfigText, textBoxId)));
        Utils.clearAndType(element, condition);
    }

    public String getInputConfigText(String textBoxId)
    {
        WebElement element = getBrowser().findElement(By.cssSelector(String.format(inputConfigText, textBoxId)));
        return element.getAttribute("value");
    }

    public void selectWhenDropDownCondition(WhenRule whenRuleName)
    {
        Select whenType = new Select(getBrowser().findElement(whenDropDown));
        whenType.selectByValue(whenRuleName.getValue());
    }

    public void selectIfDropDownCondition(IfAllCriteriaAreMetRule ifAllCriteriaAreMetRuleName)
    {
        Select ifType = new Select(getBrowser().findElement(ifAllCriteriaAreMetDropDown));
        ifType.selectByVisibleText(ifAllCriteriaAreMetRuleName.getName());
    }

    public void selectPerformActionDropDown(PerformActionList performActionName)
    {
        Select performAction = new Select(getBrowser().findElement(performActionDropDown));
        performAction.selectByValue(performActionName.getValue());
    }

    public void defineRule(String ruleName, String siteName, String sourceFolder, WhenRule whenRuleName, IfAllCriteriaAreMetRule ifAllCriteriaAreMetRuleName, PerformActionList performActionName) {
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(sourceFolder, ItemActions.MANAGE_RULES);
        manageRulesPage.clickCreateRules();
        typeName(ruleName);
        selectWhenDropDownCondition(whenRuleName);
        selectIfDropDownCondition(ifAllCriteriaAreMetRuleName);
        selectPerformActionDropDown(performActionName);
    }

    public enum WhenRule
    {
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