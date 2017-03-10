package org.alfresco.po.share.site.calendar;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Claudia Agache on 9/1/2016.
 */
@PageObject
public class DeleteEventDialog extends DeleteDialog
{
    @Autowired
    CalendarPage calendarPage;

    public CalendarPage confirmEventDelete()
    {
        clickDelete();
        return (CalendarPage) calendarPage.renderedPage();
    }
}
