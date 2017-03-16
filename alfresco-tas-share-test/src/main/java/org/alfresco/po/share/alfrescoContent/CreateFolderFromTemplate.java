package org.alfresco.po.share.alfrescoContent;

import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.utility.web.annotation.PageObject;

/**
 * @author Bogdan.Simion
 */
@PageObject
public class CreateFolderFromTemplate extends NewContentDialog
{
    /**
     * Method to check if Create Folder From Template form is displayed
     */

    public boolean isCreateFolderFromTemplatePopupDisplayed()
    {
        return browser.isElementDisplayed(dialogTitle) && dialogTitle.getText().equals("Create Folder From Template");
    }
}
