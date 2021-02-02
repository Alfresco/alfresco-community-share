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

    SiteUserActivitiesFilter(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return this.description;
    }
}
