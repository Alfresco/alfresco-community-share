package org.alfresco.po.share.dashlet;

/**
 * Contains the days range filters on My Activities Dashlet.
 *
 * @author Cristina.Axinte
 */
public enum SiteActivitiesDaysRangeFilter
{
    TODAY("today"),
    SEVEN_DAYS("in the last 7 days"),
    FOURTEEN_DAYS("in the last 14 days"),
    TWENTY_EIGHT_DAYS("in the last 28 days");

    private final String description;

    /**
     * Set the description for the each filter.
     *
     * @param description - The Filter Description on HTML Page.
     */
    SiteActivitiesDaysRangeFilter(String description)
    {
        this.description = description;
    }

    public static SiteActivitiesDaysRangeFilter getFilter(String description)
    {
        for (SiteActivitiesDaysRangeFilter filter : SiteActivitiesDaysRangeFilter.values())
        {
            if (description.contains(filter.getDescription()))
            {
                return filter;
            }
        }
        return null;
    }

    /**
     * Gets description.
     *
     * @return String description
     */
    public String getDescription()
    {
        return this.description;
    }
}
