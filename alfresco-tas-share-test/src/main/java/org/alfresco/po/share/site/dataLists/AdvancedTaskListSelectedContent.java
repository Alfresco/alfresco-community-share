package org.alfresco.po.share.site.dataLists;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AdvancedTaskListSelectedContent extends ListItemSelectedContent
{
    AdvancedTaskListItemsTable tableRow;
    private By listItems = By.cssSelector("div[id$='default-grid'] table tbody[class='yui-dt-data'] tr");

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
        for (int i = 0; i < rows.size(); i++)
        {
            WebElement row = rows.get(i);
            tableRow = new AdvancedTaskListItemsTable(row, browser);
            if (tableRow.getTitleColumn().getText().equals(listDetails.get(i)))
            {
                if (tableRow.getDescriptionColumn().getText().equals(listDetails.get(i)))
                {
                    if (tableRow.getStartDateColumn().getText().equals(listDetails.get(i)))
                    {
                        if (tableRow.getEndDateColumn().getText().equals(listDetails.get(i)))
                        {
                            if (tableRow.getAssigneeColumn().getText().equals(listDetails.get(i)))
                            {
                                if (tableRow.getPriorityColumn().getText().equals(listDetails.get(i)))
                                {
                                    if (tableRow.getStatusColumn().getText().equals(listDetails.get(i)))
                                    {
                                        if (tableRow.getCompleteColumn().getText().equals(listDetails.get(i)))
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
            }
            i++;
        }
        return found;
    }

    public void editItem(List<String> listDetails)
    {
        tableRow = new AdvancedTaskListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getEditButton().click();
    }

    public void duplicateItem(List<String> listDetails)
    {
        tableRow = new AdvancedTaskListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getDuplicateButton().click();
    }

    public void deleteItem(List<String> listDetails)
    {
        tableRow = new AdvancedTaskListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getDeleteButton().click();
    }
}
