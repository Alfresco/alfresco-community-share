package org.alfresco.po.share.site.discussion;

import org.openqa.selenium.WebDriver;

/**
 * Created by Claudia Agache on 8/9/2016.
 */
public class EditTopicPage extends CreateNewTopicPage
{
    public EditTopicPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
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
