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

public class MeetingAgendaListSelectedContentPage extends BaseDialogComponent {
    DataListsPage dataListsPage;
    MeetingAgendaListItemsTable tableRow;
    protected WebBrowser browser;
    private By listItems = By.cssSelector("div[id$='default-grid'] table tbody[class='yui-dt-data'] tr");
    public MeetingAgendaListSelectedContentPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
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
        waitUntilElementIsDisplayedWithRetry(listItems);
        List<WebElement> rows = findElements(listItems);
        List<WebElement> found = new ArrayList<>();
        for (WebElement row : rows)
        {
            List<String> rowValues = new ArrayList<>();

            tableRow = new MeetingAgendaListItemsTable(row, browser);

            rowValues.add(tableRow.getReferenceColumn().getText());
            rowValues.add(tableRow.getTitleColumn().getText());
            rowValues.add(tableRow.getDescriptionColumn().getText());
            rowValues.add(tableRow.getTimeColumn().getText());
            rowValues.add(tableRow.getOwnerColumn().getText());
            rowValues.add(tableRow.getAttachmentsColumn().getText());

            if (CollectionUtils.isEqualCollection(rowValues, listDetails))
            {
                found.add(row);
            }
        }
        return found;
    }

    public void editItem(List<String> listDetails)
    {
        tableRow = new MeetingAgendaListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getEditButton().click();
    }

    public void duplicateItem(List<String> listDetails)
    {
        tableRow = new MeetingAgendaListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getDuplicateButton().click();
    }

    public void deleteItem(List<String> listDetails)
    {
        tableRow = new MeetingAgendaListItemsTable(findRow(listDetails).get(0), browser);
        tableRow.getDeleteButton().click();
    }

    /**
     * This method is checking if all the columns that should be in 'Meeting Agenda' table are actually displayed.
     * Checking if all the elements from the enum list exists in getTextOfTableColumnHeader() list method.
     *
     * @return - true if all the elements from the enum are displayed in 'Item List' table.
     * - false if there is at least one element missing.
     */
    public boolean isTableHeaderComplete()
    {
        dataListsPage = new DataListsPage(webDriver);
        waitInSeconds(3);
        return isEnumContainedByList(MeetingAgendaListSelectedContent.MeetingAgendaColumns.class, dataListsPage.getTextOfTableColumnHeader());
    }


    public enum MeetingAgendaColumns
    {
        Reference("Reference"),
        Title("Title"),
        Description("Description"),
        TimeMins("Time (mins)"),
        Owner("Owner"),
        Attachments("Attachments"),
        Actions("Actions");

        public final String name;

        MeetingAgendaColumns(String name)
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
