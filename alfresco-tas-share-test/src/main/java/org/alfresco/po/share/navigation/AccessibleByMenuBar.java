package org.alfresco.po.share.navigation;

/**
 * Handle @PageObject that is accessible from Alfresco Menu bar
 *
 * @author Paul.Brodner
 */
public interface AccessibleByMenuBar
{
    <T> T navigateByMenuBar();
}
