package org.alfresco.po.enums;

public enum DashletHelpIcon
{
    MY_SITES("my-sites"),
    MY_TASKS("my-tasks"),
    MY_ACTIVITIES("activities"),
    MY_DOCUMENTS("my-documents"),
    MY_PROFILE("dashlet"),
    MY_DOC_WORKSPACES("my-workspaces"),
    MY_CALENDAR("user-calendar"),
    RSS_FEED("rssfeed"),
    WEB_VIEW("webview"),
    SAVED_SEARCH("savedsearch"),
    SITE_SEARCH("sitesearch"),
    MY_DISCUSSIONS("forumsummary"),
    MY_MEETING_WORKSPACES("my-meeting-workspaces"),
    SITE_PROFILE("site-profile"),
    SITE_CALENDAR("calendar"),
    SITE_LINKS("site-links"),
    SITE_CONTENT("docsummary"),
    WIKI("wiki"),
    SITE_ACTIVITIES("activities"),
    SITE_MEMBERS("colleagues"),
    DATA_LISTS("site-data-lists"),
    SITE_NOTICE("notice-dashlet"),
    CONTENT_IM_EDITING("content-im-editing"),
    IMAGE_PREVIEW("dashlet resizable yui-resize");
    public final String name;

    DashletHelpIcon(String name)
    {
        this.name = name;
    }
}
