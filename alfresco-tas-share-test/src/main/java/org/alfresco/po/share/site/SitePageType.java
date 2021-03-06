package org.alfresco.po.share.site;

import org.openqa.selenium.By;

/**
 * @author bogdan.bocancea
 */
public enum SitePageType
{
    WIKI("li[id$='_default-page-wiki-page']", "#HEADER_SITE_WIKI-PAGE", "Wiki"),
    BLOG("li[id$='_default-page-blog-postlist']", "#HEADER_SITE_BLOG-POSTLIST", "Blog"),
    CALENDER("li[id$='_default-page-calendar']", "#HEADER_SITE_CALENDAR", "Calendar"),
    DATA_LISTS("li[id$='_default-page-data-lists']", "#HEADER_SITE_DATA-LISTS", "Data Lists"),
    DISCUSSIONS("li[id$='_default-page-discussions-topiclist']", "#HEADER_SITE_DISCUSSIONS-TOPICLIST", "Discussions"),
    DOCUMENT_LIBRARY("li[id$='_default-page-documentlibrary']", "#HEADER_SITE_DOCUMENTLIBRARY", "Document Library"),
    LINKS("li[id$='_default-page-links']", "#HEADER_SITE_LINKS", "Links");

    private final String customizeLocator;
    private final String dashboardLocation;
    private final String text;

    SitePageType(String customizeLocator, String dashboardLocation, String text)
    {
        this.customizeLocator = customizeLocator;
        this.dashboardLocation = dashboardLocation;
        this.text = text;
    }

    public By getDashboardLocator()
    {
        return By.cssSelector(dashboardLocation);
    }

    public String getCustomizeCssLocator()
    {
        return customizeLocator;
    }

    public String getDashboardCssLocator()
    {
        return dashboardLocation;
    }

    public String getDisplayText()
    {
        return text;
    }
}
