package org.alfresco.po.share.site.discussion;

import org.alfresco.utility.web.annotation.PageObject;

/**
 * Created by Claudia Agache on 8/9/2016.
 */
@PageObject
public class EditTopicPage extends CreateNewTopicPage
{
    /**
     * Check if page is opened
     *
     * @return true if page header is Edit Topic
     */
    public boolean isPageDisplayed()
    {
        return pageHeader.getText().equals("Edit Topic");
    }
}
