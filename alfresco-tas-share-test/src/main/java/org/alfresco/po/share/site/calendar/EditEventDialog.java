package org.alfresco.po.share.site.calendar;

import org.alfresco.utility.web.annotation.PageObject;

/**
 * Created by Claudia Agache on 7/20/2016.
 */
@PageObject
public class EditEventDialog extends AddEventDialog
{
    public Boolean isDialogDisplayed()
    {
        return browser.isElementDisplayed(dialogHeader) && dialogHeader.getText().equals("Edit Event");
    }
}
