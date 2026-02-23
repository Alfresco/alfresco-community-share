package org.alfresco.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.alfresco.utility.web.AbstractWebTest.getBrowser;

public class Table
{
    public Table()
    {
        PageFactory.initElements(getBrowser().getDriver(), this);
    }

    public List<List<WebElement>> getRows() {
        return getBrowser().findElements(By.xpath(".//tr")).stream().map((rowElement) -> rowElement.findElements(By.xpath(".//td"))).filter((row) -> row.size() > 0).collect(Collectors.toList());
    }

    public List<List<WebElement>> getColumns()
    {
        List<List<WebElement>> columns = new ArrayList<>();
        List<List<WebElement>> rows = this.getRows();
        if (rows.isEmpty()) {
            return columns;
        } else {
            int columnCount = ((List)rows.get(0)).size();

            for(int i = 0; i < columnCount; ++i) {
                List<WebElement> column = new ArrayList();

                for(List<WebElement> row : rows) {
                    column.add(row.get(i));
                }

                columns.add(column);
            }

            return columns;
        }
    }

    public List<List<String>> getColumnsAsString() {
        return (List) this.getColumns().stream().map(
            (row) -> (List) row.stream().map(
                WebElement::getText).collect(Collectors.toList())
            ).collect(Collectors.toList()
        );
    }
}
