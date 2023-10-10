package org.alfresco.po.enums;

public enum Theme
{
    GREEN("Green Theme", "greenTheme", "#92c15f"),
    BLUE("Blue Theme", "default", "#d7e0e7"),
    LIGHT("Light Theme", "lightTheme", "#eeeeee"),
    YELLOW("Yellow Theme", "yellowTheme", "#eeeeee"),
    GOOGLE_DOCS("Google Docs Theme", "gdocs", "#eff3fa"),
    HIGH_CONTRAST("High Contrast Theme", "hcBlack", "#d7e0e7"),
    APPLICATION_SET("Application Set Theme", "", "");

    public final String name;
    public final String selectValue;
    public final String dashletHexColor;

    public String getSelectValue()
    {
        return this.selectValue;
    }

    Theme(String name, String selectValue, String hexColor)
    {
        this.name = name;
        this.selectValue = selectValue;
        this.dashletHexColor = hexColor;
    }
}
