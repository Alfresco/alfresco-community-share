package org.alfresco.po.enums;

public enum Layout
{
    ONE_COLUMN("One column"),
    TWO_COLUMNS_WIDE_LEFT("Two columns: wide left, narrow right"),
    TWO_COLUMNS_WIDE_RIGHT("Two columns: narrow left, wide right"),
    THREE_COLUMNS("Three columns: wide centre"),
    FOUR_COLUMNS("Four columns");
    public final String description;

    Layout(String description)
    {
        this.description = description;
    }
}