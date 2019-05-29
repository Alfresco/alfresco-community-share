package org.alfresco.po.share.site.discussion;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Claudia Agache on 9/1/2016.
 */
@PageObject
public class DeleteTopicDialog extends DeleteDialog
{
    @Autowired
    TopicListPage topicListPage;

    public TopicListPage confirmTopicDelete()
    {
        clickDelete();
        return (TopicListPage) topicListPage.renderedPage();
    }
}
