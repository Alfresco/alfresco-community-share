package org.alfresco.po.share.site.dataLists;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class EventListSelectedContent extends ListItemSelectedContent
{

    EventListItemsTable tableRow;
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
            tableRow = new EventListItemsTable(row, browser);
            if (tableRow.getTitleColumn().getText().equals(listDetails.get(i)))
            {

                if (tableRow.getDescriptionColumn().getText().equals(listDetails.get(i)))
                {

                    if (tableRow.getLocationColumn().getText().equals(listDetails.get(i)))
                    {

                        if (tableRow.getStartDateColumn().getText().equals(listDetails.get(i)))
                        {

                            if (tableRow.getEndDateColumn().getText().equals(listDetails.get(i)))
                            {

                                if (tableRow.getRegistrationsColumn().getText().equals(listDetails.get(i)))
                                {

                                    if (tableRow.getAttachmentsColumn().getText().equals(listDetails.get(i)))
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
            }
            i++;
        }
        return found;
    }

    public void editItem(List<String> listDetails)
    {
        tableRow = new EventListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getEditButton().click();
    }

    public void duplicateItem(List<String> listDetails)
    {
        tableRow = new EventListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getDuplicateButton().click();
    }

    public void deleteItem(List<String> listDetails)
    {
        tableRow = new EventListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getDeleteButton().click();
    }

    public enum EventListColumns
    {
        Tittle("cm_title"),
        Location("eventLocation"),
        StartDate("eventStartDate"),
        EndDate("eventEndDate"),
        Description("cm_description"),
        Registrations("eventRegistrations"),
        EventNote("eventNote");

        public final String name;

        EventListColumns(String name)
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
