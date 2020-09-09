package org.alfresco.po.share.dashlet;

import org.openqa.selenium.WebElement;
import ru.yandex.qatools.htmlelements.element.Link;

public class ActivityLink
{
    private final String description;
    private final WebElement user;
    private final WebElement document;
    private final WebElement site;

    /**
     * Constructor.
     *
     * @param user     {@link Link} user link
     * @param document {@link Link} document link
     * @param site     {@link Link} site link
     */
    public ActivityLink(final WebElement user, final WebElement document, final WebElement site, final String description)
    {
        if (null == user)
        {
            throw new UnsupportedOperationException("Use share link is required");
        }
        if (null == document)
        {
            throw new UnsupportedOperationException("Document share link is required");
        }
        if (null == site)
        {
            throw new UnsupportedOperationException("Site share link is required");
        }
        this.user = user;
        this.document = document;
        this.site = site;
        this.description = description;
    }

    /**
     * Constructor.
     *
     * @param user {@link Link} user link
     * @param site {@link Link} site link
     */
    public ActivityLink(final WebElement user, final WebElement site, final String description)
    {
        if (null == user)
        {
            throw new UnsupportedOperationException("Use share link is required");
        }
        if (null == site)
        {
            throw new UnsupportedOperationException("Site share link is required");
        }
        this.user = user;
        this.document = null;
        this.site = site;
        this.description = description;
    }

    /**
     * Constructor.
     *
     * @param user {@link Link} user link
     */
    public ActivityLink(final WebElement user, final String description)
    {
        if (null == user)
        {
            throw new UnsupportedOperationException("Use share link is required");
        }

        this.user = user;
        this.description = description;
        this.document = null;
        this.site = null;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ActivityLink [description=");
        builder.append(description);
        builder.append(user.toString());
        builder.append(document.toString());
        builder.append(site.toString());
        builder.append("]");
        return builder.toString();
    }

    public String getDescription()
    {
        return description;
    }

    public WebElement getUser()
    {
        return user;
    }

    public WebElement getDocument()
    {
        return document;
    }

    public WebElement getSite()
    {
        return site;
    }
}
