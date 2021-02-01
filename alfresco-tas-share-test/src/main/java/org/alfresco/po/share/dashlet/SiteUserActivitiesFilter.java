package org.alfresco.po.share.dashlet;

/**
 * Contains options for Site User Activities filter
 *
 */
public enum SiteUserActivitiesFilter
{
    MY_ACTIVITIES("My activities"),
    OTHERS_ACTIVITIES("Everyone else's activities"),
    EVERYONES_ACTIVITIES("Everyone's activities"),
    IM_FOLLOWING("I'm following");

    private final String description;

    /**
     * Set the description for each filter
     *
     * @param description The filter Description from UI
     */
    SiteUserActivitiesFilter(String description)
    {
        this.description = description;
    }

    public static SiteUserActivitiesFilter getFilter(String description)
    {
        for (SiteUserActivitiesFilter filter : SiteUserActivitiesFilter.values())
        {
            if (description.contains(filter.getDescription()))
            {
                return filter;
            }
        }
        return null;
    }

    public String getDescription()
    {
        return this.description;
    }
}
