package org.alfresco.po.share.site.discussion;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.browser.WebBrowser;

/**
 * Created by Claudia Agache on 8/9/2016.
 */
public class EditTopicPage extends CreateNewTopicPage
{
    public EditTopicPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

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
