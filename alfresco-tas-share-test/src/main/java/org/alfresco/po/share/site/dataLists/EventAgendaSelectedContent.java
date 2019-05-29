package org.alfresco.po.share.site.dataLists;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class EventAgendaSelectedContent extends ListItemSelectedContent
{
    EventAgendaItemsTable tableRow;
    private By noListItems = By.cssSelector("div[id$='default-grid'] table tbody tr");
    private By listItems = By.cssSelector("div[id$='default-grid'] table tbody[class='yui-dt-data'] tr");

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
            tableRow = new EventAgendaItemsTable(row, browser);
            if (tableRow.getReferenceColumn().getText().equals(listDetails.get(i)))
            {

                if (tableRow.getStartTimeColumn().getText().equals(listDetails.get(i)))
                {
                    if (tableRow.getEndTimeColumn().getText().equals(listDetails.get(i)))
                    {

                        if (tableRow.getSessionNameColumn().getText().equals(listDetails.get(i)))
                        {
                            if (tableRow.getPresenterColumn().getText().equals(listDetails.get(i)))
                            {

                                if (tableRow.getAudienceColumn().getText().equals(listDetails.get(i)))
                                {

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
            i++;
        }
        return found;
    }

    public void editItem(List<String> listDetails)
    {
        tableRow = new EventAgendaItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getEditButton().click();
    }

    public void duplicateItem(List<String> listDetails)
    {
        tableRow = new EventAgendaItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getDuplicateButton().click();
    }

    public void deleteItem(List<String> listDetails)
    {
        tableRow = new EventAgendaItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getDeleteButton().click();
    }

    public enum EventAgendaColumns
    {
        Reference, Presenter, Audience, Attachments, Notes, Actions
    }
}
