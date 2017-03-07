package org.alfresco.po.share.searching;

import org.alfresco.po.annotation.PageObject;

/**
 * Created by Claudia Agache on 8/23/2016.
 */
@PageObject
public class EditFilterPopup extends CreateNewFilterPopup
{
    public Boolean isDialogDisplayed(String filterId)
    {
        return browser.isElementDisplayed(dialogTitle) && dialogTitle.getText().equals(filterId);
    }
}
