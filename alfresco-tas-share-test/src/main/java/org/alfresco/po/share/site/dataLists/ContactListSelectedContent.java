package org.alfresco.po.share.site.dataLists;

import static org.alfresco.common.DataUtil.isEnumContainedByList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.utility.web.annotation.PageObject;
import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class ContactListSelectedContent extends ListItemSelectedContent
{
    protected static By balloon = By.cssSelector("div[class='bd'] span[class='message']");
    @FindAll (@FindBy (css = "div[id$='default-grid'] th span"))
    protected List<WebElement> tableColumnHeader;
    //@Autowired
    DataListsPage dataListsPage;
    ContactListItemsTable tableRow;
    @FindBy (css = "div[class='grid yui-dt'] table")
    private WebElement table;
    private By noListItems = By.cssSelector("div[id$='default-grid'] table tbody tr");
    private By listItems = By.cssSelector("div[id$='default-grid'] table tbody[class='yui-dt-data'] tr");
    private String selectColumn = "div[id$='default-grid'] table thead tr div[id*='%s'] span a";
    private String selectedColumnItems = "table tbody td[class*='%s']";

    /**
     * This method is checking if all the columns that should be in 'Contact List' table are actually displayed.
     * Checking if all the elements from the enum list exists in getTextOfTableColumnHeader() list method.
     *
     * @return - true if all the elements from the enum are displayed in 'New Item' table.
     * - false if there is at least one element missing.
     */
    public boolean isTableHeaderComplete()
    {
        return isEnumContainedByList(ContactListColumns.class, dataListsPage.getTextOfTableColumnHeader());
    }

    private void clickOnSpecificColumn(String column)
    {
        browser.findElement(By.cssSelector(String.format(selectColumn, column))).click();
        browser.waitInSeconds(2);
    }

    private List<String> retrieveSpecificColumnItems(String column)
    {
        browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector(String.format(selectedColumnItems, column)));
        List<WebElement> items = browser.findElements(By.cssSelector(String.format(selectedColumnItems, column)));
        List<String> text = new ArrayList<>(items.size());
        for (WebElement item : items)
        {
            text.add(item.getText());
        }
        return text;
    }

    public boolean areItemsSortedByColumnAfterClickingTheColumn(String column)
    {
        List<String> currentOrder = retrieveSpecificColumnItems(column);
        Collections.sort(currentOrder);
        clickOnSpecificColumn(column);
        return currentOrder.equals(retrieveSpecificColumnItems(column));
    }

    public boolean isAnyListItemDisplayed()
    {
        List<WebElement> listItemsList = browser.findElements(noListItems);
        return !(listItemsList.size() == 1 && listItemsList.get(0).findElement(By.cssSelector("td div")).getText().equals("No list items"));
    }

    public boolean isListItemDisplayed(List<String> listDetails)
    {
        return findRow(listDetails) != null;
    }

    public boolean duplicatedRows(List<String> listDetails)
    {
        return findRow(listDetails).size() > 1;
    }

    public List<WebElement> findRow(List<String> listDetails)
    {
        browser.waitUntilElementIsDisplayedWithRetry(listItems);
        List<WebElement> rows = browser.findElements(listItems);
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

    public void editItem(List<String> listDetails)
    {
        tableRow = new ContactListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getEditButton().click();
    }

    public void duplicateItem(List<String> listDetails)
    {
        tableRow = new ContactListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getDuplicateButton().click();
    }

    public void deleteItem(List<String> listDetails)
    {
        tableRow = new ContactListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getDeleteButton().click();
    }

    public boolean isItemChecked(List<String> listDetails)
    {
        tableRow = new ContactListItemsTable(findRow(listDetails).get(0), browser);
        return tableRow.getCheckBoxColumn().isSelected();
    }

    /**
     * Method verifies all the items from a table with that value and
     * returns the number of checked and unchecked items
     *
     * @param listDetails
     * @return results(0) - number of checked items / results(1) - number of unchecked items
     */
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

    public enum ContactListColumns
    {
        FirstName("FirstName"),
        LastName("LastName"),
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

}
