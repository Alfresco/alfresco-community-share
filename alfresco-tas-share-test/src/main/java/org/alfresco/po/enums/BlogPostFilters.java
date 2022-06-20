package org.alfresco.po.enums;

public enum BlogPostFilters
{
    MY_DRAFTS_POSTS("ul.filterLink span.mydrafts>a", "My Draft Posts"),
    MY_PUBLISHED_POSTS("ul.filterLink span.mypublished>a", "My Published Posts"),
    LATEST_POSTS("ul.filterLink span.new>a", "New Posts"),
    ALL_POSTS("ul.filterLink span.all>a", "All Posts");

    private final String locator;
    private final String expectedFilterLabel;

    BlogPostFilters(String locator, String expectedFilterLabel)
    {
        this.locator = locator;
        this.expectedFilterLabel = expectedFilterLabel;
    }

    public String getLocator()
    {
        return locator;
    }

    public String getExpectedFilterLabel()
    {
        return expectedFilterLabel;
    }
}
