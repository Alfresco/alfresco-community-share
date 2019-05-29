package org.alfresco.po.share.site.dataLists;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PageObject
public class ContactListSelectedContent extends ListItemSelectedContent
{
    @RenderWebElement
    @FindBy (css = "div[id$='default-grid'] table thead tr")
    private List<WebElement> listItemsTableHead;

    private By noListItems = By.cssSelector("div[id$='default-grid'] table tbody tr");
    private By listItems = By.cssSelector("div[id$='default-grid'] table tbody[class='yui-dt-data'] tr");
    private String selectColumn = "div[id$='default-grid'] table thead tr div[id*='%s'] span a";
    private String selectedColumnItems = "table tbody td[class*='%s']";
    protected static By balloon = By.cssSelector("div[class='bd'] span[class='message']");
    ContactListItemsTable tableRow;

    public enum ListColumns
    {
        Email, Company, Notes, Actions
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
            int i = 0;
            tableRow = new ContactListItemsTable(row, browser);
            if (tableRow.getFirstNameColumn().getText().equals(listDetails.get(i)))
            {
                i++;
                if (tableRow.getLastNameColumn().getText().equals(listDetails.get(i)))
                {
                    i++;
                    if (tableRow.getEmailColumn().getText().equals(listDetails.get(i)))
                    {
                        i++;
                        if (tableRow.getCompanyColumn().getText().equals(listDetails.get(i)))
                        {
                            i++;
                            if (tableRow.getJobTitleColumn().getText().equals(listDetails.get(i)))
                            {
                                i++;
                                if (tableRow.getOfficePhoneColumn().getText().equals(listDetails.get(i)))
                                {
                                    i++;
                                    if (tableRow.getMobilePhoneColumn().getText().equals(listDetails.get(i)))
                                    {
                                        i++;
                                        if (tableRow.getNotesColumn().getText().equals(listDetails.get(i)))
                                        {
                                            found.add(row);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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

}
