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

public class ContactListSelectedContentPage extends BaseDialogComponent {
    private By noListItems = By.cssSelector("div[id$='default-grid'] table tbody tr");
    private By listItems = By.cssSelector("div[id$='default-grid'] table tbody[class='yui-dt-data'] tr");
    private String selectColumn = "div[id$='default-grid'] table thead tr div[id*='%s'] span a";
    private String selectedColumnItems = "table tbody td[class*='%s']";
    DataListsPage dataListsPage;
    ContactListItemsTable tableRow;
    protected WebBrowser browser;
    public ContactListSelectedContentPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public boolean isTableHeaderComplete()
    {
        dataListsPage = new DataListsPage(webDriver);
        waitInSeconds(3);
        return isEnumContainedByList(ContactListColumns.class, dataListsPage.getTextOfTableColumnHeader());
    }
    public boolean isListItemDisplayed(List<String> listDetails)
    {
        return findRow(listDetails) != null;
    }
    public List<WebElement> findRow(List<String> listDetails)
    {
        waitUntilElementIsDisplayedWithRetry(listItems);
        List<WebElement> rows = findElements(listItems);
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
    public enum ContactListColumns
    {
        FirstName("First Name"),
        LastName("Last Name"),
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
