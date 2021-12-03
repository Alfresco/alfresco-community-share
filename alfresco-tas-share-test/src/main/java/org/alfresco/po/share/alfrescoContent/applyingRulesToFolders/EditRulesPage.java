package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import static org.alfresco.common.Wait.WAIT_3;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

@Slf4j
public class EditRulesPage extends SiteCommon<EditRulesPage>
{

    private static final int THIRD_OPTION = 2;

    private final By pageHeader = By.cssSelector(".rule-edit .edit-header");
    private final By nameInputField = By.cssSelector("input[id*='title']");
    private final By descriptionInputField = By.cssSelector("textarea[id*='description']");
    private final By createButton = By.cssSelector(".main-buttons button[id*='create-button']");
    private final By cancelButton = By.cssSelector(".main-buttons button[id*='cancel-button']");
    private final By saveButton = By.cssSelector(".edit-buttons button[id*='save-button']");
    private final By createAndCreateAnotherButton = By.cssSelector(".main-buttons button[id*='createAnother']");
    private final By disableRuleCheckbox = By.cssSelector(".disabled input");
    private final By ruleAppliesToSubfoldersCheckbox = By.cssSelector("input[id*='default-applyToChildren']");
    private final By unlessCheckbox = By.cssSelector("input[id*='ruleConfigUnlessCondition']");
    private final By whenDropDown = By.cssSelector("div[id*=ruleConfigType] .config-name");
    private final By ifAllCriteriaAreMetDropDown = By.cssSelector("div[id*=ruleConfigIfCondition] .config-name");
    private final By performActionDropDown = By.cssSelector("div[id*=ruleConfigAction] .config-name");

    private final By copySelectButtonSelector = By.cssSelector("div[id*='ruleConfigAction'] .parameters button");
    private final ArrayList<String> selectedValues = new ArrayList<>();
    private final By aspectDropdownList = By.cssSelector("select[title='aspect-name']>option");

    private final String dropdownSelector = "div[id*='%s'] select[class='config-name']";
    private final String secondDropdownSelector = "div[id*='%s'] select[class='suppress-validation']";
    private final String inputConfigText = "ul[id*='%s'] input[type='text']";

    public EditRulesPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override public String getRelativePath()
    {
        return String.format("share/page/site/%s/rule-edit", getCurrentSiteName());
    }

    public String getRulePageHeader()
    {
        return getElementText(pageHeader);
    }

    public void typeName(String name)
    {
        waitInSeconds(WAIT_3.getValue());
        clearAndType(nameInputField, name);
    }

    public void typeDescription(String description)
    {
        clearAndType(descriptionInputField, description);
    }

    public void selectOptionFromDropdown(String dropdownId, int indexOfOption)
    {
        Select dropdown = new Select(
            findElement(By.cssSelector(String.format(dropdownSelector, dropdownId))));
        dropdown.selectByIndex(indexOfOption);
        selectedValues.add(dropdown.getFirstSelectedOption().getText());
    }

    public void selectOptionFromSecondDropdown(String dropdownId, int indexOfOption)
    {
        Select dropdown = new Select(
            findElement(By.cssSelector(String.format(secondDropdownSelector, dropdownId))));
        dropdown.selectByIndex(indexOfOption);
        selectedValues.add(dropdown.getFirstSelectedOption().getText());
    }

    public List<String> getSelectedOptionFromDropdown()
    {
        return selectedValues;
    }

    public void cleanupSelectedValues()
    {
        selectedValues.clear();
    }

    public void clickCopySelectButton()
    {
        clickElement(copySelectButtonSelector);
    }

    public EditRulesPage typeRuleDetails(String ruleName, String description, List<Integer> indexOfOptionFromDropdown)
    {
        typeName(ruleName);
        typeDescription(description);
        selectOptionFromDropdown("ruleConfigType", indexOfOptionFromDropdown.get(0));
        selectOptionFromDropdown("ruleConfigIfCondition", indexOfOptionFromDropdown.get(1));
        selectOptionFromDropdown("ruleConfigAction", indexOfOptionFromDropdown.get(2));

        if (indexOfOptionFromDropdown.get(THIRD_OPTION).equals(THIRD_OPTION))
        {
            clickCopySelectButton();
        }
        return this;
    }

    public void clickDisableRuleCheckbox()
    {
        clickElement(disableRuleCheckbox);
    }

    public void clickRulesAppliesToSubfoldersCheckbox()
    {
        clickElement(ruleAppliesToSubfoldersCheckbox);
    }

    public RuleDetailsPage clickCreateButton()
    {
        clickElement(createButton);
        return new RuleDetailsPage(webDriver);
    }

    public RuleDetailsPage clickCancelButton()
    {
        clickElement(cancelButton);
        return new RuleDetailsPage(webDriver);
    }

    public EditRulesPage clickCreateAndCreateAnotherButton()
    {
        clickElement(createAndCreateAnotherButton);
        return this;
    }

