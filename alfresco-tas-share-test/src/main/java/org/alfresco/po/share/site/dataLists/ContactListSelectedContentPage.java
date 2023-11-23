package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.web.browser.WebBrowser;
import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.common.DataUtil.isEnumContainedByList;

public class ContactListSelectedContentPage extends BaseDialogComponent {
    private By noListItems = By.cssSelector("div[id$='default-grid'] table tbody tr");
    private By listItems = By.cssSelector("div[id$='default-grid'] table tbody[class='yui-dt-data'] tr");
    private String selectColumn = "div[id$='default-grid'] table thead tr div[id*='%s'] span a";
    private String selectedColumnItems = "table tbody td[class*='%s']";

    private By selectButtonSelector = By.cssSelector("button[id*='itemSelect']");
    private By invertSelectionButtonOption = By.cssSelector(".datagrid-bar.flat-button .selectInvert");
    private By selectAllButtonOption = By.cssSelector(".datagrid-bar.flat-button .selectAll");
    private By selectNoneButtonOption = By.cssSelector(".datagrid-bar.flat-button .selectNone");
    private By itemDataTable = By.cssSelector("div[class='grid yui-dt'] table");
    protected By deselectAllItemsSelector = By.cssSelector("span[class='onActionDeselectAll']");
    protected By confirmDeleteButtonSelector = By.cssSelector("span[class*='alf-primary-button'] button");
    protected By cancelDeleteButtonSelector = By.cssSelector("span[class*='yui-push-button default'] button");
    protected By duplicateItemsSelector = By.cssSelector("span[class='onActionDuplicate']");
    protected By deleteItemsSelector = By.cssSelector("span[class='onActionDelete']");
    protected By selectItemsButtonSelector = By.cssSelector("button[id*='selectedItems']");

    DataListsPage dataListsPage;
    ContactListItemsTable tableRow;
    protected WebBrowser browser;
    public ContactListSelectedContentPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public boolean isTableHeaderComplete()
    {
        dataListsPage = new DataListsPage(webDriver);
        waitInSeconds(3);
        return isEnumContainedByList(ContactListColumns.class, dataListsPage.getTextOfTableColumnHeader());
    }
    public boolean isListItemDisplayed(List<String> listDetails)
    {
        return findRow(listDetails) != null;
    }
    public List<WebElement> findRow(List<String> listDetails)
    {
        waitUntilElementIsDisplayedWithRetry(listItems);
        List<WebElement> rows = findElements(listItems);
        List<WebElement> found = new ArrayList<>();
        for (WebElement row : rows)
        {
            List<String> rowValues = new ArrayList<>();

            tableRow = new ContactListItemsTable(row, browser);

            rowValues.add(tableRow.getFirstNameColumn().getText());
            rowValues.add(tableRow.getLastNameColumn().getText());
            rowValues.add(tableRow.getEmailColumn().getText());
            rowValues.add(tableRow.getCompanyColumn().getText());
            rowValues.add(tableRow.getJobTitleColumn().getText());
            rowValues.add(tableRow.getOfficePhoneColumn().getText());
            rowValues.add(tableRow.getMobilePhoneColumn().getText());
            rowValues.add(tableRow.getNotesColumn().getText());

            if (CollectionUtils.isEqualCollection(rowValues, listDetails))
            {
                found.add(row);
            }
        }
        return found;
    }
    public enum ContactListColumns
    {
        FirstName("First Name"),
        LastName("Last Name"),
        Email("Email"),
        Company("Company"),
        JobTitle("Job Title"),
        PhoneOffice("Phone (Office)"),
        PhoneMobile("Phone (Mobile)"),
        Notes("Notes"),
        Actions("Actions");

        public final String name;

        ContactListColumns(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return this.name;
        }
    }

    public boolean isSelectButtonDisplayed()
    {
        return isElementDisplayed(findElement(selectButtonSelector));
    }

    public boolean isInvertSelectionButtonOptionEnabled()
    {
        return findElement(invertSelectionButtonOption).isEnabled();
    }

    public boolean isSelectAllButtonOptionDisplayed()
    {
        return findElement(selectAllButtonOption).isEnabled();
    }

    public boolean isSelectNoneButtonOptionDisplayed()
    {
        return findElement(selectNoneButtonOption).isEnabled();
    }

    public void clickSelectButton()
    {
       waitUntilElementClickable(itemDataTable);
       findElement(selectButtonSelector).click();
    }

    public void clickSelectAllOption()
    {
        clickSelectButton();
        waitUntilElementClickable(selectAllButtonOption);
        findElement(selectAllButtonOption).click();
    }

    public void clickInvertSelectionOption()
    {
        clickSelectButton();
        waitUntilElementClickable(invertSelectionButtonOption);
        findElement(invertSelectionButtonOption).click();
    }

    public void clickSelectNoneOption()
    {
        clickSelectButton();
        waitUntilElementClickable(invertSelectionButtonOption);
        findElement(selectNoneButtonOption).click();
    }

    public boolean isItemChecked(List<String> listDetails)
    {
        tableRow = new ContactListItemsTable(findRow(listDetails).get(0), browser);
        return tableRow.getCheckBoxColumn().isSelected();
    }

    public boolean isSelectItemsButtonDisplayed()
    {
        return findElement(selectItemsButtonSelector).isDisplayed();
    }

    public boolean isSelectItemsButtonEnabled()
    {
        return findElement(selectItemsButtonSelector).isEnabled();
    }

    public void clickSelectedItemsButton()
    {
        findElement(selectItemsButtonSelector).click();
    }

    public boolean isDuplicateItemsDisplayed()
    {
        return findElement(duplicateItemsSelector).isDisplayed();
    }

    public boolean isDeleteSelectedItemsDisplayed()
    {
        return findElement(deleteItemsSelector).isDisplayed();
    }

    public boolean isDeselectSelectedItemsDisplayed()
    {
        return findElement(deselectAllItemsSelector).isDisplayed();
    }

    public void clickDuplicateItemsOption()
    {
        clickSelectedItemsButton();
        findElement(duplicateItemsSelector).click();
    }

    public List<String> returnNumberOfCheckedAndUncheckedItems(List<String> listDetails)
    {
        int checkedItems = 0;
        int uncheckedItems = 0;
        List<String> results = new ArrayList<>();
        List<WebElement> rows = findRow(listDetails);
        for (WebElement row : rows)
        {
            tableRow = new ContactListItemsTable(row, browser);
            if (tableRow.getCheckBoxColumn().isSelected())
                checkedItems++;
            else
                uncheckedItems++;
        }
        results.add(Integer.toString(checkedItems));
        results.add(Integer.toString(uncheckedItems));

        return results;
    }

    public void clickDeleteItemsOption(boolean delete)
    {
        clickSelectedItemsButton();
        findElement(deleteItemsSelector).click();
        if (delete)
            clickConfirmDelete();
        else
            clickCancelDelete();
    }

    public void clickConfirmDelete()
    {
        findElement(confirmDeleteButtonSelector).click();
    }

    public void clickCancelDelete()
    {
        findElement(cancelDeleteButtonSelector).click();
    }

    public void clickDeselectAllItemsOption()
    {
        clickSelectedItemsButton();
        findElement(deselectAllItemsSelector).click();
    }

    public boolean duplicatedRows(List<String> listDetails)
    {
        return findRow(listDetails).size() > 1;
    }

}
