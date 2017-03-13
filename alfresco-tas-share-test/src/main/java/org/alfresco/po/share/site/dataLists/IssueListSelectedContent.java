package org.alfresco.po.share.site.dataLists;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class IssueListSelectedContent extends ListItemSelectedContent
{

    private By noListItems = By.cssSelector("div[id$='default-grid'] table tbody tr");
    private By listItems = By.cssSelector("div[id$='default-grid'] table tbody[class='yui-dt-data'] tr");
    IssueListItemsTable tableRow;

    public boolean isAnyListItemDisplayed()
    {
        List<WebElement> listItemsList = browser.findElements(noListItems);
        if (listItemsList.size() == 1 && listItemsList.get(0).findElement(By.cssSelector("td div")).getText().equals("No list items"))
            return false;
        return true;
    }

    public boolean isListItemDisplayed(List<String> listDetails)
    {
        if (findRow(listDetails) != null)
        {
            return true;
        }
        return false;
    }

    public boolean duplicatedRows(List<String> listDetails)
    {
        if (findRow(listDetails).size() > 1)
        {
            return true;
        }
        return false;
    }

    public List<WebElement> findRow(List<String> listDetails)
    {
        browser.waitUntilElementIsDisplayedWithRetry(listItems);
        List<WebElement> rows = browser.findElements(listItems);
        List<WebElement> found = new ArrayList<WebElement>();
        for (WebElement row : rows)
        {
            int i = 0;
            tableRow = new IssueListItemsTable(row);
            if (tableRow.getIssueIDColumn().getText().equals(listDetails.get(i)))
            {
                if (tableRow.getTitleColumn().getText().equals(listDetails.get(i)))
                {
                    if (tableRow.getAssignedToColumn().getText().equals(listDetails.get(i)))
                    {
                        if (tableRow.getStatusColumn().getText().equals(listDetails.get(i)))
                        {
                            if (tableRow.getPriorityColumn().getText().equals(listDetails.get(i)))
                            {
                                if (tableRow.getDescriptionColumn().getText().equals(listDetails.get(i)))
                                {
                                    if (tableRow.getDueDateColumn().getText().equals(listDetails.get(i)))
                                    {
                                        if (tableRow.getCommentsColumn().getText().equals(listDetails.get(i)))
                                        {
                                            if (tableRow.getAttachmentsColumn().getText().equals(listDetails.get(i)))
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
            i++;
        }
        return found;
    }

    public void editItem(List<String> listDetails)
    {
        tableRow = new IssueListItemsTable(findRow(listDetails).get(0));
        tableRow.getEditButton().click();
    }

    public void duplicateItem(List<String> listDetails)
    {
        tableRow = new IssueListItemsTable(findRow(listDetails).get(0));
        tableRow.getDuplicateButton().click();
    }

    public void deleteItem(List<String> listDetails)
    {
        tableRow = new IssueListItemsTable(findRow(listDetails).get(0));
        tableRow.getDeleteButton().click();
    }
}
