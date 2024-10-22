package org.alfresco.po.share.site.dataLists;

import static org.alfresco.common.DataUtil.isEnumContainedByList;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class EventAgendaSelectedContent extends ListItemSelectedContent
{
    //@Autowired
    DataListsPage dataListsPage;
    EventAgendaItemsTable tableRow;
    private By noListItems = By.cssSelector("div[id$='default-grid'] table tbody tr");
    private By listItems = By.cssSelector("div[id$='default-grid'] table tbody[class='yui-dt-data'] tr");

    /**
     * This method is checking if all the columns that should be in 'Event Agenda' table are actually displayed.
     * Checking if all the elements from the enum list exists in getTextOfTableColumnHeader() list method.
     *
     * @return - true if all the elements from the enum are displayed in 'Item List' table.
     * - false if there is at least one element missing.
     */
    public boolean isTableHeaderComplete()
    {
        return isEnumContainedByList(EventAgendaColumns.class, dataListsPage.getTextOfTableColumnHeader());
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
        Reference("Reference"),
        StartTime("Start Time"),
        EndTime("End Time"),
        SessionName("Session Name"),
        Presenter("Presenter"),
        Audience("Audience"),
        Attachments("Attachments"),
        Notes("Notes"),
        Actions("Actions");

        public final String name;

        EventAgendaColumns(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return this.name;
        }
    }

    public enum EventAgendaColumn {
        eventAgendaRef("Event Reference"),
        StartTime("StartTime"),
        SessionName("Session Name"),
        EndTime("EndTime");

        public final String name;

        EventAgendaColumn(String name)
        {
            this.name = name;
        }
    }
}
