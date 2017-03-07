package org.alfresco.po.share.site.dataLists;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@PageObject
public class ToDoListSelectedContent extends ListItemSelectedContent
{
    
    private By listItems = By.cssSelector("div[id$='default-grid'] table tbody[class='yui-dt-data'] tr");
    ToDoListItemsTable tableRow;
    
    public boolean isListItemDisplayed(List<String> listDetails)
    {
        if(findRow(listDetails)!=null)
        {
            return true;
        }
        return false;
    }
    
    public boolean duplicatedRows(List<String> listDetails)
    {
        if(findRow(listDetails).size()>1)
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
        for(WebElement row : rows)
        {
            int i=0;
            tableRow = new ToDoListItemsTable(row, browser);
            if(!tableRow.getTitleColumn().getText().contains(listDetails.get(i)))
                break;
            i++;
//            if(!tableRow.getDueDateColumn().getText().contains(listDetails.get(i)))
//                break;
//            i++;
            if(!tableRow.getPriorityColumn().getText().contains(listDetails.get(i)))
                break;
            i++;
            if(!tableRow.getStatusColumn().getText().contains(listDetails.get(i)))
                break;
            i++;
            if(!tableRow.getAssigneeColumn().getText().contains(listDetails.get(i)))
                break;
            i++;
            if(!tableRow.getAttachmentsColumn().getText().contains(listDetails.get(i)))
                break;
            found.add(row);
        }
        return found;
    }
    
    public void editItem(List<String> listDetails)
    {
        tableRow = new ToDoListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getEditButton().click();
    }
    
    public void duplicateItem(List<String> listDetails)
    {
        tableRow = new ToDoListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getDuplicateButton().click();
    }
    
    public void deleteItem(List<String> listDetails)
    {
        tableRow = new ToDoListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getDeleteButton().click();
    }
    
}
