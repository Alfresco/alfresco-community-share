package org.alfresco.po.share.site.dataLists;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LocationListSelectedContent extends ListItemSelectedContent
{

    LocationListItemsTable tableRow;
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
        for (WebElement row : rows)
        {
            int i = 0;
            tableRow = new LocationListItemsTable(row, browser);
            if (!tableRow.getTitleColumn().getText().equals(listDetails.get(i)))
                break;
            i++;
            if (!tableRow.getAddress1Column().getText().equals(listDetails.get(i)))
                break;
            i++;
            if (!tableRow.getAddress2Column().getText().equals(listDetails.get(i)))
                break;
            i++;
            if (!tableRow.getAddress3Column().getText().equals(listDetails.get(i)))
                break;
            i++;
            if (!tableRow.getZipCodeColumn().getText().equals(listDetails.get(i)))
                break;
            i++;
            if (!tableRow.getStateColumn().getText().equals(listDetails.get(i)))
                break;
            i++;
            if (!tableRow.getCountryColumn().getText().equals(listDetails.get(i)))
                break;
            i++;
            if (!tableRow.getDescriptionColumn().getText().equals(listDetails.get(i)))
                break;
            i++;
            if (!tableRow.getAttachmentsColumn().getText().equals(listDetails.get(i)))
                break;
            found.add(row);
        }
        return found;
    }

    public void editItem(List<String> listDetails)
    {
        tableRow = new LocationListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getEditButton().click();
    }

    public void duplicateItem(List<String> listDetails)
    {
        tableRow = new LocationListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getDuplicateButton().click();
    }

    public void deleteItem(List<String> listDetails)
    {
        tableRow = new LocationListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getDeleteButton().click();
    }
}
