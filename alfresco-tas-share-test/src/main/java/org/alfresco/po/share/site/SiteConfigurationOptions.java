package org.alfresco.po.share.site;

/**
 * Created by Claudia Agache on 7/8/2016.
 */
public enum SiteConfigurationOptions
{
    BECOME_SITE_MANAGER("Become Site Manager"),
    EDIT_SITE_DETAILS("Edit Site Details"),
    JOIN_SITE("Join Site"),
    LEAVE_SITE("Leave Site"),
    REQUEST_TO_JOIN("Request to Join"),
    CUSTOMIZE_SITE("Customize Site"),
    CUSTOMIZE_DASHBOARD("Customize Dashboard");

    private final String value;

    SiteConfigurationOptions(String optionText)
    {
        this.value = optionText;
    }

    public String getValue()
    {
        return value;
    }
}