    public RuleDetailsPage clickSaveButton()
    {
        clickElement(saveButton);
        return new RuleDetailsPage(webDriver);
    }

    public List<String> verifyDropdownOptions(String dropdownId, List<String> expectedOptionsList)
    {
        Select dropdown = new Select(findElement(By.cssSelector(String.format(dropdownSelector, dropdownId))));
        List<WebElement> options = dropdown.getOptions();
        ArrayList<String> optionsTextList = new ArrayList<>();

        for (int i = 0; i < options.size(); i++)
        {
            String optionText = options.get(i).getText();
            assertEquals(optionText, expectedOptionsList.get(i), dropdownId + " option");
        }
        return optionsTextList;
    }

    public void clickUnlessCheckbox()
    {
        clickElement(unlessCheckbox);
    }

    public boolean isUnlessCheckboxSelected()
    {
        return findElement(unlessCheckbox).isSelected();
    }

    public void typeInputConfigText(String textBoxId, String condition)
    {
        WebElement element = findElement(By.cssSelector(String.format(inputConfigText, textBoxId)));
        clearAndType(element, condition);
    }

    public String getInputConfigText(String textBoxId)
    {
        WebElement element = findElement(By.cssSelector(String.format(inputConfigText, textBoxId)));
        return element.getAttribute("value");
    }

    public void selectWhenDropDownCondition(WhenRule whenRuleName)
    {
        Select whenType = new Select(findElement(whenDropDown));
        whenType.selectByValue(whenRuleName.getValue());
    }

    public void selectIfDropDownCondition(IfAllCriteriaAreMetRule ifAllCriteriaAreMetRuleName)
    {
        Select ifType = new Select(findElement(ifAllCriteriaAreMetDropDown));
        ifType.selectByVisibleText(ifAllCriteriaAreMetRuleName.getName());
    }

    public void selectPerformActionDropDown(PerformActionList performActionName)
    {
        Select performAction = new Select(findElement(performActionDropDown));
        performAction.selectByValue(performActionName.getValue());
    }

    public void defineRule(String ruleName, String siteName, String sourceFolder,
        WhenRule whenRuleName, IfAllCriteriaAreMetRule ifAllCriteriaAreMetRuleName,
        PerformActionList performActionName)
    {
        DocumentLibraryPage documentLibraryPage = new DocumentLibraryPage(webDriver);
        ManageRulesPage manageRulesPage = new ManageRulesPage(webDriver);

        documentLibraryPage.navigate(siteName);
        documentLibraryPage.selectItemAction(sourceFolder, ItemActions.MANAGE_RULES);
        manageRulesPage.openCreateNewRuleForm();
        typeName(ruleName);
        selectWhenDropDownCondition(whenRuleName);
        selectIfDropDownCondition(ifAllCriteriaAreMetRuleName);
        selectPerformActionDropDown(performActionName);
    }

    //todo: move into separate file
    public enum WhenRule
    {
        itemsCreatedOrEnterFolder("Items are created or enter this folder",
            "inbound"), itemsUpdated("Items are updated", "update"), itemsDeletedOrLeaveFolder(
        "Items are deleted or leave this folder", "outbound");

        private String name;
        private String value;

        WhenRule(String name, String value)
        {
            this.name = name;
            this.value = value;
        }

        public String getName()
        {
            return name;
        }

        public String getValue()
        {
            return value;
        }
    }

    //todo: move into separate file
    public enum IfAllCriteriaAreMetRule
    {
        ALL_ITEMS("All Items", "no-condition", "condition"),
        SIZE("Size", "compare-property-value", "SIZE"),
        CREATED_DATE("Created Date", "compare-property-value", "created"),
        MODIFIED_DATE("Modified Date", "compare-property-value", "modified"),
        CREATOR("Creator", "compare-property-value", "creator"),
        MODIFIER("Modifier", "compare-property-value", "modifier"),
        AUTHOR("Author", "compare-property-value", "author"),
        MIMETYPE("Mimetype", "compare-mime-type", "MIME_TYPE"),
        ENCODING("Encoding", "compare-property-value", "ENCODING"),
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

        IfAllCriteriaAreMetRule(String name, String value, String rel)
        {
            this.name = name;
            this.value = value;
            this.rel = rel;
        }

        public String getName()
        {
            return name;
        }

        public String getValue()
        {
            return value;
        }

        public String getRel()
        {
            return rel;
        }
    }

    /**
     * List of values from `Perform Action` dropdown
     */
    //todo:move into separate file
    public enum PerformActionList
    {
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

        PerformActionList(String name, String value)
        {
            this.name = name;
            this.value = value;
        }

        public String getName()
        {
            return name;
        }

        public String getValue()
        {
            return value;
        }
    }

    public EditRulesPage assertRulesPageHeaderEquals(String ruleName)
    {
        log.info("Verify Rules Page Header");
        assertEquals(getRulePageHeader(),
            String.format(language.translate("rules.editPageHeader"), ruleName), "Page header=");
        return this;
    }
}