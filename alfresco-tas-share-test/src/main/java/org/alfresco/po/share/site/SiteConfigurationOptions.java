package org.alfresco.po.share.site;

import org.openqa.selenium.By;

/**
 * Created by Claudia Agache on 7/8/2016.
 */
public enum SiteConfigurationOptions
{
    BECOME_SITE_MANAGER("div.alf-menu-groups[style*='visible'] td[id='HEADER_BECOME_SITE_MANAGER_text']", "Become Site Manager"),
    EDIT_SITE_DETAILS("div.alf-menu-groups[style*='visible'] td[id='HEADER_EDIT_SITE_DETAILS_text']", "Edit Site Details"),
    JOIN_SITE("div.alf-menu-groups[style*='visible'] td[id='HEADER_JOIN_SITE_text']", "Join Site"),
    LEAVE_SITE("div.alf-menu-groups[style*='visible'] td[id='HEADER_LEAVE_SITE_text']", "Leave Site"),
    REQUEST_TO_JOIN("div.alf-menu-groups[style*='visible'] td[id='HEADER_JOIN_SITE_text']", "Request to Join"),
    CUSTOMIZE_SITE("div.alf-menu-groups[style*='visible'] td[id='HEADER_CUSTOMIZE_SITE_text']", "Customize Site");

    private String optionLocator;
    private String optionText;

    SiteConfigurationOptions(String optionLocator, String optionText)
    {
        this.optionLocator = optionLocator;
        this.optionText = optionText;
    }

    public By getOptionLocator()
    {
        return By.cssSelector(optionLocator);
    }

    public String getOptionText()
    {
        return optionText;
    }
}
