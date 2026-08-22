package org.alfresco.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Lightweight replacement for the retired {@code ru.yandex.qatools.htmlelements.element.Table}.
 * <p>
 * Wraps a {@code <table>} {@link WebElement} and exposes the subset of the original html-elements
 * {@code Table} API that the Share page objects rely on. Behaviour mirrors html-elements 1.20.0.
 */
public class HtmlTable
{
    private final WebElement wrappedElement;

    public HtmlTable(WebElement wrappedElement)
    {
        this.wrappedElement = wrappedElement;
    }

    public List<WebElement> getHeadings()
    {
        return wrappedElement.findElements(By.xpath(".//th"));
    }

    public List<List<WebElement>> getRows()
    {
        return wrappedElement.findElements(By.xpath(".//tr")).stream()
            .map(row -> row.findElements(By.xpath(".//td")))
            .filter(cells -> !cells.isEmpty())
            .collect(Collectors.toList());
    }

    public List<WebElement> getColumnByIndex(int index)
    {
        return wrappedElement.findElements(By.cssSelector(String.format("tr > td:nth-of-type(%d)", index + 1)));
    }

    public List<List<WebElement>> getColumns()
    {
        List<List<WebElement>> columns = new ArrayList<>();
        List<List<WebElement>> rows = getRows();
        if (!rows.isEmpty())
        {
            int columnsCount = rows.get(0).size();
            for (int i = 0; i < columnsCount; i++)
            {
                columns.add(getColumnByIndex(i));
            }
        }
        return columns;
    }

    public List<List<String>> getColumnsAsString()
    {
        return getColumns().stream()
            .map(column -> column.stream().map(WebElement::getText).collect(Collectors.toList()))
            .collect(Collectors.toList());
    }

    public WebElement getCellAt(int rowIndex, int columnIndex)
    {
        return getRows().get(rowIndex).get(columnIndex);
    }
}
