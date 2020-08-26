package org.alfresco.po.share.searching;

import org.alfresco.utility.web.annotation.PageObject;

/**
 * Created by Claudia Agache on 8/23/2016.
 */
@PageObject
public class EditFilterPopup extends CreateNewFilterDialog
{
    public Boolean isDialogDisplayed(String filterId)
    {
        return browser.isElementDisplayed(dialogTitle) && dialogTitle.getText().equals(filterId);
    }
}
