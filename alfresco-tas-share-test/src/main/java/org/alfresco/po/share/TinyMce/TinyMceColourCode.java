package org.alfresco.po.share.TinyMce;

/**
 * Created by Alex Argint
 */
public enum TinyMceColourCode
{
    BLUE("div[title='Blue']", "div[title='Blue']"),
    BLACK("div[title='Black']", "div[title='Black']"),
    YELLOW("div[title='Yellow']", "div[title='Yellow']");


    private String foreColourLocator;
    private String bgColourLocator;

    TinyMceColourCode(String foreColourLocator, String bgColourLocator)
    {
        this.foreColourLocator = foreColourLocator;
        this.bgColourLocator = bgColourLocator;
    }

    public String getForeColourLocator()
    {
        return foreColourLocator;
    }

    public String getBgColourLocator()
    {
        return bgColourLocator;
    }
}
