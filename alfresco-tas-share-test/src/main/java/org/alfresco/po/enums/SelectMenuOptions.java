package org.alfresco.po.enums;

public enum SelectMenuOptions
{
    DOCUMENTS(".selectDocuments"),
    FOLDERS(".selectFolders"),
    ALL(".selectAll"),
    INVERT_SELECTION(".selectInvert"),
    NONE(".selectNone");

    private final String locator;

    SelectMenuOptions(String locator)
    {
        this.locator = locator;
    }

    public String getLocator()
    {
        return locator;
    }
}
